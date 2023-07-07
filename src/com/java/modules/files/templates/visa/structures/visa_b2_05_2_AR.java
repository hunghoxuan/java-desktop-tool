package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_2_AR extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _installment_payment_amount;
	public AsciiField _country_code;
	public AsciiField _installment_payment_indicator;
	public AsciiField _no_of_installment_payments;
	public AsciiField _install_payment_number;
	public AsciiField _install_payment_interest_amt;
	public AsciiField _install_payment_interest_vat;
	public AsciiField _install_payment_risk_fee_amt;
	public AsciiField _install_payment_risk_fee_vat;
	public AsciiField _install_payment_irf_ind;
	public AsciiField _install_payment_settle_ind;
	public AsciiField _deferred_billing_date;
	public AsciiField _deferred_settlement_date;
	public AsciiField _tip_amount;
	public AsciiField _interchange_reimbursement_fee;
	public AsciiField _vat_national_reimbursement_fee;
	public AsciiField _promotion_data;
	public AsciiField _orig_deferred_settlement_date;
	public AsciiField _reserverd_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] installment_payment_amount; // [12] <open=suppress, name = "installment_payment_amount">;
	public char[] country_code; // [3] <open=suppress, name = "country_code">;
	public char[] installment_payment_indicator; // [2] <open=suppress, name = "installment_payment_indicator">;
	public char[] no_of_installment_payments; // [2] <open=suppress, name = "no_of_installment_payments">;
	public char[] install_payment_number; // [2] <open=suppress, name = "install_payment_number">;
	public char[] install_payment_interest_amt; // [12] <open=suppress, name = "install_payment_interest_amt">;
	public char[] install_payment_interest_vat; // [10] <open=suppress, name = "install_payment_interest_vat">;
	public char[] install_payment_risk_fee_amt; // [10] <open=suppress, name = "install_payment_risk_fee_amt">;
	public char[] install_payment_risk_fee_vat; // [10] <open=suppress, name = "install_payment_risk_fee_vat">;
	public char[] install_payment_irf_ind; // [1] <open=suppress, name = "install_payment_irf_ind">;
	public char[] install_payment_settle_ind; // [1] <open=suppress, name = "install_payment_settle_ind">;
	public char[] deferred_billing_date; // [6] <open=suppress, name = "deferred_billing_date">;
	public char[] deferred_settlement_date; // [6] <open=suppress, name = "deferred_settlement_date">;
	public char[] tip_amount; // [12] <open=suppress, name = "tip_amount">;
	public char[] interchange_reimbursement_fee; // [10] <open=suppress, name = "interchange_reimbursement_fee">;
	public char[] vat_national_reimbursement_fee; // [10] <open=suppress, name = "vat_national_reimbursement_fee">;
	public char[] promotion_data; // [20] <open=suppress, name = "promotion_data">;
	public char[] orig_deferred_settlement_date; // [6] <open=suppress, name = "orig_deferred_settlement_date">;
	public char[] reserverd_2; // [29] <open=suppress, name = "reserverd_2">;

	public visa_b2_05_2_AR(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_2_AR(String content) {
		super(content);
	}

	public visa_b2_05_2_AR() {
		super();
	}

	public visa_b2_05_2_AR(int ifReturn) {
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
		addElement("installment_payment_amount", 12);
		addElement("country_code", 3);
		addElement("installment_payment_indicator", 2);
		addElement("no_of_installment_payments", 2);
		addElement("install_payment_number", 2);
		addElement("install_payment_interest_amt", 12);
		addElement("install_payment_interest_vat", 10);
		addElement("install_payment_risk_fee_amt", 10);
		addElement("install_payment_risk_fee_vat", 10);
		addElement("install_payment_irf_ind", 1);
		addElement("install_payment_settle_ind", 1);
		addElement("deferred_billing_date", 6);
		addElement("deferred_settlement_date", 6);
		addElement("tip_amount", 12);
		addElement("interchange_reimbursement_fee", 10);
		addElement("vat_national_reimbursement_fee", 10);
		addElement("promotion_data", 20);
		addElement("orig_deferred_settlement_date", 6);
		addElement("reserverd_2", 29);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		installment_payment_amount = getElement("installment_payment_amount");
		country_code = getElement("country_code");
		installment_payment_indicator = getElement("installment_payment_indicator");
		no_of_installment_payments = getElement("no_of_installment_payments");
		install_payment_number = getElement("install_payment_number");
		install_payment_interest_amt = getElement("install_payment_interest_amt");
		install_payment_interest_vat = getElement("install_payment_interest_vat");
		install_payment_risk_fee_amt = getElement("install_payment_risk_fee_amt");
		install_payment_risk_fee_vat = getElement("install_payment_risk_fee_vat");
		install_payment_irf_ind = getElement("install_payment_irf_ind");
		install_payment_settle_ind = getElement("install_payment_settle_ind");
		deferred_billing_date = getElement("deferred_billing_date");
		deferred_settlement_date = getElement("deferred_settlement_date");
		tip_amount = getElement("tip_amount");
		interchange_reimbursement_fee = getElement("interchange_reimbursement_fee");
		vat_national_reimbursement_fee = getElement("vat_national_reimbursement_fee");
		promotion_data = getElement("promotion_data");
		orig_deferred_settlement_date = getElement("orig_deferred_settlement_date");
		reserverd_2 = getElement("reserverd_2");

	}

	@Override
	public String getDescription() {
		return "TCR 2 - AR";
	}
}