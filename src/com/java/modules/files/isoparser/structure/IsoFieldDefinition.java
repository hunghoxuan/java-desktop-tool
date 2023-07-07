package com.java.modules.files.isoparser.structure;

import java.util.ArrayList;
import java.util.List;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

ISO 85883 field definition class
 */

public class IsoFieldDefinition {
    public enum LengthType {
        Fixed, Embedded
    };

    public String name;
    public String description;
    public String groupName;
    public LengthType lengthType;
    public Integer length;
    public boolean binary = false;
    public boolean mask = false;
    public Integer index;
    public String subFieldConnector = "";

    public List<IsoFieldDefinition> children = new ArrayList<IsoFieldDefinition>();

    public List<IsoFieldDefinition> getChildren() {
        return children;
    }

    public void addChildren(LengthType ltype, int l, String nm, String desc, boolean bin) {
        IsoFieldDefinition child = new IsoFieldDefinition(ltype, l, nm, desc, bin);
        child.index = children.size();
        children.add(child);
        if (this.length == 0) {
            int parentL = 0;
            for (IsoFieldDefinition c : children) {
                parentL += c.length;
            }
            this.length = parentL;
        }
    }

    public String getName() {
        return name;
    }

    public Integer getIndex() {
        if (index == null && this.getName() != null) {
            String tmp = this.getName().toUpperCase().replace("DE", "").replace("PDS", "").replace("_", "").trim();

            try {
                index = Integer.parseInt(tmp);
            } catch (Exception ex) {
            }
        }
        return index;
    }

    public IsoFieldDefinition() {
        lengthType = LengthType.Fixed;
        length = 0;
    }

    public IsoFieldDefinition(LengthType ltype, Integer l, String nm, boolean bin) {
        lengthType = ltype;
        length = l;
        name = nm;
        binary = bin;
    }

    public IsoFieldDefinition(LengthType ltype, Integer l, String nm, boolean bin, boolean maskable) {
        lengthType = ltype;
        length = l;
        name = nm;
        binary = bin;
        mask = maskable;
    }

    public IsoFieldDefinition(LengthType ltype, Integer l, String nm, String desc, boolean bin, boolean maskable) {
        lengthType = ltype;
        description = desc;
        length = l;
        name = nm;
        binary = bin;
        mask = maskable;
    }

    public IsoFieldDefinition(LengthType ltype, Integer l, String nm, String desc, boolean bin) {
        lengthType = ltype;
        description = desc;
        length = l;
        name = nm;
        binary = bin;
    }

    public IsoFieldDefinition(LengthType ltype, Integer l, String nm, String desc, String groupname, boolean bin,
            boolean maskable) {
        lengthType = ltype;
        description = desc;
        groupName = groupname;
        length = l;
        name = nm;
        binary = bin;
        mask = maskable;
    }
}
