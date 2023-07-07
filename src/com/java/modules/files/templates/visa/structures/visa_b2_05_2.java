package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_2 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _country_code;
	public AsciiField _trans_data;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] country_code; // [3] <open=suppress, name="country_code">;
	public char[] trans_data; // [149] <open=suppress, name="trans_data">;

	public visa_b2_05_2(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_2(String content) {
		super(content);
	}

	public visa_b2_05_2() {
		super();
	}

	public visa_b2_05_2(int ifReturn) {
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
		addElement("reserved_1", 12);
		addElement("country_code", 3);
		addElement("trans_data", 149);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		country_code = getElement("country_code");
		trans_data = getElement("trans_data");

	}

	@Override
	public String getDescription() {
		return "TCR 2 - ELSE  visa_b2_05_2_ST";
	}
}