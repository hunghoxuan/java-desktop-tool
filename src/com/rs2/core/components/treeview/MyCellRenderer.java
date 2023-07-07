package com.rs2.core.components.treeview;

import javax.swing.JLabel;
import javax.swing.JTree;

import com.rs2.core.settings.Settings;

import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Color;
import java.awt.Component;

import com.rs2.core.utils.Utils;

public class MyCellRenderer extends DefaultTreeCellRenderer {
    private final JLabel label;

    public MyCellRenderer() {
        label = Utils.createLabel("");

        setBackgroundSelectionColor(Settings.ColorSelectedText);
        setOpaque(true);
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return Settings.ColorTreeBG;
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return Settings.ColorSelectedText;
    }

    @Override
    public Color getBackground() {
        return Settings.ColorTreeBG;
    }

    // @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
                hasFocus);

        if (selected)
            label.setForeground(Settings.ColorSelectedText);
        else
            label.setForeground(Color.DARK_GRAY);
        return label;

    }
}
