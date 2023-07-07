package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_CORPAS extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _item_seq_no;
	public AsciiField _conjunction_ticket;
	public AsciiField _coupon_1;
	public AsciiField _carrier_code_1;
	public AsciiField _flight_number_leg_1;
	public AsciiField _service_class_1;
	public AsciiField _depart_airport_1;
	public AsciiField _stop_over_code_1;
	public AsciiField _destination_code_1;
	public AsciiField _fare_basis_code_1;
	public AsciiField _departure_date_1;
	public AsciiField _departure_time_1;
	public AsciiField _arrival_time_1;
	public AsciiField _coupon_2;
	public AsciiField _carrier_code_2;
	public AsciiField _flight_number_leg_2;
	public AsciiField _service_class_2;
	public AsciiField _depart_airport_2;
	public AsciiField _stop_over_code_2;
	public AsciiField _destination_code_2;
	public AsciiField _fare_basis_code_2;
	public AsciiField _departure_date_2;
	public AsciiField _departure_time_2;
	public AsciiField _arrival_time_2;
	public AsciiField _control_id;
	public AsciiField _reserved;
	public AsciiField _ticket_issue_date;
	public AsciiField _reimburs_attrib;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] item_seq_no; // [4] <open=suppress, name="item_seq_no">;
	public char[] conjunction_ticket; // [13] <open=suppress, name="conjunction_ticket">;
	public char[] coupon_1; // [1] <open=suppress, name="coupon_1">;
	public char[] carrier_code_1; // [2] <open=suppress, name="carrier_code_1">;
	public char[] flight_number_leg_1; // [5] <open=suppress, name="flight_number_leg_1">;
	public char[] service_class_1; // [2] <open=suppress, name="service_class_1">;
	public char[] depart_airport_1; // [5] <open=suppress, name="depart_airport_1">;
	public char[] stop_over_code_1; // [1] <open=suppress, name="stop_over_code_1">;
	public char[] destination_code_1; // [5] <open=suppress, name="destination_code_1">;
	public char[] fare_basis_code_1; // [8] <open=suppress, name="fare_basis_code_1">;
	public char[] departure_date_1; // [6] <open=suppress, name="departure_date_1">;
	public char[] departure_time_1; // [4] <open=suppress, name="departure_time_1">;
	public char[] arrival_time_1; // [4] <open=suppress, name="arrival_time_1">;
	public char[] coupon_2; // [1] <open=suppress, name="coupon_2">;
	public char[] carrier_code_2; // [2] <open=suppress, name="carrier_code_2">;
	public char[] flight_number_leg_2; // [5] <open=suppress, name="flight_number_leg_2">;
	public char[] service_class_2; // [2] <open=suppress, name="service_class_2">;
	public char[] depart_airport_2; // [5] <open=suppress, name="depart_airport_2">;
	public char[] stop_over_code_2; // [1] <open=suppress, name="stop_over_code_2">;
	public char[] destination_code_2; // [5] <open=suppress, name="destination_code_2">;
	public char[] fare_basis_code_2; // [8] <open=suppress, name="fare_basis_code_2">;
	public char[] departure_date_2; // [6] <open=suppress, name="departure_date_2">;
	public char[] departure_time_2; // [4] <open=suppress, name="departure_time_2">;
	public char[] arrival_time_2; // [4] <open=suppress, name="arrival_time_2">;
	public char[] control_id; // [13] <open=suppress, name="control_id">;
	public char[] reserved; // [6] <open=suppress, name="reserved">;
	public char[] ticket_issue_date; // [8] <open=suppress, name="ticket_issue_date">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_CORPAS(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_CORPAS(String content) {
		super(content);
	}

	public visa_b2_50_0_CORPAS() {
		super();
	}

	public visa_b2_50_0_CORPAS(int ifReturn) {
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
		addElement("item_seq_no", 4);
		addElement("conjunction_ticket", 13);
		addElement("coupon_1", 1);
		addElement("carrier_code_1", 2);
		addElement("flight_number_leg_1", 5);
		addElement("service_class_1", 2);
		addElement("depart_airport_1", 5);
		addElement("stop_over_code_1", 1);
		addElement("destination_code_1", 5);
		addElement("fare_basis_code_1", 8);
		addElement("departure_date_1", 6);
		addElement("departure_time_1", 4);
		addElement("arrival_time_1", 4);
		addElement("coupon_2", 1);
		addElement("carrier_code_2", 2);
		addElement("flight_number_leg_2", 5);
		addElement("service_class_2", 2);
		addElement("depart_airport_2", 5);
		addElement("stop_over_code_2", 1);
		addElement("destination_code_2", 5);
		addElement("fare_basis_code_2", 8);
		addElement("departure_date_2", 6);
		addElement("departure_time_2", 4);
		addElement("arrival_time_2", 4);
		addElement("control_id", 13);
		addElement("reserved", 6);
		addElement("ticket_issue_date", 8);
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
		conjunction_ticket = getElement("conjunction_ticket");
		coupon_1 = getElement("coupon_1");
		carrier_code_1 = getElement("carrier_code_1");
		flight_number_leg_1 = getElement("flight_number_leg_1");
		service_class_1 = getElement("service_class_1");
		depart_airport_1 = getElement("depart_airport_1");
		stop_over_code_1 = getElement("stop_over_code_1");
		destination_code_1 = getElement("destination_code_1");
		fare_basis_code_1 = getElement("fare_basis_code_1");
		departure_date_1 = getElement("departure_date_1");
		departure_time_1 = getElement("departure_time_1");
		arrival_time_1 = getElement("arrival_time_1");
		coupon_2 = getElement("coupon_2");
		carrier_code_2 = getElement("carrier_code_2");
		flight_number_leg_2 = getElement("flight_number_leg_2");
		service_class_2 = getElement("service_class_2");
		depart_airport_2 = getElement("depart_airport_2");
		stop_over_code_2 = getElement("stop_over_code_2");
		destination_code_2 = getElement("destination_code_2");
		fare_basis_code_2 = getElement("fare_basis_code_2");
		departure_date_2 = getElement("departure_date_2");
		departure_time_2 = getElement("departure_time_2");
		arrival_time_2 = getElement("arrival_time_2");
		control_id = getElement("control_id");
		reserved = getElement("reserved");
		ticket_issue_date = getElement("ticket_issue_date");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_CORPAS_ST";
	}
}