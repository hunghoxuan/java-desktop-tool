
create or replace procedure T3MP_DELETE_MERCHANT (
	p_institution_number in varchar2,
	p_client_number in varchar,
	p_terminal_id IN varchar2 default ''
)
is
  v_application_number varchar(10);
begin

	begin

	  delete from CIS_SETTLEMENT_INFORMATION where INSTITUTION_NUMBER = p_institution_number and CLIENT_NUMBER = p_client_number;
      delete from CIS_CLIENT_LINKS where institution_number = p_institution_number and client_number = p_client_number;
	  delete from CIS_ADDRESSES where institution_number = p_institution_number and client_number = p_client_number;
	  delete from CIS_CLIENT_ADDENDUM where institution_number = p_institution_number and client_number = p_client_number;
	  delete from CAS_CLIENT_ACCOUNT where institution_number = p_institution_number and client_number = p_client_number;
	  delete from CIS_CLIENT_DETAILS where institution_number = p_institution_number and client_number = p_client_number;
	  delete from CAS_CYCLE_BOOK_BALANCE where institution_number = p_institution_number and acct_number like (p_client_number || '%');
	  delete from SVC_CLIENT_SERVICE where institution_number = p_institution_number and client_number = p_client_number;
 	  DELETE from CIS_DEVICE_LINK WHERE institution_number = p_institution_number and client_number = p_client_number; --added by lupi
      dbms_output.put_line('Deleted client data inst:' || p_institution_number || ', client_number: ' || p_client_number);
      COMMIT;
	  
 	  select APPLICATION_NUMBER into v_application_number from CIS_APPLICATION_DETAIL where INSTITUTION_NUMBER = p_institution_number and CLIENT_NUMBER = p_client_number and rownum <= 1;

 	  delete from CIS_APPLICATION_DETAIL where INSTITUTION_NUMBER = p_institution_number and CLIENT_NUMBER = p_client_number;
	  delete from CIS_APPLICATION_ADDR where INSTITUTION_NUMBER = p_institution_number and APPLICATION_NUMBER = v_application_number;
	  delete from CIS_APPLICATION_ACCT_TYPE where INSTITUTION_NUMBER = p_institution_number and APPLICATION_NUMBER = v_application_number;
	  delete from CIS_APPLICATION_SERVICES where INSTITUTION_NUMBER = p_institution_number and APPLICATION_NUMBER = v_application_number;
      delete from CIS_APPL_CLIENT_ADDENDUM where institution_number = p_institution_number and application_number = v_application_number;

	  DELETE FROM CIS_APPL_TERMINAL_INPUT WHERE INSTITUTION_NUMBER = p_institution_number AND TERMINAL_ID=p_terminal_id ; --Delete Terminal ID
	  dbms_output.put_line('Deleted application data inst:' || p_institution_number || ', app_number: ' || v_application_number);
      COMMIT;
	exception
	  when no_data_found then
	  	null;
      when others then
		dbms_output.put_line(' > ERROR: [' || sqlerrm || ']');

	end;

end;

