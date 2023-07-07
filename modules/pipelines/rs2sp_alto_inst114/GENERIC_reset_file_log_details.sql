update int_file_log_details
set processing_status = '003', file_id = NULL
where institution_number = '00000114'
and (lower(original_file_name) like '%tranbo%' or lower(original_file_name) like '%tranlive%' or lower(original_file_name) like '%-dom-d%');
commit;