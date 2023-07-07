package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_52_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _acct_number;
	public AsciiField _acct_number_ext;
	public AsciiField _acq_ref_number;
	public AsciiField _acq_buss_id;
	public AsciiField _purchase_date;
	public AsciiField _tran_amount;
	public AsciiField _tran_curr_code;
	public AsciiField _merch_name;
	public AsciiField _merch_city;
	public AsciiField _merch_country_code;
	public AsciiField _merch_category_code;
	public AsciiField _us_merch_zip_code;
	public AsciiField _merch_state;
	public AsciiField _issuer_control_number;
	public AsciiField _req_reason_code;
	public AsciiField _settlement_flag;
	public AsciiField _nat_reimbursement_fee;
	public AsciiField _acct_selection;
	public AsciiField _retreival_req_id;
	public AsciiField _central_process_date;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] acct_number; // [16] <open=suppress,name="acct_number">;
	public char[] acct_number_ext; // [3] <open=suppress,name="acct_number_ext">;
	public char[] acq_ref_number; // [23] <open=suppress,name="acq_ref_number">;
	public char[] acq_buss_id; // [8] <open=suppress,name="acq_buss_id">;
	public char[] purchase_date; // [4] <open=suppress,name="purchase_date">;
	public char[] tran_amount; // [12] <open=suppress,name="tran_amount">;
	public char[] tran_curr_code; // [3] <open=suppress,name="tran_curr_code">;
	public char[] merch_name; // [25] <open=suppress,name="merch_name">;
	public char[] merch_city; // [13] <open=suppress,name="merch_city">;
	public char[] merch_country_code; // [3] <open=suppress,name="merch_country_code">;
	public char[] merch_category_code; // [4] <open=suppress,name="merch_category_code">;
	public char[] us_merch_zip_code; // [5] <open=suppress,name="us_merch_zip_code">;
	public char[] merch_state; // [3] <open=suppress,name="merch_state">;
	public char[] issuer_control_number; // [9] <open=suppress,name="issuer_control_number">;
	public char[] req_reason_code; // [2] <open=suppress,name="req_reason_code">;
	public char[] settlement_flag; // [1] <open=suppress,name="settlement_flag">;
	public char[] nat_reimbursement_fee; // [12] <open=suppress,name="nat_reimbursement_fee">;
	public char[] acct_selection; // [1] <open=suppress,name="acct_selection">;
	public char[] retreival_req_id; // [12] <open=suppress,name="retreival_req_id">;
	public char[] central_process_date; // [4] <open=suppress,name="central_process_date">;
	public char[] reimbursement_attr; // [1] <open=suppress,name="reimbursement_attr">;

	public visa_b2_52_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_52_0(String content) {
		super(content);
	}

	public visa_b2_52_0() {
		super();
	}

	public visa_b2_52_0(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("acct_number", 16);
		addElement("acct_number_ext", 3);
		addElement("acq_ref_number", 23);
		addElement("acq_buss_id", 8);
		addElement("purchase_date", 4);
		addElement("tran_amount", 12);
		addElement("tran_curr_code", 3);
		addElement("merch_name", 25);
		addElement("merch_city", 13);
		addElement("merch_country_code", 3);
		addElement("merch_category_code", 4);
		addElement("us_merch_zip_code", 5);
		addElement("merch_state", 3);
		addElement("issuer_control_number", 9);
		addElement("req_reason_code", 2);
		addElement("settlement_flag", 1);
		addElement("nat_reimbursement_fee", 12);
		addElement("acct_selection", 1);
		addElement("retreival_req_id", 12);
		addElement("central_process_date", 4);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		acct_number = getElement("acct_number");
		acct_number_ext = getElement("acct_number_ext");
		acq_ref_number = getElement("acq_ref_number");
		acq_buss_id = getElement("acq_buss_id");
		purchase_date = getElement("purchase_date");
		tran_amount = getElement("tran_amount");
		tran_curr_code = getElement("tran_curr_code");
		merch_name = getElement("merch_name");
		merch_city = getElement("merch_city");
		merch_country_code = getElement("merch_country_code");
		merch_category_code = getElement("merch_category_code");
		us_merch_zip_code = getElement("us_merch_zip_code");
		merch_state = getElement("merch_state");
		issuer_control_number = getElement("issuer_control_number");
		req_reason_code = getElement("req_reason_code");
		settlement_flag = getElement("settlement_flag");
		nat_reimbursement_fee = getElement("nat_reimbursement_fee");
		acct_selection = getElement("acct_selection");
		retreival_req_id = getElement("retreival_req_id");
		central_process_date = getElement("central_process_date");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "tc52 tcr";
	}
}