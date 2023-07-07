package com.rs2.modules.codegen;

import com.rs2.core.components.MyTree;
import com.rs2.core.components.treeview.DataNode;
import com.rs2.core.components.treeview.LabelNode;
import com.rs2.core.settings.Settings;
import com.rs2.core.MainScreen;
import com.rs2.core.base.MyPane;

import javax.swing.text.JTextComponent;

import org.w3c.dom.Document;

import com.rs2.core.components.MyFileBrowser;
import com.rs2.core.data.DBLookup;
import com.rs2.core.data.DBParam;
import com.rs2.core.data.DBQuery;
import com.rs2.modules.dataviewer.DataViewerService;
import com.rs2.modules.db.DBService;
import com.rs2.core.utils.Utils;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.awt.GridLayout;

//hold local params (data) for each tab.
public class CodeGenPane extends MyPane {

}