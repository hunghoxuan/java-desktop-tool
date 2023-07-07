PROCEDURE GENERATE_BWT_GENERIC_CHARGEBACK_REASON AS
    C INT;
BEGIN
    SELECT COUNT(*) INTO c FROM all_tables WHERE table_name = 'BWT_GENERIC_CHARGEBACK_REASON';
    IF (c != 0) THEN
        EXECUTE IMMEDIATE 'DROP TABLE BWT_GENERIC_CHARGEBACK_REASON';
    END IF;

    EXECUTE IMMEDIATE 'CREATE TABLE BWT_GENERIC_CHARGEBACK_REASON AS
                        SELECT
                            INDEX_FIELD,
                            VISA_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            VISA_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''003'') AS CARD_ORGANIZATION, -- VISA
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE VISA_REASON_CODE IS NOT NULL
                        AND GROUPS = ''V''
                        UNION
                        SELECT
                            INDEX_FIELD,
                            VCR_REASON_CODE || ''.'' || VCR_DISPUTE_CONDITION AS REASON_CODE,
                            DISPUTE_CONDITION AS REASON_DESCRIPTION,
                            VCR_REASON_CODE || ''.'' || VCR_DISPUTE_CONDITION  || '' - '' || DISPUTE_CONDITION AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''003'') AS CARD_ORGANIZATION, -- VISA
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_VCR_DISPUTE_CONDITION
                        UNION
                        SELECT
                            INDEX_FIELD,
                            IPM_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            IPM_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''002'') AS CARD_ORGANIZATION, -- MASTERCARD
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE IPM_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            AMEX_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            AMEX_REASON_CODE|| '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''004'') AS CARD_ORGANIZATION, -- AMEX
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE AMEX_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            DINERS_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            DINERS_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''006'') AS CARD_ORGANIZATION, -- DINERS
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE DINERS_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            CUP_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            CUP_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''001'') AS CARD_ORGANIZATION, -- CUP
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE CUP_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            JCB_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            JCB_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''007'') AS CARD_ORGANIZATION, -- JCB
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE JCB_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            ELO_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            ELO_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''203'') AS CARD_ORGANIZATION, -- ELO
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE ELO_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            DISCOVER_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            DISCOVER_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''023'') AS CARD_ORGANIZATION, -- DISCOVER
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_CHARGEBACK_REASON
                        WHERE DISCOVER_REASON_CODE IS NOT NULL';
END;