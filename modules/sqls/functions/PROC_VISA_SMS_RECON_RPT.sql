FUNCTION proc_visa_sms_recon_rpt(
    p_institution_number IN VARCHAR2,
    p_disable_client_number IN VARCHAR2,
    p_single_file_number IN VARCHAR2 DEFAULT NULL,
    p_multiple_file_number IN VARCHAR2 DEFAULT NULL,
    p_single_client_number IN VARCHAR2 DEFAULT NULL,
    p_multiple_client_number IN VARCHAR2 DEFAULT NULL,
    p_merchant_numbers IN VARCHAR2 DEFAULT NULL,
    p_is_matched IN VARCHAR2 DEFAULT NULL
)
  return tab_sms_recon pipelined
as
  v_select_clause clob;
  v_join_clause clob;
  v_where_clause clob;
  v_tmp_count number;
  visa_sms_info tab_sms_recon;

begin
  --

    v_select_clause := 'SELECT  /*+index(hstiss IX5_COS_BPR_DATA) index(bprvisa IX2_COS_BPR_DATA) */
              row_sms_recon(tran.record_date,
                        rawdata.record_date,
                        rawdata.settlement_date,
                        tran.institution_number,
                        lic.institution_name,
                        tran.file_number,
                        tran.transaction_slip,
                        bprvisa.acquiring_inst_id,
                        bprvisa.merchant_id,
                        bprvisa.terminal_id,
                        TO_CHAR(bprvisa.transaction_date, ''YYYYMMDD''),
                        bprvisa.transaction_link_id,
                        bprvisa.retrieval_reference,
                        bprvisa.stan,
                        bprvisa.process_code,
                        bprvisa.transmission_date,
                        currency.swift_code,
                        bprvisa.requested_amount,
                        DECODE(bprvisa.message_type, ''0400'', ''yes'', ''no''),
                        DECODE(tran.institution_number, rawdata.institution_number, ''1'', ''0'')' ;


    v_join_clause := '  FROM int_transactions tran
                      INNER JOIN cos_bpr_data bprvisa
                          ON bprvisa.institution_number = tran.institution_number
                          AND bprvisa.auth_code = tran.auth_code
                          AND bprvisa.card_number = tran.card_number
                          AND bprvisa.message_type = ''0210''
                          AND bprvisa.response_code = ''00''
                          AND bprvisa.bpr_type_name IN (SELECT bpr_type_name
                                      FROM bwt_destination_bpr
                                      WHERE institution_number = bprvisa.institution_number
                                        AND language = ''USA''
                                        AND card_org = BWTPAD(''BWT_CARD_ORGANIZATION'', ''003''))
                      INNER JOIN cos_bpr_data hstiss
                          ON hstiss.institution_number = tran.institution_number
                              AND hstiss.retrieval_reference = tran.retrieval_reference
                              AND hstiss.auth_code = tran.auth_code
                              AND hstiss.card_number = tran.card_number
                              AND hstiss.transaction_link_id = bprvisa.transaction_link_id
                              AND hstiss.message_type = ''0210''
                              AND hstiss.response_code = ''00''
                              AND hstiss.bpr_type_name in (''BPR_HST_ISS'',''BPR_INTRA'')
                      LEFT OUTER JOIN int_visa_rawdata rawdata
                          ON rawdata.institution_number = tran.institution_number
                              AND rawdata.retrieval_reference = bprvisa.retrieval_reference
                              AND rawdata.auth_code = bprvisa.auth_code
                      LEFT OUTER JOIN bwt_currency currency
                          ON currency.institution_number = tran.institution_number
                              AND currency.iso_code = bprvisa.requested_currency
                              AND currency.language = ''USA''
                      INNER JOIN sys_institution_licence lic
                          ON lic.institution_number = tran.institution_number';

    v_where_clause := ' WHERE tran.transaction_class = bwtPad(''BWT_TRANSACTION_CLASS'',''002'')
                        AND tran.transaction_source IN (bwtPad(''CHT_CLEARING_CHANNEL'', ''015''), bwtPad(''CHT_CLEARING_CHANNEL'', ''744''))
                        AND tran.institution_number = :institution_number';

    -- Dynamically add the client number column
    IF p_disable_client_number != '1' THEN
        v_select_clause := v_select_clause || ', cdl.CLIENT_NUMBER ) ';
        v_join_clause := v_join_clause || ' INNER JOIN cis_device_link cdl
                        ON cdl.institution_number = bprvisa.institution_number
                            AND cdl.merchant_id = bprvisa.merchant_id
                            AND cdl.terminal_id = bprvisa.terminal_id ';
    ELSE
        v_select_clause := v_select_clause || ', '''' ) ';
    END IF;

    IF p_single_file_number IS NOT NULL THEN
      v_where_clause := v_where_clause || ' AND tran.file_number = ' || p_single_file_number ;
    END IF;

    IF p_multiple_file_number IS NOT NULL THEN
        v_where_clause := v_where_clause || ' AND tran.file_number IN (' || p_multiple_file_number || ') ';
    END IF;

    IF p_single_client_number IS NOT NULL THEN
      v_where_clause := v_where_clause || ' AND cdl.client_number = '|| p_single_client_number;
    END IF;

    IF p_multiple_client_number IS NOT NULL THEN
      v_where_clause := v_where_clause || ' AND cdl.client_number IN (' || p_multiple_client_number || ') ';
    END IF;

    IF p_merchant_numbers IS NOT NULL THEN
      v_where_clause := v_where_clause || ' AND cdl.client_number IN (' || p_merchant_numbers || ') ';
    END IF;

    IF p_is_matched IS NOT NULL THEN
      v_where_clause := v_where_clause || ' AND DECODE(tran.institution_number, rawdata.institution_number, ''1'', ''0'') = ' || p_is_matched ;
    END IF;

    execute immediate v_select_clause || v_join_clause || v_where_clause BULK COLLECT INTO visa_sms_info using p_institution_number;

    FOR i IN 1 .. visa_sms_info.COUNT
    LOOP
        PIPE row(row_sms_recon(
            visa_sms_info(i).RECORD_DATE,
            visa_sms_info(i).POSTING_DATE,
            visa_sms_info(i).SETTLEMENT_DATE,
            visa_sms_info(i).INSTITUTION_NUMBER,
            visa_sms_info(i).INSTITUTION_NAME,
            visa_sms_info(i).FILE_NUMBER,
            visa_sms_info(i).TRANSACTION_SLIP,
            visa_sms_info(i).ACQ_INSTITUTION_ID,
            visa_sms_info(i).CARD_ACCEPTOR_ID,
            visa_sms_info(i).CARD_ACCEPTOR_TERMINAL_ID,
            visa_sms_info(i).TRANSACTION_DATE,
            visa_sms_info(i).TRAN_LINK_ID,
            visa_sms_info(i).RETRIEVAL_REF_NUMBER,
            visa_sms_info(i).TRACE_NUMBER,
            visa_sms_info(i).PROCESSING_CODE,
            visa_sms_info(i).TRANSMISSION_DATETIME,
            visa_sms_info(i).TRAN_CURRENCY,
            visa_sms_info(i).TRAN_AMOUNT_GR,
            visa_sms_info(i).REVERSAL_FLAG,
            visa_sms_info(i).ISMATCHED,
            visa_sms_info(i).CLIENT_NUMBER
       ));
    END LOOP;

exception
  when others then
    raise_application_error(-20022, 'Error in proc_visa_sms_recon_rpt:'||sqlerrm||dbms_utility.format_error_backtrace);
end proc_visa_sms_recon_rpt;