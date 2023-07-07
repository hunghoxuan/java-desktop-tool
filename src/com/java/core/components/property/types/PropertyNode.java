package com.java.core.components.property.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import com.java.core.components.property.types.PropertyTypes.ListType;

public class PropertyNode extends AbstractMutableTreeTableNode {
	public Map<String, Class<?>> COLUMNS_DEF = new LinkedHashMap<String, Class<?>>();
	public Map<Integer, String> COLUMNS_INDEXES = new LinkedHashMap<Integer, String>();
	public String[] data = new String[] {};

	public static final class ChildCount {
		public final int count;

		private ChildCount(int count) {
			this.count = count;
		}
	}

	private String key;
	private PropertyType<?> type;

	public PropertyNode(String key, PropertyType<?> type) {
		super(type.getDefaultValue());
		this.key = key;
		this.type = type;
	}

	public PropertyNode(String[] data) {
		this.data = data;
	}

	public PropertyNode(Map<String, String> data) {
		this.data = data.values().toArray(new String[] {});
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String[] getData() {
		return this.data;
	}

	public PropertyNode(PropertyNode cloneFrom) {
		super(cloneFrom.getType().getDefaultValue());

		this.setKey(cloneFrom.getKey());
		this.setType(cloneFrom.getType());
		this.setData(cloneFrom.getData());
		this.setUserObject(cloneFrom.getUserObject());
		this.setAllowsChildren(cloneFrom.getAllowsChildren());
		children.clear();
		for (int i = 0; i < cloneFrom.getChildCount(); i++) {
			add(new PropertyNode((PropertyNode) cloneFrom.getChildAt(i)));
		}
	}

	public PropertyNode(String key, PropertyType<?> type, Object value) {
		super(value);
		this.key = key;
		this.type = type;
	}

	public String getKey() {
		if (isInList()) {
			return "[" + getParent().getIndex(this) + "]";
		} else {
			return key;
		}
	}

	private void setKey(String key) {
		this.key = key;
	}

	public PropertyType<?> getType() {
		return type;
	}

	private void setType(PropertyType<?> type) {
		this.type = type;
		userObject = type.getDefaultValue();
	}

	public Object toObject() {
		return getType().toObject(this);
	}

	/**
	 * @return <code>true</code> in case this node is contained within a list.
	 */
	private boolean isInList() {
		if (getParent() == null) {
			return false;
		} else {
			PropertyNode parentProperty = (PropertyNode) getParent();
			return parentProperty.getType() instanceof ListType;
		}
	}

	// AbstractMutableTreeTableNode overrides

	@Override
	public boolean getAllowsChildren() {
		return type.isCollection();
	}

	@Override
	public int getColumnCount() {
		if (this.data.length > 0)
			return this.data.length;
		else
			return 3;
	}

	@Override
	public Object getValueAt(int column) {
		if (this.data.length > 0)
			return this.data[column];
		switch (column) {
			case 0:
				return getKey();
			case 1:
				return getType();
			case 2:
				if (type.isCollection()) {
					return new ChildCount(getChildCount());
				} else {
					return userObject;
				}
			default:
				throw new IllegalArgumentException("Invalid column: " + column);
		}
	}

	@Override
	public boolean isEditable(int column) {
		if (this.data.length > 0)
			return column > 0;

		switch (column) {
			case 0:
				return !isInList();
			case 1:
				return true;
			case 2:
				return !getAllowsChildren();
			default:
				throw new IllegalArgumentException("Invalid column: " + column);
		}
	}

	@Override
	public void setValueAt(Object aValue, int column) {
		if (this.data.length > 0) {
			this.data[column] = (String) aValue;
			return;
		}
		switch (column) {
			case 0:
				setKey((String) aValue);
				return;
			case 1:
				setType((PropertyType<?>) aValue);
				return;
			case 2:
				setUserObject(aValue);
				return;
			default:
				throw new IllegalArgumentException("Invalid column: " + column);
		}
	}

	@Override
	public void setParent(MutableTreeTableNode newParent) {
		super.setParent(newParent);

		generateKey();
	}

	/**
	 * Generates a default key for this property
	 */
	private void generateKey() {
		if (getParent() != null && key == null) {
			for (int keyIndex = getParent().getChildCount();; keyIndex++) {
				String key = "item " + keyIndex;
				boolean taken = false;
				// check if key is not yet taken
				for (int childIndex = 0; childIndex < getParent().getChildCount(); childIndex++) {
					PropertyNode childNode = (PropertyNode) getParent().getChildAt(childIndex);
					if (key.equals(childNode.getKey())) {
						taken = true;
						break;
					}
				}
				if (!taken) {
					this.key = key;
					return;
				}
			}
		}
	}

	// Object overrides

	@Override
	public String toString() {
		return getKey();
	}

}
