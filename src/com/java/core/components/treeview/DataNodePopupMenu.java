package com.java.core.components.treeview;

import java.util.List;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.tree.*;

import org.apache.poi.ss.usermodel.Sheet;

import com.java.core.components.MyDialog;
import com.java.core.components.MyTree;
import com.java.core.data.DBQuery;
import com.java.core.settings.Settings;
import com.java.modules.dataviewer.DataViewerService;
import com.java.core.utils.Utils;

public class DataNodePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	public DataViewerService DataViewerService = new DataViewerService();
	public final JPopupMenu popupTreeChoice;
	final MyTree tree;
	final TreePath selPath;
	final List<DBQuery> listAllQueryDefinitions;

	public DataNodePopupMenu(MyTree theTree, TreePath selPath, List<DBQuery> listAllQueryDefinitions) {
		MenuItemListener menuItemListener = new MenuItemListener();
		this.tree = theTree;
		this.selPath = selPath;
		this.listAllQueryDefinitions = listAllQueryDefinitions;
		DefaultMutableTreeNode currNode = (DefaultMutableTreeNode) (selPath.getLastPathComponent());
		popupTreeChoice = new JPopupMenu(); // Popup menu f�r den Klick auf der rechten Maustaste
		JMenuItem mi = null;

		int selectedCount = tree.getSelectionCount();
		boolean isLabel = currNode.getUserObject() instanceof String;

		if (isLabel) {
			mi = new JMenuItem("Export SQL data to Excel");
			mi.setActionCommand("db2excel");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("Export Tree data to Excel");
			mi.setActionCommand("tree2excel");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("Refresh");
			mi.setActionCommand("refresh");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("Remove element");
			mi.setActionCommand("removeElements");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);
			popupTreeChoice.add(new JSeparator());

			popupTreeChoice.add(new JSeparator());

			mi = new JMenuItem("Edit Raw SQL");
			mi.setActionCommand("editSQL");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View table SQL");
			mi.setActionCommand("copySQL");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View Insert SQL");
			mi.setActionCommand("TableInsert");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			popupTreeChoice.add(new JSeparator());

			mi = new JMenuItem("View table (tab separated)");
			mi.setActionCommand("TableCopyTab");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View table (comma separated)");
			mi.setActionCommand("TableCopyComma");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View table and all sub-records (tab separated)");
			mi.setActionCommand("TableCopySubTab");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View table and all sub-records (comma separated)");
			mi.setActionCommand("TableCopySubComma");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);
		} else {
			String selection = selectedCount > 1 ? ("" + selectedCount + " selected items") : "selected item";
			mi = new JMenuItem("Remove " + selection);
			mi.setActionCommand("removeElements");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			popupTreeChoice.add(new JSeparator());

			mi = new JMenuItem("Select " + selection + "");
			mi.setActionCommand("copySQL");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("Insert " + selection + " (batch)");
			mi.setActionCommand("insertSQL_batch");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("Edit " + selection + " (batch)");
			mi.setActionCommand("updateSQL_batch");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			if (selectedCount > 1) {
				mi = new JMenuItem("Insert " + selection + " (single)");
				mi.setActionCommand("insertSQL_single");
				mi.addActionListener(menuItemListener);
				popupTreeChoice.add(mi);

				mi = new JMenuItem("Edit " + selection + " (single)");
				mi.setActionCommand("updateSQL_single");
				mi.addActionListener(menuItemListener);
				popupTreeChoice.add(mi);
			}

			popupTreeChoice.add(new JSeparator());

			mi = new JMenuItem("View " + selection + " (tab separated)");
			mi.setActionCommand("SelecCopyTab");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View " + selection + " (comma separated)");
			mi.setActionCommand("SelecCopyComma");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View " + selection + " and all sub-records (tab separated)");
			mi.setActionCommand("SelecCopySubTab");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			mi = new JMenuItem("View " + selection + " and all sub-records (comma separated)");
			mi.setActionCommand("SelecCopySubComma");
			mi.addActionListener(menuItemListener);
			popupTreeChoice.add(mi);

			popupTreeChoice.add(new JSeparator());
		}

		popupTreeChoice.setOpaque(true);
		popupTreeChoice.setLightWeightPopupEnabled(false);
	}

	class MenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode currNode = (DefaultMutableTreeNode) (selPath.getLastPathComponent());
			DataNode selectedDataNodeLabel = null;
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			TreePath[] paths = tree.getSelectionPaths();

			if (currNode.getUserObject() instanceof DataNode) {
				selectedDataNodeLabel = (DataNode) (currNode.getUserObject());
				if (selectedDataNodeLabel.isLabel == false && selectedDataNodeLabel.getParent() != null)
					selectedDataNodeLabel = selectedDataNodeLabel.getParent();
			} else if (currNode.getUserObject() instanceof String) {
				if (currNode.getNextNode().getUserObject() instanceof DataNode)
					selectedDataNodeLabel = ((DataNode) (currNode.getNextNode().getUserObject())).getParent();
				else
					selectedDataNodeLabel = (DataNode) currNode.getNextNode().getNextNode().getUserObject();
			}

			String selection = null;
			String separator = null;

			if (e.getActionCommand().contains("db2excel")) {
				try {
					Utils.exportDBQueryToExcel(selectedDataNodeLabel.dBQuery);
					return;
				} catch (Exception ex) {
					MyDialog.showException(ex, "Error when exporting queries to excel");
					return;
				}
			}

			if (e.getActionCommand().contains("tree2excel")) {
				try {
					Sheet sheet = null;
					DataViewerService.generateExcel(
							selectedDataNodeLabel, sheet, true, 0);
					return;
				} catch (Exception ex) {
					MyDialog.showException(ex, "Error when exporting tree to excel");
					return;
				}
			}

			if (e.getActionCommand().contains("refresh")) {
				try {
					selectedDataNodeLabel.dBQuery.generateData(true);
					selectedDataNodeLabel.composeTree();
					return;
				} catch (Exception ex) {
					MyDialog.showException(ex, "Error when refreshing data");
					return;
				}
			}

			if (e.getActionCommand().contains("removeElements")) {

				if (paths != null) {
					for (TreePath path : paths) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
						if (node.getParent() != null) {
							model.removeNodeFromParent(node);
						}
						// else {
						// tree.removeAll();
						// }
					}
				}
			}

			if (e.getActionCommand().contains("editSQL"))
				selection = DataViewerService.generateSqlQueryFromTreeElement(currNode,
						listAllQueryDefinitions,
						Settings.actionEdit, paths) + ";";

			if (e.getActionCommand().contains("copySQL"))
				selection = DataViewerService.generateSqlQueryFromTreeElement(currNode,
						listAllQueryDefinitions,
						Settings.actionView, paths) + ";";

			boolean addSub = false;
			if (e.getActionCommand().endsWith("Tab"))
				separator = "\t";
			else
				separator = ",";

			if (e.getActionCommand().contains("Sub"))
				addSub = true;

			if (e.getActionCommand().contains("TableCopy")) {
				selection = DataViewerService.generateTableDisplay(selectedDataNodeLabel, separator,
						addSub, 1);
			}

			if (e.getActionCommand().contains("TableInsert")) {
				selection = DataViewerService.generateTableInsert(selectedDataNodeLabel, 1,
						e.getActionCommand().contains("_batch"));
			}

			if (e.getActionCommand().contains("SelecCopy")) {
				TreePath[] tp = tree.getSelectionPaths();
				if (tp.length > 0)
					selection = DataViewerService.generateSelectionDisplay(tp, separator, addSub);
			}

			if (e.getActionCommand().contains("insertSQL")) {
				if (paths.length > 0)
					selection = DataViewerService.generateSelectionInsert(selectedDataNodeLabel,
							paths,
							e.getActionCommand().contains("_batch"));
			}

			if (e.getActionCommand().contains("updateSQL")) {
				if (paths.length > 0) {
					selection = DataViewerService.generateSelectionUpdate(selectedDataNodeLabel,
							paths,
							e.getActionCommand().contains("_batch"));
				}
			}

			if (selection != null) {
				JMenuItem menuItem = e.getSource() instanceof JMenuItem ? (JMenuItem) e.getSource() : null;
				int selectedOption = JOptionPane.CANCEL_OPTION;
				if (e.getActionCommand().contains("editSQL")) {
					selection = MyDialog.showEdit(selection, menuItem != null ? menuItem.getText() : "");
					if (selection == null)
						selectedOption = JOptionPane.CANCEL_OPTION;
				} else {
					selectedOption = MyDialog.showDialog(selection, menuItem != null ? menuItem.getText() : "");
				}

				if (selectedOption == JOptionPane.OK_OPTION || MyDialog.getSelectedOption() == JOptionPane.OK_OPTION) {
					if (tree.myPane != null && (selection.toLowerCase().contains("insert ") || selection.toLowerCase()
							.contains("select ") || selection.toLowerCase().contains("delete ")
							|| selection
									.toLowerCase().contains("update ")))
						tree.myPane.setSQL(selection);
					Utils.copyToClipboard(selection, tree);
				}
			}

		}

	}
}
