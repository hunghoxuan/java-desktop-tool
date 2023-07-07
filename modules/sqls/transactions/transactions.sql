define institution_number = '00002001';
define client_number = '00000004';
define currency = '978';
define start_date = '20220714';
define end_date = '20220722';
-- define posting_date = '20230322';

-- TRANSACTIONS
SELECT
	summ.client_number,
	-- t.AUTHORIZED_BY,
	-- summ.AUTHORIZED_BY,
	 ccd.our_reference as merchant_id,
	  (bcb.index_field || ' - ' || bcb.card_brand) as card_brand,
    (bco.index_field || ' - ' || bco.card_organization) as card_organization, -- digits 123 from AUTHORIZED_BY
    T.transaction_date as transaction_date,

	T.transaction_slip as transaction_slip,
	summ.transaction_slip as summary_transaction_slip,
	  t.account_amount_gr * decode(t."TRANSACTION_TYPE", '005',1,-1) as gross_amount,
    (bc.iso_code || ' - ' || bc.name) as transaction_currency,
      charges.transaction_amount as charge_amount,
       t.account_amount_gr * decode(t."TRANSACTION_TYPE", '005',1,-1) - charges.transaction_amount as net_amount,
    (bc1.iso_code || ' - ' || bc1.name) as settlement_currency,
	bw3_mask_card_no.DISGUISE_CARD_NO( t.card_number, NULL, NULL, NULL ) card_number, -- disguide card number

  	(cdl.location || ' - ' || cdl.pos_region || ' - ' || cdl.pos_city) as location,
   	(btc.index_field || ' - ' || btc.transaction_category) as transaction_category,
    (btc1.index_field || ' - ' || btc1.transaction_class) as transaction_class,
    (btt.index_field || ' - ' || btt.transaction_type) as transaction_type,

      (aoa.index_field || ' - ' || aoa.area_of_action) as area_of_action, -- digits 456 from AUTHORIZED_BY
      (bst.index_field || ' - ' || bst.service_type) as service_type, -- digits 789 from AUTHORIZED_BY
      (bs.index_field || ' - ' || bs.service_id) as service_id, -- digits 101112 from AUTHORIZED_BY

    t.rate_fx_tran_settl as rate_fx,
    (bts.index_field || ' - ' || bts.transaction_status) as transaction_status,
    (bct.index_field || ' - ' || bct.charge_type) as charge_type,
    (bcm.index_field || ' - ' || bcm.capture_method) as sale_channel,
    summ.retrieval_reference,
    t.acquirer_reference,
    t.terminal_id,
    T.summary_settlement as summary_number,
    t.rate_fx_tran_settl as exchange_rate,
	T.*
