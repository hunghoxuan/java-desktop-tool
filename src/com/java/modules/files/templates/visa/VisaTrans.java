package com.java.modules.files.templates.visa;

import java.util.LinkedList;
import java.util.List;

import com.java.core.logs.LogManager;
import com.java.modules.files.isoparser.MyField;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;
import com.java.modules.files.templates.visa.structures.*;
import com.java.core.utils.Utils;
import java.lang.reflect.Field;

public class VisaTrans {
    public char[] trans_code;
    public VisaBatch visaBatch;

    public int offset = 0;
    public int length = 0;

    public ARN ARN;
    public visa_b2_01_0 visa_b2_01_0;
    public visa_b2_01_9 visa_b2_01_9;
    public visa_b2_04_0 visa_b2_04_0;
    public visa_b2_04_9 visa_b2_04_9;
    public visa_b2_05_3_LG visa_b2_05_3_LG;
    public visa_b2_05_3_CA visa_b2_05_3_CA;
    public visa_b2_05_3_FL visa_b2_05_3_FL;
    public visa_b2_05_3_CR visa_b2_05_3_CR;
    public visa_b2_05_3_AN visa_b2_05_3_AN;
    public visa_b2_05_3 visa_b2_05_3;
    public visa_b2_05_E_JA visa_b2_05_E_JA;
    public visa_b2_05_E visa_b2_05_E;
    public visa_b2_05_4 visa_b2_05_4;
    public visa_b2_05_4_SFD visa_b2_05_4_SFD;
    public visa_b2_05_4_SFPD visa_b2_05_4_SFPD;
    public visa_b2_05_4_PD visa_b2_05_4_PD;
    public visa_b2_05_4_SVCRFD visa_b2_05_4_SVCRFD;
    public visa_b2_05_5 visa_b2_05_5;
    public visa_b2_05_6 visa_b2_05_6;
    public visa_b2_05_7 visa_b2_05_7;
    public visa_b2_05_8 visa_b2_05_8;
    public visa_b2_05_0 visa_b2_05_0;
    public visa_b2_05_1 visa_b2_05_1;
    public visa_b2_05_2_SE visa_b2_05_2_SE;
    public visa_b2_05_2_BR visa_b2_05_2_BR;
    public visa_b2_05_2_AR visa_b2_05_2_AR;
    public visa_b2_05_2 visa_b2_05_2;
    public visa_b2_05_3_AI visa_b2_05_3_AI;
    public visa_b2_05_9 visa_b2_05_9;
    public visa_b2_10_0 visa_b2_10_0;
    public visa_b2_10_1 visa_b2_10_1;
    public visa_b2_10_9 visa_b2_10_9;
    public visa_b2_10 visa_b2_10;
    public visa_b2_32_0_99 visa_b2_32_0_99;
    public visa_b2_32_0_GB visa_b2_32_0_GB;
    public visa_b2_32_0_DE visa_b2_32_0_DE;
    public visa_b2_32_0_AU visa_b2_32_0_AU;
    public visa_b2_32 visa_b2_32;
    public visa_b2_33_0_400083_POS visa_b2_33_0_400083_POS;
    public visa_b2_33_0_400083_PSR visa_b2_33_0_400083_PSR;
    public visa_b2_33_0_TRSVISABIN_HEADER visa_b2_33_0_TRSVISABIN_HEADER;
    public visa_b2_33_0_TRSVISABIN_TRAILE visa_b2_33_0_TRSVISABIN_TRAILE;
    public visa_b2_33_0_TRSVISABIN visa_b2_33_0_TRSVISABIN;
    public visa_b2_33_0_DSPLUSBIN_TAPEHE visa_b2_33_0_DSPLUSBIN_TAPEHE;
    public visa_b2_33_0_DSPLUSBIN_TAPETR visa_b2_33_0_DSPLUSBIN_TAPETR;
    public visa_b2_33_0_DSPLUSBIN visa_b2_33_0_DSPLUSBIN;
    public visa_b2_33_0_CFGEANSBII visa_b2_33_0_CFGEANSBII;
    public visa_b2_33_0_BIIDCCURR1 visa_b2_33_0_BIIDCCURR1;
    public visa_v2_33_0_SMSRAWDATA_V22000 visa_v2_33_0_SMSRAWDATA_V22000;
    public visa_v2_33_0_SMSRAWDATA_V22120 visa_v2_33_0_SMSRAWDATA_V22120;
    public visa_v2_33_0_SMSRAWDATA_V22220 visa_v2_33_0_SMSRAWDATA_V22220;
    public v22225_original_data_elements v22225_original_data_elements;
    public visa_v2_33_0_SMSRAWDATA_V22225 visa_v2_33_0_SMSRAWDATA_V22225;
    public visa_v2_33_0_SMSRAWDATA_V22210 visa_v2_33_0_SMSRAWDATA_V22210;
    public visa_v2_33_0_SMSRAWDATA_V22200 visa_v2_33_0_SMSRAWDATA_V22200;
    public visa_v2_33_0_SMSRAWDATA_V22240 visa_v2_33_0_SMSRAWDATA_V22240;
    public visa_v2_33_0_SMSRAWDATA_V22255 visa_v2_33_0_SMSRAWDATA_V22255;
    public visa_v2_33_0_SMSRAWDATA_V22270 visa_v2_33_0_SMSRAWDATA_V22270;
    public visa_v2_33_0_SMSRAWDATA_V22261 visa_v2_33_0_SMSRAWDATA_V22261;
    public visa_v2_33_0_SMSRAWDATA_V22900 visa_v2_33_0_SMSRAWDATA_V22900;
    public visa_v2_33_0_FEE_D visa_v2_33_0_FEE_D;
    public visa_v2_33_0_FEE_S visa_v2_33_0_FEE_S;
    public visa_v2_33_0_SMSRAWDATA_UNSUPPORTED visa_v2_33_0_SMSRAWDATA_UNSUPPORTED;
    public visa_b2_33_0 visa_b2_33_0;
    public visa_b2_33_1 visa_b2_33_1;
    public visa_v2_33_1_FEE visa_v2_33_1_FEE;
    public visa_b2_40_0_TS visa_b2_40_0_TS;
    public visa_b2_40_0_MS visa_b2_40_0_MS;
    public visa_b2_40_0_TR visa_b2_40_0_TR;
    public visa_b2_40_0 visa_b2_40_0;
    public visa_b2_40_1 visa_b2_40_1;
    public visa_b2_40_2 visa_b2_40_2;
    public visa_b2_40_3 visa_b2_40_3;
    public visa_b2_40_4 visa_b2_40_4;
    public visa_b2_40_7 visa_b2_40_7;
    public visa_b2_46_0 visa_b2_46_0;
    public visa_b2_46_1 visa_b2_46_1;
    public visa_b2_46 visa_b2_46;
    public visa_b2_48_7 visa_b2_48_7;
    public visa_b2_48_0_0 visa_b2_48_0_0;
    public visa_b2_48_0_1 visa_b2_48_0_1;
    public visa_b2_48_0_2 visa_b2_48_0_2;
    public visa_b2_48_1 visa_b2_48_1;
    public visa_b2_48_2 visa_b2_48_2;
    public visa_b2_48_6 visa_b2_48_6;
    public visa_b2_50_0_PURCHA visa_b2_50_0_PURCHA;
    public visa_b2_50_0_PURCHL visa_b2_50_0_PURCHL;
    public visa_b2_50_0_CORPAI visa_b2_50_0_CORPAI;
    public visa_b2_50_0_CORPAS visa_b2_50_0_CORPAS;
    public visa_b2_50_0_CORPLG visa_b2_50_0_CORPLG;
    public visa_b2_50_0_CORPCA visa_b2_50_0_CORPCA;
    public visa_b2_50_0_OPNFMT visa_b2_50_0_OPNFMT;
    public visa_b2_50_0 visa_b2_50_0;
    public visa_b2_50 visa_b2_50;
    public visa_b2_51_0 visa_b2_51_0;
    public visa_b2_51_1 visa_b2_51_1;
    public visa_b2_51_9 visa_b2_51_9;
    public visa_b2_51 visa_b2_51;
    public visa_b2_52_0 visa_b2_52_0;
    public visa_b2_52_1 visa_b2_52_1;
    public visa_b2_52_4 visa_b2_52_4;
    public visa_b2_52_4 visa_b2_52_5;
    public visa_b2_52_4 visa_b2_52_6;
    public visa_b2_52_4 visa_b2_52_7;
    public visa_b2_52_4 visa_b2_52_8;

    public visa_b2_55 visa_b2_55;
    public visa_b2_56_0 visa_b2_56_0;
    public visa_b2_56_1 visa_b2_56_1;
    public visa_b2_90 visa_b2_90;
    public visa_b2_91 visa_b2_91;
    public visa_b2 visa_b2;

    public List<AsciiMessage> messages = new LinkedList<AsciiMessage>();

    public void setAttribute(AsciiMessage data) {
        Utils.setFieldFromNameValue(this, data.getClassName(), data);
        messages.add(data);
    }

    public int setAttribute(String attribute, char[] data) {
        String[] attrs = attribute.split("\\.");

        Field class1 = Utils.getFieldFromName(this, attrs[0]);
        if (class1 != null) {
            class1.setAccessible(true);
            try {
                Utils.setFieldFromNameValue(class1.get(this), attrs[1], data);
                MyField fld = ((AsciiMessage) class1.get(this)).getField(attrs[1]);
                if (fld != null) {
                    fld.setData(new String(data));
                    return fld.offset;
                }
            } catch (Exception ex) {
                LogManager.getLogger().error(ex);
            }
        }
        return -1;
    }
}
