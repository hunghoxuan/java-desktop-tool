package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class ARN extends AsciiMessage {
	public AsciiField _ARN_01;
	public AsciiField _ARN_02;
	public AsciiField _ARN_03;
	public AsciiField _ARN_04;
	public AsciiField _ARN_05;
	public char[] trans_code;
	public char[] ARN_01; // [1] <open=suppress, name="01 - Mixed Use">;
	public char[] ARN_02; // [6] <open=suppress, name="02 - Acquirer's Bin">;
	public char[] ARN_03; // [4] <open=suppress, name="03 - Julian Processing Date YDDD">;
	public char[] ARN_04; // [11] <open=suppress, name="04 - Acquirer's Sequence Number">;
	public char[] ARN_05; // [1] <open=suppress, name="05 - Check Digit">;

	public ARN(int offset, String content) {
		super(offset, content);
	}

	public ARN(String content) {
		super(content);
	}

	public ARN() {
		super();
	}

	public ARN(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("ARN_01", 1);
		addElement("ARN_02", 6);
		addElement("ARN_03", 4);
		addElement("ARN_04", 11);
		addElement("ARN_05", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		ARN_01 = getElement("ARN_01");
		ARN_02 = getElement("ARN_02");
		ARN_03 = getElement("ARN_03");
		ARN_04 = getElement("ARN_04");
		ARN_05 = getElement("ARN_05");

	}

	@Override
	public String getDescription() {
		return "";
	}
}