/*
select bpr_log_id from bw3.cos_bpr_data where institution_number = '00000114' and length(bpr_log_id) = 8
and substr(bpr_log_id, 1, 4) = '5657'
*/

delete bw3.cos_bpr_log where bpr_log_id in ( select bpr_log_id from bw3.cos_bpr_data where institution_number = '00000114' and length(bpr_log_id) = 8
and substr(bpr_log_id, 1, 4) = '5657' );
commit;

delete cos_bpr_data t where institution_number = '00000114' and length(bpr_log_id) = 8
and substr(bpr_log_id, 1, 4) = '5657';
commit;
