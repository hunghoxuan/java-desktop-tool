package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22220 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _pos_condition_code;
	public AsciiField _pos_entry_mode;
	public AsciiField _pos_terminal_type;
	public AsciiField _pos_terminal_entry_capability;
	public AsciiField _merchant_type;
	public AsciiField _card_acceptor_terminal_id;
	public AsciiField _card_acceptor_id;
	public AsciiField _card_acceptor_name;
	public AsciiField _card_acceptor_city;
	public AsciiField _card_acceptor_country;
	public AsciiField _geo_state_code;
	public AsciiField _geo_zip_code;
	public AsciiField _geo_country_code;
	public AsciiField _acquiring_inst_country_code;
	public AsciiField _pan_extended_country_code;
	public AsciiField _forward_inst_id;
	public AsciiField _forwarding_inst_country_code;
	public AsciiField _customer_id_method;
	public AsciiField _issuer_affiliate_bin;
	public AsciiField _filler;
	public AsciiField _recurring_payment_ind_flag;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="report_line_sequence_number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] pos_condition_code; // [2] <open=suppress, name="pos_condition_code">;
	public char[] pos_entry_mode; // [3] <open=suppress, name="pos_entry_mode">;
	public char[] pos_terminal_type; // [2] <open=suppress, name="pos_terminal_type">;
	public char[] pos_terminal_entry_capability; // [1] <open=suppress, name="pos_terminal_entry_capability">;
	public char[] merchant_type; // [4] <open=suppress, name="merchant_type">;
	public char[] card_acceptor_terminal_id; // [8] <open=suppress, name="card_acceptor_terminal_id">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] card_acceptor_name; // [25] <open=suppress, name="card_acceptor_name">;
	public char[] card_acceptor_city; // [13] <open=suppress, name="card_acceptor_city">;
	public char[] card_acceptor_country; // [2] <open=suppress, name="card_acceptor_country">;
	public char[] geo_state_code; // [2] <open=suppress, name="geo_state_code">;
	public char[] geo_zip_code; // [9] <open=suppress, name="geo_zip_code">;
	public char[] geo_country_code; // [3] <open=suppress, name="geo_country_code">;
	public char[] acquiring_inst_country_code; // [3] <open=suppress, name="acquiring_inst_country_code">;
	public char[] pan_extended_country_code; // [3] <open=suppress, name="pan_extended_country_code">;
	public char[] forward_inst_id; // [11] <open=suppress, name="forward_inst_id">;
	public char[] forwarding_inst_country_code; // [3] <open=suppress, name="forwarding_inst_country_code">;
	public char[] customer_id_method; // [1] <open=suppress, name="customer_id_method">;
	public char[] issuer_affiliate_bin; // [10] <open=suppress, name="issuer_affiliate_bin">;
	public char[] filler; // [6] <open=suppress, name="filler">; //3
	public char[] recurring_payment_ind_flag; // [1] <open=suppress, name="recurring_payment_ind_flag">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22220(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22220(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22220() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22220(int ifReturn) {
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
		addElement("pos_condition_code", 2);
		addElement("pos_entry_mode", 3);
		addElement("pos_terminal_type", 2);
		addElement("pos_terminal_entry_capability", 1);
		addElement("merchant_type", 4);
		addElement("card_acceptor_terminal_id", 8);
		addElement("card_acceptor_id", 15);
		addElement("card_acceptor_name", 25);
		addElement("card_acceptor_city", 13);
		addElement("card_acceptor_country", 2);
		addElement("geo_state_code", 2);
		addElement("geo_zip_code", 9);
		addElement("geo_country_code", 3);
		addElement("acquiring_inst_country_code", 3);
		addElement("pan_extended_country_code", 3);
		addElement("forward_inst_id", 11);
		addElement("forwarding_inst_country_code", 3);
		addElement("customer_id_method", 1);
		addElement("issuer_affiliate_bin", 10);
		addElement("filler", 6);
		addElement("recurring_payment_ind_flag", 1);
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
		pos_condition_code = getElement("pos_condition_code");
		pos_entry_mode = getElement("pos_entry_mode");
		pos_terminal_type = getElement("pos_terminal_type");
		pos_terminal_entry_capability = getElement("pos_terminal_entry_capability");
		merchant_type = getElement("merchant_type");
		card_acceptor_terminal_id = getElement("card_acceptor_terminal_id");
		card_acceptor_id = getElement("card_acceptor_id");
		card_acceptor_name = getElement("card_acceptor_name");
		card_acceptor_city = getElement("card_acceptor_city");
		card_acceptor_country = getElement("card_acceptor_country");
		geo_state_code = getElement("geo_state_code");
		geo_zip_code = getElement("geo_zip_code");
		geo_country_code = getElement("geo_country_code");
		acquiring_inst_country_code = getElement("acquiring_inst_country_code");
		pan_extended_country_code = getElement("pan_extended_country_code");
		forward_inst_id = getElement("forward_inst_id");
		forwarding_inst_country_code = getElement("forwarding_inst_country_code");
		customer_id_method = getElement("customer_id_method");
		issuer_affiliate_bin = getElement("issuer_affiliate_bin");
		filler = getElement("filler");
		recurring_payment_ind_flag = getElement("recurring_payment_ind_flag");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_SMSRAWDATA_V22220_ST";
	}
}