package com.rs2.core.utils;

import javax.swing.text.JTextComponent;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import com.rs2.Main;
import com.rs2.core.components.MyLinkedMap;
import com.rs2.core.components.mempoi.builder.MempoiBuilder;
import com.rs2.core.components.mempoi.builder.MempoiSheetBuilder;
import com.rs2.core.components.mempoi.domain.MempoiReport;
import com.rs2.core.components.mempoi.domain.MempoiSheet;
import com.rs2.core.components.mempoi.exception.MempoiException;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.base.MyPane;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBTableExport;
import com.rs2.core.data.XmlElement;
import com.rs2.modules.dataviewer.DataPane;
import com.rs2.modules.db.DBService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils extends UIUtil {

    public static String[] getSqlsFromContent(String content) {
        if (!content.toLowerCase().contains("<root>")) { // not xml
            String[] lines = Utils.splitSQLs(content);
            ;
            List<String> sqls = new LinkedList<String>();
            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;
                if (line.trim().toLowerCase().startsWith("select ")) {
                    String sql = line.trim();
                    if (sql.endsWith(";"))
                        sql = sql.substring(0, sql.length() - 1);
                    sqls.add(sql);
                } else if (line.trim().startsWith(Settings.paramComment)
                        && line.trim().contains(Settings.paramCommandStart)) { // -- @name1 = value1, name2 = value2..
                    sqls.add(line.trim());
                }
            }
            return sqls.toArray(new String[0]);
        }
        return null;
    }

    public static String[] getSqlsFromFile(String filePath) {
        String content = getContentFromFile(filePath);
        if (content != null)
            return getSqlsFromContent(content);
        return null;
    }

    public static String replaceParams(String content, Map<String, String> params) {
        return replaceParams(content, params, "v_", ":=", "'", "BEGIN", "--");
    }

    public static String replaceParams(String content, Map<String, String> params, String paramPrefix,
            String paramAssignOperator, String paramValueQuote, String endParamArea, String commentPrefix) {
        String[] lines = content.trim().split("\n");
        String tmp = "";
        boolean inParamArea = true;
        for (String line : lines) {
            if (endParamArea != null && !endParamArea.isEmpty()
                    && line.toLowerCase().trim().startsWith(endParamArea.toLowerCase()))
                inParamArea = false;
            for (String param : params.keySet()) {
                String value = params.get(param);
                if (value == null)
                    value = "";
                // replace line like this, for eg: -- CARD_NUMBERS --
                if (!commentPrefix.isEmpty() && line.trim().replaceAll(" ", "").toLowerCase()
                        .startsWith(commentPrefix + param + commentPrefix)) {
                    line = line + "\n" + line.substring(0, line.indexOf(commentPrefix))
                            + value.replace("\n", "\n" + line
                                    .substring(0, line.indexOf(commentPrefix)))
                            + "\n";
                } else if (inParamArea && (line.trim().toLowerCase().startsWith(param.toLowerCase())
                        || line.trim().toLowerCase().startsWith((paramPrefix + param).toLowerCase()))
                        && line.trim().toLowerCase().contains(paramAssignOperator)) {
                    int idxStart = line.indexOf(paramAssignOperator);
                    if (idxStart > -1)
                        idxStart += paramAssignOperator.length();
                    if (!paramValueQuote.isEmpty() && line.indexOf(paramValueQuote, idxStart) > -1)
                        idxStart = line.indexOf(paramValueQuote, idxStart) + 1;

                    int idxEnd = -1;
                    if (!paramValueQuote.isEmpty() && line.indexOf(paramValueQuote, idxStart) > -1)
                        idxEnd = line.indexOf(paramValueQuote, idxStart) + 1;
                    if (idxEnd == -1 && line.indexOf(";", idxStart) > -1) {
                        idxEnd += line.indexOf(";", idxStart);
                    }

                    System.out.println(line + "_" + String.valueOf(idxStart) + "_" + String.valueOf(idxEnd));

                    String comment = "";
                    if (value.contains(":")) {
                        comment = value.substring(value.indexOf(":")).trim();
                        value = value.substring(0, value.indexOf(":")).trim();

                    } else if (value.contains("-")) {
                        comment = value.substring(value.indexOf("-")).trim();
                        value = value.substring(0, value.indexOf("-")).trim();
                    }
                    if (idxEnd == -1 && idxStart > -1)
                        line = line.substring(0, idxStart) + value;
                    else if (idxEnd > idxStart && idxStart > -1)
                        line = line.substring(0, idxStart) + value + line.substring(idxEnd);
                    if (!comment.isEmpty()) {
                        if (line.contains(commentPrefix))
                            line = line.trim() + " " + comment;
                        else
                            line = line.trim() + " " + commentPrefix + " " + comment;
                    }
                }
            }

            tmp += line + "\n";
        }
        return tmp;
    }

    public static Map<String, String> getJPanelInput(JPanel myPanel) {
        Map<String, String> list = new LinkedHashMap<String, String>();
        for (Component component : myPanel.getComponents()) {
            if (component instanceof JPanel) {
                list.putAll(getJPanelInput((JPanel) component));
                continue;
            }
            if (component.getName() == null)
                continue;
            if (component instanceof JTextComponent) {
                list.put(component.getName(), getComponentValue((JTextComponent) component));
            } else if (component instanceof JComboBox) {
                list.put(component.getName(), getComponentValue((JComboBox) component));
            } else if (component instanceof JCheckBox) {
                list.put(component.getName(), getComponentValue((JCheckBox) component));
            }
        }
        return list;
    }

    public static JFrame run(JFrame frame, String title, int width, int height) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (title != null && !title.isEmpty())
            frame.setTitle(title);
        frame.setSize(width, height);
        frame.setVisible(true);
        return frame;
    }

    public static JFrame run(JApplet applet, String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(applet);
        frame.setSize(width, height);
        applet.init();
        applet.start();
        frame.setVisible(true);
        return frame;
    }

    public static JFrame run(JPanel panel, String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(width, height);
        frame.setVisible(true);
        return frame;
    }

    public static JFrame run(JDialog dialog, String title, int width, int height) {
        dialog.setVisible(true);
        return null;
    }

    public static String getInputValue(Component[] components, String key) {
        for (Component component : components) {
            if (component instanceof JTextField && !component.getName().equalsIgnoreCase(key)) {
                return ((JTextField) component).getText();
            }
        }
        return null;
    }

    public static List<DBParam> getDynamicListMapsFromComponent(Component[] components) {
        List<DBParam> params = new LinkedList<DBParam>();

        for (Component component : components) {
            if (component instanceof JTextField) {
                String key = (String) ((JTextField) component).getClientProperty(Settings.TagKey);
                String value = ((JTextField) component).getText();
                if (key != null && !key.isEmpty()) {
                    DBParam param = new DBParam();
                    param.setKey(key.trim());
                    param.setValue(value.trim());
                    params.add(param);
                }
            }
        }
        return params;
    }

    public static Map<String, String> getDynamicMapsFromComponent(Component[] components) {
        Map<String, String> params = new MyLinkedMap();

        for (Component component : components) {
            if (component instanceof JTextField) {
                String key = (String) ((JTextField) component).getClientProperty(Settings.TagKey);
                String value = ((JTextField) component).getText();
                if (key != null && !key.isEmpty()) {
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    public static String getExceptionMessage(Throwable ex, String message) {
        String tmp = ex.getMessage() == null ? "" : ex.getMessage();
        String theWholeMessage = "[ERROR] " + tmp.toUpperCase().trim() + " (" + ex.getClass().getSimpleName() + ")\n"
                + message
                + getExceptionMessage(ex.getStackTrace());
        return theWholeMessage;
    }

    public static String getExceptionMessage(Throwable ex) {
        return getExceptionMessage(ex, "");
    }

    public static String getExceptionMessage(StackTraceElement[] stacky) {
        StringBuilder allMessages = new StringBuilder("\n...\n");
        int i = 0;
        for (int t = 0; t < stacky.length; t++) {
            if (i > Settings.errorTraceLevel)
                break;
            if (!stacky[t].toString().startsWith("java.") && !stacky[t].toString().startsWith("oracle.")) {
                allMessages.append(stacky[t].toString() + "\n");
                i += 1;
            }
        }
        if (i < stacky.length)
            allMessages.append("..." + "\n");
        return allMessages.toString();
    }

    public static MyPane createErrorPane(Exception e, String message) {
        JTextArea txt = createErrorTextArea(e, message);
        MyPane errorPane = new DataPane(txt);
        errorPane.setEnabled(true);
        return errorPane;
    }

    public static JTextArea createErrorTextArea(Exception e, String message) {
        String content = e != null ? getExceptionMessage(e, message) : message;
        JTextArea txt = Utils.createJTextArea("[EXCEPTION]\n" + content, false);
        txt.setForeground(Color.RED);

        return txt;
    }

    public static JTextArea createMessageTextArea(String content) {
        JTextArea txt = Utils.createJTextArea("[WARNING]\n" + content, false);
        txt.setForeground(Color.BLUE);

        return txt;
    }

    public static String getOutputFolder() {
        return getOutputFolder(null);
    }

    public static String getOutputFolder(String savedFolder) {
        String folder = (savedFolder != null && !savedFolder.isEmpty()) ? savedFolder : new File("").getAbsolutePath();
        if (!folder.toLowerCase().endsWith("\\output") && !folder.toLowerCase().endsWith("\\output\\"))
            folder = folder + "\\output";
        if (!folder.toLowerCase().endsWith("\\"))
            folder = folder + "\\";
        return folder;
    }

    public static String[] splitSQLs(String input) {
        List<String> result = new LinkedList<String>();
        String[] arr;

        // remove first /* */
        if (input.startsWith("/*") && input.contains("*/"))
            input = input.substring(input.indexOf("*/") + 2).trim();

        // remove comments at the end of each line
        arr = input.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String item : arr) {
            if (item.contains(";") && item.contains(Settings.paramComment)
                    && item.lastIndexOf(";") < item.indexOf(Settings.paramComment)) {
                sb.append(item.substring(0, item.indexOf(Settings.paramComment)).trim() + "\n");
            } else if (item.contains(",") && item.contains(Settings.paramComment)
                    && item.indexOf(",") < item.indexOf(Settings.paramComment)) {
                sb.append(item.substring(0, item.indexOf(Settings.paramComment)).trim() + "\n");
            } else if (item.indexOf(Settings.paramComment) > 0) {
                sb.append(item.substring(0, item.indexOf(Settings.paramComment)).trim() + "\n");
            } else if (item.trim().startsWith(Settings.paramComment)) {

            } else
                sb.append(item + "\n");
        }

        input = sb.toString().trim();

        // find blocks
        sb = new StringBuilder();
        arr = input.split("/\n");
        for (String item : arr) {
            if (item.trim().toUpperCase().startsWith("CREATE"))
                result.add(item.trim());
            else if (item.trim().toUpperCase().startsWith("BEGIN"))
                result.add(item.trim());
            else if (item.trim().toUpperCase().startsWith("DECLARE"))
                result.add(item.trim());
            else if (!item.trim().isEmpty() && item.endsWith("\n") && item.startsWith("\n"))
                result.add(item.trim());
            else if (!item.isEmpty())
                sb.append(item.trim());
            // result.addAll(Arrays.asList(splitSQLs(item.trim())));
        }

        input = sb.toString();
        arr = input.split(";");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].trim().startsWith(Settings.paramComment)) {
                if (arr[i].trim().contains("@") && !arr[i].trim().contains("\n")) {
                    result.add(arr[i].trim());
                } else if (arr[i].contains("\n")) {
                    String[] arr2 = arr[i].split("\n");
                    for (int j = 0; j < arr2.length; j++) {
                        result.add(arr2[j].trim());
                    }
                }
                continue;
            }
            if (!arr[i].trim().isEmpty())
                result.add(arr[i].trim());
        }
        return result.toArray(new String[0]);
    }

    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    public static File getUserHomeDirectory() {
        return new File(System.getProperty("user.home"));
    }

    public static String convertToHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                // We need Tab support here, because we print StackTraces as HTML
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }

        String result = builder.toString();

        String linkReg = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
        Pattern patt = Pattern.compile(linkReg);
        Matcher matcher = patt.matcher(result);
        result = matcher.replaceAll("<a href=\"$1\">$1</a>");
        return result;
    };

    public static List<Map<String, String>> getListMapString(List<XmlElement> values) {
        List<Map<String, String>> tmpValues = new LinkedList<Map<String, String>>();
        for (XmlElement tmpParam : values) {
            tmpValues.add(tmpParam.getXmlTags());
        }
        return tmpValues;
    }

    public static List<Map<String, String>> getListMapStringFromListParam(List<DBParam> values) {
        List<Map<String, String>> tmpValues = new LinkedList<Map<String, String>>();
        for (DBParam tmpParam : values) {
            tmpValues.add(tmpParam.getXmlTags());
        }
        return tmpValues;
    }

    public static List<Map<String, String>> getListMapStringFromListLookups(List<DBLookup> values) {
        List<Map<String, String>> tmpValues = new LinkedList<Map<String, String>>();
        for (DBLookup tmpParam : values) {
            tmpValues.add(tmpParam.getXmlTags());
        }
        return tmpValues;
    }

    public static List<DBParam> getListDBParamsFromListString(List<String> list) {
        List<DBParam> params = new LinkedList<DBParam>();

        for (String item : list) {
            params.add(new DBParam(item, ""));
        }
        return params;
    }

    public static List<DBParam> getListDBParamFromListMapString(List<Map<String, String>> values) {
        List<DBParam> tmpValues = new LinkedList<DBParam>();
        for (Map<String, String> tmpParam : values) {
            tmpValues.add(new DBParam(tmpParam));
        }
        return tmpValues;
    }

    public static List<DBLookup> getListDBLookupFromListMapString(List<Map<String, String>> values) {
        List<DBLookup> tmpValues = new LinkedList<DBLookup>();
        for (Map<String, String> tmpParam : values) {
            String columnData = tmpParam.containsKey(Settings.TagFieldLookup.toUpperCase())
                    ? tmpParam.get(Settings.TagFieldLookup
                            .toUpperCase())
                    : "";
            String[] arr = columnData.split(",");
            for (String column : arr) {
                tmpParam.put(Settings.TagFieldLookup.toUpperCase(), column);
                tmpValues.add(new DBLookup(tmpParam));
            }
        }
        return tmpValues;
    }

    /**
     * Sets a field value on a given object
     *
     * @param targetObject the object to set the field value on
     * @param fieldName    exact name of the field
     * @param fieldValue   value to set on the field
     * @return true if the value was successfully set, false otherwise
     */
    public static Field setFieldFromNameValue(Object targetObject, String fieldName, Object fieldValue) {
        Field field = getFieldFromName(targetObject, fieldName);

        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        try {
            field.set(targetObject, fieldValue);
            return field;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Field getFieldFromName(Object targetObject, String fieldName) {
        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }
        Class superClass = targetObject.getClass().getSuperclass();
        while (field == null && superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                superClass = superClass.getSuperclass();
            }
        }
        return field;
    }

    public static Object getFieldValueFromName(Object targetObject, String fieldName) {
        Field field = getFieldFromName(targetObject, fieldName);
        if (field == null)
            return null;
        field.setAccessible(true);
        try {
            return field.get(targetObject);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getCurrentText(JTextComponent txt) {
        String str = txt.getSelectedText();
        if (str != null)
            return str;
        str = txt.getText();

        if (DBService.isStatementSQL(str))
            return str.trim();

        int currPos = txt.getCaretPosition();
        if (currPos > str.length())
            currPos = str.length();
        int startPos = currPos == 0 ? -1 : (str.substring(0, currPos - 1).lastIndexOf(";") + 1);
        int endPos = str.indexOf(";", currPos);
        if (startPos < endPos && startPos > -1)
            return str.substring(startPos, endPos).trim();
        return str.trim();
    }

    public static List<String> getParamsFromSql(String sql) {
        return getParamsFromSql(sql, false);
    }

    public static List<String> getParamsFromSqlWithoutComments(String sql) {
        return getParamsFromSql(sql, true);
    }

    public static List<String> getParamsFromSql(String sql, boolean ignoreComment) {
        List<String> list = new LinkedList<String>();
        // why ?
        // if (sql.toLowerCase().contains("begin") &&
        // sql.toLowerCase().contains("declare")) {
        // sql = substringBetween(sql, "declare", "begin");
        // }
        String[] sqls = Utils.splitSQLs(sql);
        for (String sql1 : sqls) {
            sql1 = sql1.trim();
            List<String> tmp;
            if (!ignoreComment && sql1.toLowerCase().trim().startsWith(Settings.paramComment)) {
                if (sql1.toLowerCase().contains("step ")
                        || sql1.equalsIgnoreCase(Settings.paramComment)) {
                    list.add(sql1);
                }
            } else if (sql1.contains(Settings.paramPrefix) && !sql1.contains(Settings.paramPrefix + "=")) {
                tmp = getParamsFromSql(sql1, Settings.paramPrefix);
                list.addAll(tmp);
            } else if (sql1.contains(Settings.paramPrefix1)) {
                tmp = getParamsFromSql(sql1, Settings.paramPrefix1);
                list.addAll(tmp);
            }
        }

        return CommonUtil.getUniqueList(list);
    }

    public static List<String> getParamsFromListDBQuery(List<DBQuery> listPlans) {
        List<String> list = new ArrayList<String>();
        for (DBQuery plan : listPlans) {
            list.addAll(getParamsFromSql(plan.getSqlQuery()));
        }
        return CommonUtil.getUniqueList(list);
    }

    public static List<String> getParamsFromModulePlansDBTableExport(
            Map<String, Map<String, DBTableExport>> modulePlans) {
        List<String> list = new ArrayList<String>();
        for (Map<String, DBTableExport> module : modulePlans.values()) {
            for (DBTableExport plan : module.values()) {
                list.addAll(getParamsFromSql(plan.getSqlQuery() + plan.getWhereClause()));
            }
        }

        return getUniqueList(list);
    }

    public static List<String> getParamsFromListDBTableExport(List<DBTableExport> listPlans) {
        List<String> list = new ArrayList<String>();
        for (DBTableExport plan : listPlans) {
            list.addAll(getParamsFromSql(plan.getSqlQuery()));
        }
        return getUniqueList(list);
    }

    public static List<String> getParamsFromSql(String sql, String paramPrefix) {
        List<String> list = new LinkedList<String>();
        if (Settings.paramPostfix.length() > 0) {
            list.addAll(getParamsFromSql(sql, paramPrefix, Settings.paramPostfix));
        } else {
            if (sql.contains("'" + paramPrefix)) {
                List<String> tmp = getParamsFromSql(sql, paramPrefix, "'");
                if (tmp.size() > 0) {
                    list.addAll(tmp);
                    return list;
                }
            }
            String[] operators = new String[] { "\n", ")", "}", ";", ",", "]", "[", "|", "%", "^", "&", ">", "<", "+",
                    "-",
                    " ", "" };
            for (String operator : operators) {
                List<String> tmp = getParamsFromSql(sql, paramPrefix, operator);
                if (tmp.size() > 0) {
                    list.addAll(tmp);
                    return list;
                }
            }
        }
        return list;
    }

    public static List<String> getParamsFromSql(String sql, String openChar, String endChar) {
        List<String> result = new LinkedList<String>();
        int startIdx = 0;
        int endIdx = 0;
        while (startIdx > -1 && endIdx > -1) {
            startIdx = sql.indexOf(openChar, endIdx);
            endIdx = endChar.isEmpty() ? sql.length() : sql.indexOf(endChar, startIdx + 1);
            if (endIdx > startIdx && startIdx > -1) {
                String tmp = sql.substring(startIdx, endIdx).trim();
                if (tmp.length() > openChar.length() && !result.contains(tmp) && !tmp.contains(" ")
                        && !tmp.contains("\n"))
                    result.add(tmp);
            }
        }
        return result;
    }

    public static String formatParams(String format, Map<String, Object> objects) {
        return formatTemplate(format, objects, Settings.paramPrefix, Settings.paramPrefix);
    }

    public static String formatQueryParams(String format, List<DBParam> params, String fieldStart, String fieldEnd) {
        Map<String, Object> objects = new LinkedHashMap<String, Object>();
        for (DBParam param : params) {
            objects.put(param.getKey(), "'" + param.getValue() + "'");
        }
        String result = formatTemplate(format, objects, "'" + fieldStart, fieldEnd + "'");
        result = formatTemplate(result, objects, fieldStart, fieldEnd);
        return result;
    }

    public static String getDBParamValue(List<DBParam> params, String key) {
        return getDBParamValue(params, key, "");
    }

    public static String getDBParamValue(List<DBParam> params, String key, String defaultValue) {
        for (DBParam param : params) {
            if (param.getKey().toLowerCase().equalsIgnoreCase(key.toLowerCase()) || param.getKey().toLowerCase()
                    .startsWith(key.toLowerCase() + Settings.paramParamValueSeparator)) {
                return param.getValue();
            }
        }
        return defaultValue;
    }

    public static Map<String, String> addSettingsToInputParams(Map<String, String> params, String[] actions,
            String title) {
        params.put(Settings.paramComment + " " + title, "");
        for (String action : actions) {
            String key = action;

            if (!action.contains(Settings.paramParamValueSeparator))
                key += Settings.paramParamValueSeparator + Settings.YES + "_" + Settings.NO;
            params.put(key, getLookupValuesFromName(key).get(0));
        }

        return params;
    }

    public static String exportDBQueryToExcel(DBQuery query) {
        List<DBQuery> list = new LinkedList<DBQuery>();
        list.add(query);

        return exportListDBQueryToExcel(list);
    }

    // public static String exportListDataToExcel(List<List<String>> data) {
    // String fileName = Utils.selectFile("xlsx", Settings.getAppFolder() +
    // "/excels");
    // return exportExcelToFile(data, fileName);
    // }

    public static String exportMapListDataToExcel(Map<String, List<List<String>>> data) {
        String fileName = Utils.selectFile("xlsx", Settings.getOutputFolder() + "/excels");
        return exportMapListDataToFile(data, fileName);
    }

    public static String exportListDBQueryToExcel(List<DBQuery> listAllQueryDefinitions) {
        String fileName = Utils.selectFile("xlsx", Settings.getOutputFolder() + "/excels");
        return exportListDBQueryToExcel(listAllQueryDefinitions, fileName);
    }

    public static String exportListDbQueryToExcel(List<DBQuery> listAllQueryDefinitions, String fileName) {
        return exportListDBQueryToExcel(listAllQueryDefinitions, fileName);
    }

    public static String exportListDataToFolder(List<List<String>> data, String folder) {
        return exportListDataToFolder(data, folder, "Sheet");
    }

    public static String exportListDataToFolder(List<List<String>> data, String folder, String sheetName) {
        String fileName = Utils.selectFile("xlsx", Settings.getOutputFolder() + "/" + folder);
        return exportListDataToFolder(data, fileName, sheetName);
    }

    public static String exportListDataToFile(List<List<String>> data, String fileName) {
        return exportListDataToFolder(data, fileName, "Sheet");
    }

    public static String exportListDataToFile(List<List<String>> data, String fileName, String sheetName) {
        Map<String, List<List<String>>> mapData = new HashMap<String, List<List<String>>>();
        mapData.put(sheetName, data);

        return exportMapListDataToFile(mapData, fileName);
    }

    public static String exportMapListDataToFile(Map<String, List<List<String>>> data, String fileName) {
        List<DBQuery> listAllQueryDefinitions = new LinkedList<DBQuery>();

        int i = 0;
        for (Map.Entry<String, List<List<String>>> entry : data.entrySet()) {
            i += 1;
            DBQuery query = new DBQuery();
            query.setData(entry.getValue());
            query.setTableName(entry.getKey());
            listAllQueryDefinitions.add(query);
        }

        return exportListDBQueryToExcel(listAllQueryDefinitions, fileName);
    }

    public static String exportListDBQueryToSQL(List<DBQuery> listAllQueryDefinitions) {
        String fileName = Utils.selectFile("sql", Settings.getSqlsOutputFolder());
        return exportListDBQueryToSQL(listAllQueryDefinitions, fileName);
    }

    public static String exportListDBQueryToSQL(List<DBQuery> listAllQueryDefinitions, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            LogManager.getLogger().error("Export File name is null or empty !!");
            return "";
        }

        File fileDest = openFile(fileName);
        StringBuilder sql = new StringBuilder();

        for (DBQuery dbQuery : listAllQueryDefinitions) {
            if (dbQuery.getTableName().isEmpty() || dbQuery.getSqlQuery().isEmpty())
                continue;
            sql.append("-- ").append(dbQuery.getTableName()).append(" (").append(String.valueOf(dbQuery.getRowsCount()))
                    .append(")").append("\n").append(dbQuery.getSqlQuery()).append(";\n\n");
        }
        saveFile(fileName, sql.toString());
        Utils.showToast("File " + fileName + " saved !!");
        return sql.toString();
    }

    public static String exportListDBQueryToExcel(List<DBQuery> listAllQueryDefinitions, String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            LogManager.getLogger().error("Export File name is null or empty !!");
            return "";
        }

        if (listAllQueryDefinitions == null || listAllQueryDefinitions.size() == 0) {
            LogManager.getLogger().error("Export data is null or empty !!");
            return "";
        }

        File fileDest = openFile(fileName, "xlsx");

        try {
            MempoiBuilder eb = MempoiBuilder.aMemPOI()
                    .withAdjustColumnWidth(true)
                    .withFile(fileDest);
            int i = 0;
            List<String> sheetNames = new ArrayList<String>();
            for (DBQuery query : listAllQueryDefinitions) {
                i += 1;
                if (query.getData() == null || !query.hasResultData()) {
                    LogManager.getLogger().debug("Query data is null or empty: " + query.getSQLQueryLabel());
                    continue;
                }

                String sheetName = query.getSQLQueryLabel().replace(":", "_") + "("
                        + String.valueOf(query.getRowsCount())
                        + ")";
                if (sheetNames.contains(sheetName))
                    sheetName = sheetName + "_" + String.valueOf(i);
                sheetNames.add(sheetName);
                MempoiSheet mempoiSheet = MempoiSheetBuilder.aMempoiSheet()
                        // .withPrepStmt(conn.prepareStatement(query.getSqlQuery()))
                        .withData(query.getData())
                        .withSheetName(sheetName)
                        .withSimpleHeaderText(
                                sheetName + ": " + query.getSqlQueryWithParams())
                        .build();
                eb.addMempoiSheet(mempoiSheet);
            }

            CompletableFuture<MempoiReport> fut = eb.build().prepareMempoiReport();
            MempoiReport mempoiReport = fut.get();
            fileName = mempoiReport.getFile();
            Utils.showToast("File " + fileName + " saved !!");
        } catch (InterruptedException | ExecutionException | MempoiException e1) {
            LogManager.getLogger().error(e1);
            return "";
        }
        return fileName;
    }

    public static String writeArrayToExcel(Object[][] data, String fileName) {
        return writeArrayToExcel(data, null, fileName);
    }

    public static String writeArrayToExcel(Object[][] data, String[] columns, String fileName) {
        try {
            File fileDest = openFile(fileName, "xlsx");
            MempoiBuilder eb = MempoiBuilder.aMemPOI()
                    .withAdjustColumnWidth(true)
                    .withFile(fileDest);
            MempoiSheet mempoiSheet = MempoiSheetBuilder.aMempoiSheet()
                    // .withPrepStmt(conn.prepareStatement(query.getSqlQuery()))
                    .withData(Utils.convert2DArrayToList(data, columns))
                    .withSheetName("Sheet1")
                    .build();
            eb.addMempoiSheet(mempoiSheet);
            CompletableFuture<MempoiReport> fut = eb.build().prepareMempoiReport();
            MempoiReport mempoiReport = fut.get();
            fileName = mempoiReport.getFile();
        } catch (MempoiException e1) {
            LogManager.getLogger().error(e1);
            return null;
        } catch (InterruptedException e) {
            LogManager.getLogger().error(e);
            return null;
        } catch (ExecutionException e) {
            LogManager.getLogger().error(e);
            return null;
        }
        Utils.showToast("File " + fileName + " saved !!");
        return fileName;
    }

    public static Object[][] readArray2DFromExcel(String fileName) {
        return readArray2DFromExcel(fileName, null);
    }

    public static Workbook createWorkbook(File file) throws EncryptedDocumentException, IOException {
        WorkbookFactory.addProvider(new HSSFWorkbookFactory());
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        return WorkbookFactory.create(file);
    }

    public static Workbook createWorkbook(InputStream file) throws EncryptedDocumentException, IOException {
        WorkbookFactory.addProvider(new HSSFWorkbookFactory());
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
        return WorkbookFactory.create(file);
    }

    public static Object[][] readArray2DFromExcel(String fileName, String[] columns) {
        Object[][] data = null;
        boolean skipHeader = columns != null && columns.length > 0;
        int i = 0;
        int j = 0;
        try {
            Workbook workbook = createWorkbook(new File(fileName.toString()));
            Integer sheet = workbook.getNumberOfSheets();
            DataFormatter dataFormatter = new DataFormatter();
            for (int s = 0; s < sheet; s++) {
                Sheet ws = workbook.getSheetAt(s);
                Iterator<Row> rowIterator = ws.rowIterator();
                int rowNum = ws.getLastRowNum() + 1;

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (data == null) {
                        int colNum = skipHeader ? columns.length : row.getLastCellNum();
                        data = new Object[skipHeader ? rowNum - 1 : rowNum][colNum];
                    }

                    if (i == 0 && skipHeader) {
                        skipHeader = false;
                        continue;
                    }

                    if (columns != null && columns.length > 0) {
                        j = 0;
                        for (String col : columns) {
                            Cell cell = row.getCell(j);
                            data[i][j] = getExcelCellValue(cell);
                            j++;
                        }
                        i++;
                        continue;
                    } else {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        j = 0;
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            data[i][j] = getExcelCellValue(cell);
                            j++;
                        }
                        i++;
                    }
                }
            }
        } catch (IOException e) {
            LogManager.getLogger().error("Error load data from File: " + fileName);
            // LogManager.getLogger().error(e);
        } catch (Exception e1) {
            LogManager.getLogger().error("File: " + fileName);
            LogManager.getLogger().error(e1);
        }
        return data;
    }

    public static Object getExcelCellValue(Cell cell) {
        if (cell == null)
            return "";
        CellType type;
        Object result = "";
        type = cell.getCellType();

        switch (type) {
            case NUMERIC:// numeric value in excel
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue();
                } else {
                    result = cell.getNumericCellValue();
                }
                String doubleAsString = String.valueOf(result);
                if (doubleAsString.endsWith(".0")) {
                    result = Integer.valueOf(doubleAsString.substring(0, doubleAsString.length() - 2));
                }
                break;
            case STRING: // string value in excel
                result = cell.getStringCellValue();
                break;
            case BOOLEAN: // boolean value in excel
                result = cell.getBooleanCellValue();
                break;
            case FORMULA:
                result = cell.getCellFormula();
                break;
            case BLANK:
                result = " ";
                break;
            default:
                result = "";
        }

        return result;
    }

    public static void showDialog(JDialog dialog) {
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        Utils.centerOnScreen(dialog, true);
    }

    // restart java application
    public static void restartApplication() {
        // Main.main(null);
        try {
            StringBuilder cmd = new StringBuilder();
            cmd.append(System.getProperty("java.home") + File.separator + "bin" +
                    File.separator + "java ");
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                cmd.append(jvmArg + " ");
            }
            cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
            cmd.append(Main.class.getName()).append(" ");
            // for (String arg : args) {
            // cmd.append(arg).append(" ");
            // }
            Runtime.getRuntime().exec(cmd.toString());
            System.exit(0);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
    }

    public static void runCmd(String cmd) {
        try {
            Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"" + cmd + "\"");
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
    }
}