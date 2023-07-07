package com.rs2.core.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.w3c.dom.Document;

import com.rs2.Main;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyTreeMap;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.logs.LogManager;
import com.rs2.core.logs.Logger;
import com.rs2.core.settings.Settings;
import com.rs2.modules.db.DBService;

import com.rs2.core.utils.XMLUtil;

import com.rs2.core.utils.Utils;

public class MyService extends MyCache {
	private static MyService instance;

	public static MyService getInstance() {
		if (instance == null)
			instance = new MyService();
		return instance;
	}

	public static Logger logger = LogManager.getLogger();
	public static String ServiceTitle = "";

	public static List<Map<String, String>> connectionDetails = null;
	public static List<String> storedFileLocators;
	public static final String iniFile = Settings.getIniFilePath();

	public static File lastDir = new File("");
	public static String warningLevel = "HIGH";
	public static String lastFile = "";
	public static String lastConnection = "";
	public static String lastConnectionFile = "";
	public static String lastLookupFile = "";
	public static LinkedList<String> defFiles;
	public static Map<String, Connection> connections;
	public static List<DBLookup> lookupColumns;

	public static MyPane createPane() {
		return null;
	}

	public static MyServiceDialog createDialog() {
		return null;
	}

	public static Map<String, Connection> getConnections() {
		return connections;
	}

	public static void setConnection(Connection conn) {
		Main.connection = conn;
	}

	public static Connection getConnection() {
		return Main.connection;
	}

	public static Map<String, String> getLookupData(String connectionName, String sql, String fieldKey,
			String fieldValue, List<DBParam> queryParams) {
		Map<String, String> lookupData = null;
		String tableName = DBService.getTableNameFromSql(sql).toLowerCase();
		Object[][] cachedFileData = null;

		// try to get from cached file (excel) first
		String cachedFile = Settings.getSettingsFolder() + "\\" + connectionName.toLowerCase();
		Utils.createFolder(cachedFile);
		cachedFile += "\\" + tableName + ".xlsx";
		if (Utils.isFileExist(cachedFile)) {
			cachedFileData = Utils.readArray2DFromExcel(cachedFile);
			if (cachedFileData != null && cachedFileData.length > 0) {
				lookupData = new MyTreeMap();
				for (int i = 1; i < cachedFileData.length; i++) {
					lookupData.put(cachedFileData[i][0].toString(), cachedFileData[i][1].toString());
				}
				return lookupData;
			}
		}

		// if not found then get from database
		sql = DBService.applyParams(sql, queryParams);
		lookupData = new MyTreeMap();

		String[] keysArray = null;
		String[] valuesArray = null;
		int keysIndex = 0, valuesIndex = 1;
		Map<String, Integer> columnsIndex = new TreeMap<String, Integer>();
		String emptyCols = "";
		String emptyHeaders = "";
		int columnsIndexMax = 1;

		try {
			Connection conn = DBService.getConnection(connectionName, null, false);
			if (conn == null)
				return null;
			String[][] data = DBService.convertAndFlipListArray(DBService.executeSqlAsList(sql, conn, -1));
			if (data == null || data.length == 0)
				return lookupData;

			if (fieldKey != null && !fieldKey.isEmpty()) {
				for (int i = 0; i < data.length; i++) {
					if (Utils.equals(data[i][0], fieldKey)) {
						keysArray = data[i];
						keysIndex = i;
						break;
					}
				}
			} else {
				keysIndex = 0;
				keysArray = data[0]; // first column
			}

			if (fieldValue != null && !fieldValue.isEmpty()) {
				for (int i = 0; i < data.length; i++) {
					if (Utils.equals(data[i][0], fieldValue)) {
						valuesArray = data[i];
						valuesIndex = i;
						break;
					}
				}
			} else if (data.length > 1) {
				valuesIndex = 1;
				valuesArray = data[1]; // second column
			}

			if (queryParams != null) {
				for (DBParam param : queryParams) {
					String key = param.getKey();
					String value = param.getValue();
					if (value != null && key != null && key.equalsIgnoreCase(Settings.paramInstitution)) {
						String[] values = value.split(",");
						columnsIndexMax = values.length;
						for (int i = 0; i < values.length; i++) {
							if (!columnsIndex.containsKey(values[i]))
								columnsIndex.put(values[i], i);
						}
					}
				}
			}

			if (keysArray != null && valuesArray != null && keysArray.length > 1) {
				for (int i = 0; i < keysArray.length; i++) {
					if (keysArray[i] == null)
						continue;
					String key = keysArray[i].trim().toLowerCase();
					String value = valuesArray[i] != null ? valuesArray[i].trim() : "";
					boolean buildEmptyCols = emptyCols.isEmpty();

					int colIndex = 0;
					for (int j = 0; j < data.length; j++) {
						if (j != keysIndex && j != valuesIndex) {
							value = value + Settings.dataSeperator + data[j][i]; // value

							if (buildEmptyCols)
								emptyCols = emptyCols + Settings.dataSeperator + " ";

							if (columnsIndex.containsKey(data[j][i]))
								colIndex = columnsIndex.get(data[j][i]);
						}
					}

					if (i == 0 && lookupData instanceof MyTreeMap) // columns, headers {
					{
						emptyHeaders = value;
						// ((MyMapString) lookupData).headers = new String[] {key, value};
						((MyTreeMap) lookupData).headers = new String[] { key,
								Utils.multiplyChars(value, columnsIndexMax, Settings.dataSeperator) };
					} else {
						if (!columnsIndex.containsKey("_" + key))
							columnsIndex.put("_" + key, 0);
						if (lookupData.containsKey(key)) {
							lookupData.put(key, lookupData.get(key) + Settings.dataSeperator + value);
							columnsIndex.put("_" + key, columnsIndex.get("_" + key) + 1);
						} else {
							if (colIndex > 0) {
								lookupData.put(key,
										Utils.multiplyChars(emptyCols, colIndex) + Settings.dataSeperator + value);
								columnsIndex.put("_" + key, colIndex + 1);
							} else {
								lookupData.put(key, value);
								columnsIndex.put("_" + key, 1);
							}
						}
					}
				}
			}

		} catch (SQLException ex) {
			MyDialog.showException(ex, sql);
		}

		for (Map.Entry<String, String> entry : lookupData.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (columnsIndex.containsKey("_" + key) && columnsIndex.get("_" + key) < columnsIndexMax) {
				lookupData.put(key, lookupData.get(key) + Settings.dataSeperator
						+ Utils.multiplyChars(emptyCols, columnsIndexMax - columnsIndex.get("_" + key)));
			}
		}

		// save to cachedFile
		if (cachedFile != null && !cachedFile.isEmpty()) {
			cachedFileData = new Object[lookupData.size() + 1][2];
			int i = 1;
			cachedFileData[0][0] = "key";
			cachedFileData[0][1] = "value";
			for (Map.Entry<String, String> entry : lookupData.entrySet()) {
				cachedFileData[i][0] = entry.getKey();
				cachedFileData[i][1] = entry.getValue();
				i++;
			}
			Utils.writeArrayToExcel(cachedFileData, cachedFile);

		}
		return lookupData;
	}

