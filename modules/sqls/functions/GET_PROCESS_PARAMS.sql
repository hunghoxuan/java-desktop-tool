function get_process_params(
  p_instno in varchar2,
  p_process_name in varchar2
) return varchar2
is
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
    return BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(p_instno, bwtpad( 'BWT_PROCESS_NAME', p_process_name), v_postdate);
    --
    exception
        when err_no_station_configured then
            return 'FAIL: No entry in SYS_CONFIGURATION for [Automation/StationNumber].';
        when err_no_posting_date_configured then
            return 'FAIL: No entry in posting date configured for automation user.';
        when others then
            return 'FAIL: '||sqlerrm;

end get_process_params;