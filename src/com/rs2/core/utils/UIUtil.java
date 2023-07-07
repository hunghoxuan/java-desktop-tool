package com.rs2.core.utils;

import javax.sql.RowSetInternal;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.rs2.Main;
import com.rs2.core.base.MyCache;
import com.rs2.core.base.MyPane;
import com.rs2.core.base.MyService;
import com.rs2.core.components.MyAutoComplete;
import com.rs2.core.components.MyFileFilter;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.components.MyLayoutPanel;
import com.rs2.core.components.MyToastMessage;
import com.rs2.core.components.MyTree;
import com.rs2.core.components.mycombobox.ComboBoxFilterDecorator;
import com.rs2.core.components.mycombobox.CustomComboRenderer;
import com.rs2.core.components.myeditor.MyTextPane;
import com.rs2.core.components.myjtable.MyJTable;
import com.rs2.core.components.myjtable.MyJTableModel;
import com.rs2.core.components.treeview.DataNode;
import com.rs2.core.components.treeview.MyCellRenderer;
import com.rs2.core.settings.Settings;
import com.rs2.modules.dataviewer.DataPane;
import com.rs2.modules.db.DBService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UIUtil extends CommonUtil {
    public static void sizeColumnsToFit(JTable table) {
        sizeColumnsToFit(table, 5);
    }

    public static void sizeColumnsToFit2(JTable table, int columnMargin, DataNode line) {
        JTableHeader tableHeader = table.getTableHeader();
        FontMetrics lineFontMetrics = table.getFontMetrics(table.getFont());
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());

        // System.out.println(line+" MaxColumnLength: "+line.getMaxColumnLength());
        for (int t = 0; t < table.getRowCount(); t++) {
            for (int tt = 0; tt < table.getColumnCount(); tt++) {
                table.getColumnModel().getColumn(tt).setPreferredWidth(
                        (columnMargin * 2) + line.getMaxColumnLength().get(table.getColumnName(tt))
                                * lineFontMetrics.charsWidth("ABCD1834".toCharArray(), 0, 8) / 8);
                tableHeader.getColumnModel().getColumn(tt).setPreferredWidth(
                        (columnMargin * 2) + line.getMaxColumnLength().get(table.getColumnName(tt))
                                * headerFontMetrics.charsWidth("ABCDabcd1834".toCharArray(), 0, 12) / 12);
            }
        }
    }

    public static void sizeColumnsToFit(JTable table, int columnMargin) {
        JTableHeader tableHeader = table.getTableHeader();
        if (tableHeader == null) {
            return;
        }
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        int[] minWidths = new int[table.getColumnCount()];
        int[] maxWidths = new int[table.getColumnCount()];

        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            int headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));
            minWidths[columnIndex] = headerWidth + columnMargin;
            int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth);
            maxWidths[columnIndex] = Math.max(maxWidth, minWidths[columnIndex]) + columnMargin;
        }
        adjustMaximumWidths(table, minWidths, maxWidths);
        for (int i = 0; i < minWidths.length; i++) {
            if (minWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);
            }
            if (maxWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMaxWidth(maxWidths[i]);
                table.getColumnModel().getColumn(i).setWidth(maxWidths[i]);
                table.getColumnModel().getColumn(i).setMinWidth(maxWidths[i]);
            }
        }
    }

    public static void adjustMaximumWidths(JTable table, int[] minWidths, int[] maxWidths) {
        if (table.getWidth() > 0) {
            // to prevent infinite loops in exceptional situations
            int breaker = 0;
            // keep stealing one pixel of the maximum width of the highest column until we
            // can fit in the width of the table
            while (sum(maxWidths) > table.getWidth() && breaker < 10000) {
                int highestWidthIndex = findLargestIndex(maxWidths);
                maxWidths[highestWidthIndex] -= 1;
                maxWidths[highestWidthIndex] = Math.max(maxWidths[highestWidthIndex], minWidths[highestWidthIndex]);
                breaker++;
            }
        }
    }

    public static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth) {
        int maxWidth = headerWidth;
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        TableCellRenderer cellRenderer = column.getCellRenderer();
        if (cellRenderer == null) {
            cellRenderer = new DefaultTableCellRenderer();
        }
        for (int row = 0; row < table.getModel().getRowCount(); row++) {
            Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
                    table.getModel().getValueAt(row, columnIndex),
                    false,
                    false,
                    row,
                    columnIndex);
            double valueWidth = rendererComponent.getPreferredSize().getWidth();
            maxWidth = (int) Math.max(maxWidth, valueWidth);
        }
        return maxWidth;
    }

    public static int findLargestIndex(int[] widths) {
        int largestIndex = 0;
        int largestValue = 0;
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] > largestValue) {
                largestIndex = i;
                largestValue = widths[i];
            }
        }
        return largestIndex;
    }

    public static int sum(int[] widths) {
        int sum = 0;
        for (int width : widths) {
            sum += width;
        }
        return sum;
    }

    public static MyPane generateDataPane(// Erzeugt und zeigt den EspressoView
            String dbType1, String connectionName1, String host1, String port1,
            String connType1, String serviceName1, String userName1,
            String password1, String file1, final String execImmediateExpression) {
        DataPane pane = new DataPane();
        pane.assignLocalVars(dbType1, connectionName1, host1, port1, connType1, serviceName1, userName1, password1,
                file1, execImmediateExpression);

        return pane.generateDataPane(true);
    }

    public static JTabbedPane createJTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Settings.ColorReadOnlyBG);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        return tabbedPane;
    }

    public static JPanel createJPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Settings.ColorReadOnlyBG);

        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        // panel.setLayout(new GridLayout(20, 1, 0, 12));

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        return panel;
    }

    public static JPanel createJPanelVertial() {
        return createJPanel(FlowLayout.LEADING);
    }

    public static JPanel createJPanelHorizental() {
        return createJPanel(FlowLayout.LEFT);
    }

    public static JPanel createJPanel(int flowLayout) {
        return createJPanel(flowLayout, true);
    }

    public static JPanel createJPanel(int layoutType, boolean isFlowLayout) {
        JPanel panel = createJPanel();
        if (isFlowLayout) {
            FlowLayout layout = new FlowLayout(layoutType);
            layout.setHgap(0);
            layout.setVgap(0);
            panel.setLayout(layout);
        } else
            panel.setLayout(new BoxLayout(panel, layoutType));
        return panel;
    }

    public static JPanel createJPanel(LayoutManager layout) {
        JPanel panel = createJPanel();
        panel.setLayout(layout);
        return panel;
    }

    public static JScrollPane createJScrollPanel() {
        return (JScrollPane) createJScrollPanel(null);
    }

    public static JScrollPane createJScrollPanel(Component component) {
        JScrollPane panel = component == null ? new JScrollPane() : new JScrollPane(component);

        panel.setBackground(Settings.ColorReadOnlyBG);
        panel.setBackground(Color.RED);
        // panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        if (component != null) {
            panel.setViewportView(component);
        }
        panel.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        return panel;
    }

    public static JScrollPane createJPanel(String file, String defaultContent) {
        JEditorPane tutorialTextArea = new JEditorPane();
        try {
            File helpFile = new File(file);
            if (file.toLowerCase().endsWith(".html")) {
                tutorialTextArea.setPage(helpFile.toURI().toURL());
            } else {
                defaultContent = Utils.getContentFromFile(helpFile);
            }
        } catch (IOException e) {
            if (defaultContent.startsWith("file://")) {
                defaultContent = Utils.getContentFromFile(defaultContent.replace("file://", ""));
            }
        }
        if (!defaultContent.isEmpty()) {
            tutorialTextArea.setContentType("text/html");
            if (!defaultContent.toLowerCase().contains("<html>"))
                defaultContent = "<html><body>" + defaultContent.replace("\n", "<br/>") + "</body></html>";
            tutorialTextArea.setText(defaultContent);
        }
        tutorialTextArea.setBounds(0, 0, 1900, 1304);
        tutorialTextArea.setEditable(false);
        tutorialTextArea.setCaretPosition(0);

        return createJScrollPanel(tutorialTextArea);
    }

    public static JFrame createJFrame(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setVisible(true);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        return frame;
    }

    public static JPanel createJPanel(Component[] components, int layoutType, boolean isFlowLayout, String title) {
        JPanel panel = createJPanel(layoutType, isFlowLayout);
        panel.setBorder(BorderFactory.createTitledBorder("INFO"));
        for (Component comp : components) {
            panel.add(comp);
        }
        return panel;
    }

    public static boolean isComboBoxChanged(JComboBox comboBoxStoredConns) {
        return comboBoxStoredConns.getSelectedItem() != null && ((DefaultComboBoxModel) comboBoxStoredConns.getModel())
                .getIndexOf(comboBoxStoredConns.getSelectedItem()) > -1;
    }

    public static boolean setComboValue(JComboBox comboBoxStoredConns, String value) {
        for (int i = 0; i < comboBoxStoredConns.getItemCount(); i++) {
            if (comboBoxStoredConns.getItemAt(i).toString().equalsIgnoreCase(value) || comboBoxStoredConns.getItemAt(i)
                    .toString().startsWith(value + " : ")) {
                comboBoxStoredConns.setSelectedItem(comboBoxStoredConns.getItemAt(i));
                return true;
            }
        }
        // select first none empty item
        for (int i = 0; i < comboBoxStoredConns.getItemCount(); i++) {
            if (!comboBoxStoredConns.getItemAt(i).toString().isEmpty()) {
                comboBoxStoredConns.setSelectedItem(comboBoxStoredConns.getItemAt(i));
                return false;
            }
        }
        return false;
    }

    public static JPanel createButtonsPane(JButton[] buttons) {
        JPanel buttonPane = createButtonsPane();
        boolean isFirst = true;
        for (JButton button : buttons) {
            if (isFirst) {
                isFirst = false;
                button.setBackground(Settings.ColorButtonBG);
            }
            buttonPane.add(button);
        }
        return buttonPane;
    }

    public static JPanel createButtonsPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Settings.ColorButtonBG);
        buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPane.setBorder(BorderFactory.createTitledBorder(""));

        return buttonPane;
    }

    public static JFileChooser createFileChooser() {
        return createFileChooser(null, "", Settings.SELECT_FILE);
    }

    public static JFileChooser createFolderChooser() {
        return createFileChooser(null, "", Settings.SELECT_FOLDER);
    }

    public static JFileChooser createFolderChooser(String path) {
        return createFileChooser(null, path, Settings.SELECT_FOLDER);
    }

    public static String selectFileFromChooser(JFileChooser fChooser, String fileType, String selectFolder) {
        int rVal = fChooser.showOpenDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            String file = fChooser.getSelectedFile().getAbsolutePath();
            if (!selectFolder.equals(Settings.SELECT_FOLDER)) {
                if (!fileType.isEmpty() && !fileType.startsWith("."))
                    fileType = "." + fileType;
                String manyChar = "";
                if (fileType.contains(",")) {
                    manyChar = ",";
                } else if (fileType.contains(";")) {
                    manyChar = ";";
                }
                if (!manyChar.isEmpty()) {
                    String[] arr = fileType.split(manyChar);
                    fileType = arr[0];
                }
                if (!fileType.isEmpty() && !file.contains("."))
                    file = file + fileType;
            }
            return file;
        }
        return "";
    }

    public static String selectFile(String fileType, String pathname, String selectFolder) {
        JFileChooser fChooser = Utils.createFileChooser(fileType, pathname, selectFolder);
        return selectFileFromChooser(fChooser, fileType, selectFolder);
    }

    public static String selectFolder(String pathname) {
        return selectFile(null, pathname, Settings.SELECT_FOLDER);
    }

    public static String selectFile(String pathname) {
        return selectFile(null, pathname, Settings.SELECT_FILE);
    }

    public static String selectFile(String fileType, String pathname) {
        return selectFile(fileType, pathname, Settings.SELECT_FILE);
    }

    public static File openFile(String fileName, String type) {
        File fileDest = new File(fileName);
        if (fileDest.exists() && fileDest.isDirectory()) {
            fileName = Utils.selectFile(type, fileName);
            fileDest = new File(fileName);
        }
        return fileDest;
    }

    public static JFileChooser createFileChooser(String fileType, String pathname, String selectFolder) {
        return createFileChooser(fileType, pathname != null ? new File(pathname) : null, selectFolder);
    }

    public static JFileChooser createFileChooser(String fileType, File lastDir, String selectFolder) {
        JFileChooser c = new JFileChooser();

        c.setAcceptAllFileFilterUsed(false);
        if (lastDir != null) {
            if (!lastDir.exists())
                lastDir.mkdirs();
            c.setCurrentDirectory(lastDir);
        }

        if (fileType != null && !fileType.isEmpty()) {
            MyFileFilter filter = new MyFileFilter(fileType, "");
            c.addChoosableFileFilter(filter);
        }

        if (selectFolder.equals(Settings.SELECT_FOLDER)) {
            c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            c.setDialogTitle("Please select one file, the whole directory will be then selected");
            c.setApproveButtonText("Select folder of current file");
            c.setApproveButtonToolTipText("push the button!");
        } else if (selectFolder.equals(Settings.SELECT_FILE)) {
            c.setFileSelectionMode(JFileChooser.FILES_ONLY);
            c.setDialogTitle("Please select one file");
            c.setApproveButtonText("Select file");
        } else if (selectFolder.equals(Settings.SELECT_SAVE)) {
            c.setFileSelectionMode(JFileChooser.FILES_ONLY);
            c.setDialogTitle("Do you want to save file ?");
            c.setApproveButtonText("Save");

        }

        return c;
    }

    public static JButton createFileChooserButton(JTextComponent txtAppFolder, String fileType,
            String selectFolder) {
        return createFileChooserButton(txtAppFolder, fileType, selectFolder, null);
    }

    public static JButton createFolderChooserButton(JTextComponent txtAppFolder, String iniXmlTag) {
        return createFileChooserButton(txtAppFolder, "", Settings.SELECT_FOLDER, iniXmlTag);
    }

    public static JButton createFileChooserButton(JTextComponent txtAppFolder, String fileType,
            String selectFolder, String iniXmlTag) {
        JButton btnAppFolder = new JButton("+");
        btnAppFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {// Button angeklickt, Fenster geht auf
                // System.out.println("Click!");
                JFileChooser c = selectFolder.equals(Settings.SELECT_FOLDER)
                        ? Utils.createFolderChooser(txtAppFolder.getText())
                        : Utils.createFileChooser(fileType, new File(txtAppFolder.getText()), selectFolder);
                int rVal = c.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    File lastDir = c.getSelectedFile();
                    if (iniXmlTag != null)
                        Settings.storeIniSettings(iniXmlTag, lastDir.getAbsolutePath());
                    txtAppFolder.setText(lastDir.getAbsolutePath());
                }
            }
        });
        return btnAppFolder;
    }

    public static JDialog openDialog(Component component, String title, int width, int height) {
        JDialog dialog;
        if (component instanceof JDialog) {
            dialog = (JDialog) component;
        } else {
            dialog = new JDialog();
            dialog.getContentPane().add(component);
            dialog.setResizable(false);
            dialog.setBackground(Settings.ColorButtonBG);
            dialog.getContentPane().setBackground(Settings.ColorReadOnlyBG);
            dialog.setBounds(100, 100, width, height);
            dialog.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        }
        if (dialog != null) {
            dialog.setTitle(Settings.License + ": " + title);
            if (dialog.isAlwaysOnTopSupported())
                dialog.setAlwaysOnTop(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        }
        return dialog;
    }

    public static void expandTree(MyTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        tree.setSelectionRow(1);
    }

    public static void expandTree(MyTree tree, int row) {
        tree.expandRow(row);
        tree.setSelectionRow(row);
    }

    public static void collapseTree(MyTree tree) {
        int row = tree.getRowCount() - 1;
        while (row-- > 0) {
            tree.collapseRow(row);
        }
        tree.expandRow(0);
        tree.expandRow(1);
        tree.setSelectionRow(1);
    }

    public static boolean containsIgnoreCase(Collection<String> haufen, String teil) {
        boolean result = false;
        for (String str : haufen) {
            if (str.equalsIgnoreCase(teil)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static DefaultMutableTreeNode getTreeNodeForDataNode(DefaultMutableTreeNode parentTreeNode,
            DataNode dataNode) {
        DefaultMutableTreeNode returnTreeNode = null;

        if (parentTreeNode == null || parentTreeNode.getFirstChild() == null)
            return returnTreeNode;
        for (int t = 0; t < parentTreeNode.getChildCount(); t++) {
            if (dataNode == ((DefaultMutableTreeNode) parentTreeNode.getChildAt(t)).getUserObject())
                return (DefaultMutableTreeNode) parentTreeNode.getChildAt(t);
        }
        return returnTreeNode;

    }

    public static MyPane createDataPaneFromString(String content, boolean editable) {
        JTextArea textArea = Utils.createJTextArea(content, editable);
        if (!editable) {
            textArea.setForeground(Color.BLUE);
        }
        MyPane paneContent = new DataPane(textArea);
        paneContent.setEnabled(true);

        return paneContent;
    }

    public static void removeAllTreeComponents(Container comp, int level) {
        Component[] comps = comp.getComponents();
        for (int t = 0; t < comps.length; t++) {

            if (comps[t] instanceof Container && ((Container) comps[t]).getComponents().length > 0) {
                removeAllTreeComponents(((Container) comps[t]), level + 1);
            }
            if (comps[t] instanceof MyTree) {
                // System.out.println("Peng!");
                comp.remove(comps[t]);
            }
        }
    }

    public static TreePath getTreePath(TreeNode treeNode) {
        List<Object> nodes = new ArrayList<Object>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    public static TreePath getTreePath(TreeModel treeModel, String pathString) {
        return getTreePath(treeModel, pathString, ",");
    }

    public static TreePath getTreePath(TreeModel treeModel, String pathString, String pathSeparator) {
        if (treeModel == null) {
            throw new NullPointerException("treeModel == null");
        }
        if (pathString == null) {
            throw new NullPointerException("pathString == null");
        }
        if (pathSeparator == null) {
            throw new NullPointerException("pathSeparator == null");
        }
        pathString = pathString.replace("[", "").replace("]", "").replace(", ", ",").replace(" ,", ",");
        StringTokenizer tokenizer = new StringTokenizer(pathString, pathSeparator);
        int tokenCount = tokenizer.countTokens();
        int tokenNumber = 1;
        int tokenFoundCount = 0;
        Object[] path = new Object[(tokenCount > 0) ? tokenCount : 1];
        if (tokenCount > 0) {
            path[0] = treeModel.getRoot();
            tokenizer.nextToken();
            Object currentElement = treeModel.getRoot();
            boolean appended = true;
            while (appended && (tokenNumber < tokenCount)) {
                int childCount = treeModel.getChildCount(currentElement);
                String pathToken = tokenizer.nextToken().trim();
                boolean found = false;
                appended = false;
                for (int index = 0; (index < childCount) && !found; index++) {
                    Object childElement = treeModel.getChild(currentElement, index);
                    found = childElement.toString().equals(pathToken);
                    if (found) {
                        path[tokenNumber] = childElement;
                        currentElement = childElement;
                        appended = true;
                        tokenFoundCount++;
                    }
                }
                tokenNumber++;
            }
        }
        return ((tokenCount > 0) && (tokenCount - 1 == tokenFoundCount)) ? new TreePath(path) : null;
    }

    public static TreePath selectTreeRootNode(MyTree tree) {
        TreeNode rootNode = getTreeRootNode(tree);
        TreePath path = getTreePath(rootNode);
        if (rootNode != null)
            tree.setSelectionPath(path);
        return path;
    }

    public static TreeNode getTreeRootNode(MyTree tree) {
        if (tree.getModel() == null)
            return null;
        return (TreeNode) tree.getModel().getRoot();
    }

    public static void expandTree(MyTree tree, TreePath treePath) {
        DefaultMutableTreeNode selectedTreeNode = ((DefaultMutableTreeNode) (treePath.getLastPathComponent()));
        TreeNode n = (TreeNode) tree.getModel().getChild(selectedTreeNode, 0);
        TreePath path = treePath.pathByAddingChild(n);
        tree.expandPath(path);
    }

    public static Component createComponentFromString(String content, boolean editable) {
        return Utils.createJTextArea(content, editable);
    }

    // static String execImmediateExpression;
    public static JMenuItem setSelectedMenuText(JMenuItem menu, String selected) {
        if (menu == null || selected == null)
            return null;

        String text;
        if (menu.getActionCommand().equals(selected)) {
            menu.setForeground(Settings.ColorSelectedText);
            // text = "[ " + menu.getActionCommand() + " ]";
            text = menu.getActionCommand();
        } else {
            text = menu.getActionCommand();
            menu.setForeground(Settings.ColorText);
        }
        menu.setText(text);
        return menu;
    }

    public static JSplitPane createSplitPane(boolean splitVertical) {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setPreferredSize(new Dimension(1400, 528));
        splitPane.setBackground(Settings.ColorReadOnlyBG);
        splitPane.setOneTouchExpandable(false);
        splitPane.setOrientation(splitVertical ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.8);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerSize(3);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return splitPane;
    }

    public static JSplitPane createSplitPane(int direction, Component left, Component right) {
        JSplitPane splitPane = createSplitPane(direction == JSplitPane.HORIZONTAL_SPLIT ? true : false);
        if (left != null)
            splitPane.setLeftComponent(left);
        if (right != null)
            splitPane.setRightComponent(right);
        return splitPane;
    }

    public static MyTree createJTree(TreeModel model) {
        return createJTree(model, false);
    }

    public static MyTree createJTree(TreeModel model, boolean editable) {
        MyTree tree = new MyTree(model);
        tree.setBackground(Settings.ColorTreeBG);
        tree.setFocusCycleRoot(true);
        tree.setEditable(editable);
        // tree.setOpaque(false);
        tree.setCellRenderer(new MyCellRenderer());
        // tree.setCellEditor(new TreeTableCellEditor(tree));
        tree.setFont(Settings.FontNormal);
        return tree;
    }

    public static void showToast(String message, Component parent) {
        MyToastMessage.showMessage(message, parent);
    }

    public static void showToast(String message) {
        MyToastMessage.showMessage(message, null);
    }

    public static void makeUndoable(JTextComponent pTextComponent) {
        final UndoManager undoMgr = new UndoManager();

        // Add listener for undoable events
        pTextComponent.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undoMgr.addEdit(evt.getEdit());
            }
        });

        // Add undo/redo actions
        pTextComponent.getActionMap().put(Settings.actionUndo, new AbstractAction(Settings.actionUndo) {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoMgr.canUndo()) {
                        undoMgr.undo();
                    }
                } catch (CannotUndoException e) {
                    e.printStackTrace();
                }
            }
        });
        pTextComponent.getActionMap().put(Settings.actionRedo, new AbstractAction(Settings.actionRedo) {
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoMgr.canRedo()) {
                        undoMgr.redo();
                    }
                } catch (CannotRedoException e) {
                    e.printStackTrace();
                }
            }
        });

        // Create keyboard accelerators for undo/redo actions (Ctrl+Z/Ctrl+Y)
        pTextComponent.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), Settings.actionUndo);
        pTextComponent.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), Settings.actionRedo);
    }

    public static Point getComponentLocation(JComponent component) {
        Component currentComponent = component;
        int x = 0, y = 0;
        while (currentComponent != null) {
            x += currentComponent.getX();
            y += currentComponent.getY();

            currentComponent = currentComponent.getParent();
        }
        return new Point(x, y);
    }

    public static void showComponentClosedToParent(Component component, JComponent parentComponent) {
        int windowX, windowY;
        Point parentLocation = Utils.getComponentLocation(parentComponent);
        windowX = (int) parentLocation.getX();
        windowY = (int) parentLocation.getY() + parentComponent.getHeight();

        // component.setAlwaysOnTop(true);
        component.setLocation(windowX, windowY);
        // component.setMinimumSize(new Dimension(textField.getWidth(), 35));
        component.revalidate();
        component.repaint();
    }

    public static void copyToClipboard(String selection, Component parent) {
        if (selection == null || selection.isEmpty())
            return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selection), null);

        Utils.showToast("Content copied to clipboard !", parent);
    }

    public static void copyToClipboard(String selection) {
        if (selection == null || selection.isEmpty())
            return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selection), null);
        Utils.showToast("Content copied to clipboard !", null);
    }

    // Center on screen ( absolute true/false (exact center or 25% upper left) )
    public static void centerOnScreen(final Component c, final boolean absolute) {
        final int width = c.getWidth();
        final int height = c.getHeight();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width / 2) - (width / 2);
        int y = (screenSize.height / 2) - (height / 2);
        if (!absolute) {
            x /= 2;
            y /= 2;
        }
        c.setLocation(x, y);
    }

    public static void centerOnScreen(final Component c, final boolean absolute, Double ratio) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        c.setSize((int) (screenSize.width * ratio), (int) (screenSize.height * ratio));
        centerOnScreen(c, absolute);
    }

    // Center on parent ( absolute true/false (exact center or 25% upper left) )
    public static void centerOnParent(final Window child, final boolean absolute) {
        child.pack();
        boolean useChildsOwner = child.getOwner() != null
                ? ((child.getOwner() instanceof JFrame) || (child.getOwner() instanceof JDialog))
                : false;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension parentSize = useChildsOwner ? child.getOwner().getSize() : screenSize;
        final Point parentLocationOnScreen = useChildsOwner ? child.getOwner().getLocationOnScreen() : new Point(0, 0);
        final Dimension childSize = child.getSize();
        childSize.width = Math.min(childSize.width, screenSize.width);
        childSize.height = Math.min(childSize.height, screenSize.height);
        child.setSize(childSize);
        int x;
        int y;
        if ((child.getOwner() != null) && child.getOwner().isShowing()) {
            x = (parentSize.width - childSize.width) / 2;
            y = (parentSize.height - childSize.height) / 2;
            x += parentLocationOnScreen.x;
            y += parentLocationOnScreen.y;
        } else {
            x = (screenSize.width - childSize.width) / 2;
            y = (screenSize.height - childSize.height) / 2;
        }
        if (!absolute) {
            x /= 2;
            y /= 2;
        }
        child.setLocation(x, y);
    }

    public static JPanel createJPanelInput(Map<String, String> options) {
        return createJPanelInput(options, null);
    }

    public static JPanel createJPanelInput(Map<String, String> options, Map<String, List<String>> autoCompletes) {
        return createJPanelInput(options, autoCompletes, null);
    }

    public static JPanel createJPanelInput(Map<String, String> options, Map<String, List<String>> autoCompletes,
            Method changedEvent) {
        Map<String, Map<String, String>> listOptions = new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> stepOptions = new LinkedHashMap<String, String>();
        String stepTitle = "";
        for (Map.Entry<String, String> entry : options.entrySet()) {

            if (entry.getKey().toLowerCase().trim().startsWith(Settings.paramComment)) {
                if (entry.getKey().trim().equals(Settings.paramComment)) // seperator
                {
                    stepOptions.put(Settings.paramComment, "");
                } else if (true || entry.getKey().toLowerCase().contains("step")) {
                    if (stepOptions.size() > 0) {
                        listOptions.put(String.valueOf(listOptions.size() + 1) + ". " + stepTitle, stepOptions);
                        stepOptions = new LinkedHashMap<String, String>();
                    }
                    stepTitle = entry.getKey().replace(Settings.paramComment, "").trim();
                }
            } else {
                stepOptions.put(entry.getKey(), entry.getValue());
            }
        }

        if (listOptions.size() > 0 && stepOptions.size() > 0) {
            listOptions.put(String.valueOf(listOptions.size()
                    + 1) + ". " + stepTitle,
                    stepOptions);
        }

        if (listOptions.size() > 0) {
            return createJPanelWizardInput(listOptions, autoCompletes, changedEvent);
        }

        List<Component> components = new LinkedList<Component>();

        for (Map.Entry<String, String> entry : options.entrySet()) {
            boolean isVisible = true;
            if (entry.getKey().toLowerCase().contains(Settings.paramRequiredBy)) {
                isVisible = false;
            }
            String cachedKey = MyCache.getCachedKey(entry.getKey());
            if (autoCompletes != null) {
                if (autoCompletes.containsKey(Settings.paramPrefix + cachedKey)
                        && !autoCompletes.containsKey(cachedKey))
                    cachedKey = Settings.paramPrefix + cachedKey;
                else if (autoCompletes.containsKey(Settings.paramPrefix1 + cachedKey)
                        && !autoCompletes.containsKey(cachedKey))
                    cachedKey = Settings.paramPrefix1 + cachedKey;
            }
            List<String> lookupData = Utils.getLookupValuesFromName(entry.getKey()); // try to get LookupValues from
                                                                                     // Name
            if (lookupData == null || lookupData.size() == 0) // if not found, get from default lookups
            {
                if (autoCompletes != null && autoCompletes.containsKey(cachedKey))
                    lookupData = autoCompletes.get(cachedKey);
            }

            if (lookupData != null && lookupData.size() > 0) {
                JComboBox comp = Utils.createComboBox(entry.getValue(), entry.getKey(), lookupData);

                if (changedEvent != null) {
                    comp.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Object[] parameters = new Object[3];
                            parameters[0] = comp.getName();
                            parameters[1] = Utils.getComponentValue(comp);
                            parameters[2] = comp;
                            invokeMethod(changedEvent, comp, parameters);
                        }
                    });
                }
                comp.setVisible(isVisible);
                components.add(comp);
                continue;
            }

            JTextField comp = Utils.createTextField(entry.getValue(), entry.getKey(), lookupData);

            if (changedEvent != null) {
                comp.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Object[] parameters = new Object[3];
                        parameters[0] = comp.getName();
                        parameters[1] = comp.getText();
                        parameters[2] = comp;
                        invokeMethod(changedEvent, comp, parameters);
                    }

                });
            }
            components.add(comp);
        }

        return createJPanelInput(components.toArray(new Component[components.size()]));
    }

    public static JPanel createJPanelWizardInput(Map<String, String[]> options,
            Map<String, List<String>> autoCompletes) {
        Map<String, Map<String, String>> listOptions = new LinkedHashMap<String, Map<String, String>>();
        for (String key : options.keySet()) {
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (String value : options.get(key)) {
                map.put(value, value);
            }
            listOptions.put(key, map);
        }
        return createJPanelWizardInput(listOptions, autoCompletes, null);
    }

    public static JPanel createJPanelWizardInput(Map<String, Map<String, String>> listOptions,
            Map<String, List<String>> autoCompletes, Method method1) {

        // make all panels have the same height
        int maxSize = 0;
        List<String> removedKeys = new ArrayList<String>();
        for (String key : listOptions.keySet()) {
            Map<String, String> map = listOptions.get(key);
            if (map.size() == 0) {
                removedKeys.add(key);
                continue;
            }
            if (maxSize < map.size())
                maxSize = map.size();
        }
        for (String key : removedKeys) {
            listOptions.remove(key);
        }

        for (String key : listOptions.keySet()) {
            Map<String, String> map = listOptions.get(key);

            if (maxSize > map.size()) {
                for (int i = map.size(); i < maxSize; i++) {
                    map.put(Settings.tempSQLNULL + String.valueOf(i), "");
                }
                listOptions.put(key, map);
            }
        }

        // This JPanel is the base for CardLayout for other JPanels.
        final JPanel contentPane = Utils.createJPanelVertial();
        contentPane.setLayout(new CardLayout(0, 0));

        // final JPanel contentPane = Utils.createJPanelVertial();

        JPanel buttonPanel = Utils.createJPanelHorizental();
        final JButton previousButton = new JButton("<");
        previousButton.setBackground(Settings.Color2);
        // previousButton.setForeground(Settings.Color2);
        final JButton nextButton = new JButton("NEXT");
        nextButton.setBackground(Settings.Color1);
        // nextButton.setForeground(Color.WHITE);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.previous(contentPane);
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                CardLayout cardLayout = (CardLayout) contentPane.getLayout();
                cardLayout.next(contentPane);
            }
        });

        for (String key : listOptions.keySet()) {
            JPanel panel = createJPanelInput(listOptions.get(key), autoCompletes, method1);
            setBorderTitle(panel, key);
            contentPane.add(panel, key);
        }

        if (listOptions.size() == 1) {
            return contentPane;
        }
        return new MyLayoutPanel()
                .addVGroup()
                .addComponent(contentPane).addComponent(buttonPanel).endGroup();
    }

    public static JPanel createJPanelInput(String[] options, Map<String, List<String>> autoCompletes) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (String option : options) {
            map.put(option, option);
        }
        return createJPanelInput(map, autoCompletes);
    }

    public static JPanel createJPanelInput(Component[] components) {
        JPanel myPanel = new JPanel();
        int cols = 1;
        if (components.length < 15) {
            cols = 1;
        } else if (components.length < 30) {
            cols = 2;
        } else
            cols = 3;

        myPanel.setLayout(new GridLayout(Math.round(components.length / cols), cols * 2 - 1));
        for (Component comp : components) {
            String label = "";
            if (comp instanceof JCheckBox) {
                label = MyCache.getCachedKey(((JCheckBox) comp).getName());
                // comp.setBackground(Settings.ColorEditBG);
            } else if (comp instanceof JSeparator) {
                label = Settings.paramComment;
            } else if (comp instanceof JComponent) {
                label = MyCache.getCachedKey(getComponentName((JComponent) comp));
            }

            if (label.startsWith(Settings.paramComment)) {
                myPanel.add(Utils.createLabel("_________________________________"));
                myPanel.add(Utils.createLabel(""));
                // myPanel.add(new JSeparator());
                // myPanel.add(new JSeparator());
                continue;
            } else if (label.isEmpty() || label.startsWith(Settings.tempSQLNULL)) {
                myPanel.add(Utils.createLabel(""));
                myPanel.add(Utils.createLabel(""));
                continue;
            }

            // comp.setBackground(Settings.ColorEditBG);
            if (comp.getName().endsWith(Settings.paramNameMultiple))
                label = label + " (one or many)";
            myPanel.add(Utils.createLabel(label));
            if (comp instanceof JComboBox && (((JComboBox) comp).getItemCount() > 10)) {

                JPanel compPanel = Utils.createJPanelHorizental();

                compPanel.setBackground(Settings.Color1);
                compPanel.setName(comp.getName());

                JButton btn = null;
                if (comp.getName().endsWith(Settings.paramNameMultiple)
                        || comp.getName().endsWith(Settings.paramParamValueSeparator)) {
                    JTextField txtComp = Utils.createTextField(getComponentValue((JComboBox) comp));
                    txtComp.setPreferredSize(getControlDimension(comp, Settings.SizeTypeBig, 50));
                    txtComp.setName(comp.getName());
                    compPanel.add(txtComp);
                    btn = Utils.createButtonLookup(txtComp, Utils.getLookupValues((JComboBox) comp));
                } else {
                    compPanel.add(comp);
                    compPanel.setVisible(comp.isVisible());
                    btn = Utils.createButtonLookup((JComboBox) comp);
                }

                if (btn != null) {
                    compPanel.add(btn);
                    myPanel.add(compPanel);
                } else {
                    myPanel.add(comp);
                }
            } else {
                myPanel.add(comp);
            }
        }
        if (cols > 1 && components.length % cols > 0) {
            int t = components.length % cols;
            for (int i = 0; i < t; i++) {
                myPanel.add(Utils.createLabel(""));
                myPanel.add(Utils.createTextField(""));
            }
        }
        return myPanel;
    }

    // set Border title for JPanel
    public static void setBorderTitle(JPanel panel, String title) {
        panel.setBorder(BorderFactory.createTitledBorder(title));
    }

    public static JPanel createJPanelInput(String[] options) {
        return createJPanelInput(options, null);
    }

    public static JLabel createLabel(String text) {
        JLabel label = createLabel(text, -1);
        return label;
    }

    public static JLabel createLabel(String text, String description) {
        JLabel label = createLabel(getLabelHtml("[" + text + "] " + description, text), -1);
        return label;
    }

    public static JLabel createLabel(String text, int width) {
        // JLabel label = createLabel("<html><div style='width:" + width + "px'>" + text
        // + "</div></html>");
        JLabel label = new JLabel(text);
        // label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));
        label.setBackground(Settings.ColorReadOnlyBG);
        if (width > 0)
            label.setPreferredSize(new Dimension(width, Settings.LabelHeight));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        return label;
    }

    public static Dimension getControlDimension(Component view) {
        return getControlDimension(view, Settings.SizeTypeNormal);
    }

    public static Dimension getTextAreaDimension(String message) {
        String[] lines = message.split("\n");
        int maxW = 0;
        for (String line : lines) {
            if (line.length() > maxW)
                maxW = line.length();
        }
        int width = maxW / 3;
        if (width < 600)
            width = 600;
        int height = lines.length * 18 + 10;
        return new Dimension(width, height);
    }

    public static Dimension getControlDimension(Component view, int sizeType) {
        return getControlDimension(view, sizeType, 0);
    }

    public static Dimension getControlDimension(Component view, int sizeType, int extraWidth) {
        Dimension dimension;
        int width;
        int height;
        if (view instanceof JComboBox) {
            width = Settings.InputWidth;
            height = Settings.LabelHeight;
        } else if (view instanceof JLabel) {
            width = sizeType * Settings.LabelWidth;
            height = Settings.LabelHeight;
        } else if (view instanceof JTextArea) {
            width = sizeType * Settings.LabelWidth;
            height = Settings.LabelHeight * 2;
        } else {
            width = sizeType * Settings.InputWidth;
            height = Settings.LabelHeight;
        }
        dimension = new Dimension(width + extraWidth, height);
        view.setPreferredSize(dimension);
        return dimension;
    }

    public static JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<String>();

        setComponentStyle(combo, false);
        combo.setRenderer(new CustomComboRenderer());
        combo.setEditable(false);
        return combo;
    }

    public static List<String> cleanComboBoxData(List<String> items) {
        ListIterator<String> iterator = items.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.contains(Settings.dataSeperator)) {
                // Replace element
                iterator.set(next.substring(0, next.indexOf(Settings.dataSeperator)));
            }
        }
        return items;
    }

    public static JComboBox<String> createComboBox(String selectedItem, String name, List<String> items) {
        return createComboBox(selectedItem, name, items, false);
    }

    public static JComboBox<String> createComboBox(String selectedItem, String name, List<String> items,
            boolean filterable) {
        if (items == null)
            items = new ArrayList<String>();

        if (selectedItem.contains(",")) {
            selectedItem = selectedItem.substring(0, selectedItem.indexOf(",")).trim();
        }
        items = cleanComboBoxData(items);
        if (!items.contains(selectedItem)) {
            for (String item : items) {
                if (item.startsWith(selectedItem + " : "))
                    selectedItem = item;
            }
        }
        JComboBox<String> comb = createComboBox(items.toArray(), selectedItem);
        comb.setName(name);
        if (filterable)
            applyComboBoxFilter(comb, null, functionContainString());

        return comb;
    }

    public static JComboBox<String> createComboBox(List<String> items, String selectedItem) {
        items = cleanComboBoxData(items);
        return createComboBox(items.toArray(), selectedItem);
    }

    public static JComboBox<String> createComboBox(List<String> items, String selectedItem, String name) {
        items = cleanComboBoxData(items);
        return createComboBox(items.toArray(), selectedItem, name);
    }

    public static JComboBox<String> createComboBox(Object[] items, String selectedItem) {
        return createComboBox(items, selectedItem, null);
    }

    public static JComboBox<String> createComboBox(Object[] items, String selectedItem, String name) {
        JComboBox<String> combo = createComboBox();
        selectedItem = getFileNameShort(selectedItem);
        DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(items);
        theModel.insertElementAt("", 0);
        combo.setModel(theModel);
        combo.setSelectedItem("");
        if (selectedItem != null && !selectedItem.isEmpty()) {
            combo.setSelectedItem(selectedItem);
        } else {
            combo.setSelectedIndex(0);
        }
        if (name != null && !name.isEmpty())
            combo.setName(name);
        applyComboBoxFilter(combo);
        return combo;
    }

    public static JComboBox<String> applyComboBoxFilter(JComboBox<String> combo) {
        return applyComboBoxFilter(combo, null, null);
    }

    public static JComboBox<String> applyComboBoxFilter(JComboBox<String> combo,
            BiPredicate<String, String> userFilter) {
        return applyComboBoxFilter(combo, null, userFilter);
    }

    public static JComboBox<String> applyComboBoxFilter(JComboBox<String> combo,
            Function<String, String> comboDisplayTextMapper, BiPredicate<String, String> userFilter) {
        if (userFilter != null) {
            ComboBoxFilterDecorator<String> decorate = ComboBoxFilterDecorator.decorate(
                    combo,
                    comboDisplayTextMapper,
                    userFilter);
            combo.setRenderer(
                    new CustomComboRenderer(decorate.getFilterTextSupplier()));
        } else {
            combo.setRenderer(
                    new CustomComboRenderer());
        }

        return combo;
    }

    public static JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setBackground(Settings.ColorMenuBG);
        menu.setFont(Settings.FontNormal);
        return menu;
    }

    public static JMenuItem createMenuItem(String text) {
        return createMenuItem(text, null);
    }

    public static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setBackground(Settings.ColorReadOnlyBG);
        menuItem.setFont(Settings.FontNormal);
        if (listener != null)
            menuItem.addActionListener(listener);
        return menuItem;
    }

    public static String getLabelHtml(String word, String typedKeyword) {
        return getLabelHtml(word, typedKeyword, true);
    }

    public static String getLabelHtml(String word, String typedKeyword, boolean replaceFirst) {
        try {
            String[] keywords = typedKeyword.split(" ");
            for (String keyword : keywords) {
                if (!keyword.isEmpty())
                    word = (replaceFirst ? word.replaceFirst(keyword, "<b>" + keyword + "</b>")
                            : word.replaceAll(keyword, "<b>" + keyword + "</b>"));
            }
            return "<html>" + word + "</html>";
        } catch (Exception ex) {
            // LogManager.getLogger().error(ex);
            return word;
        }
    }

    public static String getLabelOriginal(String word) {
        return word.replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("<b>", "").replaceAll("</b>", "");
    }

    public static BiPredicate<String, String> functionContainString() {
        return (s, t) -> s.toLowerCase().contains(t.toLowerCase());
    }

    public static JComboBox<String> setComboBoxData(JComboBox<String> combo, Object[] items, String selectedItem) {

        DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel(items);
        theModel.insertElementAt("", 0);
        combo.setModel(theModel);
        if (selectedItem != null && !selectedItem.isEmpty())
            setComboValue(combo, selectedItem); // combo.setSelectedItem(selectedItem);
        else
            combo.setSelectedIndex(0);
        return combo;
    }

    public static JComboBox<String> setComboBoxData(JComboBox<String> combo, List<String> items) {
        if (combo == null || items == null)
            return null;
        items = cleanComboBoxData(items);
        List<String> lookupData = Utils.getLookupValuesFromName(combo.getName());
        if (lookupData != null && !lookupData.isEmpty()) {
            items = lookupData;
        }

        return setComboBoxData(combo, items.toArray(), getComponentValue(combo));
    }

    public static JComboBox<String> setComboBoxData(JComboBox<String> combo, Object[] items) {
        return setComboBoxData(combo, items, getComponentValue(combo));
    }

    public static JComboBox<String> setComboBoxData(JComboBox<String> combo, List<String> items,
            String selectedItem) {
        items = cleanComboBoxData(items);
        return setComboBoxData(combo, items.toArray(), selectedItem);
    }

    public static JComboBox<String> setAutoComplete(JComboBox<String> combo, Object[] items, String selectedItem) {
        return setComboBoxData(combo, items, selectedItem);
    }

    public static JComboBox<String> setAutoComplete(JComboBox<String> combo, List<String> items,
            String selectedItem) {
        items = cleanComboBoxData(items);
        return setComboBoxData(combo, items.toArray(), selectedItem);
    }

    public static JComboBox<String> setAutoComplete(JComboBox<String> combo, Object[] items) {
        return setComboBoxData(combo, items, getComponentValue(combo));
    }

    public static JComboBox<String> setAutoComplete(JComboBox<String> combo, List<String> items) {
        items = cleanComboBoxData(items);
        return setComboBoxData(combo, items.toArray(), getComponentValue(combo));
    }

    public static JComboBox<String> createComboBox(List<String> items) {
        return createComboBox(items, null);
    }

    public static JComboBox<String> createComboBox(Object[] items) {
        return createComboBox(items, null);
    }

    public static JCheckBox createCheckBox(String text, boolean checked) {
        JCheckBox checkBox;
        checkBox = new JCheckBox(text);
        checkBox.setBackground(Settings.ColorButtonBG);
        checkBox.getModel().setSelected(checked);
        setComponentName(checkBox, text);
        return checkBox;
    }

    public static JCheckBox createCheckBox(String text) {
        return createCheckBox(text, false);
    }

    public static JCheckBox createCheckBox(boolean checked) {
        return createCheckBox("", checked);
    }

    public static JTextField createTextField() {
        return createTextField("");
    }

    public static JPasswordField createPasswordField() {
        return createPasswordField(null, null);
    }

    public static JPasswordField createPasswordField(String text) {
        return createPasswordField(text, null);
    }

    public static JPasswordField createPasswordField(String text, String name) {
        JPasswordField passwordField = new JPasswordField();
        setComponentStyle(passwordField);
        if (text != null)
            passwordField.setText(text);
        if (name != null)
            passwordField.setName(name);
        return passwordField;
    }

    public static JTextField createTextField(String text) {
        return createTextField(text, null);
    }

    public static JTextField createTextField(String text, String name, List<String> words) {
        JTextField textField = createTextField(text, name);
        Utils.setAutoComplete(textField, words);
        return textField;
    }

    public static void setComponentBorder(JComponent input) {
        input.setBorder(BorderFactory.createCompoundBorder(
                input.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
    }

    public static void setComponentStyle(JComponent input) {
        setComponentStyle(input, true);
    }

    public static void setComponentStyle(JComponent input, boolean withBorder) {
        if (withBorder)
            setComponentBorder(input);

        input.setPreferredSize(getControlDimension(input));
        if (input instanceof JTextField) {
            input.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));

            ((JTextField) input).setDropMode(DropMode.INSERT);
            ((JTextField) input).setColumns(Settings.TextFieldWidth);
        } else if (input instanceof JComboBox) {
            input.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal - 1));
        }
        input.setBackground(Settings.ColorEditBG);
    }

    public static JTextField createTextField(JTextField clone) {
        JTextField input = new JTextField();
        input.setText(clone.getText().trim());

        setComponentStyle(input);
        if (clone.getClientProperty("name") != null)
            Utils.setComponentName(input, clone.getClientProperty("name").toString());

        return input;
    }

    public static JTextField createTextField(JTextField clone, String name) {
        JTextField input = createTextField(clone);
        if (name != null && !name.isEmpty()) {
            Utils.setComponentName(input, name);
        }

        return input;
    }

    public static JTextField createTextField(String text, String name) {
        JTextField input = new JTextField();
        input.setText(text);

        setComponentStyle(input);

        if (name != null && !name.isEmpty()) {
            Utils.setComponentName(input, name);
        }
        return input;
    }

    public static JTextArea createJTextArea() {
        return createJTextArea("", false);
    }

    public static MyTextPane createTextPane() {
        return createTextPane("", false);
    }

    public static MyTextPane createTextPane(String content, boolean editable) {
        MyTextPane textArea = new MyTextPane(); // new JTextArea(3, Settings.TextFieldWidth);

        applyJTextComponentStyle(textArea, content, editable);
        textArea.setEditable(editable);

        return textArea;
    }

    public static String getComponentValue(JComboBox<String> comp) {
        return DBService.getKeyFromDisplayKeyValue(getComponentValue(comp, false));
    }

    public static String getComponentValue(JComboBox<String> comp, boolean cutOff) {
        return getComponentValue(comp, cutOff ? new String[] { ":", "-" } : new String[] {});
    }

    public static String getComponentValue(JComboBox<String> comp, String[] chars) {
        if (comp.getSelectedItem() == null)
            return null;
        String value = comp.getSelectedItem().toString();
        if (chars != null)
            for (String char1 : chars) {
                if (value.contains(char1)) {
                    value = value.substring(0, value.indexOf(char1)).trim();
                }
            }

        return value;
    }

    public static String getComponentValue(JTextComponent comp) {
        String value = comp.getText().trim();
        return value;
    }

    public static String getComponentValue(JTextComponent comp, String key) {
        String value = Utils.getLookupValueFromKey(comp, key);
        return value;
    }

    public static String getComponentValue(JCheckBox comp, String YES_VALUE, String NO_VALUE) {
        String value = comp.isSelected() ? YES_VALUE : NO_VALUE;
        return value;
    }

    public static String getComponentValue(JCheckBox comp) {
        return String.valueOf(comp.isSelected());
    }

    public static String getComponentName(JComponent comp) {
        String label = "";
        label = comp.getName();
        if (label != null && !label.isEmpty())
            return label;

        label = (String) comp.getClientProperty(Settings.TagKey);
        if (label != null && !label.isEmpty())
            return label;
        return label;
    }

    public static JComponent setComponentName(JComponent comp, String name) {
        comp.setName(name);
        comp.putClientProperty(Settings.TagKey, name);
        return comp;
    }

    public static void applyJTextComponentStyle(JTextComponent textArea, String content, boolean editable) {
        textArea.setBackground(editable ? Settings.ColorEditBG : Settings.ColorReadOnlyBG);
        textArea.setText(content);
        textArea.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));
        textArea.setForeground(Color.DARK_GRAY);
    }

    public static JScrollPane createJTextAreaScrollable(String content, boolean editable) {
        JTextArea textArea = createJTextArea(content, editable);
        textArea.setPreferredSize(getTextAreaDimension(content));
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportBorder(BorderFactory.createLineBorder(textArea.getBackground(), 10));
        return scrollPane;
    }

    public static JTextArea createJTextArea(String content, boolean editable) {
        content = content == null ? "" : content.trim();
        JTextArea textArea = new JTextArea();
        applyJTextComponentStyle(textArea, content, editable);
        textArea.setEditable(editable);
        if (editable) {
            textArea.setAutoscrolls(true);
            textArea.setColumns(Settings.TextFieldWidth);
            textArea.setWrapStyleWord(true);
        }
        return textArea;
    }

    public static JTable createTable() {
        JTable table = new JTable();
        return table;
    }

    public static void addTableSorter(MyJTable table, String[] columns) {
        TableRowSorter<MyJTableModel> sorter = new TableRowSorter<MyJTableModel>((MyJTableModel) table.getModel());
        for (int i = 0; i < columns.length; i++) {
            sorter.setComparator(i, new Comparator<String>() {
                @Override
                public int compare(String name1, String name2) {
                    if (Utils.isNumeric(name1) && Utils.isNumeric(name2))
                        return Integer.parseInt(name1) - Integer.parseInt(name2);
                    return name1.compareTo(name2);
                }
            });
        }
        table.setRowSorter(sorter);
    }

    public static void addTableFilter(MyJTable table, JTextField txtSearch, Integer SearchColumnIndex,
            String[] columns) {
        TableRowSorter<MyJTableModel> sorter = (TableRowSorter<MyJTableModel>) table.getRowSorter();

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    String txt = txtSearch.getText().toLowerCase();
                    if (txt.length() == 0 || txt.trim() == "") {
                        sorter.setRowFilter(null);
                    } else if (SearchColumnIndex != null && SearchColumnIndex >= 0)
                        sorter.setRowFilter(RowFilter.regexFilter("^(?i)" + Pattern.quote(txt), SearchColumnIndex));
                    else {
                        List<RowFilter<Object, Object>> filters = new ArrayList<>();
                        for (int i = 0; i < columns.length; i++) {
                            filters.add(RowFilter.regexFilter(txt, i));
                        }
                        RowFilter<Object, Object> af = RowFilter.orFilter(filters);
                        sorter.setRowFilter(af);
                    }
                } catch (PatternSyntaxException pse) {
                    System.out.println("Bad regex pattern");
                }
            }
        });
    }

    public static MyAutoComplete setAutoComplete(JTextComponent textField) {
        return setAutoComplete(textField, null);
    }

    public static MyAutoComplete setAutoCompleteActionPerform(JTextComponent textField, Method actionPerform) {
        if (textField == null)
            return null;
        MyAutoComplete autoSuggestor = getAutoComplete(textField);
        autoSuggestor.setActionPerform(actionPerform);
        return autoSuggestor;
    }

    public static MyAutoComplete setAutoComplete(JTextComponent textField, Integer index, String command,
            Object words) {
        MyAutoComplete autoSuggestor = getAutoComplete(textField);

        if (autoSuggestor != null) {
            autoSuggestor.setDictionary(command, words);
        } else {
            autoSuggestor = setAutoComplete(textField, new LinkedList<String>());
            if (autoSuggestor != null)
                autoSuggestor.setDictionary(index, command, words);
        }
        return autoSuggestor;
    }

    public static MyAutoComplete setAutoComplete(JTextComponent textField, String command, Object words) {
        return setAutoComplete(textField, -1, command, words);
    }

    public static MyAutoComplete setAutoComplete(JTextComponent textField, Object data) {
        if (textField == null)
            return null;
        if (data instanceof MyAutoComplete) {
            textField.putClientProperty("autoComplete", (MyAutoComplete) data);
            return (MyAutoComplete) data;
        }
        if (!(data instanceof Collection))
            return null;

        Collection<String> words = (Collection<String>) data;

        if (words == null)
            words = new LinkedList<String>();
        MyAutoComplete autoSuggestor = getAutoComplete(textField);
        textField.removeFocusListener(null);
        for (FocusListener listner : textField.getFocusListeners()) {
            if (listner.toString().equals("setFixLength"))
                textField.removeFocusListener(listner);
        }
        if (autoSuggestor != null) {
            autoSuggestor.setDictionary(words);
        } else {
            autoSuggestor = new MyAutoComplete(textField, Main.mainScreen.getFrame(),
                    words, Color.WHITE.brighter(),
                    Color.BLUE, Color.BLACK, 1) {
                @Override
                public boolean wordTyped(String typedWord) {
                    return super.wordTyped(typedWord);
                }
            };

            textField.putClientProperty("autoComplete", autoSuggestor);
            setLookupValues(textField, words);
        }

        // Instantiate a FocusListener ONCE
        java.awt.event.FocusListener myFocusListener = new java.awt.event.FocusListener() {

            public void focusGained(java.awt.event.FocusEvent focusEvent) {
                try {
                    Utils.closeAutoComplete(textField.getParent(), textField);
                } catch (ClassCastException ignored) {
                    /* I only listen to JTextFields */
                }
            }

            public void focusLost(java.awt.event.FocusEvent focusEvent) {
                // try {
                // Utils.closeAutoComplete(textField.getParent());
                // } catch (ClassCastException ignored) {
                // /* I only listen to JTextFields */
                // }
            }

        };

        textField.addFocusListener(myFocusListener);

        return autoSuggestor;
    }

    public static MyAutoComplete getAutoComplete(JTextComponent textField) {
        MyAutoComplete autoSuggestor = textField.getClientProperty("autoComplete") != null
                ? (MyAutoComplete) textField.getClientProperty("autoComplete")
                : null;
        return autoSuggestor;
    }

    public static void closeAutoComplete(Container panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextComponent)
                closeAutoComplete((JTextComponent) comp);
        }
    }

    public static void closeAutoComplete(Container panel, JTextComponent activeComp) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextComponent && activeComp != comp)
                closeAutoComplete((JTextComponent) comp);
        }
    }

    public static void closeAutoComplete(JTextComponent comp) {
        MyAutoComplete autoSuggestor = getAutoComplete((JTextComponent) comp);
        if (autoSuggestor != null)
            autoSuggestor.hide();
        setAutoComplete(comp, autoSuggestor);
    }

    public static void disableAutoComplete(JTextComponent comp) {
        MyAutoComplete autoSuggestor = getAutoComplete((JTextComponent) comp);
        if (autoSuggestor != null)
            autoSuggestor.isActive = false;
        setAutoComplete(comp, autoSuggestor);
    }

    public static void enableAutoComplete(JTextComponent comp) {
        MyAutoComplete autoSuggestor = getAutoComplete((JTextComponent) comp);
        if (autoSuggestor != null)
            autoSuggestor.isActive = true;
        setAutoComplete(comp, autoSuggestor);
    }

    // return lookup values from control name. e.g.
    // "controlName__011_name1_012_name2" will return ["011 : name1", "012 : name2"]
    public static List<String> getLookupValuesFromName(String controlName) {
        List<String> list = new LinkedList<String>();

        if (controlName == null || !controlName.contains(Settings.paramParamValueSeparator))
            return list;
        String tmp = controlName.substring(controlName.lastIndexOf(Settings.paramParamValueSeparator) + 2,
                controlName.length());
        if (tmp.endsWith(Settings.paramNameMultiple))
            tmp = tmp.replace(Settings.paramNameMultiple, "");

        if (!tmp.isEmpty()) {
            String[] arr = tmp.split("_");
            if (arr.length < 2)
                return list;
            String t = "";
            for (String s : arr) {
                if (!isNumeric(s)) {
                    if (!t.isEmpty())
                        t = MyService.getDisplayKeyValue(t, s);
                    else
                        t = MyService.getDisplayKeyValue(s, s);
                    if (!t.isEmpty())
                        list.add(t);
                    t = "";
                    continue;
                } else {
                    t = s;
                }
            }
        }
        return list;
    }

    public static Collection<String> getLookupValues(JComponent comp) {
        if (comp instanceof JTextField) {
            Object obj = ((JTextField) comp).getClientProperty("autoComplete_data");
            return obj != null ? (Collection<String>) obj : null;
        } else if (comp instanceof JComboBox) {
            Collection<String> lookupValues = new LinkedList<String>();

            // convert combobox model to collection
            for (int i = 0; i < ((JComboBox) comp).getModel().getSize(); i++) {
                if (((JComboBox) comp).getModel().getElementAt(i) != null
                        && ((JComboBox) comp).getModel().getElementAt(i).toString().isEmpty())
                    continue;
                lookupValues.add(((JComboBox) comp).getModel().getElementAt(i).toString());
            }

            return lookupValues;
        }

        return null;
    }

    public static void setLookupValues(JComponent comp, Collection<String> lookupValues) {
        if (comp instanceof JTextField) {
            ((JTextField) comp).putClientProperty("autoComplete_data", lookupValues);
        } else if (comp instanceof JComboBox) {
            ((JComboBox) comp).setModel(new DefaultComboBoxModel(lookupValues.toArray()));
        }
    }

    public static String getLookupValueFromKey(JTextComponent comp, String key) {
        Collection<String> lookupValues = getLookupValues(comp);
        if (lookupValues != null)
            return getLookupValueFromKey(lookupValues, key);
        return key;
    }

    // return lookup value from key. e.g. key:011, ["011 : name1"] will return
    // "name1"
    public static String getLookupValueFromKey(Collection<String> words, String key) {
        for (String word : words) {
            String[] tmp = word.split(":");

            word = word.replace(" : ", ":");
            if (tmp.length > 1 && tmp[0].trim().equalsIgnoreCase(key))
                return tmp[1].trim();
        }
        return key;
    }

    public static void setFixLength(JTextComponent textBox, String fillWith, Integer length) {
        setFixLength(textBox, Utils.multiplyChars(fillWith, length));
    }

    public static void setFixLength(JTextComponent textBox, String pattern) {
        for (String varPattern : Settings.varsPatterns) {
            String[] tmp = varPattern.split(":");
            if (tmp.length > 1 && tmp[0].equalsIgnoreCase(pattern.replace(Settings.paramPrefix, "")))
                pattern = tmp[1];
        }
        if (pattern.startsWith(Settings.paramPrefix))
            return;

        if (isNumeric(pattern))
            pattern = Utils.multiplyChars("0", Integer.valueOf(pattern));

        int length = pattern.length();
        String pattern1 = pattern;

        if (getLookupValues(textBox) != null) // if this is autocomplete then ignore
            return;
        textBox.addFocusListener(new FocusAdapter() {
            @Override
            public String toString() {
                return "setFixLength";
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int maxLength = textBox.getDocument().getLength();
                    if (maxLength > length)
                        maxLength = length;
                    if (maxLength > 0)
                        textBox.setText(pattern1.substring(maxLength)
                                + textBox.getDocument().getText(0, textBox.getDocument().getLength()));
                    if (textBox.getDocument().getLength() == 0)
                        textBox.setText(
                                textBox.getDocument().getText(0, textBox.getDocument().getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static JButton createButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(Settings.FontNormal);
        btn.setForeground(Settings.ColorText);
        btn.setBackground(Settings.ColorButtonBG);
        btn.setVisible(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // btn.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null,
        // null));
        if (listener != null)
            btn.addActionListener(listener);
        return btn;
    }

    public static JButton createButton(String text) {
        return createButton(text, null);
    }

    public static JButton createButtonLookup(JComponent comp) {
        return createButtonLookup(comp, getLookupValues(comp));
    }

    public static JButton createButtonLookup(JComponent comp, Collection<String> items) {
        String name = comp.getName();
        return createButtonLookup(comp, items,
                (name != null && name.endsWith(Settings.paramNameMultiple) ? true : false));
    }

    public static JButton createButtonLookup(JComponent comp, Collection<String> items, boolean selectMany) {
        if (items == null)
            return null;
        JButton btn = Utils.createButton("...");

        btn.setPreferredSize(new Dimension(25, (int) comp.getPreferredSize().getHeight()));
        btn.setBackground(Settings.Color1);

        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String value = "";

                if (comp instanceof JTextComponent) {
                    value = ((JTextComponent) comp).getText().trim();
                } else if (comp instanceof JComboBox && ((JComboBox) comp).getSelectedItem() != null) {
                    value = ((JComboBox) comp).getSelectedItem().toString().trim();
                }

                Collection<String> currentItems = getLookupValues(comp); // get latest lookup values
                if (currentItems == null || currentItems.isEmpty())
                    currentItems = items;

                JComboBox comp1 = Utils.createComboBox(
                        value, comp.getName(),
                        new LinkedList<String>(currentItems), true);

                JPanel panel1 = Utils.createJPanelHorizental();
                String label = MyService.getCachedKey(comp.getName());
                panel1.add(Utils.createLabel(label));
                panel1.add(comp1);
                Map<String, String> tmp = MyInputDialog.instance().showMapInput(
                        "Type keyword to search, [Enter] or [Tab] to select", "Select " + label + ":",
                        panel1);
                if (tmp != null) {
                    if (comp instanceof JTextComponent) {
                        String newValue = getComponentValue(comp1);
                        if (selectMany)
                            ((JTextComponent) comp).setText(addValueToStringArray(value, newValue));
                        else
                            ((JTextComponent) comp).setText(newValue);
                    } else if (comp instanceof JComboBox) {
                        ((JComboBox) comp).setSelectedItem(comp1.getSelectedItem());
                    }
                }
            }
        });
        return btn;
    }

    public static Object[][] getTableData(JTable table) {
        return getTableData((DefaultTableModel) table.getModel());
    }

    public static Object[][] getTableData(DefaultTableModel dtm) {
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        for (int i = 0; i < nRow; i++)
            for (int j = 0; j < nCol; j++)
                tableData[i][j] = dtm.getValueAt(i, j);
        return tableData;
    }

    public static Object[] getRowData(JTable table, int rowIndex) {
        return getRowData(table.getModel(), rowIndex);
    }

    public static Object[] getRowData(TableModel model, int rowIndex) {
        Object[] rowData = new Object[model.getColumnCount()];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = model.getValueAt(rowIndex, i);
        }
        return rowData;
    }

    public static Object[][] getRowsData(JTable table, int[] rowIndexs) {
        return getRowsData(table.getModel(), rowIndexs);
    }

    public static Object[][] getRowsData(TableModel model, int[] rowIndexs) {
        Object[][] rowData = new Object[rowIndexs.length][model.getColumnCount()];
        for (int i = 0; i < rowIndexs.length; i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                rowData[i][j] = model.getValueAt(rowIndexs[i], j);
            }
        }
        return rowData;
    }

    public static Object[][] getRowsData(TableModel model, int[] rowIndexs, int[] colIndexs) {
        Object[][] rowData = new Object[rowIndexs.length][colIndexs.length];
        for (int i = 0; i < rowIndexs.length; i++) {
            for (int j = 0; j < colIndexs.length; j++) {
                rowData[i][j] = model.getValueAt(rowIndexs[i], colIndexs[j]);
            }
        }
        return rowData;
    }

    public static Object[] getSelectedRowData(JTable table) {
        return getRowData(table, table.getSelectedRow());
    }

    public static ArrayList getColumnData(JTable table, int columnIndex) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < table.getModel().getRowCount(); i++) {
            list.add(table.getModel().getValueAt(i, 0)); // get the all row values at column index 0
        }
        return list;
    }

    public static int getMaxTableRowWith(JTable table) {
        int summe = 0;
        for (int t = 0; t < table.getColumnCount(); t++) {
            summe = summe + table.getColumnModel().getColumn(t).getPreferredWidth();
        }
        return summe;
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    public static void fitColumnSizes(JTable table) {
        AbstractTableModel model = (AbstractTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        for (int j = 0; j < table.getModel().getRowCount(); j++) {
            Object[] longValues = Utils.getRowData(table, j);
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

            for (int i = 0; i < table.getColumnCount(); i++) {
                column = table.getColumnModel().getColumn(i);

                comp = headerRenderer.getTableCellRendererComponent(
                        null, column.getHeaderValue(),
                        false, false, 0, 0);
                headerWidth = comp.getPreferredSize().width;

                comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(
                        table, longValues[i],
                        false, false, 0, i);
                cellWidth = comp.getPreferredSize().width;
                column.setPreferredWidth(Math.max(headerWidth, cellWidth));
            }
        }
    }
}