package com.java.core.data;

import java.sql.Connection;

public class DBConnection {
    public Connection connection;
    public String connectionName;

    public DBConnection(Connection conn, String connName) {
        connection = conn;
        connectionName = connName;
    }
}
