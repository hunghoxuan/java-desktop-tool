package com.java.modules.export;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;

import com.java.Main;
import com.java.core.components.MyDialog;
import com.java.core.components.MyLayoutPanel;
import com.java.core.data.DBLookup;
import com.java.core.settings.Settings;
import com.java.core.base.MyPane;
import com.java.core.base.MyService;
import com.java.core.base.MyServiceDialog;
import com.java.modules.db.DBService;
import com.java.core.utils.Utils;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JPasswordField;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;

public class ExportDialog extends MyServiceDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> comboBoxDestConnMode;
	private JComboBox<String> comboBoxModuleList;
	private JComboBox<String> comboBoxSrcConnMode;
	private JComboBox<String> comboBoxIncludeDeletes;
	private JTextField textSrcUserName;
	private JTextField textSrcHost;
	private JPasswordField passwordFieldSrc;
	private JTextField textSrcConnId;
	private JTextField textSrcPort;
	private JCheckBox chckbxAllCHTs;
	private JCheckBox chckbxAllClients;
	private JCheckBox chckbxExportHasDataOnly;
	private JCheckBox chckbxAllApplications;
	JButton btnNewButton, btnSavedFolder, btnEdit, btnEditConn, btnEditConnDest;

	private JTextField textDestUserName;
	private JTextField textDestHost;
	private JTextField textDestPort;
	private JTextField txtDestConnId;
	private JPasswordField passwordFieldDest;
	private JTextField textSrcInstNr;
	private JTextField textDestInstNr;
	private JTextField textDestLocalCurr, textAuditTrail, textFilePrefix;

	JFileChooser fChooser;
	private JCheckBox chckbxRecordDate;
	private JCheckBox chckbxCollectOnlyUsed;
	private JCheckBox chckbxUseBwSequences;
	private JCheckBox chckbxOneScriptFile;
	private JComboBox<String> comboBoxApplyResets, comboBoxStoredConnsSrc, comboBoxStoredConnsDest, comboBoxSavedFolder;
	String savedFolder;
	public List<DBLookup> lookupColumns;

	public ExportDialog() {
		this.initParams();
		this.initUI();
	}

	public void initParams() {
		super.initParams();
		storedFileLocators = ExportService.getStoredFiles(ExportService.FILES_FOLDER, ExportService.FILES_EXTENSION);
		String lastDirTag = Settings.getIniSetting("EXPORT_" + Settings.TagLASTDIR);
		if (lastDirTag != null)
			lastDir = new File(lastDirTag);
		lookupColumns = MyService.loadLookupFields(MyService.lastLookupFile);
	}

	/**
	 * Create the dialog.
	 */
	public void refreshUIMain() {
		setTitle(Settings.License + ": BW Setup Module Export");

		textSrcHost = Utils.createTextField();
		textSrcUserName = Utils.createTextField();
		textSrcPort = Utils.createTextField();

		textDestUserName = Utils.createTextField();
		textDestHost = Utils.createTextField();
		textDestPort = Utils.createTextField();

		comboBoxSrcConnMode = Utils.createComboBox();
		comboBoxSrcConnMode.setModel(new DefaultComboBoxModel<String>(new String[] { "Service_Name", "SID" }));

		comboBoxDestConnMode = Utils.createComboBox();
		comboBoxDestConnMode.setModel(new DefaultComboBoxModel<String>(new String[] { "Service_Name", "SID" }));

		textSrcInstNr = Utils.createTextField(Settings.getLastInstitutionNumber());
		Utils.setFixLength(textSrcInstNr, Settings.columnInstitution);

		textSrcConnId = Utils.createTextField();
		txtDestConnId = Utils.createTextField();
		passwordFieldSrc = Utils.createPasswordField();
		passwordFieldDest = Utils.createPasswordField();
		textDestInstNr = Utils.createTextField();
		Utils.setFixLength(textDestInstNr, Settings.columnInstitution);

		textDestLocalCurr = Utils.createTextField();

		refreshComboFileDef();
		comboBoxModuleList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxModuleList.getSelectedItem() != null && ((String) (comboBoxModuleList.getSelectedItem()))
						.toString().equalsIgnoreCase(Settings.DefaultInstitutionExport)) {
					chckbxAllApplications.setEnabled(true);
					chckbxAllClients.setEnabled(true);
					comboBoxIncludeDeletes
							.setModel(new DefaultComboBoxModel<String>(new String[] { "No Delete Statements",
									"Delete Setup and Clients only", "Delete Setup, Clients and Transactions" }));
					comboBoxApplyResets.setEnabled(true);
				} else {
					chckbxAllApplications.setEnabled(false);
					chckbxAllApplications.setSelected(false);
					chckbxAllClients.setEnabled(false);
					chckbxAllClients.setSelected(false);
					comboBoxIncludeDeletes.setModel(
							new DefaultComboBoxModel<String>(
									new String[] { "No Delete Statements", "Delete as Inserted" }));
					comboBoxIncludeDeletes.setSelectedIndex(1);
					comboBoxApplyResets.setEnabled(false);
					comboBoxApplyResets.setSelectedIndex(0);
					String selectedFile = (String) comboBoxModuleList.getSelectedItem();

					if (!selectedFile.isEmpty() && textAuditTrail != null && selectedFile.contains(".")) {
						textAuditTrail
								.setText("MANUAL_" + selectedFile.substring(selectedFile.lastIndexOf("\\") + 1,
										selectedFile.lastIndexOf(".")));

					}

				}
			}
		});

		btnNewButton = Utils.createButton("+");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// System.out.println("Click!");
				fChooser = Utils.createFileChooser(Settings.FILETYPE_XML, lastDir, Settings.SELECT_FILE);

				int rVal = fChooser.showOpenDialog(ExportDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					lastDir = new File(fChooser.getCurrentDirectory().toString());
					selectedFile = fChooser.getSelectedFile().getAbsolutePath();
					comboBoxModuleList.addItem(selectedFile);
					comboBoxModuleList.setSelectedItem(selectedFile);

					storedFileLocators.add((String) selectedFile);
					Settings.storeIniSettings("EXPORT_" + Settings.TagLASTDIR,
							fChooser.getCurrentDirectory().toString());
				}

			}
		});

		btnEdit = Utils.createButton("Edit");
		btnEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!((String) comboBoxModuleList.getSelectedItem()).equals(Settings.DefaultInstitutionExport))
					MyDialog.showEditFile((String) comboBoxModuleList.getSelectedItem());
			}
		});

		btnEditConn = Utils.createButton("Select");// Den definition file locator kann man auch mit
		btnEditConn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {// Button angeklickt, Fenster geht auf
				enableDestComponents(!comboBoxStoredConnsDest.isEnabled());
			}
		});

		btnSavedFolder = Utils.createButton("+");
		refreshComboSaveFolder();
		btnSavedFolder.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// System.out.println("Click!");
				fChooser = Utils.createFileChooser("", lastDir, Settings.SELECT_FOLDER);

				int rVal = fChooser.showOpenDialog(ExportDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					savedFolder = fChooser.getSelectedFile().toString(); // fChooser.getCurrentDirectory().toString();
					comboBoxSavedFolder.addItem(savedFolder);
					comboBoxSavedFolder.setSelectedItem(savedFolder);
				}

			}
		});

		String[] storedConnectionNames = MyService.getStoredConnectionNames();

		comboBoxStoredConnsSrc = Utils.createComboBox(storedConnectionNames);
		comboBoxStoredConnsSrc.setSelectedItem(this.lastConnection);
		comboBoxStoredConnsSrc.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				refreshConnectionDetailSrc();
			}
		});
		// comboBoxStoredConnsSrc.setSelectedIndex(0);
		refreshConnectionDetailSrc();

		comboBoxStoredConnsDest = Utils.createComboBox(storedConnectionNames);
		comboBoxStoredConnsSrc.setSelectedItem(this.lastConnection);
		comboBoxStoredConnsDest.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				refreshConnectionDetailDest();
			}
		});
		// comboBoxStoredConnsDest.setSelectedIndex(0);
		refreshConnectionDetailDest();

		comboBoxIncludeDeletes = Utils.createComboBox();
		comboBoxIncludeDeletes.setModel(new DefaultComboBoxModel<String>(new String[] { "No Delete Statements",
				"Delete Setup and Clients only", "Delete Setup, Clients and Transactions" }));

		chckbxRecordDate = Utils.createCheckBox("Record Date = System Date");
		chckbxRecordDate.setSelected(true);

		comboBoxApplyResets = Utils.createComboBox();
		comboBoxApplyResets.setModel(new DefaultComboBoxModel<String>(
				new String[] { "No Resets", "Reset Accounts ", "Reset Accounts and Transaction Sequences" }));

		chckbxUseBwSequences = Utils.createCheckBox("Use BW Sequences to Create IDs");
		chckbxUseBwSequences.setEnabled(false);

		chckbxCollectOnlyUsed = Utils.createCheckBox("Collect Used CHT Records Only");
		chckbxCollectOnlyUsed.setEnabled(false);

		chckbxCollectOnlyUsed = Utils.createCheckBox("Collect Used CHT Records Only");
		chckbxCollectOnlyUsed.setEnabled(false);

		chckbxAllCHTs = Utils.createCheckBox("Include CHT Records with Institution Nr 00000000");
		chckbxAllClients = Utils.createCheckBox("Add All Clients");
		chckbxExportHasDataOnly = Utils.createCheckBox("Only show & export non empty datasets");
		chckbxExportHasDataOnly.setSelected(true);

		chckbxAllApplications = Utils.createCheckBox("Add Application Records");
		chckbxOneScriptFile = Utils.createCheckBox("One Script File per DB Table and one Folder per Module ");
		chckbxOneScriptFile.setSelected(true);
		// chckbxOneScriptFile.setEnabled(false);

		textAuditTrail = Utils.createTextField();
		textFilePrefix = Utils.createTextField();
		enableDestComponents(false);
		getMainJPanel().add(new MyLayoutPanel()
				.addVGroup(
						"Select Export Template file")
				.addComponent("Template file", comboBoxModuleList, btnNewButton, btnEdit)
				.end()
				.addHGroup()
				.addVGroup("Export From")
				.addComponent("Conection", comboBoxStoredConnsSrc)
				.addComponent("Host", textSrcHost)
				.addComponent("Port", textSrcPort)
				.addComponent(comboBoxSrcConnMode, textSrcConnId)
				.addComponent("DBUser", textSrcUserName)
				.addComponent("Password", passwordFieldSrc)
				.addComponent("Institution Nr", textSrcInstNr)
				.addGap()
				.end()
				.addVGroup("Export To (default is same of From)")
				.addComponent("Conection", comboBoxStoredConnsDest,
						btnEditConn)
				.addComponent("Host", textDestHost)
				.addComponent("Port", textDestPort)
				.addComponent(comboBoxDestConnMode, txtDestConnId)
				.addComponent("DBUser", textDestUserName)
				.addComponent("Password", passwordFieldDest)
				.addComponent("Institution Nr", textDestInstNr)
				.end()
				.end()
				.addHGroup()
				.addVGroup("Options")

				.addComponent("Deletes", comboBoxIncludeDeletes)
				.addComponent("Reset", comboBoxApplyResets)
				.addComponent("Save Folder", comboBoxSavedFolder, btnSavedFolder)
				.addComponent("File Prefix", textFilePrefix)
				.addComponent("Local Currency", textDestLocalCurr)
				.addComponent("Audit Trail", textAuditTrail)
				.end()
				.addVGroup("Options")
				.addComponent(chckbxAllApplications)
				.addComponent(chckbxAllCHTs)
				.addComponent(chckbxAllClients)
				.addComponent(chckbxExportHasDataOnly)
				.addComponent(chckbxCollectOnlyUsed)
				.addComponent(chckbxUseBwSequences)
				.addComponent(chckbxRecordDate)
				.addComponent(chckbxOneScriptFile)
				.end()

				.end());
	}

	public void run(String file) {
		ExportService.run(file);
	}

	public void run() {
		try {
			theParameters.put("connection", (String) comboBoxStoredConnsSrc.getSelectedItem());
			theParameters.put("destConnection", (String) comboBoxStoredConnsDest.getSelectedItem());

			theParameters.put("url", textSrcHost.getDocument().getText(0, textSrcHost.getDocument().getLength()));
			theParameters.put("service_name",
					textSrcConnId.getDocument().getText(0, textSrcConnId.getDocument().getLength()));
			theParameters.put("port", textSrcPort.getDocument().getText(0, textSrcPort.getDocument().getLength()));
			theParameters.put("connMode", (String) (comboBoxSrcConnMode.getSelectedItem()));
			theParameters.put("user",
					textSrcUserName.getDocument().getText(0, textSrcUserName.getDocument().getLength()));
			theParameters.put("password",
					passwordFieldSrc.getDocument().getText(0, passwordFieldSrc.getDocument().getLength()));
			if (theParameters.get("password") == null || theParameters.get("password").isEmpty())
				theParameters.put("password", Settings.getDefaultDbPassword());
			theParameters.put("instNr",
					textSrcInstNr.getDocument().getText(0, textSrcInstNr.getDocument().getLength()));
			theParameters.put("destInstNr",
					textDestInstNr.getDocument().getText(0, textDestInstNr.getDocument().getLength()));
			theParameters.put("destLocalCurr",
					textDestLocalCurr.getDocument().getText(0, textDestLocalCurr.getDocument().getLength()));
			theParameters.put("replaceFlag", Integer.toString(comboBoxIncludeDeletes.getSelectedIndex()));
			theParameters.put("applyResets", Integer.toString(comboBoxApplyResets.getSelectedIndex()));
			theParameters.put("exportAllCht", chckbxAllCHTs.isSelected() ? "1" : "0");
			theParameters.put("includeApplications", chckbxAllApplications.isSelected() ? "1" : "0");
			theParameters.put("includeAllClients", chckbxAllClients.isSelected() ? "1" : "0");
			theParameters.put("destinationURL",
					textDestHost.getDocument().getText(0, textDestHost.getDocument().getLength()));
			theParameters.put("destPort",
					textDestPort.getDocument().getText(0, textDestPort.getDocument().getLength()));
			theParameters.put("destConnMode", (String) (comboBoxDestConnMode.getSelectedItem()));
			theParameters.put("destinationService_name",
					txtDestConnId.getDocument().getText(0, txtDestConnId.getDocument().getLength()));
			theParameters.put("destinationUser",
					textDestUserName.getDocument().getText(0, textDestUserName.getDocument().getLength()));
			theParameters.put("destinationPassword",
					passwordFieldDest.getDocument().getText(0, passwordFieldDest.getDocument().getLength()));
			if (theParameters.get("destinationPassword") == null || theParameters.get("destinationPassword").isEmpty())
				theParameters.put("destinationPassword", Settings.getDefaultDbPassword());
			theParameters.put("moduleDefFile",
					((String) (comboBoxModuleList.getSelectedItem())).equals(Settings.DefaultInstitutionExport)
							? Settings.DefaultInstitutionExport
							: (String) comboBoxModuleList.getSelectedItem());
			theParameters.put("recDateIsSysDate", chckbxRecordDate.isSelected() ? "1" : "0");
			theParameters.put("ExportHasDataOnly", chckbxExportHasDataOnly.isSelected() ? Settings.TRUE
					: Settings.FALSE);
			theParameters.put("savedFolder", getFileNameFull((String) comboBoxSavedFolder.getSelectedItem()));
			theParameters.put("audit_trail", (String) textAuditTrail.getText());
			theParameters.put("file_prefix", (String) textFilePrefix.getText());

			theParameters.put("oneScriptFile",
					chckbxOneScriptFile.isSelected() ? Settings.TRUE : Settings.FALSE);
			theParameters.put("allCHTs", chckbxAllCHTs.isSelected() ? Settings.TRUE : Settings.FALSE);
			theParameters.put("collectOnlyUsed",
					chckbxCollectOnlyUsed.isSelected() ? Settings.TRUE : Settings.FALSE);
			theParameters.put("allClients", chckbxAllClients.isSelected() ? Settings.TRUE : Settings.FALSE);

			if (theParameters.get("url").equals("") ||
					theParameters.get("user").equals("") ||
					theParameters.get("password").equals("") ||
					theParameters.get("service_name").equals("")) {

				MyDialog.showException("Error!\nSource Database login credentials incomplete!\n\n");
				return;
			}
			if (theParameters.get("instNr").equals("")) {
				MyDialog.showException("Error!\nThe Source Institution Number was not provided!\n\n");
				return;
			}

			// Settings.storeLastConnectionName((String)
			// comboBoxStoredConnsSrc.getSelectedItem());

			MyPane dataPane = ExportService.generateDataPane(theParameters, this.storedFileLocators);
			Main.mainScreen.addPanel(dataPane);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	public void setConnectionName(String connectionName) {
		lastConnection = connectionName;
		comboBoxStoredConnsSrc.setSelectedItem(connectionName);
	}

	private void enableDestComponents(boolean enabled) {
		comboBoxStoredConnsDest.setEnabled(enabled);
		textDestHost.setEnabled(enabled);
		textDestPort.setEnabled(enabled);
		textDestUserName.setEnabled(enabled);
		textDestInstNr.setEnabled(enabled);
		passwordFieldDest.setEnabled(enabled);
		txtDestConnId.setEnabled(enabled);

		if (!enabled) {
			comboBoxStoredConnsDest.setSelectedIndex(0);
			textDestHost.setText("");
			textDestPort.setText("");
			textDestUserName.setText("");
			textDestInstNr.setText("");
			passwordFieldDest.setText("");
			txtDestConnId.setText("");
			btnEditConn.setText("Change");
		} else {
			btnEditConn.setText("Remove");
		}
	}

	public void refreshComboFileDef() {
		if (comboBoxModuleList == null) {
			comboBoxModuleList = Utils.createComboBox();
		}
		if (storedFileLocators == null || storedFileLocators.size() == 0) {
			storedFileLocators.add(Settings.DefaultInstitutionExport);
		} else {
			storedFileLocators.add(0, Settings.DefaultInstitutionExport);
		}

		if (storedFileLocators != null && storedFileLocators.size() > 0) {
			DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(storedFileLocators.toArray());
			theModel.insertElementAt("", 0);
			comboBoxModuleList.setModel(theModel);
		}
	}

	public void refreshComboSaveFolder() {
		if (comboBoxSavedFolder == null)
			comboBoxSavedFolder = Utils.createComboBox();

		storedSavedFolders.add(Settings.getOutputFolder());
		if (storedSavedFolders != null && storedSavedFolders.size() > 0) {
			DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(storedSavedFolders.toArray());
			theModel.insertElementAt("", 0);
			comboBoxSavedFolder.setModel(theModel);
		}
	}

	public void refreshConnectionDetailSrc() {
		refreshConnectionDetailSrc((String) comboBoxStoredConnsSrc.getSelectedItem());
	}

	public void refreshConnectionDetailSrc(String selectedConnectionName) {
		MyService.setupCachedData(null, lookupColumns, selectedConnectionName, null);
		List<String> lookupWords = MyService.getCachedDataAsList(selectedConnectionName, Settings.columnInstitution,
				null);
		Utils.setAutoComplete(textSrcInstNr, lookupWords);

		if (connectionDetails != null) // enth�lt Verbingsdetails, die aus der ausgew�hlten definition file geladen
										// wurden
		{
			Map<String, String> connectionSettings = MyService.getConnectionSettings(selectedConnectionName);
			if (connectionSettings != null) {
				textSrcHost.setText(connectionSettings.get("HOST"));
				textSrcConnId.setText(connectionSettings.get("SERVICENAME"));
				textSrcPort.setText(connectionSettings.get("PORT"));
				textSrcUserName.setText(connectionSettings.get("USERNAME"));
				passwordFieldSrc.setText(connectionSettings.get("PASSWORD"));
				comboBoxSrcConnMode.getModel().setSelectedItem(connectionSettings.get("CONNECTIONTYPE"));
			}
		}
	}

	public void refreshConnectionDetailDest() {
		refreshConnectionDetailDest((String) comboBoxStoredConnsDest.getSelectedItem());
	}

	public void refreshConnectionDetailDest(String selectedConnectionName) {
		MyService.setupCachedData(null, lookupColumns, selectedConnectionName, null);
		List<String> lookupWords = MyService.getCachedDataAsList(selectedConnectionName, Settings.columnInstitution,
				null);
		Utils.setAutoComplete(textDestInstNr, lookupWords);
		Map<String, String> connectionSettings = MyService.getConnectionSettings(selectedConnectionName);

		if (connectionSettings != null) {
			textDestHost.setText(connectionSettings.get("HOST"));
			txtDestConnId.setText(connectionSettings.get("SERVICENAME"));
			textDestPort.setText(connectionSettings.get("PORT"));
			textDestUserName.setText(connectionSettings.get("USERNAME"));
			passwordFieldDest.setText(connectionSettings.get("PASSWORD"));
			comboBoxDestConnMode.getModel().setSelectedItem(connectionSettings.get("CONNECTIONTYPE"));
			// dbTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("DBTYPE"));

		}
	}
}
