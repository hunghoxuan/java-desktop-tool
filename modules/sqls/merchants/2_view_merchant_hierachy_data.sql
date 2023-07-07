
-- @name: ClientDetail, transmits: institution_number,client_number
select * from cis_client_details where INSTITUTION_NUMBER in ('&institution_number') and client_number in ('&client_number');

	
-- @name: Settlement Information, parent: ClientDetail
select si.INSTITUTION_NUMBER, si.client_number, cl.entity_id, si.funding_client, si.settlement_number, si.RECORD_DATE,si.ACCT_CURRENCY,si.COUNTER_BANK_NUMBER,si.COUNTER_BANK_ACCOUNT,si.NOTE_TEXT,si.CONFIRMATION_METHOD,si.RECEIVER_COUNTRY_CODE
from cis_settlement_information si, cis_client_links cl 
where si.institution_number = cl.institution_number and si.client_number = cl.client_number 
and si.institution_number = '&institution_number' and si.client_number = '&client_number'

-- @name: cis_device_link, parent: ClientDetail
select * from cis_device_link where institution_number = &institution_number and client_number = &client_number
	
-- @name: CIS_ADDRESSES, parent: ClientDetail
select * from CIS_ADDRESSES where institution_number = &institution_number and client_number = &client_number


-- @name: Contracts, parent: ClientDetail, transmits: INSTITUTION_NUMBER,group_number, transmitedCondition: INSTITUTION_NUMBER,group_number	
select distinct service_contract_id, group_number from cis_client_links where INSTITUTION_NUMBER in ('&institution_number') and client_number in ('&client_number');

-- @name: CLIENT_LINKS, parent: Contracts, KeyColumn: CLIENT_NUMBER, ParentColumn: parent_client_number, TransmittedConditionColumns: INSTITUTION_NUMBER,group_number
select
a.group_number,
a.CLIENT_NUMBER,
a.parent_client_number,
b.trade_name as client_name,
a.service_contract_id,
a.entity_id,
decode(a.client_level,'001','001 : Member Level','002','002 : Group Level', '003 : Sub-Group Level') as client_level,
a.CLIENT_BRANCH,
b.CLIENT_REGION,
a.effective_Date,
a.client_tariff,
a.posting_method,
a.settlement_method,
b.CLIENT_COUNTRY, 
a.CONTRACT_REFERENCE,
b.OUR_REFERENCE,
b.client_type,
b.record_type
from
cis_client_links a, cis_client_details b
where  a.client_number=b.client_number
and a.INSTITUTION_NUMBER=b.INSTITUTION_NUMBER
-- and ((a.client_number in ('&client_number') and a.client_level in ('001')) or (a.client_level in ('002', '003')))
and a.group_number in (select distinct group_number from cis_client_links where client_number in ('&client_number') and institution_number in ('&institution_number'))
and a.institution_number in ('&institution_number') 
order by group_number,client_level desc
	

-- @name: Client Accounts, parent: Contracts, KeyColumn: ACCT_NUMBER, ParentColumn: PARENT_ACCT_NUMBER, TransmittedConditionColumns: settlement_number,client_number, account_number
select
a.group_number,
a.ACCT_NUMBER, 
nvl(PARENT_ACCOUNT_NUMBER,'null') as PARENT_ACCT_NUMBER, 
CLIENT_ACCOUNT_NAME, 
a.ACCOUNT_LEVEL,
a.client_number,
a.ACCOUNT_TYPE_ID,
decode(a.BILLING_LEVEL, '000','No','Yes') as  BILLING_LEVEL,
a.ACCT_CURRENCY,
BEGIN_BALANCE, CURRENT_BALANCE,
DATE_CYCLE_START||'-'||DATE_CYCLE_END as current_cycle,
e.POSTING_METHOD,
a.SERVICE_CONTRACT_ID, SETTLEMENT_NUMBER , LAST_SETTLEMENT_DATE, LAST_FEE_DATE,  LAST_STATEMENT_DATE,STATEMENT_TYPE,  STATEMENT_GENERATION
from
CAS_CLIENT_ACCOUNT a,
cas_cycle_book_balance d,
cis_client_links e

where  
a.INSTITUTION_NUMBER =d.INSTITUTION_NUMBER(+) and a.acct_number=d.acct_number(+) and d.PROCESSING_STATUS(+) ='004'
and e.effective_Date = (select max (EFFECTIVE_DATE)from CIS_CLIENT_LINKS where INSTITUTION_NUMBER= e.INSTITUTION_NUMBER and CLIENT_NUMBER=e.CLIENT_NUMBER and GROUP_NUMBER=e.GROUP_NUMBER and SERVICE_CONTRACT_ID=e.SERVICE_CONTRACT_ID and CLIENT_LEVEL=e.CLIENT_LEVEL)
and e.client_number! = e.parent_client_number
and a.INSTITUTION_NUMBER = e.INSTITUTION_NUMBER
and a.client_number = e.client_number
and a.INSTITUTION_NUMBER in ('&institution_number')
and a.group_number in (select group_number from cis_client_links where client_number in ('&client_number') and institution_number in ('&institution_number'))
and a.SERVICE_CONTRACT_ID = e.SERVICE_CONTRACT_ID
and a.group_number = e.group_number
order by a.group_number, a.ACCOUNT_TYPE_ID,a.acct_number,a.ACCT_CURRENCY;