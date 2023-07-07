package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _Network_ID;
	public AsciiField _Dispute_cond;
	public AsciiField _VROL_Fin_ID;
	public AsciiField _VROL_case_number;
	public AsciiField _VROL_Bundle_case_number;
	public AsciiField _Client_case_number;
	public AsciiField _Reserved_1;
	public AsciiField _Mult_Cl_Seq_Number;
	public AsciiField _Mult_Cl_Seq_Count;
	public AsciiField _Product_ID;
	public AsciiField _Spend_Qualified_indicator;
	public AsciiField _Dispute_Fin_Reason_Code;
	public AsciiField _Settl_Flag;
	public AsciiField _Usage_Code;
	public AsciiField _Trans_ID;
	public AsciiField _Acq_BID;
	public AsciiField _Reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] Network_ID; // [4] <open=suppress, name="Network_ID">;
	public char[] Dispute_cond; // [3] <open=suppress, name="Dispute_cond">;
	public char[] VROL_Fin_ID; // [11] <open=suppress, name="VROL_Fin_ID">;
	public char[] VROL_case_number; // [10] <open=suppress, name="VROL_case_number">;
	public char[] VROL_Bundle_case_number; // [10] <open=suppress, name="VROL_Bundle_case_number">;
	public char[] Client_case_number; // [20] <open=suppress, name="Client_case_number">;
	public char[] Reserved_1; // [4] <open=suppress, name="Reserved_1">;
	public char[] Mult_Cl_Seq_Number; // [2] <open=suppress, name="Mult_Cl_Seq_Number">;
	public char[] Mult_Cl_Seq_Count; // [2] <open=suppress, name="Mult_Cl_Seq_Count">;
	public char[] Product_ID; // [2] <open=suppress, name="Product_ID">;
	public char[] Spend_Qualified_indicator; // [1] <open=suppress, name="Spend_Qualified_indicator">;
	public char[] Dispute_Fin_Reason_Code; // [2] <open=suppress, name="Dispute_Fin_Reason_Code">;
	public char[] Settl_Flag; // [1] <open=suppress, name="Settl_Flag">;
	public char[] Usage_Code; // [1] <open=suppress, name="Usage_Code">;
	public char[] Trans_ID; // [15] <open=suppress, name="Trans_ID">;
	public char[] Acq_BID; // [8] <open=suppress, name="Acq_BID">;
	public char[] Reserved_2; // [68] <open=suppress, name="Reserved_2">;

	public visa_b2_33_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_1(String content) {
		super(content);
	}

	public visa_b2_33_1() {
		super();
	}

	public visa_b2_33_1(int ifReturn) {
		super(ifReturn);
	}

	@Override
	public void initFields(int ifReturn) {
		// typedef struct {;
		addElement("trans_code", 2);
		addElement("trans_code_qualif", 1);
		addElement("trans_comp_seq", 1);
		addElement("Network_ID", 4);
		addElement("Dispute_cond", 3);
		addElement("VROL_Fin_ID", 11);
		addElement("VROL_case_number", 10);
		addElement("VROL_Bundle_case_number", 10);
		addElement("Client_case_number", 20);
		addElement("Reserved_1", 4);
		addElement("Mult_Cl_Seq_Number", 2);
		addElement("Mult_Cl_Seq_Count", 2);
		addElement("Product_ID", 2);
		addElement("Spend_Qualified_indicator", 1);
		addElement("Dispute_Fin_Reason_Code", 2);
		addElement("Settl_Flag", 1);
		addElement("Usage_Code", 1);
		addElement("Trans_ID", 15);
		addElement("Acq_BID", 8);
		addElement("Reserved_2", 68);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		Network_ID = getElement("Network_ID");
		Dispute_cond = getElement("Dispute_cond");
		VROL_Fin_ID = getElement("VROL_Fin_ID");
		VROL_case_number = getElement("VROL_case_number");
		VROL_Bundle_case_number = getElement("VROL_Bundle_case_number");
		Client_case_number = getElement("Client_case_number");
		Reserved_1 = getElement("Reserved_1");
		Mult_Cl_Seq_Number = getElement("Mult_Cl_Seq_Number");
		Mult_Cl_Seq_Count = getElement("Mult_Cl_Seq_Count");
		Product_ID = getElement("Product_ID");
		Spend_Qualified_indicator = getElement("Spend_Qualified_indicator");
		Dispute_Fin_Reason_Code = getElement("Dispute_Fin_Reason_Code");
		Settl_Flag = getElement("Settl_Flag");
		Usage_Code = getElement("Usage_Code");
		Trans_ID = getElement("Trans_ID");
		Acq_BID = getElement("Acq_BID");
		Reserved_2 = getElement("Reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_1_ST";
	}
}