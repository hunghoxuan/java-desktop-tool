
select table_name from user_tables where table_name like 'XVW$%';

declare
	TYPE table_type IS table of varchar(50);
  	v_tables table_type;
  	cur_tables sys_refcursor;
  	v_table_name varchar2(50);
  	v_i_table int;
begin
	dbms_output.put_line('---------------');
	OPEN cur_tables FOR 'select table_name from user_tables where table_name like ' || '''XVW$%''';
      FETCH cur_tables BULK COLLECT INTO v_tables;
      FOR v_i_table IN 1 .. v_tables.COUNT LOOP
	      v_table_name := v_tables(v_i_table);
	      dbms_output.put_line('DROP TABLE ' || v_table_name);
		  -- execute immediate 'DROP TABLE ' || v_table_name;
	  end loop;
	  dbms_output.put_line('---------------');
end;

