----VISA POS Glenn 21

Declare

v_institution_number varchar(8) := '00002001';
v_client_number_all Varchar2(120) := '00000022';
v_amount varchar(8) := '300';
v_client_number varchar(8);
v_date Varchar2(8) := '20230227';
v_amt varchar(15);
v_auth_code varchar(6) := 'D12345';
v_retrieval_reference varchar(12) := '287155107004';
v_external_reference varchar(22) := '1234567890123456789004';
v_transaction_link_id varchar(26) := '6349694b-0d82559bb8-78abcd';
V_BPR_LOG_ID NUMBER;
v_i NUMBER;

v_station_number varchar(3) := '129';

v_posting_date varchar(8);
v_terminal_id varchar(8);

v_errors VARCHAR2(120);

Begin
	FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
		v_client_number := curClientNumber.str;
		v_errors := '';
		v_terminal_id := '';

		BEGIN
			SELECT POSTING_DATE INTO v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number; -- posting date
			if (v_date is null or v_date = '') then
				 v_date := v_posting_date - 1;
			end if;

			if (v_terminal_id is null or v_terminal_id = '') then
				SELECT terminal_id into v_terminal_id FROM CIS_DEVICE_LINK WHERE rownum = 1 and INSTITUTION_NUMBER = v_institution_number AND CLIENT_NUMBER = v_client_number;
			end if;
		EXCEPTION WHEN NO_DATA_FOUND THEN
			v_errors := 'Terminal not found in CIS_DEVICE_LINK: ' || v_client_number;
			DBMS_OUTPUT.put_line(v_errors);
		END;

		if (v_amount is null or v_amount = '') then
			v_amount := '50.00';
		end if;

		if (instr(v_amount, '.') > 0) then
		    v_amt := lpad(replace(v_amount, '.', ''), 12, '0');
		else
			v_amt := lpad(v_amount, 10, '0') || '00';
		end if;

		SELECT TO_NUMBER(TO_CHAR(SYSDATE, 'YYMMDDhh24miss') || TRIM(TO_CHAR(v_i, '000'))) INTO V_BPR_LOG_ID FROM DUAL;

		IF (v_errors is null or v_errors = '') THEN
			 DBMS_OUTPUT.put_line('Insert data: terminal_id:' || v_terminal_id || ', amount: ' || v_amount || ', ' || v_amt || ', v_date: ' || v_date);

		--Re-set Auth or Modify
