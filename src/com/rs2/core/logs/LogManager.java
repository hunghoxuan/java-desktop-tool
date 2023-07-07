package com.rs2.core.logs;

import javax.swing.JTextArea;

import com.rs2.core.MainScreen;

public class LogManager {
	public static Logger logger;

	public static Logger getLogger(String str) {
		if (logger == null) {
			logger = new Logger();
			// if (MainScreen.logTextArea != null)
			// MainScreen.logTextArea = Utils.createJTextArea();
			// logger.logTextArea = MainScreen.logTextArea;
		}
		logger.logTextArea = MainScreen.logTextArea;
		return logger;
	}

	public static Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
			// if (MainScreen.logTextArea != null)
			// MainScreen.logTextArea = Utils.createJTextArea();
			// logger.logTextArea = MainScreen.logTextArea;
		}
		logger.logTextArea = MainScreen.logTextArea;
		return logger;
	}

	public static Logger getLogger(JTextArea jtext) {
		if (logger == null)
			logger = new Logger();

		if (jtext != null)
			logger.logTextArea = jtext;
		return logger;
	}
}
