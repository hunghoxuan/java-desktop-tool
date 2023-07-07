package com.rs2.core.components.myeditor;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

//Inspired by: https://github.com/kdekooter/xml-text-editor

public class MyEditorKit extends StyledEditorKit {

    private static final long serialVersionUID = 2969169649596107757L;
    private ViewFactory xmlViewFactory;

    public MyEditorKit() {
        xmlViewFactory = new MyViewFactory();
    }

    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

}