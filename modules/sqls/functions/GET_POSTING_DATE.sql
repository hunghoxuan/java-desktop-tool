function get_posting_date(
  p_instno in varchar2
) return varchar2
is
    v_statn   varchar2(100);
    v_posting_date varchar2(100);
    err_no_station_configured exception;
begin
  --
  -- read the automation user and station number
  --
  begin
    select config_value
    into   v_statn
    from   sys_configuration
    where  institution_number in ('00000000', p_instno)
    and    config_section = 'Automation'
    and    config_keyword = 'StationNumber'
    order  by institution_number desc;
  exception
  when no_data_found then
    -- error
    raise err_no_station_configured;
  when too_many_rows then
    null;
  end;
  --
  SELECT POSTING_DATE
  INTO v_posting_date
  FROM SYS_POSTING_DATE
  WHERE INSTITUTION_NUMBER in ('00000000', p_instno)
  AND STATION_NUMBER = v_statn;
  --
  COMMIT;
  --
  return v_posting_date;
  --
exception
when others then
  return null;
end get_posting_date;