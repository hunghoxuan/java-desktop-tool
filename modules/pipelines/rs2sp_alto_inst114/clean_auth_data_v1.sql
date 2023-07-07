delete bw3.cos_bpr_log where transaction_link_id in (select transaction_link_id from bw3.cos_bpr_data where institution_number = '00000114' and substr(retrieval_reference,1,6) = 'ALTOPM');
delete cos_bpr_data t where institution_number = '00000114' and substr(retrieval_reference,1,6) = 'ALTOPM';
commit;
