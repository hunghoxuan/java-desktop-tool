function ispostingdateupdated
(
  p_institution    in varchar2,
  p_station_number in varchar2 default null
) return varchar2
is
  --
  v_sys_date         varchar2(8);
  v_sys_date_time    varchar2(19);
  v_posting_date     varchar2(8);
  v_max_posting_date varchar2(8);
  v_min_posting_date varchar2(8);
  v_return           varchar2(4000);
  station_error      varchar2(1000) := null;
  station_ok         varchar2(1000) := null;
  v_inst_no          varchar2(8) := null;
  --
begin
  --
  select to_char(SYSTIMESTAMP, 'YYYY.MM.DD HH:MI:SS'), to_char(sysdate, 'YYYYMMDD')
  into   v_sys_date_time, v_sys_date
  from   dual;
  --
  if p_institution = '00000000' then
    --
    for rec in(
      select institution_number, station_number, posting_date
      from   sys_posting_date
      order by institution_number, station_number
    ) loop
      --
      if v_inst_no is null then
        --
        v_inst_no := rec.institution_number;
        --
      elsif v_inst_no <> rec.institution_number then
        --
        if station_ok is not null then
          --
          v_return := v_return||v_sys_date_time||' OK: Posting date matches system date for institution '|| v_inst_no || ' and station number: '|| trim(station_ok) || chr(10);
          --
        end if;
        if station_error is not null then
          --
          v_return := v_return||v_sys_date_time||' Error: Posting date does not match system date for institution '|| v_inst_no || '! Station number: '|| trim(station_error) || chr(10);
          --
        end if;
        --
        v_inst_no := rec.institution_number;
        station_ok := null;
        station_error := null;
        --
      end if;
      --
      if rec.posting_date = v_sys_date then
        --
        station_ok := station_ok || rec.station_number ||', ';
        --
      else
        --
        station_error := station_error || rec.station_number ||', ';
        --
      end if;
      --
    end loop;
    --
    if station_ok is not null then
      --
      v_return := v_return||v_sys_date_time||' OK: Posting date matches system date for institution '|| v_inst_no || ' and station number: '|| trim(station_ok) || chr(10);
      --
    end if;
    if station_error is not null then
       --
       v_return := v_return||v_sys_date_time||' Error: Posting date does not match system date for institution '|| v_inst_no || '! Station number: '|| trim(station_error) || chr(10);
       --
    end if;
    --
  else
    --
    if p_station_number is not null then
      --
      select posting_date
      into   v_posting_date
      from   sys_posting_date
      where  institution_number = p_institution
      and    station_number     = p_station_number;
      --
    else
      --
      select min(posting_date), max(posting_date)
      into   v_min_posting_date, v_max_posting_date
      from   sys_posting_date
      where  institution_number = p_institution;
      --
      if v_min_posting_date = v_max_posting_date then
        --
        v_posting_date := v_max_posting_date;
        --
      else
        --
        if v_min_posting_date <> v_sys_date then
          --
          v_posting_date := v_min_posting_date;
          --
        elsif v_max_posting_date <> v_sys_date then
          --
          v_posting_date := v_max_posting_date;
          --
        end if;
        --
      end if;
      --
    end if;
    --
    if v_posting_date = v_sys_date then
      --
      v_return := v_sys_date_time||' OK: Posting date matches system date for institution '|| p_institution || case when p_station_number is not null then ' and station number ' || p_station_number end;
      --
    else
      --
      v_return := v_sys_date_time||' Error: Posting date does not match system date for institution '|| p_institution || case when p_station_number is not null then '! Station number ' || p_station_number else '!'end;
      --
    end if;
    --
  end if;
  --
  return trim(v_return);
end ispostingdateupdated;
