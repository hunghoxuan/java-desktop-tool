package com.rs2.core.data;

import java.util.Map;

import com.rs2.core.settings.Settings;

public class DBParam extends XmlElement {
	private String key, value = "";

	public DBParam(Map<String, String> tags) {
		super(tags);
	}

	public DBParam() {
		super();
	}

	public DBParam(String key, String value) {
		super();
		setKey(key);
		setValue(value);
	}

	public String getKey() {
		if (key == null || key.isEmpty())
			key = getXmlTag(Settings.TagKey);
		return key;
	}

	public void setKey(String key) {
		this.key = key != null ? key.trim() : "";
	}

	public String getValue() {
		if (value == null || value.isEmpty())
			value = getXmlTag(Settings.TagValue);
		return value;
	}

	public void setValue(String value) {
		this.value = value != null ? value.trim() : "";
	}

}