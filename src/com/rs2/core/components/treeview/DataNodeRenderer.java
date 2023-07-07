package com.rs2.core.components.treeview;

import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.*;

import com.rs2.core.components.myjtable.MyJTable;
import com.rs2.core.components.myjtable.MyJTableEditor;
import com.rs2.core.components.myjtable.MyJTableModel;
import com.rs2.core.data.DBQuery;
import com.rs2.core.settings.Settings;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.core.utils.Utils;

import java.awt.*;

public class DataNodeRenderer extends DefaultTreeCellRenderer {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	public static DataViewerService DataViewerService = new DataViewerService();

	public boolean isShowFlat = Settings.showFlatTree;
	JPanel panel;
	MyJTable table;
	JLabel label;
	int lastRowNumber;
	boolean lastIsExpanded = false;
	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	private int rowHeight = 0;

	public DataNodeRenderer() {
		panel = new JPanel(new GridLayout(0, 1));
		table = new MyJTable(); // Utils.createTable();
		// setClosedIcon(null);
		// setOpenIcon(null);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if (rowHeight > 0)
			d.height = rowHeight;
		return d;
	} // render Trees

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component returnValue = null;

		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			DefaultMutableTreeNode currTreeNode = (DefaultMutableTreeNode) value;
			DataNode firstLabelChild = null;
			DataNode currentNode = null;
			DBQuery currentQuery = null;
			Object treeObject = currTreeNode.getUserObject();

			if (!currTreeNode.isRoot() && currTreeNode.getChildCount() > 0 && treeObject instanceof String) {
				if (((DefaultMutableTreeNode) currTreeNode.getFirstChild())
						.getUserObject() instanceof DataNode) {
					firstLabelChild = (DataNode) ((DefaultMutableTreeNode) currTreeNode.getFirstChild())
							.getUserObject();
					currentQuery = firstLabelChild.dBQuery;
					currentNode = firstLabelChild.getParent();
				} else if (currTreeNode.getUserObject() instanceof DataNode) {
					currentNode = (DataNode) currTreeNode.getUserObject();
					currentQuery = currentNode.dBQuery;
				}
			}

