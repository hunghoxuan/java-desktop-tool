package com.rs2.core.logs;

import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.rs2.core.settings.Settings;
import com.rs2.core.utils.Utils;

public class Logger {
	JTextPane pane;
	public JTextArea logTextArea;
	public boolean autoScrollBottom = true;

	public void setTextArea(JTextArea txt) {
		logTextArea = txt;
	}

	public void scrollBottom() {
		scrollBottom(autoScrollBottom);
	}

	public void scrollBottom(boolean autoScrollBottom) {
		try {
			if (logTextArea.getDocument().getLength() > Settings.MaxLogLengthDisplay)
				logTextArea.setText("...\n"
						+ logTextArea.getText().substring(
								logTextArea.getDocument().getLength() - Settings.MaxLogLengthDisplay,
								logTextArea.getDocument().getLength()));
		} catch (Exception ex) {

		}
		if (autoScrollBottom)
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
		else
			logTextArea.setCaretPosition(0);
	}

	public boolean isTraceEnabled() {
		return true;

	}

	private String getMessage(String str, String view) {
		str = str.trim();
		if (!str.endsWith(";"))
			str = str + ";";
		if (view != null && !view.isEmpty())
			str = view + "\n" + str + "\n";
		if (!str.endsWith("\n"))
			str = str + "\n";
		return str;
	}

	private String showTime() {
		return "[" + Utils.showDate(new Date(), "HH:mm:ss") + "] ";
	}

	public String error(String str) {
		if (logTextArea == null)
			return null;

		logTextArea.append(showTime() + getMessage(str.replace("\n", "\n   *  "), "ERROR:--------------------------"));
		scrollBottom(true);
		if (Settings.LogTerminal)
			System.out.println(str);
		return logTextArea.toString();
	}

	public String error(Throwable ex) {
		return error(Utils.getExceptionMessage(ex));
	}

	public String error(String title, Throwable ex) {
		return error(title + "\n" + Utils.getExceptionMessage(ex));
	}

	public String warn(String str) {
		return debug(str);
	}

	public void warn(String text, Object... args) {
		debug(String.format(text, args));
	}

	public void error(String text, Object... args) {
		error(String.format(text, args));
	}

	public void trace(String text, Object... args) {
		debug(String.format(text, args));
	}

	public void trace(String text) {
		debug(text);
	}

	public void log(String text, Object... args) {
		debug(String.format(text, args));
	}

	public String log(String str) {
		return debug(str);
	}

	public void debug(String text, Object... args) {
		debug(String.format(text, args));
	}

	public String debug(String str, String view) {
		if (logTextArea == null)
			return null;
		logTextArea.append(showTime() + getMessage(str, view));
		scrollBottom(true);

		if (Settings.LogTerminal)
			System.out.println(str);
		return logTextArea.toString();
	}

	public String debug(String str) {
		return debug(str, "");
	}

	public String setText(String str) {
		if (logTextArea == null)
			return null;
		logTextArea.setText(getMessage(str, ""));
		scrollBottom(true);

		if (Settings.LogTerminal)
			System.out.println(str);
		return logTextArea.toString();
	}
}
