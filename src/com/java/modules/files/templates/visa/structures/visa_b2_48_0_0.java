package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_0_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _format_code;
	public AsciiField _constant_identifier;
	public AsciiField _reserved_1;
	public AsciiField _account_number;
	public AsciiField _persistent_fx_eligible;
	public AsciiField _reserved_2;
	public AsciiField _reserved_3;
	public AsciiField _response_code;
	public AsciiField _reserved_4;
	public AsciiField _transaction_date;
	public AsciiField _reserved_5;
	public AsciiField _transaction_tine;
	public AsciiField _reserved_6;
	public AsciiField _transaction_amount;
	public AsciiField _reserved_7;
	public AsciiField _inquiring_center;
	public AsciiField _reserved_8;
	public AsciiField _authorisation_code;
	public AsciiField _reserved_9;
	public AsciiField _trans_code_mci;
	public AsciiField _reserved_10;
	public AsciiField _stand_in_reason_code;
	public AsciiField _reserved_11;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] format_code; // [1] <open=suppress, name="format_code">;
	public char[] constant_identifier; // [8] <open=suppress, name="constant_identifier">;
	public char[] reserved_1; // [1] <open=suppress, name="reserved_1">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] persistent_fx_eligible; // [1] <open=suppress, name="persistent_fx_eligible">;
	public char[] reserved_2; // [6] <open=suppress, name="reserved_2">;
	public char[] reserved_3; // [2] <open=suppress, name="reserved_3">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] reserved_4; // [2] <open=suppress, name="reserved_4">;
	public char[] transaction_date; // [4] <open=suppress, name="transaction_date">;
	public char[] reserved_5; // [2] <open=suppress, name="reserved_5">;
	public char[] transaction_tine; // [4] <open=suppress, name="transaction_tine">;
	public char[] reserved_6; // [2] <open=suppress, name="reserved_6">;
	public char[] transaction_amount; // [8] <open=suppress, name="transaction_amount">;
	public char[] reserved_7; // [2] <open=suppress, name="reserved_7">;
	public char[] inquiring_center; // [4] <open=suppress, name="inquiring_center">;
	public char[] reserved_8; // [2] <open=suppress, name="reserved_8">;
	public char[] authorisation_code; // [5] <open=suppress, name="authorisation_code">;
	public char[] reserved_9; // [2] <open=suppress, name="reserved_9">;
	public char[] trans_code_mci; // [3] <open=suppress, name="trans_code_mci">;
	public char[] reserved_10; // [2] <open=suppress, name="reserved_10">;
	public char[] stand_in_reason_code; // [1] <open=suppress, name="stand_in_reason_code">;
	public char[] reserved_11; // [72] <open=suppress, name="reserved_11">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_48_0_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_0_0(String content) {
		super(content);
	}

	public visa_b2_48_0_0() {
		super();
	}

	public visa_b2_48_0_0(int ifReturn) {
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
		addElement("format_code", 1);
		addElement("constant_identifier", 8);
		addElement("reserved_1", 1);
		addElement("account_number", 16);
		addElement("persistent_fx_eligible", 1);
		addElement("reserved_2", 6);
		addElement("reserved_3", 2);
		addElement("response_code", 2);
		addElement("reserved_4", 2);
		addElement("transaction_date", 4);
		addElement("reserved_5", 2);
		addElement("transaction_tine", 4);
		addElement("reserved_6", 2);
		addElement("transaction_amount", 8);
		addElement("reserved_7", 2);
		addElement("inquiring_center", 4);
		addElement("reserved_8", 2);
		addElement("authorisation_code", 5);
		addElement("reserved_9", 2);
		addElement("trans_code_mci", 3);
		addElement("reserved_10", 2);
		addElement("stand_in_reason_code", 1);
		addElement("reserved_11", 72);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		format_code = getElement("format_code");
		constant_identifier = getElement("constant_identifier");
		reserved_1 = getElement("reserved_1");
		account_number = getElement("account_number");
		persistent_fx_eligible = getElement("persistent_fx_eligible");
		reserved_2 = getElement("reserved_2");
		reserved_3 = getElement("reserved_3");
		response_code = getElement("response_code");
		reserved_4 = getElement("reserved_4");
		transaction_date = getElement("transaction_date");
		reserved_5 = getElement("reserved_5");
		transaction_tine = getElement("transaction_tine");
		reserved_6 = getElement("reserved_6");
		transaction_amount = getElement("transaction_amount");
		reserved_7 = getElement("reserved_7");
		inquiring_center = getElement("inquiring_center");
		reserved_8 = getElement("reserved_8");
		authorisation_code = getElement("authorisation_code");
		reserved_9 = getElement("reserved_9");
		trans_code_mci = getElement("trans_code_mci");
		reserved_10 = getElement("reserved_10");
		stand_in_reason_code = getElement("stand_in_reason_code");
		reserved_11 = getElement("reserved_11");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_48_0_0_ST";
	}
}