package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_CA extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _total_day_rental;
	public AsciiField _reserved_2;
	public AsciiField _no_show_id;
	public AsciiField _extra_charges;
	public AsciiField _reserved_3;
	public AsciiField _check_out_date;
	public AsciiField _daily_rental_rate;
	public AsciiField _weekly_rental_rate;
	public AsciiField _insurance_charges;
	public AsciiField _fuel_charges;
	public AsciiField _car_class_code;
	public AsciiField _owd_off_charges;
	public AsciiField _renter_name;
	public AsciiField _reserved_4;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] total_day_rental; // [2] <open=suppress, name="total_day_rental">;
	public char[] reserved_2; // [6] <open=suppress, name="reserved_2">;
	public char[] no_show_id; // [1] <open=suppress, name="no_show_id">;
	public char[] extra_charges; // [6] <open=suppress, name="extra_charges">;
	public char[] reserved_3; // [4] <open=suppress, name="reserved_3">;
	public char[] check_out_date; // [6] <open=suppress, name="check_out_date">;
	public char[] daily_rental_rate; // [12] <open=suppress, name="daily_rental_rate">;
	public char[] weekly_rental_rate; // [12] <open=suppress, name="weekly_rental_rate">;
	public char[] insurance_charges; // [12] <open=suppress, name="insurance_charges">;
	public char[] fuel_charges; // [12] <open=suppress, name="fuel_charges">;
	public char[] car_class_code; // [2] <open=suppress, name="car_class_code">;
	public char[] owd_off_charges; // [12] <open=suppress, name="owd_off_charges">;
	public char[] renter_name; // [40] <open=suppress, name="renter_name">;
	public char[] reserved_4; // [23] <open=suppress, name="reserved_4">;

	public visa_b2_05_3_CA(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_CA(String content) {
		super(content);
	}

	public visa_b2_05_3_CA() {
		super();
	}

	public visa_b2_05_3_CA(int ifReturn) {
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
		addElement("total_day_rental", 2);
		addElement("reserved_2", 6);
		addElement("no_show_id", 1);
		addElement("extra_charges", 6);
		addElement("reserved_3", 4);
		addElement("check_out_date", 6);
		addElement("daily_rental_rate", 12);
		addElement("weekly_rental_rate", 12);
		addElement("insurance_charges", 12);
		addElement("fuel_charges", 12);
		addElement("car_class_code", 2);
		addElement("owd_off_charges", 12);
		addElement("renter_name", 40);
		addElement("reserved_4", 23);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		reserved_1 = getElement("reserved_1");
		fast_funds_indicator = getElement("fast_funds_indicator");
		bus_format_code = getElement("bus_format_code");
		total_day_rental = getElement("total_day_rental");
		reserved_2 = getElement("reserved_2");
		no_show_id = getElement("no_show_id");
		extra_charges = getElement("extra_charges");
		reserved_3 = getElement("reserved_3");
		check_out_date = getElement("check_out_date");
		daily_rental_rate = getElement("daily_rental_rate");
		weekly_rental_rate = getElement("weekly_rental_rate");
		insurance_charges = getElement("insurance_charges");
		fuel_charges = getElement("fuel_charges");
		car_class_code = getElement("car_class_code");
		owd_off_charges = getElement("owd_off_charges");
		renter_name = getElement("renter_name");
		reserved_4 = getElement("reserved_4");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - CA";
	}
}