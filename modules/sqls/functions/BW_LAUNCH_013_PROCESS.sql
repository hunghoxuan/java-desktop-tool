procedure bw_launch_013_process(
  p_instno in varchar2,
  p_userid in varchar2,
  p_return out varchar2
)
is
  v_params varchar2(2000);
  v_procno varchar2(20);
  v_statn varchar2(100);
  v_postdate varchar(100);
  err_no_station_configured exception;
  err_no_posting_date_configured exception;

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
  begin
    select posting_date
    into v_postdate
    from sys_posting_date
    where institution_number = p_instno
      and station_number = v_statn;
  exception
  when no_data_found then
    -- error
    raise err_no_posting_date_configured;
  when too_many_rows then
    null;
  end;
  --
  v_params := BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(p_instno, '013', v_postdate);
  --
  if v_params != '[# NO DATA FOUND! #]' then
    --
    bw_process_control.run_process(
        p_InstitutionNumber_IN  => p_instno,
        p_ProcessName_IN        => '013',
        p_ProcessParameters_IN  => v_params,
        p_UserID_IN             => p_userid,
        p_StationNumber_IN      => v_statn,
        p_ApplicationName_IN    => 'SleepyLion',
        p_ApplicationVersion_IN => '1',
        p_ParentProcess_IN      => null,
        p_ProcessNumber_OUT     => v_procno
      );
    --
    commit;
    --
  end if;
  --
  p_return := v_procno;
  --
exception
when err_no_station_configured then
  p_return := 'FAIL: No entry in SYS_CONFIGURATION for [Automation/StationNumber].';
when err_no_posting_date_configured then
  p_return := 'FAIL: No entry in posting date configured for automation user.';
when others then
  p_return := 'FAIL: '||sqlerrm;
end bw_launch_013_process;