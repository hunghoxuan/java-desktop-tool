procedure POPULATE_INCOMING_TRAN_TABLE (
    p_institution_number IN VARCHAR2,
    p_file_number IN FILE_DETAILS_ARRAY,
    p_file_name IN FILE_DETAILS_ARRAY,
    p_get_file_id IN FILE_DETAILS_ARRAY, --getFileNumber("FILE_ID =" & strFileID , blnWithInstitution) getFileNumberForId
    p_date_criteria IN VARCHAR2,
    p_start_date VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
    p_end_date VARCHAR2 DEFAULT TO_CHAR(SYSDATE, 'YYYYMMDD'),
    p_filter_by_settlement_date IN VARCHAR2 DEFAULT 'FALSE',
    p_rpt_id VARCHAR2  --(Institution Number || Full Date - YYYYMMDDHH24MMSS)
) AS
    v_sql   CLOB;
    v_where_clause VARCHAR2(4000);
    v_view_select CLOB;
    v_timestamp VARCHAR2(24);
    v_using VARCHAR2(4000);
    v_column_name VARCHAR2(50);
    v_trans_status_entered VARCHAR2(3); --002
    v_trans_status_paid VARCHAR2(3); --004
    v_trans_status_matched VARCHAR2(3); --006
    v_trans_status_processed VARCHAR2(3); --007
    v_trans_status_cleared VARCHAR2(3); --009
    v_trans_status_reprocessed VARCHAR2(3); --011
    v_trans_status_chargebacked VARCHAR2(3); --050
    v_trans_class_clearing VARCHAR2(3); --002
    v_trans_class_non_financial VARCHAR2(3); --011
    v_trans_class_settlement VARCHAR2(3); --005
    v_process_name_base VARCHAR2(6); --609
    v_process_name_inward_mc VARCHAR2(6); --366
    v_process_name_visa_base VARCHAR2(6); --003
    v_process_name_visa_inw_iss VARCHAR(6); --187
    v_process_name_pos_nt VARCHAR2(6); --455
    v_process_name_inet_bin_load VARCHAR2(6); --086
    v_process_name_mc_ipm_inward VARCHAR2(6); --621
    v_process_name_recovery_loader VARCHAR2(6); --001434
    v_process_name_elo_set_file_inw VARCHAR2(6); --001484
    v_process_name_elo_settlement VARCHAR2(6); --001075
    v_completed_process_status VARCHAR2(3); --002
    v_decimal_config_value VARCHAR2(4000);
    v_thousand_config_value VARCHAR2(4000);
