-- This tool is intended to copy existing Authorisations from 1 system to other systems by way of generating the necessary SQL scripts
-- version 1.1.3
-- Date 20230215
-- Author Luke Micallef Capello
--
-- Modify Variables in the upper section as indicated and execute with Golden or equivalent script by script.
-- The SQL scripts to copy the Auths will be provided in the Result Set. Copy the whole result set
-- and execute on the environment where you want to replicate the auths.

-- *** Known Issues / Limitations ***
-- 1. Milli Seconds within RECORD_DATE and START_RECORD_DATE are not supported and will be set to 0s.

-- Script 1 - Create Temp Table
Begin
	--Drop Temp Table if Exists so we always create the one with latest specs
	Begin
		Execute Immediate 'DROP TABLE TMP_AUTH_COPY_SCRIPTS';
	Exception
		When others then
			Null;
	End;
	--
	--Drop Temp Types
	Begin
		Execute Immediate 'Drop Type tmp_link_arr_list';
	Exception
		When others then
			Null;
	End;
	--
	Begin
		Execute Immediate 'Drop Type tmp_date_arr_list';
	Exception
		When others then
			Null;
	End;
	Execute Immediate 'CREATE TABLE TMP_AUTH_COPY_SCRIPTS (ID integer, SCRIPT_NUM Integer, SQL_SCRIPT Varchar2(2500))';
	Execute Immediate 'Create type tmp_link_arr_list is table of Varchar2(30)';
	Execute Immediate 'Create type tmp_date_arr_list is table of Varchar2(8)';
End;
/

-- Script 2 - Generate Export SQL Scripts
-- Some vars may require amendments.
Set Define Off;
Declare
	--------------------------------------------------------------------------------
	-- *** FILTERING VARS
	v_inst_num 	Varchar2(8) 			:= '00000111';    -- Change Institution Number as necessary
	t_Auth_date tmp_date_arr_list 		:= tmp_date_arr_list('20230214');    -- This is the BW3_RECORD_DATE. This is Mandatory and cannot be left empty. You can input multiple date filters like in an IN statement
	v_tid Varchar2(8) 					:= '';		  -- Filter Auths on this Terminal ID. Set to 00000000 if no filtering is needed.
	v_exclude_Status_998 Varchar2(1) 	:= '1';  -- If set to 1 authorisations having status 998 will be excluded. Useful for when you want to exclude certain auths.
	v_exclude_NonApprovd Varchar2(1) 	:= '0';  -- If set to 1 Non Approved Auths will not be copied. Non Approved Auths are not loaded by POS NT LOADER so there is little need for them.
	lnk_arr_list  tmp_link_arr_list 	:= tmp_link_arr_list('63eba70e-3338807c77-14bf47'); -- List of Transaction Link IDs. There must always be 1 empty value when not used. When used, enter values as if you are inputting an IN Clause.
	---------------------
	-- *** OVERRIDE VARS
	v_merchant_id Varchar2(15) 	:= '';		  -- Use this value to hardcode the Merchant ID of the auths to copy. *Does not affect filtering* LEAVE Blank if not used
	v_terminal_id Varchar2(8) 	:= '';		  -- Use this value to hardcode the Terminal ID of the Auths to copy. *Does not affect filtering* LEAVE Blank if not used
	---------------------
	--------------------------------------------------------------------------------

	-- Do not alter vars below this point
	n_counter Integer := 0;
	v_SQL Varchar2(4000);
	v_version Varchar2(10) := '1.1.3';
	--
	-- BLOB hanling vars
	i integer := 0;
	lob_size integer;
	buffer_size integer := 1000;
	buffer raw(32767);
	i_length integer := 0;
	v_dummy Varchar2(1000);
	v_skip_T_Link Varchar2(1) := 'N';
	v_time Varchar2(8);
	v_risk_time_start Varchar2(8);
	--
	Function GetRecordDate(p_bpr_log_id in varchar2) return varchar2 is
		tempTime Varchar2(15);
	Begin
		Select to_char(record_date, 'HH24:MI:SS') into tempTime From COS_BPR_DATA
		WHERE INSTITUTION_NUMBER = v_inst_num
		and BPR_LOG_ID = p_bpr_log_id
		and rownum = 1; -- Multiple records may exist in case of data manipulation.
		Return  tempTime;
	End;
