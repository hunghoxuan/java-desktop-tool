procedure update_cas_am_charges_stats(start_date in varchar2, end_date in varchar2, par_degree in varchar, tid in varchar)
as
  
row_cas_am_charges_stats cas_am_charges_statistics%rowtype;

type tab_am_stats is table of cas_am_charges_statistics%rowtype index by pls_integer;     
tam_stats tab_am_stats;   

v_error varchar2(4000); 

v_billing_acct varchar2(11);   

i_indx pls_integer;

begin
    --
  /*   UPDATE CAS_AM_CHARGES_STATISTICS
	  SET    INSTITUTION_NUMBER = '90000108'
	  WHERE  INSTITUTTION_NUMBER = '00000108'
	  AND    RECORD_DATE BETWEEN start_date AND  end_date;
    */
    for CHARGES in (
	   select      sum(ACCOUNT_AMOUNT) as COMMISSION_AMT, 
	               sum(LOCAL_AMOUNT) as COMMISSION_AMT_LOCAL, 
	               max(GROUP_RECORD_ID_NUMBER) as GROUP_RECORD_ID_NUMBER,
	               sum(ORIG_ACCOUNT_AMOUNT_GR) as CASH_BACK_AMOUNT, 
	               count(*) as NUMBER_SLIPS, 
	               max(CHARGE_INSTITUTION) as CHARGE_INSTITUTION,
	               max(CHARGE_TYPE) as CHARGE_TYPE, 
	               max(MIN_MAX_INDICATOR) as MIN_MAX_INDICATOR
	               ,max(ACCT_CURRENCY) as ACCT_CURRENCY
	               ,max(ENTITY_ID) as ENTITY_ID
	               ,MISC_DATA as MISC_DATA  
	               ,BILLING_ACCT_NUMBER,
	                RECORD_DATE
	        from   INT_ADDENDUM_CHARGES
	        where  institution_number = '00000108'
	        and    MOD(BILLING_ACCT_NUMBER,par_degree) = tid     
	        and    record_date between start_date and end_date   
	        --and    charge_type in ('002', '911')
	        group  by BILLING_ACCT_NUMBER,RECORD_DATE,MISC_DATA 
	)
	loop        
	  --
	  row_cas_am_charges_stats.account_amount         :=    charges.commission_amt;
      row_cas_am_charges_stats.local_amount           :=    charges.COMMISSION_AMT_LOCAL	  ;
	  row_cas_am_charges_stats.group_record_id_number :=    charges.group_record_id_number;
	  row_cas_am_charges_stats.ORIG_ACCOUNT_AMOUNT_GR :=    charges.CASH_BACK_AMOUNT;
	  row_cas_am_charges_stats.NUMBER_SLIPS           :=    charges.NUMBER_SLIPS;
	  row_cas_am_charges_stats.CHARGE_INSTITUTION     :=    charges.CHARGE_INSTITUTION;
	  row_cas_am_charges_stats.CHARGE_TYPE            :=    charges.CHARGE_TYPE;
	  row_cas_am_charges_stats.MIN_MAX_INDICATOR      :=    nvl(charges.MIN_MAX_INDICATOR,000);
	  row_cas_am_charges_stats.INSTITUTION_NUMBER     :=    '00000108';                
	  v_billing_acct := charges.BILLING_ACCT_NUMBER; 
	  row_cas_am_charges_stats.BILLING_ACCT_NUMBER    :=    charges.BILLING_ACCT_NUMBER;      
	  row_cas_am_charges_stats.RECORD_DATE            :=    charges.RECORD_DATE;
	  row_cas_am_charges_stats.CYCLE                  :=    '001';
	  row_cas_am_charges_stats.ORIG_LOCAL_AMOUNT_GR   :=    charges.CASH_BACK_AMOUNT;
	  row_cas_am_charges_stats.ENTITY_ID              :=    '000';
	  row_cas_am_charges_stats.MISC_DATA              :=    nvl(charges.MISC_DATA,'0') ;
	  tam_stats(tam_stats.count+1)                    :=    row_cas_am_charges_stats;         
	  --
	  if  tam_stats.count = 500 then
	   forall ind in 1..tam_stats.count  SAVE EXCEPTIONS
	     insert into cas_am_charges_statistics
	     values tam_stats(ind);
	     
	   tam_stats.delete;  
	   
	   commit;
	  end if;
	  
	  
	end loop;  
	--       
	
	if tam_stats.count > 0 then
		forall ind in 1..tam_stats.count SAVE EXCEPTIONS
		     insert into cas_am_charges_statistics
		     values tam_stats(ind);
	    tam_stats.delete;	       
	end if;        

    commit;

      
exception
when others then                              
 rollback;
   dbms_output.put_line(dbms_utility.format_error_backtrace());  
     FOR indx IN 1 .. SQL%BULK_EXCEPTIONS.COUNT  
      LOOP                            
       i_indx := SQL%BULK_EXCEPTIONS(indx).ERROR_INDEX;
       v_error := substr(tam_stats(i_indx).billing_acct_number || '|'|| sqlerrm,1,3500); 
       insert into stat_error(error)  values (v_error);       
      end loop;  
 commit;       
 
 raise;
end;
