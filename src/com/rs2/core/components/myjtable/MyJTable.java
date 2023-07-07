package com.rs2.core.components.myjtable;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import com.rs2.core.utils.Utils;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class MyJTable extends JTable {
	Object[][] originalData;
	Object[][] tempData;
	String[] columns;
	boolean isEditing = false;
	KeyAdapter keyListener;
	JTextField filterText;
	JComboBox filterColumn;
	TableRowSorter<MyJTableModel> sorter;

	private static final String LINE_BREAK = System.lineSeparator();
	private static final String CELL_BREAK = "\t";
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	public MyJTable(Object[][] data, String[] columns) {
		super();
		setData(data, columns);
	}

	public MyJTable(MyJTableModel model) {
		super(model);
		initUI();
	}

	public MyJTable() {
		super();
		initUI();
	}

	public void initUI() {
		if (keyListener == null) {
			keyListener = new MyKeyAdapter(this);
			addKeyListener(keyListener);
		}
		setRowHeight(25);
		// Set up column sizes.
		Utils.fitColumnSizes(this);
		getTableHeader().setReorderingAllowed(false);
		setFillsViewportHeight(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setAutoCreateRowSorter(true);
		// setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setCellSelectionEnabled(true);
		getActionMap().put("copy", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				copyToClipboard();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) { // to detect doble click events
					if (getSelectedColumn() == 0) {
						setRowSelectionInterval(getSelectedRow(), getSelectedRow());
						setColumnSelectionInterval(1, getColumnCount() - 1);
					} else if (!isEditing()) {
						String cellValue = getModel().getValueAt(getSelectedRow(), getSelectedColumn()).toString();
						StringSelection stringSelection = new StringSelection(cellValue);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
						Utils.showToast("[" + cellValue + "] copied to clipboard");
					}
				}

				if (me.getClickCount() == 1 && getSelectedColumn() == 0) {
					if (getSelectedRows().length > 0) {
						setRowSelectionInterval(getSelectedRows()[0], getSelectedRows()[getSelectedRows().length - 1]);
						setColumnSelectionInterval(1, getColumnCount() - 1);
					}
				}
			}
		});
	}

	public Object[][] getOriginalData() {
		return Utils.removeIdColumnFrom2DArray(originalData);
	}

	public void setEditing(boolean editing) {
		isEditing = editing;
	}

	public boolean getEditing() {
		return isEditing;
	}

	public void setData(Object[][] data, String[] columns) {
		originalData = Utils.addIdColumnTo2DArray(data); // add id Column
		this.columns = Utils.addIdColumnToStringArray(columns); // add id Column
		setModel(new MyJTableModel(Utils.addIdColumnTo2DArray(data), Utils.addIdColumnToStringArray(columns)));
		initUI();

		getColumnModel().getColumn(0).setCellRenderer(new ReadOnlyRenderer());
		for (int i = 1; i < columns.length; i++) {
			getColumnModel().getColumn(i + 1).setCellRenderer(new LookupRenderer(columns[i]));
		}

		Utils.addTableSorter(this, columns);

		if (filterText != null) {
			Utils.addTableFilter(this, filterText, null, columns);
		}

		if (filterColumn != null) {
			filterColumn.setModel(Utils.createComboBox(Utils.addIdColumnToStringArray(columns)).getModel());

			filterColumn.addItemListener(e -> {
				Utils.addTableFilter(this, filterText,
						filterColumn.getSelectedIndex() - 1, columns);
			});
		}

		this.repaint();
		this.revalidate();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0)
			return false;
		return isEditing;
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		if (toggle && !isRowSelected(rowIndex))
			return; // Don't do the selection
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	public void setFilterText(JTextField filterText) {
		this.filterText = filterText;
	}

	public void setFilterColumn(JComboBox filterColumn) {
		this.filterColumn = filterColumn;
	}

	public MyJTableModel getJTableModel() {
		return (MyJTableModel) getModel();
	}

	public Object[][] getData() {
		return Utils.removeIdColumnFrom2DArray(getJTableModel().getData());
	}

	public String[] getColumns() {
		return Utils.removeIdColumnToStringArray(this.columns);
	}

	public void setColumnEditor(String column, String[] dropdownValues) {
		setColumnEditor(getColumn(column), new JComboBox(dropdownValues));
	}

	public void setColumnEditor(String column, JComboBox comboBox) {
		setColumnEditor(getColumn(column), comboBox);
	}

	public void setColumnEditor(TableColumn column, JComboBox comboBox) {

		column.setCellEditor(new DefaultCellEditor(comboBox));

		// Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		column.setCellRenderer(renderer);
	}

	public List<Integer> readOnlyColumns() {
		return new LinkedList<Integer>();
	}

	public void setComponentSize(int width, int height) {
		setPreferredScrollableViewportSize(new Dimension(width, height));
		revalidate();
	}

	public Object getSelectedCellData() {
		return getValueAt(getSelectedRow(), getSelectedColumn());
	}

	public Object[] getSelectedRowData() {
		return ((MyJTableModel) this.getModel()).getRowData(getSelectedRow());
	}

	public Object[][] getSelectedRowsData() {
		return ((MyJTableModel) this.getModel()).getRowsData(getSelectedRows());
	}

	public Object[][] getSelectedCellsData() {
		return ((MyJTableModel) this.getModel()).getCellsData(getSelectedRows(), getSelectedColumns());
	}

	public void cloneSelectedRows() {
		addRows(getSelectedRowsData());
	}

	public void addRows(Object[][] selectrows) {
		for (int i = 0; i < selectrows.length; i++) {
			selectrows[i][0] = null;
			if (getSelectedRow() > 0 && getSelectedRow() < getRowCount() - 1)
				getJTableModel().insertRow(getSelectedRow() + 1 + i, selectrows[i]);
			else
				getJTableModel().addRow(selectrows[i]);
		}
	}

	@Override
	public int[] getSelectedRows() {
		int[] selectedRows = super.getSelectedRows();
		// sort selectedRows
		for (int i = 0; i < selectedRows.length; i++) {
			for (int j = i + 1; j < selectedRows.length; j++) {
				if (selectedRows[i] > selectedRows[j]) {
					int temp = selectedRows[i];
					selectedRows[i] = selectedRows[j];
					selectedRows[j] = temp;
				}
			}
		}
		return selectedRows;
	}

	public Object[][] getCopiedData() {
		return tempData;
	}

	public void setCopiedData(Object[][] data) {
		tempData = data;
	}

	public void pasteFromClipboard() {
		if (getCopiedData() != null) // Copy
			addRows(getCopiedData());
	}

	public void copyToClipboard() {
		int numCols = getColumnCount();
		int numRows = getRowCount();
		StringBuilder excelStr = new StringBuilder();
		int startColumn = 1;
		Object[][] selectedData = getSelectedCellsData(); // getSelectedRowsData();

		if (selectedData.length > 0) {
			numRows = selectedData.length;
			numCols = selectedData[0].length;
			startColumn = 0;
			setCopiedData(selectedData);
			for (int i = 0; i < numRows; i++) {
				for (int j = startColumn; j < numCols; j++) { // Skip ID column
					excelStr.append(escape(selectedData[i][j]));
					if (j < numCols - 1) {
						excelStr.append(CELL_BREAK);
					}
				}
				excelStr.append(LINE_BREAK);
			}
		} else {
			for (int i = 0; i < numRows; i++) {
				for (int j = startColumn; j < numCols; j++) { // Skip ID column
					excelStr.append(escape(getValueAt(i, j)));
					if (j < numCols - 1) {
						excelStr.append(CELL_BREAK);
					}
				}
				excelStr.append(LINE_BREAK);
			}
		}

		StringSelection sel = new StringSelection(excelStr.toString());
		CLIPBOARD.setContents(sel, sel);
		Utils.showToast(String.valueOf(numRows) + " rows copied to clipboard");
	}

	private String escape(Object cell) {
		return "" + (cell == null ? "" : cell.toString().replace(LINE_BREAK, " ").replace(CELL_BREAK, " "));
	}

}
