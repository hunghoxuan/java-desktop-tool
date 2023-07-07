package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_0_TR extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _account_number;
	public AsciiField _account_extension;
	public AsciiField _acquirer_bus_id;
	public AsciiField _response_code;
	public AsciiField _purchase_date;
	public AsciiField _merchant_name;
	public AsciiField _merchant_city;
	public AsciiField _merchant_country;
	public AsciiField _merchant_category;
	public AsciiField _merchant_province;
	public AsciiField _fraud_amount;
	public AsciiField _fraud_currency;
	public AsciiField _central_proc_date;
	public AsciiField _authorisation_type;
	public AsciiField _notification_code;
	public AsciiField _account_sequence;
	public AsciiField _reserved;
	public AsciiField _fraud_reason;
	public AsciiField _card_expiry_date;
	public AsciiField _merchant_zip_code;
	public AsciiField _investigation_status;
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
	public char[] purchase_date; // [4] <open=suppress, name="purchase_date">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_city; // [13] <open=suppress, name="merchant_city">;
	public char[] merchant_country; // [3] <open=suppress, name="merchant_country">;
	public char[] merchant_category; // [4] <open=suppress, name="merchant_category">;
	public char[] merchant_province; // [3] <open=suppress, name="merchant_province">;
	public char[] fraud_amount; // [12] <open=suppress, name="fraud_amount">;
	public char[] fraud_currency; // [3] <open=suppress, name="fraud_currency">;
	public char[] central_proc_date; // [4] <open=suppress, name="central_proc_date">;
	public char[] authorisation_type; // [1] <open=suppress, name="authorisation_type">;
	public char[] notification_code; // [1] <open=suppress, name="notification_code">;
	public char[] account_sequence; // [4] <open=suppress, name="account_sequence">;
	public char[] reserved; // [1] <open=suppress, name="reserved">;
	public char[] fraud_reason; // [1] <open=suppress, name="fraud_reason">;
	public char[] card_expiry_date; // [4] <open=suppress, name="card_expiry_date">;
	public char[] merchant_zip_code; // [10] <open=suppress, name="merchant_zip_code">;
	public char[] investigation_status; // [2] <open=suppress, name="investigation_status">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_40_0_TR(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_0_TR(String content) {
		super(content);
	}

	public visa_b2_40_0_TR() {
		super();
	}

	public visa_b2_40_0_TR(int ifReturn) {
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
		addElement("purchase_date", 4);
		addElement("merchant_name", 25);
		addElement("merchant_city", 13);
		addElement("merchant_country", 3);
		addElement("merchant_category", 4);
		addElement("merchant_province", 3);
		addElement("fraud_amount", 12);
		addElement("fraud_currency", 3);
		addElement("central_proc_date", 4);
		addElement("authorisation_type", 1);
		addElement("notification_code", 1);
		addElement("account_sequence", 4);
		addElement("reserved", 1);
		addElement("fraud_reason", 1);
		addElement("card_expiry_date", 4);
		addElement("merchant_zip_code", 10);
		addElement("investigation_status", 2);
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
		purchase_date = getElement("purchase_date");
		merchant_name = getElement("merchant_name");
		merchant_city = getElement("merchant_city");
		merchant_country = getElement("merchant_country");
		merchant_category = getElement("merchant_category");
		merchant_province = getElement("merchant_province");
		fraud_amount = getElement("fraud_amount");
		fraud_currency = getElement("fraud_currency");
		central_proc_date = getElement("central_proc_date");
		authorisation_type = getElement("authorisation_type");
		notification_code = getElement("notification_code");
		account_sequence = getElement("account_sequence");
		reserved = getElement("reserved");
		fraud_reason = getElement("fraud_reason");
		card_expiry_date = getElement("card_expiry_date");
		merchant_zip_code = getElement("merchant_zip_code");
		investigation_status = getElement("investigation_status");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_0_TR_ST";
	}
}