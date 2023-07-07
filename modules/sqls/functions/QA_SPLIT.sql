function qa_split (v_clients in varchar, seperator in varchar := ',')
return BW_TAB_VARCHAR2_100
is
  pos pls_integer;
  bef_pos pls_integer;
  d_array BW_TAB_VARCHAR2_100;
begin
  bef_pos := 1;
  d_array := BW_TAB_VARCHAR2_100();
  if (v_clients is null) or (length(v_clients) = 0) then
    return d_array;
  end if;
  loop
    pos := INSTR(v_clients, seperator, bef_pos);
    if pos = 0 then
      d_array.extend;
      d_array(d_array.count) := substr(v_clients, bef_pos);
      return d_array;
    end if;
    d_array.extend;
    d_array(d_array.count) := substr(v_clients, bef_pos, pos-bef_pos);
    bef_pos := pos + 1;
  end loop;
end;
