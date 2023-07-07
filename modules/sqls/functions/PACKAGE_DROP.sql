PROCEDURE     package_drop (pkg_owner IN VARCHAR2, pkg_name IN VARCHAR2)
PROCEDURE         package_drop (pkg_owner IN VARCHAR2, pkg_name IN VARCHAR2)
IS
IS
	INVALID_TYPE_COUNT   INTEGER;
	INVALID_TYPE_COUNT   INTEGER;
	INVALID_TYPE_NAME    VARCHAR2(100);
	INVALID_TYPE_NAME    VARCHAR2(100);
BEGIN
BEGIN
	LOOP
	LOOP
	BEGIN
	BEGIN
	EXECUTE IMMEDIATE 'drop package ' || pkg_owner || '.' || pkg_name;
	EXECUTE IMMEDIATE 'drop package ' || pkg_owner || '.' || pkg_name;
	INVALID_TYPE_COUNT := 0;
	INVALID_TYPE_COUNT := 0;
EXCEPTION
EXCEPTION
	WHEN OTHERS THEN
	WHEN OTHERS THEN
		SELECT count (*) INTO  INVALID_TYPE_COUNT from user_objects where object_type like 'TYPE' and status != 'VALID' and object_name like 'SYS_PLSQL_%';
		SELECT count (*) INTO  INVALID_TYPE_COUNT from user_objects where object_type like 'TYPE' and status != 'VALID' and object_name like 'SYS_PLSQL_%';
   		IF INVALID_TYPE_COUNT > 0 THEN
   		IF INVALID_TYPE_COUNT > 0 THEN
			SELECT distinct object_name INTO  INVALID_TYPE_NAME from user_objects where  object_type like 'TYPE' and status != 'VALID' and object_name like 'SYS_PLSQL_%' and ROWNUM = 1;
			SELECT distinct object_name INTO  INVALID_TYPE_NAME from user_objects where  object_type like 'TYPE' and status != 'VALID' and object_name like 'SYS_PLSQL_%' and ROWNUM = 1;
			execute immediate 'drop type bw_risk.' || INVALID_TYPE_NAME;
			execute immediate 'drop type bw3.' || INVALID_TYPE_NAME;
		END IF;
		END IF;
	END;
	END;
	EXIT WHEN INVALID_TYPE_COUNT = 0;
	EXIT WHEN INVALID_TYPE_COUNT = 0;
	END LOOP;
	END LOOP;
END;
END;