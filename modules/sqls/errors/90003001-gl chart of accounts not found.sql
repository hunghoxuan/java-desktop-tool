/*
How to resolve

b)   Search criteria such as Synthetic Extension, GL Account Type ID, Account Type ID and Account Currency, as specified in the error.
c)   If there are no results for the specified criteria try a smaller sample.  Often fields such as Service Contract/ GL Account Type ID/ Account Type ID are “catch all” records specified by “n/a” in many of the records.
d)    A record may be returned which is similar but not exact to what is being looked for in the error.  Use this record as a template to add the missing set up, and change the relevant fields. 
e)   Make sure the effective date of the record you are adding is on or before the “Effective date” or “Posting date” in the error.
*/

SELECT * FROM BW3.CBR_CHART_OF_ACCOUNTS
 WHERE INSTITUTION_NUMBER = '&institution_number' --INST NO
AND SYNTH_EXTENSION = '&synth_extension'
AND GL_ACCOUNT_TYPE_ID = '&account_type_id'
AND ACCT_CURRENCY = '&currency';