CREATE OR REPLACE PROCEDURE T3MP_ONBOARD_MERCHANT(
	p_institution_number in VARCHAR2,
	p_merchant_name in VARCHAR2 DEFAULT 'Merchant',

  	p_client_type in VARCHAR2 DEFAULT '002', -- 002: acq, 009: broker, 013: agent, 014: isv
  	p_service_contract_id in VARCHAR2 DEFAULT '111',
	p_account_type_ids in VARCHAR2 DEFAULT '007,012',
  	p_service_ids in VARCHAR2 DEFAULT NULL,

	p_client_level in VARCHAR2 DEFAULT '001', -- 001: member, 002: sub-group, 003: group
  	p_billing_level in VARCHAR2 DEFAULT '001',
  	p_application_number in VARCHAR2 DEFAULT NULL,
  	p_parent_application_number in VARCHAR2 DEFAULT NULL,
  	p_client_number  in VARCHAR2 DEFAULT NULL,
	p_group_number in VARCHAR2 DEFAULT NULL,
    p_parent_client_number in VARCHAR2 DEFAULT NULL,

	p_settlement_method in VARCHAR2 DEFAULT '510',
	p_posting_method in VARCHAR2 DEFAULT '001',
	p_client_tariff in VARCHAR2 DEFAULT '000502',

  	p_merchant_tariff in VARCHAR2 DEFAULT '000000',
  	p_fx_tariff in VARCHAR2 DEFAULT '000001',
	p_condition_set in VARCHAR2 DEFAULT '000001',

	p_currency in VARCHAR2 DEFAULT '978',
	p_merchant_country in VARCHAR2 DEFAULT '280',
	p_post_code VARCHAR2 DEFAULT '63263',
	p_merchant_email in VARCHAR2 DEFAULT 'hung.ho@rs2.com',

	p_risk_rule_group in VARCHAR2 DEFAULT '001',
	p_risk_group in VARCHAR2 DEFAULT '001',
	p_entity_id in VARCHAR2 DEFAULT '001',
	p_record_type in VARCHAR2 DEFAULT '003',

	p_BANK_CLEARING_NUMBER in VARCHAR2 DEFAULT 'BCN123456',
	p_COUNTER_BANK_ACCOUNT in VARCHAR2 DEFAULT 'DE12345678910',
	p_eod_indicator in varchar2 DEFAULT '002',

	p_client_tax_record_id in VARCHAR2 DEFAULT NULL,
  	p_terminal_id in VARCHAR2 DEFAULT NULL,

	p_record_date in VARCHAR2 DEFAULT NULL,
	p_audit_trail in VARCHAR2 DEFAULT 'MANUAL',
	p_application_status in VARCHAR2 DEFAULT '002',
	p_run_soa_process in VARCHAR2 DEFAULT 'Y'
	)
IS
	v_application_number varchar(12) := p_application_number;
	v_client_number  varchar(8) := p_client_number;
	v_group_number varchar(8) := p_group_number;
	v_parent_client_number varchar(8) := p_parent_client_number;

	v_settlement_method VARCHAR2(3):= p_settlement_method;
	v_posting_method VARCHAR2(3):= p_posting_method;
	v_client_tariff VARCHAR2(6):= p_client_tariff;

	v_client_tax_record_id varchar(15) := p_client_tax_record_id;
	v_merchant_name varchar(20) := p_merchant_name;

	v_record_date varchar2(8) := p_record_date;

	v_terminal_id varchar2(15) := p_terminal_id;
	v_our_reference varchar2(20) := '0000000' || v_client_number;
	v_account_type_ids varchar2(40) := p_account_type_ids;
	v_is_broker boolean;
	v_service_category varchar2(3);
	v_contract_category varchar2(3);
	v_contract_type varchar2(3);

	v_service_ids varchar(30) := case when p_service_ids is null then '' else p_service_ids end;

	v_funding_bank varchar(12) := case p_entity_id when '001' then p_institution_number when '002' then '90000102' else p_institution_number end;

	v_PAYMENT_REFERENCE varchar2(24);
	v_trans_date VARCHAR2(8);
	v_CORRESP_BANK_NUMBER varchar2(35);
	v_funding_bank1 varchar2(12);
	p_BANK_CLEARING_NUMBER1 varchar2(35);
	p_COUNTER_BANK_ACCOUNT1 varchar2(16);
    p_currency_iso varchar(3);

	v_account_type_id varchar(3);
	v_service_id varchar(3);
	v_account_type_name VARCHAR2(20);

	p_client_level_member VARCHAR2(3) := '001';
	p_client_level_group VARCHAR2(3) := '002';
	p_client_level_sub_group VARCHAR2(3) := '003';

	v_user_id VARCHAR2(6) := '999999';
	v_station_number VARCHAR2(3) := '129';

	V_INIT_RETURN PLS_INTEGER;
	v_params varchar2(100);
	v_errors VARCHAR2(520);
	v_tmp varchar(100);