--			UPDATE cos_bpr_data
--			SET Transaction_status = '999',
--			BATCH_ID = '',
--			NETWORK_ID ='',
--			terminal_id = v_terminal_id, --Modify depending with merchant you want to use
--			merchant_id = '0000000' || v_client_number, --Modify depending with merchant you want to use
--			card_number = '4999224500000014'
--			where institution_number = v_institution_number
--			and transaction_link_id in ('6349694b-0d82559bb8-10aaaa');
--			commit;
--
--			Update cis_device_link
--			Set DATE_SETTLEMENT = ''
--			where institution_number = v_institution_number
--			and client_number = v_client_number; --Modify depending with merchant you want to use
--			COMMIT;
--
--			delete from cos_bpr_Data
--			where institution_number = v_institution_number
--			and transaction_link_id in ('6349694b-0d82559bb8-10aaaa');
--			commit;
--
--			delete from cos_bpr_log
--			where transaction_link_id in ('6349694b-0d82559bb8-10aaaa');
--			commit;

			INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
			VALUES (V_BPR_LOG_ID +1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,'15:51:09','BPR_HST_ISS',v_institution_number,'0200',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,'093687','00000000999','00000000000','999',NULL,NULL,'4999224500000014','2212','000000','5999','4999224500000014=2212221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'978',v_amount,NULL,NULL,to_date(v_date , 'YYYYMMDD'),NULL,NULL,'901','00','00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/ECOM:210/DE60:50000800/',NULL,NULL,NULL,'0000000' || v_client_number,'BUNIT MERCHANT           BUNIT CITY   GB',NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_transaction_link_id,v_transaction_link_id,'RS2\NAFIDH-A557');
			INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
			VALUES (V_BPR_LOG_ID +2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,'15:51:10','BPR_VISA_ACQ',v_institution_number,'0100',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),'228713950502','950502','00000123456','00000000000','999',NULL,NULL,'4999224500000014','2212','000000','5999','4999224500000014=2212221',v_terminal_id,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'978',v_amount,NULL,NULL,NULL,NULL,NULL,'901','050000000000','00DE25:00/DE60.8:00/DE52:N/DE126.10:N/BIN_CNTRY:MT/',NULL,NULL,NULL,'0000000' || v_client_number,'BUNIT MERCHANT           BUNIT CITY   GB',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'6349694e-bc7eeed614-82ebc9',v_transaction_link_id,'RS2\NAFIDH-A557');
			INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
			VALUES (V_BPR_LOG_ID +3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,'15:51:10','BPR_VISA_ACQ',v_institution_number,'0110',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),'228713950502','950502','00000123456','00000000000','999',NULL,NULL,'4999224500000014',NULL,'000000',NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,'978',v_amount,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'00DE25:00/DE62.2:000000000093738/DE52:N/DE44.2: /DE44.5:2/DE44.10:P/DE126.10:N/BIN_CNTRY:MT/DE44.1:V/DE44.3: /',NULL,NULL,NULL,'0000000' || v_client_number,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'6349694e-bc7eeed614-82ebc9',v_transaction_link_id,'RS2\NAFIDH-A557');
			INSERT INTO COS_BPR_DATA (BPR_LOG_ID,RECORD_DATE,BW3_RECORD_DATE,BW3_RECORD_TIME,BPR_TYPE_NAME,INSTITUTION_NUMBER,MESSAGE_TYPE,TRANSACTION_DATE,TRANSMISSION_DATE,RETRIEVAL_REFERENCE,STAN,ACQUIRING_INST_ID,FORWARDING_INST_ID,TRANSACTION_STATUS,NETWORK_ID,BATCH_ID,CARD_NUMBER,EXPIRY_DATE,PROCESS_CODE,MCC_CODE,TRACK_2_DATA,TERMINAL_ID,RESPONSE_CODE,RESPONSE_DATA,AUTH_CODE,BILLING_CURRENCY,BILLING_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_AMOUNT,REQUESTED_CURRENCY,REQUESTED_AMOUNT,SETTLEMENT_RATE,SETTLEMENT_DATE,SETTLEMENT_RATE_DATE,SETTLEMENT_FEE,PROCESSING_FEE,POS_ENTRY,POS_DATA,INF_DATA,ORIGINAL_DATA,REPLACEMENT_AMOUNT,REASON_CODE,MERCHANT_ID,MERCHANT_NAME,ECOM_SECURITY_LEVEL,CVC_INVALID,LOCKING_COUNTER,CASHBACK_AMOUNT,EXTERNAL_REF,SYNC_ID,RESPONSE_CODE_INTERNAL,TRANSACTION_ID_OLD,TRANSACTION_LINK_ID_OLD,TRANSACTION_ID,TRANSACTION_LINK_ID,HOSTNAME)
			VALUES (V_BPR_LOG_ID +4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),v_date,'15:51:10','BPR_HST_ISS',v_institution_number,'0210',to_date(v_date , 'YYYYMMDD'),to_date(v_date , 'YYYYMMDD'),v_retrieval_reference,'093687','00000000999','00000000000','999',NULL,NULL,'4999224500000014',NULL,'000000',NULL,NULL,v_terminal_id,'00',NULL,v_auth_code,NULL,NULL,NULL,NULL,'978',v_amount,NULL,NULL,NULL,NULL,NULL,'901',NULL,'00DE52:N/DE48.43:N/AVS_DATA:N/DE63.PY:N/DE63.AR:N/DE63.LG:N/DE63.CA:N/DE63.AD:N/DE63.IT:N/DE63.IA:N/DE63.GS:N/BW_DST:009/CVC2_RC:P/MERC_RC:06/',NULL,NULL,NULL,'0000000' || v_client_number,'BUNIT MERCHANT           BUNIT CITY   GB',NULL,NULL,NULL,NULL,v_external_reference,NULL,NULL,NULL,NULL,v_transaction_link_id,v_transaction_link_id,'RS2\NAFIDH-A557');


			INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			VALUES (V_BPR_LOG_ID +1,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"msg":{"mti":"0200","rrn":"' || v_retrieval_reference || '","ecom":{"sec":"210"},"aq_id":"' || v_institution_number || '","fin":{"fx_dt":"1014","amt":"' || v_amt || '","ccy":"978"},"tx_dt":"1014155107","pos":{"pin_cap":"1","pan_in":"90","id":"' || v_terminal_id || '","cond":"00","dat":"50000800"},"date":"1014","merc":{"mcc":"5999","name":"BUNIT MERCHANT           ","id":"0000000' || v_client_number || '","ctry":"GB","city":"BUNIT CITY   "},"type":"00","tpdu_header":"00000000303030310100","crd":{"pan":"****************","trk2":"********************************","exp":"2212"},"hst":{"external_ref":"' || v_external_reference || '"},"time":"155107","fr_idx":"00","stan":"093687","to_idx":"00"},"hdr":{"lnk_id":"' || v_transaction_link_id || '","type":6,"sts":0,"ttl":2,"dt":{"us":"955345","s":"1665755467"},"src":"ADT_HST/NafidH-A557/1","id":"6349694b-2ca953705f-10edfb","key":"BPR_HST_RISK_ISS00000000999000000000001014155107093687","dst":"BPR_HST_RISK_ISS/NafidH-A557/1","rte":4}}'),v_transaction_link_id);
			INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			VALUES (V_BPR_LOG_ID +2,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"BPR_VISA_ACQ/NafidH-A557/1","dst":"ADT_VISA","agt":"","type":6,"ttl":2,"rte":4,"sts":4,"size":478,"flds":249,"max_time":10,"id":"6349694e-3fd86ce139-82ebd1","dt":{"s":1665755470,"us":284104},"tx_dt":{"s":0,"us":0},"trn_id":"6349694e-bc7eeed614-82ebc9","lnk_id":"' || v_transaction_link_id || '","flgs":2,"usr_flgs":0,"tmo":0},"msg":{"mti":"0100","sig":"GEN00STD00VIP00","crd":{"pan":"                ","exp":"2212","trk2":"                                "},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","ccy":"978"},"tx_dt":"1014135110","stan":"950502","time":"155107","date":"1014","merc":{"mcc":"5999","id":"0000000' || v_client_number || '","name":"BUNIT MERCHANT           ","city":"BUNIT CITY   ","ctry":"GB"},"aq_ctry":"470","pos":{"pan_in":"90","pin_cap":"1","cond":"00","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"228713950502","vip":{"de60":{"sf1":"0","sf2":"5","sf3_4_5":"0000","sf6_7":"00","sf8":"00","sf9":"0","sf10":"0"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"0000","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"},"cvc2_dat":"      "}}}'),v_transaction_link_id);
			INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			VALUES (V_BPR_LOG_ID +3,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"src":"ADT_VISA/NafidH-A557/1","dst":"BPR_VISA_ACQ/NafidH-A557/1","agt":"","type":6,"ttl":2,"rte":5,"sts":4,"size":409,"flds":247,"max_time":10,"id":"6349694e-03cbb7b3b5-a254c6","dt":{"s":1665755470,"us":540261},"tx_dt":{"s":0,"us":0},"trn_id":"6349694e-bc7eeed614-82ebc9","lnk_id":"' || v_transaction_link_id || '","flgs":0,"usr_flgs":0,"tmo":0},"msg":{"mti":"0110","sig":"GEN00STD00VIP00","crd":{"pan":"                ","avs":{"rc":" "},"cvc_rc":"2","cvc2_rc":"P"},"type":"00","fr_idx":"00","to_idx":"00","fin":{"amt":"' || v_amt || '","st_dt":"1014","ccy":"978"},"tx_dt":"1014135110","stan":"950502","aq_ctry":"470","pos":{"cond":"00","id":"' || v_terminal_id || '"},"aq_id":"123456","rrn":"228713950502","auth_cd":"' || v_auth_code || '","rc":"00","merc":{"id":"0000000' || v_client_number || '"},"vip":{"de44":{"sf1":"V","sf3":" ","sf4":" ","sf6":"  ","sf7":" ","sf8":" ","sf9":" ","sf11":"  ","sf12":" "},"de62":{"sf2":"000000000093738"},"de63":{"sf01":"0000"},"hdr":{"fld1":"01","fld2":"02","fld3":"0096","fld4":"000000","fld5":"415220","fld6":"00","fld7":"0000","fld8":"000000","fld9":"00","fld10":"000000","fld11":"00"}}}}'),v_transaction_link_id);
			INSERT INTO COS_BPR_LOG (BPR_LOG_ID,RECORD_DATE,MESSAGE_DATA,LOCKING_COUNTER,MSG_DATA_JSON,TRANSACTION_LINK_ID)
			VALUES (V_BPR_LOG_ID +4,to_timestamp(v_date || '00:00:00','YYYYMMDD HH24.MI.SS'),EMPTY_BLOB(),'0',utl_raw.cast_to_raw('{"hdr":{"lnk_id":"' || v_transaction_link_id || '","rte":0,"type":6,"sts":0,"ttl":0,"dt":{"us":"682106","s":"1665755470"},"src":"BPR_HST_RISK_ISS/NafidH-A557/1","id":"6349694e-db9f303bab-902efc"},"msg":{"mti":"0210","orig":{},"rrn":"' || v_retrieval_reference || '","ecom":{},"net":{},"fr_idx":"00","tx_dt":"1014155107","pos":{"id":"' || v_terminal_id || '"},"auth_cd":"' || v_auth_code || '","date":"1014","stan":"093687","pymt":{"acct":{}},"type":"00","tpdu_header":"00000000303030310100","rc":"00","crd":{"pan":"****************","avs":{},"cvc2_rc":"P"},"time":"155107","fin":{"trace_id":"000000000093738","amt":"' || v_amt || '","ccy":"978"},"tkn":{},"aq_id":"00000999","emv":{},"merc":{"rc":"06"},"bw":{"inst":"00000999"},"bat":{},"to_idx":"00"}}'),v_transaction_link_id);

			Commit;
		END IF;
	END LOOP;
END;
/

select * from COS_BPR_DATA where institution_number = '00002001' and transaction_status in ('999') order by BPR_LOG_ID desc ;



