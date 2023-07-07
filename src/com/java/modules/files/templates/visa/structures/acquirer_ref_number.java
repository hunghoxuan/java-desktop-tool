package com.java.modules.files.templates.visa.structures;

import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;

public class acquirer_ref_number extends AsciiMessage {
    public char[] ARN_01;
    public char[] ARN_02;
    public char[] ARN_03;
    public char[] ARN_04;
    public char[] ARN_05;

    public acquirer_ref_number() {
        addElement("ARN_01", 1);
        addElement("ARN_02", 6);
        addElement("ARN_03", 4);
        addElement("ARN_04", 11);
        addElement("ARN_05", 1);
    }

    public acquirer_ref_number(String content) {
        this();
        setDataString(content);
    }

    public void setDataString(String content) {
        super.setDataString(content);
        ARN_01 = getElement("ARN_01");
        ARN_02 = getElement("ARN_02");
        ARN_03 = getElement("ARN_03");
        ARN_04 = getElement("ARN_04");
        ARN_05 = getElement("ARN_05");
    }
}
