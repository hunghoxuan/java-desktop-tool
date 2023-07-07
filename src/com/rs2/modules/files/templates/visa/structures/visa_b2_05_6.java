package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_6 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _local_tax;
	public AsciiField _local_tax_incl;
	public AsciiField _national_tax;
	public AsciiField _national_tax_incl;
	public AsciiField _merchant_vat_num;
	public AsciiField _customer_vat_num;
	public AsciiField _reserved_1;
	public AsciiField _summary_commod_code;
	public AsciiField _other_tax;
	public AsciiField _message_id;
	public AsciiField _time_of_purchase;
	public AsciiField _customer_code;
	public AsciiField _product_code_1;
	public AsciiField _product_code_2;
	public AsciiField _product_code_3;
	public AsciiField _product_code_4;
	public AsciiField _product_code_5;
	public AsciiField _product_code_6;
	public AsciiField _product_code_7;
	public AsciiField _product_code_8;
	public AsciiField _merchant_postal_code;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] local_tax; // [12] <open=suppress, name="local_tax">;
	public char[] local_tax_incl; // [1] <open=suppress, name="local_tax_incl">;
	public char[] national_tax; // [12] <open=suppress, name="national_tax">;
	public char[] national_tax_incl; // [1] <open=suppress, name="national_tax_incl">;
	public char[] merchant_vat_num; // [20] <open=suppress, name="merchant_vat_num">;
	public char[] customer_vat_num; // [13] <open=suppress, name="customer_vat_num">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] summary_commod_code; // [4] <open=suppress, name="summary_commod_code">;
	public char[] other_tax; // [12] <open=suppress, name="other_tax">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] time_of_purchase; // [4] <open=suppress, name="time_of_purchase">;
	public char[] customer_code; // [17] <open=suppress, name="customer_code">;
	public char[] product_code_1; // [2] <open=suppress, name="product_code_1">;
	public char[] product_code_2; // [2] <open=suppress, name="product_code_2">;
	public char[] product_code_3; // [2] <open=suppress, name="product_code_3">;
	public char[] product_code_4; // [2] <open=suppress, name="product_code_4">;
	public char[] product_code_5; // [2] <open=suppress, name="product_code_5">;
	public char[] product_code_6; // [2] <open=suppress, name="product_code_6">;
	public char[] product_code_7; // [2] <open=suppress, name="product_code_7">;
	public char[] product_code_8; // [2] <open=suppress, name="product_code_8">;
	public char[] merchant_postal_code; // [11] <open=suppress, name="merchant_postal_code">;
	public char[] reserved_2; // [14] <open=suppress, name="reserved_2">;

	public visa_b2_05_6(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_6(String content) {
		super(content);
	}

	public visa_b2_05_6() {
		super();
	}

	public visa_b2_05_6(int ifReturn) {
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
		addElement("local_tax", 12);
		addElement("local_tax_incl", 1);
		addElement("national_tax", 12);
		addElement("national_tax_incl", 1);
		addElement("merchant_vat_num", 20);
		addElement("customer_vat_num", 13);
		addElement("reserved_1", 12);
		addElement("summary_commod_code", 4);
		addElement("other_tax", 12);
		addElement("message_id", 15);
		addElement("time_of_purchase", 4);
		addElement("customer_code", 17);
		addElement("product_code_1", 2);
		addElement("product_code_2", 2);
		addElement("product_code_3", 2);
		addElement("product_code_4", 2);
		addElement("product_code_5", 2);
		addElement("product_code_6", 2);
		addElement("product_code_7", 2);
		addElement("product_code_8", 2);
		addElement("merchant_postal_code", 11);
		addElement("reserved_2", 14);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		local_tax = getElement("local_tax");
		local_tax_incl = getElement("local_tax_incl");
		national_tax = getElement("national_tax");
		national_tax_incl = getElement("national_tax_incl");
		merchant_vat_num = getElement("merchant_vat_num");
		customer_vat_num = getElement("customer_vat_num");
		reserved_1 = getElement("reserved_1");
		summary_commod_code = getElement("summary_commod_code");
		other_tax = getElement("other_tax");
		message_id = getElement("message_id");
		time_of_purchase = getElement("time_of_purchase");
		customer_code = getElement("customer_code");
		product_code_1 = getElement("product_code_1");
		product_code_2 = getElement("product_code_2");
		product_code_3 = getElement("product_code_3");
		product_code_4 = getElement("product_code_4");
		product_code_5 = getElement("product_code_5");
		product_code_6 = getElement("product_code_6");
		product_code_7 = getElement("product_code_7");
		product_code_8 = getElement("product_code_8");
		merchant_postal_code = getElement("merchant_postal_code");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_05_6_ST";
	}
}