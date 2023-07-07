
-- @name: applications_details, table: CIS_APPLICATION_DETAIL
SELECT * FROM CIS_APPLICATION_ACCT_TYPE WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPLICATION_ADDR WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPLICATION_DETAIL WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPLICATION_SERVICES WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPL_TERMINAL_INPUT WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPL_TRAN_CHARGES WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

SELECT * FROM CIS_APPLICATION_MANDATE WHERE institution_number  in ('&institution_number') AND (APPLICATION_NUMBER = '&application_number') order by 1;

