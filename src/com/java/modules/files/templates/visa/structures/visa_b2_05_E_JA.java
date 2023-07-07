package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_E_JA extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _bus_format_code;
	public AsciiField _agent_unique_id;
	public AsciiField _auth_method;
	public AsciiField _auth_reason_code;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] agent_unique_id; // [5] <open=suppress, name="agent_unique_id">;
	public char[] auth_method; // [2] <open=suppress, name="auth_method">;
	public char[] auth_reason_code; // [2] <open=suppress, name="auth_reason_code">;
	public char[] reserved; // [153] <open=suppress, name="reserved">;

	public visa_b2_05_E_JA(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_E_JA(String content) {
		super(content);
	}

	public visa_b2_05_E_JA() {
		super();
	}

	public visa_b2_05_E_JA(int ifReturn) {
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
		addElement("agent_unique_id", 5);
		addElement("auth_method", 2);
		addElement("auth_reason_code", 2);
		addElement("reserved", 153);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		bus_format_code = getElement("bus_format_code");
		agent_unique_id = getElement("agent_unique_id");
		auth_method = getElement("auth_method");
		auth_reason_code = getElement("auth_reason_code");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "";
	}
}