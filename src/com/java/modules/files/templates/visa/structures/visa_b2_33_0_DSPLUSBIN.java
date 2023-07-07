package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_DSPLUSBIN extends AsciiMessage {
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
	public AsciiField _account_type;
	public AsciiField _bin_flag;
	public AsciiField _reserved_1;
	public AsciiField _iss_country_code_1;
	public AsciiField _reserved_2;
	public AsciiField _id_tag;
	public AsciiField _reserved;
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
	public char[] account_type; // [3] <open=suppress, name="account_type">;
	public char[] bin_flag; // [1] <open=suppress, name="bin_flag">;
	public char[] reserved_1; // [1] <open=suppress, name="reserved_1">;
	public char[] iss_country_code_1; // [3] <open=suppress, name="iss_country_code_1">;
	public char[] reserved_2; // [4] <open=suppress, name="reserved_2">;
	public char[] id_tag; // [14] <open=suppress, name="id_tag">;
	public char[] reserved; // [84] <open=suppress, name="reserved">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0_DSPLUSBIN(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_DSPLUSBIN(String content) {
		super(content);
	}

	public visa_b2_33_0_DSPLUSBIN() {
		super();
	}

	public visa_b2_33_0_DSPLUSBIN(int ifReturn) {
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
		addElement("account_type", 3);
		addElement("bin_flag", 1);
		addElement("reserved_1", 1);
		addElement("iss_country_code_1", 3);
		addElement("reserved_2", 4);
		addElement("id_tag", 14);
		addElement("reserved", 84);
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
		account_type = getElement("account_type");
		bin_flag = getElement("bin_flag");
		reserved_1 = getElement("reserved_1");
		iss_country_code_1 = getElement("iss_country_code_1");
		reserved_2 = getElement("reserved_2");
		id_tag = getElement("id_tag");
		reserved = getElement("reserved");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_DSPLUSBIN_ST";
	}
}