package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_01_9 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _orig_trans_code;
	public AsciiField _orig_trans_code_qualif;
	public AsciiField _orig_trans_comp_seq;
	public AsciiField _source_batch_date;
	public AsciiField _source_batch_num;
	public AsciiField _item_sequence_num;
	public AsciiField _return_code_1;
	public AsciiField _source_amount;
	public AsciiField _source_currency;
	public AsciiField _settlement_flag;
	public AsciiField _crs_return_flag;
	public AsciiField _return_code_2;
	public AsciiField _return_code_3;
	public AsciiField _return_code_4;
	public AsciiField _return_code_5;
	public AsciiField _reserved_1;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] orig_trans_code; // [2] <open=suppress, name="orig_trans_code">;
	public char[] orig_trans_code_qualif; // [1] <open=suppress, name="orig_trans_code_qualif">;
	public char[] orig_trans_comp_seq; // [1] <open=suppress, name="orig_trans_comp_seq">;
	public char[] source_batch_date; // [5] <open=suppress, name="source_batch_date">;
	public char[] source_batch_num; // [6] <open=suppress, name="source_batch_num">;
	public char[] item_sequence_num; // [4] <open=suppress, name="item_sequence_num">;
	public char[] return_code_1; // [3] <open=suppress, name="return_code_1">;
	public char[] source_amount; // [12] <open=suppress, name="source_amount">;
	public char[] source_currency; // [3] <open=suppress, name="source_currency">;
	public char[] settlement_flag; // [1] <open=suppress, name="settlement_flag">;
	public char[] crs_return_flag; // [1] <open=suppress, name="crs_return_flag">;
	public char[] return_code_2; // [3] <open=suppress, name="return_code_2">;
	public char[] return_code_3; // [3] <open=suppress, name="return_code_3">;
	public char[] return_code_4; // [3] <open=suppress, name="return_code_4">;
	public char[] return_code_5; // [3] <open=suppress, name="return_code_5">;
	public char[] reserved_1; // [101] <open=suppress, name="reserved_1">;

	public visa_b2_01_9(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_01_9(String content) {
		super(content);
	}

	public visa_b2_01_9() {
		super();
	}

	public visa_b2_01_9(int ifReturn) {
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
		addElement("orig_trans_code", 2);
		addElement("orig_trans_code_qualif", 1);
		addElement("orig_trans_comp_seq", 1);
		addElement("source_batch_date", 5);
		addElement("source_batch_num", 6);
		addElement("item_sequence_num", 4);
		addElement("return_code_1", 3);
		addElement("source_amount", 12);
		addElement("source_currency", 3);
		addElement("settlement_flag", 1);
		addElement("crs_return_flag", 1);
		addElement("return_code_2", 3);
		addElement("return_code_3", 3);
		addElement("return_code_4", 3);
		addElement("return_code_5", 3);
		addElement("reserved_1", 101);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		orig_trans_code = getElement("orig_trans_code");
		orig_trans_code_qualif = getElement("orig_trans_code_qualif");
		orig_trans_comp_seq = getElement("orig_trans_comp_seq");
		source_batch_date = getElement("source_batch_date");
		source_batch_num = getElement("source_batch_num");
		item_sequence_num = getElement("item_sequence_num");
		return_code_1 = getElement("return_code_1");
		source_amount = getElement("source_amount");
		source_currency = getElement("source_currency");
		settlement_flag = getElement("settlement_flag");
		crs_return_flag = getElement("crs_return_flag");
		return_code_2 = getElement("return_code_2");
		return_code_3 = getElement("return_code_3");
		return_code_4 = getElement("return_code_4");
		return_code_5 = getElement("return_code_5");
		reserved_1 = getElement("reserved_1");

	}

	@Override
	public String getDescription() {
		return "visa_b2_01_9_ST";
	}
}