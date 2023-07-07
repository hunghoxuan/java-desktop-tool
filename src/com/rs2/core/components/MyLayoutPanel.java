package com.rs2.core.components;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.rs2.core.settings.Settings;
import com.rs2.core.utils.Utils;

import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

//hold local params (data) for each tab.
public class MyLayoutPanel extends JPanel {
    public javax.swing.GroupLayout layout;
    public String viewOption = Settings.viewDetail;
    public Component component;
    public String panelId;
    public JLabel label;
    public MyLayoutPanel parent;
    public JPanel contentPanel;
    public boolean isLabelVertical = true;

    public int labelWidth = Settings.LabelWidth;
    public int labelHeight = Settings.LabelHeight;
    public int controlWidth = Settings.TextFieldWidth;
    public int borderPadding = 5;
    public boolean isGroupPanel = true;
    public boolean useContentPanelAsContentPane = false;

    public JPanel getContentPane() {
        if (useContentPanelAsContentPane) {
            if (contentPanel == null) {
                contentPanel = Utils.createJPanel(isLabelVertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS, false);
                contentPanel.setBackground(Color.RED);
                ;
                this.setLayout(new GridLayout(0, 1));
                this.add(contentPanel);
            }
            return contentPanel;
        } else {
            // this.setBackground(Color.RED);;
            return this;
        }
    }

    public MyLayoutPanel(int layoutType, boolean isFlowLayout) {
        super();
        this.setBackground(Settings.ColorReadOnlyBG);
        ;

        // layout = new javax.swing.GroupLayout(getContentPane());
        // getContentPane().setLayout(layout);
        // layout.setAutoCreateGaps(true);

        if (isFlowLayout)
            getContentPane().setLayout(new FlowLayout(layoutType));
        else
            getContentPane().setLayout(new BoxLayout(getContentPane(), layoutType));
    }

    public MyLayoutPanel(boolean isVertical) {
        // this(isVertical ? BoxLayout.PAGE_AXIS : FlowLayout.TRAILING, !isVertical);
        this(isVertical ? BoxLayout.PAGE_AXIS : BoxLayout.LINE_AXIS, false);
        this.add(createGlue());
        this.isLabelVertical = isVertical;
    }

    public MyLayoutPanel() {
        this(true);
    }

    public MyLayoutPanel(String title) {
        this();
        if (title != null)
            getContentPane().setBorder(BorderFactory.createTitledBorder(title));
    }

    public MyLayoutPanel(String label, Component view, boolean isVertical) {
        this(isVertical);
        setLabel(label);
        this.addComponent(getLabel());
        this.addComponent(view);
    }

    public MyLayoutPanel(String label, Component view) {
        this(label, view, false);
    }

    public MyLayoutPanel getParentPanel() {
        return parent;
    }

    public MyLayoutPanel setLabelVertical(boolean isVertical) {
        this.isLabelVertical = isVertical;
        return this;
    }

    public MyLayoutPanel setTitle(String title) {
        this.setTitle(title);
        return this;
    }

    public MyLayoutPanel setLabelWidth(int width) {
        labelWidth = width;
        return this;
    }

    public JLabel getLabel() {
        if (label == null) {
            label = Utils.createLabel("");
            label.setSize(Settings.LabelWidth, Settings.LabelHeight);
        }
        return label;
    }

    public MyLayoutPanel setLabel(String label) {
        getLabel().setText(label);
        return this;
    }

    public MyLayoutPanel addVGroup(String title) {
        return addGroup(title, true);
    }

    public MyLayoutPanel addVGroup() {
        return addGroup(null, true);
    }

    public MyLayoutPanel addHGroup(String title) {
        return addGroup(title, false);
    }

    public MyLayoutPanel addHGroup() {
        return addGroup(null, false);
    }

    public MyLayoutPanel addVerticalGroup(String title) {
        return addGroup(title, true);
    }

    public MyLayoutPanel addVerticalGroup() {
        return addGroup(null, true);
    }

    public MyLayoutPanel addHorinzontalGroup(String title) {
        return addGroup(title, false);
    }

    public MyLayoutPanel addHorinzontalGroup() {
        return addGroup(null, false);
    }

    public MyLayoutPanel addGroup(String title) {
        return addGroup(title, false);
    }

    public MyLayoutPanel addGroup(boolean isVertical) {
        return addGroup(null, isVertical);
    }

    public MyLayoutPanel addGroup() {
        return addGroup(null, isLabelVertical);
    }

    public MyLayoutPanel addGroup(String title, boolean isVertical) {
        return addGroup(title, isVertical, null);
    }

    public MyLayoutPanel addGroup(String title, boolean isVertical, Component[] components) {
        MyLayoutPanel panel = new MyLayoutPanel(isVertical);

        panel.parent = this;
        if (title != null)
            panel.setBorder(BorderFactory.createTitledBorder(title));

        if (components != null && components.length > 0) {
            for (Component comp : components) {
                panel.addComponent(comp);
            }
        }

        addToContentPane(panel);
        return panel;
    }

    public MyLayoutPanel addToContentPane(Component comp) {
        checkComponent(comp);
        setAlignment(comp);

        getContentPane().add(comp, isLabelVertical ? BorderLayout.PAGE_START : BorderLayout.LINE_START);
        getContentPane().add(createGlue(), isLabelVertical ? BorderLayout.PAGE_START : BorderLayout.LINE_START);
        return this;
    }

    public MyLayoutPanel addComponentGroup() {
        MyLayoutPanel panel = addGroup(!isLabelVertical);
        panel.isGroupPanel = false;
        int padding = getBorderPadding();
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        setAlignment(panel);
        return panel;
    }

    public MyLayoutPanel endGroup() {
        return end();
    }

