package com.java.core.components;

import java.awt.Component;

import javax.swing.JTextArea;

import com.java.core.base.MyPane;
import com.java.core.settings.Settings;
import com.java.core.utils.Utils;

import java.awt.BorderLayout;

//hold local params (data) for each tab.
public class MyTextAreaPane extends MyPane {

    public MyTextAreaPane(Component view) {
        super(view);
    }

    public JTextArea getLogTextArea() {
        if (component != null && component instanceof JTextArea) {
            logTextArea = (JTextArea) component;
            return (JTextArea) component;
        }
        if (logTextArea == null) {
            logTextArea = Utils.createJTextArea();
            logTextArea.setForeground(Settings.ColorReadOnlyText);
        }
        component = logTextArea;
        return (JTextArea) this.logTextArea;
    }

    public void initUI(Component view) {
        super.initUI(view);
        setMainPanel(getLogTextArea());
        this.getBottomJPanel().setVisible(false);
        this.getParamsPanel().setVisible(false);

        logger.autoScrollBottom = true;
    }

    public void refreshLayout() {
        // super.refreshLayout();
        getJPanel().add(getMenuJpanel(), BorderLayout.PAGE_START);
        getJPanel().add(getMainJPanel(), BorderLayout.CENTER);
        // getJPanel().add(getBottomJPanel(), BorderLayout.PAGE_END);
        getParamsPanel().setVisible(false);
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
        btnClearLog.setVisible(true);
        this.btnHideLog.setVisible(false);
    }

    public void clearLog() {
        getLogTextArea().setText("");
    }
}