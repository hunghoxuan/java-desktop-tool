package com.rs2.core.components.property;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import com.rs2.core.components.property.types.PropertyNode;

@SuppressWarnings("serial")
public class PropertiesEditor extends JPanel {

	private final PropertiesTreeTable treeTable;

	private final PropertiesTreeTableModel treeTableModel;
	private JButton addButton;
	private JButton removeButton;
	private JButton cloneButton;

	public PropertiesTreeTable initPropertiesTreeTable(PropertiesEditorConfig config,
			PropertiesTreeTableModel treeModel) {
		return new PropertiesTreeTable(treeTableModel.getConfig(), treeTableModel);
	}

	public PropertiesEditor(PropertiesTreeTableModel treeTableModel) {

		setLayout(new GridBagLayout());
		this.treeTableModel = treeTableModel;
		treeTable = initPropertiesTreeTable(treeTableModel.getConfig(), treeTableModel);

		treeTable.addTreeSelectionListener(e -> updateButtons());

		// clear the selection, when clicking on an empty area of the table;
		// see here: http://stackoverflow.com/a/43443397
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TreePath path = treeTable.getPathForLocation(e.getX(), e.getY());
				if (path == null) {

					treeTable.clearSelection();

					ListSelectionModel selectionModel = treeTable.getSelectionModel();
					selectionModel.setAnchorSelectionIndex(-1);
					selectionModel.setLeadSelectionIndex(-1);

					TableColumnModel columnModel = treeTable.getColumnModel();
					columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
					columnModel.getSelectionModel().setLeadSelectionIndex(-1);

				}
			}
		});

		treeTable.expandAll();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		add(new JScrollPane(treeTable), c);

		c.fill = GridBagConstraints.FIRST_LINE_START;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx++;
		c.weightx = 0;
		add(createButtons(), c);

		updateButtons();
	}

	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		treeTable.setDefaultEditor(columnClass, editor);
	}

	private void updateButtons() {
		int[] selectedRows = treeTable.getSelectedRows();
		removeButton.setEnabled(selectedRows.length > 0);
	}

	public Component createButtons() {
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.gridy = 0;

		cloneButton = new JButton(PropertiesEditorConfig.buttonCloneTitle);
		cloneButton.addActionListener(e -> {
			if (treeTable.getSelectedRow() == -1) {
				PropertyNode newProperty = new PropertyNode((PropertyNode) treeTableModel.getRoot()
						.getChildAt(treeTableModel.getRoot().getChildCount() - 1));
				// at the bottom of the root element
				treeTableModel.insertNodeInto(newProperty, (PropertyNode) treeTableModel.getRoot(),
						treeTableModel.getRoot().getChildCount());
			} else {
				TreePath path = treeTable.getPathForRow(treeTable.getSelectedRow());
				PropertyNode item = (PropertyNode) path.getPathComponent(path.getPathCount() - 1);
				PropertyNode newProperty = new PropertyNode(item);
				// else, add it as ancestor below the current selection
				treeTableModel.insertNodeInto(newProperty, (PropertyNode) item.getParent(),
						item.getParent().getIndex(item) + 1);

			}

		});
		buttonPanel.add(cloneButton, c);
		c.gridy++;

		addButton = new JButton(PropertiesEditorConfig.buttonAddTitle);
		addButton.addActionListener(e -> {

			PropertyNode newProperty = new PropertyNode(null, treeTableModel.getConfig().getDefaultType());

			if (treeTable.getSelectedRow() == -1) {

				// at the bottom of the root element
				treeTableModel.insertNodeInto(newProperty, (PropertyNode) treeTableModel.getRoot(),
						treeTableModel.getRoot().getChildCount());

			} else {

				TreePath path = treeTable.getPathForRow(treeTable.getSelectedRow());
				PropertyNode item = (PropertyNode) path.getPathComponent(path.getPathCount() - 1);

				if (item.getAllowsChildren()) {
					// if the selected item allows children, add child
					treeTableModel.insertNodeInto(newProperty, item, 0);

				} else {
					// else, add it as ancestor below the current selection
					treeTableModel.insertNodeInto(newProperty, (PropertyNode) item.getParent(),
							item.getParent().getIndex(item) + 1);
				}

			}

		});
		buttonPanel.add(addButton, c);

		c.gridy++;
		removeButton = new JButton(PropertiesEditorConfig.buttonRemoveTitle);
		buttonPanel.add(removeButton, c);
		removeButton.addActionListener(e -> {
			int[] selectedRows = treeTable.getSelectedRows();
			if (selectedRows.length == 0)
				return;
			TreePath nextPath = null;
			int nextRow = selectedRows[selectedRows.length - 1];
			try {
				nextPath = treeTable.getPathForRow(selectedRows[selectedRows.length - 1] + 1);
			} catch (Exception ex) {
				nextPath = null;
			}

			for (int i = selectedRows.length - 1; i >= 0; i--) {
				TreePath path = treeTable.getPathForRow(selectedRows[i]);
				PropertyNode propertyToRemove = (PropertyNode) path.getPathComponent(path.getPathCount() - 1);
				treeTableModel.removeNodeFromParent(propertyToRemove);
			}
			try {
				if (nextPath != null)
					treeTable.setRowSelectionInterval(nextRow, nextRow);
			} catch (Exception ex) {

			}

		});
		return buttonPanel;
	}

	/**
	 * @return The model.
	 */
	public PropertiesTreeTableModel getTreeTableModel() {
		return treeTableModel;
	}

	/**
	 * @return The tree table.
	 */
	public PropertiesTreeTable getTreeTable() {
		return treeTable;
	}

	public List<Integer> readOnlyColumns() {
		return treeTable.readOnlyColumnIndexes;
	}

}
