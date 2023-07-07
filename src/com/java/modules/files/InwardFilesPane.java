package com.java.modules.files;

import com.java.core.settings.Settings;
import com.java.core.base.MyService;
import com.java.modules.db.DBService;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//hold local params (data) for each tab.
public class InwardFilesPane extends FilesPane {
	public String getId() {
		return "Inward/ Outward Files";
	}

	public boolean displayConnections() {
		return true;
	}

	@Override
	public File getRootDir() {
		String lastDirTag = Settings.getIniSetting(FilesService.settingRootFolder);
		if (lastDirTag != null && lastDirTag.length() > 0 && new File(lastDirTag).isDirectory())
			lastDir = new File(lastDirTag);
		else
			lastDir = MyService.lastDir;
		return lastDir;
	}

	@Override
	public void refreshConnectionCombo() {
		String connStr = (String) comboBoxStoredConns.getSelectedItem();

		if (connStr.isEmpty()) {
			fileBrowser.setRoot(lastDir);
		} else {
			String sql = "select s.institution_number, s.process_name, file_path from sys_process_user_setup S where S.process_group in ('001', '002') order by s.institution_number, s.process_group";
			Connection conn = initConnection();
			if (conn == null)
				return;
			try {
				Statement stmn = conn.createStatement();
				ResultSet rs = DBService.executeQuery(stmn, sql);
				while (rs.next()) {
					if (rs.getString("file_path") == null || rs.getString("file_path").isEmpty())
						continue;
					fileBrowser.addSelectedFolder(rs.getString("institution_number"),
							new String[] { rs.getString("file_path") });
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileBrowser.setRoot(null);
		}
		fileBrowser.refreshTree();
	}

}