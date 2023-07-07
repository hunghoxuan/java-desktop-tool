package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_FEE_S extends AsciiMessage {
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
	public AsciiField _acquirer_id_name;
	public AsciiField _acq_pcr_id;
	public AsciiField _card_acceptor_id;
	public AsciiField _merchant_name;
	public AsciiField _merchant_verif_value;
	public AsciiField _merchant_category_code;
	public AsciiField _domestic_tran_count;
	public AsciiField _domestic_billable_count;
	public AsciiField _cross_border_tran_count;
	public AsciiField _cross_border_billable_count;
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
	public char[] acquirer_id_name; // [25] <open=suppress, name="acquirer_id_name">;
	public char[] acq_pcr_id; // [4] <open=suppress, name="acq_pcr_id">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_verif_value; // [10] <open=suppress, name="merchant_verif_value">;
	public char[] merchant_category_code; // [4] <open=suppress, name="merchant_category_code">;
	public char[] domestic_tran_count; // [7] <open=suppress, name="domestic_tran_count">;
	public char[] domestic_billable_count; // [7] <open=suppress, name="domestic_billable_count">;
	public char[] cross_border_tran_count; // [7] <open=suppress, name="cross_border_tran_count">;
	public char[] cross_border_billable_count; // [7] <open=suppress, name="cross_border_billable_count">;
	public char[] reserved; // [3] <open=suppress, name="reserved">;

	public visa_v2_33_0_FEE_S(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_FEE_S(String content) {
		super(content);
	}

	public visa_v2_33_0_FEE_S() {
		super();
	}

	public visa_v2_33_0_FEE_S(int ifReturn) {
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
		addElement("acquirer_id_name", 25);
		addElement("acq_pcr_id", 4);
		addElement("card_acceptor_id", 15);
		addElement("merchant_name", 25);
		addElement("merchant_verif_value", 10);
		addElement("merchant_category_code", 4);
		addElement("domestic_tran_count", 7);
		addElement("domestic_billable_count", 7);
		addElement("cross_border_tran_count", 7);
		addElement("cross_border_billable_count", 7);
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
		acquirer_id_name = getElement("acquirer_id_name");
		acq_pcr_id = getElement("acq_pcr_id");
		card_acceptor_id = getElement("card_acceptor_id");
		merchant_name = getElement("merchant_name");
		merchant_verif_value = getElement("merchant_verif_value");
		merchant_category_code = getElement("merchant_category_code");
		domestic_tran_count = getElement("domestic_tran_count");
		domestic_billable_count = getElement("domestic_billable_count");
		cross_border_tran_count = getElement("cross_border_tran_count");
		cross_border_billable_count = getElement("cross_border_billable_count");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_FEE_S_ST";
	}
}