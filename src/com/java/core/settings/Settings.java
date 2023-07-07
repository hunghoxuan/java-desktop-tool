package com.java.core.settings;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.yaml.snakeyaml.Yaml;

import com.java.Main;
import com.java.core.base.MyService;
import com.java.core.components.MyDialog;
import com.java.core.components.oraconf.ConfigurationFile;
import com.java.core.components.oraconf.ConfigurationParameter;
import com.java.core.components.oraconf.ParameterFile;
import com.java.core.components.oraconf.Parser;
import com.java.core.logs.LogManager;
import com.java.core.utils.Utils;
import com.java.core.utils.XMLUtil;

public class Settings extends Config {
        public static String defaultOraFile = "C:\\oracle\\ora122\\network\\admin\\tnsnames.ora";
        public static String defaultInstitutionNumber = "";
        public static String appFolder = "";
        public static String cypressFolder = "";

        static final String iniFile = "settings.yml";
        public static MyService MyService = new MyService();

        public static Map<String, Object> iniSettings = new LinkedHashMap<String, Object>();

        public static String getIniFilePath() {
                File iniFileLocator = new File(iniFile);
                if (!iniFileLocator.exists()) {
                        try {
                                iniFileLocator.createNewFile();
                                Utils.saveFile(iniFileLocator.getAbsolutePath(), "");
                        } catch (Exception e) {
                                MyDialog.showException(e,
                                                "Problems occured during the creation of the file iniSettings.xml");
                                return iniFile;
                        }
                }
                return iniFileLocator.getAbsolutePath();
        }

        public static String getAppFolder() {
                if (!Settings.appFolder.isEmpty())
                        return Settings.appFolder;

                String folder = Paths.get(".").toAbsolutePath().normalize().toString();
                Settings.appFolder = folder;
                return folder;
        }

        public static String getCypressFolder() {
                if (!Settings.cypressFolder.isEmpty())
                        return Settings.cypressFolder;

                String folder = Settings.getIniSetting(Settings.TagCYPRESSFOLDER);
                if (folder.isEmpty())
                        folder = Paths.get(".").toAbsolutePath().normalize().toString() + "\\modules\\cypress\\";
                if (!folder.endsWith("\\"))
                        folder += "\\";

                Settings.cypressFolder = folder;
                return folder;
        }

        public static String getDocsFolder() {
                return getAppFolder() + "\\modules\\docs";
        }

        public static String getOutputFolder() {
                return getAppFolder() + "\\output";
        }

        public static String getOutputFolder(String name) {
                return getAppFolder() + "\\output\\" + Utils.showDate("YYYYMMDD_HHII") + "_" + name;
        }

        public static String getModulesFolder() {
                return getAppFolder() + "\\modules";
        }

        public static String getSettingsFolder() {
                return getAppFolder() + "\\modules\\settings";
        }

        public static String getTransGenFolder() {
                return getAppFolder() + "\\modules\\transgen";
        }

        public static String getExcelsFolder() {
                return getOutputFolder() + "\\excels";
        }

        public static String getSqlsFolder() {
                return getModulesFolder() + "\\sqls";
        }

        public static String getSqlsOutputFolder() {
                return getOutputFolder() + "\\sqls";
        }

        public static void storeLastIntsitutionNumber(String institutionNumber) {
                storeIniSettings(Settings.TagINSTITUTION, institutionNumber);
        }

        public static String getLastInstitutionNumber() {
                return Settings.getIniSetting(Settings.TagINSTITUTION, Settings.defaultInstitutionNumber);
        }

        public static void storeLastConnectionName(String connectionName) {
                storeIniSettings(Settings.TagCONNECTION, connectionName);
                MyService.lastConnection = connectionName;
                Main.connectionName = connectionName;
        }

        public static String getLastConnectionName() {
                return Settings.getIniSetting(Settings.TagCONNECTION);
        }

        public static String getLastConnectionFile() {
                return getSettingsFolder() + "\\__connections.xml";
        }

        public static String getLookupFile() {
                return getSettingsFolder() + "\\__lookups.xml";
        }

        public static String getSchemaCachedFile(String connectionName) {
                return getSettingsFolder() + "\\" + connectionName + "\\schema.json";
        }

        public static List<Map<String, String>> getConnections() {
                return getConnections(getLastConnectionFile());
        }

