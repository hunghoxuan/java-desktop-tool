package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22210 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _local_trans_date;
	public AsciiField _local_trans_time;
	public AsciiField _giv_flag;
	public AsciiField _giv_flag_previous;
	public AsciiField _acquiring_inst_id;
	public AsciiField _acquirer_business_id;
	public AsciiField _source_station_id;
	public AsciiField _destination_source_id;
	public AsciiField _message_reason_code;
	public AsciiField _stip_reason_code;
	public AsciiField _auth_id_resp_code;
	public AsciiField _network_id;
	public AsciiField _advice_source_flag;
	public AsciiField _advice_transaction_flag;
	public AsciiField _base_I_bill_flag;
	public AsciiField _track_data_indicator;
	public AsciiField _reimbursement_attribute;
	public AsciiField _spend_qualified_indicator;
	public AsciiField _reserved;
	public AsciiField _pvs_performed_indicator;
	public AsciiField _transmission_date;
	public AsciiField _transmission_time;
	public AsciiField _transaction_other_amount;
	public AsciiField _downgrade_reason_code;
	public AsciiField _aci;
	public AsciiField _response_message_type;
	public AsciiField _card_sequence_number;
	public AsciiField _card_expiration_date;
	public AsciiField _cvv_result_code;
	public AsciiField _settlement_service_requested;
	public AsciiField _settlement_service_selected;
	public AsciiField _irf_option;
	public AsciiField _moto_ecommerce_ind;
	public AsciiField _merchant_volume_ind;
	public AsciiField _dcc_ind;
	public AsciiField _fee_program_ind;
	public AsciiField _filler;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="report_line_sequence_number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] local_trans_date; // [4] <open=suppress, name="local_trans_date">;
	public char[] local_trans_time; // [6] <open=suppress, name="local_trans_time">;
	public char[] giv_flag; // [1] <open=suppress, name="giv_flag">;
	public char[] giv_flag_previous; // [1] <open=suppress, name="giv_flag_previous">;
	public char[] acquiring_inst_id; // [11] <open=suppress, name="acquiring_inst_id">;
	public char[] acquirer_business_id; // [8] <open=suppress, name="acquirer_business_id">;
	public char[] source_station_id; // [6] <open=suppress, name="source_station_id">;
	public char[] destination_source_id; // [6] <open=suppress, name="destination_source_id">;
	public char[] message_reason_code; // [4] <open=suppress, name="message_reason_code">;
	public char[] stip_reason_code; // [4] <open=suppress, name="stip_reason_code">;
	public char[] auth_id_resp_code; // [6] <open=suppress, name="auth_id_resp_code">; //1
	public char[] network_id; // [4] <open=suppress, name="network_id">; //1
	public char[] advice_source_flag; // [1] <open=suppress, name="advice_source_flag">;
	public char[] advice_transaction_flag; // [1] <open=suppress, name="advice_transaction_flag">;
	public char[] base_I_bill_flag; // [1] <open=suppress, name="base_I_bill_flag">;
	public char[] track_data_indicator; // [1] <open=suppress, name="track_data_indicator">;
	public char[] reimbursement_attribute; // [1] <open=suppress, name="reimbursement_attribute">;
	public char[] spend_qualified_indicator; // [1] <open=suppress, name="spend_qualified_indicator">;
	public char[] reserved; // [7] <open=suppress, name="reserved">;
	public char[] pvs_performed_indicator; // [1] <open=suppress, name="pvs_performed_indicator">;
	public char[] transmission_date; // [4] <open=suppress, name="transmission_date">;
	public char[] transmission_time; // [6] <open=suppress, name="transmission_time">;
	public char[] transaction_other_amount; // [12] <open=suppress, name="transaction_other_amount">;
	public char[] downgrade_reason_code; // [2] <open=suppress, name="downgrade_reason_code">;
	public char[] aci; // [1] <open=suppress, name="aci">;
	public char[] response_message_type; // [4] <open=suppress, name="response_message_type">;
	public char[] card_sequence_number; // [3] <open=suppress, name="card_sequence_number">;
	public char[] card_expiration_date; // [4] <open=suppress, name="card_expiration_date">;
	public char[] cvv_result_code; // [1] <open=suppress, name="cvv_result_code">;
	public char[] settlement_service_requested; // [1] <open=suppress, name="settlement_service_requested">;
	public char[] settlement_service_selected; // [1] <open=suppress, name="settlement_service_selected">;
	public char[] irf_option; // [1] <open=suppress, name="irf_option">;
	public char[] moto_ecommerce_ind; // [1] <open=suppress, name="moto_ecommerce_ind">;
	public char[] merchant_volume_ind; // [2] <open=suppress, name="merchant_volume_ind">;
	public char[] dcc_ind; // [1] <open=suppress, name="dcc_ind">;
	public char[] fee_program_ind; // [3] <open=suppress, name="fee_program_ind">; //2
	public char[] filler; // [5] <open=suppress, name="filler">; //2
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22210(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22210(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22210() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22210(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("report_identifier", 10);
		addElement("report_line_sequence_number", 8);
		addElement("record_type", 6);
		addElement("local_trans_date", 4);
		addElement("local_trans_time", 6);
		addElement("giv_flag", 1);
		addElement("giv_flag_previous", 1);
		addElement("acquiring_inst_id", 11);
		addElement("acquirer_business_id", 8);
		addElement("source_station_id", 6);
		addElement("destination_source_id", 6);
		addElement("message_reason_code", 4);
		addElement("stip_reason_code", 4);
		addElement("auth_id_resp_code", 6);
		addElement("network_id", 4);
		addElement("advice_source_flag", 1);
		addElement("advice_transaction_flag", 1);
		addElement("base_I_bill_flag", 1);
		addElement("track_data_indicator", 1);
		addElement("reimbursement_attribute", 1);
		addElement("spend_qualified_indicator", 1);
		addElement("reserved", 7);
		addElement("pvs_performed_indicator", 1);
		addElement("transmission_date", 4);
		addElement("transmission_time", 6);
		addElement("transaction_other_amount", 12);
		addElement("downgrade_reason_code", 2);
		addElement("aci", 1);
		addElement("response_message_type", 4);
		addElement("card_sequence_number", 3);
		addElement("card_expiration_date", 4);
		addElement("cvv_result_code", 1);
		addElement("settlement_service_requested", 1);
		addElement("settlement_service_selected", 1);
		addElement("irf_option", 1);
		addElement("moto_ecommerce_ind", 1);
		addElement("merchant_volume_ind", 2);
		addElement("dcc_ind", 1);
		addElement("fee_program_ind", 3);
		addElement("filler", 5);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		report_identifier = getElement("report_identifier");
		report_line_sequence_number = getElement("report_line_sequence_number");
		record_type = getElement("record_type");
		local_trans_date = getElement("local_trans_date");
		local_trans_time = getElement("local_trans_time");
		giv_flag = getElement("giv_flag");
		giv_flag_previous = getElement("giv_flag_previous");
		acquiring_inst_id = getElement("acquiring_inst_id");
		acquirer_business_id = getElement("acquirer_business_id");
		source_station_id = getElement("source_station_id");
		destination_source_id = getElement("destination_source_id");
		message_reason_code = getElement("message_reason_code");
		stip_reason_code = getElement("stip_reason_code");
		auth_id_resp_code = getElement("auth_id_resp_code");
		network_id = getElement("network_id");
		advice_source_flag = getElement("advice_source_flag");
		advice_transaction_flag = getElement("advice_transaction_flag");
		base_I_bill_flag = getElement("base_I_bill_flag");
		track_data_indicator = getElement("track_data_indicator");
		reimbursement_attribute = getElement("reimbursement_attribute");
		spend_qualified_indicator = getElement("spend_qualified_indicator");
		reserved = getElement("reserved");
		pvs_performed_indicator = getElement("pvs_performed_indicator");
		transmission_date = getElement("transmission_date");
		transmission_time = getElement("transmission_time");
		transaction_other_amount = getElement("transaction_other_amount");
		downgrade_reason_code = getElement("downgrade_reason_code");
		aci = getElement("aci");
		response_message_type = getElement("response_message_type");
		card_sequence_number = getElement("card_sequence_number");
		card_expiration_date = getElement("card_expiration_date");
		cvv_result_code = getElement("cvv_result_code");
		settlement_service_requested = getElement("settlement_service_requested");
		settlement_service_selected = getElement("settlement_service_selected");
		irf_option = getElement("irf_option");
		moto_ecommerce_ind = getElement("moto_ecommerce_ind");
		merchant_volume_ind = getElement("merchant_volume_ind");
		dcc_ind = getElement("dcc_ind");
		fee_program_ind = getElement("fee_program_ind");
		filler = getElement("filler");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_SMSRAWDATA_V22210_ST";
	}
}