package com.rs2.core.data;

import java.util.Map;

import com.rs2.core.settings.Settings;

public class DBLookup extends XmlElement {
	private String fieldLookup, fieldKey, fieldValue, tableName, lookupTable, cache, sql, refreshSQL = "";

	public DBLookup(Map<String, String> tags) {
		super(tags);
	}

	public DBLookup() {
		super();
	}

	public DBLookup(String key, String value) {
		super();
		// setKey(key);
		// setValue(value);
	}

	public String getFieldLookup() {
		if (fieldLookup == null || fieldLookup.isEmpty())
			fieldLookup = getXmlTag(Settings.TagFieldLookup);
		return fieldLookup;
	}

	public String getColumn() {
		return getFieldLookup();
	}

	public void setFieldLookup(String key) {
		this.fieldLookup = key != null ? key.trim() : "";
	}

	public String getCache() {
		if (cache == null || cache.isEmpty())
			cache = getXmlTag(Settings.TagCache);
		return cache;
	}

	public String getFieldKey() {
		if (fieldKey == null || fieldKey.isEmpty())
			fieldKey = getXmlTag(Settings.TagFieldKey);
		return fieldKey;
	}

	public String getRefreshSQL() {
		if (refreshSQL == null || refreshSQL.isEmpty())
			refreshSQL = getXmlTag("REFRESHSQL");
		return refreshSQL;
	}

	public void setFieldKey(String value) {
		this.fieldKey = value != null ? value.trim() : "";
	}

	public String getFieldValue() {
		if (fieldValue == null || fieldValue.isEmpty())
			fieldValue = getXmlTag(Settings.TagFieldValue);
		return fieldValue;
	}

	public void setFieldValue(String value) {
		this.fieldValue = value != null ? value.trim() : "";
	}

	public String getTableName() {
		if (tableName == null || tableName.isEmpty())
			tableName = getXmlTag(Settings.TagTable);
		if (tableName == null || tableName.isEmpty())
			tableName = getXmlTag(Settings.TagLookupTable);
		return tableName;
	}

	public String getLookupTable() {
		return getTableName();
	}

	public void setTableName(String value) {
		this.tableName = value != null ? value.trim() : "";
	}

	public String getSql() {
		if (sql == null || sql.isEmpty())
			sql = getXmlTag(Settings.TagSQLQUERY);
		if (!sql.toLowerCase().contains("select")) {
			String[] items = sql.split(",");

		}
		return sql;
	}

	public void setSql(String value) {
		this.sql = value != null ? value.trim() : "";
	}
}