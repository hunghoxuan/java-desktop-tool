procedure activate_client_links (
  p_src_inst_no    in varchar2,
  p_dest_inst_no   in varchar2,
  p_station_no     in varchar2,
  p_user_id        in varchar2
)
is
  n_init_return    number;
  v_message        varchar2(500);
  rt_links         cis_client_links%rowtype;
  rt_links2        cis_client_links%rowtype;
  --
  err_cannot_initialise exception;
  e_no_page             exception;
  e_no_component        exception;
  --
  mv_activation_page         sys_configuration.config_value%type;
  mv_activation_component    sys_configuration.config_value%type;
  mv_active_code_valid       sys_configuration.config_value%type;
  mv_uns_id                  sys_configuration.config_value%type;
  --
  procedure activate_portal(
    p_client_num    in varchar2,
    p_inst_no       in varchar2,
    p_group_num     in varchar2
  )
  is
    --
    type typbrandtable is record (
      column_name varchar2(100)
      );
    --
    type tabtypbrandtable is table of typbrandtable index by pls_integer;
    tab_brandtable tabtypbrandtable;
    --
    type typcpsbrand is record(
      column_value	varchar2(100),
      column_name   varchar2(30),
      table_name    varchar2(30),
      brand_id      varchar2(3)
    );
    type tcpsbrand is table of typcpsbrand;
    typ_Brand         tcpsbrand;
    --
    v_fact              bw_uns_mdf.TYP_REC_FACT;
    v_data              bw_uns_mdf.TYPTAB_FACT;
    --
    v_sql               varchar2(4000);
    v_brand_id          varchar2(3) := null;
    v_activation_url    sys_configuration.config_value%type;
    v_language_id       varchar2(3);
    v_language_index    varchar2(3);
    --
    row_user_info       cps_user_information%rowtype;
    row_user_activation cps_portal_user_activation%rowtype;
    row_brand_info      cps_merchant_portal_captions%rowtype;
    --
    e_user_info         exception;
    e_user_activation   exception;
    e_no_brand_id       exception;
    e_no_url            exception;
    e_uns               exception;
    --
    cursor c_brand_info (cp_brand_id in varchar2, cp_lang in varchar2)
    is
      --
      select *
      from cps_merchant_portal_captions
      where brand_id = cp_brand_id
      and language_id = cp_lang
      and controlindex >= '9000';
    --
  begin
    --
    begin
      select *
        into row_user_info
      from   cps_user_information
      where  institution_number = p_inst_no
      and    client_number = p_client_num
      and    group_number = p_group_num
      and    status = '003';
    exception
      when no_data_found then
        raise e_user_info;
    end;
    --
    row_user_activation.userid          := row_user_info.userid;
    row_user_activation.activation_code := upper(sys_guid());
    row_user_activation.pin_no_sent     := null;
    row_user_activation.activation_code_sent := sysdate;
    --
    insert into cps_portal_user_activation
    values row_user_activation;
    --
    if sql%rowcount = 0 then
      raise e_user_activation;
    end if;
    --
    v_fact.key         := 'EMAIL';
    v_fact.value       := row_user_info.portal_email_addr;
    v_data(v_fact.key) := v_fact;
    --
    v_fact.key         := 'FULL_NAME';
    v_fact.value       := row_user_info.first_name;
    v_data(v_fact.key) := v_fact;
    --
    v_fact.key         := 'CLIENT_NUMBER';
    v_fact.value       := p_client_num;
    v_data(v_fact.key) := v_fact;
    --
    if not typ_Brand.exists(1) then
      --
      typ_Brand   := TCpsBrand();
      --
      for rec in (
        select column_value, column_name , table_name, brand_id
        from cps_brand
        order by priority
      ) loop
        --
        typ_brand.extend;
        typ_brand(typ_brand.last).column_value := rec.column_value;
        typ_brand(typ_brand.last).column_name := rec.column_name ;
        typ_brand(typ_brand.last).table_name := rec.table_name;
        typ_brand(typ_brand.last).brand_id := rec.brand_id;
        --
      end loop;
      --
    end if;
    --
    For i in 1..typ_Brand.last
    loop
      --
      if typ_brand(i).column_value <> '999' then
        --
        if i = 1 or  (typ_brand(i).column_name <> typ_brand(i-1).column_name or
                typ_brand(i).table_name  <> typ_brand(i-1).table_name) then
          --
          v_sql := 'select '||typ_brand(i).column_name|| '
                    from ' || typ_brand(i).table_name || ' X' ||
                  ' where client_number   =  :client_number
                    and group_number =  :group_number
                    and institution_number = :inst_no'
                    ;
          --
          execute immediate v_sql
          bulk collect into tab_BrandTable
          using p_client_num, p_group_num, p_inst_no;
          --
        end if;
        --
        for t in 1..tab_BrandTable.count
        loop
          --
          if tab_brandtable(t).column_name = typ_brand(i).column_value then
            v_brand_id := typ_brand(i).brand_id;
          end if;
          --
          exit when v_brand_id is not null ;
          --
        end loop;
        --
      else
        --
        v_brand_id := typ_Brand(i).brand_id;
        --
      end if;
      --
      exit when v_brand_id is not null ;
      --
    end loop;
    --
    If v_brand_id is null then
      --
      raise e_no_brand_id;
      --
    end if;
    --
    v_activation_url := bw_lib_scri.GetChoiceDisplayValue('bwt_cps_brand', 'index_field', 'url', v_brand_id);
    --
    if v_activation_url is null then
      --
      raise e_no_url;
      --
    end if;
    --
    v_activation_url := v_activation_url || mv_activation_page || '?activation_code=' ||row_user_activation.activation_code||mv_activation_component;
    --
    v_fact.key         := 'ACTIVATION_URL';
    v_fact.value       := v_activation_url;
    v_data(v_fact.key) := v_fact;
    --
    begin
      execute immediate 'select language_index '
                      ||'from   bw_warp.locales '
                      ||'where  object_id = :locale' into v_language_index using row_user_info.locale;
      --
      v_language_id := bw_lib_scri.GetChoiceDisplayValue('bwt_client_language', 'index_field', 'country_code', v_language_index);
    exception
      when no_data_found then
        null;
    end;
    --
    v_fact.key         := 'LANGUAGE';
    v_fact.value       := v_language_id;
    v_data(v_fact.key) := v_fact;
    --
    v_fact.key         := 'PORTAL_LOGIN_NAME';
    v_fact.value       := row_user_info.login_name;
    v_data(v_fact.key) := v_fact;
    --
    v_fact.key         := 'ACTIVATION_CODE_VALIDITY';
    v_fact.value       := mv_active_code_valid;
    v_data(v_fact.key) := v_fact;
    --
    open c_brand_info(v_brand_id, v_language_id);
    loop
      --
      fetch c_brand_info into row_brand_info;
      exit when c_brand_info%notfound;
      --
      CASE  row_brand_info.controlindex
        --
        when '9000' then
          --
          v_fact.key         := 'PRODUCT_NAME';
          v_fact.value       := row_brand_info.caption;
          v_data(v_fact.key) := v_fact;
          --
        when '9001' then
          --
          v_fact.key         := 'WEBSITE';
          v_fact.value       := row_brand_info.caption;
          v_data(v_fact.key) := v_fact;
          --
        when '9002' then
          --
          v_fact.key         := 'TELEPHONE_NUMBERS';
          v_fact.value       := row_brand_info.caption;
          v_data(v_fact.key) := v_fact;
          --
        when '9004' then
          --
          v_fact.key         := 'MESSAGE_FROM';
          v_fact.value       := row_brand_info.caption;
          v_data(v_fact.key) := v_fact;
          --
        else
          --
          null;
          --
        end case;
    end loop;
    --
    begin
      bw_uns_mdf.generate_and_queue(
                    p_instno      => p_inst_no,
                    p_uns_id      => mv_uns_id,
                    p_source_id   => null,
                    p_trigger_id  => null,
                    p_message_id  => null,
                    p_data        => v_data,
                    p_generate    => false
      );
    exception
      when others then
        raise e_uns;
    end;
    --
  exception
  when e_user_info then
    dbms_output.put_line('Portal User Information for client ['||p_client_num||'] not found!');
  when e_user_activation then
    dbms_output.put_line('Error when creating User Portal Activation for client ['||p_client_num||']! '|| sqlerrm);
  when e_no_brand_id then
    dbms_output.put_line('The Brand ID to determine the client portal was not found! Please review the setup in cps_brand! Client Number ['||p_client_num||']! ');
  when e_no_url then
    dbms_output.put_line('The portal URL for the respective brand id for this application was not found in bwt_cps_brand! Client Number ['||p_client_num||'] Brand ID ['||v_brand_id||']! ');
  when e_uns then
    dbms_output.put_line('Error sending UNS call!');
  end activate_portal;
  --
