package com.rs2.modules.db;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.rs2.core.components.MyTree;
import com.rs2.core.components.myeditor.MyTextPane;
import com.rs2.core.components.treeview.DBTreeModel;
import com.rs2.core.components.treeview.DataNode;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.MainScreen;
import com.rs2.core.base.IService;
import com.rs2.core.base.MyCache;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;

import javax.swing.tree.TreePath;

import com.rs2.Main;
import com.rs2.core.components.MyAutoComplete;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.data.DBColumn;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBSchema;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.core.utils.RS2Util;
import com.rs2.core.utils.Utils;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.FocusEvent;

//hold local params (data) for each tab.
public class DBPane extends MyPane implements IService {

	public static boolean overrideOldTree = false;

	public String searchTable, searchColumn, searchKeyword;
	public JTextField textBoxTable, textBoxColumn, textBoxKeyword, textBoxInstitutionNumber;
	public JCheckBox chkAutoFixSql, chkShowTree;
	public JComboBox comboSearchExact, comboAllMatch;

	JSplitPane splitPane, splitPane1;
	JPanel inputPanel;

	Collection<String> allTables;
	Collection<String> allColumns = new LinkedList<String>();

	DBSchema schema;

	public String getId() {
		if (panelId == null || panelId.isEmpty())
			panelId = ServiceTitle;
		return super.getId();
	}

	public DBPane(Component view) {
		super(view);
	}

	public DBPane() {
		super();
	}

	public void run(String file) {
		Main.mainScreen.addPanel(this);
		String fileContent = openFile(file); // get File content
		this.txtSQL.setText(fileContent);
		add();
	}

	public void run(boolean overrideOldTree) {
		super.run(overrideOldTree);
	}

	// dynamically show input dialog for parameters and then generate sql file
	public void add() {
		this.txtSQL.selectAll();
		run(true);
	}

	@Override
	public void refreshLayout() {
		if (theBrowseTree == null) {
			theBrowseTree = createJTree("theBrowseTree");
		}

		splitPane1 = Utils.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(theBrowseTree),
				getMainJPanel());
		splitPane1.setDividerLocation(250);

