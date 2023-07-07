package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_E extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _bus_format_code;
	public AsciiField _trans_data;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] trans_data; // [162] <open=suppress, name="trans_data">;

	public visa_b2_05_E(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_E(String content) {
		super(content);
	}

	public visa_b2_05_E() {
		super();
	}

	public visa_b2_05_E(int ifReturn) {
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
		addElement("bus_format_code", 2);
		addElement("trans_data", 162);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		bus_format_code = getElement("bus_format_code");
		trans_data = getElement("trans_data");

	}

	@Override
	public String getDescription() {
		return "";
	}
}