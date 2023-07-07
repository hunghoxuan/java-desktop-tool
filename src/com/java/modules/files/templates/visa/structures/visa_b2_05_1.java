package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_1 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _bus_format_code;
	public AsciiField _token_assurance_level;
	public AsciiField _rate_table_id;
	public AsciiField _reserved_1;
	public AsciiField _chargeback_ref_num;
	public AsciiField _document_indicator;
	public AsciiField _message_text;
	public AsciiField _special_cond_ind;
	public AsciiField _fee_program_indicator;
	public AsciiField _issuer_charge;
	public AsciiField _persistent_fx_applied;
	public AsciiField _card_acceptor_id;
	public AsciiField _terminal_id;
	public AsciiField _national_reimbrs_fee;
	public AsciiField _mail_telefon_ind;
	public AsciiField _special_chgbck_ind;
	public AsciiField _interface_trace;
	public AsciiField _cardhld_acct_ind;
	public AsciiField _prepaid_card_ind;
	public AsciiField _service_development_field;
	public AsciiField _avs_response_code;
	public AsciiField _auth_source_code;
	public AsciiField _purch_ind_format;
	public AsciiField _atm_account_selection;
	public AsciiField _installment_pay_cnt;
	public AsciiField _purchase_id;
	public AsciiField _cashback;
	public AsciiField _chip_condition_code;
	public AsciiField _pos_environment;
	public AsciiField _undefined_data;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] bus_format_code; // [1] <open=suppress, name="bus_format_code">;
	public char[] token_assurance_level; // [2] <open=suppress, name="token_assurance_level">;
	public char[] rate_table_id; // [5] <open=suppress, name="rate_table_id">; // BOC-210172 begin
	public char[] reserved_1; // [4] <open=suppress, name="reserved_1">;
	public char[] chargeback_ref_num; // [6] <open=suppress, name="chargeback_ref_num">;
	public char[] document_indicator; // [1] <open=suppress, name="document_indicator">;
	public char[] message_text; // [50] <open=suppress, name="message_text">;
	public char[] special_cond_ind; // [2] <open=suppress, name="special_cond_ind">;
	public char[] fee_program_indicator; // [3] <open=suppress, name="fee_program_indicator">;
	public char[] issuer_charge; // [1] <open=suppress, name="issuer_charge">;
	public char[] persistent_fx_applied; // [1] <open=suppress, name="persistent_fx_applied">; // BOC-210172 end
	public char[] card_acceptor_id; // [15] <open=suppress, name="card_acceptor_id">;
	public char[] terminal_id; // [8] <open=suppress, name="terminal_id">;
	public char[] national_reimbrs_fee; // [12] <open=suppress, name="national_reimbrs_fee">;
	public char[] mail_telefon_ind; // [1] <open=suppress, name="mail_telefon_ind">;
	public char[] special_chgbck_ind; // [1] <open=suppress, name="special_chgbck_ind">;
	public char[] interface_trace; // [6] <open=suppress, name="interface_trace">;
	public char[] cardhld_acct_ind; // [1] <open=suppress, name="cardhld_acct_ind">;
	public char[] prepaid_card_ind; // [1] <open=suppress, name="prepaid_card_ind">;
	public char[] service_development_field; // [1] <open=suppress, name="service_development_field">;
	public char[] avs_response_code; // [1] <open=suppress, name="avs_response_code">;
	public char[] auth_source_code; // [1] <open=suppress, name="auth_source_code">;
	public char[] purch_ind_format; // [1] <open=suppress, name="purch_ind_format">;
	public char[] atm_account_selection; // [1] <open=suppress, name="atm_account_selection">;
	public char[] installment_pay_cnt; // [2] <open=suppress, name="installment_pay_cnt">;
	public char[] purchase_id; // [25] <open=suppress, name="purchase_id">;
	public char[] cashback; // [9] <open=suppress, name="cashback">;
	public char[] chip_condition_code; // [1] <open=suppress, name="chip_condition_code">;
	public char[] pos_environment; // [1] <open=suppress, name="pos_environment">;
	public char[] undefined_data; // [3] <open=suppress, name="validation_record_type">;

	public visa_b2_05_1(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_1(String content) {
		super(content);
	}

	public visa_b2_05_1() {
		super();
	}

	public visa_b2_05_1(int ifReturn) {
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
		addElement("bus_format_code", 1);
		addElement("token_assurance_level", 2);
		addElement("rate_table_id", 5);
		addElement("reserved_1", 4);
		addElement("chargeback_ref_num", 6);
		addElement("document_indicator", 1);
		addElement("message_text", 50);
		addElement("special_cond_ind", 2);
		addElement("fee_program_indicator", 3);
		addElement("issuer_charge", 1);
		addElement("persistent_fx_applied", 1);
		addElement("card_acceptor_id", 15);
		addElement("terminal_id", 8);
		addElement("national_reimbrs_fee", 12);
		addElement("mail_telefon_ind", 1);
		addElement("special_chgbck_ind", 1);
		addElement("interface_trace", 6);
		addElement("cardhld_acct_ind", 1);
		addElement("prepaid_card_ind", 1);
		addElement("service_development_field", 1);
		addElement("avs_response_code", 1);
		addElement("auth_source_code", 1);
		addElement("purch_ind_format", 1);
		addElement("atm_account_selection", 1);
		addElement("installment_pay_cnt", 2);
		addElement("purchase_id", 25);
		addElement("cashback", 9);
		addElement("chip_condition_code", 1);
		addElement("pos_environment", 1);
		// if(ReadByte() != 0x0d && ReadByte() != 0x0a){
		addElement("undefined_data", 3);
		// FSkip(168);;
		// }

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		bus_format_code = getElement("bus_format_code");
		token_assurance_level = getElement("token_assurance_level");
		rate_table_id = getElement("rate_table_id");
		reserved_1 = getElement("reserved_1");
		chargeback_ref_num = getElement("chargeback_ref_num");
		document_indicator = getElement("document_indicator");
		message_text = getElement("message_text");
		special_cond_ind = getElement("special_cond_ind");
		fee_program_indicator = getElement("fee_program_indicator");
		issuer_charge = getElement("issuer_charge");
		persistent_fx_applied = getElement("persistent_fx_applied");
		card_acceptor_id = getElement("card_acceptor_id");
		terminal_id = getElement("terminal_id");
		national_reimbrs_fee = getElement("national_reimbrs_fee");
		mail_telefon_ind = getElement("mail_telefon_ind");
		special_chgbck_ind = getElement("special_chgbck_ind");
		interface_trace = getElement("interface_trace");
		cardhld_acct_ind = getElement("cardhld_acct_ind");
		prepaid_card_ind = getElement("prepaid_card_ind");
		service_development_field = getElement("service_development_field");
		avs_response_code = getElement("avs_response_code");
		auth_source_code = getElement("auth_source_code");
		purch_ind_format = getElement("purch_ind_format");
		atm_account_selection = getElement("atm_account_selection");
		installment_pay_cnt = getElement("installment_pay_cnt");
		purchase_id = getElement("purchase_id");
		cashback = getElement("cashback");
		chip_condition_code = getElement("chip_condition_code");
		pos_environment = getElement("pos_environment");
		undefined_data = getElement("undefined_data");

	}

	@Override
	public String getDescription() {
		return "TCR 1 - Additional Data";
	}
}