SET DEFINE ON;
SET SERVEROUTPUT ON;

DECLARE
	v_institution_number VARCHAR2(8) := '&1';
	v_client_number VARCHAR2(120) := '&2'; -- 81

BEGIN
	FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
	    v_client_number := trim(curClientNumber.str);
		dbms_output.put_line('Client Number: '|| v_client_number);
		update cas_client_account
		set LAST_SETTLEMENT_DATE = '00000000'
		where acct_number like v_client_number || '%' and institution_number = v_institution_number;
	END LOOP;
	dbms_output.put_line('MERCHANT PAYMENTS COMPLETE ');
	commit;
END;
/