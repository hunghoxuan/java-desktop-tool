procedure find_string(p_str in varchar2) authid current_user as
     l_query    long;
     l_case     long;
     l_runquery boolean;
     l_tname    varchar2(30);
     l_cname    varchar2(30);
   begin
     dbms_application_info.set_client_info('%' || upper(p_str) || '%');

     for x in (select * from user_tables) loop
       l_query    := 'select ''' || x.table_name ||
                     ''', $$
                         from ' || x.table_name || '
                        where rownum = 1 and ( 1=0 ';
       l_case     := 'case ';
       l_runquery := FALSE;
       for y in (select *
                   from user_tab_columns
                  where table_name = x.table_name
                    and data_type in ('VARCHAR2', 'CHAR')) loop
         l_runquery := TRUE;
         l_query    := l_query || ' or upper(' || y.column_name ||
                       ') like userenv(''client_info'') ';
         l_case     := l_case || ' when upper(' || y.column_name ||
                       ') like userenv(''client_info'') then ''' ||
                       y.column_name || '''';
       end loop;
       if (l_runquery) then
         l_case  := l_case || ' else NULL end';
         l_query := replace(l_query, '$$', l_case) || ')';
         begin
           execute immediate l_query
             into l_tname, l_cname;
           dbms_output.put_line('Found in ' || l_tname || '.' || l_cname);
         exception
           when no_data_found then
            null;
            -- dbms_output.put_line('No hits in ' || x.table_name);
         end;
       end if;

     end loop;
   end;
