PROCEDURE GENERATE_BWT_GENERIC_RETRIEVAL_REASON AS
    C INT;
BEGIN
    SELECT COUNT(*) INTO c FROM all_tables WHERE table_name = 'BWT_GENERIC_RETRIEVAL_REASON';
    IF (c != 0) THEN
        EXECUTE IMMEDIATE 'DROP TABLE BWT_GENERIC_RETRIEVAL_REASON';
    END IF;
    SELECT COUNT(*) INTO c FROM all_views WHERE view_name = 'BWT_GENERIC_RETRIEVAL_REASON';
    IF (c != 0) THEN
        EXECUTE IMMEDIATE 'DROP VIEW BWT_GENERIC_RETRIEVAL_REASON';
    END IF;

    EXECUTE IMMEDIATE 'CREATE TABLE BWT_GENERIC_RETRIEVAL_REASON AS
                        SELECT
                            INDEX_FIELD,
                            VISA_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            VISA_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''003'') AS CARD_ORGANIZATION, -- VISA
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_RETRIEVAL_REASON
                        WHERE VISA_REASON_CODE IS NOT NULL
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
                        FROM BWT_RETRIEVAL_REASON
                        WHERE IPM_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            AMEX_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            AMEX_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''004'') AS CARD_ORGANIZATION, -- AMEX
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_RETRIEVAL_REASON
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
                        FROM BWT_RETRIEVAL_REASON
                        WHERE DINERS_REASON_CODE IS NOT NULL
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
                        FROM BWT_RETRIEVAL_REASON
                        WHERE JCB_REASON_CODE IS NOT NULL
                        UNION
                        SELECT
                            INDEX_FIELD,
                            DISCOVER_REASON_CODE AS REASON_CODE,
                            SUNDRY_REASON AS REASON_DESCRIPTION,
                            DISCOVER_REASON_CODE || '' - '' || SUNDRY_REASON AS REASON_CODE_DESCRIPTION,
                            BWTPAD(''BWT_CARD_ORGANIZATION'', ''023'') AS CARD_ORGANIZATION,
                            LANGUAGE,
                            GROUPS,
                            INSTITUTION_NUMBER
                        FROM BWT_RETRIEVAL_REASON
                        WHERE DISCOVER_REASON_CODE IS NOT NULL';
END;