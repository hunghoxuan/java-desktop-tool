SET SERVEROUTPUT ON;
/
BEGIN
	 T3MP_CREATE_BATCH_INPUT(
	     p_institution_number => '&1',
	     p_client_number => '&2',
	     p_card_number => '&3',
	     p_currency => '&4',
		 p_audit_trail => 'PIPELINE',
		 p_run_soa_process => 'N'
	 	);
END;
/
