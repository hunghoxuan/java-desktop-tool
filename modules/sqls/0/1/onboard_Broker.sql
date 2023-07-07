-- @REQUIRE: T3MP_ONBOARD_MERCHANT.sql
/
BEGIN
	-- Create merchant
 	T3MP_ONBOARD_MERCHANT(
	  	p_institution_number => '&institution_number',
		p_merchant_name => '&merchant_name__Broker',
	    p_client_type => '&client_type__009_broker_013_agent_014_isv',
		p_service_contract_id => '&service_contract_id__123_partner',
		p_account_type_ids => '&account_type_id__996',
		p_service_ids => '',
        
		p_client_level => '&client_level__001_member_002_group_003_subgroup', 
		p_billing_level => '&billing_level__001',
		p_parent_client_number => '&parent_application_number__',
		p_client_number => '&client_number__',
		p_group_number => '&group_number__',
		p_parent_application_number => '',

		p_settlement_method => '996',
		p_posting_method => '999',
		p_client_tariff => '000000',

		p_merchant_tariff => '000000',
		p_fx_tariff => '000001',
		p_condition_set => '021000', 

		p_currency => '&currency__978',
		p_merchant_country => '&country__280',
		p_post_code => '&postcode__63523',
		p_merchant_email => '&merchant_email__hung.ho@rs2.com',

		p_risk_rule_group => '001',
		p_risk_group => '001',
		p_entity_id => '001',
		p_record_type => '003',

		p_BANK_CLEARING_NUMBER => 'BCN123456',
		p_COUNTER_BANK_ACCOUNT => 'DE12345678910',
		p_eod_indicator => '002',

		p_client_tax_record_id => '',
		p_terminal_id => ''

		p_record_date => '&record_date',
		p_audit_trail => '&audit_trail__MANUAL',
		p_application_status => '&application_status__002',
		p_run_soa_process => '&run_soa_process__N_Y' 
     );
END;
/
select * from CIS_APPLICATION_DETAIL where application_status IN ('&application_status__002') and institution_number = '&institution_number' and audit_trail = '&audit_trail__MANUAL' order by record_date desc, application_number desc;
