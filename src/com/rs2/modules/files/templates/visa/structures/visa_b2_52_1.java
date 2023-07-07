package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_52_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved;
	public AsciiField _fax_number;
	public AsciiField _interface_trace_number;
	public AsciiField _req_fulfillment_method;
	public AsciiField _established_fulfillment_method;
	public AsciiField _issuer_rfc_bin;
	public AsciiField _issuer_rfx_sub_addr;
	public AsciiField _issuer_billing_curr_code;
	public AsciiField _issuer_billing_trans_amount;
	public AsciiField _trans_identifier;
	public AsciiField _excluded_trans_identifier_reason;
	public AsciiField _crs_processing_code;
	public AsciiField _multiple_clearing_seq_number;
	public AsciiField _pan_token;
	public AsciiField _reserved_1;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved; // [12] <open=suppress,name="reserved">;
	public char[] fax_number; // [16] <open=suppress,name="fax_number">;
	public char[] interface_trace_number; // [6] <open=suppress,name="interface_trace_number">;
	public char[] req_fulfillment_method; // [1] <open=suppress,name="req_fulfillment_method">;
	public char[] established_fulfillment_method; // [1] <open=suppress,name="established_fulfillment_method">;
	public char[] issuer_rfc_bin; // [6] <open=suppress,name="issuer_rfc_bin">;
	public char[] issuer_rfx_sub_addr; // [7] <open=suppress,name="issuer_rfx_sub_addr">;
	public char[] issuer_billing_curr_code; // [3] <open=suppress,name="issuer_billing_curr_code">;
	public char[] issuer_billing_trans_amount; // [12] <open=suppress,name="issuer_billing_trans_amount">;
	public char[] trans_identifier; // [15] <open=suppress,name="trans_identifier">;
	public char[] excluded_trans_identifier_reason; // [1] <open=suppress,name="excluded_trans_identifier_reason">;
	public char[] crs_processing_code; // [1] <open=suppress,name="crs_processing_code">;
	public char[] multiple_clearing_seq_number; // [2] <open=suppress,name="multiple_clearing_seq_number">;
	public char[] pan_token; // [16] <open=suppress,name="pan_token">;
	public char[] reserved_1; // [65] <open=suppress,name="reserved_1">;

	public visa_b2_52_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_52_1(String content) {
		super(content);
	}

	public visa_b2_52_1() {
		super();
	}

	public visa_b2_52_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct;
		// {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("reserved", 12);
		addElement("fax_number", 16);
		addElement("interface_trace_number", 6);
		addElement("req_fulfillment_method", 1);
		addElement("established_fulfillment_method", 1);
		addElement("issuer_rfc_bin", 6);
		addElement("issuer_rfx_sub_addr", 7);
		addElement("issuer_billing_curr_code", 3);
		addElement("issuer_billing_trans_amount", 12);
		addElement("trans_identifier", 15);
		addElement("excluded_trans_identifier_reason", 1);
		addElement("crs_processing_code", 1);
		addElement("multiple_clearing_seq_number", 2);
		addElement("pan_token", 16);
		addElement("reserved_1", 65);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved = getElement("reserved");
		fax_number = getElement("fax_number");
		interface_trace_number = getElement("interface_trace_number");
		req_fulfillment_method = getElement("req_fulfillment_method");
		established_fulfillment_method = getElement("established_fulfillment_method");
		issuer_rfc_bin = getElement("issuer_rfc_bin");
		issuer_rfx_sub_addr = getElement("issuer_rfx_sub_addr");
		issuer_billing_curr_code = getElement("issuer_billing_curr_code");
		issuer_billing_trans_amount = getElement("issuer_billing_trans_amount");
		trans_identifier = getElement("trans_identifier");
		excluded_trans_identifier_reason = getElement("excluded_trans_identifier_reason");
		crs_processing_code = getElement("crs_processing_code");
		multiple_clearing_seq_number = getElement("multiple_clearing_seq_number");
		pan_token = getElement("pan_token");
		reserved_1 = getElement("reserved_1");

	}

	@Override
	public String getDescription() {
		return "tc52 tcr";
	}
}