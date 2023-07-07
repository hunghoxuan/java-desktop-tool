package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_1_FEE extends AsciiMessage {
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_seq_num;
	public AsciiField _retrieval_reference_num;
	public AsciiField _local_trans_time;
	public AsciiField _merchant_verif_value;
	public AsciiField _merchant_category_code;
	public AsciiField _payment_facilitator_id;
	public AsciiField _sub_merchant_id;
	public AsciiField _reserved;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [7] <open=suppress, name="report_identifier">;
	public char[] report_line_seq_num; // [8] <open=suppress, name="report_line_seq_num">;
	public char[] retrieval_reference_num; // [12] <open=suppress, name="retrieval_reference_num">;
	public char[] local_trans_time; // [6] <open=suppress, name="local_trans_time">;
	public char[] merchant_verif_value; // [10] <open=suppress, name="merchant_verif_value">;
	public char[] merchant_category_code; // [4] <open=suppress, name="merchant_category_code">;
	public char[] payment_facilitator_id; // [11] <open=suppress, name="payment_facilitator_id">;
	public char[] sub_merchant_id; // [15] <open=suppress, name="sub_merchant_id">;
	public char[] reserved; // [79] <open=suppress, name="reserved">;

	public visa_v2_33_1_FEE(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_1_FEE(String content) {
		super(content);
	}

	public visa_v2_33_1_FEE() {
		super();
	}

	public visa_v2_33_1_FEE(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("report_identifier", 7);
		addElement("report_line_seq_num", 8);
		addElement("retrieval_reference_num", 12);
		addElement("local_trans_time", 6);
		addElement("merchant_verif_value", 10);
		addElement("merchant_category_code", 4);
		addElement("payment_facilitator_id", 11);
		addElement("sub_merchant_id", 15);
		addElement("reserved", 79);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		report_identifier = getElement("report_identifier");
		report_line_seq_num = getElement("report_line_seq_num");
		retrieval_reference_num = getElement("retrieval_reference_num");
		local_trans_time = getElement("local_trans_time");
		merchant_verif_value = getElement("merchant_verif_value");
		merchant_category_code = getElement("merchant_category_code");
		payment_facilitator_id = getElement("payment_facilitator_id");
		sub_merchant_id = getElement("sub_merchant_id");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_v2_33_1_FEE_ST";
	}
}