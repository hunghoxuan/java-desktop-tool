package com.rs2.core.components.myeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import com.rs2.core.settings.Settings;

//Inspired by: https://github.com/kdekooter/xml-text-editor
public class MyView extends PlainView {

    private static HashMap<Pattern, Color> patternColors;
    private static String GENERIC_XML_NAME = "[A-Za-z]+[A-Za-z0-9\\-_]*(:[A-Za-z]+[A-Za-z0-9\\-_]+)?";
    private static String TAG_PATTERN = "(</?" + GENERIC_XML_NAME + ")";
    private static String TAG_END_PATTERN = "(>|/>)";
    private static String TAG_ATTRIBUTE_PATTERN = "(" + GENERIC_XML_NAME + ")\\w*\\=";
    private static String TAG_ATTRIBUTE_VALUE = "\\w*(\"[^\"]*\")";
    private static String TAG_COMMENT = "(<\\!--[\\w ]*-->|--[\\w ]*|--[\\w]*|REM[\\w ]*)";
    private static String TAG_CDATA = "(<\\!\\[CDATA\\[.*\\]\\]>)";

    // private static String TAG_SQLS_DISABLE = "\\b(rem bak none|commit|rem|rem
    // inserting|set define off|;|,|null)\\b";
    private static String TAG_SQLS_HIGHTLIGHT = "\\b(COMMIT|DECLARE|BEGIN|END|VARCHAR2|PLS_INTEGER|is null|sysdate|nvl|to_number|set|is not null|rownum|trunc|between|not|count|connect by|trim|start with|nocycle|prior|encode|decode|left join|right join|join|as|max|min|group by|order by|distinct|like|and|or|in|where|values|insert|into|update|from|delete|drop|select|:|&)\\b";
    private static String TAG_CODE_SYNTAX = "\\b(class|int|void|static|final|public|private|protected|float|if|else|for|while|try|catch|boolean|import|return)\\b";

    private static String TAG_KEYWORDS = "\\b(institution_number|client_number|index_field)\\b";

    private static String[] SQL_KEYWORDS = new String[] {
            "ABORT", "ACCEPT", "ACCESS", "ADD", "ADMIN", "ALL", "ALTER", "AND", "ANY", "ARRAY",
            "ARRAYLEN", "AS", "ASC", "ASSERT", "ASSIGN", "AT", "ATTRIBUTES", "AUDIT",
            "AUTHORIZATION", "AUTO_INCREMENT", "AVG", "BASE_TABLE", "BEGIN", "BETWEEN",
            "BINARY_INTEGER", "BODY", "BOOLEAN", "BY", "CASE", "CAST", "CHAR", "CHAR_BASE",
            "CHECK", "CLOSE", "CLUSTER", "CLUSTERS", "COLAUTH", "COLUMN", "COMMENT", "COMMIT",
            "COMPRESS", "CONNECT", "CONNECTED", "CONSTANT", "CONSTRAINT", "CRASH", "CREATE",
            "CURRENT", "CURRVAL", "CURSOR", "DATABASE", "DATA_BASE", "DBA", "DEALLOCATE",
            "DEBUGOFF", "DEBUGON", "DECIMAL", "DECLARE", "DEFAULT", "DEFINITION", "DELAY",
            "DELETE", "DESC", "DIGITS", "DISPOSE", "DISTINCT", "DO", "DROP", "ELSE", "ELSIF",
            "ENABLE", "END", "ENTRY", "ESCAPE", "EXCEPTION", "EXCEPTION_INIT", "EXCHANGE",
            "EXCLUSIVE", "EXEC", "EXISTS", "EXIT", "EXTERNAL", "FAST", "FETCH", "FILE", "FOR",
            "FORCE", "FOREIGN", "FORM", "FROM", "FUNCTION", "GENERIC", "GOTO", "GRANT", "GROUP",
            "HAVING", "IDENTIFIED", "IF", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INDEXES",
            "INDICATOR", "INITIAL", "INITRANS", "INNER", "INSERT", "INTERFACE", "INTERSECT",
            "INTO", "IS", "JOIN", "KEY", "LEFT", "LEVEL", "LIBRARY", "LIKE", "LIMIT", "LIMITED",
            "LOCAL", "LOCK", "LOG", "LOGGING", "LONG", "LOOP", "MASTER", "MAXEXTENTS", "MAXTRANS",
            "MAXVALUE", "MEMBER", "MINEXTENTS", "MINUS", "MISLABEL", "MODE", "MODIFY", "MULTISET",
            "NEW", "NEXT", "NO", "NOAUDIT", "NOCOMPRESS", "NOLOGGING", "NOPARALLEL", "NOT",
            "NOWAIT", "NULL", "NUMBER_BASE", "OBJECT", "OF", "OFF", "OFFLINE", "ON", "ONLINE",
            "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER", "PACKAGE", "PARALLEL",
            "PARTITION", "PCTFREE", "PCTINCREASE", "PCTUSED", "PLS_INTEGER", "POSITIVE",
            "POSITIVEN", "PRAGMA", "PRIMARY", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE",
            "PUBLIC", "RAISE", "RANGE", "RAW", "READ", "REBUILD", "RECORD", "REF", "REFERENCES",
            "REFRESH", "RELEASE", "REMR", "RENAME", "REPLACE", "RESOURCE", "RESTRICT", "RETURN",
            "RETURNING", "REVERSE", "REVOKE", "RIGHT", "ROLLBACK", "ROW", "ROWID", "ROWLABEL",
            "ROWNUM", "ROWS", "RUN", "SAVEPOINT", "SCHEMA", "SEGMENT", "SELECT", "SEPARATE",
            "SESSION", "SET", "SHARE", "SNAPSHOT", "SOME", "SPACE", "SPLIT", "SQL", "START",
            "STATEMENT", "STORAGE", "SUBTYPE", "SUCCESSFUL", "SYNONYM", "TABAUTH", "TABLE",
            "TABLES", "TABLESPACE", "TASK", "TERMINATE", "THEN", "TO", "TOP", "TRIGGER",
            "TRUNCATE", "TYPE", "UNION", "UNIQUE", "UNLIMITED", "UNRECOVERABLE", "UNUSABLE",
            "UPDATE", "USE", "USING", "VALIDATE", "VALUE", "VALUES", "VARIABLE", "VIEW", "VIEWS",
            "WHEN", "WHENEVER", "WHEREWHERE", "WHILE", "WITH", "WORK"
    };

