package com.java.core.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.java.Main;
import com.java.core.components.myeditor.MyTextPane;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.core.utils.Utils;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class MyDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static MyDialog dialog = new MyDialog();

	public int maxWidth = Settings.DIALOG_WIDTH;
	public int maxHeight = Settings.DIALOG_HEIGHT;

	public MyProgressBar progressBar;
	MyTextPane txtErrMessage;
	public String file;
	public String action;
	public String type;
	public String content;
	public int selectedOption;
	JButton okButton, cancelButton;
	JPanel buttonPane;

	public MyDialog() {
		super();
		this.getContentPane().setLayout(new BorderLayout());

		setBounds(0, 0, maxWidth, maxHeight);
		Utils.centerOnScreen(this, true);
		action = "";

		buttonPane = Utils.createButtonsPane();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = Utils.createButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (file != null && !file.isEmpty()) {
					Utils.saveFile(file, txtErrMessage.getText());
					file = null;
				}
				action = "OK";
				selectedOption = JOptionPane.OK_OPTION;
				dispose();
			}
		});
		buttonPane.add(okButton);

		cancelButton = Utils.createButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				action = "Cancel";
				selectedOption = JOptionPane.CANCEL_OPTION;
				dispose();
			}
		});
		buttonPane.add(cancelButton);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		txtErrMessage = Utils.createTextPane();
		scrollPane.setViewportView(txtErrMessage);
		txtErrMessage.setCaretPosition(0);
	}

	@Override
	public void dispose() {
		content = txtErrMessage.getText().trim();

		if (dialog != null) {
			dialog.setMessage("");
			dialog.file = null;
			dialog.setVisible(false);
			// dialog = null;
		}
		txtErrMessage.setText("");
		file = null;
		setVisible(false);
		// super.dispose();
	}

	public static MyDialog getDialog() {
		return dialog;
	}

	public static int getSelectedOption() {
		return dialog != null ? dialog.selectedOption : JOptionPane.CANCEL_OPTION;
	}

	public MyDialog(String ErrMessage) {
		this();
		setMessage(ErrMessage);
	}

	public MyDialog(String ErrMessage, String type) {
		this();
		setMessage(ErrMessage, type);
	}

	public void setMessage(String ErrMessage) {
		setMessage(ErrMessage, "info");
	}

	public static String getText1() {
		return dialog != null ? dialog.getText() : "";
	}

	public String getText() {
		if (content != null && !content.isEmpty())
			return content;
		return txtErrMessage != null ? txtErrMessage.getText() : "";
	}

	public void setMessage(String ErrMessage, String type) {
		this.type = type;

		if (txtErrMessage == null) {
			txtErrMessage = Utils.createTextPane();
			txtErrMessage.setCaretPosition(0);
		}

		txtErrMessage.setText(txtErrMessage.getText() + "\n" + ErrMessage + "\n");
		if (type == "error") {
			txtErrMessage.setBackground(Settings.ColorErrorBG);
			txtErrMessage.setEditable(false);
		} else if (type == "info") {
			txtErrMessage.setBackground(Settings.ColorReadOnlyBG);
			txtErrMessage.setEditable(false);
		} else if (type == "edit") {
			txtErrMessage.setBackground(Settings.ColorEditBG);
			txtErrMessage.setEditable(true);
			txtErrMessage.setText(ErrMessage);
		} else if (type == "warning") {
			txtErrMessage.setBackground(Settings.ColorWarning);
			txtErrMessage.setEditable(false);
		} else if (type == "fatal") {
			txtErrMessage.setBackground(Settings.ColorFatal);
			txtErrMessage.setEditable(false);
		} else {
			txtErrMessage.setBackground(Settings.ColorReadOnlyBG);
			txtErrMessage.setEditable(false);
		}
	}

	public void run() {

	}

	public void actionPerformed(ActionEvent e) {
		run();
	}

	public static JTextArea getTxtMessage() {
		return LogManager.getLogger().logTextArea;
	}

	public static int showException(String message, String title) {
		return showWindow(message, "error", title);
	}

	public static int showMessage(String message, String title) {
		return showWindow(message, "info", title);
	}

	public static int showInformation(String content) {
		LogManager.logger.debug(content);
		return JOptionPane.CANCEL_OPTION;
	}

	public static String getText(int response) {
		if (response == JOptionPane.OK_OPTION) {
			return MyDialog.getDialog().getText();
		}
		return null;
	}

	public static String showEdit(String message, String title) {
		return getText(showWindow(message, "edit", title));
	}

	public static String showEdit(String message, String title, JComponent component) {
		addComponent(component);
		return getText(showWindow(message, "edit", title));
	}

	public static String showEdit(String message, String title, String file) {
		return getText(showWindow(message, "edit", title));
	}

	public static int showException(String message) {
		return showWindow(message, "error");
	}

	public static int showFatal(String message) {
		return showWindow(message, "fatal");
	}

	public static int showWarning(String message) {
		return showWindow(message, "warning");
	}

	public static int showMessage(String message) {
		return showWindow(message, "info");
	}

	public static int showEdit(String message) {
		return showWindow(message, "edit");
	}

	public static int showEditFile(String filePath) {
		if (filePath == null || filePath.isEmpty())
			return JOptionPane.CANCEL_OPTION;
		filePath = Utils.getFileNameFull(filePath);
		String content = Utils.getContentFromFile(filePath);
		dialog.file = filePath;
		return showWindow(content, "edit", filePath);
	}

	public static int showException(Throwable ex, String message) {
		String theWholeMessage = Utils.getExceptionMessage(ex, message);
		LogManager.logger.error(theWholeMessage);
		if (Main.isThreadRunning()) {
			Main.addMessage(theWholeMessage);
			return -1;
		}
		return showWindow(theWholeMessage, "error", message);
	}

	public static int showWindow(String theWholeMessage, String type) {
		return showWindow(theWholeMessage, type, "");
	}

	public static int showWindow(String theWholeMessage, String type, String title) {
		return showWindow(theWholeMessage, type, title, Settings.DIALOG_WIDTH, Settings.DIALOG_HEIGHT);
	}

	public static int showWindow(String theWholeMessage, String type, String title, int width, int height) {
		return showWindow(theWholeMessage, type, title, width, height, true);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if (dim.width > maxWidth)
			dim.width = maxWidth;
		if (dim.height > maxHeight)
			dim.height = maxHeight;
		return dim;
	}

	public void addProgressBar() {
		progressBar = new MyProgressBar();
		this.add(progressBar);
	}

	// public static int showWindow(String theWholeMessage, String type, String
	// title, String file) {
	// int response = showWindow(theWholeMessage, type, title, maxWidth, maxHeight);
	// if (dialog != null)
	// dialog.file = file;
	// return response;
	// }

	public static int showWindow(String theWholeMessage, String type, String title, int width, int height,
			boolean isDialog) {
		if (!Settings.errorShowDialog || (Main.isThreadRunning() && type.equalsIgnoreCase("error")))
			return JOptionPane.CANCEL_OPTION;
		try {
			if (dialog == null) {
				dialog = new MyDialog();
			}

			dialog.setMessage(theWholeMessage, type);

			dialog.setTitle(type + ": " + title);

			dialog.setLocationRelativeTo(null);

			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			dialog.setBounds(0, 0, width, height);
			Utils.centerOnScreen(dialog, true);
			if (isDialog)
				dialog.setModal(true);

			dialog.pack();
			dialog.setLocationRelativeTo(null);

			if (dialog.isAlwaysOnTopSupported())
				dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dialog != null)
			return dialog.selectedOption;
		else
			return JOptionPane.CANCEL_OPTION;
	}

	public static int showDialog(String theWholeMessage, String type, String title, int width, int height) {
		return showWindow(theWholeMessage, type, title, width, height, true);
	}

	public static int showDialog(String theWholeMessage, String type, String title) {
		return showWindow(theWholeMessage, type, title, Settings.DIALOG_WIDTH, Settings.DIALOG_HEIGHT, true);
	}

	public static int showDialog(String theWholeMessage, String title) {
		return showWindow(theWholeMessage, "info", title, Settings.DIALOG_WIDTH, Settings.DIALOG_HEIGHT, true);
	}

	public static void addComponent(JComponent component) {
		try {
			if (dialog == null) {
				dialog = new MyDialog();
			}

			dialog.buttonPane.add(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeComponent(JComponent component) {
		try {
			if (dialog == null) {
				return;
			}
			dialog.buttonPane.remove(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeComponents() {
		try {
			if (dialog == null) {
				return;
			}
			for (Component comp : dialog.buttonPane.getComponents()) {
				if (comp instanceof JButton)
					continue;
				dialog.buttonPane.remove(comp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
