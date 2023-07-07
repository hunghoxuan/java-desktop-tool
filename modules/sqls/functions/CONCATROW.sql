FUNCTION     ConcatRow(strRow in VARCHAR2) RETURN VARCHAR2 IS
    strReturn  VARCHAR2(4000);
     strBuild VARCHAR2(4000);
      curMain  sys_refcursor;
    BEGIN
        OPEN curMain FOR strRow;
          LOOP
            FETCH curMain INTO strBuild;
            EXIT WHEN curMain%NOTFOUND;
               IF strReturn IS NULL THEN
                 strReturn := strBuild;
               ELSE
                 strReturn := strReturn || ',' ||strBuild;
               END IF;
         END LOOP;
         RETURN strReturn;
       END;