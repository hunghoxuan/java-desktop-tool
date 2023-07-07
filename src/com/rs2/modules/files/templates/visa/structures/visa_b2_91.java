package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_91 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _processing_bin;
	public AsciiField _processing_date;
	public AsciiField _destination_amount;
	public AsciiField _number_of_mon_trans;
	public AsciiField _number_of_batches;
	public AsciiField _number_of_tcr;
	public AsciiField _reserved_1;
	public AsciiField _center_batch_id;
	public AsciiField _number_of_trans;
	public AsciiField _reserved_2;
	public AsciiField _source_amount;
	public AsciiField _reserved_3;
	public AsciiField _reserved_4;
	public AsciiField _reserved_5;
	public AsciiField _reserved_6;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] processing_bin; // [6] <open=suppress, name="processing_bin">;
	public char[] processing_date; // [5] <open=suppress, name="processing_date">;
	public char[] destination_amount; // [15] <open=suppress, name="destination_amount">;
	public char[] number_of_mon_trans; // [12] <open=suppress, name="number_of_mon_trans">;
	public char[] number_of_batches; // [6] <open=suppress, name="number_of_batches">;
	public char[] number_of_tcr; // [12] <open=suppress, name="number_of_tcr">;
	public char[] reserved_1; // [6] <open=suppress, name="reserved_1">;
	public char[] center_batch_id; // [8] <open=suppress, name="center_batch_id">;
	public char[] number_of_trans; // [9] <open=suppress, name="number_of_trans">;
	public char[] reserved_2; // [18] <open=suppress, name="reserved_2">;
	public char[] source_amount; // [15] <open=suppress, name="source_amount">;
	public char[] reserved_3; // [15] <open=suppress, name="reserved_3">;
	public char[] reserved_4; // [15] <open=suppress, name="reserved_4">;
	public char[] reserved_5; // [15] <open=suppress, name="reserved_5">;
	public char[] reserved_6; // [7] <open=suppress, name="reserved_6">;

	public visa_b2_91(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_91(String content) {
		super(content);
	}

	public visa_b2_91() {
		super();
	}

	public visa_b2_91(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("processing_bin", 6);
		addElement("processing_date", 5);
		addElement("destination_amount", 15);
		addElement("number_of_mon_trans", 12);
		addElement("number_of_batches", 6);
		addElement("number_of_tcr", 12);
		addElement("reserved_1", 6);
		addElement("center_batch_id", 8);
		addElement("number_of_trans", 9);
		addElement("reserved_2", 18);
		addElement("source_amount", 15);
		addElement("reserved_3", 15);
		addElement("reserved_4", 15);
		addElement("reserved_5", 15);
		addElement("reserved_6", 7);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		processing_bin = getElement("processing_bin");
		processing_date = getElement("processing_date");
		destination_amount = getElement("destination_amount");
		number_of_mon_trans = getElement("number_of_mon_trans");
		number_of_batches = getElement("number_of_batches");
		number_of_tcr = getElement("number_of_tcr");
		reserved_1 = getElement("reserved_1");
		center_batch_id = getElement("center_batch_id");
		number_of_trans = getElement("number_of_trans");
		reserved_2 = getElement("reserved_2");
		source_amount = getElement("source_amount");
		reserved_3 = getElement("reserved_3");
		reserved_4 = getElement("reserved_4");
		reserved_5 = getElement("reserved_5");
		reserved_6 = getElement("reserved_6");

	}

	@Override
	public String getDescription() {
		return "visa_b2_91_ST";
	}
}