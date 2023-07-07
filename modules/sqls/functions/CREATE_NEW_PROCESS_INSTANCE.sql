function create_new_process_instance(
  p_instno in varchar2,
  p_userid in  varchar2
) return varchar2
is
    v_statn   varchar2(100);
    v_process_number varchar2(100);
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
  v_process_number := Bw_Lib_Seq.GenerateProcessNumber();
  --
  Insert into INT_PROCESS_LOG (
   PROCESS_NUMBER,INSTITUTION_NUMBER,PROCESS_NAME,
   PROCESS_ID,USER_ID,PROCESS_START_DATE,
   PROCESS_END_DATE,PROCESS_START_TIME,PROCESS_END_TIME,
   STATION_NUMBER,APPLICATION_NAME,APPLICATION_VERSION,
   POSTING_DATE,PROCESSING_STATUS,PARENT_PROCESS_NUMBER,
   PRIOR_PROCESS_NUMBER,BRANCH_PROCESS_NUMBER,PROCEDURE_PROCESS_NAME,
   PROCEDURE_NUMBER,ORIGINAL_PROCEDURE_NAME,ORIGINAL_PROCEDURE_NUMBER,
   NUMBER_OF_TRANS,LOCKING_COUNTER)
  values (
   v_process_number, p_instno,'922',
   '902', p_userid, to_char(sysdate, 'YYYYMMDD'),
   to_char(sysdate, 'YYYYMMDD'),to_char(sysdate, 'HH24:MI:SS'),to_char(sysdate, 'HH24:MI:SS'),
   v_statn,'AUTOMATION_SERVER','1.0',
   to_char(sysdate, 'YYYYMMDD'),  '003', null,
   null, null,'922',
   null,  null,null,
   null, null);
  --
  COMMIT;
  --
  return v_process_number;
  --
exception
when others then
  return null;
end create_new_process_instance;