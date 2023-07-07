package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_CORPLG extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _valet_parking_charges;
	public AsciiField _minibar_charges;
	public AsciiField _laundry_charges;
	public AsciiField _telephone_charges;
	public AsciiField _giftshop_purchases;
	public AsciiField _movie_charges;
	public AsciiField _buss_center_charges;
	public AsciiField _healthclub_charges;
	public AsciiField _other_charges;
	public AsciiField _total_non_room_charges;
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
	public char[] valet_parking_charges; // [12] <open=suppress, name="valet_parking_charges">;
	public char[] minibar_charges; // [12] <open=suppress, name="minibar_charges">;
	public char[] laundry_charges; // [12] <open=suppress, name="laundry_charges">;
	public char[] telephone_charges; // [12] <open=suppress, name="telephone_charges">;
	public char[] giftshop_purchases; // [12] <open=suppress, name="giftshop_purchases">;
	public char[] movie_charges; // [12] <open=suppress, name="movie_charges">;
	public char[] buss_center_charges; // [12] <open=suppress, name="buss_center_charges">;
	public char[] healthclub_charges; // [12] <open=suppress, name="healthclub_charges">;
	public char[] other_charges; // [12] <open=suppress, name="other_charges">;
	public char[] total_non_room_charges; // [12] <open=suppress, name="total_non_room_charges">;
	public char[] reserved; // [7] <open=suppress, name="reserved">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_CORPLG(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_CORPLG(String content) {
		super(content);
	}

	public visa_b2_50_0_CORPLG() {
		super();
	}

	public visa_b2_50_0_CORPLG(int ifReturn) {
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
		addElement("valet_parking_charges", 12);
		addElement("minibar_charges", 12);
		addElement("laundry_charges", 12);
		addElement("telephone_charges", 12);
		addElement("giftshop_purchases", 12);
		addElement("movie_charges", 12);
		addElement("buss_center_charges", 12);
		addElement("healthclub_charges", 12);
		addElement("other_charges", 12);
		addElement("total_non_room_charges", 12);
		addElement("reserved", 7);
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
		valet_parking_charges = getElement("valet_parking_charges");
		minibar_charges = getElement("minibar_charges");
		laundry_charges = getElement("laundry_charges");
		telephone_charges = getElement("telephone_charges");
		giftshop_purchases = getElement("giftshop_purchases");
		movie_charges = getElement("movie_charges");
		buss_center_charges = getElement("buss_center_charges");
		healthclub_charges = getElement("healthclub_charges");
		other_charges = getElement("other_charges");
		total_non_room_charges = getElement("total_non_room_charges");
		reserved = getElement("reserved");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_CORPLG_ST";
	}
}