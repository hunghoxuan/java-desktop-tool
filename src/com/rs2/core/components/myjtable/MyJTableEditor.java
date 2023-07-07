package com.rs2.core.components.myjtable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.data.DBQuery;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.core.utils.Utils;
import com.rs2.modules.db.DBService;

@SuppressWarnings("serial")
public class MyJTableEditor extends JPanel implements ActionListener, TableModelListener, TreeCellRenderer {
	private final MyJTable table;
	private JButton editButton, addButton, removeButton, cloneButton, exportButton, clearButton, commitButton,
			resetButton;
	private JTextField filterText;
	private JComboBox<String> filterColumn;
	public String[] columns;
	public String[] primaryKeys;
	public Object[][] originalData;

	public Set<Object[]> deletedRows = new HashSet<Object[]>();
	public Set<Object[]> addedRows = new HashSet<Object[]>();
	public Set<Object[]> updatedRows = new HashSet<Object[]>();

	public String outputFolder;
	private String fileName;
	private String fileExtension;
	private String tableName;
	private String sql;
	private DBQuery dbQuery;

	public MyJTableEditor() {
		this(new MyJTableModel(null, null));
	}

	public MyJTableEditor(String[] columns) {
		this(new MyJTableModel(null, columns));
		this.columns = columns;
	}

	public MyJTableEditor(Object[][] data, String[] columns) {
		this(new MyJTable(data, columns));
		this.originalData = data;
		this.columns = columns;
	}

	public MyJTableEditor(Object[][] data) {
		this(new MyJTableModel(data, null));
		this.originalData = data;
	}

	public MyJTableEditor(DBQuery query) {
		this(new MyJTable());
		setDBQuery(query);
	}

	public MyJTableEditor(MyJTableModel model) {
		this(new MyJTable(model));
	}

