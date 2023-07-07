SET SERVEROUTPUT ON;
CREATE OR REPLACE TYPE p_type AS VARRAY(200) OF VARCHAR2(50);
/
create or replace procedure delete_merchant (
	p_client_number in varchar, p_terminal_id IN varchar
)
is
  v_application_number varchar(10);
begin

	begin
		select APPLICATION_NUMBER into v_application_number from CIS_APPLICATION_DETAIL where INSTITUTION_NUMBER = '00000111' and CLIENT_NUMBER = p_client_number and rownum <= 1;

	  delete from CIS_APPLICATION_DETAIL where INSTITUTION_NUMBER = '00000111' and CLIENT_NUMBER = p_client_number;
	  delete from CIS_SETTLEMENT_INFORMATION where INSTITUTION_NUMBER = '00000111' and CLIENT_NUMBER = p_client_number;
      delete from CIS_CLIENT_LINKS where institution_number = '00000111' and client_number = p_client_number;
	  delete from CIS_ADDRESSES where institution_number = '00000111' and client_number = p_client_number;
	  delete from CIS_CLIENT_ADDENDUM where institution_number = '00000111' and client_number = p_client_number;
	  delete from CAS_CLIENT_ACCOUNT where institution_number = '00000111' and client_number = p_client_number;
	  delete from CIS_CLIENT_DETAILS where institution_number = '00000111' and client_number = p_client_number;
	  delete from CAS_CYCLE_BOOK_BALANCE where institution_number = '00000111' and acct_number like (p_client_number || '%');
	  delete from SVC_CLIENT_SERVICE where institution_number = '00000111' and client_number = p_client_number;
 	  DELETE from CIS_DEVICE_LINK WHERE institution_number = '00000111' and client_number = p_client_number; --added by lupi
	  delete from CIS_APPLICATION_ADDR where INSTITUTION_NUMBER = '00000111' and APPLICATION_NUMBER = v_application_number;
	  delete from CIS_APPLICATION_ACCT_TYPE where INSTITUTION_NUMBER = '00000111' and APPLICATION_NUMBER = v_application_number;
	  delete from CIS_APPLICATION_SERVICES where INSTITUTION_NUMBER = '00000111' and APPLICATION_NUMBER = v_application_number;
      delete from CIS_APPL_CLIENT_ADDENDUM where institution_number = '00000111' and application_number = v_application_number;
	 
	  DELETE FROM CIS_APPL_TERMINAL_INPUT WHERE INSTITUTION_NUMBER = '00000111' AND TERMINAL_ID=p_terminal_id ; --Delete Terminal ID 
	 
	exception
	  when no_data_found then
	  	null;

		when others then
		  dbms_output.put_line(' > ERROR: [' || sqlerrm || ']');

	end;

end;
/
-- Main function
declare
  address_categs  p_type;
	account_ids     p_type;
	sid             p_type;
	settlement      p_type;
	billing_level   varchar(3);
	v_init_return   pls_integer;
begin
	BW_PRC_RES.INITGLOBALVARS ('00000111','129','999999', v_init_return);
	delete_merchant('69111111','P'||substr('69111111',-7,7));
	--delete_merchant('69222222','P'||substr('69222222',-7,7));
	--delete_merchant('69333333','P'||substr('69333333',-7,7));
	--delete_merchant('69444444','P'||substr('69444444',-7,7));

end;
/
drop procedure delete_merchant;

