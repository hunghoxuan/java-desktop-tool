--clean the file from int_file_log_details

Delete int_file_log_details
where institution_number = '&1'
and original_file_name in ('&2');

commit;