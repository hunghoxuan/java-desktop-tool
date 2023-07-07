package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22200 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _issuer_acquirer_ind;
	public AsciiField _mvv_value;
	public AsciiField _remote_terminal_ind;
	public AsciiField _charge_ind;
	public AsciiField _product_id;
	public AsciiField _business_application_id;
	public AsciiField _source_of_funds;
	public AsciiField _product_subtype;
	public AsciiField _account_funding_source;
	public AsciiField _affliate_bin;
	public AsciiField _settlement_date;
	public AsciiField _transaction_id;
	public AsciiField _validation_code;
	public AsciiField _retrieval_reference_number;
	public AsciiField _trace_number;
	public AsciiField _batch_number;
	public AsciiField _request_message_type;
	public AsciiField _responce_code;
	public AsciiField _processing_code;
	public AsciiField _card_number;
	public AsciiField _transaction_amount;
	public AsciiField _currency_code_transaction_amt;
	public AsciiField _filler;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="report_line_sequence_number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] issuer_acquirer_ind; // [1] <open=suppress, name="issuer_acquirer_ind">;
	public char[] mvv_value; // [10] <open=suppress, name="mvv_value">;
	public char[] remote_terminal_ind; // [1] <open=suppress, name="remote_terminal_ind">;
	public char[] charge_ind; // [1] <open=suppress, name="charge_ind">;
	public char[] product_id; // [2] <open=suppress, name="product_id">;
	public char[] business_application_id; // [2] <open=suppress, name="business_application_id">;
	public char[] source_of_funds; // [1] <open=suppress, name="source_of_funds">;
	public char[] product_subtype; // [2] <open=suppress, name="product_subtype">;
	public char[] account_funding_source; // [1] <open=suppress, name="account_funding_source">;
	public char[] affliate_bin; // [10] <open=suppress, name="affliate_bin">;
	public char[] settlement_date; // [6] <open=suppress, name="settlement_date">; //1
	public char[] transaction_id; // [15] <open=suppress, name="transaction_id">; //1
	public char[] validation_code; // [4] <open=suppress, name="validation_code">;
	public char[] retrieval_reference_number; // [12] <open=suppress, name="retrieval_reference_number">;
	public char[] trace_number; // [6] <open=suppress, name="trace_number">;
	public char[] batch_number; // [4] <open=suppress, name="batch_number">;
	public char[] request_message_type; // [4] <open=suppress, name="request_message_type">;
	public char[] responce_code; // [2] <open=suppress, name="responce_code">;
	public char[] processing_code; // [6] <open=suppress, name="processing_code">;
	public char[] card_number; // [19] <open=suppress, name="card_number">;
	public char[] transaction_amount; // [12] <open=suppress, name="transaction_amount">;
	public char[] currency_code_transaction_amt; // [3] <open=suppress, name="currency_code_transaction_amt">;
	public char[] filler; // [3] <open=suppress, name="filler">; //0
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22200(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22200(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22200() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22200(int ifReturn) {
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
		addElement("report_identifier", 10);
		addElement("report_line_sequence_number", 8);
		addElement("record_type", 6);
		addElement("issuer_acquirer_ind", 1);
		addElement("mvv_value", 10);
		addElement("remote_terminal_ind", 1);
		addElement("charge_ind", 1);
		addElement("product_id", 2);
		addElement("business_application_id", 2);
		addElement("source_of_funds", 1);
		addElement("product_subtype", 2);
		addElement("account_funding_source", 1);
		addElement("affliate_bin", 10);
		addElement("settlement_date", 6);
		addElement("transaction_id", 15);
		addElement("validation_code", 4);
		addElement("retrieval_reference_number", 12);
		addElement("trace_number", 6);
		addElement("batch_number", 4);
		addElement("request_message_type", 4);
		addElement("responce_code", 2);
		addElement("processing_code", 6);
		addElement("card_number", 19);
		addElement("transaction_amount", 12);
		addElement("currency_code_transaction_amt", 3);
		addElement("filler", 3);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		report_identifier = getElement("report_identifier");
		report_line_sequence_number = getElement("report_line_sequence_number");
		record_type = getElement("record_type");
		issuer_acquirer_ind = getElement("issuer_acquirer_ind");
		mvv_value = getElement("mvv_value");
		remote_terminal_ind = getElement("remote_terminal_ind");
		charge_ind = getElement("charge_ind");
		product_id = getElement("product_id");
		business_application_id = getElement("business_application_id");
		source_of_funds = getElement("source_of_funds");
		product_subtype = getElement("product_subtype");
		account_funding_source = getElement("account_funding_source");
		affliate_bin = getElement("affliate_bin");
		settlement_date = getElement("settlement_date");
		transaction_id = getElement("transaction_id");
		validation_code = getElement("validation_code");
		retrieval_reference_number = getElement("retrieval_reference_number");
		trace_number = getElement("trace_number");
		batch_number = getElement("batch_number");
		request_message_type = getElement("request_message_type");
		responce_code = getElement("responce_code");
		processing_code = getElement("processing_code");
		card_number = getElement("card_number");
		transaction_amount = getElement("transaction_amount");
		currency_code_transaction_amt = getElement("currency_code_transaction_amt");
		filler = getElement("filler");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_SMSRAWDATA_V22200_ST";
	}
}