package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class v22225_original_data_elements extends AsciiMessage {
	public AsciiField _message_type;
	public AsciiField _trace_number;
	public AsciiField _transmission_date;
	public AsciiField _transmission_time;
	public AsciiField _acquiring_institution_id;
	public AsciiField _forwarding_institution_id;
	public char[] message_type; // [4] <open=suppress, name="message_type">;
	public char[] trace_number; // [6] <open=suppress, name="trace_number">;
	public char[] transmission_date; // [4] <open=suppress, name="transmission_date">;
	public char[] transmission_time; // [6] <open=suppress, name="transmission_time">;
	public char[] acquiring_institution_id; // [11] <open=suppress, name="acquiring_institution_id">;
	public char[] forwarding_institution_id; // [11] <open=suppress, name="forwarding_institution_id">;

	public v22225_original_data_elements(int offset, String content) {
		super(offset, content);
	}

	public v22225_original_data_elements(String content) {
		super(content);
	}

	public v22225_original_data_elements() {
		super();
	}

	public v22225_original_data_elements(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("message_type", 4);
		addElement("trace_number", 6);
		addElement("transmission_date", 4);
		addElement("transmission_time", 6);
		addElement("acquiring_institution_id", 11);
		addElement("forwarding_institution_id", 11);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		message_type = getElement("message_type");
		trace_number = getElement("trace_number");
		transmission_date = getElement("transmission_date");
		transmission_time = getElement("transmission_time");
		acquiring_institution_id = getElement("acquiring_institution_id");
		forwarding_institution_id = getElement("forwarding_institution_id");

	}

	@Override
	public String getDescription() {
		return "";
	}
}