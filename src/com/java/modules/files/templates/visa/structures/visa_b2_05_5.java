package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_05_5 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _transaction_identifier;
	public AsciiField _authorized_amount;
	public AsciiField _authorized_currency;
	public AsciiField _response_code;
	public AsciiField _validation_code;
	public AsciiField _excluded_tran_id_reason;
	public AsciiField _crs_processing_code;
	public AsciiField _chargeback_rights_indicator;
	public AsciiField _multi_clear_seq_number;
	public AsciiField _multi_clear_seq_count;
	public AsciiField _auth_data_indicator;
	public AsciiField _total_authorized_amount;
	public AsciiField _information_indicator;
	public AsciiField _merchant_telephone;
	public AsciiField _additional_data_indicator;
	public AsciiField _merchant_volume_indicator;
	public AsciiField _ecommerce_goods_indicator;
	public AsciiField _merchant_verification_value;
	public AsciiField _interchange_fee_amount;
	public AsciiField _interchange_fee_sign;
	public AsciiField _source_to_base_rate;
	public AsciiField _base_to_destination_rate;
	public AsciiField _isa_amount;
	public AsciiField _product_id;
	public AsciiField _reserved_1;
	public AsciiField _DCC_Conversion_Flag;
	public AsciiField _Account_Type_Identification;
	public AsciiField _spend_qualified_indicator;
	public AsciiField _pan_token;
	public AsciiField _Reserved_2;
	public AsciiField _cvv2_result_code;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] transaction_identifier; // [15] <open=suppress, name="transaction_identifier">;
	public char[] authorized_amount; // [12] <open=suppress, name="authorized_amount">;
	public char[] authorized_currency; // [3] <open=suppress, name="authorized_currency">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] validation_code; // [4] <open=suppress, name="validation_code">;
	public char[] excluded_tran_id_reason; // [1] <open=suppress, name="excluded_tran_id_reason">;
	public char[] crs_processing_code; // [1] <open=suppress, name="crs_processing_code">;
	public char[] chargeback_rights_indicator; // [2] <open=suppress, name="chargeback_rights_indicator">;
	public char[] multi_clear_seq_number; // [2] <open=suppress, name="multi_clear_seq_number">;
	public char[] multi_clear_seq_count; // [2] <open=suppress, name="multi_clear_seq_count">;
	public char[] auth_data_indicator; // [1] <open=suppress, name="auth_data_indicator">;
	public char[] total_authorized_amount; // [12] <open=suppress, name="total_authorized_amount">;
	public char[] information_indicator; // [1] <open=suppress, name="information_indicator">;
	public char[] merchant_telephone; // [14] <open=suppress, name="merchant_telephone">;
	public char[] additional_data_indicator; // [1] <open=suppress, name="additional_data_indicator">;
	public char[] merchant_volume_indicator; // [2] <open=suppress, name="merchant_volume_indicator">;
	public char[] ecommerce_goods_indicator; // [2] <open=suppress, name="ecommerce_goods_indicator">;
	public char[] merchant_verification_value; // [10] <open=suppress, name="merchant_verification_value">;
	public char[] interchange_fee_amount; // [15] <open=suppress, name="interchange_fee_amount">;
	public char[] interchange_fee_sign; // [1] <open=suppress, name="interchange_fee_sign">;
	public char[] source_to_base_rate; // [8] <open=suppress, name="source_to_base_rate">;
	public char[] base_to_destination_rate; // [8] <open=suppress, name="base_to_destination_rate">;
	public char[] isa_amount; // [12] <open=suppress, name="isa_amount">;
	public char[] product_id; // [2] <open=suppress, name="product_id">;
	public char[] reserved_1; // [6] <open=suppress, name="reserved_1">;
	public char[] DCC_Conversion_Flag; // [1] <open=suppress, name="DCC_Conversion_Flag">;
	public char[] Account_Type_Identification; // [4] <open=suppress, name="Account_Type_Identification">;
	public char[] spend_qualified_indicator; // [1] <open=suppress, name="spend_qualified_indicator">;
	public char[] pan_token; // [16] <open=suppress, name="pan_token">;
	public char[] Reserved_2; // [2] <open=suppress, name="Reserved_2">;
	public char[] cvv2_result_code; // [1] <open=suppress, name="cvv2_result_code">;

	public visa_b2_05_5(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_05_5(String content) {
		super(content);
	}

	public visa_b2_05_5() {
		super();
	}

	public visa_b2_05_5(int ifReturn) {
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
		addElement("transaction_identifier", 15);
		addElement("authorized_amount", 12);
		addElement("authorized_currency", 3);
		addElement("response_code", 2);
		addElement("validation_code", 4);
		addElement("excluded_tran_id_reason", 1);
		addElement("crs_processing_code", 1);
		addElement("chargeback_rights_indicator", 2);
		addElement("multi_clear_seq_number", 2);
		addElement("multi_clear_seq_count", 2);
		addElement("auth_data_indicator", 1);
		addElement("total_authorized_amount", 12);
		addElement("information_indicator", 1);
		addElement("merchant_telephone", 14);
		addElement("additional_data_indicator", 1);
		addElement("merchant_volume_indicator", 2);
		addElement("ecommerce_goods_indicator", 2);
		addElement("merchant_verification_value", 10);
		addElement("interchange_fee_amount", 15);
		addElement("interchange_fee_sign", 1);
		addElement("source_to_base_rate", 8);
		addElement("base_to_destination_rate", 8);
		addElement("isa_amount", 12);
		addElement("product_id", 2);
		addElement("reserved_1", 6);
		addElement("DCC_Conversion_Flag", 1);
		addElement("Account_Type_Identification", 4);
		addElement("spend_qualified_indicator", 1);
		addElement("pan_token", 16);
		addElement("Reserved_2", 2);
		addElement("cvv2_result_code", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		transaction_identifier = getElement("transaction_identifier");
		authorized_amount = getElement("authorized_amount");
		authorized_currency = getElement("authorized_currency");
		response_code = getElement("response_code");
		validation_code = getElement("validation_code");
		excluded_tran_id_reason = getElement("excluded_tran_id_reason");
		crs_processing_code = getElement("crs_processing_code");
		chargeback_rights_indicator = getElement("chargeback_rights_indicator");
		multi_clear_seq_number = getElement("multi_clear_seq_number");
		multi_clear_seq_count = getElement("multi_clear_seq_count");
		auth_data_indicator = getElement("auth_data_indicator");
		total_authorized_amount = getElement("total_authorized_amount");
		information_indicator = getElement("information_indicator");
		merchant_telephone = getElement("merchant_telephone");
		additional_data_indicator = getElement("additional_data_indicator");
		merchant_volume_indicator = getElement("merchant_volume_indicator");
		ecommerce_goods_indicator = getElement("ecommerce_goods_indicator");
		merchant_verification_value = getElement("merchant_verification_value");
		interchange_fee_amount = getElement("interchange_fee_amount");
		interchange_fee_sign = getElement("interchange_fee_sign");
		source_to_base_rate = getElement("source_to_base_rate");
		base_to_destination_rate = getElement("base_to_destination_rate");
		isa_amount = getElement("isa_amount");
		product_id = getElement("product_id");
		reserved_1 = getElement("reserved_1");
		DCC_Conversion_Flag = getElement("DCC_Conversion_Flag");
		Account_Type_Identification = getElement("Account_Type_Identification");
		spend_qualified_indicator = getElement("spend_qualified_indicator");
		pan_token = getElement("pan_token");
		Reserved_2 = getElement("Reserved_2");
		cvv2_result_code = getElement("cvv2_result_code");

	}

	@Override
	public String getDescription() {
		return "TCR5 - Payment Service Data";
	}
}