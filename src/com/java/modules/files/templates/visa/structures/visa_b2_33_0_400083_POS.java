package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_33_0_400083_POS extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _tc33_application_code;
	public AsciiField _julian_day;
	public AsciiField _rpt_line_seq_number;
	public AsciiField _reserved_1;
	public AsciiField _acquirer_bin;
	public AsciiField _terminal_id;
	public AsciiField _transaction_date;
	public AsciiField _transaction_time;
	public AsciiField _processing_code;
	public AsciiField _account_number;
	public AsciiField _merchant_category_code;
	public AsciiField _authorized_amount;
	public AsciiField _expiry_date;
	public AsciiField _response_code;
	public AsciiField _authorization_code;
	public AsciiField _communication_line_type;
	public AsciiField _acquirer_station_id;
	public AsciiField _cvv_result;
	public AsciiField _avs_result;
	public AsciiField _entry_mode_code;
	public AsciiField _entry_capability;
	public AsciiField _condition_code;
	public AsciiField _market_specific_indicator;
	public AsciiField _terminal_format_code;
	public AsciiField _stand_in_advice_code;
	public AsciiField _currency_code;
	public AsciiField _auth_characteristics_indicator;
	public AsciiField _payment_service_tran_id;
	public AsciiField _validation_code;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] tc33_application_code; // [3] <open=suppress, name="tc33_application_code">;
	public char[] julian_day; // [3] <open=suppress, name="julian_day">;
	public char[] rpt_line_seq_number; // [10] <open=suppress, name="rpt_line_seq_number">;
	public char[] reserved_1; // [2] <open=suppress, name="reserved_1">;
	public char[] acquirer_bin; // [11] <open=suppress, name="acquirer_bin">;
	public char[] terminal_id; // [23] <open=suppress, name="terminal_id">;
	public char[] transaction_date; // [6] <open=suppress, name="transaction_date">;
	public char[] transaction_time; // [6] <open=suppress, name="transaction_time">;
	public char[] processing_code; // [2] <open=suppress, name="processing_code">;
	public char[] account_number; // [16] <open=suppress, name="account_number">;
	public char[] merchant_category_code; // [4] <open=suppress, name="merchant_category_code">;
	public char[] authorized_amount; // [12] <open=suppress, name="authorized_amount">;
	public char[] expiry_date; // [4] <open=suppress, name="expiry_date">;
	public char[] response_code; // [2] <open=suppress, name="response_code">;
	public char[] authorization_code; // [6] <open=suppress, name="authorization_code">;
	public char[] communication_line_type; // [2] <open=suppress, name="communication_line_type">;
	public char[] acquirer_station_id; // [4] <open=suppress, name="acquirer_station_id">;
	public char[] cvv_result; // [1] <open=suppress, name="cvv_result">;
	public char[] avs_result; // [1] <open=suppress, name="avs_result">;
	public char[] entry_mode_code; // [3] <open=suppress, name="entry_mode_code">;
	public char[] entry_capability; // [2] <open=suppress, name="entry_capability">;
	public char[] condition_code; // [2] <open=suppress, name="condition_code">;
	public char[] market_specific_indicator; // [1] <open=suppress, name="market_specific_indicator">;
	public char[] terminal_format_code; // [1] <open=suppress, name="terminal_format_code">;
	public char[] stand_in_advice_code; // [1] <open=suppress, name="stand_in_advice_code">;
	public char[] currency_code; // [3] <open=suppress, name="currency_code">;
	public char[] auth_characteristics_indicator; // [1] <open=suppress, name="auth_characteristics_indicator">;
	public char[] payment_service_tran_id; // [15] <open=suppress, name="payment_service_tran_id">;
	public char[] validation_code; // [4] <open=suppress, name="validation_code">;
	public char[] reserved_2; // [1] <open=suppress, name="reserved_2">;

	public visa_b2_33_0_400083_POS(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_33_0_400083_POS(String content) {
		super(content);
	}

	public visa_b2_33_0_400083_POS() {
		super();
	}

	public visa_b2_33_0_400083_POS(int ifReturn) {
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
		addElement("tc33_application_code", 3);
		addElement("julian_day", 3);
		addElement("rpt_line_seq_number", 10);
		addElement("reserved_1", 2);
		addElement("acquirer_bin", 11);
		addElement("terminal_id", 23);
		addElement("transaction_date", 6);
		addElement("transaction_time", 6);
		addElement("processing_code", 2);
		addElement("account_number", 16);
		addElement("merchant_category_code", 4);
		addElement("authorized_amount", 12);
		addElement("expiry_date", 4);
		addElement("response_code", 2);
		addElement("authorization_code", 6);
		addElement("communication_line_type", 2);
		addElement("acquirer_station_id", 4);
		addElement("cvv_result", 1);
		addElement("avs_result", 1);
		addElement("entry_mode_code", 3);
		addElement("entry_capability", 2);
		addElement("condition_code", 2);
		addElement("market_specific_indicator", 1);
		addElement("terminal_format_code", 1);
		addElement("stand_in_advice_code", 1);
		addElement("currency_code", 3);
		addElement("auth_characteristics_indicator", 1);
		addElement("payment_service_tran_id", 15);
		addElement("validation_code", 4);
		addElement("reserved_2", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		tc33_application_code = getElement("tc33_application_code");
		julian_day = getElement("julian_day");
		rpt_line_seq_number = getElement("rpt_line_seq_number");
		reserved_1 = getElement("reserved_1");
		acquirer_bin = getElement("acquirer_bin");
		terminal_id = getElement("terminal_id");
		transaction_date = getElement("transaction_date");
		transaction_time = getElement("transaction_time");
		processing_code = getElement("processing_code");
		account_number = getElement("account_number");
		merchant_category_code = getElement("merchant_category_code");
		authorized_amount = getElement("authorized_amount");
		expiry_date = getElement("expiry_date");
		response_code = getElement("response_code");
		authorization_code = getElement("authorization_code");
		communication_line_type = getElement("communication_line_type");
		acquirer_station_id = getElement("acquirer_station_id");
		cvv_result = getElement("cvv_result");
		avs_result = getElement("avs_result");
		entry_mode_code = getElement("entry_mode_code");
		entry_capability = getElement("entry_capability");
		condition_code = getElement("condition_code");
		market_specific_indicator = getElement("market_specific_indicator");
		terminal_format_code = getElement("terminal_format_code");
		stand_in_advice_code = getElement("stand_in_advice_code");
		currency_code = getElement("currency_code");
		auth_characteristics_indicator = getElement("auth_characteristics_indicator");
		payment_service_tran_id = getElement("payment_service_tran_id");
		validation_code = getElement("validation_code");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_33_0_400083_POS_ST";
	}
}