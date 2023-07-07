package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_40_4 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _mcc11_amt_pct;
	public AsciiField _mcc12_typ;
	public AsciiField _mcc12_trans_cnt;
	public AsciiField _mcc12_amt_total;
	public AsciiField _mcc12_trans_pct;
	public AsciiField _mcc12_amt_pct;
	public AsciiField _mcc13_typ;
	public AsciiField _mcc13_trans_cnt;
	public AsciiField _mcc13_amt_total;
	public AsciiField _mcc13_trans_pct;
	public AsciiField _mcc13_amt_pct;
	public AsciiField _mcc14_typ;
	public AsciiField _mcc14_trans_cnt;
	public AsciiField _mcc14_amt_total;
	public AsciiField _mcc14_trans_pct;
	public AsciiField _mcc14_amt_pct;
	public AsciiField _mcc15_typ;
	public AsciiField _mcc15_trans_cnt;
	public AsciiField _mcc15_amt_total;
	public AsciiField _mcc15_trans_pct;
	public AsciiField _mcc15_amt_pct;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] mcc11_amt_pct; // [4] <open=suppress, name="mcc11_amt_pct">;
	public char[] mcc12_typ; // [4] <open=suppress, name="mcc12_typ">;
	public char[] mcc12_trans_cnt; // [5] <open=suppress, name="mcc12_trans_cnt">;
	public char[] mcc12_amt_total; // [12] <open=suppress, name="mcc12_amt_total">;
	public char[] mcc12_trans_pct; // [4] <open=suppress, name="mcc12_trans_pct">;
	public char[] mcc12_amt_pct; // [4] <open=suppress, name="mcc12_amt_pct">;
	public char[] mcc13_typ; // [4] <open=suppress, name="mcc13_typ">;
	public char[] mcc13_trans_cnt; // [5] <open=suppress, name="mcc13_trans_cnt">;
	public char[] mcc13_amt_total; // [12] <open=suppress, name="mcc13_amt_total">;
	public char[] mcc13_trans_pct; // [4] <open=suppress, name="mcc13_trans_pct">;
	public char[] mcc13_amt_pct; // [4] <open=suppress, name="mcc13_amt_pct">;
	public char[] mcc14_typ; // [4] <open=suppress, name="mcc14_typ">;
	public char[] mcc14_trans_cnt; // [5] <open=suppress, name="mcc14_trans_cnt">;
	public char[] mcc14_amt_total; // [12] <open=suppress, name="mcc14_amt_total">;
	public char[] mcc14_trans_pct; // [4] <open=suppress, name="mcc14_trans_pct">;
	public char[] mcc14_amt_pct; // [4] <open=suppress, name="mcc14_amt_pct">;
	public char[] mcc15_typ; // [4] <open=suppress, name="mcc15_typ">;
	public char[] mcc15_trans_cnt; // [5] <open=suppress, name="mcc15_trans_cnt">;
	public char[] mcc15_amt_total; // [12] <open=suppress, name="mcc15_amt_total">;
	public char[] mcc15_trans_pct; // [4] <open=suppress, name="mcc15_trans_pct">;
	public char[] mcc15_amt_pct; // [4] <open=suppress, name="mcc15_amt_pct">;
	public char[] reserved; // [44] <open=suppress, name="reserved">;

	public visa_b2_40_4(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_40_4(String content) {
		super(content);
	}

	public visa_b2_40_4() {
		super();
	}

	public visa_b2_40_4(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("mcc11_amt_pct", 4);
		addElement("mcc12_typ", 4);
		addElement("mcc12_trans_cnt", 5);
		addElement("mcc12_amt_total", 12);
		addElement("mcc12_trans_pct", 4);
		addElement("mcc12_amt_pct", 4);
		addElement("mcc13_typ", 4);
		addElement("mcc13_trans_cnt", 5);
		addElement("mcc13_amt_total", 12);
		addElement("mcc13_trans_pct", 4);
		addElement("mcc13_amt_pct", 4);
		addElement("mcc14_typ", 4);
		addElement("mcc14_trans_cnt", 5);
		addElement("mcc14_amt_total", 12);
		addElement("mcc14_trans_pct", 4);
		addElement("mcc14_amt_pct", 4);
		addElement("mcc15_typ", 4);
		addElement("mcc15_trans_cnt", 5);
		addElement("mcc15_amt_total", 12);
		addElement("mcc15_trans_pct", 4);
		addElement("mcc15_amt_pct", 4);
		addElement("reserved", 44);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		mcc11_amt_pct = getElement("mcc11_amt_pct");
		mcc12_typ = getElement("mcc12_typ");
		mcc12_trans_cnt = getElement("mcc12_trans_cnt");
		mcc12_amt_total = getElement("mcc12_amt_total");
		mcc12_trans_pct = getElement("mcc12_trans_pct");
		mcc12_amt_pct = getElement("mcc12_amt_pct");
		mcc13_typ = getElement("mcc13_typ");
		mcc13_trans_cnt = getElement("mcc13_trans_cnt");
		mcc13_amt_total = getElement("mcc13_amt_total");
		mcc13_trans_pct = getElement("mcc13_trans_pct");
		mcc13_amt_pct = getElement("mcc13_amt_pct");
		mcc14_typ = getElement("mcc14_typ");
		mcc14_trans_cnt = getElement("mcc14_trans_cnt");
		mcc14_amt_total = getElement("mcc14_amt_total");
		mcc14_trans_pct = getElement("mcc14_trans_pct");
		mcc14_amt_pct = getElement("mcc14_amt_pct");
		mcc15_typ = getElement("mcc15_typ");
		mcc15_trans_cnt = getElement("mcc15_trans_cnt");
		mcc15_amt_total = getElement("mcc15_amt_total");
		mcc15_trans_pct = getElement("mcc15_trans_pct");
		mcc15_amt_pct = getElement("mcc15_amt_pct");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "visa_b2_40_4_ST";
	}
}