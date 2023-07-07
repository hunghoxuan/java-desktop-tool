package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_AN extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _an_ticket_document_num;
	public AsciiField _an_service_category_1;
	public AsciiField _an_service_sub_category_1;
	public AsciiField _an_service_category_2;
	public AsciiField _an_service_sub_category_2;
	public AsciiField _an_service_category_3;
	public AsciiField _an_service_sub_category_3;
	public AsciiField _an_service_category_4;
	public AsciiField _an_service_sub_category_4;
	public AsciiField _passenger_name;
	public AsciiField _connection_with_ticket_num;
	public AsciiField _credit_reason_ind;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] an_ticket_document_num; // [15] <open=suppress, name="an_ticket_document_num">;
	public char[] an_service_category_1; // [4] <open=suppress, name="an_service_category_1">;
	public char[] an_service_sub_category_1; // [4] <open=suppress, name="an_service_sub_category_1">;
	public char[] an_service_category_2; // [4] <open=suppress, name="an_service_category_2">;
	public char[] an_service_sub_category_2; // [4] <open=suppress, name="an_service_sub_category_2">;
	public char[] an_service_category_3; // [4] <open=suppress, name="an_service_category_3">;
	public char[] an_service_sub_category_3; // [4] <open=suppress, name="an_service_sub_category_3">;
	public char[] an_service_category_4; // [4] <open=suppress, name="an_service_category_4">;
	public char[] an_service_sub_category_4; // [4] <open=suppress, name="an_service_sub_category_4">;
	public char[] passenger_name; // [20] <open=suppress, name="passenger_name">;
	public char[] connection_with_ticket_num; // [15] <open=suppress, name="connection_with_ticket_num">;
	public char[] credit_reason_ind; // [1] <open=suppress, name="credit_reason_ind">;
	public char[] reserved; // [67] <open=suppress, name="reserved">;

	public visa_b2_05_3_AN(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_AN(String content) {
		super(content);
	}

	public visa_b2_05_3_AN() {
		super();
	}

	public visa_b2_05_3_AN(int ifReturn) {
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
		addElement("an_ticket_document_num", 15);
		addElement("an_service_category_1", 4);
		addElement("an_service_sub_category_1", 4);
		addElement("an_service_category_2", 4);
		addElement("an_service_sub_category_2", 4);
		addElement("an_service_category_3", 4);
		addElement("an_service_sub_category_3", 4);
		addElement("an_service_category_4", 4);
		addElement("an_service_sub_category_4", 4);
		addElement("passenger_name", 20);
		addElement("connection_with_ticket_num", 15);
		addElement("credit_reason_ind", 1);
		addElement("reserved", 67);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		fast_funds_indicator = getElement("fast_funds_indicator");
		bus_format_code = getElement("bus_format_code");
		an_ticket_document_num = getElement("an_ticket_document_num");
		an_service_category_1 = getElement("an_service_category_1");
		an_service_sub_category_1 = getElement("an_service_sub_category_1");
		an_service_category_2 = getElement("an_service_category_2");
		an_service_sub_category_2 = getElement("an_service_sub_category_2");
		an_service_category_3 = getElement("an_service_category_3");
		an_service_sub_category_3 = getElement("an_service_sub_category_3");
		an_service_category_4 = getElement("an_service_category_4");
		an_service_sub_category_4 = getElement("an_service_sub_category_4");
		passenger_name = getElement("passenger_name");
		connection_with_ticket_num = getElement("connection_with_ticket_num");
		credit_reason_ind = getElement("credit_reason_ind");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - AN";
	}
}