package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_text;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_text; // [146] <open=suppress, name="message_text">;

	public visa_b2_50_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0(String content) {
		super(content);
	}

	public visa_b2_50_0() {
		super();
	}

	public visa_b2_50_0(int ifReturn) {
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
		addElement("service_id", 6);
		addElement("message_text", 146);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		service_id = getElement("service_id");
		message_text = getElement("message_text");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_ST";
	}
}