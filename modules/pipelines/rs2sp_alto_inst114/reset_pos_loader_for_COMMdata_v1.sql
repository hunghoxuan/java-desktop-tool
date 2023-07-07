update cos_bpr_data set transaction_status = '999', network_id = null, batch_id = null
where institution_number = '00000114' and substr(retrieval_reference,1,5) = 'ACOMM'
and length (response_code) > 1;   -- with valid response_codes only

--workaround RSSP-50221
--update cos_bpr_data set settlement_currency = '360'
--where institution_number = '00000114' and settlement_currency <> '360' and substr(retrieval_reference,1,5) = 'ACOMM';
--

update cis_device_link set date_settlement = '', time_settlement = ''
where institution_number = '00000114' and (terminal_id like '98888915%' --or terminal_id like '28888998%'
);
commit;
