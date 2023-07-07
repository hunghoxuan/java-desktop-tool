package com.java.core.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.yaml.snakeyaml.Yaml;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.java.core.components.MyDialog;
import com.java.core.components.evalex.Expression;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;

public class CommonUtil {
    public static String showDate(long date) {
        return showDate(date, "dd-MM-yyyy HH-mm-ss");
    }

    public static String showDate(long date, String format) {
        return new SimpleDateFormat(format).format(new Date(date));
    }

    public static String showDate(File file) {
        return showDate(file.lastModified(), "dd-MM-yyyy HH-mm-ss");
    }

    public static String showDate(Date date, String format) {
        SimpleDateFormat loggingTimeFormat = new SimpleDateFormat(format);
        return loggingTimeFormat.format(date);
    }

    public static String showDate(Date date) {
        return showDate(date, "dd-MM-yyyy");
    }

    public static String showDatetime(Date date) {
        return showDate(date, "dd-MM-yyyy HH:mm");
    }

    public static String showDate() {
        return showDate(new Date(), "dd-MM-yyyy");
    }

    public static String showDateTime() {
        return showDate(new Date(), "dd-MM-yyyy HH:mm");
    }

    public static String showDate(String format) {
        return showDate(new Date(), format);
    }

    public static String formatDate(String format, Date date) {
        try {
            // Date date = new Date();
            // System.out.println("Original Date: " + date);

            // Specify format as "yyyy-MM-dd"
            SimpleDateFormat dmyFormat = new SimpleDateFormat(format);

            // Use format method on SimpleDateFormat
            String formattedDateStr = dmyFormat.format(date);
            return formattedDateStr;
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
            return format;
        }
        // System.out.println("Formatted Date in String format: " + formattedDateStr);
    }

    public static String formatDate(String format) {
        return formatDate(format, new Date());
    }

    public static String formatDate() {
        return formatDate("yyyyMMdd", new Date());
    }

    public static Date convertToDate(String dateInString, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

        Date date;
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return date;
    }

