package com.rs2.core.data;

import java.util.*;
import java.sql.*;

import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyLinkedMap;
import com.rs2.core.components.MyTreeMap;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.base.MyService;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.Utils;

public class DBQuery extends XmlElement {
	public static DBService DBService = new DBService();
	public static DataViewerService DataViewerService = new DataViewerService();

	private String uid;

	public DBQuery superSQLQuery;
	public List<DBQuery> subQueries; // sqlQuery children
	private List<String> resultColumns, allResultColumns, groupByColumns; // variable set of columns to be displayed
	private boolean dataGenerated, conditionsReceived, suppressDisplayIfNoData, executeOnFirstRun,
			dataRetrievalTriggeredByGUI;
	private boolean emptyResultset;
	private String sqlQuery, sqlQueryLabel, rebuiltQuery, dynamicSqlQuery, queryTitle; // the SQL Query
	private String tableName, connectionName;
	private Set<String> primaryColumns;
	private Set<String> transmittedConditionColumns;// the column names supposed to be inherited by the children in the
													// where-clause of this sqlQuery
	public List<TransmittedCondition> transmittedConditions; // the where clause supposed to be inherited by the
																// children of
	// this sqlQuery
	public StringBuilder inheritedConditions; // columns from the super-sqlQuery that will be added to the where clause
												// of the
	// sqlQuery
	private int maxInheritanceDepth, maximumResultRows;
	private String emptyColumnDisplayMode = Settings.emptyDisplayModeSUPRESS;
	private Map<String, Boolean> isColumnNull;
	private String tempTable, db;
	private String[][] resultRowsMatrix;
	private List<List<String>> resultData;
	private List<DBParam> queryParams;
	private List<DBLookup> lookupColumns;
	private String checkCondition;
	private String parentColumn;
	private String keyColumn;
	private String queryType;
	private boolean hasError = false;

	private Connection connection;
	private ResultSet resultSet;
	private PreparedStatement statement;

	public DBQuery() {
		// super("");
		subQueries = new LinkedList<DBQuery>();
		resultColumns = new LinkedList<String>();
		groupByColumns = new LinkedList<String>();
		allResultColumns = new LinkedList<String>();
		isColumnNull = new HashMap<String, Boolean>();
		queryParams = new LinkedList<DBParam>();
		lookupColumns = new LinkedList<DBLookup>();
		transmittedConditionColumns = new HashSet<String>();
		this.transmittedConditions = new LinkedList<TransmittedCondition>();
	}

	public DBQuery(String query, String queryLabel) {
		this();
		this.sqlQuery = query;
		this.sqlQueryLabel = queryLabel;
	}

	public DBQuery(String query, String queryName, DBQuery superQuery, int maxInheritanceDepth) {
		this(query, queryName);
		if (superQuery != null) {
			this.superSQLQuery = superQuery;
			superQuery.subQueries.add(this);
		}
		if (maxInheritanceDepth >= 0)
			this.maxInheritanceDepth = maxInheritanceDepth;
		else
			this.maxInheritanceDepth = 99;
	}

	public void setProperties(Map<String, Object> properties) {
		if (properties == null)
			return;
		if (properties.containsKey(Settings.TagTITLE))
			this.setQueryTitle((String) properties.get(Settings.TagTITLE));
		if (properties.containsKey(Settings.TagTable))
			this.setTableName((String) properties.get(Settings.TagTable));
		if (properties.containsKey(Settings.TagParentColumn))
			this.setParentColumn((String) properties.get(Settings.TagParentColumn));
		// if (properties.containsKey(Settings.TagKey))
		// this.setKeyColumn((String) properties.get(Settings.TagKeyColumn));
	}

	@Override
	public String toString() {
		return this.getSQLQueryLabel() + ":" + this.getSqlQuery();
	}

	public String getDb() {
		if (db == null)
			db = "Oracle";
		return db;
	}

	public String getKeyColumn() {
		if (keyColumn != null)
			return keyColumn;
		List<String> columns = getResultColumns();
		for (String column : columns) {
			if (column.equalsIgnoreCase("id") || column.equalsIgnoreCase(Settings.keyColumnDefault)) {
				keyColumn = column;
				break;
			}
		}
		return keyColumn;
	}

	public void setKeyColumn(String value) {
		keyColumn = value;
	}

	public String getReplaceValue() {
		return getXmlTag(Settings.TagREPLACEVALUE).toUpperCase();
	}

	public String[] getReplaceValuesArray() {
		String a = getReplaceValue();
		if (a.isEmpty())
			return null;
		return a.split(",");
	}

	public String getCheckCondition() {
		if (checkCondition != null)
			return checkCondition;
		checkCondition = getXmlTag(Settings.TagCheckCondition);
		return checkCondition;
	}

	public void setCheckCondition(String value) {
		checkCondition = value;
	}

	public boolean hasError() {
		if (hasError == true)
			return hasError;
		for (DBQuery subQuery : subQueries) {
			if (subQuery.hasError())
				return true;
		}
		return false;
	}

	public void setError(Boolean value) {
		hasError = value;
	}

	public String getParentColumn() {
		return parentColumn;
	}

	public void setParentColumn(String value) {
		parentColumn = value;
	}

	public boolean isLabel() {
		return getSqlQuery().isEmpty() && !hasData();
	}

	public String getSqlQuery() {
		if (sqlQuery == null || sqlQuery.isEmpty())
			sqlQuery = getXmlTag(Settings.TagSQLQUERY);
		if (sqlQuery == null)
			sqlQuery = "";
		return sqlQuery.trim();
	}

