package com.rs2.modules.files.isoparser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rs2.modules.files.FilesService;
import com.rs2.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.rs2.core.utils.Utils;

public class MyField {
	public String name;
	public Integer offset;
	public int length = -1;

	public String parsedData;
	public String description;
	public byte[] rawData;
	private List<MyField> children;

	public MyField() {
		// messages = new LinkedList<IsoMessage>();
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public List<MyField> getSubFields() {
		if (children == null)
			children = new LinkedList<MyField>();
		return children;
	}

	@Override
	public String toString() {
		// return getValue();
		return "FieldData{" + name + " => " + parsedData + "}";
	}

	public String asText() {
		return toString();
	}

	public String getDescription() {
		return description == null ? "" : description;
	}

	public String getValue() {
		return parsedData;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	public String getData() {
		return parsedData;
	}

	public void setData(String data) {
		this.parsedData = data;
	}

	public String getNameDsc() {
		String res = (getName().toUpperCase().trim()
				+ (getDescription().isEmpty() ? "" : (" : " + getDescription().toLowerCase()))).trim();
		res += " (pos " + String.valueOf(offset) + ")";
		res += " [" + getLengthDesc() + "]";
		return res;
	}

	public int getLength() {
		if (length <= 0 && parsedData != null)
			length = parsedData.length();
		return length;
	}

	public String getLengthDesc() {
		if (getLength() <= 0)
			return "";
		return String.valueOf(getLength());
	}

	// public List<MyField> getChildren() {
	// if (children == null) {
	// children = new ArrayList<MyField>();
	// }
	// return children;
	// }

	public Map<String, Object> asPropertiesObject() {
		Map<String, Object> root = new LinkedHashMap<>();
		root.put(
				getName(),
				this.getValue());
		// if (this.getChildren() != null && this.getChildren().size() > 0) {
		// for (MyField child : this.getChildren()) {
		// root.put(
		// FilesService.subFieldPrefix + child.getNameDsc(),
		// child.getValue());
		// }
		// }
		return root;
	}

	// public void initAttributes() {

	// }

	// public void parseChildrenAndUpdateParent() {

	// }

}