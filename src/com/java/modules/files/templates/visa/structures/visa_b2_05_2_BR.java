package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_2_BR extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _country_code;
	public AsciiField _reserved_3;
	public AsciiField _settlement_type;
	public AsciiField _national_reimbursement_fee;
	public AsciiField _central_processing_date;
	public AsciiField _installment_payment_count;
	public AsciiField _merchant_identifier;
	public AsciiField _purchase_identifier;
	public AsciiField _reserved_4;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [12] <open=suppress, name="reserved_1">;
	public char[] country_code; // [3] <open=suppress, name="country_code">;
	public char[] reserved_3; // [3] <open=suppress, name="reserved_3">;
	public char[] settlement_type; // [3] <open=suppress, name="settlement_type">;
	public char[] national_reimbursement_fee; // [10] <open=suppress, name="national_reimbursement_fee">;
	public char[] central_processing_date; // [4] <open=suppress, name="central_processing_date">;
	public char[] installment_payment_count; // [2] <open=suppress, name="installment_payment_count">;
	public char[] merchant_identifier; // [5] <open=suppress, name="merchant_identifier">;
	public char[] purchase_identifier; // [1] <open=suppress, name="purchase_identifier">;
	public char[] reserved_4; // [121] <open=suppress, name="reserved_4">;

	public visa_b2_05_2_BR(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_2_BR(String content) {
		super(content);
	}

	public visa_b2_05_2_BR() {
		super();
	}

	public visa_b2_05_2_BR(int ifReturn) {
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
		addElement("reserved_1", 12);
		addElement("country_code", 3);
		addElement("reserved_3", 3);
		addElement("settlement_type", 3);
		addElement("national_reimbursement_fee", 10);
		addElement("central_processing_date", 4);
		addElement("installment_payment_count", 2);
		addElement("merchant_identifier", 5);
		addElement("purchase_identifier", 1);
		addElement("reserved_4", 121);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		country_code = getElement("country_code");
		reserved_3 = getElement("reserved_3");
		settlement_type = getElement("settlement_type");
		national_reimbursement_fee = getElement("national_reimbursement_fee");
		central_processing_date = getElement("central_processing_date");
		installment_payment_count = getElement("installment_payment_count");
		merchant_identifier = getElement("merchant_identifier");
		purchase_identifier = getElement("purchase_identifier");
		reserved_4 = getElement("reserved_4");

	}

	@Override
	public String getDescription() {
		return "TCR 2 - BR";
	}
}