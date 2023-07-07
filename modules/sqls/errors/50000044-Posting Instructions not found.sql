/*
The posting instructions for the current institution and service contract   are not found in the posting instructions.
*/

select * from cbr_posting_instructions where institution_number = '&institution_number'
and service_contract_id = '&service_contract_id'
and service_id in (999,'&service_id') 
and transaction_category in (999, '&transaction_category')
and transaction_type in (999,'&transaction_type')
and tran_currency in (999, '&transaction_currency')
and service_type in (999, '&service_type')
and area_of_event in (999,'&area_of_event')
and posting_method in (999, '&posting_tariff')
and transaction_class in (999, '&transaction_class')
and settlement_currency in (999, '&settlement_currency');