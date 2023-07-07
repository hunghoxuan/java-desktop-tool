package com.rs2.modules.export;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyLinkedMap;
import com.rs2.core.data.DBColumn;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBSchema;
import com.rs2.core.data.DBTableExport;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.core.base.MyServiceDialog;
import com.rs2.modules.db.DBService;

import com.rs2.core.utils.XMLUtil;

import com.rs2.core.utils.RS2Util;
import com.rs2.core.utils.Utils;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JTextArea;

public class ExportService extends MyService {
	public static String ServiceTitle = "Data Export";
	public static String FILES_FOLDER = "export";
	public static String FILES_EXTENSION = "xml";

	private static ExportService instance;
	static ExportDialog myDialog;

	public static ExportService getInstance() {
		if (instance == null)
			instance = new ExportService();
		return instance;
	}

	public ExportService() {

	}

	public static MyServiceDialog createDialog() {
		if (myDialog == null)
			myDialog = new ExportDialog();
		return myDialog;
	}

	public static List<String> getFiles(String lastFile) {
		return getFiles(FILES_FOLDER, FILES_EXTENSION, lastFile);
	}

	public static JMenu createMenu() {
		return createMenu(ServiceTitle,
				FILES_FOLDER, FILES_EXTENSION, createDialog());
	}

	public static void run(String file) {
		Map<String, String> theParameters = new HashMap<String, String>();
		theParameters.put("moduleDefFile", file);

		run(file, generateDataPane(theParameters, null));
	}

	public static ExportPane generateDataPane(String file) {
		ExportPane pane = new ExportPane();
		pane.run();
		return pane;
	}

	public static MyPane generateDataPane(Map<String, String> theParameters, List<String> storedFileLocators) {
		ExportPane pane = new ExportPane(theParameters);
		pane.storedFileLocators = storedFileLocators;
		pane.run();

		return pane;
	}

