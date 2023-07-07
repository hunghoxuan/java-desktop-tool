--clean the file from int_file_log_details

update int_file_log_details
  set file_id = '',
      processing_status = '003'
where institution_number = '&1'
and original_file_name in ('&2');

commit;