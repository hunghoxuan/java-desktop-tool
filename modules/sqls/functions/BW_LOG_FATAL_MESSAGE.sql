procedure bw_log_fatal_message(
  p_processno in varchar2,
  p_errorcode in varchar2,
  p_script in varchar2,
  p_addText in varchar2,
  p_param0 in varchar2,
  p_param1 in varchar2,
  p_param2 in varchar2,
  p_param3 in varchar2,
  p_param4 in varchar2,
  p_param5 in varchar2,
  p_param6 in varchar2,
  p_param7 in varchar2,
  p_param8 in varchar2,
  p_param9 in varchar2,
  p_param10 in varchar2,
  p_param11 in varchar2,
  p_param12 in varchar2,
  p_param13 in varchar2,
  p_param14 in varchar2,
  p_param15 in varchar2
)
is
  --
begin
  --
  Bw_Lib_Proc_Log.gstrIntProcLogNumber := p_processno;
  Bw_Lib_Messages.FillMsgParams(p_param0, p_param1, p_param2, p_param3, p_param4, p_param5, p_param6, p_param7, p_param8, p_param9, p_param10, p_param11, p_param12, p_param13, p_param14, p_param15);
  Bw_Lib_Messages.BWS_MsgLog(p_errorcode, bw_const2.PRC_MSG_TYP_FATAL, p_script, STRADDTEXT=>p_addText);
  --
end bw_log_fatal_message;