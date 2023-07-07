PROCEDURE GenerateDirfReport(RunID in varchar2, InstitutionNumber in varchar2, PostingDate in varchar2, Year In varchar2, MonthFrom In Varchar2, MonthTo In Varchar2, Result in out nocopy varchar2)
    -- This function will populate the necessary data fro the Settlement Process Report.
    -- Old Data is automatically deleted by this function.
Is
    v_RunID_Count Number := 0;
    --
    ex_existingRunID exception;
Begin
    Select count(*) into v_RunID_Count
    From RPT_DIRF_REPORT where RUN_ID = RunID;
    if v_RunID_Count <> 0 then
        raise ex_existingRunID;
    end if;
    --Delete data older than 10 days.
    execute immediate 'Delete RPT_DIRF_REPORT where GENERATION_DATE < to_char(sysdate - 10 ,''YYYYMMDD'')';
    insert Into RPT_DIRF_REPORT  (
    select RUNID,
    INSTITUTION_NUMBER,
    to_char(sysdate,'YYYYMMDD'),
    TRADE_NAME,
    CNPJ_CPF,
    STREET_NAME,
    STREET_NUMBER,
    NEIGHBOURHOOD,
    CITY_NAME,
    CLIENT_STATE_CODE,
    ZIP_CODE,
    INST_CNPJ_CPF,
    INSTITUTION_TRADE_NAME,
    INST_STREET_NAME,
    INST_STREET_NUMBER,
    INST_STREET_NUMBER,
    INST_CITY_NAME,
    ACCT_NUMBER,
    ACCT_TYPE_TXT,
    LINKED_SERVICE_TYPE,
    CAPTURE_MONTH,
    SALES,
    COMMISSION_PAID,
    COMMISSION_BANK,
    INC_TAX_HELD,
    COMMISSION_ISS_BANK,
    AMOUNTDECIMAL,
    AMOUNTTHOUSANDS
    from(
        select
    lic.institution_number,
    lic.institution_name,
    dtl.Trade_name,
    decode(length(nvl(dtl.registration_number,'0')),14,
        'CNPJ: ' || substr(dtl.registration_number,1,2) || '.' || substr(dtl.registration_number,3,3) || '.' || substr(dtl.registration_number,6,3) || '/' || substr(dtl.registration_number,10,4) || '-' || substr(dtl.registration_number,13,2) ,11,
        'CPF: ' || substr(dtl.registration_number,1,3) || '.' || substr(dtl.registration_number,3,3) || '.' || substr(dtl.registration_number,6,3) || '-' || substr(dtl.registration_number,9,2), null)
        as CNPJ_CPF,
    addr.addr_line_1 as STREET_NAME,
    addr.addr_line_2 as STREET_NUMBER,
    addr.addr_line_3 as NEIGHBOURHOOD,
    addr.ADDR_CLIENT_CITY as CITY_NAME,
    state.state_code as CLIENT_STATE_CODE,
    addr.post_code as ZIP_CODE,
    decode(length(nvl(inst.registration_number,'0')),14,
        'CNPJ: ' || substr(inst.registration_number,1,2) || '.' || substr(inst.registration_number,3,3) || '.' || substr(inst.registration_number,6,3) || '/' || substr(inst.registration_number,10,4) || '-' || substr(inst.registration_number,13,2) ,11,
        'CPF: ' || substr(inst.registration_number,1,3) || '.' || substr(inst.registration_number,3,3) || '.' || substr(inst.registration_number,6,3) || '-' || substr(inst.registration_number,9,2), null)
        as INST_CNPJ_CPF,
    inst.TRADE_NAME as INSTITUTION_TRADE_NAME,
    addrInst.addr_line_1 as INST_STREET_NAME,
    addrInst.addr_line_2 as INST_STREET_NUMBER,
    addrInst.addr_line_3 as INST_NEIGHBORHOOD,
    addrInst.ADDR_CLIENT_CITY as INST_CITY_NAME,
    acct.acct_number,
    type_id.type_id as ACCT_TYPE_TXT,
    type_id.linked_service_type, -- Debit / Credit
    nvl(TURNOVER.CAPTURE_MONTH,'0') as CAPTURE_MONTH,
    nvl(TURNOVER.GROSS_AMOUNT,'0') as SALES,
    nvl(TURNOVER.COMMISSION_PAID,'0') as COMMISSION_PAID,
    nvl(TURNOVER.COMMISSION_BANK,'0') as COMMISSION_BANK,
    nvl(TURNOVER.INC_TAX_HELD,'0') as INC_TAX_HELD,
    nvl(TURNOVER.COMMISSION_ISS_BANK,'0') as COMMISSION_ISS_BANK,
    nvl(confdecimal.config_value, '.') as amountdecimal,
    nvl(confthousand.config_value, ',') as amountthousands
From
    cas_client_account acct,
    cis_client_details dtl,
    cis_addresses addr,
    cis_client_details inst, -- Institution Details
    cis_addresses addrInst, -- Institution Address
    sys_institution_licence lic,
    bwt_account_type_id type_id,
    bwt_country_state state,
    (
    Select
    INSTITUTION_NUMBER,
    ACCT_NUMBER,
    CAPTURE_MONTH,
    sum(GROSS_AMOUNT) as GROSS_AMOUNT,
    sum(COMMISSION_PAID) as COMMISSION_PAID,
    sum(COMMISSION_BANK) as COMMISSION_BANK,
    --sum(to_number(COMMISSION_BANK) * irf_rate/100) as INC_TAX_HELD,
  sum(nvl(local_amount,0)) as INC_TAX_HELD,
    sum(COMMISSION_ISS_BANK) as COMMISSION_ISS_BANK
    From
        (
        select /*+index(SUMM IX12_TRN_ACCT_NO)*/ trn.institution_number, --BWW-8220: Added hint to improve query performance
            trn.local_amount_gr as GROSS_AMOUNT,
            trn.local_amount_inw_chg as COMMISSION_PAID, --GROSS MDR
            to_number(nvl(trn.local_amount_inw_chg,'0')) - to_number(nvl(trn.local_amount_out_chg,'0')) as COMMISSION_BANK, -- MDR_NET
            trn.local_amount_out_chg as COMMISSION_ISS_BANK, --INTERCHANGE
            substr(trn.transaction_date,5,2) as CAPTURE_MONTH,
            summ.acct_number,
      chg.local_amount
            -- (Select irf_rate from cbr_inflation_rates where institution_number = trn.institution_number
                                                     -- and effective_date = (select max(sub_rates.effective_date)
                                                     -- from cbr_inflation_rates sub_rates
                                                     -- where sub_rates.institution_number = institution_number
                                                     -- and sub_rates.effective_date <= trn.transaction_date) ) as irf_rate
        from int_transactions summ,
            int_transactions trn,
                int_addendum_charges chg,
            cas_client_account acct,
            cis_client_details dtl
        where trn.institution_number = InstitutionNumber
            and trn.institution_number = summ.institution_number
            and trn.source_settlement = summ.source_settlement
            and trn.transaction_class = '002'
            and summ.transaction_class = '012'
                and chg.transaction_slip(+) = trn.transaction_slip
                and chg.institution_number(+) = trn.institution_number
                and chg.charge_type(+) = '506' -- tax charge type
            and acct.institution_number = summ.institution_number
            and acct.acct_number = summ.acct_number
            and dtl.institution_number = acct.institution_number
            and dtl.client_number = acct.client_number
            and dtl.client_type in ('002','100') -- CNPJ / CPF clients only
            and dtl.registration_number >= '0000000000000'
            and dtl.registration_number <= '9999999999999'
            and trn.transaction_date >= Year || substr(MonthFrom,2,2) || '01'--'20130101' -- Date From
            and trn.transaction_date <= Year || substr(MonthTo,2,2) || '31'--'20131231' -- Date To
            --BWW-8220: Added filters to improve query performance
            and summ.record_date >= Year || substr(MonthFrom,2,2) || '01'--'20210101' -- Date From
            and summ.record_date <= Year || substr(MonthTo,2,2) || '31'--'20211231' -- Date To
            and trn.record_date >= Year || substr(MonthFrom,2,2) || '01'--'20210101' -- Date From
            and trn.record_date <= Year || substr(MonthTo,2,2) || '31'--'20211231' -- Date To
        ) TURNOVERS
    group by INSTITUTION_NUMBER, ACCT_NUMBER, CAPTURE_MONTH
    ) TURNOVER,
    sys_configuration confdecimal,
    sys_configuration confthousand
Where
    dtl.institution_number = InstitutionNumber
    and dtl.institution_number = acct.institution_number
    and dtl.client_number = acct.client_number
    and dtl.client_type in (bwtpad('BWT_CLIENT_TYPE','002'),bwtpad('BWT_CLIENT_TYPE','100')) -- CNPJ / CPF clients only
    and dtl.registration_number >= '0000000000000'
    and dtl.registration_number <= '9999999999999'
    and addr.institution_number = dtl.institution_number
    and addr.client_number = dtl.client_number
    and addr.address_category = bwtpad('BWT_ADDRESS_CATEGORY','001') --Standard Address
    and addr.group_number = '99999999'
    and addr.effective_date = (select max(effective_date) from cis_addresses
                                where institution_number = addr.institution_number
                                and client_number = addr.client_number
                                and address_category = addr.address_category
                                and group_number = addr.group_number)
    and inst.institution_number = dtl.institution_number
    and inst.client_number = inst.institution_number
    and addrInst.institution_number = Inst.institution_number
    and addrInst.client_number = Inst.client_number
    and addrInst.address_category = bwtpad('BWT_ADDRESS_CATEGORY','001') --Standard Address
    and addrInst.group_number = '99999999'
    and addrInst.effective_date = (select max(effective_date) from cis_addresses
                                where institution_number = addrInst.institution_number
                                and client_number = addrInst.client_number
                                and address_category = addrInst.address_category
                                and group_number = addrInst.group_number)
    and type_id.institution_number = acct.institution_number
    and type_id.language = 'USA'
    and type_id.index_field = acct.account_type_id
    and type_id.linked_service_type in (bwtpad('BWT_SERVICE_TYPE','001'),bwtpad('BWT_SERVICE_TYPE','002')) -- Credit / Debit accounts only
    and state.institution_number = addr.institution_number
    and state.language = 'USA'
    and state.index_field = addr.client_state
    and TURNOVER.institution_number(+) = acct.institution_number
    and TURNOVER.acct_number(+) = acct.acct_number
    and dtl.institution_number = lic.institution_number
    and confdecimal.institution_number(+) = '00000000'
    and confdecimal.config_keyword(+) = 'AmountDecimal'
    and confthousand.institution_number(+) = '00000000'
    and confthousand.config_keyword(+) = 'AmountThousands'
order by acct.institution_number, acct.acct_number, capture_month)
);
    Commit;
    Result := 'Success';
    --
exception
    when ex_existingRunID then
        Result := 'Run ID already exists';
    when others then
        Result :=  'Fail';
End GenerateDirfReport;