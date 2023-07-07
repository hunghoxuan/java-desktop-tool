define institution_number = '00000111';
define client_number = '00000181';
define currency = '978';
define start_date = '20220315';
define end_date = '20220322';

select * from bwt_transaction_class where language = 'USA' and institution_number = &institution_number order by 1;

--1)find merchant payment file_number per date
select file_number, transaction_category, transaction_class, tran_amount_gr from int_transactions where
INSTITUTION_NUMBER  = &institution_number and record_date = '20220322' and transaction_category in ('008') and transaction_class in ('002');

--00002661
select transaction_slip from int_transactions where file_number = '00002661' and INSTITUTION_NUMBER  = &institution_number
and transaction_class = '002'
 order by transaction_class; ---21100063166  326.24

--2) find merchant transactions   which has been funded from above merchant payment file
select summary_settlement, file_number from  int_transactions t1
where t1.retrieval_reference =
(select transaction_slip from int_transactions where file_number = '00002661' and INSTITUTION_NUMBER  = &institution_number
and transaction_class = '002')
and INSTITUTION_NUMBER  = &institution_number
and client_number = &client_number
and transaction_class = '012';
 order by 1;  -- file_number      -- 330  10    200      120
   ---03 files
	-- 00002585
	--00002587
	--00002595

	--- accordingly with the Files for Fees generated with Merchant transaction file  00002586 00002588    00002596

select transaction_slip, card_number, retrieval_reference from int_transactions where file_number = '00002585'  and   INSTITUTION_NUMBER  = '00000111'  and transaction_class = '002'
 and summary_settlement = '21000060867'
  order by transaction_class;

--check 1 merchant transaction  file 00002585
select acct_currency, institution_number, client_number, transaction_slip, card_number, retrieval_reference, tran_amount_gr, transaction_class, transaction_type, transaction_category, transaction_status, record_date
from int_transactions where
file_number = '00002585' and INSTITUTION_NUMBER  = '00000111' and transaction_class = '002'
 and summary_settlement = '21000060867'
  order by transaction_class;

-- who the hell is 90000001
select * from cis_client_details where client_number = '90000001' and institution_number = '00000111';

------
select
-- institution_number, client_number, acct_currency,
transaction_slip, card_number, retrieval_reference, tran_amount_gr, transaction_class, transaction_type, transaction_category, transaction_status, record_date
from int_transactions
where
-- file_number = '00002585' and summary_settlement = '21000060867'
INSTITUTION_NUMBER  = '00000111' and transaction_class = '002' and client_number = '90000001' and record_date between &start_date and &end_date and acct_currency = &currency
order by transaction_class;
-------

select t1.client_number, t2.card_number
from int_transactions t1
inner join int_transactions t2;



  --- 02 transactions  for this file
--  5123150000000004
--5101412000090005

--3) ------find card organization based on transaction_destination in merchant transactions file
select card_organization from CBR_CHANNEL_DEFINITION where TRANSACTION_DESTINATION     = '154' and INSTITUTION_NUMBER  = &institution_number order by 1; ---002

---4) find card_brand
select card_brand from SYS_MAST_INTER_BIN_TABLE where START_BIN_VALUE  like '512315%' order by 1;  --card_brand = '010'


--5) find service_id
select * from CBR_SERVICE_DEFINITION where INSTITUTION_NUMBER    = '00000111'
and card_organization = '002'
and card_brand = '010'
and service_category = '002' --acquiring
  ;

-- 6) find service name
select * from bwt_services where index_field = 102 and language = 'USA' and institution_number = &institution_number;


select summary_settlement, file_number from  int_transactions t1
where t1.retrieval_reference =  (select transaction_slip from int_transactions where file_number = '00002661' and INSTITUTION_NUMBER  = &institution_number
and transaction_class = '002')
and INSTITUTION_NUMBER  = &institution_number and client_number = &client_number
and transaction_class = '012';

