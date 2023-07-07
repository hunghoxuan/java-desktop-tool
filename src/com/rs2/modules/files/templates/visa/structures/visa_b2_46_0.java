package com.rs2.modules.files.templates.visa.structures;

import com.rs2.modules.files.isoparser.elements.ascii.AsciiField;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_46_0 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _reporting_for_sre;
	public AsciiField _rollup_to_sre;
	public AsciiField _funds_transfer_sre;
	public AsciiField _settlement_service;
	public AsciiField _settlement_currency;
	public AsciiField _clearing_currency;
	public AsciiField _business_mode;
	public AsciiField _no_data_ind;
	public AsciiField _reserved_1;
	public AsciiField _report_group;
	public AsciiField _report_subgroup;
	public AsciiField _report_id_num;
	public AsciiField _report_id_suffix;
	public AsciiField _settlement_date;
	public AsciiField _report_date;
	public AsciiField _from_date;
	public AsciiField _to_date;
	public AsciiField _charge_type;
	public AsciiField _business_trans_type;
	public AsciiField _business_trans_cycle;
	public AsciiField _reversal_ind;
	public AsciiField _return_ind;
	public AsciiField _jurisdiction;
	public AsciiField _ir_routing_ind;
	public AsciiField _source_country;
	public AsciiField _destination_country;
	public AsciiField _source_region;
	public AsciiField _destination_region;
	public AsciiField _fee_level_desc;
	public AsciiField _cr_db_net_ind;
	public AsciiField _summary_level;
	public AsciiField _reserved_2;
	public AsciiField _reimbursement_attr;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] reporting_for_sre; // [10] <open=suppress, name="reporting_for_sre">;
	public char[] rollup_to_sre; // [10] <open=suppress, name="rollup_to_sre">;
	public char[] funds_transfer_sre; // [10] <open=suppress, name="funds_transfer_sre">;
	public char[] settlement_service; // [3] <open=suppress, name="settlement_service">;
	public char[] settlement_currency; // [3] <open=suppress, name="settlement_currency">;
	public char[] clearing_currency; // [3] <open=suppress, name="clearing_currency">;
	public char[] business_mode; // [1] <open=suppress, name="business_mode">;
	public char[] no_data_ind; // [1] <open=suppress, name="no_data_ind">;
	public char[] reserved_1; // [1] <open=suppress, name="reserved_1">;
	public char[] report_group; // [1] <open=suppress, name="report_group">;
	public char[] report_subgroup; // [1] <open=suppress, name="report_subgroup">;
	public char[] report_id_num; // [3] <open=suppress, name="report_id_num">;
	public char[] report_id_suffix; // [2] <open=suppress, name="report_id_suffix">;
	public char[] settlement_date; // [7] <open=suppress, name="settlement_date">;
	public char[] report_date; // [7] <open=suppress, name="report_date">;
	public char[] from_date; // [7] <open=suppress, name="from_date">;
	public char[] to_date; // [7] <open=suppress, name="to_date">;
	public char[] charge_type; // [3] <open=suppress, name="charge_type">;
	public char[] business_trans_type; // [3] <open=suppress, name="business_trans_type">;
	public char[] business_trans_cycle; // [1] <open=suppress, name="business_trans_cycle">;
	public char[] reversal_ind; // [1] <open=suppress, name="reversal_ind">;
	public char[] return_ind; // [1] <open=suppress, name="return_ind">;
	public char[] jurisdiction; // [2] <open=suppress, name="jurisdiction">;
	public char[] ir_routing_ind; // [1] <open=suppress, name="ir_routing_ind">;
	public char[] source_country; // [3] <open=suppress, name="source_country">;
	public char[] destination_country; // [3] <open=suppress, name="destination_country">;
	public char[] source_region; // [2] <open=suppress, name="source_region">;
	public char[] destination_region; // [2] <open=suppress, name="destination_region">;
	public char[] fee_level_desc; // [16] <open=suppress, name="fee_level_desc">;
	public char[] cr_db_net_ind; // [1] <open=suppress, name="cr_db_net_ind">;
	public char[] summary_level; // [2] <open=suppress, name="summary_level">;
	public char[] reserved_2; // [33] <open=suppress, name="reserved_2">;
	public char[] reimbursement_attr; // [1] <open=suppress, name="reimbursement_attr">;

	public visa_b2_46_0(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_46_0(String content) {
		super(content);
	}

	public visa_b2_46_0() {
		super();
	}

	public visa_b2_46_0(int ifReturn) {
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
		addElement("reporting_for_sre", 10);
		addElement("rollup_to_sre", 10);
		addElement("funds_transfer_sre", 10);
		addElement("settlement_service", 3);
		addElement("settlement_currency", 3);
		addElement("clearing_currency", 3);
		addElement("business_mode", 1);
		addElement("no_data_ind", 1);
		addElement("reserved_1", 1);
		addElement("report_group", 1);
		addElement("report_subgroup", 1);
		addElement("report_id_num", 3);
		addElement("report_id_suffix", 2);
		addElement("settlement_date", 7);
		addElement("report_date", 7);
		addElement("from_date", 7);
		addElement("to_date", 7);
		addElement("charge_type", 3);
		addElement("business_trans_type", 3);
		addElement("business_trans_cycle", 1);
		addElement("reversal_ind", 1);
		addElement("return_ind", 1);
		addElement("jurisdiction", 2);
		addElement("ir_routing_ind", 1);
		addElement("source_country", 3);
		addElement("destination_country", 3);
		addElement("source_region", 2);
		addElement("destination_region", 2);
		addElement("fee_level_desc", 16);
		addElement("cr_db_net_ind", 1);
		addElement("summary_level", 2);
		addElement("reserved_2", 33);
		addElement("reimbursement_attr", 1);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		reporting_for_sre = getElement("reporting_for_sre");
		rollup_to_sre = getElement("rollup_to_sre");
		funds_transfer_sre = getElement("funds_transfer_sre");
		settlement_service = getElement("settlement_service");
		settlement_currency = getElement("settlement_currency");
		clearing_currency = getElement("clearing_currency");
		business_mode = getElement("business_mode");
		no_data_ind = getElement("no_data_ind");
		reserved_1 = getElement("reserved_1");
		report_group = getElement("report_group");
		report_subgroup = getElement("report_subgroup");
		report_id_num = getElement("report_id_num");
		report_id_suffix = getElement("report_id_suffix");
		settlement_date = getElement("settlement_date");
		report_date = getElement("report_date");
		from_date = getElement("from_date");
		to_date = getElement("to_date");
		charge_type = getElement("charge_type");
		business_trans_type = getElement("business_trans_type");
		business_trans_cycle = getElement("business_trans_cycle");
		reversal_ind = getElement("reversal_ind");
		return_ind = getElement("return_ind");
		jurisdiction = getElement("jurisdiction");
		ir_routing_ind = getElement("ir_routing_ind");
		source_country = getElement("source_country");
		destination_country = getElement("destination_country");
		source_region = getElement("source_region");
		destination_region = getElement("destination_region");
		fee_level_desc = getElement("fee_level_desc");
		cr_db_net_ind = getElement("cr_db_net_ind");
		summary_level = getElement("summary_level");
		reserved_2 = getElement("reserved_2");
		reimbursement_attr = getElement("reimbursement_attr");

	}

	@Override
	public String getDescription() {
		return "visa_b2_46_0_ST";
	}
}