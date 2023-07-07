package com.rs2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;

import com.java.core.components.MyDialog;
import com.java.core.data.DBSchema;
import com.java.core.settings.Settings;
import com.java.core.MainScreen;
import com.java.core.base.MyService;
import com.java.modules.dataviewer.DataViewerService;
import com.java.modules.db.DBService;
import com.java.modules.export.ExportService;
import com.java.core.utils.Utils;

import java.awt.*;

public class Main {
	// global variables
	public static JDialog dialog;
	public static MainScreen mainScreen;
	public static List<String> runningThreads = new LinkedList<String>();
	public static Map<String, List<String>> messages = new LinkedHashMap<String, List<String>>();

	public static Connection connection;
	public static String connectionName;
	public static DBSchema dbSchema;
	public static Set<String> dbTempItems = new HashSet<String>();
	public static boolean isStartup = true;

	public static void main(String[] args) {
		MyService.getAppFolder(); // important
		MyService.initParams();

		if (args == null || args.length == 0) {
			mainScreen = new MainScreen();
			mainScreen.run();
		} else {
			// java -jar exportInst.jar ? oder //java -jar exportInst.jar help
			if (args[0].equals("?") || args[0].toLowerCase().equals("help") ||
					args[0].equals("-?")
					|| args[0].toLowerCase().equals("-help")) {

				System.out.println(
						"Please specify the following parameters separated by blank and in the following order: \nMandatory parameters:\n"
								+
								" 1) dataviewer Port Service Name User Password");
				return;
			}
			if (args[0].equalsIgnoreCase(DataViewerService.ServiceTitle)) {
				DataViewerService.run(Arrays.copyOfRange(args, 1, args.length));
			}
			if (args[0].equalsIgnoreCase(ExportService.ServiceTitle)) {
				ExportService.run(Arrays.copyOfRange(args, 1, args.length));
			}
		}
	}

	public static void closeDialog() {
		if (Main.dialog != null) {
			Main.dialog.dispose();
		} else {
			Main.finish();
		}
	}

	public static JDialog openDialog(Component component) {
		return openDialog(component, "");
	}

	public static JDialog openDialog(Component component, String title) {
		return openDialog(component, title, 600, 500);
	}

	public static JDialog openDialog(Component component, String title, int width, int height) {
		JDialog dialog = Utils.openDialog(component, title, width, height);
		Main.dialog = dialog;
		return dialog;
	}

	// cleanup and exit system
	public static void finish() {
		try {
			if (Main.connection != null && !Main.connection.isClosed()) {
				DBService.dropAllTempTables();
				Main.connection.close();
			}
		} catch (SQLException sq) {
			MyDialog.showException(sq, "DB error occured while tried to close the connection.");
		}
		System.exit(0);
	}

	// if a thread started / running will postpone all exceptions until the thread
	// stops.
	public static void startThread(String threadName) {
		Main.runningThreads.add(threadName);
	}

	public static void stopThread() {
		String thread = Main.currentThread();
		Main.runningThreads.remove(Main.runningThreads.size() - 1);
		if (Main.runningThreads.isEmpty())
			Main.showMessages("Exceptions / messages during the process: " + thread);
	}

	public static String currentThread() {
		if (!Main.runningThreads.isEmpty())
			return Main.runningThreads.get(Main.runningThreads.size() - 1);
		return "";
	}

	public static boolean isThreadRunning() {
		return Main.runningThreads != null && !Main.runningThreads.isEmpty();
	}

	public static void addMessage(Throwable ex, String message) {
		String thread = Main.currentThread();
		if (!Main.messages.containsKey(thread))
			Main.messages.put(thread, new LinkedList<String>());
		Main.messages.get(thread).add(Utils.getExceptionMessage(ex, message));
	}

	public static void addMessage(String ex) {
		String thread = Main.currentThread();
		if (!Main.messages.containsKey(thread))
			Main.messages.put(thread, new LinkedList<String>());
		Main.messages.get(thread).add(ex);
	}

	public static int showMessages(String title) {
		String theWholeMessage = "";
		if (Main.messages.size() > 0) {
			int i = 0;
			for (String thread : Main.messages.keySet()) {
				i += 1;
				theWholeMessage += Settings.lineSeperator + "\n[STEP " + String.valueOf(i) + "] *** "
						+ thread.toUpperCase() + " *** \n";
				for (String ex : Main.messages.get(thread)) {
					theWholeMessage += Settings.lineSeperator + ex;
				}
			}

			Main.messages.clear();
			return MyDialog.showWindow(theWholeMessage, "error", title.toUpperCase());
		}

		return -1;
	}
}