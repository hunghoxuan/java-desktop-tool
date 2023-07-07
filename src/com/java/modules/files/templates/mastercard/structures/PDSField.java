package com.java.modules.files.templates.mastercard.structures;

import com.java.modules.files.isoparser.elements.iso.IsoField;

public class PDSField extends IsoField {

    public PDSField() {
        super();
    }

    public PDSField(IsoField fld) {
        super(fld);
        initAttributes();
    }

    public PDSField0105 PDS_0105;
    public IsoField PDS_0306;
    public IsoField PDS_0394;
    public IsoField PDS_0395;
    public IsoField PDS_0300;
    public IsoField PDS_0301;
}