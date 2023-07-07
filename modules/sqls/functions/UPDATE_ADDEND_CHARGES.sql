procedure     update_addend_charges(start_date in varchar2, end_date in varchar2, par_degree in varchar, tid in varchar)
as

  --
  type upd_struc is record(
    transaction_slip int_transactions.transaction_slip%type,
    transaction_amount int_addendum_charges.transaction_amount%type,
    settlement_amount int_addendum_charges.settlement_amount%type,
    local_amount int_addendum_charges.local_amount%type,
    account_amount int_addendum_charges.account_amount%type,
    record_id_number int_addendum_charges.record_id_number%type,
    group_record_id_number int_addendum_charges.group_record_id_number%type,
    misc_data int_addendum_charges.misc_data%type,
	misc_data_901 int_addendum_charges.misc_data%type,
	misc_data_911 int_addendum_charges.misc_data%type,
    rowid_trn rowid,       
    rowid_chg rowid,
	rowid_901 rowid,
	rowid_911 rowid,
	int_trn_rowid rowid,
    o_transaction_amount int_addendum_charges.transaction_amount%type,
    o_settlement_amount int_addendum_charges.settlement_amount%type,
    o_local_amount int_addendum_charges.local_amount%type,
    o_account_amount int_addendum_charges.account_amount%type,
    o_misc_data int_addendum_charges.misc_data%type,
	o_misc_data_901 int_addendum_charges.misc_data%type,
	o_misc_data_911 int_addendum_charges.misc_data%type,
    o_group_record_id_number int_addendum_charges.group_record_id_number%type,
    transaction_type int_addendum_charges.transaction_type%type,
    FAIL_STAGE_2 varchar2(2),
    ERR_DESC varchar2(4000)
  );
  --
  type typfail is record(
    rowid_trn rowid,
    FAIL_STAGE_2 varchar2(2),
    ERR_DESC varchar2(4000)
  );
  type coltyp_typfail is table of typfail index by pls_integer;
  
  type coltyp_upd_struc is table of upd_struc index by binary_integer;
  
  tab_update_struc coltyp_upd_struc;
  tab_fail coltyp_typfail;
  
  counter pls_integer := 0;
  
  v_fee_rule  bwt_fee_identifier.fee_rule%type;
  v_ic_rate varchar2(10);
  v_ic_fee  varchar2(10);  
  v_area_lookup varchar2(3);
  v_area_lookup_new varchar2(3);
  v_area_lookup_old varchar2(3);
  --
  v_pp_service_type varchar2(3);
  v_bilateral_code varchar2(50);
  v_bilateral_code_desc varchar2(100);
  v_audit_T     varchar2(50) := to_Char(sysdate, 'YYYYMMDDHH24MISS')||'_'||start_date||end_date||par_degree||tid;      
  v_position number;

begin
    --
    tab_update_struc.delete;
    --
--THIS SCRIPT IS FOR VISA PREPAID ONLY
       bw_lib_incl.gstrinstitutionnumber := '00000108';    --
    for rec_addend in
    (
     select /*+ INDEX(chg PK_INT_ADDENDUM_CHARGES)*/ tmp.rowid as tmp_rowid, tmp.transaction_slip, chg.acct_currency, chg.tran_currency, chg.settlement_currency, chg.record_date,
            chg.transaction_amount, chg.settlement_amount, chg.local_amount, chg.account_amount, tmp.new_record_id_number, chg.misc_data, trn.misc_data as misc_data_trn,
            trn.authorized_by,  tmp.aoe, trn.int_fee_rule, chg.orig_account_amount_gr, tmp.new_service_type, tmp.f_f_tt, chg.group_record_id_number ,
            tmp.new_group_record_id_number, trn.rowid as trn_rowid, chg.rowid as chg_rowid, tmp.F_T_A, tmp.F_S_A, tmp.F_L_A
            
     from   tmp_bnz_20220729 tmp,
            --int_file_log_details file_log,
            int_addendum_charges chg,
            int_transactions trn
     where  tmp.fail_stage is null
     and    mod(tmp.file_number, par_degree) = tid     
     --and    file_log.institution_number= '00000108'
     --and    file_log.file_number = tmp.file_number
     --and    file_log.record_date between start_date and end_date

     and    trn.institution_number = '00000108'
     and    trn.transaction_slip = tmp.transaction_slip
     and    trn.file_number = tmp.file_number
     and    chg.institution_number = trn.institution_number
     and    chg.transaction_slip = tmp.transaction_slip
     and    chg.charge_type='002'
     and    chg.file_number='00000000'   
     --and    chg.misc_data is not null                                  
    )
    loop
  
       --
