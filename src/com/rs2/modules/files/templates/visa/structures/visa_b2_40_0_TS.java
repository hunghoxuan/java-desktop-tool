package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_0_TS extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _account_number;
	public AsciiField _account_extension;
	public AsciiField _acquirer_bus_id;
	public AsciiField _response_code;
	public AsciiField _rej_warn_code4_total;
	public AsciiField _rej_warn_code5_day;
	public AsciiField _rej_warn_code5_total;
	public AsciiField _rej_warn_code6_day;
	public AsciiField _rej_warn_code6_total;
	public AsciiField _rej_warn_code7_day;
	public AsciiField _rej_warn_code7_total;
	public AsciiField _rej_warn_code8_day;
	public AsciiField _rej_warn_code8_total;
	public AsciiField _rej_warn_code9_day;
	public AsciiField _rej_warn_code9_total;
	public AsciiField _rej_warn_code10_day;
	public AsciiField _rej_warn_code10_total;
	public AsciiField _fraud_type0_trans_cnt;
	public AsciiField _fraud_type0_total_amt;
	public AsciiField _fraud_type1_trans_cnt;
	public AsciiField _fraud_type1_total_amt;
	public AsciiField _fraud_type2_trans_cnt;
	public AsciiField _reserved;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] account_extension; // [7] <open=suppress, name="account_extension">;
	public char[] acquirer_bus_id; // [8] <open=suppress, name="acquirer_bus_id">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] rej_warn_code4_total; // [5] <open=suppress, name="rej_warn_code4_total">;
	public char[] rej_warn_code5_day; // [3] <open=suppress, name="rej_warn_code5_day">;
	public char[] rej_warn_code5_total; // [5] <open=suppress, name="rej_warn_code5_total">;
	public char[] rej_warn_code6_day; // [3] <open=suppress, name="rej_warn_code6_day">;
	public char[] rej_warn_code6_total; // [5] <open=suppress, name="rej_warn_code6_total">;
	public char[] rej_warn_code7_day; // [3] <open=suppress, name="rej_warn_code7_day">;
	public char[] rej_warn_code7_total; // [5] <open=suppress, name="rej_warn_code7_total">;
	public char[] rej_warn_code8_day; // [3] <open=suppress, name="rej_warn_code8_day">;
	public char[] rej_warn_code8_total; // [5] <open=suppress, name="rej_warn_code8_total">;
	public char[] rej_warn_code9_day; // [3] <open=suppress, name="rej_warn_code9_day">;
	public char[] rej_warn_code9_total; // [5] <open=suppress, name="rej_warn_code9_total">;
	public char[] rej_warn_code10_day; // [3] <open=suppress, name="rej_warn_code10_day">;
	public char[] rej_warn_code10_total; // [5] <open=suppress, name="rej_warn_code10_total">;
	public char[] fraud_type0_trans_cnt; // [5] <open=suppress, name="fraud_type0_trans_cnt">;
	public char[] fraud_type0_total_amt; // [12] <open=suppress, name="fraud_type0_total_amt">;
	public char[] fraud_type1_trans_cnt; // [5] <open=suppress, name="fraud_type1_trans_cnt">;
	public char[] fraud_type1_total_amt; // [12] <open=suppress, name="fraud_type1_total_amt">;
	public char[] fraud_type2_trans_cnt; // [5] <open=suppress, name="fraud_type2_trans_cnt">;
	public char[] reserved; // [3] <open=suppress, name="reserved">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_40_0_TS(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_0_TS(String content) {
		super(content);
	}

	public visa_b2_40_0_TS() {
		super();
	}

	public visa_b2_40_0_TS(int ifReturn) {
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
		addElement("account_number", 16);
		addElement("account_extension", 7);
		// ARN_STR acquirer_ref_number <name="acquirer_ref_number">;;
		addElement("ARN_01", 1);
		addElement("ARN_02", 6);
		addElement("ARN_03", 4);
		addElement("ARN_04", 11);
		addElement("ARN_05", 1);

		addElement("acquirer_bus_id", 8);
		addElement("response_code", 2);
		addElement("rej_warn_code4_total", 5);
		addElement("rej_warn_code5_day", 3);
		addElement("rej_warn_code5_total", 5);
		addElement("rej_warn_code6_day", 3);
		addElement("rej_warn_code6_total", 5);
		addElement("rej_warn_code7_day", 3);
		addElement("rej_warn_code7_total", 5);
		addElement("rej_warn_code8_day", 3);
		addElement("rej_warn_code8_total", 5);
		addElement("rej_warn_code9_day", 3);
		addElement("rej_warn_code9_total", 5);
		addElement("rej_warn_code10_day", 3);
		addElement("rej_warn_code10_total", 5);
		addElement("fraud_type0_trans_cnt", 5);
		addElement("fraud_type0_total_amt", 12);
		addElement("fraud_type1_trans_cnt", 5);
		addElement("fraud_type1_total_amt", 12);
		addElement("fraud_type2_trans_cnt", 5);
		addElement("reserved", 3);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		account_number = getElement("account_number");
		account_extension = getElement("account_extension");
		acquirer_bus_id = getElement("acquirer_bus_id");
		response_code = getElement("response_code");
		rej_warn_code4_total = getElement("rej_warn_code4_total");
		rej_warn_code5_day = getElement("rej_warn_code5_day");
		rej_warn_code5_total = getElement("rej_warn_code5_total");
		rej_warn_code6_day = getElement("rej_warn_code6_day");
		rej_warn_code6_total = getElement("rej_warn_code6_total");
		rej_warn_code7_day = getElement("rej_warn_code7_day");
		rej_warn_code7_total = getElement("rej_warn_code7_total");
		rej_warn_code8_day = getElement("rej_warn_code8_day");
		rej_warn_code8_total = getElement("rej_warn_code8_total");
		rej_warn_code9_day = getElement("rej_warn_code9_day");
		rej_warn_code9_total = getElement("rej_warn_code9_total");
		rej_warn_code10_day = getElement("rej_warn_code10_day");
		rej_warn_code10_total = getElement("rej_warn_code10_total");
		fraud_type0_trans_cnt = getElement("fraud_type0_trans_cnt");
		fraud_type0_total_amt = getElement("fraud_type0_total_amt");
		fraud_type1_trans_cnt = getElement("fraud_type1_trans_cnt");
		fraud_type1_total_amt = getElement("fraud_type1_total_amt");
		fraud_type2_trans_cnt = getElement("fraud_type2_trans_cnt");
		reserved = getElement("reserved");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_0_TS_ST";
	}
}