package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_90 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _processing_bin;
	public AsciiField _processing_date;
	public AsciiField _reserved_1;
	public AsciiField _settlement_date;
	public AsciiField _reserved_2;
	public AsciiField _release_number;
	public AsciiField _test_option;
	public AsciiField _reserved_3;
	public AsciiField _security_code;
	public AsciiField _reserved_4;
	public AsciiField _file_id;
	public AsciiField _reserved_5;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] processing_bin; // [6] <open=suppress, name="processing_bin">;
	public char[] processing_date; // [5] <open=suppress, name="processing_date">;
	public char[] reserved_1; // [6] <open=suppress, name="reserved_1">;
	public char[] settlement_date; // [5] <open=suppress, name="settlement_date">;
	public char[] reserved_2; // [2] <open=suppress, name="reserved_2">;
	public char[] release_number; // [3] <open=suppress, name="release_number">;
	public char[] test_option; // [4] <open=suppress, name="test_option">;
	public char[] reserved_3; // [29] <open=suppress, name="reserved_3">;
	public char[] security_code; // [8] <open=suppress, name="security_code">;
	public char[] reserved_4; // [6] <open=suppress, name="reserved_4">;
	public char[] file_id; // [3] <open=suppress, name="file_id">;
	public char[] reserved_5; // [89] <open=suppress, name="reserved_5">;

	public visa_b2_90(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_90(String content) {
		super(content);
	}

	public visa_b2_90() {
		super();
	}

	public visa_b2_90(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("processing_bin", 6);
		addElement("processing_date", 5);
		addElement("reserved_1", 6);
		addElement("settlement_date", 5);
		addElement("reserved_2", 2);
		addElement("release_number", 3);
		addElement("test_option", 4);
		addElement("reserved_3", 29);
		addElement("security_code", 8);
		addElement("reserved_4", 6);
		addElement("file_id", 3);
		addElement("reserved_5", 89);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		processing_bin = getElement("processing_bin");
		processing_date = getElement("processing_date");
		reserved_1 = getElement("reserved_1");
		settlement_date = getElement("settlement_date");
		reserved_2 = getElement("reserved_2");
		release_number = getElement("release_number");
		test_option = getElement("test_option");
		reserved_3 = getElement("reserved_3");
		security_code = getElement("security_code");
		reserved_4 = getElement("reserved_4");
		file_id = getElement("file_id");
		reserved_5 = getElement("reserved_5");

	}

	@Override
	public String getDescription() {
		return "visa_b2_90_ST";
	}
}