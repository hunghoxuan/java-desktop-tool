procedure activate_client_details (
  p_src_inst_no    in varchar2,
  p_dest_inst_no   in varchar2,
  p_station_no     in varchar2,
  p_user_id        in varchar2
)
is
  n_init_return    number;
  v_message        varchar2(500);
  --
  err_cannot_initialise exception;
  --
begin
  --
  bw_prc_res.InitGlobalVars(p_src_inst_no, p_station_no, p_user_id, n_init_return);
  --
  if n_init_return = 0 then
    --
    raise err_cannot_initialise;
    --
  end if;
  --
  --Activate Client Details
  --
  dbms_output.put_line('--------------------------------------------------------------------------------');
  dbms_output.put_line('                           Activating Client Details                            ');
  dbms_output.put_line('--------------------------------------------------------------------------------');
  --
  for rec in (
    select client_number, rowid
    from   cis_client_details
    where  institution_number = p_dest_inst_no
    and    client_status = bw_const.STA_CUST_SUSPENDED
  ) loop
    --
    v_message := null;
    --
    update cis_client_details
    set    client_status = bw_const.STA_CUST_ACTIVE
    where  rowid = rec.rowid;
    --
    if sql%rowcount = 1 then
      --
      v_message := 'Details for Client Number ['||rec.client_number||'] were set to Active ['||bw_const.STA_CUST_ACTIVE||'] on Destination Institution ['||p_dest_inst_no||']. ';
      --
    end if;
    --
    update cis_client_details
    set    client_status = bw_const.STA_CUST_SUSPENDED
    where  institution_number = p_src_inst_no
    and    client_number = rec.client_number;
    --
    if sql%rowcount = 1 then
      --
      v_message := v_message || 'Details for Client Number ['||rec.client_number||'] were set to Suspended ['||bw_const.STA_CUST_SUSPENDED||'] on Source Institution ['||p_src_inst_no||']. ';
      --
    elsif sql%rowcount = 0 then
      --
      v_message := v_message || 'Client Details for Client Number ['||rec.client_number||'] were not found on Source Institution ['||p_src_inst_no||']!';
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
end activate_client_details;