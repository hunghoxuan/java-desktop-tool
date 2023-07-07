package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_9 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _reserved_1;
	public AsciiField _run_date;
	public AsciiField _batch_number;
	public AsciiField _batch_sequence;
	public AsciiField _reserved_2;
	public AsciiField _source_amount;
	public AsciiField _source_currency;
	public AsciiField _settlement_flag;
	public AsciiField _reserved_3;
	public AsciiField _validation_code_1;
	public AsciiField _validation_code_2;
	public AsciiField _validation_code_3;
	public AsciiField _validation_code_4;
	public AsciiField _validation_code_5;
	public AsciiField _validation_code_6;
	public AsciiField _validation_code_7;
	public AsciiField _validation_code_8;
	public AsciiField _validation_code_9;
	public AsciiField _validation_code_10;
	public AsciiField _reserved_4;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] reserved_1; // [4] <open=suppress, name="reserved_1">;
	public char[] run_date; // [5] <open=suppress, name="run_date">;
	public char[] batch_number; // [6] <open=suppress, name="batch_number">;
	public char[] batch_sequence; // [4] <open=suppress, name="batch_sequence">;
	public char[] reserved_2; // [3] <open=suppress, name="reserved_2">;
	public char[] source_amount; // [12] <open=suppress, name="source_amount">;
	public char[] source_currency; // [3] <open=suppress, name="source_currency">;
	public char[] settlement_flag; // [1] <open=suppress, name="settlement_flag">;
	public char[] reserved_3; // [13] <open=suppress, name="reserved_3">;
	public char[] validation_code_1; // [4] <open=suppress, name="validation_code_1">;
	public char[] validation_code_2; // [4] <open=suppress, name="validation_code_2">;
	public char[] validation_code_3; // [4] <open=suppress, name="validation_code_3">;
	public char[] validation_code_4; // [4] <open=suppress, name="validation_code_4">;
	public char[] validation_code_5; // [4] <open=suppress, name="validation_code_5">;
	public char[] validation_code_6; // [4] <open=suppress, name="validation_code_6">;
	public char[] validation_code_7; // [4] <open=suppress, name="validation_code_7">;
	public char[] validation_code_8; // [4] <open=suppress, name="validation_code_8">;
	public char[] validation_code_9; // [4] <open=suppress, name="validation_code_9">;
	public char[] validation_code_10; // [4] <open=suppress, name="validation_code_10">;
	public char[] reserved_4; // [61] <open=suppress, name="reserved_4">;

	public visa_b2_05_9(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_9(String content) {
		super(content);
	}

	public visa_b2_05_9() {
		super();
	}

	public visa_b2_05_9(int ifReturn) {
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
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("reserved_1", 4);
		addElement("run_date", 5);
		addElement("batch_number", 6);
		addElement("batch_sequence", 4);
		addElement("reserved_2", 3);
		addElement("source_amount", 12);
		addElement("source_currency", 3);
		addElement("settlement_flag", 1);
		addElement("reserved_3", 13);
		addElement("validation_code_1", 4);
		addElement("validation_code_2", 4);
		addElement("validation_code_3", 4);
		addElement("validation_code_4", 4);
		addElement("validation_code_5", 4);
		addElement("validation_code_6", 4);
		addElement("validation_code_7", 4);
		addElement("validation_code_8", 4);
		addElement("validation_code_9", 4);
		addElement("validation_code_10", 4);
		addElement("reserved_4", 61);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		reserved_1 = getElement("reserved_1");
		run_date = getElement("run_date");
		batch_number = getElement("batch_number");
		batch_sequence = getElement("batch_sequence");
		reserved_2 = getElement("reserved_2");
		source_amount = getElement("source_amount");
		source_currency = getElement("source_currency");
		settlement_flag = getElement("settlement_flag");
		reserved_3 = getElement("reserved_3");
		validation_code_1 = getElement("validation_code_1");
		validation_code_2 = getElement("validation_code_2");
		validation_code_3 = getElement("validation_code_3");
		validation_code_4 = getElement("validation_code_4");
		validation_code_5 = getElement("validation_code_5");
		validation_code_6 = getElement("validation_code_6");
		validation_code_7 = getElement("validation_code_7");
		validation_code_8 = getElement("validation_code_8");
		validation_code_9 = getElement("validation_code_9");
		validation_code_10 = getElement("validation_code_10");
		reserved_4 = getElement("reserved_4");

	}

	@Override
	public String getDescription() {
		return "visa_b2_05_9_ST";
	}
}