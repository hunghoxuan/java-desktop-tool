package com.rs2.core.components.myeditor;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

//Inspired by: https://github.com/kdekooter/xml-text-editor

public class MyViewFactory extends Object implements ViewFactory {

    /**
     * @see javax.swing.text.ViewFactory#create(javax.swing.text.Element)
     */
    public View create(Element element) {
        return new MyView(element);
    }

}