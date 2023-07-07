/*
When error message such as the message above occurs in the log file take the following steps to deal with this error:
- Posting Instructions
This error message is derived from the posting instructions that are set up in BW3 for a certain set of transaction details of the specific transaction that caused the error to occur. 
Please check CBR_POSTING_INSTRUCTIONS with the parameters in the error log.
If a discrepancy is found, confirm what is the correct set up for the posting instructions and amend as is appropriate

Otherwise the Posting Instructions are correct so move on the next step.

- Client Account (chain)
The next step is to check Client Account details in CAS_CLIENT_ACCOUNT and determine in order to see if the required record is set up in the system.

If the Client Number is 90000001, 90000002, then run the follow sql in Golden filling in the specific Client Number and Account Currency to check if this client account is present 

select * from BW3.CAS_CLIENT_ACCOUNT
WHERE INSTITUTION_NUMBER = '' --INST NO.
and CLIENT_NUMBER = '' --ADD CLIENT NO
and ACCT_CURRENCY = '' --ADD ACCT CURR
If the client account is not present and everything else is correct then this client account needs to be added.

If the client number in the error message is Client Number is 90000001, 90000002, or the same as the institution number.

Add Client Account record - Client Account
The Client Number, Account Type ID, and Account Currency are all to be as in the error message and the Billing Cycle should be set to be the same as other Account Types set up in the institution check if the same account is set up but with a different currency and add like that set up.

Once the missing setup is inserted this issue should be resolved.
*/
SELECT * FROM BW3.CBR_POSTING_INSTRUCTIONS WHERE institution_number = '&institution_number' and SERVICE_CONTRACT_ID = '&service_contract_id' and account_type_id = '&account_type_id' and acct_currency = '&currency';

select * from BW3.CAS_CLIENT_ACCOUNT WHERE INSTITUTION_NUMBER = '&institution_number' AND CLIENT_NUMBER = '&client_number' and acct_currency = '&currency';