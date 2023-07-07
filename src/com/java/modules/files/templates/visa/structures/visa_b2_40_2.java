package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_2 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _transaction_identifier;
	public AsciiField _excl_trans_id_reason;
	public AsciiField _multi_clear_seq_number;
	public AsciiField _card_acceptor_id;
	public AsciiField _terminal_id;
	public AsciiField _travel_agency_code;
	public AsciiField _cashback_indicator;
	public AsciiField _authorisation_code;
	public AsciiField _cardholder_id_meth;
	public AsciiField _pos_entry_mode;
	public AsciiField _pos_terminal_cap;
	public AsciiField _card_capab;
	public AsciiField _reserved_1;
	public AsciiField _cashback_amount;
	public AsciiField _cholder_activated_term_ind;
	public AsciiField _tel_ecomm_indicator;
	public AsciiField _agent_unique_id;
	public AsciiField _auth_method;
	public AsciiField _auth_reason_code;
	public AsciiField _reserved_2;
	public AsciiField _pan_token;
	public AsciiField _pan_token_extension;
	public AsciiField _network_id;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] transaction_identifier; // [15] <open=suppress, name="transaction_identifier">;
	public char[] excl_trans_id_reason; // [1] <open=suppress, name="excl_trans_id_reason">;
	public char[] multi_clear_seq_number; // [2] <open=suppress, name="multi_clear_seq_number">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] terminal_id; // [8] <open=suppress, name="terminal_id">;
	public char[] travel_agency_code; // [8] <open=suppress, name="travel_agency_code">;
	public char[] cashback_indicator; // [1] <open=suppress, name="cashback_indicator">;
	public char[] authorisation_code; // [6] <open=suppress, name="authorisation_code">;
	public char[] cardholder_id_meth; // [1] <open=suppress, name="cardholder_id_meth">;
	public char[] pos_entry_mode; // [2] <open=suppress, name="pos_entry_mode">;
	public char[] pos_terminal_cap; // [1] <open=suppress, name="pos_terminal_cap">;
	public char[] card_capab; // [1] <open=suppress, name="card_capab">;
	public char[] reserved_1; // [6] <open=suppress, name="reserved_1">;
	public char[] cashback_amount; // [9] <open=suppress, name="cashback_amount">;
	public char[] cholder_activated_term_ind; // [1] <open=suppress, name="cholder_activated_term_ind">;
	public char[] tel_ecomm_indicator; // [1] <open=suppress, name="tel_ecomm_indicator">;
	public char[] agent_unique_id; // [5] <open=suppress, name="agent_unique_id">;
	public char[] auth_method; // [2] <open=suppress, name="auth_method">;
	public char[] auth_reason_code; // [2] <open=suppress, name="auth_reason_code">;
	public char[] reserved_2; // [54] <open=suppress, name="reserved_2">;
	public char[] pan_token; // [16] <open=suppress, name="pan_token">;
	public char[] pan_token_extension; // [3] <open=suppress, name="pan_token_extension">;
	public char[] network_id; // [4] <open=suppress, name="network_id">;

	public visa_b2_40_2(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_2(String content) {
		super(content);
	}

	public visa_b2_40_2() {
		super();
	}

	public visa_b2_40_2(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("transaction_identifier", 15);
		addElement("excl_trans_id_reason", 1);
		addElement("multi_clear_seq_number", 2);
		addElement("card_acceptor_id", 15);
		addElement("terminal_id", 8);
		addElement("travel_agency_code", 8);
		addElement("cashback_indicator", 1);
		addElement("authorisation_code", 6);
		addElement("cardholder_id_meth", 1);
		addElement("pos_entry_mode", 2);
		addElement("pos_terminal_cap", 1);
		addElement("card_capab", 1);
		addElement("reserved_1", 6);
		addElement("cashback_amount", 9);
		addElement("cholder_activated_term_ind", 1);
		addElement("tel_ecomm_indicator", 1);
		addElement("agent_unique_id", 5);
		addElement("auth_method", 2);
		addElement("auth_reason_code", 2);
		addElement("reserved_2", 54);
		addElement("pan_token", 16);
		addElement("pan_token_extension", 3);
		addElement("network_id", 4);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		transaction_identifier = getElement("transaction_identifier");
		excl_trans_id_reason = getElement("excl_trans_id_reason");
		multi_clear_seq_number = getElement("multi_clear_seq_number");
		card_acceptor_id = getElement("card_acceptor_id");
		terminal_id = getElement("terminal_id");
		travel_agency_code = getElement("travel_agency_code");
		cashback_indicator = getElement("cashback_indicator");
		authorisation_code = getElement("authorisation_code");
		cardholder_id_meth = getElement("cardholder_id_meth");
		pos_entry_mode = getElement("pos_entry_mode");
		pos_terminal_cap = getElement("pos_terminal_cap");
		card_capab = getElement("card_capab");
		reserved_1 = getElement("reserved_1");
		cashback_amount = getElement("cashback_amount");
		cholder_activated_term_ind = getElement("cholder_activated_term_ind");
		tel_ecomm_indicator = getElement("tel_ecomm_indicator");
		agent_unique_id = getElement("agent_unique_id");
		auth_method = getElement("auth_method");
		auth_reason_code = getElement("auth_reason_code");
		reserved_2 = getElement("reserved_2");
		pan_token = getElement("pan_token");
		pan_token_extension = getElement("pan_token_extension");
		network_id = getElement("network_id");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_2_ST";
	}
}