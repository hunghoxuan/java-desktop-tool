package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_10_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _reason_code;
	public AsciiField _country_code;
	public AsciiField _event_date;
	public AsciiField _account_number;
	public AsciiField _account_extension;
	public AsciiField _destination_amount;
	public AsciiField _destination_currency;
	public AsciiField _source_amount;
	public AsciiField _source_currency;
	public AsciiField _message_text;
	public AsciiField _settlement_flag;
	public AsciiField _transaction_id;
	public AsciiField _Funding_Source;
	public AsciiField _central_proc_date;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] reason_code; // [4] <open=suppress, name="reason_code">;
	public char[] country_code; // [3] <open=suppress, name="country_code">;
	public char[] event_date; // [4] <open=suppress, name="event_date">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] account_extension; // [3] <open=suppress, name="account_extension">;
	public char[] destination_amount; // [12] <open=suppress, name="destination_amount">;
	public char[] destination_currency; // [3] <open=suppress, name="destination_currency">;
	public char[] source_amount; // [12] <open=suppress, name="source_amount">;
	public char[] source_currency; // [3] <open=suppress, name="source_currency">;
	public char[] message_text; // [70] <open=suppress, name="message_text">;
	public char[] settlement_flag; // [1] <open=suppress, name="settlement_flag">;
	public char[] transaction_id; // [15] <open=suppress, name="transaction_id">;
	public char[] Funding_Source; // [1] <open=suppress, name="Funding_Source">;
	public char[] central_proc_date; // [4] <open=suppress, name="central_proc_date">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_10_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_10_0(String content) {
		super(content);
	}

	public visa_b2_10_0() {
		super();
	}

	public visa_b2_10_0(int ifReturn) {
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
		addElement("reason_code", 4);
		addElement("country_code", 3);
		addElement("event_date", 4);
		addElement("account_number", 16);
		addElement("account_extension", 3);
		addElement("destination_amount", 12);
		addElement("destination_currency", 3);
		addElement("source_amount", 12);
		addElement("source_currency", 3);
		addElement("message_text", 70);
		addElement("settlement_flag", 1);
		addElement("transaction_id", 15);
		addElement("Funding_Source", 1);
		addElement("central_proc_date", 4);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		reason_code = getElement("reason_code");
		country_code = getElement("country_code");
		event_date = getElement("event_date");
		account_number = getElement("account_number");
		account_extension = getElement("account_extension");
		destination_amount = getElement("destination_amount");
		destination_currency = getElement("destination_currency");
		source_amount = getElement("source_amount");
		source_currency = getElement("source_currency");
		message_text = getElement("message_text");
		settlement_flag = getElement("settlement_flag");
		transaction_id = getElement("transaction_id");
		Funding_Source = getElement("Funding_Source");
		central_proc_date = getElement("central_proc_date");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_10_0_ST";
	}
}