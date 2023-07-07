SET SERVEROUTPUT ON;
/
BEGIN
   T3MP_CREATE_AUTH(
     p_institution_number => '&institution_number',
     p_client_number => '&client_number',
     p_card_number => '&card_number__4035853306200869',
     p_currency => '&currency__978',
     p_audit_trail => '&audit_trail__MANUAL',
     p_run_soa_process => '&run_soa_process__N_Y'
	);
END;
/
select * from COS_BPR_DATA where institution_number = '&institution_number' and hostname = 'RS2\' || '&audit_trail__MANUAL' order by bpr_log_id desc;
