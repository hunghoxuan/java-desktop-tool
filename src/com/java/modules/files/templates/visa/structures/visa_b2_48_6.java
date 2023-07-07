package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_48_6 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _auth_resp_cryptogram;
	public AsciiField _auth_resp_cryptogram_code;
	public AsciiField _reserved_1;
	public AsciiField _card_authen_results_code;
	public AsciiField _cryptogram_amount;
	public AsciiField _cryptogram_currency_code;
	public AsciiField _cryptogram_cashback_amount;
	public AsciiField _issuer_discretionary_data;
	public AsciiField _card_authen_reliability_ind;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] auth_resp_cryptogram; // [16] <open=suppress, name="auth_resp_cryptogram">;
	public char[] auth_resp_cryptogram_code; // [2] <open=suppress, name="auth_resp_cryptogram_code">;
	public char[] reserved_1; // [1] <open=suppress, name="reserved_1">;
	public char[] card_authen_results_code; // [1] <open=suppress, name="card_authen_results_code">;
	public char[] cryptogram_amount; // [12] <open=suppress, name="cryptogram_amount">;
	public char[] cryptogram_currency_code; // [3] <open=suppress, name="cryptogram_currency_code">;
	public char[] cryptogram_cashback_amount; // [9] <open=suppress, name="cryptogram_cashback_amount">;
	public char[] issuer_discretionary_data; // [32] <open=suppress, name="issuer_discretionary_data">;
	public char[] card_authen_reliability_ind; // [1] <open=suppress, name="card_authen_reliability_ind">;
	public char[] reserved_2; // [87] <open=suppress, name="reserved_2">;

	public visa_b2_48_6(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_48_6(String content) {
		super(content);
	}

	public visa_b2_48_6() {
		super();
	}

	public visa_b2_48_6(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("auth_resp_cryptogram", 16);
		addElement("auth_resp_cryptogram_code", 2);
		addElement("reserved_1", 1);
		addElement("card_authen_results_code", 1);
		addElement("cryptogram_amount", 12);
		addElement("cryptogram_currency_code", 3);
		addElement("cryptogram_cashback_amount", 9);
		addElement("issuer_discretionary_data", 32);
		addElement("card_authen_reliability_ind", 1);
		addElement("reserved_2", 87);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		auth_resp_cryptogram = getElement("auth_resp_cryptogram");
		auth_resp_cryptogram_code = getElement("auth_resp_cryptogram_code");
		reserved_1 = getElement("reserved_1");
		card_authen_results_code = getElement("card_authen_results_code");
		cryptogram_amount = getElement("cryptogram_amount");
		cryptogram_currency_code = getElement("cryptogram_currency_code");
		cryptogram_cashback_amount = getElement("cryptogram_cashback_amount");
		issuer_discretionary_data = getElement("issuer_discretionary_data");
		card_authen_reliability_ind = getElement("card_authen_reliability_ind");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "TCR-6 ISO-Enriched CTF-Incoming Interchange";
	}
}