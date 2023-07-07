package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_7 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _cryptogram_transaction_type;
	public AsciiField _card_sequence_number;
	public AsciiField _terminal_transaction_date;
	public AsciiField _terminal_capability_profile;
	public AsciiField _terminal_country_code;
	public AsciiField _terminal_serial_number;
	public AsciiField _unpredictable_number;
	public AsciiField _application_tran_counter;
	public AsciiField _application_inter_profile;
	public AsciiField _cryptogram;
	public AsciiField _iad_byte_2;
	public AsciiField _iad_byte_3;
	public AsciiField _term_verification_results;
	public AsciiField _iad_byte_4_7;
	public AsciiField _cryptogram_amount;
	public AsciiField _iad_byte_8;
	public AsciiField _iad_byte_9_16;
	public AsciiField _iad_byte_1;
	public AsciiField _iad_byte_17;
	public AsciiField _iad_byte_18_32;
	public AsciiField _form_factor_ind;
	public AsciiField _issuer_script_1_results;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] cryptogram_transaction_type; // [2] <open=suppress, name="cryptogram_transaction_type">;
	public char[] card_sequence_number; // [3] <open=suppress, name="card_sequence_number">;
	public char[] terminal_transaction_date; // [6] <open=suppress, name="terminal_transaction_date">;
	public char[] terminal_capability_profile; // [6] <open=suppress, name="terminal_capability_profile">;
	public char[] terminal_country_code; // [3] <open=suppress, name="terminal_country_code">;
	public char[] terminal_serial_number; // [8] <open=suppress, name="terminal_serial_number">;
	public char[] unpredictable_number; // [8] <open=suppress, name="unpredictable_number">;
	public char[] application_tran_counter; // [4] <open=suppress, name="application_tran_counter">;
	public char[] application_inter_profile; // [4] <open=suppress, name="application_inter_profile">;
	public char[] cryptogram; // [16] <open=suppress, name="cryptogram">;
	public char[] iad_byte_2; // [2] <open=suppress, name="iad_byte_2">;
	public char[] iad_byte_3; // [2] <open=suppress, name="iad_byte_3">;
	public char[] term_verification_results; // [10] <open=suppress, name="term_verification_results">;
	public char[] iad_byte_4_7; // [8] <open=suppress, name="iad_byte_4_7">;
	public char[] cryptogram_amount; // [12] <open=suppress, name="cryptogram_amount">;
	public char[] iad_byte_8; // [2] <open=suppress, name="iad_byte_8">;
	public char[] iad_byte_9_16; // [16] <open=suppress, name="iad_byte_9_16">;
	public char[] iad_byte_1; // [2] <open=suppress, name="iad_byte_1">;
	public char[] iad_byte_17; // [2] <open=suppress, name="iad_byte_17">;
	public char[] iad_byte_18_32; // [30] <open=suppress, name="iad_byte_18_32">;
	public char[] form_factor_ind; // [8] <open=suppress, name="form_factor_ind">;
	public char[] issuer_script_1_results; // [10] <open=suppress, name="issuer_script_1_results">;

	public visa_b2_05_7(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_7(String content) {
		super(content);
	}

	public visa_b2_05_7() {
		super();
	}

	public visa_b2_05_7(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct (int ifReturn){;
		if (ifReturn != 1) {
			addElement("trans_code", 2);
			addElement("trans_code_qualif", 1);
			addElement("trans_comp_seq", 1);
		}
		addElement("cryptogram_transaction_type", 2);
		addElement("card_sequence_number", 3);
		addElement("terminal_transaction_date", 6);
		addElement("terminal_capability_profile", 6);
		addElement("terminal_country_code", 3);
		addElement("terminal_serial_number", 8);
		addElement("unpredictable_number", 8);
		addElement("application_tran_counter", 4);
		addElement("application_inter_profile", 4);
		addElement("cryptogram", 16);
		addElement("iad_byte_2", 2);
		addElement("iad_byte_3", 2);
		addElement("term_verification_results", 10);
		addElement("iad_byte_4_7", 8);
		addElement("cryptogram_amount", 12);
		addElement("iad_byte_8", 2);
		addElement("iad_byte_9_16", 16);
		addElement("iad_byte_1", 2);
		addElement("iad_byte_17", 2);
		addElement("iad_byte_18_32", 30);
		addElement("form_factor_ind", 8);
		addElement("issuer_script_1_results", 10);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		cryptogram_transaction_type = getElement("cryptogram_transaction_type");
		card_sequence_number = getElement("card_sequence_number");
		terminal_transaction_date = getElement("terminal_transaction_date");
		terminal_capability_profile = getElement("terminal_capability_profile");
		terminal_country_code = getElement("terminal_country_code");
		terminal_serial_number = getElement("terminal_serial_number");
		unpredictable_number = getElement("unpredictable_number");
		application_tran_counter = getElement("application_tran_counter");
		application_inter_profile = getElement("application_inter_profile");
		cryptogram = getElement("cryptogram");
		iad_byte_2 = getElement("iad_byte_2");
		iad_byte_3 = getElement("iad_byte_3");
		term_verification_results = getElement("term_verification_results");
		iad_byte_4_7 = getElement("iad_byte_4_7");
		cryptogram_amount = getElement("cryptogram_amount");
		iad_byte_8 = getElement("iad_byte_8");
		iad_byte_9_16 = getElement("iad_byte_9_16");
		iad_byte_1 = getElement("iad_byte_1");
		iad_byte_17 = getElement("iad_byte_17");
		iad_byte_18_32 = getElement("iad_byte_18_32");
		form_factor_ind = getElement("form_factor_ind");
		issuer_script_1_results = getElement("issuer_script_1_results");

	}

	@Override
	public String getDescription() {
		return "TCR7 - Chip Card Transaction Data";
	}
}