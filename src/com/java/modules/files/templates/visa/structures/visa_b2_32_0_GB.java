package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_32_0_GB extends AsciiMessage {
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
	public AsciiField _merchant_cetegory_code;
	public AsciiField _mcc_response_flag;
	public AsciiField _listing_reason_code;
	public AsciiField _lrc_response_flag;
	public AsciiField _merchant_legal_name;
	public AsciiField _mln_response_flag;
	public AsciiField _merc_trading_name_dba;
	public AsciiField _mtn_dba_response_flag;
	public AsciiField _vat_reg_no_merchant_id1;
	public AsciiField _vat_reg_n_merc_resp_flag;
	public AsciiField _bank_sort_code;
	public AsciiField _bk_srt_cde_response_flag;
	public AsciiField _bank_account_number;
	public AsciiField _bk_ac_no_response_flag;
	public AsciiField _contract_opened_date;
	public AsciiField _cod_response_flag;
	public AsciiField _contract_closed_date;
	public AsciiField _ccd_response_flag;
	public AsciiField _merchant_tel_no_1;
	public AsciiField _reserved_2;
	public AsciiField _merc_tel_n_response_flag;
	public AsciiField _reserved_3;
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
	public char[] merchant_cetegory_code; // [4] <open=suppress, name="merchant_cetegory_code">;
	public char[] mcc_response_flag; // [1] <open=suppress, name="mcc_response_flag">;
	public char[] listing_reason_code; // [2] <open=suppress, name="listing_reason_code">;
	public char[] lrc_response_flag; // [1] <open=suppress, name="lrc_response_flag">;
	public char[] merchant_legal_name; // [30] <open=suppress, name="merchant_legal_name">;
	public char[] mln_response_flag; // [1] <open=suppress, name="mln_response_flag">;
	public char[] merc_trading_name_dba; // [30] <open=suppress, name="merc_trading_name_dba">;
	public char[] mtn_dba_response_flag; // [1] <open=suppress, name="mtn_dba_response_flag">;
	public char[] vat_reg_no_merchant_id1; // [9] <open=suppress, name="vat_reg_no_merchant_id1">;
	public char[] vat_reg_n_merc_resp_flag; // [1] <open=suppress, name="vat_reg_n_merc_resp_flag">;
	public char[] bank_sort_code; // [6] <open=suppress, name="bank_sort_code">;
	public char[] bk_srt_cde_response_flag; // [1] <open=suppress, name="bk_srt_cde_response_flag">;
	public char[] bank_account_number; // [12] <open=suppress, name="bank_account_number">;
	public char[] bk_ac_no_response_flag; // [1] <open=suppress, name="bk_ac_no_response_flag">;
	public char[] contract_opened_date; // [6] <open=suppress, name="contract_opened_date">;
	public char[] cod_response_flag; // [1] <open=suppress, name="cod_response_flag">;
	public char[] contract_closed_date; // [6] <open=suppress, name="contract_closed_date">;
	public char[] ccd_response_flag; // [1] <open=suppress, name="ccd_response_flag">;
	public char[] merchant_tel_no_1; // [11] <open=suppress, name="merchant_tel_no_1">;
	public char[] reserved_2; // [4] <open=suppress, name="reserved_2">;
	public char[] merc_tel_n_response_flag; // [1] <open=suppress, name="merc_tel_n_response_flag">;
	public char[] reserved_3; // [2] <open=suppress, name="reserved_3">;
	public char[] reimbursement_attr_code; // [1] <open=suppress, name="reimbursement_attr_code">;

	public visa_b2_32_0_GB(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_32_0_GB(String content) {
		super(content);
	}

	public visa_b2_32_0_GB() {
		super();
	}

	public visa_b2_32_0_GB(int ifReturn) {
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
		addElement("merchant_cetegory_code", 4);
		addElement("mcc_response_flag", 1);
		addElement("listing_reason_code", 2);
		addElement("lrc_response_flag", 1);
		addElement("merchant_legal_name", 30);
		addElement("mln_response_flag", 1);
		addElement("merc_trading_name_dba", 30);
		addElement("mtn_dba_response_flag", 1);
		addElement("vat_reg_no_merchant_id1", 9);
		addElement("vat_reg_n_merc_resp_flag", 1);
		addElement("bank_sort_code", 6);
		addElement("bk_srt_cde_response_flag", 1);
		addElement("bank_account_number", 12);
		addElement("bk_ac_no_response_flag", 1);
		addElement("contract_opened_date", 6);
		addElement("cod_response_flag", 1);
		addElement("contract_closed_date", 6);
		addElement("ccd_response_flag", 1);
		addElement("merchant_tel_no_1", 11);
		addElement("reserved_2", 4);
		addElement("merc_tel_n_response_flag", 1);
		addElement("reserved_3", 2);
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
		merchant_cetegory_code = getElement("merchant_cetegory_code");
		mcc_response_flag = getElement("mcc_response_flag");
		listing_reason_code = getElement("listing_reason_code");
		lrc_response_flag = getElement("lrc_response_flag");
		merchant_legal_name = getElement("merchant_legal_name");
		mln_response_flag = getElement("mln_response_flag");
		merc_trading_name_dba = getElement("merc_trading_name_dba");
		mtn_dba_response_flag = getElement("mtn_dba_response_flag");
		vat_reg_no_merchant_id1 = getElement("vat_reg_no_merchant_id1");
		vat_reg_n_merc_resp_flag = getElement("vat_reg_n_merc_resp_flag");
		bank_sort_code = getElement("bank_sort_code");
		bk_srt_cde_response_flag = getElement("bk_srt_cde_response_flag");
		bank_account_number = getElement("bank_account_number");
		bk_ac_no_response_flag = getElement("bk_ac_no_response_flag");
		contract_opened_date = getElement("contract_opened_date");
		cod_response_flag = getElement("cod_response_flag");
		contract_closed_date = getElement("contract_closed_date");
		ccd_response_flag = getElement("ccd_response_flag");
		merchant_tel_no_1 = getElement("merchant_tel_no_1");
		reserved_2 = getElement("reserved_2");
		merc_tel_n_response_flag = getElement("merc_tel_n_response_flag");
		reserved_3 = getElement("reserved_3");
		reimbursement_attr_code = getElement("reimbursement_attr_code");

	}

	@Override
	public String getDescription() {
		return "TCR0 - GB -Additional Data";
	}
}