    private static String[] SQL_DATATYPES = new String[] {
            "BFILE", "BIGINT", "BINARY", "BIT", "BLOB", "CHAR", "CHARACTER", "CLOB", "CURSOR",
            "DATE", "DATETIME", "DATETIME2", "DATETIMEOFFSET", "DEC", "DECIMAL", "DOUBLE", "ENUM",
            "FLOAT", "IMAGE", "INT", "INTEGER", "LONGBLOB", "LONGTEXT", "MEDIUMBLOB", "MEDIUMINT",
            "MEDIUMTEXT", "MLSLABEL", "MONEY", "NATURAL", "NATURALN", "NCHAR", "NCLOB", "NTEXT",
            "NUMBER", "NUMERIC", "NVARCHAR", "NVARCHAR2", "REAL", "ROWTYPE", "SIGNTYPE",
            "SMALLDATETIME", "SMALLINT", "SMALLMONEY", "SQL_VARIANT", "STRING", "TEXT", "TIME",
            "TIMESTAMP", "TINYINT", "TINYTEXT", "UNIQUEIDENTIFIER", "VARBINARY", "VARCHAR",
            "VARCHAR2", "XML", "YEAR"
    };

    private static String[] SQL_FUNCTIONS = new String[] {
            "ABS", "ACOS", "ADDDATE", "ADDTIME", "ASCII", "ASIN", "ATAN", "ATAN2", "AVG", "BIN",
            "BINARY", "CASE", "CAST", "CEIL", "CEILING", "CHARACTER_LENGTH", "CHAR_LENGTH",
            "COALESCE", "CONCAT", "CONCAT_WS", "CONNECTION_ID", "CONV", "CONVERT", "CONVERT_TZ",
            "COS", "COT", "COUNT", "CURDATE", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "CURRENT_USER", "CURTIME", "DATABASE", "DATE", "DATEDIFF", "DATE_ADD", "DATE_FORMAT",
            "DATE_SUB", "DAY", "DAYNAME", "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "DEGREES", "DIV",
            "EXP", "EXTRACT", "FIELD", "FIND_IN_SET", "FLOOR", "FORMAT", "FROM_DAYS",
            "FROM_UNIXTIME", "GET_FORMAT", "GREATEST", "HOUR", "IF", "IFNULL", "INSERT", "INSTR",
            "ISNULL", "LAST_DAY", "LAST_INSERT_ID", "LCASE", "LEAST", "LEFT", "LENGTH", "LN",
            "LOCALTIME", "LOCALTIMESTAMP", "LOCATE", "LOG", "LOG10", "LOG2", "LOWER", "LPAD",
            "LTRIM", "MAKEDATE", "MAKETIME", "MAX", "MICROSECOND", "MID", "MIN", "MINUTE", "MOD",
            "MONTH", "MONTHNAME", "NOW", "NULLIF", "PERIOD_ADD", "PERIOD_DIFF", "PI", "POSITION",
            "POW", "POWER", "QUARTER", "RADIANS", "RAND", "REPEAT", "REPLACE", "REVERSE", "RIGHT",
            "ROUND", "RPAD", "RTRIM", "SECOND", "SEC_TO_TIME", "SESSION_USER", "SIGN", "SIN",
            "SPACE", "SQRT", "STRCMP", "STR_TO_DATE", "SUBDATE", "SUBSTR", "SUBSTRING",
            "SUBSTRING_INDEX", "SUBTIME", "SUM", "SYSDATE", "SYSTEM_USER", "TAN", "TIME",
            "TIMEDIFF", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TIME_FORMAT",
            "TIME_TO_SEC", "TO_DAYS", "TO_SECONDS", "TRIM", "TRUNCATE", "UCASE", "UNIX_TIMESTAMP",
            "UPPER", "USER", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VERSIONWEEK", "WEEK",
            "WEEKDAY", "WEEKOFYEAR", "YEAR", "YEARWEEKYEARWEEK"
    };

