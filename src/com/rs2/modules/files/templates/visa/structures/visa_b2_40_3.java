package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_3 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _card_mailing_date;
	public AsciiField _card_mailing_city;
	public AsciiField _card_mailing_state;
	public AsciiField _paym_brand_product;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] card_mailing_date; // [6] <open=suppress, name="card_mailing_date">;
	public char[] card_mailing_city; // [28] <open=suppress, name="card_mailing_city">;
	public char[] card_mailing_state; // [2] <open=suppress, name="card_mailing_state">;
	public char[] paym_brand_product; // [2] <open=suppress, name="paym_brand_product">;
	public char[] reserved; // [126] <open=suppress, name="reserved">;

	public visa_b2_40_3(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_3(String content) {
		super(content);
	}

	public visa_b2_40_3() {
		super();
	}

	public visa_b2_40_3(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("card_mailing_date", 6);
		addElement("card_mailing_city", 28);
		addElement("card_mailing_state", 2);
		addElement("paym_brand_product", 2);
		addElement("reserved", 126);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		card_mailing_date = getElement("card_mailing_date");
		card_mailing_city = getElement("card_mailing_city");
		card_mailing_state = getElement("card_mailing_state");
		paym_brand_product = getElement("paym_brand_product");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_3_ST";
	}
}