package com.java.core.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.java.core.components.MyLayoutPanel;
import com.java.core.logs.LogManager;
import com.java.core.logs.Logger;
import com.java.core.AboutWindow;
import com.java.core.base.MyService;
import com.java.core.utils.Utils;

public class SettingsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(AboutWindow.class.getName());
	MyService MyService = new MyService();

	public SettingsDialog() {
		setType(Type.POPUP);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Settings");
		setBounds(100, 100, 637, 403);
		// getContentPane().setLayout(new BorderLayout());

		MyLayoutPanel settingsPanel = new MyLayoutPanel();

		settingsPanel.addHGroup("Settings");
		settingsPanel.addComponent(Utils.createLabel("Settings"));

		// connection
		JComboBox comboBoxStoredConns = Utils.createComboBox(MyService.getConnectionNames(),
				Settings.getLastConnectionName());

		JTextField textBoxInstitution = MyService.initInstitutionTextField();
		JTextField textBoxCypressFolder = Utils.createTextField(Settings.getCypressFolder(), Settings.TagCYPRESSFOLDER);

		settingsPanel.addComponent(Settings.TagCONNECTION, comboBoxStoredConns);
		settingsPanel.addComponent(Settings.TagINSTITUTION, textBoxInstitution);
		settingsPanel.addComponent(Settings.TagCYPRESSFOLDER, textBoxCypressFolder);
		settingsPanel.end();

		JPanel panel = Utils.createJPanelVertial();
		panel.add(settingsPanel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Settings", null, panel, null);
		tabbedPane.setEnabledAt(0, true);

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.WHITE);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (comboBoxStoredConns.getSelectedItem() != null) {
						Settings.storeLastConnectionName(comboBoxStoredConns.getSelectedItem().toString());
					}
					Settings.storeLastIntsitutionNumber(textBoxInstitution.getText().trim());

					Settings.storeIniSettings(Settings.TagCYPRESSFOLDER, textBoxCypressFolder.getText().trim());
					Settings.cypressFolder = textBoxCypressFolder.getText().trim();

					dispose();
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);

			JButton canelButton = new JButton("Cancel");
			canelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			canelButton.setActionCommand("Cancel");
			buttonPane.add(canelButton);
		}
	}

	public void showException(String string) {
	}

	public void showInformation(String string) {
	}
}
