package com.java.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.JTabbedPane;

import com.java.core.logs.LogManager;
import com.java.core.logs.Logger;
import com.java.core.settings.Settings;
import com.java.core.utils.Utils;

public class AboutWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static Logger logger = LogManager.getLogger(AboutWindow.class.getName());

	public AboutWindow() {
		setType(Type.POPUP);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("About " + Settings.License);
		getContentPane().setBackground(Color.WHITE);
		setBounds(100, 100, 1037, 803);
		getContentPane().setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		Collection<String> files = Utils.getFiles(Settings.getDocsFolder(), "html,txt", false);
		if (files.size() > 0) {
			for (String file : files) {
				tabbedPane.addTab(Utils.substringBetween(file, "\\", "."), null, Utils.createJPanel(file, ""),
						null);
			}
		} else {
			tabbedPane.addTab("About", null,
					Utils.createJPanel(Settings.getDocsFolder() + "/about.txt",
							"file://About"),
					null);
			tabbedPane.addTab("License", null,
					Utils.createJPanel(Settings.getDocsFolder() + "/license.txt",
							"file://License"),
					null);
		}
		tabbedPane.setEnabledAt(0, true);

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.WHITE);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	}

	public void showException(String string) {
	}

	public void showInformation(String string) {
	}
}
