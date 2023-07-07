define institution_number = '00002001';
define client_number = '00000041';
define currency = '978';
define start_date = '20220215';
define end_date = '20220322';

-- eur

-- CAS_CLIENT_ACCOUNT [CLIENT_NUMBER]: Found 1 records.
     SELECT * FROM CAS_CLIENT_ACCOUNT WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_ADDRESSES [CLIENT_NUMBER]: Found 1 records.
     SELECT * FROM CIS_ADDRESSES WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_APPLICATION_DETAIL [CLIENT_NUMBER]: Found 2 records.
     SELECT * FROM CIS_APPLICATION_DETAIL WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_CLIENT_DETAILS [CLIENT_NUMBER]: Found 1 records.
     SELECT * FROM CIS_CLIENT_DETAILS WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_CLIENT_LINKS [CLIENT_NUMBER]: Found 1 records.
     SELECT * FROM CIS_CLIENT_LINKS WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_DEVICE_LINK [CLIENT_NUMBER]: Found 1 records.
     SELECT * FROM CIS_DEVICE_LINK WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- CIS_SETTLEMENT_INFORMATION [CLIENT_NUMBER]: Found 2 records.
     SELECT * FROM CIS_SETTLEMENT_INFORMATION WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

-- INT_BATCH_CAPTURE [CLIENT_NUMBER]: Found 1 records.
     SELECT  (bts.index_field || ' - ' || bts.transaction_status) as transaction_status,
     tran_amount_gr, bc.*
     FROM INT_BATCH_CAPTURE bc
     inner join bwt_transaction_status bts on bts.index_field = bc.transaction_status and bts.institution_number = &institution_number and bts.language = 'USA'
     WHERE (bc.institution_number like  &institution_number) AND (UPPER(bc.CLIENT_NUMBER) LIKE &client_number)
     order by record_date;

-- INT_TRANSACTIONS [CLIENT_NUMBER]: bts 2 records.

--- Final ???
SELECT
	T.transaction_slip,
	t.card_number as card_number,
 	T.transaction_date as transaction_date,
  	(cdl.location || ' - ' || cdl.pos_region || ' - ' || cdl.pos_city) as location,
   	(btc.index_field || ' - ' || btc.transaction_category) as transaction_category,
    (btc1.index_field || ' - ' || btc1.transaction_class) as transaction_class,
    (btt.index_field || ' - ' || btt.transaction_type) as transaction_type,
    (bcb.index_field || ' - ' || bcb.card_brand) as card_brand,
    (bco.index_field || ' - ' || bco.card_organization) as card_organization,
    t.account_amount_gr * decode(t."TRANSACTION_TYPE", '005',1,-1) as gross_amount,
    (bc.iso_code || ' - ' || bc.name) as transaction_currency,
    t.account_amount_net * decode(t."TRANSACTION_TYPE", '005',1,-1) as net_amount,
        t.account_amount_chg * decode(t."TRANSACTION_TYPE", '005',1,-1) as charge_amount,
    (bc1.iso_code || ' - ' || bc1.name) as settlement_currency,
    t.rate_fx_tran_settl as rate_fx,
    (bts.index_field || ' - ' || bts.transaction_status) as transaction_status,
    (bct.index_field || ' - ' || bct.charge_type) as charge_type,
    (bcm.index_field || ' - ' || bcm.capture_method) as sale_channel,
    t.retrieval_reference,
    t.card_number,
    t.acquirer_reference,
    ccd.our_reference as merchant_id,
    t.terminal_id,
    T.summary_settlement as summary_number,
    t.rate_fx_tran_settl as exchange_rate,
	T.*
FROM INT_TRANSACTIONS T
	left join bwt_transaction_status bts on bts.index_field = T.transaction_status and bts.institution_number = t.institution_number and bts.language = 'USA'
 	left join bwt_transaction_category btc on btc.index_field = T.transaction_category and btc.institution_number = t.institution_number and btc.language = 'USA'
  	left join bwt_transaction_class btc1 on btc1.index_field = T.transaction_class and btc1.institution_number = t.institution_number and btc1.language = 'USA'
   	left join BWT_SIGN_OPERATORS bso on bso.index_field = T.dr_cr_indicator and bso.institution_number = t.institution_number and bso.language = 'USA'
    left join BWT_TRANSACTION_TYPE btt on btt.index_field = T.transaction_type and btt.institution_number = t.institution_number and btt.language = 'USA'
    left join BWT_CARD_BRAND bcb on bcb.index_field = T.card_brand and bcb.institution_number = t.institution_number and bcb.language = 'USA'
    left join BWT_CARD_ORGANIZATION bco on bco.index_field = SUBSTR(t.AUTHORIZED_BY,0,3) and bco.institution_number = t.institution_number and bco.language = 'USA'
    left join bwt_currency bc on bc.iso_code = t.tran_currency and bc.institution_number = t.institution_number and bc.language = 'USA'
    left join bwt_currency bc1 on bc1.iso_code = t.settlement_currency and bc1.institution_number = t.institution_number and bc1.language = 'USA'
    left join cis_device_link cdl on cdl.terminal_id = T.terminal_id and cdl.institution_number = t.institution_number
    left join int_addendum_charges charge on charge.institution_number = t.institution_number AND charge.charge_transaction_slip = t.transaction_slip
	left JOIN bwt_charge_type bct ON bct.index_field = charge.charge_type and bct.institution_number = charge.institution_number and bct.language = 'USA'
	left JOIN bwt_capture_method bcm ON bcm.index_field = T.capture_method and bcm.institution_number = t.institution_number and bcm.language = 'USA'
	left JOIN CIS_CLIENT_DETAILS ccd ON ccd.institution_number = t.institution_number and ccd.client_number = t.client_number
	left join cas_client_account cca on cca.institution_number = t.institution_number and cca.client_number = t.client_number and cca.acct_number = t.acct_number and cca.acct_currency = t.acct_currency
