update cos_bpr_data set transaction_status = '999', network_id = null, batch_id = null where institution_number = '00000114' and substr(retrieval_reference,1,6) = 'ALTOPM';
update cis_device_link set date_settlement = '', time_settlement = '' where institution_number = '00000114' and (terminal_id like '988889%' or terminal_id like '20210528%');
commit;
