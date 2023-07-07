package com.rs2.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs2.core.components.MyDialog;
import com.rs2.core.settings.Settings;
import com.rs2.modules.db.DBService;
import com.rs2.core.base.MyService;
import com.rs2.core.utils.Utils;

import java.security.KeyStore.Entry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DBSchema {
	private static Map<String, DBSchema> CachedData = new TreeMap<String, DBSchema>();
	private Connection connection;
	public String connectionName;
	public String tablePattern;
	public boolean isCachedData = false;

	public String getTableSearch() {
		return tablePattern;
	}

	public void setTableSearch(String value) {
		tablePattern = value;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String value) {
		connectionName = value;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection value) {
		connection = value;
	}

	Map<String, DBTableExport> tableList;
	Map<String, List<String>> tableColumns;
	Map<String, List<DBColumn>> columnsList;

	public DBSchema() {
		tableList = new LinkedHashMap<String, DBTableExport>();
	}

	public Map<String, DBTableExport> getTableList() {
		return tableList;
	}

	public Set<String> getTableNames() {
		if (tableList != null && tableList.size() > 0)
			return tableList.keySet();
		if (tableColumns != null && tableColumns.size() > 0)
			return tableColumns.keySet();
		if (columnsList != null && columnsList.size() > 0)
			return columnsList.keySet();

		return null;
	}

	public List<String> getTableNames(String tablesFilter) {
		return getTableNames(tablesFilter, "");
	}

	public List<String> getTableNames(String tablesFilter, String columnsFilter) {
		List<String> tables = new LinkedList<String>();
		if (tablesFilter == null || tablesFilter.isEmpty())
			tablesFilter = Settings.operatorAll;
		if (columnsFilter == null || columnsFilter.isEmpty())
			columnsFilter = Settings.operatorAll;
		boolean allMatch = columnsFilter.contains(Settings.operatorAND);
		String[] tableFilters = tablesFilter.toUpperCase().split(",");
		String[] colFilters = columnsFilter.toUpperCase().split(allMatch ? Settings.operatorAND : Settings.operatorOR);
		List<String> colFiltersList = new ArrayList<String>();
		for (String column : colFilters) {
			colFiltersList.add(column);
		}

		for (String table : getTableNames()) {
			boolean found = false;
			for (String tableFilter : tableFilters) {
				if (found)
					break;
				tableFilter = tableFilter.replace(Settings.operatorAll, "");
				tableFilter = tableFilter.replace(Settings.operatorAll1, "");
				if (tableFilter.isEmpty() || table.contains(tableFilter)) {
					if (columnsFilter.equalsIgnoreCase(Settings.operatorAll)) {
						found = true;
					} else {
						List<String> columns = getColumnNames(table);
						found = allMatch ? columns.stream().allMatch(element -> colFiltersList.contains(element))
								: columns.stream().anyMatch(element -> colFiltersList.contains(element));
					}

					if (found)
						tables.add(table);
				}
			}
		}
		return tables;

	}

	public Map<String, List<String>> getTableColumns() {
		if (tableColumns == null) {
			tableColumns = new LinkedHashMap<String, List<String>>();
			for (String tableName1 : tableList.keySet()) {
				DBTableExport table = tableList.get(tableName1);
				tableColumns.put(tableName1, table.getColumnList());
			}
		}
		return tableColumns;
	}

	public void setTableColumns(Map<String, List<String>> tableColumns) {
		this.tableColumns = tableColumns;
	}

	public Map<String, List<DBColumn>> getDBColumns() {
		return getColumnsList();
	}

	public List<DBColumn> getDBColumns(String tableName) {
		return getColumnsList().containsKey(tableName) ? getColumnsList().get(tableName) : new LinkedList<DBColumn>();
	}

	public DBColumn getDBColumn(String tableName, String columnName) {
		List<DBColumn> columns = getDBColumns(tableName);
		for (DBColumn column : columns) {
			if (column.getName().equalsIgnoreCase(columnName))
				return column;
		}

		return null;
	}

	public Map<String, List<DBColumn>> getColumnsList() {
		if (columnsList == null) {
			columnsList = new LinkedHashMap<String, List<DBColumn>>();
			for (String tableName1 : tableList.keySet()) {
				DBTableExport table = tableList.get(tableName1);
				List<DBColumn> list = new LinkedList<DBColumn>();
				list.addAll(table.getColumns().values());
				columnsList.put(tableName1, list);
			}
		}
		return columnsList;
	}

	// public void setColumnsList(Map<String, List<DBColumn>> columnsList) {

	// this.columnsList = columnsList;
	// }

	public void setColumnsList(Map<String, List<Object>> columnsList) {
		this.columnsList = new LinkedHashMap<String, List<DBColumn>>();
		for (Map.Entry<String, List<Object>> entry : columnsList.entrySet()) {
			List<DBColumn> list = new LinkedList<DBColumn>();
			HashMap<String, DBColumn> columns = new LinkedHashMap<String, DBColumn>();

			for (Object val : entry.getValue()) {
				if (val instanceof DBColumn)
					columns.put(((DBColumn) val).getName(), (DBColumn) val);
				else {
					Map<String, Object> values = (Map<String, Object>) val;
					columns.put(values.get(Settings.TagFieldLookup.toLowerCase()).toString(),
							new DBColumn(values.get(Settings.TagFieldLookup.toLowerCase()).toString(),
									values.get("type").toString(),
									Utils.convertToInt(values.get("length").toString()), null, null,
									Utils.convertToBoolean(values.get("isPrimaryKey").toString())));
				}
			}
			DBTableExport tableExport = new DBTableExport(entry.getKey());
			tableExport.setColumns(columns);
			tableExport.setColumnList(new LinkedList(columns.keySet()));
			this.tableList.put(tableExport.getTableName(), tableExport);
			this.columnsList.put(entry.getKey(), new LinkedList(columns.values()));
		}
	}

	public List<String> getColumnNames(String tableName) {
		if (getTableColumns().containsKey(tableName))
			return Utils.sortListString(getTableColumns().get(tableName));
		if (getColumnsList().containsKey(tableName)) {
			List<String> list = new LinkedList<String>();
			for (DBColumn column : getColumnsList().get(tableName))
				list.add(column.getName());
			return Utils.sortListString(list);
		}
		return null;
	}

	public Set<String> getColumnNames(String tableName, String columnPattern) {
		return DBService.getColumns(this, tableName, columnPattern);
	}

	public Set<String> getColumnNames() {
		Set<String> columns = new TreeSet<String>();

		for (String tableName1 : tableList.keySet()) {
			DBTableExport table = tableList.get(tableName1);
			columns.addAll(table.getColumnList());
		}
		return columns;
	}

	public void setTableList(Map<String, DBTableExport> tableList) {
		this.tableList = tableList;
	}

	public void addTable(DBTableExport table) {
		this.tableList.put(table.getTableName(), table);
		getTableColumns().put(table.getTableName(), table.getColumnList());
	}

	public void setPrimaryKeys(Connection conn) {
		if (tableList == null || tableList.size() == 0)
			return;
		String pkQuery = "select uc.table_name,column_name " +
				"from " +
				"user_constraints uc, user_cons_columns ucc " +
				"where " +
				"uc.owner  = ucc.owner " +
				"and uc.constraint_name = ucc.constraint_name " +
				"and uc.constraint_type ='P' and status='ENABLED' " +
				"and uc.Table_name = ?" +
				"order by table_name ";
		PreparedStatement getThePKs = null;
		ResultSet rsPkPerTable = null;
		try {

			getThePKs = conn.prepareStatement(pkQuery);

			for (DBTableExport currTable : tableList.values()) {
				getThePKs.setString(1, currTable.getTableName());
				rsPkPerTable = getThePKs.executeQuery();
				HashSet<String> pkSet = new HashSet<String>();
				while (rsPkPerTable.next()) {
					pkSet.add(rsPkPerTable.getString(2));
				}
				currTable.setPrimaryKey(pkSet);
			}

		} catch (SQLException sq) {
			LogManager.getLogger().error("SQL error.  " + sq + "\n" + pkQuery);
		}
	}

	public void setPrimaryKeys(String tableInclusionClause) {
		if (tableList == null || tableList.size() == 0)
			return;
		if (!tableInclusionClause.contains("table_name") && !tableInclusionClause.contains(" ")) // single table
			tableInclusionClause = "table_name = '" + tableInclusionClause + "'";

		String pkQuery = "select uc.table_name,column_name " +
				"from " +
				"user_constraints uc, user_cons_columns ucc " +
				"where " +
				"uc.owner  = ucc.owner " +
				"and uc.constraint_name = ucc.constraint_name " +
				"and uc.constraint_type ='P' and status='ENABLED' " +
				"and (" + tableInclusionClause.replace("table_name", "uc.table_name") + ") " +
				"order by table_name, position";
		;
		Statement getThePKs = null;
		ResultSet rsPkPerTable = null;
		try {

			getThePKs = connection.createStatement();
			rsPkPerTable = DBService.executeQuery(getThePKs, pkQuery);
			String tableName = "";
			DBTableExport currTable = null;
			HashSet<String> pkSet = null;

			while (rsPkPerTable.next()) {
				if (!rsPkPerTable.getString(1).equalsIgnoreCase(tableName)) {
					tableName = rsPkPerTable.getString(1);
					currTable = tableList.containsKey(tableName) ? tableList.get(tableName) : null;
					if (currTable == null)
						continue;
					pkSet = currTable.getPrimaryKey();
					if (pkSet == null)
						pkSet = new LinkedHashSet<String>();
				}

				pkSet.add(rsPkPerTable.getString(2));
				currTable.setPrimaryKey(pkSet);
			}

		} catch (SQLException sq) {
			LogManager.getLogger().error("SQL error.  " + sq + "\n" + pkQuery);
		}
	}

	public String showTablesWithDef() {
		String result = "";
		for (DBTableExport table : tableList.values()) {
			result = result
					+ "The DB table " + table.getLinkedTables().get(0).getColumnList()
					+ " was selected with " + table.getLinkedTables().get(0).getTableName() + " "
					+ table.getTableName()
					+ " and "
					+ (table.getWhereClause() == null || table.getWhereClause().length() == 0 ? ""
							: " is restricted by: " + table.getWhereClause())
					+
					"\n";
		}
		return result;
	}

	public String showDefwithTables() {
		String result = "";
		for (DBTableExport table : tableList.values()) {
			result = result
					+ "The DB table " + table.getLinkedTables().get(0).getColumnList()
					+ " was selected with " + table.getLinkedTables().get(0).getTableName() + " "
					+ table.getTableName()
					+ " and "
					+ (table.getWhereClause() == null || table.getWhereClause().length() == 0 ? ""
							: " is restricted by: " + table.getWhereClause())
					+
					"\n";
		}
		return result;
	}

	public String getTotals() {
		int colTotal = 0;
		Iterator<DBTableExport> ite = tableList.values().iterator();
		while (ite.hasNext()) {
			colTotal = colTotal + ite.next().getColumnList().size();
		}
		return "Total tables " + this.tableList.size() + " Total Colums " + colTotal;

	}

	public void addToCache() {
		this.isCachedData = true;
		CachedData.put(MyService.getCachedKey(connectionName, tablePattern), this);
		if (tablePattern.equalsIgnoreCase(Settings.tableSearchAll))
			saveJson();
	}

	public void saveJson() {
		String json = Utils.toJson(this.getColumnsList());
		Utils.saveFile(
				Settings.getSchemaCachedFile(getConnectionName()),
				json);
	}

	public static DBSchema getFromJson(String connectionName, String tablePattern) {
		String filePath = Settings.getSchemaCachedFile(connectionName);
		if (!Utils.checkExists(filePath))
			return null;
		String jsonString = Utils.getContentFromFile(
				filePath);

		// Map<String, List<String>> tableColumns = Utils.fromJson(jsonString,
		// Map.class);
		// DBSchema schema = new DBSchema();
		// schema.setTableColumns(tableColumns);

		Map<String, List<Object>> tableColumns = Utils.fromJson(jsonString,
				Map.class);
		DBSchema schema = new DBSchema();
		schema.setColumnsList(tableColumns);
		schema.setConnectionName(connectionName);
		schema.setTableSearch(tablePattern);
		return schema;
	}

	public static DBSchema getFromCache(String connectionName) {
		return getFromCache(connectionName, "");
	}

	public static DBSchema getFromCache(String connectionName, String tablePattern) {
		if (CachedData.containsKey(MyService.getCachedKey(connectionName, tablePattern)))
			return CachedData.get(MyService.getCachedKey(connectionName, tablePattern));
		if (tablePattern.equalsIgnoreCase(Settings.tableSearchAll))
			return getFromJson(connectionName, tablePattern);
		return null;
	}

	public static DBSchema getDBSchema(Connection conn, String connectionName, String tableInclusionClause) {
		if (connectionName == null || connectionName.isEmpty())
			return null;

		DBSchema cachedSchema = DBSchema.getFromCache(connectionName, tableInclusionClause);
		if (cachedSchema != null && cachedSchema.getTableNames().size() > 0)
			return cachedSchema;

		DBSchema destSchema = new DBSchema();
		destSchema.setConnection(conn);
		destSchema.setTableSearch(tableInclusionClause);
		destSchema.setConnectionName(connectionName);

		if (!tableInclusionClause.toLowerCase().contains("table_name ")) {
			String[] searchPatterns = tableInclusionClause.split(",");
			tableInclusionClause = "";
			for (String searchPattern : searchPatterns) {
				if (searchPattern.isEmpty())
					searchPattern = "%";
				tableInclusionClause = tableInclusionClause + " or (tc.table_name like '" + searchPattern.toUpperCase()
						+ "')";
			}
			if (tableInclusionClause.startsWith(" or "))
				tableInclusionClause = tableInclusionClause.substring(3, tableInclusionClause.length());
		}

		tableInclusionClause = tableInclusionClause.replace("table_name ", "tc.table_name ");
		tableInclusionClause = tableInclusionClause.replace(Settings.tempSQLNULL, "");
		tableInclusionClause = tableInclusionClause.replace("tc.tc.", "tc.");

		String query = "select tc.table_name, tc.column_name, tc.DATA_TYPE,DATA_LENGTH,DATA_PRECISION,DATA_SCALE, cols.position, cons.status, cons.owner from user_tab_columns tc "
				+
				"left join user_constraints cons on tc.table_name = cons.table_name AND cons.constraint_type = 'P' " +
				"left join user_cons_columns cols  on  cons.constraint_name = cols.constraint_name AND cons.owner = cols.owner AND tc.column_name = cols.column_name AND tc.table_name = cols.table_name "
				+
				" where " + tableInclusionClause +
				" order by tc.table_name asc, column_id, cols.position";

		try {
			Statement getDBSchema = conn.createStatement();
			ResultSet rsGetDBSchema = DBService.executeQuery(getDBSchema, query); // getDBSchema.executeQuery(query);

			String tableName0 = "";
			String tableName1 = "";
			DBTableExport currTable = new DBTableExport(tableName0);
			String columnName = "";
			String dataType = "";
			String dataLength = "";
			String dataPrecision = "";
			String dataScale = "";
			String position = "";

			// currTable.addColumn(columnName, dataType, dataLength,
			// dataPrecision, dataScale, position != null && !position.isEmpty());

			// destSchema.addTable(currTable);

			while (rsGetDBSchema.next()) {
				columnName = rsGetDBSchema.getString("COLUMN_NAME");
				dataType = rsGetDBSchema.getString("DATA_TYPE");
				dataLength = rsGetDBSchema.getString("DATA_LENGTH");
				dataPrecision = rsGetDBSchema.getString("DATA_PRECISION");
				dataScale = rsGetDBSchema.getString("DATA_SCALE");
				position = rsGetDBSchema.getString("POSITION");
				if (tableName0.isEmpty()) {
					tableName0 = rsGetDBSchema.getString("TABLE_NAME");
					tableName1 = "";
				} else {
					tableName1 = rsGetDBSchema.getString("TABLE_NAME");
				}
				if (tableName1.equals(tableName0)) {
					currTable.addColumn(columnName, dataType, dataLength, dataPrecision, dataScale,
							position != null && !position.isEmpty());
				} else {
					if (tableName1.isEmpty()) {
						currTable = new DBTableExport(tableName0);

					} else {
						currTable = new DBTableExport(tableName1);
						tableName0 = tableName1;
					}
					currTable.addColumn(columnName, dataType, dataLength,
							dataPrecision, dataScale, position != null && !position.isEmpty());

					destSchema.addTable(currTable);
				}
			}
			rsGetDBSchema.close();
		} catch (SQLException sq) {
			MyDialog.showException(sq, query);
		}

		// destSchema.setPrimaryKeys(tableInclusionClause);
		destSchema.addToCache(); // store for later use

		return destSchema;
	}

}