	public String getSqlQuery(String action) {
		if (action == null || action.isEmpty() || action.equalsIgnoreCase(Settings.actionView))
			return getSqlQuery();

		if (action.equalsIgnoreCase(Settings.actionDelete)) {
			return "DELETE " + this.getTableName();
		}

		return "";
	}

	public String getTitle() {
		if (queryTitle == null || queryTitle.isEmpty())
			queryTitle = getXmlTag(Settings.TagTITLE);

		if (queryTitle == null || queryTitle.isEmpty())
			return getTableName().toLowerCase();
		return queryTitle.toLowerCase();
	}

	public String getTableName() {
		if (tableName == null || tableName.isEmpty())
			tableName = getXmlTag(Settings.TagTable);

		String sql = getSqlQuery();
		if ((tableName == null || tableName.isEmpty()) && (sql != null && !sql.isEmpty())) {
			tableName = DBService.getTableNameFromSql(sql);
		}

		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName != null ? tableName.trim() : "";
	}

	public String getType() {
		if (queryType == null || queryType.isEmpty())
			queryType = getXmlTag(Settings.TagTYPE);

		return queryType;
	}

	public void setType(String value) {
		queryType = value;
	}

	public String getTempTable() {
		return tempTable;
	}

	public void setTempTable(String value) {
		tempTable = value;
	}

