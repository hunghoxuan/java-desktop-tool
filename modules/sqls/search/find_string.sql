------ find value & columns in tables
declare

    v_owner varchar2(255) := 'BW3';
    v_institution_number varchar2(12) := '00002001';

    v_search_tables varchar2(40) := 'cis_%';
	v_search_columns varchar2(40) := 'client_number';
    v_search_string varchar2(4000) := '000000041';

    ---------

   	v_columns_group integer := 10;
    v_show_found_only boolean := true;
    v_search_exact boolean := true;
    v_show_sql boolean := true;
    v_debug boolean := false;
    v_data_type varchar2(255) := '%CHAR%';

	---------
	TYPE table_type IS table of varchar(50);
  	v_tables table_type;
    v_columns table_type;

    v_sql_count clob := '';
    v_sql clob := '';
    v_sql_where clob := '';
    v_sql_where_tables clob := '';
    v_sql_where_columns clob := '';
    v_sql_where_owner clob := '';
    v_sql_where_institution clob := '';
    v_sql_where_data_type clob := '';

    v_sql_tables clob := '';
    v_sql_columns clob := '';
    v_str_columns clob := '';

    v_match_count integer := 0;
    v_counter integer := 0;
    v_counter2 integer := 0;
    v_counter_tables integer := 0;
    v_counter_tables_found integer := 0;
    v_counter_values_found integer := 0;
    v_counter_columns_found integer := 0;

    v_search varchar2(2) := '';
    v_sql_final clob := '';
    v_columns_count integer := 0;
    v_table_name varchar2(50);
    v_column_name varchar2(50);
    v_i_table int;
    v_i_column int;
    v_i int;

    cur_tables sys_refcursor;
    cur_columns sys_refcursor;
    rc sys_refcursor;
