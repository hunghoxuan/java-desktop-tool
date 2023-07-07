package com.rs2.core.data;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.rs2.core.settings.Settings;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.Utils;

public class DBTableExport extends XmlElement {
	private String id, tableName, columnName, connectionName;
	private boolean existsInSrc = true;
	private List<String> columnList;
	private HashMap<String, DBColumn> columns;
	private String parentName, childrenName, parentField, childrenField, whereClause, sql, tag;
	// private HashMap<String, String> columnDefinition;
	private int numberOfRows;
	private List<DBTableExport> linkedTables;
	private HashMap<String, String> replaceInstruction;
	private HashSet<String> primaryKey;
	private List<DBParam> queryParams;

	public DBTableExport(String tableName) {
		columnList = new ArrayList<String>();
		columns = new LinkedHashMap<String, DBColumn>();
		setTableName(tableName);
		this.numberOfRows = 0;
	}

	public DBTableExport(Map<String, String> xmlTags) {
		columnList = new ArrayList<String>();
		setXmlTags(xmlTags);
		getWhereClause();
		getReplaceInstruction();
		getTableName();
		getConnectionName();
		getFilePostFix();
		// getColumnDefinition();
		getColumnList();
		this.numberOfRows = 0;
	}

	public List<DBTableExport> getLinkedTables() {
		return linkedTables;
	}

	public String showLinkedTables() {
		StringBuffer result = new StringBuffer("table name = " + this.tableName);
		if (this.columnList.size() > 0 && (this.columns == null || this.columns.size() == 0)) {
			result.append(" and the column name(s) ");
			for (String col : columnList) {
				result.append(col + ", ");
			}
			result = result.delete(result.length() - 2, result.length());
		}
		result.append(" was linked to the table(s): ");
		for (DBTableExport linkedTab : this.linkedTables) {
			result.append("\n" + linkedTab.getTableName());
		}
		result.append("\n");
		return result.toString();
	}

	public List<DBParam> getQueryParams() {
		return this.queryParams;
	}

	public void setQueryParams(List<DBParam> params) {
		this.queryParams = params;
	}

	public void setLinkedTable(List<DBTableExport> linkedTables) {
		this.linkedTables = linkedTables;
	}

	public void addLinkedTable(DBTableExport linkedTable) {
		if (this.linkedTables == null)
			linkedTables = new ArrayList<DBTableExport>();
		this.linkedTables.add(linkedTable);
	}

	public String getId() {
		if (id == null || id.isEmpty())
			id = getXmlTag(Settings.TagID);
		if (id == null || id.isEmpty()) {
			String tag = getTag();
			if (tag.isEmpty())
				id = getTableName();
			else
				id = (getTableName() + "_" + tag).trim();
		}
		return id;
	}

	public String getTag() {
		if (tag == null || tag.isEmpty())
			tag = getXmlTag(Settings.TagFILEPOSTFIX);
		return this.tag == null ? "" : this.tag.trim();
	}

	public String getTableName() {
		if (tableName == null || tableName.isEmpty())
			tableName = getXmlTag(Settings.TagTable);
		return tableName != null ? tableName.trim().toUpperCase() : "";
	}

	public void setTableName(String tableName) {
		this.tableName = tableName != null ? tableName.trim().toUpperCase() : "";
	}

	public String getColumnName() {
		if (columnName == null || columnName.isEmpty())
			columnName = getXmlTag(Settings.TagCOLUMNNAME);
		return columnName != null ? columnName.trim().toUpperCase() : "";
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName != null ? columnName.trim().toUpperCase() : "";
	}

	public boolean isChildren() {
		return !getParentName().isEmpty();
	}

	public String getParentName() {
		if (parentName == null || parentName.isEmpty())
			parentName = getXmlTag(Settings.TagParent);
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName != null ? parentName.trim() : "";
	}

	public String getChildrenName() {
		if (childrenName == null || childrenName.isEmpty())
			childrenName = getXmlTag(Settings.TagChildren);
		return childrenName;
	}

	public void setChildrenName(String childrenName) {
		this.childrenName = childrenName != null ? childrenName.trim() : "";
	}

	public String getParentField() {
		if (parentField == null || parentField.isEmpty())
			parentField = getXmlTag("PARENTFIELD,GROUPBY");
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField != null ? parentField.trim() : "";
	}

