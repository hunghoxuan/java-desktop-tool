package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22261 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _reimbursement_fee;
	public AsciiField _reimbursement_fee_dr_cr_ind;
	public AsciiField _cashback_irf_amount_settlement_currency;
	public AsciiField _transaction_integrity_fee;
	public AsciiField _transaction_integrity_fee_dr_cr_ind;
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
	public char[] reimbursement_fee; // [11] <open=suppress, name="reimbursement_fee">;
	public char[] reimbursement_fee_dr_cr_ind; // [1] <open=suppress, name="reimbursement_fee_dr_cr_ind">;
	public char[] cashback_irf_amount_settlement_currency; // [11] <open=suppress,
															// name="cashback_irf_amount_settlement_currency">;
	public char[] transaction_integrity_fee; // [11] <open=suppress, name="transaction_integrity_fee">;
	public char[] transaction_integrity_fee_dr_cr_ind; // [1] <open=suppress,
														// name="transaction_integrity_fee_dr_cr_ind">;
	public char[] filler; // [92] <open=suppress, name="filler">; //89
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22261(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22261(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22261() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22261(int ifReturn) {
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
		addElement("reimbursement_fee", 11);
		addElement("reimbursement_fee_dr_cr_ind", 1);
		addElement("cashback_irf_amount_settlement_currency", 11);
		addElement("transaction_integrity_fee", 11);
		addElement("transaction_integrity_fee_dr_cr_ind", 1);
		addElement("filler", 92);
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
		reimbursement_fee = getElement("reimbursement_fee");
		reimbursement_fee_dr_cr_ind = getElement("reimbursement_fee_dr_cr_ind");
		cashback_irf_amount_settlement_currency = getElement("cashback_irf_amount_settlement_currency");
		transaction_integrity_fee = getElement("transaction_integrity_fee");
		transaction_integrity_fee_dr_cr_ind = getElement("transaction_integrity_fee_dr_cr_ind");
		filler = getElement("filler");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_SMSRAWDATA_V22261_ST";
	}
}