FROM INT_TRANSACTIONS T
    	left join INT_TRANSACTIONS summ on summ.INSTITUTION_NUMBER = T.INSTITUTION_NUMBER
		left join int_addendum_charges charges on charges.transaction_slip = T.transaction_slip and charges.institution_number = T.institution_number
 		left join bwt_transaction_status bts on bts.index_field = T.transaction_status and bts.institution_number = t.institution_number and bts.language = 'USA'
 	left join bwt_transaction_category btc on btc.index_field = T.transaction_category and btc.institution_number = t.institution_number and btc.language = 'USA'
  	left join bwt_transaction_class btc1 on btc1.index_field = T.transaction_class and btc1.institution_number = t.institution_number and btc1.language = 'USA'
   	left join BWT_SIGN_OPERATORS bso on bso.index_field = T.dr_cr_indicator and bso.institution_number = t.institution_number and bso.language = 'USA'
    left join BWT_TRANSACTION_TYPE btt on btt.index_field = T.transaction_type and btt.institution_number = t.institution_number and btt.language = 'USA'
    left join BWT_CARD_BRAND bcb on bcb.index_field = T.card_brand and bcb.institution_number = t.institution_number and bcb.language = 'USA'
    left join BWT_CARD_ORGANIZATION bco on bco.index_field = SUBSTR(t.AUTHORIZED_BY,0,3) and bco.institution_number = t.institution_number and bco.language = 'USA'
    left join cht_area_of_action  aoa on aoa.index_field = SUBSTR(t.AUTHORIZED_BY,3,3) and aoa.institution_number = t.institution_number and aoa.language = 'USA'
     left join bwt_service_type  bst on bst.index_field = SUBSTR(t.AUTHORIZED_BY,6,3) and bst.institution_number = t.institution_number and bst.language = 'USA'
      left join bwt_services  bs on bs.index_field = SUBSTR(t.AUTHORIZED_BY,9,3) and bs.institution_number = t.institution_number and bs.language = 'USA'
    left join bwt_currency bc on bc.iso_code = t.tran_currency and bc.institution_number = t.institution_number and bc.language = 'USA'
    left join bwt_currency bc1 on bc1.iso_code = t.settlement_currency and bc1.institution_number = t.institution_number and bc1.language = 'USA'
    left join cis_device_link cdl on cdl.terminal_id = T.terminal_id and cdl.institution_number = t.institution_number
    left join int_addendum_charges charge on charge.institution_number = t.institution_number AND charge.charge_transaction_slip = t.transaction_slip
	left JOIN bwt_charge_type bct ON bct.index_field = charge.charge_type and bct.institution_number = charge.institution_number and bct.language = 'USA'
	left JOIN bwt_capture_method bcm ON bcm.index_field = T.capture_method and bcm.institution_number = t.institution_number and bcm.language = 'USA'
	left JOIN CIS_CLIENT_DETAILS ccd ON ccd.institution_number = summ.institution_number and ccd.client_number = summ.client_number
	left join cas_client_account cca on cca.institution_number = t.institution_number and cca.client_number = t.client_number and cca.acct_number = t.acct_number and cca.acct_currency = t.acct_currency
WHERE
	T.INSTITUTION_NUMBER = &institution_number
    AND summ.CLIENT_NUMBER = &client_number         -- To insert hierarchical
    AND summ.ACCT_CURRENCY = &currency
    AND summ.TRANSACTION_DATE between &start_date and &end_date
    AND summ.TRANSACTION_CLASS = '012'
    AND summ.TRANSACTION_CATEGORY = '016'
    AND T.SOURCE_SETTLEMENT = summ.TRANSACTION_SLIP
    AND T.CLIENT_NUMBER <> &client_number
    AND T.TRANSACTION_CLASS = '002'
    AND T.TRANSACTION_CATEGORY = '001'
    AND T.TRANSACTION_STATUS in ('002', '004' ,'009','007')
    AND T.TRANSACTION_TYPE != '006'
    AND T.REVERSAL_FLAG = '000'              -- Not reversed

order by T.transaction_date desc, T.transaction_slip asc;

-- CHARGE BACKS
select cbk.*
from int_transactions cbk
where
	cbk.institution_number = &institution_number
	and cbk.transaction_category = '002'
	and cbk.transaction_class = '002'
	AND cbk.settlement_CURRENCY =  &currency
	AND cbk.SETTLEMENT_DATE between &start_date AND &end_date
order by cbk.transaction_date desc, cbk.transaction_slip asc;

--  FEE
SELECT *
FROM INT_TRANSACTIONS fee
WHERE
	fee.INSTITUTION_NUMBER = &institution_number
	AND fee.CLIENT_NUMBER = &client_number
	AND fee.ACCT_CURRENCY =  &currency
	AND fee.TRANSACTION_DATE between &start_date AND &end_date
	AND fee.REVERSAL_FLAG = '000'
	AND fee.TRANSACTION_STATUS in ('002', '004' ,'009','007')
	AND fee.TRANSACTION_CLASS in ('002')
	AND fee.TRANSACTION_CATEGORY in ('007')
order by fee.transaction_date desc, fee.transaction_slip asc;


select * from bwt_transaction_category where institution_number = &institution_number and language='USA' order by 1;
select * from bwt_transaction_class where institution_number = &institution_number and language='USA' order by 1;
select * from bwt_transaction_type where institution_number = &institution_number and language='USA' order by 1;





