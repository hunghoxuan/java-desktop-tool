package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _VCR_record_identifier;
	public AsciiField _dispute_status;
	public AsciiField _dispute_tran_code;
	public AsciiField _dispute_tran_qualifier;
	public AsciiField _reserved;
	public AsciiField _account_number;
	public AsciiField _account_number_ext;
	public AsciiField _Acquirer_Ref_Number;
	public AsciiField _Purchase_Date_MMDD;
	public AsciiField _Source_Amount;
	public AsciiField _Source_Currency;
	public AsciiField _Merchant_Name;
	public AsciiField _Merchant_City;
	public AsciiField _Merchant_Country;
	public AsciiField _Merchant_Category;
	public AsciiField _Merchant_Province;
	public AsciiField _Merchant_Zip_Code;
	public AsciiField _Payment_Service;
	public AsciiField _Authorisation_Code;
	public AsciiField _POS_Entry_Mode;
	public AsciiField _Central_Proc_Date;
	public AsciiField _Card_Acceptor_ID;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] VCR_record_identifier; // [3] <open=suppress, name="VCR_record_identifier">;
	public char[] dispute_status; // [2] <open=suppress, name="dispute_status">;
	public char[] dispute_tran_code; // [2] <open=suppress, name="dispute_tran_code">;
	public char[] dispute_tran_qualifier; // [1] <open=suppress, name="dispute_tran_qualifier">;
	public char[] reserved; // [1] <open=suppress, name="reserved">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] account_number_ext; // [3] <open=suppress, name="account_number_ext">;
	public char[] Acquirer_Ref_Number; // [23] <open=suppress, name="Acquirer_Ref_Number">;
	public char[] Purchase_Date_MMDD; // [4] <open=suppress, name="Purchase_Date_MMDD">;
	public char[] Source_Amount; // [12] <open=suppress, name="Source_Amount">;
	public char[] Source_Currency; // [3] <open=suppress, name="Source_Currency">;
	public char[] Merchant_Name; // [25] <open=suppress, name="Merchant_Name">;
	public char[] Merchant_City; // [13] <open=suppress, name="Merchant_City">;
	public char[] Merchant_Country; // [3] <open=suppress, name="Merchant_Country">;
	public char[] Merchant_Category; // [4] <open=suppress, name="Merchant_Category">;
	public char[] Merchant_Province; // [3] <open=suppress, name="Merchant_Province">;
	public char[] Merchant_Zip_Code; // [5] <open=suppress, name="Merchant_Zip_Code">;
	public char[] Payment_Service; // [1] <open=suppress, name="Payment_Service">;
	public char[] Authorisation_Code; // [6] <open=suppress, name="Authorisation_Code">;
	public char[] POS_Entry_Mode; // [2] <open=suppress, name="POS_Entry_Mode">;
	public char[] Central_Proc_Date; // [4] <open=suppress, name="Central_Proc_Date">;
	public char[] Card_Acceptor_ID; // [15] <open=suppress, name="Card_Acceptor_ID">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0(String content) {
		super(content);
	}

	public visa_b2_33_0() {
		super();
	}

	public visa_b2_33_0(int ifReturn) {
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
		addElement("VCR_record_identifier", 3);
		addElement("dispute_status", 2);
		addElement("dispute_tran_code", 2);
		addElement("dispute_tran_qualifier", 1);
		addElement("reserved", 1);
		addElement("account_number", 16);
		addElement("account_number_ext", 3);
		addElement("Acquirer_Ref_Number", 23);
		addElement("Purchase_Date_MMDD", 4);
		addElement("Source_Amount", 12);
		addElement("Source_Currency", 3);
		addElement("Merchant_Name", 25);
		addElement("Merchant_City", 13);
		addElement("Merchant_Country", 3);
		addElement("Merchant_Category", 4);
		addElement("Merchant_Province", 3);
		addElement("Merchant_Zip_Code", 5);
		addElement("Payment_Service", 1);
		addElement("Authorisation_Code", 6);
		addElement("POS_Entry_Mode", 2);
		addElement("Central_Proc_Date", 4);
		addElement("Card_Acceptor_ID", 15);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		VCR_record_identifier = getElement("VCR_record_identifier");
		dispute_status = getElement("dispute_status");
		dispute_tran_code = getElement("dispute_tran_code");
		dispute_tran_qualifier = getElement("dispute_tran_qualifier");
		reserved = getElement("reserved");
		account_number = getElement("account_number");
		account_number_ext = getElement("account_number_ext");
		Acquirer_Ref_Number = getElement("Acquirer_Ref_Number");
		Purchase_Date_MMDD = getElement("Purchase_Date_MMDD");
		Source_Amount = getElement("Source_Amount");
		Source_Currency = getElement("Source_Currency");
		Merchant_Name = getElement("Merchant_Name");
		Merchant_City = getElement("Merchant_City");
		Merchant_Country = getElement("Merchant_Country");
		Merchant_Category = getElement("Merchant_Category");
		Merchant_Province = getElement("Merchant_Province");
		Merchant_Zip_Code = getElement("Merchant_Zip_Code");
		Payment_Service = getElement("Payment_Service");
		Authorisation_Code = getElement("Authorisation_Code");
		POS_Entry_Mode = getElement("POS_Entry_Mode");
		Central_Proc_Date = getElement("Central_Proc_Date");
		Card_Acceptor_ID = getElement("Card_Acceptor_ID");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_ST";
	}
}