package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_0_2 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _format_code;
	public AsciiField _account_number;
	public AsciiField _response_code;
	public AsciiField _destination_amount;
	public AsciiField _destination_curr_code;
	public AsciiField _source_amount;
	public AsciiField _source_currency_code;
	public AsciiField _pos_entry_mode;
	public AsciiField _pos_terminal_capability;
	public AsciiField _merchant_name;
	public AsciiField _merchant_city;
	public AsciiField _merchant_country_code;
	public AsciiField _merchant_zip_code;
	public AsciiField _reserved_1;
	public AsciiField _merchant_stat_prov_code;
	public AsciiField _card_acceptor_term_id;
	public AsciiField _terminal_id;
	public AsciiField _merchant_category_code;
	public AsciiField _persistent_fx_eligible;
	public AsciiField _rate_table_id;
	public AsciiField _reserved_2;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] format_code; // [1] <open=suppress, name="format_code">;
	public char[] account_number; // [28] <open=suppress, name="account_number">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] destination_amount; // [12] <open=suppress, name="destination_amount">;
	public char[] destination_curr_code; // [3] <open=suppress, name="destination_curr_code">;
	public char[] source_amount; // [12] <open=suppress, name="source_amount">;
	public char[] source_currency_code; // [3] <open=suppress, name="source_currency_code">;
	public char[] pos_entry_mode; // [2] <open=suppress, name="pos_entry_mode">;
	public char[] pos_terminal_capability; // [1] <open=suppress, name="pos_terminal_capability">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_city; // [13] <open=suppress, name="merchant_city">;
	public char[] merchant_country_code; // [3] <open=suppress, name="merchant_country_code">;
	public char[] merchant_zip_code; // [5] <open=suppress, name="merchant_zip_code">;
	public char[] reserved_1; // [4] <open=suppress, name="reserved_1">;
	public char[] merchant_stat_prov_code; // [3] <open=suppress, name="merchant_stat_prov_code">;
	public char[] card_acceptor_term_id; // [15] <open=suppress, name="card_acceptor_term_id">;
	public char[] terminal_id; // [8] <open=suppress, name="terminal_id">;
	public char[] merchant_category_code; // [4] <open=suppress, name="merchant_category_code">;
	public char[] persistent_fx_eligible; // [1] <open=suppress, name="persistent_fx_eligible">;
	public char[] rate_table_id; // [5] <open=suppress, name="rate_table_id">;
	public char[] reserved_2; // [1] <open=suppress, name="reserved_2">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_48_0_2(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_0_2(String content) {
		super(content);
	}

	public visa_b2_48_0_2() {
		super();
	}

	public visa_b2_48_0_2(int ifReturn) {
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
		addElement("format_code", 1);
		addElement("account_number", 28);
		addElement("response_code", 2);
		addElement("destination_amount", 12);
		addElement("destination_curr_code", 3);
		addElement("source_amount", 12);
		addElement("source_currency_code", 3);
		addElement("pos_entry_mode", 2);
		addElement("pos_terminal_capability", 1);
		addElement("merchant_name", 25);
		addElement("merchant_city", 13);
		addElement("merchant_country_code", 3);
		addElement("merchant_zip_code", 5);
		addElement("reserved_1", 4);
		addElement("merchant_stat_prov_code", 3);
		addElement("card_acceptor_term_id", 15);
		addElement("terminal_id", 8);
		addElement("merchant_category_code", 4);
		addElement("persistent_fx_eligible", 1);
		addElement("rate_table_id", 5);
		addElement("reserved_2", 1);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		format_code = getElement("format_code");
		account_number = getElement("account_number");
		response_code = getElement("response_code");
		destination_amount = getElement("destination_amount");
		destination_curr_code = getElement("destination_curr_code");
		source_amount = getElement("source_amount");
		source_currency_code = getElement("source_currency_code");
		pos_entry_mode = getElement("pos_entry_mode");
		pos_terminal_capability = getElement("pos_terminal_capability");
		merchant_name = getElement("merchant_name");
		merchant_city = getElement("merchant_city");
		merchant_country_code = getElement("merchant_country_code");
		merchant_zip_code = getElement("merchant_zip_code");
		reserved_1 = getElement("reserved_1");
		merchant_stat_prov_code = getElement("merchant_stat_prov_code");
		card_acceptor_term_id = getElement("card_acceptor_term_id");
		terminal_id = getElement("terminal_id");
		merchant_category_code = getElement("merchant_category_code");
		persistent_fx_eligible = getElement("persistent_fx_eligible");
		rate_table_id = getElement("rate_table_id");
		reserved_2 = getElement("reserved_2");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_48_0_2_ST";
	}
}