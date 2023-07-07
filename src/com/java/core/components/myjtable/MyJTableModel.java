package com.java.core.components.myjtable;

import javax.swing.table.DefaultTableModel;
import com.java.core.utils.Utils;

public class MyJTableModel extends DefaultTableModel {
	String[] columns;
	Object[][] originalData;
	Object[] defaultRowData;

	public MyJTableModel(Object[][] data, String[] columns) {
		super(data, columns);
		this.columns = columns;
		this.originalData = data;
	}

	public String[] getColumns() {
		return columns;
	}

	public Object[][] getData() {
		return Utils.getTableData(this);
	}

	public Object[][] getOriginalData() {
		return originalData;
	}

	public Object[] getRowData(int row) {
		return Utils.getRowData(this, row);
	}

	public Object[][] getRowsData(int[] rows) {
		return Utils.getRowsData(this, rows);
	}

	public Object[][] getCellsData(int[] rows, int[] cols) {
		return Utils.getRowsData(this, rows, cols);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (columns[col].equals("#"))
			return false;
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
		// rowData[row][col] = value;
		fireTableCellUpdated(row, column);
	}

	public void setDefaultRowData(Object[] defaultRowData) {
		this.defaultRowData = defaultRowData;
	}

	public Object[] getDefaultRowData() {
		if (defaultRowData == null)
			defaultRowData = new Object[getColumnCount()];
		return defaultRowData;
	}
}
