 	select * from (
 		select
 		    lnk.GROUP_NUMBER,
 		    lnk.PARENT_CLIENT_NUMBER,
            cdet.CLIENT_NUMBER,
              
                cdet.INSTITUTION_NUMBER,
            lnk.CLIENT_LEVEL,
            cdet.TITLE,
            cdet.SHORT_NAME,
            cdet.LEGAL_FORM,

            cdet.RECORD_TYPE,
            cdet.RECORD_DATE,
            cdet.AUDIT_TRAIL,
            cdet.CONTACT_NAME,
            cdet.CLIENT_STATUS,
            cdet.CLIENT_BRANCH,
            cdet.BUSINESS_CLASS,
            cdet.VAT_REG_NUMBER,
            cdet.CLIENT_LANGUAGE,
            cdet.CLIENT_NUMBER_RBS,
            cdet.WEBSITE_ADDRESS,
            cdet.ECOMMERCE_INDICATOR,
            cdet.CLIENT_REGION,
            cdet.TEL_PRIVATE,
            cdet.TEL_WORK,
            cdet.REGISTRATION_NUMBER,
            cdet.COMPANY_NAME,
            cdet.TRADE_NAME,
            cdet.OUR_REFERENCE,
            cdet.MOBILE_NO_1,
            cdet.MOBILE_NO_2,
            cdet.CLIENT_TYPE,
            lnk.SERVICE_CONTRACT_ID,
            '' CLIENT_CHOOSER_FILTER
          from
            BW3.CIS_CLIENT_DETAILS cdet,
            BW3.CIS_CLIENT_LINKS lnk
          where (
            cdet.INSTITUTION_NUMBER = '&institution_number'
            and lnk.INSTITUTION_NUMBER = cdet.INSTITUTION_NUMBER
            and lnk.CLIENT_NUMBER = cdet.CLIENT_NUMBER
            and lnk.CONTRACT_STATUS in (
              '001', '002', '003'
            )
            and lnk.EFFECTIVE_DATE = (
              select max(sublnk.EFFECTIVE_DATE)
              from BW3.CIS_CLIENT_LINKS sublnk
              where (
                sublnk.CLIENT_NUMBER = lnk.CLIENT_NUMBER
                and sublnk.GROUP_NUMBER = lnk.GROUP_NUMBER
                and sublnk.SERVICE_CONTRACT_ID = lnk.SERVICE_CONTRACT_ID
                and sublnk.INSTITUTION_NUMBER = lnk.INSTITUTION_NUMBER
                and sublnk.CLIENT_LEVEL = lnk.CLIENT_LEVEL
                and sublnk.EFFECTIVE_DATE <= '20300131'
              )
            )
          )
        ) lnk
        start with (
          lnk.CLIENT_NUMBER = '&client_number'
          and lnk.INSTITUTION_NUMBER = '&institution_number'
        )
        connect by (
          prior lnk.CLIENT_NUMBER = lnk.PARENT_CLIENT_NUMBER
          and prior lnk.GROUP_NUMBER = lnk.GROUP_NUMBER
          and prior lnk.INSTITUTION_NUMBER = lnk.INSTITUTION_NUMBER
          and prior lnk.CLIENT_LEVEL <> '001'
          and prior lnk.CLIENT_NUMBER <> lnk.INSTITUTION_NUMBER
        );
		
		
-- accounts hierachy
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
			order by a.group_number, a.ACCOUNT_TYPE_ID,a.ACCT_CURRENCY, a.acct_number asc;

-- client_links hierachy
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
			-- and ((a.client_number in (':client_number') and a.client_level in ('001')) or (a.client_level in ('002', '003')))
			and a.group_number in (select distinct group_number from cis_client_links where client_number in ('&client_number') and institution_number in ('&institution_number'))
			and a.institution_number in ('&institution_number')
			and a.service_contract_id in (select service_contract_id from cis_client_links where institution_number = '&institution_number' and client_number in ('&client_number'))
			order by group_number,client_number asc;
