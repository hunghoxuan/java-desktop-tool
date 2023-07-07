/*
This SQL script's objective is insert add merchant application. Save time because dont need to login into WebGui.
Use cases: BA, Testers, Automation test.
Developed and maintained by: HUNG.HO@RS2.COM
*/
SET DEFINE OFF;
DECLARE
-- MANUAL PARAMS
v_user_id VARCHAR2(6) := '999999';
v_station_number VARCHAR2(3) := '129';
--
v_institution_number VARCHAR2(8) := '00000111';
v_merchant_name VARCHAR2(35) := 'Test_1_RSSP43001';
v_merchant_street VARCHAR2(40) := 'NeuIsenburg';
v_merchant_city VARCHAR2(18) := 'Frankfurt';
v_merchant_contact VARCHAR2(35) := 'Mia';
v_merchant_country VARCHAR2(3) := '280';  -- 280: germany
v_client_type VARCHAR2(3) := '002'; -- 002: Member
--
v_application_status VARCHAR2(3) := '002'; -- 002: approved, 006: entered
v_billing_level VARCHAR2(3) := '001'; -- 001: Yes
v_client_level VARCHAR2(3) := '001'; -- 001: member, 002: Group, 003: Sub-group
--
v_service_contract_id VARCHAR2(3) := '111'; -- 111: Ecommerce, 110: POS
--
-- Settlement method:
	-- Option 1.net settlement
	v_settlement_method VARCHAR2(3) := '510'; -- 510: Daily NET tran-based
	v_posting_method VARCHAR2(3) := '001'; -- 001: Net
	-- Option 2. gross settlement
	-- v_settlement_method VARCHAR2(3) := '523'; -- 523: Daily GROSS Tran-based
	-- v_posting_method VARCHAR2(3) := '002'; -- 002: GC++
--
-- CLIENT TARIFF:
	-- Option 1: Blended 502---
	v_client_tariff VARCHAR2(6) := '000502';
    --
	-- Option 2: IC++  509 --
	-- v_client_tariff VARCHAR2(6) := '000509';
