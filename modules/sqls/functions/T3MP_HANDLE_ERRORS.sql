CREATE OR REPLACE PROCEDURE T3MP_HANDLE_ERRORS (
	p_message in varchar2 default ''
)
IS
BEGIN
	dbms_output.put_line('[ERROR]: '|| p_message || '. Detail: ' || sqlerrm || ' ' || dbms_utility.format_error_backtrace);rollback;
END;