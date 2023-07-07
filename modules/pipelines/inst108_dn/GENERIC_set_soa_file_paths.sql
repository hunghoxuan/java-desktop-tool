update sys_process_user_setup
set file_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\in\trn_in', file_move_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\in\trn_in'
where institution_number = '00000108'
and process_name = '000609';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\acquirer_reports', file_move_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\acquirer_reports'
where institution_number = '00000108'
and process_name = '000080';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\mc_out', file_move_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\mc_out'
where institution_number = '00000108'
and process_name = '000362';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\visa_out', file_move_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\visa_out'
where institution_number = '00000108'
and process_name = '000029';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\payment_fee', file_move_path = 'D:\Bankworks\bnz_test_pipeline\Inst108_BNZ\out\payment_fee'
where institution_number = '00000108'
and process_name = '000231';
commit;
