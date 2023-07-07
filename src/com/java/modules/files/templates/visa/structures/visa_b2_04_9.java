package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class visa_b2_04_9 extends AsciiMessage {
	public AsciiField _trans_code;
	public AsciiField _trans_code_qualif;
	public AsciiField _trans_comp_seq;
	public AsciiField _destination_bin;
	public AsciiField _source_bin;
	public AsciiField _orig_trans_code;
	public AsciiField _orig_trans_code_qualif;
	public AsciiField _orig_trans_comp_seq;
	public AsciiField _source_batch_date;
	public AsciiField _source_batch_num;
	public AsciiField _item_sequence_num;
	public AsciiField _product_reclass_reason;
	public AsciiField _settlement_product_ID;
	public AsciiField _Settlm_spend_qualif_ind;
	public AsciiField _reserved_1;
	public AsciiField _settled_ifi;
	public AsciiField _settled_aci;
	public AsciiField _settled_rps;
	public AsciiField _settled_ra;
	public AsciiField _submitted_irf_desc;
	public AsciiField _settled_irf_desc;
	public AsciiField _ps_reclass_reason;
	public AsciiField _fee_reclass_reason;
	public AsciiField _mv_reclass_reason;
	public AsciiField _submitted_fee_prog_ind;
	public AsciiField _assessed_fee_prog_ind;
	public AsciiField _fee_prog_reclass_reason;
	public AsciiField _moto_eci_reclass_reason;
	public AsciiField _interchange_fee_amount;
	public AsciiField _interchange_fee_sign;
	public AsciiField _reserved_2;
	public char[] trans_code; // [2] <open=suppress, name="trans_code">;
	public char[] trans_code_qualif; // [1] <open=suppress, name="trans_code_qualif">;
	public char[] trans_comp_seq; // [1] <open=suppress, name="trans_comp_seq">;
	public char[] destination_bin; // [6] <open=suppress, name="destination_bin">;
	public char[] source_bin; // [6] <open=suppress, name="source_bin">;
	public char[] orig_trans_code; // [2] <open=suppress, name="orig_trans_code">;
	public char[] orig_trans_code_qualif; // [1] <open=suppress, name="orig_trans_code_qualif">;
	public char[] orig_trans_comp_seq; // [1] <open=suppress, name="orig_trans_comp_seq">;
	public char[] source_batch_date; // [5] <open=suppress, name="source_batch_date">;
	public char[] source_batch_num; // [6] <open=suppress, name="source_batch_num">;
	public char[] item_sequence_num; // [4] <open=suppress, name="item_sequence_num">;
	public char[] product_reclass_reason; // [3] <open=suppress, name="product_reclass_reason">; //visa 13.2 - New Field
	public char[] settlement_product_ID; // [2] <open=suppress, name="settlement_product_ID">; //visa 13.2 - New Field
	public char[] Settlm_spend_qualif_ind; // [1] <open=suppress, name="Settlm_spend_qualif_ind">; //visa 13.2 - New
											// Field
	public char[] reserved_1; // [26] <open=suppress, name="reserved_1">;
	public char[] settled_ifi; // [1] <open=suppress, name="settled_ifi">;
	public char[] settled_aci; // [1] <open=suppress, name="settled_aci">;
	public char[] settled_rps; // [1] <open=suppress, name="settled_rps">;
	public char[] settled_ra; // [1] <open=suppress, name="settled_ra">;
	public char[] submitted_irf_desc; // [16] <open=suppress, name="submitted_irf_desc">;
	public char[] settled_irf_desc; // [16] <open=suppress, name="settled_irf_desc">;
	public char[] ps_reclass_reason; // [3] <open=suppress, name="ps_reclass_reason">;
	public char[] fee_reclass_reason; // [3] <open=suppress, name="fee_reclass_reason">;
	public char[] mv_reclass_reason; // [3] <open=suppress, name="mv_reclass_reason">;
	public char[] submitted_fee_prog_ind; // [3] <open=suppress, name="submitted_fee_prog_ind">;
	public char[] assessed_fee_prog_ind; // [3] <open=suppress, name="assessed_fee_prog_ind">;
	public char[] fee_prog_reclass_reason; // [3] <open=suppress, name="fee_prog_reclass_reason">;
	public char[] moto_eci_reclass_reason; // [3] <open=suppress, name="moto_eci_reclass_reason">;
	public char[] interchange_fee_amount; // [15] <open=suppress, name="interchange_fee_amount">;
	public char[] interchange_fee_sign; // [1] <open=suppress, name="interchange_fee_sign">;
	public char[] reserved_2; // [28] <open=suppress, name="reserved_2">;

	public visa_b2_04_9(int offset, String content) {
		super(offset, content);
	}

	public visa_b2_04_9(String content) {
		super(content);
	}

	public visa_b2_04_9() {
		super();
	}

	public visa_b2_04_9(int ifReturn) {
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
		addElement("orig_trans_code", 2);
		addElement("orig_trans_code_qualif", 1);
		addElement("orig_trans_comp_seq", 1);
		addElement("source_batch_date", 5);
		addElement("source_batch_num", 6);
		addElement("item_sequence_num", 4);
		//// BYTE reserved_1 [32] <open=suppress, name="reserved_1">;;
		addElement("product_reclass_reason", 3);
		addElement("settlement_product_ID", 2);
		addElement("Settlm_spend_qualif_ind", 1);
		addElement("reserved_1", 26);
		addElement("settled_ifi", 1);
		addElement("settled_aci", 1);
		addElement("settled_rps", 1);
		addElement("settled_ra", 1);
		addElement("submitted_irf_desc", 16);
		addElement("settled_irf_desc", 16);
		addElement("ps_reclass_reason", 3);
		addElement("fee_reclass_reason", 3);
		addElement("mv_reclass_reason", 3);
		addElement("submitted_fee_prog_ind", 3);
		addElement("assessed_fee_prog_ind", 3);
		addElement("fee_prog_reclass_reason", 3);
		addElement("moto_eci_reclass_reason", 3);
		addElement("interchange_fee_amount", 15);
		addElement("interchange_fee_sign", 1);
		addElement("reserved_2", 28);

	}

	public void setDataString(String content) {
		super.setDataString(content);
		trans_code = getElement("trans_code");
		trans_code_qualif = getElement("trans_code_qualif");
		trans_comp_seq = getElement("trans_comp_seq");
		destination_bin = getElement("destination_bin");
		source_bin = getElement("source_bin");
		orig_trans_code = getElement("orig_trans_code");
		orig_trans_code_qualif = getElement("orig_trans_code_qualif");
		orig_trans_comp_seq = getElement("orig_trans_comp_seq");
		source_batch_date = getElement("source_batch_date");
		source_batch_num = getElement("source_batch_num");
		item_sequence_num = getElement("item_sequence_num");
		product_reclass_reason = getElement("product_reclass_reason");
		settlement_product_ID = getElement("settlement_product_ID");
		Settlm_spend_qualif_ind = getElement("Settlm_spend_qualif_ind");
		reserved_1 = getElement("reserved_1");
		settled_ifi = getElement("settled_ifi");
		settled_aci = getElement("settled_aci");
		settled_rps = getElement("settled_rps");
		settled_ra = getElement("settled_ra");
		submitted_irf_desc = getElement("submitted_irf_desc");
		settled_irf_desc = getElement("settled_irf_desc");
		ps_reclass_reason = getElement("ps_reclass_reason");
		fee_reclass_reason = getElement("fee_reclass_reason");
		mv_reclass_reason = getElement("mv_reclass_reason");
		submitted_fee_prog_ind = getElement("submitted_fee_prog_ind");
		assessed_fee_prog_ind = getElement("assessed_fee_prog_ind");
		fee_prog_reclass_reason = getElement("fee_prog_reclass_reason");
		moto_eci_reclass_reason = getElement("moto_eci_reclass_reason");
		interchange_fee_amount = getElement("interchange_fee_amount");
		interchange_fee_sign = getElement("interchange_fee_sign");
		reserved_2 = getElement("reserved_2");

	}

	@Override
	public String getDescription() {
		return "visa_b2_04_9_ST";
	}
}