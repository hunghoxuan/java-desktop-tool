SET DEFINE ON;
SET SERVEROUTPUT ON;
Declare
-- MANUAL PARAMS. Empty -> auto generate
v_user_id VARCHAR2(8) := '999999';
v_station_number VARCHAR2(3) := '129';
v_institution_number Varchar2(8) := '&1'; -- 00002001
v_posting_date VARCHAR2(8) := '&2';
v_system_date VARCHAR2(8);

BEGIN
  if (v_posting_date = '-1' or v_posting_date = 'EOC' or v_posting_date = 'eoc') then -- end of cycle
  	 SELECT CURRENT_CYCLE_END into v_posting_date FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = v_institution_number;
  elsif (v_posting_date = '0' or v_posting_date = 'TODAY' or v_posting_date = 'today') then -- system date
  	 SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_posting_date FROM DUAL;
  elsif (length(v_posting_date) < 8) then
  	 SELECT TO_CHAR(TO_DATE(POSTING_DATE, 'YYYYMMDD') + v_posting_date, 'YYYYMMDD') into v_posting_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = v_institution_number AND STATION_NUMBER = v_station_number;
  end if;
 	
  if (length(v_posting_date) = 8) then    
      dbms_output.put_line('Update Posting date: ' || v_posting_date);
	  UPDATE SYS_POSTING_DATE
	  SET POSTING_DATE = v_posting_date
	  WHERE INSTITUTION_NUMBER = v_institution_number
	  AND STATION_NUMBER = v_station_number;
	  commit;
  end if;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/
SELECT CURRENT_CYCLE_END FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = '00002001';
