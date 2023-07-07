DECLARE V_INIT_RETURN PLS_INTEGER;

	CURRDATE COS_BPR_DATA.BW3_RECORD_DATE%TYPE := TO_CHAR (SYSDATE-1, 'YYYYMMDD');
	CURRDATETrans COS_BPR_DATA.TRANSACTION_DATE%TYPE :=  TO_DATE('2019-12-03 11:10:09', 'YYYY-MM-DD HH24:MI:SS') ;--SYSDATE-1;
	CURRTIME COS_BPR_DATA.BW3_RECORD_TIME%TYPE := TO_CHAR (SYSDATE, 'HH24:MI:SS');

	CLIENT_REC CIS_CLIENT_DETAILS%ROWTYPE;	
	TERMINAL_REC CIS_DEVICE_LINK%ROWTYPE;

	TID COS_BPR_DATA.TRANSACTION_ID%TYPE := 1;

	PROCEDURE LOG(
		P_TID 					IN COS_BPR_DATA.TRANSACTION_ID%TYPE,
		P_CLIENT_REC 			IN CIS_CLIENT_DETAILS%ROWTYPE,
		P_TERMINAL_REC 			IN CIS_DEVICE_LINK%ROWTYPE
	 )
	IS
	BEGIN
		
		--@@@@@@@@@@@@@ THESE ARE VISA AUTHS @@@@@@@@@--
		
		--DELETE FROM COS_BPR_DATA WHERE INSTITUTION_NUMBER = '00000111' AND TERMINAL_ID='E8800031' AND CARD_NUMBER='5333170000000008'; --4104030000000012
	
		INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
			VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_HST_ISS', P_TID, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, '0200', CURRDATETrans, NULL, NULL, LPAD(P_TID, 6, '0'), '00000000000', '00000000000', '999', '0001', NULL, '4104030000000012', '1712', '000000', NULL, NULL, P_TERMINAL_REC.TERMINAL_ID, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, '012', '00', '00MERCHANT_DATA:additional merchant data/CARDHOLDER_DATA:additional cardholder data/DE52:N/DE60.1:Y/', NULL, NULL, NULL, LPAD( P_TERMINAL_REC.MERCHANT_ID, 15, '0' ), NULL, '11', NULL, NULL, NULL, '00002689');

		--INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
		--VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_HST_ISS', P_TID, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, 'A0100', CURRDATETrans, NULL, CURRDATE ||LPAD(P_TID, 4, '0'), LPAD(P_TID, 6, '0'), '00000000000', '00000000000', '999', '0001', NULL, '4104030000000012', '1712', '000000', P_CLIENT_REC.BUSINESS_CLASS, NULL, P_TERMINAL_REC.TERMINAL_ID, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, '012', '59', '00ECOM:210/DE52:N/DE60.1:Y/', NULL, NULL, NULL, LPAD( P_TERMINAL_REC.MERCHANT_ID, 15, '0' ), 'DUTCH WITHOUT TAX        AMSTERDAM    NL', '11', NULL, NULL, NULL, NULL);

		--INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
		--VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_HST_ISS', P_TID, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, 'A0110', CURRDATETrans, NULL, CURRDATE ||LPAD(P_TID, 4, '0'), LPAD(P_TID, 6, '0'), '00000000000', '00000000000', '999', '0001', NULL, '4104030000000012', '1712', '000000', P_CLIENT_REC.BUSINESS_CLASS, NULL, P_TERMINAL_REC.TERMINAL_ID, '000', NULL, 'S93019', NULL, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, '012', '59', '00ECOM:210/DE52:N/DE60.1:N/', NULL, NULL, NULL, LPAD( P_TERMINAL_REC.MERCHANT_ID, 15, '0' ), NULL, '11', NULL, NULL, NULL, NULL);

		INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
		VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_HST_ISS', P_TID, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, '0210', CURRDATETrans, NULL, CURRDATE ||LPAD(P_TID, 4, '0'), LPAD(P_TID, 6, '0'), '00000000000', '00000000000', '999', '0001', NULL, '4104030000000012', '1712', '000000', NULL, NULL, P_TERMINAL_REC.TERMINAL_ID, '00', NULL, 'S93019', NULL, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, '012', '00', '00ECOM:210/DE52:N/DE60.1:N/', NULL, NULL, NULL, LPAD( P_TERMINAL_REC.MERCHANT_ID, 15, '0' ), NULL, '11', NULL, NULL, NULL, '00002689');

		INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
		VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_VISA_ACQ', P_TID + 2, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, '0100', NULL, CURRDATETrans, CURRDATE ||LPAD(P_TID, 4, '0'), LPAD(P_TID + 2, 6, '0'), '00000483844', '00000000000', '999', NULL, NULL, '4104030000000012', '1712', '000000', P_CLIENT_REC.BUSINESS_CLASS, NULL, P_TERMINAL_REC.TERMINAL_ID, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, '012', '0200000007', '00DE25:59/DE60.8:07/DE52:N/DE126.10:Y/', NULL, NULL, NULL, LPAD( P_TERMINAL_REC.MERCHANT_ID, 15, '0' ), 'DUTCH WITHOUT TAX      AMSTERDAM     NLD', NULL, NULL, NULL, NULL, NULL);

		INSERT INTO COS_BPR_DATA (BPR_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, BPR_TYPE_NAME, TRANSACTION_ID, TRANSACTION_LINK_ID, INSTITUTION_NUMBER, MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE, RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID, TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA, REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, LOCKING_COUNTER, CASHBACK_AMOUNT, EXTERNAL_REF)
		VALUES(COS_LOG_ID_SEQ.NEXTVAL, SYSDATE, CURRDATE, CURRTIME, 'BPR_VISA_ACQ', P_TID + 2, P_TID + 1, P_CLIENT_REC.INSTITUTION_NUMBER, '0110', NULL, CURRDATETrans, CURRDATE ||LPAD(P_TID, 4, '0'), LPAD(P_TID + 2, 6, '0'), '00000483844', '00000000000', '999', NULL, NULL, '4104030000000012', NULL, '000000', NULL, NULL, P_TERMINAL_REC.TERMINAL_ID, '00', NULL, 'S93019', NULL, NULL, NULL, NULL, '978', '2500.00', NULL, NULL, NULL, NULL, NULL, NULL, '0200000000', '00DE25:59/DE60.8:00/DE62.2:000000000293018/DE52:N/DE44.2: /DE44.5:2/DE44.10:M/DE126.10:Y/', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	END;

BEGIN

	-- 'INSTITUTION NUMBER','STATION GROUP','USER NUMBER'

	BW_PRC_RES.INITGLOBALVARS ('00000111','129','999999', V_INIT_RETURN);

	SELECT * INTO CLIENT_REC FROM CIS_CLIENT_DETAILS WHERE  INSTITUTION_NUMBER = '00000111' AND client_number='88000310';
	SELECT * INTO TERMINAL_REC FROM CIS_DEVICE_LINK WHERE  CLIENT_NUMBER = CLIENT_REC.CLIENT_NUMBER AND INSTITUTION_NUMBER = CLIENT_REC.INSTITUTION_NUMBER AND rownum=1;

	LOG( 1, CLIENT_REC, TERMINAL_REC );

	COMMIT;
END;
/