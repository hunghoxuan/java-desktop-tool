procedure     invoice_report(
  p_cursor    in out bw_gc_reports.cInvoice,
  p_instno    in varchar2,
  p_acct_no   in varchar2,
  p_as_at     in varchar2
)
is
  --
  -- parameters in
--  p_acct_no        varchar2(12) := '00005491001';
--  p_instno         varchar2(8) := '00000002';
--  p_statement_date varchar2(8) := '20130131';
  --
  v_fee_par        varchar2(23);
  v_acct_ccy       varchar2(3);
  v_volume         varchar2(50);
  v_amount         varchar2(50);
  --
  v_client_number        varchar2(8);  --done
  v_client_group         varchar2(8);  --done
  v_company_name         varchar2(35); --done
  v_addr_contact         varchar2(35); --done
  v_addr_line_1          varchar2(35); --done
  v_addr_line_2          varchar2(35); --done
  v_addr_line_3          varchar2(35); --done
  v_addr_line_4          varchar2(35); --done
  v_addr_line_5          varchar2(35); --done
  v_post_code            varchar2(20); --done
  v_client_city          varchar2(35); --done
  v_client_country_iso   varchar2(3);  --done
  v_client_country       varchar2(50); --done
  v_invoice_number       varchar2(20); --done
  v_invoice_date         varchar2(8);  --done
  v_period_from          varchar2(8);  --done
  v_period_to            varchar2(8);  --done
  v_contract_id          varchar2(4);  --done
  v_vat_reg_number       varchar2(30); --done
  v_service_contract_id  varchar2(3);  --done
  v_vat_code             varchar2(5) := '2';
  --
begin
  -- cleanup
  delete tmp_gc_invoice_results;
  delete tmp_gc_invoice_workings;
  --
  -- determine the Fee PAR account and currency
  select acct_number, acct_currency, client_number, group_number, service_contract_id
  into   v_fee_par, v_acct_ccy, v_client_number, v_client_group, v_service_contract_id
  from   cas_client_account
  where  acct_number = p_acct_no
  and    institution_number = p_instno
  and    account_type_id = '007'
  and    billing_level = '001';
  --
--dbms_output.put_line( v_fee_par );
--
  -- verify that the P_AS_AT parameter is a valid DATE_STATEMENT_END for the given account
  select date_statement_start, date_statement_end, statement_number, date_statement_end
  into   v_period_from, v_period_to, v_invoice_number, v_invoice_date
  from   hst_statement_account
  where  acct_number = p_acct_no
  and    statement_type = '060' -- daily invoice
  and    date_statement_end = p_as_at;
  --
