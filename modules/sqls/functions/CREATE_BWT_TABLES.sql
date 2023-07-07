PROCEDURE     Create_Bwt_Tables
PROCEDURE     Create_Bwt_Tables
IS
IS


v_columns VARCHAR2(1000);
v_columns VARCHAR2(1000);
v_cursor NUMBER;
v_cursor NUMBER;
v_index_field VARCHAR2(20);
v_index_field VARCHAR2(20);
v_ignore NUMBER;
v_ignore NUMBER;
v_sql VARCHAR2(5000);
v_sql VARCHAR2(5000);
v_union_columns VARCHAR2(1000);
v_union_columns VARCHAR2(1000);


Cursor Tab_Cur
Cursor Tab_Cur
    IS
    IS
    SELECT table_name
    SELECT table_name
      FROM user_tables
      FROM user_tables
     WHERE table_name like 'CHT%'
     WHERE table_name like 'CHT%'
       AND table_name not like 'CHT_CACHE%';
       AND table_name not like 'CHT_CACHE%';


Tab_Rec Tab_Cur%ROWTYPE;
Tab_Rec Tab_Cur%ROWTYPE;




Cursor Column_Cur
Cursor Column_Cur
    (v_table_name VARCHAR2)
    (v_table_name VARCHAR2)
    IS
    IS
    SELECT column_name
    SELECT column_name
      FROM user_tab_columns
      FROM user_tab_columns
     WHERE table_name = v_table_name
     WHERE table_name = v_table_name
     ORDER BY column_id;
     ORDER BY column_id;


Column_Rec Column_Cur%ROWTYPE;
Column_Rec Column_Cur%ROWTYPE;


BEGIN
BEGIN