where T.transaction_slip in (
	SELECT tran.transaction_slip
		FROM INT_TRANSACTIONS tran,                     -- Individual slips, cardholder transactions (option 1: source summary)
   			 INT_TRANSACTIONS summ                       -- Source summaries, grouped cardholder transactions by charge group posted to merchant accounts
		WHERE summ.INSTITUTION_NUMBER = &institution_number
		    AND summ.CLIENT_NUMBER = &client_number         -- To insert hierarchical
		    AND summ.ACCT_CURRENCY = &currency
		    AND summ.TRANSACTION_DATE between &start_date and &end_date
		    AND tran.SOURCE_SETTLEMENT = summ.TRANSACTION_SLIP
		    AND tran.INSTITUTION_NUMBER = summ.INSTITUTION_NUMBER
		    AND tran.TRANSACTION_CLASS = '002'
		    AND tran.TRANSACTION_CATEGORY = '001'
		    AND tran.TRANSACTION_STATUS in ('004' ,'009','007')
		    AND tran.TRANSACTION_TYPE != '006'
		    AND tran.REVERSAL_FLAG = '000'              -- Not reversed
		    AND summ.TRANSACTION_CLASS = '012'
		    AND summ.TRANSACTION_CATEGORY = '016'
	union
	SELECT fee.transaction_slip
		FROM INT_TRANSACTIONS fee
		WHERE fee.INSTITUTION_NUMBER = &institution_number
			AND fee.CLIENT_NUMBER = &client_number
			AND fee.ACCT_CURRENCY =  &currency
			AND fee.TRANSACTION_DATE between &start_date AND &end_date
			AND fee.REVERSAL_FLAG = '000'
		 	-- AND fee.TRANSACTION_STATUS in ('004' ,'009','007')
		 	AND fee.TRANSACTION_CLASS in ('002')
		 	AND fee.TRANSACTION_CATEGORY in ('007')
	)
ORDER BY t.transaction_date, t.transaction_slip
 	;

 select f.* from CBR_CLIENT_FEES f
 where f.INSTITUTION_NUMBER = &institution_number;

 	-- Purchase
 	SELECT T.*
		FROM INT_TRANSACTIONS T
		inner join int_transactions t2 on t2.institution_number = t.institution_number and t2.summary_settlement = t.summary_settlement and t2.file_number = t.file_number and t2.transaction_class in ('012')
		WHERE T.institution_number = &institution_number AND T2.CLIENT_NUMBER = &client_number and  T.ACCT_CURRENCY =  &currency and T.record_date between &start_date and &end_date
       and T.transaction_status in ('004' ,'009','007') and  T.transaction_class in ('002') and T.transaction_category in ('007', '001', '016', '027', '003', '002') and t.transaction_type in ('005');

	-- fees
	SELECT T.*
		FROM INT_TRANSACTIONS T
		WHERE T.institution_number = &institution_number AND T.CLIENT_NUMBER = &client_number and  T.ACCT_CURRENCY =  &currency and T.record_date between &start_date and &end_date
	 	and T.transaction_status in ('004' ,'009','007') and T.transaction_class in ('002') and T.transaction_category in ('007', '001', '016', '027', '003', '002');


    --- settled transactions amount
    SELECT tran.INSTITUTION_NUMBER, summ.CLIENT_NUMBER, summ.ACCT_CURRENCY, SUBSTR(tran.AUTHORIZED_BY,0,3) CARD_ORGANIZATION, SUM(tran.ACCOUNT_AMOUNT_GR) GROSS_AMOUNT, SUM(tran.ACCOUNT_AMOUNT_NET) NET_AMOUNT, SUM(tran.ACCOUNT_AMOUNT_CHG) CHARGE_AMOUNT, ROUND(AVG(tran.ACCOUNT_AMOUNT_GR), 2) AVERAGE_TICKET, COUNT(tran.TRANSACTION_SLIP) TRAN_COUNT
FROM INT_TRANSACTIONS tran,                     -- Individual slips, cardholder transactions
    INT_TRANSACTIONS summ                       -- Source summaries, grouped cardholder transactions by charge group posted to merchant accounts
