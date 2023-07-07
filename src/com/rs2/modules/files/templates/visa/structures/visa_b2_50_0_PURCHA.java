package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_PURCHA extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _discount_amount;
	public AsciiField _shipping_amount;
	public AsciiField _duty_amount;
	public AsciiField _dest_postal_code;
	public AsciiField _ship_postal_code;
	public AsciiField _dest_country_code;
	public AsciiField _vat_ref_no;
	public AsciiField _order_date;
	public AsciiField _acct_number;
	public AsciiField _acct_number_ext;
	public AsciiField _tax_amount_freight;
	public AsciiField _tax_rate_freight;
	public AsciiField _auth_code;
	public AsciiField _reserved;
	public AsciiField _invoice_level_disc_code;
	public AsciiField _tax_treatments;
	public AsciiField _discount_amount_signage;
	public AsciiField _freight_amount_signage;
	public AsciiField _duty_amount_signage;
	public AsciiField _vat_tax_amount_signage;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] item_seq_no; // [3] <open=suppress, name="item_seq_no">;
	public char[] discount_amount; // [12] <open=suppress, name="discount_amount">;
	public char[] shipping_amount; // [12] <open=suppress, name="shipping_amount">;
	public char[] duty_amount; // [12] <open=suppress, name="duty_amount">;
	public char[] dest_postal_code; // [10] <open=suppress, name="dest_postal_code">;
	public char[] ship_postal_code; // [10] <open=suppress, name="ship_postal_code">;
	public char[] dest_country_code; // [3] <open=suppress, name="dest_country_code">;
	public char[] vat_ref_no; // [15] <open=suppress, name="vat_ref_no">;
	public char[] order_date; // [6] <open=suppress, name="order_date">;
	public char[] acct_number; // [16] <open=suppress, name="acct_number">;
	public char[] acct_number_ext; // [3] <open=suppress, name="acct_number_ext">;
	public char[] tax_amount_freight; // [12] <open=suppress, name="tax_amount_freight">;
	public char[] tax_rate_freight; // [4] <open=suppress, name="tax_rate_freight">;
	public char[] auth_code; // [6] <open=suppress, name="auth_code">;
	public char[] reserved; // [1] <open=suppress, name="reserved">;
	public char[] invoice_level_disc_code; // [1] <open=suppress, name="invoice_level_disc_code">;
	public char[] tax_treatments; // [1] <open=suppress, name="tax_treatments">;
	public char[] discount_amount_signage; // [1] <open=suppress, name="discount_amount_signage">;
	public char[] freight_amount_signage; // [1] <open=suppress, name="freight_amount_signage">;
	public char[] duty_amount_signage; // [1] <open=suppress, name="duty_amount_signage">;
	public char[] vat_tax_amount_signage; // [1] <open=suppress, name="vat_tax_amount_signage">;

	public visa_b2_50_0_PURCHA(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_PURCHA(String content) {
		super(content);
	}

	public visa_b2_50_0_PURCHA() {
		super();
	}

	public visa_b2_50_0_PURCHA(int ifReturn) {
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
		addElement("service_id", 6);
		addElement("message_id", 15);
		addElement("item_seq_no", 3);
		addElement("discount_amount", 12);
		addElement("shipping_amount", 12);
		addElement("duty_amount", 12);
		addElement("dest_postal_code", 10);
		addElement("ship_postal_code", 10);
		addElement("dest_country_code", 3);
		addElement("vat_ref_no", 15);
		addElement("order_date", 6);
		addElement("acct_number", 16);
		addElement("acct_number_ext", 3);
		addElement("tax_amount_freight", 12);
		addElement("tax_rate_freight", 4);
		addElement("auth_code", 6);
		addElement("reserved", 1);
		addElement("invoice_level_disc_code", 1);
		addElement("tax_treatments", 1);
		addElement("discount_amount_signage", 1);
		addElement("freight_amount_signage", 1);
		addElement("duty_amount_signage", 1);
		addElement("vat_tax_amount_signage", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		service_id = getElement("service_id");
		message_id = getElement("message_id");
		item_seq_no = getElement("item_seq_no");
		discount_amount = getElement("discount_amount");
		shipping_amount = getElement("shipping_amount");
		duty_amount = getElement("duty_amount");
		dest_postal_code = getElement("dest_postal_code");
		ship_postal_code = getElement("ship_postal_code");
		dest_country_code = getElement("dest_country_code");
		vat_ref_no = getElement("vat_ref_no");
		order_date = getElement("order_date");
		acct_number = getElement("acct_number");
		acct_number_ext = getElement("acct_number_ext");
		tax_amount_freight = getElement("tax_amount_freight");
		tax_rate_freight = getElement("tax_rate_freight");
		auth_code = getElement("auth_code");
		reserved = getElement("reserved");
		invoice_level_disc_code = getElement("invoice_level_disc_code");
		tax_treatments = getElement("tax_treatments");
		discount_amount_signage = getElement("discount_amount_signage");
		freight_amount_signage = getElement("freight_amount_signage");
		duty_amount_signage = getElement("duty_amount_signage");
		vat_tax_amount_signage = getElement("vat_tax_amount_signage");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_PURCHA_ST";
	}
}