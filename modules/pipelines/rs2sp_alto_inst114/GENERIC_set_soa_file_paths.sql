update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\trn_in', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\trn_in'
where institution_number = '00000114'
and process_name = '000609';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\acquirer_reports', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\acquirer_reports'
where institution_number = '00000114'
and process_name = '000080';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\acquirer_reports', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\acquirer_reports'
where institution_number = '00000114'
and process_name = '000011';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\mc_out', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\mc_out'
where institution_number = '00000114'
and process_name = '000362';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\visa_out', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\visa_out'
where institution_number = '00000114'
and process_name = '000029';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\payment_fee', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\payment_fee'
where institution_number = '00000114'
and process_name = '000231';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\statements', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\out\statements'
where institution_number = '00000114'
and process_name = '001228';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\visa', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\visa'
where institution_number = '00000114'
and process_name = '000187';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\visa\chargeback', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\visa\chargeback'
where institution_number = '00000114'
and process_name = '000003';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\mc', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\mc'
where institution_number = '00000114'
and process_name = '000366';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\mc\chargeback', file_move_path = 'D:\Bankworks\alto_test_pipeline\Inst114_ALTO\in\mc\chargeback'
where institution_number = '00000114'
and process_name = '000559';
commit;
