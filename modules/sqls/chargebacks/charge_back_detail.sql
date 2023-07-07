-- select * from bwt_generic_chargeback_reason where institution_number = '&institution_number' order by 1;

SELECT * FROM INT_SUNDRY_HISTORY WHERE institution_number in ('&institution_number') AND (CASE_NUMBER ='&case_number') order by 1;