        public static Map<String, String> fillConnectionDetails(Map<String, String> settings, String connectionName,
                        String host, String port, String sid, String dbType, String connectionType,
                        String username, String password) {
                if (connectionName != null && !connectionName.isEmpty())
                        settings.put(Settings.TagCONNECTIONNAME, connectionName);
                if (host != null && !host.isEmpty())
                        settings.put(Settings.TagHOST, host);
                if (sid != null && !sid.isEmpty())
                        settings.put(Settings.TagSERVICENAME, sid);
                if (port != null && !port.isEmpty())
                        settings.put(Settings.TagPORT, port);
                if (dbType != null && !dbType.isEmpty())
                        settings.put(Settings.TagDBTYPE, dbType);
                if (!settings.containsKey(Settings.TagDBTYPE))
                        settings.put(Settings.TagDBTYPE, Settings.defaultDbType);

                if (connectionType != null && !connectionType.isEmpty())
                        settings.put(Settings.TagCONNECTIONTYPE, connectionType);
                if (!settings.containsKey(Settings.TagCONNECTIONTYPE))
                        settings.put(Settings.TagCONNECTIONTYPE, Settings.defaultConnType);

                if (username != null && !username.isEmpty())
                        settings.put(Settings.TagUSERNAME, username);
                if (password != null && !password.isEmpty())
                        settings.put("PASSWORD", password);
                return settings;
        }

        public static List<Map<String, String>> updateConnections(List<Map<String, String>> connectionDetailsTemp,
                        String connectionName, String host, String port, String sid, String dbType,
                        String connectionType,
                        String username, String password) {
                Map<String, String> settings = null;
                int idx = -1;
                for (int i = 0; i < connectionDetailsTemp.size(); i++) {
                        if (connectionDetailsTemp.get(i).get(Settings.TagCONNECTIONNAME)
                                        .equalsIgnoreCase(connectionName)
                                        ||
                                        (connectionDetailsTemp.get(i).get(Settings.TagHOST).equalsIgnoreCase(host)
                                                        && connectionDetailsTemp.get(i).get(Settings.TagSERVICENAME)
                                                                        .equalsIgnoreCase(sid)
                                                        && connectionDetailsTemp.get(i).get(
                                                                        Settings.TagPORT).equalsIgnoreCase(port)
                                                        && connectionDetailsTemp.get(i).get(Settings.TagDBTYPE)
                                                                        .equalsIgnoreCase(dbType)
                                                        && connectionDetailsTemp.get(i).get(Settings.TagCONNECTIONTYPE)
                                                                        .equalsIgnoreCase(connectionType))) {
                                settings = connectionDetailsTemp.get(i);
                                settings = fillConnectionDetails(settings, connectionName, host, port, sid, dbType,
                                                connectionType,
                                                username, password);
                                connectionDetailsTemp.set(i, settings);

                                idx = i;
                        }
                }

                if (settings == null) {
                        settings = new HashMap<String, String>();
                        settings = fillConnectionDetails(settings, connectionName, host, port, sid, dbType,
                                        connectionType,
                                        username, password);
                        connectionDetailsTemp.add(settings);
                }

                return connectionDetailsTemp;
        }

        public static List<Map<String, String>> getConnections(String lastConnectionFile) {
                List<Map<String, String>> connectionDetailsTemp = null;
                if (lastConnectionFile != null && !lastConnectionFile.isEmpty()) {
                        connectionDetailsTemp = MyService.loadConnectionDetails(lastConnectionFile);
                }

                String path = null;
                if (System.getProperty("oracle.net.tns_admin") != null) {
                        path = System.getProperty("oracle.net.tns_admin") + File.separator + "tnsnames.ora";
                } else if (System.getenv("TNS_ADMIN") != null) {
                        path = System.getenv("TNS_ADMIN") + File.separator + "tnsnames.ora";
                } else if (Utils.checkExists(Settings.defaultOraFile)) {
                        path = Settings.defaultOraFile;
                }

                if (path != null) {
                        try {
                                ConfigurationFile cFile = new Parser(new ParameterFile(path)).parse();
                                for (ConfigurationParameter parameter : cFile.getParameters()) {
                                        // System.out.print(parameter.toString());
                                        String tmp = parameter.getValues().toString().toLowerCase().replace(" ", "");
                                        String host = Utils.substringBetween(tmp, "host=", ")");
                                        String sid = Utils.substringBetween(tmp, "service_name=", ")");
                                        String port = Utils.substringBetween(tmp, "port=", ")");

                                        connectionDetailsTemp = updateConnections(connectionDetailsTemp,
                                                        parameter.getName(), host, port, sid,
                                                        null,
                                                        null,
                                                        null,
                                                        null);
                                }
                        } catch (IOException e) {
                                LogManager.getLogger().error(e);
                        } catch (Exception e) {
                                LogManager.getLogger().error(e);
                        }
                }

                return Utils.sortListMap(connectionDetailsTemp, "CONNECTIONNAME");
        }

