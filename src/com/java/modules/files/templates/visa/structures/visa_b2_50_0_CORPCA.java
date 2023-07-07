package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_CORPCA extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _auto_towing_charges;
	public AsciiField _reg_mileage_charges;
	public AsciiField _extra_mileage_charges;
	public AsciiField _late_return_charges;
	public AsciiField _car_returned_to_location;
	public AsciiField _total_tax_vat;
	public AsciiField _telephone_charges;
	public AsciiField _other_charges;
	public AsciiField _corporate_id;
	public AsciiField _reserved;
	public AsciiField _reimburs_attrib;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] item_seq_no; // [3] <open=suppress, name="item_seq_no">;
	public char[] auto_towing_charges; // [12] <open=suppress, name="auto_towing_charges">;
	public char[] reg_mileage_charges; // [12] <open=suppress, name="reg_mileage_charges">;
	public char[] extra_mileage_charges; // [12] <open=suppress, name="extra_mileage_charges">;
	public char[] late_return_charges; // [12] <open=suppress, name="late_return_charges">;
	public char[] car_returned_to_location; // [25] <open=suppress, name="car_returned_to_location">;
	public char[] total_tax_vat; // [12] <open=suppress, name="total_tax_vat">;
	public char[] telephone_charges; // [12] <open=suppress, name="telephone_charges">;
	public char[] other_charges; // [12] <open=suppress, name="other_charges">;
	public char[] corporate_id; // [12] <open=suppress, name="corporate_id">;
	public char[] reserved; // [6] <open=suppress, name="reserved">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_CORPCA(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_CORPCA(String content) {
		super(content);
	}

	public visa_b2_50_0_CORPCA() {
		super();
	}

	public visa_b2_50_0_CORPCA(int ifReturn) {
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
		addElement("auto_towing_charges", 12);
		addElement("reg_mileage_charges", 12);
		addElement("extra_mileage_charges", 12);
		addElement("late_return_charges", 12);
		addElement("car_returned_to_location", 25);
		addElement("total_tax_vat", 12);
		addElement("telephone_charges", 12);
		addElement("other_charges", 12);
		addElement("corporate_id", 12);
		addElement("reserved", 6);
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
		auto_towing_charges = getElement("auto_towing_charges");
		reg_mileage_charges = getElement("reg_mileage_charges");
		extra_mileage_charges = getElement("extra_mileage_charges");
		late_return_charges = getElement("late_return_charges");
		car_returned_to_location = getElement("car_returned_to_location");
		total_tax_vat = getElement("total_tax_vat");
		telephone_charges = getElement("telephone_charges");
		other_charges = getElement("other_charges");
		corporate_id = getElement("corporate_id");
		reserved = getElement("reserved");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_CORPCA_ST";
	}
}