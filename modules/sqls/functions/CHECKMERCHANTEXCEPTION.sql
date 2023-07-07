FUNCTION "CHECKMERCHANTEXCEPTION" (instno_in IN VARCHAR2,
                               Batchslip_in IN VARCHAR2, BatchClnt_in IN VARCHAR2,
                               Batchgrpno_in IN VARCHAR2, slipno_in IN VARCHAR2)
RETURN VARCHAR2
/*
   Returns predefined Exceptional transactions made on Merchants.

   01 - Card on restricted card list
   02 - Trans Date too old or missing
   03 - Trans Amnt above Floor Limit without Auth Code
   04 - Incorrect Auth Code (Own Trans only)
   05 - Merchant or H.O. Status Not 'Active'
   06 - Card Expired(Own Cards Only)

   									-----Maintenance History------

   Updated by: Marion Tibubos
   Date: 08.05.2003
   Changes: Exclude Quikcash while checking the error on exception 01, 04, and 06.
   Reason: Shows Quickcash with an error.

   Updated by: Marion Tibubos
   Date: 08.07.2003
   Changes: Same as Quikcash, Visa Electron Debit Card should exclude while checking the error on exception 01, 04, and 06.
   Reason: Shows Visa Electron Debit Card with an error.

*/
IS
   CONST_MAX_NUM_DAYS		CONSTANT NUMBER := 4;
   CONST_MERCHANT_ACTIVE	CONSTANT VARCHAR2(3) := '001';
   CONST_MAIN_ACCOUNT	   CONSTANT VARCHAR2(3) := '001';
   CONST_ACTIVE_CARD	      CONSTANT VARCHAR2(3) := '001';

   CURSOR trans_cur IS
            select * FROM int_transactions
            WHERE transaction_slip = slipno_in
            AND institution_number = instno_in;

   trans_rec trans_cur%ROWTYPE;

   CURSOR servdef_cur(cardbrand_in VARCHAR2,cardorg_in VARCHAR2,recdate_in VARCHAR2) IS
         SELECT SERVICE_ID FROM CBR_SERVICE_DEFINITION SERDEF
         WHERE INSTITUTION_NUMBER = instno_in
            AND CARD_BRAND = cardbrand_in
            AND CARD_ORGANIZATION = cardorg_in
            AND SERVICE_CATEGORY ='002'
            AND EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE) FROM CBR_SERVICE_DEFINITION
            							WHERE INSTITUTION_NUMBER=SERDEF.INSTITUTION_NUMBER
            							AND CARD_BRAND = SERDEF.CARD_BRAND
            							AND CARD_ORGANIZATION = SERDEF.CARD_ORGANIZATION
            							AND SERVICE_CATEGORY = SERDEF.SERVICE_CATEGORY
            							AND EFFECTIVE_DATE <= recdate_in);

   servdef_rec servdef_cur%ROWTYPE;

   CURSOR floorlimit_cur(serviceid_in VARCHAR2,country_in VARCHAR2,trancur_in VARCHAR2,grpnum_in VARCHAR2
                        ,clntnum_in VARCHAR2, recdate_in VARCHAR2) IS
         SELECT FLOOR_LIMIT
         FROM CBR_MERCHANT_FLOORLIMIT FLLM
         WHERE INSTITUTION_NUMBER = instno_in
         AND (SERVICE_ID = serviceid_in OR SERVICE_ID = '999')
         AND (COUNTRY = country_in OR COUNTRY = '999')
         AND (GROUP_NUMBER = grpnum_in OR GROUP_NUMBER = '99999999')
         AND (CLIENT_NUMBER = clntnum_in OR CLIENT_NUMBER = '99999999')
         AND ISO_BUSS_CLASS_GROUP = '999'
         AND EFFECTIVE_DATE =
              (SELECT MAX(EFFECTIVE_DATE) FROM CBR_MERCHANT_FLOORLIMIT
              WHERE INSTITUTION_NUMBER = FLLM.INSTITUTION_NUMBER
              AND SERVICE_ID = FLLM.SERVICE_ID
              AND COUNTRY = FLLM.COUNTRY
              AND ISO_BUSS_CLASS_GROUP = FLLM.ISO_BUSS_CLASS_GROUP
              AND GROUP_NUMBER = FLLM.GROUP_NUMBER
              AND CLIENT_NUMBER = FLLM.CLIENT_NUMBER
              AND EFFECTIVE_DATE <= recdate_in);

   floorlimit_rec floorlimit_cur%ROWTYPE;

   CURSOR cardrec_cur(cardnum_in varchar2) IS
         SELECT card_number,expiry_date,card_status FROM svc_client_cards
         WHERE card_number = cardnum_in;

   cardrec_rec cardrec_cur%ROWTYPE;

   CURSOR authrec_cur(authcode VARCHAR2,cardnum VARCHAR2) IS
         SELECT auth_code FROM int_online_transactions
         WHERE auth_code = authcode
         AND card_number = cardnum;

   authrec_rec authrec_cur%ROWTYPE;

   dteTranDate DATE;
   v_floorlimit NUMBER := '999999999999999999'; --Set default to the highest number if floorlimit is not used.
   v_cardorg VARCHAR2(3);
   v_service_id VARCHAR2(3);
   v_isCardONUS BOOLEAN := FALSE;
   v_Restrc VARCHAR2(2) := '--';          --01
   v_OldDate VARCHAR2(2) := '--';         --02
   v_FloorAuth VARCHAR2(2) := '--';       --03
   v_ONUSAuthCode VARCHAR2(2) := '--';    --04
   v_NotActive VARCHAR2(2) := '--';       --05
   v_Expired VARCHAR2(2) := '--';         --06
   Result VARCHAR2(30);