    public MyLayoutPanel end() {
        if (parent != null)
            return parent;
        return (MyLayoutPanel) getParent();
    }

    public void setAlignment(Component component) {
        if (component instanceof JComponent) {
            if (isLabelVertical)
                ((JComponent) component).setAlignmentX(Component.LEFT_ALIGNMENT);
            else
                ((JComponent) component).setAlignmentY(Component.TOP_ALIGNMENT);
        }
    }

    public boolean checkComponent(Component view) {
        if (view instanceof JTextField || view instanceof JTextArea || view instanceof JComboBox
                || view instanceof JCheckBox || view instanceof JList) {
            component = view;
            return true;
        } else if (view instanceof JLabel) {
            label = (JLabel) view;
        }
        return false;
    }

    public MyLayoutPanel addLabel(String text) {
        JLabel view = Utils.createLabel(text, Settings.LabelWidth);
        view.setPreferredSize(Utils.getControlDimension(view, Settings.SizeTypeSmall));
        view.setOpaque(true);
        view.setBackground(Settings.ColorReadOnlyBG);

        this.label = view;
        return addComponent(view);
    }

    public Component createGlue() {
        Component glue;
        if (isLabelVertical)
            glue = Box.createVerticalGlue();
        else
            glue = Box.createVerticalGlue();

        return glue;
    }

    public MyLayoutPanel addGap() {
        return addGap(1);
    }

    public MyLayoutPanel addGap(int count) {
        MyLayoutPanel _this = this;
        for (int i = 0; i < count; i++)
            _this.addComponent(Utils.createLabel("", 1), Utils.createLabel("", 1));
        return this;
    }

    public MyLayoutPanel addComponent(Component view) {
        if (view == null)
            view = Utils.createLabel("");
        // addToContentPane(view);
        addComponentGroup().addToContentPane(view);
        return this;
    }

    public MyLayoutPanel addComponent(JComboBox<String> view) {
        if (view == null)
            return this;
        component = view;
        // addToContentPane(view);
        addComponentGroup().addToContentPane(view);
        return this;
    }

    public MyLayoutPanel addComponent(String label, Component view, Component view2) {
        return addComponent(Utils.createLabel(label), view, view2);
    }

    public MyLayoutPanel addComponent(String label, Component view, Component view2, Component view3) {
        return addComponent(Utils.createLabel(label), view, view2, view3);
    }

    public MyLayoutPanel addComponent(String label, Component view) {
        if (view == null)
            return this;
        if (view instanceof JComponent) {
            setAlignment((JComponent) view);
            ((JComponent) view).putClientProperty(Settings.TagKey, label);
        }

        if (view.getName() == null || view.getName().isEmpty())
            view.setName(label);

        addComponentGroup().addLabel(label).addComponent(view).end();
        getLabel().setLabelFor(view);

        return this;
    }

    public int getLabelWidth() {
        return labelWidth;
    }

    public int getBorderPadding() {
        return borderPadding;
    }

    public MyLayoutPanel addComponent(Component label, Component view) {
        if (label instanceof JPanel) {
        } else {
            label.setPreferredSize(Utils.getControlDimension(label, Settings.SizeTypeSmall));
        }
        addComponentGroup().addComponent(label).addComponent(view).end();
        return this;
    }

    public MyLayoutPanel addComponent(Component label, Component view, Component comp1) {
        if (label instanceof JPanel) {
        } else {
            label.setPreferredSize(Utils.getControlDimension(label, Settings.SizeTypeSmall));
        }
        addComponentGroup().addComponent(label).addComponent(view).addComponent(comp1).end();
        return this;
    }

    public MyLayoutPanel addComponent(Component label, Component view, Component comp1, Component comp2) {
        if (label instanceof JPanel) {
        } else {
            label.setPreferredSize(Utils.getControlDimension(label, Settings.SizeTypeSmall));
        }
        addComponentGroup().addComponent(label).addComponent(view).addComponent(comp1).addComponent(comp2).end();
        return this;
    }

    public MyLayoutPanel addComponent(Component[] components) {
        return addComponent(components, false);
    }

    public MyLayoutPanel addComponent(Component[] components, boolean isVertical) {
        MyLayoutPanel panel = addGroup(isVertical);
        for (Component comp : components) {
            panel.addComponent(comp);
        }
        return panel.end();
    }

    public MyLayoutPanel addHorizontal(Component[] components, boolean isVertical) {
        return addComponent(components, false);
    }

    public MyLayoutPanel addVertical(Component[] components, boolean isVertical) {
        return addComponent(components, true);
    }

    public MyLayoutPanel addComponent(String label, JComboBox<String> view) {
        if (view == null)
            return this;
        if (view.getName() == null || view.getName().isEmpty())
            view.setName(label);

        addComponentGroup().addLabel(label).addComponent(view).end();
        getLabel().setLabelFor(getOriginalComponent());

        return this;
    }

    public Component getOriginalComponent() {
        return component;
    }

    public String getText() {
        return getValue();

    }

    public String getValue() {
        if (component == null)
            return null;
        if (component instanceof JTextField || component instanceof JTextArea) {
            return ((JTextField) component).getText();
        } else if (component instanceof JTextArea) {
            return ((JTextArea) component).getText();
        } else if (component instanceof JComboBox) {
            return ((JComboBox<String>) component).getSelectedItem().toString();
        }
        // else if (component instanceof JCheckBox) {
        // return ((JCheckBox) component).getSelectedItem().toString();
        // }
        return null;
    }

    public String getId() {
        if (panelId != null && !panelId.isEmpty())
            return panelId;
        return "";
    }

    public void setId(String value) {
        panelId = value;
    }

}