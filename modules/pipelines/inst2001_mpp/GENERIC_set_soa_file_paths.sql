update sys_process_user_setup
set file_path = 'D:\Bankworks\&2\in\trn_in', file_move_path = 'D:\Bankworks\&2\in\trn_in'
where institution_number = '&1'
and process_name = '000609';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\&2\out\acquirer_reports', file_move_path = 'D:\Bankworks\&2\out\acquirer_reports'
where institution_number = '&1'
and process_name = '000080';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\&2\out\ipm', file_move_path = 'D:\Bankworks\&2\out\ipm'
where institution_number = '&1'
and process_name = '000362';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\&2\out\visa', file_move_path = 'D:\Bankworks\&2\out\visa'
where institution_number = '&1'
and process_name = '000029';
--
update sys_process_user_setup
set file_path = 'D:\Bankworks\&2\out\payment_fee', file_move_path = 'D:\Bankworks\&2\out\payment_fee'
where institution_number = '&1'
and process_name = '000231';
commit;