	public MyJTableEditor(MyJTable table) {

		setLayout(new GridBagLayout());
		this.table = table;

		table.getModel().addTableModelListener(this);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
				int viewRow = table.getSelectedRow();
				if (viewRow < 0) {

				} else {
					updateButtons();

				}
			}
		});
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (event.getValueIsAdjusting()) {
					return;
				}
			}
		});

		// clear the selection, when clicking on an empty area of the table;
		// see here: http://stackoverflow.com/a/43443397
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		add(createButtons(), c);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), c);

		updateButtons();
	}

	public void setComponentSize(int width, int height) {
		table.setComponentSize(width, height);
	}

	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		table.setDefaultEditor(columnClass, editor);
	}

	public Object[][] getOriginalData() {
		return originalData;
	}

	private void updateButtons() {
		int[] selectedRows = table.getSelectedRows();
		exportButton.setVisible(table.getRowCount() > 0);
		resetButton.setVisible(table.getRowCount() > 0);
		editButton.setVisible(table.getRowCount() > 0);
		filterText.setVisible(table.getRowCount() > 0);
		filterColumn.setVisible(table.getRowCount() > 0);

		addButton.setVisible(getTable().getEditing());

		cloneButton.setEnabled(selectedRows.length > 0);
		cloneButton.setVisible(getTable().getEditing());

		removeButton.setEnabled(selectedRows.length > 0);
		removeButton.setVisible(getTable().getEditing());

		clearButton.setEnabled(table.getRowCount() > 0);
		clearButton.setVisible(getTable().getEditing());

		// commitButton.setVisible(getTable().getEditing() && this.tableName != null &&
		// this.tableName != "");
		commitButton.setVisible(getTable().getEditing() && this.tableName != null && this.tableName != "");

		// exportButton.setVisible(getTable().getEditing());
		// resetButton.setVisible(getTable().getEditing());
		editButton.setText(table.getEditing() ? "..." : "Edit");
	}

	public Component createButtons() {
		JPanel buttonPanel = Utils.createJPanelHorizental();
		clearButton = createButton("Clear");
		clearButton.addActionListener(e -> {
			int rowsCount = table.getRowCount();
			for (int i = rowsCount - 1; i >= 0; i--) {
				getModel().removeRow(i);
			}
		});

		cloneButton = createButton("Clone");
		cloneButton.addActionListener(e -> {
			getTable().cloneSelectedRows();
		});

		editButton = createButton("Edit");
		editButton.addActionListener(e -> {
			getTable().setEditing(!getTable().getEditing());
			getTable().repaint();
			getTable().revalidate();
			updateButtons();
		});

		addButton = createButton("Add row");
		addButton.addActionListener(e -> {
			if (getTable().getSelectedRow() > 0 && getTable().getSelectedRow() < getTable().getRowCount() - 1)
				getModel().insertRow(getTable().getSelectedRow() + 1,
						this.getTable().getJTableModel().getDefaultRowData());
			else
				getModel().addRow(this.getTable().getJTableModel().getDefaultRowData());
		});

		removeButton = createButton("Remove row");
		removeButton.addActionListener(e -> {
			int[] selectrows = table.getSelectedRows();
			for (int i = selectrows.length - 1; i >= 0; i--) {
				getModel().removeRow(selectrows[i]);
			}
		});

		resetButton = createButton("Reset");
		resetButton.addActionListener(e -> {
			this.getTable().setData(this.getTable().getOriginalData(), this.getTable().getColumns());
			// editButton.doClick();
		});

		commitButton = createButton("Save DB");
		commitButton.addActionListener(e -> {
			String sql = MyDialog.showEdit(generateSQL("Changed"), "Preview SQL");
			if (sql != null) {
				int option = MyDialog.showDialog("Are you sure to execute this script ?", "Confirmation");
				if (option == JOptionPane.YES_OPTION) {
					try {
						DBService.executeSql(sql);
						MyDialog.showDialog("SQL executed successfully", "Success");
					} catch (Exception e1) {
						MyDialog.showDialog("Error executing SQL: " + e1.getMessage(), "Error");
					}
				}
				Utils.copyToClipboard(sql);
			}
		});

		exportButton = createButton("Save File");
		exportButton.addActionListener(e -> {
			JComboBox comboSettings = Utils.createComboBox(
					new String[] { "Excel", "SQL:Changed", "SQL:Insert", "SQL:Update", "SQL:Delete" },
					"Excel", "Export to");

			if (MyInputDialog.instance().showComponents("", "Export to", new Component[] { comboSettings }) != null) {
				String exportTo = (String) comboSettings.getSelectedItem();

				if (exportTo.contains("SQL")) {
					exportTo = exportTo.replace("SQL:", "");
					String sql = generateSQL(exportTo);
					if (sql.isEmpty()) {
						MyDialog.showDialog("No changes to save", "");
						return;
					}
					sql = MyDialog.showEdit(generateSQL(exportTo), "Preview SQL");
					if (sql != null) {
						fileName = Utils.selectFile("sql",
								outputFolder != null ? outputFolder : Settings.getSqlsOutputFolder());
						Utils.saveFile(fileName, sql);
					}
				} else if (exportTo.contains("Excel")) {
					fileName = Utils.selectFile("xlsx",
							outputFolder != null ? outputFolder : Settings.getExcelsFolder());
					Utils.writeArrayToExcel(getData(), columns, fileName);
				}

				fileName = null;
			}
			// if (getTable().getEditing())
			// editButton.doClick();
		});

		filterColumn = Utils.createComboBox(new String[] { "" }, "");
		filterText = Utils.createTextField("", "filterText");

		buttonPanel.add(editButton);
		buttonPanel.add(addButton);
		buttonPanel.add(cloneButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(commitButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(filterColumn);
		buttonPanel.add(filterText);

		table.setFilterText(filterText);
		table.setFilterColumn(filterColumn);

		return buttonPanel;
	}

	JButton createButton(String text) {
		JButton button = Utils.createButton(text);
		Dimension d = button.getPreferredSize();
		d.height = 25;
		button.setPreferredSize(d);
		button.setFont(Settings.FontNormal);
		button.setForeground(Settings.ColorText);
		button.setBackground(Settings.Color2);
		// button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		return button;
	}

	public void setData(Object[][] data, String[] columns) {
		this.columns = columns;
		this.originalData = data;
		this.getTable().setData(data, columns);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		exportButton.setText("Export");
		commitButton.setVisible(true);
	}

	public String getFileName() {
		return fileName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setSQL(String sql) {
		this.sql = sql;
	}

	public String getSQL() {
		return sql;
	}

	public void setDBQuery(DBQuery dbQuery) {
		this.dbQuery = dbQuery;
		setTableName(dbQuery.getSQLQueryLabel());
		setSQL(dbQuery.getSqlQuery());
		setData(dbQuery.getRowsArray(), dbQuery.getColumnsArray());
	}

	public DBQuery getDbQuery() {
		return dbQuery;
	}

	/**
	 * @return The model.
	 */
	public MyJTableModel getModel() {
		return (MyJTableModel) getTable().getModel();
	}

	/**
	 * @return The tree table.
	 */
	public MyJTable getTable() {
		return table;
	}

	public Object[][] getData() {
		return this.getTable().getData();
	}

	public Object[][] getOrignialData() {
		return this.getTable().originalData;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();

		TableModel model = (TableModel) e.getSource();
		if (column > -1) {
			String columnName = model.getColumnName(column);
			Object data = model.getValueAt(row, column);
			// LogManager.getLogger().log("row: " + row + "-" + e.getLastRow() + ", column:
			// " + column + ", " + columnName
			// + ", data: " + String.valueOf(data));
		} else {
			// LogManager.getLogger().log("row: " + row + "-" + e.getLastRow());
		}
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		final String v = (String) ((DefaultMutableTreeNode) value).getUserObject();
		table.setModel(new DefaultTableModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public int getRowCount() {
				return 2;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public Object getValueAt(int row, int column) {
				return v + ":" + row + ":" + column;
			}
		});
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		return this;
	}

	String generateSQL(String settings) {
		Object[][] data = this.getTable().getData();
		Object[][] dataRaw = this.getTable().getJTableModel().getData();
		String[] columns = this.getTable().getColumns();
		Object[][] originalDataRaw = this.getTable().getJTableModel().getOriginalData();
		Set<String> primaryColumns = dbQuery.getPrimaryColumns();

		StringBuilder sb = new StringBuilder();
		Set<String> ids = new HashSet<String>();

		for (int i = 0; i < data.length; i++) {
			String[] row = Utils.convertToStringArray(data[i]);
			if ((settings.equals("Changed") && (dataRaw[i][0] == "" || dataRaw[i][0] == null))
					|| settings.equals("Insert")) {
				sb.append(DBService.generateInsertSQL(tableName, columns, row)).append("\n");
				continue;
			}
			if ((settings.equals("Changed") && (dataRaw[i][0] != "" && dataRaw[i][0] != null))
					|| settings.equals("Update")) {
				Object[] originalRow = null;
				for (int j = 0; j < originalDataRaw.length; j++) {
					if (originalDataRaw[j][0].equals(dataRaw[i][0]))
						originalRow = Utils.removeIdColumnToStringArray(Utils.convertToStringArray(originalDataRaw[j]));
				}
				if (originalRow == null)
					continue;
				ids.add((String) dataRaw[i][0]);
				// check if really changed
				for (int k = 0; k < row.length; k++) {
					if (row[k] != originalRow[k]) {
						sb.append(DBService.generateUpdateSQL(tableName, columns, row, null, primaryColumns,
								Utils.removeIdColumnToStringArray(Utils.convertToStringArray(originalRow))))
								.append("\n");
						break;
					}
				}
			}
		}

		if (settings == "Delete")
			ids = new HashSet<String>();
		if (settings.equals("Changed") || settings.equals("Delete")) {
			for (int i = 0; i < originalDataRaw.length; i++) {
				if (!ids.contains(originalDataRaw[i][0]))
					sb.append(DBService.generateDeleteSQL(tableName, columns,
							Utils.removeIdColumnToStringArray(Utils.convertToStringArray(originalDataRaw[i])),
							primaryColumns)).append("\n");
			}
		}
		return sb.toString();
	}

}
