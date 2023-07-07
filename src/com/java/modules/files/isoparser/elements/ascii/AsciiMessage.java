package com.java.modules.files.isoparser.elements.ascii;

import java.util.*;

import com.java.core.logs.LogManager;
import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyField;
import com.java.modules.files.isoparser.MyMessage;
import com.java.core.utils.Utils;

public class AsciiMessage extends MyMessage {
	public String dataString;
	public int indexCurr = 0;
	public static int maxLength = 168;
	public static String lineSeperator = "\r\n";

	public Map<String, AsciiField> isoFields = new LinkedHashMap<String, AsciiField>();

	public AsciiMessage() {
		initFields();
	}

	public AsciiMessage(int ifReturn) {
		initFields(ifReturn);
	}

	public AsciiMessage(int offset, String dataString) {
		this();
		setOffset(offset);
		setDataString(dataString);
	}

	public AsciiMessage(String dataString) {
		this();
		setDataString(dataString);
	}

	public String getDescription() {
		return getClassName();
	}

	public String getName() {
		return getClassName();
	}

	public void initFields() {
		initFields(0);
	}

	public void initFields(int ifReturn) {

	}

	public void setOffset(int offset) {
		this.offset = offset;
		for (AsciiField fld : isoFields.values()) {
			fld.offset = this.offset + fld.startIndex;
		}
	}

	public List<MyField> getFields() {
		List<MyField> list = new LinkedList<MyField>();
		for (AsciiField fld : isoFields.values()) {
			list.add(fld);
		}
		return list;
	}

	@Override
	public int getLength() {
		if (length != null && length > 0)
			return length;
		return maxLength;
		// if (length != null && length > 0)
		// return length;
		// int l = 0;
		// for (MyField fld : getFields()) {
		// l += fld.getLength();
		// }
		// length = l;
		// return length;
	}

	public void setDataString(String dataString) {
		if (maxLength > 0 && maxLength > dataString.length()) {
			dataString = dataString + Utils.multiplyChars(" ", maxLength - dataString.length());
		}
		this.dataString = dataString;
		int endIndex;
		if (isoFields != null) {
			for (Map.Entry<String, AsciiField> entry : isoFields.entrySet()) {
				if (entry.getValue().startIndex + entry.getValue().length >= dataString.length())
					endIndex = dataString.length() - 1;
				else
					endIndex = entry.getValue().startIndex + entry.getValue().length;
				if (entry.getValue().startIndex <= endIndex)
					addElement(entry.getKey(), dataString.substring(entry.getValue().startIndex, endIndex));
			}
		}
	}

	public void addElement(String field, int length) {
		this.addElement(indexCurr, length, field, null);
		indexCurr = indexCurr + length;
	}

	public void addElement(int startIndex, int length, String field, String value) {
		AsciiField item = new AsciiField(startIndex, length, value);
		item.name = field;
		item.offset = offset + startIndex;
		isoFields.put(field, item);

		Utils.setFieldFromNameValue(this, FilesService.getAttributeName(field), item);
	}

	public void addElement(String field, String value) {
		if (isoFields.containsKey(field))
			isoFields.get(field).parsedData = value;
		else
			this.addElement(indexCurr, value.length(), field, value);
	}

	public String getContent(String field) {
		if (isoFields.containsKey(field))
			return isoFields.get(field).parsedData;
		return null;
	}

	public char[] getElement(String field) {
		if (isoFields.containsKey(field))
			return isoFields.get(field).parsedData.toCharArray();
		return null;
	}

	public String toString() {
		return toString(true);
	}

	public String toString(boolean toDisplay) {
		StringBuilder sb = new StringBuilder();
		if (toDisplay)
			sb.append("<").append(getClassName())
					.append(getDescription().contains(getClassName()) ? "" : " - " + getDescription())
					.append(" pos: ").append(offset).append(" [").append(getLength())
					.append("]>\n");

		if (isoFields != null) {
			for (Map.Entry<String, AsciiField> entry : isoFields.entrySet()) {
				sb.append(entry.getValue().toString(toDisplay));
				if (toDisplay)
					sb.append("\n");
			}
			dataString = sb.toString();
		} else {
			sb.append(dataString);
		}

		String result = sb.toString();
		if (!toDisplay) {
			if (result.length() > AsciiMessage.maxLength) {
				result = result.substring(0, AsciiMessage.maxLength);
			} else if (result.length() < AsciiMessage.maxLength) {
				result = result + Utils.multiplyChars(" ", AsciiMessage.maxLength - result.length());
			}
		}
		return result;
	}

	public String getClassName() {
		return this.getClass().getSimpleName().toLowerCase();
	}

	public Map<String, Object> asPropertiesObject() {
		Map<String, Object> root = new LinkedHashMap<>();
		for (MyField msg : this.getFields()) {
			root.put(msg.getName().toLowerCase() + " [" + String.valueOf(msg.getLength()) + "]", msg.getValue());
		}
		return root;
	}

	public void updateFromProperties() {
		if (isoFields != null) {
			for (Map.Entry<String, AsciiField> entry : isoFields.entrySet()) {
				Object property = Utils.getFieldValueFromName(this, FilesService.getAttributeName(entry.getKey()));
				Object fldValue;
				if (property != null) {
					fldValue = ((AsciiField) property).getData().toCharArray();
				} else {
					fldValue = Utils.getFieldValueFromName(this, entry.getKey());
				}

				String value = fldValue != null ? new String((char[]) fldValue) : null;
				// System.out.println(getClassName() + " " + entry.getKey() + " : " + value);
				if (value != null) {
					entry.getValue().setData(value);
				}
				isoFields.put(entry.getKey(), entry.getValue());
			}
		}
	}
}