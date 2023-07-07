package com.java.core.components.myjtable;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.java.core.utils.Utils;

public class MyKeyAdapter extends KeyAdapter {
    private final MyJTable table;

    public MyKeyAdapter(MyJTable table) {
        this.table = table;
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.isControlDown()) {
            if (event.getKeyCode() == KeyEvent.VK_C) { // Copy
                table.copyToClipboard();
            }

            if (event.getKeyCode() == KeyEvent.VK_V) {
                table.pasteFromClipboard();
            }
        }
    }

}