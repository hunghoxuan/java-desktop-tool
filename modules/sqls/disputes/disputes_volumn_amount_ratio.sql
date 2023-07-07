-- Dispute volume
select count(*) from int_transactions
where institution_number = '&institution_number'
and merchant_number = '&client_number'
and transaction_category = '002'
and transaction_source = '&transaction_source'
and record_date = '&record_date')

-- Disputed Amount Show maximum of 5 currencies based on dispute volume
select sum(tran_amount_gr) from int_transactions
where institution_number = '&institution_number'
and merchant_number = '&client_number'
and transaction_category = '002'
and transaction_source = '&transaction_source'
and record_date = '&record_date');

-- Dispute Ratio
select sum(tran_amount_gr) from int_transactions
where institution_number = '&institution_number'
and merchant_number = '&client_number'
and transaction_category = '002'
and transaction_source = '&transaction_source'
and record_date = '&record_date');