OPEN Tab_Cur;
OPEN Tab_Cur;
LOOP
LOOP
   FETCH Tab_Cur INTO Tab_Rec;
   FETCH Tab_Cur INTO Tab_Rec;
   EXIT WHEN Tab_Cur%NOTFOUND;
   EXIT WHEN Tab_Cur%NOTFOUND;


   IF Tab_Rec.table_name = 'CHT_CURRENCY' THEN
   IF Tab_Rec.table_name = 'CHT_CURRENCY' THEN
      v_index_field := 'ISO_CODE';
      v_index_field := 'ISO_CODE';
   ELSE
   ELSE
      v_index_field := 'INDEX_FIELD';
      v_index_field := 'INDEX_FIELD';
   END IF;
   END IF;


   v_sql := 'CREATE OR REPLACE VIEW BWT'||SUBSTR(Tab_Rec.table_name,4,27)||' '||
   v_sql := 'CREATE OR REPLACE VIEW BWT'||SUBSTR(Tab_Rec.table_name,4,27)||' '||
            'AS SELECT ';
            'AS SELECT ';


   v_columns := NULL;
   v_columns := NULL;


   OPEN Column_Cur(Tab_Rec.table_name);
   OPEN Column_Cur(Tab_Rec.table_name);
   LOOP
   LOOP
      FETCH Column_Cur INTO Column_Rec;
      FETCH Column_Cur INTO Column_Rec;
      EXIT WHEN Column_Cur%NOTFOUND;
      EXIT WHEN Column_Cur%NOTFOUND;


      v_columns := v_columns||Column_Rec.column_name||',';
      v_columns := v_columns||Column_Rec.column_name||',';


   END LOOP;
   END LOOP;
   CLOSE Column_Cur;
   CLOSE Column_Cur;


   v_columns := RTRIM(v_columns,',');
   v_columns := RTRIM(v_columns,',');


   v_union_columns := REPLACE(v_columns,'INSTITUTION_NUMBER','SYS.INSTITUTION_NUMBER');
   v_union_columns := REPLACE(v_columns,'INSTITUTION_NUMBER','SYS.INSTITUTION_NUMBER');




   v_sql := v_sql||v_columns||
   v_sql := v_sql||v_columns||
            ' FROM '||Tab_Rec.table_name||
            ' FROM '||Tab_Rec.table_name||
            ' WHERE '||v_index_field||' >= '||''''||'000'||''''||
            ' WHERE '||v_index_field||' >= '||''''||'000'||''''||
            ' AND INSTITUTION_NUMBER != '||''''||'00000000'||''''||
            ' AND INSTITUTION_NUMBER != '||''''||'00000000'||''''||
            ' UNION ALL SELECT '||
            ' UNION ALL SELECT '||
            v_union_columns||
            v_union_columns||
            ' FROM '||Tab_Rec.table_name||' DEF ,SYS_INSTITUTION_LICENCE SYS '||
            ' FROM '||Tab_Rec.table_name||' DEF ,SYS_INSTITUTION_LICENCE SYS '||
            ' WHERE SYS.INSTITUTION_NUMBER > '||''''||'00000000'||''''||
            ' WHERE SYS.INSTITUTION_NUMBER > '||''''||'00000000'||''''||
            ' AND SYS.INSTITUTION_NUMBER < '||''''||'11111111'||''''||
            ' AND SYS.INSTITUTION_NUMBER < '||''''||'11111111'||''''||
            ' AND DEF.'||v_index_field||' >= '||''''||'000'||''''||
            ' AND DEF.'||v_index_field||' >= '||''''||'000'||''''||
            ' AND DEF.INSTITUTION_NUMBER = '||''''||'00000000'||''''||
            ' AND DEF.INSTITUTION_NUMBER = '||''''||'00000000'||''''||
            ' AND NOT EXISTS (SELECT '||''''||'X'||''''||
            ' AND NOT EXISTS (SELECT '||''''||'X'||''''||
            ' FROM '||Tab_Rec.table_name||' DEF '||
            ' FROM '||Tab_Rec.table_name||' DEF '||
            ' WHERE '||v_index_field||' >= '||''''||'000'||''''||
            ' WHERE '||v_index_field||' >= '||''''||'000'||''''||
            ' AND DEF.'||v_index_field||' = '||v_index_field||
            ' AND DEF.'||v_index_field||' = '||v_index_field||
            ' AND SYS.INSTITUTION_NUMBER = INSTITUTION_NUMBER '||
            ' AND SYS.INSTITUTION_NUMBER = INSTITUTION_NUMBER '||
            ' AND DEF.LANGUAGE = LANGUAGE)';
            ' AND DEF.LANGUAGE = LANGUAGE)';






   v_cursor := dbms_sql.open_cursor;
   v_cursor := dbms_sql.open_cursor;
   dbms_sql.parse(v_cursor, v_sql,dbms_sql.v7);
   dbms_sql.parse(v_cursor, v_sql,dbms_sql.v7);
   v_ignore :=dbms_sql.execute(v_cursor);
   v_ignore :=dbms_sql.execute(v_cursor);
   dbms_sql.close_cursor(v_cursor);
   dbms_sql.close_cursor(v_cursor);


END LOOP;
END LOOP;
CLOSE Tab_Cur;
CLOSE Tab_Cur;




EXCEPTION
EXCEPTION
   WHEN OTHERS THEN
   WHEN OTHERS THEN
      IF dbms_sql.is_open(v_cursor) then
      IF dbms_sql.is_open(v_cursor) then
         dbms_sql.close_cursor(v_cursor);
         dbms_sql.close_cursor(v_cursor);
      END IF;
      END IF;
      IF Tab_Cur%ISOPEN THEN
      IF Tab_Cur%ISOPEN THEN
         CLOSE Tab_Cur;
         CLOSE Tab_Cur;
      END IF;
      END IF;
      IF Column_Cur%ISOPEN THEN
      IF Column_Cur%ISOPEN THEN
         CLOSE Column_Cur;
         CLOSE Column_Cur;
      END IF;
      END IF;
      raise_application_error(-20100,'Error in Create_Bwt_Views: '||sqlerrm(sqlcode));
      raise_application_error(-20100,'Error in Create_Bwt_Views: '||sqlerrm(sqlcode));
END;END;