package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_TRSVISABIN extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _data_sequence_nr;
	public AsciiField _sequence_number_1;
	public AsciiField _segment_number_1;
	public AsciiField _acct_number_len_1;
	public AsciiField _bin_len_1;
	public AsciiField _bin_1;
	public AsciiField _iss_country_code_1;
	public AsciiField _reserved_1;
	public AsciiField _sequence_number_2;
	public AsciiField _segment_number_2;
	public AsciiField _acct_number_len_2;
	public AsciiField _bin_len_2;
	public AsciiField _bin_2;
	public AsciiField _iss_country_code_2;
	public AsciiField _reserved_2;
	public AsciiField _sequence_number_3;
	public AsciiField _segment_number_3;
	public AsciiField _acct_number_len_3;
	public AsciiField _bin_len_3;
	public AsciiField _bin_3;
	public AsciiField _iss_country_code_3;
	public AsciiField _reserved_3;
	public AsciiField _visa_table_id;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] data_sequence_nr; // [8] <open=suppress, name="data_sequence_nr">;
	public char[] sequence_number_1; // [6] <open=suppress, name="sequence_number_1">;
	public char[] segment_number_1; // [1] <open=suppress, name="segment_number_1">;
	public char[] acct_number_len_1; // [2] <open=suppress, name="acct_number_len_1">;
	public char[] bin_len_1; // [2] <open=suppress, name="bin_len_1">;
	public char[] bin_1; // [12] <open=suppress, name="bin_1">;
	public char[] iss_country_code_1; // [3] <open=suppress, name="iss_country_code_1">;
	public char[] reserved_1; // [14] <open=suppress, name="reserved_1">;
	public char[] sequence_number_2; // [6] <open=suppress, name="sequence_number_2">;
	public char[] segment_number_2; // [1] <open=suppress, name="segment_number_2">;
	public char[] acct_number_len_2; // [2] <open=suppress, name="acct_number_len_2">;
	public char[] bin_len_2; // [2] <open=suppress, name="bin_len_2">;
	public char[] bin_2; // [12] <open=suppress, name="bin_2">;
	public char[] iss_country_code_2; // [3] <open=suppress, name="iss_country_code_2">;
	public char[] reserved_2; // [14] <open=suppress, name="reserved_2">;
	public char[] sequence_number_3; // [6] <open=suppress, name="sequence_number_3">;
	public char[] segment_number_3; // [1] <open=suppress, name="segment_number_3">;
	public char[] acct_number_len_3; // [2] <open=suppress, name="acct_number_len_3">;
	public char[] bin_len_3; // [2] <open=suppress, name="bin_len_3">;
	public char[] bin_3; // [12] <open=suppress, name="bin_3">;
	public char[] iss_country_code_3; // [3] <open=suppress, name="iss_country_code_3">;
	public char[] reserved_3; // [14] <open=suppress, name="reserved_3">;
	public char[] visa_table_id; // [13] <open=suppress, name="visa_table_id">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0_TRSVISABIN(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_TRSVISABIN(String content) {
		super(content);
	}

	public visa_b2_33_0_TRSVISABIN() {
		super();
	}

	public visa_b2_33_0_TRSVISABIN(int ifReturn) {
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
		addElement("data_sequence_nr", 8);
		addElement("sequence_number_1", 6);
		addElement("segment_number_1", 1);
		addElement("acct_number_len_1", 2);
		addElement("bin_len_1", 2);
		addElement("bin_1", 12);
		addElement("iss_country_code_1", 3);
		addElement("reserved_1", 14);
		addElement("sequence_number_2", 6);
		addElement("segment_number_2", 1);
		addElement("acct_number_len_2", 2);
		addElement("bin_len_2", 2);
		addElement("bin_2", 12);
		addElement("iss_country_code_2", 3);
		addElement("reserved_2", 14);
		addElement("sequence_number_3", 6);
		addElement("segment_number_3", 1);
		addElement("acct_number_len_3", 2);
		addElement("bin_len_3", 2);
		addElement("bin_3", 12);
		addElement("iss_country_code_3", 3);
		addElement("reserved_3", 14);
		addElement("visa_table_id", 13);
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
		data_sequence_nr = getElement("data_sequence_nr");
		sequence_number_1 = getElement("sequence_number_1");
		segment_number_1 = getElement("segment_number_1");
		acct_number_len_1 = getElement("acct_number_len_1");
		bin_len_1 = getElement("bin_len_1");
		bin_1 = getElement("bin_1");
		iss_country_code_1 = getElement("iss_country_code_1");
		reserved_1 = getElement("reserved_1");
		sequence_number_2 = getElement("sequence_number_2");
		segment_number_2 = getElement("segment_number_2");
		acct_number_len_2 = getElement("acct_number_len_2");
		bin_len_2 = getElement("bin_len_2");
		bin_2 = getElement("bin_2");
		iss_country_code_2 = getElement("iss_country_code_2");
		reserved_2 = getElement("reserved_2");
		sequence_number_3 = getElement("sequence_number_3");
		segment_number_3 = getElement("segment_number_3");
		acct_number_len_3 = getElement("acct_number_len_3");
		bin_len_3 = getElement("bin_len_3");
		bin_3 = getElement("bin_3");
		iss_country_code_3 = getElement("iss_country_code_3");
		reserved_3 = getElement("reserved_3");
		visa_table_id = getElement("visa_table_id");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "TC33 - VISA PLUS BIN";
	}
}