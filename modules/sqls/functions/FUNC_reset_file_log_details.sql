update int_file_log_details
set processing_status = '003', file_id = NULL
where institution_number = '&1'
and (lower(original_file_name) like '%&2%' or lower(original_file_name) like '%&3%');
commit;