package com.rs2.modules.files.templates.mastercard;

import java.lang.reflect.Field;

import com.rs2.core.logs.LogManager;
import com.rs2.modules.files.isoparser.MyField;
import com.rs2.modules.files.isoparser.configuration.AppConfig;
import com.rs2.modules.files.isoparser.containers.CleanInputStream;
import com.rs2.modules.files.isoparser.elements.iso.IsoField;
import com.rs2.modules.files.isoparser.elements.iso.IsoMessage;
import com.rs2.modules.files.templates.mastercard.structures.PDSField;
import com.rs2.core.utils.Utils;

public class IpmMessage extends IsoMessage {

    public IpmMessage() {
        super();
    }

    public IpmMessage(AppConfig _cfg, CleanInputStream _in) {
        super(_cfg, _in);
    }

    public void initFields() {
        for (IsoField fld : getIsoFields()) {
            Utils.setFieldFromNameValue(this, fld.getName(), fld);
            if (fld.getName().equalsIgnoreCase("DE_48"))
                PDS = new PDSField(fld);
        }
    }

    @Override
    public void addField(IsoField fld) {
        super.addField(fld);
        Utils.setFieldFromNameValue(this, fld.getName(), fld);
        if (fld.getName().equalsIgnoreCase("DE_48"))
            PDS = new PDSField(fld);
    }

    @Override
    public void setMti(String mti) {
        super.setMti(mti);
        this.MTI = mti;
    }

    @Override
    public String getAttribute(String attribute) {
        attribute = attribute.replace("PDS.", "DE_48.");

        return super.getAttribute(attribute);
    }

    @Override
    public int setAttribute(String attribute, String value) {
        attribute = attribute.replace("PDS.", "DE_48.");
        if (attribute.equalsIgnoreCase("MTI"))
            MTI = value;
        return super.setAttribute(attribute, value);
    }

    @Override
    public String getDescription() {
        String def = getMti() + "-" + (isoFields.containsKey(24) ? isoFields.get(24).parsedData : "");
        switch (def) {
            case "1644-697":
                def += " File Header";
                break;
            case "1644-695":
                def += " File Trailer";
                break;
            case "1644-603":
                def += " Retrieval Request";
                break;
            case "1644-693":
                def += " Text Message";
                break;
            case "1644-699":
                def += " File Reject";
                break;
            case "1644-685":
                def += " Financial Position Detail";
                break;
            case "1644-688":
                def += " Settlement Position Detail";
                break;
            case "1644-680":
                def += " File Currency Summary";
                break;
            case "1644-691":
                def += " Message Exception";
                break;
            case "1644-640":
                def += " Currency Update";
                break;
            case "1240-200":
                def += " First Presentment";
                break;
            case "1240-205":
            case "1240-282":
                def += " Second Presentment";
                break;
            case "1442-450":
            case "1442-453":
            case "1442-451":
            case "1442-454":
                def += " Chargeback";
                break;
            default:
                ;
        }

        // if (offset != null)
        def += " (pos " + String.valueOf(offset) + ") [" + String.valueOf(getLength()) + "]";

        return def;
    }

    public IsoField DE_49;
    public IsoField DE_48;
    public IsoField DE_99; // DE_99;
    public IsoField DE_98; // DE_98;
    public IsoField DE_97; // DE_97;
    public IsoField DE_96; // DE_96;
    public IsoField DE_95; // DE_95;
    public IsoField DE_94; // DE_94;
    public IsoField DE_93; // DE_93;
    public IsoField DE_92; // DE_92;
    public IsoField DE_91; // DE_91;
    public IsoField DE_90; // DE_90;
    public IsoField DE_89; // DE_89;
    public IsoField DE_88; // DE_88;
    public IsoField DE_87; // DE_87;
    public IsoField DE_86; // DE_86;
    public IsoField DE_85; // DE_85;
    public IsoField DE_84; // DE_84;
    public IsoField DE_83; // DE_83;
    public IsoField DE_82; // DE_82;
    public IsoField DE_81; // DE_81;
    public IsoField DE_80; // DE_80;
    public IsoField DE_79; // DE_79;
    public IsoField DE_78; // DE_78;
    public IsoField DE_77; // DE_77;
    public IsoField DE_76; // DE_76;
    public IsoField DE_75; // DE_75;
    public IsoField DE_74; // DE_74;
    public IsoField DE_73; // DE_73;
    public IsoField DE_72; // DE_72;
    public IsoField DE_71; // DE_71;
    public IsoField DE_70; // DE_70;
    public IsoField DE_69; // DE_69;
    public IsoField DE_68; // DE_68;
    public IsoField DE_67; // DE_67;
    public IsoField DE_66; // DE_66;
    public IsoField DE_65; // DE_65;
    public IsoField DE_64; // DE_64;

    public IsoField DE_63; // DE_63;
    public IsoField DE_62; // DE_62;
    public IsoField DE_61; // DE_61;
    public IsoField DE_60; // DE_60;
    public IsoField DE_59; // DE_59;
    public IsoField DE_58; // DE_58;
    public IsoField DE_57; // DE_57;
    public IsoField DE_56; // DE_56;
    public IsoField DE_55; // DE_55;
    public IsoField DE_54; // DE_54;
    public IsoField DE_53; // DE_53;
    public IsoField DE_52; // DE_52;
    public IsoField DE_51; // DE_51;
    public IsoField DE_50; // DE_50;

    public PDSField PDS; // PDS;
    public IsoField DE_47; // DE_47) + 3;
    public IsoField DE_45; // DE_45;
    public IsoField DE_44; // DE_44;
    public IsoField DE_43; // DE_43;
    public IsoField DE_42; // DE_42;
    public IsoField DE_41; // DE_41;
    public IsoField DE_40; // DE_40;
    public IsoField DE_39; // DE_39;
    public IsoField DE_38; // DE_38;
    public IsoField DE_37; // DE_37;
    public IsoField DE_36; // DE_36;
    public IsoField DE_35; // DE_35;
    public IsoField DE_34; // DE_34;
    public IsoField DE_33; // DE_33;
    public IsoField DE_32; // DE_32;
    public IsoField DE_31; // DE_31;
    public IsoField DE_30; // DE_30;
    public IsoField DE_29; // DE_29;
    public IsoField DE_28; // DE_28;
    public IsoField DE_27; // DE_27;
    public IsoField DE_26; // DE_26;
    public IsoField DE_25; // DE_25;
    public IsoField DE_24; // DE_24;
    public IsoField DE_23; // DE_23;
    public IsoField DE_22; // DE_22;
    public IsoField DE_21; // DE_21;
    public IsoField DE_20; // DE_20;
    public IsoField DE_19; // DE_19;
    public IsoField DE_18; // DE_18;
    public IsoField DE_17; // DE_17;
    public IsoField DE_16; // DE_16;

    public IsoField DE_15; // DE_15;
    public IsoField DE_14; // DE_14;
    public IsoField DE_13; // DE_13;
    public IsoField DE_12; // DE_12;

    public IsoField DE_11; // DE_11;
    public IsoField DE_10; // DE_10;
    public IsoField DE_9; // DE_9;
    public IsoField DE_8; // DE_8;
    public IsoField DE_7; // DE_7;
    public IsoField DE_6; // DE_6;
    public IsoField DE_5; // DE_5;
    public IsoField DE_4; // DE_4;
    public IsoField DE_3; // DE_3;
    public IsoField DE_2; // DE_2;
    public String MTI;
}