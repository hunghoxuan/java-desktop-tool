package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_4_SVCRFD extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _agent_unique_id;
	public AsciiField _reserved_1;
	public AsciiField _business_format_code;
	public AsciiField _network_id_code;
	public AsciiField _contact_info;
	public AsciiField _adjustment_proc_ind;
	public AsciiField _message_reason_code;
	public AsciiField _dispute_condition;
	public AsciiField _vrol_financial_id;
	public AsciiField _vrol_case_number;
	public AsciiField _vrol_bundle_case_number;
	public AsciiField _client_case_number;
	public AsciiField _dispute_status;
	public AsciiField _surcharge_amount;
	public AsciiField _surcharge_c_d_ind;
	public AsciiField _reserved;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] agent_unique_id; // [5] <open=suppress, name="agent_unique_id">;
	public char[] reserved_1; // [5] <open=suppress, name="reserved_1">;
	public char[] business_format_code; // [2] <open=suppress, name="business_format_code">;
	public char[] network_id_code; // [4] <open=suppress, name="network_id_code">;
	public char[] contact_info; // [25] <open=suppress, name="contact_info">;
	public char[] adjustment_proc_ind; // [1] <open=suppress, name="adjustment_proc_ind">;
	public char[] message_reason_code; // [4] <open=suppress, name="message_reason_code">;
	public char[] dispute_condition; // [3] <open=suppress, name="dispute_condition">;
	public char[] vrol_financial_id; // [11] <open=suppress, name="vrol_financial_id">;
	public char[] vrol_case_number; // [10] <open=suppress, name="vrol_case_number">;
	public char[] vrol_bundle_case_number; // [10] <open=suppress, name="vrol_bundle_case_number">;
	public char[] client_case_number; // [20] <open=suppress, name="client_case_number">;
	public char[] dispute_status; // [2] <open=suppress, name="dispute_status">;
	public char[] surcharge_amount; // [8] <open=suppress, name="surcharge_amount">;
	public char[] surcharge_c_d_ind; // [2] <open=suppress, name="surcharge_c_d_ind">;
	public char[] reserved; // [52] <open=suppress, name="reserved">;

	public visa_b2_05_4_SVCRFD(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_4_SVCRFD(String content) {
		super(content);
	}

	public visa_b2_05_4_SVCRFD() {
		super();
	}

	public visa_b2_05_4_SVCRFD(int ifReturn) {
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
		addElement("agent_unique_id", 5);
		addElement("reserved_1", 5);
		addElement("business_format_code", 2);
		addElement("network_id_code", 4);
		addElement("contact_info", 25);
		addElement("adjustment_proc_ind", 1);
		addElement("message_reason_code", 4);
		addElement("dispute_condition", 3);
		addElement("vrol_financial_id", 11);
		addElement("vrol_case_number", 10);
		addElement("vrol_bundle_case_number", 10);
		addElement("client_case_number", 20);
		addElement("dispute_status", 2);
		addElement("surcharge_amount", 8);
		addElement("surcharge_c_d_ind", 2);
		addElement("reserved", 52);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		agent_unique_id = getElement("agent_unique_id");
		reserved_1 = getElement("reserved_1");
		business_format_code = getElement("business_format_code");
		network_id_code = getElement("network_id_code");
		contact_info = getElement("contact_info");
		adjustment_proc_ind = getElement("adjustment_proc_ind");
		message_reason_code = getElement("message_reason_code");
		dispute_condition = getElement("dispute_condition");
		vrol_financial_id = getElement("vrol_financial_id");
		vrol_case_number = getElement("vrol_case_number");
		vrol_bundle_case_number = getElement("vrol_bundle_case_number");
		client_case_number = getElement("client_case_number");
		dispute_status = getElement("dispute_status");
		surcharge_amount = getElement("surcharge_amount");
		surcharge_c_d_ind = getElement("surcharge_c_d_ind");
		reserved = getElement("reserved");

	}

	@Override
	public String getDescription() {
		return "";
	}
}