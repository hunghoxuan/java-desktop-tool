package com.java.modules.files.isoparser.structure;

import java.util.Map;

import com.java.modules.files.isoparser.configuration.AppConfig;
import com.java.modules.files.isoparser.elements.iso.IsoFile;
import com.java.modules.files.isoparser.elements.iso.IsoMessage;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.templates.mastercard.IpmMessageDefinition;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

Abstract structure definition class
 */

public abstract class IsoMessageDefinition {
    public Map<Integer, IsoFieldDefinition> isoFieldDefinitions;

    public class ApplicationDataParseError extends Exception {
        private String message;

        public ApplicationDataParseError(String msg) {
            message = msg;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private static IsoMessageDefinition instance = null;

    public abstract Map<Integer, IsoFieldDefinition> getFieldDefinitions();

    public abstract void afterMessageParsed(IsoMessage msg) throws ApplicationDataParseError;

    public abstract void afterFileParsed(IsoFile file);

    // public abstract void runReport(IsoFile file, String report);

    public void addField(int index, LengthType ltype, int l, String nm, String desc, boolean bin) {
        if (isoFieldDefinitions == null)
            isoFieldDefinitions = getFieldDefinitions();

        if (isoFieldDefinitions.get(index) != null) {
            isoFieldDefinitions.get(index).name = nm;
            isoFieldDefinitions.get(index).description = desc;
            isoFieldDefinitions.get(index).binary = bin;
            return;
        }
        IsoFieldDefinition field = new IsoFieldDefinition(ltype, l, nm, desc, bin);
        isoFieldDefinitions.put(index, field);
    }

    public void addField(int index, LengthType ltype, String nm, String desc, boolean bin) {
        if (isoFieldDefinitions == null)
            isoFieldDefinitions = getFieldDefinitions();
        if (isoFieldDefinitions.get(index) != null) {
            isoFieldDefinitions.get(index).name = nm;
            isoFieldDefinitions.get(index).description = desc;
            isoFieldDefinitions.get(index).binary = bin;
            return;
        }
        IsoFieldDefinition field = new IsoFieldDefinition(ltype, 0, nm, desc, bin);
        isoFieldDefinitions.put(index, field);
    }

    public void addField(int index, LengthType ltype, int l, String nm, String desc, boolean bin, boolean masked) {
        if (isoFieldDefinitions == null)
            isoFieldDefinitions = getFieldDefinitions();
        if (isoFieldDefinitions.get(index) != null) {
            isoFieldDefinitions.get(index).name = nm;
            isoFieldDefinitions.get(index).description = desc;
            isoFieldDefinitions.get(index).mask = masked;
            isoFieldDefinitions.get(index).binary = bin;

            return;
        }
        IsoFieldDefinition field = new IsoFieldDefinition(ltype, l, nm, desc, bin, masked);
        isoFieldDefinitions.put(index, field);
    }

    public void addSubField(int index, LengthType ltype, int l, String nm, String desc, boolean bin) {

        if (isoFieldDefinitions == null)
            isoFieldDefinitions = getFieldDefinitions();
        if (isoFieldDefinitions.get(index) == null) {
            addField(index, ltype, "", "", bin);
        }
        if (isoFieldDefinitions.get(index) != null) {
            isoFieldDefinitions.get(index).addChildren(ltype, l, nm, desc, bin);
        }
    }

    public IsoFieldDefinition getDefinition(int index) {
        if (isoFieldDefinitions == null)
            isoFieldDefinitions = getFieldDefinitions();
        if (isoFieldDefinitions.containsKey(index))
            return isoFieldDefinitions.get(index);
        return null;
    }

    public static IsoMessageDefinition getStructure(AppConfig cfg) {
        if (instance == null) {
            switch (cfg.variant) {
                case IPM:
                    instance = new IpmMessageDefinition();
                    break;
                case JCB:
                    instance = new JcbMessageDefinition();
                    break;
                default:
                    instance = null;
            }
        }
        return instance;
    }

    public static String getSubFieldStringConnector(String tag) {
        return "";
    }

}
