procedure bw_get_station_number(
  p_instno in varchar2,
  p_return out varchar2
)
is
  v_statn varchar2(100);
  err_no_station_configured exception;

begin
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
  p_return := v_statn;
  --
exception
when err_no_station_configured then
  p_return := 'FAIL: No entry in SYS_CONFIGURATION for [Automation/StationNumber].';
when others then
  p_return := 'FAIL: '||sqlerrm;
end bw_get_station_number;