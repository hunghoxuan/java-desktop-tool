package com.rs2.core.components;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import java.awt.Component;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;

import com.rs2.core.base.MyPane;
import com.rs2.core.components.treeview.MyCellRenderer;
import com.rs2.core.logs.LogManager;
import com.rs2.core.settings.Settings;
import com.rs2.modules.files.FilesService;
import com.rs2.core.utils.Utils;

public class MyFileBrowser extends JPanel {

  public MyTree fileTree;
  public FileSystemModel fileSystemModel;
  public JTextComponent txtContent = Utils.createTextPane();
  // private JTextArea fileContentTextArea = Utils.createJTextArea();
  public JScrollPane treePane, filePane;
  public MyPane myPane;
  public String rootPath;
  public File rootFolder;
  public JSplitPane splitPane;
  public Component currentComponent;
  public Object currentDataObject;
  public String originalContent;
  public Map<String, String[]> files = new TreeMap<String, String[]>();
  public boolean sortByName = true;
  public boolean autoCopyToClipboard = false;

  public MyFileBrowser(String directory) {
    this(new File(directory));
  }

  public MyFileBrowser(File directory) {
    super();
    if (directory.isFile())
      directory = directory.getParentFile();
    rootFolder = directory;

    this.setLayout(new GridLayout(0, 1));
    txtContent.setEditable(false);

    refreshTree(directory, null);
    fileTree.setEditable(true);
    fileTree.setCellRenderer(new MyCellRenderer());

    fileTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent event) {
        // if (myPane != null) {
        // myPane.cancel();
        // }
        File file = (File) fileTree.getLastSelectedPathComponent();
        TreePath path = fileTree.getSelectionPath();

        if (file != null && file.isDirectory()) {
          return;
        }

        refreshEditor(file);
        // fileTree.setSelectionPath(path);
      }
    });

    MouseListener ml = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null)
          return;
        File file = (File) fileTree.getLastSelectedPathComponent();

        int level = selPath.getPathCount();
        if (selRow != -1) {
          if (e.getClickCount() == 1) {
            // mySingleClick(selRow, selPath);
          } else if (e.getClickCount() == 2) {
            if (file != null && file.isDirectory() && level == 1) {
              changeRoot(file);
            }
            // myDoubleClick(selRow, selPath);
          }
        }
      }
    };
    fileTree.addMouseListener(ml);

    treePane = new JScrollPane(fileTree);
    filePane = new JScrollPane(txtContent);
    // filePane.add(fileContentTextArea);
    splitPane = Utils.createSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, filePane);
    splitPane.setDividerLocation(250);
    currentComponent = txtContent;
    // getContentPane().add(Utils.createButton("Refresh"));
    getContentPane().add(splitPane);
    // setDefaultCloseOperation(EXIT_ON_CLOSE);
    // setSize(640, 480);
    setVisible(true);
  }

  public void setRoot(File root) {
    this.rootFolder = root;
  }

  public void addSelectedFolder(String folder, List<String> children) {
    if (fileSystemModel.selectedFolders.containsKey(folder)) {
      List<String> items = fileSystemModel.selectedFolders.get(folder);
      for (String item : children) {
        if (item == null || item.isEmpty())
          continue;
        File f = new File(item);
        if (f.isDirectory() && !items.contains(item))
          items.add(item);
      }
      fileSystemModel.selectedFolders.put(folder, items);

    } else
      fileSystemModel.selectedFolders.put(folder, children);
  }

  public void addSelectedFolder(String folder, String[] children) {
    addSelectedFolder(folder, new LinkedList<String>(Arrays.asList(children)));
  }

  public void addSelectedFile(String file) {
    if (!fileSystemModel.selectedFiles.contains(file))
      fileSystemModel.selectedFiles.add(file);
  }

  public void setComponent(Component comp) {
    if (comp != null && currentComponent == comp)
      return;
    int localtion = splitPane.getDividerLocation();
    filePane = new JScrollPane(comp);
    splitPane.setRightComponent(filePane);
    splitPane.setDividerLocation(localtion);
    currentComponent = comp;
  }

  public void changeRoot(File file) {
    String newFolder = Utils.selectFolder(file.getAbsolutePath());
    if (newFolder != null && !newFolder.isEmpty()) {
      refreshTree(new File(newFolder), null);
    }
  }

  public void clearCachedFiles(Object parent) {
    File directory = (File) parent;
    if (files.containsKey(directory.getAbsolutePath()))
      files.remove(directory.getAbsolutePath());
  }

  public void clearCachedFiles(String folder) {
    if (folder == null)
      return;
    if (files.containsKey(folder))
      files.remove(folder);
  }

  public void refreshTree() {
    TreePath path = fileTree.getSelectionPath();
    if (path == null) {
      // File rootNode = (File) fileTree.getModel().getRoot();
      path = new TreePath(fileTree.getModel().getRoot());
    }
    refreshTree(rootFolder, path);
  }

  public void refreshTree(TreePath selectionPath) {
    refreshTree(rootFolder, selectionPath);
  }

  public void refreshTree(File directory, TreePath selectionPath) {
    String parentFolder = getCurrentFolder(selectionPath);
    clearCachedFiles(parentFolder);
    // clearCachedFiles(directory);

    Map<String, List<String>> selectedFolders = fileSystemModel == null ? new LinkedHashMap<String, List<String>>()
        : fileSystemModel.selectedFolders;
    // List<String> selectedFiles = fileSystemModel == null ? new
    // LinkedList<String>() : fileSystemModel.selectedFiles;

    fileSystemModel = new FileSystemModel(directory);
    fileSystemModel.fileBrowser = this;
    // fileSystemModel.selectedFiles = selectedFiles;
    fileSystemModel.selectedFolders = selectedFolders;

    rootFolder = directory;
    if (fileTree == null) {
      fileTree = Utils.createJTree(fileSystemModel);
      fileTree.myPane = this.myPane;
    } else
      fileTree.setModel(fileSystemModel);
    fileSystemModel.fileTree = fileTree;

    if (selectionPath != null && directory != null) {
      selectPath(selectionPath);
    }
    this.validate();
  }

  public void selectPath(TreePath treePath) {
    File selectedTreeNode = ((File) (treePath.getLastPathComponent()));
    if (selectedTreeNode.isDirectory()) {
      File n = (File) fileTree.getModel().getChild(selectedTreeNode, 0);
      if (n == null)
        return;
      TreePath path = treePath.pathByAddingChild(n);
      // fileTree.expandPath(path);
      fileTree.setSelectionPath(path);
    } else {
      fileTree.setSelectionPath(treePath);
    }
  }

  public void refreshEditor(File file) {
    String content = getFileDetails(file);
    if (content == null) {
      LogManager.getLogger().error("File content is null: " + file.getAbsolutePath());
      return;
    }
    currentDataObject = content;
    txtContent.setText(content);
    txtContent.setCaretPosition(0);
    if (!content.isEmpty() && autoCopyToClipboard)
      Utils.copyToClipboard(content, fileTree);

    setEditable(false);
  }

  public JPanel getContentPane() {
    return this;
  }

  public String getCurrentFolder() {
    File file = getCurrentFile();
    String fileParentPath = file != null ? file.getParent() : "";
    return fileParentPath;
  }

  public String getCurrentFolder(TreePath selectionPath) {
    if (selectionPath == null)
      return "";
    File file = (File) selectionPath.getLastPathComponent();
    String fileParentPath = file.isDirectory() ? file.getAbsolutePath() : file.getParent();
    return fileParentPath;
  }

  public File getCurrentFile() {
    return (File) fileTree.getLastSelectedPathComponent();
  }

  public File getSelectedFile() {
    return (File) fileTree.getLastSelectedPathComponent();
  }

  public void setEditable(boolean value) {
    txtContent.setEditable(value);
    txtContent.setBackground(value ? Settings.ColorEditBG : Settings.ColorReadOnlyBG);

  }

  public String getContent() {
    return txtContent.getText();
  }

  public String getText() {
    return getContent();
  }

  public void setText(String content) {
    setContent(content);
  }

  public void setContent(String content) {
    txtContent.setText(content);
    txtContent.setCaretPosition(0);
  }

  public void save() {
    File file = getCurrentFile();
    if (file.isFile() && txtContent.isEditable()) {
      Utils.saveFile(file.getAbsolutePath(), getContent());
      setEditable(false);
    }
  }

  public void edit() {
    File file = getCurrentFile();
    if (file.isFile()) {
      originalContent = getContent();
      setEditable(true);
      fileTree.setEnabled(false);
    }
  }

  public void cancel() {
    setContent(originalContent);
    setEditable(false);
    fileTree.setEnabled(true);
  }

  public void delete() {
    File file = getCurrentFile();
    if (file.isFile()) {
      FilesService.deleteFile(file.getAbsolutePath());
      refreshTree();
    } else {
      MyDialog.showException("Delete folder is not allowed");
    }
  }

  public String getFileDetails(File file) {
    if (file == null || file.isDirectory())
      return "";
    return Utils.getContentFromFile(file);
  }
}

