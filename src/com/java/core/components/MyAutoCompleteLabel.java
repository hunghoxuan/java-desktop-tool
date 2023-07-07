package com.java.core.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import com.java.core.settings.Settings;

//hold local params (data) for each tab.
public class MyAutoCompleteLabel extends JLabel {

    private boolean focused = false;
    private final MyAutoComplete autoSuggestor;
    private Color suggestionsTextColor, suggestionBorderColor;

    public MyAutoCompleteLabel(String string, final Color borderColor, Color suggestionsTextColor,
            MyAutoComplete autoSuggestor) {
        super(string);
        this.setFont(new Font(Settings.FontName, Font.PLAIN, Settings.FontSizeNormal));
        this.setBackground(Settings.ColorReadOnlyBG);
        // this.setPreferredSize(new Dimension(Settings.LabelWidth * 2,
        // Settings.LabelHeight));
        this.suggestionsTextColor = suggestionsTextColor;
        this.autoSuggestor = autoSuggestor;
        this.suggestionBorderColor = borderColor;
        this.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                autoSuggestor.setText(getText());
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "OK");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "OK");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "Cancel");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "Cancel");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, 0, true), "Cancel");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, true), "Cancel");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.KEY_TYPED, 0, false), "Typed");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
                0, true), "OK");

        getActionMap().put("OK", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                autoSuggestor.setText(getText());
            }
        });

        getActionMap().put("Typed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                autoSuggestor.setText(getText());
            }
        });

        getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                autoSuggestor.clear();
            }
        });
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }
}