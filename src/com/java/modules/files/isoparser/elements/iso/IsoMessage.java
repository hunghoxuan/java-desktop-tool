package com.java.modules.files.isoparser.elements.iso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;

import com.java.core.logs.LogManager;
import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyField;
import com.java.modules.files.isoparser.MyMessage;
import com.java.modules.files.isoparser.Trace;
import com.java.modules.files.isoparser.configuration.AppConfig;
import com.java.modules.files.isoparser.containers.CleanInputStream;
import com.java.modules.files.isoparser.containers.writers.RDWOutputStream;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition;
import com.java.modules.files.isoparser.structure.IsoMessageDefinition;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.templates.mastercard.IpmMessage;
import com.java.core.utils.Utils;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

ISO 8583 message data class
 */

public class IsoMessage extends MyMessage {
    public IsoMessage() {

    }

    public IsoMessage(AppConfig _cfg, CleanInputStream _in) {
        cfg = _cfg;
        in = _in;
    }

    public static IsoMessage newInstance(String type) {
        if (type.equalsIgnoreCase(FilesService.TYPE_MASTERCARD))
            return new IpmMessage();
        else if (type.equalsIgnoreCase(FilesService.TYPE_VISA))
            return new IsoMessage();
        return new IsoMessage();
    }

    public static IsoMessage newInstance(String type, AppConfig _cfg, CleanInputStream _in) {
        if (type.equalsIgnoreCase(FilesService.TYPE_MASTERCARD))
            return new IpmMessage(_cfg, _in);
        return new IsoMessage();
    }

    public class IsoFieldNotDefined extends Exception {
        private Integer index;

        public IsoFieldNotDefined(Integer idx) {
            index = idx;
        }

        @Override
        public String getMessage() {
            return "ISO field is not defined in structure: " + index.toString();
        };
    };

    public class IsoFieldReadError extends Exception {
        private String message;

        public IsoFieldReadError(String msg) {
            message = msg;
        }

        @Override
        public String getMessage() {
            return "ISO field can not be read from stream: " + message;
        };
    };

    public IsoHeader header;
    private List<IsoField> fields = new ArrayList<IsoField>();
    private HashMap<String, String> namedFields = new HashMap<>();
    public HashMap<Integer, IsoField> isoFields = new HashMap<Integer, IsoField>();
    private ArrayList<Integer> fieldSet = new ArrayList<Integer>();

    private String mti = null;
    public Integer number;

    private CleanInputStream in;
    private AppConfig cfg;

    public List<MyField> getFields() {
        List<MyField> list = new LinkedList<MyField>();
        for (IsoField fld : fields) {
            list.add(fld);
        }
        return list;
    }

    public List<IsoField> getIsoFields() {
        return fields;
    }

    @Override
    public int getLength() {
        if (length != null && length > 0)
            return length;

        int l = 20; // Mti + 2 bitmap
        for (IsoField fld : getIsoFields()) {
            l += fld.getActualLength();
        }
        return l;
    }

    public String getMti() {
        if ((mti == null || mti.isEmpty()) && header != null)
            mti = header.mti;
        return mti;
    }

    public void setMti(String mti) {
        this.mti = mti;
        if (header == null)
            header = new IsoHeader();
        header.mti = mti;
    }

    public void setFields(List<IsoField> fields) {
        this.fields = fields;
    }

    public int getIndex() {
        return number;
    }

    private void parse(IsoMessageDefinition struc) throws IsoFieldNotDefined, IsoFieldReadError {
        Trace.log("IsoMessage", "Parsing fields");
        Map<Integer, IsoFieldDefinition> defs = struc.getFieldDefinitions();
        Trace.log("IsoMessage", "Got fields definitions from structure");

        String ids = "";
        for (int i = 0; i < header.fields.size(); i++)
            ids += String.valueOf(header.fields.get(i)) + " ";

        Trace.log("IsoMessage", "Fields ids: " + ids);

        for (int i = 0; i < header.fields.size(); i++) {
            Integer idx = header.fields.get(i);
            if (idx == 1)
                continue; // secondary bitmap
            Trace.log("IsoMessage", "Reading field " + idx);
            IsoFieldDefinition def = defs.get(idx);
            if (def == null) {
                Trace.error("IsoMessage", "Field definition is not present: " + idx);
                throw new IsoFieldNotDefined(idx);
            }
            try {
                int off = (int) ((long) in.getOffset());

                byte[] rawData = FilesService.readFromStream(in, def, cfg);

                IsoField field = new IsoField();
                field.name = def.name;
                field.description = def.description;
                field.rawData = rawData;
                if (def.lengthType == LengthType.Embedded)
                    off += def.length;
                field.offset = off;
                field.definition = def;
                field.setIndex(idx);
                if (!def.binary) {
                    field.rawConvertedData = FilesService.bytesToString(rawData, cfg.getCharset());
                    field.parsedData = new String(field.rawConvertedData);
                } else {
                    field.parsedData = "(binary)";
                }
                if (def.mask && cfg.masked) {
                    field.masked = true;
                }

                addField(field);

            } catch (Exception e) {
                Trace.error("IsoMessage", "Field read error: " + e.getMessage());
                throw new IsoFieldReadError(e.getMessage());
            }
        }

        try {
            Trace.log("IsoMessage", "Invoking application-level parser");
            struc.afterMessageParsed(this);
        } catch (IsoMessageDefinition.ApplicationDataParseError e) {
            Trace.error("IsoMessage", "Application-level parser error: " + e.getMessage());
        }

        for (IsoField fd : fields)
            namedFields.put(fd.name, fd.parsedData);
    }

