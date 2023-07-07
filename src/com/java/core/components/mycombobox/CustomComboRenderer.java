package com.java.core.components.mycombobox;

import javax.swing.*;

import com.java.core.settings.Settings;

import java.awt.*;
import java.util.function.Supplier;

public class CustomComboRenderer extends DefaultListCellRenderer {
    public static final Color background = Settings.Color1;
    private static final Color defaultBackground = (Color) UIManager.get("List.background");
    private static final Color defaultForeground = (Color) UIManager.get("List.foreground");

    private Supplier<String> highlightTextSupplier;

    public CustomComboRenderer() {
    }

    public CustomComboRenderer(Supplier<String> highlightTextSupplier) {
        this.highlightTextSupplier = highlightTextSupplier;
    }

    public CustomComboRenderer(Supplier<String> highlightTextSupplier, boolean isFilterable) {
        this.highlightTextSupplier = highlightTextSupplier;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value == null)
            return this;
        String text = String.valueOf(value);
        if (highlightTextSupplier != null)
            text = HtmlHighlighter.highlightText(text, highlightTextSupplier.get());
        this.setText(text);
        if (!isSelected) {
            this.setBackground(index % 2 == 0 ? background : defaultBackground);
        }
        this.setForeground(defaultForeground);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal -
                1));
        return this;
    }
}