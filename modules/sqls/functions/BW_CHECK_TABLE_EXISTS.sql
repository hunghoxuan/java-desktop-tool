function bw_check_table_exists( p_table_name in varchar2 ) return integer
is
  i integer;
begin
  select count(*)
  into   i
  from   user_tables
  where  table_name = upper(p_table_name);
  --
  return i;
  --
end bw_check_table_exists;