--       dbms_output.put_line(rec_Addend.transaction_Slip);
       --
       bw_lib_incl.gstrPostingDate := rec_addend.record_date;
       
       if counter = 500 then
         forall indx in  1..counter 
           update int_addendum_charges
           set 
               record_id_number = tab_update_struc(indx).record_id_number
              ,group_record_id_number = tab_update_struc(indx).group_record_id_number
              ,transaction_amount =  tab_update_struc(indx).transaction_amount
              ,settlement_amount = tab_update_struc(indx).settlement_amount
              ,local_amount = tab_update_struc(indx).local_amount
              ,account_amount = tab_update_struc(indx).account_amount
             -- ,misc_data =  tab_update_struc(indx).misc_data
              ,transaction_type = tab_update_struc(indx).transaction_type
           where rowid = tab_update_struc(indx).rowid_chg;
		   --
		-- forall indx in  1..counter  
		--   update int_addendum_charges
        --   set misc_data =  tab_update_struc(indx).misc_data_901
        --   where rowid = tab_update_struc(indx).rowid_901;
		   --
		-- forall indx in  1..counter  
		--   update int_addendum_charges
        --   set misc_data =  tab_update_struc(indx).misc_data_911
        --   where rowid = tab_update_struc(indx).rowid_911;
		   --     
		   
--		   ?????????????????????? Maybe this can be done in the another script when splitting class 12s, not sure.
		   forall indx in  1..counter 
           update int_transactions
           set 
               inward_fee_number = tab_update_struc(indx).record_id_number              
              ,pl_ind_inward = tab_update_struc(indx).transaction_type
           where rowid= tab_update_struc(indx).int_trn_rowid; 
--           ??????????????????????
           --
           /*forall indx in  1..counter 
             update tmp_bnz_20220501
             set 
                 o_misc_data = tab_update_struc(indx).o_misc_data
				,o_misc_data_901 = tab_update_struc(indx).o_misc_data_901
				,o_misc_data_911 = tab_update_struc(indx).o_misc_data_911
                ,o_group_record_id_number = tab_update_struc(indx).o_group_record_id_number     
                ,new_misc_data = tab_update_struc(indx).misc_data
                ,new_misc_data_901 = tab_update_struc(indx).misc_data_901  
                ,new_misc_data_911 = tab_update_struc(indx).misc_data_911                                   
                --,o_record_id_number = tab_update_struc(indx).o_record_id_number  
                --,o_f_tt = tab_update_struc(indx).o_f_tt
                ,audit_trail = v_audit_T
                ,FAIL_STAGE_2 = tab_update_struc(indx).FAIL_STAGE_2
                ,ERR_DESC = tab_update_struc(indx).ERR_DESC
             where rowid = tab_update_struc(indx).rowid_trn; */
           --
           counter := 0;
           tab_update_struc.delete;
           
           commit;
           
       end if;
       
       counter := counter + 1;
       
       tab_update_struc(counter).transaction_slip := rec_addend.transaction_slip;
       tab_update_struc(counter).transaction_type := rec_addend.f_f_tt;
       tab_update_struc(counter).rowid_trn := rec_addend.tmp_rowid;
	   tab_update_struc(counter).rowid_chg := rec_addend.chg_rowid;
	   tab_update_struc(counter).int_trn_rowid := rec_addend.trn_rowid;      
	   
	   if rec_addend.f_t_a = rec_addend.transaction_amount then     
	     --   
	     tab_update_struc(counter).transaction_amount := rec_addend.transaction_amount;
	     tab_update_struc(counter).settlement_amount := rec_addend.settlement_amount;
	     tab_update_struc(counter).local_amount := rec_addend.local_amount;
	     tab_update_struc(counter).account_amount := rec_addend.account_amount;
	   else       
	     --
	     tab_update_struc(counter).transaction_amount := rec_addend.f_t_a;
	     tab_update_struc(counter).settlement_amount := rec_addend.f_s_a;
	     tab_update_struc(counter).local_amount := rec_addend.f_l_a;      
	     if rec_addend.transaction_amount <> 0 then	     
	       tab_update_struc(counter).account_amount := round((rec_addend.account_amount/rec_addend.transaction_amount) * rec_addend.f_t_a,4);
	     else   
	       tab_update_struc(counter).account_amount := '0';
	     end if;
	   end if;       
	   
	   --tab_update_struc(counter).o_transaction_amount := rec_addend.transaction_amount;
	   --tab_update_struc(counter).o_settlement_amount := rec_addend.settlement_amount;
	   --tab_update_struc(counter).o_local_amount := rec_addend.local_amount;
	   --tab_update_struc(counter).o_account_amount := rec_addend.account_amount;
                     
          
       tab_update_struc(counter).record_id_number := rec_addend.new_record_id_number;     
       tab_update_struc(counter).o_group_record_id_number:= rec_addend.group_record_id_number;
       tab_update_struc(counter).group_record_id_number := rec_addend.new_group_record_id_number;        
       --tab_update_struc(counter).o_misc_data := rec_addend.misc_data; 
       
       
       --replace old group record id number with new
       
       --tab_update_struc(counter).misc_data := replace(rec_addend.misc_data, '_'||rec_addend.group_record_id_number||'_', '_'||rec_addend.new_group_record_id_number||'_');
       
       --v_area_lookup :=  bw_lib_scri.GetChoiceDisplayValue('BWT_AREA_OF_ACTION', 'INDEX_FIELD', 'AREA_LOOKUP', rec_addend.aoe);
       
       /*if v_area_lookup = '002' then      
         
           v_position := INSTR(rec_addend.misc_data, '_' , 1, 11  )  ;          
           tab_update_struc(counter).misc_data := substr(tab_update_struc(counter).misc_data , 1,v_position-1)||'_'||rec_addend.new_service_type||substr(tab_update_struc(counter).misc_data ,v_position);   
           
           for rec_interchange in (
			   select rowid, charge_type, misc_data
			   from   int_addendum_charges 
			   where  institution_number = '00000108'
			   and    transaction_Slip = rec_addend.transaction_Slip
			   and    charge_type in ('901','911')
			   and    record_date = rec_addend.record_date         
			   and    misc_data is not null
		   )
		   loop            
		     v_position := INSTR(rec_interchange.misc_data, '_' , 1, 11  )  ;     
		     if rec_interchange.charge_type = '901' then
		       tab_update_struc(counter).o_misc_data_901 := rec_interchange.misc_data;
			   tab_update_struc(counter).misc_data_901 := substr(rec_interchange.misc_data, 1,v_position-1)||'_'||rec_addend.new_service_type||substr(rec_interchange.misc_data,v_position);
			   tab_update_struc(counter).rowid_901 := rec_interchange.rowid;
			 else
			   tab_update_struc(counter).o_misc_data_911 :=rec_interchange.misc_data;
			   tab_update_struc(counter).misc_data_911 := substr(rec_interchange.misc_data, 1,v_position-1)||'_'||rec_addend.new_service_type||substr(rec_interchange.misc_data,v_position);
			   tab_update_struc(counter).rowid_911 := rec_interchange.rowid;
	
			 end if;
		   end loop;
       
       end if;*/
                     

       
    end loop;
    --
