function GetDisputeReqActionCount
(p_userid varchar2,
 p_date varchar2,
 p_institution varchar2)
return number result_cache RELIES_ON (int_sundry_history, int_transactions)
is
  v_return number;
  v_min_date varchar2(8);
  v_max_date varchar2(8);
begin
  --
  v_max_date:= to_char(to_date(p_date, 'YYYYMMDD') - 34, 'YYYYMMDD');
  v_min_date:= to_char(to_date(p_date, 'YYYYMMDD') - 365, 'YYYYMMDD');
  --
  if (p_userid is null) then
	  select count(*)
	  into v_return
	  FROM  int_sundry_history h1
	  JOIN  int_sundry_history h2
	  ON    h1.institution_number  = h2.institution_number
	  AND   h1.case_number         = h2.case_number
	  JOIN  int_transactions trn
	  ON    h1.sundry_transaction_slip = trn.transaction_slip
	  AND   h1.institution_number      = trn.institution_number
	  WHERE h1.sundry_type IN ('001')                 --chargeback
	  AND   h2.rule_action = '110'
	  AND   h2.sundry_status NOT IN ('015', '017')    --closed , cancelled
	  AND   trn.record_date <= v_max_date
	  AND   trn.record_date >= v_min_date
	  AND   trn.transaction_status = '009'            --cleared
	  AND NOT EXISTS
	      (SELECT 1
	      FROM  int_sundry_history h3
	      WHERE sundry_type = '003'           --representment
	      AND   h3.institution_number = h1.institution_number
	      AND   h3.case_number = h1.case_number
	      AND   h3. sundry_history_id > h1.sundry_history_id)
	  and h1.institution_number = p_institution;
	  --
	else
      --
	  select count(*)
	  into v_return
	  FROM  int_sundry_history h1
	  JOIN  int_sundry_history h2
	  ON    h1.institution_number  = h2.institution_number
	  AND   h1.case_number         = h2.case_number
	  JOIN  int_transactions trn
	  ON    h1.sundry_transaction_slip = trn.transaction_slip
	  AND   h1.institution_number      = trn.institution_number
	  WHERE h1.sundry_type IN ('001')                 --chargeback
	  AND   h2.rule_action = '110'
	  AND   h2.sundry_status NOT IN ('015', '017')    --closed , cancelled
	  AND   trn.record_date <= v_max_date
	  AND   trn.record_date >= v_min_date
	  AND   trn.transaction_status = '009'            --cleared
	  AND NOT EXISTS
	      (SELECT 1
	      FROM  int_sundry_history h3
	      WHERE sundry_type = '003'           --representment
	      AND   h3.institution_number = h1.institution_number
	      AND   h3.case_number = h1.case_number
	      AND   h3. sundry_history_id > h1.sundry_history_id)
	  and h1.institution_number = p_institution
	  and h1.user_id = p_userid;
	end if;
  --
  return v_return;
  --
end;