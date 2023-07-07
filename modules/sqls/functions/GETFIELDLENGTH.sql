FUNCTION GetFieldLength(
strTableName IN  varchar2,
strFieldName IN  varchar2
)
return number $IF DBMS_DB_VERSION.VERSION >= 11 $THEN RESULT_CACHE $END AS

v_return pls_integer:=0;
begin
  SELECT char_length
  INTO   v_return
  FROM   USER_TAB_COLUMNS
  WHERE  upper(column_name) = upper(strFieldName)
  AND upper(table_name) = upper(strTableName);
  return v_return;
exception
when no_data_found then
 return 0;
 --
end GetFieldLength;