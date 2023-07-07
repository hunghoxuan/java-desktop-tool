package com.java.modules.files.isoparser.elements.iso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.java.core.logs.LogManager;
import com.java.core.utils.Utils;

import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyField;
import com.java.modules.files.isoparser.Trace;
import com.java.modules.files.isoparser.configuration.AppConfig;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition;
import com.java.modules.files.isoparser.structure.IsoMessageDefinition;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.templates.mastercard.IpmMessageDefinition;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

Parsed field data class
 */

public class IsoField extends MyField {

    public byte[] rawConvertedData;
    public boolean masked = false;
    public Integer index;
    public boolean binary;
    public IsoFieldDefinition definition;
    public boolean isFullyParsed = false;
    public LengthType lengthType = LengthType.Fixed;

    public ArrayList<String> appParserProblems = new ArrayList<String>();
    public List<IsoField> children;

    public IsoField() {

    }

    public IsoField(IsoField fld) {
        offset = fld.offset;
        this.length = fld.getLength();
        lengthType = fld.getLengthType();
        name = fld.getName();
        children = fld.getChildren();
        initAttributes();
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        if (length <= 0)
            length = getDefinition().length;
        return length;
    }

    public int getActualLength() {
        if (getLengthType() == LengthType.Fixed)
            return getLength();

        return getLength() + parsedData.length();
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LengthType getLengthType() {
        if (lengthType == null)
            lengthType = getDefinition().lengthType;
        return lengthType;
    }

    public void setLengthType(LengthType lengthType) {
        this.lengthType = lengthType;
    }

    public IsoFieldDefinition getDefinition() {
        if (definition == null)
            definition = new IsoFieldDefinition();
        return definition;
    }

    public String getLengthDesc() {
        if (getLength() <= 0)
            return "";
        return getLengthType().equals(LengthType.Embedded) ? Utils.multiplyChars("L", getLength())
                : String.valueOf(getLength());
    }

    @Override
    public void setName(String value) {
        if (value != null && value.contains(":")) {
            String[] arr = value.split(":");
            this.name = arr[0].trim();
            this.description = arr[1].trim();
            return;
        }
        this.name = value;
    }

    public Integer getIndex() {
        if ((index == null || index < 0) && this.getName() != null) {
            index = FilesService.getFieldIndexByTag(this.getName());
        }
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    @Override
    public String asText() {
        return asText(null);
    }

    public List<IsoField> getChildren() {
        if (children == null) {
            children = new ArrayList<IsoField>();
        }
        return children;
    }

    public String asText(AppConfig cfg) {
        // String nm = getNameDsc();
        // if (cfg != null && cfg.offset) {
        // nm += (offset != null ? " (Offset 0x" + Long.toHexString(offset) + ")" : "");
        // }
        // if (masked)
        // return nm + ": (masked sensitive data)";

        String res = getNameDsc() + ": '" + getValue() + "'";

        if (appParserProblems.size() > 0) {
            res += "\n  Parsing problems:";
            for (String p : appParserProblems) {
                res += "\n     " + p;
            }
            res += "\n";
        }

        boolean needSplitter = false;

        if (cfg != null && cfg.raw) {
            if (rawData != null) {
                res += "\nRAW: " + FilesService.bin2hex(rawData);
                if (rawConvertedData != null && !Arrays.equals(rawConvertedData, rawData))
                    res += "\nRAW (converted): " + FilesService.bin2hex(rawConvertedData);

                needSplitter = true;
            }
        }

        if (children != null && children.size() > 0) {
            needSplitter = true;
            String addRes = "";
            for (IsoField child : children) {
                String cdata = "\n" + child.asText(cfg);
                addRes += cdata.replaceAll("\n", "\n    ");
            }

            res += addRes;
        }

        // if (needSplitter)
        // res += Settings.lineSeperator;

        return res;
    }

    @Override
    public String toString() {
        // return getValue();
        return "FieldData{" + name + " => " + parsedData + "}";
    }

    public void merge(OutputStream out) throws IOException {
        Trace.log("IsoField", "Merging field " + index.toString());
        IsoMessageDefinition mdef = AppConfig.get().getMesssageDef();
        if (mdef == null) {
            Trace.log("IsoField", "Failed to get message definition");
            throw new IOException("No message definition");
        }
        IsoFieldDefinition fdef = this.definition;

        if (fdef == null && this.index != null)
            mdef.getFieldDefinitions().get(index);

        if (fdef == null) {
            Trace.log("IsoField", "Failed to get field definition");
            throw new IOException("No field definition: " + name);
        }

        byte[] buff = null;
        if (rawData != null) {
            Trace.log("IsoField", "Will use predefined RAW data");
            buff = rawData;
        } else if (parsedData != null) {
            Trace.log("IsoField", "Preparing field data");
            buff = parsedData.getBytes(AppConfig.get().getRawCharset());
        }

        if (fdef.lengthType == LengthType.Embedded) {
            Trace.log("IsoField", "Writing embedded length value");
            Integer len = buff.length;
            String slen = len.toString();
            if (slen.length() > fdef.length) {
                Trace.log("IsoField", "Embedded length value is too long");
                throw new IOException("Field length value exceeds defined size" + name);
            }
            while (slen.length() < fdef.length)
                slen = "0" + slen;

            out.write(slen.getBytes(AppConfig.get().getRawCharset()));

        } else {
            if (buff.length != fdef.length) {
                Trace.log("IsoField", "Field value has wrong length. Should be  " + fdef.length);
                throw new IOException("Wrong field value length: " + name);
            }
        }

        out.write(buff);
    }

    private static byte[] readFixed(InputStream in, int len) throws IOException {
        byte[] buff = new byte[len];
        int cnt = in.read(buff);
        if (cnt != len) {
            Trace.log("IsoField", "No enough bytes to read next data potion");
            throw new IOException("No enough bytes");
        }
        return buff;
    }

    private static byte[] readEmbedded(InputStream in, int len) throws IOException {
        byte[] rawLen = null;
        try {
            Trace.log("IsoField", "Reading embedded length");
            rawLen = readFixed(in, len);
        } catch (IOException e) {
            Trace.log("IsoField", "Failed to read embedded length");
            throw new IOException("Can not read field length", e);
        }

        String rawLenString = new String(FilesService.convertBytesOnRead(rawLen, AppConfig.get().getRawCharset()));
        Integer flen = (rawLenString == null || rawLenString.trim().isEmpty()) ? 0
                : Integer.parseInt(rawLenString.trim());
        Trace.log("IsoField", "Embedded length parsed");
        if (flen > 0) {
            try {
                Trace.log("IsoField", "Reading value");
                return readFixed(in, flen);
            } catch (IOException e) {
                Trace.log("IsoField", "Failed to read value");
                throw new IOException("Can not read field data", e);
            }
        } else {
            return new byte[0];
        }
    }

    public static IsoField read(InputStream in, IsoFieldDefinition def) throws IOException {
        Trace.log("IsoField", "Reading field " + def.index.toString());
        IsoField field = new IsoField();
        field.setIndex(def.index);
        field.definition = def;

        byte[] buff = null;

        try {
            switch (def.lengthType) {
                case Fixed:
                    Trace.log("IsoField", "Fixed length field");
                    buff = readFixed(in, def.length);
                    break;
                case Embedded:
                    Trace.log("IsoField", "Embedded length field");
                    buff = readEmbedded(in, def.length);
                    break;
            }
        } catch (IOException e) {
            Trace.log("IsoField", "Error reading field: " + e.getMessage());
            throw new IOException("Can not read field " + field.getIndex().toString(), e);
        }

        field.setRawData(def.binary ? buff : FilesService.convertBytesOnRead(buff, AppConfig.get().getRawCharset()));
        if (!def.binary) {
            Trace.log("IsoField", "Parsed string data representation");
            field.setData(new String(field.getRawData()));
        }
        field.setBinary(def.binary);
        return field;
    }

    public Map<String, Object> asPropertiesObject() {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put(
                getNameDsc(),
                this.getValue());
        if (this.getChildren() != null && this.getChildren().size() > 0) {
            for (IsoField child : this.getChildren()) {
                root.put(
                        FilesService.subFieldPrefix + child.getNameDsc(),
                        child.getValue());
            }
        }
        return root;
    }

    public String getNameDsc() {
        String res = (getName().toUpperCase().trim()
                + (getDescription().isEmpty() ? "" : (" : " + getDescription().toLowerCase()))).trim();
        res += " (pos " + String.valueOf(offset) + ")";
        res += " [" + getLengthDesc() + "]";
        return res;
    }

    public boolean isDynamicLengthLL() {
        return getLengthType().equals(LengthType.Embedded) && getLength() == 3;
    }

    public void initAttributes() {
        if (children != null && children.size() > 0) {
            for (IsoField child : children) {
                Utils.setFieldFromNameValue(this, child.getName(), child);
                Field field = Utils.getFieldFromName(this, child.getName());
                if (field != null) {
                    try {
                        ((IsoField) field.get(this)).initAttributes();
                    } catch (Exception ex) {
                        LogManager.getLogger().error(ex);
                    }
                }
            }
        }
    }

    public void parseChildrenAndUpdateParent() {
        if (isFullyParsed)
            return;

        String lastTag = "";
        String lastData = "";
        int lastLength = 0;

        if (getChildren().size() == 0) {
            return; // refresh calculation
        }

        setData(""); // refresh calculation
        length = 0;

        for (IsoField fld : getChildren()) {
            String subFldConnector = IpmMessageDefinition.getSubFieldStringConnector(fld.getName());

            String parentData = getData() == null ? "" : getData();
            if (isDynamicLengthLL()) {
                String tmp = FilesService.getFieldIndexStringByTag(fld.getName()); // PDS_0105_xx ==> 0105
                if (tmp.equalsIgnoreCase(lastTag)) {
                    lastData += fld.getData();
                    lastLength += fld.getLength();
                    continue;
                } else {
                    if (!lastTag.isEmpty()) {
                        // 4 bytes = tag, 3 bytes = length
                        setData(parentData + lastTag + Utils.intToFixedLengthString(lastLength, 3) + lastData);
                    }
                    lastTag = tmp;
                    lastData = fld.getData();
                    lastLength = fld.getLength();
                }
            } else // if (getLengthType().equals(LengthType.Fixed))
            {
                if (getLengthType().equals(LengthType.Fixed))
                    setLength(getLength() + fld.getLength());
                setData(parentData + fld.getData() + subFldConnector);
            }
        }

        if (definition != null && definition.length > 0) {
            setLength(definition.length);
        }

        if (!lastTag.isEmpty() && isDynamicLengthLL()) {
            String parentData = getData() == null ? "" : getData();
            setData(parentData + lastTag + Utils.intToFixedLengthString(lastLength, 3) + lastData); // 4 bytes = tag, 3
                                                                                                    // bytes = length
        }
        if (getChildren().get(0).offset != null && (offset == null || offset <= 0)) {
            if (getLengthType().equals(LengthType.Fixed)) {
                offset = getChildren().get(0).offset;
            } else if (getLengthType().equals(LengthType.Embedded)) {
                offset = getChildren().get(0).offset - 7;
            }
        }
    }
}
