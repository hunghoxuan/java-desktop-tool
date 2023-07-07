package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _account_number;
	public AsciiField _account_extension;
	public AsciiField _floor_limit_ind;
	public AsciiField _cwb_crb_ind;
	public AsciiField _pcas;
	public AsciiField _acquirer_bus_id;
	public AsciiField _purchase_date;
	public AsciiField _destination_amount;
	public AsciiField _destination_currency;
	public AsciiField _source_amount;
	public AsciiField _source_currency;
	public AsciiField _merchant_name;
	public AsciiField _merchant_city;
	public AsciiField _merchant_country;
	public AsciiField _merchant_category;
	public AsciiField _merchant_zip_code;
	public AsciiField _merchant_province;
	public AsciiField _payment_service;
	public AsciiField _Number_of_payment_forms;
	public AsciiField _usage_code;
	public AsciiField _reason_code;
	public AsciiField _settlement_flag;
	public AsciiField _authorisation_ind;
	public AsciiField _authorisation_code;
	public AsciiField _pos_terminal_cap;
	public AsciiField _internal_fee_ind;
	public AsciiField _cardholder_id_meth;
	public AsciiField _collection_flag;
	public AsciiField _pos_entry_mode;
	public AsciiField _central_proc_date;
	public AsciiField _reimbursement_attr;
	public AsciiField _validation_record_type;
	public AsciiField _validation_field_number;
	public AsciiField _validation_error_code;
	public AsciiField _filler;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] account_extension; // [3] <open=suppress, name="account_extension">;
	public char[] floor_limit_ind; // [1] <open=suppress, name="floor_limit_ind">;
	public char[] cwb_crb_ind; // [1] <open=suppress, name="cwb_crb_ind">;
	public char[] pcas; // [1] <open=suppress, name="pcas">;
	public char[] acquirer_bus_id; // [8] <open=suppress, name="acquirer_bus_id">;
	public char[] purchase_date; // [4] <open=suppress, name="purchase_date">;
	public char[] destination_amount; // [12] <open=suppress, name="destination_amount">;
	public char[] destination_currency; // [3] <open=suppress, name="destination_currency">;
	public char[] source_amount; // [12] <open=suppress, name="source_amount">;
	public char[] source_currency; // [3] <open=suppress, name="source_currency">;
	public char[] merchant_name; // [25] <open=suppress, name="merchant_name">;
	public char[] merchant_city; // [13] <open=suppress, name="merchant_city">;
	public char[] merchant_country; // [3] <open=suppress, name="merchant_country">;
	public char[] merchant_category; // [4] <open=suppress, name="merchant_category">;
	public char[] merchant_zip_code; // [5] <open=suppress, name="merchant_zip_code">;
	public char[] merchant_province; // [3] <open=suppress, name="merchant_province">;
	public char[] payment_service; // [1] <open=suppress, name="payment_service">;
	public char[] Number_of_payment_forms; // [1] <open=suppress, name="Number_of_payment_forms">; //;Visa 13.2 - New
											// field - Changed from Reserved_1
	public char[] usage_code; // [1] <open=suppress, name="usage_code">;
	public char[] reason_code; // [2] <open=suppress, name="reason_code">;
	public char[] settlement_flag; // [1] <open=suppress, name="settlement_flag">;
	public char[] authorisation_ind; // [1] <open=suppress, name="authorisation_ind">;
	public char[] authorisation_code; // [6] <open=suppress, name="authorisation_code">;
	public char[] pos_terminal_cap; // [1] <open=suppress, name="pos_terminal_cap">;
	public char[] internal_fee_ind; // [1] <open=suppress, name="internal_fee_ind">;
	public char[] cardholder_id_meth; // [1] <open=suppress, name="cardholder_id_meth">;
	public char[] collection_flag; // [1] <open=suppress, name="collection_flag">;
	public char[] pos_entry_mode; // [2] <open=suppress, name="pos_entry_mode">;
	public char[] central_proc_date; // [4] <open=suppress, name="central_proc_date">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;
	public char[] validation_record_type; // [1] <open=suppress, name="validation_record_type">;
	public char[] validation_field_number; // [2] <open=suppress, name="validation_field_number">;
	public char[] validation_error_code; // [2] <open=suppress, name="validation_error_code">;
	public char[] filler; // [15] <open=suppress, name="filler">;

	public visa_b2_05_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_0(String content) {
		super(content);
	}

	public visa_b2_05_0() {
		super();
	}

	public visa_b2_05_0(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct (int ifReturn){;
		if (ifReturn != 1) {
			addElement("trans_code", 2);
			addElement("trans_code_qualif", 1);
			addElement("trans_comp_seq", 1);
		}
		addElement("account_number", 16);
		addElement("account_extension", 3);
		addElement("floor_limit_ind", 1);
		addElement("cwb_crb_ind", 1);
		addElement("pcas", 1);

		// ARN_STR acquirer_ref_number <name="acquirer_ref_number">;;
		addElement("ARN_01", 1);
		addElement("ARN_02", 6);
		addElement("ARN_03", 4);
		addElement("ARN_04", 11);
		addElement("ARN_05", 1);

		addElement("acquirer_bus_id", 8);
		addElement("purchase_date", 4);
		addElement("destination_amount", 12);
		addElement("destination_currency", 3);
		addElement("source_amount", 12);
		addElement("source_currency", 3);
		addElement("merchant_name", 25);
		addElement("merchant_city", 13);
		addElement("merchant_country", 3);
		addElement("merchant_category", 4);
		addElement("merchant_zip_code", 5);
		addElement("merchant_province", 3);
		addElement("payment_service", 1);
		addElement("Number_of_payment_forms", 1);
		addElement("usage_code", 1);
		addElement("reason_code", 2);
		addElement("settlement_flag", 1);
		addElement("authorisation_ind", 1);
		addElement("authorisation_code", 6);
		addElement("pos_terminal_cap", 1);
		addElement("internal_fee_ind", 1);
		addElement("cardholder_id_meth", 1);
		addElement("collection_flag", 1);
		addElement("pos_entry_mode", 2);
		addElement("central_proc_date", 4);
		addElement("reimbursement_attr", 1);
		// if(ReadByte() != 0x0d && ReadByte() != 0x0a){
		addElement("validation_record_type", 1);
		addElement("validation_field_number", 2);
		addElement("validation_error_code", 2);
		addElement("filler", 15);
		// FSkip(151);;
		// }

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		account_number = getElement("account_number");
		account_extension = getElement("account_extension");
		floor_limit_ind = getElement("floor_limit_ind");
		cwb_crb_ind = getElement("cwb_crb_ind");
		pcas = getElement("pcas");
		acquirer_bus_id = getElement("acquirer_bus_id");
		purchase_date = getElement("purchase_date");
		destination_amount = getElement("destination_amount");
		destination_currency = getElement("destination_currency");
		source_amount = getElement("source_amount");
		source_currency = getElement("source_currency");
		merchant_name = getElement("merchant_name");
		merchant_city = getElement("merchant_city");
		merchant_country = getElement("merchant_country");
		merchant_category = getElement("merchant_category");
		merchant_zip_code = getElement("merchant_zip_code");
		merchant_province = getElement("merchant_province");
		payment_service = getElement("payment_service");
		Number_of_payment_forms = getElement("Number_of_payment_forms");
		usage_code = getElement("usage_code");
		reason_code = getElement("reason_code");
		settlement_flag = getElement("settlement_flag");
		authorisation_ind = getElement("authorisation_ind");
		authorisation_code = getElement("authorisation_code");
		pos_terminal_cap = getElement("pos_terminal_cap");
		internal_fee_ind = getElement("internal_fee_ind");
		cardholder_id_meth = getElement("cardholder_id_meth");
		collection_flag = getElement("collection_flag");
		pos_entry_mode = getElement("pos_entry_mode");
		central_proc_date = getElement("central_proc_date");
		reimbursement_attr = getElement("reimbursement_attr");
		validation_record_type = getElement("validation_record_type");
		validation_field_number = getElement("validation_field_number");
		validation_error_code = getElement("validation_error_code");
		filler = getElement("filler");

	}

	@Override
	public String getDescription() {
		return "visa_b2_05_0_ST";
	}
}