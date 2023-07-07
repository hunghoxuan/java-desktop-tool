procedure update_addend_charges_non_p_jobs(
  p_Start_Date in varchar2,
  p_end_date   in varchar2,
  p_threads    in varchar2)
as
v_job_name               varchar2( 30 );
v_job_action             varchar2( 4000 );
i_thread_cnt    pls_integer := 0;
begin
  for i_mod in 0 .. p_threads - 1
  loop
    v_job_name := 'BNZ_INT_CHARGES_NONP_JOBS_'||i_thread_cnt;
    dbms_output.put_line(v_job_name);
    --
    v_job_action := 'begin update_addend_charges_non_p(start_date=>'''||p_start_date||''',end_date=>'''||p_end_date||''', par_degree=>'''||p_threads||''', tid=>'''||i_thread_cnt||'''); end;';
    --
    dbms_output.put_line(v_job_action);
    DBMS_SCHEDULER.CREATE_JOB(
         job_name => v_job_name
        ,job_type => 'PLSQL_BLOCK'
        ,job_action => v_job_action
        ,start_date => null
        ,enabled => TRUE
        ,comments => 'update_addend_charges_non_p_jobs');
    --
    i_thread_cnt := i_thread_cnt + 1;
  end loop;
exception
when others then
  dbms_output.put_line(sqlerrm);
end;