package com.java.modules.automation;

import com.java.core.components.MyInputDialog;
import com.java.core.components.MyLayoutPanel;
import com.java.core.components.myjtable.MyJTableEditor;
import com.java.core.components.myjtable.MyJTableModel;
import com.java.core.components.property.PropertiesTreeTableModel;
import com.java.core.settings.Settings;
import com.java.core.base.MyPane;
import com.java.core.base.MyServiceDialog;
import com.java.core.utils.Utils;

import javafx.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Map;

public class AutomationDialog extends MyServiceDialog {
	private static final long serialVersionUID = 1L;

	private JTextField textClientId, textTestFile, textTestId;
	private JButton btnSelectFile;
	MyJTableEditor tableEditor;
	Object[][] data = null;
	String[] columns;

	public AutomationDialog() {
		super();
	}

	public MyPane generatePane() {
		return null;
	}

	public String[] getColumns() {
		if (columns == null)
			columns = new String[] { "step_number", "keyword", "keyword_name", "field_name", "id_type", "id_type_name",
					"identifier", "value" };
		return columns;
	}

	public void run() {

		String cypressFolder = Settings.getCypressFolder(); // "C:\\_Works\\automation-testing\\rs2-testing-framework-cypress\\";

		String clientId = textClientId.getText();
		String testId = textTestId.getText();
		String testCase = textTestFile.getText();
		String content = "";
		if (clientId.length() > 0 && testId.length() > 0) {
			content = Utils.getContentFromFile(cypressFolder + "\\templates\\api.tpl.js");
		} else {
			content = Utils.getContentFromFile(cypressFolder + "\\templates\\excel.tpl.js");
			testId = testCase;
		}

		content = content.replace("{{clientId}}", clientId);
		content = content.replace("{{testId}}", testId);
		Utils.saveFile(cypressFolder + "\\cypress\\e2e\\testcases\\" + (clientId.length() > 0 ? (clientId + "-") : "")
				+ testId + ".cy.js", content);
		Utils.saveFile(cypressFolder + "\\cypress\\e2e\\_test.cy.js", content);

		JComboBox browserType = Utils.createComboBox(new String[] { "chrome", "firefox", "edge" });
		JComboBox runType = Utils.createComboBox(new String[] { "headed", "headless" });
		JComboBox openType = Utils.createComboBox(new String[] { "run", "open" });
		browserType.setSelectedItem("chrome");
		browserType.setName("browser");

		runType.setSelectedItem("headed");
		runType.setName("mode");

		openType.setSelectedItem("run");
		openType.setName("run");

		Map<String, String> result = MyInputDialog.instance().showComponents("Select Settings", "",
				new Component[] { openType, runType, browserType });

		if (result != null) {
			String cmd = "cd " + cypressFolder + " & npx cypress "
					+ (openType.getSelectedItem() != "" ? openType.getSelectedItem() : "open");
			if (openType.getSelectedItem() != "open") {
				cmd += " --" + runType.getSelectedItem() + " --no-exit --browser " + browserType.getSelectedItem()
						+ " --spec **/_test.*";
			}
			Utils.saveFile("cypress.bat", cmd);
			Utils.runCmd("cypress.bat");
		}
	}

	public void refreshUIMain() {
		setTitle(Settings.License + ": Automation");
		String cypressFolder = Settings.getCypressFolder();

		textClientId = Utils.createTextField();
		textTestId = Utils.createTextField();
		textTestFile = Utils.createTextField();
		btnSelectFile = Utils.createButton("...");
		btnSelectFile.addActionListener(this);
		btnSelectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String file = Utils.selectFile("xlsx", cypressFolder + "\\testcases");
				if (file != null) {
					textTestFile.setText(Utils.getFileName(file));
					textTestId.setText("");
					textClientId.setText("");
					data = Utils.readArray2DFromExcel(file, getColumns());
					tableEditor.getTable().setData(data, getColumns());
					tableEditor.setFileName(file);
				}
			}
		});

		tableEditor = new MyJTableEditor(getColumns());
		tableEditor.setComponentSize(850, 500);
		// JComboBox comboBox = new JComboBox(new String[] { "1", "2", "3" });
		// tableEditor.getTable().setColumnEditor("keyword_name", comboBox);

		addMainPanel(new MyLayoutPanel()
				.addVGroup("Automation Portal Test tool")
				.addComponent("Run local Testcase", textTestFile, btnSelectFile)
				.addComponent("Test Steps", tableEditor)
				.addComponent("", new JSeparator())
				.addComponent("Run API (ClientId / TestID)", textClientId, textTestId)
				.end());
	}

}