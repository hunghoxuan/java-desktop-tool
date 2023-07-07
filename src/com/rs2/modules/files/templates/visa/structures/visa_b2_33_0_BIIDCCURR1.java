package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_BIIDCCURR1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _report_identifier;
	public AsciiField _Sequence_Number;
	public AsciiField _Billing_Identifier;
	public AsciiField _Filler_1;
	public AsciiField _Recipient;
	public AsciiField _Filler_2;
	public AsciiField _Account_Low_Range;
	public AsciiField _Filler_3;
	public AsciiField _Account_High_Range;
	public AsciiField _Filler_4;
	public AsciiField _CH_Billing_Currency;
	public AsciiField _Reserved_1;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] report_identifier; // [10] <open=suppress, name="report_identifier">;
	public char[] Sequence_Number; // [8] <open=suppress, name="Sequence_Number">;
	public char[] Billing_Identifier; // [10] <open=suppress, name="Billing_Identifier">;
	public char[] Filler_1; // [1] <open=suppress, name="Filler_1">;
	public char[] Recipient; // [12] <open=suppress, name="Recipient">;
	public char[] Filler_2; // [1] <open=suppress, name="Filler_2">;
	public char[] Account_Low_Range; // [18] <open=suppress, name="Account_Low_Range">;
	public char[] Filler_3; // [1] <open=suppress, name="Filler_3">;
	public char[] Account_High_Range; // [18] <open=suppress, name="Account_High_Range">;
	public char[] Filler_4; // [1] <open=suppress, name="Filler_4">;
	public char[] CH_Billing_Currency; // [3] <open=suppress, name="CH_Billing_Currency">;
	public char[] Reserved_1; // [68] <open=suppress, name="Reserved_1">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_33_0_BIIDCCURR1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_BIIDCCURR1(String content) {
		super(content);
	}

	public visa_b2_33_0_BIIDCCURR1() {
		super();
	}

	public visa_b2_33_0_BIIDCCURR1(int ifReturn) {
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
		addElement("report_identifier", 10);
		addElement("Sequence_Number", 8);
		addElement("Billing_Identifier", 10);
		addElement("Filler_1", 1);
		addElement("Recipient", 12);
		addElement("Filler_2", 1);
		addElement("Account_Low_Range", 18);
		addElement("Filler_3", 1);
		addElement("Account_High_Range", 18);
		addElement("Filler_4", 1);
		addElement("CH_Billing_Currency", 3);
		addElement("Reserved_1", 68);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		report_identifier = getElement("report_identifier");
		Sequence_Number = getElement("Sequence_Number");
		Billing_Identifier = getElement("Billing_Identifier");
		Filler_1 = getElement("Filler_1");
		Recipient = getElement("Recipient");
		Filler_2 = getElement("Filler_2");
		Account_Low_Range = getElement("Account_Low_Range");
		Filler_3 = getElement("Filler_3");
		Account_High_Range = getElement("Account_High_Range");
		Filler_4 = getElement("Filler_4");
		CH_Billing_Currency = getElement("CH_Billing_Currency");
		Reserved_1 = getElement("Reserved_1");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_BIIDCCURR1_ST";
	}
}