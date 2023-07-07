package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_3_AI extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _reserved_1;
	public AsciiField _fast_funds_indicator;
	public AsciiField _bus_format_code;
	public AsciiField _reserved_2;
	public AsciiField _passenger_name;
	public AsciiField _departure_date;
	public AsciiField _org_city_ap_code;
	public AsciiField _carrier_code_1;
	public AsciiField _service_class_1;
	public AsciiField _stop_over_code_1;
	public AsciiField _destination_code_1;
	public AsciiField _carrier_code_2;
	public AsciiField _service_class_2;
	public AsciiField _stop_over_code_2;
	public AsciiField _destination_code_2;
	public AsciiField _carrier_code_3;
	public AsciiField _service_class_3;
	public AsciiField _stop_over_code_3;
	public AsciiField _destination_code_3;
	public AsciiField _carrier_code_4;
	public AsciiField _service_class_4;
	public AsciiField _stop_over_code_4;
	public AsciiField _destination_code_4;
	public AsciiField _travel_agency_code;
	public AsciiField _travel_agency_name;
	public AsciiField _restr_ticket_ind;
	public AsciiField _fare_basis_code_1;
	public AsciiField _fare_basis_code_2;
	public AsciiField _fare_basis_code_3;
	public AsciiField _fare_basis_code_4;
	public AsciiField _comp_res_system;
	public AsciiField _flight_number_leg_1;
	public AsciiField _flight_number_leg_2;
	public AsciiField _flight_number_leg_3;
	public AsciiField _flight_number_leg_4;
	public AsciiField _reserved_3;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] reserved_1; // [11] <open=suppress, name="reserved_1">;
	public char[] fast_funds_indicator; // [1] <open=suppress, name="fast_funds_indicator">;
	public char[] bus_format_code; // [2] <open=suppress, name="bus_format_code">;
	public char[] reserved_2; // [8] <open=suppress, name="reserved_2">;
	public char[] passenger_name; // [20] <open=suppress, name="passenger_name">;
	public char[] departure_date; // [6] <open=suppress, name="departure_date">;
	public char[] org_city_ap_code; // [3] <open=suppress, name="org_city_ap_code">;
	public char[] carrier_code_1; // [2] <open=suppress, name="carrier_code_1">;
	public char[] service_class_1; // [1] <open=suppress, name="service_class_1">;
	public char[] stop_over_code_1; // [1] <open=suppress, name="stop_over_code_1">;
	public char[] destination_code_1; // [3] <open=suppress, name="destination_code_1">;
	public char[] carrier_code_2; // [2] <open=suppress, name="carrier_code_2">;
	public char[] service_class_2; // [1] <open=suppress, name="service_class_2">;
	public char[] stop_over_code_2; // [1] <open=suppress, name="stop_over_code_2">;
	public char[] destination_code_2; // [3] <open=suppress, name="destination_code_2">;
	public char[] carrier_code_3; // [2] <open=suppress, name="carrier_code_3">;
	public char[] service_class_3; // [1] <open=suppress, name="service_class_3">;
	public char[] stop_over_code_3; // [1] <open=suppress, name="stop_over_code_3">;
	public char[] destination_code_3; // [3] <open=suppress, name="destination_code_3">;
	public char[] carrier_code_4; // [2] <open=suppress, name="carrier_code_4">;
	public char[] service_class_4; // [1] <open=suppress, name="service_class_4">;
	public char[] stop_over_code_4; // [1] <open=suppress, name="stop_over_code_4">;
	public char[] destination_code_4; // [3] <open=suppress, name="destination_code_4">;
	public char[] travel_agency_code; // [8] <open=suppress, name="travel_agency_code">;
	public char[] travel_agency_name; // [25] <open=suppress, name="travel_agency_name">;
	public char[] restr_ticket_ind; // [1] <open=suppress, name="restr_ticket_ind">;
	public char[] fare_basis_code_1; // [6] <open=suppress, name="fare_basis_code_1">;
	public char[] fare_basis_code_2; // [6] <open=suppress, name="fare_basis_code_2">;
	public char[] fare_basis_code_3; // [6] <open=suppress, name="fare_basis_code_3">;
	public char[] fare_basis_code_4; // [6] <open=suppress, name="fare_basis_code_4">;
	public char[] comp_res_system; // [4] <open=suppress, name="comp_res_system">;
	public char[] flight_number_leg_1; // [5] <open=suppress, name="flight_number_leg_1">;
	public char[] flight_number_leg_2; // [5] <open=suppress, name="flight_number_leg_2">;
	public char[] flight_number_leg_3; // [5] <open=suppress, name="flight_number_leg_3">;
	public char[] flight_number_leg_4; // [5] <open=suppress, name="flight_number_leg_4">;
	public char[] reserved_3; // [3] <open=suppress, name="reserved_3">;

	public visa_b2_05_3_AI(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_3_AI(String content) {
		super(content);
	}

	public visa_b2_05_3_AI() {
		super();
	}

	public visa_b2_05_3_AI(int ifReturn) {
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
		addElement("passenger_name", 20);
		addElement("departure_date", 6);
		addElement("org_city_ap_code", 3);
		addElement("carrier_code_1", 2);
		addElement("service_class_1", 1);
		addElement("stop_over_code_1", 1);
		addElement("destination_code_1", 3);
		addElement("carrier_code_2", 2);
		addElement("service_class_2", 1);
		addElement("stop_over_code_2", 1);
		addElement("destination_code_2", 3);
		addElement("carrier_code_3", 2);
		addElement("service_class_3", 1);
		addElement("stop_over_code_3", 1);
		addElement("destination_code_3", 3);
		addElement("carrier_code_4", 2);
		addElement("service_class_4", 1);
		addElement("stop_over_code_4", 1);
		addElement("destination_code_4", 3);
		addElement("travel_agency_code", 8);
		addElement("travel_agency_name", 25);
		addElement("restr_ticket_ind", 1);
		addElement("fare_basis_code_1", 6);
		addElement("fare_basis_code_2", 6);
		addElement("fare_basis_code_3", 6);
		addElement("fare_basis_code_4", 6);
		addElement("comp_res_system", 4);
		addElement("flight_number_leg_1", 5);
		addElement("flight_number_leg_2", 5);
		addElement("flight_number_leg_3", 5);
		addElement("flight_number_leg_4", 5);
		addElement("reserved_3", 3);

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
		passenger_name = getElement("passenger_name");
		departure_date = getElement("departure_date");
		org_city_ap_code = getElement("org_city_ap_code");
		carrier_code_1 = getElement("carrier_code_1");
		service_class_1 = getElement("service_class_1");
		stop_over_code_1 = getElement("stop_over_code_1");
		destination_code_1 = getElement("destination_code_1");
		carrier_code_2 = getElement("carrier_code_2");
		service_class_2 = getElement("service_class_2");
		stop_over_code_2 = getElement("stop_over_code_2");
		destination_code_2 = getElement("destination_code_2");
		carrier_code_3 = getElement("carrier_code_3");
		service_class_3 = getElement("service_class_3");
		stop_over_code_3 = getElement("stop_over_code_3");
		destination_code_3 = getElement("destination_code_3");
		carrier_code_4 = getElement("carrier_code_4");
		service_class_4 = getElement("service_class_4");
		stop_over_code_4 = getElement("stop_over_code_4");
		destination_code_4 = getElement("destination_code_4");
		travel_agency_code = getElement("travel_agency_code");
		travel_agency_name = getElement("travel_agency_name");
		restr_ticket_ind = getElement("restr_ticket_ind");
		fare_basis_code_1 = getElement("fare_basis_code_1");
		fare_basis_code_2 = getElement("fare_basis_code_2");
		fare_basis_code_3 = getElement("fare_basis_code_3");
		fare_basis_code_4 = getElement("fare_basis_code_4");
		comp_res_system = getElement("comp_res_system");
		flight_number_leg_1 = getElement("flight_number_leg_1");
		flight_number_leg_2 = getElement("flight_number_leg_2");
		flight_number_leg_3 = getElement("flight_number_leg_3");
		flight_number_leg_4 = getElement("flight_number_leg_4");
		reserved_3 = getElement("reserved_3");

	}

	@Override
	public String getDescription() {
		return "TCR 3 - AI";
	}
}