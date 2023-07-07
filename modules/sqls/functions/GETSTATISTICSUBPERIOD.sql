function GetStatisticSubPeriod (
  p_statistic_sub_period in varchar2
)  return varchar2
is
  v_stat_sub_period     varchar2(3);
--
begin
  --
  case p_statistic_sub_period
    when '01' then v_stat_sub_period := '101';
    when '02' then v_stat_sub_period := '102';
    when '03' then v_stat_sub_period := '103';
    when '04' then v_stat_sub_period := '101';
    when '05' then v_stat_sub_period := '102';
    when '06' then v_stat_sub_period := '103';
    when '07' then v_stat_sub_period := '101';
    when '08' then v_stat_sub_period := '102';
    when '09' then v_stat_sub_period := '103';
    when '10' then v_stat_sub_period := '101';
    when '11' then v_stat_sub_period := '102';
    when '12' then v_stat_sub_period := '103';
    else v_stat_sub_period := null;
  end case;
  --
  return v_stat_sub_period;
  --
exception
  when others then
    null;
end;