class FileSystemModel implements TreeModel {
  private File root;
  private File nullFile = new File("");

  private Vector listeners = new Vector();
  public String fileFilter;
  public MyTree fileTree;
  public MyFileBrowser fileBrowser;
  public List<String> selectedFiles = new LinkedList<String>();
  public Map<String, List<String>> selectedFolders = new LinkedHashMap<String, List<String>>();

  public FileSystemModel(File rootDirectory) {
    root = rootDirectory;
  }

  public FileSystemModel(String filter) {
    root = Utils.getUserHomeDirectory();
    fileFilter = filter;
  }

  public FileSystemModel() {
    root = Utils.getUserHomeDirectory();
  }

  public FileSystemModel(File rootDirectory, String filter) {
    root = rootDirectory;
    fileFilter = filter;
  }

  public Object getRoot() {
    if (root == null)
      return nullFile;
    return root;
  }

  public String[] getFiles(Object parent) {
    return getFiles(parent, true);
  }

  public boolean isNull(Object parent) {
    return parent == null || parent.equals(nullFile);
  }

  public String[] getFiles(Object parent, boolean loadFromCached) {
    String[] children;
    if (isNull(parent)) {
      children = selectedFolders.keySet().toArray(new String[0]);
    } else if (selectedFolders.containsKey(parent.toString())) {
      return selectedFolders.get(parent.toString()).toArray(new String[0]);
    } else {
      File directory = (File) parent;

      if (loadFromCached && fileBrowser.files.containsKey(directory.getAbsolutePath()))
        children = fileBrowser.files.get(directory.getAbsolutePath());
      else {
        children = fileFilter != null ? directory.list(new MyFilenameFilter(fileFilter)) : directory.list();
      }
      if (children != null && children.length > 1) {
        // System.out.println("Tree getFiles " + String.join(",", children));

        if (fileBrowser.sortByName) {
          Arrays.sort(children, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
              File f1 = new File(directory.getAbsolutePath() + "\\" + o1);
              File f2 = new File(directory.getAbsolutePath() + "\\" + o2);
              if (f1.isDirectory() && f2.isDirectory())
                return o1.compareToIgnoreCase(o2);
              if (f1.isFile() && f2.isFile())
                return o1.compareToIgnoreCase(o2);
              if (f1.isDirectory() && f2.isFile())
                return -1;
              return 1;
            }
          });
        } else {

          Arrays.sort(children, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
              File f1 = new File(directory.getAbsolutePath() + "\\" + o1);
              File f2 = new File(directory.getAbsolutePath() + "\\" + o2);
              if (f1.isDirectory() && f2.isDirectory())
                return -1 * Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
              ;
              if (f1.isFile() && f2.isFile())
                return -1 * Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
              ;
              if (f1.isDirectory() && f2.isFile())
                return -1;
              return 1;
            }
          });

