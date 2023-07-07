procedure bw_launch_process(
  p_instno in varchar2,
  p_userid in varchar2,
  p_process_params in varchar2,
  p_process_name in varchar2,
  p_return out varchar2
)
is
  --
  v_process_name                 cht_process_name.index_field%type;
  v_params                       varchar2(4000);
  v_procno                       varchar2(20);
  v_proc_nos                     varchar2(4000);
  v_statn                        varchar2(100);
  v_postdate                     varchar(100);
  v_selection_mode               varchar2(3);
  err_no_station_configured      exception;
  err_no_posting_date_configured exception;
  --
begin
  --
  begin
    select    config_value
    into      v_statn
    from      sys_configuration
    where     (institution_number in ('00000000', p_instno))
    and       (config_section = 'Automation')
    and       (config_keyword = 'StationNumber')
    order by  institution_number desc;
  exception
  when no_data_found then
    -- error
    raise err_no_station_configured;
  when too_many_rows then
    null;
  end;
  --
  v_process_name := bwtpad( 'BWT_PROCESS_NAME', p_process_name );
  --
  begin
    select  posting_date
    into    v_postdate
    from    sys_posting_date
    where   (institution_number = p_instno)
    and     (station_number = v_statn);
  exception
  when no_data_found then
    -- error
    raise err_no_posting_date_configured;
  when too_many_rows then
    null;
  end;
  --
  if p_process_params is null or p_process_params = '' then
    v_params := BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(p_instno, v_process_name, v_postdate);
  else
    v_params := p_process_params;
  end if;
  --
  if v_params != '[# NO DATA FOUND! #]' then
    --
    select selection_mode
    into   v_selection_mode
    from   sys_process_selection
    where  process_name = v_process_name; 
    --
    if v_selection_mode = bw_const.CONF_YES then --'001'
      --
      bw_process_control.run_process(
        p_InstitutionNumber_IN  => p_instno,
        p_ProcessName_IN        => v_process_name,
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
      v_proc_nos := v_procno;
      --
    else
      --
      for rec in (
        select column_value param
        from   table( bw_util.split( v_params, chr(10) ) )
      ) loop
        --
        bw_process_control.run_process(
          p_InstitutionNumber_IN  => p_instno,
          p_ProcessName_IN        => v_process_name,
          p_ProcessParameters_IN  => rec.param,
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
        if v_proc_nos is null then
          v_proc_nos := v_procno;
        else
          v_proc_nos := v_proc_nos || ',' || v_procno;
        end if;
        --
      end loop;
      --
    end if;
    --   
  end if;
  --
  p_return := v_proc_nos;
  --
exception
when err_no_station_configured then
  p_return := 'FAIL: No entry in SYS_CONFIGURATION for [Automation/StationNumber].';
when err_no_posting_date_configured then
  p_return := 'FAIL: No entry in posting date configured for automation user.';
when others then
  p_return := 'FAIL: ' || sqlerrm;
  if bw_lib_incl.gblnIsCommServer then
   raise;
  end if;
end bw_launch_process;