BEGIN

	-- CHECK DATA & AUTO-CACULATED PARAMS
	  if (p_client_type = '002') then
	    v_service_category := '002';
	    v_contract_type := '002';
	    v_contract_category := '003';
	    v_is_broker := false;
	  else
	    v_service_category := '016';
	    v_contract_type := '009';
	    v_contract_category := '003';
	    v_is_broker := true;
	  end if;

	if (p_settlement_method = 'net' or p_settlement_method = '523' or p_settlement_method = '' or p_settlement_method is null) then
	   	v_settlement_method := '523';
		v_posting_method := '002';
	else
	    v_settlement_method := '510';
		v_posting_method := '001';
	end if;

	if (p_client_tariff = 'blended' or p_client_tariff = '000502' or p_client_tariff = '' or p_client_tariff is null) then
	   	v_client_tariff := '000502';
	else
	    v_client_tariff := '000509';
	end if;

	BW_PRC_RES.INITGLOBALVARS (p_institution_number, v_station_number, v_user_id, V_INIT_RETURN);

	if (v_record_date is null or v_record_date = '') then
		SELECT POSTING_DATE INTO v_record_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = p_institution_number AND STATION_NUMBER = v_station_number; -- posting date
	end if;

	if (v_application_number is null or v_application_number = '') then
		v_application_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
	end if;

	if (v_client_number is null or v_client_number = '') then
		v_client_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('010',1);
		v_our_reference := '0000000' || v_client_number;
	end if;

	if (v_group_number is null or v_group_number = '') then
	    v_group_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('018',1);
	end if;

	if (v_client_tax_record_id is null or v_client_tax_record_id = '') then
	    v_client_tax_record_id := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1);
	end if;

	if (v_terminal_id is null or v_terminal_id = '') then
	    v_terminal_id := 'T'||substr(v_client_number,-7,7);
	end if;

	SELECT swift_code INTO p_currency_iso FROM BWT_CURRENCY WHERE INSTITUTION_NUMBER = p_institution_number AND ISO_CODE = p_currency AND Language = 'USA';

	if (p_parent_application_number is null or p_parent_application_number = '' or p_parent_application_number = '0000000000') then
		DBMS_OUTPUT.put_line('');
	else
		SELECT CLIENT_NUMBER, GROUP_NUMBER INTO v_parent_client_number, v_group_number FROM CIS_APPLICATION_DETAIL WHERE INSTITUTION_NUMBER = p_institution_number AND service_contract_id = p_service_contract_id AND APPLICATION_NUMBER = p_parent_application_number;
	end if;


	-- INSERT [CIS_APPLICATION_DETAIL]
	DBMS_OUTPUT.put_line('CIS_APPLICATION_DETAIL: ');
	INSERT INTO CIS_APPLICATION_DETAIL
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,RECORD_TYPE,CLIENT_NUMBER,GROUP_NUMBER, APPLICATION_STATUS,CONTACT_NAME,VAT_REG_NUMBER,REGISTRATION_NUMBER,CLIENT_TYPE,RESIDENCE_STATUS,CLIENT_LANGUAGE,INSTITUTION_ACCT_OFFICER,PROVIDER_ACCT_OFFICER,CLIENT_BRANCH,SHORT_NAME,COMPANY_NAME,LEGAL_FORM,TRADE_NAME,BUSINESS_CLASS,OUR_REFERENCE,SERVICE_CONTRACT_ID,CONDITION_SET,LIMIT_CURRENCY,FLOOR_LIMIT,CLIENT_LEVEL,SETTLEMENT_METHOD,POSTING_METHOD,PARENT_APPL_NUMBER,CLIENT_ORGANIZATION,LAST_AMENDMENT_DATE,AUDIT_TRAIL,CLIENT_COUNTRY,CLIENT_CITY,CONTRACT_CATEGORY,MERCHANT_STREET,CLIENT_REGION,ECOMMERCE_INDICATOR,RISK_GROUP, RISK_RULE_GROUP_ID, LOCKING_COUNTER,ACCUMULATOR_SCHEME,APPL_PROC_INVOKED,CLIENT_SCHEME,TRANSFER_METHOD,CLIENT_STATUS,MERCHANT_TRAN_TARIFF,CONTRACT_REGION,FX_TARIFF,ENTITY_ID)
	VALUES
		(p_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		v_record_date, -- RECORD_DATE
		p_record_type, -- RECORD_TYPE
		v_client_number, -- CLIENT_NUMBER
		v_group_number,
		p_application_status, -- APPLICATION_STATUS
		v_merchant_name, -- CONTACT_NAME
		'12345565222411', -- VAT_REG_NUMBER
		'11111111111111', -- REGISTRATION_NUMBER
		p_client_type, -- CLIENT_TYPE
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
		p_service_contract_id, -- SERVICE_CONTRACT_ID
		p_condition_set, -- CONDITION_SET
		p_currency, -- LIMIT_CURRENCY
		'0.', -- FLOOR_LIMIT
		p_client_level, -- CLIENT_LEVEL
		v_settlement_method, -- SETTLEMENT_METHOD
		v_posting_method, -- POSTING_METHOD
		p_parent_application_number, -- PARENT_APPL_NUMBER
		v_parent_client_number, -- CLIENT_ORGANIZATION
		v_record_date, -- LAST_AMENDMENT_DATE
		p_audit_trail, -- AUDIT_TRAIL
		p_merchant_country, -- CLIENT_COUNTRY
		'Frankfurt', -- CLIENT_CITY

		v_contract_category, -- CONTRACT_CATEGORY
		'Frankfurt', -- MERCHANT_STREET
		'000', -- CLIENT_REGION
		'999', -- ECOMMERCE_INDICATOR
		p_risk_group, -- RISK_GROUP
		p_risk_rule_group, -- RISK RULE GROUP
		'0', -- LOCKING_COUNTER
		'000', -- ACCUMULATOR_SCHEME
		'0', -- APPL_PROC_INVOKED
		'000000', -- CLIENT_SCHEME
		'000001', -- TRANSFER_METHOD
		'001', -- CLIENT_STATUS
		p_merchant_tariff, -- MERCHANT_TRAN_TARIFF
		'999', -- CONTRACT_REGION
		p_fx_tariff, -- FX_TARIFF
		p_entity_id -- ENTITY_ID
		);
   	DBMS_OUTPUT.put_line('...application_number: ' || v_application_number || ' client_number: ' || v_client_number || ' GROUP_NUMBER: ' || v_group_number || ' Service contract: ' || p_service_contract_id || ' Merchant name: ' || v_merchant_name);

       -- [CIS_APPLICATION_ADDR]
	DBMS_OUTPUT.put_line('CIS_APPLICATION_ADDR: ');
	INSERT INTO CIS_APPLICATION_ADDR
		 (INSTITUTION_NUMBER,APPLICATION_NUMBER,ADDRESS_CATEGORY,EMAIL_ADDR,RECORD_DATE,ADDR_LINE_1,POST_CODE,ADDR_CLIENT_CITY,CLIENT_COUNTRY,AUDIT_TRAIL,RECORD_TYPE,CONTACT_NAME,DELIVERY_METHOD,EFFECTIVE_DATE,GROUP_SPECIFIC,LOCKING_COUNTER)
	VALUES
		(p_institution_number, -- INSTITUTION_NUMBER
		v_application_number, -- APPLICATION_NUMBER
		'001', -- ADDRESS_CATEGORY: 001 - standard, 012 - work
		p_merchant_email, -- EMAIL_ADDR
		v_record_date, -- RECORD_DATE
		'Frankfurt', -- ADDR_LINE_1
		p_post_code, -- POST_CODE
		'Frankfurt', -- ADDR_CLIENT_CITY
		p_merchant_country, -- CLIENT_COUNTRY
		p_audit_trail, -- AUDIT_TRAIL
		p_record_type, -- RECORD_TYPE
		v_merchant_name, -- CONTACT_NAME
		'000', -- DELIVERY_METHOD
		v_record_date, -- EFFECTIVE_DATE
		'000', -- GROUP_SPECIFIC
		'0' -- LOCKING_COUNTER
		);
    DBMS_OUTPUT.put_line('...' || p_merchant_country || ' - ' || v_merchant_name);
	-- [CIS_APPLICATION_SERVICES]
	if (p_client_level = p_client_level_member) then  -- Only apply service for client_level, group & sub-group does not need services assigned
		DBMS_OUTPUT.put_line('CIS_APPLICATION_SERVICES: ');
		FOR curServices in (select distinct service_id from cbr_assigned_services where institution_number = p_institution_number and service_contract_id = p_service_contract_id and service_category = v_service_category
			-- and service_availability = '001'
			)
		LOOP
			if (v_service_ids is null or v_service_ids = '' or v_service_ids = '*') then
				NULL;
			else
				IF (INSTR(',' || v_service_ids || ',', ',' || curServices.service_id || ',') <= 0) then  -- skip item not in selected list
					CONTINUE;
				END IF;
			end if;

			v_service_id := curServices.service_id;
			INSERT INTO CIS_APPLICATION_SERVICES
			 	(INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,AUDIT_TRAIL,SERVICE_CONTRACT_ID,SERVICE_ID,SERVICE_ASSIGNED,RECORD_TYPE,CLIENT_TARIFF,LOCKING_COUNTER,REVIEW_DATE,EXPIRY_DATE,EFFECTIVE_DATE)
			VALUES
				(p_institution_number, -- INSTITUTION_NUMBER
				v_application_number, -- APPLICATION_NUMBER
				v_record_date, -- RECORD_DATE
				p_audit_trail, -- AUDIT_TRAIL
				p_service_contract_id, -- SERVICE_CONTRACT_ID
				v_service_id, -- SERVICE_ID
				'001', -- SERVICE_ASSIGNED
				p_record_type, -- RECORD_TYPE
				v_client_tariff, -- CLIENT_TARIFF
				'0', -- LOCKING_COUNTER
				'99991231', -- REVIEW_DATE
				'99991231', -- EXPIRY_DATE
				v_record_date -- EFFECTIVE_DATE
				);
			 DBMS_OUTPUT.put_line('...service_id: ' || v_service_id);
		END LOOP;
	END IF;

	-- INSERT [CIS_APPLICATION_ACCT_TYPE]
	DBMS_OUTPUT.put_line('CIS_APPLICATION_ACCT_TYPE: ');
	IF (v_settlement_method = '523' AND INSTR(',' || v_account_type_ids || ',', ',007,') <= 0) then  -- if GROSS then must have account_type_id 007 (fee)
		v_account_type_ids := v_account_type_ids || ',007';
	END IF;
	FOR curAccountTypes in (select distinct account_type_id from CBR_CONTRACT_ACCT_TYPES where institution_number = p_institution_number and service_contract_id = p_service_contract_id and acct_currency = p_currency)
	LOOP
		if (v_account_type_ids is null or v_account_type_ids = '' or v_account_type_ids = '*') then
			 null;
		else
			IF (INSTR(',' || v_account_type_ids || ',', ',' || curAccountTypes.account_type_id || ',') <= 0) then  -- skip item not in selected list
				-- dbms_output.put_line('skip ' || curAccountTypes.account_type_id);
				CONTINUE;
			END IF;
		end if;

		v_account_type_id := curAccountTypes.account_type_id;

    --If net settlement only fill account_type_id = 012. If gross settlement then must fill both 007 and 112
		if (v_account_type_id = '012' or v_settlement_method ='523') then
			p_BANK_CLEARING_NUMBER1 := p_BANK_CLEARING_NUMBER;
			p_COUNTER_BANK_ACCOUNT1 := p_COUNTER_BANK_ACCOUNT;
			v_PAYMENT_REFERENCE :=  v_account_type_id || v_client_number || p_currency_iso;
			v_CORRESP_BANK_NUMBER := v_account_type_id || v_client_number;
			v_funding_bank1 := v_funding_bank;

			SELECT TYPE_ID INTO v_account_type_name FROM BWT_ACCOUNT_TYPE_ID WHERE INSTITUTION_NUMBER = p_institution_number AND Index_field = v_account_type_id AND Language = 'USA';
		else
			p_BANK_CLEARING_NUMBER1 := '';
			p_COUNTER_BANK_ACCOUNT1 := '';
			v_PAYMENT_REFERENCE := '1';
			v_CORRESP_BANK_NUMBER := '';
			v_funding_bank1 := '';

			v_account_type_name := '';
		end if;

    if (p_billing_level = '000') then
      p_BANK_CLEARING_NUMBER1 := '';
      p_COUNTER_BANK_ACCOUNT1 := '';
	end if;
		INSERT INTO CIS_APPLICATION_ACCT_TYPE
		 	(INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,COUNTER_BANK_ACCOUNT_NAME,FUNDING_BANK,BANK_CLEARING_NUMBER,COUNTER_BANK_ACCOUNT,PAYMENT_REFERENCE,CORRESP_BANK_NUMBER,ACCT_CURRENCY,RECORD_TYPE,RECORD_DATE,BILLING_LEVEL,AUDIT_TRAIL,STATEMENT_GENERATION,STATEMENT_TYPE,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE)
		VALUES
			(p_institution_number, -- INSTITUTION_NUMBER
			v_application_number, -- APPLICATION_NUMBER
			v_account_type_id, -- ACCOUNT_TYPE_ID
			v_account_type_name,
			v_funding_bank1,
			p_BANK_CLEARING_NUMBER1,
			p_COUNTER_BANK_ACCOUNT1,
			v_PAYMENT_REFERENCE,
			v_CORRESP_BANK_NUMBER,
			p_currency, -- ACCT_CURRENCY
			p_record_type, -- RECORD_TYPE
			v_record_date, -- RECORD_DATE
			p_billing_level, -- BILLING_LEVEL
			p_audit_trail, -- AUDIT_TRAIL
			'001', -- STATEMENT_GENERATION
			'900', -- STATEMENT_TYPE
			'0', -- LOCKING_COUNTER
			p_merchant_country -- RECEIVER_COUNTRY_CODE
			);

			DBMS_OUTPUT.put_line('...account_type_id: ' || v_account_type_id || ' ' || v_account_type_name);

		-- [CIS_APPLICATION_MANDATE]
		if (p_client_level = p_client_level_group) then
			INSERT INTO CIS_APPLICATION_MANDATE
			 	(INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,ACCT_CURRENCY,RECORD_DATE,AUDIT_TRAIL,UNIQUE_MANDATE_REF,LOCKING_COUNTER,INSTRUMENT_CODE,FIRST_OCCURRENCE,DATE_OF_SIGNATURE)
			VALUES
				(p_institution_number, -- INSTITUTION_NUMBER
				v_application_number, -- APPLICATION_NUMBER
				v_account_type_id, -- ACCOUNT_TYPE_ID
				p_currency, -- ACCT_CURRENCY
				v_record_date, -- RECORD_DATE
				p_audit_trail, -- AUDIT_TRAIL
				v_PAYMENT_REFERENCE, -- UNIQUE_MANDATE_REF
				'0', -- LOCKING_COUNTER
				'001', -- INSTRUMENT_CODE
				'000', -- FIRST_OCCURRENCE
				v_record_date -- DATE_OF_SIGNATURE
				);
           end if;
	END LOOP;

   	if (v_is_broker = false and p_client_level = p_client_level_member) then -- only insert account type if not onboarding broker
		-- 6. INSERT [CIS_APPL_TERMINAL_INPUT]
		DBMS_OUTPUT.put_line('CIS_APPL_TERMINAL_INPUT: ');
		INSERT INTO CIS_APPL_TERMINAL_INPUT
			 (APPLICATION_NUMBER,TERMINAL_ID,INSTITUTION_NUMBER,MERCHANT_ID,RECORD_DATE,TERMINAL_STATUS,DEVICE_CAPABILITY,EOD_INDICATOR,TERMINAL_CURRENCY,POS_FEE_TYPE,AUDIT_TRAIL,LOCKING_COUNTER)
		VALUES
			(v_application_number, -- APPLICATION_NUMBER
			v_terminal_id, -- TERMINAL_ID
			p_institution_number, -- INSTITUTION_NUMBER
			v_our_reference, -- MERCHANT_ID
			v_record_date, -- RECORD_DATE
			'001', -- TERMINAL_STATUS
			'006', -- DEVICE_CAPABILITY
			p_eod_indicator, -- EOD_INDICATOR
			p_currency, -- TERMINAL_CURRENCY
			'000', -- POS_FEE_TYPE
			p_audit_trail, -- AUDIT_TRAIL
			'0' -- LOCKING_COUNTER
			);
        DBMS_OUTPUT.put_line('...terminal_id: ' || v_terminal_id || ' eod:' || p_eod_indicator);

	    --9. INSERT [CIS_APPL_CLIENT_TAX_STATUS]
		DBMS_OUTPUT.put_line('CIS_APPL_CLIENT_TAX_STATUS: ');
		INSERT INTO CIS_APPL_CLIENT_TAX_STATUS
			 (INSTITUTION_NUMBER,RECORD_DATE,APPLICATION_NUMBER,TAX_TYPE,EFFECTIVE_DATE,EXPIRY_DATE,TAX_STATUS,RECORD_ID_NUMBER,AUDIT_TRAIL,LOCKING_COUNTER)
		VALUES
			(p_institution_number, -- INSTITUTION_NUMBER
			v_record_date, -- RECORD_DATE
			v_application_number, -- APPLICATION_NUMBER
			'003', -- TAX_TYPE
			v_record_date, -- EFFECTIVE_DATE
			'99991231', -- EXPIRY_DATE
			'001', -- TAX_STATUS    ---001: Subject to VAT, 002: Exempt for VAT
			v_client_tax_record_id, -- RECORD_ID_NUMBER
			p_audit_trail, -- AUDIT_TRAIL
			'0' -- LOCKING_COUNTER
			);
        DBMS_OUTPUT.put_line('...client_tax_record_id: ' || v_client_tax_record_id);
	END IF;

	COMMIT;

	dbms_output.put_line('OUTPUT:');
 	dbms_output.put_line('- Client number: ' || v_client_number || '. Group number:' || v_group_number || '. Application:' || v_application_number);

 	T3MP_run_soa_process('467', p_institution_number, p_run_soa_process);

EXCEPTION
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('> DATA NOT FOUND: ' || sqlerrm || ' ' || dbms_utility.format_error_backtrace);rollback;
	when others then
     	dbms_output.put_line('> ERROR: ' || sqlerrm || ' ' || dbms_utility.format_error_backtrace);rollback;
END;