    private List<String> proccessed = new LinkedList<String>();

    static {
        // NOTE: the order is important!
        patternColors = new LinkedHashMap<Pattern, Color>();
        patternColors.put(Pattern.compile(TAG_COMMENT), Color.GRAY);
        patternColors.put(Pattern.compile(TAG_PATTERN), Settings.ColorSelectedText);
        patternColors.put(Pattern.compile(TAG_CDATA), Color.GRAY);
        // patternColors.put(Pattern.compile(TAG_ATTRIBUTE_PATTERN),
        // Settings.ColorKeyword);
        patternColors.put(Pattern.compile(TAG_END_PATTERN), Settings.ColorSelectedText);
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_VALUE), Settings.ColorSelectedText);
        patternColors.put(Pattern.compile("\\w*(\'[^\']*\')"), Settings.ColorSelectedText);
        // patternColors.put(Pattern.compile("\\w*(\{[^\']*\})"),
        // Settings.ColorSelectedText);

        // patternColors.put(Pattern.compile(TAG_KEYWORDS, Pattern.CASE_INSENSITIVE),
        // Color.DARK_GRAY);

        patternColors.put(Pattern.compile(TAG_CODE_SYNTAX, Pattern.CASE_INSENSITIVE), Settings.ColorKeyword);

        // patternColors.put(Pattern.compile(TAG_SQLS_DISABLE,
        // Pattern.CASE_INSENSITIVE), Color.GRAY);
        patternColors.put(Pattern.compile(TAG_SQLS_HIGHTLIGHT, Pattern.CASE_INSENSITIVE), Settings.ColorKeyword);
    }

    public MyView(Element element) {

        super(element);

        // Set tabsize to 4 (instead of the default 8)
        getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
        proccessed = new LinkedList<String>();
    }

    @Override
    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
            int p1) throws BadLocationException {
        proccessed = new LinkedList<String>();
        int lastEnd = 0;
        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = getLineBuffer();

        SortedMap<Integer, Integer> startMap = new TreeMap<Integer, Integer>();
        SortedMap<Integer, Color> colorMap = new TreeMap<Integer, Color>();

        // Match all regexes on this snippet, store positions
        for (Map.Entry<Pattern, Color> entry : patternColors.entrySet()) {

            Matcher matcher = entry.getKey().matcher(text);

            while (matcher.find()) {
                if (colorMap.containsKey(matcher.start(1)))
                    continue;
                startMap.put(matcher.start(1), matcher.end());
                colorMap.put(matcher.start(1), entry.getValue());
            }
        }

        // TODO: check the map for overlapping parts

        int i = 0;
        graphics.setFont(Settings.FontNormal);

        // Colour the parts
        for (Map.Entry<Integer, Integer> entry : startMap.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();

            if (end < lastEnd)
                continue; // avoid duplicate painting

            if (i < start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);
                // System.out.println("haha " + segment.toString() + ":" + i + ":" + start + ":"
                // + end);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor(colorMap.get(start));
            i = end;
            doc.getText(p0 + start, i - start, segment);

            if (!proccessed.contains("" + end)) { // avoid duplicate painting
                // proccessed.add(segment.toString() + ":" + end);
                proccessed.add("" + end);
                lastEnd = end;
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
                // System.out.println("hihi " + segment.toString() + ":" + x + ":" + i + ":" +
                // start + ":" + end);
            }
            // System.out.println(proccessed);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }

}