--
----temporary fix to see data during testing!
----
--v_period_from := '20130101';
--v_period_to   := '20130131';
--
--
--dbms_output.put_line( v_period_from ||','||v_period_to );
--
--
--
  -- get client details
  select vat_reg_number,    substr(our_reference, -4),   company_name,
         contact_name,      client_country
  into   v_vat_reg_number,  v_contract_id,               v_company_name,
         v_addr_contact,    v_client_country_iso
  from   cis_client_details
  where  institution_number = p_instno
  and    client_number = v_client_number;
  --
  -- get address details
  begin
    --
    select addr_line_1,          addr_line_2,          addr_line_3,
           addr_line_4,          addr_line_5,          post_code,
           addr_client_city,     client_country
    into   v_addr_line_1,        v_addr_line_2,        v_addr_line_3,
           v_addr_line_4,        v_addr_line_5,        v_post_code,
           v_client_city,        v_client_country_iso
    from   cis_addresses addr
    where  addr.address_category = '001' --standard
    and    addr.effective_date = ( select max(effective_date)
                                   from   cis_addresses addr2
                                   where  addr2.institution_number = addr.institution_number
                                   and    addr2.address_category = addr.address_category
                                   and    addr2.client_number = addr.client_number
                                   and    addr2.group_number = addr.group_number
                                   and    nvl(addr2.expiry_date,'99999999') > v_period_to
                                   and    addr2.effective_date <= v_period_to
                                 )
    and    nvl(addr.expiry_date,'99999999') > v_period_to
    and    addr.client_number =  v_client_number
    and    addr.group_number = v_client_group;
    --
  exception
  when no_data_found then
    -- repeat above query for the non-group-specific address
    begin
      --
      select addr_line_1,          addr_line_2,          addr_line_3,
             addr_line_4,          addr_line_5,          post_code,
             addr_client_city,     client_country
      into   v_addr_line_1,        v_addr_line_2,        v_addr_line_3,
             v_addr_line_4,        v_addr_line_5,        v_post_code,
             v_client_city,        v_client_country_iso
      from   cis_addresses addr
      where  addr.address_category = '001' --standard
      and    addr.effective_date = ( select max(effective_date)
                                     from   cis_addresses addr2
                                     where  addr2.institution_number = addr.institution_number
                                     and    addr2.address_category = addr.address_category
                                     and    addr2.client_number = addr.client_number
                                     and    addr2.group_number = addr.group_number
                                     and    nvl(addr2.expiry_date,'99999999') > v_period_to
                                     and    addr2.effective_date <= v_period_to
                                   )
      and    nvl(addr.expiry_date,'99999999') > v_period_to
      and    addr.client_number =  v_client_number
      and    group_number = '99999999';
      --
    exception
    when no_data_found then
      v_addr_line_1  := '<ERROR: NO ADDRESS FOUND!>';
    when others then
      v_addr_line_1  := '<ERROR: NO ADDRESS FOUND!>';
    end;
  when others then
   v_addr_line_1  := '<ERROR: NO ADDRESS FOUND!>';
  end;
  --
  begin
    select client_country
    into   v_client_country
    from   bwt_country
    where  index_field = v_client_country_iso
    and    institution_number = p_instno
    and    language = 'USA';
  exception
  when others then
    v_client_country := '<ERR:NO CTRY['||v_client_country_iso||']!>';
  end;

  -- extract special commissions
  insert into tmp_gc_invoice_workings (
    fee_category               ,
    paym_txn_slip              ,
    paym_value_date            ,
    src_acct_ccy               ,
    src_acct_amount_being_paid ,
    paym_ccy                   ,
    paym_amount                ,
    rate_fx_tran_to_acct       ,
    spec_comm_trn_slp          ,
    spec_comm_fee_id           ,
    spec_comm_fee_name         ,
    fee_details                ,
    spec_comm_in_orig_ccy      ,
    spec_comm_in_paym_ccy      ,
    txn_volume                 ,
    txn_value_in_orig_ccy      ,
    txn_value_in_acct_ccy
  )
  select 'SPECIAL COMMISSIONS'   as fee_category,
         paym.transaction_slip   as paym_txn_slip,
         paym.value_date         as paym_value_date,
         paym.TRAN_CURRENCY      as src_acct_ccy,
         paym.tran_amount_gr     as src_acct_amount_being_paid,
         paym.acct_currency      as paym_ccy,
         paym.account_amount_net as paym_amount,
         paym.account_amount_net / paym.tran_amount_gr  as rate_fx_tran_to_acct,
         spec_comm.transaction_slip as spec_comm_trn_slp,
         spec_comm.transaction_type as spec_comm_fee_id,
         tt.transaction_type        as spec_comm_fee_name,
         case
           when nvl(tc.fee_percent,0) > 0 then
             tc.fee_percent ||'% '
           else
             null
         end
         ||
         case
           when nvl(tc.fee_base,0) > 0 then
             'Base='|| nvl(tc.fee_base,0) ||' ' || fee_ccy.Swift_code ||' '
           else
             null
         end
         ||
         case
           when nvl(tc.fee_minimum,0) > 0 then
             '(Min='|| nvl(tc.fee_minimum,0) ||' ' || fee_ccy.Swift_code ||') '
           else
             null
         end
         ||
         case
           when nvl(tc.fee_maximum,0) > 0 then
             '(Max='|| nvl(tc.fee_maximum,0) ||' ' || fee_ccy.Swift_code ||') '
           else
             null
         end
         as fee_details,
         spec_comm.account_amount_net   as spec_comm_in_orig_ccy,
         '              0.00' as spec_comm_in_paym_ccy, --this will be calculated below
         '              0' as txn_volume,               --this will be calculated below
         '              0.00' as txn_value_in_orig_ccy, --this will be calculated below
         '              0.00' as txn_value_in_acct_ccy  --this will be calculated below
  from   int_transactions paym
  join   int_transactions spec_comm
         on  spec_comm.retrieval_reference = paym.transaction_slip
         and spec_comm.institution_number = paym.institution_number
  join   cbr_transaction_charges tc /*tc=transaction charges*/
         on  tc.institution_number = spec_comm.institution_number
         and tc.record_id_number = spec_comm.inward_fee_number
         and tc.charge_type <> '002' -- do not list commissions where hstacct.statement_type = '060'
  left outer join bwt_currency fee_ccy
         on  fee_ccy.iso_code = tc.fee_currency
         and fee_ccy.institution_number = tc.institution_number
         and fee_ccy.language = 'USA'
  left join bwt_transaction_type tt
         on  tt.institution_number = spec_comm.institution_number
         and tt.INDEX_FIELD = spec_comm.TRANSACTION_TYPE
         and tt.language = 'USA'
  where  paym.acct_number = v_fee_par
  and    paym.institution_number = p_instno
  and    paym.transaction_class = '002' --clearing txn
  and    paym.transaction_category = '008' --payments
  and    paym.value_date between v_period_from and v_period_to;
  --
  -- calculate the special commission in payment currency (USD)
  update tmp_gc_invoice_workings
  set    spec_comm_in_paym_ccy = spec_comm_in_orig_ccy * rate_fx_tran_to_acct;
  --
  -- calculate the volumes and the amounts over which these special commissions were charged
  for rec in (
    select * from tmp_gc_invoice_workings
  ) loop
    --
    select count(*), sum(trn.TRAN_AMOUNT_GR)
    into   v_volume, v_amount
    from   int_addendum_charges chg_pkg
    join   int_transactions trn
           on  trn.institution_number = chg_pkg.institution_number
           and trn.transaction_slip = chg_pkg.TRANSACTION_SLIP
    where  chg_pkg.institution_number = p_instno
    and    chg_pkg.CHARGE_TRANSACTION_SLIP = rec.spec_comm_trn_slp;
    --
    update tmp_gc_invoice_workings
    set    txn_volume = v_volume,
           txn_value_in_orig_ccy = v_amount,
           txn_value_in_acct_ccy = v_amount *  rec.rate_fx_tran_to_acct
    where  spec_comm_trn_slp = rec.spec_comm_trn_slp;
    --
  end loop;
  --
  -- clean up any rubbish data (probably useful only during testing where TXNs were deleted
  delete tmp_gc_invoice_workings
  where  txn_value_in_orig_ccy is null
  and    txn_value_in_acct_ccy is null;
  --
  -- update currencies with text-code
  update tmp_gc_invoice_workings  t
  set    t.src_acct_ccy_txt = (select c.SWIFT_CODE
                               from   bwt_currency c
                               where  c.iso_code = t.src_acct_ccy
                               and    c.institution_number = p_instno
                               and    c.language = 'USA');
  --
  update tmp_gc_invoice_workings  t
  set    t.paym_ccy_txt = (select c.SWIFT_CODE
                                from   bwt_currency c
                                where  c.iso_code = t.paym_ccy
                                and    c.institution_number = p_instno
                                and    c.language = 'USA');
  --
  -- summarise all fees as required by report
  insert into tmp_gc_invoice_results (
    institution_number,  --1
    client_number,
    company_name,
    addr_contact,
    addr_line_1,         --5
    addr_line_2,
    addr_line_3,
    addr_line_4,
    addr_line_5,
    post_code,           --10
    client_city,
    client_country_iso,
    client_country,
    invoice_number,
    invoice_date,        --15
    period_from,
    period_to,
    contract_id,
    vat_reg_number,
    contact_name,        --20
    fee_category,
    fee_name,
    fee_tran_type,
    volume,
    amount_usd,          --25
    tariff_details,
    tariff_amount_usd,
    vat_code,
    service_contract_id
  )
  select p_instno,            --1
         v_client_number,
         v_company_name,
         v_addr_contact,
         v_addr_line_1,       --5
         v_addr_line_2,
         v_addr_line_3,
         v_addr_line_4,
         v_addr_line_5,
         v_post_code,         --10
         v_client_city,
         v_client_country_iso,
         v_client_country,
         v_invoice_number,
         v_invoice_date,      --15
         v_period_from,
         v_period_to,
         v_contract_id,
         v_vat_reg_number,
         v_addr_contact,      --20
         tmp.fee_category,
         tmp.spec_comm_fee_name,
         tmp.spec_comm_fee_id,
         to_char(sum(tmp.txn_volume),'FM999999999999990') as volume,
         to_char(sum(tmp.txn_value_in_acct_ccy), 'FM999999999999990.00'),  --25
         tmp.fee_details,
         to_char(sum(tmp.spec_comm_in_paym_ccy), 'FM999999999999990.00'),
         v_vat_code,
         v_service_contract_id
  from   tmp_gc_invoice_workings tmp
  group  by fee_category,
         src_acct_ccy,
         src_acct_ccy_txt,
         paym_ccy,
         paym_ccy_txt,
         spec_comm_fee_id,
         spec_comm_fee_name,
         fee_details;
  --
  -- extract direct fees posted to the FEE PAR(account- and service- fees)
  --
  insert into tmp_gc_invoice_results (
    institution_number,  --1
    client_number,
    company_name,
    addr_contact,
    addr_line_1,         --5
    addr_line_2,
    addr_line_3,
    addr_line_4,
    addr_line_5,
    post_code,           --10
    client_city,
    client_country_iso,
    client_country,
    invoice_number,
    invoice_date,        --15
    period_from,
    period_to,
    contract_id,
    vat_reg_number,
    contact_name,        --20
    fee_category,
    fee_name,
    fee_tran_type,
    volume,
    amount_usd,          --25
    tariff_details,
    tariff_amount_usd,
    vat_code,
    service_contract_id
  )
  select p_instno,            --1
         v_client_number,
         v_company_name,
         v_addr_contact,
         v_addr_line_1,       --5
         v_addr_line_2,
         v_addr_line_3,
         v_addr_line_4,
         v_addr_line_5,
         v_post_code,         --10
         v_client_city,
         v_client_country_iso,
         v_client_country,
         v_invoice_number,
         v_invoice_date,      --15
         v_period_from,
         v_period_to,
         v_contract_id,
         v_vat_reg_number,
         v_addr_contact,      --20
         'DIREC',
         max(f.CLIENT_FEE_ID),
         max(fee.client_fee_id),
         to_char(sum(nvl(trn.number_slips,1)), 'FM999999999999990'),  --volume works only on type of fees that depend on a count of txn
         '0',  --25 --amount is almost impossible to work out but if the fee has a percentage it might be possible to work it backwards but the moment you have a base and minimum and maximum it is next to impossible!
         case
          when nvl(max(fee.FEE_PERCENT),0) > 0 then
               max(fee.FEE_PERCENT) ||'% '
          else
             null
          end
          ||
          case
           when nvl(max(fee.fee_base),0) > 0 then
             'Base='|| nvl(max(fee.fee_base),0) ||' ' || max(fee_ccy.Swift_code) ||' '
           else
             null
          end
          ||
          case
           when nvl(max(fee.fee_minimum),0) > 0 then
             '(Min='|| nvl(max(fee.fee_minimum),0) ||' ' || max(fee_ccy.Swift_code) ||') '
           else
             null
          end
          ||
          case
           when nvl(max(fee.FEE_MAXIMUM),0) > 0 then
             '(Max='|| nvl(max(fee.FEE_MAXIMUM),0) ||' ' || max(fee_ccy.Swift_code) ||') '
          else
             null
          end,  --26
         to_char(sum(trn.account_amount_net),'FM999999999999990.00'),
         v_vat_code,
         v_service_contract_id
  from   int_transactions trn
  join   cbr_client_fees fee
         on  fee.institution_number = trn.institution_number
         and fee.record_id_number = trn.inward_fee_number
  left outer join bwt_currency fee_ccy
         on  fee_ccy.institution_number = fee.institution_number
         and fee_ccy.ISO_CODE = fee.FEE_CURRENCY
         and fee_ccy.LANGUAGE = 'USA'
  left outer join bwt_client_fee_id f
         on  f.index_field = fee.client_fee_id
         and f.institution_number = fee.institution_number
         and f.language = 'USA'
  where  trn.institution_number = p_instno
  and    trn.transaction_class = '002'
  and    trn.acct_number = v_fee_par
  and    trn.transaction_category <> '008' -- exclude payments from interim / to par
  and    trn.value_date between v_period_from and v_period_to
  group  by trn.acct_number,
         trn.transaction_type,
         trn.acct_currency,
         trn.inward_fee_number
  order  by acct_number,
         transaction_type;
  --
  open p_cursor for
    select * from tmp_gc_invoice_results;
  --
--exception
--when others then
--  raise;
end invoice_report;
