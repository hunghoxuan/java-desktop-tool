/*
This SQL script's objective is to fix missing cas client accounts & book cycle of selected merchants.
Input params:
- 1: institution_number
- 2: client_number (one to many, seperated by comma)
- 3: currency (one to many, seperated by comma)
- 4: account typ ids (one to many, seperated by comma)
Output: insert/update data in:
- CAS_CLIENT_ACCOUNT
- CAS_CYCLE_BOOK_BALANCE
Use cases: BA, Testers, Automation test.
*/
SET DEFINE ON;
SET SERVEROUTPUT ON;
DECLARE
-- MANUAL PARAMS
v_user_id VARCHAR2(8) := '999999';
v_station_number VARCHAR2(3) := '129';

v_institution_number VARCHAR2(8) := '&institution_number';
v_client_number VARCHAR2(420) := '&client_number';
v_currency VARCHAR2(30) := '&currency';
v_account_type_id_all VARCHAR2(30) := '&account_type_id';

v_audit_trail VARCHAR2(35) := 'MANUAL_INPUT_';

-- CACULATED PARAMS
v_acct_number VARCHAR2(11);
v_acct_number_last VARCHAR2(11);
v_service_contract_id VARCHAR2(3);
v_account_type_id VARCHAR2(3);
v_posting_date VARCHAR2(8);
v_cycle_start VARCHAR2(8);
v_cycle_end VARCHAR2(8);
v_group_number varchar(8);
v_date_cycle_end VARCHAR2(8);
v_system_date VARCHAR2(8);
v_count number;
v_i number;
v_errors VARCHAR2(120);
P_OUT varchar2(100);
v_params varchar2(100);
v_currency_all VARCHAR2(30);
v_trans_type_all VARCHAR2(30);