	public String getChildrenField() {
		if (childrenField == null || childrenField.isEmpty())
			childrenField = getXmlTag("CHILDRENFIELD,GROUPBY");
		return childrenField;
	}

	public void setChildrenField(String childrenField) {
		this.childrenField = childrenField != null ? childrenField.trim() : "";
	}

	public String getConnectionName() {
		if (connectionName == null || connectionName.isEmpty())
			connectionName = getXmlTag(Settings.TagCONNECTION);
		return connectionName;
	}

	public void setConnectionName(String connName) {
		this.connectionName = connName != null ? connName.trim() : "";
	}

	public List<String> getColumnList() {
		if (columnList == null)
			columnList = new LinkedList<String>();
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

	public void addColumn(String columnName) {
		this.columnList.add(columnName);
	}

	public void addColumn(String columnName, String dataType, String dataLength, String dataPrecision, String dataScale,
			Boolean isPrimaryKey) {
		this.columnList.add(columnName);
		if (columns == null)
			columns = new LinkedHashMap<String, DBColumn>();
		this.columns.put(columnName, new DBColumn(columnName, dataType, Utils.convertToInt(dataLength),
				Utils.convertToInt(dataPrecision), Utils.convertToInt(dataScale), isPrimaryKey));
		if (isPrimaryKey)
			setPrimaryColumn(columnName);
	}

	public void setPrimaryColumn(String columnName) {
		if (getPrimaryKey() == null)
			setPrimaryKey(new LinkedHashSet<String>());
		if (!getPrimaryKey().contains(columnName))
			getPrimaryKey().add(columnName);
	}

	public String getWhereClause() {
		if (whereClause == null || whereClause.isEmpty())
			whereClause = getXmlTag(Settings.TagWHERECLAUSE);
		return whereClause;
	}

	public String getWhereClause(List<DBParam> queryParams) {
		return DBService.applyParams(getWhereClause(), queryParams);
	}

	public String getWhereClause(String instNr, String exportAllCht) {
		return getWhereClause(instNr, exportAllCht, false);
	}

	public String getWhereClause(String instNr, String exportAllCht, boolean isSourceInst) {
		return getWhereClause(Settings.columnInstitution, instNr, exportAllCht, isSourceInst);
	}

	private String replaceSqlWithValue(String whereClause, String columnName, String columnValue,
			boolean isSourceInst) {
		columnName = columnName.toLowerCase();
		String paramColumnName = ":" + columnName;

		if (columnList != null && columnList.contains(columnName.toUpperCase())) {
			if (whereClause.toLowerCase().contains(
					columnName)
					&& !whereClause.toLowerCase().contains(paramColumnName)) {
				if (!isSourceInst) {
					int startIdx = whereClause.indexOf("'", whereClause.toLowerCase().indexOf(columnName));
					int endIdex = startIdx < whereClause.length() - 1
							? whereClause.toLowerCase().indexOf("'", startIdx + 1)
							: -1;
					if (startIdx > -1 && endIdex > -1 && endIdex > startIdx) { // if specific inst is
																				// included in source inst
																				// whereClause then respect
																				// it
						whereClause = Utils.replaceBetween(whereClause, paramColumnName, startIdx + 1, endIdex);
					}
					return whereClause.replace(paramColumnName, columnValue);
				}
			} else if (whereClause.toLowerCase().contains(paramColumnName)) {
				return whereClause.replaceAll(paramColumnName, columnValue);
			} else if (!whereClause.toLowerCase().contains(columnName)) {
				if (whereClause.isEmpty())
					whereClause = columnName + " like '" + columnValue + "'";
				else
					whereClause = whereClause + " and " + columnName + " like '" + columnValue + "'";
			}
			// if (whereClause != null)
			// return finalWhereClause + "and " + whereClause;
		}
		return whereClause;
	}

	public String getWhereClause(String columnName, String columnValue, String exportAllCht, boolean isSourceInst) {
		List<DBParam> queryParams = new LinkedList<DBParam>();
		DBParam param = new DBParam(columnName, columnValue);
		queryParams.add(param);
		return getWhereClause(queryParams, exportAllCht, isSourceInst);
	}

	public String getWhereClause(List<DBParam> queryParams, String exportAllCht, boolean isSourceInst) {
		String finalWhereClause = "where ";
		String sql = getSqlQuery();

		String whereClause = getWhereClause();

		if (whereClause == null || whereClause.isEmpty()) {
			if (sql != null && sql.toLowerCase().contains("where "))
				whereClause = Utils.substringBetween(sql, "where ", "");
		}

		for (DBParam param : queryParams) {
			String columnName = param.getKey().replace(Settings.paramPrefix, "").replace(Settings.paramPrefix1, "");
			String columnValue = param.getValue();
			if (columnList != null && columnList.contains(columnName.toUpperCase())) {
				if (columnName.equalsIgnoreCase(Settings.columnInstitution) && exportAllCht.equals("1")
						&& tableName.startsWith("CHT_")) {
					finalWhereClause = finalWhereClause + "(" + columnName + " like '" + columnValue + "' or "
							+ columnName + "='00000000')";
					return finalWhereClause;
				}

				whereClause = replaceSqlWithValue(whereClause, columnName, columnValue, isSourceInst);
			}
		}

		if (!isSourceInst) { // if get where for dest inst ==> replace possible values in where clause in
								// SELECT/ DELETE
			Map<String, Map<String, String>> replaceMapping = getReplaceMapping();
			if (replaceMapping != null) {
				for (String replCol : replaceMapping.keySet()) {
					if (!Pattern.matches(replCol, columnName)) {
						for (Map.Entry<String, String> entry1 : replaceMapping.get(replCol).entrySet()) {
							String oldString = entry1.getKey(); // v_seq_num
							String newString = entry1.getValue(); // BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)
							if (!oldString.isEmpty() && Utils.isNumeric(oldString)) {
								whereClause = replaceSqlWithValue(whereClause, replCol, newString, isSourceInst);
							}
						}
					}
				}
			}
		}

		if (whereClause != null)
			return finalWhereClause + whereClause;
		else
			return "";
	}

	public boolean containsColumnList(List<String> externalColList) {
		boolean result = false;
		if (this.columnList == null || this.columnList.size() == 0 || this.columnList.get(0) == null)
			return false;
		for (String externalCol : externalColList) {
			for (String column : columnList) {
				boolean intention = true;
				if (externalCol.toUpperCase().replaceFirst(Pattern.quote("^"), "NOT ").equals("NOT "))
					intention = false;
				if (Pattern.matches(
						externalCol.replaceAll("\\*", ".*").replaceAll("%", ".*").toUpperCase().toUpperCase()
								.replaceFirst("NOT ", "").replaceFirst(Pattern.quote("^"), "").trim(),
						column) == intention) {
					result = true;
					break;
				}
			}
			if (result == false)
				return false;
		}
		return result;
	}

	public boolean isExistsInSrc() {
		return existsInSrc;
	}

	public void setExistsInSrc(boolean existsInSrc) {
		this.existsInSrc = existsInSrc;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause != null ? whereClause.trim() : "";
	}

	public void setSqlQuery(String setSqlQuery) {
		this.sql = setSqlQuery != null ? setSqlQuery.trim() : "";
	}

	public String getSqlQuery() {
		if (sql == null || sql.isEmpty())
			sql = getXmlTag(Settings.TagSQLQUERY);
		return this.sql;
	}

	public String buildSql() {
		sql = getSqlQuery();
		if (sql != null && !sql.isEmpty())
			return this.sql;
		String table = this.getTableName();
		String whereClause = this.getWhereClause();
		if (table == null || table.isEmpty())
			return null;
		String sqlTmp = "Select * from " + table;
		if (whereClause.isEmpty() || !whereClause.toLowerCase().contains(Settings.columnInstitution)) {
			if (whereClause.isEmpty())
				whereClause = Settings.columnInstitution + " = '" + Settings.paramInstitution + "'";
			else
				whereClause = whereClause + " and " + Settings.columnInstitution + " = '" + Settings.paramInstitution
						+ "'";
		}
		if (whereClause != null && !whereClause.isEmpty())
			sqlTmp = sqlTmp + " where " + whereClause;

		return DBService.applyParams(sqlTmp, queryParams);
	}

	public void setFilePostFix(String value) {
		this.tag = value;
	}

	public String getFilePostFix() {
		return getTag();
	}

	public Map<String, Map<String, String>> getReplaceMapping() {
		if (getReplaceInstruction() == null || getReplaceInstruction().size() == 0)
			return null;

		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

		for (String replCol : getReplaceInstruction().keySet()) {
			String[] replacement = getReplaceInstruction(replCol)
					.replaceAll("\\*", ".*").replaceAll("%", ".*")
					.split(","); // <REPLACEVALUE>RECORD_ID_NUMBER,v_seq_num,BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)</REPLACEVALUE>
			if (replacement == null || replacement.length == 0) {
				replacement = new String[] { "%", "" };
			}
			String oldString = replacement[0].trim().replaceAll("'", "")
					.replaceAll("\"", ""); // v_seq_num, v_seq_num
			String newString = String.join(",", Arrays
					.copyOfRange(replacement, 1, replacement.length))
					.trim();
			if (!result.containsKey(replCol)) {
				result.put(replCol, new HashMap<String, String>());
			}
			result.get(replCol).put(oldString, newString);
		}
		return result;
	}

	// column : oldvalue,newvalue
	public HashMap<String, String> getReplaceInstruction() {
		String replaceInstr = "";
		String tagReplaceInstruction = getXmlTag(Settings.TagREPLACEVALUE);
		if (replaceInstruction == null && !tagReplaceInstruction.isEmpty())
			try {
				String[] replInstructions = tagReplaceInstruction.split("\n");
				for (int t = 0; t < replInstructions.length; t++) {
					String replColName, replColValue;
					if (replInstructions[t].trim().length() >= 5) {
						replaceInstr = replInstructions[t].trim();

						if (replInstructions[t].contains(",")) {
							replColName = replInstructions[t].substring(0, replInstructions[t].indexOf(",")).trim()
									.toUpperCase();
							replColValue = replInstructions[t].substring(replInstructions[t].indexOf(",") + 1).trim();
						} else if (replaceInstr.equalsIgnoreCase("RECORD_ID_NUMBER")) {
							replColName = replaceInstr;
							replColValue = "v_seq_num,BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)";
						} else {
							replColName = replaceInstr;
							replColValue = "v_seq_num,BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)";
						}
						addReplaceInstruction(replColName, replColValue);
					}
				}
			} catch (Exception e) {
			}
		return replaceInstruction;
	}

	public String getReplaceInstruction(String colName) {
		if (replaceInstruction == null)
			replaceInstruction = getReplaceInstruction();
		return replaceInstruction.get(colName);
	}

	public void setReplaceInstruction(HashMap<String, String> replInstr) {
		this.replaceInstruction = replInstr;
	}

	public void addReplaceInstruction(String columnName, String replaceInstr) {
		if (replaceInstruction == null)
			replaceInstruction = new HashMap<String, String>();
		this.replaceInstruction.put(columnName.trim().replaceAll("'", "").replaceAll("\"", "").replaceAll("\\*", ".*")
				.replaceAll("%", ".*"), replaceInstr);
	}

	public HashSet<String> getPrimaryKey() {
		if (primaryKey == null)
			primaryKey = new LinkedHashSet<String>();
		return primaryKey;
	}

	public void setPrimaryKey(HashSet<String> primaryKey) {
		this.primaryKey = primaryKey;
		// Collections.sort( this.primaryKey);
	}

	public HashMap<String, DBColumn> getColumns() {

		return columns;
	}

	public void setColumns(HashMap<String, DBColumn> columns) {
		this.columns = columns;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	@Override
	public String toString() {
		return " " + tableName;
	}

	public void cloneFrom(DBTableExport from) {
		this.setXmlTags(from.getXmlTags());
		this.setWhereClause(from.getWhereClause());
		this.setSqlQuery(from.getSqlQuery());
		this.setConnectionName(from.getConnectionName());
		this.setReplaceInstruction(from.getReplaceInstruction());
		this.setFilePostFix(from.getFilePostFix());
		this.setParentField(from.getParentField());
		this.setChildrenField(from.getChildrenField());
		this.setParentName(from.getParentName());
		this.setChildrenName(from.getChildrenName());

		this.addLinkedTable(from);
		// this.setColumnList(from.getColumnList());
		from.addLinkedTable(this);
		from.setColumnList(this.getColumnList());
	}
}