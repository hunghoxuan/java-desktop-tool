package com.java.core.components.treeview;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.java.core.components.MyTree;
import com.java.core.data.DBSchema;
import com.java.core.data.DBTableExport;
import com.java.core.settings.Settings;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.java.Main;
import com.java.modules.db.DBService;

public class DBTreeModel implements TreeModel {
    public DBSchema dbSchema;
    public List<String> tableNames;
    public String tablesFilter, columnsFilter;
    public MyTree treeView;

    public DBTreeModel(Connection conn, String connectionName, String tablesFilter) {
        super();

        dbSchema = DBSchema.getDBSchema(conn, connectionName, tablesFilter);
        tableNames = new LinkedList<String>();
        tableNames.addAll(dbSchema.getTableNames());
        // this.tablesFilter = tablesFilter;
    }

    public DBTreeModel(DBSchema schema) {
        super();
        dbSchema = schema;
        if (dbSchema != null) {
            tableNames = new LinkedList<String>();
            tableNames.addAll(dbSchema.getTableNames());
        }
    }

    public DBTreeModel(DBSchema schema, MyTree tree) {
        this(schema);
        treeView = tree;
    }

    public DBTreeModel(DBSchema schema, List<String> tables, String filter) {
        super();
        dbSchema = schema;
        tableNames = tables;
        tablesFilter = filter;
    }

    public List<String> getColumnNames(String tableName) {
        if (dbSchema != null) {
            return dbSchema.getColumnNames(tableName);
        }
        return null;
    }

    public void setTablesFilter(String filter) {
        if (filter == null || filter.isEmpty())
            filter = Settings.tableSearchAll;
        if (dbSchema == null)
            return;
        tablesFilter = filter;
        refreshTree();
    }

    public void setColumnsFilter(String filter) {
        if (dbSchema == null)
            return;
        columnsFilter = filter;
        refreshTree();
    }

    public void refreshTree() {
        List<String> tables = dbSchema.getTableNames(tablesFilter, columnsFilter);

        // new LinkedList<String>();
        // if (tablesFilter == null || tablesFilter.isEmpty())
        // tablesFilter = Settings.operatorAll;
        // if (columnsFilter == null || columnsFilter.isEmpty())
        // columnsFilter = Settings.operatorAll;
        // boolean allMatch = columnsFilter.contains(Settings.operatorAND);
        // String[] tableFilters = tablesFilter.toUpperCase().split(",");
        // String[] colFilters = columnsFilter.toUpperCase().split(allMatch ?
        // Settings.operatorAND : Settings.operatorOR);
        // List<String> colFiltersList = new ArrayList<String>();
        // for (String column : colFilters) {
        // colFiltersList.add(column);
        // }

        // for (String table : dbSchema.getTableNames()) {
        // boolean found = false;
        // for (String tableFilter : tableFilters) {
        // if (found)
        // break;
        // tableFilter = tableFilter.replace(Settings.operatorAll, "");
        // if (tableFilter.isEmpty() || table.contains(tableFilter)) {
        // if (columnsFilter.equalsIgnoreCase(Settings.operatorAll)) {
        // found = true;
        // } else {
        // List<String> columns = getColumnNames(table);
        // found = allMatch ? columns.stream().allMatch(element ->
        // colFiltersList.contains(element))
        // : columns.stream().anyMatch(element -> colFiltersList.contains(element));

        // // for (String column : columns) {
        // // if (found)
        // // break;
        // // for (String columnFilter : colFilters) {
        // // columnFilter = columnFilter.replace("%", "");
        // // if (columnFilter.isEmpty() || column.contains(columnFilter)) {
        // // found = true;
        // // break;
        // // }
        // // }
        // // }
        // }

        // if (found)
        // tables.add(table);
        // }
        // }
        // }

        tableNames = tables;
        if (treeView != null) {
            treeView.setModel(null);
            DBTreeModel model = new DBTreeModel(dbSchema, tableNames, tablesFilter);
            model.treeView = treeView;
            treeView.setModel(model);
            // treeView.revalidate();
        }
    }

    public Object getRoot() {
        if (dbSchema != null)
            return dbSchema.getConnectionName();
        return "<>";
    }

    public Object getChild(Object parent, int index) {
        if (isRoot(parent)) {
            if (tableNames != null)
                return tableNames.get(index);
        }
        List<String> columnNames = getColumnNames((String) parent);
        if (columnNames != null && index < columnNames.size())
            return columnNames.get(index);
        return "";
    }

    public boolean isTable(Object node) {
        if (tableNames != null)
            return tableNames.contains((String) node);
        return false;
    }

    public boolean isRoot(Object parent) {
        return ((String) parent).equalsIgnoreCase((String) getRoot());
    }

    public boolean isLeaf(Object node) {
        if (isRoot(node)) {
            return false;
        }
        if (tableNames != null)
            return !tableNames.contains((String) node);
        return false;
    }

    public int getChildCount(Object parent) {
        if (isRoot(parent) && tableNames != null) {
            return tableNames.size();
        }
        List<String> columnNames = getColumnNames((String) parent);
        if (columnNames != null)
            return columnNames.size();
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (isRoot(parent) && tableNames != null) {
            return tableNames.indexOf(child) - 1;
        }

        List<String> columnNames = getColumnNames((String) parent);
        if (columnNames != null)
            return columnNames.indexOf(child) - 1;
        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // TODO Auto-generated method stub

    }
}
