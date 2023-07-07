Function RPT_GENERATE_DAILY_INTEREST(
	RPT_ID 				in varchar2,    --(Institution Number || Installation Number || Full Date - YYYYMMDDHH24MMSS)
	p_DateFrom 			in varchar2,
	p_DateTo 			in Varchar2,
	p_AcctRangeFrom 	in varchar2 default Null,
	p_AcctRangeTo 		in Varchar2 default Null,
	p_inst_filter		in Varchar2 default Null, -- If Null, all Institutions will be loaded
	p_service_contract 	in Varchar2 default Null, -- If Null, all contracts will be loaded
	p_account_type_id	in Varchar2 default Null, -- If Null, all account types will be loaded
	p_account_currency  in Varchar2 default Null, -- If Null, all currencies will be loaded
	p_sub_balance		in Varchar2 default Null -- If null, all sub balances will be loaded
) return varchar2
Is
	v_return varchar2(1000) := 0;
	-- Possible outputs:
	-- 0 - Count() = Correct
	-- n + Description
	--
	r_rpt_daily_interest RPT_DAILY_INTEREST%rowtype;
	r_rpt_daily_interest_empty RPT_DAILY_INTEREST%rowtype;
	v_inst_no Varchar2(8);
	v_inst_name Varchar2(50);
	n_recs Number := 0;
Begin
	--
	v_inst_no := substr(RPT_ID, 1, 8);
	--
	select INSTITUTION_NAME into v_inst_name
	From sys_institution_licence
	where institution_number = v_inst_no;
	--
	-- Load Main Interest Data
	for VDB in (
		Select VDB.*, SUBB.SUB_BALANCE as SUB_BALANCE_TXT, ACCT.GROUP_NUMBER, CURR.SWIFT_CODE, TYP.TYPE_ID
		From CAS_INTEREST_VALUE_BALANCE VDB, CAS_CLIENT_ACCOUNT ACCT, BWT_SUB_BALANCE SUBB, BWT_CURRENCY CURR, BWT_ACCOUNT_TYPE_ID TYP
		WHERE VDB.INSTITUTION_NUMBER = v_inst_no
		And VDB.VALUE_DATE >= p_DateFrom
		and VDB.VALUE_DATE <= p_DateTo
		and VDB.RECORD_TYPE = '100' --DR Interest Only
		and VDB.SUB_BALANCE not in ('000','999','900','903','904')
		and (VDB.SUB_BALANCE = p_sub_balance or p_sub_balance is null)
		and ACCT.INSTITUTION_NUMBER = VDB.INSTITUTION_NUMBER
		and ACCT.ACCT_NUMBER = VDB.ACCT_NUMBER
		And (ACCT.ACCT_NUMBER >= p_AcctRangeFrom or p_AcctRangeFrom is null)
		And (ACCT.ACCT_NUMBER <= p_AcctRangeTo or p_AcctRangeTo is null)
		And (ACCT.ACCOUNT_TYPE_ID = p_account_type_id or p_account_type_id is null)
		And (ACCT.ACCT_CURRENCY = p_account_currency or p_account_currency is null)
		And (ACCT.SERVICE_CONTRACT_ID = p_service_contract or p_service_contract is null)
		and SUBB.INSTITUTION_NUMBER = VDB.INSTITUTION_NUMBER
		and SUBB.language = 'USA'
		and SUBB.INDEX_FIELD = VDB.SUB_BALANCE
		and CURR.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
		and CURR.LANGUAGE = 'USA'
		and CURR.ISO_CODE = ACCT.ACCT_CURRENCY
		and TYP.LANGUAGE = 'USA'
		And TYP.INSTITUTION_NUMBER = ACCT.INSTITUTION_NUMBER
		And TYP.INDEX_FIELD = ACCT.ACCOUNT_TYPE_ID
		Order by VDB.acct_number, VDB.SUB_BALANCE, VDB.RECORD_DATE
	)
	Loop
		n_recs := n_recs + 1;
		--
		--
		-- Refresh structure
		r_rpt_daily_interest := r_rpt_daily_interest_empty;
		--
		-- Populate main data
		r_rpt_daily_interest.rpt_id 				:= RPT_ID;
		r_rpt_daily_interest.ID 					:= n_recs;
		r_rpt_daily_interest.DATE_START				:= p_DateFrom;
		r_rpt_daily_interest.DATE_END				:= p_DateTo;
		r_rpt_daily_interest.INSTITUTION_NAME		:= v_inst_name;
		r_rpt_daily_interest.MAIN_ACCT_NUMBER		:= VDB.ACCT_NUMBER;
		r_rpt_daily_interest.ACCT_NUMBER 			:= VDB.ACCT_NUMBER;
		r_rpt_daily_interest.GROUP_NUMBER 			:= VDB.GROUP_NUMBER;
		r_rpt_daily_interest.ACCT_CURRENCY 			:= VDB.SWIFT_CODE;
		r_rpt_daily_interest.VALUE_DATE 			:= VDB.value_date;
		r_rpt_daily_interest.DAILY_ACCRUED_INTEREST := NVL(VDB.ACCRUED_INTEREST, '0');
		r_rpt_daily_interest.SUB_BALANCE 			:= VDB.SUB_BALANCE_TXT;
		r_rpt_daily_interest.DAILY_BALANCE 			:= VDB.BALANCE;
		r_rpt_daily_interest.ACCT_TYPE 				:= VDB.TYPE_ID;
		--
		if (to_number(VDB.BALANCE) > 0 or to_number(VDB.BALANCE) < 0) and VDB.INTEREST_RATE is not null  then
			r_rpt_daily_interest.DAILY_INTEREST_RATE:= to_char(round((to_number(VDB.ACCRUED_INTEREST) / to_number(VDB.BALANCE)) * 100, 8)) || '%';
		ElsIf VDB.INTEREST_RATE is NUll  then
			-- If Empty, set 0%
			r_rpt_daily_interest.DAILY_INTEREST_RATE:=  '0%';
		Else
			-- If Balance is - then the rate is obviously zero!
			r_rpt_daily_interest.DAILY_INTEREST_RATE := '0%';
		End if;
		--
		if VDB.INTEREST_RATE is not null then
			-- Add the percentage sign only when a value is valid.
			r_rpt_daily_interest.EARP 					:= VDB.INTEREST_RATE || '%';
		else
		    r_rpt_daily_interest.EARP 					:=  '0%';
		End if;
		--
		Insert into RPT_DAILY_INTEREST values r_rpt_daily_interest;
	End Loop;
	--
	--
	return v_return || ' - ' || to_char(n_recs);
Exception
	When others then
		return  '1 - ' || sqlerrm;
End;