	public static void run(String[] args) {

		if (args == null && args.length == 0) {

			try {
				ExportDialog dialog = new ExportDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		} else if (args.length == 1) {
			if (args[0].equals("?") || args[0].toLowerCase().equals("help")// java -jar exportInst.jar ? oder //java
																			// -jar exportInst.jar help
					|| args[0].equals("-?") || args[0].toLowerCase().equals("-help")) {

				System.out.println(
						"\nDear Colleague or BW User, welcome to the Bankworks Product Modularization and Export Tool!\n\n"
								+
								"Please specify the following parameters separated by blank and in the following order: \nMandatory parameters:\n"
								+
								"	1) URL of the database host\n	2) Port\n	3) Service Name\n	4) User\n	5) Password\n	6) Source Institution_Number (for instance 00000002)\n"
								+
								"	7) Destination Institution_Number\n	8) Destination Institution Local Currency (please enter the numeric currency ISO 4217 code)\n	9) Delete Statements [0 = false| 1 = delete setup and clients only | 2 = delete setup, clients and transactions]\n"
								+
								"	10) Export All CHT Table Records with institution_number='00000000' [0 = false | 1 = true]\n	11) Record date = System date [0 = false | 1 = true]\n\n	EITHER you use an export definition file: \n	12) File Locator of the Module Definition XML\n"
								+
								"		And further Optional Parameters:\n			13) Destination URL\n" +
								"			14) Destination Port\n			15) Destination Service Name\n			16) Destination Username\n"
								+
								"			17) Destination Password\n\n	OR  you use the default institution export instead of the module definition file and...\n	12) Include Applications [0 = false | 1 = true]\n	13) Include All Clients [0 = false | 1 = true]\n	14) Apply Resets [0 = no resets | 1 = reset accounts | 2 = reset accounts and transaction sequences]"
								+ "\n		And further Optional Parameters:\n" +
								"			15) Destination URL\n			16) Destination Port\n			17) Destination Service Name\n			18) Destination Username\n"
								+
								"			19) Destination Password");
				return;
			}
			if (args[0].toLowerCase().equals("-info") || args[0].toLowerCase().equals("info")
					|| args[0].toLowerCase().equals("-version") || args[0].toLowerCase().equals("version")
					|| args[0].toLowerCase().equals("-ver") || args[0].toLowerCase().equals("ver")) {
				System.out
						.println(
								"\n\nDear Colleague or BW User, welcome to the Bankworks Product Modularization and Export Tool! \n"
										+
										"Developed, used and maintained by Radu-Marcel Dumitru since 2009.\n"
										+ "Last updated 13.03.2019\n" +
										"Type java -jar exportInst.jar help or java -jar exportInst.jar ? for a description of the available parameters.\n");
				return;
			}
		}
		// for (int t=0;t<args.length;t++){
		// System.out.println(t+" "+args[t]+" "+args.length);
		// }
		// if (args.length != 0) {
		if (args.length == 12 || args.length == 14 || args.length == 17 || args.length == 19) {
			Map<String, String> theParameters = new MyLinkedMap();

			theParameters.put("url", args[0]);
			theParameters.put("port", args[1]);
			theParameters.put("service_name", args[2]);
			theParameters.put("user", args[3]);
			theParameters.put("password", args[4]);
			theParameters.put("instNr", args[5]);
			theParameters.put("destInstNr", args[6]);
			theParameters.put("destLocalCurr", args[7]);
			theParameters.put("replaceFlag", args[8]);
			theParameters.put("exportAllCht", args[9]);
			theParameters.put("recDateIsSysDate", args[10]);

			if (args.length == 12) {// source instance only + definition file
				theParameters.put("moduleDefFile", args[11]);
			}

			if (args.length >= 14) {// source instance only + default institution export
				theParameters.put("includeApplications", args[11]);
				theParameters.put("includeAllClients", args[12]);
				theParameters.put("applyResets", args[13]);
			}

			if (args.length == 17) { // source + dest instances + definition file
				theParameters.put("moduleDefFile", args[11]);
				theParameters.put("destinationURL", args[12]);
				theParameters.put("destinationPort", args[13]);
				theParameters.put("destinationService_name", args[14]);
				theParameters.put("destinationUser", args[15]);
				theParameters.put("destinationPassword", args[16]);
			}

			if (args.length == 19) { // source + dest instances + default institution export
				theParameters.put("includeApplications", args[11]);
				theParameters.put("includeAllClients", args[12]);
				theParameters.put("applyResets", args[13]);
				theParameters.put("destinationURL", args[14]);
				theParameters.put("destinationPort", args[15]);
				theParameters.put("destinationService_name", args[16]);
				theParameters.put("destinationUser", args[17]);
				theParameters.put("destinationPassword", args[18]);
			}

			if (args.length == 12 || args.length == 17) { //

				theParameters.put("includeApplications", "0");
				theParameters.put("includeAllClients", "0");
				theParameters.put("applyResets", "0");
			}
			executeExport(theParameters, null);
		} else {
			System.out.println("\nYou entered an incorrect number of parameters: " + args.length
					+ "Type java -jar exportInst.jar help or java -jar exportInst.jar ? to see a list of the available parameters.\n");

			return;
		}
		// }
	}// end main

	public static List<DBQuery> executeExport(Map<String, String> theParameters, MyPane pane) {
		return executeExport(theParameters, null, pane);
	}

	public static String getSaveFolder(Map<String, String> theParameters) throws IOException {
		String to = "";
		String destinationURL = theParameters.get("destinationURL");
		String destinationService_name = theParameters.get("destinationService_name");
		String savedFolder = theParameters.get("savedFolder");
		String service_name = theParameters.get("service_name");
		String instNr = theParameters.get("instNr");
		String instNrDest = theParameters.get("destInstNr");
		String connectionName = theParameters.get("connection");
		String connectionNameDest = theParameters.get("destConnection");
		String moduleDefFile = getFileNameFull(theParameters.get("moduleDefFile"));
		if (!moduleDefFile.equals(Settings.DefaultInstitutionExport))
			moduleDefFile = new File(moduleDefFile).getName();

		if (destinationURL != null && destinationURL.length() > 0)
			to = "_to_" + destinationService_name;
		SimpleDateFormat normalTimeFormat = new SimpleDateFormat("YMd");
		String folder = Utils.getOutputFolder(savedFolder) + moduleDefFile + "__" + connectionName + "." + instNr + "__"
				+ connectionNameDest + "." + instNrDest + "__" + normalTimeFormat.format(new Date());
		// Utils.createFolder(folder);;

		return folder;
	}

	public static DBTableExport getDBTable(String tableName, Map<String, DBTableExport> moduleDef) {
		return getDBTableByName(tableName, moduleDef);
	}

	public static DBTableExport getDBTableByName(String tableName, Map<String, DBTableExport> moduleDef) {
		for (Map.Entry<String, DBTableExport> entry : moduleDef.entrySet()) {
			if (tableName.equalsIgnoreCase(entry.getValue().getTableName()))
				return entry.getValue();
		}
		return null;
	}

	public static DBTableExport getDBTableByKey(String tableId, Map<String, DBTableExport> moduleDef) {
		for (Map.Entry<String, DBTableExport> entry : moduleDef.entrySet()) {
			if (tableId.equalsIgnoreCase(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}

	public static DBTableExport getDBTableParent(DBTableExport table, Map<String, DBTableExport> moduleDef) {
		if (table.getParentName().isEmpty())
			return null;

		return getDBTableByKey(table.getId(), moduleDef);
	}

	public static Set<DBTableExport> getDBTableChildren(DBTableExport table, Map<String, DBTableExport> moduleDef) {
		Set<DBTableExport> children = null;
		for (Map.Entry<String, DBTableExport> entry : moduleDef.entrySet()) {
			if (entry.getValue().getParentName().equalsIgnoreCase(table.getTableName())
					&& entry.getValue().getTag().equals(table.getTag())) {
				if (children == null)
					children = new LinkedHashSet<DBTableExport>();
				children.add(entry.getValue());
			}
		}
		return children;
	}

	public static String getParamValue(Map<String, String> theParameters, String paramName) {
		return getParamValue(theParameters, paramName, "");
	}

	public static String getParamValue(Map<String, String> theParameters, String paramName, String defaultValue) {
		if (theParameters != null && theParameters.containsKey(paramName))
			return theParameters.get(paramName);

		theParameters.put(paramName, defaultValue);
		return defaultValue;
	}

	public static List<DBQuery> executeExport(Map<String, String> theParameters, String folder, MyPane pane) {
		boolean guiUsed = true;
		// List<DBQuery> listDBQueryList = new LinkedList<DBQuery>();
		List<DBQuery> listDBQuerySource = new LinkedList<DBQuery>();
		List<DBQuery> listDBQueryDest = new LinkedList<DBQuery>();

		// HashSet<String> EnableFK = new HashSet<String>();
		List<String> dataSelectionQueries = new ArrayList<String>();
		Date currDate = new Date();
		SimpleDateFormat auditTrailFormat;
		if (Integer.valueOf(new SimpleDateFormat("D").format(currDate)) > 99)
			auditTrailFormat = new SimpleDateFormat("yyD'-'hhmmss'-999999-129-00429-'");
		else
			auditTrailFormat = new SimpleDateFormat("yy'0'D'-'hhmmss'-999999-129-00429-'");

		// SimpleDateFormat normalTimeFormat = new SimpleDateFormat("_HHmmss");
		SimpleDateFormat loggingTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer tablesInSrcNotInDest = new StringBuffer("");
		StringBuffer columnDifferences = new StringBuffer("");
		StringBuffer pKDifferences = new StringBuffer("");
		StringBuffer columnDefinitonDifferences = new StringBuffer("");

		String connectionName = getParamValue(theParameters, "connection"),
				connectionNameDest = getParamValue(theParameters, "destConnection"),
				url = getParamValue(theParameters, "url"),
				service_name = getParamValue(theParameters, "service_name"),
				port = getParamValue(theParameters, "port",
						Settings.defaultPort),
				connMode = getParamValue(theParameters, "connMode",
						"SERVICE_NAME"),
				user = getParamValue(theParameters, "user"),
				password = getParamValue(theParameters, "password"),
				instNr = getParamValue(theParameters, "instNr"),
				destInstNr = getParamValue(theParameters, "destInstNr",
						instNr),
				destLocalCurr = getParamValue(theParameters, "destLocalCurr"),
				// destLocalCurrSwift="",
				replaceFlag = getParamValue(theParameters, "replaceFlag", "0"),
				applyResets = getParamValue(theParameters, "applyResets"),
				exportAllCht = getParamValue(theParameters, "exportAllCht"),
				includeApplications = getParamValue(theParameters, "includeApplications"),
				includeAllClients = getParamValue(theParameters, "includeAllClients"),
				destinationURL = getParamValue(theParameters, "destinationURL"),
				destPort = getParamValue(theParameters, "destPort"),

				destConnMode = getParamValue(theParameters, "destConnMode", "SERVICE_NAME"),
				destinationService_name = getParamValue(theParameters, "destinationService_name",
						service_name),
				destinationUser = getParamValue(theParameters, "destinationUser",
						user),
				destinationPassword = getParamValue(theParameters, "destinationPassword", password),
				moduleDefFile = getFileNameFull(getParamValue(theParameters, "moduleDefFile")),
				recDateIsSysDate = getParamValue(theParameters, "recDateIsSysDate", Settings.TRUE),
				savedFolder = getParamValue(theParameters, "savedFolder",
						Settings.getOutputFolder()),
				auditTrail = getParamValue(theParameters, "audit_trail"),
				oneScriptFile = getParamValue(theParameters, "oneScriptFile", Settings.FALSE),
				allCHTs = getParamValue(theParameters, "allCHTs", Settings.FALSE),
				collectOnlyUsed = getParamValue(theParameters, "collectOnlyUsed", Settings.TRUE),
				allClients = getParamValue(theParameters, "allClients", Settings.FALSE),
				file_prefix = getParamValue(theParameters, "file_prefix", ""),
				ExportHasDataOnly = getParamValue(theParameters, "ExportHasDataOnly", Settings.TRUE),
				query = "q",
				queryDelete = "";
		// String defaultWhereClause=" institution_number like ''"+instNr+"'''";

		if (file_prefix == null)
			file_prefix = "";
		MyDialog.showInformation(
				"\n" + Utils.showDateTime() + ": Process started with the following options: \n" +
						"Source Instance: Host = " + url + " Port= " + port + " " + connMode + " = " + service_name
						+ " User =" + user + "\n"
						+ "Destination Instance: Host = " + destinationURL + " port = " + destPort + " " + destConnMode
						+ " = " + destinationService_name + " User = " + destinationUser + "\n"
						+ "Read from source institution number " + instNr
						+ " and generates export scripts for institution number " + destInstNr + "\n"
						+ "Use the Module Definition as in " + moduleDefFile + "\n"
						+ "Destination local currency: " + destLocalCurr + "\n"
						+ "Include Deletes: " + replaceFlag + "\n"
						+ "Include all CHT records with institution_number = '00000000': " + exportAllCht + "\n"
						+ "Include all Applications: " + includeApplications + "\n"
						+ "Include all Clients: " + includeAllClients + "\n\n");

		String tableInclusionClause = " ( " +
				"		table_name like 'SYS_%' or " +
				"		table_name like 'CHT_%' or " +
				"		table_name like 'CBR_%' or " +
				"		table_name like 'GUI_%' or " +
				"		table_name = 'CAS_CLIENT_ACCOUNT' or " +
				"		table_name = 'CIS_CLIENT_LINKS' or " +
				"		table_name = 'CIS_CLIENT_DETAILS' or " +
				"		table_name = 'CIS_SETTLEMENT_INFORMATION' or " +
				"		table_name = 'CIS_FUNDING_INFORMATION' or " +
				"		table_name = 'CIS_ADDRESSES' or " +
				"		table_name = 'CAS_CYCLE_BOOK_BALANCE' or " +
				"		table_name = 'CIS_INTERCHANGE_DETAILS' or " +
				"		table_name = 'SVC_CLIENT_SERVICE' or " +
				"		table_name = 'SVC_CLIENT_FEES' or " +
				"		table_name like decode(1," + includeApplications + ",'CIS_APPL%','impossible') " +
				"		) " +
				"	and exists (select '' from user_tab_columns where tc.table_name =table_name and column_name='INSTITUTION_NUMBER') "
				+
				"	and table_name not in ('SYS_ACTION_LOG','SYS_PENDING_REPORTS','SYS_SUNDRY_PROCESS') " +
				"and table_name not in (select view_name from user_views) " +
				"and table_name not in (select TABLE_NAME from user_snapshots) " +
				// "and exists (select '' from user_cons_columns a, user_constraints b "+
				// " where a.CONSTRAINT_NAME=b.CONSTRAINT_NAME and CONSTRAINT_TYPE='P' "+
				// " and a.TABLE_NAME=tc.TABLE_NAME and a.COLUMN_NAME='INSTITUTION_NUMBER') " +
				"\n";

		String tableDeletionClause = "("
				+ "table_name like 'SYS_%' or "
				+ "table_name like 'CHT_%' or "
				+ "table_name like 'CBR_%' or "
				+ "table_name like 'GUI_%' or "
				+ "table_name = 'CAS_CLIENT_ACCOUNT' or "
				+ "table_name = 'CIS_CLIENT_LINKS' or "
				+ "table_name = 'CIS_CLIENT_DETAILS' or "
				+ "table_name = 'CIS_SETTLEMENT_INFORMATION' or "
				+ "table_name = 'CIS_FUNDING_INFORMATION' or "
				+ "table_name = 'CAS_CYCLE_BOOK_BALANCE' or "
				+ "table_name = 'CAS_PAYMENT_HISTORY' or "
				+ "table_name = 'CAS_VALUE_DATED_BALANCE' or "
				+ "table_name = 'CIS_ADDRESSES' or "
				+ "table_name = 'CIS_INTERCHANGE_DETAILS' or "
				+ "table_name = 'SVC_CLIENT_SERVICE' or "
				+ "table_name = 'SVC_CLIENT_FEES' or "
				+ "table_name like decode(1,'" + includeApplications + "','CIS_APPL%','impossible') "
				+ (replaceFlag.equals("2") == false ? ""
						: " or table_name like 'INT_ADDEN%' "
								+ "or table_name in ('SCH_PROCESS_LOG','INT_GL_TRANSACTIONS','INT_SUNDRY_TRANSACTIONS','INT_SUNDRY_HISTORY','INT_BATCH_CAPTURE','INT_TRANSACTIONS', "
								+ "				  'INT_FILE_LOG_DETAILS', 'INT_FILE_LOG_STATUS','INT_VISA_VSS_TRANSACTIONS', 'INT_PROCESS_MESSAGE_LOG') ")
				+ ") "
				+ "and table_name not in ('SYS_ACTION_LOG','SYS_PENDING_REPORTS','SYS_SUNDRY_PROCESS') "
				+ " and exists (select '' from user_tab_columns where tc.table_name =table_name and column_name='INSTITUTION_NUMBER') "
				+ "and table_name not in (select view_name from user_views) "
				+ "and table_name not in (select TABLE_NAME from user_snapshots) " +
				"\n";

		Map<String, Map<String, DBTableExport>> modules;
		if (moduleDefFile != null && !moduleDefFile.equals(Settings.DefaultInstitutionExport)) {
			modules = ExportService.loadModuleDefFile(moduleDefFile);
			if ((pane != null && !pane.isRunning) || modules == null || modules.isEmpty()) {
				MyDialog.showException("\nExport terminated with errors\n");
				return listDBQuerySource;
			}
		} else {
			modules = new HashMap<String, Map<String, DBTableExport>>();
			modules.put(Settings.DefaultInstitutionExport, new HashMap<String, DBTableExport>());
		}

		// init query Params
		List<DBParam> queryParams = new LinkedList<DBParam>();
		queryParams.add(new DBParam(Settings.paramInstitution, instNr));
		queryParams.addAll(Utils.getListDBParamsFromListString(Utils.getParamsFromModulePlansDBTableExport(modules)));
		queryParams = DBService.inputQueryParams(queryParams);

		List<DBParam> queryParamsDest = DBService.getQueryParams(queryParams, Settings.paramInstitution, destInstNr);

		try {
			MyDialog.showInformation("\n**********************\n" + Utils.showDateTime()
					+ ": Establishing a read only connection to the source data base\n");
			Connection conn = (connectionName != null && !connectionName.isEmpty())
					? DBService.getConnection(connectionName, password)
					: DBService.getNewConnection(connectionName, url, service_name, user, password, port, connMode);

			Connection connDest = (connectionNameDest != null &&
					!connectionNameDest.isEmpty())
							? DBService.getConnection(connectionNameDest, destinationPassword)
							: DBService.getNewConnection(connectionNameDest, destinationURL, destinationService_name,
									destinationUser,
									destinationPassword, destPort, destConnMode);

			if (connDest == null) {
				connDest = conn;
				connectionNameDest = connectionName;
			}

			for (String moduleName : modules.keySet()) {// The big loop through the modules
				if (pane != null && !pane.isRunning)
					break;
				Map<String, DBTableExport> moduleDef = modules.get(moduleName);
				// sort tables, put children table before parent table

				DBQuery modulePlan = new DBQuery("", connectionName + "." + instNr + " : " + moduleName); // add to end
																											// of loop
																											// module
				DBQuery modulePlanDest = new DBQuery("", connectionNameDest + "." + destInstNr + " : " + moduleName);

				MyDialog.showInformation("\n\n***" + "Processing the Module Export Definition " + moduleName + "***\n");
				if (moduleDefFile != null && !moduleDefFile.equals(Settings.DefaultInstitutionExport)) {
					// if the there is a module definition file then the table selection restricted
					// to the tables defined there
					tableInclusionClause = "";
					for (DBTableExport table : moduleDef.values()) {// loop through the table names as they are
																	// specified in
						// table extract definition
						tableInclusionClause = tableInclusionClause + " (table_name like '"
								+ table.getTableName().replaceAll(Pattern.quote("*"), "%").trim() + "'";

						List<String> columnNames = new ArrayList<String>();
						if (table.getColumnName() != null && !table.getColumnName().isEmpty()) {
							String[] arr = table.getColumnName().split(",");
							for (String item : arr)
								columnNames.add(item);
						} else if (table.getColumnList() != null && table.getColumnList().size() > 0) {
							// columnNames = table.getColumnList();
						}
						// loop though the restriction by column mentioned in the table extract
						// definition
						for (String columnName : columnNames) {
							if (columnName != null && columnName.length() > 0) {
								tableInclusionClause = tableInclusionClause +
										" and " +
										(columnName.trim().toUpperCase().replaceFirst(Pattern.quote("^"), "NOT ")
												.startsWith("NOT ") ? "NOT " : "")
										+
										"exists (select '' from user_tab_columns tc2 where tc2.table_name"
										+ Settings.tempSQLNULL + " = table_name and tc2.column_name " // will replace
																										// tempSQLNULL =
																										// '' in future
										+
										"like '"
										+ columnName.trim().replaceAll(Pattern.quote("*"), "%").toUpperCase()
												.replaceFirst("NOT ", "").replaceFirst(Pattern.quote("^"), "")
										+ "')";
							}
						}
						tableInclusionClause = tableInclusionClause + ") or ";
					}
					;
					if (tableInclusionClause.length() >= 3)
						tableInclusionClause = tableInclusionClause.substring(0, tableInclusionClause.length() - 3);
					tableDeletionClause = tableInclusionClause;
				}

				// get the source and destination schema
				DBSchema srcSchema = DBSchema.getDBSchema(conn, connectionName,
						tableDeletionClause);
				if (moduleDefFile != null && !moduleDefFile.equals(Settings.DefaultInstitutionExport)) {
					String inconsitantTableDef = "";
					for (DBTableExport tableExport : moduleDef.values()) {
						String moduleTable = tableExport.getTableName();
						// the eventually provided where clauses are transported from the module
						// definition in source schema object
						for (String realTable : srcSchema.getTableList().keySet()) {
							if (Pattern.matches(moduleTable.replaceAll("%", ".*").replaceAll("\\*", ".*"), realTable)) {
								if (tableExport.getColumnList() == null ||
										tableExport.getColumnList().isEmpty() ||
										srcSchema.getTableList().get(realTable).containsColumnList(
												tableExport.getColumnList())) {
									if (!srcSchema.isCachedData
											&& srcSchema.getTableList().get(realTable).getLinkedTables() != null) {
										inconsitantTableDef = inconsitantTableDef + "The table " + realTable
												+ " is matching with "
												+ srcSchema.getTableList().get(realTable).getLinkedTables().get(0)
														.getTableName()
												+ " "
												+ srcSchema.getTableList().get(realTable).getLinkedTables().get(0)
														.getColumnList()
												+ " and also with " + moduleTable + " "
												+ tableExport.getColumnList() + "\n";
									}
									// sync between srcSchema and moduleDef
									srcSchema.getTableList().get(realTable).cloneFrom(tableExport);
									tableExport.setColumnList(srcSchema.getTableList().get(realTable).getColumnList());
								}
							}
						}
						if (tableExport.getLinkedTables() != null
								&& tableExport.getLinkedTables().size() > 0) {
						} else {
							tableExport.setExistsInSrc(false);
						}

						if (!srcSchema.getTableList().containsKey(tableExport.getId()))
							srcSchema.getTableList().put(tableExport.getId(), tableExport);
					}

					if (inconsitantTableDef.length() > 0) {
						MyDialog.showException("Inconsitent Table Definition!\n" + inconsitantTableDef);
						MyDialog.showException("\nExport terminated with errors");
						listDBQuerySource.addAll(listDBQueryDest);
						return listDBQuerySource;
					}
				}

				DBSchema destSchema = null;
				if (connDest != null && connDest.isValid(0)) {
					destSchema = DBSchema.getDBSchema(connDest, connectionNameDest, tableDeletionClause);
					// System.out.println(destSchema+"\n\n"+destSchema.getTotals());
				} else// for the default inst export only the tables having the
						// column_name='INSTITUTION_NUMBER' are considered
				{
					tableDeletionClause = tableDeletionClause + " and column_name='INSTITUTION_NUMBER' ";
				}
				try {

					// ge
					StringBuilder fileBuilder = new StringBuilder();
					String srcPostingDate = "";
					String srcLocalCurr = "XXX";
					String recordDate = "";
					String locCurr = "";
					String destInstallationNumber = "";
					if (connDest != null && connDest.isValid(0)) {
						try {
							Statement getDestInstallationNumber = connDest.createStatement();
							ResultSet getDestInstNumber = getDestInstallationNumber
									.executeQuery("select distinct INSTALLATION_NUMBER from sys_institution_licence");

							getDestInstNumber.next();
							destInstallationNumber = getDestInstNumber.getString("INSTALLATION_NUMBER");
							if (getDestInstNumber.next() == true)
								// if there are more than one distinct installation numbers in the destination
								// instance then there will be trouble to initialize SOA processes and
								// in also webGUI.
								MyDialog.showInformation("\n" + Utils.showDateTime()
										+ ": Data inconsistency found in the destination Institution!!\nMultiple installation numbers found in sys_institution_licence.\n\n");

							getDestInstNumber.close();
							getDestInstallationNumber.close();
						} catch (SQLException sq) {
							MyDialog.showException(sq, "SQL error:  "
									+ "select distinct INSTALLATION_NUMBER from sys_institution_licence");
						}
					}

					// get local currency
					if (destLocalCurr == null || destLocalCurr.length() == 0) {
						destLocalCurr = RS2Util.getLocalCurrency(connDest, destInstNr); // if the destination local
																						// currency was not mentioned by
																						// the
						// user then it is set to be equal the local currency of the
						// source instance
						MyDialog.showInformation(
								"\n No local currency was specified!. The local currency of the source instance will be used, and this is: "
										+ srcLocalCurr + "\n\n");
					}
					recordDate = RS2Util.getSystemDate(conn);
					srcPostingDate = RS2Util.getPostingDate(conn, instNr, "129");

					int insertsTot = 0;
					int insertsTable = 0;

					StringBuffer valueList = new StringBuffer();
					List<String> columnList = new ArrayList<String>();
					StringBuffer tableInserts = new StringBuffer();

					Map<String, StringBuilder> tableInsertStatements = new LinkedHashMap<String, StringBuilder>();
					Map<String, StringBuilder> tempInsertStatements = new LinkedHashMap<String, StringBuilder>();
					Map<String, StringBuilder> tableDeleteStatements = new LinkedHashMap<String, StringBuilder>();
					Map<String, StringBuilder> tableSelectStatements = new LinkedHashMap<String, StringBuilder>();
					Map<String, String> baseDeleteStatements = new LinkedHashMap<String, String>(); // table, sql: table
																									// is unqiue
					Map<String, String> baseSelectStatements = new LinkedHashMap<String, String>(); // table, sql: table
																									// is unqiue

					Map<String, Map<String, String>> tableDeclareVarsList = new LinkedHashMap<String, Map<String, String>>();
					Map<String, String> tableKeys = new LinkedHashMap<String, String>();

					Statement stmnt = conn.createStatement();

					queryDelete = "Select "
							+ "distinct CASE WHEN Table_name like 'CHT_%' and 1=" + exportAllCht + " THEN "
							+ "'delete from '||table_name||' where institution_number = '||chr(39)||'00000000'||chr(39)||' or institution_number like '||chr(39)||'"
							+ destInstNr.trim() + "'||chr(39)||';' "
							+ " ELSE 'delete from '||table_name||' where institution_number like '||chr(39)||'"
							+ destInstNr.trim() + "'||chr(39)||';' end as DELETE_STMNT, TABLE_NAME "
							+ "from "
							+ "user_tab_columns tc "
							+ "where " + tableDeletionClause
							+ " order by decode (table_name,'SYS_INSTITUTION_LICENCE','ZZZZ','CIS_CLIENT_DETAILS','ZZZY','CBR_ISO_BUSS_GROUPS','ZZZX','CBR_SETTLEMENT_SCHEMES','AAAA',table_name) asc ";
					ResultSet rs = DBService.executeQuery(stmnt, queryDelete); // stmnt.executeQuery(queryDelete);

					// delete statements
					while (rs.next()) {
						String tableName = "";
						String deleteStatement = "";
						tableName = rs.getString("TABLE_NAME");
						deleteStatement = rs.getString("DELETE_STMNT");
						baseDeleteStatements.put(tableName, deleteStatement);

						if (destSchema == null || destSchema.getTableList().containsKey(tableName)) {
							baseDeleteStatements.put(tableName, deleteStatement);
						} else {
							tablesInSrcNotInDest.append(tableName + "\n");
						}
					}

					rs.close();

					query = "select distinct tc.TABLE_NAME," +
							"'select '||" +
							"LISTAGG (tc.column_name ,',') WITHIN GROUP (ORDER BY COLUMN_ID)|| ' from '|| tc.table_name "
							+
							"||' a where institution_number like ''" + instNr + "'''" +
							"|| CASE " +
							"WHEN " + includeAllClients
							+ "!=1 and table_name  = 'CIS_CLIENT_DETAILS' THEN ' and (client_type =''003'' or record_type =''004'' or client_number=institution_number or client_number like ''999999%'') "
							+
							"'WHEN " + includeAllClients
							+ "!=1 and table_name  in ('CAS_CLIENT_ACCOUNT','CIS_CLIENT_LINKS','CIS_SETTLEMENT_INFORMATION','CIS_ADDRESSES','SVC_CLIENT_SERVICE') THEN 'and (institution_number, client_number) in (select institution_number, client_number from cis_client_details where client_type =''003'' or record_type =''004'' or client_number=institution_number or client_number like ''999999%'' )' "
							+
							"WHEN " + includeAllClients
							+ "!=1 and table_name ='CAS_CYCLE_BOOK_BALANCE' THEN ' and processing_status=''004'' and (institution_number, substr(acct_number,1,8)) in (select institution_number, client_number from cis_client_details where client_type = ''003'' or record_type = ''004'' or client_number=institution_number or client_number like ''999999%'' )' "
							+
							"WHEN Table_name like 'CHT_%' and '1'='" + exportAllCht
							+ "' THEN ' or institution_number =''00000000''' " +
							// "WHEN table_name like 'CBR_CURRENCY_RATES%' THEN 'and effective_date =(select
							// max(effective_date) from cbr_currency_rates where INSTITUTION_NUMBER
							// =a.INSTITUTION_NUMBER and FX_RATE_CATEGORY=a.FX_RATE_CATEGORY and
							// CURRENCY=a.CURRENCY)' " +
							"ELSE '' end " +
							// "||' order by '|| "+
							// "substr( substr( LISTAGG (tc.column_name ,',') WITHIN GROUP (ORDER BY
							// COLUMN_ID),1,1000), 1,instr (substr( LISTAGG (tc.column_name ,',') WITHIN
							// GROUP (ORDER BY COLUMN_ID),1,1000) , ',' ,-1,1) -1) "+
							"as query " +
							"from user_tab_columns tc " +
							"where " +
							tableInclusionClause + " " +
							"group by tc.table_name " +
							"order by decode (tc.table_name,'SYS_INSTITUTION_LICENCE','ZZZZ','CIS_CLIENT_DETAILS','ZZZY','CBR_ISO_BUSS_GROUPS','ZZZX','CBR_SETTLEMENT_SCHEMES','AAAA',tc.table_name) desc ";

					ResultSet rs1 = DBService.executeQuery(stmnt, query); // stmnt.executeQuery(query);
					while (rs1.next()) {
						String tableName = rs1.getString(1);
						String selectStatement = rs1.getString(2);
						baseSelectStatements.put(tableName, selectStatement);
					}
					rs1.close();

					// insert statement
					int tableIndex = 0;
					for (Map.Entry<String, DBTableExport> entry : moduleDef.entrySet()) {
						List<String> tables = new LinkedList<String>();

						if (entry.getKey().contains(Settings.operatorAll)
								|| entry.getKey().contains(Settings.operatorAll1)) // if many tables
						{
							for (String table : srcSchema.getTableNames()) {
								if (!table.contains(Settings.operatorAll) && !table.contains(
										Settings.operatorAll1)
										&& table.contains(entry.getKey().replace(Settings.operatorAll, "").replace(
												Settings.operatorAll1, ""))) {
									tables.add(table);
								}
							}
						} else {
							tables.add(entry.getKey());
						}
						for (String table : tables) {
							tableIndex += 1;
							String currTableKey = table;
							DBTableExport currTableDef = entry.getValue();
							currTableDef.setTableName(table);
							String currTableName = table;

							Set<DBTableExport> currTableChildren = getDBTableChildren(currTableDef, moduleDef);
							String childrenTempInsertKey = "";
							String insertStatementDest = "";
							String insertStatement = "";
							String insertsSqlPrefix = "";
							String exportColumnValue = "";
							String currColumnValue = "";
							String childInsertKey = "";

							Map<String, String> declareVarsList = new LinkedHashMap<String, String>();

							Connection currConn = conn;
							if (currTableDef.getConnectionName() != null && !currTableDef.getConnectionName().isEmpty())
								currConn = DBService.getConnection(currTableDef.getConnectionName(), password); // each
																												// tableDef
																												// can
																												// define
																												// its
																												// own
																												// connection
							if (currConn == null)
								currConn = conn;

							Statement stmntExpTable = currConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							Statement stmntExpTableDest = connDest.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);

							if (destSchema == null || destSchema.getTableList().containsKey(currTableName)) {
								try {

									if (currTableDef.getSqlQuery() != null && !currTableDef.getSqlQuery().isEmpty()) {
										insertStatement = currTableDef.getSqlQuery();
									} else {
										insertStatement = baseSelectStatements.get(currTableName);
										if (moduleDefFile != null
												&& !moduleDefFile.equals(Settings.DefaultInstitutionExport)
												&& insertStatement != null && !insertStatement.isEmpty()) {

											// modify insertStatement and add where condition
											insertStatement = insertStatement.substring(0,
													insertStatement.toLowerCase().indexOf("where"))
													+ " "
													+ currTableDef.getWhereClause(
															queryParams,
															exportAllCht, true); // if
											// specific
											// inst
											// is
											// included
											// in
											// source
											// inst
											// whereClause
											// then
											// respect
											// it
										}
									}
									insertStatement += getMaxEffectiveDateClause(srcPostingDate, currTableDef);
									insertStatement += getOrderBy(currTableDef);

									insertStatement = DBService.applyParams(insertStatement, queryParams);

									dataSelectionQueries.add(insertStatement);

									// show result in tree view
									// Source tree
									DBQuery plan = new DBQuery(insertStatement, currTableName);
									plan.setQueryTitle(currTableDef.getWhereClause(queryParams));

									ResultSet rsTableData = DBService.executeQuery(stmntExpTable, insertStatement); // stmntExpTable.executeQuery(insertStatement);//
																													// select-Query
																													// wird
																													// ausgef�hrt
									plan.setData(rsTableData);
									if (ExportHasDataOnly.equalsIgnoreCase(Settings.FALSE)
											|| plan.getRowsCount() > 0) {
										listDBQuerySource.add(plan);
										if (modulePlan != null)
											plan.setSuperSQLQuery(modulePlan);
									}

									// Destination Tree
									insertStatementDest = DBService.replaceParamValueInSql(insertStatement, instNr,
											destInstNr);
									DBQuery planDest = new DBQuery(insertStatementDest, currTableName);
									planDest.setQueryTitle(currTableDef.getWhereClause(queryParamsDest));

									if (ExportHasDataOnly.equalsIgnoreCase(Settings.FALSE)
											|| planDest.getRowsCount() > 0) {
										listDBQueryDest.add(planDest);
										if (modulePlanDest != null)
											planDest.setSuperSQLQuery(modulePlanDest);
									}

									ResultSetMetaData rsMetaData = rsTableData.getMetaData();// die column names der
																								// select
																								// Query
									int totalCols = rsMetaData.getColumnCount();
									for (int t = 1; t <= totalCols; t++)
										if (destSchema == null || destSchema.getTableList().get(currTableName)
												.getColumnList().contains(rsMetaData.getColumnName(t))) // die column
																										// muss
																										// auch in der
																										// destination
																										// instance
																										// existieren
											columnList.add(rsMetaData.getColumnName(t));

									totalCols = columnList.size();

									String destColumnsStr = String.join(",", Utils.convertToStringArray(columnList));
									insertStatementDest = insertStatementDest.replace(insertStatementDest.substring(0,
											insertStatementDest.toLowerCase().indexOf(" from ")),
											"SELECT " + destColumnsStr);
									ResultSet rsTableDataDest = DBService.executeQuery(stmntExpTableDest,
											insertStatementDest); //
									planDest.setData(rsTableDataDest);

									// System.out.println(columnList);
									insertsTable = 0;

									// start building insert statement for each src Tabledata
									while (rsTableData.next()) {
										insertsTable++;
										insertsTot++;
										childInsertKey = "";
										childrenTempInsertKey = "";

										valueList.append("(");
										for (String currColumn : columnList) {
											try {
												if (rsTableData.getString(currColumn) != null) {
													currColumnValue = rsTableData.getString(currColumn);

													// store children data group by getChildrenField() if any
													// <export table="CBR_ACCT_RULES_INSTRUCTIONS"
													// parent="CBR_ACCT_RULES_GROUP" childField="RECORD_ID_NUMBER">
													if (currTableDef.isChildren() && currTableDef.getChildrenField()
															.equalsIgnoreCase(currColumn)) {
														childrenTempInsertKey = currTableKey + ":" + currColumn + ":"
																+ currColumnValue;
														// LogManager.logger.debug("childrenTempInsertKey : " +
														// childrenTempInsertKey);
													}

													// <export table="CBR_ACCT_RULES_GROUP"
													// parentField="RECORD_ID_NUMBER">
													if (currTableDef.getParentField().equalsIgnoreCase(currColumn)) {
														childInsertKey = currTableDef.getParentField() + ":"
																+ currColumnValue;
														// LogManager.logger.debug("childInsertKey : " +
														// childInsertKey);
													}

													exportColumnValue = currColumnValue;

													if (currTableDef.getReplaceMapping() != null) {
														for (String replCol : currTableDef.getReplaceMapping()
																.keySet()) {
															if (Pattern.matches(replCol, currColumn)) {
																for (Map.Entry<String, String> entry1 : currTableDef
																		.getReplaceMapping().get(replCol).entrySet()) {
																	String oldString = entry1.getKey(); // v_seq_num
																	String newString = entry1.getValue(); // BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)

																	// if replace apply for all values, then can define
																	// REPLACEVALUE as Column,,New Value

																	if (oldString != null && !oldString.isEmpty()
																			&& oldString
																					.equalsIgnoreCase("v_seq_num")) {

																		exportColumnValue = Settings.dataSeperator
																				+ oldString;
																	} else if (oldString != null && !oldString.isEmpty()
																			&& oldString.startsWith("@")) {
																		oldString = oldString.substring(1,
																				oldString.length());
																		exportColumnValue = Settings.dataSeperator
																				+ oldString;
																	} else if (oldString != null && !oldString.isEmpty()
																			&& newString.isEmpty()) {
																		exportColumnValue = oldString;
																	} else if (oldString == null || oldString.isEmpty()
																			|| oldString.equals("%")
																			|| oldString.equals("*")
																			|| oldString.equalsIgnoreCase("all")) {
																		exportColumnValue = newString;
																	} else if (Pattern.matches(oldString,
																			exportColumnValue)) {
																		exportColumnValue = exportColumnValue
																				.replaceAll(oldString, newString);
																	}

																	if (exportColumnValue
																			.startsWith(Settings.dataSeperator)) {
																		insertsSqlPrefix = oldString + " := "
																				+ newString
																				+ ";"; // v_seq_num
																						// :=
																						// BW_CODE_LIBRARY.GETNEXTSEQNUMBER
																						// ('015',1);
																		if (!childInsertKey.isEmpty()) {
																			insertsSqlPrefix = "-- " + childInsertKey
																					+ "\n"
																					+ insertsSqlPrefix;
																		}
																		declareVarsList.put(oldString,
																				insertsSqlPrefix);

																	} else if (exportColumnValue.startsWith("[")
																			&& exportColumnValue.endsWith("]")) {
																		exportColumnValue = Settings.dataSeperator
																				+ exportColumnValue.substring(1,
																						exportColumnValue.length() - 1);
																	} else if (exportColumnValue.startsWith("@")) {
																		exportColumnValue = Settings.dataSeperator
																				+ exportColumnValue.substring(1,
																						exportColumnValue.length());
																	} else if (exportColumnValue.contains("'")) {
																		exportColumnValue = Settings.dataSeperator
																				+ exportColumnValue;
																	}
																	break;

																}
															}
														}
													}

													if (currColumn.contains("CLIENT_NUMBER")
															&& exportColumnValue.contains(instNr))
														exportColumnValue = destInstNr;
													else if (currColumn.contains("INSTITUTION")) {
														if (exportColumnValue.contains(instNr))
															exportColumnValue = exportColumnValue.replace(instNr,
																	destInstNr);
														else
															exportColumnValue = destInstNr;
													} else if ((currColumn.contains("ACCT_NUMBER")
															|| currColumn.contains("ACCOUNT"))
															&& exportColumnValue.startsWith(instNr))
														exportColumnValue = exportColumnValue.replace(instNr,
																destInstNr);
													else if (currTableName.equals("SYS_CONFIGURATION")
															&& currColumn.contains("CONFIG_VALUE")
															&& exportColumnValue.contains(srcLocalCurr))
														exportColumnValue = exportColumnValue.replace(srcLocalCurr,
																destLocalCurr);
													if (currTableName.equals("SYS_INSTITUTION_LICENCE")) {
														if (currColumn.equals("INSTITUTION_ID"))
															exportColumnValue = Integer.valueOf(destInstNr).toString();
														if (currColumn.equals("INSTALLATION_NUMBER")
																&& destInstallationNumber != "")
															exportColumnValue = destInstallationNumber;
													}

													// replace AUDIT_TRAIL
													if (currColumn.equals("AUDIT_TRAIL")) {
														currDate = new Date();
														if (auditTrail != null && !auditTrail.isEmpty())
															exportColumnValue = auditTrail;
														else
															exportColumnValue = auditTrailFormat.format(currDate) +
																	String.format("%05d",
																			(insertsTable + Integer.parseInt(String
																					.valueOf(currDate.getTime())
																					.substring(
																							String.valueOf(
																									(currDate
																											.getTime()))
																									.length() - 5))));
													} else if (currColumn.equals("RECORD_DATE")
															&& recDateIsSysDate.equals("1"))
														exportColumnValue = recordDate;
													else if (currColumn.contains("CURRENCY")
															&& exportColumnValue.equals(srcLocalCurr)
															&& !currTableName.equals("CBR_CURRENCY_RATES")
															&& !currTableName.contains("CBR_FX_"))
														exportColumnValue = destLocalCurr;

													if (exportColumnValue.contains("'"))
														exportColumnValue = exportColumnValue.replace("'", "''");

													// if exportColumnValue is a variable, does not need quote of the
													// string
													if (exportColumnValue.startsWith(Settings.dataSeperator)
															|| exportColumnValue.equalsIgnoreCase("null")) {
														exportColumnValue = exportColumnValue
																.replace(Settings.dataSeperator, "");
														valueList.append(exportColumnValue)
																.append(Settings.stringSeperator);
													} else {
														exportColumnValue = exportColumnValue.replaceAll("'", "")
																.replaceAll("\"", "");
														valueList.append("'" + exportColumnValue + "'")
																.append(Settings.stringSeperator);
													}
												} else
													valueList.append("null").append(Settings.stringSeperator);
											} catch (SQLException sq) {
												MyDialog.showException(sq, "SQL error.  " + sq + "\n"
														+ currColumn + "<-->" + rsTableData.getString(currColumn)
														+ " in "
														+ currTableKey + "\n");
											}
										} // end loop

										valueList.replace(valueList.length() - Settings.stringSeperator.length(),
												valueList.length(), ")").append(";");
										;

										// if tableInserts is already to long !!
										if (tableInserts.length() > 400000) {
											String tableKey = String.valueOf(tableIndex); // String tableKey =
																							// currTableName;
											tableInsertStatements.put(tableKey,
													!tableInsertStatements.containsKey(tableKey)
															? new StringBuilder(tableInserts.toString() + "\n")
															: tableInsertStatements.get(tableKey)
																	.append(tableInserts.toString() + "\n"));
											tableInserts.setLength(0);
										}

										if (!insertsSqlPrefix.isEmpty()) {
											tableInserts.append(insertsSqlPrefix).append("\n");
											insertsSqlPrefix = "";
										}

										String insertSql = "Insert into " + currTableName
												+ "("
												+ columnList.toString().substring(1).substring(0,
														columnList.toString().lastIndexOf("]") - 1)
												+ ")" + " Values "
												+ valueList.toString() + "\n";
										tableInserts.append(insertSql);

										// if current table is children --> group by Key
										if (currTableDef.isChildren()) {
											if (childrenTempInsertKey != null && !childrenTempInsertKey.isEmpty()) {
												tempInsertStatements.put(childrenTempInsertKey,
														!tempInsertStatements.containsKey(childrenTempInsertKey)
																? new StringBuilder(insertSql)
																: tempInsertStatements.get(childrenTempInsertKey)
																		.append(insertSql));
											}
										}

										// if has children
										if (currTableChildren != null && currTableChildren.size() > 0) {
											// System.out.println(currTableChildren);
											for (DBTableExport currTableChild : currTableChildren) {
												String childKey = currTableChild.getId() + ":" + childInsertKey;
												// LogManager.logger.debug("childKey : " + childKey);
												if (tempInsertStatements.containsKey(childKey))
													tableInserts.append(tempInsertStatements.get(childKey))
															.append("\n");
											}
										}

										valueList.setLength(0);
										; // clear valueList
									} // end src data loop
									columnList.clear();

									stmntExpTable.close();
									stmntExpTableDest.close();
								} catch (SQLException sq) {
									MyDialog.showException(sq, insertStatement + ";" + insertStatementDest);
								}
								String tableKey = String.valueOf(tableIndex);
								tableInsertStatements.put(tableKey, !tableInsertStatements.containsKey(tableKey)
										? new StringBuilder(tableInserts.toString() + "\n")
										: tableInsertStatements.get(tableKey).append(tableInserts.toString() + "\n"));

								tableInserts.setLength(0);
								tableDeclareVarsList.put(tableKey, declareVarsList);

								if (moduleDefFile != null && !moduleDefFile.equals(Settings.DefaultInstitutionExport)) {
									String deleteStatement = baseDeleteStatements.get(currTableName);
									if (deleteStatement != null) {
										// modify deleteStatement and add where condition
										deleteStatement = deleteStatement.substring(0,
												deleteStatement.toLowerCase().indexOf("where"))
												+ " "
												+ srcSchema.getTableList().get(currTableKey).getWhereClause(
														queryParamsDest,
														exportAllCht, false)
												+ ";";
										String selectStatement = deleteStatement.replace("delete ", "select * ");
										if (selectStatement.endsWith(";"))
											selectStatement = selectStatement.substring(0,
													selectStatement.length() - 1);
										selectStatement = selectStatement + " order by 1;";
										if (!replaceFlag.equals("0")) { // if include delete statements in export file
											tableDeleteStatements.put(tableKey,
													!tableDeleteStatements.containsKey(tableKey)
															? new StringBuilder(deleteStatement + "\n")
															: tableDeleteStatements.get(tableKey)
																	.append(deleteStatement + "\n"));
										}
										tableSelectStatements.put(tableKey, !tableSelectStatements.containsKey(tableKey)
												? new StringBuilder(selectStatement + "\n")
												: tableSelectStatements.get(tableKey).append(selectStatement + "\n"));
									}
								}

								tableKeys.put(tableKey, currTableKey);

								srcSchema.getTableList().get(currTableKey).setNumberOfRows(insertsTable);
								// MyDialog.showInformation("...Total inserts " + insertsTot + ", " +
								// currTableName + " exported " + insertsTable + " Records\n");
							}
						}
					}

					if ((moduleDefFile == null || moduleDefFile.equals(Settings.DefaultInstitutionExport))
							&& !applyResets.equals("0")) { // no reset if a module file was selected and if no
															// transactions exported
						// Reseting cycle book balance
						fileBuilder.append("UPDATE CAS_CYCLE_BOOK_BALANCE SET " +
								"CURRENT_BALANCE = 0," +
								"BEGIN_BALANCE = 0," +
								"DR_BALANCE_CASH = 0," +
								"DR_BALANCE_RETAIL = 0," +
								"DR_BALANCE_INTEREST = 0," +
								"DR_BALANCE_CHARGES = 0," +
								"CR_BALANCE_PAYMENTS = 0," +
								"CR_BALANCE_REFUNDS = 0," +
								"CR_BALANCE_BONUS = 0," +
								"CR_BALANCE_INTEREST = 0," +
								"NUMBER_TRAN_CHRG_DR = 0," +
								"NUMBER_TRAN_CHRG_CR = 0 " +
								"WHERE INSTITUTION_NUMBER = '" + destInstNr + "' " +
								"AND PROCESSING_STATUS = '004';\n");
						// Reset account fees
						fileBuilder.append("update cas_client_account " +
								"set last_fee_date = null, last_settlement_date=null, LAST_STATEMENT_DATE = null, LAST_STATEMENT_NUMBER = null "
								+
								"where institution_number = '" + destInstNr + "';\n");
					}
					if ((moduleDefFile == null || moduleDefFile.equals(Settings.DefaultInstitutionExport))
							&& applyResets.equals("2")) {
						// reset File number
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000001' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '001'; --reset File number\n");

						// reset GL File number
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000001' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '006'; --reset GL File number\n");

						// reset Transaction slip no.
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000000001' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '019'; --reset Transaction slip no.\n");

						// rest GL tran slip number
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000000000' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '038'; --reset GL tran slip number\n");

						// reset Batch Tran Slip No.
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000000001' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '042'; --reset Batch Tran Slip No.\n");

						// reset Sundry History
						fileBuilder.append(
								"UPDATE CBR_SEQUENCE_NUMBERS SET SEQUENCE_VALUE = '00000000001' WHERE INSTITUTION_NUMBER = '"
										+ destInstNr + "' AND SEQUENCE_ID = '078'; --reset Sundry History\n");
					}

					// save export file
					StringBuilder resultContent = new StringBuilder();

					String exportScriptFileName = "";
					String[] tmp = file_prefix.split("_");
					int fileIndex = 0;
					if (tmp.length > 1) {
						try {
							fileIndex = Integer.parseInt(tmp[tmp.length - 1]);
							file_prefix = String.join("_", Arrays.copyOf(tmp, tmp.length - 1));
						} catch (NumberFormatException ex) {
							// error handling
						}
					}

					// start save File
					if (folder == null || folder.isEmpty())
						folder = getSaveFolder(theParameters);
					savedFolder = Utils.createFolder(folder + "\\" + moduleName + "_scripts");

					if (oneScriptFile.equalsIgnoreCase(Settings.TRUE)) {
						int i = 0;
						for (Map.Entry<String, StringBuilder> insertStatement : tableInsertStatements.entrySet()) {
							i += 1;
							String tableKey = insertStatement.getKey();
							String currTableKey = tableKeys.containsKey(tableKey) ? tableKeys.get(tableKey) : "";
							if (currTableKey.isEmpty())
								continue;
							DBTableExport currTableDef = findDBTableExportFromModule(moduleDef, currTableKey);
							if (currTableDef == null) {
								LogManager.getLogger()
										.debug("[Export service] TableExport for " + currTableKey + " is null.");
								continue;
							}
							String currTableName = currTableDef.getTableName();
							Set<DBTableExport> currTableChildren = getDBTableChildren(currTableDef, moduleDef);

							DBQuery currDBQuery = DBService.getDBQueryByName(listDBQuerySource, currTableName);
							// if is children or no data then not gegenrate files
							if (!currTableDef.getParentName().isEmpty() ||
									(ExportHasDataOnly.equalsIgnoreCase(Settings.TRUE)
											&& (currDBQuery == null || currDBQuery.getRowsCount() == 0)))
								// file (included in parent file). Skip this
								continue;

							fileBuilder = new StringBuilder();
							fileBuilder.append("REM BAK NONE \n");
							fileBuilder.append("REM INSERTING into ").append(currTableName).append("\n");
							fileBuilder.append("SET DEFINE OFF; \n\n");

							fileBuilder.append("-- table " + currTableName.toUpperCase() + "\n");

							// select statement
							if (tableSelectStatements.containsKey(tableKey))
								fileBuilder.append(tableSelectStatements.get(tableKey).toString()).append("\n");

							// delete statement: if no insert then should not delete
							String deletePrefix = "";
							if (insertStatement.getValue().toString().trim().isEmpty()) {
								deletePrefix = "-- "; // if no insert then should not delete
							}

							// try to delete child record firsts (if any)
							if (currTableChildren != null && currTableChildren.size() > 0) {
								for (DBTableExport currTableChild : currTableChildren) {
									for (Map.Entry<String, String> tableEntry : tableKeys.entrySet()) {
										if (tableEntry.getValue().equalsIgnoreCase(currTableChild.getId())) {
											String childTableKey = tableEntry.getKey();
											String childTableName = tableEntry.getValue();
											fileBuilder.append("-- Child table " + childTableName.toUpperCase() + "\n");

											if (tableDeleteStatements.containsKey(childTableKey))
												fileBuilder.append(deletePrefix)
														.append(tableDeleteStatements.get(childTableKey).toString())
														.append("\n");
										}
									}
								}
							}

							if (tableDeleteStatements.containsKey(tableKey))
								fileBuilder.append(deletePrefix).append(tableDeleteStatements.get(tableKey).toString())
										.append("\n");

							Map<String, String> declareVarsList = tableDeclareVarsList.get(tableKey);
							// start insert
							if (declareVarsList.size() > 0) {
								fileBuilder.append("DECLARE \n");
								for (Map.Entry<String, String> declareVar : declareVarsList.entrySet()) {
									fileBuilder.append(declareVar.getKey()).append(" VARCHAR2(18);\n");
								}
								fileBuilder.append("V_INIT_RETURN PLS_INTEGER;\n\n");
								fileBuilder.append("BEGIN\n");
								fileBuilder.append("-- 'INSTITUTION NUMBER','STATION GROUP','USER NUMBER'\n");
								fileBuilder.append("BW_PRC_RES.INITGLOBALVARS ('").append(destInstNr)
										.append("','129','999999', V_INIT_RETURN);\n\n");
							}

							// insert sqls
							fileBuilder.append(insertStatement.getValue().toString());

							if (declareVarsList.size() > 0) {
								fileBuilder.append("COMMIT;\n");
								fileBuilder.append("END;\n").append("/").append("\n");
								declareVarsList.clear();
							} else {
								fileBuilder.append("COMMIT;\n");
							}

							if (tableSelectStatements.containsKey(tableKey))
								fileBuilder.append(tableSelectStatements.get(tableKey).toString()).append("\n");

							String file_postfix = currTableDef != null ? currTableDef.getFilePostFix() : "";
							if (file_postfix != null && !file_postfix.isEmpty())
								file_postfix = "_" + file_postfix;

							exportScriptFileName = (file_prefix.isEmpty() ? moduleName.toLowerCase() : file_prefix)
									+ "_" + Utils.leftPad(String.valueOf(fileIndex + i), 3, "0") + "_insert_"
									+ currTableName.toLowerCase() + file_postfix + ".sql";
							Utils.saveFile(savedFolder + "\\" + exportScriptFileName, fileBuilder.toString());

							resultContent.append("-- " + savedFolder + "\\" + exportScriptFileName + " \n")
									.append(fileBuilder.toString()).append("\n\n");

							// rollback -> delete
							fileBuilder.setLength(0);
							;
							fileBuilder.append("REM BAK NONE \n");

							fileBuilder.append("-- " + currTableName + "\n");
							// try to delete child record firsts (if any)
							if (currTableChildren != null && currTableChildren.size() > 0) {
								for (DBTableExport currTableChild : currTableChildren) {
									for (Map.Entry<String, String> tableEntry : tableKeys.entrySet()) {
										if (tableEntry.getValue().equalsIgnoreCase(currTableChild.getId())) {
											String childTableKey = tableEntry.getKey();
											String childTableName = tableEntry.getValue();
											fileBuilder.append("-- Child table " + childTableName.toUpperCase() + "\n");

											if (tableDeleteStatements.containsKey(childTableKey))
												fileBuilder.append(deletePrefix)
														.append(tableDeleteStatements.get(childTableKey).toString())
														.append("\n");
										}
									}
								}
							}
							if (tableDeleteStatements.containsKey(tableKey))
								fileBuilder.append(deletePrefix).append(tableDeleteStatements.get(tableKey).toString())
										.append("\n");
							fileBuilder.append("COMMIT;\n");

							// rollback
							Utils.saveFile(savedFolder + "\\" + "rb_" + exportScriptFileName, fileBuilder.toString());

							Utils.saveFile(savedFolder + "\\" + exportScriptFileName.replaceAll(".sql", "_nss.sql"),
									"REM BAK NONE");
							// resultContent.append("-- " + exportScriptFileName + "
							// \n").append(fileBuilder.toString()).append("\n\n");
						}
					} else {
						fileBuilder.append("REM BAK NONE \n");
						fileBuilder.append("SET DEFINE OFF; \n");

						for (Map.Entry<String, StringBuilder> insertStatement : tableInsertStatements.entrySet()) {
							String tableKey = insertStatement.getKey();
							String currTableName = tableKeys.containsKey(tableKey) ? tableKeys.get(tableKey) : "";
							// String currTableName = insertStatement.getKey();
							fileBuilder.append("\n -- table " + currTableName.toUpperCase() + "\n");
							if (tableSelectStatements.containsKey(tableKey))
								fileBuilder.append(tableSelectStatements.get(tableKey).toString()).append("\n");
							if (tableDeleteStatements.containsKey(tableKey))
								fileBuilder.append(tableDeleteStatements.get(tableKey).toString()).append("\n");

							fileBuilder.append(insertStatement.getValue().toString());
							fileBuilder.append("COMMIT;\n");
						}

						for (Map.Entry<String, StringBuilder> insertStatement : tableInsertStatements.entrySet()) {
							String tableKey = insertStatement.getKey();
							if (tableSelectStatements.containsKey(tableKey))
								fileBuilder.append(tableSelectStatements.get(tableKey).toString()).append("\n");
						}

						exportScriptFileName = file_prefix + "_export.sql";
						Utils.saveFile(savedFolder + "\\" + exportScriptFileName, fileBuilder.toString());
						resultContent.append("-- " + savedFolder + "\\" + exportScriptFileName + " \n")
								.append(fileBuilder.toString()).append("\n\n");
					}

					// if (pane != null)
					// pane.log(resultContent.toString());
					// else
					// MyDialog.showInformation(resultContent.toString());

					// save Logfile
					if (destSchema != null) {// then schema comparison will be done
						for (DBTableExport srcTable : srcSchema.getTableList().values()) {
							String pkDiff = "";
							String colDiff = "", colDefDiff = "";
							if (destSchema.getTableList().containsKey(srcTable.getTableName())
									&& srcTable.getNumberOfRows() != 0) {

								pkDiff = compareStringLists(
										srcTable.getPrimaryKey(),
										destSchema.getTableList().get(srcTable.getTableName()).getPrimaryKey(), "PK");
								colDiff = compareStringLists(
										srcTable.getColumnList(),
										destSchema.getTableList().get(srcTable.getTableName()).getColumnList(), "COL");
								colDefDiff = compareColumnDef(
										srcTable.getColumns(),
										destSchema.getTableList().get(srcTable.getTableName()).getColumns());
							}

							if (pkDiff.length() > 0) {
								pKDifferences.append(
										srcTable.getTableName() + ": the Primary Key field(s): " + pkDiff + "\n");
							}
							if (colDiff.length() > 0) {
								columnDifferences.append(srcTable.getTableName() + ": " + colDiff + "\n");
							}
							if (colDefDiff.length() > 0) {
								columnDefinitonDifferences.append(srcTable.getTableName() + ": " + colDefDiff + "\n");
							}
						}

						if (tablesInSrcNotInDest.length() > 0)
							MyDialog.showInformation(
									" WARNING: The following tables present in the source instance are not existing in destination instance (Action taken: The export script does not include insert statements for the above tables.)\n"
											+ tablesInSrcNotInDest);
						if (pKDifferences.length() > 0)
							MyDialog.showInformation(
									" WARNING: Primary Key definition differences: \n" + pKDifferences);
						if (columnDifferences.length() > 0)
							MyDialog.showInformation(
									" WARNING: Table Column differences (Action taken: The missing columns are not included in the inserts scripts. Anyway, please verify the export script in order to prevent possible Primary Key violations.)\n"
											+ columnDifferences);
						if (columnDefinitonDifferences.length() > 0)
							MyDialog.showInformation(" WARNING: Column Definition Differences: " +
									columnDifferences);
					}

				} // end IO try
				catch (IOException e) {
					MyDialog.showException(e,
							" Write Error! The file has a write protection. Check if it is used by another application\n");
				}

				listDBQuerySource.add(modulePlan);
				listDBQueryDest.add(modulePlanDest);

			} // end of big loop through the modules
			conn.close();
			if (connDest != null)
				connDest.close();

			Utils.exportListDBQueryToExcel(listDBQuerySource, savedFolder + "/result.xlsx"); // save result to excel
																								// file
			Utils.exportListDBQueryToSQL(listDBQuerySource, savedFolder + "/result.sql"); // save result to sql file

			listDBQuerySource.addAll(listDBQueryDest);
			return listDBQuerySource;
		} // end SQL try
		catch (SQLException sq) {
			MyDialog.showException(sq, "Database ERROR while using this SQL:\n" + sq + "\n");
		}
		return listDBQuerySource;

	}// end of execute

	static String compareStringLists(Collection<String> srcSet, Collection<String> destSet, String strDesc) {
		StringBuffer notInDest = new StringBuffer("");
		StringBuffer notInSrc = new StringBuffer("");

		for (String srcCol : srcSet) {
			if (destSet.contains(srcCol) == false)
				notInDest.append(", " + srcCol);
		}
		for (String destCol : destSet) {
			if (srcSet.contains(destCol) == false)
				notInSrc.append(", " + destCol);
		}

		String result = "";
		if (notInDest.length() > 0)
			result = result + notInDest.toString().replaceFirst(", ", "")
					+ " is(are) in the SOURCE Instance but NOT in the DESTINATION Instance " + "\n";
		if (notInSrc.length() > 0)
			result = result + notInSrc.toString().replaceFirst(", ", "")
					+ " is(are) in the DESTINATION Instance but NOT in the SOURCE Instance " + "\n";
		// System.out.println("notInDest: "+notInDest.length()+"
		// notInSrc:"+notInSrc.length()+" \n"+result);
		return result;
	}

	static String compareColumnDef(HashMap<String, DBColumn> srcSet, HashMap<String, DBColumn> destSet) {
		StringBuffer result = new StringBuffer("");
		if (srcSet != null && destSet != null) {
			for (String theKey : srcSet.keySet()) {
				// System.out.println(" SOURCE instance "+ srcSet.get(theKey)+" DESTINATION
				// instance "+destSet.get(theKey));
				if (destSet.containsKey(theKey) && !destSet.get(theKey).equals(srcSet.get(theKey))) {
					result.append("The column " + theKey + " is defined in the SOURCE instance " + srcSet.get(theKey)
							+ " and in the DESTINATION instance " + destSet.get(theKey) + "\n");
				}
			}
		}

		return result.toString();
	}

	static String getMaxEffectiveDateClause(String postingDate, DBTableExport table) {
		StringBuffer clause = new StringBuffer();
		if (postingDate == null)
			postingDate = "99991231";

		if (table == null || table.getPrimaryKey() == null || !table.getPrimaryKey().contains("EFFECTIVE_DATE"))
			return "";
		for (String pkColName : table.getPrimaryKey()) {

			clause.append("a." + pkColName + "=" + pkColName + " and ");
		}
		String result = clause.toString();
		if (table.getTableName().equals("CBR_CURRENCY_RATES")) {
			result = result.replace("a.GROUP_ID_NUMBER=GROUP_ID_NUMBER and ", "");
			result = result.replace("a.EFFECTIVE_TIME=EFFECTIVE_TIME and ", "");
			return " and EFFECTIVE_DATE >= (select max(EFFECTIVE_DATE) from " + table.getTableName() + " where " +
					result.replace("a.EFFECTIVE_DATE=EFFECTIVE_DATE and ", "") + " 1=1)";
		}

		return " and EFFECTIVE_DATE >= (select max(EFFECTIVE_DATE) from " + table.getTableName() + " where " +
				result.replace("a.EFFECTIVE_DATE=EFFECTIVE_DATE and ", "") + "EFFECTIVE_DATE<='" + postingDate + "')";

	}

	static String getOrderBy(DBTableExport table) {
		StringBuffer orderClause = new StringBuffer();

		if (table == null || table.getPrimaryKey() == null || table.getPrimaryKey().size() < 1)
			return "";
		for (String pkColName : table.getPrimaryKey()) {

			orderClause.append(pkColName + ",");
		}
		orderClause = orderClause.deleteCharAt(orderClause.length() - 1);
		return " order by " + orderClause.toString();
	}

	static DBTableExport findDBTableExportFromModule(Map<String, DBTableExport> moduleDef, String currTableKey) {
		DBTableExport currTableDef = moduleDef.get(currTableKey);
		if (currTableDef == null) { // must seek for item with generic value (contains %)
			for (String k : moduleDef.keySet()) {
				String selectManyChar = "";
				if (k.contains("%"))
					selectManyChar = "%";
				else if (k.contains("*"))
					selectManyChar = "*";
				if (!selectManyChar.isEmpty() && currTableKey.contains(k.replace(selectManyChar, ""))) {
					currTableDef = moduleDef.get(k);
					currTableDef.setTableName(currTableKey);
				}
			}
		}
		return currTableDef;
	}

	public static Map<String, Map<String, DBTableExport>> loadModuleDefFile(String filePath) {
		Map<String, Map<String, DBTableExport>> modules = new LinkedHashMap<String, Map<String, DBTableExport>>();
		String[] sqls = Utils.getSqlsFromFile(filePath);

		if (sqls != null && sqls.length > 0) {
			Map<String, DBTableExport> modelDef = new LinkedHashMap<String, DBTableExport>();
			for (String sql : sqls) {
				String tableName = Utils.substringBetween(sql.toUpperCase(), "FROM ", " WHERE");
				DBTableExport table = new DBTableExport(tableName);
				table.setSqlQuery(sql);
				modelDef.put(tableName, table);
			}
			modules.put(filePath.replace(" ", "_"), modelDef);
			return modules;
		}

		Map<String, List<Map<String, String>>> modulesTmp = XMLUtil.loadXmlModules(filePath, Settings.TagMODULE,
				Settings.TagTABLEEXTRACTDEFINITION);
		for (Map.Entry<String, List<Map<String, String>>> entry : modulesTmp.entrySet()) {
			Map<String, DBTableExport> modelDef = new LinkedHashMap<String, DBTableExport>();
			ArrayList<DBTableExport> list = new ArrayList<DBTableExport>();
			for (Map<String, String> xmlElement : entry.getValue()) {
				DBTableExport table = new DBTableExport(xmlElement);
				list.add(table);
				if (table.isChildren()) {
					DBTableExport tmp = null;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getTableName().equalsIgnoreCase(table.getParentName())) {
							tmp = list.get(i);
							list.remove(i);
							break;
						}
					}
					if (tmp != null)
						list.add(tmp); // always make sure children table is before parent table
				}
			}

			for (DBTableExport table : list) {
				if (!modelDef.containsKey(table.getId()))
					modelDef.put(table.getId(), table);
			}

			modules.put(entry.getKey(), modelDef);
		}

		return modules;
	}

}// End of class!

// Todo: implement script that dynamically check and update(create schema update
// script the schema
// declare
// v_sql varchar2(4000);
// v_colsize_dest integer;
//
// begin
//
// if exists TABLE_NAME then
//
// For t=1, t++; t<Number of Columns
// if not exist column[t] in destination schema then
// v_sql:='alter table TABLE_NAME ADD COLUMN_NAME[t] Type Contraints';
// execute immediate v_sql;
// elsif v_colsize_dest is of type Varchar2 and v_colsize_dest < column[t].size
// then
//
// v_sql:='alter table TABLE_NAME modify (COLUMN_NAME[t] VARCHAR2(new size) )';
// execute immediate v_sql;
//
// elseif type of column[t] != type of the destination column then
// v_sql:='alter table TABLE_NAME modify (COLUMN_NAME[t] new type(size) )';
// execute immediate v_sql;
// elseif column[t] has a constraint and the destiantion column has it not then
// Display a descripitve message
// elseif column[t] has a default value then
// v_sql:='alter table TABLE_NAME modify (COLUMN_NAME[t] DEFAULT defValue );
// execute immediate v_sql;
// else null;
// end if;
// end loop;
//
// end if;
// end;
// /