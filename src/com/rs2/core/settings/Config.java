package com.rs2.core.settings;

import java.awt.*;

import javax.swing.JOptionPane;

import com.rs2.modules.db.DBService;

public class Config {
        public static String defaultOraFile = "C:\\oracle\\ora122\\network\\admin\\tnsnames.ora";
        public static String defaultInstitutionNumber = "";
        public static String appFolder = "";
        public static String cypressFolder = "";
        public static boolean readOnlyDB = false;

        public static boolean allowTempTables = true;
        public static boolean forceClearCachedBeforeEachExecution = true;
        public static String tempTablePrefix = "XVW$";
        public static String tempSQLNULL = "$NULL$";
        public static String errorPrefix = "ERROR@";
        public static String dataSeperator = "@�@";
        public static String stringSeperator = ", ";

        public static int DIALOG_WIDTH = 800;
        public static int DIALOG_HEIGHT = 500;

        public static String defaultUsername = "";
        public static String defaultPassword = "";;
        public static String defaultDbType = "Oracle";
        public static String defaultPort = "";
        public static String defaultConnType = "Service_Name";
        public static String defaultServiceName = "";

        public static boolean errorShowDialog = true;
        public static int errorTraceLevel = 3;

        public static boolean showFlatTree = false;
        public static boolean allowExecuteSQLStatement = true;
        public static String featuredSubFolder = "0";

        public static String DefaultFIELDS_FIXED_LENGTH = "institution_number:8;client_number:8";
        public static String DefaultTABLES_PATTERN = "CBR_%,CHT_%,CAS_%,SYS_%,INT_%,BWT_%,CIS_%,COS_%,HST_%,WSM_%";

        public static String lookupKeyValueSeperator = ":";
        public static String[] autoCompleteStartChars = new String[] { " ", ",", ".", "\t", "\n", "=", ">", "<", "'",
                        "\"", "!" };
        public static String[] tablePrefixes = new String[] { "A", "B", "C", "D", "E" };
        public static String autoCompleteEndChar = "";
        public static boolean autoCompleteFullTextSearch = true;
        public static int MaxSuggestions = 35;

        public static String emptyDisplayModeNOSUPRESS = "NOSUPPRESS";
        public static String emptyDisplayModeSUPRESS = "SUPPRESS";
        public static String keyColumnDefault = "index_field";

        public static String nullValue = "";
        public static boolean hideInheritedColumns = true;
        public static boolean showQueryOnLog = true;
        public static boolean showLogPanelAtDefault = false;
        public static boolean showNodeLabelWhenChildrenIsEmpty = true;
        public static boolean autoSaveConnection = true;
        public static boolean viewForceRefreshedWhenSwitchView = true;
        public static boolean queryOnDemand = true;
        public static boolean LogTerminal = true;
        public static boolean defaultSplitVertical = true;
        public static boolean overrideTabPanelWithSameTitle = false;

        public static boolean settingOverride = false;
        public static boolean settingExecute = true;
        public static boolean settingSaveFile = false;

        public static int MaximumResultRows = 5000;
        public static int MaxLogLengthDisplay = 30000;
        public static int MaxSubmenuLength = 6;

        public static String TagFIELDS_FIXED_LENGTH = "FIELDS_FIXED_LENGTH";
        public static String TagTABLES_PATTERN = "TABLES_PATTERN";

        public static String TagSQLQUERYLABEL = "SQLQUERYLABEL,NAME";
        public static String TagSQLQUERY = "SQLQUERY,SQL,_";
        public static String TagTITLE = "TITLE,DESCRIPTION";
        public static String TagTRANSMITTEDCONDITIONCOLUMNS = "TRANSMITTEDCONDITIONCOLUMNS";
        public static String TagTRANSMITTEDCOLUMNS = "TRANSMITTEDCOLUMNS";
        public static String TagSUBQUERYLOCATOR = "SUBQUERYLOCATOR,INCLUDE";
        public static String TagINCLUDE = "INCLUDE";
        public static String TagREQUIRE = "REQUIRE";
        public static String TagFILEPOSTFIX = "FILEPOSTFIX,PREFIX,VERSION,TAG";
        public static String TagCheckCondition = "CHECK,ERROR,CONDITION,DISPLAY";

