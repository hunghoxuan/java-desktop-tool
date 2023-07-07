select 	distinct chg.record_id_number,
		aoe.area_of_action,
		cap.capture_method,
		servid.service_id,
		fee_percent,
		fee_base,
		fee_minimum,
		Fee_maximum,
		fee_identifier,
		brnd.card_brand,
		tran.transaction_type
from	cbr_transaction_charges chg,
		cht_capture_method cap,
		cht_area_of_action aoe,
		cht_fee_identifier fee,
		cbr_service_definition serv,
		cht_services servid,
		cht_card_brand brnd,
		cht_transaction_type tran
where	chg.institution_number = '00000015'
and		chg.capture_method = cap.index_field
and     chg.area_of_event = aoe.index_field
and     chg.fee_id = fee.index_field
and		chg.service_id = serv.service_id
and		chg.service_id = servid.index_field
and		serv.service_id = servid.index_field
and 	serv.card_brand	= brnd.index_field
and		chg.transaction_type = tran.index_field
