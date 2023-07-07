package com.rs2.modules.codegen;

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

import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyLinkedMap;
import com.rs2.core.data.DBColumn;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBSchema;
import com.rs2.core.data.DBTableExport;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.RS2Util;
import com.rs2.core.utils.Utils;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class CodeGenService extends MyService {

}// End of class!
