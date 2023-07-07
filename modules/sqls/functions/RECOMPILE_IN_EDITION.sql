PROCEDURE     recompile_in_edition
IS
  c_name all_objects.OBJECT_NAME%TYPE;
  CURSOR c_all_objects IS SELECT OBJECT_NAME FROM DBA_OBJECTS WHERE object_type IN ('PACKAGE', 'PACKAGE BODY') AND OWNER = 'BW3' AND EDITION_NAME <> SYS_CONTEXT('userenv', 'current_edition_name') AND status = 'VALID';
BEGIN
   dbms_output.enable(NULL);
   dbms_output.put_line('-- COMPILE IN EDITION --');
   OPEN c_all_objects;
   LOOP
   FETCH c_all_objects INTO c_name;
      EXIT WHEN c_all_objects%notfound;
      IF c_name = 'BWSP_SMTP_EMAIL' THEN
      --IF c_name = 'BWSP_SMTP_EMAIL' OR c_name = 'BW_CS_NOTIFY' OR c_name = 'BW_CS_INTRA' THEN
        CONTINUE;
      END IF;

      dbms_output.put_line('COMPILING: ' || c_name );
      EXECUTE IMMEDIATE 'ALTER PACKAGE BW3.' || c_name || ' COMPILE PACKAGE';
   END LOOP;
   CLOSE c_all_objects;
END;
