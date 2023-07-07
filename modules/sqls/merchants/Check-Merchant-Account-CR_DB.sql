select account_amount_net *decode( dr_cr_indicator, '001',1,-1), a.acct_number, a.last_settlement_date , t.value_date

from int_transactions t , cas_client_account a
where t.institution_number = '00000006' --enter institution number of merchant/client
and t.transaction_status ='002'
and t.client_number = '10149893' --enter client number you are checking
and a.institution_number = t.institution_number
and a.acct_number = t.acct_number

and t.value_date > a.last_settlement_date

order by t.value_date