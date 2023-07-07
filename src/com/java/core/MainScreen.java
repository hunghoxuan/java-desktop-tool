package com.java.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.awt.event.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.java.Main;
import com.java.core.components.MyDialog;
import com.java.core.components.MyTabbedPane;
import com.java.core.components.MyTextAreaPane;
import com.java.core.logs.LogManager;
import com.java.core.logs.Logger;
import com.java.core.settings.Settings;
import com.java.core.settings.SettingsDialog;
import com.java.core.base.MyPane;
import com.java.core.base.MyServiceDialog;
import com.java.modules.automation.AutomationDialog;
import com.java.modules.cardsgen.CardsGenPane;
import com.java.modules.dataviewer.DataPane;
import com.java.modules.dataviewer.DataViewerDialog;
import com.java.modules.dataviewer.DataViewerService;
import com.java.modules.db.DBPane;
import com.java.modules.db.DBService;
import com.java.modules.export.ExportDialog;
import com.java.modules.export.ExportService;
import com.java.modules.files.FilesPane;
import com.java.modules.files.FunctionsFilesPane;
import com.java.modules.files.InwardFilesPane;
import com.java.modules.transgen.TransGenPane;
import com.java.core.utils.Utils;

import java.awt.Component;
import javax.swing.*;

public class MainScreen {
	public static JTextArea logTextArea;
	public static JFrame frame;
	static int tabPaneIndex = JOptionPane.YES_OPTION; // top panne

	public static JTabbedPane tabbedPaneTop = Utils.createJTabbedPane();
	public static JTabbedPane tabbedPaneBottom = Utils.createJTabbedPane();
	public static JTabbedPane currentTabbedPane;
	public static MyTextAreaPane logPane;

	// global variables
	public static Logger logger = LogManager.getLogger(logTextArea);

	public String viewOption;
	public boolean dataSuccess;

	List<Map<String, String>> connectionDetails;
	LinkedList<String> files;

	JSplitPane splitPane;
	boolean splitVertical = true;

	JButton btnSplitPane, btnClearLog;

	public JFrame getFrame() {
		return frame;
	}

