/*
Error message is normally encountered when:
Either the SERVICE_CONTRACT_ID in CIS_CLIENT _LINKS is assigned incorrectly
Or the respective clients links record for the merchant is assigned a future effective date date (Posting date <effective date) resulting in the contract details from the merchant not retrieved successfully

Align the client links record to the correct service contract ID or ensure whether the effective date assigned is effective at the time of processing.
Whenever the above have been corrected, the rejected transaction could then be reprocessed via Process 049 - Reprocess Srce Susp.
 The transaction in question will go through the standard set of validations and if all these are passed successfully, a new transaction will be created in BankWORKS, aligned to the newly defined parameters, which can then in turn be cleared off to the CardScheme.
If posting of destination class 002 fails (in cases of fees category 007 for example), repcoessing via 041 - Reprocess Dest grp.
*/

select * from int_transactions where institution_number = '&institution_number' and transaction_status='209' and client_number = '&client_number'; -- and record_date='&record_date';
select * from int_transactions where institution_number = '&institution_number' and client_number = '&client_number' and (transaction_slip='&transaction_slip' or file_number = '&file_number');
select * from cis_client_links where institution_number = '&institution_number' and client_number = '&client_number';