WHERE tran.INSTITUTION_NUMBER = summ.INSTITUTION_NUMBER
    AND tran.SOURCE_SETTLEMENT = summ.TRANSACTION_SLIP
    AND tran.TRANSACTION_CLASS = '002'
    AND tran.TRANSACTION_CATEGORY = '001'
    AND tran.TRANSACTION_TYPE != '006'
    AND tran.REVERSAL_FLAG = '000'              -- Not reversed
    AND summ.TRANSACTION_CLASS = '012'
    AND summ.TRANSACTION_CATEGORY = '016'
    AND summ.INSTITUTION_NUMBER = &institution_number
    AND summ.CLIENT_NUMBER = &client_number         -- To insert hierarchical
    AND tran.TRANSACTION_DATE between &start_date and &end_date      -- Transaction date to be filtered according to selected date
GROUP BY tran.INSTITUTION_NUMBER, summ.CLIENT_NUMBER, summ.ACCT_CURRENCY, tran.AUTHORIZED_BY;

	--- settled transactions amount
    SELECT tran.INSTITUTION_NUMBER, summ.CLIENT_NUMBER, summ.ACCT_CURRENCY, SUBSTR(tran.AUTHORIZED_BY,0,3) CARD_ORGANIZATION, tran.card_number, tran.acct_number, tran.ACCOUNT_AMOUNT_GR GROSS_AMOUNT, tran.ACCOUNT_AMOUNT_NET NET_AMOUNT, tran.ACCOUNT_AMOUNT_CHG CHARGE_AMOUNT
FROM INT_TRANSACTIONS tran,                     -- Individual slips, cardholder transactions
    INT_TRANSACTIONS summ                       -- Source summaries, grouped cardholder transactions by charge group posted to merchant accounts
WHERE tran.INSTITUTION_NUMBER = summ.INSTITUTION_NUMBER
    AND tran.SOURCE_SETTLEMENT = summ.TRANSACTION_SLIP
    AND tran.TRANSACTION_CLASS = '002'      -- clearing
    AND tran.TRANSACTION_CATEGORY = '001'   -- presentment
    AND tran.TRANSACTION_TYPE != '006'
    AND tran.REVERSAL_FLAG = '000'              -- Not reversed
    AND summ.TRANSACTION_CLASS = '012'          -- source summary control
    AND summ.TRANSACTION_CATEGORY = '016'       -- source control information
    AND summ.INSTITUTION_NUMBER = &institution_number
    AND summ.CLIENT_NUMBER = &client_number         -- To insert hierarchical
    AND tran.TRANSACTION_DATE between &start_date and &end_date      -- Transaction date to be filtered according to selected date

order BY tran.INSTITUTION_NUMBER, summ.CLIENT_NUMBER, summ.ACCT_CURRENCY, tran.AUTHORIZED_BY;

  -- transaction_status = 004 Paid, 009: Cleared, 007: Processed.
  -- transaction_class 002: clearing, 012: source summary control.
  -- transaction_category 001: Presentments, 002 - chargebacks, 003 - representments, 007 - charges & fees , 016: Source control information, 008: Payments, 027: authorization

  ---  Option 2: all charges & fees
select *
from INT_ADDENDUM_CHARGES a
where
a.institution_number = &institution_number and a.client_number = &client_number and tran_currency = &currency
 and a.record_date between &start_date and &end_date
-- and charge_transaction_slip in ('21000060936')
order by charge_transaction_slip;

-------
select * from int_transactions where transaction_slip in (21000061066,
21000060874,
21000060920

);


  -- by summary settlement
  select
  	-- t.record_date,
  	t.file_number, t2.file_number as t2file,
  	-- t.transaction_category, t2.transaction_category as t2category,
  	-- t.transaction_class, t2.transaction_class as t2class,
  	-- t.transaction_status, t2.transaction_status as t2status,
   t.transaction_slip, t2.transaction_slip as t2slip,
  	t.number_original_slip, t2.number_original_slip as t2original_slip,
  	t.inward_fee_number, t.outward_fee_number, t.file_number_outward,
  	-- t.bank_account_number, t2.bank_account_number,
  	-- t.original_ref_number, t2.original_ref_number,
  	t.tran_amount_out_chg, -- t2.tran_amount_out_chg,
  	t.local_amount_inw_net, t.local_amount_inw_chg, t.local_amount_out_net, t.local_amount_out_chg,

  	t.retrieval_reference,
  	t2.acct_number,
  	-- t.file_number,
  	t.card_number,
  	t.account_amount_gr * decode(t."DR_CR_INDICATOR", '001',-1,1) as gross_amount,
  	t.settlement_amount_net * decode(t."DR_CR_INDICATOR", '001',-1,1) as settlement_amount
  from int_transactions t -- class 02, clearing -> to get card number etc
  inner join int_transactions t2 on t2.institution_number = t.institution_number and t2.summary_settlement = t.summary_settlement and t2.file_number = t.file_number and t2.transaction_class in ('012')   -- to get payment
  where t.institution_number = &institution_number and t2.client_number = &client_number and T.ACCT_CURRENCY = &currency and T.record_date between &start_date and &end_date
  and t.transaction_class in ('002')
  and t.transaction_category in ('001', '002', '003', '016')
  -- and t.transaction_slip in ('21000060866', '21000060876')

  order by T.record_date, t.transaction_slip, t.transaction_category, t.transaction_class asc;

  -----

  select transaction_slip, summary_settlement, account_amount_gr, file_number
  from INT_TRANSACTIONS t where
  t.institution_number = &institution_number and t.ACCT_CURRENCY = &currency and t.record_date between &start_date and &end_date
  and
  -- t.transaction_category in ('007')
  transaction_slip in ('21000000409');

  -- option 2