    // convert a yml string format to a map
    public static Map<String, Object> convertYamlStringToMap(String yamlString) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        try {
            Yaml yaml = new Yaml();
            if (!yamlString.startsWith("{"))
                yamlString = "{" + yamlString;
            if (!yamlString.endsWith("}"))
                yamlString = yamlString + "}";
            Map<String, Object> map = (Map<String, Object>) yaml.load(yamlString);

            for (String key : map.keySet()) {
                result.put(key.toUpperCase(), map.get(key));
            }
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
        }
        return result;
    }

    public static <K, V> void insertElementToHashMap(Map<K, V> map, int index, K key, V value) {
        assert (map != null);
        assert !map.containsKey(key);
        assert (index >= 0) && (index < map.size());

        int i = 0;
        List<Entry<K, V>> rest = new ArrayList<Entry<K, V>>();
        for (Entry<K, V> entry : map.entrySet()) {
            if (i++ >= index) {
                rest.add(entry);
            }
        }
        map.put(key, value);
        for (int j = 0; j < rest.size(); j++) {
            Entry<K, V> entry = rest.get(j);
            map.remove(entry.getKey());
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public static boolean inArray(String yourValue, String[] yourArray) {
        return Arrays.asList(yourArray).contains(yourValue);
    }

    public static boolean contains(String[] yourArray, String yourValue) {
        return Arrays.asList(yourArray).contains(yourValue);
    }

    public static boolean endsWith(String yourValue, String[] yourArray) {
        if (yourValue == null || yourValue.length() == 0)
            return false;
        return Arrays.asList(yourArray).contains(yourValue.substring(yourValue.length() - 1));
    }

    public static String leftPad(String inputString, int length) {
        return leftPad(inputString, length, "0");
    }

    public static String leftPad(String inputString, int length, String fillWith) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append(fillWith);
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static String encrypt(String Data) {
        try {
            return encrypt(Data, Settings.kb);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            return decrypt(encryptedData, Settings.kb);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String encrypt(String Data, byte[] k) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k, "AES"));
        byte[] encVal = c.doFinal(Data.getBytes());
        // String encryptedValue = new BASE64Encoder().encode(encVal);
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData, byte[] k) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k, "AES"));
        // byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    public static int countString(String str, String target) {
        return (str.length() - str.replace(target, "").length()) / target.length();
    }

    public static boolean equals(String str, String target) {
        if (str == null && target == null)
            return true;
        if (str == null || target == null)
            return false;
        return str.trim().compareToIgnoreCase(target.trim()) == 0;
    }

    public static boolean containsIgnoreCase(String str, String target) {
        if (str == null || target == null)
            return false;
        String[] values = target.split(",");
        boolean contains = false;
        for (String value : values) {
            contains = str.toLowerCase().contains(value.toLowerCase());
            if (contains)
                break;
        }
        return contains;
    }

    public static boolean startWithIgnoreCase(String str, String target) {
        if (str == null || target == null)
            return false;
        String[] values = target.split(",");
        boolean contains = false;
        for (String value : values) {
            contains = str.toLowerCase().startsWith(value.toLowerCase());
            if (contains)
                break;
        }
        return contains;
    }

    public static boolean equalsWithSeperator(String str, String target) {
        return equalsWithSeperator(str, target, ",");
    }

    public static boolean equalsWithSeperator(String str, String target, String seperator) {

        if (str == null || target == null)
            return false;
        String[] values = target.split(seperator);
        boolean contains = false;
        for (String value : values) {
            contains = str.trim().equalsIgnoreCase(value.trim());
            if (contains)
                break;
        }
        return contains;
    }

    public static String replaceIgnoreCase(String content, String search, String replacedBy) {
        return content.replace(search.toLowerCase(), replacedBy).replace(search.toUpperCase(), replacedBy);
    }

    public static String replaceBetween(String text, String find, String replaceBy) {
        int startIdx = text.toLowerCase().indexOf(find.toLowerCase());
        if (startIdx == -1)
            return text;

        return text.substring(0, startIdx) + replaceBy + text.substring(startIdx + find.length());
    }

    // replace text between startIdx and endIdx with replaceBy.
    public static String replaceBetween(String text, String replaceBy, int startIdx, int endIdx) {

        if (startIdx == -1 && endIdx == -1)
            return text;
        if (startIdx > -1 && endIdx > -1 && startIdx > endIdx)
            return text;

        return endIdx > -1 ? (text.substring(0, startIdx) + replaceBy + text.substring(endIdx))
                : (text.substring(0, startIdx) + replaceBy);
    }

    public static String substringBetween(String text, String start, String end) {
        if (text == null)
            return null;
        if (start == null)
            start = "";
        if (end == null)
            end = "";

        int startIdx = (start == null || start.isEmpty()) ? 0 : text.toLowerCase().indexOf(start.toLowerCase());
        int startIdx1 = (start == null || start.isEmpty()) ? 0 : text.toLowerCase().lastIndexOf(start.toLowerCase());
        int endIdx = (end == null || end.isEmpty()) ? text.length()
                : text.toLowerCase().indexOf(end.toLowerCase(), startIdx + 1);
        int endIdx1 = (end == null || end.isEmpty()) ? text.length()
                : text.toLowerCase().lastIndexOf(end.toLowerCase(), startIdx + 1);
        if (endIdx > startIdx1)
            startIdx = startIdx1;
        else if (endIdx1 > startIdx1) {
            startIdx = startIdx1;
            endIdx = endIdx1;
        }

        if (startIdx == -1 && endIdx == -1)
            return "";

        if (startIdx > -1)
            startIdx = startIdx + start.length();
        else
            startIdx = 0;

        if (endIdx < startIdx && startIdx >= 0)
            endIdx = text.length();

        if (endIdx <= startIdx)
            return "";

        return endIdx > -1 ? text.substring(startIdx, endIdx) : text.substring(startIdx);
    }

    public static boolean checkBoolean(String value) {
        if (value != null && value.trim() != null
                && (value.trim().equalsIgnoreCase(Settings.TRUE)
                        || value.trim().equalsIgnoreCase(Settings.YES)
                        || value.trim().equalsIgnoreCase("1")))
            return true;
        if (value != null && value.trim() != null
                && (value.trim().equalsIgnoreCase(Settings.FALSE)
                        || value.trim().equalsIgnoreCase(Settings.NO)
                        || value.trim().equalsIgnoreCase("0")))
            return false;
        return false;
    }

    public static boolean isNullOrEmptyValue(String value) {
        return isNullOrEmptyValue(value, "null");
    }

    public static boolean isNullOrEmptyValue(String value, String nullValue) {
        return value == null || value.length() == 0 || value.equalsIgnoreCase(nullValue);
    }

    public static boolean isNullOrEmptyArray(String[] value, String nullValue) {
        boolean isNull = true;
        for (int i = 0; i < value.length; i++)
            if (!isNullOrEmptyValue(value[i], nullValue))
                isNull = false;
        return isNull;
    }

    public static String[] split(String seperator, String content) {
        if (content == null)
            return new String[] { null };
        if (content.contains(Settings.dataSeperator))
            return content.split(Settings.dataSeperator);
        return content.split(seperator);
    }

    public static String[] split(String content) {
        if (content == null)
            return new String[] { null };
        if (content.contains(Settings.dataSeperator))
            return content.split(Settings.dataSeperator);
        if (content.contains("\n"))
            return content.split("\n");
        if (content.contains("\t"))
            return content.split("\t");
        if (content.contains(";"))
            return content.split(";");
        return content.split(",");
    }

    public static String[] splitIgnoreInQuote(String input, char seperator, char ignoreQuote) {
        List<String> result = new ArrayList<String>();
        int start = 0;
        boolean inQuotes = false;
        for (int current = 0; current < input.length(); current++) {
            if (input.charAt(current) == ignoreQuote)
                inQuotes = !inQuotes; // toggle state
            else if (input.charAt(current) == seperator && !inQuotes) {
                result.add(input.substring(start, current));
                start = current + 1;
            }
        }
        result.add(input.substring(start));
        return result.toArray(new String[0]);
    }

    public static boolean isContainsInArray(String[] arr, String value) {
        return Arrays.asList(arr).contains(value);
    }

    public static List<String> getUniqueList(List<String> list) {
        List<String> result = new LinkedList<String>();
        for (String item : list) {
            if (!result.contains(item.trim()))
                result.add(item);
        }
        return result;
    }

    public static List<String> getUniqueListFromArray(String[] arr) {
        List<String> result = new LinkedList<>();
        for (int j = 0; j < arr.length - 1; j++) {
            if (!result.contains(arr[j]))
                result.add(arr[j]);
        }
        return result;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String getLabelTitle(String givenString) {
        return toTitleCase(givenString);
    }

    public static String getCammelCase(String givenString) {
        return toTitleCase(givenString);
    }

    public static String getTitleCase(String givenString) {
        return toTitleCase(givenString);
    }

    public static String toTitleCase(String givenString) {
        if (givenString == null)
            return "";
        givenString = givenString.trim();

        // givenString = givenString.replace("_", " ").replace(":", "");
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }

    public static List<Map<String, String>> sortListMap(List<Map<String, String>> list, String field) {
        Collections.sort(list, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> m1, Map<String, String> m2) {
                return m1.get(field).compareToIgnoreCase(m2.get(field));
            }
        });
        return list;
    }

    public static List<String> sortListString(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            public int compare(String m1, String m2) {
                return m1.compareToIgnoreCase(m2);
            }
        });
        return list;
    }

    public static Collection<String> sortCollectionString(Collection<String> list) {
        List<String> result = Utils.convertToStringList(list);
        Collections.sort(result, new Comparator<String>() {
            public int compare(String m1, String m2) {
                return m1.compareToIgnoreCase(m2);
            }
        });
        return result;
    }

    public static Object invokeMethod(Method method, Object obj, Object[] parameters) {
        try {
            return method.invoke(obj, parameters);
        } catch (IllegalAccessException e1) {
            LogManager.getLogger().error(e1);
        } catch (IllegalArgumentException e1) {
            LogManager.getLogger().error(e1);
        } catch (InvocationTargetException e1) {
            LogManager.getLogger().error(e1);
        }
        return null;
    }

    public static boolean convertToBoolean(String value) {
        if (value == null || value.isEmpty())
            return false;
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1");
    }

    private static final String HEXES = "0123456789ABCDEF";

    public static String byteArrayToHexString(byte[] raw) {
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    // public static String byteArrayToHexString(byte[] byteArray){
    // String hexString = "";

    // for(int i = 0; i < byteArray.length; i++){
    // String thisByte = "".format("%x", byteArray[i]);
    // hexString += thisByte;
    // }

    // return hexString;
    // }

    public static byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            String sub = hexString.substring(i, i + 2);
            Integer intVal = Integer.parseInt(sub, 16);
            bytes[i / 2] = intVal.byteValue();
            String hex = "".format("0x%x", bytes[i / 2]);
        }

        return bytes;
    }

    public static <T> T[] arrayMerge(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static byte[] arrayBytesMerge(byte[] array1, byte[] array2) {
        if (array1 == null)
            array1 = new byte[] {};
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    public static void memcpy(Object to, Object from, int size) {
        System.arraycopy(from, 0, to, 0, size);
    }

    public static void memcpy(Object to, int posto, Object from, int posfrom, int size) {
        System.arraycopy(from, posfrom, to, posto, size);
    }

    public static String copyBytesToString(byte[] src, int arg1, int arg2) {
        char[] chrs = new char[arg2 - arg1];
        for (int i = 0, j = arg1; j < arg2; i++, j++) {
            chrs[i] = (char) src[j];
        } // from w ww.jav a2 s .co m

        String s = String.valueOf(chrs);
        return s;
    }

    public static int copyBytes(final byte[] source, final int sOffset, final byte[] destination, final int dOffset,
            final int length) {
        requireSourceLength(source, sOffset, length);
        requireDestinationLength(destination, dOffset, length);
        for (int i = 0; i < length; ++i) {
            destination[i + dOffset] = source[i + sOffset];
        }
        return length;
    }

    public static byte[] copyBytes(byte[] src, int start, int end) {
        int length = end - start;
        byte[] dest = new byte[length];

        for (int i = 0; i < length && i < src.length; i++) {
            dest[i] = src[i - start];
        }

        return dest;
    }

    /**
     * Helper method to check the Contract precondition of read methods
     */
    public static void requireSourceLength(final byte[] source, final int offset, final int length) {
        if (source.length < offset + length - 1) {
            throw new IllegalArgumentException("Not enough data available in source array:\n have " + source.length
                    + " bytes, need " + (offset + length));
        }
    }

    /**
     * Helper method to check the Contract precondition of write methods
     */
    public static void requireDestinationLength(final byte[] destination, final int offset, final int length) {
        if (destination.length < offset + length - 1) {
            throw new IllegalArgumentException("Not enough room available in destination array:\n have "
                    + destination.length + " bytes, need " + (offset + length));
        }
    }

    public static String evalExpression(String expression) {
        return evalExpression(expression, null, null);
    }

    public static String evalExpression(String expression, Map<String, String> vars, Set<String> columns) {
        if (expression == null || expression.isEmpty())
            return "";
        try {
            BigDecimal result = null;
            if (columns == null)
                columns = new TreeSet<String>();

            expression = expression.replace("'", "\"");
            expression = Utils.replaceIgnoreCase(expression, " AND ", " && ");
            expression = Utils.replaceIgnoreCase(expression, " OR ", " || ");

            if (vars != null) {
                for (Map.Entry<String, String> entry : vars.entrySet()) {
                    try {
                        String expressionTmp = Utils.replaceIgnoreCase(expression, entry.getKey(), entry.getValue());
                        if (!expression.equalsIgnoreCase(expressionTmp)) {
                            columns.add(entry.getKey());
                            expression = expressionTmp;
                        }
                        // System.out.println(entry.getKey() + ": " + entry.getValue());
                    } catch (Exception ex) {
                        // LogManager.getLogger().log(ex.getMessage());
                        LogManager.getLogger().error(ex);
                    }
                }
            }

            Expression expression1 = new Expression(expression);
            for (String column : columns) {
                expression1.with(column, vars.get(column));
            }
            result = expression1.eval(); // 1.333333

            return result.toString();
        } catch (Exception ex) {
            // LogManager.getLogger().log(ex.getMessage());
            return ex.getMessage();
        }
    }

    public static boolean isMatchingExpression(String expressionString, Map<String, String> vars, Set<String> columns) {
        String[] expressions = expressionString.split("\n");
        boolean result = false;
        for (String expression : expressions) {
            expression = expression.trim();
            if (expression.isEmpty())
                continue;
            result = convertToBoolean(evalExpression(expression, vars, columns));
            if (result)
                return result;
            if (columns != null)
                columns.clear();
        }
        // LogManager.getLogger().log(expression + " : " + String.valueOf(result));
        return result;
    }

    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                // .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        String json = gson.toJson(obj);
        return json;
    }

    public static <T> T fromJson(String jsonString, Class<T> classOfT) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                // .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        if (jsonString == null || jsonString.isEmpty())
            return null;
        try {
            return gson.fromJson(jsonString, classOfT);
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
            return null;
        }
    }

    public static String format(String format, Map<String, Object> objects, String fieldStart, String fieldEnd) {
        if (!fieldStart.contains("\\")) {
            char[] tmp = fieldStart.toCharArray();
            for (char t : tmp) {
                fieldStart = "\\" + String.valueOf(t);
            }
        }
        if (!fieldEnd.contains("\\")) {
            char[] tmp = fieldEnd.toCharArray();
            for (char t : tmp) {
                fieldEnd = "\\" + String.valueOf(t);
            }
        }

        String regex = fieldStart + "([^}]+)" + fieldEnd;
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(format);
        String result = format;
        while (m.find()) {
            String[] found = m.group(1).split("\\.");
            if (objects.containsKey(found[0])) {
                Object o = objects.get(found[0]);
                Object newVal = null;
                if (found.length > 1) {
                    newVal = Utils.getFieldValueFromName(o, found[1]);
                } else {
                    newVal = o;
                }
                if (newVal != null)
                    result = result.replaceFirst(regex, newVal.toString());
            }
        }
        return result;
    }

    public static String formatTemplate(String format, Object data, String fieldStart,
            String fieldEnd) {
        Handlebars handlebars = new Handlebars();
        Template template;
        String result = "";
        try {
            template = handlebars.compileInline(format, fieldStart, fieldEnd);
            result = template.apply(data).replace("&#x27;", "'").replace("&#x3D;", "=").replace("&amp;", "&");
        } catch (IOException e1) {
            LogManager.getLogger().error(e1);
            result = format;
        }
        return result;
    }

    public static String formatTemplate(String format, Object data) {
        return formatTemplate(format, data, "{", "}");
    }

    public static String getContentFromURL(String url) {
        URLConnection connection = null;
        String content = null;

        try {
            connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        } catch (Exception ex) {
            MyDialog.showException(ex, "Error while reading content from URL: " + url);
        }
        return content;
    }

    public static String getFileContent(File file) {
        if (file == null)
            return null;
        return getContentFromFile(file.getAbsolutePath());
    }

    public static String getContentFromFile(File file) {
        if (file == null)
            return null;
        return getContentFromFile(file.getAbsolutePath());
    }

    public static byte[] getBytesFromFile(String file) {
        if (file == null)
            return null;
        try {
            return Files.readAllBytes(getFilePath(file));
        } catch (IOException e) {
            LogManager.getLogger().error(e);
            return null;
        }
    }

    public static String getFileContent(Path path) {
        return getContentFromFile(path);
    }

    public static String getContentFromFile(Path path) {
        if (path == null)
            return null;
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            LogManager.getLogger().error(e);
            return null;
        }
    }

    public static String getFileName(String file, boolean isFull) {
        if (!isFull)
            return getFileNameShort(file);
        return getFileNameFull(file);
    }

    public static String getFileNameFull(String file) {
        if (file == null)
            return "";
        return file.replace("...", Settings.getModulesFolder());
    }

    public static String getFileNameShort(String file) {
        if (file == null)
            return "";
        return file.replace(Settings.getModulesFolder(), "...");
    }

    public static Path getFilePath(String file) {
        // file = getFileNameFull(file);
        return Paths.get(file);
    }

    public static boolean isBinaryFile(File f) throws IOException {
        String type = Files.probeContentType(f.toPath());
        if (type == null) {
            // type couldn't be determined, assume binary
            return true;
        } else if (type.startsWith("text")) {
            return false;
        } else {
            // type isn't text
            return true;
        }
    }

    public static boolean checkExists(String filePath) {
        return Files.exists(Utils.getFilePath(filePath));
    }

    public static File getFileOrCreate(String filePath) {
        filePath = getFileNameFull(filePath);
        if (checkExists(filePath))
            return new File(filePath);
        Utils.saveFile(filePath, "");
        return new File(filePath);
    }

    public static InputStream getInputStream(String filePath) {
        try {
            return Files.newInputStream(Utils.getFilePath(filePath));
        } catch (IOException e) {
            LogManager.getLogger().error(e);
            return null;
        }
    }

    public static String getContentFromFile(String filePath) {
        if (filePath == null || filePath.isEmpty())
            return null;

        if (!filePath.contains(".")) { // read from internal file
            BufferedReader file = null;
            StringBuffer sb = new StringBuffer("");
            try {
                file = new BufferedReader(new FileReader(new File(filePath)));
                while (file.ready()) {
                    sb.append(new String(file.readLine()) + "\n");
                }
                return sb.toString();
            } catch (FileNotFoundException e) {
                return "";
            } catch (IOException e) {
                return "";
            }
        }

        // filePath = getFileNameFull(filePath);
        String content = null;
        if (filePath.toLowerCase().endsWith(".docx")
                || filePath.toLowerCase().endsWith(".exe")
                || filePath.toLowerCase().endsWith(".xlxx")
                || filePath.toLowerCase().endsWith(".doc")
                || filePath.toLowerCase().endsWith(".ppt")
                || filePath.toLowerCase().endsWith(".pptx")
                || filePath.toLowerCase().endsWith(".zip")
                || filePath.toLowerCase().endsWith(".rar")
                || filePath.toLowerCase().endsWith(".jar")
                || filePath.toLowerCase().endsWith(".pdf")
                || filePath.toLowerCase().endsWith(".jpg")
                || filePath.toLowerCase().endsWith(".png"))
            return "";

        Path path = Utils.getFilePath(filePath);
        if (Files.notExists(path))
            return null;

        // if (isBinaryFile(file))
        // return "";
        byte[] bytes = Utils.getBytesFromFile(filePath);
        if (bytes == null)
            return null;
        content = new String(bytes);

        // content = content.replace(" =", "=");
        // content = content.replace("= ", "=");
        // if (content != null) {
        // content = content.replace(",'", ", '");
        // content = content.replace("=", "= ");
        // content = content.replace(" =", " =");
        // content = content.replace("= ", "= ");
        // content = content.replace(": =", ":=");
        // }

        return content;
    }

    public static File getLastModifiedFolder(String folder) {
        return getLastModifiedFolder(new File(folder));
    }

    public static File getLastModifiedFolder(File dir) {
        File[] files = dir.listFiles();
        File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified))
                .orElse(null);
        // System.out.println(lastModified);
        return lastModified;
    }

    public static File openExternalFile(File file) {
        if (file != null && file.exists()) {
            try {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogManager.getLogger().error(e);
            }
        }

        return file;
    }

    public static File openExternalFile(String filePath) {
        File file = openFile(filePath);
        return openExternalFile(file);
    }

    public static File openFile(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            LogManager.getLogger().error(e);
        }
        if (file == null || !file.exists())
            LogManager.getLogger().error("The file " + filePath + "doesn't exist !!");
        return file;
    }

    public static String getFileName(String file) {
        file = file.replace(Settings.getAppFolder(), "");
        int i1 = file.lastIndexOf("/") + 1;
        int i2 = file.lastIndexOf("\\") + 1;

        file = file.substring(i1 > i2 ? i1 : i2, file.length());
        if (file.contains("."))
            file = file.substring(0, file.lastIndexOf("."));
        return file;
    }

    public static String getFolder(String file) {
        file = getFileNameFull(file);
        int i1 = file.lastIndexOf("/");
        int i2 = file.lastIndexOf("\\");

        return file.substring(0, i1 > i2 ? i1 : i2);
    }

    public static String getFileName(String file, String newFile) {
        file = getFileNameFull(file);
        int i1 = file.lastIndexOf("/") + 1;
        int i2 = file.lastIndexOf("\\") + 1;

        file = file.substring(0, i1 > i2 ? i1 : i2) + newFile;
        return file;
    }

    public static Collection<String> getFiles(final File folder, String extension) {
        return getFiles(folder, extension, true);
    }

    public static Collection<String> getFolders(final File folder, Boolean recursively) {
        return getFiles(folder, "folder", recursively);
    }

    public static Collection<String> getFolders(String folder, Boolean recursively) {
        return getFiles(new File(folder), "folder", recursively);
    }

    public static Collection<String> getFiles(final File folder, String extension, Boolean recursively) {
        if (extension == null)
            extension = "";
        boolean getFoldersOnly = extension.equalsIgnoreCase("folder");

        Collection<String> files = new LinkedList<String>();
        String[] exts = extension.split(",");
        if (folder != null && folder.isDirectory() && folder.listFiles() != null) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    if (getFoldersOnly)
                        files.add(fileEntry.getAbsolutePath());
                    else if (recursively) {
                        files.addAll(getFiles(fileEntry, extension, recursively));
                    }
                } else {
                    for (String ext : exts) {
                        if (!ext.isEmpty() && ext.startsWith("."))
                            ext = "." + ext;
                        if (ext == null || ext.isEmpty()
                                || fileEntry.getAbsolutePath().toLowerCase().endsWith(ext))
                            files.add(fileEntry.getAbsolutePath());
                    }
                }
            }
        }
        return files;
    }

    public static Collection<String> getFiles(final File folder) {
        return getFiles(folder, null);
    }

    public static Collection<String> getFiles(String folder, String extension, Boolean recursively) {
        return getFiles(new File(folder), extension, recursively);
    }

    public static Collection<String> getFiles(String folder, String extension) {
        return getFiles(new File(folder), extension);
    }

    public static Collection<String> getFiles(String folder) {
        return getFiles(new File(folder), null);
    }

    public static Collection<String> getFilesShortNames(Collection<String> files) {
        Collection<String> result = new LinkedList<String>();
        for (String file : files) {
            result.add(getFileNameShort(file));
        }
        return result;
    }

    public static boolean isFileExist(String file) {
        return Files.exists(Paths.get(file));
    }

    public static String createFolder(String folder) {
        if (Files.exists(Paths.get(folder)))
            return folder;
        try {
            Files.createDirectories(Paths.get(folder));
            return folder;
        } catch (IOException ex) {
            LogManager.getLogger().error(ex);
            return "";
        }
    }

    public static void saveFile(String fileName, String content, boolean append) {
        if (fileName == null || fileName.isEmpty()) {
            LogManager.getLogger().error("File name is null or empty !!!");
            return;
        }
        FileWriter filew;
        try {
            filew = new FileWriter(fileName, append);
            filew.append(content);
            filew.flush();
            filew.close();
        } catch (IOException e) {
            MyDialog.showException(e, "Error when saving file: " + fileName);
        }
    }

    public static void saveFile(String fileName, String content) {
        saveFile(fileName, content, false);
    }

    public static void saveFile(String content) {
        String subFolder = "";
        String extension = "";
        if (content.toLowerCase().contains("declare") || content.toLowerCase().contains(":=")) {
            subFolder = "\\sqls";
            extension = "sql";
        } else if (content.toLowerCase().contains("select") || content.toLowerCase().contains("from")) {
            subFolder = "\\sqls";
            extension = "sql";
        }
        String fileName = Utils.selectFile(extension, Settings.getOutputFolder() + subFolder);
        if (!fileName.isEmpty())
            saveFile(fileName, content, false);
    }

    public static void saveFileYaml(String fileName, Object data) {
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(fileName)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
    }

    public static String[] addValueToStringArray(String[] arr, String newValue) {
        if (!Arrays.asList(arr).contains(newValue)) {
            arr = Arrays.copyOf(arr, arr.length + 1);
            arr[arr.length - 1] = newValue;
        }
        return arr;
    }

    public static String addValueToStringArray(String value, String newValue) {
        String[] arr = value.split(",");
        arr = addValueToStringArray(arr, newValue);
        return String.join(",", arr);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }
        try {
            strNum = strNum.trim();
            while (strNum.startsWith("0")) {
                strNum = strNum.substring(1);
            }
            if (strNum.isEmpty())
                return true;
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static Integer convertToInt(String value) {
        if (value == null)
            return null;
        try {
            return (int) Math.round(Double.parseDouble(value));
        } catch (Exception ex) {
            LogManager.getLogger().error(ex);
            return null;
        }
    }

    public static final byte[] convertToByteArray(int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value };
    }

    public static final byte[] convertToByteArray(long value) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; ++i) {
            b[i] = (byte) (value >> (8 - i - 1 << 3));
        }
        return b;
    }

    public static byte[] convertToByteArray(final int[] pIntArray) {
        final byte[] array = new byte[pIntArray.length * 4];
        for (int j = 0; j < pIntArray.length; j++) {
            final int c = pIntArray[j];
            array[j * 4] = (byte) ((c & 0xFF000000) >> 24);
            array[j * 4 + 1] = (byte) ((c & 0xFF0000) >> 16);
            array[j * 4 + 2] = (byte) ((c & 0xFF00) >> 8);
            array[j * 4 + 3] = (byte) (c & 0xFF);
        }
        return array;
    }

    public static int[] convertToIntArray(final byte[] pByteArray) {
        final int[] array = new int[pByteArray.length / 4];
        for (int i = 0; i < array.length; i++)
            array[i] = (((int) (pByteArray[i * 4]) << 24) & 0xFF000000) |
                    (((int) (pByteArray[i * 4 + 1]) << 16) & 0xFF0000) |
                    (((int) (pByteArray[i * 4 + 2]) << 8) & 0xFF00) |
                    ((int) (pByteArray[i * 4 + 3]) & 0xFF);
        return array;
    }

    public static String[] convertToStringArray(Collection<String> list) {
        return list.stream().toArray(String[]::new);
    }

    public static String[] convertToStringArray(Set<String> list) {
        return list.stream().toArray(String[]::new);
    }

    public static List<String> convertToStringList(Set<String> list) {
        return list.stream().collect(Collectors.toList());
    }

    public static List<String> convertToStringList(Collection<String> list) {
        return list.stream().collect(Collectors.toList());
    }

    public static String[] convertToStringArray(List<String> list) {
        return list.stream().toArray(String[]::new);
    }

    public static String convertToStringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static List<List<String>> convert2DArrayToList(Object[][] array) {
        return convert2DArrayToList(array, null);
    }

    public static List<List<String>> convert2DArrayToList(Object[][] array, String[] columns) {
        List<List<String>> list = new ArrayList<List<String>>();
        if (columns != null) {
            List<String> tmp = new ArrayList<String>();
            for (String o : columns)
                tmp.add(String.valueOf(o));
            list.add(tmp);
        }
        for (Object[] l : array) {
            List<String> tmp = new ArrayList<String>();
            for (Object o : l)
                tmp.add(String.valueOf(o));
            list.add(tmp);
        }
        return list;
    }

    public static String[] convertToStringArray(Object[] array) {
        String[] tmp = new String[array.length];
        for (int i = 0; i < array.length; i++)
            tmp[i] = String.valueOf(array[i]);
        return tmp;
    }

    public static String[][] convertToString2DArray(Object[][] array) {
        String[][] tmp = new String[array.length][array[0].length];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++)
                tmp[i][j] = String.valueOf(array[i][j]);
        return tmp;
    }

    public static String[] addIdColumnToStringArray(String[] arr) {
        String[] newArr = new String[arr.length + 1];
        newArr[0] = "#";
        for (int i = 0; i < arr.length; i++) {
            newArr[i + 1] = arr[i];
        }
        return newArr;
    }

    public static String[] removeIdColumnToStringArray(String[] arr) {
        String[] newArr = new String[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            newArr[i - 1] = arr[i];
        }
        return newArr;
    }

    public static Object[][] addIdColumnTo2DArray(Object[][] arr) {
        if (arr == null)
            return null;
        Object[][] newArr = new Object[arr.length][arr[0].length + 1];
        for (int i = 0; i < arr.length; i++) {
            newArr[i][0] = String.valueOf(i + 1);
            for (int j = 0; j < arr[i].length; j++) {
                newArr[i][j + 1] = arr[i][j];
            }
        }
        return newArr;
    }

    public static Object[][] removeIdColumnFrom2DArray(Object[][] arr) {
        if (arr == null)
            return null;
        Object[][] newArr = new Object[arr.length][arr[0].length - 1];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length - 1; j++) {
                newArr[i][j] = arr[i][j + 1];
            }
        }
        return newArr;
    }

    public static String intToFixedLengthString(int value, int length) {
        return intToFixedLengthString(value, length, "0");
    }

    public static String intToFixedLengthString(int value, int length, String fillWith) {
        return String.format("%" + fillWith + length + "d", value);
    }

    public static String getFixLengthValue(String pattern, String value) {
        for (String varPattern : Settings.varsPatterns) {
            String[] tmp = varPattern.split(":");
            if (tmp.length > 1 && tmp[0].equalsIgnoreCase(pattern.replace(Settings.paramPrefix, "")))
                pattern = tmp[1];
        }
        if (isNumeric(pattern))
            pattern = multiplyChars("0", Integer.valueOf(pattern));
        if (value.length() < pattern.length())
            return pattern.substring(0, pattern.length() - value.length()) + value;
        return value;
    }

    public static String multiplyChars(String ch, int multiplier) {
        return multiplyChars(ch, multiplier, "");
    }

    public static String multiplyChars(String ch, int multiplier, String seperator) {
        if (multiplier == 0)
            return "";

        StringBuilder result = new StringBuilder(ch);
        for (int t = 1; t < multiplier; t++) {
            result.append(seperator).append(ch);
        }
        return result.toString();
    }

    public static String multiplyChars(Character ch, int multiplier) {
        return multiplyChars(ch, multiplier, "");
    }

    public static String multiplyChars(Character ch, int multiplier, String seperator) {
        StringBuilder result = new StringBuilder(ch);
        for (int t = 1; t < multiplier; t++) {
            result.append(seperator).append(ch);
        }
        return result.toString();
    }

    public static String removeAliases(String expression) {
        if (!expression.contains("."))
            return expression;
        StringBuilder exp = new StringBuilder(expression);

        if (exp.indexOf(".") == exp.lastIndexOf(".")) {

        }
        return expression;
    }
}