begin
  --
  bw_prc_res.InitGlobalVars(p_dest_inst_no, p_station_no, p_user_id, n_init_return);
  --
  if n_init_return = 0 then
    --
    raise err_cannot_initialise;
    --
  end if;
  --
  --Activate Client Links
  --
  dbms_output.put_line('--------------------------------------------------------------------------------');
  dbms_output.put_line('                             Activating Client Links                            ');
  dbms_output.put_line('--------------------------------------------------------------------------------');
  --
  if bw_lib_config.get_config_setting('Web-GUI', 'MerchantPortalActivationPage', p_dest_inst_no, null,
                                      mv_activation_page) <> bw_lib_config.C_PROCESS_COMPLETED then
    mv_activation_page := null;
  end if;
  --
  if mv_activation_page is null then
    raise e_no_page;
  end if;
  --
  if mv_activation_component is null then
    if bw_lib_config.get_config_setting('MerchantPortal', 'ActivationComponent', p_dest_inst_no, null,
                                        mv_activation_component ) <> bw_lib_config.C_PROCESS_COMPLETED then
      mv_activation_component := null;
    end if;
    --
    if mv_activation_component is null then
      raise e_no_component;
    end if;
  end if;
  --
  if bw_lib_config.get_config_setting('MerchantPortal', 'ActivationCodeValidity', p_dest_inst_no, null,
                                      mv_active_code_valid) <> bw_lib_config.C_PROCESS_COMPLETED then
    mv_active_code_valid := null;
  end if;
  --
  if bw_lib_config.get_config_setting('MerchantPortal', 'EmailActivationUNSID', p_dest_inst_no, null,
                                        mv_uns_id) <> bw_lib_config.C_PROCESS_COMPLETED then
    mv_uns_id := null;
  end if;
  --
  for rec in (
    select *
    from   cis_client_links lnks
    where  institution_number = p_dest_inst_no
    and    contract_status = bw_const.STA_CUST_SUSPENDED
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
    if rt_links.effective_date = bw_lib_incl.gstrPostingDate then
      --
      update cis_client_links
      set    contract_status = bw_const.STA_CUST_ACTIVE
      where  institution_number = rt_links.institution_number
      and    client_number = rt_links.client_number
      and    group_number = rt_links.group_number
      and    service_contract_id = rt_links.service_contract_id
      and    client_level = rt_links.client_level
      and    effective_date = rt_links.effective_date;
      --
    else
      --
      rt_links.contract_status := bw_const.STA_CUST_ACTIVE;
      rt_links.effective_date := bw_lib_incl.gstrPostingDate;
      rt_links.audit_trail := bw_code_library.CreateAuditTrail;
      --
      insert into cis_client_links
      values rt_links;
      --
    end if;
    --
    if sql%rowcount = 1 then
      --
      v_message := 'Client Number ['||rec.client_number||'] contract status was set to Active ['||bw_const.STA_CUST_ACTIVE||'] on Destination Institution ['||p_dest_inst_no||']. ';
      --
    end if;
    --
    dbms_output.put_line('INST ['||p_src_inst_no||'] client ['||rt_links.client_number||']');
    --
    for rec2 in (
      select *
      from   cis_client_links lnks
      where  institution_number = p_src_inst_no
      and    contract_status = bw_const.STA_CUST_ACTIVE
      and    client_number = rt_links.client_number
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
      rt_links2 := rec2;
      --
      rt_links2.contract_status := bw_const.STA_CUST_SUSPENDED;
      rt_links2.effective_date := bw_lib_incl.gstrPostingDate;
      rt_links2.audit_trail := bw_code_library.CreateAuditTrail;
      --
      insert into cis_client_links
      values rt_links2;
      --
      if sql%rowcount = 1 then
        --
        v_message := v_message || 'Client Number ['||rec.client_number||'] contract status was set to Suspended ['||bw_const.STA_CUST_SUSPENDED||'] on Source Institution ['||p_src_inst_no||']. ';
        --
      end if;
      --
    end loop;
    --
    --activate_portal(rt_links.client_number, rt_links.institution_number, rt_links.group_number);
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
when e_no_page then -- To move into main Procedure
  dbms_output.put_line('Activation page not defined! Please check configuration setting Web-GUI/MerchantPortalActivationPage!');
when e_no_component then -- To move into main Procedure
  dbms_output.put_line('Activation component not found! Please check configuration setting MerchantPortal/ActivationComponent!');
end activate_client_links;