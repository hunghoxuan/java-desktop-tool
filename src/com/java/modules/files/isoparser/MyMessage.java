package com.java.modules.files.isoparser;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyMessage {
	public Integer offset = 0;
	public Integer length = 0;
	public byte[] rawData;
	private List<MyField> fields = new LinkedList<MyField>();

	public MyMessage() {
	}

	public int getRawDataLength() {
		if (rawData != null)
			return rawData.length;
		return getLength();
	}

	public int getLength() {
		if (length != null && length > 0)
			return length;
		return 0;
	}

	public Collection<MyField> getFields() {
		return fields;
	}

	public void addField(MyField fld) {
		fields.add(fld);
	}

	public String getDescription() {
		return this.getClass().getSimpleName();
	}

	public Map<String, Object> asPropertiesObject() {
		Map<String, Object> root = new LinkedHashMap<>();

		for (MyField fld : this.getFields()) {
			for (Map.Entry<String, Object> entry : fld.asPropertiesObject().entrySet()) {
				root.put(entry.getKey(), entry.getValue());
			}
		}
		return root;
	}

	public String asText() {
		String res = "";
		res += "<" + getDescription() + ">\n";
		// if (offset != null) res += "\nOffset: 0x" + Long.toHexString(offset);

		// res += "\nFields: \n";
		for (MyField fld : getFields()) {
			res += fld.asText() + "\n";
		}
		return res;
	}

	public MyField getField(String attribute) {
		// Call the corresponding getter for a recognized attribute_name
		String[] fldNames = attribute.split("\\.");
		MyField rootFld = null, dataFld = null;
		for (MyField fld : getFields()) {
			if (fld.getName().equalsIgnoreCase(fldNames[0])) {
				rootFld = fld;
				break;
			}
		}

		if (rootFld != null && fldNames.length > 1) {
			for (MyField fld : rootFld.getSubFields()) {
				if (fld.getName().equalsIgnoreCase(fldNames[fldNames.length - 1])) {
					dataFld = fld;
					break;
				}
			}
		}

		if (dataFld != null) {
			return dataFld;
		} else if (rootFld != null && fldNames.length == 1) {
			return rootFld;
		}

		return null;
	}

	public String getAttribute(String attribute) {
		MyField fld = getField(attribute);
		if (fld != null)
			return fld.getData();

		return null;
	}
}