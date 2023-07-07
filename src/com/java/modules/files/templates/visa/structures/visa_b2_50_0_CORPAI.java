package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_CORPAI extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _acct_number;
	public AsciiField _acct_number_ext;
	public AsciiField _passenger_name;
	public AsciiField _total_fare_amount;
	public AsciiField _total_tax_amount;
	public AsciiField _national_tax_amount;
	public AsciiField _total_fee_amount;
	public AsciiField _currency_code;
	public AsciiField _exchg_ticket_number;
	public AsciiField _exchg_ticket_amount;
	public AsciiField _travel_agency_code;
	public AsciiField _internet_indicator;
	public AsciiField _reserved;
	public AsciiField _reimburs_attrib;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] item_seq_no; // [4] <open=suppress, name="item_seq_no">;
	public char[] acct_number; // [16] <open=suppress, name="acct_number">;
	public char[] acct_number_ext; // [3] <open=suppress, name="acct_number_ext">;
	public char[] passenger_name; // [20] <open=suppress, name="passenger_name">;
	public char[] total_fare_amount; // [12] <open=suppress, name="total_fare_amount">;
	public char[] total_tax_amount; // [12] <open=suppress, name="total_tax_amount">;
	public char[] national_tax_amount; // [12] <open=suppress, name="national_tax_amount">;
	public char[] total_fee_amount; // [12] <open=suppress, name="total_fee_amount">;
	public char[] currency_code; // [3] <open=suppress, name="currency_code">;
	public char[] exchg_ticket_number; // [13] <open=suppress, name="exchg_ticket_number">;
	public char[] exchg_ticket_amount; // [12] <open=suppress, name="exchg_ticket_amount">;
	public char[] travel_agency_code; // [8] <open=suppress, name="travel_agency_code">;
	public char[] internet_indicator; // [1] <open=suppress, name="internet_indicator">;
	public char[] reserved; // [2] <open=suppress, name="reserved">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_CORPAI(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_CORPAI(String content) {
		super(content);
	}

	public visa_b2_50_0_CORPAI() {
		super();
	}

	public visa_b2_50_0_CORPAI(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("service_id", 6);
		addElement("message_id", 15);
		addElement("item_seq_no", 4);
		addElement("acct_number", 16);
		addElement("acct_number_ext", 3);
		addElement("passenger_name", 20);
		addElement("total_fare_amount", 12);
		addElement("total_tax_amount", 12);
		addElement("national_tax_amount", 12);
		addElement("total_fee_amount", 12);
		addElement("currency_code", 3);
		addElement("exchg_ticket_number", 13);
		addElement("exchg_ticket_amount", 12);
		addElement("travel_agency_code", 8);
		addElement("internet_indicator", 1);
		addElement("reserved", 2);
		addElement("reimburs_attrib", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		service_id = getElement("service_id");
		message_id = getElement("message_id");
		item_seq_no = getElement("item_seq_no");
		acct_number = getElement("acct_number");
		acct_number_ext = getElement("acct_number_ext");
		passenger_name = getElement("passenger_name");
		total_fare_amount = getElement("total_fare_amount");
		total_tax_amount = getElement("total_tax_amount");
		national_tax_amount = getElement("national_tax_amount");
		total_fee_amount = getElement("total_fee_amount");
		currency_code = getElement("currency_code");
		exchg_ticket_number = getElement("exchg_ticket_number");
		exchg_ticket_amount = getElement("exchg_ticket_amount");
		travel_agency_code = getElement("travel_agency_code");
		internet_indicator = getElement("internet_indicator");
		reserved = getElement("reserved");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_CORPAI_ST";
	}
}