package com.rs2.core.base;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

import com.rs2.core.components.MyTree;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;

import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.joran.spi.XMLUtil;

import com.rs2.Main;
import com.rs2.core.MainScreen;
import com.rs2.core.components.MyAutoComplete;
import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyFileBrowser;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.components.MyProgressBar;
import com.rs2.core.components.myeditor.MyTextPane;
import com.rs2.core.components.myjtable.MyJTableEditor;
import com.rs2.core.components.treeview.DataNode;
import com.rs2.core.components.treeview.DataNodePopupMenu;
import com.rs2.core.components.treeview.DataNodeRenderer;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.core.data.DBSchema;
import com.rs2.modules.db.DBService;
import com.rs2.core.logs.LogManager;
import com.rs2.core.logs.Logger;
import com.rs2.core.settings.Settings;
import com.rs2.core.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Color;

//hold local params (data) for each tab.
public class MyPane extends JScrollPane implements ActionListener, IPanel {
    public String ServiceTitle = "";
    public boolean isRunning = false;

    public Map<String, String> theParameters = new HashMap<String, String>();
    public String host;
    public String serviceName;
    public String port;
    public String userName;
    public String password;
    public String connType;
    public String dbType;

    public JTabbedPane tabbedPane;
    public MainScreen mainScreen;
    public JPanel topPanel, menuPanel, mainPanel, paramsPanel;
    public JPanel bottomPanel;
    public JTextComponent txtContent;
    public JTextArea logTextArea; // log Area
    public MyTree theGUITree, theBrowseTree;;
    public JButton btnOk, btnClose, btnReset, btnCancel, btnAdd, btnEdit, btnCollapse, btnClearLog, btnHideLog,
            btnSearch, btnExport, btnDelete, btnSort, btnRefresh, buttonShowFlat, btnOpen;
    public Logger logger;
    public MyTextPane txtSQL;
    JScrollPane scrollPaneSQl;

    public String file;
    public String connectionName = Settings.getLastConnectionName();
    public Connection conn;
    public String viewOption = Settings.viewDetail;
    public Component component;
    public int tabPaneIndex = 0, tabComponentIndex = -1;
    public boolean isCollapesed = false;
    public boolean isHideLog = Settings.showLogPanelAtDefault;
    public String panelId = "";
    public MyProgressBar progressBar;

    public List<String> files, conns;
    public JComboBox<String> comboBoxFileDef, comboBoxStoredConns;
    public String originalFile, originalConnectionName;
    public List<DBQuery> listAllQueryDefinitions;

    public List<DBParam> queryParams;
    public Map<String, String> params = new LinkedHashMap<String, String>();

    public List<DBLookup> lookupColumns;
    public boolean treeExpandMode;
    public boolean overrideOldTree = true;
    public Document xmlDocument;
    public boolean dataSuccess;
    public String isQueryParamsMissing = "";
    public boolean isShowFlat = Settings.showFlatTree;

    public JPopupMenu popupMenu = new JPopupMenu();
    public MyFileBrowser fileBrowser;
    public MyInputDialog inputDialog = new MyInputDialog(this);

    public MyPane() {
        super(Utils.createJPanel(FlowLayout.LEFT));
        initParams();
        initUI(null);
    }

    public MyPane(Component view) {
        super(Utils.createJPanel(FlowLayout.LEFT));
        initParams();
        initUI(view);
    }

    protected MyService service;

    public MyService getService() {
        if (service == null)
            service = new MyService();
        return service;
    }

    public void refreshButton(JButton button) {
        button.setText(getButtonText(button));
        button.revalidate();
        button.repaint();
    }

    public boolean isShowTree() {
        return true;
    }

    public String getButtonText(JButton button) {
        if (button == btnEdit)
            return viewOption == Settings.viewDetail ? "Edit" : "Save";
        if (button == btnOk)
            return !isRunning ? "Run" : "Stop";
        if (button == btnAdd)
            return "Gen code";
        if (button == btnCancel)
            return "Cancel";
        return "";
    }

    public void initParams() {
        getService().initParams();
        lookupColumns = getService().lookupColumns;
    }

