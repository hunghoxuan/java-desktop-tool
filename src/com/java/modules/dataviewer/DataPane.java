package com.java.modules.dataviewer;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import com.java.core.components.MyTree;
import com.java.core.components.treeview.DataNode;
import com.java.core.components.treeview.DataNodePopupMenu;
import com.java.core.settings.Settings;
import com.java.core.MainScreen;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.text.JTextComponent;

import org.w3c.dom.Document;

import com.java.Main;
import com.java.core.components.MyDialog;
import com.java.core.data.DBParam;
import com.java.core.data.DBQuery;
import com.java.core.data.TransmittedCondition;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Component;

import java.util.LinkedList;
import java.util.List;

import java.awt.event.*;

//hold local params (data) for each tab.
public class DataPane extends MyPane {
	public String ServiceTitle = DataViewerService.ServiceTitle;

	String fileType = "";

	public DataPane(Component view) {
		super(view);
	}

	public DataPane() {
		super();
	}

	public DataPane(String dbType1, String connectionName1, String host1, String port1, String connType1,
			String serviceName1, String userName1, String password1, String file1, String execImmediateExpression) {
		super();
		assignLocalVars(dbType1, connectionName1, host1, port1, connType1, serviceName1, userName1, password1, file1,
				execImmediateExpression);
		initData();
	}