        public static String TagSUPERSQLQUERY = "SUPERSQLQUERY,PARENT";
        public static String TagEXECUTEONFIRSTRUN = "EXECUTEONFIRSTRUN";
        public static String TagRESULTCOLUMNS = "RESULTCOLUMNS,COLUMNS";
        public static String TagMAXINHERITANCEDEPTH = "MAXINHERITANCEDEPTH";
        public static String TagEMPTYCOLUMNDISPLAYMODE = "EMPTYCOLUMNDISPLAYMODE";
        public static String TagSUPPRESSDISPLAYIFNODATA = "SUPPRESSDISPLAYIFNODATA";
        public static String TagMAXRESULTROWS = "MAXRESULTROWS";
        public static String TagSQLQUERYDEFINITION = "SQLQueryDefinition,Query";
        public static String TagDBDEFINITOIN = "DBDEFINITION";
        public static String TagPARAM = "SQLQUERYPARAM,PARAM";
        public static String TagLOOKUP = "LOOKUPCOLUMN,LOOKUP";
        public static String TagFILE = "FILE";

        public static String TagCONNECTION = "CONNECTION";
        public static String TagINSTITUTION = "INSITUTION";
        public static String TagCYPRESSFOLDER = "CYPRESSFOLDER";
        public static String TagCONNECTIONDEFINITION = "DBConnectionDefinition,CONNECTION";
        public static String TagLASTFILE = "LASTFILE";
        public static String TagLASTDIR = "LASTDIR";
        public static String TagFILELOCATOR = "FILELOCATOR";
        public static String TagSAVEFOLDERS = "SAVEFOLDER";
        public static String TagGROUPBY = "GROUPBY";
        public static String TagTYPE = "TYPE";

        public static String TagTABLEEXTRACTDEFINITION = "TABLEEXTRACTDEFINITION,EXPORT";
        public static String TagMODULENAME = "MODULENAME,NAME";
        public static String TagMODULE = "MODULE";
        public static String TagWHERECLAUSE = "WHERECLAUSE,WHERE";
        public static String TagCOLUMNNAME = "COLUMNNAME,COLUMN";
        public static String TagREPLACEVALUE = "REPLACEVALUE";
        public static String DefaultInstitutionExport = "Default Institution Export";

        public static String TagCONNECTIONNAME = "CONNECTIONNAME";
        public static String TagCONNECTIONTYPE = "CONNECTIONTYPE";
        public static String TagDBTYPE = "DBTYPE";
        public static String TagSERVICENAME = "SERVICENAME";
        public static String TagHOST = "HOST";
        public static String TagPORT = "PORT";
        public static String TagUSERNAME = "USERNAME";
        public static String TagPASSWORD = "PASSWORD";

        public static String viewDetail = "detail";
        public static String viewStructure = "structure";
        public static String viewCompact = "compact";
        public static String viewDebug = "debug";
        public static String viewContent = "content";

        public static String actionEdit = "edit";
        public static String actionDelete = "delete";
        public static String actionInsert = "insert";
        public static String actionView = "view";
        public static String actionExport = "export";
        public static String actionUndo = "Undo";
        public static String actionRedo = "Redo";

        public static String actionSave = "save";
        public static String actionExecute = "execute";
        public static String actionOverride = "override";

        public static String TRUE = "true";
        public static String FALSE = "false";
        public static String YES = "yes";
        public static String NO = "no";

        public static String searchMethod_Equals = "equals";
        public static String searchMethod_Contains = "contains";
        public static String searchMethod_NotEquals = "!equals";
        public static String searchMethod_NotContains = "!contains";

        public static String searchCondition_AND = "and";
        public static String searchCondition_OR = "or";

        public static String paramRequiredBy = "_required_by_";
        public static String paramNameMultiple = "_s";

        public static String paramPrefix = ":";
        public static String paramPrefix1 = "&";
        public static String paramPostfix = "";
        public static String paramParamValueSeparator = "__";
        public static String paramComment = "--";
        public static String paramCommandStart = "@";
        public static String operatorAND = "&";
        public static String operatorOR = ",";
        public static String operatorAll = "%";
        public static String operatorAll1 = "*";
        public static String columnInstitution = "institution_number";
        public static String paramInstitution = DBService.getParamName(Settings.columnInstitution);// ":institution_number";
        public static String columnClientNumber = "client_number";

        public static String labelHierarchy = "Hierarchy";

