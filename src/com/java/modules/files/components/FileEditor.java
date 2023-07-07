package com.java.modules.files.components;

import com.java.core.components.property.PropertiesEditor;
import com.java.core.components.property.PropertiesEditorConfig;
import com.java.core.components.property.PropertiesTreeTable;
import com.java.core.components.property.PropertiesTreeTableModel;

//hold local params (data) for each tab.
public class FileEditor extends PropertiesEditor {
	public static String ReadOnlyColumnPrefix = "*";

	public FileEditor(PropertiesTreeTableModel treeTableModel) {
		super(treeTableModel);
		readOnlyColumns().add(0); // first column (field name) is readonly
	}

	public FileEditor(Object data) {
		super(new PropertiesTreeTableModel(data));
		readOnlyColumns().add(0); // first column (field name) is readonly
	}

	public PropertiesTreeTable initPropertiesTreeTable(PropertiesEditorConfig config,
			PropertiesTreeTableModel treeModel) {
		return new FileTreeTable(config, treeModel);
	}
}