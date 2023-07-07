package com.rs2.modules.files.isoparser.elements.ascii;

import com.rs2.modules.files.isoparser.MyField;
import com.rs2.modules.files.isoparser.elements.iso.IsoField;
import com.rs2.core.utils.Utils;

public class AsciiField extends MyField {
	public int startIndex;

	public AsciiField(int start, int length) {
		this.startIndex = start;
		this.length = length;
		this.parsedData = Utils.multiplyChars(" ", this.length);
	}

	public AsciiField(int start, int length, String content) {
		this.startIndex = start;
		this.length = length;
		if (content == null)
			content = "";
		if (length > 0 && length > content.length()) {
			content = content + Utils.multiplyChars(" ", length - content.length());
		}
		this.parsedData = content;
	}

	public AsciiField(int length) {
		this.length = length;
	}

	public String toString() {
		return toString(true);
	}

	public String toString(boolean toDisplay) {
		if (toDisplay)
			return name.toUpperCase() + " (pos " + String.valueOf(offset) + ") [" + String.valueOf(length) + "]: '"
					+ parsedData + "'";
		return parsedData;
	}
}