SET SERVEROUTPUT ON;
CREATE OR REPLACE TYPE p_type AS VARRAY(200) OF VARCHAR2(50);
/
create or replace procedure create_merchant(
	p_client_number       in varchar2,
    p_address_categs      in p_type,
	p_account_type_ids    in p_type,
	p_service_ids         in p_type,
	p_settlement          in p_type,
	p_billing_level       in varchar2,
	p_service_contract_id in varchar2,
 	p_condition_set      in varchar2,
	p_merch_tran_tariff  in varchar2,
	p_settlement_method  in varchar2,
	p_posting_method     in varchar2,
	p_acct_currency      in varchar2,
	p_buisnessClass      in varchar2,
	p_client_tariff IN varchar2)
is
  v_application_number varchar(10);
 
begin

  v_application_number :=  BW_CODE_LIBRARY.GETNEXTSEQNUMBER('017',1); --Application Number

  --IC++ NET EUR MERCHANT
  
   INSERT INTO CIS_APPLICATION_DETAIL (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,RECORD_TYPE,CLIENT_NUMBER,CLIENT_NUMBER_RBS,APPLICATION_STATUS,LAST_NAME,FIRST_NAME,EMBOSSING_LINE_2,CONTACT_NAME,TITLE,MARITAL_STATUS,TEL_PRIVATE,TEL_WORK,FAX_PRIVATE,FAX_WORK,ID_NUMBER,PASSPORT_NUMBER,DRIVING_LICENSE,VAT_REG_NUMBER,REGISTRATION_NUMBER,BIRTH_DATE,BIRTH_PLACE,CLIENT_TYPE,RESIDENCE_STATUS,RESIDENCE_STATUS_DATE,FATHERS_NAME,CLIENT_LANGUAGE,NATIONALITY,EMPLOYMENT_STATUS,INSTITUTION_ACCT_OFFICER,PROVIDER_ACCT_OFFICER,CLIENT_BRANCH,SHORT_NAME,COMPANY_NAME,LEGAL_FORM,TRADE_NAME,BUSINESS_CLASS,OUR_REFERENCE,SERVICE_CONTRACT_ID,CONDITION_SET,LIMIT_CURRENCY,FLOOR_LIMIT,CLIENT_LEVEL,NOTE_TEXT,CONTRACT_REFERENCE,BANK_REFERENCE,SETTLEMENT_METHOD,POSTING_METHOD,PARENT_APPL_NUMBER,CLIENT_ORGANIZATION,PARENT_CLIENT_NUMBER_RBS,LAST_AMENDMENT_DATE,AUDIT_TRAIL,CLIENT_COUNTRY,CLIENT_CITY,ORIGINAL_GROUP_NUMBER,GROUP_NUMBER,REGISTRATION_DATE,CONTRACT_CATEGORY,RESIDENCE_TYPE,EMPLOYMENT_POSITION,EMPLOYER_NAME,EMPLOYMENT_DATE,INCOME_RANGE,EMPLOYMENT_EXTRA_INFO,MARITAL_STATUS_DATE,COST_CENTER,CLIENT_CLASSIFICATION,APPLICATION_INSTRUCTION,POST_CODE,CLIENT_STATE,WORKING_SECTOR,TOTAL_SCORE,APPLICATION_DATE,MERCHANT_STREET,CLIENT_GENDER,CLIENT_REGION,APPLICATION_RECEIVED_DATE,APPLICATION_SOURCE,ID_EXPIRY_DATE,MIDDLE_NAME,EMBOSS_LINE_1,EDUCATION_LEVEL,NO_OF_DEPENDANTS,SALARY_DAY,INCOME_AMOUNT,OTHER_INCOME_AMOUNT,PIN_REQUIRED,CREDIT_SCORE_STATUS,MOTHER_MAIDEN_NAME,BIRTH_NAME,CLIENT_PASSWORD,IQAMA_NUMBER,TRADE_NAME_L2,COMPANY_NAME_L2,REGISTRATION_EXPIRY_DATE,PROVIDER_ACCT_OFFICER_2,SAMA_REFERENCE,ID_TYPE,CARD_RELATION,FIRST_NAME_L2,FATHERS_NAME_L2,GRANDFATHERS_NAME,GRANDFATHERS_NAME_L2,LAST_NAME_L2,SHORT_NAME_L2,SCORE_DATE,CREDIT_SCORE,EMBOSSING_LINE_1,FILE_NUMBER,CLIENT_LIMIT,ECOMMERCE_INDICATOR,SERVICE_TEL_NUMBER,EMBOSSING_LINE_3,RISK_GROUP,PROCESSING_FILE_NUMBER,LOCKING_COUNTER,ACCUMULATOR_SCHEME,APPL_PROC_INVOKED,MC_IP_QUALIFICATION,MC_IP_VALUE,VISA_IP_QUALIFICATION,VISA_IP_VALUE,CREDIT_DELTA_INDICATOR,BILLBACK_INDICATOR,SERVICE_CATEGORY_CODE,LOAN_CURRENCY,LOAN_AMOUNT,NUMBER_INSTALLMENTS,INSTALLMENT_AMOUNT,FIRST_INSTALLMENT_AMOUNT,DATE_FIRST_INSTALLMENT,INSTALLMENT_CYCLE,RCC,CROSS_BORDER_FEE_IND,DOMESTIC_MCC,NET_MONTHLY,EXPIRY_DATE,CLIENT_SCHEME,NEXT_FEE_DATE,BRANCH_REGION,COUNTRY_OF_ISSUE,WEBSITE_ADDRESS,REMINDER_FEE_TYPE,REMINDER_CLASS,CARD_NUMBER,LOAN_TYPE_ID,RT_FLAG,PIN_GENERATED,CARD_EMBOSSED,APPLICATION_TYPE,TIER_GROUP,PARENT_CLIENT_ID,PREPAYMENT_IND,PREPAY_QUARANTINE_PERIOD,RESERVE_PERCENT,CNAE,SALES_TURNOVER_CATEGORY,SERVICE_TEL_NUM,FATCA,GREEN_CARD,CIS_ID,INVOICES,ACCOUNT_SELLER,TRANSFER_METHOD,CHARGE_TIER_LEVEL,BIRTH_COUNTRY,ALLOCATION_METHOD,"CLIENT_SCHEME$3","CONDITION_SET$3",FEE_PROGRAM_INDICATOR,CLIENT_STATUS,MERCHANT_TRAN_TARIFF,LEGAL_DOCUMENT_EXPIRY_DATE,DOCUMENTS_RECEIVED,SEC_VAT_REG_NUMBER,SEC_REGISTRATION_NUMBER,CONTRACT_REGION,VAT_REGION,VAT_COUNTRY,DINERS_INTES_CODE,FX_TARIFF,RISK_RULE_GROUP_ID) VALUES
      ('00000111',v_application_number,'20191130','003',p_client_number,NULL,'002',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'11111111111111','111111111111111',NULL,NULL,'002',NULL,NULL,NULL,'001',NULL,NULL,'000','000','000','pipeline-IC++ net','pipeline-IC++ net','004','pipeline-IC++ net',p_buisnessClass,LPAD( p_client_number, 15, '0' ),p_service_contract_id,p_condition_set,p_acct_currency,NULL,'001',NULL,NULL,NULL,p_settlement_method,p_posting_method,'0000000000',NULL,NULL,'20200101','19337-163159-900129-129-99999-00002','280','Frankfurt',NULL,'00000059',NULL,'003',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'6000',NULL,NULL,NULL,NULL,'abc',NULL,'000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'999',NULL,NULL,'001','00000506','2','000','0','000',NULL,'000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'000','0000',NULL,NULL,'000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'000001',NULL,NULL,NULL,NULL,NULL,NULL,'001',NULL,NULL,NULL,NULL,NULL,'999',NULL,NULL,NULL,p_merch_tran_tariff,NULL);
     
    
   INSERT INTO CIS_APPL_TERMINAL_INPUT (APPLICATION_NUMBER,TERMINAL_ID,INSTITUTION_NUMBER,MERCHANT_ID,RECORD_DATE,TERMINAL_STATUS,SERIAL_NUMBER,TERMINAL_TYPE,CONTACT_NAME,DEVICE_CAPABILITY,LOCATION,EOD_INDICATOR,TERMINAL_CURRENCY,POS_FEE_TYPE,AUDIT_TRAIL,LOCKING_COUNTER,CONTACT_PERSON,TELEPHONE_NUMBER,TERMINAL_SERIAL_NO,TERMINAL_LOCATION,NOTE_TEXT,MANUAL_ENTRY,FOREIGN_CARD_FLAG,TMS_REFERENCE_ID,FAMS,SETTLEMENT_INDICATOR) VALUES 
 		(v_application_number,'P'||substr(p_client_number,-7,7),'00000111',LPAD( p_client_number, 15, '0' ),'20191130','001',NULL,NULL,NULL,'006',NULL,'000',p_acct_currency,'000','19336-144511-900133-129-02160-53111','0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
   
 	FOR i IN 1..p_address_categs.count LOOP
	   INSERT INTO CIS_APPLICATION_ADDR (INSTITUTION_NUMBER,APPLICATION_NUMBER,ADDRESS_CATEGORY,RECORD_DATE,ADDR_LINE_1,ADDR_LINE_2,ADDR_LINE_3,ADDR_LINE_4,ADDR_LINE_5,POST_CODE,ADDR_CLIENT_CITY,CLIENT_COUNTRY,AUDIT_TRAIL,RECORD_TYPE,TEL_WORK,FAX_WORK,CONTACT_NAME,EMAIL_ADDR,GREETING,DELIVERY_METHOD,EFFECTIVE_DATE,GROUP_SPECIFIC,CLIENT_STATE,CLIENT_REGION,TEL_HOME,TEL_OTHER,TEL_MOBILE,ADDR_LANGUAGE,PO_BOX,LOCKING_COUNTER,EMAIL_ADDR_2)    VALUES
       ('00000111',v_application_number,p_address_categs(i),'20191130','abc1',NULL,NULL,NULL,NULL,'6000',NULL,'280','19336-142912-900133-129-00599-52152','003',NULL,NULL,NULL,'email@rs2.com',NULL,'000','20191130','000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL);
    END LOOP;
 	
    FOR i IN 1..p_account_type_ids.count LOOP
	  INSERT INTO CIS_APPLICATION_ACCT_TYPE (INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,ACCT_CURRENCY,RECORD_TYPE,RECORD_DATE,BILLING_LEVEL,BANK_CLEARING_NUMBER,SETTLEMENT_BANK_NAME,SETTLEMENT_BANK_CITY,BANK_CONTACT_NAME,BANK_TEL_NUMBER,COUNTER_BANK_ACCOUNT,LIMIT_CURRENCY,CLIENT_LIMIT,BANK_GUARANTEE,PAYMENT_REFERENCE,AUDIT_TRAIL,CORRESP_BANK_NUMBER,CORRESP_BANK_ACCOUNT,COUNTER_BANK_ACCOUNT_NAME,STATEMENT_GENERATION,ACCT_NUMBER_RBS,ACCT_NUMBER,STATEMENT_TYPE,LIMIT_DISTRIBUTION,PARENT_LIMIT_NUMBER,LIMIT_NUMBER,SETTL_PERC_TOTAL,CASH_LIMIT,CASH_LIMIT_PERCENT,BILLING_CYCLE,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE,SETTLEMENT_BANK_CITY_DR,ACCT_STARTUP_DATE,BANK_CLEARING_NUMBER_DR,BANK_CONTACT_NAME_DR,BANK_TEL_NUMBER_DR,CLEARING_ENTITY,CLEARING_ENTITY_DR,CORRESP_BANK_ACCOUNT_DR,CORRESP_BANK_NUMBER_DR,COUNTER_BANK_ACCOUNT_DR,COUNTER_BANK_ACCOUNT_NAME_DR,COUNTER_BANK_ACCOUNT_TYPE,FUNDING_NARRATIVE,FUNDING_NARRATIVE_DR,IBAN_REFERENCE,IBAN_REFERENCE_DR,PAYMENT_METHOD,PAYMENT_METHOD_DR,REUTERS_CODE,REUTERS_CODE_DR,SETTLEMENT_BANK_NAME_DR,BRANCH_REGISTRATION,BILLING_CLIENT,PAYMENT_BANK_CLIENT,LOCK_IN_FLAG,CALENDAR_DAYS_DR,CALENDAR_DAYS,COUNTER_BANK_NUMBER,FUNDING_BANK,CALENDAR_TYPE,PREPAYMENT_ID,ALLOCATION_RULE,LIMIT_OVERRIDE_PERCENT,REVIEW_PERIOD,ACCOUNT_BASE_ID,SETTLEMENT_NUMBER,SEC_COUNTER_BANK_ACCOUNT,SEC_COUNTER_BANK_ACCOUNT_DR,SEC_IBAN_REFERENCE,SEC_IBAN_REFERENCE_DR,NOTE_TEXT,CLIENT_ACCOUNT_NAME) VALUES 
       ('00000111',v_application_number,p_account_type_ids(i),p_acct_currency,'003','20191130','001',NULL,NULL,NULL,NULL,NULL,'DE54506521240034000075',NULL,NULL,NULL,NULL,'pipeline insert',NULL,NULL,NULL,'001',NULL,NULL,'900',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',null,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
    END LOOP;
---------------------------GBP/USD PAYMENT ACCOUNT -------------   
  --GBP
   INSERT INTO CIS_APPLICATION_ACCT_TYPE (INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,ACCT_CURRENCY,RECORD_TYPE,RECORD_DATE,BILLING_LEVEL,BANK_CLEARING_NUMBER,SETTLEMENT_BANK_NAME,SETTLEMENT_BANK_CITY,BANK_CONTACT_NAME,BANK_TEL_NUMBER,COUNTER_BANK_ACCOUNT,LIMIT_CURRENCY,CLIENT_LIMIT,BANK_GUARANTEE,PAYMENT_REFERENCE,AUDIT_TRAIL,CORRESP_BANK_NUMBER,CORRESP_BANK_ACCOUNT,COUNTER_BANK_ACCOUNT_NAME,STATEMENT_GENERATION,ACCT_NUMBER_RBS,ACCT_NUMBER,STATEMENT_TYPE,LIMIT_DISTRIBUTION,PARENT_LIMIT_NUMBER,LIMIT_NUMBER,SETTL_PERC_TOTAL,CASH_LIMIT,CASH_LIMIT_PERCENT,BILLING_CYCLE,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE,SETTLEMENT_BANK_CITY_DR,ACCT_STARTUP_DATE,BANK_CLEARING_NUMBER_DR,BANK_CONTACT_NAME_DR,BANK_TEL_NUMBER_DR,CLEARING_ENTITY,CLEARING_ENTITY_DR,CORRESP_BANK_ACCOUNT_DR,CORRESP_BANK_NUMBER_DR,COUNTER_BANK_ACCOUNT_DR,COUNTER_BANK_ACCOUNT_NAME_DR,COUNTER_BANK_ACCOUNT_TYPE,FUNDING_NARRATIVE,FUNDING_NARRATIVE_DR,IBAN_REFERENCE,IBAN_REFERENCE_DR,PAYMENT_METHOD,PAYMENT_METHOD_DR,REUTERS_CODE,REUTERS_CODE_DR,SETTLEMENT_BANK_NAME_DR,BRANCH_REGISTRATION,BILLING_CLIENT,PAYMENT_BANK_CLIENT,LOCK_IN_FLAG,CALENDAR_DAYS_DR,CALENDAR_DAYS,COUNTER_BANK_NUMBER,FUNDING_BANK,CALENDAR_TYPE,PREPAYMENT_ID,ALLOCATION_RULE,LIMIT_OVERRIDE_PERCENT,REVIEW_PERIOD,ACCOUNT_BASE_ID,SETTLEMENT_NUMBER,SEC_COUNTER_BANK_ACCOUNT,SEC_COUNTER_BANK_ACCOUNT_DR,SEC_IBAN_REFERENCE,SEC_IBAN_REFERENCE_DR,NOTE_TEXT,CLIENT_ACCOUNT_NAME) VALUES 
          ('00000111',v_application_number,'012','826','003','20191130','001',NULL,NULL,NULL,NULL,NULL,'DE54506521240034000075',NULL,NULL,NULL,NULL,'pipeline insert',NULL,NULL,NULL,'001',NULL,NULL,'900',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',null,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
  --USD
   INSERT INTO CIS_APPLICATION_ACCT_TYPE (INSTITUTION_NUMBER,APPLICATION_NUMBER,ACCOUNT_TYPE_ID,ACCT_CURRENCY,RECORD_TYPE,RECORD_DATE,BILLING_LEVEL,BANK_CLEARING_NUMBER,SETTLEMENT_BANK_NAME,SETTLEMENT_BANK_CITY,BANK_CONTACT_NAME,BANK_TEL_NUMBER,COUNTER_BANK_ACCOUNT,LIMIT_CURRENCY,CLIENT_LIMIT,BANK_GUARANTEE,PAYMENT_REFERENCE,AUDIT_TRAIL,CORRESP_BANK_NUMBER,CORRESP_BANK_ACCOUNT,COUNTER_BANK_ACCOUNT_NAME,STATEMENT_GENERATION,ACCT_NUMBER_RBS,ACCT_NUMBER,STATEMENT_TYPE,LIMIT_DISTRIBUTION,PARENT_LIMIT_NUMBER,LIMIT_NUMBER,SETTL_PERC_TOTAL,CASH_LIMIT,CASH_LIMIT_PERCENT,BILLING_CYCLE,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE,SETTLEMENT_BANK_CITY_DR,ACCT_STARTUP_DATE,BANK_CLEARING_NUMBER_DR,BANK_CONTACT_NAME_DR,BANK_TEL_NUMBER_DR,CLEARING_ENTITY,CLEARING_ENTITY_DR,CORRESP_BANK_ACCOUNT_DR,CORRESP_BANK_NUMBER_DR,COUNTER_BANK_ACCOUNT_DR,COUNTER_BANK_ACCOUNT_NAME_DR,COUNTER_BANK_ACCOUNT_TYPE,FUNDING_NARRATIVE,FUNDING_NARRATIVE_DR,IBAN_REFERENCE,IBAN_REFERENCE_DR,PAYMENT_METHOD,PAYMENT_METHOD_DR,REUTERS_CODE,REUTERS_CODE_DR,SETTLEMENT_BANK_NAME_DR,BRANCH_REGISTRATION,BILLING_CLIENT,PAYMENT_BANK_CLIENT,LOCK_IN_FLAG,CALENDAR_DAYS_DR,CALENDAR_DAYS,COUNTER_BANK_NUMBER,FUNDING_BANK,CALENDAR_TYPE,PREPAYMENT_ID,ALLOCATION_RULE,LIMIT_OVERRIDE_PERCENT,REVIEW_PERIOD,ACCOUNT_BASE_ID,SETTLEMENT_NUMBER,SEC_COUNTER_BANK_ACCOUNT,SEC_COUNTER_BANK_ACCOUNT_DR,SEC_IBAN_REFERENCE,SEC_IBAN_REFERENCE_DR,NOTE_TEXT,CLIENT_ACCOUNT_NAME) VALUES 
          ('00000111',v_application_number,'012','840','003','20191130','001',NULL,NULL,NULL,NULL,NULL,'DE54506521240034000075',NULL,NULL,NULL,NULL,'pipeline insert',NULL,NULL,NULL,'001',NULL,NULL,'900',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',null,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
----------------------------------------------------------------

 --CLIENT TARRIF IC++ (000511)
  FOR i IN 1..p_service_ids.count LOOP
		Insert into CIS_APPLICATION_SERVICES (INSTITUTION_NUMBER,APPLICATION_NUMBER,RECORD_DATE,AUDIT_TRAIL,SERVICE_CONTRACT_ID,SERVICE_ID,SERVICE_ASSIGNED,SERVICE_INFO,RECORD_TYPE,SERVICE_DATA,CLIENT_TARIFF,SERVICE_MISC_DATA1,PIN_REQUIRED,CLIENT_TYPE,LOCKING_COUNTER,BANK_REFERENCE,REVIEW_DATE,REVIEW_PERIOD,PLASTIC_TYPE,CARD_EVENT_STATUS,MOTIF_CODE,NO_OF_INSTALLMENTS,SERVICE_NUMBER,CLIENT_TARIFF$3) values 
	    ('00000111',v_application_number,'20191130','19021-090443-000110-129-99999-00001',p_service_contract_id,p_service_ids(i),'001','0.00','003',null,p_client_tariff,null,null,null,'0',null,null,null,null,null,null,null,null,null);
  END LOOP;

  --useless insert according to luke
  --FOR i IN 1..p_settlement.count LOOP
  --      Insert into CIS_SETTLEMENT_INFORMATION (RECORD_DATE,INSTITUTION_NUMBER,CLIENT_NUMBER,SETTLEMENT_CATEGORY,AUDIT_TRAIL,REUTERS_CODE,ACCT_CURRENCY,CORRESP_BANK_NUMBER,COUNTER_BANK_NUMBER,COUNTER_CLIENT_NUMBER,CORRESP_BANK_ACCOUNT,COUNTER_BANK_ACCOUNT,CONTING_LIAB_ACCOUNT,SETTLEMENT_NUMBER,NOTE_TEXT,CONFIRMATION_METHOD,BANK_CONTACT_NAME,BANK_TEL_NUMBER,PAYMENT_REFERENCE,BANK_CLEARING_NUMBER,COUNTER_BANK_NAME,COUNTER_BANK_ACCOUNT_NAME,SETTL_PERC_TOTAL,RECORD_TYPE,ACCT_NUMBER_RBS,LOCKING_COUNTER,RECEIVER_COUNTRY_CODE,COUNTER_BANK_CITY,IBAN_REFERENCE,FUNDING_NARRATIVE,REUTERS_CODE_DR,CORRESP_BANK_NUMBER_DR,COUNTER_BANK_NUMBER_DR,CORRESP_BANK_ACCOUNT_DR,COUNTER_BANK_ACCOUNT_DR,NOTE_TEXT_DR,BANK_CONTACT_NAME_DR,BANK_TEL_NUMBER_DR,COUNTER_BANK_NAME_DR,COUNTER_BANK_ACCOUNT_NAME_DR,COUNTER_BANK_CITY_DR,IBAN_REFERENCE_DR,FUNDING_NARRATIVE_DR,PAYMENT_METHOD,CLEARING_ENTITY,PAYMENT_METHOD_DR,CLEARING_ENTITY_DR,CALENDAR_DAYS,CALENDAR_DAYS_DR,COUNTER_BANK_ACCOUNT_TYPE,CALENDAR_TYPE,FUNDING_CLIENT) values 
  --       ('20191130','00000111',p_client_number,lpad(i, 3, '0'),'Pipeline Insert',null,p_acct_currency,null,null,null,null,'DE54506521240034000075',null,lpad(i, 2, '0'),null,'004',null,null,null,'00000000',null,null,null,'003',null,'0',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,'0','0',null,null,null);
  --END LOOP;

 dbms_output.put_line('Successfully created merchant:' || p_client_number);
 commit;
End;
/
-- Main function
declare
  address_categs  p_type;
	account_ids     p_type;
	sid             p_type;
	settlement      p_type;	
	v_init_return   pls_integer;
	v_billing_level varchar(3);
	v_service_contract_id varchar(3);
	v_posting_method varchar(3);
	v_currency varchar(3);
	v_BuisnessClass varchar(4);
	v_condition_set varchar(6);
	v_merch_tran_tariff varchar(6);
	v_settlement_method varchar(3);
	v_client_tariff varchar(6);
	v_client_number varchar(8);
begin

	BW_PRC_RES.INITGLOBALVARS ('00000111','129','999999', v_init_return);

    v_client_number :=  '69444444';--BW_CODE_LIBRARY.GETNEXTSEQNUMBER('010',1); --Client ID
	
  	account_ids := p_type(); account_ids.extend(3);
	account_ids(1) := '007'; account_ids(2) := '012'; account_ids(3) := '009';--009 Dispute Account --account_ids(2) := '015';
	
	address_categs := p_type(); address_categs.extend(1);
	address_categs(1) := '001';

	v_service_contract_id := '112';

	sid := p_type(); sid.extend(3);
	sid(1) := '201'; sid(2) := '102'; sid(3):='202';

	settlement := p_type(); settlement.extend(2);
	settlement(1) := '001';  settlement(2) := '002'; 

	v_billing_level := '001';
    
	v_posting_method :='001';--002=GROSS +charges ,--001=NET  
	v_currency :='978'; --EUR
	v_settlement_method :='026'; --026 = Bi-Wly NOreserve NET;021 = Wly 1 NOreserve GR --; 010 = Dly 7 NOreserve NET 
	v_BuisnessClass := '5999';
	v_condition_set:= '000011';--GROSS ='000011'; NET ='000011';
	v_merch_tran_tariff := '000000'; --GROSS ='000000'; NET ='000000';
	v_client_tariff := '000511'; --000511 = IC++ ; 000502 =	Blended Gross+Charge; --000501 = Blended NET;

    create_merchant(v_client_number, address_categs, account_ids, sid, settlement, v_billing_level, v_service_contract_id, v_condition_set,v_merch_tran_tariff, v_settlement_method, v_posting_method, v_currency, v_BuisnessClass, v_client_tariff);

  COMMIT;
end;
/
drop procedure create_merchant;
