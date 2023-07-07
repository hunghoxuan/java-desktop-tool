package com.java.core.base;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.java.Main;
import com.java.core.MainScreen;
import com.java.core.components.MyDialog;
import com.java.core.components.MyLinkedMap;
import com.java.core.components.MyProgressBar;
import com.java.core.logs.LogManager;
import com.java.core.logs.Logger;
import com.java.core.settings.Settings;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class MyServiceDialog extends JDialog implements ActionListener, IPanel {
	public static Logger logger = LogManager.getLogger();

	private static final long serialVersionUID = 1L;

	public File lastDir = new File("");

	public List<Map<String, String>> connectionDetails = null;
	public List<String> storedFileLocators, storedSavedFolders;
	public String iniFile = Settings.getIniFilePath();

	private boolean uiGenerated = false;
	public JTabbedPane tabbedPane;
	public String panelId;
	public String selectedFile;

	public String warningLevel = "HIGH";
	public String lastFile = "";
	public String lastConnection = Settings.getLastConnectionName();
	public String lastConnectionFile = "";
	public JPanel mainPanel, bottomPanel, menuPanel;
	public JButton btnOk, btnClose, btnSave, btnClear;
	public JTextArea logTextArea;
	public MainScreen mainScreen;
	public MyProgressBar progressBar;
	public JScrollPane contentPane;
	public Map<String, String> theParameters = new MyLinkedMap();

	public MyServiceDialog() {
		super();
		initParams();
		getContentPane().setBackground(Settings.ColorReadOnlyBG);
		setBounds(100, 100, 6000, 4000);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		initUI();
	}

	public JScrollPane getPane() {
		return contentPane;
	}

	// private MyService service;

	// public MyService getService() {
	// if (service == null)
	// service = new MyService();
	// return service;
	// }

	public void initParams() {
		MyService.initParams();
		lastDir = MyService.lastDir;
		lastFile = MyService.lastFile;
		connectionDetails = MyService.getConnectionDetails();
		storedSavedFolders = new LinkedList<String>();
		lastConnectionFile = MyService.lastConnectionFile;
	}

	public List<String> getFiles() {
		return storedFileLocators;
	}

	public void initUI() {
		if (uiGenerated)
			return;

		logger = LogManager.getLogger(getLogTextArea());
		logger.autoScrollBottom = false;

		if (btnOk == null) {
			btnOk = Utils.createButton("Start", new actionOK());
			btnClose = Utils.createButton("Close", new actionCancel());
			// btnClear = Utils.createButton("Clear Log", new actionClear());
			// progressBar = new MyProgressBar();

			getMenuJpanel().add(btnOk);
			// getMenuJpanel().add(btnClear);
			getMenuJpanel().add(btnClose);
			// getMenuJpanel().add(progressBar, BorderLayout.AFTER_LINE_ENDS);
		}

		// getBottomJPanel().add(Utils.createJScrollPanel(getLogTextArea()));

		refreshUI();
		uiGenerated = true;
	}

	public void refreshLayout() {
		getContentPane().add(getMainJPanel(), BorderLayout.PAGE_START);
		getContentPane().add(getMenuJpanel(), BorderLayout.PAGE_END);
		// getContentPane().add(getBottomJPanel(), BorderLayout.PAGE_END);
		// getPane().add(getMainJPanel());
		// getPane().add(getMenuJpanel());
		// getPane().add(getBottomJPanel());
	}

	public void refreshUI() {
		refreshLayout();
		refreshUIMenu();
		refreshUIMain();
		uiGenerated = true;
	}

	public void refreshUIMenu() {
		btnClose.setVisible(true);
		btnOk.setVisible(true);
	}

	public void refreshUIMain() {

	}

	public void setConnectionName(String connectionName) {
		lastConnection = connectionName;
	}

	public MainScreen getMainScreen() {
		if (mainScreen == null)
			mainScreen = Main.mainScreen;
		return mainScreen;
	}

	public JPanel getMenuJpanel() {
		if (menuPanel == null) {
			menuPanel = Utils.createButtonsPane();
		}
		return (JPanel) this.menuPanel;
	}

	public JPanel getBottomJPanel() {
		if (bottomPanel == null) {
			bottomPanel = Utils.createJPanel(FlowLayout.LEFT);
			bottomPanel.setBorder(BorderFactory.createTitledBorder("INFO"));
		}
		return (JPanel) this.bottomPanel;
	}

	public JPanel getMainJPanel() {
		if (mainPanel == null) {
			mainPanel = Utils.createJPanel(FlowLayout.LEFT);
			mainPanel.setOpaque(true);
		}
		return (JPanel) this.mainPanel;
	}

	public void addMainPanel(Component view) {
		if (view == null)
			return;
		view.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		getMainJPanel().add(view);
	}

	public void setMainPanel(Component view) {
		if (view == null)
			return;
		view.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		getMainJPanel().removeAll();
		getMainJPanel().add(view);
	}

	public JTextArea getLogTextArea() {
		// return MainScreen.logTextArea;
		if (logTextArea == null) {
			logTextArea = Utils.createJTextArea();
			logTextArea.setForeground(Settings.ColorReadOnlyText);
		}
		return (JTextArea) this.logTextArea;
	}

	public void actionPerformed(ActionEvent e) {
		// progressBar.start();
		// Thread queryThread = new Thread() {
		// public void run() {
		// run();
		// progressBar.stop();
		// }
		// };
		// queryThread.start();
	}

	public void run(String file) {
		MyDialog.showMessage("Should implement something here");
	}

	public void run() {
		MyDialog.showMessage("Should implement something here");
	}

	public MyPane generateMainPage() {
		return null;
	}

	public void close() {
		if (tabbedPane != null)
			Main.mainScreen.closePane(tabbedPane, getId());
	}

	public void clear() {
		if (getLogTextArea() != null)
			getLogTextArea().setText("");
	}

	public String getId() {
		if (panelId != null)
			return panelId;
		return getTitle();
	}

	public void setId(String id) {
		panelId = id;
	}

	class actionCancel implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			close();
			;
		}
	}

	class actionClear implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clear();
			;
		}
	}

	class actionOK implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// progressBar.start();
			Thread queryThread = new Thread() {
				public void run() {
					try {
						MyServiceDialog.this.run();
						// progressBar.stop();
					} catch (Exception ex) {
						// progressBar.stop();
						MyDialog.showException(ex, "Error performing action in " + getTitle());
					}
				}
			};
			queryThread.start();
		}
	}

	public void setLog(String log) {
		if (getLogTextArea() == null) {
			getLogger().setText(log);
			requestFocus();
			return;
		}
		getLogTextArea().setText(log);
	}

	public void log(String log) {
		if (getLogTextArea() == null) {
			getLogger().debug(log);
			requestFocus();
			return;
		}
		getLogTextArea().append(log);
	}

	public void error(Exception e) {
		error(e, "Error in " + getTitle());
	}

	public void error(Exception e, String log) {
		MyDialog.showException(e, log);
	}

	public Logger getLogger() {
		if (logger == null) {
			logger = LogManager.getLogger(getLogTextArea());
			logger.autoScrollBottom = false;
		}
		return logger;
	}

	public void setFileName(JComboBox<String> comboBoxFileDef, String file, boolean isSaved) {
		if (comboBoxFileDef != null) {
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBoxFileDef.getModel();

			if (model.getIndexOf(file) != -1)
				comboBoxFileDef.setSelectedItem(file);
			else if (model.getIndexOf(getFileName(file)) != -1)
				comboBoxFileDef.setSelectedItem(getFileName(file));
		}
	}

	public String getFileNameFull(String file) {
		return Utils.getFileNameFull(file);
	}

	public String getFileName(String file) {
		return Utils.getFileNameShort(file);
	}
}
