FUNCTION "ISNUMERIC" (val_in IN VARCHAR2)
return boolean
/*
--------------------------------------------------------------------------------------------
   Purpose: Checks if the value is convertable to tonumber. Returns true if successful
         false when not.

   Developer: Carlos Gevido
---------------------------------------------------------------------------------------------

*/
IS
   Converted_value NUMBER;
   Result boolean;
BEGIN
   Converted_value := TO_NUMBER(val_in);

   Result := TRUE;

   RETURN Result;
EXCEPTION
   WHEN INVALID_NUMBER
   THEN
      RETURN FALSE;
   WHEN OTHERS
   THEN
      RETURN FALSE;
END IsNumeric;