	public void clearTempTable(String value) {
		if (tempTable == value)
			tempTable = null;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getDynamicSqlQuery() {
		return dynamicSqlQuery;
	}

	public void setDynamicSqlQuery(String dynamicSqlQuery) {
		this.dynamicSqlQuery = dynamicSqlQuery;
	}

	public DBQuery getSuperSQLQuery() {
		return superSQLQuery;
	}

	public void setSuperSQLQuery(DBQuery plan) {
		plan.subQueries.add(this);
		superSQLQuery = plan;
	}

	public boolean hasSuperSQLQuery() {
		if (this.superSQLQuery == null)
			return false;
		return true;
	}

	public List<String> getGroupByColumns() {
		if (groupByColumns.isEmpty()) {
			String[] arr = this.getXmlTag(Settings.TagGROUPBY).split(",");
			for (String t : arr)
				groupByColumns.add(t);
		}
		return groupByColumns;
	}

	public void addGroupByColumns(String column) {
		if (!groupByColumns.contains((String) column))
			groupByColumns.add((String) column);
	}

	@SuppressWarnings("unchecked")
	public String getResultColumns(boolean asSelectClause) {
		if (asSelectClause)
			return getElementsWithSeparator((AbstractCollection<String>) (resultColumns), ",");
		return "X";
	}

	public List<String> getResultColumns() {
		if (resultColumns != null && resultColumns.size() > 0)
			return resultColumns;
		String columnsStr = Utils.substringBetween(this.getSqlQuery().toLowerCase(), "select ", " from ");
		if ((resultColumns == null || resultColumns.size() == 0) && columnsStr.length() > 0
				&& !columnsStr.equalsIgnoreCase("*")) {
			this.addResultColumn(columnsStr);
		}
		return resultColumns;
	}

	public void setResultColumns(List<String> columns) {
		resultColumns = columns;
	}

	public void addResultColumn(String resultColumn) {
		String[] columns = resultColumn.split(",");
		for (String resCol : columns) {
			resCol = resCol.trim();
			if (resCol == "")
				return;
			if (resCol.contains("."))
				resCol = resCol.substring(resCol.indexOf(".") + 1);
			resultColumns.add(resCol.toUpperCase().trim());
		}
	}

	public String getSQLQueryLabel() {
		if (sqlQueryLabel == null || sqlQueryLabel.isEmpty()) {
			sqlQueryLabel = getTableName(); // get tablename as sql query label
		}
		return sqlQueryLabel;
	}

	public String getInheritedConditionsAsString(boolean applicable) {// used for the structure only display
		StringBuilder inheritance = new StringBuilder("  ");
		if (this.superSQLQuery == null || this.superSQLQuery.transmittedConditions == null
				|| this.superSQLQuery.transmittedConditions.size() == 0)
			return " ";
		int idx;
		for (TransmittedCondition cond : this.superSQLQuery.transmittedConditions) {
			String sternchen = "";
			idx = this.transmittedConditions.indexOf(cond);
			if (idx >= 0 && this.transmittedConditions.get(idx)
					.getNotApplicableReason() == TransmittedCondition.MAX_DEPTH_EXCEEDED)
				sternchen = "*";
			if (idx >= 0 && this.transmittedConditions.get(idx).inheritanceIsNotApplicable() != applicable)
				inheritance.append(cond.getIdentifier() + "(" + cond.getDepth() + sternchen + "), ");
		}

		return inheritance.deleteCharAt(inheritance.length() - 2).toString();
	}

	public Set<String> getInheritedApplicableConditions() {
		if (this.superSQLQuery == null || this.superSQLQuery.transmittedConditions == null
				|| this.superSQLQuery.transmittedConditions.size() == 0)
			return null;
		Set<String> inheritance = new HashSet<String>();
		int idx;
		for (TransmittedCondition cond : this.superSQLQuery.transmittedConditions) { // The child asks the parent for
																						// for instantiated conditions
																						// (column X = something )...
			idx = this.transmittedConditions.indexOf(cond);
			if (idx >= 0 && this.transmittedConditions.get(idx).inheritanceIsNotApplicable() == false)
				inheritance.add(cond.getIdentifier());// ..and inherits every applicable condition
		}
		return inheritance;
	}

	public List<TransmittedCondition> getTransmittedConditions() {
		return transmittedConditions;
	}

	public Set<String> getTransmittedColumns() {
		if (!getXmlTag(Settings.TagTRANSMITTEDCOLUMNS).isEmpty()) {
			String columns = getXmlTag(Settings.TagTRANSMITTEDCOLUMNS).toUpperCase().trim();
			Set<String> result = new HashSet<String>();

			result.clear();
			String[] arr = columns.split(",");
			for (String item : arr) {
				result.add(item.trim());
			}
			return result;
		}
		return transmittedConditionColumns;
	}

	public Set<String> getTransmittedConditionColumns() {
		return transmittedConditionColumns;
	}

	public String getNewTransmittedConditionColumnsAsString() {// for the structure only view
		StringBuilder transmitted = new StringBuilder("  ");
		boolean hasInheritance = false;
		if (this.transmittedConditions == null || this.transmittedConditions.size() == 0)
			return " ";

		if (this.superSQLQuery != null && this.superSQLQuery.transmittedConditions != null
				&& this.superSQLQuery.transmittedConditions.size() > 0)
			hasInheritance = true;

		for (TransmittedCondition condi : this.transmittedConditions) {
			if (!hasInheritance || !this.superSQLQuery.transmittedConditions.contains(condi))
				transmitted.append(condi.toString() + ",");
		}
		return transmitted.deleteCharAt(transmitted.length() - 1).toString();
	}

	public Set<String> getNewTransmittedConditionColumns() {
		boolean hasInheritance = false;
		if (this.transmittedConditions == null || this.transmittedConditions.size() == 0)
			return null;
		Set<String> newTransmittedConditionColumn = new HashSet<String>();

		if (this.superSQLQuery != null && this.superSQLQuery.transmittedConditions != null
				&& this.superSQLQuery.transmittedConditions.size() > 0)
			hasInheritance = true;

		for (TransmittedCondition condi : this.transmittedConditions) {
			if (!hasInheritance || !this.superSQLQuery.transmittedConditions.contains(condi))// ist upperCase n�tig?
				newTransmittedConditionColumn.add(condi.getIdentifier());
		}
		return newTransmittedConditionColumn;
	}

	public void addTransmittedConditionAttribute(String transmittedConditionColumn) {
		this.transmittedConditionColumns.add(transmittedConditionColumn);
	}

	public void setQueryTitle(String title) {
		this.queryTitle = title;
	}

	public String getQueryTitle() {
		return this.queryTitle;
	}

	public boolean isSuppressDisplayIfNoData() {
		return suppressDisplayIfNoData;
	}

	public void setSuppressDisplayIfNoData(boolean suppressLabelIfNoData) {
		this.suppressDisplayIfNoData = suppressLabelIfNoData;
	}

	public boolean isEmptyResultset() {
		return emptyResultset;
	}

	// if already run then do need to run sql again !!
	public boolean isExecuteOnFirstRun() {
		return executeOnFirstRun;
	}

	// returns true if there is no Anfrageplan ancestors or if all Anfrageplan
	// ancestors have executeOnFirstRun == true
	public boolean isAncestorExecuteOnFirstRun() {
		if (this.hasSuperSQLQuery() == false)
			return true;
		return this.getSuperSQLQuery().rekIsAncestorExecuteOnFirstRun();
	}

	private boolean rekIsAncestorExecuteOnFirstRun() {
		if (isExecuteOnFirstRun() == false)
			return false;
		if (this.hasSuperSQLQuery() == false)
			return true;
		return getSuperSQLQuery().rekIsAncestorExecuteOnFirstRun();
	}

	public void setExecuteOnFirstRun(boolean queryOnDemand) {
		this.executeOnFirstRun = queryOnDemand;
	}

	public boolean isDataGenerated() {
		return dataGenerated;
	}

	public String[][] getResultRowsMatrix() {
		return resultRowsMatrix;
	}

	public List<List<String>> getData() {
		return resultData;
	}

	public String[][] getDataArray() {
		String[][] result = new String[resultRowsMatrix[0].length][resultRowsMatrix.length];
		for (int i = 0; i < resultRowsMatrix[0].length; i++) {
			for (int j = 0; j < resultRowsMatrix.length; j++) {
				result[i][j] = resultRowsMatrix[j][i];
			}
		}
		return result;
	}

	public String[][] getRowsArray() {
		String[][] result = new String[resultRowsMatrix[0].length - 1][resultRowsMatrix.length];
		for (int i = 1; i < resultRowsMatrix[0].length; i++) {
			for (int j = 0; j < resultRowsMatrix.length; j++) {
				result[i - 1][j] = resultRowsMatrix[j][i];
			}
		}
		return result;
	}

	public List<List<String>> getRowsData() {
		return resultData.subList(1, resultData.size());
	}

	public List<String> getColumnsDistinctData(String column) {
		return Utils.getUniqueListFromArray(getColumnsData(column));
	}

	public String[] getColumnsData(String column) {
		if (resultRowsMatrix != null) {
			for (int i = 0; i < resultRowsMatrix.length - 1; i++) {
				if (resultRowsMatrix[i].length > 0 && resultRowsMatrix[i][0].equalsIgnoreCase(column)) {
					return Arrays.copyOfRange(resultRowsMatrix[i], 1, resultRowsMatrix[i].length);
				}
			}
		}
		return new String[] {};
	}

	public int getRowsCount() {
		if (resultData == null) {
			if (this.resultRowsMatrix != null)
				return this.getResultRowsMatrix()[0].length;
			return -1;
		}
		return resultData.size() - 1;
	}

	public void setData(String[][] resultRowsMatrix) {
		this.resultRowsMatrix = resultRowsMatrix;
	}

	public void setData(List<List<String>> _data) {
		this.resultData = _data;
		this.resultRowsMatrix = DBService.convertAndFlipListArray(this.resultData);
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setData(ResultSet rs) {
		try {
			this.resultSet = rs;
			this.resultData = DBService.executeSqlAsList(rs, -1);
			rs.beforeFirst();
			this.resultRowsMatrix = DBService.convertAndFlipListArray(this.resultData);
		} catch (SQLException ex) {
			MyDialog.showException(ex, "Error executing Db Query: " + this.getTableName());
		}
	}

	public void setData() {
		String sql = getSqlQuery();
		Connection conn = getConnection();
		if (sql != null && conn != null)
			setData(getSqlQuery(), getConnection());
	}

	public void setData(String sql, Connection conn) {
		try {
			if (conn == null || conn.isClosed()) {
				MyDialog.showException("Connection is null or closed. Can not execute " + sql);
				return;
			}

			this.connection = conn;

			Statement stmn = conn.createStatement();
			ResultSet rs = DBService.executeQuery(stmn, sql); // stmn.executeQuery(sql);
			this.resultSet = rs;
			this.resultData = DBService.executeSqlAsList(rs, -1);
			this.resultRowsMatrix = DBService.convertAndFlipListArray(this.resultData);
			rs.close();
			stmn.close();
			// this.statement = stmn;
		} catch (SQLException ex) {
			MyDialog.showException(ex, sql);
		}
	}

	public void setData(Set<String> dataValues, String columnName) {
		if (dataValues == null)
			return;

		int colsCount = 1;

		String[][] data = new String[colsCount][dataValues.size() + 1];
		int i = 1;
		for (String entry : dataValues) {
			data[0][i] = entry;
			i += 1;
		}
		String col1 = columnName;

		data[0][0] = col1;
		this.allResultColumns = new LinkedList<String>();
		this.allResultColumns.add(col1);
		this.resultColumns = this.allResultColumns;
		this.resultRowsMatrix = data;
	}

	public PreparedStatement getStatement(String sql) {
		if (this.statement != null)
			return this.statement;
		Connection conn = getConnection();
		if (conn != null) {
			try {
				this.statement = conn.prepareStatement(sql);
			} catch (SQLException e) {
				LogManager.getLogger().error(e);
			}
		}
		return this.statement;
	}

	public PreparedStatement getStatement() {
		return getStatement(getSqlQuery());
	}

	public void setData(Map<String, String> dataValues) {

		int colsCount = 0;
		if (dataValues == null)
			return;
		for (Map.Entry<String, String> entry : dataValues.entrySet()) {
			colsCount = entry.getValue().split(Settings.dataSeperator).length;
			break;
		}

		String[][] data = new String[colsCount + 1][dataValues.size() + 1];
		int i = 1;
		for (Map.Entry<String, String> entry : dataValues.entrySet()) {
			data[0][i] = entry.getKey();
			data[1][i] = entry.getValue();
			i += 1;
		}
		// data[0][0] = data[0][i-1];
		String col1 = "KEY";
		String col2 = "VALUE";
		if (dataValues instanceof MyTreeMap && ((MyTreeMap) dataValues).headers != null) {
			col1 = ((MyTreeMap) dataValues).headers[0].toUpperCase();
			col2 = ((MyTreeMap) dataValues).headers[1].toUpperCase();
		} else if (dataValues instanceof MyLinkedMap && ((MyLinkedMap) dataValues).headers != null) {
			col1 = ((MyLinkedMap) dataValues).headers[0].toUpperCase();
			col2 = ((MyLinkedMap) dataValues).headers[1].toUpperCase();
		}
		this.allResultColumns = new LinkedList<String>();
		this.allResultColumns.add(col1);
		data[0][0] = col1;
		if (colsCount > 0) {
			data[1][0] = col2;
			this.allResultColumns.add(col2);
		}

		this.resultColumns = this.allResultColumns;
		this.resultRowsMatrix = data;
	}

	public int getMaxInheritanceDepth() {
		return maxInheritanceDepth;
	}

	public int getMaximumResultRows() {
		return maximumResultRows;
	}

	public void setMaximumResultRows(int maximumResultRows) {
		this.maximumResultRows = maximumResultRows;
	}

	public List<String> getAllResultColumns() {
		return allResultColumns;
	}

	public String[] getColumnsArray() {
		return Utils.convertToStringArray(this.getColumns());
	}

	public Set<String> getPrimaryColumns() {
		if (primaryColumns == null)
			primaryColumns = DBService.getPrimaryColumns(getConnection(), getTableName());
		return primaryColumns;
	}

	public List<String> getColumns() {
		if (allResultColumns.size() == 0) {
			if (resultRowsMatrix != null && resultRowsMatrix.length > 0) {
				for (int i = 0; i < resultRowsMatrix.length; i++) {
					allResultColumns.add(resultRowsMatrix[i][0]);
				}
			}
		}
		return allResultColumns;
	}

	public void setAllResultColumns(List<String> allResultColumns) {
		this.allResultColumns = allResultColumns;
	}

	public boolean isDataRetrievalTriggeredByGUI() {
		return dataRetrievalTriggeredByGUI;
	}

	public void setDataRetrievalTriggeredByGUI(boolean dataRetrievalTriggeredByGUI) {
		this.dataRetrievalTriggeredByGUI = dataRetrievalTriggeredByGUI;
	}

	public String getEmptyColumnDisplayMode() {
		return emptyColumnDisplayMode;
	}

	public void setEmptyColumnDisplayMode(String emptyColumnDisplayMode) {
		this.emptyColumnDisplayMode = emptyColumnDisplayMode;
	}

	public Map<String, Boolean> getIsColumnNull() {
		return isColumnNull;
	}

	public void setIsColumnNull(Map<String, Boolean> isColumnNull) {
		this.isColumnNull = isColumnNull;
	}

	public List<DBParam> getQueryParams() {
		return this.queryParams;
	}

	public void setQueryParams(List<DBParam> params) {
		this.queryParams = params;
	}

	public List<DBLookup> getLookupColumns() {
		return this.lookupColumns;
	}

	public void setLookupColumns(List<DBLookup> columns) {
		this.lookupColumns = columns;
	}

	public String applyParams(String query) {
		return DBService.applyParams(query, this.queryParams);
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String value) {
		connectionName = value;
	}

	public void setConnection(Connection value) {
		connection = value;
	}

	public Connection getConnection() {
		if (connection == null && connectionName != null && connectionName.length() > 0) {
			connection = DBService.getConnection(connectionName, "");
		}
		return connection;
	}

	public String getRebuiltQuery(boolean isFirstRun) {
		if (isFirstRun == false) {
			if (this.isExecuteOnFirstRun() == false && this.getDynamicSqlQuery() != null
					&& this.getDynamicSqlQuery().length() > 0) {
				this.rebuiltQuery = this.getDynamicSqlQuery();
				// this.setDynamicSqlQuery(null);
			} else {
				if (this.isAncestorExecuteOnFirstRun() == false && this.inheritedConditions != null
						&& this.inheritedConditions.length() > 0)
					this.conditionsReceived = false;
				this.rebuiltQuery = this.sqlQuery;
			}

		} else
			this.rebuiltQuery = this.sqlQuery;

		this.rebuiltQuery = this.applyParams(this.rebuiltQuery);

		return DBService.cleanSql(this.rebuiltQuery);
	}

	@SuppressWarnings("unchecked")
	public boolean generateData(boolean isFirstRun) throws Exception {
		if (dataGenerated && isFirstRun)
			return true;
		Connection conn = this.getConnection();

		if (Settings.allowTempTables && (this.tempTable == null || this.tempTable.length() == 0)) {
			this.tempTable = DataViewerService.generateTempTable(conn, this, logger);
			if (tempTable == null)
				return false;
		}

		this.rebuiltQuery = getRebuiltQuery(isFirstRun);

		if (this.rebuiltQuery != null && !this.rebuiltQuery.isEmpty()) {

			log("+ " + this.getSQLQueryLabel() + ".");

			// -------------------------------------------------------------------------
			// prepare transmitted condition columns to sub queries
			if (!conditionsReceived && this.transmittedConditions != null && this.subQueries.size() > 0
					&& this.transmittedConditionColumns.size() == 0) {
				for (String columnName : this.getAllResultColumns()) {
					for (DBQuery childQuery : this.subQueries) {
						for (String childColumnName : childQuery.getAllResultColumns()) {
							if (columnName.toUpperCase().equals(childColumnName.toUpperCase()))
								this.transmittedConditionColumns.add(columnName.toUpperCase());
						}
					}
				}
			}
			// -------------------------------------------------------------------------
			if (!conditionsReceived && this.superSQLQuery != null && this.superSQLQuery.transmittedConditions != null
					&& this.superSQLQuery.transmittedConditions.size() > 0) {

				List<String> rawInheritedCondition = new LinkedList<String>();
				for (TransmittedCondition toInherit : this.superSQLQuery.transmittedConditions) {
					if (toInherit.getDepth() <= this.maxInheritanceDepth
							&& Utils.containsIgnoreCase(this.allResultColumns, toInherit.getIdentifier())
							&& !columnHasWildKey(toInherit.getIdentifier(), this.rebuiltQuery))
						rawInheritedCondition.add(toInherit.getIdentifier() + toInherit.getValue());
				}
				if (rawInheritedCondition.size() > 0) {
					this.inheritedConditions = new StringBuilder(
							getElementsWithSeparator((AbstractCollection<String>) (rawInheritedCondition), "AND"));
					conditionsReceived = true;
					this.rebuiltQuery = "select * from (" + this.rebuiltQuery + "\n) 	a where "
							+ this.inheritedConditions;
				}

				for (TransmittedCondition inheritedConditionToTransmit : this.superSQLQuery.transmittedConditions) {
					this.transmittedConditionColumns.add(inheritedConditionToTransmit.getIdentifier());
					TransmittedCondition newInheritedConditionToTransmit = new TransmittedCondition(
							inheritedConditionToTransmit.getIdentifier(),
							inheritedConditionToTransmit.getValue(),
							inheritedConditionToTransmit.getDepth() + 1);
					if (inheritedConditionToTransmit.getDepth() > this.maxInheritanceDepth) {
						newInheritedConditionToTransmit.setInheritanceIsNotApplicable(true);
						newInheritedConditionToTransmit.setNotApplicableReason(TransmittedCondition.MAX_DEPTH_EXCEEDED);
					}
					if (!Utils.containsIgnoreCase(this.getAllResultColumns(),
							newInheritedConditionToTransmit.getIdentifier())) {
						newInheritedConditionToTransmit.setInheritanceIsNotApplicable(true);
						newInheritedConditionToTransmit.setNotApplicableReason(TransmittedCondition.COLUMN_NOT_FOUND);
					}
					if (this.transmittedConditions.contains(newInheritedConditionToTransmit) == false)
						this.transmittedConditions.add(newInheritedConditionToTransmit);
					else {
						this.transmittedConditions.remove(transmittedConditions
								.get(this.transmittedConditions.indexOf(newInheritedConditionToTransmit)));
						this.transmittedConditions.add(newInheritedConditionToTransmit);
					}

				}
			}

			// -------------------------------------------------------------------------
			if ((isFirstRun == true
					&& (this.isExecuteOnFirstRun() == false || this.isAncestorExecuteOnFirstRun() == false))
					|| (isFirstRun == false && this.isExecuteOnFirstRun() == false
							&& this.getDynamicSqlQuery() == null)) {
				// //Hung: why select empty rows ??

				this.rebuiltQuery = "select * from (" + this.rebuiltQuery + ") b";
			}

			String tempQuery = this.rebuiltQuery.replace("\n", "\n\t"); // to display in debug pane

			if (Settings.allowTempTables && this.tempTable != null && this.rebuiltQuery != null
					&& this.tempTable.length() > 0 && this.rebuiltQuery.length() > 0) {
				this.rebuiltQuery = "insert into " + this.tempTable + "\n  " + this.rebuiltQuery;
				this.setDynamicSqlQuery(null);
				try {
					// System.out.println( this.rebuiltQuery);
					DBService.executeSql(rebuiltQuery, conn);
					String SQLExecutionFeedback = generateDataMatrix(conn);

					if (SQLExecutionFeedback.length() > 0)
						logger.debug(SQLExecutionFeedback);

					if (!rebuiltQuery.endsWith("b where 1=0") && !transmittedConditionColumns.isEmpty()) {
						setTransmittedConditions();
					}
				} catch (SQLException sq) {
					MyDialog.showException(sq, this.getSQLQueryLabel() + ":\n" + this.rebuiltQuery + "\n");
					DBService.dropAllTempTables();
					return false;
				}
			} else {
				this.setDynamicSqlQuery(null);
				try {
					String SQLExecutionFeedback = generateDataMatrix(conn);
					logger.debug(
							"\t " + tempQuery + "; -- retrieved " + (this.resultRowsMatrix[0].length - 1) + " rows."
									+ SQLExecutionFeedback);

					if (!rebuiltQuery.endsWith("b where 1=0") && !transmittedConditionColumns.isEmpty()) {
						setTransmittedConditions();
					}
				} catch (SQLException sq) {
					MyDialog.showException(sq, this.getSQLQueryLabel() + ":\n" + this.rebuiltQuery);
					DBService.dropAllTempTables();
					return false;
				}
			}
		}

		int t = 0;
		if (!this.subQueries.isEmpty()) {
			while (this.subQueries.size() > t) {
				// generate data for all sub queries
				boolean success = this.subQueries.get(t).generateData(isFirstRun);
				if (!success)
					return false;
				t++;
			}
		}
		return true;
	}

	public void setTransmittedConditions() throws SQLException {
		Set<String> applicableConditionColumns = new HashSet<String>();
		for (String trCond : this.transmittedConditionColumns) {
			if (Utils.containsIgnoreCase(this.allResultColumns, trCond)) {
				applicableConditionColumns.add(trCond);
			}
		}
		if (applicableConditionColumns.size() == 0)
			return;

		String message = validateAttributes(
				applicableConditionColumns,
				"<TransmittedConditionColumns>",
				"Zu vererbende Bedingungen sind nicht definiert",
				"in der SQL-Abfrage NICHT selektiert");

		if (!message.equals("")) {
			throw new SQLException(message);
		}

		for (int t = 0; t < resultRowsMatrix.length; t++) {
			// System.out.println(resultRowsMatrix[t][0] );
			if (Utils.containsIgnoreCase(applicableConditionColumns, resultRowsMatrix[t][0])) {// resultRowsMatrix[t][0]
																								// is the column name
				String columnName = resultRowsMatrix[t][0];
				HashSet<String> conditionValues = new HashSet<String>();
				// System.out.println(resultRowsMatrix[t][0]);
				boolean hasMoreThan1000Expressions = false;
				if (resultRowsMatrix[t].length < Settings.MaximumResultRows) {
					for (int tt = 1; tt < resultRowsMatrix[t].length; tt++) {
						// System.out.println(resultRowsMatrix[t][tt]);
						conditionValues.add("'" + resultRowsMatrix[t][tt] + "'");
					}
				} else
					hasMoreThan1000Expressions = true;

				if (conditionValues.size() > 0) {
					if (!this.transmittedConditions.contains(new TransmittedCondition(columnName, null, 0))) {
						this.transmittedConditions.add(new TransmittedCondition(columnName, " in " +
								"("
								+ getElementsWithSeparator((AbstractCollection<String>) (conditionValues), ",")
										.replaceAll("'null'", "null")
								+ ")", 1));
					} else
						this.transmittedConditions.get(
								this.transmittedConditions.indexOf(new TransmittedCondition(columnName, null, 0)))
								.setValue(" in ("
										+ getElementsWithSeparator((AbstractCollection<String>) (conditionValues), ",")
												.replaceAll("'null'", "null")
										+ ")");
				}
				if (conditionValues.size() == 0 && hasMoreThan1000Expressions == false)
					this.transmittedConditions.add(new TransmittedCondition(columnName, " = null", 1));

				if (hasMoreThan1000Expressions) {
					if (!this.transmittedConditions.contains(new TransmittedCondition(columnName, null, 0)))
						this.transmittedConditions.add(new TransmittedCondition(columnName, " like '%'", 1));
					else
						this.transmittedConditions
								.get(this.transmittedConditions.indexOf(new TransmittedCondition(columnName, null, 0)))
								.setValue(" like '%'");
				}
			}
		}

	}

	public String validateAttributes(Collection<String> conditionAttributes, String tagName, String missstandGrob,
			String missstandDetail) {
		String message = "";
		List<String> badConditions = new LinkedList<String>();
		for (String condAttr : conditionAttributes) {
			boolean attributeExists = false;
			if (Utils.containsIgnoreCase(this.allResultColumns, condAttr.replaceAll("NOT:", "").trim()))
				attributeExists = true;
			if (attributeExists == false)
				badConditions.add(condAttr);
		}
		if (badConditions.size() > 0) {
			String MessageNumerusForm = "";
			if (badConditions.size() == 1)
				MessageNumerusForm = "One column name part of " + tagName + " is";
			else
				MessageNumerusForm = "Following column names part of " + tagName + " are";

			if (badConditions.size() == conditionAttributes.size())
				MessageNumerusForm = "All column names of " + tagName + " are";
			if (!MessageNumerusForm.equals(""))
				message = missstandGrob + " in report entity " + this.getSQLQueryLabel() + "\n" + MessageNumerusForm
						+ " " + missstandDetail +
						": " + badConditions.toString().replace("[", "").replace("]", "") + "\n\n";
		}
		return message;
	}

	public boolean columnHasWildKey(String columnName, String query) {
		// returns true if the given column name contains the wildkey *
		query = query.toUpperCase().replaceAll(" ", "").replaceAll("\n", "");
		if (!query.contains(columnName + ",") && !query.contains(columnName + "FROM")) // a column name is followed
																						// either by , or by the word
																						// from
			return false;
		String previousColumn;
		int currCol = 0;
		for (currCol = 0; currCol < this.allResultColumns.size(); currCol++) {
			if (this.allResultColumns.get(currCol).toUpperCase().equals(columnName)) {
				break;
			}
		}
		if (currCol == 0)
			previousColumn = "SELECT";
		else
			previousColumn = this.allResultColumns.get(currCol - 1).toUpperCase() + ",";

		if (query.contains(columnName + ","))
			columnName = columnName + ",";
		else
			columnName = columnName + "FROM";
		String relevantPart = "";
		relevantPart = query.substring(query.indexOf(previousColumn) + previousColumn.length(),
				query.indexOf(columnName));

		if (relevantPart.contains("*"))
			return true;
		else
			return false;
	}

	public boolean isLookupColumn(String columnName, String connectionName) {
		String mappingKey = MyService.getCachedKey(connectionName, columnName);
		// System.out.println("Mapping Key: " + mappingKey);
		if (MyService.LookupMapping != null && MyService.LookupMapping.containsKey(mappingKey))
			return true;
		return false;
	}

	String generateDataMatrix() {
		return generateDataMatrix(this.getConnection());
	}

	String generateDataMatrix(Connection conn) {
		String resultSQL = null;
		if (!Settings.allowTempTables || this.tempTable.isEmpty()) {
			resultSQL = this.applyParams(this.rebuiltQuery);
		} else {
			resultSQL = "Select * from " + this.tempTable;
		}

		return generateDataMatrix(conn, resultSQL);
	}

	String generateDataMatrix(Connection conn, String resultSQL) {
		String SQLExecutionFeedback = "";
		try {
			this.resultData = DBService.executeSqlAsList(resultSQL, conn, -1);
			if (this.allResultColumns == null
					|| this.allResultColumns.size() == 0 && this.resultData != null && this.resultData.size() > 0) {
				this.allResultColumns = this.resultData.get(0);
			}
			if (this.parentColumn != null && this.keyColumn != null) {
				this.resultData = DBService.sortHierarchy(this.resultData, parentColumn, keyColumn);
			}

			this.resultRowsMatrix = DBService.convertAndFlipListArray(this.resultData);
			this.emptyResultset = this.resultData.size() == 1;
			for (int colNum = 0; colNum < this.resultRowsMatrix.length; colNum++) {
				boolean isEmptyColumn = DBService.isNullOrEmptyArray(
						Arrays.copyOfRange(this.resultRowsMatrix[colNum], 1, this.resultRowsMatrix[colNum].length), "");
				if (isEmptyColumn) // if column does not have any data then can remove that column !
					this.isColumnNull.put(resultRowsMatrix[colNum][0], true);
			}

			dataGenerated = true;
		} catch (SQLException sq) {
			SQLExecutionFeedback = Utils.getExceptionMessage(sq,
					"Database vendor thrown an error while executing the query:\n");
		}
		return SQLExecutionFeedback;
	}

	public boolean hasDataIgnoreFirstRowAsColumns() {
		if (this.resultRowsMatrix == null || this.getResultRowsMatrix()[0].length <= 1)
			return false;
		return true;
	}

	public boolean hasData() {
		return hasData(0);
	}

	public boolean hasRowsData() {
		return getRowsCount() > 0;
	}

	public boolean hasResultData() {
		return this.resultRowsMatrix != null
				&& this.resultRowsMatrix[0].length > 1;
	}

	public boolean hasData(int maxRows) {
		if (this.resultRowsMatrix == null || this.resultRowsMatrix.length <= maxRows)
			return false;
		return true;
	}

	public String getSqlQueryWithParams(List<DBParam> params) {
		return DBService.applyParams(getSqlQuery(), params);
	}

	public String getSqlQueryWithParams() {
		return DBService.applyParams(getSqlQuery(), queryParams);
	}

	String getElementsWithSeparator(AbstractCollection<String> coll, String separator) {
		if (coll.size() == 0)
			return "";
		String elements = "";
		for (String str : coll) {
			elements = elements + str + " " + separator + " ";
		}
		elements = elements.substring(0, elements.lastIndexOf(separator));
		return elements;
	}

	int getNumberofRows(String query, Connection conn) {
		return DBService.getNumberofRows(query, conn);
	}

	String showDBQuery(boolean includeSubTree) {
		StringBuilder result = new StringBuilder();
		showDBQueryRek(1, includeSubTree, result);
		return result.toString();
	}

	void showDBQueryRek(int level, boolean includeSubTree, StringBuilder result) {
		String spaces = Utils.multiplyChars(" ", (level - 1) * 10);

		result.append(spaces + "SQL Query Label: " + this.getSQLQueryLabel() + "\n");
		result.append(spaces + "Execute on first run: " + this.executeOnFirstRun + ", suppress display if no data: "
				+ this.suppressDisplayIfNoData + "\n");
		result.append(spaces + "All Result Columns: " + this.allResultColumns + "\n");
		result.append(spaces + "SQL Query\n" + this.getSqlQuery() + "\n\n");
		result.append(spaces + "Rebuilt SQL Query\n" + spaces + this.rebuiltQuery + "\n\n");
		result.append(spaces + "Data generated: " + this.dataGenerated + ", empty result set: " + this.emptyResultset
				+ " conditions received: " + this.conditionsReceived + "\n");
		result.append(spaces + "Result Set\n");
		String ResultMatrix[][] = this.getResultRowsMatrix();
		if (ResultMatrix == null)
			return;
		int colNr = 0, rowNr = 0;
		for (rowNr = 0; rowNr < ResultMatrix[0].length; rowNr++) {
			result.append(spaces);
			for (colNr = 0; colNr < ResultMatrix.length; colNr++) {
				result.append(ResultMatrix[colNr][rowNr] + "\t");
			}
			result.append("\n");
		}
		result.append(spaces + "Inherited Conditions " + this.inheritedConditions + "\n");
		result.append(spaces + "Transmitted Conditions\n" + spaces + this.transmittedConditions + "\n");
		result.append(spaces + "Dynamic SQL Query\n" + spaces + this.dynamicSqlQuery + "\n");
		if (includeSubTree) {
			for (DBQuery a : this.subQueries) {
				a.showDBQueryRek(level + 1, includeSubTree, result);
			}
		}
		// return result.toString();
	}

	public String getCompareSql() {
		for (DBParam param : queryParams) {
			if (param.getKey().isEmpty())
				continue;
			String keyOriginal = param.getKey();
			return getCompareSql(keyOriginal);
		}
		return "";
	}

	public String getCompareSql(String paramName) {
		String paramValue = DBService.getQueryParamvalue(queryParams, paramName);
		return getCompareSql(Settings.tablePrefixes, paramValue.split(",").length, paramName);
	}

	public String getCompareSql(String[] prefixs, int size, String paramName) {
		if (size == 1)
			return sqlQuery;
		List<String> columns = getResultColumns();
		String idColumn = getKeyColumn();
		if (idColumn == null)
			return sqlQuery;

		String sql = "select distinct ";
		for (int i = 0; i < prefixs.length; i++) {
			if (i >= size)
				break;
			String prefix = prefixs[i];
			for (String column : columns) {
				sql = sql + prefix + "." + column + " as " + column + "_" + prefix + ",";
			}

		}
		sql = sql.substring(0, sql.length() - 1);
		sql = sql + " from ";

		String lastIdColumn = "";
		String orderBy = "";
		for (int i = 0; i < prefixs.length; i++) {
			if (i >= size)
				break;
			String prefix = prefixs[i];
			orderBy = orderBy + prefix + "." + idColumn + ",";

			if (i == 0) {
				sql = sql + " (" + sqlQuery + ") " + prefix;
				lastIdColumn = prefix + "." + idColumn;
			} else {
				sql = sql + " FULL OUTER JOIN ";
				sql = sql + " (" + sqlQuery + ") " + prefix;
				sql = sql + " ON " + lastIdColumn + " = " + prefix + "." + idColumn;
				lastIdColumn = prefix + "." + idColumn;
			}

		}

		orderBy = orderBy.substring(0, orderBy.length() - ",".length());
		// sql = sql.substring(0, sql.length() - "=".length());
		sql = sql + " order by " + orderBy;
		sql = DBService.applyParams(sql, queryParams, true, true);
		return sql;
	}

}