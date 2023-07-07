package com.rs2.core.components;

import javax.swing.filechooser.FileFilter;

import com.rs2.core.settings.Settings;

import java.io.File;

public class MyFileFilter extends FileFilter {

	public MyFileFilter(String ext, String desc) {
		super();
		this.extension = ext;
		this.description = desc;
	}

	public MyFileFilter() {
		super();
		this.extension = Settings.FILETYPE_XML;
		this.description = "XML Files";
	}

	String extension;
	String description;

	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory())
				return true;
			if (f != null) {
				String[] exts = extension.split(";");
				if (extension.contains(","))
					exts = extension.split(",");
				for (String ext : exts) {
					ext = ext.replace("*.", ".");
					if (f.getName().toLowerCase().endsWith(ext))
						return true;
				}
				return false;
			}
		}
		;
		return false;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}