begin

     if not v_search_exact then
		v_search := '%';
	 else
		v_search := '';
	 end if;

	 if v_search_tables is null then
		v_search_tables := '%';
	 end if;

	 if v_search_columns is null then
		v_search_columns := '%';
     else
     	v_columns_group := 1;
	 end if;

	 if v_institution_number is null then
		v_institution_number := '%';
	 end if;

	 if v_owner is null then
		v_owner := '%';
	 end if;

	 if v_data_type is null then
		v_owner := '%';
	 end if;


	 dbms_output.put_line('---------------');
	 if LENGTH(v_search_string) > 0 then
     	dbms_output.put_line('[' || TO_CHAR(SYSDATE,'DD-MM-YYYY HH24:MI') || '] db user: ' || v_owner || ', institution: ' || v_institution_number || '. Searching for keyword: "' || v_search_string || '"' || v_search || ' in table: [' || v_search_tables || '], column [' || v_search_columns || ']');
  	 else
  	 	dbms_output.put_line('[' || TO_CHAR(SYSDATE,'DD-MM-YYYY HH24:MI') || '] db user: ' || v_owner || ', institution: ' || v_institution_number || '. Searching column ' || v_search_columns || ' in table [' || v_search_tables || ']' );
  	 end if;


      --- query tables
      v_i := 0;
      for cur in (WITH DATA AS ( SELECT v_search_tables str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(str, ',', '","') || '"'))) loop
      	if v_i > 0 then
      		v_sql_where_tables := v_sql_where_tables || ' OR ';
      	end if;
      	v_sql_where_tables := v_sql_where_tables || ' upper(table_name) LIKE upper(''' || cur.str || ''')';
      	v_i := v_i + 1;
      end loop;

       --- query columns
      v_i := 0;
      for cur in (WITH DATA AS ( SELECT v_search_columns str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(str, ',', '","') || '"'))) loop
      	if v_i > 0 then
      		v_sql_where_columns := v_sql_where_columns || ' OR ';
      	end if;
      	v_sql_where_columns := v_sql_where_columns || ' upper(column_name) LIKE upper(''' || cur.str || ''')';
      	v_i := v_i + 1;
      end loop;

      v_sql_where_owner := 'upper(owner) like upper(''' || v_owner || ''')';
      v_sql_where_data_type := 'upper(data_type) like upper(''' || v_data_type || ''')';
      v_sql_where_institution := 'institution_number like ''' || v_institution_number || '''';


      v_sql_tables := 'select table_name from all_tables where (' || v_sql_where_owner || ') and table_name in
	                       (select table_name from all_tab_columns where owner = all_tables.owner and (' || v_sql_where_data_type || ') and (' || v_sql_where_tables || '))
                       order by table_name';

		if v_debug then
         	dbms_output.put_line('--- SQL: ' || v_sql_tables || ';');
  		end if;

      -- dbms_output.put_line('     ' || v_sql_tables || ';');
      OPEN cur_tables FOR v_sql_tables;
      FETCH cur_tables BULK COLLECT INTO v_tables;
      FOR v_i_table IN 1 .. v_tables.COUNT LOOP
      v_table_name := v_tables(v_i_table);

  	 -- loop all tables
       	v_counter_tables := v_counter_tables + 1;

       	-- reset temp vars before each loop
        v_counter := 0; v_counter2 := 0; v_sql := ''; v_sql_count := ''; v_sql_where := ''; v_columns_count := 0; v_sql_columns := '';

        execute immediate 'select count(*) from all_tab_columns where (' || v_sql_where_owner || ') and table_name = ''' || v_table_name || ''' and (' || v_sql_where_data_type || ') and (' || v_sql_where_columns || ')' into v_columns_count;

        if v_columns_count > 0 then

        	v_counter_tables_found := v_counter_tables_found + 1;

			v_sql_columns := 'select column_name from all_tab_columns where (' || v_sql_where_owner || ') and table_name = ''' || v_table_name || ''' and (' || v_sql_where_data_type || ') and (' || v_sql_where_columns || ') order by column_id asc';
			OPEN cur_columns FOR v_sql_columns;
			FETCH cur_columns BULK COLLECT INTO v_columns;
			FOR v_i_column IN 1 .. v_columns.COUNT LOOP
			v_column_name := v_columns(v_i_column);
	            if v_counter2 > 0 then
	                v_sql_where := v_sql_where || ' OR ';
	                v_str_columns := v_str_columns || ', ';
	            end if;

				v_sql_where := v_sql_where || 'UPPER(' || v_column_name || ') LIKE ''' || v_search || upper(v_search_string) || v_search || '''';
				v_str_columns := v_str_columns || v_column_name;

	            v_counter := v_counter + 1; v_counter2 := v_counter2 + 1;
	            v_counter_columns_found := v_counter_columns_found + 1;

		        if v_counter = v_columns_count or v_counter2 = v_columns_group then   -- table has column

		            if LENGTH(v_search_string) > 0 then -- try to search for value
		                v_sql := 'SELECT * FROM ' || v_table_name || ' WHERE (' || v_sql_where_institution || ') AND (' || v_sql_where || ')';
				        v_sql_count := 'SELECT COUNT(*) FROM ' || v_table_name || ' WHERE (' || v_sql_where_institution || ') AND (' || v_sql_where || ')';
				        if v_debug then
			            	dbms_output.put_line('--- SQL: ' || v_sql || ';' || v_sql_count);
                        end if;

			            begin
				        	execute immediate v_sql_count into v_match_count;
					        if v_match_count > 0 then   -- value found in table and columns group.
					        	if v_counter2 > 1 then   -- columns group has more than 1 column then keep looping to find exact column
									for cur_columns1 in (WITH DATA AS ( SELECT v_str_columns str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(str, ',', '","') || '"'))) loop
										v_sql_where := 'UPPER(' || cur_columns1.str || ') LIKE ''' || v_search || upper(v_search_string) || v_search || '''';
										v_sql_count := 'SELECT COUNT(*) FROM ' || v_table_name || ' WHERE (' || v_sql_where_institution || ') AND (' || v_sql_where || ')';
										v_sql := 'SELECT * FROM ' || v_table_name || ' WHERE (' || v_sql_where_institution || ') AND (' || v_sql_where || ')';
										execute immediate v_sql_count into v_match_count;
						        		if v_match_count > 0 then
						        			v_counter_values_found := v_counter_values_found + 1;
						        			v_sql_final := v_sql;
						        			dbms_output.put_line('');
						        			 dbms_output.put_line('-- ' || v_table_name || ' [' || cur_columns1.str || ']: Found ' || v_match_count || ' records. ');
								             if v_show_sql then
								            	dbms_output.put_line('     ' || v_sql || ';');
					                         end if;

						        		end if;
									end loop;
                                else
						        	v_counter_values_found := v_counter_values_found + 1;
						        	v_sql_final := v_sql;
						        	dbms_output.put_line('');
						            dbms_output.put_line('-- ' || v_table_name || ' [' || v_str_columns || ']: Found ' || v_match_count || ' records. ');
						            if v_show_sql then
						            	dbms_output.put_line('     ' || v_sql || ';');
			                        end if;
			                    end if;
					        else
					        	if not v_show_found_only then
					        		dbms_output.put_line('-- ' || v_table_name || '.' || v_column_name || ': No record found');
					        	end if;
					        end if;
							exception
				        		when others then
				        			if not v_show_found_only then
				            			dbms_output.put_line('---- ERROR: ' || v_table_name || '.' || v_column_name || ': ' || SQLCODE || SQLERRM || dbms_lob.substr(v_sql_count, 32600));
				            		end if;
					    end;
					 else
					 	dbms_output.put_line(v_table_name);
			    	 end if;

			    	  -- reset temp vars before each loop
			    	 v_counter2 := 0; v_sql := ''; v_sql_count := ''; v_sql_where := '';  v_str_columns := '';
			   	end if;
			end loop;
	    else
	    	 if not v_show_found_only then
	    	 	dbms_output.put_line('-- ' || v_table_name || ': No column');
	    	 end if;
	    end if;
    end loop;

    -- show result in result query
	if v_sql_final is not null then
		dbms_output.put_line('Final SQL: ' || v_sql_final);
		-- open :rc for v_sql_final;
	end if;
	dbms_output.put_line('');
    dbms_output.put_line('[' || TO_CHAR(SYSDATE,'DD-MM-YYYY HH24:MI') || '] Completed searching ' || v_counter_tables || ' tables and ' || v_counter_columns_found || ' columns. ' || v_counter_tables_found || ' tables has column "' || v_search_columns || '". ' || v_counter_values_found || ' tables has value "' || v_search_string || '". ');
    dbms_output.put_line('---------------');
    exception
        when others then
            dbms_output.put_line('---- ERROR: ' || SQLCODE || SQLERRM || dbms_lob.substr(v_sql, 32600));
end;
