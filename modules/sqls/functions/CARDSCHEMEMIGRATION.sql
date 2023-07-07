PROCEDURE CardSchemeMigration
(
  P_INST_NO                VARCHAR2,
  P_STATION_NO             VARCHAR2,
  P_USER_ID                VARCHAR2,
  P_CARD_ORG               VARCHAR2,
  P_NEW_ACCT_TYPE          VARCHAR2,
  P_SRC_ACCT_TYPE          VARCHAR2,
  P_NEW_SERV_ID            VARCHAR2,
  p_src_serv_id            varchar2,
  P_NUM_OF_INSTALLMENTS    VARCHAR2 := '12'
)
IS

  V_INIT_RETURN               NUMBER;
  i_new_serv_count            PLS_INTEGER;
  V_OUT                       PLS_INTEGER;

  BLNFOUND                    BOOLEAN;
  BLNCREATERECORD             BOOLEAN;


  V_CARD_ORG                  VARCHAR2(100);
  V_INST_NAME                 VARCHAR2(100);
  V_TEXT                      VARCHAR2(100);
  N_MAPPING_COUNT             NUMBER;
  N_VALID_CLIENT              NUMBER := 0;
  N_CLIENT_COUNT              NUMBER := 0;
  N_INVALID_CLIENTS           NUMBER := 0;
  V_CURRENT_SCID              CIS_CLIENT_LINKS.SERVICE_CONTRACT_ID%TYPE;
  V_AUDIT_TRAIL               CIS_APPLICATION_DETAIL.AUDIT_TRAIL%TYPE;

  V_MERCHANT_SQL              VARCHAR2(4000);

  ROW_APPLICATION             CIS_APPLICATION_DETAIL%ROWTYPE;
  ROW_APPL_ACCT               CIS_APPLICATION_ACCT_TYPE%ROWTYPE;
  ROW_SERVICE                 SVC_CLIENT_SERVICE%ROWTYPE;

  C_MERCHANTS                 SYS_REFCURSOR;
  --
  V_CLIENT_TARIFF             VARCHAR2(6);
  --
  TYPE T_MERCHANT IS RECORD (
    CLIENT_NUMBER             CIS_CLIENT_LINKS.CLIENT_NUMBER%TYPE,
    GROUP_NUMBER              CIS_CLIENT_LINKS.GROUP_NUMBER%TYPE,
    SERVICE_CONTRACT_ID       CIS_CLIENT_LINKS.SERVICE_CONTRACT_ID%TYPE,
    INSTITUTION_NUMBER        CIS_CLIENT_LINKS.INSTITUTION_NUMBER%TYPE,
    PAYMENT_ACCT_NUMBER       CAS_CLIENT_ACCOUNT.PAYMENT_ACCT_NUMBER%TYPE,
    ACCT_NUMBER_RBS           CAS_CLIENT_ACCOUNT.ACCT_NUMBER_RBS%TYPE,
    PAYMENT_BANK_CLIENT       CAS_CLIENT_ACCOUNT.PAYMENT_BANK_CLIENT%TYPE,
    ACCT_STATUS               CAS_CLIENT_ACCOUNT.ACCT_STATUS%TYPE,
    CLIENT_ACCOUNT_NAME       CAS_CLIENT_ACCOUNT.CLIENT_ACCOUNT_NAME%TYPE,
    CLIENT_REFERENCE          CIS_CLIENT_REFERENCE.CLIENT_REFERENCE_NUMBER%TYPE,
    PREPAYMENT_ID             CAS_CLIENT_ACCOUNT.PREPAYMENT_ID%TYPE,
    STATEMENT_GENERATION      CAS_CLIENT_ACCOUNT.STATEMENT_GENERATION%TYPE,
  );
  R_MERCHANT                  T_MERCHANT;



  ERR_CANNOT_INITIALISE       EXCEPTION;
  ERR_NOT_VALIDATED           EXCEPTION;



  FUNCTION CREATEGLOBALTEMPTABLE(
    P_TABLE_NAME           IN VARCHAR2,
    P_TEMP_TABLE_NAME      IN VARCHAR2
  )
  RETURN BOOLEAN
  IS

    V_PK_FIELD_LIST         VARCHAR2(200) := NULL;
    V_SQL_CREATE            VARCHAR2(4000);
    V_SQL_SELECT            VARCHAR2(4000);
    NUM_COLS                NUMBER := 0;
    NUM_COLS_TEMP           NUMBER := 0;

  BEGIN

    IF RTRIM(P_TABLE_NAME) IS NULL OR (P_TEMP_TABLE_NAME) IS NULL THEN
      RETURN FALSE;
    END IF;


    SELECT COUNT(*)
    INTO   NUM_COLS
    FROM   USER_TAB_COLUMNS
    WHERE  TABLE_NAME = UPPER(RTRIM(P_TABLE_NAME));

    SELECT COUNT(*)
    INTO   NUM_COLS_TEMP
    FROM   USER_TAB_COLUMNS
    WHERE  TABLE_NAME = UPPER(RTRIM(P_TEMP_TABLE_NAME));


    IF (NUM_COLS_TEMP > 0 AND NUM_COLS_TEMP <> NUM_COLS) THEN

      IF NOT BW_LIB_DDL.DROPTEMPTABLE(P_TEMP_TABLE_NAME) THEN
        RAISE ERR_CANNOT_INITIALISE;
      END IF;

      NUM_COLS_TEMP := 0;

    END IF;

    IF NUM_COLS_TEMP = 0 THEN

      V_SQL_SELECT := 'SELECT * FROM ' || P_TABLE_NAME || ' WHERE 1=0 ';



      V_SQL_CREATE := 'CREATE GLOBAL TEMPORARY TABLE '||P_TEMP_TABLE_NAME||' ON COMMIT PRESERVE ROWS ';

      V_SQL_CREATE := V_SQL_CREATE||'AS ('||V_SQL_SELECT||') ';

      IF NOT BW_LIB_DDL.P_EXEC_STATEMENT ( V_SQL_CREATE, FALSE ) THEN
        RETURN FALSE;
      END IF;



      FOR REC IN (
        SELECT COLS.COLUMN_NAME
        FROM   USER_CONSTRAINTS CONS,
               USER_CONS_COLUMNS COLS
        WHERE  COLS.TABLE_NAME      = P_TABLE_NAME
        AND    CONS.CONSTRAINT_TYPE = 'P'
        AND    CONS.CONSTRAINT_NAME = COLS.CONSTRAINT_NAME
        AND    CONS.OWNER           = COLS.OWNER
        ORDER BY COLS.POSITION
      ) LOOP
        V_PK_FIELD_LIST := CASE
                           WHEN V_PK_FIELD_LIST IS NOT NULL
                           THEN V_PK_FIELD_LIST || ',' || REC.COLUMN_NAME
                           ELSE
                           REC.COLUMN_NAME
                           END;
      END LOOP;
      V_PK_FIELD_LIST := '(' || V_PK_FIELD_LIST || ')';



      IF V_PK_FIELD_LIST IS NOT NULL THEN

        V_SQL_CREATE := 'ALTER TABLE '||P_TEMP_TABLE_NAME||' '||
                        'ADD CONSTRAINT '||'PK'||substr(P_TEMP_TABLE_NAME,1,25) ||' PRIMARY KEY '||V_PK_FIELD_LIST||' ';

        IF NOT BW_LIB_DDL.P_EXEC_STATEMENT ( V_SQL_CREATE, FALSE ) THEN
          RETURN FALSE;
        END IF;

      END IF;

    ELSE
      EXECUTE IMMEDIATE 'TRUNCATE TABLE '||P_TEMP_TABLE_NAME;
    END IF;

    RETURN TRUE;

  EXCEPTION
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('Error Creating Global Temporary Table for: '|| P_TEMP_TABLE_NAME
                          ||' Error: ' || SQLERRM || ' ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
    RETURN FALSE;
  END;



  FUNCTION VALIDATEPARAMETERS (
    P_CARD_ORG_NAME    IN OUT VARCHAR2,
    P_INST_NAME        IN OUT VARCHAR2
  ) RETURN BOOLEAN
  IS
    V_DUMMY  VARCHAR2(10);
    BLNRET   BOOLEAN := TRUE;
  BEGIN

    IF P_INST_NO IS NULL THEN

      DBMS_OUTPUT.PUT_LINE('Institution number cannot be null');
      BLNRET := FALSE;

    ELSE

      BEGIN

        SELECT INSTITUTION_NAME
        INTO   P_INST_NAME
        FROM   SYS_INSTITUTION_LICENCE
        WHERE  INSTITUTION_NUMBER = P_INST_NO;

      EXCEPTION WHEN NO_DATA_FOUND THEN

        DBMS_OUTPUT.PUT_LINE('Institution number is not valid: ['||P_INST_NO||']. Institution number provided must exist in sys_institution_licence');
        BLNRET := FALSE;

      END;

    END IF;

    IF P_CARD_ORG IS NULL THEN

      DBMS_OUTPUT.PUT_LINE('Card Organization cannot be null');
      BLNRET := FALSE;

    ELSE

      BEGIN

        SELECT CARD_ORGANIZATION
        INTO   P_CARD_ORG_NAME
        FROM   BWT_CARD_ORGANIZATION
        WHERE  INSTITUTION_NUMBER = P_INST_NO
        AND    INDEX_FIELD        = P_CARD_ORG
        AND    LANGUAGE           = 'USA';

      EXCEPTION WHEN NO_DATA_FOUND THEN

        DBMS_OUTPUT.PUT_LINE('Card Organization is not valid: ['||P_CARD_ORG||']. Card Organization provided must exist in bwt_card_organization');
        BLNRET := FALSE;

      END;

    END IF;

    IF P_SRC_ACCT_TYPE IS NULL THEN

      DBMS_OUTPUT.PUT_LINE('Source Account Type cannot be null');
      BLNRET := FALSE;

    ELSE

      BEGIN

        SELECT 1
        INTO   V_DUMMY
        FROM   BWT_ACCOUNT_TYPE_ID
        WHERE  INSTITUTION_NUMBER = P_INST_NO
        AND    INDEX_FIELD        = P_SRC_ACCT_TYPE
        AND    LANGUAGE           = 'USA';

      EXCEPTION WHEN NO_DATA_FOUND THEN

        DBMS_OUTPUT.PUT_LINE('Source Account Type is not valid: ['||P_SRC_ACCT_TYPE||']. Source Account Type provided must exist in bwt_account_type_id');
        BLNRET := FALSE;

      END;

    END IF;

    IF P_NEW_ACCT_TYPE IS NULL THEN

      DBMS_OUTPUT.PUT_LINE('New Account Type cannot be null');
      BLNRET := FALSE;

    ELSE

      BEGIN

        SELECT 1
        INTO   V_DUMMY
        FROM   BWT_ACCOUNT_TYPE_ID
        WHERE  INSTITUTION_NUMBER = P_INST_NO
        AND    INDEX_FIELD        = P_NEW_ACCT_TYPE
        AND    LANGUAGE           = 'USA';

      EXCEPTION WHEN NO_DATA_FOUND THEN

        DBMS_OUTPUT.PUT_LINE('New Account Type is not valid: ['||P_NEW_ACCT_TYPE||']. New Account Type provided must exist in bwt_account_type_id');
        BLNRET := FALSE;

      END;

    END IF;

    IF P_NEW_SERV_ID IS not null then
      --
      select count(*)
      into i_new_serv_count
      from table(bw_util.split(P_NEW_SERV_ID, '|')) ;
      --
      BEGIN

        SELECT count(*)
        INTO   V_DUMMY
        FROM   BWT_SERVICES
        WHERE  INSTITUTION_NUMBER = P_INST_NO
        AND    INDEX_FIELD        in (
                                      select column_value
            						  from table(bw_util.split(P_NEW_SERV_ID, '|'))
                                     )
        AND    LANGUAGE           = 'USA';
        --
        if v_dummy <> i_new_serv_count then
          DBMS_OUTPUT.PUT_LINE('Some Service IDs provided are not valid: ['||P_NEW_SERV_ID||']. New Service IDs must exist in bwt_services');
          BLNRET := FALSE;
        end if;

      EXCEPTION WHEN NO_DATA_FOUND THEN

        DBMS_OUTPUT.PUT_LINE('New Service IDs are not valid: ['||P_NEW_SERV_ID||']. New Service IDs provided must exist in bwt_services');
        BLNRET := FALSE;

      END;

    END IF;

    RETURN BLNRET;

  END VALIDATEPARAMETERS;




BEGIN

  BW_PRC_RES.INITGLOBALVARS (P_INST_NO, P_STATION_NO, P_USER_ID, V_INIT_RETURN);


  IF NOT VALIDATEPARAMETERS(V_CARD_ORG,V_INST_NAME) THEN
    RAISE ERR_NOT_VALIDATED;
  END IF;


  V_OUT := '0';
  V_TEXT := V_CARD_ORG || ' card scheme to Institution '|| P_INST_NO ||'('|| V_INST_NAME ||') Migration.';
  DBMS_OUTPUT.PUT_LINE(V_TEXT);





  V_AUDIT_TRAIL := TO_CHAR(SYSDATE,'YYDDD')||'-'||'Setup-RS2-MIG'||'-'||SUBSTR(P_INST_NO,-4)||P_CARD_ORG||TO_CHAR(SYSDATE,'HH24MI');
  DBMS_OUTPUT.PUT_LINE('Audit Trail: (To be used in case of Rollback): '||V_AUDIT_TRAIL);


  IF NOT CREATEGLOBALTEMPTABLE('CIS_APPLICATION_DETAIL'     , 'CIS_APPL_DETAIL_MIGRATION'    ) THEN

    DBMS_OUTPUT.PUT_LINE('Error creating temporary table CIS_APPL_DETAIL_MIGRATION');
    RAISE ERR_CANNOT_INITIALISE;

  END IF;

  IF NOT CREATEGLOBALTEMPTABLE('CIS_APPLICATION_ACCT_TYPE'  , 'CIS_APPL_ACCT_TYPE_MIGRATION' ) THEN

    DBMS_OUTPUT.PUT_LINE('Error creating temporary table CIS_APPL_ACCT_TYPE_MIGRATION');
    RAISE ERR_CANNOT_INITIALISE;

  END IF;

  IF NOT CREATEGLOBALTEMPTABLE('SVC_CLIENT_SERVICE'         , 'SVC_CLIENT_SERVICE_MIGRATION' ) THEN

    DBMS_OUTPUT.PUT_LINE('Error creating temporary table SVC_CLIENT_SERVICE_MIGRATION');
    RAISE ERR_CANNOT_INITIALISE;

  END IF;


  IF V_INIT_RETURN = 0 THEN
    RAISE ERR_CANNOT_INITIALISE;
  END IF;


  V_MERCHANT_SQL := 'select lnk.client_number, lnk.group_number, lnk.service_contract_id, lnk.institution_number, '||
           '       nvl(substr(payment_acct_number,1,8),''N/A'') as payment_acct_number, acct_number_rbs, payment_bank_client, acct_status, '||
           '       client_account_name, null as client_reference, acct.prepayment_id, acct.statement_generation '||
           'from   cis_client_details ccd,  '||
                 ' cis_client_links lnk,    '||
                 ' cas_client_account acct, '||
                 ' bwt_service_contract_id bsci '||
           'where  ccd.institution_number     = '''||P_INST_NO|| ''' '||
           'and    ccd.record_type            = '''|| BW_CONST2.RECORD_MERCHANT || '''  ' ||
           'and    ccd.client_status     not in ('''|| BW_CONST.STA_CUST_CLOSED || ''') ' ||
           'and    lnk.contract_status   not in ('''|| BW_CONST.STA_CUST_CLOSED || ''') ' ||
           'and    lnk.institution_number     = ccd.institution_number ' ||
           'and    lnk.client_number          = ccd.client_number ' ||
           'and    lnk.client_level           = '''|| BW_CONST2.LEVL_MEMBER || ''' ' ||
           'and    lnk.effective_date         = (select max(effective_date) ' ||
                                               ' from   cis_client_links ' ||
                                               ' where  client_number       = lnk.client_number ' ||
                                               ' and    institution_number  = lnk.institution_number ' ||
                                               ' and    service_contract_id = lnk.service_contract_id ' ||
                                               ' and    effective_date     <= to_char(sysdate,''YYYYMMDD'') ' ||
                                               ' and    client_level        = lnk.client_level) ' ||
           'and    bsci.institution_number    = lnk.institution_number ' ||
           'and    bsci.index_field           = lnk.service_contract_id ' ||
           'and    bsci.language              = ''USA'' ' ||
           'and    acct.client_number         = ccd.client_number ' ||
           'and    acct.institution_number    = ccd.institution_number ' ||
           'and    acct.account_type_id       = '''||P_SRC_ACCT_TYPE||'''  ' ||
           'and    acct.acct_currency         = ''986'' ';

  OPEN C_MERCHANTS FOR V_MERCHANT_SQL;

  LOOP
    FETCH C_MERCHANTS
    INTO R_MERCHANT;

    EXIT WHEN C_MERCHANTS%NOTFOUND;

    SAVEPOINT LAST_MERCHANT;

    N_CLIENT_COUNT := N_CLIENT_COUNT + 1;
    BLNFOUND := TRUE;


    IF BLNFOUND THEN


      ROW_APPLICATION.APPLICATION_NUMBER  := BW_CODE_LIBRARY.GETNEXTSEQNUMBER(BW_CONST.SEQ_APPLICATION_NO,1) ;
      ROW_APPLICATION.INSTITUTION_NUMBER  := BW_LIB_INCL.GSTRINSTITUTIONNUMBER;
      ROW_APPLICATION.RECORD_DATE         := BW_LIB_INCL.GSTRPOSTINGDATE;
      ROW_APPLICATION.AUDIT_TRAIL         := V_AUDIT_TRAIL;
      ROW_APPLICATION.CLIENT_NUMBER       := R_MERCHANT.CLIENT_NUMBER;
      ROW_APPLICATION.GROUP_NUMBER        := R_MERCHANT.GROUP_NUMBER;
      ROW_APPLICATION.RECORD_TYPE         := BW_CONST2.RECORD_MERCHANT_EXT;
      ROW_APPLICATION.APPLICATION_STATUS  := BW_CONST.STA_APPL_APPROVED;
      ROW_APPLICATION.SERVICE_CONTRACT_ID := R_MERCHANT.SERVICE_CONTRACT_ID;
      ROW_APPLICATION.CLIENT_LEVEL        := BW_CONST2.LEVL_MEMBER;
      ROW_APPLICATION.RISK_GROUP          := BW_CONST.SIGN_NA;
      ROW_APPLICATION.SHORT_NAME          := R_MERCHANT.CLIENT_ACCOUNT_NAME;

      EXECUTE IMMEDIATE 'insert into CIS_APPL_DETAIL_MIGRATION '||
        '(application_number, institution_number, record_date, audit_trail, '||
        'client_number, group_number, record_type, application_status, '||
        'service_contract_id, client_level, risk_group, short_name) '||
        'values '||
        '(:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11, :12)'
      USING
        ROW_APPLICATION.APPLICATION_NUMBER , ROW_APPLICATION.INSTITUTION_NUMBER, ROW_APPLICATION.RECORD_DATE, ROW_APPLICATION.AUDIT_TRAIL,
        ROW_APPLICATION.CLIENT_NUMBER      , ROW_APPLICATION.GROUP_NUMBER      , ROW_APPLICATION.RECORD_TYPE, ROW_APPLICATION.APPLICATION_STATUS,
        ROW_APPLICATION.SERVICE_CONTRACT_ID, ROW_APPLICATION.CLIENT_LEVEL      , ROW_APPLICATION.RISK_GROUP , ROW_APPLICATION.SHORT_NAME;
      --
      --
      ROW_APPL_ACCT.APPLICATION_NUMBER   := ROW_APPLICATION.APPLICATION_NUMBER;
      ROW_APPL_ACCT.INSTITUTION_NUMBER   := ROW_APPLICATION.INSTITUTION_NUMBER;
      ROW_APPL_ACCT.RECORD_DATE          := ROW_APPLICATION.RECORD_DATE;
      ROW_APPL_ACCT.AUDIT_TRAIL          := ROW_APPLICATION.AUDIT_TRAIL;
      ROW_APPL_ACCT.BILLING_LEVEL        := BW_CONST.CONF_YES;
      ROW_APPL_ACCT.RECORD_TYPE          := BW_CONST2.RECORD_MERCHANT_EXT;
      ROW_APPL_ACCT.ACCOUNT_TYPE_ID      := P_NEW_ACCT_TYPE;
      ROW_APPL_ACCT.ACCT_CURRENCY        := '986';
      ROW_APPL_ACCT.STATEMENT_GENERATION := R_MERCHANT.STATEMENT_GENERATION;
      ROW_APPL_ACCT.STATEMENT_TYPE       := '900';
      ROW_APPL_ACCT.LOCK_IN_FLAG         := BW_CONST.SIGN_NA;
      ROW_APPL_ACCT.PREPAYMENT_ID        := R_MERCHANT.PREPAYMENT_ID;

      ROW_APPL_ACCT.BILLING_CLIENT       := R_MERCHANT.PAYMENT_ACCT_NUMBER;
      ROW_APPL_ACCT.ACCT_NUMBER_RBS      := R_MERCHANT.ACCT_NUMBER_RBS;
      ROW_APPL_ACCT.PAYMENT_BANK_CLIENT  := R_MERCHANT.PAYMENT_BANK_CLIENT;

      EXECUTE IMMEDIATE 'insert into CIS_APPL_ACCT_TYPE_MIGRATION ('||
        'application_number  , institution_number , record_date    , audit_trail   , '||
        'billing_level       , record_type        , account_type_id, acct_currency , '||
        'statement_generation, statement_type     , lock_in_flag   , billing_client, '||
        'acct_number_rbs     , payment_bank_client, prepayment_id) '||
        'values (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14,:15)'
      USING
      ROW_APPL_ACCT.APPLICATION_NUMBER   ,ROW_APPL_ACCT.INSTITUTION_NUMBER   ,ROW_APPL_ACCT.RECORD_DATE          ,ROW_APPL_ACCT.AUDIT_TRAIL          ,
      ROW_APPL_ACCT.BILLING_LEVEL        ,ROW_APPL_ACCT.RECORD_TYPE          ,ROW_APPL_ACCT.ACCOUNT_TYPE_ID      ,ROW_APPL_ACCT.ACCT_CURRENCY        ,
      ROW_APPL_ACCT.STATEMENT_GENERATION ,ROW_APPL_ACCT.STATEMENT_TYPE       ,ROW_APPL_ACCT.LOCK_IN_FLAG         ,ROW_APPL_ACCT.BILLING_CLIENT       ,
      ROW_APPL_ACCT.ACCT_NUMBER_RBS      ,ROW_APPL_ACCT.PAYMENT_BANK_CLIENT  ,ROW_APPL_ACCT.PREPAYMENT_ID;
      --
      dbms_output.put_line('ROW_APPLICATION.CLIENT_NUMBER['||ROW_APPLICATION.CLIENT_NUMBER||']');
      --
       if p_new_serv_id is not null then
        --
        for rec in (
            select column_value as serv_id
            from table(bw_util.split(p_new_serv_id, '|'))
        )loop
        --
          begin
            SELECT s.CLIENT_TARIFF
            INTO  V_CLIENT_TARIFF
            FROM  SVC_CLIENT_SERVICE s
            WHERE s.INSTITUTION_NUMBER = ROW_APPLICATION.INSTITUTION_NUMBER
            AND   s.SERVICE_STATUS = '001' -- active
            AND   s.SERVICE_ID     = P_SRC_SERV_ID
            AND   s.CLIENT_NUMBER  = ROW_APPLICATION.CLIENT_NUMBER
            and   s.EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE) FROM SVC_CLIENT_SERVICE
                                    WHERE INSTITUTION_NUMBER = S.INSTITUTION_NUMBER
                                    AND SERVICE_ID = S.SERVICE_ID
                                    AND GROUP_NUMBER = S.GROUP_NUMBER
                                    AND CLIENT_NUMBER = S.CLIENT_NUMBER);
          exception when others then
            v_client_tariff := null;
          end;
          --
          ROW_SERVICE.RECORD_DATE         := ROW_APPLICATION.RECORD_DATE;
          ROW_SERVICE.AUDIT_TRAIL         := ROW_APPLICATION.AUDIT_TRAIL;
          ROW_SERVICE.SERVICE_ID          := rec.serv_id;
          ROW_SERVICE.EFFECTIVE_DATE      := TO_CHAR(SYSDATE,'YYYYMMDD');
          ROW_SERVICE.NO_OF_INSTALLMENTS  := P_NUM_OF_INSTALLMENTS;
          ROW_SERVICE.INSTITUTION_NUMBER  := ROW_APPLICATION.INSTITUTION_NUMBER;
          ROW_SERVICE.SERVICE_STATUS      := '001';
          ROW_SERVICE.CLIENT_NUMBER       := ROW_APPLICATION.CLIENT_NUMBER;
          ROW_SERVICE.SERVICE_CONTRACT_ID := ROW_APPLICATION.SERVICE_CONTRACT_ID;
          ROW_SERVICE.GROUP_NUMBER        := ROW_APPLICATION.GROUP_NUMBER;
          ROW_SERVICE.RECORD_TYPE         := '003';
          ROW_SERVICE.CLIENT_TARIFF       := V_CLIENT_TARIFF;
          ROW_SERVICE.SERVICE_CATEGORY    := '002';
          ROW_SERVICE.REVIEW_DATE         := TO_CHAR(LAST_DAY(ADD_MONTHS( TO_DATE(ROW_SERVICE.EFFECTIVE_DATE,'YYYYMMDD'), 12 )),'YYYYMMDD');
          --
          EXECUTE IMMEDIATE 'insert into SVC_CLIENT_SERVICE_MIGRATION ('||
            'record_date         , audit_trail         , service_id          , effective_date      , '||
            'no_of_installments  , institution_number  , service_status      , client_number       , '||
            'service_contract_id , group_number        , record_type         , client_tariff       , '||
            'service_category    , review_date         ) '||
            'values (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14)'
          USING
            ROW_SERVICE.RECORD_DATE         , ROW_SERVICE.AUDIT_TRAIL         , ROW_SERVICE.SERVICE_ID          , ROW_SERVICE.EFFECTIVE_DATE      ,
            ROW_SERVICE.NO_OF_INSTALLMENTS  , ROW_SERVICE.INSTITUTION_NUMBER  , ROW_SERVICE.SERVICE_STATUS      , ROW_SERVICE.CLIENT_NUMBER       ,
            ROW_SERVICE.SERVICE_CONTRACT_ID , ROW_SERVICE.GROUP_NUMBER        , ROW_SERVICE.RECORD_TYPE         , ROW_SERVICE.CLIENT_TARIFF       ,
            ROW_SERVICE.SERVICE_CATEGORY    , ROW_SERVICE.REVIEW_DATE         ;
      	  --
        end loop;
      end if;

      BLNCREATERECORD := TRUE;

    end if;
    --
    IF BLNCREATERECORD THEN

      N_VALID_CLIENT := N_VALID_CLIENT + 1;

    ELSE

      ROLLBACK TO SAVEPOINT LAST_MERCHANT;
      N_INVALID_CLIENTS := N_INVALID_CLIENTS + 1;

    END IF;

    V_CURRENT_SCID := ROW_APPLICATION.SERVICE_CONTRACT_ID;

  END LOOP;
  --
  EXECUTE IMMEDIATE 'INSERT INTO CIS_APPLICATION_DETAIL      select * from CIS_APPL_DETAIL_MIGRATION    ';
  EXECUTE IMMEDIATE 'INSERT INTO SVC_CLIENT_SERVICE          select * from SVC_CLIENT_SERVICE_MIGRATION ';
  EXECUTE IMMEDIATE 'INSERT INTO CIS_APPLICATION_ACCT_TYPE   select * from CIS_APPL_ACCT_TYPE_MIGRATION ';

  DBMS_OUTPUT.PUT_LINE('Number of merchants found for migration: '||N_CLIENT_COUNT);
  DBMS_OUTPUT.PUT_LINE('Number of merchants not migrated: '||N_INVALID_CLIENTS);
  DBMS_OUTPUT.PUT_LINE('Number of merchants migrated: '||N_VALID_CLIENT);

  COMMIT;

EXCEPTION
WHEN ERR_CANNOT_INITIALISE THEN
  DBMS_OUTPUT.PUT_LINE('Error! Could not initialize process');
  ROLLBACK;
WHEN ERR_NOT_VALIDATED  THEN
  DBMS_OUTPUT.PUT_LINE('Error! Could not validate parameters');
  ROLLBACK;
WHEN OTHERS THEN
  DBMS_OUTPUT.PUT_LINE('Oracle error: '||SQLERRM || ' ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
  ROLLBACK;
END;