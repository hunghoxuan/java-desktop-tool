package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_32_0_AU extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _request_date;
	public AsciiField _locator_number;
	public AsciiField _nmas_seq_number;
	public AsciiField _action_indicator;
	public AsciiField _rd_ln_s_ai_response_flag;
	public AsciiField _acquirer_country_code;
	public AsciiField _acc_response_flag;
	public AsciiField _merchant_country_code;
	public AsciiField _merc_ct_cd_response_flag;
	public AsciiField _merc_trading_name_dba;
	public AsciiField _reserved_4;
	public AsciiField _mtn_dba_response_flag;
	public AsciiField _merchant_street_address;
	public AsciiField _mer_str_ad_response_flag;
	public AsciiField _contract_opened_date;
	public AsciiField _cod_response_flag;
	public AsciiField _contract_closed_date;
	public AsciiField _ccd_response_flag;
	public AsciiField _listing_acquirer_bin;
	public AsciiField _listing_date;
	public AsciiField _reserved_6;
	public AsciiField _reimbursement_attr_code;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] request_date; // [6] <open=suppress, name="request_date">;
	public char[] locator_number; // [4] <open=suppress, name="locator_number">;
	public char[] nmas_seq_number; // [2] <open=suppress, name="nmas_seq_number">;
	public char[] action_indicator; // [2] <open=suppress, name="action_indicator">;
	public char[] rd_ln_s_ai_response_flag; // [1] <open=suppress, name="rd_ln_s_ai_response_flag">;
	public char[] acquirer_country_code; // [3] <open=suppress, name="acquirer_country_code">;
	public char[] acc_response_flag; // [1] <open=suppress, name="acc_response_flag">;
	public char[] merchant_country_code; // [3] <open=suppress, name="merchant_country_code">;
	public char[] merc_ct_cd_response_flag; // [1] <open=suppress, name="merc_ct_cd_response_flag">;
	public char[] merc_trading_name_dba; // [35] <open=suppress, name="merc_trading_name_dba">;
	public char[] reserved_4; // [5] <open=suppress, name="reserved_4">;
	public char[] mtn_dba_response_flag; // [1] <open=suppress, name="mtn_dba_response_flag">;
	public char[] merchant_street_address; // [60] <open=suppress, name="merchant_street_address">;
	public char[] mer_str_ad_response_flag; // [1] <open=suppress, name="mer_str_ad_response_flag">;
	public char[] contract_opened_date; // [6] <open=suppress, name="contract_opened_date">;
	public char[] cod_response_flag; // [1] <open=suppress, name="cod_response_flag">;
	public char[] contract_closed_date; // [6] <open=suppress, name="contract_closed_date">;
	public char[] ccd_response_flag; // [1] <open=suppress, name="ccd_response_flag">;
	public char[] listing_acquirer_bin; // [6] <open=suppress, name="listing_acquirer_bin">;
	public char[] listing_date; // [5] <open=suppress, name="listing_date">;
	public char[] reserved_6; // [1] <open=suppress, name="reserved_6">;
	public char[] reimbursement_attr_code; // [1] <open=suppress, name="reimbursement_attr_code">;

	public visa_b2_32_0_AU(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_32_0_AU(String content) {
		super(content);
	}

	public visa_b2_32_0_AU() {
		super();
	}

	public visa_b2_32_0_AU(int ifReturn) {
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
		addElement("request_date", 6);
		addElement("locator_number", 4);
		addElement("nmas_seq_number", 2);
		addElement("action_indicator", 2);
		addElement("rd_ln_s_ai_response_flag", 1);
		addElement("acquirer_country_code", 3);
		addElement("acc_response_flag", 1);
		addElement("merchant_country_code", 3);
		addElement("merc_ct_cd_response_flag", 1);
		addElement("merc_trading_name_dba", 35);
		addElement("reserved_4", 5);
		addElement("mtn_dba_response_flag", 1);
		addElement("merchant_street_address", 60);
		addElement("mer_str_ad_response_flag", 1);
		addElement("contract_opened_date", 6);
		addElement("cod_response_flag", 1);
		addElement("contract_closed_date", 6);
		addElement("ccd_response_flag", 1);
		addElement("listing_acquirer_bin", 6);
		addElement("listing_date", 5);
		addElement("reserved_6", 1);
		addElement("reimbursement_attr_code", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		request_date = getElement("request_date");
		locator_number = getElement("locator_number");
		nmas_seq_number = getElement("nmas_seq_number");
		action_indicator = getElement("action_indicator");
		rd_ln_s_ai_response_flag = getElement("rd_ln_s_ai_response_flag");
		acquirer_country_code = getElement("acquirer_country_code");
		acc_response_flag = getElement("acc_response_flag");
		merchant_country_code = getElement("merchant_country_code");
		merc_ct_cd_response_flag = getElement("merc_ct_cd_response_flag");
		merc_trading_name_dba = getElement("merc_trading_name_dba");
		reserved_4 = getElement("reserved_4");
		mtn_dba_response_flag = getElement("mtn_dba_response_flag");
		merchant_street_address = getElement("merchant_street_address");
		mer_str_ad_response_flag = getElement("mer_str_ad_response_flag");
		contract_opened_date = getElement("contract_opened_date");
		cod_response_flag = getElement("cod_response_flag");
		contract_closed_date = getElement("contract_closed_date");
		ccd_response_flag = getElement("ccd_response_flag");
		listing_acquirer_bin = getElement("listing_acquirer_bin");
		listing_date = getElement("listing_date");
		reserved_6 = getElement("reserved_6");
		reimbursement_attr_code = getElement("reimbursement_attr_code");

	}

	@Override
	public String getDescription() {
		return "TCR0 - AU/NZ -Additional Data";
	}
}