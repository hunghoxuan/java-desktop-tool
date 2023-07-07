package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_51_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _account_number;
	public AsciiField _account_extension;
	public AsciiField _acquirer_bus_id;
	public AsciiField _purchase_date;
	public AsciiField _transaction_amount;
	public AsciiField _transaction_currency;
	public AsciiField _merchant_name;
	public AsciiField _merchant_city;
	public AsciiField _merchant_country;
	public AsciiField _merchant_category;
	public AsciiField _merchant_zip_code;
	public AsciiField _merchant_province;
	public AsciiField _issuer_control_num;
	public AsciiField _reason_code;
	public AsciiField _settlement_flag;
	public AsciiField _national_reimbrs_fee;
	public AsciiField _atm_account_selection;
	public AsciiField _retrieval_request_id;
	public AsciiField _central_proc_date;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] account_extension; // [3] <open=suppress, name="account_extension">;
	public char[] acquirer_bus_id; // [8] <open=suppress, name="acquirer_bus_id">;
	public char[] purchase_date; // [4] <open=suppress, name="purchase_date">;
	public char[] transaction_amount; // [12] <open=suppress, name="transaction_amount">;
	public char[] transaction_currency; // [3] <open=suppress, name="transaction_currency">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_city; // [13] <open=suppress, name="merchant_city">;
	public char[] merchant_country; // [3] <open=suppress, name="merchant_country">;
	public char[] merchant_category; // [4] <open=suppress, name="merchant_category">;
	public char[] merchant_zip_code; // [5] <open=suppress, name="merchant_zip_code">;
	public char[] merchant_province; // [3] <open=suppress, name="merchant_province">;
	public char[] issuer_control_num; // [9] <open=suppress, name="issuer_control_num">;
	public char[] reason_code; // [2] <open=suppress, name="reason_code">;
	public char[] settlement_flag; // [1] <open=suppress, name="settlement_flag">;
	public char[] national_reimbrs_fee; // [12] <open=suppress, name="national_reimbrs_fee">;
	public char[] atm_account_selection; // [1] <open=suppress, name="atm_account_selection">;
	public char[] retrieval_request_id; // [12] <open=suppress, name="retrieval_request_id">;
	public char[] central_proc_date; // [4] <open=suppress, name="central_proc_date">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_51_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_51_0(String content) {
		super(content);
	}

	public visa_b2_51_0() {
		super();
	}

	public visa_b2_51_0(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("account_number", 16);
		addElement("account_extension", 3);
		// ARN_STR acquirer_ref_number <name="acquirer_ref_number">;;
		addElement("ARN_01", 1);
		addElement("ARN_02", 6);
		addElement("ARN_03", 4);
		addElement("ARN_04", 11);
		addElement("ARN_05", 1);

		addElement("acquirer_bus_id", 8);
		addElement("purchase_date", 4);
		addElement("transaction_amount", 12);
		addElement("transaction_currency", 3);
		addElement("merchant_name", 25);
		addElement("merchant_city", 13);
		addElement("merchant_country", 3);
		addElement("merchant_category", 4);
		addElement("merchant_zip_code", 5);
		addElement("merchant_province", 3);
		addElement("issuer_control_num", 9);
		addElement("reason_code", 2);
		addElement("settlement_flag", 1);
		addElement("national_reimbrs_fee", 12);
		addElement("atm_account_selection", 1);
		addElement("retrieval_request_id", 12);
		addElement("central_proc_date", 4);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		account_number = getElement("account_number");
		account_extension = getElement("account_extension");
		acquirer_bus_id = getElement("acquirer_bus_id");
		purchase_date = getElement("purchase_date");
		transaction_amount = getElement("transaction_amount");
		transaction_currency = getElement("transaction_currency");
		merchant_name = getElement("merchant_name");
		merchant_city = getElement("merchant_city");
		merchant_country = getElement("merchant_country");
		merchant_category = getElement("merchant_category");
		merchant_zip_code = getElement("merchant_zip_code");
		merchant_province = getElement("merchant_province");
		issuer_control_num = getElement("issuer_control_num");
		reason_code = getElement("reason_code");
		settlement_flag = getElement("settlement_flag");
		national_reimbrs_fee = getElement("national_reimbrs_fee");
		atm_account_selection = getElement("atm_account_selection");
		retrieval_request_id = getElement("retrieval_request_id");
		central_proc_date = getElement("central_proc_date");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_51_0_ST";
	}
}