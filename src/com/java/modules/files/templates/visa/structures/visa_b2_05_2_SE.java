package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_2_SE extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _country_code;
	public AsciiField _national_tax;
	public AsciiField _reserved_2;
	public AsciiField _payment_id;
	public AsciiField _national_merchant_id;
	public AsciiField _merchant_name;
	public AsciiField _merchant_city;
	public AsciiField _department_id;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] country_code; // [3] <open=suppress, name="country_code">;
	public char[] national_tax; // [12] <open=suppress, name="national_tax">;
	public char[] reserved_2; // [1] <open=suppress, name="reserved_2">;
	public char[] payment_id; // [2] <open=suppress, name="payment_id">;
	public char[] national_merchant_id; // [15] <open=suppress, name="national_merchant_id">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_city; // [13] <open=suppress, name="merchant_city">;
	public char[] department_id; // [81] <open=suppress, name="department_id">;

	public visa_b2_05_2_SE(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_2_SE(String content) {
		super(content);
	}

	public visa_b2_05_2_SE() {
		super();
	}

	public visa_b2_05_2_SE(int ifReturn) {
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
		addElement("reserved_1", 12);
		addElement("country_code", 3);
		addElement("national_tax", 12);
		addElement("reserved_2", 1);
		addElement("payment_id", 2);
		addElement("national_merchant_id", 15);
		addElement("merchant_name", 25);
		addElement("merchant_city", 13);
		addElement("department_id", 81);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		country_code = getElement("country_code");
		national_tax = getElement("national_tax");
		reserved_2 = getElement("reserved_2");
		payment_id = getElement("payment_id");
		national_merchant_id = getElement("national_merchant_id");
		merchant_name = getElement("merchant_name");
		merchant_city = getElement("merchant_city");
		department_id = getElement("department_id");

	}

	@Override
	public String getDescription() {
		return "TCR 2 - SE";
	}
}