package com.rs2.core.components.treeview;

import java.sql.Connection;
import java.util.*;

import javax.swing.tree.*;

import com.rs2.Main;
import com.rs2.core.components.MyLinkedMap;
import com.rs2.core.components.MyTreeMap;
import com.rs2.core.data.DBColumn;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.TransmittedCondition;
import com.rs2.core.settings.Settings;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.modules.db.DBService;

public class DataNode {
	public DataNode parent;
	private LinkedList<DataNode> children = new LinkedList<DataNode>();
	private boolean[] isResultColumn;
	public String values;
	String columns;
	public String viewOption;
	private String[] valueArray, resultValueArray, rawValueArray;
	private String[] columnArray, resultColumnArray;
	int childNumber;
	private Map<String, Integer> maxColumnLength;
	private Map<String, Boolean> isColumnNull;
	private Map<String, String> filterConditions;

	private int nodeIndex;
	private String nodeId;
	boolean allColumnsAreNull;

	public boolean isLabel;
	boolean queryOnDemand;
	public boolean isGroupbyNode = false;

	public DBQuery dBQuery;

	public DataNode(String cont) {
		values = cont;
		if (cont == null || cont.equals(""))
			values = Settings.nullValue;
		valueArray = this.values.split(Settings.dataSeperator);
	}

	DataNode() {
		this(Settings.nullValue);
	}

	DataNode(String cont, DataNode parent) {
		this(cont);
		this.parent = parent;
	}

	public DataNode(String columns, String cont, DataNode parent) {
		this(cont, parent);
		this.columns = columns;
		this.columnArray = this.columns.split(Settings.dataSeperator);
	}

	int getNodeIndex() {
		return this.nodeIndex;
	}

	public void setNodeIndex(int value) {
		this.nodeIndex = value;
	}

	public String getNodeID() {
		return this.nodeId;
	}

	public void setNodeID(String value) {
		this.nodeId = value;
	}

	public String getValues() {
		return values;
	}

	public void setRawValues(String values) {
		rawValueArray = values.split(Settings.dataSeperator);
	}

	public String getRawValues() {
		return String.join(Settings.dataSeperator, getRawValuesArray());
	}

	public String[] getRawValuesArray() {
		// if (rawValueArray == null) {
		// rawValueArray = valueArray;
		// for (int i = 0; i < rawValueArray.length; i++) {
		// String tmp = rawValueArray[i];
		// tmp = tmp.replace(Settings.errorPrefix, "");
		// if (tmp.contains(" : "))
		// tmp = tmp.substring(0, tmp.indexOf(" : "));
		// rawValueArray[i] = tmp;
		// }
		// }
		rawValueArray = getValueArray();
		for (int i = 0; i < rawValueArray.length; i++) {
			String tmp = rawValueArray[i];
			if (tmp != null) {
				tmp = tmp.replace(Settings.errorPrefix, "");
				tmp = DataViewerService.getKeyFromDisplayKeyValue(tmp);
			}
			rawValueArray[i] = tmp;
		}
		return rawValueArray;
	}

	public void setValues(String cont) {
		setContent(cont);
	}

	public Boolean isGroupByNode() {
		return isGroupbyNode;
	}

	public void setIsGroupByNode(Boolean cont) {
		isGroupbyNode = cont;
	}

	public void setNodeTitle(String cont) {
		setContent(cont);
	}

	public void setContent(String neu) {
		if (neu == null)
			neu = Settings.nullValue;
		values = neu;
		valueArray = this.values.split(Settings.dataSeperator);
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
		this.columnArray = this.columns.split(Settings.dataSeperator);
	}

	public DataNode getParent() {
		return parent;
	}

	public boolean hasParent() {
		if (parent == null)
			return false;
		return true;
	}

	public void setParent(DataNode p) {
		parent = p;
	}

	public boolean isQueryOnDemand() {
		return queryOnDemand;
	}

	public boolean isAncestorQueryOnDemand() {
		if (this.hasParent() == false)
			return false;
		return getParent().rekIsAncestorQueryOnDemand();
	}