	public void initMenuPanel() {
		getMenuJpanel().add(btnOk);
		getMenuJpanel().add(btnAdd);
		getMenuJpanel().add(buttonShowFlat);
		getMenuJpanel().add(btnCollapse);
		getMenuJpanel().add(btnExport);
		getMenuJpanel().add(btnEdit);
		getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnClose);
	}

	public void initUI(Component view) {
		super.initUI(view);
		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(!isHideLog);
		getParamsPanel().setVisible(true);
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
		buttonShowFlat.setVisible(viewOption == Settings.viewDetail);
		buttonShowFlat.setText(isShowFlat ? "+ Hierachy" : "- Flat");
	}

	public void setQueryParams(List<DBParam> params) {
		queryParams = params;
		refreshUI();
	}

	@Override
	public void updateParams() {
		queryParams = Utils.getDynamicListMapsFromComponent(getParamsPanel().getComponents());
		if (fileType.equalsIgnoreCase("sql")) {
			queryParams = getQueryParamsFromSQLs(Utils.getContentFromFile(file), queryParams);
			return;
		}
		tabComponentIndex = MainScreen.findComponentIndex(this.tabbedPane, this.getId());
		if (comboBoxFileDef != null) {
			String selectedfile = getFileNameFull((String) comboBoxFileDef.getSelectedItem());
			xmlDocument = null; // reload xmlDocument;
			listAllQueryDefinitions = null;

			if (selectedfile != null && !selectedfile.isEmpty() && !selectedfile.equalsIgnoreCase(file)) {
				// queryParams = null;
				// lookupColumns = null;
				file = selectedfile;
			}
		}

		if (comboBoxStoredConns != null) {
			String selectedConString = (String) comboBoxStoredConns.getSelectedItem();
			if (selectedConString != null && !selectedConString.isEmpty()
					&& !selectedConString.equalsIgnoreCase(connectionName)) {
				conn = null;
				connectionName = selectedConString;
				listAllQueryDefinitions = null;
			}
		}
	}

	public void resetParams() {

		if (this.originalFile != null && !this.originalConnectionName.isEmpty()) {
			if (comboBoxFileDef != null && comboBoxStoredConns != null) {
				comboBoxFileDef.setSelectedItem(getFileName(this.originalFile));
				comboBoxStoredConns.setSelectedItem(this.originalConnectionName);
			}
			file = this.originalFile;
			connectionName = this.originalConnectionName;

			queryParams = null;
			xmlDocument = null;
			lookupColumns = null;
			listAllQueryDefinitions = null;
		}
	}

	public void refreshUIParams() {
		refreshUIParams(null);
	}

	public void refreshUIParams(String message) {
		getParamsPanel().removeAll();
		getParamsPanel().setVisible(true);
		initComboFiles();
		initComboConns();

		if (queryParams != null && queryParams.size() > 0) {
			for (DBParam param : queryParams) {
				addParam(param.getKey(), param.getValue());
			}
		}

		this.validate();
	}

	public void refreshConnectionCombo() {
		if (((String) comboBoxStoredConns.getSelectedItem()).isEmpty() ||
				connectionName.equals(
						(String) comboBoxStoredConns.getSelectedItem()))
			return;
		connectionName = (String) comboBoxStoredConns.getSelectedItem();
		Document xmlDocument = DataViewerService
				.getXmlDocument((String) comboBoxFileDef.getSelectedItem());

		if (Settings.forceClearCachedBeforeEachExecution)
			DataViewerService.clearCachedData(connectionName);

		DataViewerService.generateDBQuery(xmlDocument,
				connectionName, null, null,
				true);
		queryParams = Utils.getDynamicListMapsFromComponent(getParamsPanel().getComponents());

		DataViewerService.setupCachedData(null, lookupColumns, connectionName, queryParams);
		for (Component comp : getParamsPanel().getComponents()) {
			if (comp instanceof JTextComponent) {
				List<String> lookupWords = DataViewerService.getCachedDataAsList(
						connectionName,
						DBService.getDBFieldFromParamName(comp.getName()), queryParams);
				if (lookupWords != null) {
					Utils.setAutoComplete((JTextComponent) comp, lookupWords);
				}
			}
		}
		setTitle(getIdString(getFileName((String) comboBoxFileDef.getSelectedItem()),
				(String) comboBoxStoredConns.getSelectedItem()));
	}

	public MyPane generateErrorPane(Exception e, String message) {
		DBService.dropAllTempTables((LinkedList<DBQuery>) listAllQueryDefinitions); // always delete temp tables
		return super.generateErrorPane(e, message);
	}

	public void initData() {
		fileType = "";
		isQueryParamsMissing = "";
		if (file == null)
			return;
		if (connectionName == null)
			return;

		if (originalConnectionName == null || originalConnectionName.isEmpty())
			originalConnectionName = connectionName;

		if (originalFile == null || originalFile.isEmpty())
			originalFile = file;

		if (initConnection() == null)
			return;

		// if file is SQL files
		listAllQueryDefinitions = DataViewerService.generateDBQueryFromSQLFile(file, queryParams, lookupColumns, conn,
				connectionName, null, false, true); // if file is sqls
		if (listAllQueryDefinitions != null && listAllQueryDefinitions.size() > 0) {
			// getQueryParamsFromSQLs(Utils.getContentFromFile(file));
			fileType = "sql";
			return;
		}

		// Main.connection = conn;
		if (xmlDocument == null) {
			xmlDocument = DataViewerService.getXmlDocument(file);
			if (queryParams == null) {
				queryParams = MyService.initQueryParams();
			}
		}

		if (xmlDocument != null) {
			setQueryParams(DataViewerService.loadSQLQueryParams(xmlDocument, queryParams));
		}

		// if file is export file
		listAllQueryDefinitions = DataViewerService.generateDBQueryFromExportFile(file, queryParams); // if file is sqls
		if (listAllQueryDefinitions != null && listAllQueryDefinitions.size() > 0) {
			fileType = "export";
		} else {
			lookupColumns = null;

			if (Settings.forceClearCachedBeforeEachExecution)
				DataViewerService.clearCachedData(connectionName);

			if (xmlDocument != null)
				listAllQueryDefinitions = DataViewerService.generateDBQuery(xmlDocument, connectionName, queryParams,
						lookupColumns, false);

			if (lookupColumns == null && listAllQueryDefinitions.size() > 0) {
				lookupColumns = listAllQueryDefinitions.get(0).getLookupColumns();
			}

			if (lookupColumns == null || lookupColumns.size() == 0) {
				lookupColumns = getService().lookupColumns;
			}

			MyService.initQueryParams(queryParams);

			Thread thread = new Thread() {
				public void run() {
					DataViewerService.setupCachedData(listAllQueryDefinitions, lookupColumns, connectionName,
							queryParams);
				};
			};

			thread.start();

		}

		queryParams = DBService.inputQueryParams(queryParams);

		isQueryParamsMissing = DBService.getMissingQueryParam(queryParams);
	}

	public MyPane generateDataPane(boolean refreshFlag, String execImmediateExpression, boolean overrideOldTree) {
		Main.startThread("Loading Data");
		try {
			if (refreshFlag) {
				initData();
				refreshUIParams("Generate tree");

				if (!isQueryParamsMissing.isEmpty()) {
					return generateMessagePane("Param " + isQueryParamsMissing
							+ " is missing. Please input param and click [Start] to continue.");
				}

				conn = getConnection();
				if (conn == null) {
					return generateErrorPane(null, "Failed to connect to database");
				}

				if (listAllQueryDefinitions == null || listAllQueryDefinitions.isEmpty()) {
					return generateErrorPane(null,
							"Definition file does not have any query or validation ended with error!");
				}

				if (xmlDocument == null && listAllQueryDefinitions == null)
					return generateErrorPane(null, "Xml Document is invalid format. Could not read XML Document!");

				if (fileType.equalsIgnoreCase("sql") && (queryParams == null || queryParams.size() == 0)) {
					getQueryParamsFromSQLs(Utils.getContentFromFile(file));
				}

				dataSuccess = true;
				log("\n\n[Generating temp tables and retrieving data]");
				for (DBQuery queryDefinition : listAllQueryDefinitions) {
					if ((queryDefinition.getQueryParams() == null || queryDefinition.getQueryParams().size() == 0)
							&& queryParams != null)
						queryDefinition.setQueryParams(queryParams);

					queryDefinition.setConnectionName(connectionName); // required
					queryDefinition.setConnection(conn);
					if (!queryDefinition.hasSuperSQLQuery())
						dataSuccess = dataSuccess
								&& DataViewerService.generateAllTempTables(conn, queryDefinition, logger);
					if (!dataSuccess) {
						// throw new Exception(
						// "Generate Temp Table failed: " + queryDefinition.getConnectionName() +
						// ".\n");
						return generateErrorPane(null,
								"Generate Temp Table failed: " + queryDefinition.getConnectionName());
					}
				}

				// generate data (query Sql and insert into temp tables)
				log("\n[Retrieving data]");
				for (DBQuery queryDefinition : listAllQueryDefinitions) {
					if (this.isShowFlat || !queryDefinition.hasSuperSQLQuery())
						dataSuccess = dataSuccess && queryDefinition.generateData(true);

					if (!dataSuccess) {
						// throw new Exception(
						// "Generate Data failed: " + queryDefinition.getConnectionName() + ".\n");
						return generateErrorPane(null, "Generate Data failed: " + queryDefinition.getConnectionName());
					}
				}

				DBService.dropAllTempTables((LinkedList<DBQuery>) listAllQueryDefinitions);
			}

			setTitle(getIdString(getFileName((String) comboBoxFileDef.getSelectedItem()),
					(String) comboBoxStoredConns.getSelectedItem()));

			generateDataPane(listAllQueryDefinitions, overrideOldTree);

			MyDialog.showMessage("Execution completed.");
			// // refresh tab title

		} catch (Exception e) {
			MyDialog.showException(e, "Error when generate temp tables or retreiving data or generate tree");
		}
		Main.stopThread();
		return this;
	}

	public String getTreeRootLabel() {
		return getFileDescription(file, connectionName, queryParams);
	}

	public DataNode generateRootDataNode() {
		return DataViewerService.generateJointDisplayList(listAllQueryDefinitions, getTreeRootLabel(), viewOption, true,
				this.isShowFlat, this);
	}

	public void treeKeyReleased(MyTree tree, KeyEvent event) {
		if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_C) { // Ctrl+C ist gedr�ckt
			TreePath[] tp = tree.getSelectionPaths();
			String select = "";
			if (tp.length > 0)
				select = DataViewerService.generateSelectionDisplay(tree.getSelectionPaths(), "\t", false);
			Utils.copyToClipboard(select, tree);
		}
	}

	public void treeExpanded(MyTree tree, TreeExpansionEvent arg0) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		TreePath treePath = arg0.getPath();
		DefaultMutableTreeNode selectedTreeNode = ((DefaultMutableTreeNode) (treePath.getLastPathComponent()));
		DataNode firstChildNode = null, labelNode = null, lastDataNode;

		if (((DefaultMutableTreeNode) (selectedTreeNode.getFirstChild()))
				.getUserObject() instanceof DataNode) {
			firstChildNode = ((DataNode) (((DefaultMutableTreeNode) (selectedTreeNode.getFirstChild()))
					.getUserObject()));

			// auto reload data of current node ?
			if (firstChildNode.isQueryOnDemand()) {
				String selectedLabel = ((String) selectedTreeNode.getUserObject());
				labelNode = firstChildNode.getParent();
				lastDataNode = labelNode.getParent();
				DBQuery selectedQueryDefintion = null;
				for (DBQuery a : listAllQueryDefinitions) {
					if (a.getSQLQueryLabel().equals(selectedLabel)) {
						selectedQueryDefintion = a;
						break;
					}
				}
				if (selectedQueryDefintion != null) {

					selectedQueryDefintion.setDynamicSqlQuery(
							DataViewerService.generateSqlQueryFromTreeElement(selectedTreeNode,
									listAllQueryDefinitions));

					selectedQueryDefintion.transmittedConditions = DataViewerService
							.getApplicableConditionFromNodeParents(
									lastDataNode, selectedQueryDefintion.transmittedConditions, true);
					for (TransmittedCondition tc : selectedQueryDefintion.transmittedConditions) {
						if (tc.getValue().equals("'null'") || tc.getValue().equals("null"))
							tc.setValue(" is null ");
						else
							tc.setValue(" in ('" + tc.getValue() + "')");
					}

					try {
						boolean success = DataViewerService.generateAllTempTables(conn, selectedQueryDefintion, logger);
						if (!success) {
							DBService.dropAllTempTables((LinkedList<DBQuery>) listAllQueryDefinitions);
						}
					} catch (Exception e) {
						MyDialog.showException(e,
								"Unexpected error occured during the initial stage of the data retrieval (generate Temp tables)");
					}
					try {
						dataSuccess = true;
						dataSuccess = selectedQueryDefintion.generateData(false);

					} catch (Exception e) {
						MyDialog.showException(e,
								"An unexpected error occured while retrieving and caching the data");

					}
					selectedQueryDefintion.setDataRetrievalTriggeredByGUI(true);
					DefaultMutableTreeNode addOn = DataViewerService.generateJointDisplay(selectedQueryDefintion,
							"the dynamic add", viewOption, false, isShowFlat);
					selectedQueryDefintion.setDataRetrievalTriggeredByGUI(false);

					if (addOn != null) {
						model.removeNodeFromParent((MutableTreeNode) selectedTreeNode.getFirstChild());
						labelNode.setChildren(new LinkedList<DataNode>());
						for (int childNr = 0; childNr < addOn.getChildCount(); childNr++) {
							DataNode addOnChild = ((DataNode) ((DefaultMutableTreeNode) addOn
									.getChildAt(childNr)).getUserObject());

							labelNode.insertOnLevel(addOnChild);
							addOnChild.setParent(labelNode);
						}
						if (labelNode.getFirstChild() != null)
							labelNode.getFirstChild().setQueryOnDemand(Settings.queryOnDemand); //

						for (int childNr = 0; labelNode.getChildren().size() > childNr; childNr++) {
							DefaultMutableTreeNode newLine = Utils.getTreeNodeForDataNode(addOn,
									labelNode.getChildren().get(childNr));
							model.insertNodeInto(newLine, selectedTreeNode, childNr);
						}
					} else
						model.removeNodeFromParent((MutableTreeNode) selectedTreeNode.getFirstChild());
					tree.expandRow(tree.getRowForPath(arg0.getPath()));
				}
			}
		} else { // auto expand first child node !
			TreeNode n = (TreeNode) model.getChild(selectedTreeNode, 0);
			TreePath path = treePath.pathByAddingChild(n);
			tree.expandPath(path);
		}
	}

	public JPopupMenu createTreePopuPMenu(MyTree tree, TreePath selPath, List<DBQuery> listAllQueryDefinitions) {
		return (new DataNodePopupMenu(tree, selPath, listAllQueryDefinitions)).popupTreeChoice;
	}

	public boolean hasData() {

		boolean hasData = true;
		for (DBQuery ap : listAllQueryDefinitions) {
			if (ap.hasData() == false) {
				try {
					hasData = ap.generateData(true);
				} catch (Exception ex) {
					hasData = false;
				}
				if (!hasData)
					break;
			}
		}
		return hasData;
	}

	public void assignLocalVarsFromDataPane(DataPane currentPane) {
		if (currentPane.viewOption != null)
			viewOption = currentPane.viewOption;
		file = currentPane.file;
		connectionName = currentPane.connectionName;
		if (currentPane.listAllQueryDefinitions != null)
			listAllQueryDefinitions = currentPane.listAllQueryDefinitions;
		if (currentPane.theGUITree != null)
			theGUITree = currentPane.theGUITree;
		port = currentPane.port;
		password = currentPane.password;
		serviceName = currentPane.serviceName;
		host = currentPane.host;
		treeExpandMode = currentPane.treeExpandMode;
		tabPaneIndex = currentPane.tabPaneIndex;
		if (currentPane.tabbedPane != null)
			tabbedPane = currentPane.tabbedPane;
		queryParams = currentPane.queryParams;
		lookupColumns = currentPane.lookupColumns;
	}

	// store current environment params into DataPane
	public DataPane assignLocalVarsToDataPane(DataPane panel, JTabbedPane tabbPane) {
		((DataPane) panel).file = file;
		((DataPane) panel).connectionName = connectionName;
		((DataPane) panel).host = host;
		((DataPane) panel).serviceName = serviceName;
		((DataPane) panel).listAllQueryDefinitions = listAllQueryDefinitions;
		((DataPane) panel).port = port;
		((DataPane) panel).dbType = dbType;
		((DataPane) panel).userName = userName;
		((DataPane) panel).password = password;
		((DataPane) panel).tabPaneIndex = tabPaneIndex;
		((DataPane) panel).theGUITree = theGUITree;
		((DataPane) panel).viewOption = viewOption;
		((DataPane) panel).treeExpandMode = treeExpandMode;
		((DataPane) panel).tabbedPane = tabbPane;
		((DataPane) panel).lookupColumns = lookupColumns;
		((DataPane) panel).setQueryParams(queryParams);
		panel.refreshUI();
		return panel;
	}

	public DataPane assignLocalVarsToDataPane(DataPane panel) {
		return assignLocalVarsToDataPane(panel, Main.mainScreen.getCurrentTabPanel());
	}

	public DataPane assignLocalVars(
			String dbType1, String connectionName1, String host1, String port1,
			String connType1, String serviceName1, String userName1,
			String password1, String file1, final String execImmediateExpression) {
		dbType = dbType1;
		connectionName = connectionName1;
		host = host1;
		port = port1;
		connType = connType1;
		serviceName = serviceName1;
		userName = userName1;
		password = password1;
		file = file1;
		if (comboBoxFileDef != null)
			comboBoxFileDef.setSelectedItem(getFileName(file));
		return this;
	}

}