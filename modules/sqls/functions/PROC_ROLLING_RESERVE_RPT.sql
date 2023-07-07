FUNCTION proc_rolling_reserve_rpt(
    p_institution_number IN VARCHAR2,
    p_client_number IN VARCHAR2,
    p_client_type IN VARCHAR2,
    p_date_cycle_end IN VARCHAR2,
    p_service_contract_id IN VARCHAR2,
    p_account_type_id IN VARCHAR2
)
  return tab_rolling_reserve pipelined
AS
BEGIN
    for rec in (
        SELECT 	ACCT.CLIENT_NUMBER,
              DTL.OUR_REFERENCE AS MID,
              ACCT.ACCT_NUMBER AS ACCOUNT_NUMBER,
              ACCT.ACCOUNT_TYPE_ID,
              ATI.TYPE_ID AS ACCOUNT_TYPE,
              BAL.CURRENT_BALANCE AS BALANCE,
              INST.TOTAL_AMOUNT_LIMIT AS THRESHOLD,
              CUR.SWIFT_CODE AS CURRENCY,
              ACCT.INSTITUTION_NUMBER,
              ACCT.SERVICE_CONTRACT_ID,
              BAL.DATE_CYCLE_END
          FROM
            CAS_PAYMENT_INSTRUCTION INST,
            CAS_CLIENT_ACCOUNT ACCT,
            CIS_CLIENT_LINKS LNK,
            CIS_CLIENT_DETAILS DTL,
            CAS_CYCLE_BOOK_BALANCE BAL,
            BWT_ACCOUNT_TYPE_ID ATI,
            BWT_CURRENCY CUR
          WHERE INST.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND INST.DEST_ACCT_NUMBER = ACCT.ACCT_NUMBER
          AND ACCT.ACCOUNT_TYPE_ID IN (BWTPad('BWT_ACCOUNT_TYPE_ID', '059'),
                                       BWTPad('BWT_ACCOUNT_TYPE_ID', '062'))
          --
          AND LNK.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND LNK.CLIENT_NUMBER = ACCT.CLIENT_NUMBER
          AND LNK.GROUP_NUMBER = ACCT.GROUP_NUMBER
          AND LNK.SERVICE_CONTRACT_ID = ACCT.SERVICE_CONTRACT_ID
          AND LNK.CLIENT_LEVEL = BWTPad('BWT_CLIENT_LEVEL','001')
          AND LNK.EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE)
                        FROM CIS_CLIENT_LINKS
                        WHERE INSTITUTION_NUMBER = LNK.INSTITUTION_NUMBER
                        AND CLIENT_NUMBER = LNK.CLIENT_NUMBER
                        AND GROUP_NUMBER = LNK.GROUP_NUMBER
                        AND SERVICE_CONTRACT_ID = LNK.SERVICE_CONTRACT_ID
                        AND CLIENT_LEVEL = LNK.CLIENT_LEVEL
                        AND EFFECTIVE_DATE <= BAL.DATE_CYCLE_END
                        AND EXPIRY_DATE >= BAL.DATE_CYCLE_END )
          --
          AND DTL.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND DTL.CLIENT_NUMBER = ACCT.CLIENT_NUMBER
          --
          AND BAL.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND BAL.ACCT_NUMBER = ACCT.ACCT_NUMBER
          --
          AND ATI.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND ATI.LANGUAGE = 'USA'
          AND ATI.INDEX_FIELD = ACCT.ACCOUNT_TYPE_ID
          --
          AND CUR.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
          AND CUR.LANGUAGE = 'USA'
          AND CUR.ISO_CODE = ACCT.ACCT_CURRENCY
          AND (p_client_number IS NULL
              or ACCT.CLIENT_NUMBER IN (SELECT CLIENT_NUMBER FROM CIS_CLIENT_RELATION_LINKS
                                    START WITH INSTITUTION_NUMBER = p_institution_number AND DEST_CLIENT_NUMBER = p_client_number AND DEST_INSTITUTION_NUMBER = INSTITUTION_NUMBER
                                    CONNECT BY NOCYCLE
                                    PRIOR INSTITUTION_NUMBER = INSTITUTION_NUMBER
                                    AND PRIOR DEST_INSTITUTION_NUMBER = DEST_INSTITUTION_NUMBER
                                    AND PRIOR CLIENT_NUMBER = DEST_CLIENT_NUMBER
                                    AND LINK_TYPE IN (
                                        SELECT LINK_TYPE FROM BWT_LINK_TYPE
                                        START WITH INSTITUTION_NUMBER = CIS_CLIENT_RELATION_LINKS.INSTITUTION_NUMBER AND DESTINATION_CLIENT_TYPE = p_client_type
                                        CONNECT BY NOCYCLE
                                        PRIOR INSTITUTION_NUMBER = INSTITUTION_NUMBER
                                        AND PRIOR SOURCE_CLIENT_TYPE = DESTINATION_CLIENT_TYPE
                                    )))
          AND (p_institution_number IS NULL
              or ACCT.INSTITUTION_NUMBER = p_institution_number)
          AND ((p_date_cycle_end IS NULL AND BAL.PROCESSING_STATUS = BWTPad('BWT_PROCESSING_STATUS','004'))
              or BAL.DATE_CYCLE_END = p_date_cycle_end)
          AND (p_service_contract_id IS NULL
              or ACCT.SERVICE_CONTRACT_ID = p_service_contract_id)
          AND (p_account_type_id IS NULL
              or ACCT.ACCOUNT_TYPE_ID = p_account_type_id)
    ) loop

    pipe row(row_rolling_reserve(rec.CLIENT_NUMBER, rec.MID, rec.ACCOUNT_NUMBER, rec.ACCOUNT_TYPE_ID, rec.ACCOUNT_TYPE, rec.BALANCE, rec.THRESHOLD, rec.CURRENCY, rec.INSTITUTION_NUMBER, rec.SERVICE_CONTRACT_ID, rec.DATE_CYCLE_END
    ));

  end loop;
exception
  WHEN others THEN
    raise_application_error(-20022, 'Error in proc_rolling_reserve_rpt:'||sqlerrm||dbms_utility.format_error_backtrace);
END proc_rolling_reserve_rpt;