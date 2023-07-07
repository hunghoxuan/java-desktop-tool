package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _cvv2_auth_request_data;
	public AsciiField _cvv2_result_code;
	public AsciiField _reserved_1;
	public AsciiField _original_message_type;
	public AsciiField _persistent_fx_eligible;
	public AsciiField _rate_table_id;
	public AsciiField _reserved;
	public AsciiField _trace_audit_number;
	public AsciiField _card_acceptor_name_loc;
	public AsciiField _national_pos_geo_data;
	public AsciiField _amount_issuer;
	public AsciiField _remaining_open_to_use;
	public AsciiField _address_verification_data;
	public AsciiField _forwarding_institution_id;
	public AsciiField _forwarding_inst_country_code;
	public AsciiField _file_update_error_code;
	public AsciiField _pacm_diversion_level;
	public AsciiField _pacm_diversion_reason;
	public AsciiField _chip_condition_code;
	public AsciiField _cvv_results_code;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] cvv2_auth_request_data; // [6] <open=suppress, name="cvv2_auth_request_data">;
	public char[] cvv2_result_code; // [1] <open=suppress, name="cvv2_result_code">;
	public char[] reserved_1; // [5] <open=suppress, name="reserved_1">;
	public char[] original_message_type; // [4] <open=suppress, name="original_message_type">;
	public char[] persistent_fx_eligible; // [1] <open=suppress, name="persistent_fx_eligible">;
	public char[] rate_table_id; // [5] <open=suppress, name="rate_table_id">;
	public char[] reserved; // [1] <open=suppress, name="reserved">;
	public char[] trace_audit_number; // [6] <open=suppress, name="trace_audit_number">;
	public char[] card_acceptor_name_loc; // [40] <open=suppress, name="card_acceptor_name_loc">;
	public char[] national_pos_geo_data; // [14] <open=suppress, name="national_pos_geo_data">;
	public char[] amount_issuer; // [12] <open=suppress, name="amount_issuer">;
	public char[] remaining_open_to_use; // [13] <open=suppress, name="remaining_open_to_use">;
	public char[] address_verification_data; // [29] <open=suppress, name="address_verification_data">;
	public char[] forwarding_institution_id; // [11] <open=suppress, name="forwarding_institution_id">;
	public char[] forwarding_inst_country_code; // [3] <open=suppress, name="forwarding_inst_country_code">;
	public char[] file_update_error_code; // [4] <open=suppress, name="file_update_error_code">;
	public char[] pacm_diversion_level; // [2] <open=suppress, name="pacm_diversion_level">;
	public char[] pacm_diversion_reason; // [1] <open=suppress, name="pacm_diversion_reason">;
	public char[] chip_condition_code; // [1] <open=suppress, name="chip_condition_code">;
	public char[] cvv_results_code; // [1] <open=suppress, name="cvv_results_code">;
	public char[] reserved_2; // [4] <open=suppress, name="reserved_2">;

	public visa_b2_48_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_1(String content) {
		super(content);
	}

	public visa_b2_48_1() {
		super();
	}

	public visa_b2_48_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("cvv2_auth_request_data", 6);
		addElement("cvv2_result_code", 1);
		addElement("reserved_1", 5);
		addElement("original_message_type", 4);
		addElement("persistent_fx_eligible", 1);
		addElement("rate_table_id", 5);
		addElement("reserved", 1);
		addElement("trace_audit_number", 6);
		addElement("card_acceptor_name_loc", 40);
		addElement("national_pos_geo_data", 14);
		addElement("amount_issuer", 12);
		addElement("remaining_open_to_use", 13);
		addElement("address_verification_data", 29);
		addElement("forwarding_institution_id", 11);
		addElement("forwarding_inst_country_code", 3);
		addElement("file_update_error_code", 4);
		addElement("pacm_diversion_level", 2);
		addElement("pacm_diversion_reason", 1);
		addElement("chip_condition_code", 1);
		addElement("cvv_results_code", 1);
		addElement("reserved_2", 4);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		cvv2_auth_request_data = getElement("cvv2_auth_request_data");
		cvv2_result_code = getElement("cvv2_result_code");
		reserved_1 = getElement("reserved_1");
		original_message_type = getElement("original_message_type");
		persistent_fx_eligible = getElement("persistent_fx_eligible");
		rate_table_id = getElement("rate_table_id");
		reserved = getElement("reserved");
		trace_audit_number = getElement("trace_audit_number");
		card_acceptor_name_loc = getElement("card_acceptor_name_loc");
		national_pos_geo_data = getElement("national_pos_geo_data");
		amount_issuer = getElement("amount_issuer");
		remaining_open_to_use = getElement("remaining_open_to_use");
		address_verification_data = getElement("address_verification_data");
		forwarding_institution_id = getElement("forwarding_institution_id");
		forwarding_inst_country_code = getElement("forwarding_inst_country_code");
		file_update_error_code = getElement("file_update_error_code");
		pacm_diversion_level = getElement("pacm_diversion_level");
		pacm_diversion_reason = getElement("pacm_diversion_reason");
		chip_condition_code = getElement("chip_condition_code");
		cvv_results_code = getElement("cvv_results_code");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_48_1_ST";
	}
}