package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_8 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _transaction_date;
	public AsciiField _transaction_time;
	public AsciiField _retrieval_ref_number;
	public AsciiField _card_expiry_date;
	public AsciiField _card_seq_number;
	public AsciiField _terminal_id;
	public AsciiField _card_acceptor_id;
	public AsciiField _card_length;
	public AsciiField _chb_reason_code;
	public AsciiField _ucaf_indicator;
	public AsciiField _reserverd_1;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] transaction_date; // [8] <open=suppress, name="transaction_date">;
	public char[] transaction_time; // [6] <open=suppress, name="transaction_time">;
	public char[] retrieval_ref_number; // [12] <open=suppress, name="retrieval_ref_number">;
	public char[] card_expiry_date; // [4] <open=suppress, name="card_expiry_date">;
	public char[] card_seq_number; // [3] <open=suppress, name="card_seq_number">;
	public char[] terminal_id; // [8] <open=suppress, name="terminal_id">;
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] card_length; // [2] <open=suppress, name="card_length">;
	public char[] chb_reason_code; // [4] <open=suppress, name="chb_reason_code">;
	public char[] ucaf_indicator; // [1] <open=suppress, name="ucaf_indicator">;
	public char[] reserverd_1; // [101] <open=suppress, name="reserverd_1">;

	public visa_b2_05_8(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_8(String content) {
		super(content);
	}

	public visa_b2_05_8() {
		super();
	}

	public visa_b2_05_8(int ifReturn) {
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
		addElement("transaction_date", 8);
		addElement("transaction_time", 6);
		addElement("retrieval_ref_number", 12);
		addElement("card_expiry_date", 4);
		addElement("card_seq_number", 3);
		addElement("terminal_id", 8);
		addElement("card_acceptor_id", 15);
		addElement("card_length", 2);
		addElement("chb_reason_code", 4);
		addElement("ucaf_indicator", 1);
		addElement("reserverd_1", 101);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		transaction_date = getElement("transaction_date");
		transaction_time = getElement("transaction_time");
		retrieval_ref_number = getElement("retrieval_ref_number");
		card_expiry_date = getElement("card_expiry_date");
		card_seq_number = getElement("card_seq_number");
		terminal_id = getElement("terminal_id");
		card_acceptor_id = getElement("card_acceptor_id");
		card_length = getElement("card_length");
		chb_reason_code = getElement("chb_reason_code");
		ucaf_indicator = getElement("ucaf_indicator");
		reserverd_1 = getElement("reserverd_1");

	}

	@Override
	public String getDescription() {
		return "TCR-8 - Europay Private Data";
	}
}