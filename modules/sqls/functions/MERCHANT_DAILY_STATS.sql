procedure Merchant_daily_stats(rec_date varchar2, p_institution_number varchar2 ) is

  cursor rec_datum(str_rec varchar2, p_institution_number varchar2) is
    select count(*) as items
    from   merchant_daily_report
    where  str_rec = record_date
    and    institution_number = p_institution_number;

  cursor get_stats is
    select (case
             when it.transaction_category = '001' then
              nvl(ltrim(it.merchant_number, '0'), 'unknown')
             else
              it.client_number
           end) as merch,
         	(case
             when it.transaction_category = '001' then
              nvl(it.merchant_number, 'unknown')
             else
              it.client_number
           end) as c_num,
           it.record_date,
           it.transaction_date,
           it.acct_currency,
           it.transaction_destination,
           it.business_class,
           (case
             when it.transaction_category = '002' and it.record_date = it.value_date then
              it.transaction_category || 'cat'
             else
              it.transaction_type
           end) "the real type",
           count(*) as counts,
           sum(it.LOCAL_AMOUNT_GR * decode(it.dr_cr_indicator, '001', -1, 1)) as loc_gr,
           sum(it.LOCAL_AMOUNT_INW_CHG * decode(it.dr_cr_indicator, '001', -1, 1)) as loc_chg
     from  int_transactions it
     where it.record_date = rec_date
     and   it.transaction_class = '002'
     and   it.transaction_destination in ('004', '018', '154', '937')
     and   it.transaction_source in ('015', '005', '037', '031')
     and   it.institution_number = p_institution_number
     and   it.TRANSACTION_STATUS != '011'
     group by (
               (case
                when it.transaction_category = '001' then
                  nvl(ltrim(it.merchant_number, '0'), 'unknown')
                else
                  it.client_number
                end
               ),
               (case
                when it.transaction_category = '001' then
                  nvl(it.merchant_number, 'unknown')
                else
                  it.client_number
                end
               ),
               it.business_class, it.acct_currency, it.record_date,
               it.transaction_date, it.transaction_destination,
               (case
                 when it.transaction_category = '002' and
                      it.record_date = it.value_date
                 then
                   it.transaction_category || 'cat'
                 else
                   it.transaction_type
                 end
               )
              )
    order by 1, 2, 3, 4;

  var_get_stats get_stats%Rowtype;

  type data_cont is record(

    MERCHANT_NUMBER     VARCHAR2(8),
    MERCHANT_NAME       VARCHAR2(50),
    RECORD_DATE         varchar2(8),
    CLEARING_CHANNEL    VARCHAR2(20),
    TRANSACTION_TYPE    VARCHAR2(28),
    TRANSACTION_DATE    varchar2(8),
    TOTAL_TRANS         NUMBER,
    TOTAL_TRANS_VALUE   NUMBER(16, 2),
    AVERAGE_TRANS_VALUE NUMBER(16, 2),
    TOTAL_COMMISSION    NUMBER(16, 2),
    CREATION_DATE       DATE,
    MERCHANT_CURRENCY   VARCHAR2(3),
    MCC                 VARCHAR2(4),
    MCC_DESCRIPTION     VARCHAR2(50));

  var_data_cont data_cont;

begin

  for rec_dat in rec_datum(rec_date, p_institution_number) loop

    if rec_dat.items > 0 then
      return;

      dbms_output.put_line('Stat for the day ' || rec_date ||
                           ' was already done ');
    end if;

  end loop;

  for var_get_stats in get_stats loop

    --if flag != 'first'  and flag != var_get_stats.merch  then
    --schreibe und ini

    begin

      select company_name
        into var_data_cont.MERCHANT_NAME
        from cis_client_details
       where var_get_stats.c_num = client_number
         and institution_number = p_institution_number;

   exception
      when others then
       var_data_cont.MERCHANT_NAME := 'NO Record';

    end;

    if instr(var_get_stats."the real type", 'cat') > 0 then -- If cat is found then this means that it is Transaction Category ...

      begin
        --
        select transaction_category
          into var_data_cont.TRANSACTION_TYPE
          from bwt_transaction_category
         where replace(var_get_stats."the real type", 'cat') = index_field
           and language = 'USA'
           and institution_number = p_institution_number;
        --
      exception
        when others then
         var_data_cont.TRANSACTION_TYPE := 'NO Record';

      end;

    else  -- ... otherwise it is Transaction Type.

      --
      select transaction_type
        into var_data_cont.TRANSACTION_TYPE
        from bwt_transaction_type
       where var_get_stats."the real type" = index_field
         and language = 'USA'
         and institution_number = p_institution_number;
       --
    end if;

    begin
      --
      select clearing_channel
        into var_data_cont.CLEARING_CHANNEL
        from bwt_clearing_channel
       where var_get_stats.transaction_destination = index_field
         and language = 'USA'
         and institution_number = p_institution_number;
      --
    exception
      when others then
        var_data_cont.CLEARING_CHANNEL := 'NO Record';
    end;

    begin
      --
      select swift_code
        into var_data_cont.MERCHANT_CURRENCY
        from bwt_currency
       where var_get_stats.acct_currency = iso_code
         and language = 'USA'
         and institution_number = p_institution_number;
      --
    exception
      when others then
        var_data_cont.MERCHANT_CURRENCY := 'NO Record';
    end;

    begin
      --
      select business_class
        into var_data_cont.MCC_DESCRIPTION
        from bwt_iso_buss_class
       where var_get_stats.business_class = index_field
         and language = 'USA'
         and institution_number = p_institution_number;
      --
    exception
      when others then
        var_data_cont.MCC_DESCRIPTION := 'NO Record';
    end;
    --
    insert into MERCHANT_DAILY_REPORT
    values
      (
         var_get_stats.RECORD_DATE,
         var_get_stats.transaction_date,
         var_get_stats.MERCH,
         var_data_cont.MERCHANT_NAME,
         var_data_cont.CLEARING_CHANNEL,
         substr (var_data_cont.TRANSACTION_TYPE, 20),
         var_get_stats.counts,
         var_get_stats.loc_gr,
         round(var_get_stats.loc_gr / var_get_stats.counts, 2),
         var_get_stats.loc_chg,
         var_data_cont.MERCHANT_CURRENCY,
         var_get_stats.business_class,
         var_data_cont.MCC_DESCRIPTION,
         sysdate,
         p_institution_number
      );

      commit;

/*
        dbms_output.put_line (

                var_get_stats.MERCH || '  '||
                var_data_cont.MERCHANT_NAME  || '  '||
                var_get_stats.RECORD_DATE  || '  '||
                var_data_cont.CLEARING_CHANNEL || '  '||
                var_data_cont.TRANSACTION_TYPE || '  '||
                var_get_stats.transaction_date || '  '||
                var_get_stats.counts  || '  '||
                var_get_stats.loc_gr || '  '||
                round (var_get_stats.loc_gr/var_get_stats.loc_gr, 2) || '  '||
                var_get_stats.loc_chg || '  '||
                sysdate || '  '||
                var_data_cont.MERCHANT_CURRENCY || '  '||
                var_get_stats.business_class || '  '||
                var_data_cont.MCC_DESCRIPTION
                );
*/

  end loop;

end Merchant_daily_stats;
