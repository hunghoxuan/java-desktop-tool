package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_2 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _receiving_institution_id;
	public AsciiField _receiving_inst_country_code;
	public AsciiField _issuing_institution_id;
	public AsciiField _issuing_inst_country_code;
	public AsciiField _transaction_identifier;
	public AsciiField _authorization_chars_indicator;
	public AsciiField _ms_authorization_data_indicator;
	public AsciiField _duration;
	public AsciiField _prestigious_property_indicator;
	public AsciiField _cashback;
	public AsciiField _replacement_amount;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] receiving_institution_id; // [11] <open=suppress, name="receiving_institution_id">;
	public char[] receiving_inst_country_code; // [3] <open=suppress, name="receiving_inst_country_code">;
	public char[] issuing_institution_id; // [11] <open=suppress, name="issuing_institution_id">;
	public char[] issuing_inst_country_code; // [3] <open=suppress, name="issuing_inst_country_code">;
	public char[] transaction_identifier; // [15] <open=suppress, name="transaction_identifier">;
	public char[] authorization_chars_indicator; // [1] <open=suppress, name="authorization_chars_indicator">;
	public char[] ms_authorization_data_indicator; // [1] <open=suppress, name="ms_authorization_data_indicator">;
	public char[] duration; // [2] <open=suppress, name="duration">;
	public char[] prestigious_property_indicator; // [1] <open=suppress, name="prestigious_property_indicator">;
	public char[] cashback; // [9] <open=suppress, name="cashback">;
	public char[] replacement_amount; // [12] <open=suppress, name="replacement_amount">;
	public char[] reserved_2; // [83] <open=suppress, name="reserved_2">;

	public visa_b2_48_2(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_2(String content) {
		super(content);
	}

	public visa_b2_48_2() {
		super();
	}

	public visa_b2_48_2(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("reserved_1", 12);
		addElement("receiving_institution_id", 11);
		addElement("receiving_inst_country_code", 3);
		addElement("issuing_institution_id", 11);
		addElement("issuing_inst_country_code", 3);
		addElement("transaction_identifier", 15);
		addElement("authorization_chars_indicator", 1);
		addElement("ms_authorization_data_indicator", 1);
		addElement("duration", 2);
		addElement("prestigious_property_indicator", 1);
		addElement("cashback", 9);
		addElement("replacement_amount", 12);
		addElement("reserved_2", 83);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		receiving_institution_id = getElement("receiving_institution_id");
		receiving_inst_country_code = getElement("receiving_inst_country_code");
		issuing_institution_id = getElement("issuing_institution_id");
		issuing_inst_country_code = getElement("issuing_inst_country_code");
		transaction_identifier = getElement("transaction_identifier");
		authorization_chars_indicator = getElement("authorization_chars_indicator");
		ms_authorization_data_indicator = getElement("ms_authorization_data_indicator");
		duration = getElement("duration");
		prestigious_property_indicator = getElement("prestigious_property_indicator");
		cashback = getElement("cashback");
		replacement_amount = getElement("replacement_amount");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_48_2_ST";
	}
}