package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_4 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _business_format_code;
	public AsciiField _debit_product_code;
	public AsciiField _contact_info;
	public AsciiField _adjustment_proc_ind;
	public AsciiField _message_reason_code;
	public AsciiField _surcharge_amount;
	public AsciiField _surcharge_cr_db_ind;
	public AsciiField _visa_interal_use;
	public AsciiField _reserved_2;
	public AsciiField _surcharge_amount_cbc;
	public AsciiField _reserved_3;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [10] <open=suppress, name="reserved_1">;
	public char[] business_format_code; // [2] <open=suppress, name="business_format_code">;
	public char[] debit_product_code; // [4] <open=suppress, name="debit_product_code">;
	public char[] contact_info; // [25] <open=suppress, name="contact_info">;
	public char[] adjustment_proc_ind; // [1] <open=suppress, name="adjustment_proc_ind">;
	public char[] message_reason_code; // [4] <open=suppress, name="message_reason_code">;
	public char[] surcharge_amount; // [8] <open=suppress, name="surcharge_amount">;
	public char[] surcharge_cr_db_ind; // [2] <open=suppress, name="surcharge_cr_db_ind">;
	public char[] visa_interal_use; // [16] <open=suppress, name="visa_interal_use">;
	public char[] reserved_2; // [27] <open=suppress, name="reserved_2">;
	public char[] surcharge_amount_cbc; // [8] <open=suppress, name="surcharge_amount_cbc">;
	public char[] reserved_3; // [57] <open=suppress, name="reserved_3">;

	public visa_b2_05_4(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_4(String content) {
		super(content);
	}

	public visa_b2_05_4() {
		super();
	}

	public visa_b2_05_4(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("reserved_1", 10);
		addElement("business_format_code", 2);
		addElement("debit_product_code", 4);
		addElement("contact_info", 25);
		addElement("adjustment_proc_ind", 1);
		addElement("message_reason_code", 4);
		addElement("surcharge_amount", 8);
		addElement("surcharge_cr_db_ind", 2);
		addElement("visa_interal_use", 16);
		addElement("reserved_2", 27);
		addElement("surcharge_amount_cbc", 8);
		addElement("reserved_3", 57);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		business_format_code = getElement("business_format_code");
		debit_product_code = getElement("debit_product_code");
		contact_info = getElement("contact_info");
		adjustment_proc_ind = getElement("adjustment_proc_ind");
		message_reason_code = getElement("message_reason_code");
		surcharge_amount = getElement("surcharge_amount");
		surcharge_cr_db_ind = getElement("surcharge_cr_db_ind");
		visa_interal_use = getElement("visa_interal_use");
		reserved_2 = getElement("reserved_2");
		surcharge_amount_cbc = getElement("surcharge_amount_cbc");
		reserved_3 = getElement("reserved_3");

	}

	@Override
	public String getDescription() {
		return "TCR4 - SMS Data";
	}
}