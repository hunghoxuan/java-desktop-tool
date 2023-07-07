package com.java.modules.files.components;

import com.java.core.components.MyFileBrowser;
import com.java.core.components.MyInputDialog;
import com.java.core.components.MyTextAreaPane;
import com.java.core.components.property.PropertiesEditor;
import com.java.core.components.property.PropertiesTreeTableModel;
import com.java.core.settings.Settings;
import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyFile;
import com.java.core.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

//hold local params (data) for each tab.
public class FilesBrowser extends MyFileBrowser {
	MyFile myFile = null;
	PropertiesEditor propEditor;

	public FilesBrowser(File directory) {
		super(directory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getFileDetails(File file) {
		if (file == null || file.isDirectory() || !file.exists())
			return "";

		String content = Utils.getContentFromFile(file);
		// String fileName = file.getAbsolutePath();
		// myFile = FilesService.loadFile(fileName, content);
		// if (myFile != null)
		// content = myFile.asText();
		myFile = null;

		return content;
	}

	public Object getData() {
		if (currentComponent != null && currentComponent instanceof PropertiesEditor)
			return ((PropertiesEditor) currentComponent).getTreeTableModel().getData();

		return txtContent.getText();
	}

	@Override
	public void refreshEditor(File file) {
		originalContent = getFileDetails(file);

		txtContent.setText(originalContent);
		txtContent.setCaretPosition(0);
		setComponent(txtContent);

		// setComponent(txtContent);
		if (!originalContent.isEmpty())
			Utils.copyToClipboard(originalContent, fileTree);

		setEditable(false);
		if (myPane != null)
			myPane.cancel();
	}

	public void run(String[] args) {
		String[] actions = myFile != null ? myFile.isoFileReader.getActionsList()
				: FilesService.getFileDefaultActions();

		String action = MyInputDialog.instance().showComboBox("Please select action", "Please select action",
				actions,
				actions[0]);

		if (myFile != null && action != null) {
			myFile.isoFileReader = myFile.isoFileReader.createTemplate(action, this.getCurrentFile().getAbsolutePath());
			myFile.isoFileReader.run(args);
			refreshTree();
		} else if (action != null) {
			myFile = FilesService.loadFile(this.getCurrentFile().getAbsolutePath(), action);
		}

		if (myFile != null)
			setText(myFile.asText());
	}

	public String showFilenameDialog(File file) {
		String folder = file.getParent();
		String fileName = file.getName();
		String saveFile = file.getAbsolutePath();

		fileName = MyInputDialog.instance().showTextBox("Do you want save this file ?", fileName);
		if (fileName == null || fileName.isEmpty()) // cancel
			return null;
		return folder + "\\" + fileName;
	}

	public void save() {
		if (currentComponent != null && currentComponent instanceof MyTextAreaPane) {
			super.save();
			return;
		} else {
			Object data = getData();
			String saveFile = showFilenameDialog(getCurrentFile());
			if (saveFile != null) {
				myFile.loadPropertiesObject((HashMap) data);
				myFile.save(saveFile);

				if (!saveFile.equalsIgnoreCase(getCurrentFile().getAbsolutePath()))
					refreshTree();
			}

			if (myFile != null) {
				setContent(myFile.asText());
			} else {
				originalContent = getFileDetails(getCurrentFile());
				setContent(originalContent);
			}
			setEditable(false);
			setComponent(txtContent);
			fileTree.setEnabled(true);
		}
	}

	public void cancel() {
		if (currentComponent != null && currentComponent instanceof MyTextAreaPane) {
			super.cancel();
			return;
		} else {
			if (myFile != null)
				setContent(myFile.asText());
			else
				setContent(originalContent);
			setEditable(false);
			setComponent(txtContent);
			fileTree.setEnabled(true);
		}
	}

	public void edit() {
		if (myFile != null) {
			Map<String, Object> root = myFile.asPropertiesObject();
			PropertiesTreeTableModel treeTableModel = new PropertiesTreeTableModel(root);
			propEditor = new FileEditor(treeTableModel);

			setComponent(propEditor);
			fileTree.setEnabled(false);
			return;
		}
		super.edit();
	}

	@Override
	public void changeRoot(File file) {
		super.changeRoot(file);
		Settings.storeIniSettings(FilesService.settingRootFolder, rootFolder.getAbsolutePath());
	}

}