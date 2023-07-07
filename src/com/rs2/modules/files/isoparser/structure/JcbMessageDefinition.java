package com.rs2.modules.files.isoparser.structure;

import java.util.HashMap;
import java.util.Map;

import com.rs2.modules.files.isoparser.Trace;
import com.rs2.modules.files.isoparser.elements.iso.IsoField;
import com.rs2.modules.files.isoparser.elements.iso.IsoFile;
import com.rs2.modules.files.isoparser.elements.iso.IsoMessage;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

JCB Interchange File structure definition class
 */

public class JcbMessageDefinition extends IsoMessageDefinition {
    private String pdeBuffer = "";

    @Override
    public Map<Integer, IsoFieldDefinition> getFieldDefinitions() {
        if (isoFieldDefinitions != null)
            return isoFieldDefinitions;

        isoFieldDefinitions = new HashMap<Integer, IsoFieldDefinition>();

        isoFieldDefinitions.put(2,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 2", false, true));
        isoFieldDefinitions.put(3, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 6, "Bit 3", false));
        isoFieldDefinitions.put(4, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 4", false));
        isoFieldDefinitions.put(5, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 5", false));
        isoFieldDefinitions.put(6, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 6", false));
        isoFieldDefinitions.put(9, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 8, "Bit 9", false));
        isoFieldDefinitions.put(10, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 8, "Bit 10", false));
        isoFieldDefinitions.put(12, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 12", false));
        isoFieldDefinitions.put(14, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 4, "Bit 14", false));
        isoFieldDefinitions.put(16, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 4, "Bit 16", false));
        isoFieldDefinitions.put(22, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 22", false));
        isoFieldDefinitions.put(23, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 23", false));
        isoFieldDefinitions.put(24, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 24", false));
        isoFieldDefinitions.put(25, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 4, "Bit 25", false));
        isoFieldDefinitions.put(26, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 4, "Bit 26", false));
        isoFieldDefinitions.put(30, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 24, "Bit 30", false));
        isoFieldDefinitions.put(31, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 31", false));
        isoFieldDefinitions.put(32, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 32", false));
        isoFieldDefinitions.put(33, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 33", false));
        isoFieldDefinitions.put(37, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 12, "Bit 37", false));
        isoFieldDefinitions.put(38, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 6, "Bit 38", false));
        isoFieldDefinitions.put(40, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 40", false));
        isoFieldDefinitions.put(41, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 8, "Bit 41", false));
        isoFieldDefinitions.put(42, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 15, "Bit 42", false));
        isoFieldDefinitions.put(43, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 43", false));
        isoFieldDefinitions.put(48, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 48", false));
        isoFieldDefinitions.put(49, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 49", false));
        isoFieldDefinitions.put(50, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 50", false));
        isoFieldDefinitions.put(51, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 3, "Bit 51", false));
        isoFieldDefinitions.put(54, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 54", false));
        isoFieldDefinitions.put(55, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 55", true));
        isoFieldDefinitions.put(62, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 62", false));
        isoFieldDefinitions.put(71, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 8, "Bit 71", false));
        isoFieldDefinitions.put(72, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 72", false));
        isoFieldDefinitions.put(93, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 93", false));
        isoFieldDefinitions.put(94, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 94", false));
        isoFieldDefinitions.put(97, new IsoFieldDefinition(IsoFieldDefinition.LengthType.Fixed, 17, "Bit 97", false));
        isoFieldDefinitions.put(100,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 2, "Bit 100", false));
        isoFieldDefinitions.put(123,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 123", false));
        isoFieldDefinitions.put(124,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 124", false));
        isoFieldDefinitions.put(125,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 125", false));
        isoFieldDefinitions.put(126,
                new IsoFieldDefinition(IsoFieldDefinition.LengthType.Embedded, 3, "Bit 126", false));

        return isoFieldDefinitions;
    }

    @Override
    public void afterMessageParsed(IsoMessage msg) throws ApplicationDataParseError {
        getPdeBuffer(msg);
        try {
            while (pdeBuffer.length() > 0) {
                String tag = bufRead(4);
                String len = bufRead(3);
                Integer l = Integer.parseInt(len);
                String data = bufRead(l);

                IsoField d = new IsoField();
                d.name = "PDE" + tag;
                d.parsedData = data;
                msg.addField(d);
            }
        } catch (Exception e) {
            throw new ApplicationDataParseError("Can not parse PDE: " + e.getMessage());
        }
    }

    @Override
    public void afterFileParsed(IsoFile file) {

    }

    private String bufRead(Integer len) throws ApplicationDataParseError {
        if (len > pdeBuffer.length())
            throw new ApplicationDataParseError("No enough data in PDE buffer");
        String rd = pdeBuffer.substring(0, len);
        pdeBuffer = pdeBuffer.substring(len, pdeBuffer.length());
        return rd;
    }

    private void getPdeBuffer(IsoMessage msg) {
        pdeBuffer += msg.isoFields.containsKey(48) ? msg.isoFields.get(48).parsedData : "";
        pdeBuffer += msg.isoFields.containsKey(62) ? msg.isoFields.get(62).parsedData : "";
        pdeBuffer += msg.isoFields.containsKey(123) ? msg.isoFields.get(123).parsedData : "";
        pdeBuffer += msg.isoFields.containsKey(124) ? msg.isoFields.get(124).parsedData : "";
        pdeBuffer += msg.isoFields.containsKey(125) ? msg.isoFields.get(125).parsedData : "";
        pdeBuffer += msg.isoFields.containsKey(126) ? msg.isoFields.get(126).parsedData : "";
    }

    // @Override
    // public void runReport(IsoFile file, String report) {
    // Trace.error("JCB", "No reports implemented yet");
    // }
}
