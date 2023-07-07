function get_file_number(
  p_instno in varchar2,
  p_filenumber in  varchar2
) return varchar2
is
  v_file varchar2(60);
begin
  begin
    select record_date
    into   v_file
    from   int_file_log_details
    where  institution_number in ('00000000', p_instno)
    and    file_number = p_filenumber;
  exception
  when no_data_found then
    -- error
    return '';
  when too_many_rows then
    null;
  end;
  --
  return v_file;
exception
when others then
  return null;
end get_file_number;