Begin
	--
	-- Check whether Transaction Link Type has enough values in it to merit filtering
	Select COUNT(*) INTO v_dummy From TABLE(lnk_arr_list) WHERE TRIM(COLUMN_VALUE) Is Not Null;
	If v_dummy = 0 Then
		-- No values entered by user. Skip the Transaction Link ID filtering.
		v_skip_T_Link := 'Y';
	End if;
	--
	-- Fix Vars for potentially minor mismatch
	if length(trim(v_merchant_id)) is Null then
		v_merchant_id := Null;
	End if;
	if length(trim(v_terminal_id)) is Null then
		v_terminal_id := Null;
	End if;
	if length(trim(v_tid)) is Null then
		v_tid := Null;
	End if;
	--
	-- Start by preparing DELETE SCRIPT
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 1,  'Declare');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 2,  '  v_date Varchar2(8) := ''&1'';');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 3,  'Begin');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 4,  '  -- **************************************************');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 5,  '  -- Use this command when using pipeline for clients which are set on a fixed cutoff period. Not needed for Multi Cutoff');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 6,  '  If v_date is not null Then');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 7,  '    v_date := to_char(to_date(v_date,''YYYYMMDD'') - 1,''YYYYMMDD'');');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 8,  '    dbms_output.put_line(''Provide Date has been altered. Auths to be created using date: '' || v_date); ');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 9,  '  Else');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 10, '    dbms_output.put_line(''No Date was provided. Auths will be created according to their original date''); ');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 11, '  End If;');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (1, 12, '  -- **************************************************');
	For DEL in (
		Select * From (
			Select BPR_LOG_ID, INSTITUTION_NUMBER, TRANSACTION_LINK_ID, STAN
			 from cos_bpr_data bpr
				where institution_number  = v_inst_num
				and bw3_record_date in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
				and (Transaction_Status <> '998' or v_exclude_Status_998 = '0')
				and (Not Exists (Select 1 from COS_BPR_DATA
								WHERE TRANSACTION_LINK_ID = bpr.TRANSACTION_LINK_ID
								and INSTITUTION_NUMBER = bpr.INSTITUTION_NUMBER
								and RESPONSE_CODE Not In ('00','000') and RESPONSE_CODE is Not Null
								)
					or v_exclude_NonApprovd = '0'
				)
				and (Terminal_id = v_tid or nvl(v_tid,'00000000') = '00000000')
			and (TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
		) X
		Order by 1
	)
	Loop
		Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (2, '  DELETE FROM COS_BPR_DATA WHERE BPR_LOG_ID = ' || DEL.BPR_LOG_ID || ' and INSTITUTION_NUMBER = ''' || DEL.INSTITUTION_NUMBER || ''' and STAN = ''' || DEL.STAN || ''';');
		Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (2, '  DELETE FROM COS_BPR_LOG WHERE BPR_LOG_ID = ' || DEL.BPR_LOG_ID || ' AND TRANSACTION_LINK_ID = ''' || DEL.TRANSACTION_LINK_ID || ''';');
		Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (2, '  DELETE FROM COS_EMV_LOG WHERE BPR_LOG_ID = ' || DEL.BPR_LOG_ID || ';');
		Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (2, '  DELETE FROM COS_RISK_LOG WHERE TRANSACTION_LINK_ID = ''' || DEL.TRANSACTION_LINK_ID || ''' AND INSTITUTION_NUMBER = ''' || DEL.INSTITUTION_NUMBER ||''';');
	End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (3, '  Commit;');


    -- Prepare Inserts for COS_BPR_DATA
    For BPR in
    (
    	Select BPR_LOG_ID, RECORD_DATE, BPR_TYPE_NAME, BW3_RECORD_DATE, BW3_RECORD_TIME, INSTITUTION_NUMBER,
    			NVL(TRANSACTION_ID_OLD, 0) as TRANSACTION_ID_OLD,
    			NVL(TRANSACTION_LINK_ID_OLD, 0) as TRANSACTION_LINK_ID_OLD,
    			MESSAGE_TYPE, TRANSACTION_DATE, TRANSMISSION_DATE,
    			RETRIEVAL_REFERENCE, STAN, ACQUIRING_INST_ID, FORWARDING_INST_ID,
    			TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE,
    			MCC_CODE, TRACK_2_DATA, TERMINAL_ID, RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE,
    			BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT,
    			REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, SETTLEMENT_DATE, SETTLEMENT_RATE_DATE,
    			SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, POS_DATA, INF_DATA, ORIGINAL_DATA,
    			REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID,
    			CASHBACK_AMOUNT, EXTERNAL_REF, TRANSACTION_ID, TRANSACTION_LINK_ID, HOSTNAME,
    			RESPONSE_CODE_INTERNAL, CLEARING_STATUS, STATISTIC_STATUS
    	FROM COS_BPR_DATA bpr
    	WHERE INSTITUTION_NUMBER = v_inst_num
    	And BW3_RECORD_DATE in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
    	and (Transaction_Status <> '998' or v_exclude_Status_998 = '0')
    	and (Not Exists (Select 1 from COS_BPR_DATA
								WHERE TRANSACTION_LINK_ID = bpr.TRANSACTION_LINK_ID
								and INSTITUTION_NUMBER = bpr.INSTITUTION_NUMBER
								and RESPONSE_CODE Not In ('00','000') and RESPONSE_CODE is Not Null
								)
					or v_exclude_NonApprovd = '0')
	    and (TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
	    and (Terminal_id = v_tid or nvl(v_tid,'00000000') = '00000000')
    )
    Loop
    	-- We split the insert int 2 lines because SQL plus has a limit of 3,000 chars per line
    	-- Splitting lines will help keeping the number of characters per line lower.
    	--
    	-- Get The Time. This is a separate call to keep same format.
    	--v_time := GetRecordDate(BPR.BPR_LOG_ID);
    	v_time := to_char(BPR.RECORD_DATE, 'HH24:MI:SS');
    	--
    	n_counter := n_counter + 1;
    	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values
    		(10, n_counter, '  INSERT INTO COS_BPR_DATA (' ||
    					'BPR_LOG_ID, RECORD_DATE, ' ||
    					'BPR_TYPE_NAME, BW3_RECORD_DATE, BW3_RECORD_TIME, INSTITUTION_NUMBER, ' ||
    					'TRANSACTION_ID_OLD, TRANSACTION_LINK_ID_OLD, MESSAGE_TYPE, ' ||
    					'TRANSACTION_DATE, TRANSMISSION_DATE, ' ||
    					'RETRIEVAL_REFERENCE, STAN, ' ||
    					'ACQUIRING_INST_ID, FORWARDING_INST_ID, ' ||
    					'TRANSACTION_STATUS, NETWORK_ID, BATCH_ID, ' ||
    					'CARD_NUMBER, EXPIRY_DATE, PROCESS_CODE, ' ||
    					'MCC_CODE, TRACK_2_DATA, TERMINAL_ID, ' ||
    					'RESPONSE_CODE, RESPONSE_DATA, AUTH_CODE, ' ||
    					'BILLING_CURRENCY, BILLING_AMOUNT, SETTLEMENT_CURRENCY, SETTLEMENT_AMOUNT, ' ||
    					'REQUESTED_CURRENCY, REQUESTED_AMOUNT, SETTLEMENT_RATE, ' ||
    					'SETTLEMENT_DATE, SETTLEMENT_RATE_DATE, ' ||
    					'SETTLEMENT_FEE, PROCESSING_FEE, POS_ENTRY, ' ||
    					'POS_DATA, INF_DATA, ORIGINAL_DATA, ' ||
    					'REPLACEMENT_AMOUNT, REASON_CODE, MERCHANT_ID, ' ||
    					'MERCHANT_NAME, ECOM_SECURITY_LEVEL, CVC_INVALID, ' ||
    					'CASHBACK_AMOUNT, EXTERNAL_REF, ' ||
    					'TRANSACTION_ID, TRANSACTION_LINK_ID, HOSTNAME, ' ||
    					'RESPONSE_CODE_INTERNAL, CLEARING_STATUS, STATISTIC_STATUS) ');
    	n_counter := n_counter + 1;
    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
    		'(10, ' || n_counter || ', ''   Values(' || BPR.BPR_LOG_ID || ', to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(BPR.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || v_time||''''',''''YYYYMMDD HH24:MI:SS''''), ''''' ||
    		BPR.BPR_TYPE_NAME || ''''',NVL(TRIM(v_date), ''''' || TO_CHAR(BPR.RECORD_DATE,'YYYYMMDD') || '''''),''''' || BPR.BW3_RECORD_TIME || ''''',''''' || BPR.INSTITUTION_NUMBER || ''''', ' ||
    		BPR.TRANSACTION_ID_OLD || ', ' || BPR.TRANSACTION_LINK_ID_OLD ||', ''''' || BPR.MESSAGE_TYPE || ''''', TO_DATE(NVL(TRIM(v_date), ''''' || TO_CHAR(BPR.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || TO_CHAR(BPR.TRANSACTION_DATE, 'HH24:MI:SS') ||''''',''''YYYYMMDD HH24:MI:SS''''), ' ||
    		'TO_DATE(NVL(TRIM(v_date), ''''' || TO_CHAR(BPR.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || TO_CHAR(BPR.TRANSMISSION_DATE, 'HH24:MI:SS') ||''''',''''YYYYMMDD HH24:MI:SS''''), ' ||
    		'''''' || BPR.RETRIEVAL_REFERENCE || ''''',''''' || BPR.STAN || ''''', ' ||
    		'''''' || BPR.ACQUIRING_INST_ID || ''''',''''' || BPR.FORWARDING_INST_ID || ''''', ' ||
    		'''''999'''', Null, Null, ' ||
    		'''''' || BPR.CARD_NUMBER || ''''', ''''' || BPR.EXPIRY_DATE || ''''', ''''' || BPR.PROCESS_CODE || ''''', ' ||
    		'''''' || BPR.MCC_CODE || ''''', ''''' || BPR.TRACK_2_DATA || ''''', ''''' || NVL(v_terminal_id, BPR.TERMINAL_ID) || ''''', ' ||
    		'''''' || BPR.RESPONSE_CODE || ''''', ''''' || BPR.RESPONSE_DATA || ''''', ''''' || BPR.AUTH_CODE || ''''', ' ||
    		'''''' || BPR.BILLING_CURRENCY || ''''', ''''' || BPR.BILLING_AMOUNT || ''''', ''''' || BPR.SETTLEMENT_CURRENCY || ''''', ''''' || BPR.SETTLEMENT_AMOUNT || ''''', ' ||
    		'''''' || BPR.REQUESTED_CURRENCY || ''''', ''''' || BPR.REQUESTED_AMOUNT || ''''', ''''' || BPR.SETTLEMENT_RATE || ''''', ';
    	--
    	If BPR.SETTLEMENT_DATE is not null Then
	   		v_SQL := v_SQL || 'TO_DATE(''''' ||TO_CHAR(BPR.SETTLEMENT_DATE,'YYYYMMDD') || ''''',''''YYYYMMDD''''), ';
    	else
    		v_SQL := v_SQL || 'Null, ';
       	End if;

    	If BPR.SETTLEMENT_RATE_DATE is not null Then
    		v_SQL := v_SQL || 'TO_DATE(''''' || TO_CHAR(BPR.SETTLEMENT_RATE_DATE,'YYYYMMDD') || ''''',''''YYYYMMDD''''), ';
    	Else
    		v_SQL := v_SQL || 'Null, ';
    	End if;

		v_SQL := v_SQL || '''''' || BPR.SETTLEMENT_FEE || ''''', ''''' || BPR.PROCESSING_FEE || ''''', ''''' || BPR.POS_ENTRY || ''''',';
		v_SQL := v_SQL || '''''' || BPR.POS_DATA || ''''', ''''' || BPR.INF_DATA || ''''', ''''' || BPR.ORIGINAL_DATA || ''''',';
		v_SQL := v_SQL || '''''' || BPR.REPLACEMENT_AMOUNT || ''''', ''''' || BPR.REASON_CODE || ''''', ''''' || NVL(v_merchant_id, BPR.MERCHANT_ID) || ''''',';
		v_SQL := v_SQL || '''''' || REPLACE(BPR.MERCHANT_NAME, '''','`') || ''''', ''''' || BPR.ECOM_SECURITY_LEVEL || ''''', ''''' || BPR.CVC_INVALID || ''''',';
		v_SQL := v_SQL || '''''' || BPR.CASHBACK_AMOUNT || ''''', ''''' || BPR.EXTERNAL_REF || ''''',';
		v_SQL := v_SQL || '''''' || BPR.TRANSACTION_ID || ''''', ''''' || BPR.TRANSACTION_LINK_ID || ''''', ''''' || BPR.HOSTNAME || ''''',';
		v_SQL := v_SQL || '''''' || BPR.RESPONSE_CODE_INTERNAL || ''''', ''''' || BPR.CLEARING_STATUS || ''''', ''''' || BPR.STATISTIC_STATUS || '''''';

    	v_SQL := v_SQL || ');'')';
    	Execute Immediate v_SQL;
    End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (11, '  Commit;');
	Commit;

    -- Prepare Inserts for COS_EMV_LOG
    n_counter := 0;
	For EMV in
    (
    	SELECT LOG.*, to_char(BPR.RECORD_DATE, 'HH24:MI:SS') as COS_RECORD_TIME
    	FROM COS_EMV_LOG LOG, COS_BPR_DATA BPR
    	WHERE LOG.BPR_LOG_ID = BPR.BPR_LOG_ID
    		And LOG.RECORD_DATE = BPR.RECORD_DATE
	    	And BPR.INSTITUTION_NUMBER = v_inst_num
	    	And BPR.BW3_RECORD_DATE in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
	    	and (BPR.TRANSACTION_STATUS <> '998' or v_exclude_Status_998 = '0')
	    	and (Not Exists (Select 1 from COS_BPR_DATA BPR2
								WHERE BPR2.TRANSACTION_LINK_ID = BPR.TRANSACTION_LINK_ID
								and BPR2.INSTITUTION_NUMBER = BPR.INSTITUTION_NUMBER
								and BPR2.RECORD_DATE = BPR.RECORD_DATE
								and BPR2.RESPONSE_CODE Not In ('00','000') and BPR2.RESPONSE_CODE is Not Null
								)
					or v_exclude_NonApprovd = '0')
		    and (BPR.TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
		    and (BPR.TERMINAL_ID = v_tid or nvl(v_tid,'00000000') = '00000000')
    )
    Loop
    	n_counter := n_counter + 1;
    	--
    	--Obtain Record Time From COS_BPR_DATA as this is the time used for RECORD_DATE in that table in the above script.
    	--v_time := GetRecordDate(EMV.BPR_LOG_ID);
    	v_time := EMV.COS_RECORD_TIME;
    	--
    	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values
    		(20, n_counter, '  INSERT INTO COS_EMV_LOG ('  ||
    					'BPR_LOG_ID, RECORD_DATE, ' ||
    					'BPR_TYPE_NAME, APPLICATION_INTER_PROFILE, DEDICATED_FILE_NAME, ' ||
    					'APPLICATION_TRAN_COUNTER, CARD_VERIFICATION_RESULTS, CARD_SEQUENCE_NUMBER, '  ||
    					'TERMINAL_APPL_VER_NUMBER, TERM_VERIFICATION_RESULTS, TERMINAL_TRANSACTION_DATE, ' ||
    					'TERMINAL_CAPABILITY_PROFILE, TERMINAL_COUNTRY_CODE, TERMINAL_SERIAL_NUMBER, ' ||
    					'TERMINAL_TYPE, AUTHORIZED_AMOUNT, AUTHORIZED_CURRENCY, ' ||
    					'TRANS_CATEGORY_CODE, TRANS_SEQUENCE_NUMBER, CRYPTOGRAM_VERSION, '  ||
    					'CRYPTOGRAM, CRYPTOGRAM_TRANSACTION_TYPE, CRYPTOGRAM_CASHBACK_AMOUNT, ' ||
    					'DERIVATION_KEY_INDEX, UNPREDICTABLE_NUMBER, AUTH_RESP_CRYPTOGRAM_CODE, ' ||
    					'AUTH_RESP_CRYPTOGRAM, ISSUER_SCRIPT_1_RESULTS, ISSUER_DISCRETIONARY_DATA, ' ||
    					'SCRIPT_PRESENT, FORM_FACTOR_IND, INSTITUTION_NUMBER, ' ||
    					'TRANSACTION_LINK_ID) ');
    	n_counter := n_counter + 1;
    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
    		'(20, ' || n_counter || ', ''   Values(' || EMV.BPR_LOG_ID || ', to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(EMV.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || v_time || ''''',''''YYYYMMDD HH24:MI:SS''''),';
    	v_SQL := v_SQL || '''''' || EMV.BPR_TYPE_NAME || ''''', ''''' || EMV.APPLICATION_INTER_PROFILE || ''''', ''''' || EMV.DEDICATED_FILE_NAME || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.APPLICATION_TRAN_COUNTER || ''''', ''''' || EMV.CARD_VERIFICATION_RESULTS || ''''', ''''' || EMV.CARD_SEQUENCE_NUMBER || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.TERMINAL_APPL_VER_NUMBER || ''''', ''''' || EMV.TERM_VERIFICATION_RESULTS || ''''', ''''' || EMV.TERMINAL_TRANSACTION_DATE || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.TERMINAL_CAPABILITY_PROFILE || ''''', ''''' || EMV.TERMINAL_COUNTRY_CODE || ''''', ''''' || EMV.TERMINAL_SERIAL_NUMBER || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.TERMINAL_TYPE || ''''', ''''' || EMV.AUTHORIZED_AMOUNT || ''''', ''''' || EMV.AUTHORIZED_CURRENCY || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.TRANS_CATEGORY_CODE || ''''', ''''' || EMV.TRANS_SEQUENCE_NUMBER || ''''', ''''' || EMV.CRYPTOGRAM_VERSION || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.CRYPTOGRAM || ''''', ''''' || EMV.CRYPTOGRAM_TRANSACTION_TYPE || ''''', ''''' || EMV.CRYPTOGRAM_CASHBACK_AMOUNT || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.DERIVATION_KEY_INDEX || ''''', ''''' || EMV.UNPREDICTABLE_NUMBER || ''''', ''''' || EMV.AUTH_RESP_CRYPTOGRAM_CODE || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.AUTH_RESP_CRYPTOGRAM || ''''', ''''' || EMV.ISSUER_SCRIPT_1_RESULTS || ''''', ''''' || EMV.ISSUER_DISCRETIONARY_DATA || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.SCRIPT_PRESENT || ''''', ''''' || EMV.FORM_FACTOR_IND || ''''', ''''' || EMV.INSTITUTION_NUMBER || ''''',';
    	v_SQL := v_SQL || '''''' || EMV.TRANSACTION_LINK_ID || '''''';
    	--
    	v_SQL := v_SQL || ');'')';
    	Execute Immediate v_SQL;
    End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (21, '  Commit;');
    Commit;

    --Prepare Scripts for COS_BPR_LOG
    --This table contains columns of type BLOB and thus require specific handling.
    n_counter := 0;
	For LOG in
    (
    	SELECT LOG.*, to_char(BPR.RECORD_DATE, 'HH24:MI:SS') as COS_RECORD_TIME
    	FROM COS_BPR_LOG LOG, COS_BPR_DATA BPR
    	WHERE LOG.BPR_LOG_ID = BPR.BPR_LOG_ID
    		And LOG.RECORD_DATE = BPR.RECORD_DATE
    		and LOG.TRANSACTION_LINK_ID = BPR.TRANSACTION_LINK_ID
	    	And BPR.INSTITUTION_NUMBER = v_inst_num
	    	And BPR.BW3_RECORD_DATE in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
	    	and (BPR.TRANSACTION_STATUS <> '998' or v_exclude_Status_998 = '0')
	    	and (Not Exists (Select 1 from COS_BPR_DATA BPR2
								WHERE BPR2.TRANSACTION_LINK_ID = BPR.TRANSACTION_LINK_ID
								and BPR2.INSTITUTION_NUMBER = BPR.INSTITUTION_NUMBER
								and BPR2.RECORD_DATE = BPR.RECORD_DATE
								and BPR2.RESPONSE_CODE Not In ('00','000') and BPR2.RESPONSE_CODE is Not Null
								)
					or v_exclude_NonApprovd = '0')
		    and (BPR.TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
		    and (BPR.TERMINAL_ID = v_tid or nvl(v_tid,'00000000') = '00000000')
    )
    Loop
    	n_counter := n_counter + 1;
    	--
    	--Obtain Record Time From COS_BPR_DATA as this is the time used for RECORD_DATE in that table in the above script.
    	--v_time := GetRecordDate(LOG.BPR_LOG_ID);
    	v_time := LOG.COS_RECORD_TIME;
    	--
    	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values
    		(30, n_counter, '  INSERT INTO COS_BPR_LOG ('  ||
    					'BPR_LOG_ID, RECORD_DATE, TRANSACTION_LINK_ID) ');
    	n_counter := n_counter + 1;
    	--
    	--Prepare The Insert without the BLOB attributes
    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
    		'(30, ' || n_counter || ', ''   Values(' || LOG.BPR_LOG_ID || ', to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(LOG.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || v_time || ''''',''''YYYYMMDD HH24:MI:SS''''), ''''' || LOG.TRANSACTION_LINK_ID || '''''';
    	v_SQL := v_SQL || ');'')';
    	Execute Immediate v_SQL;
    	--
    	-- Parse the BLOB and use UPDATE scripts to migrate the BLOB in separate scripts. Keeping in mind the 3000 Character Limit of the SQL PLUS!!!
    	lob_size := dbms_lob.getlength(LOG.MSG_DATA_JSON);
    	if lob_size > 0 Then
    		n_counter := n_counter + 1;
    		--
    		v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(30, ' || n_counter || ' , ''  UPDATE COS_BPR_LOG Set MSG_DATA_JSON = ';
			v_SQL := v_SQL || 'hextoraw('''''''''')';
		    Execute Immediate v_SQL;
		    --
    		for i in 0 .. (lob_size / buffer_size) loop
			    buffer := dbms_lob.substr(LOG.MSG_DATA_JSON, buffer_size, i * buffer_size + 1);
			    If Length(To_Char(rawtohex(buffer))) > 0 Then
                    n_counter := n_counter + 1;
			    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(30, ' || n_counter || ' , ';
				    v_SQL := v_SQL || '''    || ''''' || To_Char(rawtohex(buffer))  || '''''''';
					v_SQL := v_SQL || ')';
		    		Execute Immediate v_SQL;

			    End if;
			    i_length := i_length + length(To_Char(rawtohex(buffer)));
		  	end loop;

		  	n_counter := n_counter + 1;
		  	v_SQL := '  Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(30, ' || n_counter || ' , ''';
		  	v_SQL := v_SQL || ') ';
		    v_SQL := v_SQL || 'WHERE BPR_LOG_ID = ''''' || LOG.BPR_LOG_ID || ''''' and TRANSACTION_LINK_ID = ''''' || LOG.TRANSACTION_LINK_ID || '''''';
		    v_SQL := v_SQL || ' and RECORD_DATE = to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(LOG.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || v_time || ''''',''''YYYYMMDD HH24:MI:SS'''')';
		    v_SQL := v_SQL || ';'')';
		    Execute Immediate v_SQL;
    	End if;



    End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (31, '  Commit;');
    Commit;
    --
    --
    --Prepare Scripts for COS_RISK_LOG
    --This table contains columns of type BLOB and thus require specific handling.
    --Do not link the RISK table with RECORD_DATE as it is different between RISK and BPR_DATA.
    n_counter := 0;
	For RISK in
    (
    	SELECT LOG.*
    	FROM COS_RISK_LOG LOG,
    		(SELECT distinct TRANSACTION_LINK_ID, BW3_RECORD_DATE, INSTITUTION_NUMBER
    		FROM COS_BPR_DATA BPR
	    	WHERE BPR.INSTITUTION_NUMBER = v_inst_num
		    	And BPR.BW3_RECORD_DATE in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
		    	and (BPR.TRANSACTION_STATUS <> '998' or v_exclude_Status_998 = '0')
		    	and (Not Exists (Select 1 from COS_BPR_DATA BPR2
									WHERE BPR2.TRANSACTION_LINK_ID = BPR.TRANSACTION_LINK_ID
									and BPR2.INSTITUTION_NUMBER = BPR.INSTITUTION_NUMBER
									and BPR2.RECORD_DATE = BPR.RECORD_DATE
									and BPR2.RESPONSE_CODE Not In ('00','000') and BPR2.RESPONSE_CODE is Not Null
									)
						or v_exclude_NonApprovd = '0')
			    and (BPR.TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
			    and (BPR.TERMINAL_ID = v_tid or nvl(v_tid,'00000000') = '00000000')
			    ) BPR
    	WHERE LOG.BW3_RECORD_DATE = BPR.BW3_RECORD_DATE
    		And LOG.INSTITUTION_NUMBER = BPR.INSTITUTION_NUMBER
    		And LOG.TRANSACTION_LINK_ID = BPR.TRANSACTION_LINK_ID
    )
    Loop
    	n_counter := n_counter + 1;
    	--
    	v_time := to_char(RISK.RECORD_DATE, 'HH24:MI:SS');
    	v_risk_time_start := NVL(to_char(RISK.START_RECORD_DATE, 'HH24:MI:SS'), v_time);
    	--
    	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values
    		(40, n_counter, 'INSERT INTO COS_RISK_LOG ('  ||
    		'RISK_LOG_ID, RECORD_DATE, BW3_RECORD_DATE, BW3_RECORD_TIME, ' ||
    		'BPR_TYPE_NAME, BPR_TYPE_NAME_SRC, PROCESSING_STATUS, RISK_RESPONSE, ' ||
    		'INSTITUTION_NUMBER, FORWARDING_INST_ID, TRANSACTION_LINK_ID, MESSAGE_TYPE, ' ||
    		'RETRIEVAL_REFERENCE, REQUESTED_CURRENCY, REQUESTED_AMOUNT, CARD_NUMBER, ' ||
    		'MCC_CODE, TERMINAL_ID, MERCHANT_ID, HOSTNAME, ' ||
    		'BIN_COUNTRY, START_RECORD_DATE, TRANSACTION_RESPONSE_CODE, INF_DATA) ');
    	n_counter := n_counter + 1;
    	--
    	--Prepare The Insert without the BLOB attributes
    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
    		'(40, ' || n_counter || ', '' Values(' || RISK.RISK_LOG_ID || ', to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(RISK.RECORD_DATE,'YYYYMMDD') || ''''') || '''' ' || v_time || ''''',''''YYYYMMDD HH24:MI:SS''''), ';
    	v_SQL := v_SQL || 'NVL(TRIM(v_date), ''''' || TO_CHAR(RISK.RECORD_DATE,'YYYYMMDD') || '''''), ';
		v_SQL := v_SQL || '''''' || RISK.BW3_RECORD_TIME || ''''',''''' || RISK.BPR_TYPE_NAME || ''''',''''' ||RISK.BPR_TYPE_NAME_SRC ||  ''''',';
		v_SQL := v_SQL || '''''' || RISK.PROCESSING_STATUS ||  ''''',''''' || RISK.RISK_RESPONSE ||  ''''',''''' || RISK.INSTITUTION_NUMBER ||  ''''',';
		v_SQL := v_SQL || '''''' || RISK.FORWARDING_INST_ID ||  ''''','''''|| RISK.TRANSACTION_LINK_ID ||  ''''',''''' || RISK.MESSAGE_TYPE ||  ''''',';
		v_SQL := v_SQL || '''''' || RISK.RETRIEVAL_REFERENCE ||  ''''',''''' || RISK.REQUESTED_CURRENCY ||  ''''',''''' || RISK.REQUESTED_AMOUNT ||  ''''',''''' || RISK.CARD_NUMBER ||  ''''',';
		v_SQL := v_SQL || '''''' || RISK.MCC_CODE ||  ''''',''''' || NVL(v_terminal_id, RISK.TERMINAL_ID) ||  ''''',''''' || NVL(v_merchant_id, RISK.MERCHANT_ID) ||  ''''',''''' || RISK.HOSTNAME ||  ''''',';
		v_SQL := v_SQL || '''''' || RISK.BIN_COUNTRY ||  '''''';
    	v_SQL := v_SQL || ',to_timestamp(NVL(TRIM(v_date), ''''' || TO_CHAR(NVL(RISK.START_RECORD_DATE, RISK.RECORD_DATE),'YYYYMMDD') || ''''') || '''' ' || v_risk_time_start || ''''',''''YYYYMMDD HH24:MI:SS''''),''''' || RISK.TRANSACTION_RESPONSE_CODE || ''''',';
    	v_SQL := v_SQL || '''''' || RISK.INF_DATA || ''''' );'')';
    	Execute Immediate v_SQL;
    	--
    	-- Parse the BLOB and use UPDATE scripts to migrate the BLOB in separate scripts. Keeping in mind the 3000 Character Limit of the SQL PLUS!!!
    	lob_size := dbms_lob.getlength(RISK.MSG_DATA_JSON);
    	if lob_size > 0 Then
    		n_counter := n_counter + 1;
    		--
    		v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(40, ' || n_counter || ' , ''UPDATE COS_RISK_LOG Set MSG_DATA_JSON = ';
			v_SQL := v_SQL || 'hextoraw('''''''''')';
		    Execute Immediate v_SQL;
		    --
    		for i in 0 .. (lob_size / buffer_size) loop
			    buffer := dbms_lob.substr(RISK.MSG_DATA_JSON, buffer_size, i * buffer_size + 1);
			    If Length(To_Char(rawtohex(buffer))) > 0 Then
                    n_counter := n_counter + 1;
			    	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(40, ' || n_counter || ' , ';
				    v_SQL := v_SQL || '''    || ''''' || To_Char(rawtohex(buffer))  || '''''''';
					v_SQL := v_SQL || ')';
		    		Execute Immediate v_SQL;

			    End if;
			    i_length := i_length + length(To_Char(rawtohex(buffer)));
		  	end loop;

		  	n_counter := n_counter + 1;
		  	v_SQL := 'Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(40, ' || n_counter || ' , ''';
		  	v_SQL := v_SQL || ') ';
		    v_SQL := v_SQL || 'WHERE RISK_LOG_ID = ''''' || RISK.RISK_LOG_ID || ''''' and TRANSACTION_LINK_ID = ''''' || RISK.TRANSACTION_LINK_ID || '''''';
		    v_SQL := v_SQL || ';'')';
		    Execute Immediate v_SQL;
    	End if;
    End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (41, '  Commit;');
    Commit;

    -- Reset Device Date
    n_counter := 0;
    For BPR in
    (
    	Select TERMINAL_ID, MERCHANT_ID, MIN(RECORD_DATE) as MIN_RECORD_DATE
    	FROM COS_BPR_DATA bpr
    	WHERE INSTITUTION_NUMBER = v_inst_num
    	And BW3_RECORD_DATE in (SELECT COLUMN_VALUE FROM TABLE(t_Auth_date))
    	and merchant_id is not null
    	and terminal_id is not null
    	and (Transaction_Status <> '998' or v_exclude_Status_998 = '0')
    	and (Not Exists (Select 1 from COS_BPR_DATA
								WHERE TRANSACTION_LINK_ID = bpr.TRANSACTION_LINK_ID
								and INSTITUTION_NUMBER = bpr.INSTITUTION_NUMBER
								and RESPONSE_CODE <> '00' and RESPONSE_CODE is Not Null
								)
					or v_exclude_NonApprovd = '0')
		and (TRANSACTION_LINK_ID in (SELECT COLUMN_VALUE FROM TABLE(lnk_arr_list)) or v_skip_T_Link = 'Y')
		and (Terminal_id = v_tid or nvl(v_tid,'00000000') = '00000000')
		Group By TERMINAL_ID, MERCHANT_ID
    )
    Loop
    	n_counter := n_counter + 1;
    	v_SQL := '  Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values ' ||
			    			'(50, ' || n_counter || ' , ''  UPDATE CIS_DEVICE_LINK Set ';
		v_SQL := v_SQL || ' DATE_SETTLEMENT = TO_CHAR(TO_DATE(NVL(TRIM(v_date), ''''' || TO_CHAR(BPR.MIN_RECORD_DATE,'YYYYMMDD') || '''''),''''YYYYMMDD'''') -2,''''YYYYMMDD'''')';
		v_SQL := v_SQL || ', TIME_SETTLEMENT = ''''000000''''';
		v_SQL := v_SQL || ' WHERE INSTITUTION_NUMBER = ''''' || v_inst_num || '''''';
		v_SQL := v_SQL || ' AND TERMINAL_ID = ''''' || NVL(v_terminal_id, BPR.TERMINAL_ID) || '''''';
		v_SQL := v_SQL || ' AND MERCHANT_ID = ''''' || NVL(v_merchant_id, BPR.MERCHANT_ID) || '''''';
		v_SQL := v_SQL || ';'')';
		Execute Immediate v_SQL;
	End Loop;
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SQL_SCRIPT) Values (51, '  Commit;');

    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 0, '  dbms_output.put_line(''Finished Successfully.'');');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 1, 'Exception');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 2, '  When Others Then');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 3, '    dbms_output.put_line(''******************************'');');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 4, '    dbms_output.put_line(sqlerrm);');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 5, '    dbms_output.put_line(dbms_utility.format_error_backtrace);');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 6, '    dbms_output.put_line(''******************************'');');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 7, '    Rollback;');
    Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (990, 8, '    Raise;');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (998, 1, 'End;');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (999, 1, '/');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (999, 2, '--SCROLL TO THE TOP AND READ THE NOTES BEFORE YOU EXECUTE!!!');
	Commit;

	-- Provide Some notes for users: This will be printed on top
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 0, '--           *****************');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 1, '--           **** READ ME ****');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 2, '--           *****************');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 3, '-- Generated by Auth Copy Tool Version: ' || v_version);
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 4, '-- Execute Fully using Golden, SQL PLUS or equivalent. An input prompt will pop up and the Date of the Auth must be inputted');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 5, '-- The Date of the Auth will affect when it can be processed. Ideally it should be set as Posting Date - 1');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 6, '-- The Terminal ID is updated with the provided date - 1 day so that POS NT Loader can be processed immediately.');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 7, '-- This script can be re-executed again and again to reset the authorisations and terminals so you can re-execute POS NT Loader as needed.');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 8, '-- Kindly open the DBMS OUTPUT Window if running in Golden as the logs will be placed there.');
	Insert Into TMP_AUTH_COPY_SCRIPTS( ID, SCRIPT_NUM, SQL_SCRIPT) Values (0, 9, '-- In case this script fails, the basic error handling should log the error and line number in the same DBMS Output Window');
	Commit;
	--
	--Drop Temp Type
	Begin
		Execute Immediate 'Drop Type tmp_link_arr_list';
	Exception
		When others then
			Null;
	End;
	--
	dbms_output.put_line('Finished - No Errors!');
Exception
	When Others then
		dbms_output.put_line(sqlerrm);
		dbms_output.put_line(v_SQL);
		dbms_output.put_line(dbms_utility.format_error_stack || dbms_utility.format_error_backtrace);
		Rollback;
End;
/
;
-- Script 3 - Query the Scripts in their respective Order
Select ID, SCRIPT_NUM as NUM, SQL_SCRIPT as SCRIPT
FROM TMP_AUTH_COPY_SCRIPTS
Order by ID, SCRIPT_NUM;