		splitPane = Utils.createSplitPane(JSplitPane.VERTICAL_SPLIT, getTopJPanel(), splitPane1);
		splitPane.setDividerLocation(200);
		getJPanel().add(splitPane, BorderLayout.CENTER);
		refreshTree(null);
		getBottomJPanel().setVisible(!isHideLog);
	}

	public void initMenuPanel() {
		getMenuJpanel().add(btnOk);
		getMenuJpanel().add(btnAdd);
		getMenuJpanel().add(btnCollapse);
		// getMenuJpanel().add(btnHideLog);
		// getMenuJpanel().add(btnClearLog);
		getMenuJpanel().add(btnOpen);
		// btnCancel.setText("Save / Export");
		// getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnSearch);
		getMenuJpanel().add(btnExport);
		getMenuJpanel().add(btnClose);
	}

	public void initUI(Component view) {
		super.initUI(view);
		isHideLog = true;

		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(!isHideLog);
		getParamsPanel().setVisible(true);
		getSQLJpanel().setVisible(true);

		textBoxTable = Utils.createTextField(Settings.tableSearchAll, "tables");
		textBoxColumn = Utils.createTextField("", "columns");
		textBoxKeyword = Utils.createTextField("", "search_keyword");
		textBoxInstitutionNumber = MyService.initInstitutionTextField();
		textBoxInstitutionNumber.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (RS2Util.isValidInstitutionNumber(textBoxInstitutionNumber.getText().trim())) {
					// setupCachedData();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});

		chkAutoFixSql = Utils.createCheckBox(true);
		chkShowTree = Utils.createCheckBox(false);
		chkShowTree.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				generateMainComponent(true);
				// System.out.println(e.getStateChange() == ItemEvent.SELECTED
				// ? "SELECTED" : "DESELECTED");
			}
		});

		comboSearchExact = Utils.createComboBox(new String[] {
				Settings.searchMethod_Equals, Settings.searchMethod_Contains }, Settings.searchMethod_Equals,
				"Search Method");
		comboAllMatch = Utils.createComboBox(new String[] { Settings.searchCondition_AND, Settings.searchCondition_OR },
				Settings.searchCondition_OR, "Search Condition");

		initComboConns();

		refreshConnectionCombo();

		addParam("Tables", textBoxTable);
		addParam("Columns", textBoxColumn);
		addParam(Settings.paramInstitution, textBoxInstitutionNumber);
		addParam("show as tree", chkShowTree);

		if (view != null) {
			component = view;
			if (view instanceof MyTree)
				theGUITree = (MyTree) view;
		}

		textBoxTable.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (textBoxTable.getText().isEmpty())
					textBoxTable.setText(Settings.tableSearchAll);

				refreshTreeTables();
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});

		textBoxColumn.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				refreshTreeTables(false, true);
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public MyTextPane initSQLTextPane() {
		super.initSQLTextPane();
		Collection<String> files = Utils.getFilesShortNames(Utils.getFiles(Settings.getSqlsFolder(), "sql", true));
		Utils.setAutoComplete(txtSQL, "file", files);
		Utils.setAutoComplete(txtSQL, "sql", files);

		Collection<String> folders = Utils.getFolders(Settings.getSqlsFolder(),
				false);
		for (String folder : folders) {
			folder = Utils.getFileName(folder);
			if (Utils.isNumeric(folder) || folder.equals("0"))
				continue;
			Utils.setAutoComplete(txtSQL, folder,
					Utils.getFilesShortNames(Utils.getFiles(Settings.getSqlsFolder() + "/" +
							folder, "sql", true)));
		}

		return txtSQL;
	}

	@Override
	public void autoCompleteActionPerformed(String typedWord) {
		this.txtSQL.selectAll();
		run(true);
	}

	@Override
	public void setSQL(String sql) {
		if (sql == null)
			sql = "";
		if (sql.startsWith("+")) {
			int currPos = txtSQL.getCaretPosition();
			txtSQL.setText(txtSQL.getText().substring(0, currPos) + " " + sql.substring(1) + " "
					+ txtSQL.getText().substring(currPos));
			txtSQL.setCaretPosition(currPos + 1 + sql.substring(1).length());
			return;
		}
		txtSQL.setText((txtSQL.getText().trim() + "\n" + sql.trim()).trim());
		// Utils.copyToClipboard(sql.trim());
	}

	public void refreshUIMenu() {
		btnAdd.setVisible(viewOption == Settings.viewDetail);
		// btnEdit.setText(getButtonText(btnEdit));
		// btnEdit.setVisible(true);
		btnCancel.setVisible(true);
		btnOpen.setVisible(viewOption == Settings.viewDetail);
		btnReset.setVisible(viewOption == Settings.viewDetail);
		btnCollapse.setVisible(viewOption == Settings.viewDetail);
		btnSearch.setVisible(viewOption == Settings.viewDetail);
		btnOk.setVisible(viewOption == Settings.viewDetail);
		btnClose.setVisible(true);
	}

	public void updateParams() {

	}

	public void refreshButtons() {
		this.viewOption = Settings.viewDetail;
		super.refreshButtons();
		btnCancel.setVisible(true);
	}

	public void open() {
		this.open(Settings.getSqlsFolder(), true);
	}

	public void open(String folder) {
		this.open(folder, true);
	}

	public void open(String folder, boolean execute) {
		String filePath = Utils.selectFile("*.sql;*.xml", folder);
		if (filePath == null)
			return;
		String content = Utils.getContentFromFile(filePath);
		if (content == null)
			return;
		List<DBQuery> listAllQueryDefinitions = null;

		if (filePath.endsWith(".xml") || content.toLowerCase().contains("<root>")) {
			List<DBParam> params = new LinkedList<DBParam>();

			listAllQueryDefinitions = DataViewerService.generateDBQueryFromExportFile(filePath, params);
			if (listAllQueryDefinitions == null || listAllQueryDefinitions.size() == 0)
				listAllQueryDefinitions = DataViewerService.generateDBQuery(filePath, connectionName);
		}

		if (listAllQueryDefinitions != null && listAllQueryDefinitions.size() > 0) {
			content = "";
			for (DBQuery query : listAllQueryDefinitions) {
				List<String> tables = schema.getTableNames(query.getTableName(), "");
				String sql = "";
				String sqlOld = query.getSqlQuery();
				for (String tableName : tables) {
					sql = sqlOld.toLowerCase().replace(" " + query.getTableName().toLowerCase() + " ",
							" " + tableName + " ");
					if (!sql.isEmpty())
						content = content + "\n" + sql + ";";
				}
			}
		}
		setSQL(content.trim());
		this.viewOption = Settings.viewDetail;
		refreshButtons();

		if (execute)
			run(true);
	}

	public String getExportFileType() {
		return inputDialog.showComboBox("Please select file type", "Please select file type",
				new String[] { "sql", "dataviewer", "excel" }, "sql");
	}

	// save file
	public void export(String fileType) {
		String content = txtSQL.getText();
		if (fileType == null || fileType.isEmpty())
			return;
		if (fileType.equals("dataviewer")) {
			String fileName = Utils.selectFile("xml", Settings.getModulesFolder() + "\\dataviewer");
			if (fileName == null)
				return;
			if (!fileName.endsWith(".xml"))
				fileName = fileName + ".xml";
			StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			sb.append("<ROOT>\n");
			String[] sqls = Utils.splitSQLs(content);
			List<String> paramNames = Utils.getParamsFromSql(content);
			if (paramNames != null && paramNames.size() > 0) {
				for (String param : paramNames) {
					sb.append("<param key=\"").append(param).append("\">").append("</param>\n");
				}
			}
			for (String sql : sqls) {
				sb.append("<SQLQueryDefinition>\n").append("\t<SQLQuery>\n\t\t").append(sql).append("\n\t</SQLQuery>\n")
						.append("</SQLQueryDefinition>\n");
			}
			sb.append("</ROOT>");
			content = sb.toString();
			Utils.saveFile(fileName, content);

			this.viewOption = Settings.viewDetail;
			refreshButtons();

		} else {
			super.export(fileType);
		}
	}

	public void resetParams() {
		searchTable = "";
		searchColumn = "";
		searchKeyword = "";
		listAllQueryDefinitions = null;
	}

	public void refreshUIParams() {
		refreshUIParams(null);
	}

	public void refreshUIParams(String message) {
		this.validate();
	}

	public boolean isShowTree() {
		return chkShowTree.isSelected();
	}

	public void refreshTree(DBSchema schema) {
		if (theBrowseTree == null) {
			theBrowseTree = createJTree("theBrowseTree");
			splitPane1.setLeftComponent(new JScrollPane(theBrowseTree));
			splitPane1.revalidate();
		} else {
			theBrowseTree.setModel(new DBTreeModel(schema, theBrowseTree));
			theBrowseTree.revalidate();
		}
	}

	@Override
	public JPopupMenu createPopupMenu(MyTree tree, TreePath selPath) {
		if (tree.getName() != null && tree.getName().equalsIgnoreCase("theBrowseTree")) {
			popupMenu = new JPopupMenu();

			String[] sqls = new String[] { "+{table}",
					"+{columns}",
					"+{columnsDATA}",
					"+{columnsWITHDATA}",
					"+({columnsWITHDATA_AND})",
					"+({columnsWITHDATA_OR})",
					"",
					"SELECT * FROM {table} ORDER BY 1;",
					"SELECT * FROM {table} ORDER BY {columns};",
					"SELECT * FROM {table} WHERE {columnsWITHDATA_AND} ORDER BY 1;",
					"",
					"SELECT DISTINCT {columns} FROM {table} ORDER BY 1;",
					"SELECT DISTINCT {columns} FROM {table} ORDER BY 1 WHERE ROWNUM <= 100;",
					"",
					"SELECT {column0}, count(*) FROM {table} GROUP BY {column0} ORDER BY 1;",
					"SELECT {columns}, count(*) FROM {table} GROUP BY {columns} ORDER BY 1;",
					"",
					"INSERT INTO {table} ({columns}) VALUES ({columnsDATA});",
					"UPDATE {table} SET {columnsWITHDATA} WHERE {columnsWITHDATA_AND};",
					"DROP TABLE {table};"
			};

			for (String sql : sqls) {
				addDBTreeMenuItem(popupMenu, sql);
			}
			return popupMenu;
		}
		return super.createPopupMenu(tree, selPath);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String selItem = (String) theBrowseTree.getLastSelectedPathComponent();
		TreePath[] tp = theBrowseTree.getSelectionPaths();
		Map<String, List<String>> data = new LinkedHashMap<String, List<String>>();

		for (TreePath path : tp) {
			String column = (String) path.getLastPathComponent();
			if (!((DBTreeModel) theBrowseTree.getModel()).isTable(column)) {
				// selectedColumns.add(column);
				String table = (String) path.getParentPath().getLastPathComponent();
				if (!data.containsKey(table))
					data.put(table, new LinkedList<String>());
				data.get(table).add(column);
			} else {
				String table = column;
				if (!data.containsKey(table))
					data.put(table, new LinkedList<String>());
			}
		}
		if (((DBTreeModel) theBrowseTree.getModel()).isTable(selItem)) {
			String table = selItem;
			if (!data.containsKey(table))
				data.put(table, new LinkedList<String>());
		}

		for (String table : data.keySet()) {
			List<String> selectedColumns = data.get(table);
			List<String> allColumns = new LinkedList<String>();

			String columns = "";
			String columnsData = "";
			String columnsWITHDATA = "";
			String columnsWITHDATA_AND = "";
			String columnsWITHDATA_OR = "";

			String primaryKeysWITHDATA = "";
			String column0 = "";

			if (table.length() > 0 && theBrowseTree.myPane != null && ((DBPane) theBrowseTree.myPane).schema != null) {
				allColumns = ((DBPane) theBrowseTree.myPane).schema.getColumnNames(table);
			}

			if (selectedColumns.size() == 0)
				selectedColumns = allColumns;
			if (selectedColumns.size() > 0) {
				for (String column : selectedColumns) {
					if (column0.length() == 0)
						column0 = column;
					columns = columns + column + ",";
					columnsWITHDATA = columnsWITHDATA + column + " = '" + Settings.paramPrefix1 + column + "',";
					columnsData = columnsData + "'" + Settings.paramPrefix1 + column + "',";
				}
				columns = columns.substring(0, columns.length() - 1);
				columnsWITHDATA = columnsWITHDATA.substring(0, columnsWITHDATA.length() - 1);
				columnsData = columnsData.substring(0, columnsData.length() - 1);
				columnsWITHDATA_AND = columnsWITHDATA.replace(",", " AND ");
				columnsWITHDATA_OR = columnsWITHDATA.replace(",", " OR ");
			}

			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("table".toLowerCase(), table);
			params.put("columns".toLowerCase(), columns);
			params.put("columnsDATA".toLowerCase(), columnsData);
			params.put("columnsWITHDATA".toLowerCase(), columnsWITHDATA);
			params.put("primaryKeysWITHDATA".toLowerCase(), primaryKeysWITHDATA);
			params.put("columnsWITHDATA_AND".toLowerCase(), columnsWITHDATA_AND);
			params.put("columnsWITHDATA_OR".toLowerCase(), columnsWITHDATA_OR);
			params.put("column0", column0);

			String sql = Utils.formatTemplate(e.getActionCommand().toLowerCase(), params, "{", "}").toLowerCase();

			List<DBParam> sqlParams = DBService.getQueryParamsFromSQLs(sql, queryParams, theParameters);
			if (!textBoxInstitutionNumber.getText().isEmpty())
				sqlParams = DBService.addQueryParam(
						sqlParams, Settings.paramInstitution,
						textBoxInstitutionNumber.getText().trim());
			sql = DBService.applyParams(sql, sqlParams);
			setSQL(sql);
		}
	}

	protected void refreshTreeTables() {
		refreshTreeTables(true, true);
	}

	protected void refreshTreeTables(boolean filterTable, boolean filterColumn) {
		if (filterTable)
			((DBTreeModel) theBrowseTree.getModel()).setTablesFilter(textBoxTable.getText().trim());
		if (filterColumn)
			((DBTreeModel) theBrowseTree.getModel()).setColumnsFilter(textBoxColumn.getText().trim());
	}

	@Override
	public void search() {

		btnHideLog.setText("Search");
		if (textBoxInstitutionNumber == null) // not initialize ui yet
			return;

		listAllQueryDefinitions = new LinkedList<DBQuery>();

		List<String> tables1 = ((DBTreeModel) theBrowseTree.getModel()).tableNames;
		if (textBoxColumn.getText().contains(Settings.operatorAND))
			comboAllMatch.setSelectedItem(Settings.searchCondition_AND);

		Map<String, String> settings = MyInputDialog.instance().showMapInput(null,
				"Search [keyword] "
						+ (textBoxColumn.getText().trim().isEmpty() ? ""
								: " in columns [" + textBoxColumn.getText().trim() + "]")
						+ (tables1 == null ? "" : " in " + String.valueOf(tables1.size()) + " tables."),
				new Component[] {
						textBoxKeyword,
						new JSeparator(),
						Utils.createTextField(textBoxInstitutionNumber, "institution_number"),
						Utils.createTextField(textBoxTable, "tables"),
						Utils.createTextField(textBoxColumn, "columns"),

						// chkSearchExact, chkAllMatch,
						comboSearchExact, comboAllMatch });
		if (settings == null) // cancel
			return;
		textBoxTable.setText(settings.get("tables"));
		textBoxColumn.setText(settings.get("columns"));
		textBoxInstitutionNumber.setText(settings.get("institution_number"));
		refreshTreeTables();

		searchKeyword = textBoxKeyword.getText().trim();
		if (searchKeyword == null || searchKeyword.isEmpty())
			return;

		final boolean searchExact = String.valueOf(comboSearchExact.getSelectedItem())
				.equalsIgnoreCase(Settings.searchMethod_Equals);

		if (searchExact) {
			searchKeyword = searchKeyword.replace(Settings.operatorAll, "");
		} else {
			if (!searchKeyword.contains(Settings.operatorAll))
				searchKeyword = Settings.operatorAll + searchKeyword + Settings.operatorAll;
		}
		textBoxKeyword.setText(searchKeyword);

		final boolean allMatch = String.valueOf(comboAllMatch.getSelectedItem())
				.equalsIgnoreCase(Settings.searchCondition_AND);

		if (allMatch) {
			textBoxColumn.setText(textBoxColumn.getText().replace(Settings.operatorOR, Settings.operatorAND));
		}

		// validate connection
		conn = getConnection();
		if (conn == null) {
			MyDialog.showException("Connection is not available. Please reconnect", "Error");
			return;
		}

		if (tables1 == null) {
			refreshConnectionCombo();
			if (((DBTreeModel) theBrowseTree.getModel()).tableNames == null) {
				initSchema(conn, connectionName, Settings.tableSearchAll);
				while (((DBTreeModel) theBrowseTree.getModel()).tableNames == null) {
					// wait until finish thread
				}
			}
			refreshTreeTables();
		}

		final List<String> tables = ((DBTreeModel) theBrowseTree.getModel()).tableNames;
		final String institution_number = textBoxInstitutionNumber.getText().trim();
		final String searchColumn = textBoxColumn.getText().trim();
		final String searchKeyword = textBoxKeyword.getText().trim();
		final String searchTable = textBoxTable.getText().trim();

		startProgressBar();
		Utils.disableAutoComplete(txtSQL); // avoid error
		Thread queryThread = new Thread() {
			public void run() {
				try {

					log("\n\nSearching " + " tables [" + searchTable + "] with columns [" + searchColumn
							+ "] with keyword ["
							+ searchKeyword + "]. Total " + tables.size() + " searched.");

					int countTablesHasColumn = 0;
					int countTablesHasKeywords = 0;
					String logSuccess, logFail = "";
					int i = 0;
					isRunning = true;
					Set<String> columns;

					for (String table : tables) {
						if (!isRunning)
							break;
						i += 1;
						String log = "-- " + String.valueOf(i) + ". Table [" + table + "] ";
						log(log);

						columns = DBService.getColumns(schema, table, searchColumn);

						String operatorAND_OR = allMatch ? "AND" : "OR";

						DBQuery plan = null;
						String sql = "";
						if (columns.size() > 0) {
							StringBuilder sqlBuilder = new StringBuilder();
							sqlBuilder.append("SELECT * FROM ").append(table).append(" WHERE (");
							if (!searchKeyword.equals("%") && columns != null && columns.size() > 0) {
								for (String column : columns) {
									DBColumn dbColumn = schema.getDBColumn(table, column);
									if (dbColumn != null && dbColumn.isNumberDataType() && !Utils.isNumeric(
											searchKeyword)) // skip number column
										continue;

									if (searchKeyword.contains(",")) {

										String prefix = searchKeyword.startsWith("%") ? "%" : "";
										String postfix = searchKeyword.endsWith("%") ? "%" : "";

										if (searchKeyword.contains("%")) {
											String[] keywords = searchKeyword.split(",");
											sqlBuilder.append("(");
											for (String keyword : keywords) {

												sqlBuilder.append(column).append(" LIKE '")
														.append((prefix + keyword + postfix).replace("%%", "%"))
														.append("' OR ");
											}
											sqlBuilder.delete(sqlBuilder.length() - 3, sqlBuilder.length()).append(") ")
													.append(operatorAND_OR).append(" ");
										} else {
											sqlBuilder.append(column).append(" IN ('")
													.append(searchKeyword.replace(",", "','"))
													.append("') ").append(operatorAND_OR).append(" ");
										}
									} else if (searchKeyword.contains("%"))
										sqlBuilder.append(column).append(" LIKE '").append(searchKeyword)
												.append("' ").append(operatorAND_OR).append(" ");
									else
										sqlBuilder.append(column).append(" = '").append(searchKeyword)
												.append("' ").append(operatorAND_OR).append(" ");
								}
							}

							if (sqlBuilder.toString().endsWith(" " + operatorAND_OR + " "))
								sqlBuilder.delete(sqlBuilder.length() - (" " + operatorAND_OR + " ").length(),
										sqlBuilder.length());
							if (sqlBuilder.toString().endsWith(" WHERE "))
								sqlBuilder.delete(sqlBuilder.length() - " WHERE ".length(), sqlBuilder.length());

							sqlBuilder.append(")");
							// sqlBuilder.append(" ORDER BY 1");

							plan = new DBQuery("", table);
							if (!searchKeyword.equals("%")) {
								// plan.setQueryTitle(sqlBuilder.toString());
								sql = sqlBuilder.toString();
								if (chkAutoFixSql.isSelected()) {
									if (!institution_number.isEmpty()) {
										sql = DataViewerService.getSQLWithInstitutionWhereClause(sql);
										DBService.addQueryParam(queryParams, Settings.paramInstitution,
												institution_number);
									}
									if (!sql.toLowerCase().contains("order by"))
										sql = sql + " order by 1";
								}

								sql = DBService.applyParams(sql, queryParams);
								plan.setQueryTitle(sql);
								plan.setConnectionName(connectionName);
								plan.setQueryParams(queryParams);
								plan.setData(sql, conn);

								if (plan.hasDataIgnoreFirstRowAsColumns())
									listAllQueryDefinitions.add(plan);
							} else {

								if (columns != null && columns.size() > 0) {
									plan.setData(columns, "COLUMNS");
									listAllQueryDefinitions.add(plan);
								}
							}

							// try to get institution_number from query data, to prepare cached lookup
							if (institution_number.isEmpty() && plan.hasResultData()) {
								List<String> institutionList = plan
										.getColumnsDistinctData(Settings.columnInstitution);
								if (institutionList.size() > 0) {
									DBService.addQueryParam(queryParams, Settings.paramInstitution,
											institutionList.get(0));
								}
							}
						}

						if (!searchKeyword.isEmpty()) {
							if (columns.size() > 0) {
								// log += (" has column [" + searchColumn + "].");
								if (!searchKeyword.equals("%")) {
									if (plan.hasResultData()) {
										log = "-- has keyword: YES ("
												+ String.valueOf(plan.getResultRowsMatrix()[0].length - 1)
												+ " rows).";
										countTablesHasKeywords += 1;
										setSQL(sql + ";");
									} else
										log = ("-- does not have keyword.");
								}
								countTablesHasColumn += 1;
							} else
								log = "-- does not have column.";
						}

						log(log + "\n");

					}

					DBService.setupCachedData(listAllQueryDefinitions, lookupColumns, connectionName, queryParams);

					String log = "";
					if (countTablesHasColumn > 0)
						log += ("Total " + String.valueOf(countTablesHasColumn)
								+ " tables has column "
								+ searchColumn);
					if (countTablesHasKeywords > 0)
						log += ("," + String.valueOf(countTablesHasKeywords) + " tables has keywords " + searchKeyword);
					else
						log += (", NO table has keywords " + searchKeyword);

					MyDialog.showDialog(log, "Search completed");
					stopProgressBar();

				} catch (Exception ex) {
					stopProgressBar();
					MyDialog.showException(ex, "Error when performing search.");
				}
				generateDataPane(listAllQueryDefinitions, true);
				Utils.enableAutoComplete(txtSQL);
			}
		};
		queryThread.start();
	}

	@Override
	public void log(String text) {
		LogManager.getLogger().debug(text);
	}

	public void initSchema(String tableInclusionClause) {
		startProgressBar();
		Thread queryThread = new Thread() {
			public void run() {
				try {
					initSchema(conn, connectionName, tableInclusionClause);
					stopProgressBar();
				} catch (Exception ex) {
					stopProgressBar();
					MyDialog.showException(ex, "Error when initializing schema for connection " + connectionName);
				}
			}
		};
		queryThread.start();
		;
	}

	public DBSchema initSchema(Connection conn, String connectionName, String tableInclusionClause) {
		schema = DBSchema.getDBSchema(conn, connectionName, tableInclusionClause);
		if (Main.connectionName.equalsIgnoreCase(connectionName))
			Main.dbSchema = schema; // set current schema

		refreshTree(schema);
		if (schema != null) {
			Utils.setAutoComplete(textBoxTable, schema.getTableNames());
			Collection<String> tables = schema.getTableNames();
			Collection<String> tableCommands = new LinkedList<String>();
			Map<String, Collection<String>> tableColumns = new LinkedHashMap<String, Collection<String>>();
			Collection<String> tableColumnsAll = new LinkedList<String>();

			for (String table : tables) {
				String sql = "select * from " + table;

				List<String> columns = schema.getColumnNames(table);
				if (columns.contains(Settings.columnInstitution.toUpperCase()))
					sql += " where " + Settings.columnInstitution + " = '" + textBoxInstitutionNumber.getText().trim()
							+ "'";
				sql += " order by 1;";
				tableCommands.add(sql);
				tableColumns.put(table.toLowerCase(), columns);
				for (String column : columns)
					tableColumnsAll.add(column + "   @" + table);
			}
			Utils.setAutoComplete(txtSQL, 1, "table", tableColumns);
			Utils.setAutoComplete(txtSQL, 1, "column", tableColumnsAll);
			Utils.setAutoComplete(txtSQL, 1, "select", tableCommands);

			// MyCache.CachedData.keySet();
			Map<String, List<String>> dataLookup = DataViewerService
					.getAutoCompletes(new String[] { "transaction_status", "transaction_type", "cards" });
			Utils.setAutoComplete(txtSQL, 1, "data", dataLookup);

			// for (String table : tables) {
			// LinkedList<String> tmp = new LinkedList<String>();
			// tmp.add("select * from " + table + " order by 1;");
			// tmp.addAll(schema.getColumnNames(table));
			// Utils.setAutoComplete(txtSQL, table, tmp);
			// }
		}
		return schema;
	}

	public void initQueryParams() {
		queryParams = new LinkedList<DBParam>();
		queryParams.add(new DBParam(Settings.paramInstitution, textBoxInstitutionNumber.getText()));
	}

	public void setupCachedData() {
		setupCachedData(null);
	}

	public void setupCachedData(List<String> columns) {
		boolean needRefresh = true;
		Main.startThread("Setting cached data");

		if (queryParams != null) {
			for (DBParam param : queryParams) {
				if (param.getKey().equalsIgnoreCase(Settings.paramInstitution)
						&& param.getValue().equalsIgnoreCase(textBoxInstitutionNumber.getText())) {
					needRefresh = false;
					break;
				}
			}
		}
		if (needRefresh || MyCache.CachedData.size() == 0) {
			initQueryParams();
			if (Settings.forceClearCachedBeforeEachExecution)
				DBService.clearCachedData(connectionName);
		}
		DBService.setupCachedData(lookupColumns, connectionName, queryParams, columns);
		Main.stopThread();
	}

	public void refreshConnectionCombo() {
		try {
			boolean connectionChanged = isConnectionChanged() || conn == null || conn.isClosed();
			if (connectionChanged) {
				initConnection();
				if (conn == null)
					return;
				// Settings.storeLastConnectionName(connectionName);

				setupCachedData();

				List<String> lookupWords = getService().getCachedDataAsList(connectionName,
						DBService.getDBFieldFromParamName(Settings.paramInstitution), queryParams);
				Utils.setAutoComplete(textBoxInstitutionNumber, lookupWords);

				initSchema(Settings.tableSearchAll);

				if (textBoxTable != null) {
					String searchTable = textBoxTable.getText().trim();
					if (!searchTable.contains("%"))
						searchTable = searchTable + "%";
				}
				refreshTree(null);
				setTitle((String) comboBoxStoredConns.getSelectedItem());
			}

		} catch (Exception ex) {
			LogManager.getLogger().error(ex);
		}
	}

	public void initData() {
		searchTable = textBoxTable.getText().toUpperCase();

		if (searchTable.isEmpty()) {
			searchTable = Settings.tableSearchAll;
			textBoxTable.setText(searchTable);
		}

		initQueryParams();
	}

	public MyPane generateDataPane(boolean refreshFlag, String execImmediateExpression, boolean overrideOldTree) {
		// logger.setTextArea(logTextArea);
		try {
			if (refreshFlag) {
				initData();
				generateListDBQuery();
				refreshUIParams("Generate tree");
			}

			if (listAllQueryDefinitions != null)
				generateDataPane(listAllQueryDefinitions, overrideOldTree);
		} catch (Exception e) {
			return generateErrorPane(e, "Error when generate temp tables or retreiving data or generate tree");
		}
		return this;
	}

	public void generateListDBQuery() {
		generateListDBQuery(Utils.getCurrentText(txtSQL).trim());
	}

	public void generateListDBQuery(String searchSQL) {
		conn = getConnection();
		// initQueryParams();

		listAllQueryDefinitions = new LinkedList<DBQuery>();
		String institution_number = textBoxInstitutionNumber.getText().trim();

		if (viewOption.equalsIgnoreCase(Settings.viewDetail)) {
			if (!searchSQL.isEmpty()) {
				List<DBParam> queryParams = getQueryParamsFromSQLs(searchSQL);
				List<String> params = Utils.getParamsFromSqlWithoutComments(searchSQL);
				// int response = JOptionPane.OK_OPTION;
				if (queryParams != null) { // if null then means cancel is clicked
					JCheckBox actionExecute = new JCheckBox("Execute SQL", Settings.settingExecute);
					JCheckBox actionOverride = new JCheckBox("Override SQL", Settings.settingOverride);
					JCheckBox actionSave = new JCheckBox("Save to file", Settings.settingSaveFile);

					if (params.size() > 0) { // has params (&) in sql ->
						searchSQL = DBService.applyParamsRawSQL(searchSQL, queryParams);
						searchSQL = DBService.applyRequireFile(searchSQL, this.file);

						MyDialog.addComponent(actionSave);
						MyDialog.addComponent(actionOverride);
						MyDialog.addComponent(actionExecute);

						searchSQL = MyDialog.showEdit(searchSQL,
								"Preview generated SQL. Click OK to continue.");

						MyDialog.removeComponents();
					}

					// else {
					if (searchSQL != null && !searchSQL.isEmpty()) {
						Utils.copyToClipboard(searchSQL);

						if (actionSave.isSelected()) {
							export(searchSQL);
						}

						if (actionOverride.isSelected()) {
							txtSQL.setText(searchSQL);
						}

						if (DBService.isStatementSQL(searchSQL)) {
							this.listAllQueryDefinitions = null;

						} else {
							listAllQueryDefinitions = DataViewerService.generateDBQueryFromSQLsList(Utils.splitSQLs(
									searchSQL), queryParams,
									lookupColumns, conn, connectionName, institution_number, chkAutoFixSql.isSelected(),
									!Settings.allowExecuteSQLStatement);
						}
					}
				}
			}
		}
	}

	private boolean isRefreshing = false;

	public void refreshInputPanelComponents(JPanel inputPanel) {
		for (Component comp : inputPanel.getComponents()) {
			if (comp instanceof JPanel) { // if it is wizard panel
				refreshInputPanelComponents((JPanel) comp);
				continue;
			}

			if (comp instanceof JLabel || comp.getName() == null || !(comp instanceof JComponent))
				continue;

			if (comp instanceof JComboBox) {
				// set value again to cause refresh performance actions
				refreshComponentData((JComboBox) comp);
			}
		}
	}

	// refresh data of every components in input panel.
	public void initInputPanelComponents(JPanel inputPanel) {
		for (Component comp : inputPanel.getComponents()) {
			if (comp instanceof JPanel) { // if it is wizard panel
				initInputPanelComponents((JPanel) comp);
				continue;
			}

			if (comp instanceof JLabel || comp.getName() == null || !(comp instanceof JComponent))
				continue;
			// refreshInputPanelComponents(comp.getName(),
			// Utils.getComponentValue((JComboBox) comp), (JComponent) comp);

			// show /hide components based on requiredBy component
			if (comp.getName().toLowerCase().contains(Settings.paramRequiredBy) && comp instanceof JComboBox) {
				String tmp = Utils.substringBetween(comp.getName(), Settings.paramRequiredBy, "");
				for (Component comp1 : inputPanel.getComponents()) {
					if (comp1 == comp || comp1 instanceof JLabel || comp1.getName() == null
							|| !(comp1 instanceof JComponent)
							|| !(comp1 instanceof JComboBox))
						continue;
					String requiredByField = MyCache.getCachedKey(comp1.getName());
					if (tmp.contains(requiredByField)) {
						String requiredByValue = Utils.substringBetween(tmp, requiredByField + "_", "_");
						if (requiredByValue.contains(Utils.getComponentValue((JComboBox) comp1))) {
							comp.setVisible(true);
						} else {
							comp.setVisible(false);
						}
					}
				}
			}

			if (comp instanceof JTextField) {
				((JTextField) comp).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						refreshComponentData((JTextField) comp);

					}
				});
			} else if (comp instanceof JComboBox) {
				((JComboBox) comp).addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						if (arg0.getStateChange() == ItemEvent.SELECTED) {
							Thread queryThread = new Thread() {
								public void run() {
									refreshComponentData((JComboBox) comp);
								};
							};
							queryThread.start();
						}
					}
				});

				((JComboBox) comp).addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						Thread queryThread = new Thread() {
							public void run() {
								refreshComponentData((JComboBox) comp);
							};;
						};
						queryThread.start();
					}
				});
			}
		}
	}

	public void autoCompleteActionPerform(MyAutoComplete autoComplete) {
		if (autoComplete == null)
			return;
	}

	// refresh component values if other component changed value
	public void refreshInputPanelComponents(String fieldName, String fieldValue, JComponent component) {
		if (component instanceof JComboBox)
			if (!Utils.isComboBoxChanged((JComboBox) component))
				return;
		List<String> changedFields = new LinkedList<String>();
		Map<String, String> refreshSQLS = new LinkedHashMap<String, String>();
		Map<String, String> tmpParams = Utils.getJPanelInput(inputPanel);
		List<DBParam> dbParams = new LinkedList<DBParam>();

		for (Map.Entry<String, String> param : tmpParams.entrySet()) {
			DBService.addQueryParam(dbParams, Settings.paramPrefix + MyCache.getCachedKey(param.getKey()),
					param.getValue());
		}

		refreshInputPanelComponents(fieldName, fieldValue, component, this.inputPanel, changedFields, refreshSQLS,
				tmpParams, dbParams);
	}

	// refresh component values if other component changed value, recursive and
	// build up changedFields & refreshSQLS
	public void refreshInputPanelComponents(String fieldName, String fieldValue, JComponent component,
			JPanel inputPanel, List<String> changedFields, Map<String, String> refreshSQLS,
			Map<String, String> tmpParams, List<DBParam> dbParams) {

		String lookupFieldName = MyCache.getCachedKey(fieldName);
		if (!changedFields.contains(lookupFieldName))
			changedFields.add(lookupFieldName);

		for (Component comp : inputPanel.getComponents()) {
			if (comp instanceof JPanel) {
				refreshInputPanelComponents(fieldName, fieldValue, component, (JPanel) comp, changedFields,
						refreshSQLS, tmpParams, dbParams);
				continue;
			}
			if (comp instanceof JButton || comp instanceof JLabel || comp.getName() == null
					|| comp.getName().equalsIgnoreCase(fieldName)
					|| !(comp instanceof JComponent))
				continue;
			String compField = MyCache.getCachedKey(comp.getName());
			// if (comp instanceof JComboBox) {
			DBLookup lookup = null;
			for (DBLookup tmpLookup : lookupColumns) {
				if (tmpLookup.getFieldLookup().equalsIgnoreCase(compField)) {
					lookup = tmpLookup;
					break;
				}
			}
			if (lookup != null) {
				String refreshSQL = "";

				for (String changedField : changedFields) {
					lookupFieldName = MyCache.getCachedKey(changedField);
					String lookupFieldValue = component instanceof JComboBox
							? Utils.getComponentValue((JComboBox) component)
							: Utils.getComponentValue((JTextField) component);

					// this component need refresh because sql contains
					if (lookup.getRefreshSQL().contains(Settings.paramPrefix + lookupFieldName)
							&& lookupFieldValue != null && !lookupFieldValue.isEmpty()) {
						refreshSQL = lookup.getRefreshSQL();
						List<String> missingParams = DBService.getMissingParams(refreshSQL, dbParams);
						if (missingParams.size() > 0) { // if missing params (params with no data) then dont use this
							refreshSQL = "";
						} else {
							changedFields.add(compField);
						}
					}
					if (refreshSQL.isEmpty() && lookup.getSql().contains(Settings.paramPrefix + lookupFieldName)) {
						refreshSQL = lookup.getSql();
					}

					if (!refreshSQL.isEmpty()) {
						refreshSQLS.put(comp.getName(), refreshSQL);
						comp.setEnabled(false);
						break;
					}
				}
			}
			// }
		}

		refreshInputPanelComponents(inputPanel, refreshSQLS, dbParams);

		isRefreshing = false;
	}

	void refreshInputPanelComponents(JPanel inputPanel, Map<String, String> refreshSQLS, List<DBParam> dbParams) {
		for (Component comp : inputPanel.getComponents()) {
			if (comp instanceof JPanel) {
				refreshInputPanelComponents((JPanel) comp, refreshSQLS, dbParams);
				continue;
			}
			if (comp.isEnabled())
				continue;

			String refreshSQL = refreshSQLS.containsKey(comp.getName()) ? refreshSQLS.get(comp.getName()) : "";

			if (!refreshSQL.isEmpty()) {
				refreshSQLS.put(comp.getName(), refreshSQL);
				List<String> listData = MyService.getLookupDataList(connectionName, refreshSQL,
						null, null, dbParams);

				if (comp instanceof JComboBox) {
					Utils.setComboBoxData((JComboBox) comp, listData);
					refreshDBParams(dbParams, (JComboBox) comp);

				} else if (comp instanceof JTextField) {
					Utils.setLookupValues((JTextField) comp, listData);
				}
			}

			comp.setEnabled(true);
		}

	}

	void refreshComponentData(JComponent comp) {
		if (comp == null)
			return;
		if (comp instanceof JComboBox) {
			refreshInputPanelComponents(comp.getName(),
					MyService.getKeyFromDisplayKeyValue(
							((JComboBox) comp).getSelectedItem() == null ? ""
									: ((JComboBox) comp).getSelectedItem().toString()),
					(JComboBox) comp);
		} else if (comp instanceof JTextField) {
			refreshInputPanelComponents(comp.getName(),
					MyService.getKeyFromDisplayKeyValue(
							((JTextField) comp).getText()),
					(JTextField) comp);
		}
	}

	void refreshComponentData(JComboBox comp, String refreshSQL, List<DBParam> dbParams) {
		if (comp == null)
			return;
		List<String> listData = MyService.getLookupDataList(connectionName, refreshSQL, null, null, dbParams);
		Utils.setComboBoxData(comp, listData);
		refreshDBParams(dbParams, comp);
	}

	void refreshDBParams(List<DBParam> dbParams, JComboBox comp) {
		if (comp == null)
			return;
		DBService.addQueryParam(dbParams, MyCache.getCachedKey(comp
				.getName()), MyService.getKeyFromDisplayKeyValue(
						comp.getSelectedItem() == null ? "" : comp.getSelectedItem().toString()));
	}

	@Override
	public List<DBParam> getQueryParamsFromSQLs(String sql) {
		List<DBParam> tmp = getQueryParamsFromSQLs(sql, queryParams);
		if (tmp != null)
			queryParams = tmp;
		return tmp;
	}

	public List<DBParam> getQueryParamsFromSQLs(String sql, List<DBParam> queryParams) {
		params = new LinkedHashMap<String, String>();
		initQueryParams();

		queryParams = DBService.getQueryParamsFromSQLs(sql, queryParams, params, false);
		int i = 0;
		List<String> columns = new LinkedList<String>();
		for (Map.Entry<String, String> param : params.entrySet()) {
			columns.add(MyCache.getCachedKey(param.getKey()));
			// try to get default value and display !!
			if (param.getKey().contains(Settings.paramParamValueSeparator) && param.getValue().trim().isEmpty()) {
				String[] arr = param.getKey().split(Settings.paramParamValueSeparator);
				if (arr.length > 1) {
					param.setValue(arr[1].contains("_") ? arr[1].substring(0, arr[1].indexOf("_")) : arr[1]);
					params.put(param.getKey(), param.getValue());
				}
			} else if (MyCache.getCachedKey(param.getKey()).equals(MyCache.getCachedKey(Settings.paramInstitution))
					&& param.getValue().trim().isEmpty()) {
				params.put(param.getKey(), textBoxInstitutionNumber.getText().trim());
			}

			if (param.getValue().toLowerCase().contains("yyyymmdd"))
				params.put(param.getKey(),
						Utils.replaceBetween(param.getValue(), "yyyyMMdd", Utils.showDate("yyyyMMdd")));
			else if (param.getKey().toLowerCase().startsWith("&audit_trail"))
				params.put(param.getKey(),
						RS2Util.getAuditTrail(conn, param.getValue().isEmpty() ? "MANUAL"
								: param.getValue(),
								""));
			i += 1;
		}

		if (params.size() > 0) {
			setupCachedData(columns);
			Map<String, List<String>> autoCompletes = DataViewerService
					.getAutoCompletes(columns.toArray(new String[columns.size()]));

			inputPanel = Utils.createJPanelInput(params, autoCompletes);

			// assign lookup values for each component inside panel
			initInputPanelComponents(inputPanel);

			refreshInputPanelComponents(inputPanel);

			Map<String, String> tmpParams = MyInputDialog.instance().showMapInput(
					getContentMessage(this.txtSQL.getText()),
					"Please input parameters (from left to right, downward):",
					inputPanel);

			if (tmpParams != null) {
				params = tmpParams;
				if (queryParams != null) {
					for (Map.Entry<String, String> entry : params.entrySet()) {
						DBService.addQueryParam(queryParams, entry.getKey(), entry.getValue());
					}
				}

				return queryParams;
			}
			return null;
		}
		return queryParams;
	}

	public String getTreeRootLabel() {
		return "";
		// return ServiceTitle;
		// return "Search table: " + searchTable + ", columns: " + searchColumn + ",
		// keywords: " + searchKeyword;
	}

	public DataNode generateRootDataNode() {
		return DataViewerService.generateDataNode(listAllQueryDefinitions, getTreeRootLabel(), viewOption, true, false);
	}
}