          // for (int i = 0; i < children.length; i++) {
          // File f1 = new File(directory.getAbsolutePath() + "\\" + children[i]);
          // if (f1.isFile())
          // children[i] = children[i] + "......." + Utils.showDate(f1.lastModified(),
          // "d.M.Y");
          // }
        }
      }

      fileBrowser.files.put(directory.getAbsolutePath(), children);
    }

    return children;
  }

  public Object getChild(Object parent, int index) {
    File directory = (File) parent;
    String[] children = getFiles(parent);
    if (children == null || children.length == 0 || index >= children.length)
      return null;
    if (isNull(directory) || selectedFolders.containsKey(directory.getName()))
      directory = null;
    return new TreeFile(directory, children[index]);
  }

  public int getChildCount(Object parent) {
    File file = (File) parent;
    if (isNull(parent) || file == null || file.isDirectory()) {
      String[] fileList = getFiles(parent);
      if (fileList != null)
        return fileList.length;
    }
    return 0;
  }

  public boolean isLeaf(Object node) {
    File file = (File) node;
    return file.isFile();
  }

  public int getIndexOfChild(Object parent, Object child) {
    File directory = (File) parent;
    File file = (File) child;
    String[] children = getFiles(parent);
    for (int i = 0; i < children.length; i++) {
      if (file.getName().equals(children[i])) {
        return i;
      }
    }
    return -1;

  }

  public String getCurrentFolder() {
    File file = getCurrentFile();
    String fileParentPath = file != null ? file.getParent() : "";
    return fileParentPath;
  }

  public File getCurrentFile() {
    return fileBrowser.getCurrentFile();
  }

  public void valueForPathChanged(TreePath path, Object value) {
    File oldFile = (File) path.getLastPathComponent();
    String fileParentPath = oldFile.getParent();
    String oldFileName = oldFile.getName();
    String newFileName = (String) value;
    File newFile = new File(fileParentPath, newFileName);
    boolean changed = false;
    try {
      Files.move(oldFile.toPath(), newFile.toPath());
      Utils.showToast("File name changed to [" + newFileName + "]");
      changed = true;
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      changed = false;
    }
    if (changed) {
      File parent = new File(fileParentPath);
      int[] changedChildrenIndices = { getIndexOfChild(parent, newFile) };
      Object[] changedChildren = { newFile };
      try {
        fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
      } catch (Exception ex) {

      }

      if (fileBrowser != null) {
        fileBrowser.clearCachedFiles(fileParentPath);
        TreePath newPath = Utils.getTreePath(this,
            path.toString().replace(oldFileName, newFileName));
        fileBrowser.refreshTree(newPath);
      }
    }
  }

  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
    Iterator iterator = listeners.iterator();
    TreeModelListener listener = null;
    while (iterator.hasNext()) {
      listener = (TreeModelListener) iterator.next();
      listener.treeNodesChanged(event);
    }

  }

  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }

  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }

  private class TreeFile extends File {
    public String fileName = "";
    public File parentFile = null;
    public Boolean isDirectory = false;
    public Boolean isReal = true;

    public TreeFile(File parent, String child) {
      super(parent, child);
      parentFile = parent;
      if (parent == null || parent.getName().isEmpty() || !parent.getAbsolutePath().contains("\\")) {
        isDirectory = true;
        isReal = false;
      }
      fileName = child;
    }

    // public TreeFile(File parent, String child) {
    // this(parent, child, parent == null);
    // }

    @Override
    public String toString() {
      if (parentFile == null)
        return super.toString();

      String tmp = getName();
      if (tmp.isEmpty())
        tmp = fileName;
      return tmp;
    }

    @Override
    public boolean isDirectory() {
      if (super.isDirectory() || super.isFile())
        return super.isDirectory();
      File f = new File(fileName);
      return (f.isDirectory() || f.isFile()) ? f.isDirectory() : isDirectory;
    }

    @Override
    public boolean isFile() {
      if (super.isDirectory() || super.isFile())
        return super.isFile();
      File f = new File(fileName);
      return (f.isDirectory() || f.isFile()) ? f.isFile() : !isDirectory;
    }
  }

  public class MyFilenameFilter implements FilenameFilter {
    public String fileFilter;

    public MyFilenameFilter(String filter) {
      fileFilter = filter;
    }

    @Override
    public boolean accept(File dir, String fileName) {
      if (fileFilter == null || fileFilter.isEmpty() || fileFilter.equalsIgnoreCase("*")
          || fileFilter.equalsIgnoreCase(".*"))
        return true;
      File file = new File(dir.getAbsolutePath() + "\\" + fileName);
      if (file.isHidden())
        return false;
      String[] filters = fileFilter.split(",");
      for (String filter : filters) {
        if (!filter.isEmpty() && fileName.toLowerCase().endsWith(filter))
          return true;
      }
      return false;
    }
  }
}