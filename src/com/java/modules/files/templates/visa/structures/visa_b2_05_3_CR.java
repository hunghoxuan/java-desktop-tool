package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_CR extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _source_of_funds;
	public AsciiField _payment_reversal_reason_code;
	public AsciiField _sender_reference_number;
	public AsciiField _sender_account_number;
	public AsciiField _sender_name;
	public AsciiField _sender_address;
	public AsciiField _sender_city;
	public AsciiField _sender_state;
	public AsciiField _sender_country;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] source_of_funds; // [1] <open=suppress, name="source_of_funds">;
	public char[] payment_reversal_reason_code; // [2] <open=suppress, name="payment_reversal_reason_code">;
	public char[] sender_reference_number; // [16] <open=suppress, name="sender_reference_number">;
	public char[] sender_account_number; // [34] <open=suppress, name="sender_account_number">;
	public char[] sender_name; // [30] <open=suppress, name="sender_name">;
	public char[] sender_address; // [35] <open=suppress, name="sender_address">;
	public char[] sender_city; // [25] <open=suppress, name="sender_city">;
	public char[] sender_state; // [2] <open=suppress, name="sender_state">;
	public char[] sender_country; // [3] <open=suppress, name="sender_country">;
	public char[] reserved_2; // [2] <open=suppress, name="reserved_2">;

	public visa_b2_05_3_CR(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_CR(String content) {
		super(content);
	}

	public visa_b2_05_3_CR() {
		super();
	}

	public visa_b2_05_3_CR(int ifReturn) {
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
		addElement("reserved_1", 11);
		addElement("fast_funds_indicator", 1);
		addElement("bus_format_code", 2);
		addElement("source_of_funds", 1);
		addElement("payment_reversal_reason_code", 2);
		addElement("sender_reference_number", 16);
		addElement("sender_account_number", 34);
		addElement("sender_name", 30);
		addElement("sender_address", 35);
		addElement("sender_city", 25);
		addElement("sender_state", 2);
		addElement("sender_country", 3);
		addElement("reserved_2", 2);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		fast_funds_indicator = getElement("fast_funds_indicator");
		bus_format_code = getElement("bus_format_code");
		source_of_funds = getElement("source_of_funds");
		payment_reversal_reason_code = getElement("payment_reversal_reason_code");
		sender_reference_number = getElement("sender_reference_number");
		sender_account_number = getElement("sender_account_number");
		sender_name = getElement("sender_name");
		sender_address = getElement("sender_address");
		sender_city = getElement("sender_city");
		sender_state = getElement("sender_state");
		sender_country = getElement("sender_country");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - CR";
	}
}