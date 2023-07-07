package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_55 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _transaction_code;
	public AsciiField _tran_code_qualif;
	public AsciiField _tran_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _record_type_1;
	public AsciiField _format_code_1;
	public AsciiField _account_number_1;
	public AsciiField _region_flags_1;
	public AsciiField _filler_1;
	public AsciiField _record_type_2;
	public AsciiField _format_code_2;
	public AsciiField _account_number_2;
	public AsciiField _region_flags_2;
	public AsciiField _filler_2;
	public AsciiField _record_type_3;
	public AsciiField _format_code_3;
	public AsciiField _account_number_3;
	public AsciiField _region_flags_3;
	public AsciiField _filler_3;
	public AsciiField _record_type_4;
	public AsciiField _format_code_4;
	public AsciiField _account_number_4;
	public AsciiField _region_flags_4;
	public AsciiField _filler_4;
	public AsciiField _record_id;
	public AsciiField _copyright_notice;
	public AsciiField _filler;
	public AsciiField _confidential_notice;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] transaction_code; // [2] <open=suppress, name="transaction_code">;
	public char[] tran_code_qualif; // [1] <open=suppress, name="tran_code_qualif">;
	public char[] tran_comp_seq; // [1] <open=suppress, name="tran_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] record_type_1; // [1] <open=suppress, name="record_type_1">;
	public char[] format_code_1; // [1] <open=suppress, name="format_code_1">;
	public char[] account_number_1; // [19] <open=suppress, name="account_number_1">;
	public char[] region_flags_1; // [2] <open=suppress, name="region_flags_1">;
	public char[] filler_1; // [2] <open=suppress, name="filler_1">;
	public char[] record_type_2; // [1] <open=suppress, name="record_type_2">;
	public char[] format_code_2; // [1] <open=suppress, name="format_code_2">;
	public char[] account_number_2; // [19] <open=suppress, name="account_number_2">;
	public char[] region_flags_2; // [2] <open=suppress, name="region_flags_2">;
	public char[] filler_2; // [2] <open=suppress, name="filler_2">;
	public char[] record_type_3; // [1] <open=suppress, name="record_type_3">;
	public char[] format_code_3; // [1] <open=suppress, name="format_code_3">;
	public char[] account_number_3; // [19] <open=suppress, name="account_number_3">;
	public char[] region_flags_3; // [2] <open=suppress, name="region_flags_3">;
	public char[] filler_3; // [2] <open=suppress, name="filler_3">;
	public char[] record_type_4; // [1] <open=suppress, name="record_type_4">;
	public char[] format_code_4; // [1] <open=suppress, name="format_code_4">;
	public char[] account_number_4; // [19] <open=suppress, name="account_number_4">;
	public char[] region_flags_4; // [2] <open=suppress, name="region_flags_4">;
	public char[] filler_4; // [2] <open=suppress, name="filler_4">;
	public char[] record_id; // [4] <open=suppress, name="record_id">;
	public char[] copyright_notice; // [23] <open=suppress, name="copyright_notice">;
	public char[] filler; // [1] <open=suppress, name="filler">;
	public char[] confidential_notice; // [23] <open=suppress, name="confidential_notice">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_55(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_55(String content) {
		super(content);
	}

	public visa_b2_55() {
		super();
	}

	public visa_b2_55(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		//// END CODE CHANGES BY SIGMOND;
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("transaction_code", 2);
		addElement("tran_code_qualif", 1);
		addElement("tran_comp_seq", 1);
		addElement("destination_bin", 6);
		addElement("source_bin", 6);
		addElement("record_type_1", 1);
		addElement("format_code_1", 1);
		addElement("account_number_1", 19);
		addElement("region_flags_1", 2);
		addElement("filler_1", 2);
		addElement("record_type_2", 1);
		addElement("format_code_2", 1);
		addElement("account_number_2", 19);
		addElement("region_flags_2", 2);
		addElement("filler_2", 2);
		addElement("record_type_3", 1);
		addElement("format_code_3", 1);
		addElement("account_number_3", 19);
		addElement("region_flags_3", 2);
		addElement("filler_3", 2);
		addElement("record_type_4", 1);
		addElement("format_code_4", 1);
		addElement("account_number_4", 19);
		addElement("region_flags_4", 2);
		addElement("filler_4", 2);
		addElement("record_id", 4);
		addElement("copyright_notice", 23);
		addElement("filler", 1);
		addElement("confidential_notice", 23);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		transaction_code = getElement("transaction_code");
		tran_code_qualif = getElement("tran_code_qualif");
		tran_comp_seq = getElement("tran_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		record_type_1 = getElement("record_type_1");
		format_code_1 = getElement("format_code_1");
		account_number_1 = getElement("account_number_1");
		region_flags_1 = getElement("region_flags_1");
		filler_1 = getElement("filler_1");
		record_type_2 = getElement("record_type_2");
		format_code_2 = getElement("format_code_2");
		account_number_2 = getElement("account_number_2");
		region_flags_2 = getElement("region_flags_2");
		filler_2 = getElement("filler_2");
		record_type_3 = getElement("record_type_3");
		format_code_3 = getElement("format_code_3");
		account_number_3 = getElement("account_number_3");
		region_flags_3 = getElement("region_flags_3");
		filler_3 = getElement("filler_3");
		record_type_4 = getElement("record_type_4");
		format_code_4 = getElement("format_code_4");
		account_number_4 = getElement("account_number_4");
		region_flags_4 = getElement("region_flags_4");
		filler_4 = getElement("filler_4");
		record_id = getElement("record_id");
		copyright_notice = getElement("copyright_notice");
		filler = getElement("filler");
		confidential_notice = getElement("confidential_notice");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_55_ST";
	}
}