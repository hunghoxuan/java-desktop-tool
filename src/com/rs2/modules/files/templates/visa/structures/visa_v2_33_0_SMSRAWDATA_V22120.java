package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_v2_33_0_SMSRAWDATA_V22120 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _report_line_sequence_number;
	public AsciiField _record_type;
	public AsciiField _funds_trans_sre;
	public AsciiField _processor_id;
	public AsciiField _affiliate_bin;
	public AsciiField _sre;
	public AsciiField _settlement_serv_ind;
	public AsciiField _issuer_inter_amount;
	public AsciiField _acquirer_inter_amount;
	public AsciiField _other_inter_amount;
	public AsciiField _gross_inter_amount;
	public AsciiField _filler;
	public AsciiField _tc33_filler;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] report_line_sequence_number; // [8] <open=suppress, name="report_line_sequence_number">;
	public char[] record_type; // [6] <open=suppress, name="record_type">;
	public char[] funds_trans_sre; // [10] < open = suppress, name = "funds_transfer_sre" > ;
	public char[] processor_id; // [10] < open = suppress, name = "processor_id" > ;
	public char[] affiliate_bin; // [10] < open = suppress, name = "affiliate_bin" > ;
	public char[] sre; // [10] < open = suppress, name = "sre" > ;
	public char[] settlement_serv_ind; // [3] < open = suppress, name = "settlement_service_indicator" > ;
	public char[] issuer_inter_amount; // [16] < open = suppress, name = "issuer_interchange_amount" > ;
	public char[] acquirer_inter_amount; // [16] < open = suppress, name = "acquirer_interchange_amount" > ;
	public char[] other_inter_amount; // [16] < open = suppress, name = "other_interchange_amount" > ;
	public char[] gross_inter_amount; // [16] < open = suppress, name = "gross_interchange_amount" > ;
	public char[] filler; // [17] < open = suppress, name = "filler" > ;
	public char[] tc33_filler; // [3] < open = suppress, name = "tc33_filler" > ;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_v2_33_0_SMSRAWDATA_V22120(int offset, String content) {
		super(offset, content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22120(String content) {
		super(content);
	}

	public visa_v2_33_0_SMSRAWDATA_V22120() {
		super();
	}

	public visa_v2_33_0_SMSRAWDATA_V22120(int ifReturn) {
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
		addElement("funds_trans_sre", 10);
		addElement("processor_id", 10);
		addElement("affiliate_bin", 10);
		addElement("sre", 10);
		addElement("settlement_serv_ind", 3);
		addElement("issuer_inter_amount", 16);
		addElement("acquirer_inter_amount", 16);
		addElement("other_inter_amount", 16);
		addElement("gross_inter_amount", 16);
		addElement("filler", 17);
		addElement("tc33_filler", 3);
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
		funds_trans_sre = getElement("funds_trans_sre");
		processor_id = getElement("processor_id");
		affiliate_bin = getElement("affiliate_bin");
		sre = getElement("sre");
		settlement_serv_ind = getElement("settlement_serv_ind");
		issuer_inter_amount = getElement("issuer_inter_amount");
		acquirer_inter_amount = getElement("acquirer_inter_amount");
		other_inter_amount = getElement("other_inter_amount");
		gross_inter_amount = getElement("gross_inter_amount");
		filler = getElement("filler");
		tc33_filler = getElement("tc33_filler");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "";
	}
}