	public static List<String> getLookupDataList(String connectionName, String sql, String fieldKey,
			String fieldValue, List<DBParam> queryParams) {
		Map<String, String> lookupData = getLookupData(connectionName, sql, fieldKey, fieldValue, queryParams);
		return getLookupDataList(lookupData);
	}

	public static List<String> getLookupDataList(Map<String, String> lookupData) {
		List<String> arr = null;
		if (lookupData != null) {
			arr = new ArrayList<String>();
			for (Map.Entry<String, String> entry : lookupData.entrySet()) {
				arr.add(getDisplayKeyValue(entry.getKey(), entry.getValue()));
			}
		}
		return arr;
	}

	public static List<String> getCachedDataAsList(String connectionName, String fieldLookup,
			List<DBParam> queryParams) {
		Map<String, String> lookupData = getCachedData(connectionName, fieldLookup, queryParams);
		return getLookupDataList(lookupData);
	}

	public static String getDisplayKeyValue(String key, String value) {
		if (key != null && value != null && key.contains(value))
			return key;
		return (value != null && !value.isEmpty()) ? (key + " " + Settings.lookupKeyValueSeperator + " " + value).trim()
				: key;
	}

	public static String getKeyFromDisplayKeyValue(String text) {
		if (text == null)
			return "";

		if (text.contains(" " + Settings.lookupKeyValueSeperator + " "))
			text = text.split(Settings.lookupKeyValueSeperator)[0].trim();
		// else if (text.contains(":"))
		// text = text.split(":")[0].trim();
		// else if (text.contains("-"))
		// text = text.split("-")[0].trim();
		// else if (text.contains("|"))
		// text = text.split("|")[0].trim();
		// else if (text.contains(","))
		// text = text.split(",")[0].trim();
		return text;
	}