			// if tree node is table node, not label node
			if (treeObject instanceof DataNode && !((DataNode) treeObject).isLabel
					&& ((DataNode) treeObject).columns != null) {
				DataNode line = (DataNode) treeObject;

				MyJTableModel tableModel;
				boolean resultSetColumnsOnly = true;

				if (line != null && line.getChildNumber() != 0 && !line.getViewOption().equals(Settings.viewStructure))
					tableModel = new MyJTableModel(new Object[][] { line.getValueArray(resultSetColumnsOnly) },
							line.getColumnArray(resultSetColumnsOnly));
				// names
				else {
					tableModel = new MyJTableModel(
							new Object[][] { line.getColumnArray(
									resultSetColumnsOnly), line.getValueArray(resultSetColumnsOnly) },
							line.getColumnArray(resultSetColumnsOnly));
				}
				// table.setEditingColumn(1);
				table.setModel(tableModel);
				table.setSize(new Dimension(2 + Utils.getMaxTableRowWith(table),
						table.getRowHeight(0) * table.getRowCount()));
				table.setRowHeight(25);
				// table.setFillsViewportHeight(true);
				table.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));

				// render by column
				for (int t = 0; t < table.getColumnCount(); t++) {
					table.getColumnModel().getColumn(t).setCellEditor(new DefaultCellEditor(Utils.createTextField()));

					table.getColumnModel().getColumn(t).setCellRenderer(new DefaultTableCellRenderer() {
						JLabel labl = new JLabel();

						public Component getTableCellRendererComponent(JTable table,
								Object value,
								boolean isSelected,
								boolean hasFocus,
								int row,
								int column) {

							if (row == 0 && table.getRowCount() > 1) {
								labl.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeSmall));// Column
																												// names
								labl.setForeground((Color) Color.GRAY);
								// labl.setBackground((Color) Color.GRAY);
								value = ((String) value).toLowerCase();
							} else {
								labl.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal)); //// Values
								labl.setForeground((Color) Color.DARK_GRAY);
							}

							// Column Color
							if (column == 0 || column == table.getColumnCount() - 1) {
								labl.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeSmall));// Column
																												// names
								labl.setForeground((Color) Color.GRAY);
								// labl.setBackground((Color) Color.GRAY);
							}

							if (column == 0 && row > 0) // index
							{
								labl.setText("" + row);
							} else {
								String cellValue = (String) table.getModel().getValueAt(row, column);
								// System.out.print(cellValue);
								String columnName = table.getModel().getColumnName(column);

								if (cellValue != null && cellValue.startsWith(Settings.errorPrefix)) {
									labl.setForeground((Color) Color.RED);
									cellValue = cellValue.substring(
											cellValue.indexOf(Settings.errorPrefix) + Settings.errorPrefix.length(),
											cellValue.length());
								}
								labl.setText(cellValue == null ? "" : (String) cellValue);
							}
							// if (column > 0)
							labl.setBorder(new EmptyBorder(3, 3, 3, 1)); // cell padding
							// labl.setBackground((Color) Color.GRAY);

							return labl;
						}
					});
				}

				Utils.sizeColumnsToFit2(table, 5, line);
				panel.setPreferredSize(
						new Dimension(
								(table.getColumnCount()) + line.getRowLength() * 2
										* panel.getFontMetrics(table.getFont())
												.charsWidth("ABCdefgxyz".toCharArray(), 0, 10)
										/ 20,
								table.getRowHeight(0) * table.getRowCount()));

				if (selected) {
					panel.setBorder(new MatteBorder(1, 1, 1, 1, Settings.ColorSelectedText));
				} else {
					panel.setBorder(null);
				}

				panel.setEnabled(tree.isEnabled());
				returnValue = panel;

				if (lastRowNumber == row && expanded && hasFocus && lastIsExpanded == false
						&& line.getChildren().size() == 1 && line.getFirstChild().isQueryOnDemand() == true) {
					tree.expandRow(row + 1);
				}

				// MyJTableEditor editor = new MyJTableEditor(table);
				panel.add(table);

			} else if (treeObject instanceof String && ((String) treeObject) != null) {
				rowHeight = 50;
				String line = (String) treeObject;

				if (currTreeNode.getParent() == null) {
					label = new JLabel(line.toUpperCase());
				} else if (currentQuery != null) {
					// customize tree label node (root node label)
					String condition;
					if (currentNode.isGroupbyNode)
						condition = "";
					else
						condition = this.isShowFlat ? "" : currentNode.getFilterConditionsAsString();
					label = new JLabel("["
							+ DataViewerService.getDisplayKeyValue(line.toUpperCase(), currentQuery.getTitle()) + "] "
							+ condition.toLowerCase() + " (" + DataNode.getRowsCountFromTreeNode(currTreeNode) + ")"); // show
																														// children
																														// number
																														// of
																														// labels
				} else {
					label = new JLabel(
							"[" + line + "] " + " (" + DataNode.getRowsCountFromTreeNode(currTreeNode) + ")");
				}

				// Tree label color
				boolean hasError = currentQuery != null && currentQuery.hasError();

				if (expanded) {
					label.setForeground(selected ? (hasError ? Color.RED : Settings.ColorSelectedText)
							: (hasError ? Color.RED : Settings.ColorLabelBG));
					label.setFont(new Font(Settings.FontName, Font.BOLD, Settings.FontSizeNormal));
				} else if (tree.isExpanded(row) && row != 0) {
					label.setForeground((hasError ? Color.RED : Color.BLACK));
					label.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));
				} else {
					label.setForeground((hasError ? Color.RED : Settings.ColorLabelBG));
					label.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));
				}

				label.setBorder(new EmptyBorder(3, 5, 5, 5));

				returnValue = label;
			}

		}
		if (returnValue == null) {
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);
		}
		if (hasFocus && !leaf) {
			lastIsExpanded = expanded;
			lastRowNumber = row;
		}
		return returnValue;
	}
}
