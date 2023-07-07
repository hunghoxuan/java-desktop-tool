FUNCTION CALCULATE_LIMIT_PERCENT (v_avail varchar2, v_client_limit varchar2) return number
as
begin
  if   to_number(v_client_limit) <  to_number(v_avail) then
    return 0;
  else
       return (100 - (to_number(v_avail)/to_number(v_client_limit) * 100));
  end if;
exception when others then
   return 0;
end;