    public static IsoMessage read(AppConfig _cfg, CleanInputStream _in) {
        try {
            Trace.log("IsoMessage", "Reading message");
            int off = (int) ((long) _in.getOffset());
            IsoHeader hdr = IsoHeader.read(_cfg, _in);
            if (hdr != null) {
                Trace.log("IsoMessage", "ISO header read ok");
                IsoMessage msg = IsoMessage.newInstance(FilesService.TYPE_MASTERCARD, _cfg, _in);
                msg.header = hdr;
                msg.header.readAndParse();
                msg.offset = off;

                IsoMessageDefinition struc = IsoMessageDefinition.getStructure(_cfg);
                if (struc == null) {
                    Trace.error("IsoMessage", "No structure defined");
                    return null;
                }

                msg.parse(struc);

                return msg;
            } else {
                Trace.log("IsoMessage", "Header was not read");
                return null;
            }
        } catch (Exception e) {
            Trace.error("IsoMessage", "Message can not be parsed: " + e.getMessage());
            return null;
        }
    }

    public String getBitmapPrimaryAsHex() {
        if (header != null)
            return FilesService.bin2hex(header.bitmap1);
        return "";
    }

    public String getBitmapSecondaryAsHex() {
        if (header != null)
            return FilesService.bin2hex(header.bitmap2);
        return "";
    }

    public void setBitmapPrimary(String value) {
        if (header == null)
            header = new IsoHeader();
        header.bitmap1 = FilesService.hexToBytes(value);
    }

    public void setBitmapSecondary(String value) {
        if (header == null)
            header = new IsoHeader();
        header.bitmap2 = FilesService.hexToBytes(value);
    }

    public void setBitmapPrimary(byte[] value) {
        if (header == null)
            header = new IsoHeader();
        header.bitmap1 = value;
    }

    public void setBitmapSecondary(byte[] value) {
        if (header == null)
            header = new IsoHeader();
        header.bitmap2 = value;
    }

    public String asText() {
        String res = "";
        res += "<" + getDescription() + ">\n";
        // if (offset != null) res += "\nOffset: 0x" + Long.toHexString(offset);
        res += "MTI: '" + getMti() + "'\n";
        if (header != null) {
            res += "Primary bitmap: " + getBitmapPrimaryAsHex() + "\n";
            if (header.bitmap2 != null)
                res += "Secondary bitmap: " + getBitmapSecondaryAsHex() + "\n";
        }
        // res += "\nFields: \n";
        for (int i = 0; i < fields.size(); i++) {
            res += fields.get(i).asText(cfg) + "\n";
        }
        return res;
    }

    public String getName() {
        return getDescription();
    }

    public void addField(IsoField fld) {
        fields.add(fld);
        isoFields.put(fld.getIndex(), fld);
    }

    public String getDescription() {
        String def = getMti();

        // if (offset != null)
        def += " (pos " + String.valueOf(offset) + ") [" + String.valueOf(getLength())
                + "]";

        return def;
    }

    @Override
    public String toString() {
        return "IsoMessage{" + getDescription() + "}";
    }

    private static byte[] readBitmap(InputStream in) throws IOException {
        Trace.log("IsoMessage", "Reading bitmap");
        byte[] bm = new byte[8];
        int cnt = in.read(bm);
        if (cnt < 8) {
            Trace.log("IsoMessage", "No enough bytes to read bitmap");
            throw new IOException("No enough bytes to read bitmap");
        }
        return bm;
    }

