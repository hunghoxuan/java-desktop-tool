/*
This SQL script's objective is insert random acquirer batch inputs. Save time because dont need to login into WebGui.
Input params:
- 1: institution_number
- 2: client_number (one to many, seperated by comma)
- 3: card_numbers (one to many, seperated by comma)
- 4: currency  (one to many, seperated by comma)
- 5: transaction type (Purchase, Reversal, Refund)
Output: insert/update data in:
- INT_BATCH_CAPTURE
Use cases: BA, Testers, Automation test.
Contributors: Hung, Mia, Nga
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;

DECLARE
	-- MANUAL PARAMS
	v_institution_number VARCHAR2(8) := '&1';
	v_client_number VARCHAR2(120) := '&2'; -- 81
	v_card_number VARCHAR2(190) := '&3'; -- Visa: 4035853306200869 4999224500000014, MC: 5373520180000000007 5115800300000001. 5154968300000008
	v_currency VARCHAR2(30) := '&4';  -- 978, 826, 820
	v_trans_type varchar(10) := '&5'; -- Purchase, Reversal, Refund
	
	v_terminal_capability VARCHAR2(3) := '001';
	v_capture_method VARCHAR2(3) := '030';
	
	v_audit_trail VARCHAR2(35) := 'MANUAL_INPUT_';
	
	v_auth_code  VARCHAR2(6) := '000000';
	v_card_expiration_date   VARCHAR2(6) := '203012';
	
	v_run_soa_process_id varchar2(3) := ''; -- 086/Y: run soa process for batch input, empty: dont run soa process
	v_user_id VARCHAR2(8) := '999999';
	v_station_number VARCHAR2(3) := '129';
	
	-- FIXED PARAMS
	v_transaction_status VARCHAR2(3) := '008'; -- dont change this.
	v_trans_category VARCHAR2(3) := '001';  -- dont change this.
	v_number_slips VARCHAR2(1) := '1';  -- dont change this.
	v_trans_source VARCHAR2(3) := '005';
	
	v_amount_min PLS_INTEGER := 10;
	v_amount_max PLS_INTEGER := 200;
	v_CARD_ORGANIZATION VARCHAR2(3);
	v_CARD_BRAND VARCHAR2(3);
	
	-- CACULATED PARAMS
	v_seq_num VARCHAR2(18);
	v_merchant_number VARCHAR2(15);
	
	v_batch_trans_slip VARCHAR2(11);
	v_batch_trans_slip1 VARCHAR2(11);
	v_batch_trans_slip2 VARCHAR2(11);
	
	v_tran_currency VARCHAR2(3);
	v_terminal_id VARCHAR2(10);
	
	V_INIT_RETURN PLS_INTEGER;
	v_amount VARCHAR2(11);
	
	v_record_date VARCHAR2(8);
	v_trans_date VARCHAR2(8);
	v_posting_date VARCHAR2(8);
	v_system_date VARCHAR2(8);
	
	v_merchant_name VARCHAR2(25);
	v_merchant_city VARCHAR2(13);
	v_merchant_country VARCHAR2(3);
	v_merchant_state VARCHAR2(3);
	v_trans_prefix VARCHAR2(3);
	v_dr_cr varchar(3);
	v_reversal_flag varchar(3);
	
	v_card_org varchar2(3);
	v_card_brand1 varchar2(3);
	v_value_date VARCHAR2(8);
	v_currency_all VARCHAR2(30);
	v_client_number_all VARCHAR2(120);
	v_trans_type_all VARCHAR2(30);
	v_card_number_all VARCHAR2(190);
	
	v_errors VARCHAR2(120);
	P_OUT varchar2(100);
	v_params varchar2(100);

BEGIN

	if (v_institution_number is null or v_institution_number = ''
		or v_user_id is null or v_user_id = ''
		or v_client_number is null or v_client_number = ''
		or v_card_number is null or v_card_number = ''
		or v_station_number is null or v_station_number = '') THEN
		v_errors := 'Param is required or missing';
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	end if;

	IF (v_errors is null or v_errors = '') THEN
		-- AUTO-CACULATED PARAMS
		if (v_posting_date is null or v_posting_date = '') then
			SELECT POSTING_DATE INTO v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number; -- posting date
		end if;
		SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL; -- get system date
		v_value_date := v_posting_date; -- get value_date
		v_trans_date:= v_posting_date;
		v_record_date:= v_posting_date;

		v_audit_trail := v_audit_trail || v_system_date;
        v_currency_all := v_currency;
        v_card_number_all := v_card_number;
        v_trans_type_all := v_trans_type;
		v_client_number_all := v_client_number;

		DBMS_OUTPUT.put_line( v_card_number_all || ' ' || v_client_number_all || ' ' ||  v_currency_all);

		FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
			BEGIN
		 		v_client_number := trim(curClientNumber.str);
				SELECT TRADE_NAME INTO v_merchant_name FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				SELECT CLIENT_CITY INTO v_merchant_city FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				SELECT client_country into v_merchant_country FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
			EXCEPTION WHEN NO_DATA_FOUND THEN
				DBMS_OUTPUT.put_line('Client Number not found: ' || v_client_number);
				CONTINUE;
			END;
		FOR curCardNumber in (WITH DATA AS ( SELECT v_card_number_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
		FOR curCurrency in (WITH DATA AS ( SELECT v_currency_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
			v_client_number := trim(curClientNumber.str);
			v_card_number := trim(curCardNumber.str);  -- get card number
   			v_currency := trim(curCurrency.str);

			v_CARD_ORGANIZATION := '';  -- 002: mastercard, 003: visa. Leave Empty to auto detect from card_number

			SELECT TO_CHAR(TO_DATE(v_system_date, 'YYYYMMDD'), 'YWW') - 1 INTO v_trans_prefix FROM DUAL;  -- get prefix (transaction_slip = 3 digits YWW + sequence number 042

			-- LOOKUP FROM DB PARAMS
			BEGIN
		 		SELECT merchant_id into v_merchant_number FROM CIS_DEVICE_LINK WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				SELECT terminal_id into v_terminal_id FROM CIS_DEVICE_LINK WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
			EXCEPTION WHEN NO_DATA_FOUND THEN
				DBMS_OUTPUT.put_line('Terminal not found in CIS_DEVICE_LINK: ' || v_client_number);
				v_terminal_id := '00000000';
				v_merchant_number := '0000000' || v_client_number;   -- 15 digits
			END;

			BW_PRC_RES.INITGLOBALVARS (v_institution_number, v_station_number, v_user_id, V_INIT_RETURN);

			select floor(dbms_random.value(v_amount_min,v_amount_max)) into v_amount from dual; -- get random amount, v_amount := '100.00';

            if (v_trans_type_all = '' or v_trans_type_all is null or v_trans_type_all = '*') then -- if trans_type is random then set to Refund if v_amount is small enough
            	if (floor(v_amount) < 50) then
            		v_trans_type := 'refund';
            	elsif (floor(v_amount) < 100) then
            		v_trans_type := 'sale';
            	else
            	    v_trans_type := 'purchase';
            	end if;
            else
            	v_trans_type := v_trans_type_all;
            end if;

			v_trans_type := LOWER(v_trans_type);
			-- DBMS_OUTPUT.put_line('Amount: ' || v_amount || 'Trans type: ' || v_trans_type);

			if (v_trans_type = 'purchase' or v_trans_type = 'p') then
			   	v_trans_type := '005';
			   	v_dr_cr := '001';
			   	v_reversal_flag := '000';
			elsif (v_trans_type = 'sale' or v_trans_type = 's') then
			   	v_trans_type := '005';
			   	v_dr_cr := '001';
			   	v_reversal_flag := '000';
			elsif (v_trans_type = 'refund' or v_trans_type = 'r') then
				v_trans_type := '006';
				v_dr_cr := '000';
				v_reversal_flag := '000';
			else
				v_trans_type := '005';
				v_dr_cr := '001';
				v_reversal_flag := '000';
			end if;

			if (v_CARD_ORGANIZATION = '' or v_CARD_ORGANIZATION is null) then
				if (SUBSTR(v_card_number, 0, 1) = '5') then
				   v_card_org := '002'; -- master card
				elsif (SUBSTR(v_card_number, 0, 1) = '4') then
				   v_card_org := '003'; -- visa
				elsif (SUBSTR(v_card_number, 0, 1) = '3') then
				   v_card_org := '006'; -- diner
				end if;
			else
				v_card_org := v_CARD_ORGANIZATION;
			end if;

			v_card_brand1 := v_CARD_BRAND;

			DBMS_OUTPUT.put_line('card_number: ' || v_card_number || '. card org: ' || v_card_org || '. record date: ' || v_record_date || '. posting date: ' || v_posting_date || '. trans date: ' || v_trans_date || '. trans type: ' || v_trans_type || '. dr/cr: ' || v_dr_cr);
			DBMS_OUTPUT.put_line('client_number: ' || v_client_number || '. merchant name: ' || v_merchant_name || '. merchant city: ' || v_merchant_city || '. merchant country: ' || v_merchant_country || '. terminal id: ' || v_terminal_id || '. currency: ' || v_currency);

			v_batch_trans_slip := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('042',1);
			v_batch_trans_slip := v_trans_prefix || SUBSTR(v_batch_trans_slip, 4);

			v_batch_trans_slip1 := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('042',1);
			v_batch_trans_slip1 := v_trans_prefix || SUBSTR(v_batch_trans_slip1, 4);

			v_batch_trans_slip2 := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('042',1);
			v_batch_trans_slip2 := v_trans_prefix || SUBSTR(v_batch_trans_slip2, 4);
			DBMS_OUTPUT.put_line('==> summary settlement: ' || v_batch_trans_slip || '. transaction slip 001: ' || v_batch_trans_slip1 || '. transaction slip 002: ' || v_batch_trans_slip2);

			Insert into INT_BATCH_CAPTURE (RECORD_DATE,VALUE_DATE,SETTLEMENT_DATE,TRANSACTION_STATUS,INSTITUTION_NUMBER,TRANSACTION_SLIP,NUMBER_ORIGINAL_SLIP,TRANSACTION_CLASS,TRANSACTION_CATEGORY,TRANSACTION_SOURCE,TRANSACTION_DESTINATION,TRANSACTION_DATE,TRANSACTION_TYPE,DR_CR_INDICATOR,REVERSAL_FLAG,CARD_NUMBER,BUSINESS_CLASS,MERCHANT_NAME,MERCHANT_CITY,MERCHANT_COUNTRY,MERCHANT_STATE,TERMINAL_CAPABILITY,CAPTURE_METHOD,AUTH_CODE,ACCT_NUMBER,TRANSACTION_REF,CLIENT_NUMBER,TRAN_AMOUNT_GR,TRAN_CURRENCY,TERMINAL_ID,TIME_TRANSACTION,CARD_EXPIRATION_DATE,AUDIT_TRAIL,CARD_BRAND,NUMBER_SLIPS,FILE_NUMBER,AUTHORIZED_BY,AUTHOR,SUMMARY_SETTLEMENT,ACCOUNT_ADDRESS,CLIENT_REFERENCE,DEST_ACCT_NUMBER,DEST_CARD_NUMBER,RETRIEVAL_REFERENCE,FILE_NUMBER_OUTWARD,CARD_SEQUENCE_NUMBER,CARD_ORGANIZATION,MERCHANT_NUMBER,ORIGINAL_REF_NUMBER,NUMBER_INSTALLMENTS,INSTALLMENT_AMOUNT,TRANS_NARRATIVE,LOAN_AMOUNT,LOAN_TYPE_ID,CASHBACK_AMOUNT,GROUP_NUMBER,UCAF_INDICATOR,TRACE_ID,FEE_SEQUENCE_NUMBER,FEE_DESCRIPTOR,ACQUIRER_REFERENCE,AVS,CVC2,RCC,SERVICE_CODE,LOCKING_COUNTER,VAU_RESPONSE_CODE,FIRST_INSTALLMENT_AMOUNT,DATE_FIRST_INSTALLMENT,ACQUIRING_BIN,POS_DATA_CODE,ACTION_CODE,FUNDING_DATE,USR_FIELDS,DCC_CONVERSION_FLAG,SETTLEMENT_AMOUNT_GR,SETTLEMENT_CURRENCY,DATE_FX_TRAN_SETTL,RATE_FX_TRAN_SETTL,PAYMENT_PROCESSOR_ID,SEC_CARD_NUMBER)
			values (v_record_date,null, null,v_transaction_status,v_institution_number,v_batch_trans_slip,v_batch_trans_slip,'001',v_trans_category,v_trans_source,null,v_trans_date,v_trans_type,v_dr_cr,v_reversal_flag,null,null,v_merchant_name,v_merchant_city,v_merchant_country,null,v_terminal_capability,v_capture_method,null,null,'002',v_client_number,v_amount,v_currency,v_terminal_id,null,null,v_audit_trail,v_card_brand1,v_number_slips,null,' ',v_user_id,v_batch_trans_slip,null,null,null,null,null,null,null, v_card_org, v_merchant_number,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,'0',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);

			Insert into INT_BATCH_CAPTURE (RECORD_DATE,VALUE_DATE,SETTLEMENT_DATE,TRANSACTION_STATUS,INSTITUTION_NUMBER,TRANSACTION_SLIP,NUMBER_ORIGINAL_SLIP,TRANSACTION_CLASS,TRANSACTION_CATEGORY,TRANSACTION_SOURCE,TRANSACTION_DESTINATION,TRANSACTION_DATE,TRANSACTION_TYPE,DR_CR_INDICATOR,REVERSAL_FLAG,CARD_NUMBER,BUSINESS_CLASS,MERCHANT_NAME,MERCHANT_CITY,MERCHANT_COUNTRY,MERCHANT_STATE,TERMINAL_CAPABILITY,CAPTURE_METHOD,AUTH_CODE,ACCT_NUMBER,TRANSACTION_REF,CLIENT_NUMBER,TRAN_AMOUNT_GR,TRAN_CURRENCY,TERMINAL_ID,TIME_TRANSACTION,CARD_EXPIRATION_DATE,AUDIT_TRAIL,CARD_BRAND,NUMBER_SLIPS,FILE_NUMBER,AUTHORIZED_BY,AUTHOR,SUMMARY_SETTLEMENT,ACCOUNT_ADDRESS,CLIENT_REFERENCE,DEST_ACCT_NUMBER,DEST_CARD_NUMBER,RETRIEVAL_REFERENCE,FILE_NUMBER_OUTWARD,CARD_SEQUENCE_NUMBER,CARD_ORGANIZATION,MERCHANT_NUMBER,ORIGINAL_REF_NUMBER,NUMBER_INSTALLMENTS,INSTALLMENT_AMOUNT,TRANS_NARRATIVE,LOAN_AMOUNT,LOAN_TYPE_ID,CASHBACK_AMOUNT,GROUP_NUMBER,UCAF_INDICATOR,TRACE_ID,FEE_SEQUENCE_NUMBER,FEE_DESCRIPTOR,ACQUIRER_REFERENCE,AVS,CVC2,RCC,SERVICE_CODE,LOCKING_COUNTER,VAU_RESPONSE_CODE,FIRST_INSTALLMENT_AMOUNT,DATE_FIRST_INSTALLMENT,ACQUIRING_BIN,POS_DATA_CODE,ACTION_CODE,FUNDING_DATE,USR_FIELDS,DCC_CONVERSION_FLAG,SETTLEMENT_AMOUNT_GR,SETTLEMENT_CURRENCY,DATE_FX_TRAN_SETTL,RATE_FX_TRAN_SETTL,PAYMENT_PROCESSOR_ID,SEC_CARD_NUMBER)
			values (v_record_date,null,null,v_transaction_status,v_institution_number,v_batch_trans_slip2,v_batch_trans_slip1,'002',v_trans_category,v_trans_source,null,v_trans_date,v_trans_type,v_dr_cr,v_reversal_flag,v_card_number,null,v_merchant_name,v_merchant_city,v_merchant_country,null,v_terminal_capability,v_capture_method,v_auth_code,null,'002',null,v_amount,v_currency,v_terminal_id,null, v_card_expiration_date ,    v_audit_trail,v_card_brand1,null,null,null,v_user_id,v_batch_trans_slip,null,null,null,null,null,null,null, v_card_org, v_merchant_number,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,'0',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    	END LOOP; -- Currency
		END LOOP; -- Card number
   		END LOOP; -- Client number

		COMMIT;

		-- run soa process to board merchant ?
		if (v_run_soa_process_id = 'Y' or v_run_soa_process_id = '1' or v_run_soa_process_id = 'y' or v_run_soa_process_id = '086') then
			SELECT BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(v_institution_number, '086', v_record_date) INTO v_params FROM DUAL;
			BW_process_control.run_process(v_institution_number, '086', v_params, v_user_id, v_station_number, v_audit_trail, 'v1','', P_OUT, '001');
			DBMS_OUTPUT.put_line('FINISHED SOA PROCESS - 086');
			COMMIT;
		end if;
	END IF;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/
select * from INT_BATCH_CAPTURE where institution_number = '00002001' and transaction_class in (001, 002) and transaction_status in ('008') order by record_date desc ;