    public void initUI(Component view) {
        getLogger();

        JPanel panel = getJPanel();
        // panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setLayout(new BorderLayout());
        // panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(true);

        btnOk = Utils.createButton("Run", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                reload();
            }
        });

        btnAdd = Utils.createButton("Run +", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                add();
            }
        });

        btnCancel = Utils.createButton("Cancel", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                cancel();
            }
        });

        btnReset = Utils.createButton("Reset", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                reset();
            }
        });

        btnClearLog = Utils.createButton("Clear Log", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                clearLog();
            }
        });

        btnHideLog = Utils.createButton(isHideLog ? "Show Log" : "Hide Log", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                hideLog();
            }
        });

        btnSearch = Utils.createButton("Search", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                search();
            }
        });

        btnExport = Utils.createButton("Export/Save", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                export();
            }
        });

        btnRefresh = Utils.createButton("Refresh", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                refresh();
            }
        });

        btnOpen = Utils.createButton("Open", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                open();
            }
        });

        btnSort = Utils.createButton("Sort", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                sort();
            }
        });

        btnDelete = Utils.createButton("Delete", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                delete();
            }
        });

        buttonShowFlat = Utils.createButton(isShowFlat ? "+ Hierachy" : "- Flat", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                isShowFlat = !isShowFlat;
                refreshMainComponent(true);
                ;
                buttonShowFlat.setText(isShowFlat ? "+ Hierachy" : "- Flat");
            }
        });

        btnEdit = Utils.createButton("Edit File", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (viewOption == Settings.viewDetail) {
                    edit();
                    viewOption = Settings.viewContent;
                    refreshButtons();
                } else if (viewOption == Settings.viewContent) {
                    save();
                    viewOption = Settings.viewDetail;
                    refreshButtons();
                }
            }
        });

        btnCollapse = Utils.createButton(isCollapesed ? " + " : " - ", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (isCollapesed)
                    expand();
                else
                    collapse();
                ;

                isCollapesed = !isCollapesed;
                btnCollapse.setText(isCollapesed ? " + " : " - ");
            }
        });

        btnClose = Utils.createButton("Close", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        });

        progressBar = new MyProgressBar();

        initMenuPanel();
        getMenuJpanel().add(progressBar, BorderLayout.AFTER_LINE_ENDS);

        if (getLogTextArea() == null) {
            hideLog();
        } else {
            getBottomJPanel().add(getLogTextArea());
        }

        if (view != null) {
            getMainJPanel().add(view, BorderLayout.PAGE_START);
            component = view;
        }

        refreshUI();
    }

    public void initMenuPanel() {
        getMenuJpanel().add(btnOk);
        getMenuJpanel().add(btnAdd);
        getMenuJpanel().add(btnReset);
        getMenuJpanel().add(btnCollapse);
        getMenuJpanel().add(btnHideLog);
        getMenuJpanel().add(btnClearLog);
        getMenuJpanel().add(btnEdit);
        getMenuJpanel().add(btnCancel);
        getMenuJpanel().add(btnClose);
    }

    public Component getOriginalComponent() {
        return component;
    }

    public JPanel getJPanel() {
        return (JPanel) this.getViewPort().getView();
    }

    public MainScreen getMainScreen() {
        if (mainScreen == null)
            mainScreen = Main.mainScreen;
        return mainScreen;
    }

    public JPanel getTopJPanel() {
        if (topPanel == null) {
            topPanel = Utils.createJPanel(BoxLayout.Y_AXIS, false);
            topPanel.add(getParamsPanel(), BorderLayout.PAGE_START);
            topPanel.add(getSQLJpanel(), BorderLayout.CENTER);
            topPanel.add(getMenuJpanel(), BorderLayout.PAGE_END);
        }
        return (JPanel) this.topPanel;
    }

    public JPanel getParamsPanel() {
        if (paramsPanel == null) {
            paramsPanel = Utils.createJPanel(FlowLayout.LEFT);
        }
        return (JPanel) this.paramsPanel;
    }

    public JPanel getMenuJpanel() {
        if (menuPanel == null) {
            menuPanel = Utils.createButtonsPane();
        }
        return (JPanel) this.menuPanel;
    }

    public JScrollPane getSQLJpanel() {
        if (scrollPaneSQl == null) {
            initSQLTextPane();
        }
        return this.scrollPaneSQl;
    }

    public JPanel getBottomJPanel() {
        if (bottomPanel == null) {
            bottomPanel = Utils.createJPanel(FlowLayout.LEFT);
            setBottomTitle("INFO");
        }
        return (JPanel) this.bottomPanel;
    }

    public JPanel getMainJPanel() {
        if (mainPanel == null) {
            mainPanel = Utils.createJPanel(BoxLayout.Y_AXIS, false);
            mainPanel.setOpaque(true);
        }
        return (JPanel) this.mainPanel;
    }

    public void setBottomTitle(String text) {
        bottomPanel.setBorder(BorderFactory.createTitledBorder(text));
    }

    public void addMainPanel(Component view) {
        addComponent(getMainJPanel(), view);
    }

    public void setMainPanel(Component view) {
        setComponent(getMainJPanel(), view);
    }

    public void addComponent(JPanel container, Component view) {
        if (view == null)
            return;
        view.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        if (view instanceof JScrollPane)
            container.add(view);
        else {
            container.add(new JScrollPane(view));
        }
    }

    public void setComponent(JPanel container, Component view) {
        container.removeAll();

        if (view != null) {
            view.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            if (view instanceof JScrollPane)
                container.add(view);
            else {
                component = view;
                container.add(new JScrollPane(view));
            }
        }

        container.revalidate();
        container.repaint();
    }

    public JViewport getViewPort() {
        return viewport;
    }

    public void reload() {
        run();
    }

    public void updateParams() {

    }

    public void run() {
        run(this.overrideOldTree);
    }

    public void run(String file) {
        run();
    }

    public void startProgressBar() {
        isRunning = true;
        Main.startThread("Performing action of " + getTitle());
        progressBar.start();
    }

    public void stopProgressBar() {
        isRunning = false;
        Main.stopThread();
        progressBar.stop();
    }

    public void run(boolean overrideOldTree) {
        if (isRunning) {
            stopProgressBar();
            refreshButtons();
        } else {
            startProgressBar();
            ;
            Thread queryThread = new Thread() {
                public void run() {
                    try {
                        isRunning = true;
                        refreshButtons();
                        setMainPanel(null);
                        updateParams();
                        generateDataPane(true, "", overrideOldTree);
                        stopProgressBar();
                        refreshButtons();
                    } catch (Exception ex) {
                        stopProgressBar();
                        MyDialog.showException(ex, "Error while performing " + getTitle());
                    }
                }
            };
            queryThread.start();
        }

    }

    public void resetParams() {

    }

    public void reset() {
        progressBar.start();
        Thread queryThread = new Thread() {
            public void run() {
                try {
                    resetParams();
                    generateDataPane(true);
                    progressBar.stop();
                } catch (Exception ex) {
                    MyDialog.showException(ex, "Error while performing " + getTitle());
                }
            }
        };
        queryThread.start();
    }

    public void cancel() {
        if (viewOption != Settings.viewContent)
            return;
        viewOption = Settings.viewDetail;
        setMainPanel(getCurrentTree());
        refreshButtons();
    }

    public void close() {
        getMainScreen().closePane(getTabbedPane(), this.getId());
    }

    public void clearLog() {
        if (logTextArea != null)
            logTextArea.setText("");
        ;
        hideLog();
    }

    public JTextComponent getCurrentTextArea() {
        if (txtContent != null)
            return txtContent;
        if (component instanceof JTextArea)
            return (JTextArea) component;
        for (Component comp : getMainJPanel().getComponents()) {
            if (comp instanceof JTextArea)
                return (JTextArea) comp;
        }
        return null;
    }

    public String getEditFile() {
        return file;
    }

    public void edit() {
        Path filePath = Utils.getFilePath(getEditFile());
        String content = "";
        viewOption = Settings.viewContent;
        content = Utils.getContentFromFile(filePath);
        if (content != null) {
            // content = XMLUtil.prettyXMLFormat(content);
            if (txtContent == null)
                txtContent = Utils.createTextPane(content, true);
            else
                txtContent.setText(content);

            theGUITree = getCurrentTree(); // to reserve after click Cancel
            setMainPanel(txtContent);
            refreshButtons();
        }
    }

    public void save() {
        try {
            if (viewOption == Settings.viewContent) {
                JTextComponent textArea = getCurrentTextArea();
                if (textArea != null)
                    Files.write(Utils.getFilePath(getEditFile()), textArea.getText().getBytes());

                viewOption = Settings.viewDetail;
                refreshButtons();
                // important --> must switch to detail view to refresh !!
            }

            reset();
            ;
        } catch (IOException ex) {
            MyDialog.showException(ex, "Failed to save file: " + Utils.getFilePath(getEditFile()));
        }
    }

    public void collapse() {
        Utils.collapseTree(getCurrentTree());
    }

    public void expand() {
        Utils.expandTree(getCurrentTree());
    }

    public void search() {

    }

    public String getExportFileType() {
        return inputDialog.showComboBox("Please select file type", "Please select file type",
                new String[] { "sql", "dataviewer", "excel" }, "sql");
    }

    public void export() {
        if (listAllQueryDefinitions == null || listAllQueryDefinitions.size() == 0) {
            if (txtContent != null && !txtContent.getText().isEmpty()) {
                export(txtContent.getText());
                return;
            }
        }

        String fileType = getExportFileType();
        export(fileType);

        this.viewOption = Settings.viewDetail;
        refreshButtons();
    }

    public void export(String fileType) {
        if (fileType.equalsIgnoreCase("sql")) {
            Utils.exportListDBQueryToSQL(listAllQueryDefinitions);
        } else if (fileType.equalsIgnoreCase("excel")) {
            Utils.exportListDBQueryToExcel(listAllQueryDefinitions);
        } else {
            String content = fileType.trim();
            if (file != null && !file.isEmpty() && !file.equals("null")) {
                String outputFile = Settings.getOutputFolder() + "\\sqls\\" + Utils.getFileName(file) + "_"
                        + Utils.showDate("yyyyMMdd_HHmmss") + ".sql";
                JFileChooser fileChooser = Utils.createFileChooser("sql", Settings.getOutputFolder() + "\\sqls\\",
                        Settings.SELECT_SAVE);
                fileChooser.setSelectedFiles(new File[] { new File(outputFile) });
                outputFile = Utils.selectFileFromChooser(fileChooser, Settings.getOutputFolder() + "\\sqls\\",
                        Settings.SELECT_SAVE);

                if (outputFile != null && !outputFile.isEmpty()) {
                    Utils.saveFile(outputFile, content);
                }
            } else {
                Utils.saveFile(content);
            }
        }
    }

    public void delete() {

    }

    public void sort() {

    }

    public void refresh() {

    }

    public void open() {

    }

    public void open(String folder, String extension, boolean execute) {

    }

    public void open(String folder) {

    }

    public MyTree getCurrentTree() {
        if (theGUITree != null)
            return theGUITree;

        if (component != null && component instanceof MyTree)
            return (MyTree) component;

        return theGUITree;
    }

    public Component generateMainComponent() {
        return generateMainComponent(this.overrideOldTree);
    }

    public Component generateMainComponent(boolean overrideOldTree) {
        if (this.isShowTree()) {
            return generateTheTree(overrideOldTree);
        } else {
            return generateTheList(overrideOldTree);
        }
    }

    public void refreshMainComponent(boolean overrideOldTree) {
        component = generateMainComponent(overrideOldTree);
        if (component instanceof MyTree)
            theGUITree = (MyTree) component;
        setMainPanel(component);
        // if (overrideOldTree)
        // setMainPanel(component);
        // else
        // addMainPanel(component);
    }

    public void add() {
        run(false);
    }

    public void hideLog() {
        showLog(isHideLog);
    }

    public void showLog(boolean showLog) {
        isHideLog = !showLog;
        getBottomJPanel().setVisible(!isHideLog);
        btnHideLog.setText(isHideLog ? "Show Log" : "Hide Log");
        btnClearLog.setVisible(!isHideLog);
        getBottomJPanel().revalidate();
        getBottomJPanel().repaint();
    }

    // prepare data params before executing
    public void initData() {
        generateListDBQuery();
    }

    public void generateListDBQuery() {
        listAllQueryDefinitions = new LinkedList<DBQuery>();
    }

    public void refreshLayout() {
        getJPanel().add(getTopJPanel(), BorderLayout.PAGE_START);
        getJPanel().add(getMainJPanel(), BorderLayout.CENTER);
        getJPanel().add(getBottomJPanel(), BorderLayout.PAGE_END);
        getParamsPanel().setVisible(false);
        getSQLJpanel().setVisible(false);
        getBottomJPanel().setVisible(!isHideLog);
    }

    public void refreshUI() {
        refreshLayout();
        refreshUIMenu();
        refreshUIMain();
    }

    public void refreshUIMenu() {
        btnCollapse.setVisible(false);
        btnAdd.setVisible(false);
        btnClose.setVisible(true);
        btnOk.setVisible(false);
        btnReset.setVisible(false);
        btnEdit.setVisible(false);
        btnCancel.setVisible(false);
        btnCollapse.setVisible(false);
        // btnOpen.setVisible(false);
        btnClearLog.setVisible(!isHideLog);
    }

    public void refreshButtons() {
        btnAdd.setVisible(!isRunning && !isEditing());
        btnCollapse.setVisible(!isRunning && !isEditing());
        btnReset.setVisible(!isRunning && !isEditing());
        btnClearLog.setVisible(!isEditing());
        btnExport.setVisible(!isRunning && !isEditing());
        btnSearch.setVisible(!isRunning && !isEditing());
        buttonShowFlat.setVisible(!isRunning && !isEditing());
        btnEdit.setEnabled(!isRunning);
        btnCancel.setEnabled(!isRunning);
        btnCancel.setVisible(isEditing());
        btnOk.setVisible(!isEditing());
        btnOpen.setEnabled(!isRunning);

        // buttonShowFlat.setEnabled(!isRunning && !isEditing());

        refreshButton(btnOk);
        refreshButton(btnEdit);
    }

    public boolean isEditing() {
        return viewOption.equalsIgnoreCase(Settings.viewContent);
    }

    public void refreshUIMain() {

    }

    public void setTitle(String title) {
        setId(title);
        if (tabComponentIndex > -1 && this.tabbedPane != null
                && tabComponentIndex < this.tabbedPane.getComponentCount())
            this.tabbedPane.setTitleAt(tabComponentIndex, title);
    }

    public String getTitle() {
        return getId();
    }

    public String getId() {
        if (panelId != null && !panelId.isEmpty())
            return panelId;
        return getIdString(file, connectionName);
    }

    public void setId(String value) {
        panelId = value;
    }

    public JTabbedPane getTabbedPane() {
        if (tabbedPane != null)
            return tabbedPane;
        return MainScreen.currentTabbedPane;
    }

    public static String getIdString(String file, String connectionName) {
        if (file == null)
            file = "";
        if (connectionName == null)
            connectionName = "";
        if (file.isEmpty() && connectionName.isEmpty())
            return "";
        return (new File(file)).getName().toLowerCase() + ":" + connectionName.trim().toLowerCase();
    }

    public MyPane generateErrorPane(Exception e, String message) {
        return generateErrorPane(e, message, false);
    }

    public MyPane generateErrorPane(Exception e, String message, boolean createNewPane) {
        Main.stopThread();
        if (!createNewPane) {
            setMainPanel(Utils.createErrorTextArea(e, message));
            return null;
        } else {
            MyPane errorPane = Utils.createErrorPane(e, message);
            errorPane.setId(this.getId());
            return errorPane;
        }
    }

    public MyPane generateMessagePane(String message) {
        return generateMessagePane(message, false);
    }

    public MyPane generateMessagePane(String message, boolean createNewPane) {
        Main.stopThread();
        if (!createNewPane) {
            setMainPanel(Utils.createMessageTextArea(message));
            return null;
        } else {
            MyPane pane = Utils.createDataPaneFromString(message, false);
            // setMainPanel(pane);
            // return this;
            pane.setId(this.getId());
            return pane;
        }
    }

    public MyPane generateDataPane(boolean refreshFlag) {
        return generateDataPane(refreshFlag, "", true);
    }

    public MyPane generateDataPane(boolean refreshFlag, String execImmediateExpression, boolean overrideOldTree) {
        return generateDataPane(listAllQueryDefinitions, overrideOldTree);
    }

    public MyPane generateDataPane(Map<String, List<DBQuery>> listAllQueryDefinitions, boolean overrideOldTree) {
        return generateDataPane(new LinkedList<DBQuery>(), overrideOldTree);
    }

    public MyPane generateDataPane(List<DBQuery> listAllQueryDefinitions, boolean overrideOldTree) {
        try {

            refreshMainComponent(overrideOldTree);
            setTitle(getId()); // refresh tab title
            scrollTop();
            return this;

        } catch (Exception e) {
            return generateErrorPane(e, "Error when generate temp tables or retreiving data or generate tree");
        }
    }

    public void scrollTop() {
        getViewport().setViewPosition(new Point(0, 0));
    }

    public void scrollBottom() {
        getLogTextArea().setCaretPosition(getLogTextArea().getDocument().getLength());
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getConnectionNames() {
        return getService().getConnectionNames();
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
        if (comboBoxStoredConns != null)
            comboBoxStoredConns.setSelectedItem(connectionName);
        initConnection();
    }

    public boolean isConnectionChanged() {
        String connectionNameTmp = comboBoxStoredConns != null ? (String) comboBoxStoredConns.getSelectedItem() : "";
        if (connectionNameTmp == null)
            connectionNameTmp = "";
        boolean needConnection = false;
        if (!connectionNameTmp.equalsIgnoreCase(connectionName) && !connectionNameTmp.isEmpty()) {
            connectionName = connectionNameTmp;
            needConnection = true;
        }
        return needConnection;
    }

    public Connection initConnection() {
        if (Main.isStartup)
            return null;
        return initConnection(connectionName);
    }

    public Map<String, String> getConnectionSettings() {
        return getConnectionSettings(connectionName);
    }

    public void updateConnectionSettings() {
        updateConnectionSettings(getConnectionSettings());
    }

    public void updateConnectionSettings(Map<String, String> connectionDetails) {
        if (connectionDetails == null)
            return;
        host = connectionDetails.get("HOST");
        serviceName = connectionDetails.get("SERVICENAME");
        port = connectionDetails.get("PORT");
        userName = connectionDetails.get("USERNAME");
        connType = connectionDetails.get("CONNECTIONTYPE");
        dbType = connectionDetails.get("DBTYPE");
        password = connectionDetails.get("PASSWORD");
    }

    public Map<String, String> getConnectionSettings(String connectionName) {
        return getService().getConnectionSettings(connectionName);
    }

    public Connection initConnection(String connectionName) {

        boolean needConnection = false;

        Map<String, String> connectionDetails = getConnectionSettings(connectionName);

        if (connectionDetails != null) {
            if (!connectionDetails.get("HOST").equalsIgnoreCase(host)
                    || !connectionDetails.get("SERVICENAME").equalsIgnoreCase(serviceName)
                    || !connectionDetails.get("PORT").equalsIgnoreCase(port)
                    || !connectionDetails.get("CONNECTIONTYPE").equalsIgnoreCase(connType)) {
                updateConnectionSettings(connectionDetails);
                needConnection = true;
            }
        }

        if (connectionName != null && !connectionName.isEmpty()) {
            try {
                if (needConnection == true || conn == null || conn.isClosed()) {
                    conn = DBService.getNewConnection(
                            connectionName, dbType, serviceName, host, port, connType, userName, password,
                            "");
                    if (conn != null && !conn.isClosed()) {
                        Main.connection = conn;
                        Main.connectionName = connectionName;
                        // Settings.storeLastConnectionName(connectionName);
                    } else {
                        this.connectionName = "";
                        if (comboBoxStoredConns != null)
                            comboBoxStoredConns.setSelectedItem(null);
                    }
                }
            } catch (SQLException ex) {
                MyDialog.showException(ex, "Could not connect to database: " + connType);
                conn = null;
            }
        }

        return conn;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed())
                conn = initConnection();
        } catch (SQLException ex) {
            LogManager.getLogger().error(ex);
            conn = initConnection();
        }
        return conn;
    }

    public void initComboFiles() {
        initComboFiles(getParamsPanel());
    }

    public void initComboFiles(JPanel panel) {
        files = getFiles();
        if (files != null && !files.isEmpty()) {
            comboBoxFileDef = Utils.createComboBox(files, getFileName(file));
            panel.add(comboBoxFileDef);
        }
    }

    public void initComboConns() {
        initComboConns(getParamsPanel());
    }

    public void initComboConns(JPanel panel) {
        conns = getConnectionNames();
        if (connectionName == null)
            connectionName = "";
        if (conns != null && !conns.isEmpty()) {
            comboBoxStoredConns = Utils.createComboBox(conns, connectionName);
            if (connectionName != null && !connectionName.isEmpty())
                refreshConnectionCombo();

            comboBoxStoredConns.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent arg0) {
                    if (connectionName == null
                            || !connectionName.equalsIgnoreCase((String) comboBoxStoredConns.getSelectedItem())) {
                        refreshConnectionCombo();
                        connectionName = (String) comboBoxStoredConns.getSelectedItem();
                    }
                }
            });

            comboBoxStoredConns.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (connectionName != null
                            && connectionName.equalsIgnoreCase((String) comboBoxStoredConns.getSelectedItem())) {
                        try {
                            if (conn == null || conn.isClosed())
                                refreshConnectionCombo();
                        } catch (SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            });
            panel.add(comboBoxStoredConns);
        }
    }

    public void refreshConnectionCombo() {
        // refreshConnectionCombo((String)comboBoxStoredConns.getSelectedItem());
    }

    public void refreshConnectionCombo(List<Map<String, String>> connectionDetails) {
        String selectedConn = (String) comboBoxStoredConns.getSelectedItem();
        refreshConnectionCombo(connectionDetails, selectedConn);
    }

    public void refreshConnectionCombo(List<Map<String, String>> connectionDetails, String selectedConn) {
        String[] storedConnections = new String[connectionDetails.size()];
        for (int t = 0; t < connectionDetails.size(); t++) {
            storedConnections[t] = connectionDetails.get(t).get(Settings.TagCONNECTIONNAME);
        }

        DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(storedConnections);
        theModel.insertElementAt("", 0);
        comboBoxStoredConns.setModel(theModel);
        refreshConnectionDetail(selectedConn);
    }

    public void refreshConnectionDetail() {
        refreshConnectionDetail(connectionName);
    }

    public void refreshConnectionDetail(String selectedItem) {
        try {
            comboBoxStoredConns.setSelectedItem(selectedItem);
        } catch (Exception ex) {
            // comboBoxStoredConns.setSelectedIndex(0);
        }
    }

    public String getFileDescription(String file, String connectionName, List<DBParam> queryParams) {
        String result = "[" + getService().getDisplayKeyValue(new File(file).getName(), connectionName) + "] ";
        if (queryParams != null && !queryParams.isEmpty()) {
            result = result + "";
            for (DBParam param : queryParams) {
                result = result + getService().getDisplayKeyValue(param.getKey(), param.getValue()) + ",";
            }
        }

        return result.substring(0, result.length() - 1).toUpperCase();
    }

    public List<DBParam> getQueryParamsFromSQLs(String sql) {
        List<DBParam> tmp = getQueryParamsFromSQLs(sql, queryParams);
        if (tmp != null)
            queryParams = tmp;
        return queryParams;
    }

    public List<DBParam> getQueryParamsFromSQLs(String sql, List<DBParam> queryParams) {
        return DBService.getQueryParamsFromSQLs(sql, queryParams, params);
    }

    public void addParam(String label, Component comp) {
        addParam(getParamsPanel(), label, comp);
    }

    public void addParam(String label, String text) {
        if (true) {
            List<String> lookupWords = getService().getCachedDataAsList(connectionName,
                    DBService.getDBFieldFromParamName(label),
                    queryParams);
            addParam(label, Utils.createTextField(text, label, lookupWords));
        }
    }

    public void addParam(JPanel panel, String label, Component comp) {
        panel.add(Utils.createLabel(Utils.toTitleCase(label)));
        // panel.add(Utils.createLabel(label));
        panel.add(comp);
        if (comp instanceof JTextField) {
            if (label.startsWith(Settings.paramPrefix)) {
                Utils.setFixLength((JTextField) comp, label);
            }
            ((JTextField) comp).addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    run();
                }
            });

            if (comp instanceof JComboBox) {
                ((JComboBox) comp).addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        for (Component other : panel.getComponents()) {
                            if (other instanceof JTextField && other != comp) {
                                Utils.closeAutoComplete((JTextField) other);
                            }
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                    }
                });
            }

            ((JTextField) comp).addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    for (Component other : panel.getComponents()) {
                        if (other instanceof JTextField && other != comp) {
                            MyAutoComplete otherAutoComplete = Utils.getAutoComplete((JTextField) other);
                            if (otherAutoComplete != null)
                                otherAutoComplete.hide();
                        }
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (((JTextField) comp).getText().contains("\n")) {
                        // Utils.closeAutoComplete((JTextField) comp);
                    }
                }
            });
        }
    }

    public boolean hasData() {
        return true;
    }

    public String getTreeRootLabel() {
        return getId();
    }

    public void treeMouseClicked(MyTree tree, MouseEvent arg0) {
        int selRow = tree.getRowForLocation(arg0.getX(), arg0.getY());//
        TreePath selPath = tree.getPathForLocation(arg0.getX(), arg0.getY());
        if (selRow == -1)
            return;
        if (arg0.getButton() == MouseEvent.BUTTON3) {// if right-clicked, but switched off for the moment
            treeRightClick(tree, arg0);
        } else if (arg0.getClickCount() == 2) {
            treeDoubleClick(tree, arg0);
        } else if (arg0.getClickCount() == 1) {
            treeSingleClick(tree, arg0);
        }
    }

    public JPopupMenu createTreePopuPMenu(MyTree tree, TreePath selPath, List<DBQuery> listAllQueryDefinitions) {
        return (new DataNodePopupMenu(tree, selPath, listAllQueryDefinitions)).popupTreeChoice;
    }

    public void treeKeyReleased(MyTree tree, KeyEvent event) {

    }

    public DataNode generateRootDataNode() {
        return null;
    }

    public TreeCellRenderer getTreeCellRenderer() {
        return new DataNodeRenderer();
    }

    public MyTree generateTheTree() {
        return generateTheTree(this.overrideOldTree);
    }

    public MyTree createJTree(DefaultMutableTreeNode root) {
        MyTree tree = Utils.createJTree(new DefaultTreeModel(root));
        tree.setName("theGUITree");
        tree.myPane = this;
        TreeCellRenderer nodeRenderer = getTreeCellRenderer();
        // nodeRenderer.isShowFlat = true;
        tree.setCellRenderer(nodeRenderer);

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                treeKeyReleased(tree, event);
            }
        });

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeCollapsed(TreeExpansionEvent arg0) {
                MyPane.this.treeCollapsed(tree, arg0);
            }

            public void treeExpanded(TreeExpansionEvent arg0) {
                MyPane.this.treeExpanded(tree, arg0);
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                treeMouseClicked(tree, e);
            }
        });

        tree.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path == null || path.getLastPathComponent() == null) {
                    return;
                }

                Object nodeHoveredOver = path.getLastPathComponent();
                // System.out.println(path.getLastPathComponent());
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

        });

        tree.expandRow(1);
        tree.setSelectionRow(2);
        tree.requestFocus();

        return tree;
    }

    private boolean addedTree = true;

    public MyTree generateTheTree(boolean overrideOldTree) {
        DataNode anfang = generateRootDataNode();
        if (!overrideOldTree && theGUITree != null && anfang != null) {
            DefaultTreeModel model = (DefaultTreeModel) theGUITree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            String rootTitle = (String) root.getUserObject();
            String anfangTitle = (String) anfang.getValues();

            if (addedTree && !anfangTitle.isEmpty()) {

                // LabelNode newroot = new LabelNode(ServiceTitle);
                DefaultMutableTreeNode newroot = new DefaultMutableTreeNode(ServiceTitle);
                newroot.add(root);
                theGUITree = createJTree(newroot);
                // root = newroot;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) anfang.composeTree();
                model.insertNodeInto(node, newroot, 0);

                addedTree = false;
                theGUITree.revalidate();
                theGUITree.repaint();
                Utils.expandTree(theGUITree, 1);
            } else {
                if (anfangTitle.isEmpty()) {
                    for (DataNode child : anfang.getChildren()) {
                        model.insertNodeInto((DefaultMutableTreeNode) child.composeTree(), root, 0);
                    }
                    Utils.expandTree(theGUITree, 1);
                } else {
                    model.insertNodeInto((DefaultMutableTreeNode) anfang.composeTree(), root, 0);
                }
            }

            return theGUITree;
        } else if (anfang != null) {
            theGUITree = createJTree(anfang.composeTree());
            return theGUITree;
        }

        return null;
    }

    public JTabbedPane generateTheList(boolean overrideOldTree) {
        JTabbedPane jTabbedPane = Utils.createJTabbedPane();
        for (DBQuery query : listAllQueryDefinitions) {
            MyJTableEditor editor = new MyJTableEditor(query);
            jTabbedPane.addTab(query.getSQLQueryLabel(),
                    new JScrollPane(editor,
                            VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED));
        }
        return jTabbedPane;
    }

    public void requestFocus() {
        if (tabbedPane != null)
            tabbedPane.requestFocus();
    }

    public void setLog(String log) {
        if (getLogTextArea() == null) {
            getLogger().setText(log);
            requestFocus();
            return;
        }
        getLogTextArea().setText(log);
    }

    public void log(String log) {
        if (getLogTextArea() == null) {
            getLogger().debug(log);
            requestFocus();
            return;
        }
        getLogTextArea().append(log);
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = LogManager.getLogger(getLogTextArea());
            logger.autoScrollBottom = false;
        }
        return logger;
    }

    public void error(Exception e) {
        error(e, "Error occured in " + getTitle());
    }

    public void error(Exception e, String log) {
        MyDialog.showException(e, log);
    }

    public JTextArea getLogTextArea() {
        return null;
    }

    public JTextArea initLogTextArea() {
        if (logTextArea == null) {
            logTextArea = Utils.createJTextArea();
            logTextArea.setForeground(Settings.ColorReadOnlyText);
        }
        return (JTextArea) this.logTextArea;
    }

    private String searchKeyword = "";
    private int currSearchPos = -1;

    public MyTextPane initSQLTextPane() {
        if (txtSQL == null) {
            txtSQL = Utils.createTextPane("", true);

            txtSQL.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            txtSQL.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // TODO Auto-generated method stub
                    // TODO Auto-generated method stub
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                        searchKeyword = MyInputDialog.instance().showTextBox("Search", searchKeyword);
                        if (searchKeyword.length() > 0) {
                            if (currSearchPos >= txtSQL.getText().length() - 1)
                                currSearchPos = -1;
                            int pos = txtSQL.getText().toLowerCase().indexOf(searchKeyword.toLowerCase(),
                                    currSearchPos);
                            if (pos > -1) {
                                txtSQL.setCaretPosition(pos);
                                currSearchPos = pos + 1;

                            }
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
            scrollPaneSQl = new JScrollPane(txtSQL);
            scrollPaneSQl.setPreferredSize(new Dimension(scrollPaneSQl.getWidth(), 250));
        }
        return txtSQL;
    }

    public void setSQL(String sql) {

    }

    public String getFileNameFull(String file) {
        return Utils.getFileNameFull(file);
    }

    public String getFileName(String file) {
        return Utils.getFileNameShort(file);
    }

    public void addDBTreeMenuItem(JPopupMenu menu, String action) {
        if (action.isEmpty() || action.startsWith("---")) {
            menu.add(new JSeparator());
        } else {
            JMenuItem mi2 = new JMenuItem(action);
            mi2.addActionListener(MyPane.this);
            mi2.setActionCommand(action);
            menu.add(mi2);
        }
    }

    public MyTree initTree(final MyTree tree) {
        MouseListener ml = new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null)
                    return;

                int level = selPath.getPathCount();
                if (selRow != -1) {
                    if (e.getClickCount() == 1) {
                        treeSingleClick(tree, e);
                    } else if (e.getClickCount() == 2) { // double click
                        treeDoubleClick(tree, e);
                    }

                    if (SwingUtilities.isRightMouseButton(e)) { // right mouse
                        treeRightClick(tree, e);
                    }
                }
            }
        };
        tree.addMouseListener(ml);
        return tree;
    }

    public MyTree createJTree() {
        MyTree tree = Utils.createJTree(null);
        tree.myPane = this;
        tree = initTree(tree);
        return tree;
    }

    public MyTree createJTree(String name) {
        MyTree tree = createJTree();
        tree.setName(name);
        return tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    public void treeExpanded(MyTree tree, TreeExpansionEvent arg0) {
        // will be overrided
    }

    public void treeCollapsed(MyTree tree, TreeExpansionEvent arg0) {
        // will be overrided
    }

    public void treeActionPerformed(MyTree tree, ActionEvent e) {
        // will be overrided
    }

    public void treeDoubleClick(MyTree tree, MouseEvent e) {
        // will be overrided
    }

    public void treeSingleClick(MyTree tree, MouseEvent e) {
        // will be overrided
    }

    public void treeRightClick(MyTree tree, MouseEvent e) {
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null)
            return;
        Rectangle pathBounds = tree.getUI().getPathBounds(tree, selPath);
        if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
            popupMenu = createPopupMenu(tree, selPath);
            popupMenu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);
        }
        // will be overrided
    }

    public JPopupMenu createPopupMenu(MyTree tree, TreePath selPath) {
        JPopupMenu rightClickOptionMenu = createTreePopuPMenu(tree, selPath, listAllQueryDefinitions);
        return rightClickOptionMenu;
    }

    public void openFileBrowser(String folder) {
        fileBrowser = new MyFileBrowser(folder);
        fileBrowser.autoCopyToClipboard = true;
        setComponent(getMainJPanel(), fileBrowser);
        setBottomTitle("Saved Folder: " + folder);
    }

    public void refreshFileBrowser() {
        if (fileBrowser != null) {
            fileBrowser.refreshTree();
        }
    }

    public String openFile(String file) {
        this.file = Utils.getFileNameFull(file);
        return getFileContent(this.file);
    }

    public String getFileContent(String file) {
        file = Utils.getFileNameFull(file);
        String fileContent = Utils.getContentFromFile(file);
        return fileContent;
    }

    public String getFileContent() {
        return getFileContent(this.file);
    }

    public String getContentMessage(String content) {
        String fileMessage = Utils.substringBetween(content, "/*", "*/").trim();
        if (file != null && !file.isEmpty() && fileMessage != null && !fileMessage.isEmpty()) {
            fileMessage = "Open file: " + file + Settings.lineSeperator + fileMessage;
        }
        return fileMessage;
    }

    public String getContentMessage() {
        return getContentMessage(getFileContent());
    }

    public String getSavedFolder() {
        return getSavedFolder(0);
    }

    public String getSavedFolder(int waitDuration) {
        return Settings.getOutputFolder("");
    }

    public void autoCompleteActionPerformed(String typedWord) {

    }
}