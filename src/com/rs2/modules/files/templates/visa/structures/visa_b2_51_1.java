package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_51_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fax_number;
	public AsciiField _interface_trace_num;
	public AsciiField _requst_fulfmnt_meth;
	public AsciiField _establ_fulfmnt_meth;
	public AsciiField _i_rfc_bin;
	public AsciiField _i_rfc_sub_address;
	public AsciiField _i_billing_currency;
	public AsciiField _i_billing_amount;
	public AsciiField _transaction_id;
	public AsciiField _excl_trans_id_reason;
	public AsciiField _crs_proc_code;
	public AsciiField _mc_sequence_num;
	public AsciiField _pan_token;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] fax_number; // [16] <open=suppress, name="fax_number">;
	public char[] interface_trace_num; // [6] <open=suppress, name="interface_trace_num">;
	public char[] requst_fulfmnt_meth; // [1] <open=suppress, name="requst_fulfmnt_meth">;
	public char[] establ_fulfmnt_meth; // [1] <open=suppress, name="establ_fulfmnt_meth">;
	public char[] i_rfc_bin; // [6] <open=suppress, name="i_rfc_bin">;
	public char[] i_rfc_sub_address; // [7] <open=suppress, name="i_rfc_sub_address">;
	public char[] i_billing_currency; // [3] <open=suppress, name="i_billing_currency">;
	public char[] i_billing_amount; // [12] <open=suppress, name="i_billing_amount">;
	public char[] transaction_id; // [15] <open=suppress, name="transaction_id">;
	public char[] excl_trans_id_reason; // [1] <open=suppress, name="excl_trans_id_reason">;
	public char[] crs_proc_code; // [1] <open=suppress, name="crs_proc_code">;
	public char[] mc_sequence_num; // [2] <open=suppress, name="mc_sequence_num">;
	public char[] pan_token; // [16] <open=suppress, name="pan_token">;
	public char[] reserved_2; // [65] <open=suppress, name="reserved_2">;

	public visa_b2_51_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_51_1(String content) {
		super(content);
	}

	public visa_b2_51_1() {
		super();
	}

	public visa_b2_51_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("reserved_1", 12);
		addElement("fax_number", 16);
		addElement("interface_trace_num", 6);
		addElement("requst_fulfmnt_meth", 1);
		addElement("establ_fulfmnt_meth", 1);
		addElement("i_rfc_bin", 6);
		addElement("i_rfc_sub_address", 7);
		addElement("i_billing_currency", 3);
		addElement("i_billing_amount", 12);
		addElement("transaction_id", 15);
		addElement("excl_trans_id_reason", 1);
		addElement("crs_proc_code", 1);
		addElement("mc_sequence_num", 2);
		addElement("pan_token", 16);
		addElement("reserved_2", 65);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		fax_number = getElement("fax_number");
		interface_trace_num = getElement("interface_trace_num");
		requst_fulfmnt_meth = getElement("requst_fulfmnt_meth");
		establ_fulfmnt_meth = getElement("establ_fulfmnt_meth");
		i_rfc_bin = getElement("i_rfc_bin");
		i_rfc_sub_address = getElement("i_rfc_sub_address");
		i_billing_currency = getElement("i_billing_currency");
		i_billing_amount = getElement("i_billing_amount");
		transaction_id = getElement("transaction_id");
		excl_trans_id_reason = getElement("excl_trans_id_reason");
		crs_proc_code = getElement("crs_proc_code");
		mc_sequence_num = getElement("mc_sequence_num");
		pan_token = getElement("pan_token");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_51_1_ST";
	}
}