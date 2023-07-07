-- update CAS_CYCLE_BOOK_BALANCE set date_cycle_start = '20230401', date_cycle_end = '20230430' where institution_number = '00002001' and processing_status = '004' and date_cycle_start < '20230401';
-- update CBR_BILLING_CYCLE set current_cycle_start = '20230401', current_cycle_end = '20230430' where institution_number = '00002001' and cycle_status = '004';
update CAS_CYCLE_BOOK_BALANCE set begin_balance = 0, current_balance = 0, dr_balance_cash=0, dr_balance_retail=0, dr_balance_interest=0, dr_balance_charges=0,cr_balance_payments=0,
cr_balance_refunds=0,
number_tran_chrg_dr=0,
number_tran_chrg_cr=0,
cr_balance_interest=0
where institution_number = '00002001' and processing_status = '004';
commit;
