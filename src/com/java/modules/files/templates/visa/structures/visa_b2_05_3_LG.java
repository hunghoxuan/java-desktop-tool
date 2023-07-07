package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_LG extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _reserved_2;
	public AsciiField _no_show_id;
	public AsciiField _extra_charges;
	public AsciiField _reserved_3;
	public AsciiField _check_in_date;
	public AsciiField _daily_room_rate;
	public AsciiField _total_tax;
	public AsciiField _prepaid_expenses;
	public AsciiField _food_beverage_charges;
	public AsciiField _folio_cash_advances;
	public AsciiField _total_nights;
	public AsciiField _total_tax_room;
	public AsciiField _reserved_4;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] reserved_2; // [8] <open=suppress, name="reserved_2">;
	public char[] no_show_id; // [1] <open=suppress, name="no_show_id">;
	public char[] extra_charges; // [6] <open=suppress, name="extra_charges">;
	public char[] reserved_3; // [4] <open=suppress, name="reserved_3">;
	public char[] check_in_date; // [6] <open=suppress, name="check_in_date">;
	public char[] daily_room_rate; // [12] <open=suppress, name="daily_room_rate">;
	public char[] total_tax; // [12] <open=suppress, name="total_tax">;
	public char[] prepaid_expenses; // [12] <open=suppress, name="prepaid_expenses">;
	public char[] food_beverage_charges; // [12] <open=suppress, name="food_beverage_charges">;
	public char[] folio_cash_advances; // [12] <open=suppress, name="folio_cash_advances">;
	public char[] total_nights; // [2] <open=suppress, name="total_nights">;
	public char[] total_tax_room; // [12] <open=suppress, name="total_tax_room">;
	public char[] reserved_4; // [51] <open=suppress, name="reserved_4">;

	public visa_b2_05_3_LG(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_LG(String content) {
		super(content);
	}

	public visa_b2_05_3_LG() {
		super();
	}

	public visa_b2_05_3_LG(int ifReturn) {
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
		addElement("reserved_1", 11);
		addElement("fast_funds_indicator", 1);
		addElement("bus_format_code", 2);
		addElement("reserved_2", 8);
		addElement("no_show_id", 1);
		addElement("extra_charges", 6);
		addElement("reserved_3", 4);
		addElement("check_in_date", 6);
		addElement("daily_room_rate", 12);
		addElement("total_tax", 12);
		addElement("prepaid_expenses", 12);
		addElement("food_beverage_charges", 12);
		addElement("folio_cash_advances", 12);
		addElement("total_nights", 2);
		addElement("total_tax_room", 12);
		addElement("reserved_4", 51);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		fast_funds_indicator = getElement("fast_funds_indicator");
		bus_format_code = getElement("bus_format_code");
		reserved_2 = getElement("reserved_2");
		no_show_id = getElement("no_show_id");
		extra_charges = getElement("extra_charges");
		reserved_3 = getElement("reserved_3");
		check_in_date = getElement("check_in_date");
		daily_room_rate = getElement("daily_room_rate");
		total_tax = getElement("total_tax");
		prepaid_expenses = getElement("prepaid_expenses");
		food_beverage_charges = getElement("food_beverage_charges");
		folio_cash_advances = getElement("folio_cash_advances");
		total_nights = getElement("total_nights");
		total_tax_room = getElement("total_tax_room");
		reserved_4 = getElement("reserved_4");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - LG";
	}
}