BEGIN
  v_timestamp :=  to_char(to_date(substr(p_rpt_id, 9),'YYYYMMDDHH24miSS'),'YYYYMMDD');
  v_trans_status_entered := BWTPad('BWT_TRANSACTION_STATUS','002');
  v_trans_status_paid := BWTPad('BWT_TRANSACTION_STATUS','004');
  v_trans_status_matched := BWTPad('BWT_TRANSACTION_STATUS','006');
  v_trans_status_processed := BWTPad('BWT_TRANSACTION_STATUS','007');
  v_trans_status_cleared := BWTPad('BWT_TRANSACTION_STATUS','009');
  v_trans_status_reprocessed := BWTPad('BWT_TRANSACTION_STATUS','011');
  v_trans_status_chargebacked := BWTPad('BWT_TRANSACTION_STATUS','050');
  v_trans_class_clearing := BWTPad('BWT_TRANSACTION_CLASS','002');
  v_trans_class_non_financial := BWTPad('BWT_TRANSACTION_CLASS','011');
  v_trans_class_settlement := BWTPad('BWT_TRANSACTION_CLASS','005');
  v_process_name_base := BWTPad('BWT_PROCESS_NAME','609');
  v_process_name_inward_mc := BWTPad('BWT_PROCESS_NAME','366');
  v_process_name_visa_base := BWTPad('BWT_PROCESS_NAME','003');
  v_process_name_visa_inw_iss := BWTPad('BWT_PROCESS_NAME','187');
  v_process_name_pos_nt := BWTPad('BWT_PROCESS_NAME','455');
  v_process_name_inet_bin_load := BWTPad('BWT_PROCESS_NAME','086');
  v_process_name_mc_ipm_inward := BWTPad('BWT_PROCESS_NAME','621');
  v_process_name_recovery_loader := BWTPad('BWT_PROCESS_NAME','001434');
  v_process_name_elo_set_file_inw := BWTPad('BWT_PROCESS_NAME','001484');
  v_process_name_elo_settlement := BWTPad('BWT_PROCESS_NAME','001075');
  v_completed_process_status := BWTPad('BWT_PROCESSING_STATUS','002');

  -- Get the config values from sys_configuration table
  SELECT NVL(CONFIG_VALUE,'.') INTO v_decimal_config_value FROM (SELECT CONFIG_VALUE FROM SYS_CONFIGURATION WHERE INSTITUTION_NUMBER IN (p_institution_number, '00000000') AND CONFIG_KEYWORD = 'AmountDecimal' ORDER BY INSTITUTION_NUMBER DESC) WHERE ROWNUM = 1;
  SELECT NVL(CONFIG_VALUE,',') INTO v_thousand_config_value FROM (SELECT CONFIG_VALUE FROM SYS_CONFIGURATION WHERE INSTITUTION_NUMBER IN (p_institution_number, '00000000') AND CONFIG_KEYWORD = 'AmountThousands' ORDER BY INSTITUTION_NUMBER DESC) WHERE ROWNUM = 1;

  --Delete records older than 5 days.
  DELETE FROM INCOMING_TRAN_TABLE WHERE substr(RPT_ID,9,8) < to_char(to_date(v_timestamp,'YYYYMMDD') - 5,'YYYYMMDD');
  --Build the where clause according to the data passed from the mask
  v_where_clause := v_where_clause || ' AND LIC.INSTITUTION_NUMBER = :p_institution_number';
  --If start date and end date are not empty, they will be included in where clause.
  IF p_start_date IS NOT NULL AND p_end_date IS NOT NULL THEN
    IF p_date_criteria = 'RECORD_DATE' THEN
      v_column_name := 'LOG.RECORD_DATE';
    ELSE
      v_column_name := 'TRN.SETTLEMENT_DATE';
    END IF;

    v_where_clause := v_where_clause || ' AND ' || v_column_name || ' >= :p_start_date AND ' || v_column_name || ' <= :p_end_date';
  ELSE
    --when a parameter is not set, a line in the form of "AND ((1=1) OR :bind IS NULL)" is included in the statement.
    --This works this way at parse time, the query optimizer sees that "1=1" is always true, therefore this whole OR expression must always equate to true,
    --so it throws the predicate away.
    v_where_clause := v_where_clause || ' AND ((1=1) OR (:p_start_date IS NULL AND :p_end_date IS NULL))';
  END IF;

  --If file number is not empty we include it in where clause
  IF p_file_number.COUNT > 0 THEN
    v_where_clause := v_where_clause || ' AND (LOG.FILE_NUMBER IN (SELECT COLUMN_VALUE FROM TABLE(:p_file_number)) ';
  ELSE
    v_where_clause := v_where_clause || ' AND (((1=1) OR exists (SELECT COLUMN_VALUE FROM TABLE(:p_file_number))) ';
  END IF;

  --If file name is not empty we include it in where clause
  IF p_file_name.COUNT > 0 THEN
    v_where_clause := v_where_clause || ' AND LOG.ORIGINAL_FILE_NAME IN (SELECT COLUMN_VALUE FROM TABLE(:p_file_name)) ';
  ELSE
    v_where_clause := v_where_clause || ' AND ((1=1) OR exists (SELECT COLUMN_VALUE FROM TABLE(:p_file_name))) ';
  END IF;

  --If p_get_file_id is true, we include the file details in where clause
  IF p_get_file_id.COUNT > 0 THEN
    v_where_clause := v_where_clause || ' AND LOG.FILE_ID IN (SELECT COLUMN_VALUE FROM TABLE(:p_get_file_id)))';
  ELSE
    v_where_clause := v_where_clause || ' AND ((1=1) OR exists (SELECT COLUMN_VALUE FROM TABLE(:p_get_file_id))))';
  END IF;

  --Building the select from the view
  IF p_filter_by_settlement_date = 'TRUE' THEN
    v_view_select := ' SELECT /*+ index(TRN IX10_TRN_FILE_OUT) */ ';
  ELSE
    v_view_select := ' SELECT ';
  END IF;

  v_view_select := v_view_select || '
                                      LIC.institution_number,
                                      LIC.institution_name,
                                      LOG.file_number,
                                      LOG.original_file_name,
                                      LOG.file_id,
                                      LOG.record_date,
                                      TRN.transaction_slip,
                                      batch.number_original_slip,
                                      TRN.summary_settlement,
                                      (TRN.transaction_category||TRN.transaction_class||TRN.transaction_type) transac_categclasstype_group,
                                      TRN.transaction_date,
                                      TRN.dr_cr_indicator,
                                      bw3_mask_card_no.DISGUISE_CARD_NO(TRN.CARD_NUMBER, NULL, NULL, NULL) AS card_number,
                                      TRN.merchant_name,
                                      TRN.settlement_amount_gr,
                                      TRN.settlement_amount_chg,
                                      TRN.settlement_amount_net,
                                      BSCUR.swift_code settlement_currency,
                                      BSCUR.exponent settlement_exponent,
                                      TRN.rate_fx_settl_local,
                                      TRN.local_amount_gr,
                                      TRN.local_amount_inw_chg,
                                      TRN.local_amount_inw_net,
                                      BLCUR.swift_code local_currency,
                                      BLCUR.exponent local_exponent,
                                      TRN.tran_amount_gr,
                                      TRN.tran_amount_chg,
                                      TRN.tran_amount_net,
                                      BTCUR.swift_code transaction_currency,
                                      BTCUR.exponent transaction_exponent,
                                      (TRN.settlement_currency||TRN.transaction_source) currency_clearingchannel_group,
                                      batch.client_number,
                                      BTSRC.clearing_channel transaction_source,
                                      TRN.transaction_class transaction_class_id, BTCLASS.transaction_class,
                                      BTCAT.transaction_category,
                                      BTTYP.transaction_type,
                                      BREV.confirmation reversal_flag_yesno,
                                      TRN.ACQUIRER_REFERENCE,
                                      TRN.INT_FEE_RULE,
                                      TO_NUMBER(NVL(TRNC.FEE_PERCENT, 0)) AS FEE_PERCENT,
                                      TO_NUMBER(NVL(TRN.LOCAL_AMOUNT_OUT_CHG, 0)) AS FEE_BASE,
                                      NVL(TRN.SETTLEMENT_AMOUNT_CHG, 0) + NVL(ADDC.TRANSACTION_AMOUNT, 0),
                                      SUBSTR(TRN.AUTHORIZED_BY,4,3),
                                      ADDAD.NETBANX_UUID,
                                      :v_decimal_config_value,
                                      :v_thousand_config_value,
                                      TRN.SETTLEMENT_DATE,
                                      TRN.ORIGINAL_REF_NUMBER,
                                      IAA.BOARDING_FEE_AMT,
                                      IAA.DOWN_PAYMENT,
                                      :p_rpt_id  AS RPT_ID
                                  FROM
                                      SYS_INSTITUTION_LICENCE LIC,
                                      INT_TRANSACTIONS TRN,
                                      INT_TRANSACTIONS BATCH,
                                      INT_FILE_LOG_DETAILS LOG, ';

  IF p_filter_by_settlement_date = 'TRUE' THEN
    v_view_select := v_view_select || ' INT_FILE_LOG_DETAILS LOG_OUTWARD,';
  END IF;

  v_view_select := v_view_select || '
                                      BWT_CLEARING_CHANNEL BTSRC,
                                      BWT_TRANSACTION_TYPE BTTYP,
                                      BWT_CONFIRMATION BREV,
                                      BWT_SIGN_OPERATORS BSOPER,
                                      BWT_CURRENCY BSCUR,
                                      BWT_CURRENCY BLCUR,
                                      BWT_CURRENCY BTCUR,
                                      BWT_TRANSACTION_CATEGORY BTCAT,
                                      BWT_TRANSACTION_CLASS BTCLASS,
                                      INT_ADDENDUM_ADDITIONAL_DATA ADDAD,
                                      (
                                          SELECT INSTITUTION_NUMBER, TRANSACTION_SLIP, SUM(TRANSACTION_AMOUNT) AS TRANSACTION_AMOUNT
                                          FROM INT_ADDENDUM_CHARGES
                                          GROUP BY INSTITUTION_NUMBER, TRANSACTION_SLIP
                                      ) ADDC,
                                      CBR_TRANSACTION_CHARGES TRNC,
                                      SYS_CONFIGURATION CONFDECIMAL,
                                      SYS_CONFIGURATION CONFTHOUSAND,
                                      INT_ADDENDUM_AIRLINE IAA
                                  WHERE
                                      LIC.institution_number = LOG.institution_number
                                      AND LOG.institution_number = TRN.institution_number
                                      AND LOG.file_number = TRN.file_number ';

  IF p_filter_by_settlement_date = 'TRUE' THEN
    v_view_select := v_view_select || ' AND (LOG.process_name IN (:v_process_name_base,
                                                               :v_process_name_inward_mc,
                                                               :v_process_name_visa_base,
                                                               :v_process_name_visa_inw_iss,
                                                               :v_process_name_pos_nt,
                                                               :v_process_name_inet_bin_load,
                                                               :v_process_name_mc_ipm_inward,
                                                               :v_process_name_recovery_loader,
                                                               :v_process_name_elo_set_file_inw,
                                                               :v_process_name_elo_settlement)
                                      AND LOG_OUTWARD.file_number = TRN.file_number_outward)
                                      AND LOG.processing_status = :v_completed_process_status ';

    --In the last line we added 5-day range in case processing is skipped by a day or two
    v_view_select := v_view_select || ' AND LOG_OUTWARD.institution_number = TRN.institution_number
                                        AND LOG_OUTWARD.processing_status = BWTPad(''BWT_PROCESSING_STATUS'',''002'')
                                        AND LOG_OUTWARD.record_date BETWEEN :p_start_date AND TO_CHAR(TO_DATE(:p_end_date , ''YYYYMMDD'') + 5, ''YYYYMMDD'')';

  ELSE

    v_view_select := v_view_select || ' AND LOG.process_name IN (:v_process_name_base,
                                                               :v_process_name_inward_mc,
                                                               :v_process_name_visa_base,
                                                               :v_process_name_visa_inw_iss,
                                                               :v_process_name_pos_nt,
                                                               :v_process_name_inet_bin_load,
                                                               :v_process_name_mc_ipm_inward,
                                                               :v_process_name_recovery_loader,
                                                               :v_process_name_elo_set_file_inw,
                                                               :v_process_name_elo_settlement)
                                        AND LOG.processing_status = :v_completed_process_status ';
    v_view_select := v_view_select || ' AND ((1=1) OR (:p_start_date IS NULL AND :p_end_date IS NULL)) ';

  END IF;

  v_view_select := v_view_select || '     AND (TRN.transaction_status IN (:v_trans_status_entered,
                                                                          :v_trans_status_paid,
                                                                          :v_trans_status_matched,
                                                                          :v_trans_status_processed,
                                                                          :v_trans_status_cleared,
                                                                          :v_trans_status_reprocessed,
                                                                          :v_trans_status_chargebacked)
                                      OR TRN.transaction_status like ''%2__'')
          AND --Any value which has the third from last character equals to 2
          TRN.transaction_class IN (:v_trans_class_clearing,
                                    :v_trans_class_non_financial,
                                    :v_trans_class_settlement)
          AND TRN.institution_number = BTCLASS.institution_number(+)
          AND TRN.transaction_class = BTCLASS.index_field(+)
          AND BTCLASS.language(+) = ''USA''
          AND TRN.institution_number = BTCAT.institution_number(+)
          AND TRN.transaction_category = BTCAT.index_field(+)
          AND BTCAT.language(+) = ''USA''
          AND TRN.institution_number = BATCH.institution_number(+)
          AND TRN.summary_settlement = BATCH.transaction_slip(+)
          AND TRN.institution_number = BTSRC.institution_number(+)
          AND TRN.transaction_source = BTSRC.index_field(+)
          AND BTSRC.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BTTYP.INSTITUTION_NUMBER(+)
          AND TRN.TRANSACTION_TYPE = BTTYP.INDEX_FIELD(+)
          AND BTTYP.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BREV.INSTITUTION_NUMBER(+)
          AND TRN.REVERSAL_FLAG = BREV.INDEX_FIELD(+)
          AND BREV.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BSOPER.INSTITUTION_NUMBER(+)
          AND TRN.DR_CR_INDICATOR = BSOPER.INDEX_FIELD(+)
          AND BSOPER.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BSCUR.INSTITUTION_NUMBER(+)
          AND TRN.SETTLEMENT_CURRENCY = BSCUR.ISO_CODE(+)
          AND BSCUR.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BLCUR.INSTITUTION_NUMBER(+)
          AND TRN.LOCAL_CURRENCY = BLCUR.ISO_CODE(+)
          AND BLCUR.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = BTCUR.INSTITUTION_NUMBER(+)
          AND TRN.TRAN_CURRENCY = BTCUR.ISO_CODE(+)
          AND BTCUR.LANGUAGE(+) = ''USA''
          AND TRN.INSTITUTION_NUMBER = ADDAD.INSTITUTION_NUMBER(+)
          AND TRN.TRANSACTION_SLIP = ADDAD.TRANSACTION_SLIP (+)
          AND TRN.INSTITUTION_NUMBER = ADDC.INSTITUTION_NUMBER(+)
          AND TRN.TRANSACTION_SLIP = ADDC.TRANSACTION_SLIP (+)
          AND TRN.INSTITUTION_NUMBER = TRNC.INSTITUTION_NUMBER(+)
          AND TRN.OUTWARD_FEE_NUMBER = TRNC.RECORD_ID_NUMBER (+)
          AND TRN.INSTITUTION_NUMBER = IAA.INSTITUTION_NUMBER(+)
          AND TRN.TRANSACTION_SLIP = IAA.TRANSACTION_SLIP(+)
          AND CONFDECIMAL.INSTITUTION_NUMBER(+) = LIC.INSTITUTION_NUMBER
          AND CONFDECIMAL.CONFIG_KEYWORD(+) = ''AmountDecimal''
          AND CONFTHOUSAND.INSTITUTION_NUMBER(+) = LIC.INSTITUTION_NUMBER
          AND CONFTHOUSAND.CONFIG_KEYWORD(+) = ''AmountThousands'' ';

  v_sql := 'INSERT INTO INCOMING_TRAN_TABLE (INSTITUTION_NUMBER,
                                             INSTITUTION_NAME,
                                             FILE_NUMBER,
                                             ORIGINAL_FILE_NAME,
                                             FILE_ID, RECORD_DATE,
                                             TRANSACTION_SLIP,
                                             NUMBER_ORIGINAL_SLIP,
                                             SUMMARY_SETTLEMENT,
                                             TRANSAC_CATEGCLASSTYPE_GROUP,
                                             TRANSACTION_DATE,
                                             DR_CR_INDICATOR,
                                             CARD_NUMBER,
                                             MERCHANT_NAME,
                                             SETTLEMENT_AMOUNT_GR,
                                             SETTLEMENT_AMOUNT_CHG,
                                             SETTLEMENT_AMOUNT_NET,
                                             SETTLEMENT_CURRENCY,
                                             SETTLEMENT_EXPONENT,
                                             RATE_FX_SETTL_LOCAL,
                                             LOCAL_AMOUNT_GR,
                                             LOCAL_AMOUNT_INW_CHG,
                                             LOCAL_AMOUNT_INW_NET,
                                             LOCAL_CURRENCY,
                                             LOCAL_EXPONENT,
                                             TRAN_AMOUNT_GR,
                                             TRAN_AMOUNT_CHG,
                                             TRAN_AMOUNT_NET,
                                             TRANSACTION_CURRENCY,
                                             TRANSACTION_EXPONENT,
                                             CURRENCY_CLEARINGCHANNEL_GROUP,
                                             CLIENT_NUMBER,
                                             TRANSACTION_SOURCE,
                                             TRANSACTION_CLASS_ID,
                                             TRANSACTION_CLASS,
                                             TRANSACTION_CATEGORY,
                                             TRANSACTION_TYPE,
                                             REVERSAL_FLAG_YESNO,
                                             ACQUIRER_REFERENCE,
                                             INT_FEE_RULE,
                                             FEE_PERCENT,
                                             FEE_BASE,
                                             PER_ITEM_FEE,
                                             AREA_OF_EVENT,
                                             UUID,
                                             AMOUNTDECIMAL,
                                             AMOUNTTHOUSANDS,
                                             SETTLEMENT_DATE,
                                             ORIGINAL_REF_NUMBER,
                                             BOARDING_FEE_AMT,
                                             DOWN_PAYMENT,
                                             RPT_ID)'
                                             || v_view_select || v_where_clause;

    --Insert records in INCOMING_TRAN_TABLE
    EXECUTE IMMEDIATE v_sql USING v_decimal_config_value, v_thousand_config_value, p_rpt_id,
                                  v_process_name_base, v_process_name_inward_mc, v_process_name_visa_base, v_process_name_visa_inw_iss, v_process_name_pos_nt,
                                  v_process_name_inet_bin_load, v_process_name_mc_ipm_inward, v_process_name_recovery_loader, v_process_name_elo_set_file_inw,
                                  v_process_name_elo_settlement, v_completed_process_status, p_start_date, p_end_date, v_trans_status_entered,
                                  v_trans_status_paid, v_trans_status_matched, v_trans_status_processed, v_trans_status_cleared, v_trans_status_reprocessed,
                                  v_trans_status_chargebacked, v_trans_class_clearing, v_trans_class_non_financial, v_trans_class_settlement,
                                  p_institution_number, p_start_date, p_end_date, p_file_number, p_file_name, p_get_file_id;

END;