	public static String getCachedDataValue(String connectionName, String fieldLookup, String fieldValue,
			List<DBParam> queryParams) {
		if (fieldValue == null)
			return fieldValue;

		Map<String, String> lookupData = getCachedData(connectionName, fieldLookup, queryParams);
		// System.out.println(connectionName + " : " + fieldLookup);
		if (lookupData != null && lookupData.containsKey(fieldValue.trim().toLowerCase())) {
			String tmpValue = lookupData.get(fieldValue.trim().toLowerCase()).split(Settings.dataSeperator)[0];
			return getDisplayKeyValue(fieldValue, tmpValue);
		}
		return fieldValue;
	}

	public static void initParams() {
		// List<Map<String, String>> fileLocators =
		// DataViewerService.loadFileLocators(iniFile);

		String lastDirTag = "";
		lastDirTag = Settings.getIniSetting(Settings.TagLASTDIR);
		lastConnection = Settings.getIniSetting(Settings.TagCONNECTION);
		lastFile = ""; // Settings.getIniSetting(Settings.TagLASTFILE);
		lastConnectionFile = Settings.getLastConnectionFile();
		lastLookupFile = Settings.getLookupFile();

		// storedFileLocators = getStoredFiles(FILES_FOLDER, FILES_EXTENSION);

		if (lastDirTag != null && lastDirTag.length() > 0 && new File(lastDirTag).isDirectory())
			lastDir = new File(lastDirTag);

		warningLevel = "HIGH";
		connectionDetails = getConnectionDetails(); // loadConnectionDetails(lastConnectionFile);
		lookupColumns = loadLookupFields(lastLookupFile);
	}