BEGIN

   OPEN trans_cur;
   FETCH trans_cur INTO trans_rec;

   IF trans_cur%FOUND
   THEN
      OPEN servdef_cur(trans_rec.card_brand,substr(trans_rec.authorized_by,1,3),trans_rec.record_date);
      FETCH servdef_cur INTO servdef_rec;

      IF servdef_cur%FOUND
      THEN
         OPEN floorlimit_cur(servdef_rec.service_id, trans_rec.merchant_country, trans_rec.tran_currency, Batchgrpno_in,
                        BatchClnt_in ,trans_rec.record_date);
         FETCH floorlimit_cur INTO floorlimit_rec;

         IF floorlimit_cur%FOUND
         THEN
            IF NOT(RTRIM(LTRIM(floorlimit_rec.floor_limit)) IS NULL OR RTRIM(LTRIM(floorlimit_rec.floor_limit)) = 0)
            THEN
               v_floorlimit := floorlimit_rec.floor_limit;
            END IF;
         END IF;
      END IF;
   END IF;

   OPEN cardrec_cur(trans_rec.card_number);
   FETCH cardrec_cur INTO cardrec_rec;

   --Check Restricted Cards
   IF cardrec_rec.card_status <> CONST_ACTIVE_CARD and substr(trans_rec.card_number,1,6) not in ('588700','458870')-- exclude Q'cash and Visa Electron (08.07.2003)
   THEN
      v_Restrc := '01';
   END IF;

   /* Check if the card is owned by HSBC */
   v_isCardONUS := checkDomestic_Onus(trans_rec.card_number) = 'ONUS';

   --Checking Transaction Date--
   BEGIN
      /* Value of Transaction Date might be invalid. Use of exception is required inorder
         to trap and to indicate that it is an invalid date.
      */
      dteTranDate := TO_DATE(trans_rec.transaction_date,'YYYYMMDD');

      IF dteTranDate IS NULL OR to_date(trans_rec.record_date,'YYYYMMDD') - dteTranDate > CONST_MAX_NUM_DAYS
      THEN
         v_OldDate := '02';
      END IF;

      IF v_isCardONUS
         --Check for transactions with HSBC expired cards
      THEN
         IF dteTranDate IS NULL OR to_date(cardrec_rec.expiry_date,'YYYYMMDD') < dteTranDate and substr(trans_rec.card_number,1,6) not in ('588700','458870')-- exclude Q'cash and Visa Electron (08.07.2003)
         THEN
            v_Expired := '06';
         END IF;
      END IF;
   EXCEPTION
      WHEN OTHERS
      --Invalid Date
      THEN
         v_OldDate := '02';
			IF v_isCardONUS
         	--Check for transactions with HSBC expired cards
      		THEN
         	  v_Expired := '06';
			END IF;
   END;

   --Checking Floor Limit and Authorization Code
   IF to_number(trans_rec.tran_amount_gr) > v_floorlimit AND trans_rec.auth_code=lpad(' ',6,' ')
   THEN
      v_FloorAuth := '03';
   END IF;

   --Check if ONUS cards and if Authorization code is existing
   IF v_isCardONUS
      /* Utilize function use for determining if a card is ONUS. This is a more accurate solution
      since it differentiate cards which belongs to HSBC and Other Banks i.e. APS, Lombard.
      */
   THEN
      IF IsNumeric(trans_rec.auth_code) and substr(trans_rec.card_number,1,6) not in ('588700','458870')-- exclude Q'cash and Visa Electron (08.07.2003)
         /*
         Don't check for authorization code which is not a numeric value. It is possible for some
         transactions having no authorization code e.g. internal generated trans.
         Don't check also those cards that are QUIKCASH.
         */
      THEN
         /* Search if the authorization code can be found in the online trans table. */
         OPEN authrec_cur(trans_rec.auth_code,trans_rec.card_number);
         FETCH authrec_cur INTO authrec_rec;

         IF  authrec_cur%NOTFOUND
         THEN
            v_ONUSAuthCode := '04';
         END IF;
         CLOSE authrec_cur;
      END IF;
   END IF;

   --Check if Merchant or MerchaNt Head Office is not active
   FOR ActHrchy IN (select client_number,parent_account_number,group_number,account_level,acct_status
                     from cas_client_account
                     start with group_number = Batchgrpno_in
                     		and client_number = BatchClnt_in
                     connect by prior parent_account_number = acct_number
                            	  and prior group_number = group_number
                     and institution_number=institution_number)
   LOOP
      --Check status of current account
      IF (BatchClnt_in = ActHrchy.client_number AND Batchgrpno_in = ActHrchy.group_number
         AND ActHrchy.acct_status<>CONST_MERCHANT_ACTIVE)
      THEN
          v_NotActive := '05';
      END IF;
      --Check main account(Head Office)
      IF (Batchgrpno_in = ActHrchy.group_number AND ActHrchy.account_level=CONST_MAIN_ACCOUNT
         AND ActHrchy.acct_status<>CONST_MERCHANT_ACTIVE)
      THEN
          v_NotActive := '05';
      END IF;
   END LOOP;

   CLOSE trans_cur;

   IF cardrec_cur%ISOPEN
   THEN
      CLOSE cardrec_cur;
   END IF;

   IF servdef_cur%ISOPEN
   THEN
      CLOSE servdef_cur;
   END IF;

   IF floorlimit_cur%ISOPEN
   THEN
      CLOSE floorlimit_cur;
   END IF;
   /*
      v_floorlimit variable by default contains 18 digits of '9'.
      Meaning it is not applicable and should be hidden in the report.
   */
   Result := v_Restrc||v_OldDate||v_FloorAuth||v_ONUSAuthCode||v_NotActive||v_Expired||v_floorlimit;

   RETURN(Result);

exception
   when others
   THEN
      	dbms_output.put_line('Batch: '||Batchslip_in);
		dbms_output.put_line(trans_rec.transaction_slip);
		dbms_output.put_line(trans_rec.tran_amount_gr);
		dbms_output.put_line(floorlimit_rec.floor_limit);
		dbms_output.put_line(sqlcode||sqlerrm);

END checkMerchantException;