        static Map<String, String> getInitSettingsByTagName(Document doc, String tagName,
                        Map<String, String> tagsAndContents) {
                if (doc == null || tagName == null || tagName.isEmpty())
                        return new HashMap<String, String>();
                tagName = tagName.toUpperCase();
                Node lastDir = XMLUtil.getXMLNodeByTagName(doc, tagName);

                Map<String, String> result = tagsAndContents != null ? tagsAndContents : new HashMap<String, String>();
                if (lastDir != null && lastDir.hasChildNodes()) {
                        String lastDirLocator = lastDir.getFirstChild().getNodeValue();
                        if (lastDirLocator != null && lastDirLocator.trim() != null) {
                                String value = lastDirLocator.trim();
                                result.put(tagName, lastDirLocator.trim());
                                // xmlExtrakt.add(tagsAndContents);
                        }
                }

                return result;
        }

        public static void loadSettings() {
                iniSettings = loadIniSettings(getIniFilePath());
        }

        public static Map<String, Object> loadIniSettings() {
                return loadIniSettings(getIniFilePath());
        }

        public static JComponent getSettingEditor(String key) {
                return Utils.createTextField(getIniSetting(key));
        }

        public static Map<String, Object> loadIniSettings(String filePath) {
                Yaml yaml = new Yaml();
                InputStream inputStream = Utils.getInputStream(filePath);
                Map<String, Object> tagsAndContents = yaml.load(inputStream);
                return tagsAndContents;
        }

        public static void refreshSettings() {
                iniSettings = Settings.loadIniSettings();
        }

        public static String getIniSetting(String key) {
                return getIniSetting(key, "");
        }

        public static String getIniSetting(String key, String defaultValue) {
                if (iniSettings == null || iniSettings.size() == 0)
                        iniSettings = Settings.loadIniSettings();
                if (key == null)
                        return "";
                if (iniSettings.containsKey(key.toUpperCase())) {
                        String value = new StringBuilder().append(iniSettings.get(key.toUpperCase())).toString();
                        if (value.startsWith("..."))
                                value = getIniSettingFolderValue(key, value, false);
                        return value;
                }
                return defaultValue;
        }

        public static void storeIniSettings(String iniSettingName, String iniSettingValue) {
                storeIniSettings(getIniFilePath(), iniSettingName, iniSettingValue);
        }

        public static void storeIniSettings(String iniFile, String iniSettingName, String iniSettingValue) {
                if (iniSettingValue == null || iniSettingValue.isEmpty())
                        return;
                iniSettingValue = getIniSettingFolderValue(iniSettingName, iniSettingValue, true);
                iniSettings.put(iniSettingName.toUpperCase(), iniSettingValue);
                StringBuilder sb = new StringBuilder();
                for (String key : iniSettings.keySet()) {
                        Object value = iniSettings.get(key);
                        sb.append(key.toUpperCase() + ": ");
                        if (value != null && Utils.isNumeric(value.toString()))
                                sb.append("'" + value.toString() + "'\n");
                        else
                                sb.append(value.toString() + "\n");
                }
                Utils.saveFile(iniFile, sb.toString());

                loadSettings(); // refresh
        }

        public static String getIniSettingFolderValue(String iniSettingName, String iniSettingValue, boolean isSaved) {
                String appFolder = getModulesFolder();
                if (isSaved && iniSettingValue.contains(appFolder)) {
                        iniSettingValue = iniSettingValue.replace(appFolder, "...");
                } else if (!isSaved && iniSettingValue.contains("...")) {
                        iniSettingValue = iniSettingValue.replace("...", appFolder);
                }
                return iniSettingValue;
        }

        public static String getDefaultDbPassword() {
                return Settings.defaultPassword;
        }
}