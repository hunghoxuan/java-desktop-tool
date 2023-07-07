package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22225 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _usage_code;
	public AsciiField _documentation_indicator;
	public AsciiField _chargeback_ref_number;
	public AsciiField _message_text;
	public AsciiField _tc33_filler;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="report_line_sequence_number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] usage_code; // [1] <open=suppress, name="usage_code">;
	public char[] documentation_indicator; // [1] <open=suppress, name="documentaion_indicator">;
	public char[] chargeback_ref_number; // [6] <open=suppress, name="chargeback_reference_number">;
	public char[] message_text; // [74] <open=suppress, name="message_text">;
	public char[] tc33_filler; // [3] < open = suppress, name = "tc33_filler" > ;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22225(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22225(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22225() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22225(int ifReturn) {
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
		// v22225_original_data_elements original_data_elements <open=suppress,
		// name="original_data_elements">;;
		addElement("usage_code", 1);
		addElement("documentation_indicator", 1);
		addElement("chargeback_ref_number", 6);
		addElement("message_text", 74);
		addElement("tc33_filler", 3);
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
		usage_code = getElement("usage_code");
		documentation_indicator = getElement("documentation_indicator");
		chargeback_ref_number = getElement("chargeback_ref_number");
		message_text = getElement("message_text");
		tc33_filler = getElement("tc33_filler");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "";
	}
}