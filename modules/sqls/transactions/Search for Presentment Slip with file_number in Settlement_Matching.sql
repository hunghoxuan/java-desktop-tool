Select * from int_settlement_matching
Where presentment_slip in (select transaction_slip from int_transactions where file_number = )
