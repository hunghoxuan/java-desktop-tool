package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_50_0_OPNFMT extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _service_id;
	public AsciiField _message_id;
	public AsciiField _merchant_sector;
	public AsciiField _source_identification;
	public AsciiField _tc50_env_sequence_no;
	public AsciiField _reserved;
	public AsciiField _message_text;
	public AsciiField _reimburs_attrib;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] service_id; // [6] <open=suppress, name="service_id">;
	public char[] message_id; // [15] <open=suppress, name="message_id">;
	public char[] merchant_sector; // [2] <open=suppress, name="merchant_sector">;
	public char[] source_identification; // [1] <open=suppress, name="source_identification">;
	public char[] tc50_env_sequence_no; // [3] <open=suppress, name="tc50_env_sequence_no">;
	public char[] reserved; // [3] <open=suppress, name="reserved">;
	public char[] message_text; // [121] <open=suppress, name="message_text">;
	public char[] reimburs_attrib; // [1] <open=suppress, name="reimburs_attrib">;

	public visa_b2_50_0_OPNFMT(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_50_0_OPNFMT(String content) {
		super(content);
	}

	public visa_b2_50_0_OPNFMT() {
		super();
	}

	public visa_b2_50_0_OPNFMT(int ifReturn) {
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
		addElement("merchant_sector", 2);
		addElement("source_identification", 1);
		addElement("tc50_env_sequence_no", 3);
		addElement("reserved", 3);
		addElement("message_text", 121);
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
		merchant_sector = getElement("merchant_sector");
		source_identification = getElement("source_identification");
		tc50_env_sequence_no = getElement("tc50_env_sequence_no");
		reserved = getElement("reserved");
		message_text = getElement("message_text");
		reimburs_attrib = getElement("reimburs_attrib");

	}

	@Override
	public String getDescription() {
		return "visa_b2_50_0_OPNFMT_ST";
	}
}