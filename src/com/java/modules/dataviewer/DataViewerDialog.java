package com.java.modules.dataviewer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import com.java.Main;
import com.java.core.components.MyDialog;
import com.java.core.components.MyLayoutPanel;
import com.java.core.components.MyLinkedMap;
import com.java.core.data.DBParam;
import com.java.core.settings.Settings;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;
import com.java.core.base.MyServiceDialog;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import javax.swing.JTextField;

import java.awt.Color;
import javax.swing.JPasswordField;

import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataViewerDialog extends MyServiceDialog {
	private static final long serialVersionUID = 1L;

	private JTextField textUserName;
	private JTextField textHost;
	private JPasswordField passwordField;
	private JComboBox<String> comboBoxStoredConns, comboBoxFileDef;
	private JTextField txtServiceName;
	private JCheckBox isShowFlatCheckbox;
	JButton btnNewButton, btnEdit, btnEditConn, btnConnectionFile, btnClearFileButton;

	private static List<Map<String, String>> queryDefinitions = null;
	private static List<DBParam> queryParams = null;

	// private JPanel buttonPane;
	private JTextField portTextField;
	private JComboBox<String> connTypeComboBox;
	private JComboBox<String> dbTypeComboBox;
	private static String execImmediateExpression;

	public boolean isShowFlat = Settings.showFlatTree;

	protected static byte[] kb = new byte[16];

	private Boolean autoSaveConnection = Settings.autoSaveConnection;

	public static List<Map<String, String>> getQueryDefinitions() {
		return queryDefinitions;
	}

	public static List<DBParam> getQueryParams() {
		return queryParams;
	}

	public DataViewerDialog() {
		super();
	}

	public void initParams() {
		super.initParams();
		lastFile = Settings.getIniSetting(Settings.TagLASTFILE);
		storedFileLocators = DataViewerService.getFiles(lastFile);
		lastConnection = Settings.getLastConnectionName();
	}

	public void run(String file) {
		DataViewerService.run(file);
	}

	public void run() {

		boolean success;
		String host = textHost.getText();
		String dbType = (String) (dbTypeComboBox.getSelectedItem());
		String serviceName = txtServiceName.getText();
		String userName = textUserName.getText();
		String connectionName = ((String) (comboBoxStoredConns.getModel().getSelectedItem()));
		String port = portTextField.getText();
		String connType = ((String) (connTypeComboBox.getModel().getSelectedItem()));
		String password = passwordField.getText();
		// passwordField.getDocument().remove(0,
		// passwordField.getDocument().getLength());
		isShowFlat = isShowFlatCheckbox.isSelected();

		if (selectedFile == null || selectedFile.isEmpty()) {
			selectedFile = getFileNameFull((String) comboBoxFileDef.getSelectedItem());
		}

		try {
			if (selectedFile == null || !Utils.checkExists(selectedFile)) {
				success = false;
				MyDialog.showException("The definition file '" + selectedFile + "' cannot be found\n");
			} else {
				Main.startThread("Running file: " + selectedFile);
				MyPane dataPane = DataViewerService.generateDataPane(
						dbType, connectionName, host, port, connType, serviceName, userName, password,
						selectedFile, execImmediateExpression, isShowFlat, storedFileLocators);
				dataPane.setFiles(storedFileLocators);
				Main.mainScreen.addPanel(dataPane);
				Main.stopThread();
				success = dataPane != null;
			}
		} catch (Exception e1) {
			MyDialog.showException(e1, "There is exception occured !!!");
			success = false;
		}

		if (success) {
			if (Main.dialog != null) {
				Main.dialog.dispose();
			}

			// Settings.storeLastConnectionName(connectionName);

			Map<String, String> newConnDet = new MyLinkedMap();
			newConnDet.put(Settings.TagDBTYPE, dbType);
			newConnDet.put(Settings.TagCONNECTIONNAME, connectionName);
			newConnDet.put(Settings.TagHOST, host);
			newConnDet.put(Settings.TagPORT, port);
			newConnDet.put(Settings.TagCONNECTIONTYPE, connType);
			newConnDet.put(Settings.TagSERVICENAME, serviceName);
			newConnDet.put(Settings.TagUSERNAME, userName);

			// host, dbType, serviceName, userName, connectionName, connType, port
			if (connectionDetails != null && connectionDetails.size() > 0) {
				boolean newConnectionNameEntered = true;
				for (int t = 0; t < comboBoxStoredConns.getModel().getSize(); t++) {
					if (

					((String) (comboBoxStoredConns.getModel().getElementAt(t))).equals(connectionName)) {
						newConnectionNameEntered = false;
						if (t > connectionDetails.size() - 1)
							break;
						// logger.debug("The used database connection name " + connectionName + "
						// already exists in the definition file");
						if (!(dbType + host + port + connType + serviceName + userName)
								.equals((connectionDetails.get(t).get(Settings.TagDBTYPE) +
										connectionDetails.get(t).get(Settings.TagHOST) +
										connectionDetails.get(t).get(Settings.TagPORT) +
										connectionDetails.get(t).get(Settings.TagCONNECTIONTYPE) +
										connectionDetails.get(t).get(Settings.TagSERVICENAME) +
										connectionDetails.get(t).get(Settings.TagUSERNAME)))) {
							boolean newConnDetailsEntered = true;

							if (newConnDetailsEntered) {
								connectionDetails = DataViewerService.loadConnectionDetails(selectedFile);
							}
						}

						break;
					}
				}

				if (newConnectionNameEntered == true) {
					if (newConnDet.get(Settings.TagCONNECTIONNAME).trim().length() == 0) {
						newConnDet.put(Settings.TagCONNECTIONNAME,
								newConnDet.get(Settings.TagUSERNAME) + " on "
										+ newConnDet.get(Settings.TagSERVICENAME) + " "
										+ (new SimpleDateFormat("hmmss").format(new Date())));
						connectionName = newConnDet.get(Settings.TagCONNECTIONNAME);
					}

					boolean newConnDetailsEntered = true;
					for (Map<String, String> conns : connectionDetails) {
						if ((dbType + host + port + connType + serviceName + userName)
								.equals((conns.get(Settings.TagDBTYPE) +
										conns.get(Settings.TagHOST) +
										conns.get(Settings.TagPORT) +
										conns.get(Settings.TagCONNECTIONTYPE) +
										conns.get(Settings.TagSERVICENAME) +
										conns.get(Settings.TagUSERNAME)))) {
							newConnDetailsEntered = false;

							connectionDetails = DataViewerService.loadConnectionDetails(lastConnectionFile);
							comboBoxStoredConns.removeItem(conns.get(Settings.TagCONNECTIONNAME));
							comboBoxStoredConns.addItem(connectionName);
							comboBoxStoredConns.setSelectedItem(connectionName);//
							break;
						}
					}

					if (newConnDetailsEntered) {
						if (newConnDet.get(Settings.TagCONNECTIONNAME).trim().length() == 0) {
							newConnDet.put(Settings.TagCONNECTIONNAME,
									newConnDet.get(Settings.TagUSERNAME) + " on "
											+ newConnDet.get(Settings.TagSERVICENAME) + " "
											+ (new SimpleDateFormat("hmmss").format(new Date())));
							connectionName = newConnDet.get(Settings.TagCONNECTIONNAME);
						}

						connectionDetails = DataViewerService.loadConnectionDetails(selectedFile);
						comboBoxStoredConns.addItem(connectionName);
						comboBoxStoredConns.setSelectedItem(connectionName);
					}
				}
			} else {
				if (autoSaveConnection) {
					if (newConnDet.get(Settings.TagCONNECTIONNAME).trim().length() == 0)
						newConnDet.put(Settings.TagCONNECTIONNAME,
								newConnDet.get(Settings.TagUSERNAME) + " on "
										+ newConnDet.get(Settings.TagSERVICENAME));
					connectionDetails = DataViewerService.loadConnectionDetails(selectedFile);
					comboBoxStoredConns.removeAllItems();
					comboBoxStoredConns.addItem(newConnDet.get(Settings.TagCONNECTIONNAME));
				}
			}
		}

	}

	public MyPane generatePane() {
		return null;
	}

	public void refreshConnectionCombo() {
		refreshConnectionCombo(lastConnectionFile);
	}

	public void refreshConnectionCombo(String connectionFile) {
		connectionDetails = MyService.getConnectionDetails();
		refreshConnectionCombo(connectionDetails);
	}

	public void refreshConnectionCombo(List<Map<String, String>> connectionDetails) {
		String selectedItem = (String) comboBoxStoredConns.getSelectedItem();
		refreshConnectionCombo(connectionDetails, selectedItem);
	}

	public void refreshConnectionCombo(List<Map<String, String>> connectionDetails, String selectedItem) {
		String[] storedConnections = new String[connectionDetails.size()];
		for (int t = 0; t < connectionDetails.size(); t++) {
			storedConnections[t] = connectionDetails.get(t).get(Settings.TagCONNECTIONNAME);
		}

		DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(storedConnections);
		theModel.insertElementAt("", 0);
		comboBoxStoredConns.setModel(theModel);
		refreshConnectionDetail(selectedItem);
		try {
			if (selectedItem != null && !selectedItem.isEmpty()
					&& !selectedItem.equalsIgnoreCase((String) comboBoxStoredConns.getSelectedItem()))
				comboBoxStoredConns.setSelectedItem(selectedItem);
		} catch (Exception ex) {

		}
	}

	public void refreshConnectionDetail() {
		refreshConnectionDetail((String) comboBoxStoredConns.getSelectedItem());
	}

	public void refreshConnectionDetail(String selectedItem) {
		DBService.updateConnectionComponents(selectedItem, textHost, txtServiceName, portTextField, textUserName,
				connTypeComboBox, dbTypeComboBox, passwordField);
	}

	public void refreshComboFileDef() {
		if (storedFileLocators.size() > 0) {
			DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(storedFileLocators.toArray());
			theModel.insertElementAt("", 0);
			comboBoxFileDef.setModel(theModel);
		}
	}

	public void setConnectionName(String connectionName) {
		lastConnection = connectionName;
		comboBoxStoredConns.setSelectedItem(connectionName);
	}

	public void refreshUIMain() {
		// logger.debug(
		// "************************************ Starting the DB login window
		// ***************************************");
		setTitle(Settings.License + ": Data Viewer");

		textUserName = Utils.createTextField();
		textHost = Utils.createTextField();
		passwordField = Utils.createPasswordField();
		txtServiceName = Utils.createTextField();
		comboBoxStoredConns = Utils.createComboBox();
		portTextField = Utils.createTextField();

		dbTypeComboBox = Utils.createComboBox();
		dbTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (dbTypeComboBox.getSelectedItem() != null
						&& ((String) (dbTypeComboBox.getSelectedItem())).equals("Oracle")) {
					connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Service_Name", "SID" }));
				} else {
					connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Database" }));
				}
			}
		});
		dbTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Oracle", "MySQL" }));

		connTypeComboBox = Utils.createComboBox();
		if (((String) (dbTypeComboBox.getSelectedItem())) != null) {
			if (((String) (dbTypeComboBox.getSelectedItem())).equals("Oracle"))
				connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Service_Name", "SID" }));
			if (((String) (dbTypeComboBox.getSelectedItem())).equals("MySQL"))
				connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "Database" }));
		} else
			connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] { "" }));
		comboBoxFileDef = Utils.createComboBox();

		refreshComboFileDef();

		comboBoxFileDef.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboBoxFileDef.getSelectedItem() != null) {
					File storedLocator = new File(getFileNameFull((String) (comboBoxFileDef.getSelectedItem())));
					if (!storedLocator.exists()) {
						comboBoxFileDef.setBackground(Settings.ColorErrorBG);
						return;
					}

					if ((selectedFile == null || !storedLocator.getAbsolutePath().equals(new File(selectedFile)))
							&& storedLocator.exists()) {
						comboBoxFileDef.setBackground(Settings.ColorEditBG);
						selectedFile = storedLocator.getAbsolutePath();
						lastFile = storedLocator.getAbsolutePath();

						if (!storedFileLocators.contains(selectedFile))
							storedFileLocators.add(selectedFile);

						refreshConnectionCombo(selectedFile);

						// test query def file
						List<Map<String, String>> queryDefinitionsTemp = DataViewerService
								.loadSQLQueryDefinitions(selectedFile);
						if (queryDefinitionsTemp.size() == 0) {

							lastFile = "";
							selectedFile = "";
							comboBoxFileDef.setBackground(Color.RED);
							comboBoxFileDef.setSelectedIndex(0); // empty item
						}
					}
				}
			}
		});

		btnNewButton = Utils.createButton("+");

		// dem FileChoser-Fenster suche
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// System.out.println("Click!");
				JFileChooser c = Utils.createFileChooser("xml;sql", lastDir, Settings.SELECT_FILE);
				int rVal = c.showOpenDialog(DataViewerDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					lastDir = new File(c.getCurrentDirectory().toString());
					Settings.storeIniSettings(Settings.TagLASTDIR, lastDir.getAbsolutePath());
					selectedFile = c.getSelectedFile().getAbsolutePath();

					// if has query definitions then add to ComboFileDef
					List<Map<String, String>> queryDefinitionsTemp = DataViewerService
							.loadSQLQueryDefinitions(selectedFile);
					if (queryDefinitionsTemp.size() > 0) {
						queryDefinitions = queryDefinitionsTemp;
						lastFile = selectedFile;
						if (!storedFileLocators.contains(selectedFile)) {
							storedFileLocators.add(selectedFile);
							comboBoxFileDef.addItem(selectedFile);
						}
						setFileName(comboBoxFileDef, selectedFile, false);
					}

					refreshConnectionCombo(selectedFile);
				}

				if (rVal == JFileChooser.CANCEL_OPTION) {
					lastDir = c.getCurrentDirectory();
				}
			}
		});

		btnClearFileButton = Utils.createButton("-");
		btnClearFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				for (String item : storedFileLocators) {
					if (selectedFile.endsWith(item.replace("...", ""))) {
						storedFileLocators.remove(item);
						refreshComboFileDef();
						break;
					}
				}
			}
		});

		btnEdit = Utils.createButton("Edit");
		btnEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MyDialog.showEditFile((String) comboBoxFileDef.getSelectedItem());
			}
		});

		btnEditConn = Utils.createButton("Edit");
		btnEditConn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				MyDialog.showEditFile(lastConnectionFile);
			}
		});

		comboBoxStoredConns.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				refreshConnectionDetail();
			}
		});

		refreshConnectionCombo();

		btnConnectionFile = Utils.createButton("+");

		// dem FileChoser-Fenster suche
		btnConnectionFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser c = Utils.createFileChooser(Settings.FILETYPE_XML, lastDir, Settings.SELECT_FILE);
				int rVal = c.showOpenDialog(DataViewerDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					refreshConnectionCombo(c.getSelectedFile().getAbsolutePath());
				}
			}
		});

		if (lastFile != null && !lastFile.isEmpty())
			setFileName(comboBoxFileDef, lastFile, false);

		if (lastConnection != null && !lastConnection.isEmpty())
			comboBoxStoredConns.setSelectedItem(lastConnection);

		isShowFlatCheckbox = Utils.createCheckBox(isShowFlat);

		addMainPanel(new MyLayoutPanel()
				.addVGroup()
				.addVGroup("Scripting")
				.addComponent(Utils.createLabel("Template file"), comboBoxFileDef, btnNewButton, btnEdit)
				// .addGap(5)
				.end()
				.addVGroup("Connection")
				.addComponent("Connection", comboBoxStoredConns, btnEditConn)
				.addComponent(Settings.TagDBTYPE, dbTypeComboBox)
				// .addComponent(new Component[] {Utils.createLabel(Settings.TagHOST), textHost,
				// Utils.createLabel(Settings.TagPORT), portTextField})
				.addComponent(Settings.TagHOST, textHost)
				.addComponent(Settings.TagPORT, portTextField)
				.addComponent(connTypeComboBox, txtServiceName)
				.addComponent("DBUser", textUserName)
				.addComponent("Password", passwordField)
				.end()
				.addVGroup("Settings")
				.addComponent(Utils.createLabel("Show Flat tree ?"), isShowFlatCheckbox)
				// .addGap(5)
				.end());
	}

}