, SERVICE_CONTRACT_ID     select T.transaction_slip,
     T.VALUE_DATE as transaction_date,
     (cdl.location || ' - ' || cdl.pos_region || ' - ' || cdl.pos_city) as location,
     (btc.index_field || ' - ' || btc.transaction_category) as transaction_category,
      (btc1.index_field || ' - ' || btc1.transaction_class) as transaction_class,
        (btt.index_field || ' - ' || btt.transaction_type) as transaction_type,
        (bcb.index_field || ' - ' || bcb.card_brand) as card_brand,
        t.account_amount_net * decode(t."DR_CR_INDICATOR", '001',1,-1) as gross_amount,
        t.settlement_amount_net * decode(t."DR_CR_INDICATOR", '001',1,-1) as net_amount,
        (bc.iso_code || ' - ' || bc.name) as transaction_currency,
        (bc1.iso_code || ' - ' || bc1.name) as settlement_currency,
        t.rate_fx_tran_settl as rate_fx,
             (bts.index_field || ' - ' || bts.transaction_status) as transaction_status,
             t.card_number, t.merchant_number, t.acquirer_reference, t.merchant_number as mid, T.summary_settlement as summary_number,
     T.*
     from INT_TRANSACTIONS T
     left join bwt_transaction_status bts on bts.index_field = T.transaction_status and bts.institution_number = t.institution_number and bts.language = 'USA'
     left join bwt_transaction_category btc on btc.index_field = T.transaction_category and btc.institution_number = t.institution_number and btc.language = 'USA'
     left join bwt_transaction_class btc1 on btc1.index_field = T.transaction_class and btc1.institution_number = t.institution_number and btc1.language = 'USA'
     left join BWT_SIGN_OPERATORS bso on bso.index_field = T.dr_cr_indicator and bso.institution_number = t.institution_number and bso.language = 'USA'
     left join BWT_TRANSACTION_TYPE btt on btt.index_field = T.transaction_type and btt.institution_number = t.institution_number and btt.language = 'USA'
     left join BWT_CARD_BRAND bcb on bcb.index_field = T.card_brand and bcb.institution_number = t.institution_number and bcb.language = 'USA'
     inner join bwt_currency bc on bc.iso_code = t.tran_currency and bc.institution_number = t.institution_number and bc.language = 'USA'
     inner join bwt_currency bc1 on bc1.iso_code = t.settlement_currency and bc1.institution_number = t.institution_number and bc1.language = 'USA'
     left join cis_device_link cdl on cdl.terminal_id = T.terminal_id and cdl.institution_number = t.institution_number
     where T.INSTITUTION_NUMBER = &institution_number AND (UPPER(T.CLIENT_NUMBER) = &client_number) and T.record_date between &start_date and &end_date and T.ACCT_CURRENCY =  &currency
     and T.file_number in (select distinct file_number from INT_FILE_LOG_DETAILS
     where INSTITUTION_NUMBER = &institution_number
     and process_name = 13 -- 13: merchant payment
     and record_date = T.record_date)
and T.transaction_category = '008' -- 008: Payments
and T.transaction_class = '012'
and T.acct_number in (select DISTINCT acct_number from cas_client_account where INSTITUTION_NUMBER = &institution_number and client_number = &client_number
and account_type_id = '012' -- 012: merchant payment account type
and  T.ACCT_CURRENCY =  &currency)
order by T.record_date asc, T.transaction_slip asc;

------------
      select * from int_transactions where INSTITUTION_NUMBER = &institution_number and client_number = &client_number and transaction_slip = '21100063166' or retrieval_reference = '21100063166' order by record_date asc;

     --- Payment Summary
select
sum(t.account_amount_net * decode(t."DR_CR_INDICATOR", '001',1,-1)) as gross_amount,
sum(t.settlement_amount_net * decode(t."DR_CR_INDICATOR", '001',1,-1)) as net_amount,
t.value_date
from int_transactions t
where t.institution_number = &institution_number and client_number = &client_number and T.record_date between &start_date and &end_date  and T.ACCT_CURRENCY =  &currency
 -- and t.transaction_status in ('004') -- only display processed transaction 007 - Processed - 004 - Paid, 009 - Cleared;
 and t.transaction_class in ('002') -- 002: Clearing transactions
 and t.transaction_category in ('008') -- 008: Payments
 group by t.value_date
order by t.value_date asc;

    --- addendum charges
	SELECT
    fee.transaction_slip fee_transaction_slip,      -- Fee transaction slip
    charge.transaction_slip,                        -- Presentment class 002 slip number for linking
     (bct.index_field || ' - ' || bct.charge_type) as charge_type,                          -- Charge type, multiple charge types possible for transaction slip
    charge.billing_acct_number,                     -- Merchant account where fee will be posted, same value as fee.acct_number
    charge.client_number,                           -- Merchant client number
    charge.acct_currency,                           -- Charge currency
    (DECODE(fee.DR_CR_INDICATOR, '001', 1, -1) * charge.account_amount ) charge_account_amount,
    fee.reversal_flag, charge.*
