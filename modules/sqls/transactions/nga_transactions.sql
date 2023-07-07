select * from INT_TRANSACTIONS    where file_number in (2595,2596) and  institution_number = '00000111' order by file_number, transaction_class;

select * from int_file_log_details where file_number = '00002661' and  institution_number = '00000111' order by 1;
-- 4149089999050823
--5115800300000001


select t4.merchant_file_number, t4.TRAN_AMOUNT_MERCHANT_FILE,t4.TRAN_AMOUNT_FEE_FILE, t4.fees_file_number, t.card_number, t.* from (

 select distinct t1.institution_number, t1.file_number as merchant_file_number , t2.file_number as fees_file_number,  t1.TRAN_AMOUNT_GR as TRAN_AMOUNT_MERCHANT_FILE, t2.TRAN_AMOUNT_GR as TRAN_AMOUNT_FEE_FILE from INT_TRANSACTIONS t1, INT_TRANSACTIONS  t2
 where    t1.institution_number = t2.institution_number
 and t1.institution_number = '00000111'
 and  t1.transaction_class = '012'
 and  t1.transaction_source = '137'
 and t1.transaction_destination = '018'
 and t1.retrieval_reference = t2.retrieval_reference
 and  t2.transaction_class = '002'
 and  t2.transaction_source = '031'
 and t2.transaction_destination = '018'
 and t1.value_date = '20220322'
 ) t4 ,   INT_TRANSACTIONS t
 where t4.institution_number = t.institution_number
 and  t.institution_number = '00000111'
 and t.file_number = t4.merchant_file_number
and t.transaction_class = '002'
and t.transaction_source = '137'
and t.transaction_category = '001'
 and t.value_date = '20220322'

order by 1;

select * from INT_TRANSACTIONS where file_number = '00001201' and   institution_number = '00000111' order by 1;
select * from INT_TRANSACTIONS where  file_number = '00001198' and   institution_number = '00000111' order by 1;
select * from INT_TRANSACTIONS where  file_number = '00001199' and   institution_number = '00000111' order by 1;


 select distinct t1.retrieval_reference, t1.tran_amount_gr, t2.tran_amount_gr, t1.institution_number, t1.file_number as merchant_file_number , t2.file_number as fees_file_number from INT_TRANSACTIONS t1, INT_TRANSACTIONS  t2
 where
 t1.institution_number = t2.institution_number
 and t1.institution_number = '00000111'
 and t1.client_number = 181
 and  t1.transaction_class = '012'
 and  t1.transaction_source = '137'
 and t1.transaction_destination = '018'
 and t1.retrieval_reference = t2.retrieval_reference
 and t2.retrieval_reference is not null
  and  t2.transaction_class = '002'
 and  t2.transaction_source = '031'
 and t2.transaction_destination = '018'
 and t1.value_date = '20220322'
 ;




select distinct t1.value_date, t1.file_number as merchant_file_number , t2.file_number as fees_file_number,  t1.TRAN_AMOUNT_GR as TRAN_AMOUNT_MERCHANT_FILE, t2.TRAN_AMOUNT_GR as TRAN_AMOUNT_FEE_FILE from INT_TRANSACTIONS t1, INT_TRANSACTIONS  t2
 where    t1.institution_number = t2.institution_number
 and t1.institution_number = '00000111'
 and  t1.transaction_class = '012'
 and  t1.transaction_source in ('137', '005')
 and t1.transaction_destination = '018'

 and t1.retrieval_reference = t2.retrieval_reference
 and t1.client_number = 181
 and t1.client_number = t2.client_number
 and t1.file_number < t2.file_number

 and t1.retrieval_reference is not null
 and  t2.transaction_class = '002'
 and  t2.transaction_source = '031'
 and t2.transaction_destination = '018'
 and t1.transaction_status in ('004', '007', '009')
 and t1.value_date = '20220322'
 --and t1.acct_number = t2.acct_number
 order by t1.value_date, merchant_file_number;


select * from int_file_log_details where file_number = '00002586' and  institution_number = '00000111' order by 1  desc ;
select * from int_file_log_details where file_number = '00002585' and  institution_number = '00000111' order by 1  desc ;
select * from int_transactions where file_number = '00002585' and transaction_status in ('004', '007', '009') and institution_number = '00000111' and transaction_class = 002 and transaction_category = 001 order by 1  desc ;
select * from int_transactions where file_number in ('00002586', '00002588', '00002596') and  institution_number = '00000111' and transaction_class = 002 and transaction_category = 007 order by 1  desc ;

select * from cht_transaction_status order by index_field;

--0000012139
/*
21000061024
21000061027

21000061017
21000061021


207508038179
207515529192




