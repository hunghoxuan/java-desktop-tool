
-- get all invalid objects
select object_type,count(*) from user_objects where status = 'INVALID'
	and object_name like 'T3MP_%'
	group by object_type;

-- get all invalid objects
select * from user_objects where status = 'INVALID'
	and object_name like 'T3MP_%';

-- get all packages
SELECT DISTINCT NAME FROM dba_source WHERE type = 'PACKAGE'
--	AND owner = 'BW3'
order by name;

-- get all procedures
SELECT DISTINCT NAME FROM dba_source WHERE type = 'PROCEDURE'
	AND NAME LIKE 'T3MP_%'
--	AND owner = 'BW3'
order by name;

-- get all functions
SELECT DISTINCT NAME FROM dba_source WHERE type = 'FUNCTION'
	AND NAME LIKE 'T3MP_%'
--	AND owner = 'BW3'
order by name;

-- get all custom types
SELECT DISTINCT NAME FROM dba_source WHERE type = 'TYPE'
--	AND owner = 'BW3'
order by name;

-- get all java class
SELECT object_name shortname, DBMS_JAVA.LONGNAME (object_name) longname FROM USER_OBJECTS WHERE object_type = 'JAVA CLASS'  AND object_name != DBMS_JAVA.LONGNAME (object_name) AND object_name != DBMS_JAVA.LONGNAME (object_name);

-- get content of functions, procedures etc
SELECT text FROM dba_source WHERE name = '&function_name'
  -- AND type = 'FUNCTION'
  -- AND owner = 'BW3'
ORDER BY line;

select * from all_errors;
