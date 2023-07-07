package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22000 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _online_settlement_date;
	public AsciiField _raw_data_recipient;
	public AsciiField _filler;
	public AsciiField _settlement_system;
	public AsciiField _vss_processing_date;
	public AsciiField _filler2;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="Report_Line_Sequence_Number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] online_settlement_date; // [6] <open=suppress, name="online_settlement_date">;
	public char[] raw_data_recipient; // [10] <open=suppress, name="raw_data_recipient">;
	public char[] filler; // [10] <open=suppress, name="filler">;
	public char[] settlement_system; // [9] <open=suppress, name="settlement_system">;
	public char[] vss_processing_date; // [6] <open=suppress, name="vss_processing_date">;
	public char[] filler2; // [86] <open=suppress, name="filler2">; //83
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22000(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22000(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22000() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22000(int ifReturn) {
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
		addElement("online_settlement_date", 6);
		addElement("raw_data_recipient", 10);
		addElement("filler", 10);
		addElement("settlement_system", 9);
		addElement("vss_processing_date", 6);
		addElement("filler2", 86);
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
		online_settlement_date = getElement("online_settlement_date");
		raw_data_recipient = getElement("raw_data_recipient");
		filler = getElement("filler");
		settlement_system = getElement("settlement_system");
		vss_processing_date = getElement("vss_processing_date");
		filler2 = getElement("filler2");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_0_SMSRAWDATA_V22000_ST";
	}
}