package com.rs2.core.data;

import java.util.*;
import java.sql.*;
import javax.swing.JTextArea;

import com.rs2.core.MainScreen;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyTreeMap;
import com.rs2.core.logs.LogManager;
import com.rs2.core.logs.Logger;
import com.rs2.core.settings.Settings;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.Utils;

public class XmlElement {
	public Logger logger;
	private Map<String, String> xmlTags;

	private Connection connection;
	private String connectionName;

	public XmlElement() {

	}

	public XmlElement(Map<String, String> tags) {
		setXmlTags(tags);
	}

	public Logger getLogger() {
		if (logger == null)
			logger = LogManager.getLogger();

		return logger;
	}

	public void log(String message) {
		getLogger().debug(message);
	}

	// all tags
	public Map<String, String> getXmlTags() {
		return this.xmlTags;
	}

	public void setXmlTags(Map<String, String> tags) {
		// this.xmlTags = tags;
		this.xmlTags = new MyTreeMap(tags);
	}

	public String get(String key) {
		return getXmlTag(key);
	}

	public void put(String key, String value) {
		setXmlTags(key, value);
	}

	public boolean containsKey(String key) {
		return xmlTags != null && xmlTags.containsKey(key);
	}

	public String getXmlTag(String key) {
		String result = Settings.nullValue;
		if (xmlTags != null && xmlTags.containsKey(key))
			result = xmlTags.get(key);
		result = result.replace("&#34;", "\"");
		result = result.replace("&#39;", "'");
		result = result.replace("&#38;", "&");
		result = result.replace("&gt;", ">");
		result = result.replace("&lt;", "<");
		return result;
	}

	public void setXmlTags(String key, String value) {
		if (xmlTags != null)
			xmlTags.put(key, value);
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
}