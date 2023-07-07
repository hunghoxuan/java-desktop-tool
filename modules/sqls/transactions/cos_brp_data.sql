select C.transaction_date, T.tran_amount_gr,
C.card_number, -- T.Card_number,
 (bcb.index_field || ' - ' || bcb.card_brand) as card_brand,
C.terminal_id, -- T.Terminal_id,
(cdl.location || ' - ' || cdl.pos_region || ' - ' || cdl.pos_city) as location,
st.approval_code,
C.retrieval_reference,
C.stan,
C.transaction_link_id,
C.response_code
--T.transaction_slip
from COS_BPR_DATA C
inner join int_transactions T on C.institution_number =a T.institution_number
and T.transaction_link_id = C.transaction_link_id and C.Card_number = T.Card_number
-- and C.retrieval_reference = T.retrieval_reference and C.Card_number = T.Card_number
 left join BWT_CARD_BRAND bcb on bcb.index_field = T.card_brand and bcb.institution_number = t.institution_number and bcb.language = 'USA'
 left join cis_device_link cdl on cdl.terminal_id = T.terminal_id and cdl.institution_number = t.institution_number
  left join int_sundry_transactions st on st.transaction_slip = T.transaction_slip and st.institution_number = t.institution_number
where C.institution_number = '00002001' and C.message_type = 210 and C.response_code = '00' order by 1;


