package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_FEE_D extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _report_line_seq_num;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_seq_num_2;
	public AsciiField _billing_month;
	public AsciiField _acq_lic_bid;
	public AsciiField _acq_lic_bid_country_code;
	public AsciiField _acquirer_identifier;
	public AsciiField _acq_pcr_id;
	public AsciiField _card_acceptor_id;
	public AsciiField _merchant_name;
	public AsciiField _terminal_id;
	public AsciiField _transaction_date;
	public AsciiField _transaction_id;
	public AsciiField _payment_acct_num_of_tkn_id;
	public AsciiField _transaction_amount;
	public AsciiField _pos_entry_mode;
	public AsciiField _auth_response_code;
	public AsciiField _fee_reason_code;
	public AsciiField _cross_border_indicator;
	public AsciiField _billable_indicator;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] report_line_seq_num; // [1] <open=suppress, name="report_line_seq_num">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [7] <open=suppress, name="report_identifier">;
	public char[] report_line_seq_num_2; // [8] <open=suppress, name="report_line_seq_num_2">;
	public char[] billing_month; // [6] <open=suppress, name="billing_month">;
	public char[] acq_lic_bid; // [8] <open=suppress, name="acq_lic_bid">;
	public char[] acq_lic_bid_country_code; // [3] <open=suppress, name="acq_lic_bid_country_code">;
	public char[] acquirer_identifier; // [6] <open=suppress, name="acquirer_identifier">;
	public char[] acq_pcr_id; // [4] <open=suppress, name="acq_pcr_id">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] terminal_id; // [8] <open=suppress, name="terminal_id">;
	public char[] transaction_date; // [6] <open=suppress, name="transaction_date">;
	public char[] transaction_id; // [15] <open=suppress, name="transaction_id">;
	public char[] payment_acct_num_of_tkn_id; // [16] <open=suppress, name="payment_acct_num_of_tkn_id">;
	public char[] transaction_amount; // [12] <open=suppress, name="transaction_amount">;
	public char[] pos_entry_mode; // [2] <open=suppress, name="pos_entry_mode">;
	public char[] auth_response_code; // [2] <open=suppress, name="auth_response_code">;
	public char[] fee_reason_code; // [4] <open=suppress, name="fee_reason_code">;
	public char[] cross_border_indicator; // [1] <open=suppress, name="cross_border_indicator">;
	public char[] billable_indicator; // [1] <open=suppress, name="billable_indicator">;
	public char[] reserved; // [3] <open=suppress, name="reserved">;

	public visa_v2_33_0_FEE_D(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_FEE_D(String content) {
		super(content);
	}

	public visa_v2_33_0_FEE_D() {
		super();
	}

	public visa_v2_33_0_FEE_D(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct{;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("report_line_seq_num", 1);
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("report_identifier", 7);
		addElement("report_line_seq_num_2", 8);
		addElement("billing_month", 6);
		addElement("acq_lic_bid", 8);
		addElement("acq_lic_bid_country_code", 3);
		addElement("acquirer_identifier", 6);
		addElement("acq_pcr_id", 4);
		addElement("card_acceptor_id", 15);
		addElement("merchant_name", 25);
		addElement("terminal_id", 8);
		addElement("transaction_date", 6);
		addElement("transaction_id", 15);
		addElement("payment_acct_num_of_tkn_id", 16);
		addElement("transaction_amount", 12);
		addElement("pos_entry_mode", 2);
		addElement("auth_response_code", 2);
		addElement("fee_reason_code", 4);
		addElement("cross_border_indicator", 1);
		addElement("billable_indicator", 1);
		addElement("reserved", 3);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		report_line_seq_num = getElement("report_line_seq_num");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		report_identifier = getElement("report_identifier");
		report_line_seq_num_2 = getElement("report_line_seq_num_2");
		billing_month = getElement("billing_month");
		acq_lic_bid = getElement("acq_lic_bid");
		acq_lic_bid_country_code = getElement("acq_lic_bid_country_code");
		acquirer_identifier = getElement("acquirer_identifier");
		acq_pcr_id = getElement("acq_pcr_id");
		card_acceptor_id = getElement("card_acceptor_id");
		merchant_name = getElement("merchant_name");
		terminal_id = getElement("terminal_id");
		transaction_date = getElement("transaction_date");
		transaction_id = getElement("transaction_id");
		payment_acct_num_of_tkn_id = getElement("payment_acct_num_of_tkn_id");
		transaction_amount = getElement("transaction_amount");
		pos_entry_mode = getElement("pos_entry_mode");
		auth_response_code = getElement("auth_response_code");
		fee_reason_code = getElement("fee_reason_code");
		cross_border_indicator = getElement("cross_border_indicator");
		billable_indicator = getElement("billable_indicator");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_FEE_D_ST";
	}
}