FROM int_addendum_charges charge
INNER JOIN int_transactions fee
    ON charge.institution_number = fee.institution_number
INNER JOIN bwt_charge_type bct
	ON bct.index_field = charge.charge_type and bct.institution_number = charge.institution_number and bct.language = 'USA'
    AND charge.charge_transaction_slip = fee.transaction_slip
    AND fee.transaction_category = '007'
    AND fee.transaction_class = '002'
WHERE charge.institution_number = &institution_number and charge.client_number = &client_number and charge.acct_currency = &currency and charge.record_date between &start_date and &end_date;

--- disputes transactions
SELECT
    tran.client_number,
    tran.record_date,
    tran.transaction_date,
    tran.value_date,
    tran.transaction_slip,
    tran.acquirer_reference,
    tran.retrieval_reference,
    tran.number_original_slip,
    tran.transaction_status,
    tran.transaction_category,
    tran.card_number,
    tran.acct_currency,
    tran.acct_number,
    tran.retrieval_reference,
    tran.tran_currency,
    SUBSTR(tran.AUTHORIZED_BY,0,3) CARD_ORGANIZATION,
    (DECODE(tran.dr_cr_indicator, '001', 1, -1) * tran.tran_amount_gr) tran_amount_gr,
    (DECODE(tran.dr_cr_indicator, '001', 1, -1) * tran.account_amount_gr) acct_amount_gr,
    tran.reversal_flag,
    dms.case_number
FROM int_transactions tran
LEFT JOIN int_sundry_history dms                          -- DMS not always available?
    ON tran.institution_number = dms.institution_number
    AND tran.number_original_slip = dms.number_original_slip
    AND tran.acquirer_reference = dms.acquirer_reference
    AND dms.rule_action = '110'                                 -- Retrieve single case record
WHERE tran.transaction_category IN ('002', '003')               -- Chargebacks + Re-presentments
    AND tran.transaction_class = '002'                          -- Clearing transactions
    AND tran.institution_number = &institution_number
    AND tran.transaction_status <> '003'                      -- Not ERROR
    AND tran.institution_number = &institution_number and tran.client_number = &client_number and tran.acct_currency = &currency and tran.record_date between &start_date and &end_date;

    --- Adhoc Fees (account/ service/ manual / misc adhoc fees)
    SELECT transaction_slip,
    record_date,
    transaction_date,
    transaction_status,
    client_number,
    acct_number,
    transaction_type,
    group_number,
    acct_currency,
    account_amount_gr,
    reversal_flag,
    retrieval_reference,
    transaction_source,
    inward_fee_number
FROM (
    SELECT
        tran.transaction_slip,
        tran.record_date,
        tran.transaction_date,
        tran.transaction_status,
        tran.client_number,
        tran.acct_number,
        tran.transaction_type,
        tran.group_number,
        tran.acct_currency,
        (DECODE(tran.dr_cr_indicator, '001', 1, -1) * tran.account_amount_gr) account_amount_gr,
        tran.reversal_flag,
        tran.retrieval_reference,
        tran.transaction_source,
        tran.inward_fee_number,
        COALESCE(SVC.RECORD_ID_NUMBER, CAS.RECORD_ID_NUMBER, DEV.RECORD_ID_NUMBER, CBR.RECORD_ID_NUMBER) RECORD_ID_NUMBER
    FROM int_transactions tran
    LEFT JOIN CBR_CLIENT_FEES CBR
        ON tran.INSTITUTION_NUMBER = CBR.INSTITUTION_NUMBER
        AND tran.INWARD_FEE_NUMBER = CBR.RECORD_ID_NUMBER
    LEFT JOIN CAS_CLIENT_FEES CAS
        ON tran.INSTITUTION_NUMBER = cas.INSTITUTION_NUMBER
        AND tran.INWARD_FEE_NUMBER = cas.RECORD_ID_NUMBER
    LEFT JOIN SVC_CLIENT_FEES SVC
        ON tran.INSTITUTION_NUMBER = SVC.INSTITUTION_NUMBER
        AND tran.INWARD_FEE_NUMBER = SVC.RECORD_ID_NUMBER
    LEFT JOIN CIS_DEVICE_FEES DEV
        ON tran.INSTITUTION_NUMBER = DEV.INSTITUTION_NUMBER
        AND tran.INWARD_FEE_NUMBER = DEV.RECORD_ID_NUMBER
    WHERE tran.transaction_category = '007'
        AND tran.transaction_class = '002'
        AND tran.institution_number = &institution_number and tran.client_number = &client_number and tran.record_date between &start_date and &end_date and tran.acct_currency = &currency
)
WHERE record_id_number = inward_fee_number  ;
 UNION ALL
 SELECT adhoc.transaction_slip,
    adhoc.record_date,
    adhoc.transaction_date,
    adhoc.transaction_status,
    adhoc.client_number,
    adhoc.acct_number,
    adhoc.transaction_type,
    adhoc.group_number,
    adhoc.acct_currency,
    (DECODE(adhoc.dr_cr_indicator, '001', 1, -1) * adhoc.account_amount_gr) account_amount_gr,
    adhoc.reversal_flag,
    adhoc.retrieval_reference,
    adhoc.transaction_source,
    adhoc.inward_fee_number
