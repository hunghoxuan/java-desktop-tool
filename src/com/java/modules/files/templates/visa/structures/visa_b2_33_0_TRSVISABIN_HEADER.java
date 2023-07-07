package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_TRSVISABIN_HEADER extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _data_sequence_nr;
	public AsciiField _sequence_number_1;
	public AsciiField _record_type_part2;
	public AsciiField _file_type;
	public AsciiField _reserved_1;
	public AsciiField _creation_date;
	public AsciiField _processing;
	public AsciiField _network_type;
	public AsciiField _reserved_2;
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
	public char[] record_type_part2; // [5] <open=suppress, name="record_type_part2">;
	public char[] file_type; // [8] <open=suppress, name="file_type">;
	public char[] reserved_1; // [2] <open=suppress, name="reserved_1">;
	public char[] creation_date; // [5] <open=suppress, name="creation_date">;
	public char[] processing; // [7] <open=suppress, name="processing">;
	public char[] network_type; // [6] <open=suppress, name="network_type">;
	public char[] reserved_2; // [81] <open=suppress, name="reserved_2">;
	public char[] visa_table_id; // [13] <open=suppress, name="visa_table_id">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0_TRSVISABIN_HEADER(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_TRSVISABIN_HEADER(String content) {
		super(content);
	}

	public visa_b2_33_0_TRSVISABIN_HEADER() {
		super();
	}

	public visa_b2_33_0_TRSVISABIN_HEADER(int ifReturn) {
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
		addElement("record_type_part2", 5);
		addElement("file_type", 8);
		addElement("reserved_1", 2);
		addElement("creation_date", 5);
		addElement("processing", 7);
		addElement("network_type", 6);
		addElement("reserved_2", 81);
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
		record_type_part2 = getElement("record_type_part2");
		file_type = getElement("file_type");
		reserved_1 = getElement("reserved_1");
		creation_date = getElement("creation_date");
		processing = getElement("processing");
		network_type = getElement("network_type");
		reserved_2 = getElement("reserved_2");
		visa_table_id = getElement("visa_table_id");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_TRSVISABIN_HEADER_ST";
	}
}