package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_46_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _currency_table_date;
	public AsciiField _first_count;
	public AsciiField _second_count;
	public AsciiField _first_amount;
	public AsciiField _first_amount_sign;
	public AsciiField _second_amount;
	public AsciiField _second_amount_sign;
	public AsciiField _third_amount;
	public AsciiField _third_amount_sign;
	public AsciiField _fourth_amount;
	public AsciiField _fourth_amount_sign;
	public AsciiField _fifth_amount;
	public AsciiField _fitth_amount_sign;
	public AsciiField _reverserd;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] currency_table_date; // [7] <open=suppress, name="currency_table_date">;
	public char[] first_count; // [15] <open=suppress, name="first_count">;
	public char[] second_count; // [15] <open=suppress, name="second_count">;
	public char[] first_amount; // [15] <open=suppress, name="first_amount">;
	public char[] first_amount_sign; // [2] <open=suppress, name="first_amount_sign">;
	public char[] second_amount; // [15] <open=suppress, name="second_amount">;
	public char[] second_amount_sign; // [2] <open=suppress, name="second_amount_sign">;
	public char[] third_amount; // [15] <open=suppress, name="third_amount">;
	public char[] third_amount_sign; // [2] <open=suppress, name="third_amount_sign">;
	public char[] fourth_amount; // [15] <open=suppress, name="fourth_amount">;
	public char[] fourth_amount_sign; // [2] <open=suppress, name="fourth_amount_sign">;
	public char[] fifth_amount; // [15] <open=suppress, name="fifth_amount">;
	public char[] fitth_amount_sign; // [2] <open=suppress, name="fitth_amount_sign">;
	public char[] reverserd; // [42] <open=suppress, name="reverserd">;

	public visa_b2_46_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_46_1(String content) {
		super(content);
	}

	public visa_b2_46_1() {
		super();
	}

	public visa_b2_46_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("currency_table_date", 7);
		addElement("first_count", 15);
		addElement("second_count", 15);
		addElement("first_amount", 15);
		addElement("first_amount_sign", 2);
		addElement("second_amount", 15);
		addElement("second_amount_sign", 2);
		addElement("third_amount", 15);
		addElement("third_amount_sign", 2);
		addElement("fourth_amount", 15);
		addElement("fourth_amount_sign", 2);
		addElement("fifth_amount", 15);
		addElement("fitth_amount_sign", 2);
		addElement("reverserd", 42);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		currency_table_date = getElement("currency_table_date");
		first_count = getElement("first_count");
		second_count = getElement("second_count");
		first_amount = getElement("first_amount");
		first_amount_sign = getElement("first_amount_sign");
		second_amount = getElement("second_amount");
		second_amount_sign = getElement("second_amount_sign");
		third_amount = getElement("third_amount");
		third_amount_sign = getElement("third_amount_sign");
		fourth_amount = getElement("fourth_amount");
		fourth_amount_sign = getElement("fourth_amount_sign");
		fifth_amount = getElement("fifth_amount");
		fitth_amount_sign = getElement("fitth_amount_sign");
		reverserd = getElement("reverserd");

	}

	@Override
	public String getDescription() {
		return "visa_b2_46_1_ST";
	}
}