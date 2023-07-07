package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_CFGEANSBII extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_seq_num;
	public AsciiField _center_bin;
	public AsciiField _reserved_1;
	public AsciiField _start_of_bin_range;
	public AsciiField _end_of_bin_range;
	public AsciiField _flag_1;
	public AsciiField _flag_2;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_seq_num; // [8] <open=suppress, name="report_line_seq_num">;
	public char[] center_bin; // [6] <open=suppress, name="center_bin">;
	public char[] reserved_1; // [4] <open=suppress, name="reserved_1">;
	public char[] start_of_bin_range; // [18] <open=suppress, name="start_of_bin_range">;
	public char[] end_of_bin_range; // [18] <open=suppress, name="end_of_bin_range">;
	public char[] flag_1; // [1] <open=suppress, name="flag_1">;
	public char[] flag_2; // [1] <open=suppress, name="flag_2">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0_CFGEANSBII(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_CFGEANSBII(String content) {
		super(content);
	}

	public visa_b2_33_0_CFGEANSBII() {
		super();
	}

	public visa_b2_33_0_CFGEANSBII(int ifReturn) {
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
		addElement("report_line_seq_num", 8);
		addElement("center_bin", 6);
		addElement("reserved_1", 4);
		addElement("start_of_bin_range", 18);
		addElement("end_of_bin_range", 18);
		addElement("flag_1", 1);
		addElement("flag_2", 1);
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
		report_line_seq_num = getElement("report_line_seq_num");
		center_bin = getElement("center_bin");
		reserved_1 = getElement("reserved_1");
		start_of_bin_range = getElement("start_of_bin_range");
		end_of_bin_range = getElement("end_of_bin_range");
		flag_1 = getElement("flag_1");
		flag_2 = getElement("flag_2");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_CFGEANSBII_ST";
	}
}