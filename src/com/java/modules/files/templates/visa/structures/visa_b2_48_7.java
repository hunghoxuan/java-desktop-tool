package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_7 extends AsciiMessage {
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
	public AsciiField _derivation_key_index;
	public AsciiField _cryptogram_version;
	public AsciiField _IAD_Byte_2;
	public AsciiField _IAD_Byte_3;
	public AsciiField _Term_Verification_Results;
	public AsciiField _IAD_Byte_4_7;
	public AsciiField _Cryptogram_Amount;
	public AsciiField _IAD_Byte_8;
	public AsciiField _IAD_Byte_9_16;
	public AsciiField _IAD_Byte_1;
	public AsciiField _IAD_Byte_17;
	public AsciiField _IAD_Byte_18_32;
	public AsciiField _Form_Factor_Ind;
	public AsciiField _Issuer_Script_1_Results;
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
	public char[] derivation_key_index; // [2] <open=suppress, name="derivation_key_index">;
	public char[] cryptogram_version; // [2] <open=suppress, name="cryptogram_version">;
	public char[] IAD_Byte_2; // [2] <open=suppress, name="IAD_Byte_2">;
	public char[] IAD_Byte_3; // [2] <open=suppress, name="IAD_Byte_3">;
	public char[] Term_Verification_Results; // [10] <open=suppress, name="Term_Verification_Results">;
	public char[] IAD_Byte_4_7; // [8] <open=suppress, name="IAD_Byte_4_7">;
	public char[] Cryptogram_Amount; // [12] <open=suppress, name="Cryptogram_Amount">;
	public char[] IAD_Byte_8; // [2] <open=suppress, name="IAD_Byte_8">;
	public char[] IAD_Byte_9_16; // [16] <open=suppress, name="IAD_Byte_9_16">;
	public char[] IAD_Byte_1; // [2] <open=suppress, name="IAD_Byte_1">;
	public char[] IAD_Byte_17; // [2] <open=suppress, name="IAD_Byte_17">;
	public char[] IAD_Byte_18_32; // [30] <open=suppress, name="IAD_Byte_18_32">;
	public char[] Form_Factor_Ind; // [8] <open=suppress, name="Form_Factor_Ind">;
	public char[] Issuer_Script_1_Results; // [10] <open=suppress, name="Issuer_Script_1_Results">;

	public visa_b2_48_7(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_7(String content) {
		super(content);
	}

	public visa_b2_48_7() {
		super();
	}

	public visa_b2_48_7(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
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
		addElement("derivation_key_index", 2);
		addElement("cryptogram_version", 2);
		addElement("IAD_Byte_2", 2);
		addElement("IAD_Byte_3", 2);
		addElement("Term_Verification_Results", 10);
		addElement("IAD_Byte_4_7", 8);
		addElement("Cryptogram_Amount", 12);
		addElement("IAD_Byte_8", 2);
		addElement("IAD_Byte_9_16", 16);
		addElement("IAD_Byte_1", 2);
		addElement("IAD_Byte_17", 2);
		addElement("IAD_Byte_18_32", 30);
		addElement("Form_Factor_Ind", 8);
		addElement("Issuer_Script_1_Results", 10);

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
		derivation_key_index = getElement("derivation_key_index");
		cryptogram_version = getElement("cryptogram_version");
		IAD_Byte_2 = getElement("IAD_Byte_2");
		IAD_Byte_3 = getElement("IAD_Byte_3");
		Term_Verification_Results = getElement("Term_Verification_Results");
		IAD_Byte_4_7 = getElement("IAD_Byte_4_7");
		Cryptogram_Amount = getElement("Cryptogram_Amount");
		IAD_Byte_8 = getElement("IAD_Byte_8");
		IAD_Byte_9_16 = getElement("IAD_Byte_9_16");
		IAD_Byte_1 = getElement("IAD_Byte_1");
		IAD_Byte_17 = getElement("IAD_Byte_17");
		IAD_Byte_18_32 = getElement("IAD_Byte_18_32");
		Form_Factor_Ind = getElement("Form_Factor_Ind");
		Issuer_Script_1_Results = getElement("Issuer_Script_1_Results");

	}

	@Override
	public String getDescription() {
		return "TCR7";
	}
}