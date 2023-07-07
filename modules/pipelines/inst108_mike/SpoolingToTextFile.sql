
spool /CI_Pipeline/Profiles/BNZ_DEV_INST_108_MIKE/qa_training/Builds/&1/SentFolder/myexcel.txt

select '00000003' as institution_number, '1010000030030009991028570051000010020000000021200502' as key, '2.5' as fee_percent, '0' as fee_base, '0' as fee_minimum, '0' as fee_maximum, '800' as pl_ind_inward, '001' as fee_mode, '99991231' as expiry_date, '000' as pl_ind_outward, '207' as value_day_reference, '001' as math_operator, '25' as number_of_days, '002' as posting_method, '000' as source_tran_type from dual
union
select '00000003' as institution_number, '1010000045010009992008570051000010020000000021200502' as key, '3.1' as fee_percent, '0' as fee_base, '0' as fee_minimum, '0' as fee_maximum, '800' as pl_ind_inward, '001' as fee_mode, '99991231' as expiry_date, '000' as pl_ind_outward, '207' as value_day_reference, '001' as math_operator, '25' as number_of_days, '002' as posting_method, '000' as source_tran_type from dual
union
select '00000003' as institution_number, '1010000045010009992018570051000010020000000021200502' as key, '3.2' as fee_percent, '0' as fee_base, '0' as fee_minimum, '0' as fee_maximum, '800' as pl_ind_inward, '001' as fee_mode, '99991231' as expiry_date, '000' as pl_ind_outward, '207' as value_day_reference, '001' as math_operator, '25' as number_of_days, '002' as posting_method, '000' as source_tran_type from dual
union
select '00000003' as institution_number, '1010001411130009993008570051000010020000000021200502' as key, '3.3' as fee_percent, '0' as fee_base, '0' as fee_minimum, '0' as fee_maximum, '800' as pl_ind_inward, '001' as fee_mode, '99991231' as expiry_date, '000' as pl_ind_outward, '207' as value_day_reference, '001' as math_operator, '25' as number_of_days, '002' as posting_method, '000' as source_tran_type from dual
union
select '00000003' as institution_number, '101' ||(select index_field from bwt_account_condition_set where institution_number = '00000003' and condition_set = 'New tariff +1' and groups = 'B')|| '0030009993018570051000010020000000021200502' as key, '3.4' as fee_percent, '0' as fee_base, '0' as fee_minimum, '0' as fee_maximum, '800' as pl_ind_inward, '001' as fee_mode, '99991231' as expiry_date, '000' as pl_ind_outward, '207' as value_day_reference, '001' as math_operator, '25' as number_of_days, '002' as posting_method, '000' as source_tran_type from dual
;

spool off
