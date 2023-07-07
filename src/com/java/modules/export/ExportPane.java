package com.java.modules.export;

import com.java.core.components.MyTree;
import com.java.core.components.treeview.DataNode;
import com.java.core.components.treeview.LabelNode;
import com.java.core.settings.Settings;
import com.java.core.MainScreen;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;

import javax.swing.text.JTextComponent;

import org.w3c.dom.Document;

import com.java.core.components.MyDialog;
import com.java.core.components.MyFileBrowser;
import com.java.core.data.DBLookup;
import com.java.core.data.DBParam;
import com.java.core.data.DBQuery;
import com.java.modules.dataviewer.DataViewerService;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.awt.GridLayout;

//hold local params (data) for each tab.
public class ExportPane extends MyPane {
	public String ServiceTitle = ExportService.ServiceTitle;
	public boolean isHideLog = false;
	public boolean isShowFlat = false;

	public String connectionNameDest, url, service_name, connMode, user, instNr, destInstNr, destLocalCurr, replaceFlag,
			applyResets, exportAllCht, includeApplications, includeAllClients, destinationURL, destPort, destConnMode,
			destinationService_name,
			destinationUser, destinationPassword, moduleDefFile, recDateIsSysDate, savedFolder;

	// public JPanel container = Utils.createJPanel();

	public List<String> storedFileLocators;

	public ExportPane(Component view) {
		super(view);
	}

	public ExportPane() {
		super();
	}

	public ExportPane(Map<String, String> params) {
		super();
		assignLocalVars(params);
		initData();
	}

