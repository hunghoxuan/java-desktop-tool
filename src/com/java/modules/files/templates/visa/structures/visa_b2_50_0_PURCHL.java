package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_PURCHL extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _item_commodity_code;
	public AsciiField _item_descriptor;
	public AsciiField _product_code;
	public AsciiField _quantity;
	public AsciiField _unit_of_measure;
	public AsciiField _unit_cost;
	public AsciiField _tax_amount;
	public AsciiField _tax_rate;
	public AsciiField _discount;
	public AsciiField _line_item_total;
	public AsciiField _item_detail_ind;
	public AsciiField _reimburs_attrib;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] item_seq_no; // [3] <open=suppress, name="item_seq_no">;
	public char[] item_commodity_code; // [12] <open=suppress, name="item_commodity_code">;
	public char[] item_descriptor; // [26] <open=suppress, name="item_descriptor">;
	public char[] product_code; // [12] <open=suppress, name="product_code">;
	public char[] quantity; // [12] <open=suppress, name="quantity">;
	public char[] unit_of_measure; // [12] <open=suppress, name="unit_of_measure">;
	public char[] unit_cost; // [12] <open=suppress, name="unit_cost">;
	public char[] tax_amount; // [12] <open=suppress, name="tax_amount">;
	public char[] tax_rate; // [4] <open=suppress, name="tax_rate">;
	public char[] discount; // [12] <open=suppress, name="discount">;
	public char[] line_item_total; // [12] <open=suppress, name="line_item_total">;
	public char[] item_detail_ind; // [1] <open=suppress, name="item_detail_ind">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_PURCHL(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_PURCHL(String content) {
		super(content);
	}

	public visa_b2_50_0_PURCHL() {
		super();
	}

	public visa_b2_50_0_PURCHL(int ifReturn) {
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
		addElement("item_commodity_code", 12);
		addElement("item_descriptor", 26);
		addElement("product_code", 12);
		addElement("quantity", 12);
		addElement("unit_of_measure", 12);
		addElement("unit_cost", 12);
		addElement("tax_amount", 12);
		addElement("tax_rate", 4);
		addElement("discount", 12);
		addElement("line_item_total", 12);
		addElement("item_detail_ind", 1);
		addElement("reimburs_attrib", 1);

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
		item_commodity_code = getElement("item_commodity_code");
		item_descriptor = getElement("item_descriptor");
		product_code = getElement("product_code");
		quantity = getElement("quantity");
		unit_of_measure = getElement("unit_of_measure");
		unit_cost = getElement("unit_cost");
		tax_amount = getElement("tax_amount");
		tax_rate = getElement("tax_rate");
		discount = getElement("discount");
		line_item_total = getElement("line_item_total");
		item_detail_ind = getElement("item_detail_ind");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_PURCHL_ST";
	}
}