--
v_fx_tariff VARCHAR2(6) := '000001';
v_merchant_tariff VARCHAR2(6) := '000000';
v_condition_set VARCHAR2(6) := '000001';  -- 001: Cbk Flat
--
v_currency VARCHAR2(3) := '978'; -- 970: EUR
v_entity_id VARCHAR2(3) := '002'; -- 001: FSEU, 002: Trustbin
v_risk_group VARCHAR2(3) := '001';
v_risk_rule_group VARCHAR2(3) := '001';
--
v_BANK_CLEARING_NUMBER varchar2(35) := 'JEEREREREE3';
v_COUNTER_BANK_ACCOUNT varchar2(16) := 'DE6565656565656';
v_funding_bank varchar2(12) := ''; -- empty: auto fill based on entity_id, otherwise: entity_id = 001 --> funding_bank = inst number / entity_id = 002 -> funding_bank = 90000102
--
v_audit_trail VARCHAR2(35) := 'MANUAL_INSERT_';
--
-- CACULATED PARAMS
v_APPLICATION_NUMBER varchar(10);
v_record_date VARCHAR2(8);
v_trans_date VARCHAR2(8);
v_posting_date VARCHAR2(8);
v_system_date VARCHAR2(8);
V_INIT_RETURN PLS_INTEGER;
v_client_number varchar(8);
v_group_number varchar(8);
v_our_reference varchar(15);
v_currency_iso VARCHAR2(3);
v_account_type_name VARCHAR2(20);
v_PAYMENT_REFERENCE varchar2(24);
v_CORRESP_BANK_NUMBER varchar2(35);
v_funding_bank1 varchar2(12);
v_BANK_CLEARING_NUMBER1 varchar2(35);
v_COUNTER_BANK_ACCOUNT1 varchar2(16);
v_client_tax_record_id varchar2(10);
--
v_account_type_ids dbms_sql.varchar2_table;
v_account_type_id varchar(3);
--
v_service_ids dbms_sql.varchar2_table;
v_service_id varchar(3);
--
v_terminal_id varchar(8);
--
BEGIN
	if (v_settlement_method = '523') then -- Gross --> must fill both 007, 012
		v_account_type_ids(1):= '012'; -- Payment ACCT
		v_account_type_ids(2):= '007'; -- Fee Coll
	ELSE
		v_account_type_ids(1):= '012'; -- Payment ACCT
	end if;
    --
	v_service_ids(1) := '009'; -- Acquire VPay
    --
	v_service_ids(2) := '102'; -- MAST Consumer
	v_service_ids(3) := '300'; -- MAST Commercial
    --
	v_service_ids(4) := '202'; -- VISA Commercial
	v_service_ids(5) := '201'; -- VISA Cons
    --
	v_service_ids(6) := '107'; -- MAES Cons
	v_service_ids(7) := '109'; -- MAES Corp
    --
	-- v_service_ids(8) := '406'; -- DIN Consumer
	-- v_service_ids(9) := '439';
    --
	-- AUTO-CACULATED PARAMS
	if (v_posting_date is null or v_posting_date = '') then
		SELECT POSTING_DATE INTO v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number; -- posting date
	end if;
	SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL; -- get system date
	v_record_date:= '20121204';
    --
	v_audit_trail := v_audit_trail || v_record_date;
    --
	BW_PRC_RES.INITGLOBALVARS (v_institution_number, v_station_number, v_user_id, V_INIT_RETURN);
	v_application_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
	v_client_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('010',1);
	v_group_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('018',1);
	v_client_tax_record_id := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1);
	v_our_reference := '0000000' || v_client_number;
	SELECT swift_code INTO v_currency_iso FROM BWT_CURRENCY WHERE INSTITUTION_NUMBER = v_institution_number AND ISO_CODE = v_currency AND Language = 'USA';
    --
	-- [CIS_APPLICATION_DETAIL]
	INSERT INTO CIS_APPLICATION_DETAIL
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,RECORD_TYPE,CLIENT_NUMBER,GROUP_NUMBER, APPLICATION_STATUS,CONTACT_NAME,VAT_REG_NUMBER,REGISTRATION_NUMBER,CLIENT_TYPE,RESIDENCE_STATUS,CLIENT_LANGUAGE,INSTITUTION_ACCT_OFFICER,PROVIDER_ACCT_OFFICER,CLIENT_BRANCH,SHORT_NAME,COMPANY_NAME,LEGAL_FORM,TRADE_NAME,BUSINESS_CLASS,OUR_REFERENCE,SERVICE_CONTRACT_ID,CONDITION_SET,LIMIT_CURRENCY,FLOOR_LIMIT,CLIENT_LEVEL,SETTLEMENT_METHOD,POSTING_METHOD,PARENT_APPL_NUMBER,LAST_AMENDMENT_DATE,AUDIT_TRAIL,CLIENT_COUNTRY,CLIENT_CITY,CONTRACT_CATEGORY,MERCHANT_STREET,CLIENT_REGION,ECOMMERCE_INDICATOR,RISK_GROUP,LOCKING_COUNTER,ACCUMULATOR_SCHEME,APPL_PROC_INVOKED,CLIENT_SCHEME,TRANSFER_METHOD,CLIENT_STATUS,MERCHANT_TRAN_TARIFF,CONTRACT_REGION,FX_TARIFF,RISK_RULE_GROUP_ID,ENTITY_ID)
	VALUES
		(v_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		v_record_date, -- RECORD_DATE
		'003', -- RECORD_TYPE
		v_client_number, -- CLIENT_NUMBER
		v_group_number,
		v_application_status, -- APPLICATION_STATUS
		v_merchant_contact, -- CONTACT_NAME
		'12345565222411', -- VAT_REG_NUMBER
		'11111111111111', -- REGISTRATION_NUMBER
		v_client_type, -- CLIENT_TYPE
		'001', -- RESIDENCE_STATUS
		'002', -- CLIENT_LANGUAGE
		'000', -- INSTITUTION_ACCT_OFFICER
		'000', -- PROVIDER_ACCT_OFFICER
		'000', -- CLIENT_BRANCH
		v_merchant_name, -- SHORT_NAME
		v_merchant_name, -- COMPANY_NAME
		'006', -- LEGAL_FORM
		v_merchant_name, -- TRADE_NAME
		'0000', -- BUSINESS_CLASS
		v_our_reference, -- OUR_REFERENCE
		v_service_contract_id, -- SERVICE_CONTRACT_ID
		v_condition_set, -- CONDITION_SET
		v_currency, -- LIMIT_CURRENCY
		'0.', -- FLOOR_LIMIT
		v_client_level, -- CLIENT_LEVEL
		v_settlement_method, -- SETTLEMENT_METHOD
		v_posting_method, -- POSTING_METHOD
		'0000000000', -- PARENT_APPL_NUMBER
		v_record_date, -- LAST_AMENDMENT_DATE
		v_audit_trail, -- AUDIT_TRAIL
		v_merchant_country, -- CLIENT_COUNTRY
		v_merchant_city, -- CLIENT_CITY
        --
		'003', -- CONTRACT_CATEGORY
		v_merchant_street, -- MERCHANT_STREET
		'000', -- CLIENT_REGION
		'999', -- ECOMMERCE_INDICATOR
		v_risk_group, -- RISK_GROUP
		'0', -- LOCKING_COUNTER
		'000', -- ACCUMULATOR_SCHEME
		'0', -- APPL_PROC_INVOKED
		'000000', -- CLIENT_SCHEME
		'000001', -- TRANSFER_METHOD
		'001', -- CLIENT_STATUS
		v_merchant_tariff, -- MERCHANT_TRAN_TARIFF
		'999', -- CONTRACT_REGION
		v_fx_tariff, -- FX_TARIFF
		v_risk_rule_group, -- Risk Rule Group ID
		v_entity_id -- ENTITY_ID
		);
   	DBMS_OUTPUT.put_line('CIS_APPLICATION_DETAIL: ');
	----
	-- [CIS_APPLICATION_ACCT_TYPE]
	if (v_entity_id = '001') then
		v_funding_bank := v_institution_number;
	Elsif (v_entity_id = '002') then
		v_funding_bank := '90000102';
	ELSE
		v_funding_bank :=  v_institution_number;
	end if;
    --
	FOR i IN v_account_type_ids.FIRST .. v_account_type_ids.LAST
	LOOP
		v_account_type_id := v_account_type_ids(i);
         --If net settlement only fill account_type_id = 012. If gross settlement then must fill both 007 and 112
		if (v_account_type_id = '012' or v_settlement_method ='523') then
			v_BANK_CLEARING_NUMBER1 := v_BANK_CLEARING_NUMBER;
			v_COUNTER_BANK_ACCOUNT1 := v_COUNTER_BANK_ACCOUNT;
			v_PAYMENT_REFERENCE :=  v_account_type_id || v_client_number || v_currency_iso;
			v_CORRESP_BANK_NUMBER := v_account_type_id || v_client_number;
			v_funding_bank1 := v_funding_bank;
            --
			SELECT TYPE_ID INTO v_account_type_name FROM BWT_ACCOUNT_TYPE_ID WHERE INSTITUTION_NUMBER = v_institution_number AND Index_field = v_account_type_id AND Language = 'USA';
		else
			v_BANK_CLEARING_NUMBER1 := '';
			v_COUNTER_BANK_ACCOUNT1 := '';
			v_PAYMENT_REFERENCE := '';
			v_CORRESP_BANK_NUMBER := '';
			v_funding_bank1 := '';
			v_account_type_name := '';
		end if;
        --
        --
		INSERT INTO CIS_APPLICATION_ACCT_TYPE
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,COUNTER_BANK_ACCOUNT_NAME,FUNDING_BANK,BANK_CLEARING_NUMBER,COUNTER_BANK_ACCOUNT,PAYMENT_REFERENCE,CORRESP_BANK_NUMBER,ACCT_CURRENCY,RECORD_TYPE,RECORD_DATE,BILLING_LEVEL,AUDIT_TRAIL,STATEMENT_GENERATION,STATEMENT_TYPE,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE)
		VALUES
		(v_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		v_account_type_id, -- ACCOUNT_TYPE_ID
		v_account_type_name,
		v_funding_bank1,
		v_BANK_CLEARING_NUMBER1,
		v_COUNTER_BANK_ACCOUNT1,
		v_PAYMENT_REFERENCE,
		v_CORRESP_BANK_NUMBER,
		v_currency, -- ACCT_CURRENCY
		'003', -- RECORD_TYPE
		v_record_date, -- RECORD_DATE
		v_billing_level, -- BILLING_LEVEL
		v_audit_trail, -- AUDIT_TRAIL
		'001', -- STATEMENT_GENERATION
		'900', -- STATEMENT_TYPE
		'0', -- LOCKING_COUNTER
		v_merchant_country -- RECEIVER_COUNTRY_CODE
		);
        --
		-- [CIS_APPLICATION_MANDATE]
		INSERT INTO CIS_APPLICATION_MANDATE
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,ACCT_CURRENCY,RECORD_DATE,AUDIT_TRAIL,UNIQUE_MANDATE_REF,LOCKING_COUNTER,INSTRUMENT_CODE,FIRST_OCCURRENCE,DATE_OF_SIGNATURE)
		VALUES
		(v_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		v_account_type_id, -- ACCOUNT_TYPE_ID
		v_currency, -- ACCT_CURRENCY
		v_record_date, -- RECORD_DATE
		v_audit_trail, -- AUDIT_TRAIL
		v_PAYMENT_REFERENCE, -- UNIQUE_MANDATE_REF
		'0', -- LOCKING_COUNTER
		'001', -- INSTRUMENT_CODE
		'000', -- FIRST_OCCURRENCE
		v_record_date -- DATE_OF_SIGNATURE
		);
	END LOOP;
    DBMS_OUTPUT.put_line('CIS_APPLICATION_ACCT_TYPE: ');
    --
	-- [CIS_APPL_TERMINAL_INPUT]
	v_terminal_id := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('140',1);
    --
	INSERT INTO CIS_APPL_TERMINAL_INPUT
		 (APPLICATION_NUMBER,TERMINAL_ID,INSTITUTION_NUMBER,MERCHANT_ID,RECORD_DATE,TERMINAL_STATUS,DEVICE_CAPABILITY,EOD_INDICATOR,TERMINAL_CURRENCY,POS_FEE_TYPE,AUDIT_TRAIL,LOCKING_COUNTER)
	VALUES
		(v_application_number, -- APPLICATION_NUMBER
		v_terminal_id, -- TERMINAL_ID
		v_institution_number, -- INSTITUTION_NUMBER
		v_our_reference, -- MERCHANT_ID
		v_record_date, -- RECORD_DATE
		'001', -- TERMINAL_STATUS
		'006', -- DEVICE_CAPABILITY
		'001', -- EOD_INDICATOR
		v_currency, -- TERMINAL_CURRENCY
		'000', -- POS_FEE_TYPE
		v_audit_trail, -- AUDIT_TRAIL
		'0' -- LOCKING_COUNTER
		);
    DBMS_OUTPUT.put_line('CIS_APPL_TERMINAL_INPUT: ');
    --
	-- [CIS_APPLICATION_ADDR]
	INSERT INTO CIS_APPLICATION_ADDR
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,ADDRESS_CATEGORY,RECORD_DATE,ADDR_LINE_1,POST_CODE,ADDR_CLIENT_CITY,CLIENT_COUNTRY,AUDIT_TRAIL,RECORD_TYPE,CONTACT_NAME,DELIVERY_METHOD,EFFECTIVE_DATE,GROUP_SPECIFIC,LOCKING_COUNTER)
	VALUES
		(v_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		'012', -- ADDRESS_CATEGORY
		v_record_date, -- RECORD_DATE
		v_merchant_street, -- ADDR_LINE_1
		'63232', -- POST_CODE
		v_merchant_city, -- ADDR_CLIENT_CITY
		v_merchant_country, -- CLIENT_COUNTRY
		v_audit_trail, -- AUDIT_TRAIL
		'003', -- RECORD_TYPE
		v_merchant_contact, -- CONTACT_NAME
		'000', -- DELIVERY_METHOD
		v_record_date, -- EFFECTIVE_DATE
		'000', -- GROUP_SPECIFIC
		'0' -- LOCKING_COUNTER
		);
    ----
	----
   	DBMS_OUTPUT.put_line('CIS_APPLICATION_ADDR: ');
    --
	-- [CIS_APPLICATION_SERVICES]
	FOR i IN v_service_ids.FIRST .. v_service_ids.LAST
	LOOP
		v_service_id := v_service_ids(i);
		INSERT INTO CIS_APPLICATION_SERVICES
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,AUDIT_TRAIL,SERVICE_CONTRACT_ID,SERVICE_ID,SERVICE_ASSIGNED,RECORD_TYPE,CLIENT_TARIFF,LOCKING_COUNTER,REVIEW_DATE,EXPIRY_DATE,EFFECTIVE_DATE)
		VALUES
		(v_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		v_record_date, -- RECORD_DATE
		v_audit_trail, -- AUDIT_TRAIL
		v_service_contract_id, -- SERVICE_CONTRACT_ID
		v_service_id, -- SERVICE_ID
		'001', -- SERVICE_ASSIGNED
		'003', -- RECORD_TYPE
		v_client_tariff, -- CLIENT_TARIFF
		'0', -- LOCKING_COUNTER
		'99991231', -- REVIEW_DATE
		'99991231', -- EXPIRY_DATE
		v_record_date -- EFFECTIVE_DATE
		);
	END LOOP;
    DBMS_OUTPUT.put_line('CIS_APPLICATION_SERVICES: ');
    --
	-- [CIS_APPL_CLIENT_TAX_STATUS]
	INSERT INTO CIS_APPL_CLIENT_TAX_STATUS
		 (INSTITUTION_NUMBER,RECORD_DATE,APPLICATION_NUMBER,TAX_TYPE,EFFECTIVE_DATE,EXPIRY_DATE,TAX_STATUS,RECORD_ID_NUMBER,AUDIT_TRAIL,LOCKING_COUNTER)
	VALUES
	(v_institution_number, -- INSTITUTION_NUMBER
	v_record_date, -- RECORD_DATE
	v_application_number, -- APPLICATION_NUMBER
	'003', -- TAX_TYPE
	v_record_date, -- EFFECTIVE_DATE
	'99991231', -- EXPIRY_DATE
	'001', -- TAX_STATUS    ---001: Subject to VAT, 002: Exempt for VAT
	v_client_tax_record_id, -- RECORD_ID_NUMBER
	v_audit_trail, -- AUDIT_TRAIL
	'0' -- LOCKING_COUNTER
	);
    --
	COMMIT;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/