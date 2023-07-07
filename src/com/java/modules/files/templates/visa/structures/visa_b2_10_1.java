package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_10_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _rate_table_id;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] rate_table_id; // [5] <open=suppress, name="rate_table_id">;
	public char[] reserved; // [159] <open=suppress, name="reserved">;

	public visa_b2_10_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_10_1(String content) {
		super(content);
	}

	public visa_b2_10_1() {
		super();
	}

	public visa_b2_10_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		//// BOC-210172 begin;
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("rate_table_id", 5);
		addElement("reserved", 159);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		rate_table_id = getElement("rate_table_id");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_b2_10_1_ST";
	}
}