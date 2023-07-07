package com.rs2.modules.dataviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;

import java.util.LinkedList;

import java.sql.Connection;

import com.rs2.Main;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.components.treeview.DataNode;
import com.rs2.core.components.treeview.LabelNode;
import com.rs2.core.logs.LogManager;
import com.rs2.core.logs.Logger;
import com.rs2.core.settings.Settings;
import com.rs2.core.MainScreen;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.core.base.MyServiceDialog;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBTableExport;
import com.rs2.core.data.TransmittedCondition;
import com.rs2.modules.db.DBService;

import com.rs2.core.utils.XMLUtil;

import com.rs2.core.utils.Utils;

import java.util.regex.Pattern;

import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DataViewerService extends MyService {
	public static String ServiceTitle = "Data Viewer";
	public static String FILES_FOLDER = "dataviewer";
	public static String FILES_EXTENSION = "xml";

	private static DataViewerService instance;
	static DataPane mypane;
	static DataViewerDialog myDialog;

	public static DataViewerService getInstance() {
		if (instance == null)
			instance = new DataViewerService();
		return instance;
	}

	public DataViewerService() {

	}

	public static MyPane createPane() {
		if (mypane == null)
			mypane = new DataPane();
		return mypane;
	}

	public static MyServiceDialog createDialog() {
		if (myDialog == null)
			myDialog = new DataViewerDialog();
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
		run(file, generateDataPane(file));
	}

	public static List<DBQuery> generateDBQuery(String filePath, String connectionName) {
		return generateDBQuery(filePath, connectionName, null, null);
	}

	public static List<DBQuery> generateDBQueryFromSQLFile(String filePath) {
		return generateDBQueryFromSQLFile(filePath, null, null, null, null, null, false, true);
	}

	public static List<DBQuery> generateDBQueryFromSQLFile(
			String filePath, List<DBParam> queryParams,
			List<DBLookup> lookupColumns, Connection conn, String connectionName, String institution_number,
			boolean chkAutoFixSql, boolean chkReadOnly) {
		String[] sqls = Utils.getSqlsFromFile(filePath);
		return generateDBQueryFromSQLsList(sqls, queryParams, lookupColumns, conn, connectionName, institution_number,
				chkAutoFixSql, chkReadOnly);
	}

	public static List<DBQuery> generateDBQueryFromSQLsList(String[] sqls, List<DBParam> queryParams,
			List<DBLookup> lookupColumns, Connection conn, String connectionName, String institution_number,
			boolean chkAutoFixSql, boolean chkReadOnly) {
		List<DBQuery> listAllQueryDefinitions = null;
		if (sqls == null)
			return null;
		listAllQueryDefinitions = new LinkedList<DBQuery>();

		Map<String, Object> planProperties = null;
		for (String sql : sqls) {
			sql = sql.trim();
			// if sql is comment then it has attributes for following sqls
			if (sql.startsWith(Settings.paramComment)) {
				planProperties = Utils.convertYamlStringToMap(
						sql.replace(Settings.paramComment + " " + Settings.paramCommandStart, ""));
				continue;
			}
			if (DBService.isStatementSQL(sql)) { // not a select statement
				if (chkReadOnly) {
					LogManager.getLogger().debug("Skip operation due to readonly mode [" + sql + "]");
					continue;
				}
				if (queryParams != null)
					sql = DBService.applyParams(sql, queryParams);

				// DBService.executeCallableStatement(sql, conn);
				int result = DBService.executeStatement(sql, conn);
				// int result = DBService.executeSql(sql, conn);
				continue;
			}

			if (chkAutoFixSql) {
				institution_number = DBService.getInstitutionNumberFromSql(sql);

				if (institution_number != null && !institution_number.isEmpty()) {
					if (queryParams != null)
						DBService.addQueryParam(queryParams, Settings.paramInstitution,
								institution_number);
					sql = getSQLWithInstitutionWhereClause(sql);
				}
			}
			if (queryParams != null)
				sql = DBService.applyParams(sql, queryParams);

			String tableName = DBService.getTableNameFromSql(sql);
			DBQuery plan = new DBQuery(sql, !tableName.isEmpty() ? tableName : sql);
			if (tableName != null && !tableName.isEmpty() && !tableName.contains(" ")) {
				plan.setTableName(tableName);
				plan.setQueryTitle(sql);
			}
			if (connectionName != null)
				plan.setConnectionName(connectionName);
			if (queryParams != null)
				plan.setQueryParams(queryParams);
			if (conn != null)
				plan.setData(sql.trim(), conn);

			if (planProperties != null) {
				plan.setProperties(planProperties); // set properties from comments for this query
				planProperties = null;
			}

			listAllQueryDefinitions.add(plan);
			// try to get institution_number from query data, to prepare cached lookup
			if (institution_number == null || institution_number.isEmpty()) {
				List<String> institutionList = plan.getColumnsDistinctData(Settings.columnInstitution);
				if (institutionList.size() > 0) {
					institution_number = institutionList.get(0);
					if (queryParams != null)
						DBService.addQueryParam(queryParams, Settings.paramInstitution,
								institution_number);
				}
			}
		}
		final List<DBQuery> allQueries = listAllQueryDefinitions;
		if (lookupColumns != null && listAllQueryDefinitions != null) {
			MyService.setupCachedData(allQueries, lookupColumns, connectionName, queryParams);
		}
		return listAllQueryDefinitions;
	}

	public static String getSQLWithInstitutionWhereClause(String sql) {
		sql = sql.trim();
		int orderbyIdx = sql.toLowerCase().indexOf("order by ");
		String orderby = "";
		if (orderbyIdx > -1) {
			orderby = sql.substring(orderbyIdx + 9, sql.length());
			sql = sql.substring(0, orderbyIdx);
		}

		if (!sql.toLowerCase().contains(Settings.columnInstitution)) {
			if (!sql.toLowerCase().contains("where"))
				sql = sql + " where " + Settings.columnInstitution + " = '"
						+ Settings.paramInstitution + "'";
			else {
				int pos = sql.toLowerCase().indexOf("where") + 5;
				sql = sql.substring(0, pos) + " " + Settings.columnInstitution + " = '"
						+ Settings.paramInstitution + "'" + " AND "
						+ sql.substring(pos, sql.length());
			}
		}

		if (orderby.isEmpty())
			sql = sql + " order by 1";
		else
			sql = sql + " order by " + orderby;

		return sql;
	}

	public static List<DBQuery> generateDBQueryFromExportFile(String filePath, List<DBParam> queryParams) {
		Map<String, List<Map<String, String>>> modules = XMLUtil.loadXmlModules(filePath);
		if (modules != null && modules.size() > 0) {
			List<DBQuery> list = new LinkedList<DBQuery>();

			for (Map.Entry<String, List<Map<String, String>>> entry : modules.entrySet()) {
				DBQuery modulePlan = new DBQuery("", entry.getKey());
				modulePlan.setQueryParams(queryParams);
				modulePlan.setTempTable(entry.getKey());
				for (Map<String, String> planDef : entry.getValue()) {
					DBTableExport table = new DBTableExport(planDef);
					DBQuery query = new DBQuery(table.buildSql(), table.getTableName());
					query.setSuperSQLQuery(modulePlan);
					query.setQueryParams(queryParams);
					list.add(query);
				}
				list.add(modulePlan);
			}

			if (queryParams == null) {
				queryParams = MyService.initQueryParams();
			}
			return list;
		}
		if (queryParams == null) {
			queryParams = MyService.initQueryParams();
		}
		return null;
	}

	public static List<DBQuery> generateDBQuery(String filePath, String connectionName, List<DBParam> queryParams,
			List<DBLookup> lookupColumns) {
		List<DBQuery> tmp = generateDBQueryFromSQLFile(filePath);
		if (tmp != null)
			return tmp;

		tmp = generateDBQueryFromExportFile(filePath, queryParams);
		if (tmp != null)
			return tmp;

		Document xmlDocument = getXmlDocument(filePath);
		if (xmlDocument == null) {
			return null;
		}

		return generateDBQuery(xmlDocument, connectionName, queryParams, lookupColumns, true);
	}

	public static List<DBQuery> generateDBQuery(Document doc, String connectionName, List<DBParam> queryParams,
			List<DBLookup> lookupColumns) {
		return generateDBQuery(doc, connectionName, queryParams, lookupColumns, true);
	}

	public static List<DBQuery> generateDBQuery(Document doc, String connectionName, List<DBParam> queryParams,
			List<DBLookup> lookupColumns, boolean setupCachedData) {
		List<Map<String, String>> xmlExtrakt = XMLUtil.loadXmlTags(doc, Settings.TagSQLQUERYDEFINITION);
		String validationMessages = validateXMLDBQuery(xmlExtrakt);
		if (!validationMessages.isEmpty()) {
			MyDialog.showMessage(validationMessages, "Fatal validation problems: " + validationMessages);
		}
		List<DBQuery> result = new LinkedList<DBQuery>();

		if (queryParams == null || queryParams.size() == 0)
			queryParams = loadSQLQueryParams(doc);

		if (lookupColumns == null || lookupColumns.size() == 0)
			lookupColumns = loadLookupFields(doc);

		Set<String> subFiles = loadSubFiles(doc);
		if (subFiles != null && !subFiles.isEmpty()) {
			for (String subFile : subFiles) {
				Document childDoc = getXmlDocument(subFile.trim());
				if (childDoc != null) {
					if (queryParams == null || queryParams.size() == 0)
						queryParams = loadSQLQueryParams(childDoc);

					if (lookupColumns == null || lookupColumns.size() == 0)
						lookupColumns = loadLookupFields(childDoc);
					List<DBQuery> sQueries = generateDBQuery(childDoc, connectionName, queryParams, lookupColumns);
					if (sQueries != null && sQueries.size() > 0) {
						result.addAll(sQueries);
					}
				}
			}
		}

		for (DBQuery queryDef : result) {
			queryDef.setLookupColumns(lookupColumns);
		}

		for (Map<String, String> queryDef : xmlExtrakt) {
			result = addQueryDifinition(result, queryDef, connectionName, queryParams, lookupColumns);
		}

		if (defFiles != null && defFiles.size() > 0)
			defFiles.removeLast();

		final List<DBQuery> tmpResult = result;

		if (setupCachedData) {
			setupCachedData(tmpResult, lookupColumns, connectionName, queryParams);
		}

		return result;
	}

	public static String validateXMLDBQuery(List<Map<String, String>> xmlExtrakt) {
		int dBQueryCounter = 0;
		StringBuilder validationComments = new StringBuilder("");
		LinkedList<String> queryLabels = new LinkedList<String>();
		String queryLabel = "", executeOnFirstRun;
		for (Map<String, String> queryDef : xmlExtrakt) {
			dBQueryCounter++;

			if (queryDef.get(Settings.TagSQLQUERYLABEL) == null
					|| queryDef.get(Settings.TagSQLQUERYLABEL).trim().isEmpty()) {
				if (queryDef.get(Settings.TagTable) == null || queryDef.get(Settings.TagTable).trim().isEmpty()) {
				} else {
					queryLabel = queryDef.get(Settings.TagTable);
				}
			} else
				queryLabel = queryDef.get(Settings.TagSQLQUERYLABEL);

			executeOnFirstRun = queryDef.get(Settings.TagEXECUTEONFIRSTRUN);
			if (dBQueryCounter == 1 && executeOnFirstRun != null
					&& (executeOnFirstRun.trim().equals("FALSE") || executeOnFirstRun.trim().equals("NO")
							|| executeOnFirstRun.trim().equals("0") || executeOnFirstRun.trim().equals("-1"))) {
				executeOnFirstRun = "1";
			}

			if (!queryLabel.isEmpty())
				queryLabels.add(queryLabel);
		}
		return validationComments.toString();
	}

	public static List<DBQuery> addQueryDifinition(List<DBQuery> result, Map<String, String> queryDef,
			String connectionName, List<DBParam> queryParams, List<DBLookup> lookupColumns) {

		DBQuery query = createDBQuery(result, queryDef, queryParams, lookupColumns);
		List<DBQuery> subQueries = new LinkedList<DBQuery>();
		if (queryDef.get(Settings.TagSUBQUERYLOCATOR) != null) {
			String[] theFiles = queryDef.get(Settings.TagSUBQUERYLOCATOR).split(",");
			for (String theSubQueryFile : theFiles) {
				subQueries = loadSubQueriesByFiles(query, theSubQueryFile, connectionName, queryParams, lookupColumns);
				if (subQueries != null && !subQueries.isEmpty()) {
					for (int i = 0; i < subQueries.size(); i++) {
						subQueries.get(i).setQueryParams(queryParams);
						subQueries.get(i).setLookupColumns(lookupColumns);
						subQueries.get(i).superSQLQuery = query;
						query.subQueries.add(subQueries.get(i));
						// result.add(subQueries.get(i));
					}
				}
			}
		}

		query.setLookupColumns(lookupColumns);
		result.add(query);
		if (subQueries != null && subQueries.size() > 0)
			result.addAll(subQueries);

		return result;
	}

	public static List<DBQuery> addQueryDifinition(List<DBQuery> result, String queryLabel, String query,
			String transmittedConditionColumns, String resultColumns, DBQuery superQuery, String executeOnFirstRun,
			String maximumResultRows, String emptyColumnDisplayMode, String suppressDisplayIfNoData,
			Integer maxInheritanceDepth, List<DBParam> queryParams, List<DBLookup> lookupColumns) {

		DBQuery queryDefinition = createDBQuery(queryLabel, query, transmittedConditionColumns, resultColumns,
				superQuery, executeOnFirstRun, maximumResultRows, emptyColumnDisplayMode, suppressDisplayIfNoData,
				maxInheritanceDepth, queryParams, lookupColumns);
		result.add(queryDefinition);

		return result;
	}

	public static List<DBQuery> addQueryDifinition(List<DBQuery> result, String queryLabel, String query,
			String transmittedConditionColumns, String resultColumns, DBQuery superQuery) {
		DBQuery queryDefinition = createDBQuery(queryLabel, query, transmittedConditionColumns, resultColumns,
				superQuery, "", "", "", "true", null,
				result.size() > 0 ? result.get(0).getQueryParams() : new LinkedList<DBParam>(),
				result.size() > 0 ? result.get(0).getLookupColumns() : new LinkedList<DBLookup>());
		result.add(queryDefinition);

		return result;
	}

	public static List<DBQuery> addQueryDifinition(List<DBQuery> result, String queryLabel, String query,
			String transmittedConditionColumns, String resultColumns, String superQueryLabel) {
		DBQuery superQuery = getDBQuery(result, superQueryLabel);
		DBQuery queryDefinition = createDBQuery(queryLabel, query, transmittedConditionColumns, resultColumns,
				superQuery, "", "", "", "true", null,
				result.size() > 0 ? result.get(0).getQueryParams() : new LinkedList<DBParam>(),
				result.size() > 0 ? result.get(0).getLookupColumns() : new LinkedList<DBLookup>());
		if (queryDefinition != null)
			result.add(queryDefinition);

		return result;
	}

	public static DBQuery getDBQuery(List<DBQuery> result, String queryLabel) {
		DBQuery query = null;
		for (DBQuery sQuery : result) {
			if (sQuery.getSQLQueryLabel().toUpperCase().equals(queryLabel.trim().toUpperCase())) {
				query = sQuery;
				break;
			}
		}
		return query;
	}

	public static DBQuery createDBQuery(String queryLabel, String query, String transmittedConditionColumns,
			String resultColumns, DBQuery superQuery, String executeOnFirstRun, String maximumResultRows,
			String emptyColumnDisplayMode, String suppressDisplayIfNoData, Integer maxInheritanceDepth,
			List<DBParam> queryParams, List<DBLookup> lookupColumns) {

		while (query != null && query.charAt(query.length() - 1) == ';') {
			query = query.substring(0, query.length() - 1).trim();
		}

		DBQuery queryDefinition = new DBQuery(query, queryLabel, superQuery, maxInheritanceDepth);

		// add Query Param !!
		queryDefinition.setQueryParams(queryParams);
		queryDefinition.setLookupColumns(lookupColumns);

		if (transmittedConditionColumns != null) {
			String[] transmittedCondCols = transmittedConditionColumns.split(",");
			for (int x = 0; x < transmittedCondCols.length; x++)
				queryDefinition.addTransmittedConditionAttribute(transmittedCondCols[x].toUpperCase().trim());
		}

		if (resultColumns != null) {
			String[] resultCols = resultColumns.split(",");
			for (int x = 0; x < resultCols.length; x++)
				queryDefinition.addResultColumn(resultCols[x].toUpperCase());
		}

		if (Utils.checkBoolean(suppressDisplayIfNoData))
			queryDefinition.setSuppressDisplayIfNoData(true);

		if (!Utils.checkBoolean(executeOnFirstRun))
			queryDefinition.setExecuteOnFirstRun(false);
		else
			queryDefinition.setExecuteOnFirstRun(true);

		queryDefinition.setMaximumResultRows(
				maximumResultRows != null ? Integer.parseInt(maximumResultRows) : Settings.MaximumResultRows);

		if (emptyColumnDisplayMode != null)
			queryDefinition.setEmptyColumnDisplayMode(emptyColumnDisplayMode.replaceAll(" ", ""));
		else
			queryDefinition.setEmptyColumnDisplayMode("NOSUPPRESS");

		return queryDefinition;
	}

	public static DBQuery createDBQuery(Map<String, String> queryDef, DBQuery superQuery, List<DBParam> queryParams,
			List<DBLookup> lookupColumns) {

		String query = queryDef.get(Settings.TagSQLQUERY);

		String queryLabel = queryDef.get(Settings.TagSQLQUERYLABEL);
		String executeOnFirstRun = queryDef.get(Settings.TagEXECUTEONFIRSTRUN);
		String suppressDisplayIfNoData = queryDef.get(Settings.TagSUPPRESSDISPLAYIFNODATA);
		String maximumResultRows = queryDef.get(Settings.TagMAXRESULTROWS);
		String emptyColumnDisplayMode = queryDef.get(Settings.TagEMPTYCOLUMNDISPLAYMODE);
		// String queryLabelTitle = queryDef.get("title");
		String transmittedConditionColumns = queryDef.get(Settings.TagTRANSMITTEDCONDITIONCOLUMNS);
		String resultColumns = queryDef.get(Settings.TagRESULTCOLUMNS);
		Integer maxInheritanceDepth = Settings.MaximumResultRows;
		if (queryDef.get(Settings.TagMAXINHERITANCEDEPTH) != null)
			maxInheritanceDepth = Integer.parseInt(queryDef.get(Settings.TagMAXINHERITANCEDEPTH));

		DBQuery queryDefinition = createDBQuery(queryLabel, query, transmittedConditionColumns, resultColumns,
				superQuery, executeOnFirstRun, maximumResultRows, emptyColumnDisplayMode, suppressDisplayIfNoData,
				maxInheritanceDepth, queryParams, lookupColumns);
		queryDefinition.setLookupColumns(lookupColumns);

		queryDefinition.setKeyColumn(queryDef.get(Settings.TagFieldKey));
		queryDefinition.setParentColumn(queryDef.get(Settings.TagParentColumn));

		queryDefinition.setXmlTags(queryDef);

		return queryDefinition;
	}

	public static DBQuery createDBQuery(Map<String, String> queryDef, List<DBQuery> result, List<DBParam> queryParams,
			List<DBLookup> lookupColumns) {

		String superSql = queryDef.get(Settings.TagSUPERSQLQUERY);
		DBQuery superQuery = null;
		if (superSql != null && !superSql.isEmpty()) {
			superQuery = getDBQuery(result, superSql);
		}
		if (queryParams == null || queryParams.size() == 0)
			queryParams = result.get(0).getQueryParams();
		if (lookupColumns == null || lookupColumns.size() == 0)
			lookupColumns = result.get(0).getLookupColumns();
		return createDBQuery(queryDef, superQuery, queryParams, lookupColumns);
	}

	public static List<DBQuery> loadSubQueriesByFiles(DBQuery plan, String theSubQueryFile, String connectionName,
			List<DBParam> queryParams, List<DBLookup> lookupColumns) {
		List<DBQuery> subQueryFile = new LinkedList<DBQuery>();
		StringBuffer subFileErorrs = new StringBuffer("");
		boolean circularity = false, fileExists = true;

		if (!theSubQueryFile.trim().isEmpty()) {
			File subqueryLocator = new File(theSubQueryFile.trim());
			if (!subqueryLocator.isAbsolute()) {
				try {
					theSubQueryFile = subqueryLocator.getCanonicalPath();
				} catch (IOException e) {
					MyDialog.showException(e,
							"I/O error thrown during loading the data from" + theSubQueryFile);
				}
			}
			if (!subqueryLocator.exists() || !subqueryLocator.isFile()) {
				subFileErorrs.append("The SQLQueryDefinition tag " + plan.getSQLQueryLabel()
						+ " is pointing to a EsspresoView Report definition file which could not be found: "
						+ theSubQueryFile + "\nThis sub-report will be ignored");
				// "In der Anfragedefinition "+queryLabel+" verweist der tag SubQueryLocator auf
				// die nicht auffindbare Datei "+subqueryLocator.getAbsolutePath()+ "\n\n");
				logger.error("The SQLQueryDefinitionTag" + plan.getSQLQueryLabel()
						+ " is pointing to a EsspresoView Report definition file which could not be found "
						+ theSubQueryFile + "\n");
				fileExists = false;
			}

			if (fileExists && !circularity) {
				subQueryFile = generateDBQuery(theSubQueryFile, connectionName, queryParams, lookupColumns);
			}
		}

		if (subFileErorrs.length() > 0) {
			logger.error(subFileErorrs.toString());
			subFileErorrs.delete(0, subFileErorrs.length() - 1);
		}
		return subQueryFile;
	}

	public static DBQuery createDBQuery(List<DBQuery> result, Map<String, String> queryDef, List<DBParam> queryParams,
			List<DBLookup> lookupColumns) {
		String querySuper = queryDef.get(Settings.TagSUPERSQLQUERY);
		DBQuery superQuery = getDBQuery(result, querySuper == null ? "" : querySuper.trim());

		DBQuery queryDefinition = createDBQuery(queryDef, superQuery, queryParams, lookupColumns);

		return queryDefinition;
	}

	public static void generateBlockDisplay(DBQuery dBQuery) {
		generateBlockDisplayRek(dBQuery, 1);
	}

	public static void generateBlockDisplayRek(DBQuery dBQuery, int level) {
		String ResultMatrix[][] = dBQuery.getResultRowsMatrix();
		int colNr = 0, rowNr = 0;

		for (rowNr = 0; rowNr < ResultMatrix[0].length; rowNr++) {
			System.out.print(Utils.multiplyChars(' ', (level - 1) * 10));
			for (colNr = 0; colNr < ResultMatrix.length; colNr++) {
				System.out.print(ResultMatrix[colNr][rowNr] + "\t");
			}
		}
		int t = 0;
		if (!dBQuery.subQueries.isEmpty()) {
			while (dBQuery.subQueries.size() > t) {
				generateBlockDisplayRek(dBQuery.subQueries.get(t), level + 1);
				t++;
			}
		}
	}

	public static DefaultMutableTreeNode generateStructDisplay(DBQuery dBQuery, String titleText, String viewOption) {
		LabelNode anfang = new LabelNode(titleText);
		anfang.viewOption = viewOption;
		generateStructDisplayRek(dBQuery, anfang, 1);
		anfang.setNodeBranchInfo(anfang);
		anfang.propagateViewOption();
		return anfang.composeTree();
	}

	public static void generateStructDisplayRek(DBQuery dBQuery, DataNode node, int level) {
		String ResultMatrix[][] = dBQuery.getResultRowsMatrix();
		String mhd = "";
		if (dBQuery.getMaxInheritanceDepth() < Settings.MaximumResultRows)
			mhd = " (max depth " + Integer.toString(dBQuery.getMaxInheritanceDepth()) + ")";
		String notApplicableColumns = dBQuery.getInheritedConditionsAsString(false);
		if (notApplicableColumns.contains("*") && mhd.length() > 0)
			mhd = mhd + "*";

		// show title of labels with children count !!
		DataNode newChild = node.insertDeep(
				new DataNode(
						dBQuery.getSQLQueryLabel() + (ResultMatrix[0].length - 1),
						dBQuery.getSqlQuery(),
						node));

		int t = 0;
		if (!dBQuery.subQueries.isEmpty()) {
			while (dBQuery.subQueries.size() > t) {
				generateStructDisplayRek(dBQuery.subQueries.get(t), newChild, level + 1);
				t++;
			}
		}
	}

	// start generate tree in detail mode, recursively
	public static DataNode generateJointDisplayList(List<DBQuery> dBQueryList, String titelText,
			String viewOption, boolean isFirstRun, boolean isShowFlat, DataPane dataPane) {

		String connectionName = dataPane.connectionName;
		List<DBLookup> lookupColumns = dataPane.lookupColumns;
		List<DBQuery> lookupDataQueryDefs = new LinkedList<DBQuery>();
		List<DBQuery> normalDataQueryDefs = new LinkedList<DBQuery>();
		Connection conn = null;

		for (DBQuery dataDef : dBQueryList) {
			if (conn == null && dataDef.getConnection() != null)
				conn = dataDef.getConnection();

			if (dataDef.getType().equalsIgnoreCase(Settings.QueryTypeLookup)) {
				if (isShowFlat)
					dataDef.setSqlQuery(dataDef.getCompareSql());
				lookupDataQueryDefs.add(dataDef);
			} else {
				normalDataQueryDefs.add(dataDef);
			}
		}

		dBQueryList = normalDataQueryDefs;
		DataNode anfang = generateDataNode(dBQueryList, titelText, viewOption, isFirstRun, isShowFlat);

		if (lookupColumns != null) {
			lookupDataQueryDefs = new LinkedList<DBQuery>();
			for (DBLookup lookupDef : lookupColumns) {
				String sql = lookupDef.getSql();
				String table = lookupDef.getTableName();
				if (sql == null || sql.isEmpty() || table == null || table.isEmpty())
					continue;
				Map<String, String> cachedData = getCachedData(connectionName, table);
				if (cachedData != null && cachedData.size() > 0) {
					DBQuery dataDef = new DBQuery(sql, table);
					dataDef.setData(cachedData);
					dataDef.setConnection(conn);
					lookupDataQueryDefs.add(dataDef);
				}
			}

		}

		// LogManager.getLogger().debug("START LOOKUP");
		if (lookupDataQueryDefs.size() > 0) {
			DataNode lookupNode = generateDataNode(lookupDataQueryDefs, "LOOKUP", viewOption, isFirstRun, isShowFlat);
			anfang.insertDeep(lookupNode);
		}

		return anfang;
	}

	public static DataNode generateJointDisplayList(List<DBQuery> dBQueryList, String titelText,
			String viewOption,
			boolean isFirstRun, boolean isShowFlat) {

		return generateJointDisplayList(dBQueryList, titelText, viewOption, isFirstRun, isShowFlat, null);
	}

	// start generate tree in detail mode, recursively
	public static DefaultMutableTreeNode generateJointDisplay(DBQuery dBQuery, String titelText, String viewOption,
			boolean isFirstRun, boolean isShowFlat) {
		DataNode anfang = generateDataNode(dBQuery, titelText, viewOption, isFirstRun, isShowFlat);
		if (anfang != null)
			return anfang.composeTree();
		return null;
	}

	public static DataNode generateDataNode(Map<String, List<DBQuery>> dBQueryGroups, String titelText,
			String viewOption,
			boolean isFirstRun, boolean isShowFlat) {

		LabelNode anfang = new LabelNode(titelText);
		anfang.viewOption = viewOption;

		for (Map.Entry<String, List<DBQuery>> pair : dBQueryGroups.entrySet()) {

			// DataNode groupNode = generateDataNode(pair.getValue(), pair.getKey(),
			// viewOption, isFirstRun, isShowFlat);
			// groupNode.isLabel = true;
			// anfang.insertDeep(groupNode);

			DataNode groupNode = anfang.insertDeep(new LabelNode(pair.getKey()));
			// groupNode.isLabel = true;
			for (DBQuery dBQuery : pair.getValue()) {
				DataNode childNode = generateDataNode(dBQuery, "", viewOption, isFirstRun, isShowFlat);
				groupNode.insertDeep(childNode);
			}
		}

		return anfang;
	}

	public static DataNode generateDataNode(List<DBQuery> dBQueryList, String titelText, String viewOption,
			boolean isFirstRun, boolean isShowFlat) {
		return generateDataNode(dBQueryList, titelText, viewOption, isFirstRun, isShowFlat, false);
	}

	public static DataNode generateDataNode(List<DBQuery> dBQueryList, String titelText, String viewOption,
			boolean isFirstRun, boolean isShowFlat, boolean isSortByName) {

		DataNode anfang = new LabelNode(titelText);
		anfang.viewOption = viewOption;

		if (dBQueryList != null) {
			if (isSortByName)
				dBQueryList = DBService.sortListDBQueryByName(dBQueryList);

			Set<DBQuery> finisheDbQueries = new LinkedHashSet<DBQuery>();

			Map<String, List<DBQuery>> dbQueryGroups = new TreeMap<String, List<DBQuery>>();
			for (DBQuery dBQuery : dBQueryList) {
				if (dBQuery.isLabel() && dBQuery.subQueries != null && dBQuery.subQueries.size() > 0) {
					dbQueryGroups.put(dBQuery.getSQLQueryLabel(), dBQuery.subQueries);
					finisheDbQueries.add(dBQuery);
					finisheDbQueries.addAll(dBQuery.subQueries);
				}
			}
			if (dbQueryGroups.size() > 0) {
				DataNode groupNodeRoot = generateDataNode(dbQueryGroups, "", viewOption, isFirstRun, isShowFlat);
				for (DataNode groupNode : groupNodeRoot.getChildren()) {
					anfang.insertDeep(groupNode);
				}
			}

			for (DBQuery dBQuery : dBQueryList) {
				if (finisheDbQueries != null && finisheDbQueries.contains(dBQuery))
					continue;
				if (isShowFlat || !dBQuery.hasSuperSQLQuery()) {
					DataNode childNode = generateDataNode(dBQuery, "", viewOption, isFirstRun, isShowFlat);
					anfang.insertDeep(childNode);
				}
			}
		}

		return anfang;
	}

	// start generate tree in detail mode, recursively
	public static DataNode generateDataNode(DBQuery dBQuery, String titelText, String viewOption,
			boolean isFirstRun, boolean isShowFlat) {
		DataNode anfang = new LabelNode(titelText, dBQuery);
		anfang.viewOption = viewOption;
		HashMap<String, TransmittedCondition> forwardJointCond = null;

		if (!isShowFlat && !isFirstRun && dBQuery.transmittedConditions != null
				&& !dBQuery.transmittedConditions.isEmpty()) {
			forwardJointCond = new HashMap<String, TransmittedCondition>();
			for (TransmittedCondition transmittedCondition : dBQuery.transmittedConditions) {
				if (transmittedCondition.getDepth() > 1
						&& transmittedCondition.getValue().replace(" ", "").contains("','") == false) {

					TransmittedCondition transmittedConditionClone = TransmittedCondition
							.cloneCondition(transmittedCondition);
					transmittedConditionClone.setValue(transmittedConditionClone.getValue().replace("in ('", "")
							.replace("' )", "").replace("')", "").trim());
					forwardJointCond.put(transmittedCondition.getIdentifier(), transmittedConditionClone);
				}
			}
		}

		generateJointDisplayRek(dBQuery, forwardJointCond, 1, anfang, viewOption, isFirstRun, isShowFlat);
		anfang.propagateViewOption();

		if (!isFirstRun && anfang.getChildren() != null && anfang.getChildren().size() > 0)
			anfang = anfang.getFirstChild();
		if (anfang != null) {
			anfang.setNodeBranchInfo(anfang);
			if (!isFirstRun && anfang.getChildren() == null)
				return null;
			if (titelText == null || titelText.isEmpty() && anfang.getFirstChild() != null)
				anfang = anfang.getFirstChild();
		}

		if (anfang != null && anfang.getChildren() != null && anfang.getChildren().size() == 1
				&& anfang.getFirstChild().isLabel)
			anfang.setChildren(anfang.getFirstChild().getChildren());
		return anfang;
	}

	public static String getColumnCellValueIdentifier(String column, String cellValue) {
		if (column == null || column.isEmpty())
			return "";
		if (cellValue == null)
			cellValue = "";
		return column.trim().toUpperCase() + "=" + cellValue.trim().toUpperCase();
	}

	// hier in dieser Methode findet der eigent�mliche DataJoin statt
	public static void generateJointDisplayRek(DBQuery dBQuery,
			HashMap<String, TransmittedCondition> inheritedJointCond,
			int level, DataNode result, String viewOption, boolean isFirstRun, boolean isShowFlat) {
		if (dBQuery.isSuppressDisplayIfNoData() && dBQuery.isEmptyResultset()
		// && !(isFirstRun && !dBQuery.isExecuteOnFirstRun())
				&& (dBQuery.isExecuteOnFirstRun() || dBQuery.isDataRetrievalTriggeredByGUI()))
			return;
		String resultMatrix[][] = dBQuery.getResultRowsMatrix();
		if (resultMatrix == null)
			return;

		String columnName, cellValue, resultLineString, resultLineColumnNamesString;

		StringBuilder resultLine = new StringBuilder();
		StringBuilder resultLineColumnNames = new StringBuilder();
		List<String> resultColumns = new LinkedList<String>();

		if (!isShowFlat) // if showFlat then always show all fields
			resultColumns.addAll(dBQuery.getResultColumns());

		DataNode newChild, parentChild;

		HashMap<String, TransmittedCondition> forwardJointCond = new HashMap<String, TransmittedCondition>();
		Set<String> transmitedColumns = dBQuery.getTransmittedColumns();

		for (TransmittedCondition condi : dBQuery.getTransmittedConditions()) {
			if (!transmitedColumns.contains(condi.getIdentifier()))
				continue;
			TransmittedCondition forwardCondi = new TransmittedCondition(condi.getIdentifier(), null, condi.getDepth(),
					condi.inheritanceIsNotApplicable());
			if (condi.inheritanceIsNotApplicable()
					&& inheritedJointCond != null
					&& inheritedJointCond.containsKey(condi.getIdentifier())) {
				forwardCondi.setValue(inheritedJointCond.get(condi.getIdentifier()).getValue());//
			}
			forwardJointCond.put(forwardCondi.getIdentifier(), forwardCondi);
		}

		HashMap<Integer, String> columnNamesUpperCase = new HashMap<Integer, String>();

		boolean included = true;
		if (resultColumns.size() != 0 && resultColumns.get(0).trim().toUpperCase().startsWith("NOT:")) {

			included = false;
			resultColumns.set(0, resultColumns.get(0).toUpperCase().replace("NOT:", "").trim());
		}

		// decide which column should be displayed / hidden from grid
		boolean[] isResultColumn = new boolean[resultMatrix.length];
		if (viewOption.equals(Settings.viewDetail)) {
			for (int t = 0; t < resultMatrix.length; t++) {
				String columnTmp = resultMatrix[t][0];
				columnNamesUpperCase.put(t, columnTmp != null ? columnTmp.toUpperCase() : "");
				resultLineColumnNames.append(columnTmp + Settings.dataSeperator);
				if (resultColumns.size() == 0
						|| Utils.containsIgnoreCase(resultColumns, columnTmp) == included)
					if (dBQuery.getEmptyColumnDisplayMode().equalsIgnoreCase(Settings.emptyDisplayModeSUPRESS)
							&& dBQuery.getIsColumnNull() != null
							&& dBQuery.getIsColumnNull().containsKey(columnTmp)
							&& dBQuery.getIsColumnNull().get(columnTmp))
						isResultColumn[t] = false;
					else if (Settings.hideInheritedColumns && !isShowFlat && dBQuery.getSuperSQLQuery() != null // auto
																												// hide
																												// columns
																												// transmitted
																												// from
					// parent super query in tree mode (inn
					// isShowFlat still show)
							&& dBQuery.getSuperSQLQuery().getTransmittedConditionColumns()
									.contains((String) columnTmp)) {
						if (dBQuery.getParentColumn() != null && dBQuery.getKeyColumn() != null
								&& (columnTmp.equalsIgnoreCase(dBQuery.getParentColumn())
										|| columnTmp
												.equalsIgnoreCase(dBQuery.getKeyColumn())))
							isResultColumn[t] = true;
						else
							isResultColumn[t] = false; // false --> hide inherited columns, true --> show inherited
														// columns
					} else
						isResultColumn[t] = true;
			}
		} else {
			Set<String> inheritedCondAttr = dBQuery.getInheritedApplicableConditions();
			Set<String> forwardedCondAttr = dBQuery.getNewTransmittedConditionColumns();
			for (int t = 0; t < resultMatrix.length; t++) {
				String columnTmp = resultMatrix[t][0];
				resultLineColumnNames.append(columnTmp + Settings.dataSeperator); // Zeigt den Namen der columns
				columnNamesUpperCase.put(t, columnTmp.toUpperCase());
				if (inheritedCondAttr != null
						&& Utils.containsIgnoreCase(inheritedCondAttr, columnTmp)) {
					resultLineColumnNames.insert(resultLineColumnNames.lastIndexOf(columnTmp), "");
					isResultColumn[t] = true;
				}
				if (forwardedCondAttr != null
						&& Utils.containsIgnoreCase(forwardedCondAttr, columnTmp)) {
					resultLineColumnNames.insert(resultLineColumnNames.lastIndexOf(columnTmp), "> ");
					isResultColumn[t] = true;
				}
			}
		}
		if (!Arrays.toString(isResultColumn).contains(Settings.TRUE))
			for (int t = 0; t < isResultColumn.length; t++) {
				String columnTmp = resultMatrix[t][0];
				if (dBQuery.getIsColumnNull() != null && dBQuery.getIsColumnNull().containsKey(columnTmp)
						&& dBQuery.getIsColumnNull().get(columnTmp))
					isResultColumn[t] = false;
				else
					isResultColumn[t] = true;
			}

		// start building root node (label node) and it sub table
		// firstly, create label node (root node) that will contain a table (group of
		// rows)

		List<String> groupByColumns = dBQuery.getGroupByColumns();

		int colNr = 0, rowNr = 1;
		List<String> groupBy = new LinkedList<String>();
		String groupByValue;
		Boolean hasGroupBy = false;
		String newChildLabel = dBQuery.getSQLQueryLabel().toUpperCase().trim();

		for (String groupbyColumn : groupByColumns) {
			for (colNr = 0; colNr < resultMatrix.length; colNr++) {
				if (columnNamesUpperCase.get(colNr).equals(groupbyColumn)) {
					for (rowNr = 1; rowNr < resultMatrix[0].length; rowNr++) {// Anfang Schleife durch rows
						groupByValue = getColumnCellValueIdentifier(groupbyColumn, resultMatrix[colNr][rowNr]);
						if (!groupBy.contains(groupByValue)) {
							groupBy.add(groupByValue);
							hasGroupBy = true;
						}
					}
				}
			}
		}

		if (groupBy.size() == 0) {
			hasGroupBy = false;
			groupBy.add(newChildLabel);
		}

		newChild = result.insertDeep(new LabelNode(newChildLabel));
		newChild.dBQuery = dBQuery;

		parentChild = newChild.getParent();
		Set<String> parentTransmittedCond = (parentChild != null && parentChild.dBQuery != null)
				? parentChild.dBQuery.getTransmittedConditionColumns()
				: new HashSet<String>();

		Map<Integer, Boolean> processedRowResult = new HashMap<Integer, Boolean>();
		String connectionName = dBQuery.getConnectionName();

		for (String groupByItem : groupBy) {
			DataNode newChildSub;
			if (!hasGroupBy) {
				newChildSub = newChild;
			} else {
				newChildSub = new LabelNode(groupByItem); // result.insertDeep(new DataNode(groupByItem));
				newChildSub.dBQuery = dBQuery;
				newChildSub.isGroupbyNode = true;
				newChild.insertDeep(newChildSub);
			}

			// decide which row should be displayed. start building filtering sub rows of
			// query
			colNr = 0;
			rowNr = 1;
			int subRowIndex = 0;

			resultLineColumnNamesString = resultLineColumnNames.toString().substring(0,
					resultLineColumnNames.length() - Settings.dataSeperator.length());

			for (rowNr = 1; rowNr < resultMatrix[0].length; rowNr++) {
				boolean rowMatchesInheritedConditions = true; // check if row sastify the condition to display in the
																// grid !!
				Map<String, String> rowValues = new LinkedHashMap<String, String>();
				Set<String> errorColumns = new TreeSet<String>();

				for (colNr = 0; colNr < resultMatrix.length; colNr++) {
					columnName = columnNamesUpperCase.get(colNr);
					cellValue = resultMatrix[colNr][rowNr];
					rowValues.put(columnName.toUpperCase(), cellValue);

					if (processedRowResult.containsKey(rowNr) && processedRowResult.get(rowNr) == true) { // row already
																											// processed,
																											// skip it
						rowMatchesInheritedConditions = false;
						break;
					}

					if (inheritedJointCond != null // && dBQuery.isExecuteOnFirstRun()
							&& inheritedJointCond.containsKey(columnName)) {
						TransmittedCondition inhertedCondi = inheritedJointCond.get(columnName);
						if (inhertedCondi.getValue() == null ||
								(dBQuery.getMaxInheritanceDepth() >= inhertedCondi.getDepth()
										// && !resultMatrix[colNr][rowNr].equals(inhertedCondi.getValue()))
										&& (cellValue == null || !Pattern.matches(cellValue, inhertedCondi.getValue())))

						) {
							rowMatchesInheritedConditions = false;
							break;
						}
					}

					if (groupBy.size() > 1 && groupByColumns.contains(columnName)) {
						if (!groupByItem.equals(getColumnCellValueIdentifier(columnName, cellValue))) {
							rowMatchesInheritedConditions = false;
							break;
						}
					}

					for (TransmittedCondition cond : forwardJointCond.values()) {
						if (cond.getIdentifier().equals(columnName)) {
							cond.setValue(cellValue);
							break;
						}
					}

					// assign filter conditions cor current node (newChild)
					if (parentTransmittedCond.contains(columnName)) {
						newChildSub.setFilterCondition(columnName, cellValue);
					}
					// display lookup value
					String displayCellValue = DBService.isNullOrEmptyValue(cellValue) ? "" : cellValue;
					if (dBQuery.isLookupColumn(columnName, connectionName))
						displayCellValue = getCachedDataValue(connectionName, columnName,
								displayCellValue, dBQuery.getQueryParams());

					// display Key column in hierarchy
					// AAA
					// BB
					// CC
					if (Utils.equals(columnName, dBQuery.getKeyColumn())
							&& resultMatrix[resultMatrix.length - 1][0] == Settings.labelHierarchy) {
						displayCellValue = Utils.multiplyChars("   ",
								Utils.countString(resultMatrix[resultMatrix.length - 1][rowNr], ">"))
								+ displayCellValue;
					}

					resultLine.append(displayCellValue + Settings.dataSeperator);
				}

				processedRowResult.put(rowNr, rowMatchesInheritedConditions);

				if (rowMatchesInheritedConditions) {
					resultLineString = resultLine.toString().substring(0,
							resultLine.length() - Settings.dataSeperator.length());

					if (isMatchingExpression(dBQuery, rowValues, errorColumns)) {
						dBQuery.setError(true);
						if (errorColumns != null && errorColumns.size() > 0) {
							String[] arrayDisplayValues = resultLineString.split(Settings.dataSeperator);
							for (colNr = 0; colNr < arrayDisplayValues.length; colNr++) {
								columnName = columnNamesUpperCase.get(colNr);
								if (errorColumns.contains(columnName))
									arrayDisplayValues[colNr] = Settings.errorPrefix + arrayDisplayValues[colNr];
							}
							// System.out.println(rowValues);
							resultLineString = String.join(Settings.dataSeperator, arrayDisplayValues);
							errorColumns.clear();
						}
					}

					DataNode newestChild = new DataNode(resultLineColumnNamesString, resultLineString, newChildSub);

					newestChild.setIsResultColumn(isResultColumn);
					newestChild.setNodeIndex(++subRowIndex); // set row index
					newestChild.dBQuery = dBQuery;

					newChildSub.insertOnLevel(newestChild);

					if (!isShowFlat) {
						if (!dBQuery.subQueries.isEmpty()) {
							int t = 0;
							while (dBQuery.subQueries.size() > t) {
								generateJointDisplayRek(dBQuery.subQueries.get(t), forwardJointCond, level + 1,
										newestChild,
										viewOption, isFirstRun, isShowFlat);
								t++;
							}
						}
					}
				}
				resultLine.delete(0, resultLine.length());
			}

			if (newChildSub != null && newChildSub.getChildren().size() == 0) { // display null (empty) row
				if (hasGroupBy && newChildSub.isGroupbyNode)
					newChild.getChildren().remove(newChildSub);
				else if ((dBQuery.isSuppressDisplayIfNoData()
						&& (dBQuery.isExecuteOnFirstRun() || dBQuery.isDataRetrievalTriggeredByGUI()))) {
					result.getChildren().remove(newChildSub);
				}
			}
			resultLine.delete(0, resultLine.length());
		} // end for loop

	}

	public static List<DataNode> getSelectedDataNodes(TreePath[] tp) {
		LinkedList<DataNode> selectedNodes = new LinkedList<DataNode>();
		if (tp != null && tp.length > 0) {
			for (int t = 0; t < tp.length; t++) {
				DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) (tp[t].getLastPathComponent());
				if (selNode.getUserObject() instanceof DataNode) {
					selectedNodes.add((DataNode) (selNode.getUserObject()));
				}
			}
		}

		return selectedNodes;
	}

	public static String generateSelectionInsert(DataNode node, TreePath[] tp, boolean batchInsert) {
		if (tp == null || tp.length == 0)
			return null;
		if (tp.length == 1
				&& ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject() instanceof String)
			return ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject().toString();

		return generateInsertSQL(node, getSelectedDataNodes(tp), batchInsert);
	}

	public static String generateSelectionUpdate(DataNode node, TreePath[] tp, boolean batchUpdate) {
		if (tp == null || tp.length == 0)
			return null;
		if (tp.length == 1
				&& ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject() instanceof String)
			return ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject().toString();

		return generateUpdateSQL(node, getSelectedDataNodes(tp), batchUpdate);
	}

	public static String generateTableInsert(DataNode node, int level, boolean batchInsert) {
		return generateInsertSQL(node, node.getChildren(), batchInsert);
	}

	public static Map<String, List<String>> getAutoCompletes(String[] columns) {
		Map<String, List<String>> autoCompletes = new HashMap<String, List<String>>();
		for (String column : columns) {
			List<String> columnCachedData = getCachedDataAsList(Main.connectionName, column.replace("&", ""),
					null);
			if (columnCachedData != null && columnCachedData.size() > 0) {
				for (int i = 0; i < columnCachedData.size(); i++) {
					if (columnCachedData.get(i).contains(Settings.dataSeperator))
						columnCachedData.set(i, columnCachedData.get(i).substring(0,
								columnCachedData.get(i).indexOf(Settings.dataSeperator)));
					else
						columnCachedData.set(i, columnCachedData.get(i));
				}
				autoCompletes.put(column, columnCachedData);
			}
		}
		return autoCompletes;
	}

	public static Map<String, String> getRecordsInput(String title, DataNode node, DataNode aNode) {
		List<DataNode> list = new LinkedList<DataNode>();
		list.add(aNode);
		return getRecordsInput(title, node, list);
	}

	public static Map<String, String> getRecordsInput(String title, Map<String, String> data, List<DBParam> queryParams,
			String[] replaceColumns) {
		Map<String, String> replaceValues = new LinkedHashMap<String, String>();
		String[] columns = Utils.convertToStringArray(data.keySet());
		String[] values = Utils.convertToStringArray(data.values());

		if (replaceColumns == null)
			replaceColumns = columns;

		Map<String, Integer> columnsIndexes = new LinkedHashMap<String, Integer>();
		for (int t = 0; t < columns.length; t++) {
			columnsIndexes.put(columns[t], t);
		}

		Map<String, List<String>> autoCompletes = new HashMap<String, List<String>>();

		for (String column : replaceColumns) {
			List<String> columnCachedData = getCachedDataAsList(Main.connectionName, column,
					queryParams);
			if (columnCachedData != null && columnCachedData.size() > 0) {
				for (int i = 0; i < columnCachedData.size(); i++) {
					if (columnCachedData.get(i).contains(Settings.dataSeperator))
						columnCachedData.set(i, columnCachedData.get(i).substring(0,
								columnCachedData.get(i).indexOf(Settings.dataSeperator)));
					else
						columnCachedData.set(i, columnCachedData.get(i));
				}
				autoCompletes.put(column, columnCachedData);
			}
			if (!replaceValues.containsKey(column)) {
				replaceValues.put(column, values[columnsIndexes.get(column)]);
			} else {
				if (replaceValues.get(column) == null
						|| !replaceValues.get(column).equalsIgnoreCase(values[columnsIndexes.get(column)]))
					replaceValues.put(column, "");
			}
		}

		if (replaceColumns.length > 0) {
			replaceValues = MyInputDialog.instance(Main.mainScreen.getFrame()).showMapInput(null,
					title, replaceValues,
					autoCompletes);
		}
		if (replaceValues != null) {
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
				entry.setValue(getKeyFromDisplayKeyValue(entry.getValue()));
			}
		}
		return replaceValues;
	}

	public static Map<String, String> getRecordsInput(String title, DataNode node, List<DataNode> selectedNodes) {
		String[] replaceColumns = node.dBQuery.getReplaceValuesArray();
		Map<String, String> replaceValues = new LinkedHashMap<String, String>();

		String[] columns = node.getFirstChild().getResultColumnArray();

		if (replaceColumns == null)
			replaceColumns = columns;

		Map<String, Integer> columnsIndexes = new LinkedHashMap<String, Integer>();
		for (int t = 0; t < columns.length; t++) {
			columnsIndexes.put(columns[t], t);
		}

		Map<String, List<String>> autoCompletes = new HashMap<String, List<String>>();

		for (int n = 0; n < selectedNodes.size(); n++) {
			DataNode aNode = selectedNodes.get(n);
			String[] values = aNode.getResultValueArray();
			for (String column : replaceColumns) {
				List<String> columnCachedData = getCachedDataAsList(Main.connectionName, column,
						node.dBQuery.getQueryParams());
				if (columnCachedData != null && columnCachedData.size() > 0) {
					for (int i = 0; i < columnCachedData.size(); i++) {
						if (columnCachedData.get(i).contains(Settings.dataSeperator))
							columnCachedData.set(i, columnCachedData.get(i).substring(0,
									columnCachedData.get(i).indexOf(Settings.dataSeperator)));
						else
							columnCachedData.set(i, columnCachedData.get(i));
					}
					autoCompletes.put(column, columnCachedData);
				}
				if (!replaceValues.containsKey(column)) {
					replaceValues.put(column, values[columnsIndexes.get(column)]);
				} else {
					if (replaceValues.get(column) == null
							|| !replaceValues.get(column).equalsIgnoreCase(values[columnsIndexes.get(column)]))
						replaceValues.put(column, "");
				}
			}
		}
		if (replaceColumns.length > 0) {
			replaceValues = MyInputDialog.instance(Main.mainScreen.getFrame()).showMapInput(null,
					title, replaceValues,
					autoCompletes);
		}
		if (replaceValues != null) {
			for (Map.Entry<String, String> entry : replaceValues.entrySet()) {
				entry.setValue(getKeyFromDisplayKeyValue(entry.getValue()));
			}
		}
		return replaceValues;
	}

	public static String generateInsertSQL(DataNode node, List<DataNode> selectedNodes, boolean batchInsert) {

		String firstDelimiter = "";
		String separator = ",";
		Map<String, String> replaceValues = new LinkedHashMap<String, String>();
		if (batchInsert) {
			replaceValues = getRecordsInput(
					"Generate Insert SQL for " + String.valueOf(selectedNodes.size()) + " records.", node,
					selectedNodes);
		}

		// Table Title
		String title = node.values;

		// Column Names
		String[] columns = node.getFirstChild().getResultColumnArray();

		String tableName;
		if (node.dBQuery != null && !node.dBQuery.getTableName().isEmpty())
			tableName = node.dBQuery.getTableName();
		else
			tableName = "[TABLE]";

		StringBuilder result = new StringBuilder("");

		// Values
		for (int n = 0; n < selectedNodes.size(); n++) {
			DataNode aNode = selectedNodes.get(n);
			String[] values = aNode.getRawValuesArray();

			if (!batchInsert) {
				replaceValues = getRecordsInput(
						"Generate Insert SQL for record #" + String.valueOf(n + 1) + ".", node,
						aNode);
			}
			result.append(DBService.generateInsertSQL(tableName, columns, values, replaceValues, separator));
		}
		return firstDelimiter + "-- [" + title + "]" + "\n" + result.toString();
	}

	public static String generateUpdateSQL(DataNode node, List<DataNode> selectedNodes) {
		return generateUpdateSQL(node, selectedNodes, false);
	}

	public static String generateUpdateSQL(DataNode node, List<DataNode> selectedNodes, boolean batchUpdate) {

		StringBuilder result = new StringBuilder("");
		String firstDelimiter = "";
		String separator = ",";
		String newLine = "\n\t";

		Map<String, String> replaceValues = new LinkedHashMap<String, String>();

		if (batchUpdate) {
			replaceValues = getRecordsInput(
					"Generate Update SQL for " + String.valueOf(selectedNodes.size()) + " records.", node,
					selectedNodes);
		}

		// Table Title
		String title = node.values;
		result.append(firstDelimiter + "-- [" + title + "]" + "\n");

		// // Column Names
		String[] columns = node.getFirstChild().getResultColumnArray();

		Set<String> primaryKeys = new LinkedHashSet<String>();

		String tableName;
		if (node.dBQuery != null && !node.dBQuery.getTableName().isEmpty()) {
			tableName = node.dBQuery.getTableName();
			primaryKeys = node.dBQuery.getPrimaryColumns();
		} else {
			tableName = "[TABLE]";
			for (int t = 0; t < columns.length; t++) {
				primaryKeys.add(columns[t]);
			}
		}

		// Values
		for (int n = 0; n < selectedNodes.size(); n++) {

			DataNode aNode = selectedNodes.get(n);
			String[] values = aNode.getRawValuesArray();
			if (!batchUpdate) {
				replaceValues = getRecordsInput(
						"Generate Update SQL for record #" + String.valueOf(n + 1) + " records.", node,
						aNode);
			}

			result.append(DBService.generateUpdateSQL(tableName, columns, values, replaceValues, primaryKeys));

			result.append(";\n----\n");
		}
		return result.toString();
	}

	public static List<List<String>> getDataForExcelExport(DataNode node, String separator, boolean includeSubTrees,
			int level) {
		List<List<String>> data = new java.util.LinkedList<List<String>>();

		String[] columns = node.getFirstChild().getColumnArray();
		data.add(Arrays.asList(columns));

		for (int n = 0; n < node.getChildren().size(); n++) {
			DataNode aNode = node.getChildren().get(n);
			String[] values = aNode.getRawValuesArray();
			data.add(Arrays.asList(values));
		}

		return data;
	}

	public static Sheet generateExcel(DataNode node, Sheet sheet, boolean includeSubTrees, int level) {
		String excelFilePath = "";
		FileInputStream inputStream = null;
		Workbook workbook = null;
		if (node == null || node.getFirstChild() == null)
			return sheet;

		if (level == 0)
			excelFilePath = Utils.selectFile("xlsx", Settings.getOutputFolder() + "/excels");

		try {
			String title = node.values;
			if (level == 0) {
				File newFile = new File(excelFilePath);

				// If file not exists we create a new workbook
				if (!newFile.exists()) {
					workbook = new HSSFWorkbook();
				} else {
					// If file exists, we open an input stream channel to it
					inputStream = new FileInputStream(newFile);
					// Provide the input stream to WorkbookFactory
					workbook = Utils.createWorkbook(inputStream);
				}

				sheet = workbook.getSheet(title);
				if (sheet == null)
					sheet = workbook.createSheet(title); // workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0)
															// : workbook.createSheet("Comments");
			}
			int rowCount = sheet.getLastRowNum();
			if (rowCount < 0)
				rowCount = 0;

			String[] columns = node.getFirstChild().getColumnArray();
			boolean[] isResultColumn = node.getFirstChild().getIsResultColumn();
			Row row = sheet.createRow(++rowCount);
			int columnCount = 0;
			row.createCell(level).setCellValue(title);
			row = sheet.createRow(++rowCount);

			for (int t = 0; t < columns.length; t++) {
				if (isResultColumn[t]) {
					Cell cell = row.createCell(columnCount++ + level);
					cell.setCellValue((String) columns[t]);
				}
			}
			for (int n = 0; n < node.getChildren().size(); n++) {
				DataNode aNode = node.getChildren().get(n);
				String[] values = aNode.getRawValuesArray();
				row = sheet.createRow(++rowCount);
				columnCount = 0;
				for (int t = 0; t < values.length; t++) {
					if (isResultColumn[t]) {
						Cell cell = row.createCell(columnCount++ + level);
						cell.setCellValue((String) values[t]);
					}
				}
				if (includeSubTrees && !aNode.getChildren().isEmpty()) {
					for (DataNode subNode : aNode.getChildren()) {
						sheet = generateExcel(subNode, sheet, includeSubTrees, level + 1);
					}
				}
			}

			if (level == 0) {

				inputStream.close();

				FileOutputStream outputStream = new FileOutputStream(excelFilePath);
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();
				Utils.showToast("File " + excelFilePath + " saved !!!");
			}

		} catch (IOException | EncryptedDocumentException ex) {
			LogManager.getLogger().error(ex);
		}
		return sheet;
	}

	public static String generateTableDisplay(DataNode node, String separator, boolean includeSubTrees, int level) {
		StringBuilder result = new StringBuilder("");
		String firstDelimiter = "";
		firstDelimiter = Utils.multiplyChars(separator.charAt(0), level);
		// Table Title
		String title = node.values;
		result.append(firstDelimiter + "[" + title + "]" + "\n");

		// Column Names
		StringBuilder columnNames = new StringBuilder();
		String[] columns = node.getFirstChild().getColumnArray();
		boolean[] isResultColumn = node.getFirstChild().getIsResultColumn();
		for (int t = 0; t < columns.length; t++) {
			if (isResultColumn[t])
				columnNames.append(columns[t].replace(separator, ".") + separator);
		}
		columnNames.deleteCharAt(columnNames.length() - 1);
		result.append(firstDelimiter + columnNames + "\n");
		// Values
		for (int n = 0; n < node.getChildren().size(); n++) {
			DataNode aNode = node.getChildren().get(n);
			String[] values = aNode.getRawValuesArray();
			result.append(firstDelimiter);
			for (int t = 0; t < values.length; t++) {
				if (isResultColumn[t])
					result.append(values[t].replace(separator, ".") + separator);
			}
			result.deleteCharAt(result.length() - 1).append("\n");
			if (includeSubTrees && !aNode.getChildren().isEmpty()) {
				for (DataNode subNode : aNode.getChildren()) {
					result.append(generateTableDisplay(subNode, separator, includeSubTrees, level + 1));
					if (node.getChildren().size() > n + 1) {
						result.append(firstDelimiter + title + "\n");
						result.append(firstDelimiter + columnNames + "\n");
					}
				}
			}
		}
		return result.toString(); // "/*\n" + result.toString() + "*/";
	}

	public static String generateSelectionDisplay(TreePath[] tp, String separator, boolean includeSubTrees) {
		if (tp == null || tp.length == 0)
			return null;
		if (tp.length == 1
				&& ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject() instanceof String)
			return ((DefaultMutableTreeNode) (tp[0].getLastPathComponent())).getUserObject().toString();
		StringBuilder result = new StringBuilder("");
		String firstDelimiter = "";
		String title = null;
		boolean[] isResultColumn = null;
		StringBuilder columnNames = new StringBuilder();
		LinkedList<DataNode> selectedNodes = new LinkedList<DataNode>();
		int levelOffset = 9999, level = 0;
		for (int t = 0; t < tp.length; t++) {
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) (tp[t].getLastPathComponent());
			if (selNode.getUserObject() instanceof DataNode) {
				selectedNodes.add((DataNode) (selNode.getUserObject()));
				if (levelOffset > ((DefaultMutableTreeNode) (tp[t].getLastPathComponent())).getLevel())
					levelOffset = ((DefaultMutableTreeNode) (tp[t].getLastPathComponent())).getLevel();
			}
		}
		for (int i = 0; i < selectedNodes.size(); i++) {
			level = (((DefaultMutableTreeNode) (tp[i].getLastPathComponent())).getLevel() - levelOffset) / 2;
			// Geteilt durch zwei, weil jede Ebene aus zwei levels bestehtLabel+Daten
			firstDelimiter = Utils.multiplyChars(separator.charAt(0), level + 1);

			if (!selectedNodes.get(i).isLabel && selectedNodes.get(i).getParent() != null) {
				if (title == null || title.equals(selectedNodes.get(i).getParent().values) == false) {
					// Table Title
					title = selectedNodes.get(i).getParent().values;
					result.append(firstDelimiter + "[" + title + "]" + "\n");

					// Column Names
					isResultColumn = selectedNodes.get(i).getIsResultColumn();
					String[] columns = selectedNodes.get(i).getColumnArray();
					for (int t = 0; t < columns.length; t++) {
						if (isResultColumn[t])
							columnNames.append(columns[t].replace(separator, ".") + separator);
					}
					columnNames.deleteCharAt(columnNames.length() - 1);
					result.append(firstDelimiter + columnNames + "\n");
				}
				// Values
				result.append(firstDelimiter);
				String[] values = selectedNodes.get(i).getRawValuesArray();
				for (int t = 0; t < values.length; t++) {
					if (isResultColumn[t])
						result.append(values[t].replace(separator, ".") + separator);
				}
				result.deleteCharAt(result.length() - 1).append("\n");
				if (includeSubTrees && !selectedNodes.get(i).getChildren().isEmpty()) {
					for (DataNode subNode : selectedNodes.get(i).getChildren()) {
						result.append(generateTableDisplay(subNode, separator, includeSubTrees, level + 2));
					}
				}
			}
		}

		return result.toString(); // "/*\n" + result.toString() + "*/";
	}

	public static String showTreeStructureDebug(DefaultMutableTreeNode tree, boolean includeSubTrees) {
		if (tree == null || tree.getUserObject() == null)
			return "Empty Tree";
		StringBuilder result = new StringBuilder("");
		result.append(rekShowTreeStructureDebug(tree, includeSubTrees, 1));

		Set<String> tempTables = DBService.getTempTables();
		result.append("Temp Tables:\n");
		for (String tempTable : tempTables) {
			result.append(tempTable).append(",");
		}
		return result.toString();
	}

	public static String rekShowTreeStructureDebug(DefaultMutableTreeNode tree, boolean includeSubTrees, int level) {
		StringBuilder result = new StringBuilder("");
		String spaces = Utils.multiplyChars(' ', (level - 1) * 10);
		DataNode node;
		String label;
		if (tree.getUserObject() instanceof DataNode) {
			node = (DataNode) tree.getUserObject();
			result.append(spaces + node.toString() + "\n");
			// result.append(spaces + node.getValueArray() + "\n");
			result.append(spaces + "Column is always null in Data Branch: " + node.getIsColumnNull() + "\n");
			result.append(spaces + "Is query on demand: " + node.isQueryOnDemand() + ", Is ancestor query on demand: "
					+ node.isAncestorQueryOnDemand() + "\n");
			result.append(spaces + "Parent Node: " + node.getParent() + "\n");
			result.append(spaces + "First Child: " + node.getChildren() + "\n");
		}
		if (tree.getUserObject() instanceof String) {
			label = (String) tree.getUserObject();
			result.append(spaces + "Label: " + label + "\n");
		}
		if (includeSubTrees && tree.getChildCount() > 0)
			for (int t = 0; t < tree.getChildCount(); t++) {
				result.append(rekShowTreeStructureDebug((DefaultMutableTreeNode) tree.getChildAt(t), includeSubTrees,
						level + 1));
			}
		return result.toString();
	}

	public static List<TransmittedCondition> getApplicableConditionFromNodeParents(DataNode currNode,
			List<TransmittedCondition> transmittedConditions, boolean includeNotApplicableCond) {
		List<TransmittedCondition> applicableConditions = new LinkedList<TransmittedCondition>();

		if (currNode == null || transmittedConditions == null || currNode.getColumnArray() == null
				|| transmittedConditions == null || currNode.getColumnArray().length == 0
				|| transmittedConditions.size() == 0)
			return applicableConditions;

		for (TransmittedCondition transmCondition : transmittedConditions) {
			DataNode currNodeCopy = currNode;
			if (transmCondition.inheritanceIsNotApplicable() == false || includeNotApplicableCond == true) {
				while (currNodeCopy != null) {
					int applCondCount = applicableConditions.size();
					for (int t = 0; t < currNodeCopy.getColumnArray().length; t++) {
						if (transmCondition.getIdentifier().equals(currNodeCopy.getColumnArray()[t].toUpperCase())) {
							applicableConditions.add(new TransmittedCondition(transmCondition.getIdentifier(),
									currNodeCopy.getValueArray()[t], transmCondition.getDepth(),
									transmCondition.inheritanceIsNotApplicable()));
						}
					}
					if (applCondCount == applicableConditions.size()
							&& currNodeCopy.getParent() != null
							&& currNodeCopy.getParent().getParent() != null
							&& currNodeCopy.getParent().getParent() instanceof DataNode
							&& currNodeCopy.getParent().getParent().getColumnArray() != null)
						currNodeCopy = currNodeCopy.getParent().getParent();
					else
						currNodeCopy = null;
				}
			}
		}
		return applicableConditions;
	}

	public static String generateSqlQueryFromTreeElement(
			DefaultMutableTreeNode currNode, List<DBQuery> listAllQueryDefinitions) {
		return generateSqlQueryFromTreeElement(currNode, listAllQueryDefinitions, Settings.actionView, null);
	}

	// generate sql from treeview
	public static String generateSqlQueryFromTreeElement(
			DefaultMutableTreeNode currNode, List<DBQuery> listAllQueryDefinitions, String action, TreePath[] paths) {
		String selection = "";

		String dataNodeLabel = "";
		boolean labelSelection = true;
		DataNode criteriaNode = null;
		DataNode parentNode;
		List<TransmittedCondition> transmConditions = null;
		String originalSql = "", orderBy = "";
		StringBuilder whereClause = new StringBuilder();

		if (currNode.getUserObject() instanceof DataNode) {

			criteriaNode = (DataNode) (currNode.getUserObject());
			parentNode = criteriaNode.getParent();
			if (parentNode.isGroupbyNode)
				dataNodeLabel = parentNode.getParent().getRawValues();
			else
				dataNodeLabel = parentNode.getRawValues();

		} else if (currNode.getUserObject() instanceof String) {

			if (currNode.getParent().getParent() != null
					&& ((DefaultMutableTreeNode) currNode.getParent().getParent()).getUserObject() instanceof DataNode)
				currNode = (DefaultMutableTreeNode) currNode.getParent();

			if (currNode != null) {
				dataNodeLabel = (String) (currNode.getUserObject());
				labelSelection = true;
				if (((DefaultMutableTreeNode) currNode.getParent()).getUserObject() instanceof DataNode)
					criteriaNode = (DataNode) (((DefaultMutableTreeNode) currNode.getParent()).getUserObject());
			}
		}

		DBQuery selectedQueryDef = null;
		for (DBQuery a : listAllQueryDefinitions) {
			if (a.getSQLQueryLabel().equalsIgnoreCase(dataNodeLabel)) {
				originalSql = a.getSqlQuery();
				originalSql = Utils.replaceIgnoreCase(originalSql, " from ", "\nfrom ");
				originalSql = Utils.replaceIgnoreCase(originalSql, " from ", "\nfrom ");
				originalSql = Utils.replaceIgnoreCase(originalSql, " where ", "\nwhere ");

				transmConditions = a.transmittedConditions;
				selectedQueryDef = a;
				break;
			}
		}

		if (action.equalsIgnoreCase(Settings.actionView)) { // View SQL
			if (selectedQueryDef != null)
				originalSql = selectedQueryDef.applyParams(originalSql);

			if (originalSql != null && listAllQueryDefinitions != null
					&& listAllQueryDefinitions.size() > 0
					&& listAllQueryDefinitions.get(0).getDb() != null
					&& listAllQueryDefinitions.get(0).getDb().equals("MySql")
					&& originalSql.toLowerCase().lastIndexOf("order by") > 0) {

				orderBy = " " + originalSql.substring(originalSql.toLowerCase().lastIndexOf("order by"));
				// orderBy = removeAliases(orderBy);
				if (!orderBy.toLowerCase().contains("where ") || !orderBy.toLowerCase().contains("from ")
						|| !orderBy.toLowerCase().contains("select ")) {
				}
			}

			if (labelSelection == false) {// Node Selection
				for (int t = 0; t < criteriaNode.getColumnArray().length && t < 15; t++) {
					if (criteriaNode.getRawValuesArray()[t].equalsIgnoreCase("null")) {
						whereClause.append(" AND " + criteriaNode.getColumnArray()[t] + " is null ");
					} else {
						whereClause.append(" AND " + criteriaNode.getColumnArray()[t] + " = '"
								+ getKeyFromDisplayKeyValue(criteriaNode.getRawValuesArray()[t])
								+ "'");
					}
				}
			} else {// Label Selection
				Map<String, String> whereKeyValues = new LinkedHashMap<String, String>();
				List<TransmittedCondition> applCond = getApplicableConditionFromNodeParents(criteriaNode,
						transmConditions, true);
				for (TransmittedCondition retrivedCondition : applCond) {
					whereKeyValues.put(retrivedCondition.getIdentifier(),
							getKeyFromDisplayKeyValue(retrivedCondition.getValue()));
				}
				if (whereKeyValues.size() == 0) {
					if (paths.length > 0) {
						for (TreePath path : paths) {
							Map<String, String> tmp = ((DataNode) ((DefaultMutableTreeNode) path.getLastPathComponent())
									.getUserObject()).getPrimaryKeyValues();
							for (Map.Entry<String, String> entry : tmp.entrySet()) {
								if (whereKeyValues.containsKey(entry.getKey())
										&& !whereKeyValues.get(entry.getKey()).contains(entry.getValue())) {
									whereKeyValues.put(entry.getKey(), whereKeyValues.get(entry.getKey())
											+ Settings.dataSeperator + entry.getValue());
								} else {
									whereKeyValues.put(entry.getKey(), entry.getValue());
								}
							}
						}
					} else {
						whereKeyValues = criteriaNode.getPrimaryKeyValues();
					}
				}
				for (Map.Entry<String, String> entry : whereKeyValues.entrySet()) {
					if (entry.getValue().equalsIgnoreCase("null")) {
						// building where condition for element View Table SQL
						whereClause.append(" AND " + entry.getKey() + " is null ");
					} else if (entry.getValue().contains(Settings.dataSeperator)) {
						whereClause.append(" AND " + entry.getKey() + " in ('"
								+ entry.getValue().replace(Settings.dataSeperator, "','") + "') ");

					} else {
						whereClause.append(" AND " + entry.getKey() + " = '" + entry.getValue() + "' ");
					}

				}
			}
			if (whereClause.length() > 0)
				selection = "SELECT * FROM (\n\t" + originalSql + "\n) _tmp_ WHERE "
						+ whereClause.toString().replaceFirst("AND ", "") + " " + orderBy;
			else
				selection = originalSql;

		} else if (action.equalsIgnoreCase(Settings.actionEdit)) { // Edit SQL
			selection = originalSql;
		} else if (action.equalsIgnoreCase(Settings.actionInsert)) { // Insert SQL
			selection = originalSql;
		}
		return selection;
	}

	public static MyPane generateDataPane(// Erzeugt und zeigt den EspressoView
			String dbType1, String connectionName1, String host1, String port1,
			String connType1, String serviceName1, String userName1,
			String password1, String file1, final String execImmediateExpression, boolean isShowFlat,
			List<String> files) {
		DataPane pane = new DataPane(dbType1, connectionName1, host1, port1, connType1, serviceName1, userName1,
				password1, file1, execImmediateExpression);
		pane.setFiles(files);
		pane.isShowFlat = isShowFlat;
		MyPane result = pane.generateDataPane(true);
		return result == null ? pane : result;
	}

	public static MyPane generateDataPane(String file1) {
		DataPane pane = new DataPane("", "",
				"", "",
				"",
				"",
				"",
				"", file1, "");
		pane.setFiles(getFiles(file1));
		pane.connectionName = Settings.getLastConnectionName();
		pane.isShowFlat = Settings.showFlatTree;
		MyPane result = pane.generateDataPane(true);
		return result == null ? pane : result;
	}

	public static String generateTempTable(Connection conn, DBQuery dBQuery, Logger logger) {
		if (!Settings.allowTempTables)
			return null;
		if (logger == null)
			logger = LogManager.getLogger();

		String tempTableName = "";
		String sql = dBQuery.getSqlQueryWithParams();
		if (sql.trim().isEmpty())
			return tempTableName;

		try {
			DatabaseMetaData db = conn.getMetaData();
			String colName;
			if (conn.getMetaData().getDatabaseProductName().toLowerCase()
					.contains("oracle"))// create a temporary table in Oracle
			{
				// dBQuery.db = "Oracle";
				String[] types = { "TABLE" };
				ResultSet rs = db.getTables(null, db.getUserName(), Settings.tempTablePrefix + "%", types);
				int tabCounter = 1;
				while (rs.next()) {
					tabCounter++;
				}
				rs.close();
				tempTableName = Settings.tempTablePrefix + tabCounter
						+ dBQuery.getSQLQueryLabel().replace(" ", "_").replace("/", "_").replace(".", "pkt")
								.toUpperCase();
				if (tempTableName.length() > 30)
					tempTableName = tempTableName.substring(0, 29);

				sql = "create global temporary table " + tempTableName + " as select * from (\n"
						+ sql + "\n) TMPTABLE where 1=0";
				DBService.executeSql(sql, conn);

				Main.dbTempItems.add(tempTableName);
				rs = db.getColumns(null, db.getUserName(), tempTableName, "%");
				while (rs.next()) {
					colName = rs.getString("COLUMN_NAME");
					if (dBQuery.getResultColumns().isEmpty() || !dBQuery.getResultColumns().contains(colName))
						dBQuery.getResultColumns().add(colName);
				}
				rs.close();
				logger.debug("Temporary table " + tempTableName + " (" + dBQuery.getResultColumns() + ") created");
			}

			if (conn.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql"))// create a temporary table
																							// in mysql
			{
				// dBQuery.db = "MySql";
				String[] types = { "TABLE" };
				ResultSet rs = db.getTables(null, db.getUserName(), Settings.tempTablePrefix + "%", types);
				tempTableName = Settings.tempTablePrefix
						+ dBQuery.getSQLQueryLabel().replace(" ", "_").replace("/", "_").replace(".", "pkt")
								.toUpperCase();
				if (tempTableName.length() > 30)
					tempTableName = tempTableName.substring(0, 29);
				sql = "create temporary table " + tempTableName + " as select * from (\n"
						+ sql + "\n) TMPTABLE where 1=0";

				DBService.executeSql(sql, conn);

				Main.dbTempItems.add(tempTableName);
				PreparedStatement colNames = conn.prepareStatement("select * from " + tempTableName);// in mySQL you
																										// cannot
																										// retrieve the
																										// temporary
																										// table name
																										// and columns,
																										// so you have
																										// find them out
																										// the hard way
				ResultSetMetaData rsMeta = colNames.executeQuery().getMetaData();
				for (int t = 1; t <= rsMeta.getColumnCount(); t++) {
					colName = rsMeta.getColumnName(t);
					if (dBQuery.getResultColumns().isEmpty() || !dBQuery.getResultColumns().contains(colName))
						dBQuery.getResultColumns().add(colName);
				}
				logger.debug("Temporary table " + tempTableName + " (" + dBQuery.getResultColumns() + ") created");
				while (rs.next()) {

					dBQuery.getResultColumns().add(rs.getString("COLUMN_NAME"));
				}
				rs.close();
			}

		} catch (SQLException sq) {
			MyDialog.showException(sq,
					"Failed to generate Temp Table " + dBQuery.getSQLQueryLabel() + " (" + tempTableName + "):\n"
							+ DBService.cleanSql(sql));
			DBService.dropAllTempTables();
			tempTableName = null;
		}
		if (dBQuery.getResultColumns().size() == 0)
			dBQuery.setResultColumns(dBQuery.getAllResultColumns());
		return tempTableName;
	}

	public static boolean generateAllTempTables(Connection conn, DBQuery dBQuery, Logger logger) {
		if (logger == null)
			logger = LogManager.getLogger();

		Main.startThread("Preparing temp tables");

		boolean success = true;
		if (dBQuery.getTempTable() == null) {
			dBQuery.setTempTable(generateTempTable(conn, dBQuery, logger));
			if (dBQuery.getTempTable() == null)
				return false;

			if (dBQuery.subQueries.size() > 0) {
				for (DBQuery kindAnfrage : dBQuery.subQueries) {
					success = generateAllTempTables(conn, kindAnfrage, logger);
					if (!success)
						break;
				}
			}
		}

		Main.stopThread();
		return success;
	}

	public static boolean isMatchingExpression(DBQuery dbQuery, Map<String, String> vars, Set<String> columns) {
		vars.put("rows_count", String.valueOf(dbQuery.getRowsCount()));
		boolean result = Utils.isMatchingExpression(dbQuery.getCheckCondition(), vars, columns);
		return result;
	}
}
