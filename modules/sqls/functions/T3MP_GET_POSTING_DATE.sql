CREATE OR REPLACE FUNCTION T3MP_GET_POSTING_DATE (
	p_institution_number in VARCHAR2
)
RETURN SYS_POSTING_DATE.POSTING_DATE%TYPE
IS
	v_record_date varchar(8);
BEGIN
	SELECT POSTING_DATE INTO v_record_date FROM SYS_POSTING_DATE WHERE INSTITUTION_NUMBER = p_institution_number AND STATION_NUMBER = 129; -- posting date
	RETURN v_record_date;
END;