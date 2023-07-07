package com.java.modules.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import com.java.Main;
import com.java.core.components.MyDialog;
import com.java.core.components.MyInputDialog;
import com.java.core.data.DBParam;
import com.java.core.data.DBQuery;
import com.java.core.data.DBSchema;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.core.MainScreen;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;
import com.java.modules.dataviewer.DataViewerService;
import com.java.core.utils.Utils;

import java.sql.DatabaseMetaData;
import java.rmi.ConnectIOException;
import java.sql.CallableStatement;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class DBService extends MyService {
	private static final String[] TYPES = { "TABLE" }; // { "GLOBAL TEMPORARY", "TABLE", "LOCAL TEMPORARY" };
	public static String ServiceTitle = "Data 360";
	public static String FILES_FOLDER = "sqls";
	public static String FILES_EXTENSION = "sql";
	static DBPane mypane;

	public static List<String> getFiles(String lastFile) {
		return getFiles(FILES_FOLDER + "/create_data", FILES_EXTENSION, lastFile);
	}

	public static MyPane createPane() {
		if (mypane == null)
			mypane = new DBPane();
		return mypane;
	}

	public static JMenu createMenu() {
		MyPane mypane = createPane();
		JMenu jMenu = createMenu(ServiceTitle,
				FILES_FOLDER, FILES_EXTENSION, mypane);
		jMenu.add(new JSeparator());

		jMenu.add(Utils.createMenuItem("Search DB", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.mainScreen.addPanel(mypane, "Search DB");
				mypane.search();
			}
		}));
		jMenu.add(Utils.createMenuItem("Open file", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.mainScreen.addPanel(mypane, "Search DB");
				mypane.open();
			}
		}));
		return jMenu;
	}

	public static void run(String file) {
		DBPane pane = new DBPane();
		run(file, pane);
	}

	public static Connection getConnection(String connectionName, String password) {
		return getConnection(connectionName, password, true);
	}

	public static Connection getConnection(String connectionName, String password, boolean autoConnect) {
		if (connections == null)
			connections = new HashMap<String, Connection>();
		String dsn = "";

		try {
			if (Main.connectionName != null && Main.connectionName.equalsIgnoreCase(connectionName)
					&& Main.connection != null && !Main.connection.isClosed())
				return Main.connection; // to avoid connection closed issue.

			if (!autoConnect)
				return null;

			Map<String, String> connectionSettings = MyService.getConnectionDetails(connectionName);

			if (connectionSettings != null) {
				connectionName = connectionSettings.get(Settings.TagCONNECTIONNAME);
				String host = connectionSettings.get(Settings.TagHOST);
				String serviceName = connectionSettings.get(Settings.TagSERVICENAME);
				String port = connectionSettings.get(Settings.TagPORT);
				String userName = connectionSettings.get(Settings.TagUSERNAME);
				String connType = connectionSettings.get(Settings.TagCONNECTIONTYPE);
				String dbType = connectionSettings.get(Settings.TagDBTYPE);
				if (connectionSettings.get(Settings.TagPASSWORD) != null && (password == null || password.isEmpty())) {
					password = connectionSettings.get(Settings.TagPASSWORD);
				}
				Connection conn = getNewConnection(
						connectionName, dbType, serviceName, host, port, connType, userName, password,
						"");
				if (conn != null) {
					connections.put(connectionName, conn);
					Main.connection = conn;
					Main.connectionName = connectionName;
				}
				return conn;

			}
		} catch (SQLException ex) {
			MyDialog.showException(ex,
					"Could not connect to database: " + connectionName + " " + dsn + ". Please try again !");
		}
		return null;
	}

	public static Connection getNewConnection(String connectionName,
			String host, String serviceName,
			String user, String password, String port, String connMode)
			throws SQLException {
		return getNewConnection(connectionName, host, serviceName, user, password, port, connMode, "oracle");
	}

	public static Connection getNewConnection(String connectionName,
			String host, String serviceName,
			String user, String password, String port, String connMode, String dbType)
			throws SQLException {
		Connection conn = DBService.getNewConnection(
				connectionName, dbType, serviceName, host, port, connMode, user, password, "");
		return conn;
	}

	public static Connection getNewConnection(String connectionName,
			String dbType, String serviceName, String host, String port,
			String connType,
			String userName, String password, String execImmediateExpression) {
		return getNewConnection(
				connectionName, dbType, serviceName, host, port, connType, userName, password, execImmediateExpression,
				true);
	}

	public static void updateConnections(String host, String port, String serviceName, String dbType, String connType,
			String userName, String password) {
		MyService.connectionDetails = Settings.updateConnections(MyService.connectionDetails,
				"", host, // must use empty connection name
				port,
				serviceName, dbType, connType, userName, password);
	}

	public static void updateConnectionComponents(String connectionName, JTextField textHost, JTextField txtServiceName,
			JTextField portTextField,
			JTextField textUserName, JComboBox connTypeComboBox, JComboBox dbTypeComboBox,
			JPasswordField passwordField) {
		Map<String, String> settings = MyService.getConnectionDetails(connectionName);

		if (settings != null) {
			textHost.setText(settings.get(Settings.TagHOST));
			txtServiceName.setText(settings.get(Settings.TagSERVICENAME));
			portTextField.setText(settings.get(Settings.TagPORT));
			textUserName.setText(settings.get(Settings.TagUSERNAME));
			connTypeComboBox.setSelectedItem(settings.get(Settings.TagCONNECTIONTYPE));
			dbTypeComboBox.setSelectedItem(settings.get(Settings.TagDBTYPE));
			passwordField.setText(settings.get(Settings.TagPASSWORD));
		}
	}

	public static String getDsn(String dbType, String serviceName, String host,
			String port,
			String connType,
			String userName, String password) {
		if (dbType.equalsIgnoreCase("oracle"))
			return "jdbc:oracle:thin:@(DESCRIPTION="
					+ "(ADDRESS_LIST=" + "(ADDRESS=(PROTOCOL=TCP)"
					+ "(HOST=" + host + ")" + "(PORT = " + port + ")" + ")" + ")"
					+ "(CONNECT_DATA=" + "(" + connType + "=" + serviceName + ")"
					+ "(SERVER=DEDICATED)" + ")" + ")";
		else if (dbType.equalsIgnoreCase("mysql"))
			return "jdbc:mysql://address=(protocol=tcp)(host=" + host + ")(port=" + port + ")/"
					+ serviceName + "?useSSL=false";
		return "";
	}

	public static Connection getNewConnection(String connectionName, String dbType, String serviceName, String host,
			String port,
			String connType,
			String userName, String password, String execImmediateExpression, boolean readOnly) {

		if (dbType == null || dbType.isEmpty())
			dbType = Settings.defaultDbType;
		if (port == null || port.isEmpty())
			port = Settings.defaultPort;
		if (connType == null || connType.isEmpty())
			connType = Settings.defaultConnType;

		if (password == null || password.isEmpty())
			password = Settings.getDefaultDbPassword();

		if (serviceName == null || serviceName.isEmpty())
			serviceName = Settings.defaultServiceName;

		boolean needLogin = false;
		while (!needLogin) {
			if (host == null || password == null || userName == null || host.isEmpty() || password.isEmpty()
					|| userName.isEmpty() || port == null || port.isEmpty() || serviceName == null
					|| serviceName.isEmpty()
					|| dbType == null || dbType.isEmpty() || connType == null || connType.isEmpty()) {
				JComboBox comboBoxStoredConns = Utils.createComboBox(MyService.getConnectionNames(),
						connectionName);

				comboBoxStoredConns.setName("Select connection");
				JTextField textHost = Utils.createTextField(host, "server");
				JTextField textService = Utils.createTextField(serviceName, "service");
				JTextField textPort = Utils.createTextField(port, "port");
				JPasswordField textPassword = Utils.createPasswordField(password, "password");
				JTextField textUsername = Utils.createTextField(userName, "username");
				JComboBox dbTypeComboBox = Utils.createComboBox();
				dbTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Oracle", "MySQL" }));
				dbTypeComboBox.setName("dbType");

				JComboBox connTypeComboBox = Utils.createComboBox();
				connTypeComboBox.setName("Service");

				if (((String) (dbTypeComboBox.getSelectedItem())) != null) {
					if (((String) (dbTypeComboBox.getSelectedItem())).equals("Oracle"))
						connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Service_Name", "SID" }));
					if (((String) (dbTypeComboBox.getSelectedItem())).equals("MySQL"))
						connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Database" }));
				} else
					connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "" }));

				dbTypeComboBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						if (dbTypeComboBox.getSelectedItem() != null
								&& ((String) (dbTypeComboBox.getSelectedItem())).equals("Oracle")) {
							connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Service_Name", "SID" }));
						} else {
							connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Database" }));
						}
					}
				});

				comboBoxStoredConns.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent arg0) {
						if (Utils.isComboBoxChanged(comboBoxStoredConns)) {
							updateConnectionComponents(
									comboBoxStoredConns.getSelectedItem()
											.toString(),
									textHost,
									textService,
									textPort,
									textUsername,
									connTypeComboBox,
									dbTypeComboBox, textPassword);

						}
					}
				});
				Map<String, String> settings = MyInputDialog.instance().showMapInput(
						"Database server: " + connectionName
								+ " is not connected. Please connect.",
						"Connect to database server",
						new Component[] {
								comboBoxStoredConns, textHost, textPort, textService,
								dbTypeComboBox,
								connTypeComboBox,
								textUsername,
								textPassword });
				if (settings == null)
					return null;
				userName = textUsername.getText().trim();
				password = textPassword.getText().trim();
				host = textHost.getText().trim();
				serviceName = textService.getText().trim();
				dbType = dbTypeComboBox.getSelectedItem().toString().trim();
				connType = connTypeComboBox.getSelectedItem().toString().trim();

				port = textPort.getText().trim();
				connectionName = comboBoxStoredConns.getSelectedItem().toString();
				Settings.storeLastConnectionName(connectionName);

				needLogin = true;
			} else {
				break;
			}
		}

		Utils.showToast("Connecting to database... Please wait.");

		String thinConn = "";

		// System.out.println("execImmediateExpression "+execImmediateExpression);
		if (dbType.equals("Oracle")) {
			// DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			try {
				Class.forName("oracle.jdbc.OracleDriver");
			} catch (ClassNotFoundException a) {
				MyDialog.showException(a, "Oracle JDBC Driver not found");
				return null;
			}
			// Enumeration<Driver> theDriver = DriverManager.getDrivers();
			// while (theDriver.hasMoreElements()) {
			// + theDriver.nextElement().getClass().getName() + "\n");
			// }
			try {
				thinConn = getDsn(dbType, serviceName, host, port, connType, userName, password);

				Connection conn = DriverManager.getConnection(thinConn, userName, password);
				if (needLogin)
					MyService.connectionDetails = Settings.updateConnections(MyService.connectionDetails,
							"", host, // must use empty connection name
							port,
							serviceName, dbType, connType, userName, password);

				conn.setAutoCommit(false);
				conn.setReadOnly(readOnly);
				Statement st = conn.createStatement();// Die national language setting der neuen session
														// werden an die settings der datenbank angepasst
				ResultSet rs = st.executeQuery(
						"SELECT 'alter session set '||parameter ||'='''||value||'''' as nls_settings from NLS_DATABASE_PARAMETERS where parameter in (SELECT parameter from NLS_session_PARAMETERS)");
				Statement st2 = conn.createStatement();
				String alterSessionQuery = "";
				while (rs.next()) {
					alterSessionQuery = rs.getString("NLS_SETTINGS");
					// System.out.println(alterSessionQuery);
					st2.executeQuery(alterSessionQuery);
				}
				st.close();
				rs.close();
				st2.close();
				logger.debug("Connected sucessfully to " + conn.getMetaData().getDatabaseProductName()
						+ " server: " + thinConn);

				executeCallableStatement(execImmediateExpression, conn);

				return conn;
			} catch (SQLException sq) {
				MyDialog.showException(sq, "Error while connecting to " + dbType + " DB server! " + thinConn);
				return null;
			} catch (Exception e) {
				MyDialog.showException(e, "Error while connecting to " + dbType + " DB server! " + thinConn);
				return null;
			}
		} else if (dbType.equals("MySQL")) {

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException a) {
				MyDialog.showException(a, "MySQL JDBC Driver not found");
			}

			try {
				thinConn = getDsn(dbType, serviceName, host, port, connType, userName, password);

				Connection conn = DriverManager.getConnection(thinConn, userName, password);
				conn.setAutoCommit(false);
				logger.debug("Connected sucessfully to " + conn.getMetaData().getDatabaseProductName()
						+ " server: " + thinConn);
				return conn;
			} catch (SQLException sq) {
				MyDialog.showException(sq, "Error while connecting to MySQL server! " + thinConn);
				return null;
			} catch (Exception e) {
				MyDialog.showException(e, "Error while connecting to MySQL server! " + thinConn);
				return null;
			}

		}
		return null;
	}

	public static void dropAllTempTables() {
		dropAllTempTables(null);
	}

	public static void dropAllTempTables(LinkedList<DBQuery> allPlans) {

		try {
			Set<String> tmpTables = getTempTables();
			if (tmpTables.size() > 0) {
				logger.debug("\n[Dropping temp tables]");
				Statement stmnt = Main.connection.createStatement();
				for (String tempTableName : tmpTables) {
					dropTempTable(stmnt, tempTableName, allPlans);
				}
			}

			Main.dbTempItems.clear();

		} catch (SQLException sq) {
			MyDialog.showException(sq,
					"Failed deleting temporary table " + Main.dbTempItems);
		}
	}

	public static void dropTempTable(Statement stmnt, String tempTable) {
		dropTempTable(stmnt, tempTable, null);
	}

	public static void dropTempTable(Statement stmnt, String tempTable, LinkedList<DBQuery> allPlans) {
		if (tempTable.length() == 0 || !tempTable.startsWith(Settings.tempTablePrefix))
			return;
		try {
			stmnt.execute("drop table " + tempTable);
			logger.debug("droped temp table " + tempTable + "");
			if (allPlans != null) {
				for (DBQuery queryDefinition : allPlans) {
					if (queryDefinition.getTempTable() == tempTable)
						queryDefinition.setTempTable(null);
				}
			}
		} catch (SQLException sq) {
			MyDialog.showException(sq, "Can not drop temp table: " + tempTable + "");
		}
	}

	public static Set<String> getTempTables(Connection conn) {
		return getTables(conn, Settings.tempTablePrefix + "%", null, null, true);
	}

	public static Set<String> getTempTables() {
		return getTempTables(Main.connection);
	}

	public static Set<String> getColumns(String tableName) {
		return getColumns(tableName, "%");
	}

	public static Set<String> getColumns(String tableName, String columnPattern) {
		return getColumns(Main.connection, tableName, columnPattern);
	}

	public static Set<String> getColumns(Connection conn, String tableName, String columnPattern) {
		return getColumns(conn, tableName, columnPattern, null);
	}

	public static Set<String> getColumns(Connection conn, String tableName, String columnPattern,
			String connectionName) {
		if (conn == null) {
			return new LinkedHashSet<String>();
		}
		DBSchema schema = DBSchema.getFromCache(connectionName, Settings.tableSearchAll);
		if (schema != null)
			return getColumns(schema, tableName, columnPattern);
		Set<String> columns = new LinkedHashSet<String>();
		String[] patterns = columnPattern.split(",");
		for (String pattern : patterns) {
			pattern = pattern.toUpperCase().trim();
			if (pattern.isEmpty())
				pattern = "%";
			try {
				DatabaseMetaData db = conn.getMetaData();
				ResultSet rs = db.getColumns(null, db.getUserName(), tableName, pattern);
				columns = getResultSetDataByColumn(rs, "COLUMN_NAME");
			} catch (SQLException ex) {
				MyDialog.showException(ex, "getTableColumns failed " + tableName + " : " + columnPattern);
			}
		}
		return columns;
	}

	public static Set<String> getPrimaryColumns(Connection conn, String tableName) {
		Set<String> columns = new LinkedHashSet<String>();
		if (conn == null) {
			return columns;
		}
		try {
			DatabaseMetaData db = conn.getMetaData();
			ResultSet rs = db.getPrimaryKeys(null, db.getUserName(), tableName);
			columns = getResultSetDataByColumn(rs, "COLUMN_NAME");
		} catch (SQLException ex) {
			MyDialog.showException(ex, "getTableColumns failed " + tableName);
		}
		return columns;
	}

	public static Set<String> getResultSetDataByColumn(ResultSet rs, String columnName) {
		Set<String> columns = new LinkedHashSet<String>();
		try {
			while (rs.next()) {
				String colName = rs.getString(columnName);
				columns.add(colName);
			}
			rs.close();

		} catch (SQLException ex) {
			LogManager.getLogger().error(ex);
		}
		return columns;
	}

	public static Set<String> getColumns(DBSchema schema, String tableName, String columnPattern) {
		if (schema == null)
			return null;
		Set<String> columns = new LinkedHashSet<String>();
		String[] patterns = columnPattern.split(",");

		if (schema != null) {

			List<String> cols = schema.getColumnNames(tableName);
			if (!columnPattern.isEmpty()) {
				for (String col : cols) {
					for (String pattern : patterns) {
						if ((pattern.contains("%") && col.contains(pattern.toUpperCase().replace("%", "")))
								|| (!pattern.contains("%") && col.equalsIgnoreCase(pattern)))
							columns.add(col);
					}
				}
			} else {
				columns = new LinkedHashSet<String>(cols);
			}

		}
		return columns;
	}

	public static Set<String> getTables(String tableNamePattern) {
		return getTables(Main.connection, tableNamePattern);
	}

	public static Set<String> getTables(String tableNamePattern, String connectionName) {
		return getTables(Main.connection, tableNamePattern, connectionName);
	}

	public static Set<String> getTables(Connection conn, String tableNamePattern, String connectionName) {
		if (conn == null) {
			return new TreeSet<String>();
		}
		DBSchema schema = DBSchema.getFromCache(connectionName, Settings.tableSearchAll);

		// DBSchema schema = DBUtils.getDBSchema(conn, connectionName,
		// Settings.tableSearchAll);
		Set<String> tables = new TreeSet<String>();
		String[] patterns = tableNamePattern.split(",");

		if (schema != null) {
			for (String tableName : schema.getTableList().keySet()) {
				for (String pattern : patterns) {
					if (tableName.contains(pattern.replaceAll("%", "")))
						tables.add(tableName);
				}
			}
		}
		if (tables.size() > 0)
			return tables;
		return getTables(conn, tableNamePattern);
	}

	public static Set<String> getTables(Connection conn, String tableNamePattern) {
		return getTables(conn, tableNamePattern, null, "", true);
	}

	public static Set<String> getTables(String tableNamePattern, String schema, String catalog, boolean isQuoted) {
		return getTables(Main.connection, tableNamePattern, schema, catalog, isQuoted);
	}

	public static Set<String> getTables(Connection conn, String tableNamePattern, String schema, String catalog,
			boolean isQuoted) {
		if (conn == null)
			return new TreeSet<String>();

		Set<String> tables = new TreeSet<String>();
		if (conn == null)
			return tables;

		String[] patterns = tableNamePattern.split(",");
		for (String pattern : patterns) {
			pattern = pattern.toUpperCase().trim();
			if (pattern.isEmpty())
				pattern = Settings.tableSearchAll;
			try {
				ResultSet rs = null;
				try {
					DatabaseMetaData meta = conn.getMetaData();
					if ((isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
						rs = meta.getTables(catalog, schema, pattern, TYPES);
					} else if ((isQuoted && meta.storesUpperCaseQuotedIdentifiers())
							|| (!isQuoted && meta.storesUpperCaseIdentifiers())) {
						rs = meta.getTables(
								catalog.toUpperCase(),
								schema.toUpperCase(),
								pattern.toUpperCase(),
								TYPES);
					} else if ((isQuoted && meta.storesLowerCaseQuotedIdentifiers())
							|| (!isQuoted && meta.storesLowerCaseIdentifiers())) {
						rs = meta.getTables(
								catalog.toLowerCase(),
								schema.toLowerCase(),
								pattern.toLowerCase(),
								TYPES);
					} else {
						rs = meta.getTables(catalog, schema, pattern, TYPES);
					}

					while (rs.next()) {
						String tableName = rs.getString("TABLE_NAME");
						tables.add(tableName);
						// System.out.println("table = " + tableName);
					}
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				} finally {
					if (rs != null)
						rs.close();
				}
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
		return tables;
	}

	public static void executeSQLBlock(String connectionName, String sql) {
		String file = Settings.getModulesFolder() + "\\sqls\\" + "____tmp.sql";
		Utils.saveFile(file, sql);
		executeSQLFile(connectionName, file);
	}

	public static void executeSQLFile(String connectionName, String file) {
		try {
			String line;
			Map<String, String> settings = getConnectionSettings(connectionName);
			String username = settings.get(Settings.TagUSERNAME);
			String password = settings.get(Settings.TagPASSWORD);

			String host = settings.get(Settings.TagHOST);
			String name = settings.get(Settings.TagDBDEFINITOIN);

			String sql = String.format("psql -U %s -d %s -h %s -f %s", username, "RS2SP_CR_1", host, file);

			Process p = Runtime.getRuntime().exec(sql);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (Exception err) {
			LogManager.getLogger().error(err);
		}
	}

	public static boolean isStatementSQL(String sql) {
		sql = sql.trim();
		if (sql.toUpperCase().startsWith("SELECT"))
			return false;
		if (sql.toUpperCase().contains("BEGIN")
				&& sql.toUpperCase().contains("END"))
			return true;
		if (sql.toUpperCase().startsWith("CREATE"))
			return true;
		if (sql.toUpperCase().startsWith("DROP"))
			return true;
		if (!sql.contains(";"))
			return true;
		return false;
	}

	public static String prepareStatementSQL(String sql) {
		if (sql.trim().toLowerCase().startsWith("exec "))
			sql = sql.substring(4);

		if (!sql.toUpperCase().contains("BEGIN") &&
				!sql.toUpperCase().contains("END")) {
			if (sql.trim().endsWith(";") == false)
				sql = sql + ";";
			sql = "BEGIN\n" + sql + "\nEND;";
		}
		// if (sql.endsWith(";"))
		// sql = sql.substring(0, sql.length() - 1);
		return sql;
	}

	public static int executeStatement(String sql, Connection conn) {
		if (conn == null) {
			logSQL(sql, "Connection is null. Please reconnect.");
			return -1;
		}
		if (sql == null || sql.isEmpty())
			return -1;
		try {
			sql = prepareStatementSQL(sql);
			logSQL(sql, "Executed Statement:");
			Statement st = conn.createStatement();
			int rs = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			return rs;
		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return -1;
		}
	}

	public static ResultSet executePreparedStatement(String sql) {
		return executePreparedStatement(sql, getConnection());
	}

	public static ResultSet executePreparedStatement(String sql, Connection conn) {
		if (conn == null) {
			logSQL(sql, "Connection is null. Please reconnect.");
			return null;
		}
		if (sql == null || sql.isEmpty())
			return null;
		try {
			sql = prepareStatementSQL(sql);
			logSQL(sql, "Executed Prepared Statement");
			PreparedStatement psProcToexecute = conn.prepareStatement(sql);
			ResultSet rs = psProcToexecute.executeQuery();
			psProcToexecute.close();
			return rs;
		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return null;
		}
	}

	public static boolean executeCallableStatement(String sql) {
		return executeCallableStatement(sql, getConnection());
	}

	public static boolean executeCallableStatement(String sql, Connection conn) {
		if (sql == null || sql.isEmpty())
			return false;
		try {
			sql = prepareStatementSQL(sql);
			logSQL(sql, "Executed Callable Statement");

			CallableStatement scriptExec = conn.prepareCall(sql);
			boolean result = scriptExec.execute();
			scriptExec.close();
			return result;
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
			return false;
		}
	}

	public static ResultSet executeQuery(String sql) {
		return executeQuery(sql, getConnection());
	}

	public static ResultSet executeQuery(String sql, Connection conn) {
		try {
			if (conn == null)
				conn = Main.connection;
			if (conn == null || conn.isClosed()) {
				MyDialog.showException("Connection is null or closed.");
				return null;
			}
			Statement st = conn.createStatement();// Die national language setting der neuen session

			// werden an die settings der datenbank angepasst
			ResultSet rs = executeQuery(st, sql); // st.executeQuery(sql);

			logSQL(sql, "Executed Query");
			// st.close();
			return rs;

		} catch (SQLException ex) {
			MyDialog.showException(ex, sql);
			return null;
		}
	}

	public static void log(String message) {
		LogManager.getLogger().debug(message);
	}

	public static void logSQL(String sql, String title) {
		LogManager.getLogger().debug(sql, title);
	}

	public static void logSQL(String sql) {
		LogManager.getLogger().debug(sql, "Executed SQL query:");
	}

	public static int executeSql(String sql) {
		return executeSql(sql, null, null);
	}

	public static int executeSql(String sql, Object[] params) {
		return executeSql(sql, params, null);
	}

	public static String cleanSql(String sql) {
		sql = sql.replace(Settings.tempSQLNULL, "");
		String[] tmps = sql.split("\n");
		for (int i = 0; i < tmps.length; i++) {
			String tmp = tmps[i].trim();
			if (tmp.contains("--")
					&& (!tmp.contains("|--|")) // ignore special cases
			) {
				tmp = tmp.substring(0, tmp.indexOf("--")).trim();
			}
			if (tmp.endsWith(";"))
				tmp = tmp.substring(0, tmp.length() - 1);
			tmps[i] = tmp;
		}
		sql = String.join("\n", tmps);
		sql = sql.trim();
		if (sql.endsWith(";"))
			sql = sql.substring(0, sql.length() - 1);

		return sql;
	}

	public static List<Object> executeStoredProcedure(String storedProcedure, String[] inputParams,
			int[] outputParamsIndex, Connection conn) {
		if (conn == null) {
			log("Connection is null. Please reconnect.");
			return null;
		}
		String command = "{ call " + storedProcedure + "(";
		for (String param : inputParams) {
			command += "?,";
		}
		for (int i : outputParamsIndex) {
			command += "?,";
		}
		command = command.substring(0, command.length() - 1) + ") }";

		List<Object> result = new LinkedList<Object>();
		try {
			CallableStatement cstmt = conn.prepareCall(command);
			int j = 0;
			for (String param : inputParams) {
				j += 1;
				cstmt.setString(j, param);
			}
			for (int i : outputParamsIndex) {
				cstmt.registerOutParameter(i, Types.NUMERIC);
			}
			cstmt.execute();
			for (int i : outputParamsIndex) {
				result.add(cstmt.getObject(i));
			}
			cstmt.close();
			LogManager.getLogger().debug(command + "; -- " + result.toString(), "Executed SQL query:");
		} catch (SQLException e) {
			LogManager.getLogger().error(e);
		}
		return result;
	}

	public static int executeSql(String sql, Connection conn) {
		return executeSql(sql, null, conn);
	}

	public static int executeSql(String sql, Object[] params, Connection conn) {
		int result = 0;

		// LogManager.getLogger().debug(sql + ";");
		try {
			if (conn == null)
				conn = Main.connection;
			if (conn == null || conn.isClosed()) {
				MyDialog.showException("Connection is null or closed");
				return -1;
			}

			sql = cleanSql(sql);

			if (params != null && params.length > 0) {
				PreparedStatement st = conn.prepareStatement(sql);// Die national language
				int i = 0;
				for (Object param : params) {
					i += 1;
					if (param instanceof String)
						st.setString(i, (String) param);
					else if (param instanceof Integer)
						st.setInt(i, (Integer) param);
					else if (param instanceof Boolean)
						st.setBoolean(i, (Boolean) param);
					else
						st.setString(i, (String) param);
				}
				logSQL(sql, "Executed SQL query:");
				result = st.executeUpdate();
				st.close();
			} else {
				Statement st = conn.createStatement();
				result = st.execute(sql) ? 1 : -1;
				st.close();
			}

			return 1;

		} catch (SQLException ex) {
			MyDialog.showException(ex, sql);
		}
		return result;

	}

	public static int getNumberofRows(String query) {
		return getNumberofRows(query, getConnection());
	}

	public static int getNumberofRows(String query, Connection conn) {
		if (conn == null) {
			log("Connection is null. Please reconnect.");
			return -1;
		}
		int numberOfRows = 0;
		query = cleanSql(query);

		try {
			PreparedStatement stmnt = conn.prepareStatement("Select count(*) from (" + query + ")");
			ResultSet rs = stmnt.executeQuery();
			while (rs.next())
				numberOfRows = rs.getInt(1);
			stmnt.close();
			rs.close();
		} catch (SQLException sq) {
			MyDialog.showException(sq, query);
			numberOfRows = -1;
		}
		return numberOfRows;
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

	public static String[][] convertAndFlipListArray(List<List<String>> list) {
		String[][] resultRowsMatrix;
		if (list == null || list.size() == 0)
			return null;
		resultRowsMatrix = new String[list.get(0).size()][list.size()];

		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).size(); j++)
				resultRowsMatrix[j][i] = list.get(i).get(j);
		}
		return resultRowsMatrix;
	}

	public static List<List<String>> executeSqlAsList(String sql) throws SQLException {
		return executeSqlAsList(sql, getConnection(), -1);
	}

	public static List<List<String>> sortHierarchy(List<List<String>> resultData, String parentColumn,
			String keyColumn) {
		return sortHierarchy(resultData, parentColumn, keyColumn, "");
	}

	public static List<List<String>> sortHierarchy(List<List<String>> resultData, String parentColumn,
			String keyColumn, String groupColumn) {
		if (parentColumn != null && keyColumn != null) {
			int parentColumnIndex = -1, keyColumnIndex = -1, groupColumnIndex = 0;
			List<String> allResultColumns = resultData.get(0);
			for (int i = 0; i < allResultColumns.size(); i++) {
				if (parentColumn != null && !parentColumn.isEmpty()
						&& allResultColumns.get(i).equalsIgnoreCase(parentColumn))
					parentColumnIndex = i;
				if (keyColumn != null && !keyColumn.isEmpty()
						&& allResultColumns.get(i).equalsIgnoreCase(keyColumn))
					keyColumnIndex = i;
				if (groupColumn != null && !groupColumn.isEmpty()
						&& allResultColumns.get(i).equalsIgnoreCase(groupColumn))
					groupColumnIndex = i;
			}
			if (parentColumnIndex > -1 && keyColumnIndex > -1) {
				return DBService.sortHierarchy(resultData, parentColumnIndex, keyColumnIndex, groupColumnIndex);
			}
		}
		return resultData;
	}

	private static List<List<String>> sortSubList(List<List<String>> subList, List<String> listKeyValues,
			List<List<String>> resultData, int parentColumnIndex,
			int keyColumnIndex, int groupColumnIndex, int columnCount, String parentKey, String parentTreeKey,
			String parentGroupNumber) {

		if (subList == null)
			subList = new LinkedList<List<String>>();

		for (int j = 0; j < resultData.size(); j++) {
			String key = resultData.get(j).get(keyColumnIndex) + "." + resultData.get(j)
					.get(parentColumnIndex);
			if (groupColumnIndex > -1)
				key += "." + resultData.get(j).get(groupColumnIndex);
			if (listKeyValues.contains(key))
				continue;
			if (resultData.get(j).get(parentColumnIndex)
					.equalsIgnoreCase(parentKey)
					&& (parentGroupNumber.isEmpty() || groupColumnIndex < 0
							|| (resultData.get(j).get(groupColumnIndex).equalsIgnoreCase(parentGroupNumber)))) {

				resultData.get(j).set(columnCount - 1,
						parentTreeKey + " > " + resultData.get(j).get(columnCount - 1));
				subList.add(resultData.get(j));
				listKeyValues.add(key);

				if (!resultData.get(j).get(keyColumnIndex)
						.equalsIgnoreCase(resultData.get(j).get(parentColumnIndex))) {
					parentKey = resultData.get(j).get(keyColumnIndex);
					parentTreeKey = resultData.get(j).get(columnCount - 1);
					List<List<String>> subListChild = sortSubList(null, listKeyValues, resultData, parentColumnIndex,
							keyColumnIndex, groupColumnIndex, columnCount, parentKey, parentTreeKey, parentGroupNumber);
					if (subListChild.size() > 0) {
						for (List<String> item : subListChild) {
							subList.add(item);
							String keyChild = item.get(keyColumnIndex) + "." + item.get(parentColumnIndex);
							if (groupColumnIndex > -1)
								keyChild += "." + item.get(groupColumnIndex);
							listKeyValues.add(keyChild);
						}
					}
				}
			}
		}

		return subList;
	}

	public static List<List<String>> sortHierarchy(List<List<String>> resultData, int parentColumnIndex,
			int keyColumnIndex, int groupColumnIndex) {
		int columnCount = resultData.get(0).size() + 1;
		List<List<String>> list = new LinkedList<List<String>>();
		List<List<String>> rootList = new LinkedList<List<String>>();
		List<List<String>> subList = new LinkedList<List<String>>();
		List<String> keyValues = new ArrayList<String>();
		List<String> listKeyValues = new ArrayList<String>();

		resultData.get(0).add(Settings.labelHierarchy);
		List<String> columnHeader = resultData.get(0);
		resultData.remove(0);
		Collections.sort(resultData, new Comparator<List<String>>() {
			@Override
			public int compare(List<String> o1, List<String> o2) {
				if (groupColumnIndex > -1) {
					return o1.get(groupColumnIndex).compareTo(o2.get(groupColumnIndex));
				}
				return o1.get(parentColumnIndex).compareTo(o2.get(parentColumnIndex));
			}
		});

		// index 0 is column name
		for (int i = 0; i < resultData.size(); i++) {
			resultData.get(i).add(resultData.get(i).get(keyColumnIndex));
			if (!keyValues.contains(resultData.get(i).get(keyColumnIndex)))
				keyValues.add(resultData.get(i).get(keyColumnIndex));
		}

		// find roots
		for (int i = 0; i < resultData.size(); i++) {
			if (!keyValues.contains(resultData.get(i).get(parentColumnIndex))) {
				String key = resultData.get(i).get(keyColumnIndex) + "." + resultData.get(i)
						.get(parentColumnIndex);
				if (groupColumnIndex > -1)
					key += "." + resultData.get(i).get(groupColumnIndex);
				if (listKeyValues.contains(key))
					continue;
				rootList.add(resultData.get(i));

				listKeyValues.add(key);
			}
		}

		String lastGroupColumnItem = "";
		int idx = -1;
		for (int i = 0; i < rootList.size(); i++) {
			String parentKey = rootList.get(i).get(keyColumnIndex);
			String parentTreeKey = rootList.get(i).get(columnCount - 1);
			String parentGroupNumber = rootList.get(i).get(groupColumnIndex);
			subList = sortSubList(subList, listKeyValues, resultData, parentColumnIndex, keyColumnIndex,
					groupColumnIndex, columnCount, parentKey, parentTreeKey, parentGroupNumber);

			if (subList.size() > 0
			// && (Utils.isNullOrEmptyValue(list.get(i).get(parentColumnIndex))
			// || !list.get(i).get(columnCount - 1).contains(" > "))

			) {
				System.out.println(" add SubList ");
				if (!lastGroupColumnItem.isEmpty()) { // resort sublist by GroupNumber
					Collections.sort(subList, new Comparator<List<String>>() {
						@Override
						public int compare(List<String> o1, List<String> o2) {
							String v1 = o1.get(o1.size() - 1);
							String v2 = o2.get(o2.size() - 1);
							int result = 0;
							if (v1.split(" > ").length > v2.split(" > ").length)
								result = 1;
							else if (v1.split(" > ").length < v2.split(" > ").length)
								result = -1;
							else
								result = 1 * o1.get(keyColumnIndex).compareTo(o2.get(keyColumnIndex));

							if (groupColumnIndex > -1) {
								if (o1.get(groupColumnIndex).equalsIgnoreCase(o2.get(groupColumnIndex))) {
									return result;
								}
								return o1.get(groupColumnIndex).compareTo(o2.get(groupColumnIndex));
							}
							return result;
						}
					});
				}

				idx += 1;
				list.add(idx, rootList.get(i));
				for (List<String> item : subList) {
					idx += 1;
					list.add(idx, item);
				}

				subList = new LinkedList<List<String>>();
			}

			if (groupColumnIndex > -1)
				lastGroupColumnItem = rootList.get(i).get(groupColumnIndex);

		}

		list.add(0, columnHeader);
		return list;
	}

	public static String[][] executeSqlAsArrayList(String sql, Connection conn, int maxRowNum) throws SQLException {
		return convertAndFlipListArray(executeSqlAsList(sql, conn, maxRowNum));
	}

	public static ResultSet executeQuery(Statement stmt, String sql) throws SQLException {
		sql = cleanSql(sql);
		logSQL(sql, "Executed Query:");

		ResultSet rs = stmt.executeQuery(sql);
		rs.setFetchDirection(ResultSet.FETCH_FORWARD);
		return rs;
	}

	public static int executeStatement(Statement stmt, String sql) throws SQLException {
		sql = cleanSql(sql);
		logSQL(sql, "Executed Statement:");

		int rs = stmt.executeUpdate(sql);
		return rs;
	}

	public static boolean executeSql(Statement stmt, String sql) throws SQLException {
		sql = cleanSql(sql);

		logSQL(sql, "Executed SQL:");
		return stmt.execute(sql);
	}

	public static List<List<String>> executeSqlAsStringCollection(String sql, Connection conn, int maxRowNum)
			throws SQLException {
		List<List<String>> list = new ArrayList<List<String>>();

		if (conn == null) {
			log("Connection is null. Please reconnect.");
			return list;
		}

		if (sql == null || sql.isEmpty() || conn == null) {
			// LogManager.getLogger().error("executeSqlAsList null sql.");
			return list;
		}
		sql = cleanSql(sql);

		PreparedStatement stmnt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmnt.executeQuery();

		list = executeSqlAsList(rs, maxRowNum);

		stmnt.close();
		rs.close();

		LogManager.getLogger().debug(sql + "; -- retrieved " + (list.size() - 1) + " rows.");
		return list;
	}

	public static List<Map<String, String>> executeSqlAsListMapRows(String sql, Connection conn, int maxRowNum)
			throws SQLException {
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();

		if (conn == null) {
			log("Connection is null. Please reconnect.");
			return list;
		}

		if (sql == null || sql.isEmpty() || conn == null) {
			// LogManager.getLogger().error("executeSqlAsList null sql.");
			return list;
		}
		sql = cleanSql(sql);
		logSQL(sql);

		PreparedStatement stmnt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmnt.executeQuery();

		list = executeSqlAsListMap(rs, maxRowNum);

		stmnt.close();
		rs.close();

		return list;
	}

	public static List<String> executeSqlAsLookupCollections(String sql, Connection conn, int maxRowNum)
			throws SQLException {
		List<String> list = new ArrayList<String>();

		if (conn == null) {
			log("Connection is null. Please reconnect.");
			return list;
		}

		if (sql == null || sql.isEmpty() || conn == null) {
			return list;
		}
		sql = cleanSql(sql);
		logSQL(sql);

		PreparedStatement stmnt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmnt.executeQuery();

		list = executeSqlAsLookupCollections(rs, maxRowNum);

		stmnt.close();
		rs.close();

		return list;
	}

	public static List<String> executeSqlAsLookupCollections(ResultSet rs, int maxRowNum) throws SQLException {
		List<String> list = new ArrayList<String>();
		if (maxRowNum == -1)
			maxRowNum = Settings.MaximumResultRows;

		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();

		// prepare rows
		int rowNum = 1, colNum = 0;
		while (rowNum <= maxRowNum && rs.next()) {
			list.add(DataViewerService.getDisplayKeyValue(rs.getString(1), rs.getString(2)));
			rowNum++;
		}
		return list;
	}

	public static List<List<String>> executeSqlAsList(String sql, Connection conn, int maxRowNum) throws SQLException {
		List<List<String>> list = new ArrayList<List<String>>();

		if (sql == null || sql.isEmpty() || conn == null) {
			return list;
		}
		sql = cleanSql(sql);
		logSQL(sql);

		PreparedStatement stmnt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmnt.executeQuery();

		list = executeSqlAsList(rs, maxRowNum);

		stmnt.close();
		rs.close();

		return list;
	}

	public static List<List<String>> executeSqlAsList(ResultSet rs, int maxRowNum) throws SQLException {
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> line;
		List<String> columns;
		if (maxRowNum == -1)
			maxRowNum = Settings.MaximumResultRows;

		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();
		// prepare columns
		columns = new LinkedList<String>();

		for (int t = 0; t < columnCount; t++) {
			columns.add(rsMeta.getColumnName(t + 1));
		}
		list.add(columns);

		// prepare rows
		int rowNum = 1, colNum = 0;
		while (rowNum <= maxRowNum && rs.next()) {
			String res = "";
			line = new LinkedList<String>();

			for (colNum = 0; colNum < columnCount; colNum++) {
				try {
					res = rs.getString(columns.get(colNum));
					if (res != null && res.length() > 0) {
						res.replaceAll("\\*", ".*").trim();
					} else
						res = Settings.nullValue;
					line.add(res);

				} catch (Exception e) {
					line.add(Settings.nullValue);
				}
			}
			list.add(line);
			rowNum++;
		}

		return list;

	}

	public static List<Map<String, String>> executeSqlAsListMap(ResultSet rs, int maxRowNum) throws SQLException {
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();
		Map<String, String> line;
		List<String> columns;
		if (maxRowNum == -1)
			maxRowNum = Settings.MaximumResultRows;

		ResultSetMetaData rsMeta = rs.getMetaData();
		int columnCount = rsMeta.getColumnCount();
		// prepare columns
		columns = new ArrayList<String>();

		for (int t = 0; t < columnCount; t++) {
			columns.add(rsMeta.getColumnName(t + 1));
		}

		// prepare rows
		int rowNum = 1, colNum = 0;
		while (rowNum <= maxRowNum && rs.next()) {
			String res = "";
			line = new LinkedHashMap<String, String>();

			for (colNum = 0; colNum < columnCount; colNum++) {
				try {
					res = rs.getString(columns.get(colNum));// get result set string for column name
					if (res != null && res.length() > 0) {
						res.replaceAll("\\*", ".*").trim();
					} else
						res = Settings.nullValue;
					line.put(columns.get(colNum), res);

				} catch (Exception e) {
					line.put(columns.get(colNum), Settings.nullValue);
				}
			}
			list.add(line);
			rowNum++;
		}

		return list;
	}

	public static String getDBFieldFromParamName(String param) {
		return param.replace(":", "");
	}

	public static String getParamNameFromDBField(String field) {
		return ":" + field;
	}

	public static List<DBParam> getQueryParams(List<DBParam> queryParams, String key, String value) {
		List<DBParam> result = new LinkedList<DBParam>();

		boolean checked = false;
		for (DBParam param : queryParams) {
			if (param.getKey().equalsIgnoreCase(key)) {
				param.setValue(value);
				checked = true;
			}
			result.add(param);
		}

		if (!checked)
			result.add(new DBParam(key, value));

		return result;
	}

	public static List<DBParam> addQueryParam(List<DBParam> queryParams, String key, String value) {
		if (queryParams == null)
			return null;
		if (key.equals(Settings.paramInstitution))
			value = Utils.getFixLengthValue(key, value);

		for (DBParam param : queryParams) {
			String key1 = param.getKey().replace(Settings.paramPrefix, "").replace(Settings.paramPrefix1, "");
			String key2 = key.replace(Settings.paramPrefix, "").replace(Settings.paramPrefix1, "");
			if (key1.equalsIgnoreCase(key2)) {
				param.setValue(value);
				return queryParams;
			}
		}

		queryParams.add(new DBParam(key, value));
		return queryParams;
	}

	public static String getQueryParamvalue(List<DBParam> queryParams, String key) {
		if (queryParams == null)
			return null;

		for (DBParam param : queryParams) {
			if (param.getKey().isEmpty())
				continue;
			String keyOriginal = param.getKey();
			if (keyOriginal.equalsIgnoreCase(key)) {
				String valueOriginal = param.getValue();
				return valueOriginal;
			}
		}
		return null;
	}

	public static String getMissingQueryParam(List<DBParam> queryParams) {
		if (queryParams != null && queryParams.size() > 0) {
			for (DBParam param : queryParams) {
				String key = param.getKey();
				String value = param.getValue();
				if (value == null || value.isEmpty()) {
					return key;
				}
			}
		}
		return "";
	}

	public static int isQueryHasParams(String query, List<DBParam> queryParams) {
		if (query == null || query.isEmpty())
			return -1;
		if (queryParams != null) {
			for (DBParam param : queryParams) {
				if (query.toLowerCase().contains(param.getKey().toLowerCase())) {
					if (param.getValue().isEmpty() || param.getValue().equals("%"))
						return 0; // sql has param but param is null
					else
						return 1; // sql has param and param has value --> can execute sql now
				}
			}
		} else {
			if (query.contains(Settings.paramPrefix))
				return 0;
		}

		return -1; // sql does not have any param -> must execute sql now
	}

	public static List<DBParam> inputQueryParams(List<DBParam> queryParams, Map<String, String> params) {
		return inputQueryParams(queryParams, params, null);
	}

	public static List<DBParam> inputQueryParams(List<DBParam> queryParams, Map<String, String> params,
			Map<String, List<String>> autoCompletes) {
		return inputQueryParams(queryParams, params, autoCompletes, null);
	}

	public static List<DBParam> inputQueryParams(List<DBParam> queryParams, Map<String, String> params,
			Map<String, List<String>> autoCompletes, Method method1) {
		JPanel myPanel = Utils.createJPanelInput(params, autoCompletes, method1);
		Map<String, String> tmpParams = MyInputDialog.instance().showMapInput(null,
				"Please enter params", myPanel);
		if (tmpParams == null)
			return null;
		params = tmpParams;
		if (queryParams != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				DBService.addQueryParam(queryParams, entry.getKey(), entry.getValue());
			}
		}

		return queryParams;
	}

	public static List<DBParam> inputQueryParams(List<DBParam> queryParams) {
		queryParams = MyService.initQueryParams(queryParams);
		return MyInputDialog.instance().showMapInputListDBParams(null,
				"Please enter params", queryParams);
	}

	public static Map<String, String> getMapStringFromListDBParam(List<DBParam> queryParams) {
		Map<String, String> tmpParams = new LinkedHashMap<String, String>();
		for (DBParam plan : queryParams) {
			tmpParams.put(plan.getKey(), plan.getValue());
		}
		return tmpParams;
	}

	public static List<DBParam> getQueryParamsFromSQLs(String sql, List<DBParam> queryParams,
			Map<String, String> params) {
		return getQueryParamsFromSQLs(sql, queryParams, params, true);
	}

	public static List<DBParam> getQueryParamsFromSQLs(String sql, List<DBParam> queryParams,
			Map<String, String> params, boolean showDialog) {
		List<String> paramNames = Utils.getParamsFromSql(sql);

		if (paramNames.size() > 0 && queryParams != null) {
			for (DBParam queryParam : queryParams) {
				if (paramNames.contains(queryParam.getKey()) && !queryParam.getValue().isEmpty()) {
					params.put(queryParam.getKey(), queryParam.getValue());
					// params.remove(queryParam.getKey());
				} else {
					if (!params.containsKey(queryParam.getKey()))
						params.put(queryParam.getKey(), "");
				}
			}
		}

		if (paramNames.size() > 0) {
			if (queryParams == null)
				queryParams = new LinkedList<DBParam>();

			params.keySet().removeIf(key -> !paramNames.contains(key));
			params.keySet().removeIf(key -> key.contains("\n"));
			params.keySet().removeIf(key -> key.contains(" "));

			for (String param : paramNames) {
				if (!params.keySet().contains(param))
					params.put(param, "");
			}

			if (showDialog)
				queryParams = DBService.inputQueryParams(queryParams, params);
		}
		return queryParams;
	}

	public static String applyParams(String query, List<DBParam> queryParams) {
		return applyParams(query, queryParams, false, true);
	}

	public static String applyParamsRawSQL(String query, List<DBParam> queryParams) {
		return applyParams(query, queryParams, false, false);
	}

	public static List<String> getMissingParams(String query, List<DBParam> queryParams) {
		List<String> paramNames = Utils.getParamsFromSql(query);
		List<String> missingParams = new LinkedList<String>();
		for (String param : paramNames) {
			boolean isMissing = true;
			for (DBParam queryParam : queryParams) {
				if (queryParam.getKey().equalsIgnoreCase(param) && !queryParam.getValue().isEmpty()) {
					isMissing = false;
					break;
				}
			}
			if (isMissing)
				missingParams.add(param);
		}
		return missingParams;
	}

	public static String applyParams(String query, List<DBParam> queryParams, boolean replaceOneByOne,
			boolean replaceOperator) {
		if (query == null || query.isEmpty())
			return "";

		query = query.replaceAll("  ", " ");

		if (queryParams != null) {
			for (DBParam param : queryParams) {
				if (param.getKey().isEmpty())
					continue;
				String keyOriginal = param.getKey();
				String valueOriginal = param.getValue();
				// if (valueOriginal == null || valueOriginal.isEmpty())
				// valueOriginal = "%";
				valueOriginal = valueOriginal.replaceAll(", ", ",").replaceAll(" ,", ",");

				String[] values = valueOriginal.split(",");
				for (int i = values.length; i >= 0; i--) {
					if (replaceOneByOne && i == 0)
						continue;
					// if params has multiple values :institution_number = 111,2002 --> in query can
					// specify :institution_number1, :institution_number2 or :institution_number
					String value = i == 0 ? valueOriginal : values[i - 1];
					String key = replaceOneByOne ? keyOriginal : (keyOriginal + (i == 0 ? "" : String.valueOf(i)));

					query = cleanupKeywords(query, " like ");
					query = cleanupKeywords(query, " in ");
					query = cleanupKeywords(query, key + " ");

					query = replaceIgnoreCase(query, " in  \\(", " in (");

					query = query
							.replaceAll("\\( ", "(")
							.replaceAll(" \\)", ")")
							.replaceAll("=  ", "=")
							.replaceAll("= ", "=");

					// normalize options of key ('&key' and ':key') to key
					if (key.startsWith(Settings.paramPrefix)) {
						query = query.replaceAll(key.replace(Settings.paramPrefix, Settings.paramPrefix1), key);
					} else if (key.startsWith(Settings.paramPrefix1)) {
						query = query.replaceAll(key.replace(Settings.paramPrefix1, Settings.paramPrefix), key);
					}

					// normalize ''&key''' => '&key'
					query = query.replaceAll("''" + key + "''", "'" + key + "'");

					// replaceOperator = false;
					if (replaceOperator) {
						// query = query.replaceAll("'" + key + "'", key);
						if (value.toLowerCase().contains("select ")
								|| value.toLowerCase().contains(" like ")
								|| value.toLowerCase().contains(">")
								|| value.toLowerCase().contains("=")
								|| value.toLowerCase().contains("(")
								|| value.toLowerCase().contains(" not ")) {
							if (replaceOneByOne) {
								query = query.replaceFirst("=" + key, value);
								query = query.replaceFirst(" in (" + key + ")", value);
							} else {
								query = query.replace("=" + key, value);
								query = query.replace(" in (" + key + ")", value);
							}

						} else if (value.contains("%")) {
							// =key, ='key', in (key), in ('key')
							String value1 = "'" + value.replaceAll(",", "','") + "'";
							if (replaceOneByOne) {
								query = query.replaceFirst("='" + key + "'", " like (" + value1 + ")");
								query = query.replaceFirst("=" + key, " like (" + value1 + ")");
								query = query.replaceFirst(" in ('" + key + "')", " like (" + value1 + ")");
								query = query.replaceFirst(" in (" + key + ")", " like (" + value1 + ")");
							} else {
								query = query.replace("='" + key + "'", " like (" + value1 + ")");
								query = query.replace("=" + key, " like (" + value1 + ")");
								query = query.replace(" in ('" + key + "')", " like (" + value1 + ")");
								query = query.replace(" in (" + key + ")", " like (" + value1 + ")");
							}

						} else {
							String value1 = "'" + value.replaceAll(",", "','") + "'";
							if (replaceOneByOne) {
								query = query.replaceFirst("='" + key + "'", " in (" + value1 + ")");
								query = query.replaceFirst("=" + key, " in (" + value1 + ")");
								query = query.replaceFirst(" in ('" + key + "')", " in (" + value1 + ")");
								query = query.replaceFirst(" in (" + key + ")", " in (" + value1 + ")");

							} else {
								query = query.replace("='" + key + "'", " in (" + value1 + ")");
								query = query.replace("=" + key, " in (" + value1 + ")");
								query = query.replace(" in ('" + key + "')", " in (" + value1 + ")");
								query = query.replace(" in (" + key + ")", " in (" + value1 + ")");

							}

						}
					}

					if (value.startsWith("'") && value.endsWith("'")) {
						value = value.substring(1, value.length() - 1);
					}

					if (replaceOneByOne) {
						query = query.replaceFirst(key, value);
					} else {
						query = query.replaceAll(key, value);
					}

					query = query.replaceAll("'',''", "','");
					query = query.replaceAll("''" + value + "''", "'" + value + "'");
				}
			}
		}

		return query;
	}

	public static String applyRequireFile(String content, String file) {
		String[] arr = content.split("\n");
		StringBuilder sb = new StringBuilder();
		String tagRequire = Settings.TagREQUIRE + ":";
		for (String line : arr) {
			if (line.startsWith(Settings.paramComment)
					&& line.toLowerCase().contains((tagRequire).toLowerCase())) {
				line = line.trim();
				sb.append(line);

				String fileRequired = line.substring(line.lastIndexOf(
						tagRequire) + tagRequire.length(), line.length())
						.trim();
				String fileTmp = Utils.getFileName(file, fileRequired);
				String content1 = Utils.getContentFromFile(fileTmp);
				if (content1 == null || content1.isEmpty()) {
					fileTmp = Settings.getSqlsFolder() + "\\functions\\" + fileRequired;
					content1 = Utils.getContentFromFile(fileTmp);
				}
				if (content1 != null && !content1.isEmpty()) {
					content1 = applyRequireFile(content1, fileTmp);

					if (content1.trim().toUpperCase().startsWith("CREATE")) {
						DBService.executeStatement(content1, getConnection());
					} else {

						if (!content1.trim().startsWith("/"))
							content1 = "/\n" + content1.trim();
						if (!content1.trim().endsWith("/"))
							content1 = content1.trim() + "\n/";
						sb.append("\n" + content1 + "\n");
					}
				}
			} else {
				sb.append(line);
			}
			sb.append("\n");
		}
		return sb.toString().trim();
	}

	public static String replaceIgnoreCase(String content, String search, String replaceBy) {
		return content.replace(search.toUpperCase(), replaceBy).replace(search.toLowerCase(), replaceBy);
	}

	public static String cleanupKeywords(String content, String search) {
		content = replaceIgnoreCase(content, search + " ", search);
		return content;
	}

	public static String cleanupKeywords(String content, String[] searchs) {
		for (int i = 0; i < searchs.length; i++) {
			content = cleanupKeywords(content, searchs[i]);
		}
		return content;
	}

	public static String generateLookupDefinitions() {
		Set<String> tables = DBService.getTables("BWT_%");
		StringBuilder builder = new StringBuilder();
		for (String table : tables) {
			Set<String> columns = DBService.getColumns(table);
			for (String column : columns) {
				if (!column.equalsIgnoreCase("index_field") && !column.equalsIgnoreCase(Settings.columnInstitution)
						&& !column.equalsIgnoreCase("description") && !column.equalsIgnoreCase("groups")
						&& !column.equalsIgnoreCase("language")) {
					builder.append("<Lookup table=\"" + table + "\" type=\"lookup\">\n"
							+ "\t select index_field," + column + "," + Settings.columnInstitution + " from " + table
							+ " where language(+) = 'USA' and " + Settings.columnInstitution + " = '"
							+ Settings.paramInstitution + "' order by 2,1 asc \n </Lookup>");
					break;
				}
			}

		}
		return builder.toString();
	}

	public static String getParamName(String column) {
		return Settings.paramPrefix + column + Settings.paramPostfix;
	}

	public static String replaceParamValueInSql(String sql, String sourceValue, String destValue,
			Map<String, Map<String, String>> replaceMapping) {
		return replaceParamValueInSql(sql, sourceValue, destValue, Settings.columnInstitution, false, replaceMapping);
	}

	public static String replaceParamValueInSql(String sql, String sourceValue, String destValue) {
		return replaceParamValueInSql(sql, sourceValue, destValue, Settings.columnInstitution, false);
	}

	public static String replaceParamValueInSql(String sql, String sourceValue, String destValue, String paramName,
			boolean isSourceInst) {
		return replaceParamValueInSql(sql, sourceValue, destValue, paramName, isSourceInst, null);
	}

	public static String replaceParamValueInSql(String sql, String sourceValue, String destValue, String paramName,
			boolean isSourceInst, Map<String, Map<String, String>> replaceMapping) {
		String[] parts = sql.toLowerCase().split(" where ");
		String tmp = parts.length > 1 ? parts[1] : sql;

		if (sql.contains("'" + sourceValue + "'")) {
			sql = sql.replaceAll("'" + sourceValue + "'", "'" + destValue + "'");
		} else if (tmp.toLowerCase().contains(paramName) && !tmp.toLowerCase().contains(getParamName(paramName))) {
			int startIdx = tmp.indexOf("'", tmp.toLowerCase().indexOf(paramName));
			int endIdex = startIdx < tmp.length() - 1 ? tmp.toLowerCase().indexOf("'", startIdx + 1) : -1;
			if (parts.length > 1) {
				startIdx = parts[0].length() + " where ".length() + startIdx;
				if (endIdex > -1)
					endIdex = parts[0].length() + " where ".length() + endIdex;
			}
			if (startIdx > -1 && endIdex > -1 && endIdex > startIdx && !isSourceInst) { // if specific inst is included
																						// in source inst whereClause
																						// then respect it
				sql = Utils.replaceBetween(sql, getParamName(paramName), startIdx + 1, endIdex);
			}
			sql = sql.replace(getParamName(paramName), destValue);
		} else if (sql.toLowerCase().contains(getParamName(paramName))) {
			sql = sql.replaceAll(getParamName(paramName), destValue);
		}

		if (replaceMapping != null) {
			for (String replCol : replaceMapping.keySet()) {
				for (Map.Entry<String, String> entry1 : replaceMapping.get(replCol).entrySet()) {
					String oldString = entry1.getKey(); // v_seq_num
					String newString = entry1.getValue(); // BW_CODE_LIBRARY.GETNEXTSEQNUMBER('015',1)
					sql = replaceParamValueInSql(sql, oldString, newString, replCol, isSourceInst, null);
				}
			}
		}
		return sql;
	}

	public static String getInstitutionNumberFromSql(String sql) {
		return getColumnValueFromSql(sql, Settings.columnInstitution);
	}

	public static String getTableNameFromSql(String sql) {
		String tmpTable = Utils.substringBetween(sql.toUpperCase(), "FROM ", " WHERE").trim();
		if (tmpTable.contains(" "))
			return tmpTable.split(" ")[0];
		return tmpTable;
	}

	public static String getColumnValueFromSql(String sql, String column) {
		sql = sql.trim()
				.replaceAll("= ", "=")
				.replaceAll(" =", "=")
				.replaceAll("  ", " ");
		if (!sql.toLowerCase().contains(column.toLowerCase()))
			return "";
		int startIdx = sql.toLowerCase().indexOf(column.toLowerCase()) + column.length();
		int endIdx = sql.toLowerCase().indexOf("and", startIdx);
		if (endIdx == -1)
			endIdx = sql.toLowerCase().indexOf("or", startIdx);
		if (endIdx == -1)
			endIdx = sql.length();

		String instSql = sql.substring(startIdx, endIdx);
		String value = "";
		instSql = instSql.toLowerCase()
				.replaceAll(" ", "")
				.replaceAll("in", "")
				.replaceAll("=", "")
				.replaceAll("'", "")
				.replaceAll(";", "")
				.replace("(", "")
				.replace(")", "")
				.replaceAll(column, "");
		if (Utils.isNumeric(instSql))
			value = instSql;
		return value;
	}

	public static DefaultMutableTreeNode createTreeNode(Object[] hierarchy) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
		DefaultMutableTreeNode child;
		for (int i = 1; i < hierarchy.length; i++) {
			Object nodeSpecifier = hierarchy[i];
			if (nodeSpecifier instanceof Object[]) // Ie node with children
				child = createTreeNode((Object[]) nodeSpecifier);
			else
				child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			node.add(child);
		}
		return (node);
	}

	public static DBQuery getDBQueryByName(List<DBQuery> list, String tablename) {
		for (DBQuery plan : list) {
			if (plan.getTableName().equalsIgnoreCase(tablename.trim()))
				return plan;
		}
		return null;
	}

	public static List<DBQuery> sortListDBQueryByName(List<DBQuery> dBQueryList) {
		Collections.sort(dBQueryList, new Comparator<DBQuery>() {
			public int compare(DBQuery p1, DBQuery p2) {
				if (!p1.hasSuperSQLQuery() && p2.hasSuperSQLQuery())
					return 1;
				return p1.getSQLQueryLabel().compareToIgnoreCase(p2.getSQLQueryLabel());
			}
		});

		return dBQueryList;
	}

	public static List<String> getFunctions(String type, String owner) {
		String sql = "SELECT DISTINCT NAME FROM dba_source WHERE type in ('" + type.replace(",", "','") + "') ";
		if (!owner.isEmpty())
			sql = sql + " AND owner = '" + owner + "' ";
		sql = sql + " order by name";

		List<String> list = new ArrayList<String>();
		ResultSet rs = executeQuery(sql);
		if (rs != null) {
			try {
				String function = "";
				while (rs.next()) {
					function = rs.getString("NAME");
					list.add(function);
				}
				rs.close();
			} catch (SQLException e) {
				LogManager.getLogger().error(e);
			}
		}
		return list;
	}

	public static String getFunctionContent(String functionName) {
		String sql = "SELECT TEXT FROM dba_source WHERE name in ('" + functionName + "') ";
		// if (!owner.isEmpty())
		// sql = sql + " AND owner = '" + owner + "' ";
		sql = sql + " order by name, line";

		StringBuilder sb = new StringBuilder();
		ResultSet rs = executeQuery(sql);
		if (rs != null) {
			try {
				while (rs.next()) {
					sb.append(rs.getString("TEXT"));
				}
				rs.close();
			} catch (SQLException e) {
				LogManager.getLogger().error(e);
			}
		}
		return sb.toString();
	}

	public static String generateInsertSQL(String tableName, String[] columns, String[] values) {
		return generateInsertSQL(tableName, columns, values, null, "");
	}

	public static String generateInsertSQL(String tableName, String[] columns, String[] values,
			Map<String, String> replaceValues, String newLine) {
		StringBuilder result = new StringBuilder("");
		String separator = ",";

		StringBuilder columnNames = new StringBuilder();
		StringBuilder cmd = new StringBuilder();
		String comment = "";
		for (int t = 0; t < values.length; t++) {
			String value = values[t];
			if (replaceValues != null && replaceValues.containsKey(columns[t]))
				value = replaceValues.get(columns[t]);
			if (Utils.isNullOrEmptyValue(value))
				value = "";
			value = value.replace(separator, ".");

			// comment = " -- " + columns[t];
			if (!value.isEmpty()) {
				columnNames.append(columns[t].replace(separator, ".") + separator);
				cmd.append("'" + value + "'");
				cmd.append(separator).append(comment).append(newLine);
			}
		}

		columnNames.deleteCharAt(columnNames.length() - separator.length());
		cmd.deleteCharAt(cmd.length() - separator.length() - comment.length());
		result.append("INSERT INTO " + tableName + " (").append(columnNames).append(") VALUES (").append(cmd.toString())
				.append(")");
		result.append(";");
		return result.toString();
	}

	public static String generateUpdateSQL(String tableName, String[] columns, String[] values) {
		return generateUpdateSQL(tableName, columns, values, null, null);
	}

	public static String generateUpdateSQL(String tableName, String[] columns, String[] values,
			Map<String, String> replaceValues, Set<String> primaryKeys) {
		return generateUpdateSQL(tableName, columns, values, replaceValues, primaryKeys, "", values);
	}

	public static String generateUpdateSQL(String tableName, String[] columns, String[] values,
			Map<String, String> replaceValues, Set<String> primaryKeys, String[] originalValues) {
		return generateUpdateSQL(tableName, columns, values, replaceValues, primaryKeys, "", originalValues);
	}

	public static String generateUpdateSQL(String tableName, String[] columns, String[] values,
			Map<String, String> replaceValues, Set<String> primaryKeys, String newLine, String[] originalValues) {
		StringBuilder result = new StringBuilder("");
		String separator = ",";
		Map<String, Integer> primaryKeysIndexes = new LinkedHashMap<String, Integer>();
		if (primaryKeys == null)
			primaryKeys = DBService.getPrimaryColumns(Main.connection, tableName);

		if (primaryKeys == null || primaryKeys.size() == 0)
			return "";
		for (String primaryKey : primaryKeys) {
			for (int t = 0; t < columns.length; t++) {
				if (columns[t].equalsIgnoreCase(primaryKey)) {
					primaryKeysIndexes.put(primaryKey, t);
					break;
				}
			}
		}

		result.append("UPDATE " + tableName + " SET ");
		for (int t = 0; t < values.length; t++) {
			String value = values[t];
			if (replaceValues != null && replaceValues.containsKey(columns[t]))
				value = replaceValues.get(columns[t]);
			if (Utils.isNullOrEmptyValue(value))
				value = "";
			value = value.replace(separator, ".");
			result.append(newLine).append(columns[t]).append(" = ").append("'" + value + "'").append(separator);
		}

		result.deleteCharAt(result.length() - separator.length()).append(newLine).append(" WHERE ");
		for (String primaryKey : primaryKeys) {
			if (primaryKeysIndexes.get(primaryKey) == null)
				continue;
			result.append(
					newLine).append(primaryKey).append(" = ")
					.append("'" + getKeyFromDisplayKeyValue(originalValues[primaryKeysIndexes.get(primaryKey)]) + "'")
					.append(" AND ");
		}
		result.delete(result.length() - " AND ".length(), result.length());
		result.append(";").append(newLine);

		return result.toString();
	}

	public static String generateDeleteSQL(String tableName, String[] columns, String[] values,
			Set<String> primaryKeys) {
		return generateDeleteSQL(tableName, columns, values, primaryKeys, "");
	}

	public static String generateDeleteSQL(String tableName, String[] columns, String[] values, Set<String> primaryKeys,
			String newLine) {
		StringBuilder result = new StringBuilder("");
		Map<String, Integer> primaryKeysIndexes = new LinkedHashMap<String, Integer>();

		for (String primaryKey : primaryKeys) {
			for (int t = 0; t < columns.length; t++) {
				if (columns[t].equalsIgnoreCase(primaryKey)) {
					primaryKeysIndexes.put(primaryKey, t);
					break;
				}
			}
		}

		result.append("DELETE " + tableName + " WHERE ");
		for (String primaryKey : primaryKeys) {
			if (primaryKeysIndexes.get(primaryKey) == null)
				continue;
			result.append(newLine).append(primaryKey).append(" = ")
					.append("'" + getKeyFromDisplayKeyValue(values[primaryKeysIndexes.get(primaryKey)]) + "'")
					.append(" AND ");
		}
		result.delete(result.length() - " AND ".length(), result.length());
		result.append(";");

		return result.toString();
	}

}
