package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_FL extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _reserved_2;
	public AsciiField _expanded_fuel_type;
	public AsciiField _type_of_purchase;
	public AsciiField _fuel_type;
	public AsciiField _unit_measure_code;
	public AsciiField _quantity;
	public AsciiField _unit_cost;
	public AsciiField _gross_fuel_price;
	public AsciiField _net_fuel_price;
	public AsciiField _gross_non_fuel_price;
	public AsciiField _net_non_fuel_price;
	public AsciiField _odometer_reading;
	public AsciiField _vat_rate;
	public AsciiField _misc_fuel_tax;
	public AsciiField _product_qualifier;
	public AsciiField _reserved_3;
	public AsciiField _misc_nonfuel_tax;
	public AsciiField _service_type;
	public AsciiField _misc_fltaxexstatus;
	public AsciiField _misc_nonfltaxexstatus;
	public AsciiField _reserved_4;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] reserved_2; // [4] <open=suppress, name="reserved_2">;
	public char[] expanded_fuel_type; // [4] <open=suppress, name="expanded_fuel_type">;
	public char[] type_of_purchase; // [1] <open=suppress, name="type_of_purchase">;
	public char[] fuel_type; // [2] <open=suppress, name="fuel_type">;
	public char[] unit_measure_code; // [1] <open=suppress, name="unit_measure_code">;
	public char[] quantity; // [12] <open=suppress, name="quantity">;
	public char[] unit_cost; // [12] <open=suppress, name="unit_cost">;
	public char[] gross_fuel_price; // [12] <open=suppress, name="gross_fuel_price">;
	public char[] net_fuel_price; // [12] <open=suppress, name="net_fuel_price">;
	public char[] gross_non_fuel_price; // [12] <open=suppress, name="gross_non_fuel_price">;
	public char[] net_non_fuel_price; // [12] <open=suppress, name="net_non_fuel_price">;
	public char[] odometer_reading; // [7] <open=suppress, name="odometer_reading">;
	public char[] vat_rate; // [4] <open=suppress, name="vat_rate">;
	public char[] misc_fuel_tax; // [12] <open=suppress, name="misc_fuel_tax">;
	public char[] product_qualifier; // [6] <open=suppress, name="product_qualifier">;
	public char[] reserved_3; // [6] <open=suppress, name="reserved_3">;
	public char[] misc_nonfuel_tax; // [12] <open=suppress, name="misc_nonfuel_tax">;
	public char[] service_type; // [1] <open=suppress, name="service_type">;
	public char[] misc_fltaxexstatus; // [1] <open=suppress, name="misc_fltaxexstatus">;
	public char[] misc_nonfltaxexstatus; // [1] <open=suppress, name="misc_nonfltaxexstatus">;
	public char[] reserved_4; // [16] <open=suppress, name="reserved_4">;

	public visa_b2_05_3_FL(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_FL(String content) {
		super(content);
	}

	public visa_b2_05_3_FL() {
		super();
	}

	public visa_b2_05_3_FL(int ifReturn) {
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
		addElement("reserved_2", 4);
		addElement("expanded_fuel_type", 4);
		addElement("type_of_purchase", 1);
		addElement("fuel_type", 2);
		addElement("unit_measure_code", 1);
		addElement("quantity", 12);
		addElement("unit_cost", 12);
		addElement("gross_fuel_price", 12);
		addElement("net_fuel_price", 12);
		addElement("gross_non_fuel_price", 12);
		addElement("net_non_fuel_price", 12);
		addElement("odometer_reading", 7);
		addElement("vat_rate", 4);
		addElement("misc_fuel_tax", 12);
		addElement("product_qualifier", 6);
		addElement("reserved_3", 6);
		addElement("misc_nonfuel_tax", 12);
		addElement("service_type", 1);
		addElement("misc_fltaxexstatus", 1);
		addElement("misc_nonfltaxexstatus", 1);
		addElement("reserved_4", 16);

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
		expanded_fuel_type = getElement("expanded_fuel_type");
		type_of_purchase = getElement("type_of_purchase");
		fuel_type = getElement("fuel_type");
		unit_measure_code = getElement("unit_measure_code");
		quantity = getElement("quantity");
		unit_cost = getElement("unit_cost");
		gross_fuel_price = getElement("gross_fuel_price");
		net_fuel_price = getElement("net_fuel_price");
		gross_non_fuel_price = getElement("gross_non_fuel_price");
		net_non_fuel_price = getElement("net_non_fuel_price");
		odometer_reading = getElement("odometer_reading");
		vat_rate = getElement("vat_rate");
		misc_fuel_tax = getElement("misc_fuel_tax");
		product_qualifier = getElement("product_qualifier");
		reserved_3 = getElement("reserved_3");
		misc_nonfuel_tax = getElement("misc_nonfuel_tax");
		service_type = getElement("service_type");
		misc_fltaxexstatus = getElement("misc_fltaxexstatus");
		misc_nonfltaxexstatus = getElement("misc_nonfltaxexstatus");
		reserved_4 = getElement("reserved_4");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - FL";
	}
}