FROM INT_TRANSACTIONS adhoc
WHERE adhoc.TRANSACTION_CATEGORY = '007'
    AND adhoc.TRANSACTION_CLASS = '002'
     AND adhoc.TRANSACTION_SOURCE = '006';
    AND adhoc.INSTITUTION_NUMBER = &institution_number and adhoc.client_number = &client_number and adhoc.record_date between &start_date and &end_date and adhoc.acct_currency = &currency;

	--- adjust transactions having null original transaction slip, created either by a process or manually for correction of merchant's balance.
	SELECT adj.client_number,
    adj.acct_number,
    adj.transaction_slip,
    adj.number_original_slip,
    adj.transaction_category,
    adj.transaction_class,
    adj.transaction_type,
    adj.transaction_source,
    adj.acct_currency,
    (DECODE(adj.dr_cr_indicator, '001', 1, -1) * adj.account_amount_gr) account_amount_gr,
    adj.reversal_flag
FROM int_transactions adj
WHERE
    adj.transaction_category = '050'    -- Adjustments
    AND adj.transaction_class = '002'       -- Class 002
    AND adj.transaction_source = '006'       -- Source 006
    AND adj.institution_number = &institution_number and adj.client_number = &client_number and adj.record_date between &start_date and &end_date and adj.acct_currency = &currency
order by record_date asc, transaction_slip;

	-- GL transactions history
   SELECT  (bts.index_field || ' - ' || bts.transaction_status) as transaction_status,
     (btc.index_field || ' - ' || btc.transaction_category) as transaction_category,
      (btc1.index_field || ' - ' || btc1.transaction_class) as transaction_class,
        (btt.index_field || ' - ' || btt.transaction_type) as transaction_type,
           (bso.index_field || bso.sign) as BWT_SIGN_OPERATORS,
     T.* FROM INT_GL_TRANSACTIONS T
     left join bwt_transaction_status bts on bts.index_field = T.transaction_status and bts.institution_number = t.institution_number and bts.language = 'USA'
     left join bwt_transaction_category btc on btc.index_field = T.transaction_category and btc.institution_number = t.institution_number and btc.language = 'USA'
     left join bwt_transaction_class btc1 on btc1.index_field = T.transaction_class and btc1.institution_number = t.institution_number and btc1.language = 'USA'
      left join BWT_SIGN_OPERATORS bso on bso.index_field = T.dr_cr_indicator and bso.institution_number = t.institution_number and bso.language = 'USA'
       left join BWT_TRANSACTION_TYPE btt on btt.index_field = T.transaction_type and btt.institution_number = t.institution_number and btt.language = 'USA'
     WHERE (T.institution_number = &institution_number ) AND (UPPER(T.CLIENT_NUMBER) = &client_number )
     -- and (T.file_number in (2587) or T.transaction_slip in (21000000425))
     -- and (T.acct_number in (00000181001))
     order by record_date asc;



-- deposits
select * from CAS_PAYMENT_STATUS_HISTORY where (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) in (&client_number));

-- cis address
select * from CIS_Address where (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) in (&client_number));

-- Final SQL: SELECT * FROM SVC_CLIENT_SERVICE WHERE (institution_number like  &institution_number) AND (UPPER(CLIENT_NUMBER) LIKE &client_number);

select * from bwt_transaction_status where index_field in ('005', '202');

-- get bw bank accounts and its balance !!
select
&client_number as client_number, cbb.acct_number, (bc.iso_code || ' - ' || bc.name) as acct_currency,
(ps.index_field || ' - ' || ps.processing_status) as processing_status, cbb.begin_balance, cbb.current_balance,
cbb.* from CAS_CYCLE_BOOK_BALANCE cbb
    inner join bwt_currency bc on bc.iso_code = cbb.acct_currency and bc.institution_number = cbb.institution_number and bc.language = 'USA'
     inner join bwt_processing_status ps on ps.index_field = cbb.processing_status and ps.institution_number = cbb.institution_number and ps.language = 'USA'
where (cbb.institution_number = &institution_number ) AND (UPPER(cbb.ACCT_NUMBER) LIKE (&client_number || '%'))
and cbb.processing_status in ('004') -- 004: current, 015: closed
order by cbb.acct_number, cbb.date_cycle_start asc;

select * from CAS_PAYMENT_STATUS_HISTORY where institution_number like &institution_number;

select * from CIS_DEVICE_LINK where institution_number like &institution_number;

select * from cht_transaction_category where index_field in ('001', '002', '003', '007', '008', '027', '016');
select * from cht_transaction_status where language = 'USA' and institution_number = &institution_number order by index_field;
select * from BWT_CHARGE_TYPE where language = 'USA' and institution_number = &institution_number order by index_field;

select transaction_slip, count(*) from int_transactions where institution_number like &institution_number group by transaction_slip;

select * from CAS_PAYMENT_STATUS_HISTORY where institution_number like &institution_number and client_number = &client_number and record_date between &start_date and &end_date and acct_currency = &currency order by record_date asc;

select * from CBR_SERVICE_CONTRACT where institution_number like &institution_number;

