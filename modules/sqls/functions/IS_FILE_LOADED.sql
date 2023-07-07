function is_file_loaded(
  p_instno in varchar2,
  p_filename in  varchar2
) return varchar2
is
  v_file varchar2(60);
begin
  begin
    select original_file_name
    into   v_file
    from   int_file_log_details
    where  institution_number in ('00000000', p_instno)
    and    original_file_name = p_filename;
  exception
  when no_data_found then
    -- error
    return 'false';
  when too_many_rows then
    null;
  end;
  --
  return 'true';
exception
when others then
  return null;
end is_file_loaded;