	public static JMenu createSubMenu(JMenu jMenu, List<String> files, IPanel dialog) {
		if (files == null || files.size() == 0)
			return jMenu;

		jMenu.add(new JSeparator());
		int i = 0;
		for (String file : files) {
			String file1 = Utils.getFileName(file);
			String folder = Utils.getFolder(file);

			if (i == Settings.MaxSubmenuLength) {
				jMenu.add(Utils.createMenuItem("More ...", new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialog.open(folder);
					}
				}));
				break;
			}
			if (file1.startsWith("___"))
				continue;
			JMenuItem jMenuItem = Utils.createMenuItem(file1, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dialog.run(file);
				}
			});
			jMenu.add(jMenuItem);
			i += 1;
		}
		return jMenu;
	}

	public static JMenu createMenu(String title, String fileFolder, String extension, IPanel dialog) {
		JMenu jMenu = Utils.createMenu(title);
		jMenu.add(Utils.createMenuItem(title, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.mainScreen.addPanel(dialog, title);
			}
		}));

		List<String> subFolders = getFolders(fileFolder + "\\" + Settings.featuredSubFolder);
		if (subFolders.size() > 0) {
			for (String folder : subFolders) {
				List<String> files = getFiles(folder, extension, null);
				jMenu = createSubMenu(jMenu, files, dialog);
			}
		} else {

			List<String> files = getFiles(fileFolder + "\\" + Settings.featuredSubFolder, extension, null);
			jMenu = createSubMenu(jMenu, files, dialog);

		}

		return jMenu;
	}

	public static JMenu createMenu(String title, List<String> files, IPanel dialog) {
		JMenu jMenu = Utils.createMenu(title);
		jMenu.add(Utils.createMenuItem(title, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.mainScreen.addPanel(dialog, title);
			}

		}));

		jMenu = createSubMenu(jMenu, files, dialog);

		return jMenu;
	}

	public static String getAppFolder() {
		return Settings.getAppFolder();
	}

	public static List<String> getFolders(String folder) {
		String app_folder = Settings.getModulesFolder();
		return Utils.convertToStringList(Utils.getFolders(folder.startsWith(app_folder) ? folder
				: (app_folder + "\\"
						+ folder),
				false));
	}

	public static List<String> getFiles(String folder, String extension, String lastFile) {
		List<String> storedFileLocators = getStoredFiles(folder,
				extension);
		if (lastFile != null && !lastFile.isEmpty()) {
			String app_folder = Settings.getModulesFolder();
			if (!storedFileLocators.contains((String) lastFile.replace(app_folder, "...")))
				storedFileLocators.add(0, lastFile.replace(app_folder, "..."));
		}
		return storedFileLocators;
	}

	public static List<String> getStoredFiles(String folder, String extension) {
		String app_folder = Settings.getModulesFolder();
		List<String> files = new LinkedList<String>();

		Collection<String> files1 = Utils.getFiles(folder.startsWith(app_folder) ? folder
				: (app_folder + "\\"
						+ folder),
				extension);
		for (String file : files1) {
			if (!files.contains(file))
				files.add(Settings.getIniSettingFolderValue(Settings.TagFILELOCATOR + "_" + folder, file, true));
		}

		for (int i = 0; i < files.size(); i++) {
			String file = files.get(i).replace(app_folder, "...");
			files.set(i, file);
		}

		files = Utils.sortListString(files);
		return files;
	}

	public static List<Map<String, String>> getConnectionDetails() {
		if (connectionDetails == null || connectionDetails.size() == 0)
			connectionDetails = Settings.getConnections(); // connectionDetails =
															// loadConnectionDetails(lastConnectionFile);
		return connectionDetails;
	}

	public static Map<String, String> getConnectionDetails(String connectionName) {
		return getConnectionSettings(connectionName);
	}

	public static Map<String, String> getConnectionSettings(String connectionName) {
		if (connectionName == null || connectionName.isEmpty())
			return null;
		List<Map<String, String>> connectionDetails = getConnectionDetails();
		for (int t = 0; t < connectionDetails.size(); t++) {
			if (connectionDetails.get(t).get(Settings.TagCONNECTIONNAME).trim()
					.equalsIgnoreCase((connectionName.trim()))) {
				return connectionDetails.get(t);
			}
		}

		return null;
	}

	public static String[] getStoredConnectionNames(List<Map<String, String>> connectionDetails) {
		if (connectionDetails == null)
			return new String[0];

		String[] storedConnectionNames = new String[connectionDetails.size()];
		for (int t = 0; t < connectionDetails.size(); t++) {
			storedConnectionNames[t] = connectionDetails.get(t).get(Settings.TagCONNECTIONNAME);
		} // ...wi
		return storedConnectionNames;
	}

	public static String[] getStoredConnectionNames() {
		return getStoredConnectionNames(getConnectionDetails());
	}

	public static List<String> getConnectionNames() {
		List<String> storedConnections = new LinkedList<String>();
		getConnectionDetails();

		if (connectionDetails != null) {
			for (int t = 0; t < connectionDetails.size(); t++) {
				storedConnections.add(connectionDetails.get(t).get(Settings.TagCONNECTIONNAME));
			}
		}

		return storedConnections;
	}

	public static Document getXmlDocument(String filePath) {
		if (defFiles == null)
			defFiles = new LinkedList<String>();
		filePath = getFileNameFull(filePath);
		try {
			defFiles.add(new File(filePath).getCanonicalPath());
		} catch (IOException e) {
			MyDialog.showException(e, "Error loading file: " + filePath);
			return null;
		}
		return XMLUtil.getXmlDocument(filePath);
	}

	public static List<Map<String, String>> loadConnectionDetails(String filePath) {
		return XMLUtil.loadDefinitions(filePath, Settings.TagCONNECTIONDEFINITION);
	}

	public static List<Map<String, String>> loadConnectionDetails(Document doc) {
		return XMLUtil.loadDefinitions(doc, Settings.TagCONNECTIONDEFINITION);
	}

	public static List<Map<String, String>> loadSQLQueryDefinitions(String filePath) {
		return XMLUtil.loadDefinitions(filePath, Settings.TagSQLQUERYDEFINITION);
	}

	public static List<Map<String, String>> loadSQLQueryDefinitions(Document doc) {
		return XMLUtil.loadDefinitions(doc, Settings.TagSQLQUERYDEFINITION);
	}

	public static List<DBLookup> loadLookupFields(String filePath) {
		return Utils.getListDBLookupFromListMapString(XMLUtil.loadDefinitions(filePath, Settings.TagLOOKUP));
	}

	public static List<DBLookup> loadLookupFields(Document doc) {
		return Utils.getListDBLookupFromListMapString(XMLUtil.loadDefinitions(doc, Settings.TagLOOKUP));
	}

	public static List<Map<String, String>> loadIncludes(Document doc) {
		List<Map<String, String>> result = XMLUtil.loadDefinitions(doc, Settings.TagINCLUDE);

		return result;
	}

	public static List<DBLookup> loadLookupFields(Document doc, List<DBLookup> values) {
		return Utils.getListDBLookupFromListMapString(
				XMLUtil.loadDefinitions(doc, Settings.TagLOOKUP, Utils.getListMapStringFromListLookups(values)));
	}

	public static List<DBParam> loadSQLQueryParams(String filePath) {
		return Utils.getListDBParamFromListMapString(XMLUtil.loadDefinitions(filePath, Settings.TagPARAM));
	}

	public static List<DBParam> loadSQLQueryParams(Document doc) {
		return Utils.getListDBParamFromListMapString(XMLUtil.loadDefinitions(doc, Settings.TagPARAM));
	}

	public static List<DBParam> loadSQLQueryParams(Document doc, List<DBParam> queryParams) {
		return loadSQLQueryParams(doc, queryParams, null);
	}

	public static List<DBParam> loadSQLQueryParams(Document doc, List<DBParam> queryParams, List<DBParam> values) {

		List<Map<String, String>> tags = values != null ? XMLUtil.loadDefinitions(doc, Settings.TagPARAM,
				Utils.getListMapStringFromListParam(values)) : XMLUtil.loadDefinitions(doc, Settings.TagPARAM);
		List<DBParam> tmpParams = new LinkedList<DBParam>();
		for (Map<String, String> tag : tags) {
			tmpParams.add(new DBParam(tag));
		}

		if (tmpParams != null && tmpParams.size() > 0) { // merge with queryParams
			if (queryParams != null) {
				for (DBParam param : tmpParams) {
					String key = param.getKey();
					for (DBParam param1 : queryParams) {
						String key1 = param1.getKey();
						String value1 = param1.getValue();
						if (key1.equalsIgnoreCase(key)) {
							param.setValue(value1);
						}
					}
				}
			}
		}

		queryParams = tmpParams;
		return queryParams;
	}

	public static List<Map<String, String>> loadDefinitions(Document doc, String definition) {
		return XMLUtil.loadDefinitions(doc, definition, null);
	}

	public static Set<String> loadSubFiles(Document doc) {
		List<Map<String, String>> list = XMLUtil.loadDefinitions(doc, Settings.TagSUBQUERYLOCATOR, null);
		Set<String> subFiles = new LinkedHashSet<String>();
		for (Map<String, String> entry : list) {
			subFiles.add(entry.get("_"));
		}
		return subFiles;
	}

	public static Boolean hasConnectionDetails(String filePath) {
		return !loadConnectionDetails(filePath).isEmpty();
	}

	public static Boolean hasSQLQueryDefinitions(String filePath) {
		return !loadSQLQueryDefinitions(filePath).isEmpty();
	}

	public static Boolean hasSQLQueryParams(String filePath) {
		return !loadSQLQueryParams(filePath).isEmpty();
	}

	public static void run(String[] args) {
		run();
	}

	public static void run(Map<String, String> parameters) {
		run();
	}

	public static void run() {

	}

	public static void run(String file, MyPane dataPane) {
		Main.startThread("Running file: " + file);
		if (dataPane != null)
			Main.mainScreen.addPanel(dataPane);
		Main.stopThread();
	}

	public static void run(String file) {
		run(file, null);
	}

	public static String getFileName(String file) {
		return Utils.getFileNameShort(file);
	}

	public static String getFileNameFull(String file) {
		return Utils.getFileNameFull(file);
	}

	public static List<DBParam> initQueryParams() {
		List<DBParam> queryParams = new LinkedList<DBParam>();
		return queryParams;
	}

	public static List<DBParam> initQueryParams(List<DBParam> queryParams) {
		if (queryParams == null)
			queryParams = initQueryParams();
		for (int i = 0; i < queryParams.size(); i++) {
			DBParam param = queryParams.get(i);
			if (param.getKey().endsWith(Settings.paramInstitution.replace(Settings.paramPrefix, ""))
					&& param.getValue().isEmpty()) {
				param.setValue(Settings.getLastInstitutionNumber());
				queryParams.set(i, param);
			}
		}
		return queryParams;
	}

	public static JTextField initInstitutionTextField() {
		return Utils.createTextField(Settings.getLastInstitutionNumber(), Settings.paramInstitution);
	}

	public static String getParamValue(Map<String, String> theParameters, String paramName, String defaultValue) {
		String value = theParameters.get(paramName);
		if (value == null) {
			value = defaultValue;
			theParameters.put(paramName, value);
		}
		return value;
	}
}