	private boolean rekIsAncestorQueryOnDemand() {
		if (queryOnDemand)
			return true;
		if (this.hasParent() == false)
			return false;
		return getParent().rekIsAncestorQueryOnDemand();
	}

	public void setQueryOnDemand(boolean queryOnDemand) {
		this.queryOnDemand = queryOnDemand;
	}

	public String getViewOption() {
		return viewOption;
	}

	public void setViewOption(String viewOption) {
		this.viewOption = viewOption;
	}

	public void propagateViewOption() {
		if (this.children.size() > 0 && this.viewOption != null) {
			for (DataNode childNode : this.children) {
				childNode.setViewOption(this.viewOption);
				if (childNode.children.size() > 0)
					for (DataNode enkelchen : childNode.children) {
						enkelchen.setViewOption(viewOption);
						enkelchen.propagateViewOption();
					}
			}
		}
	}

	public int getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(int childNumber) {
		this.childNumber = childNumber;
	}

	public LinkedList<DataNode> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<DataNode> newChildren) {
		this.children = newChildren;
	}

	public DataNode getFirstChild() {
		LinkedList<DataNode> children = this.getChildren();
		if (children.size() > 0)
			return children.getFirst();
		else
			return null;
	}

	public DataNode insertOnLevel(DataNode child) {
		children.add(child);
		child.setChildNumber(children.lastIndexOf(child));
		return this;
	}

	public DataNode insertDeep(DataNode child) {
		if (child == null)
			return this;

		this.children.add(child);
		child.setChildNumber(children.lastIndexOf(child));
		child.setParent(this);
		return child;
	}

	public boolean[] getIsResultColumn() {
		return isResultColumn;
	}

	public void setIsResultColumn(boolean[] isResultColumn) {
		this.isResultColumn = isResultColumn;
	}

	public Map<String, Integer> getMaxColumnLength() {
		return maxColumnLength;
	}

	public Map<String, Boolean> getIsColumnNull() {
		return isColumnNull;
	}

	public Map<String, String> getFilterConditions() {
		if (filterConditions == null) {
			filterConditions = new MyTreeMap();
		}

		if (this.dBQuery != null && this.getParent() != null && this.getParent().dBQuery != null) {
			List<TransmittedCondition> applCond = DataViewerService.getApplicableConditionFromNodeParents(this,
					this.getParent().dBQuery.transmittedConditions, false);
			for (TransmittedCondition retrivedCondition : applCond) {
				if (retrivedCondition.getValue().equals("null")) {

				} else {
					String key = retrivedCondition.getIdentifier();
					String value = (String) retrivedCondition.getValue();
					setFilterCondition(key, value);
				}
			}
		}

		return filterConditions;
	}

	public String getFilterConditionsAsString() {
		StringBuilder a = new StringBuilder();

		filterConditions = getFilterConditions();

		for (Map.Entry<String, String> entry : filterConditions.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			if (value != null && !value.isEmpty() && key != null && !key.isEmpty()) {
				a.append(key.toLowerCase() + ":" + value + ", ");
			}
		}
		return a.length() > 0 ? a.toString().substring(0, a.length() - 2) : "";
	}

	public void setFilterCondition(String key, String value) {
		if (filterConditions == null)
			filterConditions = new MyLinkedMap();
		if (key == null || key.isEmpty())
			return;
		// if (!filterConditions.containsKey((String) key.toLowerCase()))
		filterConditions.put((String) key.toLowerCase(), value);
	}

	public void setFilterConditions(Map<String, String> value) {
		filterConditions = value;
	}

	public void setNodeBranchInfo(DataNode node) {
		if (node.children != null) {
			for (DataNode kindchen : node.children)
				setNodeBranchInfo(kindchen);
		}
		boolean omitNullColumns = false;
		Map<String, Boolean> isColumnNullOnBranch = new HashMap<String, Boolean>();
		if (this.viewOption.equals(Settings.viewDetail) && node.dBQuery.getEmptyColumnDisplayMode() != null
				&& node.dBQuery.getEmptyColumnDisplayMode().equals("SUPPRESSBRANCHLEVEL"))
			omitNullColumns = true;
		else
			omitNullColumns = false;

		boolean resultSetColumnsOnly = true;
		if (!node.isLabel) {
			String[] resultColumnArray = node.getColumnArray(resultSetColumnsOnly);
			if (node.getChildNumber() == 0) {

				Map<String, Integer> columnsWidth = new HashMap<String, Integer>();
				String[] valueArray = node.getValueArray(resultSetColumnsOnly);

				for (int t = 0; t < resultColumnArray.length; t++) {
					if (t >= valueArray.length)
						break;
					columnsWidth.put(resultColumnArray[t],
							Math.max(resultColumnArray[t] == null ? 0 : (resultColumnArray[t].length() + 0),
									valueArray[t] == null ? 0 : (valueArray[t].length() + 0)));

					if (omitNullColumns
							&& (DBService.isNullOrEmptyValue(valueArray[t]))) {
						isColumnNullOnBranch.put(resultColumnArray[t], true);
						node.allColumnsAreNull = true;
					} else {
						isColumnNullOnBranch.put(resultColumnArray[t], false);
						node.allColumnsAreNull = false;
					}
				}
				node.maxColumnLength = columnsWidth;
				if (omitNullColumns)
					node.isColumnNull = isColumnNullOnBranch;
			} else {
				node.maxColumnLength = node.getParent().getChildren().get(node.getChildNumber() - 1).maxColumnLength;

				if (node.maxColumnLength == null)
					node.maxColumnLength = new HashMap<String, Integer>();

				if (omitNullColumns)
					node.isColumnNull = node.getParent().getChildren().get(node.getChildNumber() - 1).isColumnNull;
				for (int t = 0; t < resultColumnArray.length; t++) {

					String[] nodeValueArray = node.getValueArray(resultSetColumnsOnly);
					if (nodeValueArray != null && nodeValueArray[t] != null) {
						if (node.maxColumnLength.containsKey(resultColumnArray[t])) {
							int val1 = node.maxColumnLength.get(resultColumnArray[t]);
							int val2 = nodeValueArray[t].length(); // set column width
							if (t > 0 && t < resultColumnArray.length - 1) {
								val2 = nodeValueArray[t].length() + 6;
							}
							node.maxColumnLength.put(resultColumnArray[t], Math.max(val1, val2));

							if (omitNullColumns && node.isColumnNull.containsKey(resultColumnArray[t])
									&& node.isColumnNull.get(resultColumnArray[t])
									&& (nodeValueArray[t] != null && !nodeValueArray[t].equals("null"))) {
								node.isColumnNull.put(
										resultColumnArray[t],
										false);
								node.allColumnsAreNull = false;
							}
						}
					}
				}
			}

		}

		if (node.isLabel && node.getFirstChild() != null && !node.getFirstChild().isLabel && omitNullColumns) {
			propagateNullColumnOption(node);
		}
	}

	private void propagateNullColumnOption(DataNode node) {
		if (node.children != null && !node.getFirstChild().isLabel) {
			for (DataNode kindchen : node.children) {
				boolean isResultColumnChange = false;
				for (int t = 0; t < kindchen.columnArray.length; t++) {
					if (kindchen.isColumnNull.containsKey(kindchen.columnArray[t])
							&& kindchen.isColumnNull.get(kindchen.columnArray[t]) != null
							&& kindchen.isColumnNull.get(kindchen.columnArray[t])) {
						kindchen.isResultColumn[t] = false;
						isResultColumnChange = true;
					}
				}
				if (isResultColumnChange) {
					kindchen.setValueArray(true);
					kindchen.setColumnArray(true);
				}
			}
		}

	}

	public List<DBColumn> getDBColumns() {
		List<DBColumn> list = new LinkedList<DBColumn>();
		String[] columns = getResultColumnArray();
		String[] values = getResultValueArray();
		for (int i = 0; i < columns.length; i++) {
			DBColumn col = new DBColumn(columns[i], null, null, null, null, false);
			col.setContent(values[i]);
			list.add(col);
		}
		return list;
	}

	public String[] getValueArray() {
		return valueArray;
	}

	public String[] getResultValueArray() {
		return getValueArray(true, false);
	}

	public void setValueArray(boolean resultSetColumnsOnly) {
		this.resultValueArray = getValueArray(resultSetColumnsOnly, true);
	}

	public String[] getValueArray(boolean resultSetColumnsOnly) {
		return getValueArray(resultSetColumnsOnly, true);
	}

	public String[] getValueArray(boolean resultSetColumnsOnly, boolean forDisplay) {// only user choosen columns are
																						// considered

		if (this.isResultColumn == null) {
			this.isResultColumn = new boolean[valueArray.length];
			for (int t = 0; t < valueArray.length; t++) {
				this.isResultColumn[t] = true;
			}
		}
		int counter = 0;
		for (int t = 0; t < this.isResultColumn.length; t++)
			if (this.isResultColumn[t] == resultSetColumnsOnly)
				counter++;
		String[] adjustedValueArray = new String[forDisplay ? (counter + 2) : counter];

		if (forDisplay) {
			adjustedValueArray[0] = "" + this.getNodeIndex();
			counter = 1;
		} else {
			counter = 0;
		}

		String[] valueArray = this.getValueArray();

		for (int t = 0; t < this.isResultColumn.length; t++)
			if (this.isResultColumn[t] == resultSetColumnsOnly && t < valueArray.length) {
				adjustedValueArray[counter] = valueArray[t];
				counter++;
			}
		String lastColumnInfo = "";
		Iterator<DataNode> it = this.getChildren().iterator();
		while (it.hasNext()) {
			DataNode child = (DataNode) it.next();
			lastColumnInfo = lastColumnInfo + child.getValues()
					+ " (" + getRowsCountFromDataNode(child) + ")" // label of child ==> not working because children
																	// are not generated at that time
					+ ", ";
		}
		if (forDisplay)
			adjustedValueArray[adjustedValueArray.length - 1] = lastColumnInfo;
		return adjustedValueArray;
	}

	public String getTableName() {
		return dBQuery.getTableName();
	}

	public Connection getConnection() {
		return dBQuery.getConnection();
	}

	public Map<String, String> getKeyValues() {
		Map<String, String> data = new LinkedHashMap<String, String>();
		String[] columns = getColumnArray();
		String[] values = getRawValuesArray();

		for (int i = 0; i < values.length; i++) {
			data.put(columns[i], values[i]);
		}
		return data;
	}

	public Map<String, String> getPrimaryKeyValues() {
		Map<String, String> data = new LinkedHashMap<String, String>();
		String[] columns = getColumnArray();
		String[] values = getRawValuesArray();
		List<String> primarys = getPrimaryColumnsList();
		for (int i = 0; i < values.length; i++) {
			if (primarys.contains(columns[i]))
				data.put(columns[i], values[i]);
		}
		return data;
	}

	public String getColumnValue(String columnName) {
		String[] columns = getColumnArray();
		String[] values = getRawValuesArray();
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].equalsIgnoreCase(columnName))
				return values[i];
		}
		return null;
	}

	public String[] getPrimaryColumnsArray() {
		return DBService.getPrimaryColumns(getConnection(), getTableName()).stream().toArray(String[]::new);
	}

	public List<String> getPrimaryColumnsList() {
		return new ArrayList<String>(Arrays.asList(getPrimaryColumnsArray()));
	}

	public String[] getColumnArray() {
		return columnArray;
	}

	public List<String> getColumnsList() {
		return new ArrayList<String>(Arrays.asList(getColumnArray()));
	}

	public String[] getResultColumnArray() {
		return getColumnArray(true, false);
	}

	public String[] getColumnArray(boolean resultSetColumnsOnly) {
		if (this.resultColumnArray == null)
			setColumnArray(resultSetColumnsOnly);
		return this.resultColumnArray;
	}

	private void setColumnArray(boolean resultSetColumnsOnly) {
		this.resultColumnArray = getColumnArray(resultSetColumnsOnly, true);
	}

	public String[] getColumnArray(boolean resultSetColumnsOnly, boolean forDisplay) {

		if (this.isResultColumn == null) {// nur f�r alle F�lle
			this.isResultColumn = new boolean[valueArray.length];
			for (int t = 0; t < valueArray.length; t++) {
				this.isResultColumn[t] = true;
			}
		}
		int counter = 0;
		for (int t = 0; t < this.isResultColumn.length; t++)
			if (this.isResultColumn[t] == resultSetColumnsOnly)
				counter++;
		String[] adjustedColumnArray = new String[forDisplay ? (counter + 2) : counter];
		if (forDisplay) {
			adjustedColumnArray[0] = "#";
			counter = 1;
		} else {
			counter = 0;
		}
		for (int t = 0; t < this.isResultColumn.length; t++)
			if (this.isResultColumn[t] == resultSetColumnsOnly) {
				adjustedColumnArray[counter] = this.getColumnArray()[t];
				counter++;
			}

		if (forDisplay) {
			adjustedColumnArray[adjustedColumnArray.length - 1] = "+";
		}
		return adjustedColumnArray;
	}

	public int getRowLength() {
		int rowLength = 0;
		if (isLabel)
			values.length();
		else {
			for (String column_name : this.getColumnArray(true))
				rowLength += maxColumnLength.get(column_name);
		}
		return rowLength;
	}

	public static int[] getTreeNodeCount(DefaultMutableTreeNode treeNode) {
		int[] result = new int[2];
		if (treeNode == null) {
			result[0]++;
			return result;
		}
		Object treeObject = ((DefaultMutableTreeNode) treeNode).getUserObject();
		if (treeObject instanceof DataNode)
			result[1]++;
		else
			result[0]++;
		Enumeration<TreeNode> childNode = treeNode.children();
		while (childNode.hasMoreElements()) {
			rekGetTreeNodeCount(result, childNode.nextElement());
		}
		return result;
	}

	public static void rekGetTreeNodeCount(int[] result, TreeNode treeNode) {

		Object treeObject = ((DefaultMutableTreeNode) treeNode).getUserObject();
		if (treeObject instanceof DataNode)
			result[1]++;
		else
			result[0]++;
		Enumeration<TreeNode> childNode = (Enumeration<TreeNode>) treeNode.children();
		while (childNode.hasMoreElements()) {
			rekGetTreeNodeCount(result, childNode.nextElement());
		}
	}

	public DefaultMutableTreeNode composeTree() {
		return rekComposeTree(this);
	}

	public DefaultMutableTreeNode rekComposeTree(DataNode et) {
		if (et.children.isEmpty()) {
			if (et.isLabel)
				return new DefaultMutableTreeNode(et.getValues());
			if (!et.isLabel)
				return new DefaultMutableTreeNode(et);
		}
		DefaultMutableTreeNode baum;
		if (et.isLabel)
			baum = new DefaultMutableTreeNode(et.getValues());
		else
			baum = new DefaultMutableTreeNode(et);
		Iterator<DataNode> it = et.children.iterator();
		// int rowIndex = 0;
		while (it.hasNext()) {
			DataNode child = it.next();
			baum.add(rekComposeTree(child));
		}
		return baum;
	}

	public static Integer getRowsCountFromTreeNode(DefaultMutableTreeNode currTreeNode) {
		Integer result = 0;

		Object treeObject = currTreeNode.getUserObject();

		if (!currTreeNode.isRoot() && currTreeNode.getChildCount() > 0 && treeObject instanceof String) {
			if (((DefaultMutableTreeNode) currTreeNode.getFirstChild())
					.getUserObject() instanceof DataNode) {
				return currTreeNode.getChildCount();
			} else {
				return currTreeNode.getChildCount();
			}
		}

		return currTreeNode.getChildCount();
	}

	public static Integer getRowsCountFromDataNode(DataNode currentNode) {
		Integer result = 0;

		for (DataNode key : currentNode.getChildren()) {
			if (key.isGroupbyNode)
				result += getRowsCountFromDataNode(key);
			else
				result += 1;
		}

		return result;

	}

	@Override
	public String toString() {
		return "columnArray=" + Arrays.toString(columnArray) + "  " +
				"valueArray=" + Arrays.toString(valueArray);
	}
}