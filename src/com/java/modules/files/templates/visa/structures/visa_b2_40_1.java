package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _cholder_postcode;
	public AsciiField _market_segmt_code;
	public AsciiField _locator_number;
	public AsciiField _soc_security_nr;
	public AsciiField _cholder_last_name;
	public AsciiField _cholder_firs_tname;
	public AsciiField _cholder_mid_initial;
	public AsciiField _cholder_addr2;
	public AsciiField _cholder_addr1;
	public AsciiField _cholder_city;
	public AsciiField _cholder_state;
	public AsciiField _cholder_phone;
	public AsciiField _case_number;
	public AsciiField _arrest_code;
	public AsciiField _mailed_from_postcode;
	public AsciiField _card_valid_from;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] cholder_postcode; // [10] <open=suppress, name="cholder_postcode">;
	public char[] market_segmt_code; // [1] <open=suppress, name="market_segmt_code">;
	public char[] locator_number; // [11] <open=suppress, name="locator_number">;
	public char[] soc_security_nr; // [9] <open=suppress, name="soc_security_nr">;
	public char[] cholder_last_name; // [25] <open=suppress, name="cholder_last_name">;
	public char[] cholder_firs_tname; // [15] <open=suppress, name="cholder_firs_tname">;
	public char[] cholder_mid_initial; // [1] <open=suppress, name="cholder_mid_initial">;
	public char[] cholder_addr2; // [21] <open=suppress, name="cholder_addr2">;
	public char[] cholder_addr1; // [23] <open=suppress, name="cholder_addr1">;
	public char[] cholder_city; // [14] <open=suppress, name="cholder_city">;
	public char[] cholder_state; // [2] <open=suppress, name="cholder_state">;
	public char[] cholder_phone; // [10] <open=suppress, name="cholder_phone">;
	public char[] case_number; // [7] <open=suppress, name="case_number">;
	public char[] arrest_code; // [1] <open=suppress, name="arrest_code">;
	public char[] mailed_from_postcode; // [10] <open=suppress, name="mailed_from_postcode">;
	public char[] card_valid_from; // [4] <open=suppress, name="card_valid_from">;

	public visa_b2_40_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_1(String content) {
		super(content);
	}

	public visa_b2_40_1() {
		super();
	}

	public visa_b2_40_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("cholder_postcode", 10);
		addElement("market_segmt_code", 1);
		addElement("locator_number", 11);
		addElement("soc_security_nr", 9);
		addElement("cholder_last_name", 25);
		addElement("cholder_firs_tname", 15);
		addElement("cholder_mid_initial", 1);
		addElement("cholder_addr2", 21);
		addElement("cholder_addr1", 23);
		addElement("cholder_city", 14);
		addElement("cholder_state", 2);
		addElement("cholder_phone", 10);
		addElement("case_number", 7);
		addElement("arrest_code", 1);
		addElement("mailed_from_postcode", 10);
		addElement("card_valid_from", 4);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		cholder_postcode = getElement("cholder_postcode");
		market_segmt_code = getElement("market_segmt_code");
		locator_number = getElement("locator_number");
		soc_security_nr = getElement("soc_security_nr");
		cholder_last_name = getElement("cholder_last_name");
		cholder_firs_tname = getElement("cholder_firs_tname");
		cholder_mid_initial = getElement("cholder_mid_initial");
		cholder_addr2 = getElement("cholder_addr2");
		cholder_addr1 = getElement("cholder_addr1");
		cholder_city = getElement("cholder_city");
		cholder_state = getElement("cholder_state");
		cholder_phone = getElement("cholder_phone");
		case_number = getElement("case_number");
		arrest_code = getElement("arrest_code");
		mailed_from_postcode = getElement("mailed_from_postcode");
		card_valid_from = getElement("card_valid_from");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_1_ST";
	}
}