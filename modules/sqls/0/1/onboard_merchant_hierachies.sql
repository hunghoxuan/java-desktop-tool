-- @REQUIRE: T3MP_ONBOARD_MERCHANT.sql
/
DECLARE
	v_institution_number varchar2(8) := '&institution_number';
	v_merchant_name varchar2(10) := '&merchant_name';
	v_client_type varchar2(3) := '002';
	v_service_contract_id varchar2(3) := '&service_contract_id__111';
	v_settlement_method varchar2(5) := '&settlement_method__net_gross';
	v_posting_method varchar2(3) := '';
	v_client_tariff varchar2(8) := '&client_tariff__blended_iccplusplus';
	v_currency varchar2(3) := '&country__978';
	v_country varchar2(3) := '&country__280';

	v_merchant_tariff varchar2(6) := '000000';
	v_fx_tariff varchar2(6) := '000001';
	v_condition_set varchar2(6) := '000001';
	v_audit_trail varchar2(20) := '&audit_trail';

	v_billing_level_group varchar2(3) := '000';
	v_billing_level_subgroup varchar2(3) := '001';
    v_billing_level_member varchar2(3) := '000';

	v_application_number_group varchar2(15);
	v_application_number_subgroup varchar(15);
	v_application_number_member varchar(15);

	v_client_number_group  varchar(8);
	v_client_number_subgroup  varchar(8);
	v_client_number_member  varchar(8);

	v_group_number varchar(8);


	V_INIT_RETURN PLS_INTEGER;
BEGIN
	BW_PRC_RES.INITGLOBALVARS (v_institution_number, 129, 999999, V_INIT_RETURN);

    -- v_group_number := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('018',1);
    -- dbms_output.put_line('group number: ' || v_group_number);
	-- v_client_number_group := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('010',1);

	if (v_settlement_method = 'net' or v_settlement_method = '523' or v_settlement_method = '' or v_settlement_method is null) then
	   	v_settlement_method := '523';
		v_posting_method := '002';
	else
	    v_settlement_method := '510';
		v_posting_method := '001';
	end if;

	if (v_client_tariff = 'blended' or v_client_tariff = '000502' or v_client_tariff = '' or v_client_tariff is null) then
	   	v_client_tariff := '000502';
	else
	    v_client_tariff := '000509';
	end if;

	-- Create Group
 	v_application_number_group := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
 	T3MP_ONBOARD_MERCHANT(
		p_institution_number => v_institution_number,
		p_merchant_name => v_merchant_name || '_GROUP',
		p_client_type => v_client_type,
		p_service_contract_id => v_service_contract_id,
		p_client_level => '002',
		p_billing_level => v_billing_level_group,
		p_settlement_method => v_settlement_method,
		p_posting_method => v_posting_method,
		p_client_tariff => v_client_tariff,

		p_application_number => v_application_number_group,
		p_parent_application_number => '',
		p_group_number => v_group_number,

		p_merchant_tariff => v_merchant_tariff,
		p_fx_tariff => v_fx_tariff,
		p_condition_set => v_condition_set,
		p_risk_rule_group => '001',
		p_risk_group => '001',

		p_currency => v_currency, p_merchant_country => v_country, p_post_code => '63523', p_entity_id => '001', p_record_type => '003', p_eod_indicator => '002', p_application_status => '002', p_run_soa_process => 'Y', p_audit_trail => v_audit_trail
     );

     SELECT client_number into v_client_number_group FROM cis_application_detail where application_number = v_application_number_group and institution_number = v_institution_number and application_status = '004';
	 if (v_group_number is null or v_group_number = '') then
     	SELECT group_number into v_group_number FROM cis_application_detail where application_number = v_application_number_group and institution_number = v_institution_number and application_status = '004';
	 else
	    update cis_application_detail set group_number = v_group_number where application_number = v_application_number_group and institution_number = v_institution_number;
	    update cis_client_links set group_number = v_group_number where client_number = v_client_number_group and institution_number = v_institution_number;
	 end if;
	 dbms_output.put_line('GROUP MERCHANT CREATED SUCCESSFULLY: Group Client Number: ' || v_client_number_group || ', Group Number: ' || v_group_number);

      -- Create Sub Group
     v_application_number_subgroup := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
     T3MP_ONBOARD_MERCHANT(
		p_institution_number => v_institution_number,
		p_merchant_name => v_merchant_name || '_SUBGROUP',
		p_client_type => v_client_type,
		p_service_contract_id => v_service_contract_id,
		p_client_level => '003',
		p_billing_level => v_billing_level_subgroup,
		p_settlement_method => v_settlement_method,
		p_posting_method => v_posting_method,
		p_client_tariff => v_client_tariff,

		p_application_number => v_application_number_subgroup,
        p_parent_application_number => v_application_number_group,
        p_group_number => v_group_number,

		p_merchant_tariff => v_merchant_tariff,
		p_fx_tariff => v_fx_tariff,
		p_condition_set => v_condition_set,
		p_risk_rule_group => '001',
		p_risk_group => '001',

		p_currency => v_currency, p_merchant_country => v_country, p_post_code => '63523', p_entity_id => '001', p_record_type => '003', p_eod_indicator => '002', p_application_status => '002', p_run_soa_process => 'Y', p_audit_trail => v_audit_trail

     );

	SELECT client_number into v_client_number_subgroup FROM cis_application_detail where application_number = v_application_number_subgroup and institution_number = v_institution_number and application_status = '004';
     dbms_output.put_line('SUB-GROUP MERCHANT CREATED SUCCESSFULLY: SubGroup Client Number: ' || v_client_number_subgroup || ', Group Number: ' || v_group_number);

  	-- Create Member Client
  	v_application_number_member := BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1);
 	T3MP_ONBOARD_MERCHANT(
		p_institution_number => v_institution_number,
		p_merchant_name => v_merchant_name || '_CLIENT',
		p_client_type => v_client_type,
		p_service_contract_id => v_service_contract_id,
		p_client_level => '001',
		p_billing_level => v_billing_level_member,
		p_settlement_method => v_settlement_method,
		p_posting_method => v_posting_method,
		p_client_tariff => v_client_tariff,

		p_application_number => v_application_number_member,
        p_parent_application_number => v_application_number_subgroup,
        p_group_number => v_group_number,

		p_merchant_tariff => v_merchant_tariff,
		p_fx_tariff => v_fx_tariff,
		p_condition_set => v_condition_set,
		p_risk_rule_group => '001',
		p_risk_group => '001',

		p_currency => v_currency, p_merchant_country => v_country, p_post_code => '63523', p_entity_id => '001', p_record_type => '003', p_eod_indicator => '002', p_application_status => '002', p_run_soa_process => 'Y', p_audit_trail => v_audit_trail
     );

     SELECT client_number into v_client_number_member FROM cis_application_detail where application_number = v_application_number_member and institution_number = v_institution_number and application_status = '004';
     dbms_output.put_line('MEMBER MERCHANT CREATED SUCCESSFULLY: Member Client Number: ' || v_client_number_member || ', Group Number: ' || v_group_number);
EXCEPTION
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('> DATA NOT FOUND, GROUP NOT CREATED: ' || sqlerrm || ' ' || dbms_utility.format_error_backtrace);rollback;
END;
/
select * from CIS_APPLICATION_DETAIL where application_status IN ('&application_status__002', '008', '004') and institution_number = '&institution_number' and audit_trail = '&audit_trail' order by record_date desc, application_number desc;
