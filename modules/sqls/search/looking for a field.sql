select * from user_tab_columns
where (column_name = ('DATE_CYCLE_END'))

and table_name in

(select table_name from user_tables
where num_rows > 0 )
