package com.java.modules.files;

import com.java.core.base.MyPane;
import com.java.core.settings.Settings;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import java.io.File;
import java.util.List;

//hold local params (data) for each tab.
public class FunctionsFilesPane extends FilesPane {
	public String getId() {
		return "Stored Functions";
	}

	public boolean displayConnections() {
		return false;
	}

	@Override
	public File getRootDir() {
		lastDir = new File(Settings.getModulesFolder() + "\\sqls\\functions");
		return lastDir;
	}

	@Override
	public void refresh() {
		refreshFileBrowser();
		isHideLog = true;
	}

	@Override
	public MyPane generateDataPane(boolean overrideOldTree, String statement, boolean override) {
		List<String> functions = DBService.getFunctions("FUNCTION,PROCEDURE", "BW3");
		for (String function : functions) {
			String content = DBService.getFunctionContent(function);
			Utils.saveFile(Settings.getSqlsFolder() + "\\functions\\" + function + ".sql", content);
		}
		// List<String> sps = DBService.getFunctions("PROCEDURE", "");

		refreshFileBrowser();
		return this;
	}
}