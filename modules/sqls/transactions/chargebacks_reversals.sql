select * from cps_user_information where login_name = 'nextgen';

-- Get file number from inward visa/ ipm (monitor process) --> query on
select acquirer_reference, reversal_flag, transaction_slip, number_original_slip, T.* from int_transactions T where institution_number = '00002001' and transaction_class = '002' and file_number = 211; -- reversal chargeback:number_original_slip = original chargeback:transaction_slip

-- Get file number from inward visa/ ipm (monitor process)
select acquirer_reference,reversal_flag, T.* from int_transactions T where institution_number = '00002001' and transaction_slip = '22100004102';   -- orginal chargeback

select transaction_slip, client_case_number, acquirer_reference, ST.* from int_sundry_transactions ST where institution_number = '00002001' and transaction_slip = '22100004102';

select * from int_sundry_transactions where institution_number = '00002001' and sundry_transaction_slip = '22100004202';

select * from int_sundry_history where institution_number = '00002001' and sundry_transaction_slip = '22100004202';