        public static String lineSeperator = "\n---------------------------------------------------\n";
        public static byte[] kb = new byte[16];
        public static Color Color1 = new Color(235, 232, 230);
        public static Color Color2 = new Color(197, 211, 229);
        public static Color ColorReadOnlyBG = Color2; // new Color(197, 211, 229);
        public static Color ColorEditBG = (Color) Color.WHITE;
        public static Color ColorWarning = (Color) Color.YELLOW;
        public static Color ColorError = new Color(255, 209, 200);
        public static Color ColorFatal = (Color) Color.RED;
        // public static Color ColorMenuBG = new Color(0, 153, 204);
        public static Color ColorMenuBG = ColorReadOnlyBG;
        public static Color ColorErrorBG = new Color(255, 209, 200);
        public static Color ColorButtonBG = Color2;
        public static Color ColorLabelBG = new Color(36, 47, 155);
        public static Color ColorKeyword = new Color(161, 13, 57);
        public static Color ColorText = (Color) Color.BLACK;
        public static Color ColorReadOnlyText = (Color) Color.GRAY;
        public static Color ColorSelectedText = new Color(36, 47, 201);
        public static Color ColorTreeBG = ColorReadOnlyBG;
        public static Color ColorDialogBG = Color1;
        public static String FontName = "Futura";
        public static int FontSizeNormal = 13;
        public static int FontSizeSmall = 11;
        public static Font FontNormal = new Font(FontName, Font.PLAIN, FontSizeNormal);
        public static Font FontSmall = new Font(FontName, Font.PLAIN, FontSizeSmall);
        public static Font FontBig = new Font(FontName, Font.BOLD, 18);

        public static int TopPane = JOptionPane.YES_OPTION;
        public static int BottomPane = JOptionPane.NO_OPTION;

        public static String License = "RS2 BA TOOLS @ 2022";
        public static String AUTHOR = "hung.ho@rs2.com (BA Team, RS2 Germany GmbH";
        public static String COMPANY = "RS2 Germany GmbH (Frankfurt, Germany)";
        public static String tabTitleInformation = "log";

        public static String TagFieldLookup = "COLUMN";
        public static String TagFieldKey = "KEYCOLUMN";
        public static String TagCache = "CACHE";
        public static String TagParentColumn = "PARENTCOLUMN";
        public static String TagParent = "PARENT";
        public static String TagChildren = "CHILDREN";
        public static String TagFieldValue = "VALUECOLUMN";
        public static String TagTable = "TABLE,TABLENAME";
        public static String TagColumn = "COLUMN,COLUMNNAME";
        public static String TagID = "ID";
        public static String TagLookupTable = "LOOKUPTABLE";
        public static String TagData = "DATA";
        public static String TagQuery = "SQLQUERY";
        public static String TagKey = "KEY";
        public static String TagValue = "VALUE";

        public static String FILETYPE_XML = ".xml";
        public static String FILETYPE_SQL = ".sql";
        public static String FILETYPE_HTML = ".html";
        public static String FILETYPE_JSON = ".json";
        public static String FILETYPE_PDF = ".pdf";

        public static int InputWidth = 200;
        public static int LabelWidth = 200;
        public static int LabelHeight = 25;
        public static int TextFieldWidth = 15;
        public static int SizeTypeSmall = 1;
        public static int SizeTypeNormal = 2;
        public static int SizeTypeBig = 3;

        public static String SELECT_FILE = "file";
        public static String SELECT_FOLDER = "folder";
        public static String SELECT_SAVE = "save";

        public static String QueryTypeNormal = "normal";
        public static String QueryTypeLookup = "lookup";

        public static String StatusRunning = "running";
        public static String StatusCompleted = "completed";
        public static String StatusReady = "ready";

        public static String DBFIELD_NUMBER = "NUMBER";
        public static String DBFIELD_VARCHAR = "VARCHAR2";
        public static String DBFIELD_TIMESTAMP = "TIMESTAMP";
        public static String DBFIELD_DATE = "DATE";

        public static String[] varsPatterns = Settings.getIniSetting(
                        TagFIELDS_FIXED_LENGTH,
                        DefaultFIELDS_FIXED_LENGTH).split(";");
        public static String tableSearchAll = Settings.getIniSetting(
                        TagTABLES_PATTERN,
                        DefaultTABLES_PATTERN); // "%";
}