package com.java.modules.files.components;

import com.java.core.components.property.PropertiesEditorConfig;
import com.java.core.components.property.PropertiesTreeTable;
import com.java.core.components.property.PropertiesTreeTableModel;

//hold local params (data) for each tab.
public class FileTreeTable extends PropertiesTreeTable {

	public FileTreeTable(PropertiesEditorConfig config,
			PropertiesTreeTableModel treeModel) {
		super(config, treeModel);
	}

	public boolean isReadOnlyCell(int column, int row) {
		String value = (String) getModel().getValueAt(row, getHierarchicalColumn());
		String childValue = (String) getModel().getValueAt(row + 1, getHierarchicalColumn());
		if (childValue != null && childValue.startsWith("  ") && !value.startsWith(" "))
			return true;
		if (value.toUpperCase().startsWith("BITMAP") || value.startsWith(FileEditor.ReadOnlyColumnPrefix))
			return true;

		return super.isReadOnlyCell(column, row);
	}

}