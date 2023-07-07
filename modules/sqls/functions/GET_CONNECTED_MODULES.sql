procedure get_connected_modules(
  p_output  out sys_refcursor
)
is
  t_connected_modules tab_connected_modules;
  i		pls_integer := 0;
begin
  --
  t_connected_modules := tab_connected_modules(typ_connected_modules(null, null, null, null, null));
  for rec in (
    select sid, serial#, client_identifier, machine, osuser
    from   v$session
    where  client_identifier is not null
    order by client_identifier
  ) loop
    --
    i := i + 1;
    --
    if i > 1 then
      t_connected_modules.extend;
      t_connected_modules(i) := typ_connected_modules(null, null, null, null, null);
    end if;
    --
    t_connected_modules(i).sid := rec.sid;
    t_connected_modules(i).serial# := rec.serial#;
    t_connected_modules(i).module_name := rec.client_identifier;
    t_connected_modules(i).machine_name := rec.machine;
    t_connected_modules(i).os_user := rec.osuser;
    --
  end loop;
  --
  open p_output for
    select *
    from   table(t_connected_modules);
  --
exception
when no_data_found then
  null;
end;