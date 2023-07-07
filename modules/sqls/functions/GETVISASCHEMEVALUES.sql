procedure  GetVisaSchemeValues
(  
   	p_tc  in varchar2,
   	p_cm  in varchar2,
	V_PTC out VARCHAR2,
	V_PEM out VARCHAR2,
	V_CIM out VARCHAR2,
	V_MEI out VARCHAR2,
	V_ATI out VARCHAR2,
	p_env out VARCHAR2
  

)
is 

prec_trn         bw_lib_incl.Transactions ;
v_DE22 VARCHAR2(25);
v_PDS0023 VARCHAR2(25);
v_tc_group VARCHAR2(6);
v_cm_group VARCHAR2(6);
v_init_return number;
n number := 0;
v_termminal_capability varchar2(3) := '036';
v_capture_method varchar2(3) := '040';
err_exit exception;
begin
		BW_Prc_Res.InitGlobalVars( '00000006', '129', '999999', v_init_return );
		If trim(v_termminal_capability) is null or trim(v_capture_method) is null then
				dbms_output.put_line('Please enter a TC and CM!');
		raise err_exit;
			end if;
		prec_trn.TermCapab := p_tc;
	prec_trn.CaptMethod := p_cm;
			v_DE22 := bw_ipm_out.GetPOSDataCode( prec_trn ) ;
      		V_PTC := bw_visa_base2_out.FillCaptureField(prec_trn, 'PTC');
	V_PEM := bw_visa_base2_out.FillCaptureField(prec_trn, 'PEM');
	V_CIM := bw_visa_base2_out.FillCaptureField(prec_trn, 'CIM');
		V_MEI := bw_visa_base2_out.FillCaptureField(prec_trn, 'MEI');
		If trim(V_MEI) is null then
		V_MEI := bw_visa_base2_out.FillCaptureField(prec_trn, 'ECI');
	end if;
		V_ATI := bw_visa_base2_out.FillCaptureField(prec_trn, 'UAT');
		if prec_trn.CaptMethod = bw_trn.CAPTM_CNP_RECURRING
	   or prec_trn.AddendAdddData.RTFlag = bw_const.CONF_YES
	   or prec_trn.AddendAdddData.RTFlag = bw_const.RECURR_FIRST_TRAN
	   or prec_trn.AddendAdddData.RTFlag = bw_const.RECURR_SECOND_TRAN
	then
		p_env                         := 'R';
	elsif
	  prec_trn.CaptMethod = bw_trn.CAPTM_COF_RECURRING then
		p_env                        := 'C';
	else
	  p_env                         := ' ';
	end if;
	exception
 when err_exit then
 	null;
end GetVisaSchemeValues;