    private boolean parseBitmap(byte[] buff, int index) {
        Trace.log("IsoMessage", "Parsing bitmap");
        boolean hasNextBitmap = false;
        for (int byteIdx = 0; byteIdx < 8; byteIdx++) {
            BitSet bs = BitSet.valueOf(new byte[] { buff[byteIdx] });
            for (int bitIdx = 0; bitIdx < 8; bitIdx++) {
                int fieldIdx = ((index - 1) * 64) // bitmap number
                        + (byteIdx * 8) // byte number
                        + (bitIdx + 1);

                if (bs.get(7 - bitIdx)) {
                    if (fieldIdx == ((index - 1) * 64) + 1) {
                        Trace.log("IsoMessage", "Has next bitmap");
                        hasNextBitmap = true;
                    } else {
                        fieldSet.add(fieldIdx);
                    }
                }
            }
        }

        return hasNextBitmap;
    }

    private static String readMti(InputStream in) throws IOException {
        Trace.log("IsoMessage", "Reading MTI");
        byte[] buff = new byte[4];
        int cnt = in.read(buff);
        if (cnt <= 0) { // end of stream ?
            Trace.log("IsoMessage", "No bytes for MTI. End of stream.");
            return null;
        }
        if (cnt < 4) {
            Trace.log("IsoMessage", "No enough bytes for MTI");
            throw new IOException("No enough bytes to read MTI");
        }
        Trace.log("IsoMessage", "MTI read ok");
        return new String(buff, AppConfig.get().getRawCharset());
    }

    public static IsoMessage parse(InputStream in) throws IOException {
        Trace.log("IsoMessage", "Parsing message");
        IsoMessage msg = IsoMessage.newInstance(FilesService.TYPE_MASTERCARD);

        msg.setMti(readMti(in));
        if (msg.getMti() == null) {
            Trace.log("IsoMessage", "No MTI read. End of stream.");
            return null;
        }

        int bmIdx = 1;
        Trace.log("IsoMessage", "Getting bitmap(s)");
        while (true) {
            byte[] rawBm = readBitmap(in);
            boolean hasNextBm = msg.parseBitmap(rawBm, bmIdx);
            if (!hasNextBm)
                break;

            bmIdx++;
        }

        Trace.log("IsoMessage", "Bitmap(s) parsing finished");

        IsoMessageDefinition mdef = AppConfig.get().getMesssageDef();
        Trace.log("IsoMessage", "Got message structure definition");
        if (mdef == null) {
            Trace.log("IsoMessage", "No message definition. Don't know how to parse");
            throw new IOException("No message definition found");
        }

        Map<Integer, IsoFieldDefinition> fdefs = mdef.getFieldDefinitions();

        for (Integer fIdx : msg.fieldSet) {
            IsoFieldDefinition fdef = fdefs.get(fIdx);
            if (fdef == null) {
                Trace.log("IsoMessage", "No such field definition: " + fIdx.toString());
                throw new IOException("No field definition for index " + fIdx.toString());
            }
            Trace.log("IsoMessage", "Got field definition");

            IsoField fld = IsoField.read(in, fdef);
            Trace.log("IsoMessage", "Field parsed " + fld.asText());

            msg.addField(fld);
        }

        Trace.log("IsoMessage", "Message parsed");
        return msg;
    }

    private void setBit(byte[] bitmap, int bit) {
        int byteIndex = (bit - 1) / 8;
        int localBitIndex = (bit - 1) % 8;
        localBitIndex = 7 - localBitIndex;

        int b = bitmap[byteIndex];
        int v = (int) Math.pow(2, localBitIndex);
        b += v;
        bitmap[byteIndex] = (byte) b;
    }

    private void writeBitmap(OutputStream out) throws IOException {
        Trace.log("IsoMessage", "Preparing message bitmap(s)");

        HashMap<Integer, byte[]> bitmaps = new HashMap<>();

        for (IsoField field : fields) {
            int bitmapIndex = (field.getIndex() / 64) + 1;
            int bitIndex = field.getIndex() % 64;

            byte[] bitmap = bitmaps.get(bitmapIndex);
            if (bitmap == null) {
                Trace.log("IsoMessage", "Bitmap with index " + bitIndex + " created");
                bitmap = new byte[8];
                bitmaps.put(bitmapIndex, bitmap);
                if (bitmapIndex > 1) {
                    setBit(bitmaps.get(bitmapIndex - 1), 1);
                }
            }

            setBit(bitmap, bitIndex);

        }

        Trace.log("IsoMessage", "Writing bitmap(s)");
        int i = 1;
        while (true) {
            byte[] bitmap = bitmaps.get(i);
            if (bitmap == null)
                break;
            out.write(bitmap);
            i++;
        }
    }

