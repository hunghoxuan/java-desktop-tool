package com.java.core.data;

import java.util.Map;

import com.java.core.settings.Settings;

public class DBColumn {
	private String column = "", type = "", content = "";
	private Integer length, precision, scale;
	private Boolean isPrimaryKey;

	public DBColumn(String column, String type, Integer length, Integer precision, Integer scale,
			boolean isPrimaryKey) {
		setName(column);
		setDataType(type);
		setDataLength(length);
		setDataPrecision(precision);
		setDataScale(scale);
		setIsPrimaryKey(isPrimaryKey);
	}

	public String getName() {
		return column;
	}

	public boolean isNumberDataType() {
		return getDataType().toUpperCase().startsWith(Settings.DBFIELD_NUMBER)
				|| getDataType().toUpperCase().startsWith(Settings.DBFIELD_TIMESTAMP)
				|| getDataType().toUpperCase().startsWith(Settings.DBFIELD_DATE);
	}

	public void setName(String value) {
		column = value;
	}

	public String getDataType() {
		return type;
	}

	public void setDataType(String value) {
		type = value;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String value) {
		content = value;
	}

	public Integer getDataLength() {
		return length;
	}

	public void setDataLength(Integer value) {
		length = value;
	}

	public Integer getDataPrecision() {
		return precision;
	}

	public void setDataPrecision(Integer value) {
		precision = value;
	}

	public Integer getDataScale() {
		return scale;
	}

	public void setDataScale(Integer value) {
		scale = value;
	}

	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}

	public void setIsPrimaryKey(Boolean value) {
		isPrimaryKey = value;
	}

	public String toString() {
		return getName() + " [" + getDataType() + " (" + String.valueOf(getDataLength()) + ")]";
	}
}