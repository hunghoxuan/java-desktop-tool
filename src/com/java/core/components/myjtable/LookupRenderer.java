package com.java.core.components.myjtable;

import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.java.Main;
import com.java.core.base.MyCache;
import com.java.core.base.MyService;
import com.java.core.settings.Settings;

class LookupRenderer extends DefaultTableCellRenderer {
	String column;

	LookupRenderer(String column) {
		super();
		this.column = column;
	}

	public void setValue(Object value) {
		List<String> lookupWords = MyService.getCachedDataAsList(Main.connectionName, column, null);
		String value1 = (String) value;
		if (lookupWords != null) {
			for (String lookupWord : lookupWords) {
				if (lookupWord.startsWith(value1 + " " + Settings.lookupKeyValueSeperator + " ")) {
					if (lookupWord.contains(Settings.dataSeperator))
						lookupWord = lookupWord.split(Settings.dataSeperator)[0];
					setText(lookupWord);
					return;
				}
			}
		}
		setText(value1);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		c.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return c;
	}
}