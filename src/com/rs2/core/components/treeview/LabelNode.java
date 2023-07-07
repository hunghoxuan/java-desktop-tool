package com.rs2.core.components.treeview;

import com.rs2.core.data.DBQuery;

public class LabelNode extends DataNode {

	public LabelNode(String cont) {
		super(cont);

		isLabel = true;
		dBQuery = null;
	}

	public LabelNode(String cont, DBQuery query) {
		this(cont);

		isLabel = true;
		dBQuery = query;
	}

	LabelNode() {
		super();
		isLabel = true;
		dBQuery = null;
	}

	LabelNode(String cont, DataNode parent) {
		super(cont, parent);
		isLabel = true;
		dBQuery = null;
	}

	public LabelNode(String columns, String cont, DataNode parent) {
		super(columns, cont, parent);
		isLabel = true;
		dBQuery = null;
	}
}