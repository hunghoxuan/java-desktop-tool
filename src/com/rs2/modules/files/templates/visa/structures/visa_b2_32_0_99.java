package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_32_0_99 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _request_date;
	public AsciiField _locator_number;
	public AsciiField _nmas_seq_number;
	public AsciiField _action_indicator;
	public AsciiField _merc_additions_count;
	public AsciiField _merc_inquiries_count;
	public AsciiField _acquirer_request_count;
	public AsciiField _addition_conf_count;
	public AsciiField _inquiry_conf_count;
	public AsciiField _invalid_additions_count;
	public AsciiField _invalid_inquiries_count;
	public AsciiField _merchant_alerts_count;
	public AsciiField _retroa_merc_ale_count;
	public AsciiField _review_merc_alert_count;
	public AsciiField _nmas_response_count;
	public AsciiField _nmas_transaction_count;
	public AsciiField _base_ii_tc_32_tcrr_count;
	public AsciiField _unknown_request_count;
	public AsciiField _reserved_1;
	public AsciiField _reimbursement_att_code;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] request_date; // [6] <open=suppress, name="request_date">;
	public char[] locator_number; // [4] <open=suppress, name="locator_number">;
	public char[] nmas_seq_number; // [2] <open=suppress, name="nmas_seq_number">;
	public char[] action_indicator; // [2] <open=suppress, name="action_indicator">;
	public char[] merc_additions_count; // [7] <open=suppress, name="merc_additions_count">;
	public char[] merc_inquiries_count; // [7] <open=suppress, name="merc_inquiries_count">;
	public char[] acquirer_request_count; // [9] <open=suppress, name="acquirer_request_count">;
	public char[] addition_conf_count; // [7] <open=suppress, name="addition_conf_count">;
	public char[] inquiry_conf_count; // [7] <open=suppress, name="inquiry_conf_count">;
	public char[] invalid_additions_count; // [7] <open=suppress, name="invalid_additions_count">;
	public char[] invalid_inquiries_count; // [7] <open=suppress, name="invalid_inquiries_count">;
	public char[] merchant_alerts_count; // [7] <open=suppress, name="merchant_alerts_count">;
	public char[] retroa_merc_ale_count; // [7] <open=suppress, name="retroa_merc_ale_count">;
	public char[] review_merc_alert_count; // [7] <open=suppress, name="review_merc_alert_count">;
	public char[] nmas_response_count; // [9] <open=suppress, name="nmas_response_count">;
	public char[] nmas_transaction_count; // [10] <open=suppress, name="nmas_transaction_count">;
	public char[] base_ii_tc_32_tcrr_count; // [10] <open=suppress, name="base_ii_tc_32_tcrr_count">;
	public char[] unknown_request_count; // [9] <open=suppress, name="unknown_request_count">;
	public char[] reserved_1; // [27] <open=suppress, name="reserved_1">;
	public char[] reimbursement_att_code; // [1] <open=suppress, name="reimbursement_att_code">;

	public visa_b2_32_0_99(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_32_0_99(String content) {
		super(content);
	}

	public visa_b2_32_0_99() {
		super();
	}

	public visa_b2_32_0_99(int ifReturn) {
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
		addElement("request_date", 6);
		addElement("locator_number", 4);
		addElement("nmas_seq_number", 2);
		addElement("action_indicator", 2);
		addElement("merc_additions_count", 7);
		addElement("merc_inquiries_count", 7);
		addElement("acquirer_request_count", 9);
		addElement("addition_conf_count", 7);
		addElement("inquiry_conf_count", 7);
		addElement("invalid_additions_count", 7);
		addElement("invalid_inquiries_count", 7);
		addElement("merchant_alerts_count", 7);
		addElement("retroa_merc_ale_count", 7);
		addElement("review_merc_alert_count", 7);
		addElement("nmas_response_count", 9);
		addElement("nmas_transaction_count", 10);
		addElement("base_ii_tc_32_tcrr_count", 10);
		addElement("unknown_request_count", 9);
		addElement("reserved_1", 27);
		addElement("reimbursement_att_code", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		request_date = getElement("request_date");
		locator_number = getElement("locator_number");
		nmas_seq_number = getElement("nmas_seq_number");
		action_indicator = getElement("action_indicator");
		merc_additions_count = getElement("merc_additions_count");
		merc_inquiries_count = getElement("merc_inquiries_count");
		acquirer_request_count = getElement("acquirer_request_count");
		addition_conf_count = getElement("addition_conf_count");
		inquiry_conf_count = getElement("inquiry_conf_count");
		invalid_additions_count = getElement("invalid_additions_count");
		invalid_inquiries_count = getElement("invalid_inquiries_count");
		merchant_alerts_count = getElement("merchant_alerts_count");
		retroa_merc_ale_count = getElement("retroa_merc_ale_count");
		review_merc_alert_count = getElement("review_merc_alert_count");
		nmas_response_count = getElement("nmas_response_count");
		nmas_transaction_count = getElement("nmas_transaction_count");
		base_ii_tc_32_tcrr_count = getElement("base_ii_tc_32_tcrr_count");
		unknown_request_count = getElement("unknown_request_count");
		reserved_1 = getElement("reserved_1");
		reimbursement_att_code = getElement("reimbursement_att_code");

	}

	@Override
	public String getDescription() {
		return "TCR0 - Additional Data";
	}
}