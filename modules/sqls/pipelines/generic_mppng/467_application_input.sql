/*
This SQL script's objective is insert add merchant application. Save time because dont need to login into WebGui.
Objective: automatically get data from config tables and populate to merchant application tables to help onboarding without error.
Input params (will get default values if empty input):
- 1: institution_number
- 2: client_type: 002: Member, 003: Financial Inst. ; 009	Broker ...
- 3: service_contract_id (one to many, seperated by comma). if empty -> auto get from db
- 4: account_type_ids (one to many, seperated by comma). if empty -> auto get from db
Output: insert/update data in:
- CIS_APPLICATION_DETAIL
- CIS_APPLICATION_ADDR
- CIS_APPL_TERMINAL_INPUT
- CIS_APPLICATION_SERVICES
- CIS_APPLICATION_ACCT_TYPE
- CIS_APPLICATION_MANDATE
Use cases: BA, Testers, Automation test.
Contributors: Hung, Mia, Nga
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;

DECLARE
-- INPUT PARAMS (REQUIRED)
v_institution_number VARCHAR2(8) := '&institution_number';
v_client_type VARCHAR2(3) := '&client_type__002_merchant_009_broker_'; -- 002: Member, 003: Financial Inst. ; 009	Broker ; 011	ISO  ;012	Sub-ISO ; 013	Agent ; 014	ISV ; 020	Beneficiary Owne
v_client_level VARCHAR2(3) := '&client_level__001_member_002_group'; -- 001: member, 002: Group, 003: Sub-group
v_billing_level VARCHAR2(3) := '&billing_level__001'; -- 001: Yes
v_parent_application_number VARCHAR2(15) := '&parent_application_number__';

v_service_contract_id VARCHAR2(20) := '&service_contract_id__111_ecommerce_'; -- 111: Ecommerce, 110: POS
v_currency VARCHAR2(3) := '&currency__978_EUR'; -- 978: EUR

v_account_type_ids VARCHAR2(40) := '&account_type_id__012_payment_007_fee_'; -- 012: Payment ACCT, 007: Fee Col
v_merchant_country VARCHAR2(3) := '&country__280_germany';  -- 280: germany

v_posting_method VARCHAR2(3) := '&posting_method__001_net_002_gc'; -- 001:NET, 002: GC++
v_settlement_method VARCHAR2(3) := '&settlement_method__510_net_523_gross'; -- 510: Daily NET tran-based, 523: Daily GROSS Tran-based

v_client_tariff VARCHAR2(6) := '&client_tariff__000502';   -- Blended 000502, IC++ 000509
v_merchant_tariff VARCHAR2(6) := '&merchant_tariff__000000';

v_risk_group VARCHAR2(3) := '&risk_group_id__001';
v_risk_rule_group VARCHAR2(3) := '&risk_rule_group_id__001';
v_entity_id VARCHAR2(3) := '&entity_id__001'; -- 001: FSEU, 002: Trustbin

v_post_code VARCHAR2(6) := '&post_code__63263';
v_merchant_name VARCHAR2(35) := '&merchant_name';
v_merchant_email VARCHAR2(100) := '&merchant_email';
v_record_date VARCHAR2(8) := '&record_date__yyyymmdd';
v_record_type varchar2(3) := '&record_type__003_merchant'; -- 003: merchant

-- DEFAULT PARAMS
v_service_contract_id_default VARCHAR2(20) := '111';
v_account_type_ids_default VARCHAR2(20) := '012,007';
v_client_type_default VARCHAR2(3) := '002';

v_client_level_member VARCHAR2(3) := '001';
v_client_level_group VARCHAR2(3) := '002';
v_client_level_sub_group VARCHAR2(3) := '003';

v_merchant_country_default VARCHAR2(3) := '280';
v_record_type_default varchar2(3) := '003';
v_client_level_default VARCHAR2(3) := '001';
v_currency_default VARCHAR2(3) := '978'; -- 978: EUR
v_merchant_tariff_default VARCHAR2(6) := '000000';
v_risk_group_default VARCHAR2(3) := '001';
v_client_tariff_default VARCHAR2(6) := '000502';
v_funding_bank_default VARCHAR2(8) := '90000102';
v_entity_id_default VARCHAR2(3) := '001'; -- 001: FSEU, 002: Trustbin
v_contract_category VARCHAR2(3) := '003'; -- 001, 003 only
v_service_category_default VARCHAR2(3) := '002'; -- 002: acquiring
v_eod_indicator varchar2(3) := '002';
v_posting_method_default VARCHAR2(3) := '001'; -- 001: Net, 002: GC++
v_settlement_method_default VARCHAR2(3) := '510'; -- 510: Daily NET tran-based, 523: Daily GROSS Tran-based

v_post_code_default VARCHAR2(6) := '63263';
v_merchant_street VARCHAR2(40) := 'NeuIsenburg';
v_merchant_city VARCHAR2(18) := 'Frankfurt';

v_application_status VARCHAR2(3) := '002'; -- 006: entered, 002: approved
v_run_soa_process_id varchar2(3) := ''; -- 467: run soa process to board merchant, empty: dont run soa process

v_fx_tariff VARCHAR2(6) := '000001';

v_BANK_CLEARING_NUMBER varchar2(35) := 'JEEREREREE3';
v_COUNTER_BANK_ACCOUNT varchar2(16) := 'DE6565656565656';
v_funding_bank varchar2(12); -- empty: auto fill based on entity_id, otherwise: entity_id = 001 --> funding_bank = inst number / entity_id = 002 -> funding_bank = 90000102

v_audit_trail VARCHAR2(35) := 'MANUAL_INSERT_';
v_user_id VARCHAR2(6) := '999999';
v_station_number VARCHAR2(3) := '129';

-- CACULATED PARAMS
v_service_category varchar(3); -- 002: acquiring, 001: issuing, 016: Broker
v_APPLICATION_NUMBER varchar(10);
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
v_parent_client_number varchar2(12);
v_client_tax_record_id varchar2(10);

v_account_type_id varchar(3);
v_merchant_short_name varchar(26);
v_trade_name varchar(25);
v_service_id varchar(3);
v_terminal_id varchar(8);
v_is_broker boolean;

P_OUT varchar2(100);
v_params varchar2(100);
v_errors VARCHAR2(520);
v_tmp varchar(100);

BEGIN
	--1. CHECK REQUIRED PARAMS
	if (v_institution_number is null or v_institution_number = '') THEN
		v_errors := 'Param is required or missing';
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	end if;

	if (LOWER(v_client_type) = 'acq' or v_client_type = '002' or LOWER(v_client_type) = 'merchant') then
		v_service_category := v_service_category_default;
		v_is_broker := false;
	else    -- IF BROKER
		v_service_contract_id := '123'; -- 123: Partner
		v_account_type_ids := '*'; -- auto
		v_settlement_method := '996'; -- 996: Partner
		v_posting_method := '100'; -- 100: Partner, 999: N/A
		v_client_tariff := '000000';  --- For service fee 509 if assign
		v_fx_tariff := '000001';
		v_merchant_tariff := '000000';
		v_funding_bank := '';
		v_service_category := '016';
		v_is_broker := true;
	end if;

	--2. DEFAULT VALUES IF MISSING
	if (v_service_category is null or v_service_category = '') then
		v_service_category := v_service_category_default;
	end if;

	if (v_service_contract_id is null or v_service_contract_id = '') then
		v_service_contract_id := v_service_contract_id_default;
	end if;

	if (v_account_type_ids is null or v_account_type_ids = '') then
		v_account_type_ids := v_account_type_ids_default;
	end if;

	if (v_merchant_country is null or v_merchant_country = '') then
	    v_merchant_country := v_merchant_country_default;
	end if;

	if (v_client_type is null or v_client_type = '') then
	    v_client_type := v_client_type_default;
	end if;

	if (v_record_type is null or v_record_type = '') then
	    v_record_type := v_record_type_default;
	end if;

	if (v_client_level is null or v_client_level = '') then
	    v_client_level := v_client_level_default;
	end if;

	if (v_currency is null or v_currency = '') then
	    v_currency := v_currency_default;
	end if;

	if (v_merchant_tariff is null or v_merchant_tariff = '') then
	    v_merchant_tariff := v_merchant_tariff_default;
	end if;

	if (v_risk_group is null or v_risk_group = '') then
	    v_risk_group := v_risk_group_default;
	end if;

	if (v_client_tariff is null or v_client_tariff = '') then
	    v_client_tariff := v_client_tariff_default;
	end if;

	if (v_post_code is null or v_post_code = '') then
	    v_post_code := v_post_code_default;
	end if;

	if (v_entity_id is null or v_entity_id = '') then
	    v_entity_id := v_entity_id_default;
	end if;

	if (v_parent_application_number is null or v_parent_application_number = '') then
	    v_parent_application_number := '0000000000';
	end if;

	if (v_posting_method is null or v_posting_method = '') then
	    v_posting_method := v_posting_method_default;
	end if;

	if (v_billing_level is null or v_billing_level = '') then
	    v_billing_level := '001';
	end if;

	if (v_settlement_method is null or v_settlement_method = '') then
		if (v_posting_method = v_posting_method_default) then
		   	v_settlement_method := v_settlement_method_default;
		else
	 		v_settlement_method := '523';
	 	end if;
	end if;

	if (v_funding_bank is null) then
		if (v_entity_id = '001') then
			v_funding_bank := v_institution_number;
		Elsif (v_entity_id = '002') then
			v_funding_bank := v_funding_bank_default;
		ELSE
			v_funding_bank :=  v_institution_number;
		end if;
	end if;

	--3. CHECK DATA & AUTO-CACULATED PARAMS
	BEGIN
		if (v_posting_date is null or v_posting_date = '') then
			SELECT POSTING_DATE INTO v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number; -- posting date
		end if;

		SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL; -- get system date
		SELECT swift_code INTO v_currency_iso FROM BWT_CURRENCY WHERE INSTITUTION_NUMBER = v_institution_number AND ISO_CODE = v_currency AND Language = 'USA';

		if (v_merchant_name is null or v_merchant_name = '') then
			SELECT SERVICE_CONTRACT INTO v_merchant_name FROM BWT_SERVICE_CONTRACT_ID WHERE INSTITUTION_NUMBER = v_institution_number AND Index_field = v_service_contract_id AND Language = 'USA';
 		else
 		    SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL;
 		END IF;

		if (v_parent_application_number is null or v_parent_application_number = '' or v_parent_application_number = '0000000000') then
			SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL;
		else
			SELECT CLIENT_NUMBER, GROUP_NUMBER INTO v_parent_client_number, v_group_number FROM CIS_APPLICATION_DETAIL WHERE INSTITUTION_NUMBER = v_institution_number AND service_contract_id = v_service_contract_id AND APPLICATION_NUMBER = v_parent_application_number;
		end if;
	EXCEPTION WHEN NO_DATA_FOUND THEN
		v_errors := 'Data not found. Service contract id: ' || v_service_contract_id;
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	END;

    if (v_client_level = v_client_level_group) then
	   	v_merchant_short_name := 'Group ';
	elsif (v_client_level = v_client_level_sub_group) then
 		v_merchant_short_name := 'SubGroup ';
 	else
 		v_merchant_short_name :=  '';
	end if;

 	if (v_is_broker = true) then
 		v_merchant_short_name := v_merchant_short_name || 'Broker';
 	else
 		v_merchant_short_name := v_merchant_short_name || 'Merchant';
 	end if;

 	v_trade_name := v_merchant_short_name;
    v_merchant_name := v_merchant_short_name || v_merchant_name;

 	if (LENGTH(v_merchant_name) > 35) then
 		v_merchant_name := substr(v_merchant_name, 1, 35);
 	end if;

	IF (v_errors is null or v_errors = '') THEN
		if (v_record_date is null or v_record_date = '') then
			v_record_date:= v_posting_date; -- must equals posting_date, not system date
		end if;

		v_audit_trail := v_audit_trail || v_record_date;
		BW_PRC_RES.INITGLOBALVARS (v_institution_number, v_station_number, v_user_id, V_INIT_RETURN);
		v_application_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
		v_client_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('010',1);

		if (v_group_number is null or v_group_number = '') then
		    v_group_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('018',1);
		end if;

		v_client_tax_record_id := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1);
		v_terminal_id := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('140',1);
		v_our_reference := '0000000' || v_client_number;

		--4. INSERT [CIS_APPLICATION_DETAIL]
		INSERT INTO CIS_APPLICATION_DETAIL
			 (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,RECORD_TYPE,CLIENT_NUMBER,GROUP_NUMBER, APPLICATION_STATUS,CONTACT_NAME,VAT_REG_NUMBER,REGISTRATION_NUMBER,CLIENT_TYPE,RESIDENCE_STATUS,CLIENT_LANGUAGE,INSTITUTION_ACCT_OFFICER,PROVIDER_ACCT_OFFICER,CLIENT_BRANCH,SHORT_NAME,COMPANY_NAME,LEGAL_FORM,TRADE_NAME,BUSINESS_CLASS,OUR_REFERENCE,SERVICE_CONTRACT_ID,CONDITION_SET,LIMIT_CURRENCY,FLOOR_LIMIT,CLIENT_LEVEL,SETTLEMENT_METHOD,POSTING_METHOD,PARENT_APPL_NUMBER,CLIENT_ORGANIZATION,LAST_AMENDMENT_DATE,AUDIT_TRAIL,CLIENT_COUNTRY,CLIENT_CITY,CONTRACT_CATEGORY,MERCHANT_STREET,CLIENT_REGION,ECOMMERCE_INDICATOR,RISK_GROUP, RISK_RULE_GROUP_ID, LOCKING_COUNTER,ACCUMULATOR_SCHEME,APPL_PROC_INVOKED,CLIENT_SCHEME,TRANSFER_METHOD,CLIENT_STATUS,MERCHANT_TRAN_TARIFF,CONTRACT_REGION,FX_TARIFF,ENTITY_ID)
		VALUES
			(v_institution_number, -- INSTITUTION_NUMBER
			v_application_number, -- APPLICATION_NUMBER
			v_record_date, -- RECORD_DATE
			v_record_type, -- RECORD_TYPE
			v_client_number, -- CLIENT_NUMBER
			v_group_number,
			v_application_status, -- APPLICATION_STATUS
			v_merchant_short_name, -- CONTACT_NAME
			'12345565222411', -- VAT_REG_NUMBER
			'11111111111111', -- REGISTRATION_NUMBER
			v_client_type, -- CLIENT_TYPE
			'001', -- RESIDENCE_STATUS
			'002', -- CLIENT_LANGUAGE
			'000', -- INSTITUTION_ACCT_OFFICER
			'000', -- PROVIDER_ACCT_OFFICER
			'000', -- CLIENT_BRANCH
			v_merchant_short_name, -- SHORT_NAME
			v_merchant_name, -- COMPANY_NAME
			'006', -- LEGAL_FORM
			v_trade_name, -- TRADE_NAME
			'0000', -- BUSINESS_CLASS
			v_our_reference, -- OUR_REFERENCE
			v_service_contract_id, -- SERVICE_CONTRACT_ID
			v_client_tariff, -- CONDITION_SET
			v_currency, -- LIMIT_CURRENCY
			'0.', -- FLOOR_LIMIT
			v_client_level, -- CLIENT_LEVEL
			v_settlement_method, -- SETTLEMENT_METHOD
			v_posting_method, -- POSTING_METHOD
			v_parent_application_number, -- PARENT_APPL_NUMBER
			v_parent_client_number, -- CLIENT_ORGANIZATION
			v_record_date, -- LAST_AMENDMENT_DATE
			v_audit_trail, -- AUDIT_TRAIL
			v_merchant_country, -- CLIENT_COUNTRY
			v_merchant_city, -- CLIENT_CITY

			v_contract_category, -- CONTRACT_CATEGORY
			v_merchant_street, -- MERCHANT_STREET
			'000', -- CLIENT_REGION
			'999', -- ECOMMERCE_INDICATOR
			v_risk_group, -- RISK_GROUP
			v_risk_rule_group, -- RISK RULE GROUP
			'0', -- LOCKING_COUNTER
			'000', -- ACCUMULATOR_SCHEME
			'0', -- APPL_PROC_INVOKED
			'000000', -- CLIENT_SCHEME
			'000001', -- TRANSFER_METHOD
			'001', -- CLIENT_STATUS
			v_merchant_tariff, -- MERCHANT_TRAN_TARIFF
			'999', -- CONTRACT_REGION
			v_fx_tariff, -- FX_TARIFF
			v_entity_id -- ENTITY_ID
			);
	   	DBMS_OUTPUT.put_line('CIS_APPLICATION_DETAIL inserted: ' || v_application_number || ' client_number: ' || v_client_number || ' group_number: ' || v_group_number);

        --7. [CIS_APPLICATION_ADDR]
		INSERT INTO CIS_APPLICATION_ADDR
			 (INSTITUTION_NUMBER,APPLICATION_NUMBER,ADDRESS_CATEGORY,EMAIL_ADDR,RECORD_DATE,ADDR_LINE_1,POST_CODE,ADDR_CLIENT_CITY,CLIENT_COUNTRY,AUDIT_TRAIL,RECORD_TYPE,CONTACT_NAME,DELIVERY_METHOD,EFFECTIVE_DATE,GROUP_SPECIFIC,LOCKING_COUNTER)
		VALUES
			(v_institution_number, -- INSTITUTION_NUMBER
			v_application_number, -- APPLICATION_NUMBER
			'001', -- ADDRESS_CATEGORY: 001 - standard, 012 - work
			v_merchant_email, -- EMAIL_ADDR
			v_record_date, -- RECORD_DATE
			v_merchant_street, -- ADDR_LINE_1
			v_post_code, -- POST_CODE
			v_merchant_city, -- ADDR_CLIENT_CITY
			v_merchant_country, -- CLIENT_COUNTRY
			v_audit_trail, -- AUDIT_TRAIL
			v_record_type, -- RECORD_TYPE
			v_merchant_short_name, -- CONTACT_NAME
			'000', -- DELIVERY_METHOD
			v_record_date, -- EFFECTIVE_DATE
			'000', -- GROUP_SPECIFIC
			'0' -- LOCKING_COUNTER
			);

	   	DBMS_OUTPUT.put_line('CIS_APPLICATION_ADDR inserted: ');

		--8. [CIS_APPLICATION_SERVICES]
		if (v_client_level = v_client_level_member) then  -- Only apply service for client_level, group & sub-group does not need services assigned
			FOR curServices in (select distinct service_id from cbr_assigned_services where institution_number = v_institution_number and service_contract_id = v_service_contract_id and service_category = v_service_category
				-- and service_availability = '001'
				)
			LOOP
				v_service_id := curServices.service_id;
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
					v_record_type, -- RECORD_TYPE
					v_merchant_tariff, -- CLIENT_TARIFF
					'0', -- LOCKING_COUNTER
					'99991231', -- REVIEW_DATE
					'99991231', -- EXPIRY_DATE
					v_record_date -- EFFECTIVE_DATE
					);
				 DBMS_OUTPUT.put_line('CIS_APPLICATION_SERVICES inserted: ' || v_service_id);
			END LOOP;
		END IF;

		-- 5. INSERT [CIS_APPLICATION_ACCT_TYPE]
		FOR curAccountTypes in (select distinct account_type_id from CBR_CONTRACT_ACCT_TYPES where institution_number = v_institution_number and service_contract_id = v_service_contract_id and acct_currency = v_currency)
		LOOP
			if (v_account_type_ids is null or v_account_type_ids = '' or v_account_type_ids = '*') then
				dbms_output.put_line('ok');
			else
				IF (INSTR(',' || v_account_type_ids || ',', ',' || curAccountTypes.account_type_id || ',') <= 0) then  -- skip item not in selected list
					dbms_output.put_line('skip ' || curAccountTypes.account_type_id);
					CONTINUE;
				END IF;
			end if;

			v_account_type_id := curAccountTypes.account_type_id;

	         --If net settlement only fill account_type_id = 012. If gross settlement then must fill both 007 and 112
			if (v_account_type_id = '012' or v_settlement_method ='523') then
				v_BANK_CLEARING_NUMBER1 := v_BANK_CLEARING_NUMBER;
				v_COUNTER_BANK_ACCOUNT1 := v_COUNTER_BANK_ACCOUNT;
				v_PAYMENT_REFERENCE :=  v_account_type_id || v_client_number || v_currency_iso;
				v_CORRESP_BANK_NUMBER := v_account_type_id || v_client_number;
				v_funding_bank1 := v_funding_bank;

				SELECT TYPE_ID INTO v_account_type_name FROM BWT_ACCOUNT_TYPE_ID WHERE INSTITUTION_NUMBER = v_institution_number AND Index_field = v_account_type_id AND Language = 'USA';
			else
				v_BANK_CLEARING_NUMBER1 := '';
				v_COUNTER_BANK_ACCOUNT1 := '';
				v_PAYMENT_REFERENCE := '1';
				v_CORRESP_BANK_NUMBER := '';
				v_funding_bank1 := '';

				v_account_type_name := '';
			end if;


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
				v_record_type, -- RECORD_TYPE
				v_record_date, -- RECORD_DATE
				v_billing_level, -- BILLING_LEVEL
				v_audit_trail, -- AUDIT_TRAIL
				'001', -- STATEMENT_GENERATION
				'900', -- STATEMENT_TYPE
				'0', -- LOCKING_COUNTER
				v_merchant_country -- RECEIVER_COUNTRY_CODE
				);
                DBMS_OUTPUT.put_line('CIS_APPLICATION_ACCT_TYPE inserted: ' || v_account_type_id || ' ' || v_account_type_name);

			-- [CIS_APPLICATION_MANDATE]

			if (v_client_level = v_client_level_group) then
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
            end if;
		END LOOP;

	   	if (v_is_broker = false and v_client_level = v_client_level_member) then -- only insert account type if not onboarding broker
			-- 6. INSERT [CIS_APPL_TERMINAL_INPUT]
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
				v_eod_indicator, -- EOD_INDICATOR
				v_currency, -- TERMINAL_CURRENCY
				'000', -- POS_FEE_TYPE
				v_audit_trail, -- AUDIT_TRAIL
				'0' -- LOCKING_COUNTER
				);
		    DBMS_OUTPUT.put_line('CIS_APPL_TERMINAL_INPUT inserted: ');

		    --9. INSERT [CIS_APPL_CLIENT_TAX_STATUS]
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

		END IF;

		COMMIT;

		--10. run soa process to board merchant ?
		if (v_run_soa_process_id = 'Y' or v_run_soa_process_id = '1' or v_run_soa_process_id = 'y' or v_run_soa_process_id = '467') then
			SELECT BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(v_institution_number, '467', v_record_date) INTO v_params FROM DUAL;
			BW_process_control.run_process(v_institution_number, '467', v_params, v_user_id, v_station_number, v_audit_trail, 'v1','', P_OUT, '001');
			DBMS_OUTPUT.put_line('FINISHED SOA PROCESS - 467');
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
select * from CIS_APPLICATION_DETAIL where institution_number = '00002001' and application_status in (002) order by record_date desc ;
