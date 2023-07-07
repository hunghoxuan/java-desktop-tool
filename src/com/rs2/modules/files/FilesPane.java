package com.rs2.modules.files;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.rs2.core.components.MyTree;
import com.rs2.core.settings.Settings;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.modules.files.components.FilesBrowser;

import java.awt.Component;
import java.io.File;

//hold local params (data) for each tab.
public class FilesPane extends MyPane {
	String editFile, fileContent;
	File lastDir;

	public String getId() {
		return "Files";
	}

	public FilesPane(Component view) {
		super(view);
	}

	public FilesPane() {
		super();
	}

	public FilesPane(String folder) {
		super();
		lastDir = new File(folder);
	}

	public void initMenuPanel() {
		if (displayConnections()) {
			initComboConns();
			getMenuJpanel().add(comboBoxStoredConns);
		}

		getMenuJpanel().add(btnOk);
		getMenuJpanel().add(btnCancel);
		getMenuJpanel().add(btnRefresh);
		// getMenuJpanel().add(btnHideLog);
		getMenuJpanel().add(btnSort);

		getMenuJpanel().add(btnEdit);
		getMenuJpanel().add(btnDelete);
		getMenuJpanel().add(btnClose);
	}

	public boolean displayConnections() {
		return false;
	}

	public File getRootDir() {
		if (lastDir == null)
			lastDir = new File(Settings.getAppFolder());
		return lastDir;
	}

	public void initUI(Component view) {
		MyService.initParams();

		fileBrowser = new FilesBrowser(getRootDir());

		super.initUI(view);

		getMenuJpanel().setVisible(true);
		getMainJPanel().setVisible(true);
		getBottomJPanel().setVisible(!isHideLog);
		getParamsPanel().setVisible(true);

		if (view != null) {
			component = view;
			if (view instanceof MyTree)
				theGUITree = (MyTree) view;
		}

		fileBrowser.myPane = this;
		initTree(fileBrowser.fileTree);
		setMainPanel(fileBrowser);
	}

	public void refreshUIMenu() {
		btnAdd.setVisible(true);
		btnEdit.setText(viewOption == Settings.viewDetail ? "Edit" : "Save");
		btnEdit.setVisible(true);
		btnCancel.setVisible(viewOption == Settings.viewContent);
		btnReset.setVisible(false);
		btnCollapse.setVisible(true);
		btnOk.setVisible(true);
		btnClose.setVisible(true);
	}

	public String getEditFile() {
		return editFile;
	}

	@Override
	public void collapse() {
		fileBrowser.sortByName = false;
		isCollapesed = false;
		fileBrowser.refreshTree();
	}

	@Override
	public void expand() {
		fileBrowser.sortByName = true;
		isCollapesed = true;
		fileBrowser.refreshTree();
	}

	// @Override
	// public void reload() {

	// if (isRunning) {
	// progressBar.stop();
	// isRunning = false;
	// refreshButtons();
	// } else {
	// progressBar.start();
	// Thread queryThread = new Thread() {
	// public void run() {
	// try {
	// isRunning = true;
	// refreshButtons();
	// updateParams();
	// // fileBrowser.run(new String[] {});

	// progressBar.stop();
	// isRunning = false;
	// refreshButtons();
	// } catch (Exception ex) {
	// progressBar.stop();
	// isRunning = false;
	// MyDialog.showException(ex, "");
	// }
	// }
	// };
	// queryThread.start();
	// }
	// }

	public void edit() {
		File file = fileBrowser.getCurrentFile();
		if (file.isFile()) {
			fileBrowser.edit();
			editFile = fileBrowser.getCurrentFile().getAbsolutePath();
			// fileContent = fileBrowser.getF;
			viewOption = Settings.viewContent;
			this.refreshUIMenu();
		}
	}

	public void delete() {
		fileBrowser.delete();
	}

	public void save() {
		// LogManager.getLogger().debug(fileBrowser.getData().toString());
		if (viewOption == Settings.viewContent && editFile != null && !editFile.isEmpty()) {
			fileBrowser.save();
			viewOption = Settings.viewDetail;
			this.refreshUIMenu();
		}
	}

	public void cancel() {
		if (viewOption == Settings.viewContent) {
			fileBrowser.cancel();
			editFile = "";
			viewOption = Settings.viewDetail;
			// fileContent = "";
			this.refreshUIMenu();
			refreshButtons();
		}
	}

	public void refresh() {
		refreshFileBrowser();
		isHideLog = true;
	}

	public void updateParams() {

	}

	public void resetParams() {

	}

	public void refreshUIParams() {
		refreshUIParams(null);
	}

	public void refreshUIParams(String message) {
		getParamsPanel().removeAll();
		this.validate();
	}

	public void initData() {

	}

	public MyPane generateDataPane(boolean refreshFlag, String execImmediateExpression, boolean overrideOldTree) {
		// logger.setTextArea(logTextArea);
		try {
			if (refreshFlag) {
				initData();
				refreshUIParams("Generate tree");
			}

		} catch (Exception e) {
			return generateErrorPane(e, "Error when generate temp tables or retreiving data or generate tree");
		}
		return this;
	}

	@Override
	public void refreshConnectionCombo() {
		fileBrowser.setRoot(new File(Settings.getModulesFolder()));
		fileBrowser.refreshTree();
	}

	@Override
	public JPopupMenu createPopupMenu(MyTree tree, TreePath selPath) {
		popupMenu = new JPopupMenu();
		return popupMenu;
	}

}