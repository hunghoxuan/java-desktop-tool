package com.java.modules.codegen;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.java.core.components.MyDialog;
import com.java.core.components.MyLinkedMap;
import com.java.core.data.DBColumn;
import com.java.core.data.DBParam;
import com.java.core.data.DBQuery;
import com.java.core.data.DBSchema;
import com.java.core.data.DBTableExport;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;
import com.java.modules.db.DBService;
import com.java.core.utils.RS2Util;
import com.java.core.utils.Utils;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class CodeGenService extends MyService {

}// End of class!
