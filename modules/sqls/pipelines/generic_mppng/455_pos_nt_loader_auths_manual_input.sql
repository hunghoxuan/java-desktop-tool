/*
This SQL script's objective is insert random pos nt loeader auths. Save time because dont need to login into WebGui.
Input params:
- 1: institution_number
- 2: client_number (one to many, seperated by comma)
- 3: card_numbers (one to many, seperated by comma)
- 4: SERVICES:  ecomm, pos
- 5: trans_type: Sale, Reversal, Refund
- 6: currencies (one to many, seperated by comma)
Use cases: BA, Testers, Automation test.
Contributors: Hung, Glenn, Nga
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;

Declare
-- MANUAL PARAMS. Empty -> auto generate
v_user_id VARCHAR2(8) := '999999';
v_station_number VARCHAR2(3) := '129';

v_institution_number Varchar2(8) := '&1'; -- 00002001
v_client_number Varchar2(120) := '&2';
v_card_number varchar2(190) := '&3'; -- MC: 5115800300000001. 5154968300000008 .Visa: 4999224500000014. Diner: 36961800000003 Empty --> auto gen card number based on card organization & country
v_service varchar(15) := '&4'; -- ecomm, pos
v_trans_type_all varchar(30) := '&5'; -- Sale, Reversal, Refund
v_currency_all VARCHAR2(20) := '&6'; --

v_auth_code  VARCHAR2(6) := 'S93967';
v_exp VARCHAR2(6) := '3012';  -- card expiry

v_amount_min PLS_INTEGER := 5;
v_amount_max PLS_INTEGER := 200; -- max = min --> fixed amount. max > min : random amount
v_amount VARCHAR2(11) := '';
v_currency VARCHAR2(3) := '978'; -- default value if currencies is not provided.
v_trans_type varchar(30) := 'sale';
v_country VARCHAR2(3) := '280';

v_product_id VARCHAR2(3) := 'MCC';
v_sub_product VARCHAR2(3) := 'MCC';
v_mcc Varchar2(4) := '5999';
v_bypass_enabled VARCHAR2(1) := 'N'; -- N: No,
v_card_service VARCHAR2(3) := '001'; -- 001: credit card
v_host_name varchar(30) := 'MANUAL_INPUT';

V_BPR_TYPE_STR varchar(20) := 'BPR_HST_ISS';

v_timeliness PLS_INTEGER := 1; -- x day ahead / after posting date
v_date Varchar2(8) := ''; -- yyyyMMDD. Empty -> auto generate base on Posting_date - timeliness
v_date_m Varchar2(4) := '';

v_run_soa_process_id varchar2(3) := ''; -- 086/Y: run soa process for batch input, empty: dont run soa process

-- CACULATED PARAMS
v_record_date VARCHAR2(8);
v_trans_date VARCHAR2(8);
v_posting_date VARCHAR2(8);
v_system_date VARCHAR2(8);
v_system_short_date VARCHAR2(4);
v_system_time VARCHAR2(8);
v_time VARCHAR2(8);
v_amt VARCHAR2(14);
v_merchant_number varchar2(15);
v_merchant_name VARCHAR2(25);
v_merchant_city VARCHAR2(35);
v_merchant_all VARCHAR2(100);
v_merchant_country VARCHAR2(3);
v_merchant_state VARCHAR2(3);
v_terminal_id Varchar2(8);
v_pstcd varchar(11);
v_CARD_ORGANIZATION VARCHAR2(3); -- 002: mc, 003: visa, 006: diner

v_transaction_link_id Varchar2(30);
v_trn_id Varchar2(30);
v_retrieval_reference Varchar2(12);
v_external_reference Varchar2(12);
v_message_type1 varchar2(4);
v_message_type4 varchar2(4);
v_process_code varchar2(6);

v_stan Varchar2(8);
v_stan2 Varchar2(8);

V_BPR_LOG_ID NUMBER;
v_retrieval_reference_number NUMBER;
v_i NUMBER;
v_trace_id varchar(22);

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
    if (v_service is null or v_service = '') then
		v_service := 'ecomm,pos';
	end if;
	if (v_currency_all = '' or v_currency_all is null) then
		v_currency_all := '978';
	end if;
	if (v_trans_type_all = '' or v_trans_type_all is null) then
		v_trans_type_all := '*';
	end if;

	IF (v_errors is null or v_errors = '') THEN
		FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
		 	BEGIN
		 		v_client_number := trim(curClientNumber.str);
				SELECT TRADE_NAME INTO v_merchant_name FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				SELECT CLIENT_CITY INTO v_merchant_city FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				SELECT client_country into v_merchant_country FROM CIS_CLIENT_DETAILS WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
			EXCEPTION WHEN NO_DATA_FOUND THEN
				DBMS_OUTPUT.put_line('Client Number not found in CIS_CLIENT_DETAILS: ' || v_client_number);
				CONTINUE;
			END;
	 	FOR curCardNumber in (WITH DATA AS ( SELECT v_card_number str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
	  	FOR curCardService in (WITH DATA AS ( SELECT v_service str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
		FOR curTransType in (WITH DATA AS ( SELECT v_trans_type_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
		FOR curCurrency in (WITH DATA AS ( SELECT v_currency_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP

			v_client_number := trim(curClientNumber.str);
			v_card_number := trim(curCardNumber.str);  -- get card number
			v_service := trim(curCardService.str);
			v_currency := trim(curCurrency.str);
            v_trans_type := trim(curTransType.str);

			v_date := '';
			v_retrieval_reference := '';
			v_CARD_ORGANIZATION := '';
			v_stan := '';
			v_transaction_link_id := '';
			v_trn_id := '';
			v_country := '';
			v_merchant_number := '';
			v_terminal_id := '';
			v_amount := '';

			if (v_i is null) then
				v_i := 0;
			else
				v_i := v_i + 4;
			end if;

			if (v_CARD_ORGANIZATION = '' or v_CARD_ORGANIZATION is null) then
				if (SUBSTR(v_card_number, 0, 1) = '5') then
				   v_CARD_ORGANIZATION := '002'; -- master card
				elsif (SUBSTR(v_card_number, 0, 1) = '4') then
				   v_CARD_ORGANIZATION := '003'; -- visa
				elsif (SUBSTR(v_card_number, 0, 1) = '3') then
				   v_CARD_ORGANIZATION := '006'; -- diner
				else
					v_CARD_ORGANIZATION := '002'; -- master card
				end if;
			end if;

			if (v_card_number is null or v_card_number = '') then
				GENERATECARD(v_CARD_ORGANIZATION, v_product_id, v_sub_product, v_bypass_enabled, v_country, v_card_service, '', '', v_institution_number, v_card_number);
				DBMS_OUTPUT.put_line('Card Generated: ' || v_card_number);
			end if;

              	-- AUTO-CACULATED PARAMS
			if (v_posting_date is null or v_posting_date = '') then
				SELECT POSTING_DATE INTO v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number; -- posting date
			end if;

			if (v_date is null or v_date = '') then
				 v_date := v_posting_date - v_timeliness;
				 v_date_m := SUBSTR(v_date, -4);
			end if;

			SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL;
			SELECT TO_CHAR(SYSDATE, 'MMDD') INTO v_system_short_date FROM DUAL;

			SELECT TO_CHAR(SYSDATE, 'hh24:mi:ss') INTO v_system_time FROM DUAL;
			SELECT TO_CHAR(SYSDATE, 'hh24miss') INTO v_time FROM DUAL;

			-- DBMS_OUTPUT.put_line(TO_CHAR(SYSDATE, 'DDDhh24miss') || TRIM(TO_CHAR(v_i, '000')));

			if (v_retrieval_reference is null or v_retrieval_reference = '') then
				SELECT TO_NUMBER(TO_CHAR(SYSDATE, 'DDDhh24miss') || TRIM(TO_CHAR(v_i, '0000'))) INTO v_retrieval_reference_number FROM DUAL;
				v_retrieval_reference := TO_CHAR(v_retrieval_reference_number + 1);
				v_external_reference := TO_CHAR(v_retrieval_reference_number + 2);
			end if;

			-- DBMS_OUTPUT.put_line(TO_CHAR(v_i, '000') ||  ' -- ' || TO_CHAR(v_retrieval_reference));

			if (v_stan is null or v_stan = '') then
				v_stan := '093' || substr(to_char(v_retrieval_reference),-3);
				v_stan2 := '093' || substr(to_char(v_retrieval_reference),-3);
			end if;

			SELECT TO_NUMBER(TO_CHAR(SYSDATE, 'DDDhh24miss') || TRIM(TO_CHAR(v_i, '000'))) INTO V_BPR_LOG_ID FROM DUAL;

			if (v_transaction_link_id is null or v_transaction_link_id = '') then
				v_transaction_link_id := v_institution_number || '-' || SUBSTR(TO_CHAR(V_BPR_LOG_ID + 1), -10) || '-' || SUBSTR(v_retrieval_reference, -10);
			end if;

			if (v_trn_id is null or v_trn_id = '') then
				v_trn_id := v_transaction_link_id;
			end if;

			v_trace_id := v_retrieval_reference;

			v_merchant_name := rpad(v_merchant_name, 25);
			v_merchant_city := rpad(v_merchant_city, 35);
			v_merchant_country := rpad(v_merchant_country, 3);
			v_merchant_all := v_merchant_name || v_merchant_city || v_merchant_country;

			if (v_country is null or v_country = '') then
				 v_country := v_merchant_country;
			end if;

			BEGIN
		 		if (v_merchant_number is null or v_merchant_number = '') then
					SELECT merchant_id into v_merchant_number FROM CIS_DEVICE_LINK WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				end if;
				if (v_terminal_id is null or v_terminal_id = '') then
					SELECT terminal_id into v_terminal_id FROM CIS_DEVICE_LINK WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
				end if;
			EXCEPTION WHEN NO_DATA_FOUND THEN
				DBMS_OUTPUT.put_line('Terminal not found in CIS_DEVICE_LINK: ' || v_client_number);
				v_terminal_id := '00000000';
				v_merchant_number := '0000000' || v_client_number;
			END;

			if (v_amount is null or v_amount = '') then
				select floor(dbms_random.value(v_amount_min,v_amount_max)) into v_amount from dual; -- get random amount, v_amount := '100.00';
				v_amount := v_amount || '.00';
			end if;

			if (instr(v_amount, '.') > 0) then
			    v_amt := lpad(replace(v_amount, '.', ''), 12, '0');
			else
				v_amt := lpad(v_amount, 10, '0') || '00';
			end if;

			if (v_trans_type_all = '' or v_trans_type_all is null or v_trans_type_all = '*') then -- if trans_type is random then set to Refund if v_amount is small enough
				if (floor(v_amount) < 30) then
            		v_trans_type := 'refund';
   				elsif (floor(v_amount) < 60) then -- if trans_type is random then set to Refund if v_amount is small enough
            		v_trans_type := 'completion';
       			elsif (floor(v_amount) < 100) then -- if trans_type is random then set to Refund if v_amount is small enough
            		v_trans_type := 'reversal';
            	else
            		v_trans_type := 'sale';
	      		end if;
	      	else
	       		v_trans_type := v_trans_type_all;
	        end if;

			v_trans_type := LOWER(v_trans_type);

			if (v_trans_type = 'completion' or v_trans_type = 'c') then
			   	v_message_type1 := '0220';
				v_message_type4 := '0230';
				v_process_code := '000000';
			elsif (v_trans_type = 'sale' or v_trans_type = 's') then
			   	v_message_type1 := '0200';
				v_message_type4 := '0210';
				v_process_code := '000000';
			elsif (v_trans_type = 'reversal' or v_trans_type = 'rv') then
			   	v_message_type1 := '0420';
				v_message_type4 := '0430';
				v_process_code := '000000';
			elsif (v_trans_type = 'refund' or v_trans_type = 'r') then
			   	v_message_type1 := '0200';
				v_message_type4 := '0210';
				v_process_code := '200000';
			else
				v_message_type1 := '0200';
				v_message_type4 := '0210';
				v_process_code := '000000';
			end if;

			DBMS_OUTPUT.put_line('SVC: ' || v_service || '. v_card_number: ' || v_card_number || '. Org: ' || v_card_organization || ' .date: ' || v_date  || 'time: ' || v_time || '. posting date: ' || v_posting_date || '. V_BPR_LOG_ID: ' || V_BPR_LOG_ID || '. v_retrieval_reference: ' || v_retrieval_reference || '. v_transaction_link_id: ' || v_transaction_link_id || '. v_trn_id: ' || v_trn_id || ' amount: ' || v_amount || '->' || v_amt || ' vstan: ' || v_stan || '. trans type: ' || v_trans_type);

			-- START
			if (v_transaction_link_id is null or v_transaction_link_id = '' or V_BPR_LOG_ID is null or V_BPR_LOG_ID = 0 or v_card_number is null or v_card_number = '') then
				DBMS_OUTPUT.put_line('CANCELLED. Transaction_link_id or brp_log_id or card_number is null !!');
			else
				----
				Update cis_device_link
				Set DATE_SETTLEMENT = ''
				where institution_number = v_institution_number
				and client_number = v_client_number;

				UPDATE cos_bpr_data
				SET Transaction_status = '999',
				BATCH_ID = '',
				NETWORK_ID =''
				where institution_number = v_institution_number
				and transaction_link_id in (v_transaction_link_id);

			    delete from cos_bpr_data where institution_number = v_institution_number and transaction_link_id in (v_transaction_link_id);
				delete from cos_bpr_log where transaction_link_id in (v_transaction_link_id);

			   	if (v_service = 'ecomm' and v_card_organization = '002') then
					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,NULL,v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,to_date(v_date , 'YYYYMMDD'),NULL,NULL,'012','59','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000800/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_MCR_ACQ',v_institution_number,'0100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000018968','00000200430','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,NULL,v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'812','10251000066008261112223337','00TCC: /DE48.42:0103210/DE48.43:N/DE48.61:00001/DE48.92:Y/DE52:N/BIN_CNTRY:MT/PAN_SVC:001/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_MCR_ACQ',v_institution_number,'0110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000018968','00000200430','999',NULL,NULL,v_card_number,NULL,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,NULL,'10251000066008261112223337','00TCC:T/DE15:' || v_date_m || '/DE48.42:0103210/DE48.43:N/DE48.87:M/DE48.92:N/DE52:N/DE63:' || v_transaction_link_id || '/BIN_CNTRY:MT/PAN_SVC:001/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name || '');

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,NULL,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'012',NULL,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:060/CVC2_RC:M/MERC_RC:06/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"date":"' || v_date_m || '","mti":"0200","type":"00","tpdu_header":"00000000303030310100","hst":{"external_ref":"12345678901234567890                "},"pos":{"dat":"10009010","cond":"59","pin_cap":"2","pan_in":"01","id":"' || v_terminal_id || '"},"crd":{"exp":"' || v_exp || '","cvc2":"***","pan":"****************"},"to_idx":"00","rrn":"' || v_retrieval_reference || '","tx_dt":"' || v_date_m || v_time || '","fin":{"fx_dt":"' || v_date_m || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"merc":{"city":"' || v_merchant_city || '","name":"' || v_merchant_name || '","mcc":"' || v_mcc || '","ctry":"GB","id":"' || v_merchant_number || '"},"fr_idx":"00","time":"' || v_time || '","ecom":{"sec":"210"},"stan":"' || v_stan || '","aq_id":"00000999"},"hdr":{"dt":{"us":"734494","s":"1666003774"},"sts":0,"dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","ttl":2,"type":6,"src":"ADT_HST/' || v_host_name || '/1","rte":4,"lnk_id":"' || v_transaction_link_id || '","key":"BPR_HST_RISK_ISS0000000099900001000000' || v_date_m || v_time || v_stan || '","id":"634d333e-0cb9c9b682-900001"}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"BPR_MCR_ACQ/' || v_host_name || '/1","dst":"ADT_MCR","agt":"","type":6,"ttl":2,"rte":4,"sts":4,"size":297,"flds":119,"max_time":10,"id":"634d3341-2643044fec-957d0f","dt":{"s":1666003777,"us":437932},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":2,"usr_flgs":0,"tmo":0},"msg":{"mti":"0100","sig":"GEN00STD00","crd":{"pan":"                ","exp":"' || v_exp || '","cvc2":"   "},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","fx_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_date || '","stan":"' || v_stan2 || '","time":"' || v_time || '","date":"' || v_date_m || '","merc":{"mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","name":"' || v_merchant_name || '","city":"' || v_merchant_city || '","ctry":"GBR"},"pos":{"pan_in":"81","pin_cap":"2","id":"' || v_terminal_id || '","ctry":"' || v_country || '","pstcd":"1112223337"},"aq_id":"018968","fw_id":"200430","rrn":"' || v_retrieval_reference || '","mcr":{"de48":{"sf61":"00001"},"de61":{"sf1":"1","sf2":"0","sf3":"2","sf4":"5","sf5":"1","sf6":"0","sf7":"0","sf8":"0","sf9":"0","sf10":"6","sf11":"6","sf12":"00"},"tcc":" "},"ecom":{"sec":"0103210"}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"ADT_MCR/' || v_host_name || '/1","dst":"BPR_MCR_ACQ/' || v_host_name || '/1","agt":"","type":6,"ttl":2,"rte":5,"sts":4,"size":239,"flds":119,"max_time":10,"id":"634d3341-f071875646-9390cf","dt":{"s":1666003777,"us":690505},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":0,"usr_flgs":0,"tmo":0},"msg":{"mti":"0110","sig":"GEN00STD00MCR00","crd":{"pan":"                "},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","fx_bl":"00000000","st_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_date || '","stan":"' || v_stan2 || '","aq_id":"018968","fw_id":"200430","rrn":"' || v_retrieval_reference || '","auth_cd":"' || v_auth_code || '","rc":"00","pos":{"id":"' || v_terminal_id || '"},"mcr":{"de63":"' || v_trace_id || '","tcc":"T"},"vip":{"cvc2_dat":"M"},"ecom":{"sec":"0103210"}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"fr_idx":"00","mti":"0210","type":"00","tpdu_header":"00000000303030310100","merc":{"rc":"06"},"pos":{"id":"' || v_terminal_id || '"},"crd":{"pan":"****************","avs":{},"cvc2_rc":"M"},"to_idx":"00","ecom":{},"rc":"00","emv":{},"auth_cd":"' || v_auth_code || '","tx_dt":"' || v_date_m || v_time || '","orig":{},"net":{},"time":"' || v_time || '","date":"' || v_date_m || '","aq_id":"00000999","fin":{"trace_id":"' || v_trace_id || v_date_m || '  ","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"bw":{"inst":"' || v_institution_number || '"},"rrn":"' || v_retrieval_reference || '","pymt":{"acct":{}},"bat":{},"stan":"' || v_stan || '","tkn":{}},"hdr":{"dt":{"us":"756343","s":"1666003777"},"rte":0,"ttl":0,"type":6,"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1","sts":0,"lnk_id":"' || v_transaction_link_id || '","id":"634d3341-baa365bd4b-bda705"}}'),v_transaction_link_id);
			        DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);

				elsif (v_service = 'pos' and v_card_organization = '002') then
					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc, v_card_number || '=' || v_exp || '221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'901','00','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000800/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_MCR_ACQ',v_institution_number,'0100',to_date(v_date , 'YYYYMMDD'), to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000018968','00000200430','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc, v_card_number || '=' || v_exp || '221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'901','00000000008008261112223334','00TCC: /DE48.43:N/DE48.61:00001/DE48.92:N/DE52:N/BIN_CNTRY:MT/PAN_SVC:001/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_MCR_ACQ',v_institution_number,'0110',to_date(v_date , 'YYYYMMDD'), to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000018968','00000200430','999',NULL,NULL,v_card_number,NULL, v_process_code,NULL,NULL,							v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,NULL,'00000000008008261112223334','00TCC:R/DE15:' || v_date_m || '/DE48.43:N/DE48.92:N/DE52:N/DE63:' || v_trace_id || '/BIN_CNTRY:MT/PAN_SVC:001/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,NULL, v_process_code,NULL,NULL,							v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'901',NULL,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:060/MERC_RC:06/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"rte":4,"dt":{"us":"785602","s":"1665756386"},"ttl":2,"type":6,"key":"BPR_HST_RISK_ISS0000000099900000000000' || v_date_m || v_time || v_stan || '","id":"63496ce2-b9790fe99f-c5d333","lnk_id":"' || v_transaction_link_id || '","sts":0,"dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","src":"ADT_HST/' || v_host_name || '/1"},"msg":{"time":"' || v_system_time || '","ecom":{"sec":"210"},"tpdu_header":"00000000303030310100","rrn":"' || v_retrieval_reference || '","hst":{"external_ref":"12345678901234567890                "},"crd":{"trk2":"********************************","pan":"****************","exp":"' || v_exp || '"},"tx_dt":"' || v_date_m || v_time || '","fin":{"amt":"' || v_amt || '","ccy":"' || v_currency || '","fx_dt":"' || v_date_m || '"},"merc":{"mcc":"' || v_mcc || '","ctry":"' || v_country || '","name":"' || v_merchant_name || '","city":"' || v_merchant_city || '","id":"' || v_terminal_id || '"},"pos":{"id":"33224511","pin_cap":"1","dat":"50000800","pan_in":"90","cond":"00"},"type":"01","stan":"' || v_stan || '","fr_idx":"00","to_idx":"00","aq_id":"00000999","date":"' || v_date_m || '","mti":"0200"}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"BPR_MCR_ACQ/' || v_host_name || '/1","dst":"ADT_MCR","agt":"","type":6,"ttl":2,"rte":4,"sts":4,"size":312,"flds":111,"max_time":10,"id":"63496ce6-69fbe5e89a-4541e9","dt":{"s":1665756390,"us":606995},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":2,"usr_flgs":0,"tmo":0},"msg":{"mti":"0100","sig":"GEN00STD00","crd":{"pan":"                ","exp":"' || v_exp || '","trk2":"                                "},"type":"17","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","fx_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","time":"' || v_system_time || '","date":"' || v_date_m || '","merc":{"mcc":"' || v_mcc || '","id":"' || v_terminal_id || '","name":"' || v_merchant_name || '","city":"' || v_merchant_name || '","ctry":"' || v_country || '"},"pos":{"pan_in":"90","pin_cap":"1","id":"33224511","ctry":"' || v_country || '","pstcd":"1112223334"},"aq_id":"018968","fw_id":"200430","rrn":"' || v_external_reference || '","mcr":{"de48":{"sf61":"00001"},"de61":{"sf1":"0","sf2":"0","sf3":"0","sf4":"0","sf5":"0","sf6":"0","sf7":"0","sf8":"0","sf9":"0","sf10":"0","sf11":"8","sf12":"00"},"tcc":" "}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"ADT_MCR/' || v_host_name || '/1","dst":"BPR_MCR_ACQ/' || v_host_name || '/1","agt":"","type":6,"ttl":2,"rte":5,"sts":4,"size":225,"flds":111,"max_time":10,"id":"63496ce7-321cd8c68a-5a510d","dt":{"s":1665756391,"us":86993},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":0,"usr_flgs":0,"tmo":0},"msg":{"mti":"0110","sig":"GEN00STD00MCR00","crd":{"pan":"                "},"type":"17","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","fx_bl":"00000000","st_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","aq_id":"018968","fw_id":"200430","rrn":"' || v_external_reference || '","auth_cd":"' || v_auth_code || '","rc":"00","pos":{"id":"33224511"},"mcr":{"de63":"' || v_trace_id || '","tcc":"R"}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"rte":0,"dt":{"us":"160999","s":"1665756391"},"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1","type":6,"id":"63496ce7-1c0bde8c11-65d8e5","sts":0,"lnk_id":"' || v_transaction_link_id || '","ttl":0},"msg":{"time":"' || v_system_time || '","net":{},"tpdu_header":"00000000303030310100","rrn":"' || v_retrieval_reference || '","crd":{"pan":"****************","avs":{}},"tx_dt":"' || v_date_m || v_time || '","bat":{},"fin":{"amt":"' || v_amt || '","trace_id":' || v_trace_id || v_date_m || '  ","ccy":"' || v_currency || '"},"emv":{},"pos":{"id":"33224511"},"date":"' || v_date_m || '","fr_idx":"00","merc":{"rc":"06"},"rc":"00","bw":{"inst":"' || v_institution_number || '"},"type":"01","auth_cd":"' || v_auth_code || '","orig":{},"ecom":{},"tkn":{},"to_idx":"00","stan":"' || v_stan || '","aq_id":"00000999","pymt":{"acct":{}},"mti":"0210"}}'),v_transaction_link_id);
			       	DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);

			    elsif (v_service = 'ecomm' and v_card_organization = '003') then
					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,NULL,v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,to_date(v_date , 'YYYYMMDD'),NULL,NULL,'012','59','00DE52:N/DE48.43:Y/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:10009010/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_VISA_ACQ',v_institution_number,'0100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000123456','00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,NULL,v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'012','090000000700','00DE25:59/DE60.8:07/DE52:N/DE126.10:Y/BIN_CNTRY:MT/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_VISA_ACQ',v_institution_number,'0110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000123456','00000000000','999',NULL,NULL,v_card_number,NULL,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,NULL,'0900000000','00DE25:59/DE60.8:00/DE62.2:' || v_trace_id || '/DE52:N/DE44.2: /DE44.5:2/DE44.10:M/DE126.10:Y/BIN_CNTRY:MT/DE44.1:V/DE44.3: /',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,NULL,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'012',NULL,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:009/CVC2_RC:M/MERC_RC:06/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"type":"00","hst":{"external_ref":"12345678901234567890                "},"to_idx":"00","aq_id":"00000999","fr_idx":"00","merc":{"name":"' || v_merchant_name || '","mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","city":"' || v_merchant_city || '","ctry":"' || v_country || '"},"ecom":{"sec":"210"},"pos":{"dat":"10009010","pan_in":"01","id":"' || v_terminal_id || '","pin_cap":"2","cond":"59"},"tx_dt":"' || v_date_m || v_time || '","rrn":"' || v_retrieval_reference || '","crd":{"exp":"' || v_exp || '","pan":"****************","cvc2":"***"},"tpdu_header":"00000000303030310100","fin":{"amt":"' || v_amt || '","fx_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"date":"' || v_date_m || '","time":"' || v_system_time || '","mti":"0200","stan":"' || v_stan || '"},"hdr":{"ttl":2,"sts":0,"id":"63496bbc-0a39bb8541-e11f35","lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1665756092","us":"808618"},"key":"BPR_HST_RISK_ISS0000000099900000000000' || v_date_m || v_time || v_stan || '","src":"ADT_HST/' || v_host_name || '/1","type":6,"dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","rte":4}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"BPR_VISA_ACQ/' || v_host_name || '/1","dst":"ADT_VISA","agt":"","type":6,"ttl":2,"rte":4,"sts":4,"size":447,"flds":249,"max_time":10,"id":"63496bbf-1fa267d2af-1c3c7e","dt":{"s":1665756095,"us":594409},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":2,"usr_flgs":0,"tmo":0},"msg":{"mti":"0100","sig":"GEN00STD00VIP00","crd":{"pan":"                ","exp":"' || v_exp || '"},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","time":"' || v_system_time || '","date":"' || v_date_m || '","merc":{"mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","name":"' || v_merchant_name || '","city":"' || v_merchant_city || '","ctry":"' || v_country || '"},"aq_ctry":"470","pos":{"pan_in":"01","pin_cap":"2","cond":"59","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"' || v_external_reference || '","vip":{"de60":{"sf1":"0","sf2":"9","sf3_4_5":"0000","sf6_7":"00","sf8":"07","sf9":"0","sf10":"0"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"0000","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"},"cvc2_dat":"      "}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"ADT_VISA/' || v_host_name || '/1","dst":"BPR_VISA_ACQ/' || v_host_name || '/1","agt":"","type":6,"ttl":2,"rte":5,"sts":4,"size":414,"flds":247,"max_time":10,"id":"63496bbf-93d413eaa4-8178da","dt":{"s":1665756095,"us":886396},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":0,"usr_flgs":0,"tmo":0},"msg":{"mti":"0110","sig":"GEN00STD00VIP00","crd":{"pan":"                ","avs":{"rc":" "},"cvc_rc":"2","cvc2_rc":"M"},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","st_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","aq_ctry":"470","pos":{"cond":"59","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"' || v_external_reference || '","auth_cd":"' || v_auth_code || '","rc":"00","merc":{"id":"' || v_merchant_number || '"},"vip":{"de44":{"sf1":"V","sf3":" ","sf4":" ","sf6":"  ","sf7":" ","sf8":" ","sf9":" ","sf11":"  ","sf12":" "},"de60":{"sf1":"0","sf2":"9","sf3_4_5":"0000","sf6_7":"00","sf8":"00"},"de62":{"sf2":"' || v_trace_id || '"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"009C","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"}}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"ttl":0,"type":6,"sts":0,"id":"63496bc0-a690a5a8f1-297830","lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1665756096","us":"31397"},"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1","rte":0},"msg":{"stan":"' || v_stan || '","to_idx":"00","bat":{},"tkn":{},"fr_idx":"00","bw":{"inst":"' || v_institution_number || '"},"merc":{"rc":"06"},"crd":{"pan":"****************","avs":{},"cvc2_rc":"M"},"rc":"00","emv":{},"ecom":{},"pos":{"id":"' || v_terminal_id || '"},"type":"00","tx_dt":"' || v_date_m || v_time || '","fin":{"amt":"' || v_amt || '","trace_id":"' || v_trace_id || '","ccy":"' || v_currency || '"},"rrn":"' || v_retrieval_reference || '","net":{},"tpdu_header":"00000000303030310100","mti":"0210","aq_id":"00000999","date":"' || v_date_m || '","auth_cd":"' || v_auth_code || '","time":"' || v_system_time || '","pymt":{"acct":{}},"orig":{}}}'),v_transaction_link_id);
			      	DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);

				elsif (v_service = 'pos' and v_card_organization = '003') then
					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,to_date(v_date , 'YYYYMMDD'),NULL,NULL,'901','00','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000800/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_VISA_ACQ',v_institution_number,'0100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000123456','00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'901','050000000000','00DE25:00/DE60.8:00/DE52:N/DE126.10:N/BIN_CNTRY:MT/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_VISA_ACQ',v_institution_number,'0110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_external_reference,v_stan2,'00000123456','00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'00DE25:00/DE62.2:' || v_trace_id || '/DE52:N/DE44.2: /DE44.5:2/DE44.10:P/DE126.10:N/BIN_CNTRY:MT/DE44.1:V/DE44.3: /',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

			        INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,NULL,v_process_code,NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,v_currency,v_amount,NULL,NULL,NULL,NULL,NULL,'901',NULL,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:009/CVC2_RC:P/MERC_RC:06/',NULL,NULL,NULL,v_merchant_number,v_merchant_all,NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name || '');

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"mti":"0200","rrn":"' || v_retrieval_reference || '","ecom":{"sec":"210"},"aq_id":"00000999","fin":{"fx_dt":"' || v_date_m || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","pos":{"pin_cap":"1","pan_in":"90","id":"' || v_terminal_id || '","cond":"00","dat":"50000800"},"date":"' || v_date_m || '","merc":{"mcc":"' || v_mcc || '","name":"' || v_merchant_name || '","id":"' || v_merchant_number || '","ctry":"' || v_country || '","city":"' || v_merchant_city || '"},"type":"00","tpdu_header":"00000000303030310100","crd":{"pan":"****************","trk2":"********************************","exp":"' || v_exp || '"},"hst":{"external_ref":"12345678901234567890                "},"time":"' || v_system_time || '","fr_idx":"00","stan":"' || v_stan || '","to_idx":"00"},"hdr":{"lnk_id":"' || v_transaction_link_id || '","type":6,"sts":0,"ttl":2,"dt":{"us":"955345","s":"1665755467"},"src":"ADT_HST/' || v_host_name || '/1","id":"6349694b-2ca953705f-10edfb","key":"BPR_HST_RISK_ISS0000000099900000000000' || v_date_m || v_time || v_stan || '","dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","rte":4}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"BPR_VISA_ACQ/' || v_host_name || '/1","dst":"ADT_VISA","agt":"","type":6,"ttl":2,"rte":4,"sts":4,"size":478,"flds":249,"max_time":10,"id":"6349694e-3fd86ce139-82ebd1","dt":{"s":1665755470,"us":284104},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":2,"usr_flgs":0,"tmo":0},"msg":{"mti":"0100","sig":"GEN00STD00VIP00","crd":{"pan":"                ","exp":"' || v_exp || '","trk2":"                                "},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","time":"' || v_system_time || '","date":"' || v_date_m || '","merc":{"mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","name":"' || v_merchant_name || '","city":"' || v_merchant_city || '","ctry":"' || v_country || '"},"aq_ctry":"470","pos":{"pan_in":"90","pin_cap":"1","cond":"00","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"' || v_external_reference || '","vip":{"de60":{"sf1":"0","sf2":"5","sf3_4_5":"0000","sf6_7":"00","sf8":"00","sf9":"0","sf10":"0"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"0000","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"},"cvc2_dat":"      "}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"ADT_VISA/' || v_host_name || '/1","dst":"BPR_VISA_ACQ/' || v_host_name || '/1","agt":"","type":6,"ttl":2,"rte":5,"sts":4,"size":409,"flds":247,"max_time":10,"id":"6349694e-03cbb7b3b5-a254c6","dt":{"s":1665755470,"us":540261},"tx_dt":{"s":0,"us":0},"trn_id":"' || v_trn_id || '","lnk_id":"' || v_transaction_link_id || '","flgs":0,"usr_flgs":0,"tmo":0},"msg":{"mti":"0110","sig":"GEN00STD00VIP00","crd":{"pan":"                ","avs":{"rc":" "},"cvc_rc":"2","cvc2_rc":"P"},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","st_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","stan":"' || v_stan2 || '","aq_ctry":"470","pos":{"cond":"00","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"' || v_external_reference || '","auth_cd":"' || v_auth_code || '","rc":"00","merc":{"id":"' || v_merchant_number || '"},"vip":{"de44":{"sf1":"V","sf3":" ","sf4":" ","sf6":"  ","sf7":" ","sf8":" ","sf9":" ","sf11":"  ","sf12":" "},"de62":{"sf2":"' || v_trace_id || '"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"0096","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"}}}}'),v_transaction_link_id);
			       	INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			       	VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"lnk_id":"' || v_transaction_link_id || '","rte":0,"type":6,"sts":0,"ttl":0,"dt":{"us":"682106","s":"1665755470"},"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1","id":"6349694e-db9f303bab-902efc"},"msg":{"mti":"0210","orig":{},"rrn":"' || v_retrieval_reference || '","ecom":{},"net":{},"fr_idx":"00","tx_dt":"' || v_date_m || v_time || '","pos":{"id":"33224516"},"auth_cd":"' || v_auth_code || '","date":"' || v_date_m || '","stan":"' || v_stan || '","pymt":{"acct":{}},"type":"00","tpdu_header":"00000000303030310100","rc":"00","crd":{"pan":"****************","avs":{},"cvc2_rc":"P"},"time":"' || v_system_time || '","fin":{"trace_id":"' || v_trace_id || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tkn":{},"aq_id":"00000999","emv":{},"merc":{"rc":"06"},"bw":{"inst":"' || v_institution_number || '"},"bat":{},"to_idx":"00"}}'),v_transaction_link_id);
					DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);

				elsif (v_service = 'ecomm' and v_card_organization = '006') then
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,null,null,null,null,null,null,null,v_currency,v_amount,null,null,null,null,null,'051','00','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000150/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_external_reference,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_DNR_GA_ACQ',v_institution_number,'1100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'00000018968','00000200430','999',null,null,'36961800000003',v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,null,null,null,null,null,null,null,v_currency,v_amount,null,null,null,null,null,null,'501101501300','00DE40:N/DE52:N/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,null,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_DNR_GA_ACQ',v_institution_number,'1110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'00000018968','00000200430','999',null,null,'36961800000003',v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,'081',null,v_auth_code,v_currency,v_amount,null,null,v_currency,v_amount,null,null,null,null,null,null,'501101501300','00DE40:N/DE52:N/DE123:' || v_trace_id || '/DE44.4:00000000000/CAVV_RESULT:06/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_trace_id,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,null,v_process_code,null,null,v_terminal_id,'00',null,v_auth_code,null,null,null,null,v_currency,v_amount,null,null,null,null,null,'051',null,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:183/CVC2_RC:M/MERC_RC:06/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_external_reference,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"fin":{"amt":"' || v_amt || '","fx_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","tpdu_header":"00000000303030310100","hst":{"external_ref":"12345678901234567890                "},"to_idx":"00","pos":{"cond":"00","id":"33016405","dat":"50000150","pin_cap":"1","pan_in":"05"},"aq_id":"' || v_institution_number || '","crd":{"trk2":"********************************","exp":"2212","pan":"****************"},"ecom":{"sec":"210"},"fr_idx":"00","merc":{"mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","ctry":"GB","city":"' || v_merchant_city || '","name":"' || v_merchant_name || '"},"type":"00","mti":"0200","time":"' || v_system_time || '","date":"' || v_date_m || '","stan":"' || v_stan || '","emv":{"tg9f36":"0001","tg95":"4080000000","tg9f09":"0002","tg9f35":"22","tg9f1e":"07B1C1D4","tg9f1a":"0826","tg9c":"00","tg9f33":"E0F8C8","tg5f34":"00","tg9f10":"06010A03043000","tg9f37":"00000001","tg9f26":"07B1C1D4F53812BA","tg9f34":"410302","tg84":"A0000000031010","tg9a":"220709","tg9f02":"000000000100","tg9f53":"R","tg9f03":"000000000000","tg82":"5800","tg5f2a":"0978","tg9f27":"80","tg9f41":"00000175"},"rrn":"' || v_retrieval_reference || '"},"hdr":{"sts":0,"id":"62c927f8-b952749571-69d95b","dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","key":"BPR_HST_RISK_ISS0000000200100000000000' || v_date_m || v_time || v_stan || '","lnk_id":"' || v_transaction_link_id || ',"dt":{"us":"365988","s":"1657350136"},"rte":4,"src":"ADT_HST/' || v_host_name || '/1","ttl":2,"type":6}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"orig_ctry_cd":"826","fw_id":"123456","dnr":{"de62":{},"de106":{}},"to_idx":"00","fin":{"amt":"' || v_amt || '","ccy":"' || v_currency || '"},"merc":{"id":"' || v_merchant_number || '","ctry":"' || v_country || '","postal_cd":"          ","name":"' || v_merchant_all || '","region":"   ","mcc":"' || v_mcc || '"},"func_cd":"100","bw":{},"emv":{"tg9f35":"22","tg9f37":"00000001","tg9f33":"E0F8C8","tg9f27":"80","tg9f34":"410302","tg5f2a":"0978","tg9f1e":"07B1C1D4","tg82":"5800","tg95":"4080000000","tg84":"A0000000031010","tg9f09":"0002","tg9f41":"00000175","tg9a":"220709","tg9f10":"06010A03043000","tg9f26":"07B1C1D4F53812BA","tg9f36":"0001","tg9f1a":"0826","tg9c":"00","tg9f02":"000000000100","tg9f03":"000000000000"},"type":"00","orig":{},"bat":{},"fr_idx":"00","mti":"1100","stan":"' || v_stan2 || '","trn_dt":"' || v_system_date || v_time || '","rrn":"' || v_external_reference || '","pos":{"id":"33016405","crd":{"present":"1","data_input_md":"5","capture_cap":"1","data_input_cap":"5","data_output_cap":"3"},"crdhldr":{"auth_entity":"1","present":"0","auth_method":"0","auth_cap":"0"},"term":{"output_cap":"0"},"pin":{"capture_cap":"0"},"operating_env":"1"},"net":{},"tx_dt":"' || v_date_m || v_time || '","crd":{"pan":"****************","exp":"' || v_exp || '","seq_no":"000","trk2":"********************************"},"ecom":{},"aq_id":"123456"},"hdr":{"ttl":0,"sts":0,"src":"BPR_DNR_GA_ACQ/' || v_host_name || '/1","rte":0,"id":"62c927f9-4fe9f90ad9-72f9b8","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1657350137","us":"89981"}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"orig_ctry_cd":"826","fw_id":"123456","net_ref_id":"000000000012097","stan":"' || v_stan2 || '","de44":{"sf1":"  ","cvv_ind":"006","error_data":"00000000000","crypto_rslt_cd":"01","cavv_rslt_cd":"06"},"merc":{"id":"' || v_merchant_number || '","ctry":"' || v_country || '","postal_cd":"          ","name":"' || v_merchant_all || '","region":"   ","mcc":"' || v_mcc || '"},"func_cd":"100","fr_idx":"00","auth_cd":"' || v_auth_code || '","emv":{"tg91":"40DDA619E70796F23030"},"type":"00","rc":"081","mti":"1110","crd":{"exp":"' || v_exp || '","trk2":"********************************","pan":"****************"},"trn_dt":"220709150217","rrn":"' || v_external_reference || '","pos":{"id":"' || v_terminal_id || '","pin":{"capture_cap":"0"},"crdhldr":{"auth_entity":"1","present":"0","auth_method":"0","auth_cap":"0"},"term":{"output_cap":"0"},"crd":{"present":"1","data_input_md":"5","capture_cap":"1","data_input_cap":"5","data_output_cap":"3"},"operating_env":"1"},"tx_dt":"' || v_date_m || v_time || '","aq_id":"123456","to_idx":"00","rx_id":"12345","fin":{"amt_bl":"' || v_amt || '","ccy_bl":"' || v_currency || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"}},"hdr":{"ttl":2,"sts":0,"src":"ADT_DNR_GA/' || v_host_name || '/1","rte":5,"id":"62c927f9-94786f9efe-4a20df","dst":"BPR_DNR_GA_ACQ/' || v_host_name || '/1","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1657350137","us":"333990"}}}'),v_transaction_link_id);
			       	INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			       	VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"sts":0,"id":"62c927f9-a14751d265-833a0e","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"us":"513984","s":"1657350137"},"rte":0,"ttl":0,"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1"},"msg":{"net":{},"fin":{"trace_id":"' || v_trace_id || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","pymt":{"acct":{}},"to_idx":"00","pos":{"id":"' || v_terminal_id || '"},"aq_id":"' || v_institution_number || '","bw":{"inst":"' || v_institution_number || '"},"crd":{"avs":{},"pan":"****************","cvc2_rc":"M"},"tpdu_header":"00000000303030310100","bat":{},"ecom":{},"fr_idx":"00","merc":{"rc":"06"},"type":"00","mti":"0210","orig":{},"time":"' || v_time || '","stan":"' || v_stan || '","date":"' || v_date_m || '","rrn":"' || v_retrieval_reference || '","rc":"00","emv":{"tg91":"40DDA619E70796F23030"},"auth_cd":"' || v_auth_code || '"}}'),v_transaction_link_id);

					DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);

				elsif (v_service = 'pos' and v_card_organization = '006') then
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type1,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,null,null,null,null,null,null,null,v_currency,v_amount,null,null,null,null,null,'051','00','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000150/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_external_reference,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_DNR_GA_ACQ',v_institution_number,'1100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'00000018968','00000200430','999',null,null,'36961800000003',v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,null,null,null,null,null,null,null,v_currency,v_amount,null,null,null,null,null,null,'501101501300','00DE40:N/DE52:N/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,null,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,'BPR_DNR_GA_ACQ',v_institution_number,'1110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'00000018968','00000200430','999',null,null,'36961800000003',v_exp,v_process_code,v_mcc,v_card_number || '=' || v_exp || '221',v_terminal_id,'081',null,v_auth_code,v_currency,v_amount,null,null,v_currency,v_amount,null,null,null,null,null,null,'501101501300','00DE40:N/DE52:N/DE123:' || v_trace_id || '/DE44.4:00000000000/CAVV_RESULT:06/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_trace_id,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);
					Insert into COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
					values (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,v_system_time,V_BPR_TYPE_STR,v_institution_number,v_message_type4,to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,v_stan,'000' || v_institution_number,'00000000000','999',NULL,NULL,v_card_number,null,v_process_code,null,null,v_terminal_id,'00',null,v_auth_code,null,null,null,null,v_currency,v_amount,null,null,null,null,null,'051',null,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:183/CVC2_RC:M/MERC_RC:06/',null,null,null,v_merchant_number,v_merchant_all,null,null,'0',null,v_external_reference,v_trn_id,v_transaction_link_id,'RS2\' || v_host_name);

					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"fin":{"amt":"' || v_amt || '","fx_dt":"' || v_date_m || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","tpdu_header":"00000000303030310100","hst":{"external_ref":"' || v_external_reference || '"},"to_idx":"00","pos":{"cond":"00","id":"33016405","dat":"50000150","pin_cap":"1","pan_in":"05"},"aq_id":"' || v_institution_number || '","crd":{"trk2":"********************************","exp":"2212","pan":"****************"},"ecom":{"sec":"210"},"fr_idx":"00","merc":{"mcc":"' || v_mcc || '","id":"' || v_merchant_number || '","ctry":"GB","city":"' || v_merchant_city || '","name":"' || v_merchant_name || '"},"type":"00","mti":"0200","time":"' || v_system_time || '","date":"' || v_date_m || '","stan":"' || v_stan || '","emv":{"tg9f36":"0001","tg95":"4080000000","tg9f09":"0002","tg9f35":"22","tg9f1e":"07B1C1D4","tg9f1a":"0826","tg9c":"00","tg9f33":"E0F8C8","tg5f34":"00","tg9f10":"06010A03043000","tg9f37":"00000001","tg9f26":"07B1C1D4F53812BA","tg9f34":"410302","tg84":"A0000000031010","tg9a":"220709","tg9f02":"000000000100","tg9f53":"R","tg9f03":"000000000000","tg82":"5800","tg5f2a":"0978","tg9f27":"80","tg9f41":"00000175"},"rrn":"' || v_retrieval_reference || '"},"hdr":{"sts":0,"id":"62c927f8-b952749571-69d95b","dst":"BPR_HST_RISK_ISS/' || v_host_name || '/1","key":"BPR_HST_RISK_ISS0000000200100000000000' || v_date_m || v_time || v_stan || '","lnk_id":"' || v_transaction_link_id || ',"dt":{"us":"365988","s":"1657350136"},"rte":4,"src":"ADT_HST/' || v_host_name || '/1","ttl":2,"type":6}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"orig_ctry_cd":"826","fw_id":"123456","dnr":{"de62":{},"de106":{}},"to_idx":"00","fin":{"amt":"' || v_amt || '","ccy":"' || v_currency || '"},"merc":{"id":"' || v_merchant_number || '","ctry":"' || v_country || '","postal_cd":"          ","name":"' || v_merchant_all || '","region":"   ","mcc":"' || v_mcc || '"},"func_cd":"100","bw":{},"emv":{"tg9f35":"22","tg9f37":"00000001","tg9f33":"E0F8C8","tg9f27":"80","tg9f34":"410302","tg5f2a":"0978","tg9f1e":"07B1C1D4","tg82":"5800","tg95":"4080000000","tg84":"A0000000031010","tg9f09":"0002","tg9f41":"00000175","tg9a":"220709","tg9f10":"06010A03043000","tg9f26":"07B1C1D4F53812BA","tg9f36":"0001","tg9f1a":"0826","tg9c":"00","tg9f02":"000000000100","tg9f03":"000000000000"},"type":"00","orig":{},"bat":{},"fr_idx":"00","mti":"1100","stan":"' || v_stan2 || '","trn_dt":"' || v_system_date || v_time || '","rrn":"' || v_external_reference || '","pos":{"id":"33016405","crd":{"present":"1","data_input_md":"5","capture_cap":"1","data_input_cap":"5","data_output_cap":"3"},"crdhldr":{"auth_entity":"1","present":"0","auth_method":"0","auth_cap":"0"},"term":{"output_cap":"0"},"pin":{"capture_cap":"0"},"operating_env":"1"},"net":{},"tx_dt":"' || v_date_m || v_time || '","crd":{"pan":"****************","exp":"' || v_exp || '","seq_no":"000","trk2":"********************************"},"ecom":{},"aq_id":"123456"},"hdr":{"ttl":0,"sts":0,"src":"BPR_DNR_GA_ACQ/' || v_host_name || '/1","rte":0,"id":"62c927f9-4fe9f90ad9-72f9b8","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1657350137","us":"89981"}}}'),v_transaction_link_id);
					INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
					VALUES (V_BPR_LOG_ID + 3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"orig_ctry_cd":"826","fw_id":"123456","net_ref_id":"000000000012097","stan":"' || v_stan2 || '","de44":{"sf1":"  ","cvv_ind":"006","error_data":"00000000000","crypto_rslt_cd":"01","cavv_rslt_cd":"06"},"merc":{"id":"' || v_merchant_number || '","ctry":"' || v_country || '","postal_cd":"          ","name":"' || v_merchant_all || '","region":"   ","mcc":"' || v_mcc || '"},"func_cd":"100","fr_idx":"00","auth_cd":"' || v_auth_code || '","emv":{"tg91":"40DDA619E70796F23030"},"type":"00","rc":"081","mti":"1110","crd":{"exp":"' || v_exp || '","trk2":"********************************","pan":"****************"},"trn_dt":"' || v_system_date || v_time || '","rrn":"' || v_external_reference || '","pos":{"id":"' || v_terminal_id || '","pin":{"capture_cap":"0"},"crdhldr":{"auth_entity":"1","present":"0","auth_method":"0","auth_cap":"0"},"term":{"output_cap":"0"},"crd":{"present":"1","data_input_md":"5","capture_cap":"1","data_input_cap":"5","data_output_cap":"3"},"operating_env":"1"},"tx_dt":"' || v_date_m || v_time || '","aq_id":"123456","to_idx":"00","rx_id":"12345","fin":{"amt_bl":"' || v_amt || '","ccy_bl":"' || v_currency || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"}},"hdr":{"ttl":2,"sts":0,"src":"ADT_DNR_GA/' || v_host_name || '/1","rte":5,"id":"62c927f9-94786f9efe-4a20df","dst":"BPR_DNR_GA_ACQ/' || v_host_name || '/1","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"s":"1657350137","us":"333990"}}}'),v_transaction_link_id);
			       	INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			       	VALUES (V_BPR_LOG_ID + 4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"sts":0,"id":"62c927f9-a14751d265-833a0e","type":6,"lnk_id":"' || v_transaction_link_id || '","dt":{"us":"513984","s":"1657350137"},"rte":0,"ttl":0,"src":"BPR_HST_RISK_ISS/' || v_host_name || '/1"},"msg":{"net":{},"fin":{"trace_id":"' || v_trace_id || '","amt":"' || v_amt || '","ccy":"' || v_currency || '"},"tx_dt":"' || v_date_m || v_time || '","pymt":{"acct":{}},"to_idx":"00","pos":{"id":"' || v_terminal_id || '"},"aq_id":"' || v_institution_number || '","bw":{"inst":"' || v_institution_number || '"},"crd":{"avs":{},"pan":"****************","cvc2_rc":"M"},"tpdu_header":"00000000303030310100","bat":{},"ecom":{},"fr_idx":"00","merc":{"rc":"06"},"type":"00","mti":"0210","orig":{},"time":"' || v_time || '","stan":"' || v_stan || '","date":"' || v_date_m || '","rrn":"' || v_retrieval_reference || '","rc":"00","emv":{"tg91":"40DDA619E70796F23030"},"auth_cd":"' || v_auth_code || '"}}'),v_transaction_link_id);

		           	DBMS_OUTPUT.put_line('FINISHED SVC: ' || v_service || ' Org: ' || v_card_organization);
				end if;
			end if;
		END LOOP; -- Trans Type
		END LOOP; -- Currency
		END LOOP; -- SERVICE
		END LOOP; -- Card number
  		END LOOP; -- Client number

		COMMIT;

		-- run soa process to board merchant ?
		if (v_run_soa_process_id = 'Y' or v_run_soa_process_id = '1' or v_run_soa_process_id = 'y' or v_run_soa_process_id = '455') then
			SELECT BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(v_institution_number, '455', v_record_date) INTO v_params FROM DUAL;
			BW_process_control.run_process(v_institution_number, '455', v_params, v_user_id, v_station_number, v_host_name, 'v1','', P_OUT, '001');
			DBMS_OUTPUT.put_line('FINISHED SOA PROCESS - 455');
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
select * from COS_BPR_DATA where institution_number = '00002001' and transaction_status in ('999') order by BPR_LOG_ID desc ;