BEGIN

	if (v_institution_number is null or v_institution_number = ''
		or v_user_id is null or v_user_id = ''
		or v_station_number is null or v_station_number = '') THEN
		v_errors := 'Param is required or missing';
		DBMS_OUTPUT.put_line('CANCELLED : ' || v_errors);
	end if;

	IF (v_errors is null or v_errors = '') THEN
		-- AUTO-CACULATED PARAMS
		SELECT CURRENT_CYCLE_END INTO v_cycle_end FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = v_institution_number and rownum = 1;
		SELECT CURRENT_CYCLE_START INTO v_cycle_start FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = v_institution_number and rownum = 1;

		SELECT TO_CHAR(SYSDATE, 'YYYYMMDD') INTO v_system_date FROM DUAL; -- get system date
		v_audit_trail := v_audit_trail || v_system_date;

		v_i := 0;
		v_currency_all := v_currency;
		-- v_trans_type_all := v_trans_type;

		FOR curClientNumber in (WITH DATA AS ( SELECT v_client_number str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
			v_client_number := trim(curClientNumber.str);
			SELECT acct_number INTO v_acct_number_last FROM CAS_CLIENT_ACCOUNT WHERE institution_number = v_institution_number and acct_number like v_client_number || '%' and rownum = 1 order by acct_number desc;

			-- FIX & ADD BOOK BALANCE
			FOR currAccount in (select distinct service_contract_id, account_type_id, group_number, PARENT_CLIENT_NUMBER,BILLING_CYCLE, account_level, billing_level from CAS_CLIENT_ACCOUNT   where  institution_number = v_institution_number and acct_number like v_client_number || '%') LOOP
				FOR curAccountTypeID in (WITH DATA AS ( SELECT v_account_type_id_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
					FOR curCurrency in (WITH DATA AS ( SELECT v_currency_all str FROM dual) SELECT trim(COLUMN_VALUE) str FROM DATA, xmltable(('"' || REPLACE(REPLACE(REPLACE(str, ' ', ''), ',', '","'), '|', '","') || '"'))) LOOP
						v_currency := trim(curCurrency.str);
			   			v_service_contract_id := currAccount.service_contract_id;
			   			if (currAccount.account_type_id = curAccountTypeID.str) then
			   				v_account_type_id := currAccount.account_type_id;
			   			else
			   				v_account_type_id := curAccountTypeID.str;
			   			end if;

			   			v_group_number := currAccount.group_number;

			   			dbms_output.put_line('CHECKING Client Number ' || v_client_number || '. Acct Number: ' || v_acct_number_last || '. GroupNumber: ' || v_group_number || '. AccountTypeID: ' || v_account_type_id || '. Currency: ' || v_currency || '. Contract: ' || v_service_contract_id || '. Account: ' || v_account_type_id);

			   			-- check existence account with account_type, service_contract_id, currency
			   			v_count := 0;
			   			SELECT COUNT(*) INTO v_count FROM CAS_CLIENT_ACCOUNT WHERE institution_number = v_institution_number and acct_number like v_client_number || '%' and acct_currency = v_currency and service_contract_id = v_service_contract_id and account_type_id = v_account_type_id;

			   			if (v_count = 0) then
			   				v_acct_number_last := TRIM(TO_CHAR(TO_NUMBER(v_acct_number_last) + 1, '00000000000'));
			   				dbms_output.put_line('  Add Account: Client number: ' || v_client_number || '. Acct Number: ' || v_acct_number_last || '. GroupNumber: ' || v_group_number || '. AccountTypeID: ' || v_account_type_id || '. Currency: ' || v_currency || '. Contract id: ' || v_service_contract_id);

			   				Insert into CAS_CLIENT_ACCOUNT (
				   				ACCT_NUMBER,
				   				ACCT_NUMBER_RBS,
				   				INSTITUTION_NUMBER,
				   				CLIENT_NUMBER,
				   				GROUP_NUMBER,
				   				LIMIT_NUMBER,
				   				SERVICE_CONTRACT_ID,
				   				ACCOUNT_TYPE_ID,
				   				ACCT_CURRENCY,
				   				BILLING_CYCLE,
				   				RECORD_DATE,
				   				ACCT_STATUS,
				   				LAST_STATEMENT_DATE,
				   				UNALLOCATED_CREDITS,
				   				LAST_STATEMENT_NUMBER,
				   				GL_NUMBER,
				   				PARENT_ACCOUNT_NUMBER,
				   				LAST_UPDATE_RBS,
				   				ACCOUNT_LEVEL,
				   				AUDIT_TRAIL,
				   				RECORD_TYPE,
				   				CLIENT_ACCOUNT_NAME,
				   				BILLING_LEVEL,
				   				SETTLEMENT_NUMBER,
				   				LAST_SETTLEMENT_DATE,
				   				LAST_ACCRUAL_DATE,
				   				FRAUD_FLAG,
				   				LAST_FEE_DATE,
				   				CLIENT_LEVEL,
				   				PARENT_CLIENT_NUMBER,
				   				OVERRIDE_EXPIRY,
				   				STATEMENT_TYPE,
				   				STATEMENT_GENERATION,
				   				LAST_AMENDED_DATE,
				   				BILLING_CYCLE_ID,
				   				CURRENT_CYCLE_START,
				   				CURRENT_CYCLE_END,
				   				PAYMENT_STATUS,
				   				ACTIVE_DATE)
			   				values (
				   				v_acct_number_last,
				   				null,
				   				v_institution_number,
				   				v_client_number,
				   				v_group_number,
				   				null,
				   				v_service_contract_id,
				   				v_account_type_id,
				   				v_currency,
				   				currAccount.BILLING_CYCLE,
				   				v_system_date,
				   				'001',
				   				'00000000',
				   				'0',
				   				null,
				   				null,
				   				null,
				   				null,
				   				currAccount.ACCOUNT_LEVEL,
				   				v_audit_trail,
				   				'003',
				   				'Merch Paymt Acct ' || v_currency,
				   				currAccount.BILLING_LEVEL,
				   				'01',
				   				'00000000',
				   				null,
				   				'000',
				   				null,
				   				'001',
				   				currAccount.PARENT_CLIENT_NUMBER,
				   				null,
				   				'900',
				   				'001',
				   				null,
				   				null,
				   				v_cycle_start,
				   				v_cycle_end,
				   				null,
				   				v_system_date);
			   				COMMIT;
			   			end if;

				   	END LOOP; -- v_currency_all
   				END LOOP;   -- Account type

				FOR curr in (select distinct * from CAS_CLIENT_ACCOUNT where  institution_number = v_institution_number and acct_number like v_client_number || '%') LOOP
		     		-- check existence book balance
		   			v_count := 0;
		   			SELECT COUNT(*) INTO v_count FROM CAS_CYCLE_BOOK_BALANCE WHERE institution_number = v_institution_number and acct_number = curr.acct_number and acct_currency = curr.acct_currency;

		   			if (v_count = 0) then
		   				dbms_output.put_line('  Add Missing Book Cyling: ACCT NUMBER: ' || curr.acct_number || '. CYCLE START: ' || v_cycle_start || ' - ' || v_cycle_end);
			            Insert into CAS_CYCLE_BOOK_BALANCE
			            (RECORD_DATE,INSTITUTION_NUMBER,ACCT_NUMBER,ACCT_CURRENCY,LAST_AMENDMENT_DATE,DATE_CYCLE_START,DATE_CYCLE_END,PROCESSING_STATUS,BEGIN_BALANCE,CURRENT_BALANCE,DR_BALANCE_CASH,DR_BALANCE_RETAIL,DR_BALANCE_INTEREST,DR_BALANCE_CHARGES,CR_BALANCE_PAYMENTS,CR_BALANCE_REFUNDS,CR_BALANCE_INTEREST,CR_BALANCE_BONUS,AMOUNT_HIGH_BAL_DR,AMOUNT_LOW_BAL_DR,AMOUNT_HIGH_BAL_CR,AMOUNT_LOW_BAL_CR,NUMBER_TRAN_CHRG_DR,NUMBER_TRAN_CHRG_CR,NUMBER_TRAN_NONCHRG_DR,NUMBER_TRAN_NONCHRG_CR,TRAN_CHARGES,ACCRUED_CR,ACCRUED_DR,PENDING_AUTHS,AUDIT_TRAIL,STATEMENT_NUMBER,LOCKING_COUNTER,CURRENT_BALANCE_CASH,INSTALLMENT_BALANCE,LAST_BALANCE_UPDATE) values
			            (v_system_date,v_institution_number,curr.acct_number,curr.acct_currency,v_system_date,v_cycle_start,v_cycle_end,'004','0','0.0001','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0',null,v_audit_trail,null,null,null,null,null);
		   			    COMMIT;
		   			end if;
		   		END LOOP;

				-- FIX & ADD BOOK BALANCE
	   		    FOR curr in (select distinct * from CAS_CYCLE_BOOK_BALANCE where  institution_number = v_institution_number and acct_number like v_client_number || '%' and processing_status = '004' and DATE_CYCLE_END < v_cycle_end order by acct_number, date_cycle_start) LOOP
		            dbms_output.put_line('  Update BookCyling ACCT NUMBER: ' || curr.acct_number || '. CYCLE START: ' || curr.date_cycle_start || ' - ' || curr.date_cycle_end || '. STATUS: ' || curr.processing_status);
		            UPDATE CAS_CYCLE_BOOK_BALANCE SET processing_status = '015' WHERE institution_number = v_institution_number and processing_status = '004' AND ACCT_NUMBER = curr.acct_number AND date_cycle_start = curr.date_cycle_start;
		            COMMIT;

		            UPDATE CAS_CLIENT_ACCOUNT SET current_cycle_start = v_cycle_start, current_cycle_end = v_cycle_end WHERE institution_number = v_institution_number and ACCT_NUMBER = curr.acct_number AND current_cycle_start = curr.date_cycle_start;
	            	COMMIT;

		            Insert into CAS_CYCLE_BOOK_BALANCE
		            (RECORD_DATE,INSTITUTION_NUMBER,ACCT_NUMBER,ACCT_CURRENCY,LAST_AMENDMENT_DATE,DATE_CYCLE_START,DATE_CYCLE_END,PROCESSING_STATUS,BEGIN_BALANCE,CURRENT_BALANCE,DR_BALANCE_CASH,DR_BALANCE_RETAIL,DR_BALANCE_INTEREST,DR_BALANCE_CHARGES,CR_BALANCE_PAYMENTS,CR_BALANCE_REFUNDS,CR_BALANCE_INTEREST,CR_BALANCE_BONUS,AMOUNT_HIGH_BAL_DR,AMOUNT_LOW_BAL_DR,AMOUNT_HIGH_BAL_CR,AMOUNT_LOW_BAL_CR,NUMBER_TRAN_CHRG_DR,NUMBER_TRAN_CHRG_CR,NUMBER_TRAN_NONCHRG_DR,NUMBER_TRAN_NONCHRG_CR,TRAN_CHARGES,ACCRUED_CR,ACCRUED_DR,PENDING_AUTHS,AUDIT_TRAIL,STATEMENT_NUMBER,LOCKING_COUNTER,CURRENT_BALANCE_CASH,INSTALLMENT_BALANCE,LAST_BALANCE_UPDATE) values
		            (v_system_date,v_institution_number,curr.acct_number,curr.acct_currency,v_system_date,v_cycle_start,v_cycle_end,'004',curr.current_balance,curr.current_balance,'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0',null,v_audit_trail,null,null,null,null,null);
		   			COMMIT;
		   		END LOOP;
   			END LOOP; -- CAS ACCOUNT
      	END LOOP; -- Client number
	END IF;
exception
	WHEN NO_DATA_FOUND THEN
		dbms_output.put_line('No data found'|| sqlerrm || dbms_utility.format_error_backtrace);rollback;
	when others then
		dbms_output.put_line('An error occurred '|| chr(10) || sqlerrm || chr(10) || dbms_utility.format_error_backtrace);rollback;
END;
/
select distinct * from CAS_CYCLE_BOOK_BALANCE   where  institution_number = '00002001' and processing_status = '004' and DATE_CYCLE_END = (SELECT CURRENT_CYCLE_END FROM CBR_BILLING_CYCLE WHERE INSTITUTION_NUMBER = '00002001' and rownum = 1) order by acct_number, date_cycle_start;


