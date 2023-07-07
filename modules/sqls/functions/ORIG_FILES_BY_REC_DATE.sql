function orig_files_by_rec_date(
  p_instno in varchar2,
  p_process_name in varchar2,
  p_record_date in varchar2
) return varchar2
is
  v_return varchar2(2000);

  TYPE cur_type is ref cursor;
  rec cur_type;

  field varchar2(50);
begin
  --
  open rec for select original_file_name
  into   v_return
  from   int_file_log_details
  where  process_name = bwtpad( 'BWT_PROCESS_NAME', p_process_name)
  and institution_number = p_instno
  and record_date = p_record_date
  order by file_number desc;
  --
  loop
    fetch rec into field;
    exit when rec%NOTFOUND;
    --
    v_return := v_return || field || '|';
  end loop;
  --
  if length(v_return) = 0 then
      RETURN '';
  else
      RETURN substr(v_return,1,length(v_return)-length('|'));
  end if;
  --
  return v_return;
  --
exception
when others then
  return null;
end orig_files_by_rec_date;