	public void setupMenu(final String execImmediateExpression) {
		// Menu Bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Settings.ColorMenuBG);
		getFrame().setJMenuBar(menuBar);
		JMenu menuSettings = Utils.createMenu("Start");
		JMenuItem settingsMenuItem = Utils.createMenuItem("Settings", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Utils.showDialog(new SettingsDialog());
				} catch (Exception e) {
					MyDialog.showException(e, "Error in Settings Dialog");
				}
				return;
			}
		});
		menuSettings.add(settingsMenuItem);

		JMenuItem settingsLookup = Utils.createMenuItem("Lookups", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MyDialog.showEditFile(Settings.getLookupFile());
				return;
			}
		});
		menuSettings.add(settingsLookup);

		menuSettings.add(new JSeparator());
		JMenuItem aboutMenuItem = Utils.createMenuItem("Help & About", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Utils.showDialog(new AboutWindow());
					;

				} catch (Exception e) {
					MyDialog.showException(e, "Error in About Dialog");
				}
				return;
			}
		});
		menuSettings.add(aboutMenuItem);
		JMenuItem exitMenuItem = Utils.createMenuItem("Exit", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Main.finish();
			}
		});

		menuSettings.add(new JSeparator());
		menuSettings.add(exitMenuItem);
		menuBar.add(menuSettings);

		menuBar.add(DBService.createMenu());
		menuBar.add(DataViewerService.createMenu());
		menuBar.add(ExportService.createMenu());

		// Das menuTools
		JMenu menuTools = Utils.createMenu("Tools");
		JMenuItem automationTest = Utils.createMenuItem("Automation", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new AutomationDialog(),
						"Automation");
			}
		});
		menuTools.add(automationTest);

		JMenuItem data360 = Utils.createMenuItem(DBService.ServiceTitle, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new DBPane(),
						DBService.ServiceTitle);
			}
		});
		menuTools.add(data360);

		JMenuItem dataViewer = Utils.createMenuItem(DataViewerService.ServiceTitle, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new DataViewerDialog(),
						DataViewerService.ServiceTitle);
			}
		});
		menuTools.add(dataViewer);

		JMenuItem dataExport = Utils.createMenuItem(ExportService.ServiceTitle, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new ExportDialog(),
						ExportService.ServiceTitle);
			}
		});
		menuTools.add(dataExport);

		JMenuItem checkCards = Utils.createMenuItem(CardsGenPane.ServiceTitle, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new CardsGenPane(), CardsGenPane.ServiceTitle);
			}
		});
		menuTools.add(checkCards);

		JMenuItem transgen = Utils.createMenuItem(TransGenPane.ServiceTitle, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new TransGenPane(), TransGenPane.ServiceTitle);
			}
		});
		menuTools.add(transgen);

		JMenuItem filesBrowser = Utils.createMenuItem("Files", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new FilesPane(), "Files");
			}
		});

		JMenuItem inwardFilesBrowser = Utils.createMenuItem("Inward/Outward", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new InwardFilesPane(), "Inward/Outward");
			}
		});

		JMenuItem spFilesBrowser = Utils.createMenuItem("Stored Functions", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setActivePane(Settings.TopPane);
				addPanel(new FunctionsFilesPane(), "Stored Functions");
			}
		});
		menuTools.add(new JSeparator());
		menuTools.add(filesBrowser);
		menuTools.add(inwardFilesBrowser);
		menuTools.add(spFilesBrowser);
		menuBar.add(menuTools);

		btnSplitPane = Utils.createButton(splitVertical ? " ^ " : " <> ", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				splitVertical = !splitVertical;
				splitPane.setOrientation(splitVertical ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
				splitPane.setDividerLocation(0.8);
				btnSplitPane.setText(splitVertical ? " ^ " : " <> ");
			}
		});
		menuBar.add(btnSplitPane);

		btnClearLog = Utils.createButton("Refresh", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if (findTabPanel(Settings.tabTitleInformation) == null || logTextArea ==
				// null) {
				// setupLogTextarea();
				// }
				// logTextArea.setText("");
				DBService.dropAllTempTables();
				Utils.restartApplication();
			}
		});
		menuBar.add(btnClearLog);
	}

	public boolean run(final String execImmediateExpression) {
		setupUI(execImmediateExpression);
		refreshUI();
		setActivePane(Settings.TopPane);
		splitPane.setDividerLocation(0.8);
		// addPanel(DBService.createPane());
		return true;
	}

	public boolean run() {
		return run("");
	}

	public boolean run(DataPane pane, String execImmediateExpression) {
		setupUI(execImmediateExpression);

		Thread queryThread = new Thread() {
			public void run() {
				if (!checkAddDataPane(pane.getId()))
					return;
				addPanel(pane);
				refreshUI();
			}
		};
		queryThread.start();
		return true;
	}

	public void setupLogTextarea() {
		// if (logTextArea != null)
		// return;

		if (logTextArea == null)
			logTextArea = Utils.createJTextArea();

		logPane = new MyTextAreaPane(logTextArea);
		logPane.tabbedPane = tabbedPaneBottom;
		logPane.setId(Settings.tabTitleInformation);
		tabbedPaneBottom.add(Settings.tabTitleInformation, logPane);
	}

	public void setViewOption(String view) {
		viewOption = view;
		MyPane currentDataPane = getCurrentDataPane();
		if (currentDataPane != null)
			currentDataPane.viewOption = viewOption;
	}

	public void refreshUITab() {
		tabbedPaneTop.setBackground(tabPaneIndex == Settings.TopPane ? Settings.ColorTreeBG : Settings.ColorReadOnlyBG);
		tabbedPaneBottom
				.setBackground(tabPaneIndex == Settings.BottomPane ? Settings.ColorTreeBG : Settings.ColorReadOnlyBG);

		tabbedPaneTop
				.setBorder(tabPaneIndex == Settings.TopPane ? BorderFactory.createLineBorder(Settings.ColorSelectedText)
						: BorderFactory.createEmptyBorder());
		tabbedPaneBottom.setBorder(
				tabPaneIndex == Settings.BottomPane ? BorderFactory.createLineBorder(Settings.ColorSelectedText)
						: BorderFactory.createEmptyBorder());
		getFrame().revalidate();
	}

	public void refreshUIMenu() {

	}

	public void refreshUI() {
		MyPane pane = getCurrentDataPane();
		if (pane != null)
			getFrame().setTitle(Settings.License + " . " + pane.getId() + " . " + getTabName(tabPaneIndex, false) + ":"
					+ viewOption);
		else
			getFrame().setTitle(Settings.License);
		refreshUIMenu();
		refreshUITab();
		getFrame().validate();
	}

	public static JTabbedPane getCurrentTabPanel() {
		currentTabbedPane = tabPaneIndex == Settings.TopPane ? tabbedPaneTop : tabbedPaneBottom;
		return currentTabbedPane;
	}

	public static JTabbedPane findTabPanel(String panelId) {
		int i = findTabPanelIndex(panelId);
		if (i == Settings.TopPane)
			return tabbedPaneTop;
		else if (i == Settings.BottomPane)
			return tabbedPaneBottom;
		return null;
	}

	public static int findTabPanelIndex(String panelId) {
		if (findComponentIndex(tabbedPaneTop, panelId) > -1)
			return Settings.TopPane;

		if (findComponentIndex(tabbedPaneBottom, panelId) > -1)
			return Settings.BottomPane;
		return -1;
	}

	public static int findComponentIndex(JTabbedPane tabbedPane, String panelId) {
		if (tabbedPane == null)
			return -1;

		for (int i = 0; i < tabbedPane.getComponents().length; i++) {
			Component component = tabbedPane.getComponent(i);
			if (component instanceof MyPane && ((MyPane) component).getId().equals(panelId)) {
				return i;
			}
		}

		return -1;
	}

	public static JTabbedPane getCurrentTabPanel(String panelId) {
		JTabbedPane tabbedPane = findTabPanel(panelId);
		return tabbedPane != null ? tabbedPane : currentTabbedPane;
	}

	public MyPane getCurrentDataPane() {
		return getCurrentDataPane(-1);
	}

	public MyPane getCurrentDataPane(int tabIndex) {
		JTabbedPane pane = tabIndex < 0 ? getCurrentTabPanel()
				: (tabIndex == Settings.TopPane ? tabbedPaneTop : tabbedPaneBottom);
		if (pane.getSelectedComponent() instanceof MyPane)
			return (MyPane) pane.getSelectedComponent();

		return null;
	}

	public JTextArea getCurrentTextArea() {
		MyPane pane = getCurrentDataPane();
		if (pane != null && pane.getOriginalComponent() instanceof JTextArea)
			return (JTextArea) pane.getOriginalComponent();

		return null;
	}

	public void AddStringToPanel(String content) {
		AddStringToPanel(content, false);
	}

	public void AddEditableStringToPanel(String content) {
		AddStringToPanel(content, true);
	}

	public void AddStringToPanel(String content, boolean editable) {
		addPanel(Utils.createComponentFromString(content, editable));
	}

	public void addPanel(Component panel) {
		addPanel(panel, "", false);
	}

	// public void addPanel(Component panel, String title) {
	// addPanel(panel, title, false);
	// }

	public void addPanel(Object panel, String title) {
		if (panel instanceof Component)
			addPanel((Component) panel, title, false);
		else if (panel instanceof String)
			addPanel(Utils.createComponentFromString(panel.toString(), false));
	}

	public void addPanel(Component panel, String title, boolean forceConvertToDataPane) {
		Main.isStartup = false;
		JTabbedPane tabbPane = getCurrentTabPanel();

		if (panel instanceof JDialog) {
			if (panel instanceof MyServiceDialog) {
				((MyServiceDialog) panel).tabbedPane = tabbPane;
				((MyServiceDialog) panel).setId(title);
				((MyServiceDialog) panel).setConnectionName(Settings.getLastConnectionName());
			}
			Component component = ((JDialog) panel).getContentPane();

			addPanel(component, title, false);
		} else if (panel instanceof MyPane) {
			((MyPane) panel).tabbedPane = tabbPane;
			((MyPane) panel).setConnectionName(Settings.getLastConnectionName());

			// override existing panel ?

			for (int i = 0; i < tabbPane.getComponents().length; i++) {
				Component component = tabbPane.getComponent(i);
				if (component instanceof MyPane
						&& ((MyPane) component).getId().equals(((MyPane) panel).getId())) {
					if (Settings.overrideTabPanelWithSameTitle) {
						tabbPane.remove(component);
						tabbPane.add(panel, i);
						tabbPane.setTitleAt(i, ((MyPane) panel).getId());
						((MyPane) panel).tabComponentIndex = i;

						tabbPane.setSelectedComponent(panel);
						((MyPane) panel).scrollTop();
						refreshUITab();
						return;
					}
				}
			}

			tabbPane.add(((MyPane) panel).getId(), panel);
			((MyPane) panel).tabComponentIndex = tabbPane.getComponents().length - 1;
			tabbPane.setSelectedComponent(panel);
			((MyPane) panel).scrollTop();
			refreshUITab();
			return;
		} else {
			if (forceConvertToDataPane) {
				MyPane paneContent = new MyPane(panel);
				paneContent.setEnabled(true);
				addPanel(paneContent, title, false);

			} else {
				JPanel container = Utils.createJPanel();
				container.add(panel);

				setActivePane(Settings.TopPane);
				tabbPane = getCurrentTabPanel();
				for (int i = 0; i < tabbPane.getComponents().length; i++) {
					Component component = tabbPane.getComponent(i);
					if (tabbPane.getTitleAt(i).isEmpty() || tabbPane.getTitleAt(i).equalsIgnoreCase(title)) {
						tabbPane.remove(component);
						tabbPane.add(container, i);
						tabbPane.setTitleAt(i, title);
						tabbPane.setSelectedComponent(container);
						refreshUITab();
						return;
					}
				}
				tabbPane.add(title, container);
				tabbPane.setSelectedComponent(container);
				refreshUITab();
			}

			return;
		}
	}

	private boolean isTabbedPane(Object comp) {
		return comp instanceof JTabbedPane || comp instanceof MyTabbedPane || comp instanceof MyTabbedPane;
	}

	public void setupUI(String execImmediateExpression) {
		if (frame != null)
			return;

		if (frame == null) {
			frame = Utils.createJFrame("", 1000, 750);
			getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			Utils.centerOnScreen(frame, true, 0.8);
		}

		getFrame().setVisible(true);

		splitPane = Utils.createSplitPane(Settings.defaultSplitVertical);
		tabbedPaneTop.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (isTabbedPane(e.getSource())) {
					JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
					int tabIndex = tabbedPane == tabbedPaneTop ? Settings.TopPane : Settings.BottomPane;
					if (tabIndex != tabPaneIndex) {
						setActivePane(tabIndex);
						refreshUI();
					}
				}

			}
		});

		tabbedPaneBottom.addChangeListener(tabbedPaneTop.getChangeListeners()[0]);

		tabbedPaneTop.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				// logger.debug("addFocusListener Top" );

				setActivePane(Settings.TopPane);
				refreshUI();
			}
		});

		tabbedPaneBottom.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				// logger.debug("addFocusListener Bottom" );

				setActivePane(Settings.BottomPane);
				refreshUI();
			}
		});

		setupLogTextarea();
		;

		splitPane.setRightComponent(tabbedPaneBottom);
		splitPane.setLeftComponent(tabbedPaneTop);

		getFrame().setContentPane(splitPane);

		setupMenu(execImmediateExpression);
	}

	public void setActivePane(int tabIndex) {
		MyPane currentDataPane = getCurrentDataPane(tabIndex);
		currentTabbedPane = tabIndex == Settings.TopPane ? tabbedPaneTop : tabbedPaneBottom;
		// boolean changedTab = tabPaneIndex != tabIndex;
		tabPaneIndex = tabIndex;
	}

	private String getTabName(int tabIndex, Boolean reserve) {
		if (tabIndex == Settings.TopPane && splitVertical)
			return reserve ? "Right Tab" : " Left Tab";
		else if (tabIndex == Settings.BottomPane && splitVertical)
			return reserve ? "Left Tab" : " Right Tab";
		else if (tabIndex == Settings.TopPane && !splitVertical)
			return reserve ? "Bottom Tab" : " Top Tab";
		else if (tabIndex == Settings.BottomPane && !splitVertical)
			return reserve ? "Top Tab" : " Bottom Tab";
		return "";
	}

	public boolean checkAddDataPane(String id) {
		int tmpIsTop = findTabPanelIndex(id);
		int input;
		String msg;
		if (tmpIsTop < 0) {
			msg = "Do you want to add " + id + " to "
					+ getTabName(tabPaneIndex, false);
			tmpIsTop = tabPaneIndex;
		} else {
			msg = id + " already existed on " + getTabName(tmpIsTop, false)
					+ ". Do you want to override ?";
		}

		input = JOptionPane.showConfirmDialog(null, msg);
		if (input == JOptionPane.CANCEL_OPTION)
			return false;
		if (input == JOptionPane.NO_OPTION)
			setActivePane(tmpIsTop == Settings.TopPane ? Settings.BottomPane : Settings.TopPane);
		else if (input == JOptionPane.YES_OPTION)
			setActivePane(tmpIsTop);
		return true;
	}

	public void closeSelectedPane() {
		closePane(getCurrentTabPanel(), getCurrentTabPanel().getSelectedIndex());
	}

	public void closePane(JTabbedPane tabPane, String paneId) {
		int panelIndex = -1;
		for (int i = 0; i < tabPane.getComponentCount(); i++) {
			if (tabPane.getComponent(i) instanceof MyPane) {
				if (((MyPane) tabPane.getComponent(i)).getId().equalsIgnoreCase(paneId)) {
					panelIndex = i;
					break;
				}
			} else if (tabPane.getTitleAt(i).equalsIgnoreCase(paneId)) {
				panelIndex = i;
				break;
			}
		}
		closePane(tabPane, panelIndex);
	}

	public void closePane(JTabbedPane tabPane, int panelIndex) {
		if (panelIndex > -1) {

			tabPane.remove(panelIndex);
			if (panelIndex <= tabPane.getComponentCount() - 1) {
				tabPane.setSelectedIndex(panelIndex);
			}
			refreshPaneTabIndexes(tabPane);
			refreshUI();
		}
	}

	public void refreshPaneTabIndexes(JTabbedPane tabPane) {
		for (int i = 0; i < tabPane.getComponentCount(); i++) {
			if (tabPane.getComponent(i) instanceof MyPane) {
				((MyPane) tabPane.getComponent(i)).tabComponentIndex = i;
			}
		}
	}

	public void reloadSelectedPane() {
		MyPane pane = getCurrentDataPane();
		if (pane != null) {
			pane.reload();
		}
	}

	public void reloadPane(JTabbedPane tabbedPane, String panelId) {
		for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
			if (tabbedPane.getComponent(i) instanceof MyPane
					&& ((MyPane) tabbedPane.getComponent(i)).getId().equalsIgnoreCase(panelId)) {
				((MyPane) tabbedPane.getComponent(i)).reload();
				break;
			}
		}
	}

	public void setConnectionName(String connectionName) {
		JTabbedPane tabbedPane = currentTabbedPane;
		for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
			if (tabbedPane.getComponent(i) instanceof MyPane) {
				((MyPane) tabbedPane.getComponent(i)).setConnectionName(connectionName);
				break;
			}
		}
	}
}// end class MainScreen
