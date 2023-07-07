package com.rs2.modules.files.isoparser.elements.ascii;

import java.util.*;
import java.io.FileInputStream;

import com.rs2.modules.files.*;
import com.rs2.modules.files.isoparser.MyFile;
import com.rs2.modules.files.isoparser.MyMessage;
import com.rs2.modules.files.templates.visa.VisaFileTemplate;
import com.rs2.core.utils.Utils;

public class AsciiFile extends MyFile {
	public int indexCurr = 0;
	public int maxLength = 169;
	private List<AsciiMessage> messages = new LinkedList<AsciiMessage>();

	public AsciiFile() {
		super();
	}

	public AsciiFile(String fileName) {
		this();
		this.fileName = fileName;
		// setContent(Utils.getContentFromFile(fileName));
		fileReader = getFileReader(fileName, "r");
	}

	public AsciiFile(String fileName, String mode) {
		super();
		// setContent(Utils.getContentFromFile(fileName));
		this.fileName = fileName;
		fileReader = getFileReader(fileName, mode);
	}

	//
	public List<AsciiMessage> getIsoMessages() {
		return messages;
	}

	@Override
	public List<MyMessage> getMessages() {
		List<MyMessage> list = new LinkedList<MyMessage>();
		for (AsciiMessage msg : messages) {
			list.add(msg);
		}
		return list;
	}

	public void setContent(String dataString) {
		super.setContent(dataString);
		int offset = 0;
		String[] arr = dataString.split(AsciiMessage.lineSeperator);
		getIsoMessages().clear();
		for (String line : arr) {
			AsciiMessage lineArray = VisaFileTemplate.createAsciiMessage(offset, line);
			if (lineArray != null) {
				lineArray.offset = offset;
				lineArray.length = line.length();
				getIsoMessages().add(lineArray);
				offset += line.length() + AsciiMessage.lineSeperator.length(); // skip EO
			}
		}
	}

	public String toString() {
		return toString(false);
	}

	public String toString(boolean toDisplay) {
		StringBuilder sb = new StringBuilder();
		if (getIsoMessages() != null) {
			for (AsciiMessage msg : getIsoMessages()) {
				sb.append(msg.toString(toDisplay)).append(AsciiMessage.lineSeperator);
			}
			if (sb.length() > 2)
				sb.setLength(sb.length() - 2);
			content = sb.toString();
		} else {
			sb.append(content);
		}

		return sb.toString();
	}

	public String asText() {
		return toString(true);
	}

	public void save(String dst) {
		Utils.saveFile(dst, toString());
	}

	public void saveXMLFile(String dst) {
		// save(dst, Encoding.ASCII);
	}

	@Override
	public void loadPropertiesObject(Map<String, Map<String, Object>> data) {
		String fileContent = "";
		for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
			for (Map.Entry<String, Object> entryF : entry.getValue().entrySet()) {
				String value = (String) entryF.getValue();
				try {
					int length = Integer.parseInt(Utils.substringBetween(entryF.getKey(), "[",
							"]"));
					if (length > 0 && value.length() > length)
						value = value.substring(0, length);
					else if (length > 0 && value.length() < length)
						value = value + Utils.multiplyChars(" ", length - value.length());
				} catch (Exception ex) {

				}

				fileContent += value;
			}
			fileContent += AsciiMessage.lineSeperator;
		}
		setContent(fileContent);
	}
}