	public void initMenuPanel() {
		getMenuJpanel().add(btnOk);
		// getMenuJpanel().add(btnAdd);
		// getMenuJpanel().add(btnReset);
		// getMenuJpanel().add(buttonShowFlat);
		getMenuJpanel().add(btnCollapse);

		getMenuJpanel().add(btnExport);
		// getMenuJpanel().add(btnClearLog);
		getMenuJpanel().add(btnEdit);
		getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnClose);
	}

	public void initUI(Component view) {
		super.initUI(view);
		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(!isHideLog);
		getParamsPanel().setVisible(false);
		if (view != null) {
			component = view;
			if (view instanceof MyTree)
				theGUITree = (MyTree) view;
		}
	}

	public void refreshUIMenu() {
		btnAdd.setVisible(viewOption == Settings.viewDetail);
		btnEdit.setText(viewOption == Settings.viewDetail ? "Edit" : "Save");
		btnEdit.setVisible(true);
		btnCancel.setVisible(viewOption == Settings.viewContent);
		btnReset.setVisible(viewOption == Settings.viewDetail);
		btnCollapse.setVisible(viewOption == Settings.viewDetail);
		btnOk.setVisible(viewOption == Settings.viewDetail);
		btnClearLog.setVisible(!isHideLog);
		btnClose.setVisible(true);
	}

	public void setQueryParams(List<DBParam> params) {
		queryParams = params;
		refreshUI();
	}

	public void refreshUIParams() {
		refreshUIParams(null);
	}

	public void refreshUIParams(String message) {
		getParamsPanel().removeAll();
		// getParamsPanel().setVisible(true);
		initComboFiles();
		initComboConns();

		if (queryParams != null && queryParams.size() > 0) {
			for (DBParam param : queryParams) {
				addParam(param.getKey(), param.getValue());
			}
		}

		this.validate();
	}

	public void initData() {
		if (getParamsPanel().isVisible() && comboBoxFileDef != null
				&& !((String) comboBoxFileDef.getSelectedItem()).isEmpty())
			theParameters.put("moduleDefFile", getFileNameFull((String) comboBoxFileDef.getSelectedItem()));
	}

	public String getResultFileName() {
		SimpleDateFormat normalTimeFormat = new SimpleDateFormat("_HHmmss");

		String exportScriptFileName = new File(file).getName().replaceAll(".xml", "") + "_from_" + service_name
				+ "_Inst_" + instNr + normalTimeFormat.format(new Date()) + ".sql";
		String folder = (savedFolder != null && !savedFolder.isEmpty()) ? savedFolder : new File("").getAbsolutePath();
		exportScriptFileName = folder + "\\" + exportScriptFileName;

		return exportScriptFileName;
	}

	public MyPane generateDataPane(boolean refreshFlag, String execImmediateExpression, boolean overrideOldTree) {
		// logger.setTextArea(logTextArea);
		try {
			if (refreshFlag) {
				initData();
				refreshUIParams("Generate tree");
				String savedFolder = ExportService.getSaveFolder(theParameters);

				listAllQueryDefinitions = ExportService.executeExport(theParameters, savedFolder, this);
				MyFileBrowser fileBrowser = new MyFileBrowser(savedFolder);
				fileBrowser.autoCopyToClipboard = true;
				setComponent(getBottomJPanel(), fileBrowser);

				setBottomTitle("Saved Folder: " + savedFolder);
			}

			generateDataPane(listAllQueryDefinitions, overrideOldTree);
			MyDialog.showMessage("Execution completed.");

		} catch (Exception e) {
			return generateErrorPane(e, "Error when generate temp tables or retreiving data or generate tree");
		}
		return this;
	}

	public String getTreeRootLabel() {
		return getId();
	}

	public DataNode generateRootDataNode() {
		DataNode root = DataViewerService.generateDataNode(listAllQueryDefinitions, getTreeRootLabel(), viewOption,
				true, isShowFlat);
		// return root;
		LabelNode rootSrc = new LabelNode(connectionName + "." + instNr);
		LabelNode rootDest = new LabelNode(connectionNameDest + "." + destInstNr);
		LabelNode anfang = new LabelNode(root.getValues());

		for (DataNode child : root.getChildren()) {
			if (child.getValues().startsWith(connectionName + "." + instNr)) {
				child.setValues(Utils.substringBetween(child.getValues(), ":", "").trim());
				rootSrc.insertDeep(child);
			} else if (!connectionNameDest.isEmpty()
					&& child.getValues().startsWith(connectionNameDest + "." + destInstNr)) {
				child.setValues(Utils.substringBetween(child.getValues(), ":", "").trim());
				rootDest.insertDeep(child);
			} else if (!child.getValues().isEmpty()) {
				anfang.insertDeep(child);
			}
		}

		anfang.insertDeep(rootSrc);
		if (!connectionNameDest.isEmpty())
			anfang.insertDeep(rootDest);

		return anfang;
	}

	private String getParamValue(String paramKey) {
		String value = theParameters.get(paramKey);
		return value;
	}

	private String getParamValue(String paramKey, String defaultValue) {
		String value = theParameters.get(paramKey);
		if (value == null || value.isEmpty()) {
			if (defaultValue != null && !defaultValue.isEmpty()) {
				theParameters.put(paramKey, defaultValue);
				return defaultValue;
			}
		}
		return value;
	}

	public ExportPane assignLocalVars(Map<String, String> theParameters) {
		this.theParameters = theParameters;

		Map<String, String> settings = MyService.getConnectionSettings(Settings.getLastConnectionName());
		if (settings == null)
			settings = new LinkedHashMap<String, String>();

		connectionName = this.getParamValue("connection", Settings.getLastConnectionName());
		file = this.getParamValue("moduleDefFile");
		file = MyService.getFileNameFull(file);

		url = getParamValue("url", settings.get(Settings.TagHOST));
		service_name = getParamValue("service_name", settings.get(Settings.TagSERVICENAME));
		port = getParamValue("port", settings.get(Settings.TagPORT));
		connMode = getParamValue("connMode", settings.get(Settings.TagCONNECTIONTYPE));
		user = getParamValue("user", settings.get(Settings.TagUSERNAME));
		password = getParamValue("password", settings.get(Settings.TagPASSWORD));
		instNr = getParamValue("instNr", Settings.getLastInstitutionNumber());

		destinationURL = getParamValue("destinationURL", url);
		connectionNameDest = getParamValue("destConnection", connectionName);

		destPort = getParamValue("destPort", port);
		destConnMode = getParamValue("destConnMode", connMode);
		destInstNr = getParamValue("destInstNr", instNr);
		destLocalCurr = getParamValue("destLocalCurr");

		destinationService_name = getParamValue("destinationService_name", service_name);
		destinationUser = getParamValue("destinationUser", user);
		destinationPassword = getParamValue("destinationPassword", password);

		moduleDefFile = getParamValue("moduleDefFile");
		recDateIsSysDate = getParamValue("recDateIsSysDate");
		savedFolder = getParamValue("savedFolder",
				Settings.getOutputFolder());
		replaceFlag = getParamValue("replaceFlag", "1");
		applyResets = getParamValue("applyResets", "0");
		exportAllCht = getParamValue("exportAllCht", "1");
		includeApplications = getParamValue("includeApplications", "1");
		includeAllClients = getParamValue("includeAllClients", "1");

		return this;
	}

	public String getId() {
		if (panelId != null && !panelId.isEmpty())
			return panelId;
		if (connectionName == null || connectionName.isEmpty()) {
			connectionName = Settings.getLastConnectionName();
		}

		if (instNr == null || instNr.isEmpty()) {
			instNr = Settings.getLastInstitutionNumber();
		}

		if (destInstNr == null || destInstNr.isEmpty()) {
			destInstNr = instNr;
		}

		return (new File(file)).getName().toLowerCase() + ":" + connectionName.toLowerCase() + "." + instNr + "-"
				+ connectionNameDest.toLowerCase() + "." + destInstNr;
	}

}