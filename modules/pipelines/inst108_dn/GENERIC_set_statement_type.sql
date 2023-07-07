declare
	vDate varchar2(8);
begin
	select to_char(sysdate,'YYYYMMDD') into vDate from dual;
	--
	update 	cas_client_account cca
		set cca.statement_type = '001', cca.record_date = vDate, cca.audit_trail = 'RS2_STATEMENT_TYPE_UPDATED'
	where 	cca.institution_number = '00000108'
	and 	cca.statement_type <> '001'
	and		cca.service_contract_id = '101'
	and		cca.client_level = '001'
	and 	cca.client_number in (
		select 	scs.client_number
		from 	svc_client_service scs
		where	scs.institution_number = '00000108'
		and		scs.service_id not in ('114','408')
		and		scs.client_tariff in (select client_tariff from cbr_transaction_charges where institution_number = '00000108' and charge_type = '002' and fee_category = '501')
		-- enable this option to update statement type for all client tariff changes before this cycle end date
		and		scs.effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
		-- enable this option to update statement type for all client tariff changes within this cycle
		--and		scs.effective_date between (select current_cycle_start from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        --                                and (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        and     scs.effective_date = (select max(effective_date) from svc_client_service
																	where institution_number = scs.institution_number and client_number = scs.client_number and service_id not in ('114','408')
                                                                        and effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020'))
	);
	dbms_output.put_line(SQL%ROWCOUNT);
	update 	cas_client_account cca
		set cca.statement_type = '002', cca.record_date = vDate, cca.audit_trail = 'RS2_STATEMENT_TYPE_UPDATED'
	where 	cca.institution_number = '00000108'
	and		cca.statement_type <> '002'
	and		cca.service_contract_id = '101'
	and		cca.client_level = '001'
	and 	cca.client_number in (
		select 	scs.client_number
		from 	svc_client_service scs
		where	scs.institution_number = '00000108'
		and		scs.service_id not in ('114','408')
		and		scs.client_tariff in (select client_tariff from cbr_transaction_charges where institution_number = '00000108' and charge_type = '002' and fee_category = '502')
		-- enable this option to update statement type for all client tariff changes before this cycle end date
		and		scs.effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
		-- enable this option to update statement type for all client tariff changes within this cycle
		--and		scs.effective_date between (select current_cycle_start from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        --                                and (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        and     scs.effective_date = (select max(effective_date) from svc_client_service
																	where institution_number = scs.institution_number and client_number = scs.client_number and service_id not in ('114','408')
                                                                        and effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020'))
	);
	dbms_output.put_line(SQL%ROWCOUNT);
	--KIWI BANK SECTION
	update 	cas_client_account cca
		set cca.statement_type = '008', cca.record_date = vDate, cca.audit_trail = 'RS2_STATEMENT_TYPE_UPDATED'
	where 	cca.institution_number = '00000108'
	and		cca.statement_type <> '008'
	and		cca.service_contract_id = '102'
	and		cca.client_level = '001'
	and 	cca.client_number in (
		select 	scs.client_number
		from 	svc_client_service scs
		where	scs.institution_number = '00000108'
		and		scs.service_id not in ('114','408')
		and		scs.client_tariff in (select client_tariff from cbr_transaction_charges where institution_number = '00000108' and charge_type = '002' and fee_category = '501')
		-- enable this option to update statement type for all client tariff changes before this cycle end date
		and		scs.effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
		-- enable this option to update statement type for all client tariff changes within this cycle
		--and		scs.effective_date between (select current_cycle_start from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        --                                and (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        and     scs.effective_date = (select max(effective_date) from svc_client_service
																	where institution_number = scs.institution_number and client_number = scs.client_number and service_id not in ('114','408')
                                                                        and effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020'))
	);
	dbms_output.put_line(SQL%ROWCOUNT);
	update 	cas_client_account cca
		set cca.statement_type = '007', cca.record_date = vDate, cca.audit_trail = 'RS2_STATEMENT_TYPE_UPDATED'
	where 	cca.institution_number = '00000108'
	and		cca.statement_type <> '007'
	and		cca.service_contract_id = '102'
	and		cca.client_level = '001'
	and 	cca.client_number in (
		select 	scs.client_number
		from 	svc_client_service scs
		where	scs.institution_number = '00000108'
		and		scs.service_id not in ('114','408')
		and		scs.client_tariff in (select client_tariff from cbr_transaction_charges where institution_number = '00000108' and charge_type = '002' and fee_category = '502')
		-- enable this option to update statement type for all client tariff changes before this cycle end date
		and		scs.effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
		-- enable this option to update statement type for all client tariff changes within this cycle
		--and		scs.effective_date between (select current_cycle_start from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        --                                and (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020')
        and     scs.effective_date = (select max(effective_date) from svc_client_service
																	where institution_number = scs.institution_number and client_number = scs.client_number and service_id not in ('114','408')
                                                                        and effective_date <= (select current_cycle_end from cbr_billing_cycle where institution_number = '00000108' and billing_cycle = '020'))
	);
	dbms_output.put_line(SQL%ROWCOUNT);
commit;
end;
/
