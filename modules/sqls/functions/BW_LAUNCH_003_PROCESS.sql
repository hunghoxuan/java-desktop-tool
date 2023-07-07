procedure bw_launch_003_process(
  p_instno     in  varchar2,
  p_filepath   in  varchar2,
  p_filename   in  varchar2,
  p_filesize   in  varchar2,
  p_userid     in  varchar2,
  p_return     out varchar2
)
is
  v_params  varchar2(2000);
  v_procno  varchar2(20);
  v_statn   varchar2(100);
  err_no_station_configured exception;
begin
  --
  -- read the automation user and station number
  --
  v_params := 'p_filepath=>'''||p_filepath||'''|p_filename=>'''||p_filename||'''|p_filesize=>''123''';
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
  bw_process_control.run_process(
    p_InstitutionNumber_IN  => p_instno,
    p_ProcessName_IN        => '003',
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
  p_return := v_procno;
  --
exception
when err_no_station_configured then
  p_return := 'FAIL: No entry in SYS_CONFIGURATION for [Automation/StationNumber].';
when others then
  p_return := 'FAIL: '||sqlerrm;
end bw_launch_003_process;