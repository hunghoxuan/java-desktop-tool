/*
Check CBR_CONTRACT_ACCT_TYPES with the parameters specified in the error and add the missing setup in line with the Institution configuration
*/

SELECT * FROM BW3.CBR_CONTRACT_ACCT_TYPES  WHERE institution_number = '&institution_number' and SERVICE_CONTRACT_ID = '&service_contract_id' and account_type_id = '&account_type_id' and acct_currency = '&currency';