    public void merge(OutputStream out) throws IOException {
        Trace.log("IsoMessage", "Merging message");
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        Trace.log("IsoMessage", "Sorting message fields");
        fields.sort((Comparator.comparingInt(IsoField::getIndex)));

        // write length
        tmp.write(Utils.convertToByteArray(getLength()));

        Trace.log("IsoMessage", "Writing MTI");
        tmp.write(FilesService.convertBytesOnWrite(getMti().getBytes(), AppConfig.get().getRawCharset()));

        writeBitmap(tmp);

        Trace.log("IsoMessage", "Writing fields");
        for (IsoField field : fields) {
            field.merge(tmp);
        }

        Trace.log("IsoMessage", "Message data merged to buffer");
        byte[] buff = tmp.toByteArray();
        if (out instanceof RDWOutputStream) {
            Trace.log("IsoMessage", "Have to write RDW header");
            ((RDWOutputStream) out).writeHeader(buff);
        }
        Trace.log("IsoMessage", "Writing message data");
        out.write(buff);
        Trace.log("IsoMessage", "Message writing finished");
        tmp.close();
    }

    @Override
    public Map<String, Object> asPropertiesObject() {
        Map<String, Object> root = new LinkedHashMap<>();
        // root.put("*RecordSize [4]", this.getLength());
        root.put("MTI [4]", this.getMti());
        if (!this.getBitmapPrimaryAsHex().isEmpty())
            root.put("BITMAP [8]", this.getBitmapPrimaryAsHex());
        if (!this.getBitmapSecondaryAsHex().isEmpty())
            root.put("BITMAP2 [8]", this.getBitmapSecondaryAsHex());
        for (IsoField fld : fields) {
            for (Map.Entry<String, Object> entry : fld.asPropertiesObject().entrySet()) {
                root.put(entry.getKey(), entry.getValue());
            }
        }
        return root;
    }

    public IsoField getField(String attribute) {
        // Call the corresponding getter for a recognized attribute_name
        String[] fldNames = attribute.split("\\.");
        IsoField rootFld = null, dataFld = null;
        for (IsoField fld : fields) {
            if (fld.getName().equalsIgnoreCase(fldNames[0])) {
                rootFld = fld;
                break;
            }
        }

        if (rootFld != null && fldNames.length > 1) {
            for (IsoField fld : rootFld.getChildren()) {
                if (fld.getName().equalsIgnoreCase(fldNames[fldNames.length - 1])) {
                    dataFld = fld;
                    break;
                }
            }
        }

        if (dataFld != null) {
            return dataFld;
        } else if (rootFld != null && fldNames.length == 1) {
            return rootFld;
        }

        return null;
    }

    public String getAttribute(String attribute) {
        IsoField fld = getField(attribute);
        if (fld != null)
            return fld.getData();

        return null;
    }

    // return position
    public int setAttribute(String attribute, String value) {
        int pos = 0;
        if (attribute.equalsIgnoreCase("MTI")) {
            mti = value;
            return offset;
        } else if (attribute.equalsIgnoreCase("Bitmap")) {
            setBitmapPrimary(value);
            return offset + 8;
        } else if (attribute.equalsIgnoreCase("Bitmap2")) {
            setBitmapSecondary(value);
            return offset + 16;
        }

        IsoField fld = getField(attribute);

        if (fld != null) {
            fld.setData(value);
            pos = fld.offset;
            if (fld.getChildren().size() > 0)
                fld.parseChildrenAndUpdateParent();
        }

        return pos;
    }

    public void setAttribute(Attribute attribute) {

        // Note: Attribute class constructor ensures the name not null
        String attribute_name = attribute.getName();
        Object value = attribute.getValue();

        // Call the corresponding getter for a recognized attribute_name
        for (IsoField fld : fields) {
            if (fld.getName().equalsIgnoreCase(attribute_name)) {
                if (value instanceof String)
                    fld.setData((String) value);
                else if (value instanceof byte[])
                    fld.setRawData((byte[]) value);
            }
        }
    }

    public void updateAttributeValues() {
        for (IsoField fld : fields) {
            Field field = Utils.getFieldFromName(this, fld.getName());
            if (field != null) {
                try {
                    field.setAccessible(true);
                    fld.setData((String) field.get(this));
                } catch (Exception ex) {
                    LogManager.getLogger().error(ex);
                }
            }
        }
    }
}
