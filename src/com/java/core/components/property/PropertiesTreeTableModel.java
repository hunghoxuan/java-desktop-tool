package com.java.core.components.property;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.checkerframework.checker.units.qual.C;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.java.core.components.property.types.PropertyNode;
import com.java.core.components.property.types.PropertyType;

public class PropertiesTreeTableModel extends DefaultTreeTableModel {
	public Map<String, Class<?>> COLUMNS_DEF = new LinkedHashMap<String, Class<?>>();
	public Map<Integer, String> COLUMNS_INDEXES = new LinkedHashMap<Integer, String>();

	public PropertiesEditorConfig config;

	/**
	 * Create a new PropertiesTreeTableModel.
	 * 
	 * @param data
	 *             The data to show in the editor (must be a Map or List).
	 * @throws IllegalArgumentException
	 *                                  In case the data is not an instance of Map
	 *                                  or List.
	 */
	public PropertiesTreeTableModel(Object data) {
		this(PropertiesEditorConfig.defaultConfig(), data);
	}

	/**
	 * Create a new PropertiesTreeTableModel.
	 * 
	 * @param config
	 *               The custom configuration.
	 * @param data
	 *               The data to show in the editor (must be a Map or List).
	 * @throws IllegalArgumentException
	 *                                  In case the data is not an instance of Map
	 *                                  or List.
	 * @see PropertiesEditorConfig
	 */
	public PropertiesTreeTableModel(PropertiesEditorConfig config, Object data) {
		super(assertCollection(config.fromObject(data)));
		this.config = config;
		addColumn("Key", String.class);
		addColumn("Type", PropertyType.class);
		addColumn("Value", Object.class);
	}

	public void addColumn(String name, Class<?> type) {
		COLUMNS_INDEXES.put(COLUMNS_DEF.size(), name);
		COLUMNS_DEF.put(name, type);
	}

	public void clearColumns() {
		COLUMNS_DEF.clear();
		COLUMNS_INDEXES.clear();
	}

	private static PropertyNode assertCollection(PropertyNode propertyNode) {
		if (!propertyNode.getType().isCollection()) {
			throw new IllegalArgumentException("The data argument must be a Map or List.");
		}
		return propertyNode;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS_INDEXES.get(column);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return COLUMNS_DEF.get(COLUMNS_INDEXES.get(column));
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {

		if (column == getColumnTypeIndex()) {

			PropertyNode currentNode = (PropertyNode) node;

			PropertyType<?> oldType = currentNode.getType();
			PropertyType<?> newType = (PropertyType<?>) value;

			if (oldType == newType) {
				return;
			}

			currentNode.setValueAt(newType, column);

			if (oldType.isCollection() && !newType.isCollection()) {

				// changed from collection to primitive; remove all children
				for (int i = currentNode.getChildCount() - 1; i >= 0; i--) {
					TreeTableNode nodeToRemove = currentNode.getChildAt(i);
					removeNodeFromParent((MutableTreeTableNode) nodeToRemove);
				}

			} else if (oldType.isCollection() && newType.isCollection()) {
				// changed from list to map or vice versa; trigger update
				// listener for all child nodes b/c the keys might have changed
				fireChildrenChanged(currentNode);

			} else {
				// changed between primitives
				modelSupport.fireChildChanged(new TreePath(getPathToRoot(currentNode.getParent())),
						currentNode.getParent().getIndex(currentNode), currentNode);
			}

		} else {
			super.setValueAt(value, node, column);
		}
	}

	public int getColumnTypeIndex() {
		return 1;
	}

	protected void fireChildrenChanged(TreeTableNode node) {
		TreePath parentPath = new TreePath(getPathToRoot(node));

		int[] indices = new int[node.getChildCount()];
		Object[] children = new Object[node.getChildCount()];

		for (int i = 0; i < node.getChildCount(); i++) {
			indices[i] = i;
			children[i] = node.getChildAt(i);
		}

		modelSupport.fireChildrenChanged(parentPath, indices, children);
	}

	/**
	 * @return The data shown in the editor; this is either a <tt>Map&lt;String,
	 *         Object&gt;</tt>, or a <tt>List&lt;Object&gt;</tt>.
	 */
	public Object getData() {
		return ((PropertyNode) getRoot()).toObject();
	}

	/**
	 * Set the data shown in the editor.
	 * 
	 * @param data
	 *             The data; either a <tt>Map&lt;String, Object&gt;</tt>, or a
	 *             <tt>List&lt;Object&gt;</tt>
	 * @throws IllegalArgumentException
	 *                                  In case the data is not an instance of Map
	 *                                  or List.
	 */
	public void setData(Object data) {
		setRoot(assertCollection(config.fromObject(data)));
	}

	@Override
	public void insertNodeInto(MutableTreeTableNode newChild, MutableTreeTableNode parent, int index) {
		super.insertNodeInto(newChild, parent, index);

		// make sure all children are updated; a list entry might change its
		// key from [9] to [10], and we need to re-draw the table in this case
		fireChildrenChanged(parent);
	}

	/**
	 * @return The editor configuration.
	 */
	public PropertiesEditorConfig getConfig() {
		return config;
	}

}
