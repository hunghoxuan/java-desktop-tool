/*
This SQL script's objective is to fix sequence numbers
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;
DECLARE
-- MANUAL PARAMS
v_institution_number VARCHAR2(8) := '&institution_number';

v_audit_trail VARCHAR2(35) := 'MANUAL_INPUT_';
v_user_id VARCHAR2(8) := '999999';
v_station_number VARCHAR2(3) := '129';

v_value VARCHAR2(12) := '';

v_errors VARCHAR2(120);
P_OUT varchar2(100);
v_params varchar2(100);

BEGIN

	if (v_institution_number is null or v_institution_number = '') THEN
		v_errors := 'Param is required or missing';
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	end if;

	IF (v_errors is null or v_errors = '') THEN
		-- 010: client_number
		select lpad(max(client_number) + 1, 8, '0') into v_value from cis_client_details where institution_number = v_institution_number AND client_number < '90000000' and client_number <> institution_number;
		DBMS_OUTPUT.put_line('010 Client_number : ' || v_value);
		UPDATE cbr_sequence_numbers set sequence_value = v_value where institution_number = v_institution_number and sequence_id = '010' and sequence_value < v_value;

		-- 018: group_number, 017: application_number
		select lpad(max(group_number) + 1, 8, '0') into v_value from cis_client_links where institution_number = v_institution_number AND group_number < '90000000' and client_number < '90000000' and client_number <> institution_number;
		DBMS_OUTPUT.put_line('018 group_number : ' || v_value);
		UPDATE cbr_sequence_numbers set sequence_value = v_value where institution_number = v_institution_number and sequence_id = '018' and sequence_value < v_value;

		-- 017: application_number
		select lpad(max(application_number) + 1, 10, '0') into v_value from cis_application_detail where institution_number = v_institution_number AND client_number < '90000000' and client_number <> institution_number;
		DBMS_OUTPUT.put_line('017: application_number : ' || v_value);
		UPDATE cbr_sequence_numbers set sequence_value = v_value where institution_number = v_institution_number and sequence_id = '017' and sequence_value < v_value;

		-- 042: transaction_slip
		select lpad(max(transaction_slip) + 1, 11, '0') into v_value from INT_BATCH_CAPTURE where institution_number = v_institution_number and client_number <> institution_number;
		DBMS_OUTPUT.put_line('042: transaction_slip : ' || v_value);
		UPDATE cbr_sequence_numbers set sequence_value = v_value where institution_number = v_institution_number and sequence_id = '042' and sequence_value < v_value;

       -- 015: TAX_RECORD_ID
		select lpad(max(record_id_number) + 1, 10, '0') into v_value from CIS_CLIENT_TAX_STATUS where institution_number = v_institution_number;
		DBMS_OUTPUT.put_line('015: tax_record_id : ' || v_value);
		UPDATE cbr_sequence_numbers set sequence_value = v_value where institution_number = v_institution_number and sequence_id = '015' and sequence_value < v_value;

		COMMIT;
	END IF;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/
select * from cbr_sequence_numbers where institution_number = '&institution_number' and sequence_id in ('017', '010', '018', '042', '015');
