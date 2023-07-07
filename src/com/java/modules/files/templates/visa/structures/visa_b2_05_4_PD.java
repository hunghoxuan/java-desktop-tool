package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_4_PD extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _agent_unique_id;
	public AsciiField _reserved_1;
	public AsciiField _business_format_code;
	public AsciiField _promotion_type;
	public AsciiField _promotion_code;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] agent_unique_id; // [1] <open=suppress, name="agent_unique_id">;
	public char[] reserved_1; // [10] <open=suppress, name="reserved_1">;
	public char[] business_format_code; // [2] <open=suppress, name="business_format_code">;
	public char[] promotion_type; // [2] <open=suppress, name="promotion_type">;
	public char[] promotion_code; // [25] <open=suppress, name="promotion_code">;
	public char[] reserved; // [125] <open=suppress, name="reserved">;

	public visa_b2_05_4_PD(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_4_PD(String content) {
		super(content);
	}

	public visa_b2_05_4_PD() {
		super();
	}

	public visa_b2_05_4_PD(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("agent_unique_id", 1);
		addElement("reserved_1", 10);
		addElement("business_format_code", 2);
		addElement("promotion_type", 2);
		addElement("promotion_code", 25);
		addElement("reserved", 125);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		agent_unique_id = getElement("agent_unique_id");
		reserved_1 = getElement("reserved_1");
		business_format_code = getElement("business_format_code");
		promotion_type = getElement("promotion_type");
		promotion_code = getElement("promotion_code");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "";
	}
}