select * from cht_services where institution_number like &institution_number;
select * from CBR_SERVICE_DEFINITION where institution_number like &institution_number;

select distinct(file_number) from INT_FILE_LOG_DETAILS where institution_number like &institution_number and process_name in (86,455);

select * from INT_SUNDRY_TRANSACTIONS where institution_number like &institution_number;

select count(*) from int_transactions where transaction_slip is null;

-- check account
select account_amount_net *decode( dr_cr_indicator, '001',1,-1), a.acct_number, a.last_settlement_date , t.value_date
from int_transactions t , cas_client_account a
where t.institution_number = &institution_number --enter institution number of merchant/client
and t.transaction_status ='002'
and t.client_number = &client_number --enter client number you are checking
and a.institution_number = t.institution_number
and a.acct_number = t.acct_number
and t.value_date > a.last_settlement_date
order by t.value_date;

---

SELECT  DISTINCT
        CHT.CONDITION_SET,
        CHT.INDEX_FIELD,
        (SELECT TARGET_CLASS FROM BW3.BWT_FEE_CATEGORY WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.FEE_CATEGORY) AS FEE_CATEGORY,
        (SELECT SWIFT_CODE FROM BW3.BWT_CURRENCY WHERE INSTITUTION_NUMBER = &institution_number AND ISO_CODE = CBR.FEE_CURRENCY) AS FEE_CURRENCY,
        LISTAGG((SELECT SERVICE_ID FROM BW3.BWT_SERVICES WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.SERVICE_ID), ',') WITHIN GROUP (ORDER BY CBR.SERVICE_ID) AS SERVICE_ID,
        (SELECT AREA_OF_ACTION FROM BW3.BWT_AREA_OF_ACTION WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.AREA_OF_EVENT) AS AREA_OF_EVENT,
        (SELECT TRANSACTION_TYPE FROM BW3.BWT_TRANSACTION_TYPE WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.TRANSACTION_TYPE) AS TRANSACTION_TYPE,
        (SELECT CAPTURE_METHOD FROM BW3.BWT_CAPTURE_METHOD WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.CAPTURE_METHOD) AS CAPTURE_METHOD,
        (SELECT SERVICE_TYPE FROM BW3.BWT_SERVICE_TYPE WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.SERVICE_TYPE) AS SERVICE_TYPE,
        (SELECT STATEMENT_NARRATIVE FROM BW3.BWT_TRANSACTION_TYPE WHERE INSTITUTION_NUMBER = &institution_number AND INDEX_FIELD = CBR.PL_IND_INWARD) AS PL_IND_INWARD
FROM    BW3.CBR_TRANSACTION_CHARGES CBR,
        BW3.CHT_ACCOUNT_CONDITION_SET CHT
WHERE   CBR.INSTITUTION_NUMBER = &institution_number
AND     CBR.INSTITUTION_NUMBER = CHT.INSTITUTION_NUMBER
AND     CBR.CLIENT_TARIFF = CHT.INDEX_FIELD
AND     CBR.SERVICE_CONTRACT_ID = '112'
-- AND     CHT.INDEX_FIELD = '011377'
GROUP BY CHT.INDEX_FIELD, CHT.CONDITION_SET,CBR.FEE_CURRENCY, CBR.SERVICE_ID, CBR.CAPTURE_METHOD, CBR.CHARGE_TYPE, CBR.SERVICE_TYPE, CBR.AREA_OF_EVENT, CBR.TRANSACTION_TYPE, CBR.PL_IND_INWARD, CBR.FEE_CATEGORY
ORDER BY 1,2,3,6,5,7;

----
select object_name, count(*)
from BW_AMC.AMC_USER_SYSTEM_ACTIONS
group by object_name order by count(*) desc;

----
select * from bwt_processing_status where institution_number = &institution_number;

select * from bwt_acct_status where institution_number = &institution_number;

select * from bwt_clearing_channel where institution_number = &institution_number and language = 'USA' order by index_field;

select * from bwt_sales_channel where institution_number = &institution_number and language = 'USA';

select * from bwt_account_type_id where institution_number = &institution_number and language = 'USA';

select * from cbr_channel_definition where institution_number = &institution_number and client_number = &client_number;     -- 037, 934, 018, 153

select * from cis_application_acct_type where institution_number = &institution_number and acct_currency = &currency;

select * from CIS_SETTLEMENT_INFORMATION where institution_number = &institution_number and client_number = &client_number and acct_currency = &currency;

select * from CIS_MANDATE_INFORMATION where institution_number = &institution_number;

select * from int_transactions where retrieval_reference in ('0000015702') and institution_number = &institution_number and client_number = &client_number and acct_currency = &currency;

select distinct(bank_account_number) from int_transactions where institution_number = &institution_number and client_number = &client_number;

select * from INT_SUNDRY_TRANSACTIONS where institution_number = &institution_number and client_number = &client_number;


SELECT TRANSACTION_SLIP, x.* FROM INT_TRANSACTIONS x WHERE x.INSTITUTION_NUMBER = &institution_number and x.client_number = &client_number
and x.TRANSACTION_CLASS in (2, 11)
and x.TRANSACTION_CATEGORY in (2, 24)
and x.ACQUIRER_REFERENCE is not null
and (SELECT count(1) FROM INT_TRANSACTIONS t WHERE t.TRANSACTION_SLIP = SUBSTR(x.ACQUIRER_REFERENCE,12,11)
AND t.INSTITUTION_NUMBER||'' = &institution_number
AND t.TRANSACTION_CLASS||'' = '002' AND t.TRANSACTION_CATEGORY||'' = '001')  > 0;

