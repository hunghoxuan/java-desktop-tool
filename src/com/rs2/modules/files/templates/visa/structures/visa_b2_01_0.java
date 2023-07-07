package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_01_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;

	public visa_b2_01_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_01_0(String trans_comp_seq, int StartLineReturn) {
		super();
	}

	public visa_b2_01_0(String content) {
		super(content);
	}

	public visa_b2_01_0() {
		super();
	}

	public visa_b2_01_0(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct(string trans_comp_seq, int64 StartLine) {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		// parseReturnedTCR(trans_comp_seq, StartLine);;
		//// BYTE returned_tcr [164] <open=suppress, name="returned_tcr">;;

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");

	}

	@Override
	public String getDescription() {
		return "visa_b2_01_0_ST";
	}
}