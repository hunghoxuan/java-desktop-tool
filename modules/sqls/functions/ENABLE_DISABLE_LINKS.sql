procedure enable_disable_links (
  p_inst_no        in varchar2,
  p_station_no     in varchar2,
  p_user_id        in varchar2,
  p_mode           in varchar2
)
is
  n_init_return    number;
  v_message        varchar2(500);
  rt_links         cis_client_links%rowtype;
  v_contract_status_from     varchar2(3);
  v_contract_status_to       varchar2(3);
  --
  err_cannot_initialise exception;
  err_invalid_mode      exception;
  --
begin
  --
  bw_prc_res.InitGlobalVars(p_inst_no, p_station_no, p_user_id, n_init_return);
  --
  if n_init_return = 0 then
    --
    raise err_cannot_initialise;
    --
  end if;
  --
  if p_mode not in ('A', 'S') then
    --
    raise err_invalid_mode;
    --
  end if;
  --
  --Activate Client Links
  --
  dbms_output.put_line('--------------------------------------------------------------------------------');
  if p_mode = 'A' then
    dbms_output.put_line('                             Activating Client Links                            ');
    v_contract_status_from := bw_const.STA_CUST_SUSPENDED;
    v_contract_status_to := bw_const.STA_CUST_ACTIVE;
  else
    dbms_output.put_line('                           De-Activating Client Links                           ');
    v_contract_status_from := bw_const.STA_CUST_ACTIVE;
    v_contract_status_to := bw_const.STA_CUST_SUSPENDED;
  end if;
  dbms_output.put_line('--------------------------------------------------------------------------------');
  --
  for rec in (
    select *
    from   cis_client_links lnks
    where  institution_number = p_inst_no
    and    contract_status = v_contract_status_from
    and    effective_date = (select max(effective_date)
                             from   cis_client_links lnks2
                             where  lnks.institution_number = lnks2.institution_number
                             and    lnks.client_number = lnks2.client_number
                             and    lnks.group_number  = lnks2.group_number
                             and    lnks.service_contract_id = lnks2.service_contract_id
                             and    lnks.client_level = lnks2.client_level
                             and    lnks.effective_date <= bw_lib_incl.gstrPostingDate)
  ) loop
    --
    v_message := null;
    rt_links := rec;
    --
    if rec.effective_date >= bw_lib_incl.gstrPostingDate then
      --
      rt_links.contract_status := v_contract_status_to;
      rt_links.audit_trail := bw_code_library.CreateAuditTrail;
      --
      update cis_client_links
      set contract_status = rt_links.contract_status,
          audit_trail = rt_links.audit_trail
      where institution_number = rec.institution_number
      and   client_number = rec.client_number
      and   group_number  = rec.group_number
      and   service_contract_id = rec.service_contract_id
      and   client_level = rec.client_level
      and   effective_date = rec.effective_Date;
      --
    else
      --
      rt_links.contract_status := v_contract_status_to;
      rt_links.effective_date := bw_lib_incl.gstrPostingDate;
      rt_links.audit_trail := bw_code_library.CreateAuditTrail;
      --
      insert into cis_client_links
      values rt_links;
    end if;
    --
    if sql%rowcount = 1 then
      --
      if p_mode = 'A' then
        v_message := 'Contract Status for Client Number ['||rec.client_number||'] was set to Active ['||bw_const.STA_CUST_ACTIVE||']. ';
      else
        v_message := 'Contract Status for Client Number ['||rec.client_number||'] was set to Suspended ['||bw_const.STA_CUST_SUSPENDED||']. ';
      end if;
      --
    end if;
    --
    dbms_output.put_line(v_message);
    --
  end loop;
  --
  commit;
  --
exception
  when err_cannot_initialise then
    raise_application_error(-20100,'Cannot initialise global variables.'||sqlerrm);
  when err_invalid_mode then
    raise_application_error(-20100,'Invalid Mode passed! Valid Modes A - Active or S - Suspended!'||sqlerrm);
end enable_disable_links;