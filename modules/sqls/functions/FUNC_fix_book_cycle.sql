/*
This SQL script's objective is to fix missing book cycle of selected merchants. Save time because dont need to login into WebGui.
Input params:
- institution_number
- client_number
Use cases: BA, Testers, Automation test.
Developed and maintained by: HUNG.HO@RS2.COM
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;
DECLARE
-- MANUAL PARAMS
v_user_id VARCHAR2(8) := '999999';
v_station_number VARCHAR2(3) := '129';

v_institution_number VARCHAR2(8) := '&1';
v_client_number VARCHAR2(120) := '&2';

v_audit_trail VARCHAR2(35) := 'MANUAL_INPUT_';

-- CACULATED PARAMS
v_acct_number VARCHAR2(12);
v_posting_date VARCHAR2(8);
v_cycle_start VARCHAR2(8);
v_cycle_end VARCHAR2(8);
v_date_cycle_end VARCHAR2(8);
v_system_date VARCHAR2(8);

v_errors VARCHAR2(120);
P_OUT varchar2(100);
v_params varchar2(100);

BEGIN

	if (v_institution_number is null or v_institution_number = ''
		or v_user_id is null or v_user_id = ''
		or v_client_number is null or v_client_number = ''
		or v_station_number is null or v_station_number = '') THEN
		v_errors := 'Param is required or missing';
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	end if;

	IF (v_errors is null or v_errors = '') THEN
		-- AUTO-CACULATED PARAMS
		SELECT CURRENT_CYCLE_END INTO v_cycle_end FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = v_institution_number and rownum = 1;
		SELECT CURRENT_CYCLE_START INTO v_cycle_start FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = v_institution_number and rownum = 1;

		SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL; -- get system date
		v_audit_trail := v_audit_trail || v_system_date;

		IF (v_client_number = '*' or v_client_number = '' or v_client_number = '-1' or v_client_number is null) THEN
		    FOR curr in (select distinct * from CAS_CYCLE_BOOK_BALANCE   where  institution_number = v_institution_number and processing_status = '004' and DATE_CYCLE_END < v_cycle_end order by acct_number, date_cycle_start) LOOP
	            dbms_output.put_line('ACCT NUMBER: ' || curr.acct_number || '. CYCLE START: ' || curr.date_cycle_start || ' - ' || curr.date_cycle_end || '. STATUS: ' || curr.processing_status);
	            UPDATE CAS_CYCLE_BOOK_BALANCE SET processing_status = '015' WHERE institution_number = v_institution_number and processing_status = '004' AND ACCT_NUMBER = curr.acct_number AND date_cycle_start = curr.date_cycle_start;
	            Insert into CAS_CYCLE_BOOK_BALANCE
	            (RECORD_DATE,INSTITUTION_NUMBER,ACCT_NUMBER,ACCT_CURRENCY,LAST_AMENDMENT_DATE,DATE_CYCLE_START,DATE_CYCLE_END,PROCESSING_STATUS,BEGIN_BALANCE,CURRENT_BALANCE,DR_BALANCE_CASH,DR_BALANCE_RETAIL,DR_BALANCE_INTEREST,DR_BALANCE_CHARGES,CR_BALANCE_PAYMENTS,CR_BALANCE_REFUNDS,CR_BALANCE_INTEREST,CR_BALANCE_BONUS,AMOUNT_HIGH_BAL_DR,AMOUNT_LOW_BAL_DR,AMOUNT_HIGH_BAL_CR,AMOUNT_LOW_BAL_CR,NUMBER_TRAN_CHRG_DR,NUMBER_TRAN_CHRG_CR,NUMBER_TRAN_NONCHRG_DR,NUMBER_TRAN_NONCHRG_CR,TRAN_CHARGES,ACCRUED_CR,ACCRUED_DR,PENDING_AUTHS,AUDIT_TRAIL,STATEMENT_NUMBER,LOCKING_COUNTER,CURRENT_BALANCE_CASH,INSTALLMENT_BALANCE,LAST_BALANCE_UPDATE) values
	            (v_system_date,v_institution_number,curr.acct_number,curr.acct_currency,v_system_date,v_cycle_start,v_cycle_end,'004','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0',null,v_audit_trail,null,null,null,null,null);
	   			COMMIT;
	   		END LOOP; -- Client number
		ELSE
			FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
	   			v_client_number := trim(curClientNumber.str);
		  		FOR curr in (select distinct * from CAS_CYCLE_BOOK_BALANCE   where  institution_number = v_institution_number and acct_number like v_client_number || '%' and processing_status = '004' and DATE_CYCLE_END < v_cycle_end order by acct_number, date_cycle_start) LOOP
		            dbms_output.put_line('ACCT NUMBER: ' || curr.acct_number || '. CYCLE START: ' || curr.date_cycle_start || ' - ' || curr.date_cycle_end || '. STATUS: ' || curr.processing_status);
		            UPDATE CAS_CYCLE_BOOK_BALANCE SET processing_status = '015' WHERE institution_number = v_institution_number and processing_status = '004' AND ACCT_NUMBER = curr.acct_number AND date_cycle_start = curr.date_cycle_start;
		            Insert into CAS_CYCLE_BOOK_BALANCE
		            (RECORD_DATE,INSTITUTION_NUMBER,ACCT_NUMBER,ACCT_CURRENCY,LAST_AMENDMENT_DATE,DATE_CYCLE_START,DATE_CYCLE_END,PROCESSING_STATUS,BEGIN_BALANCE,CURRENT_BALANCE,DR_BALANCE_CASH,DR_BALANCE_RETAIL,DR_BALANCE_INTEREST,DR_BALANCE_CHARGES,CR_BALANCE_PAYMENTS,CR_BALANCE_REFUNDS,CR_BALANCE_INTEREST,CR_BALANCE_BONUS,AMOUNT_HIGH_BAL_DR,AMOUNT_LOW_BAL_DR,AMOUNT_HIGH_BAL_CR,AMOUNT_LOW_BAL_CR,NUMBER_TRAN_CHRG_DR,NUMBER_TRAN_CHRG_CR,NUMBER_TRAN_NONCHRG_DR,NUMBER_TRAN_NONCHRG_CR,TRAN_CHARGES,ACCRUED_CR,ACCRUED_DR,PENDING_AUTHS,AUDIT_TRAIL,STATEMENT_NUMBER,LOCKING_COUNTER,CURRENT_BALANCE_CASH,INSTALLMENT_BALANCE,LAST_BALANCE_UPDATE) values
		            (v_system_date,v_institution_number,curr.acct_number,curr.acct_currency,v_system_date,v_cycle_start,v_cycle_end,'004',curr.current_balance,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0',null,v_audit_trail,null,null,null,null,null);
		   			COMMIT;
		   		END LOOP;
	   		END LOOP; -- Client number
		END IF;

	END IF;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/
select distinct * from CAS_CYCLE_BOOK_BALANCE   where  institution_number = '00002001' and processing_status = '004' and DATE_CYCLE_END = (SELECT CURRENT_CYCLE_END FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = '00002001' and rownum = 1) order by acct_number, date_cycle_start;


