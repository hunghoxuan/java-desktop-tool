package com.rs2.modules.files.isoparser;

import java.util.*;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import com.rs2.modules.files.*;
import com.rs2.modules.files.templates.IsoFileTemplate;
import com.rs2.core.utils.Utils;

public class MyFile {
	public MyFileReader fileReader;
	public IsoFileTemplate isoFileReader;
	public String fileName = null;
	public String content;
	public byte[] byteArray;
	public int length;
	private List<MyMessage> messages;

	public MyFile() {
		// messages = new LinkedList<IsoMessage>();
	}

	public MyFile(String fileName, String mode) {
		this.fileName = fileName;
		// messages = new LinkedList<IsoMessage>();
		fileReader = getFileReader(fileName, mode);
	}

	public MyFile(String fileName) {
		this(fileName, "r");
	}

	public List<MyMessage> getMessages() {
		if (messages == null)
			messages = new LinkedList<MyMessage>();
		return messages;
	}

	public MyFileReader getFileReader(String fileName) {
		return getFileReader(fileName, "r");
	}

	public MyFileReader getFileReader(String fileName, String mode) {
		if (fileName == null || fileName.isEmpty())
			return null;
		try {
			MyFileReader fr = new MyFileReader(fileName, mode);
			return fr;
		} catch (Exception ex) {
			return null;
		}
	}

	public MyFileReader getFileReader(MyFileReader oldReader) {
		try {
			MyFileReader fr = new MyFileReader(oldReader.fileName, oldReader.mode);
			fr.charset = oldReader.charset;
			return fr;
		} catch (Exception ex) {
			return null;
		}
	}

	public MyFileReader getFileReader() {
		try {

			if (fileReader == null)
				fileReader = getFileReader(fileName);
			if (fileReader != null)
				fileReader.getFilePointer();
		} catch (Exception ex) {
			if (fileReader != null) {
				fileReader = getFileReader(fileReader);
			}
		}
		return fileReader;
	}

	public void setContent(String dataString) {
		this.content = dataString;
	}

	public String toString() {
		return toString(false);
	}

	public String toString(boolean toDisplay) {
		StringBuilder sb = new StringBuilder();
		sb.append(content);

		return sb.toString();
	}

	public String asText() {
		return toString(true);
	}

	public Map<String, Object> asPropertiesObject() {
		Map<String, Object> root = new LinkedHashMap<>();
		int i = 0;
		for (MyMessage msg : this.getMessages()) {
			i++;
			root.put(String.valueOf(i) + ". " + msg.getDescription().toUpperCase(), msg.asPropertiesObject());
		}
		return root;
	}

	public void loadPropertiesObject(Map<String, Map<String, Object>> data) {

	}

	public void save(String dst) {
		Utils.saveFile(dst, toString());
	}

	public void saveXMLFile(String dst) {
		// save(dst, Encoding.ASCII);
	}
}