select *
from cbr_transaction_charges a
where a.institution_number = &institution_number
and a.client_number = &client_number or a.client_number = 0; --if the lookup is by client number than the tariff should be 000
--and a.client_tariff = '510' --to be used in case there is NO setup by client number
-- and a.service_contract_id = '497'
-- and a.service_id = '101'
-- and a.transaction_type ='007' --supports the value of 999
-- and a.tran_currency = &currency  --supports the value of 999
-- and a.capture_method = '100' --supports the value of 999
-- and a.area_of_event ='002' --supports the value of 999
-- and a.service_type = '001' --supports the value of 999
-- and a.charge_Type = '002'; --commission
;

SELECT  IT.TRANSACTION_SLIP, IT.RECORD_DATE
FROM    INT_TRANSACTIONS IT,
        SVC_CLIENT_CARDS SCC
WHERE   IT.INSTITUTION_NUMBER = &institution_number AND
        IT.TRANSACTION_STATUS = '002' AND
        IT.TRANSACTION_CATEGORY = '001' AND
        SCC.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER AND
        SCC.CARD_NUMBER = IT.CARD_NUMBER AND
        SCC.SERVICE_CONTRACT_ID IN (
            SELECT  SERVICE_CONTRACT_ID
            FROM    CBR_CONTRACT_ACCT_TYPES
            WHERE   ACCOUNT_TYPE_ID = '021' AND
                    INSTITUTION_NUMBER = SCC.INSTITUTION_NUMBER
        ) AND
        IT.RECORD_DATE BETWEEN (
            SELECT  CCBB.DATE_CYCLE_START   /*MAX(CCBB.DATE_CYCLE_START) /* Config: EPPValidCycles is true */*/
            FROM    CAS_CYCLE_BOOK_BALANCE CCBB
            WHERE   CCBB.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER AND
                    CCBB.ACCT_NUMBER = IT.ACCT_NUMBER AND
                    CCBB.PROCESSING_STATUS = BWTPad('BWT_PROCESSING_STATUS', '004') /* Config: EPPValidCycles is empty or false */
                    /*CCBB.PROCESSING_STATUS = BWTPad('BWT_PROCESSING_STATUS', '015') /* Config: EPPValidCycles is true */*/
        ) AND (
            SELECT  DATE_CYCLE_END
            FROM    CAS_CYCLE_BOOK_BALANCE CCBB
            WHERE   CCBB.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER AND
                    CCBB.ACCT_NUMBER = IT.ACCT_NUMBER AND
                    CCBB.PROCESSING_STATUS = BWTPad('BWT_PROCESSING_STATUS', '004')
        ) AND
        IT.TRANSACTION_SLIP NOT IN (
            SELECT  IST.TRANSACTION_SLIP
            FROM    BW3.INT_SUNDRY_TRANSACTIONS IST
            WHERE   IST.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER AND
                    IST.TRANSACTION_SLIP = IT.TRANSACTION_SLIP
        ); /* AND
        IT.TRANSACTION_SLIP NOT IN (
            SELECT  IST.TRANSACTION_SLIP
            FROM    BW_AMC.INT_SUNDRY_TRANSACTIONS IST
            WHERE   IST.INSTITUTION_NUMBER = IT.INSTITUTION_NUMBER AND
                    IST.TRANSACTION_SLIP = IT.TRANSACTION_SLIP
        ) */ ;


SELECT GROUP_NUMBER,
       CCL.CLIENT_NUMBER,
       SERVICE_CONTRACT_ID,
       COMPANY_NAME,
       CLIENT_TARIFF,
       EFFECTIVE_DATE,
       CLIENT_LEVEL,
       CCL.CLIENT_BRANCH
FROM CIS_CLIENT_LINKS CCL,
     CIS_CLIENT_DETAILS CCD
WHERE CCL.INSTITUTION_NUMBER = &institution_number
AND CCL.CLIENT_NUMBER = &client_number
AND CONTRACT_STATUS = '001'
AND CCL.CLIENT_LEVEL = '001'
AND EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE)
                      FROM CIS_CLIENT_LINKS
                      WHERE INSTITUTION_NUMBER = CCL.INSTITUTION_NUMBER
                      AND CLIENT_NUMBER = CCL.CLIENT_NUMBER
                      AND SERVICE_CONTRACT_ID = CCL.SERVICE_CONTRACT_ID
                      AND CLIENT_LEVEL = CCL.CLIENT_LEVEL
                      AND GROUP_NUMBER = CCL.GROUP_NUMBER
                      AND EFFECTIVE_DATE <= &end_date)
AND CCD.INSTITUTION_NUMBER = CCL.INSTITUTION_NUMBER
AND CCD.CLIENT_NUMBER = CCL.CLIENT_NUMBER
ORDER BY GROUP_NUMBER, CLIENT_LEVEL DESC;

