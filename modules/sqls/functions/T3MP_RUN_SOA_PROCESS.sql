CREATE OR REPLACE PROCEDURE T3MP_RUN_SOA_PROCESS (
	p_run_soa_process_id  in varchar2,
	p_institution_number in varchar2, 
	p_proceed in varchar2 default 'Y',
	p_record_date in varchar2 default ''

) IS
	v_errors VARCHAR2(120);
	P_OUT varchar2(100);
	v_params varchar2(100); 
	v_postdate varchar(8) := p_record_date;
BEGIN 
	if (p_proceed = 'Y') then
		if (v_postdate is null or v_postdate = '') then 
			select posting_date into v_postdate from sys_posting_date where institution_number = p_institution_number and station_number = 129;    
		end if;
	      
		SELECT BW_PROCESS_CONTROL.GET_PROCESS_PARAM_STRING_LIST(p_institution_number, p_run_soa_process_id, v_postdate) INTO v_params FROM DUAL;
		BW_process_control.run_process(p_institution_number, p_run_soa_process_id, v_params, 999999, 129, 'MANUAL', 'v1','', P_OUT, '001');
		DBMS_OUTPUT.put_line('FINISHED SOA PROCESS - ' || p_run_soa_process_id);
		COMMIT;
	end if;
EXCEPTION when others then
	dbms_output.put_line('> ERROR RUN SOA PROCESS: '|| sqlerrm || ' ' || dbms_utility.format_error_backtrace);rollback;
END;