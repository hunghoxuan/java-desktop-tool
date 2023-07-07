package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_0_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _format_code;
	public AsciiField _account_number;
	public AsciiField _response_code;
	public AsciiField _authorization_code;
	public AsciiField _stand_in_response_code;
	public AsciiField _avs_reason_code;
	public AsciiField _trans_date_time;
	public AsciiField _transaction_amount_iss;
	public AsciiField _issuer_currency_code;
	public AsciiField _acquirer_currency_code;
	public AsciiField _billing_conversion_rate;
	public AsciiField _expiration_date;
	public AsciiField _acquiring_institution_id;
	public AsciiField _acquiring_inst_country;
	public AsciiField _message_type;
	public AsciiField _processing_code;
	public AsciiField _pos_condition_code;
	public AsciiField _merchant_type;
	public AsciiField _pos_entry_mode;
	public AsciiField _pos_entry_capability;
	public AsciiField _card_acceptor_term_id;
	public AsciiField _card_acceptor_id;
	public AsciiField _retrieval_reference_no;
	public AsciiField _reserved_1;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] format_code; // [1] <open=suppress, name="format_code">;
	public char[] account_number; // [28] <open=suppress, name="account_number">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] authorization_code; // [6] <open=suppress, name="authorization_code">;
	public char[] stand_in_response_code; // [1] <open=suppress, name="stand_in_response_code">;
	public char[] avs_reason_code; // [1] <open=suppress, name="avs_reason_code">;
	public char[] trans_date_time; // [10] <open=suppress, name="trans_date_time">;
	public char[] transaction_amount_iss; // [12] <open=suppress, name="transaction_amount_iss">;
	public char[] issuer_currency_code; // [3] <open=suppress, name="issuer_currency_code">;
	public char[] acquirer_currency_code; // [3] <open=suppress, name="acquirer_currency_code">;
	public char[] billing_conversion_rate; // [8] <open=suppress, name="billing_conversion_rate">;
	public char[] expiration_date; // [4] <open=suppress, name="expiration_date">;
	public char[] acquiring_institution_id; // [11] <open=suppress, name="acquiring_institution_id">;
	public char[] acquiring_inst_country; // [3] <open=suppress, name="acquiring_inst_country">;
	public char[] message_type; // [4] <open=suppress, name="message_type">;
	public char[] processing_code; // [4] <open=suppress, name="processing_code">;
	public char[] pos_condition_code; // [2] <open=suppress, name="pos_condition_code">;
	public char[] merchant_type; // [4] <open=suppress, name="merchant_type">;
	public char[] pos_entry_mode; // [2] <open=suppress, name="pos_entry_mode">;
	public char[] pos_entry_capability; // [4] <open=suppress, name="pos_entry_capability">;
	public char[] card_acceptor_term_id; // [8] <open=suppress, name="card_acceptor_term_id">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] retrieval_reference_no; // [12] <open=suppress, name="retrieval_reference_no">;
	public char[] reserved_1; // [3] <open=suppress, name="reserved_1">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_48_0_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_0_1(String content) {
		super(content);
	}

	public visa_b2_48_0_1() {
		super();
	}

	public visa_b2_48_0_1(int ifReturn) {
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
		addElement("format_code", 1);
		addElement("account_number", 28);
		addElement("response_code", 2);
		addElement("authorization_code", 6);
		addElement("stand_in_response_code", 1);
		addElement("avs_reason_code", 1);
		addElement("trans_date_time", 10);
		addElement("transaction_amount_iss", 12);
		addElement("issuer_currency_code", 3);
		addElement("acquirer_currency_code", 3);
		addElement("billing_conversion_rate", 8);
		addElement("expiration_date", 4);
		addElement("acquiring_institution_id", 11);
		addElement("acquiring_inst_country", 3);
		addElement("message_type", 4);
		addElement("processing_code", 4);
		addElement("pos_condition_code", 2);
		addElement("merchant_type", 4);
		addElement("pos_entry_mode", 2);
		addElement("pos_entry_capability", 4);
		addElement("card_acceptor_term_id", 8);
		addElement("card_acceptor_id", 15);
		addElement("retrieval_reference_no", 12);
		addElement("reserved_1", 3);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		format_code = getElement("format_code");
		account_number = getElement("account_number");
		response_code = getElement("response_code");
		authorization_code = getElement("authorization_code");
		stand_in_response_code = getElement("stand_in_response_code");
		avs_reason_code = getElement("avs_reason_code");
		trans_date_time = getElement("trans_date_time");
		transaction_amount_iss = getElement("transaction_amount_iss");
		issuer_currency_code = getElement("issuer_currency_code");
		acquirer_currency_code = getElement("acquirer_currency_code");
		billing_conversion_rate = getElement("billing_conversion_rate");
		expiration_date = getElement("expiration_date");
		acquiring_institution_id = getElement("acquiring_institution_id");
		acquiring_inst_country = getElement("acquiring_inst_country");
		message_type = getElement("message_type");
		processing_code = getElement("processing_code");
		pos_condition_code = getElement("pos_condition_code");
		merchant_type = getElement("merchant_type");
		pos_entry_mode = getElement("pos_entry_mode");
		pos_entry_capability = getElement("pos_entry_capability");
		card_acceptor_term_id = getElement("card_acceptor_term_id");
		card_acceptor_id = getElement("card_acceptor_id");
		retrieval_reference_no = getElement("retrieval_reference_no");
		reserved_1 = getElement("reserved_1");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_48_0_1_ST";
	}
}