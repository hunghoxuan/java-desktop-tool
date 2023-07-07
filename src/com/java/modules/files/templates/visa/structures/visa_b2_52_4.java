package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_52_4 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved;
	public AsciiField _network_identification_code;
	public AsciiField _contact_for_info;
	public AsciiField _reserved_1;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved; // [12] <open=suppress,name="reserved">;
	public char[] network_identification_code; // [4] <open=suppress,name="network_identification_code">;
	public char[] contact_for_info; // [25] <open=suppress,name="contact_for_info">;
	public char[] reserved_1; // [123] <open=suppress,name="reserved_1">;

	public visa_b2_52_4(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_52_4(String content) {
		super(content);
	}

	public visa_b2_52_4() {
		super();
	}

	public visa_b2_52_4(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct;
		// {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("reserved", 12);
		addElement("network_identification_code", 4);
		addElement("contact_for_info", 25);
		addElement("reserved_1", 123);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved = getElement("reserved");
		network_identification_code = getElement("network_identification_code");
		contact_for_info = getElement("contact_for_info");
		reserved_1 = getElement("reserved_1");

	}

	@Override
	public String getDescription() {
		return "tc52 tcr";
	}
}