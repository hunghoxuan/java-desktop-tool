package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_32 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _request_date;
	public AsciiField _locator_number;
	public AsciiField _nmas_seq_number;
	public AsciiField _action_indicator;
	public AsciiField _data;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] request_date; // [6] <open=suppress, name="request_date">;
	public char[] locator_number; // [4] <open=suppress, name="locator_number">;
	public char[] nmas_seq_number; // [2] <open=suppress, name="nmas_seq_number">;
	public char[] action_indicator; // [2] <open=suppress, name="action_indicator">;
	public char[] data; // [138] <open=suppress, name="data">;

	public visa_b2_32(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_32(String content) {
		super(content);
	}

	public visa_b2_32() {
		super();
	}

	public visa_b2_32(int ifReturn) {
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
		addElement("request_date", 6);
		addElement("locator_number", 4);
		addElement("nmas_seq_number", 2);
		addElement("action_indicator", 2);
		addElement("data", 138);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		request_date = getElement("request_date");
		locator_number = getElement("locator_number");
		nmas_seq_number = getElement("nmas_seq_number");
		action_indicator = getElement("action_indicator");
		data = getElement("data");

	}

	@Override
	public String getDescription() {
		return "visa_b2_32_ST";
	}
}