--    dbms_output.put_line(counter);
    if counter > 0 then
         forall indx in  1..counter 
           update int_addendum_charges
           set 
               record_id_number = tab_update_struc(indx).record_id_number
              ,group_record_id_number = tab_update_struc(indx).group_record_id_number
              ,transaction_amount =  tab_update_struc(indx).transaction_amount
              ,settlement_amount = tab_update_struc(indx).settlement_amount
              ,local_amount = tab_update_struc(indx).local_amount
              ,account_amount = tab_update_struc(indx).account_amount
              --,misc_data =  tab_update_struc(indx).misc_data
              ,transaction_type = tab_update_struc(indx).transaction_type
           where rowid = tab_update_struc(indx).rowid_chg;
		   --
		 /*forall indx in  1..counter  
		   update int_addendum_charges
           set misc_data =  tab_update_struc(indx).misc_data_901
           where rowid = tab_update_struc(indx).rowid_901;
		   --
		 forall indx in  1..counter  
		   update int_addendum_charges
           set misc_data =  tab_update_struc(indx).misc_data_911
           where rowid = tab_update_struc(indx).rowid_911;  */
		   --
--		   ????????????????????????????
		   forall indx in  1..counter 
           update int_transactions
           set 
               inward_fee_number = tab_update_struc(indx).record_id_number              
              ,pl_ind_inward = tab_update_struc(indx).transaction_type
           where rowid= tab_update_struc(indx).int_trn_rowid;     
--           ????????????????????????????
           --
           /*forall indx in  1..counter 
             update tmp_bnz_20220501
             set 
                 o_misc_data = tab_update_struc(indx).o_misc_data
				,o_misc_data_901 = tab_update_struc(indx).o_misc_data_901
				,o_misc_data_911 = tab_update_struc(indx).o_misc_data_911
                ,o_group_record_id_number = tab_update_struc(indx).o_group_record_id_number   
                ,new_misc_data = tab_update_struc(indx).misc_data
                ,new_misc_data_901 = tab_update_struc(indx).misc_data_901  
                ,new_misc_data_911 = tab_update_struc(indx).misc_data_911                                     
                --,o_record_id_number = tab_update_struc(indx).o_record_id_number  
                --,o_f_tt = tab_update_struc(indx).o_f_tt
                ,audit_trail = v_audit_T
                ,FAIL_STAGE_2 = tab_update_struc(indx).FAIL_STAGE_2
                ,ERR_DESC = tab_update_struc(indx).ERR_DESC
             where rowid = tab_update_struc(indx).rowid_trn;     */
           --
           counter := 0;
           tab_update_struc.delete;
           
           commit;
           
    end if;  
    
      
exception
when others then 
rollback; 
dbms_output.put_line(dbms_utility.format_error_backtrace());
raise;
end;