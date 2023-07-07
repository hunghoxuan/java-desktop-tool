package com.java.core.components.myjtable;

import java.text.DateFormat;
import javax.swing.table.DefaultTableCellRenderer;

class DateRenderer extends DefaultTableCellRenderer {
	DateFormat formatter;

	public DateRenderer() {
		super();
	}

	public void setValue(Object value) {
		if (formatter == null) {
			formatter = DateFormat.getDateInstance();
		}
		setText((value == null) ? "" : formatter.format(value));
	}
}
