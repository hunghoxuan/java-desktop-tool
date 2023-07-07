procedure     DINERS_REPORTS (
  p_period_start    in varchar2,
  p_period_end      in varchar2
) is
begin
  --
  delete tmp_number_of_merchant;
  --
  insert into tmp_number_of_merchant (
    institution_number,
    status,
    group_number,
    contract_status,
    effective_date,
    record_date,
    client_number,
    company_name,
    business_class,
    mcc
  )
      SELECT ccd.institution_number,
             DECODE (ccl.contract_status, '001', '001', '002') status,
             ccl.group_number, ccl.contract_status, ccl.effective_date,
             ccd.record_date, ccd.client_number, ccd.company_name,
             ccd.business_class, bgrup.group_name || '(' || bclass.diners_mcc || ')' as MCC
        FROM cis_client_links ccl,
             cis_client_details ccd,
             cis_device_link cdl,
             bwt_iso_buss_class bclass,
             bwt_iso_buss_class_grp bgrup
       WHERE ccd.record_type = '003'
         AND ccd.client_number = ccl.client_number
         AND ccd.institution_number = ccl.institution_number
         AND ccl.client_level = '001'
         AND ccl.effective_date =
                (SELECT MAX (effective_date)
                   FROM cis_client_links
                  WHERE client_number = ccl.client_number
                    AND institution_number = ccl.institution_number
                    AND group_number = ccl.group_number
                    AND service_contract_id = ccl.service_contract_id
                    AND client_level = ccl.client_level
                    AND effective_date <= p_period_end)
         AND cdl.institution_number(+) = ccd.institution_number
         AND cdl.client_number(+) = ccd.client_number
         AND ccd.institution_number = bclass.institution_number
         AND ccd.business_class = bclass.index_field
         AND bclass.language = 'USA'
         AND bclass.institution_number = bgrup.institution_number(+)
         AND bclass.language = bgrup.language(+)
         AND bclass.group_id = bgrup.index_field(+);
  --
  delete from tmp_active_clients;
  --
  insert into tmp_active_clients (
    client_number
  )
     SELECT DISTINCT (bch.client_number)
                 FROM int_transactions bch,
                      int_transactions tran,
                      int_file_log_details flog
                WHERE flog.record_date BETWEEN p_period_start AND p_period_end
                  AND flog.processing_status = '002'
                  AND flog.process_name = '670'
                  AND tran.institution_number = flog.institution_number
                  AND tran.file_number_outward = flog.file_number
                  AND tran.transaction_class = '002'
                  AND tran.transaction_category = '001'
                  AND tran.transaction_status = '009'
                  AND tran.transaction_type IN ('005', '006')
                  AND bch.transaction_slip = tran.summary_settlement
                  AND bch.institution_number = tran.institution_number
                  AND bch.transaction_class = '001';
  --

  delete TEMP_DINERS_REPORT;

  insert into TEMP_DINERS_REPORT (
  	INSTITUTION_NUMBER,
	INSTITUTION_NAME,
	MCC,
	STAGE,
	DATA_COUNT
  )
	SELECT 	 merch.institution_number,
			 lic.institution_name,
			 merch.mcc,
	    	 'Current' AS stage,
	    	 COUNT (DISTINCT (merch.group_number)) data_count
	FROM tmp_number_of_merchant merch,
			 sys_institution_licence  lic
	WHERE merch.institution_number = lic.institution_number
			 and status = '001' AND merch.record_date || '' < p_period_start
	    	 GROUP BY merch.mcc, merch.institution_number, lic.institution_name
    UNION ALL
    SELECT   merch.institution_number,
			 lic.institution_name,
		     merch.mcc,
             'Prospect' AS stage,
             COUNT (DISTINCT (merch.group_number)) data_count
        FROM tmp_number_of_merchant merch,
        	 sys_institution_licence  lic
       WHERE merch.institution_number = lic.institution_number
       		 and status = '001'
             AND merch.record_date BETWEEN p_period_start AND p_period_end
    GROUP BY merch.mcc, merch.institution_number, lic.institution_name
    UNION ALL
    SELECT   merch.institution_number,
			 lic.institution_name,
			 merch.mcc,
             'Inactive' AS stage,
             COUNT (DISTINCT (merch.group_number)) data_count
        FROM tmp_number_of_merchant merch,
        	 sys_institution_licence  lic
       WHERE merch.institution_number = lic.institution_number
       		 and status = '002'
         	 AND merch.contract_status BETWEEN '002' AND '020'
        	 AND merch.effective_date BETWEEN p_period_start AND p_period_end
    GROUP BY merch.mcc, merch.institution_number, lic.institution_name
    UNION ALL
    SELECT   merch.institution_number,
			 lic.institution_name,
			 merch.mcc,
             'Active' AS stage,
             COUNT (DISTINCT (merch.group_number)) data_count
    	FROM tmp_number_of_merchant merch,
    		 sys_institution_licence  lic
       WHERE merch.institution_number = lic.institution_number
       		 and status = '001'
         	 AND merch.client_number IN (SELECT client_number
                                       FROM tmp_active_clients)
         	 AND merch.effective_date <= p_period_end
    		GROUP BY merch.mcc, merch.institution_number, lic.institution_name;

exception
when others then
  raise;
end diners_reports;