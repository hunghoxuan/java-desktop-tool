package com.java.modules.files.isoparser.elements.iso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.java.core.utils.Utils;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;
import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyFile;
import com.java.modules.files.isoparser.MyFileReader;
import com.java.modules.files.isoparser.MyMessage;
import com.java.modules.files.isoparser.Trace;
import com.java.modules.files.isoparser.configuration.AppConfig;
import com.java.modules.files.isoparser.configuration.AppConfig.Encoding;
import com.java.modules.files.isoparser.containers.CleanInputStream;
import com.java.modules.files.isoparser.containers.Container;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition;
import com.java.modules.files.isoparser.structure.IsoMessageDefinition;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.isoparser.xml.XmlReader;
import com.java.modules.files.isoparser.xml.XmlWriter;
import com.java.modules.files.templates.IsoFileTemplate;
import com.java.modules.files.templates.mastercard.IpmMessageDefinition;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

ISO 8583 file class
 */

public class IsoFile extends MyFile {
    private CleanInputStream in;
    private AppConfig cfg;

    private List<IsoMessage> messages;

    public String checksum = null;
    public boolean checksumProblems = false;

    public IsoFile() {
        messages = new LinkedList<IsoMessage>();
    }

    public IsoFile(String filename, String mode) {
        fileName = filename;
        messages = new LinkedList<IsoMessage>();
        fileReader = getFileReader(fileName, mode);
    }

    public IsoFile(String filename) {
        this(filename, "r");
    }

    public IsoFile(AppConfig _cfg, CleanInputStream _in) {
        cfg = _cfg;
        in = _in;
        messages = new LinkedList<IsoMessage>();
        fileName = cfg.source;
        fileReader = getFileReader(fileName);

        while (true) {
            Trace.log("IsoFile", "### Parsing message number " + (messages.size() + 1));
            IsoMessage msg = IsoMessage.read(cfg, in);
            if (msg == null)
                break;

            msg.number = messages.size() + 1;
            messages.add(msg);
        }

        IsoMessageDefinition.getStructure(cfg).afterFileParsed(this);
    }

    public IsoFileTemplate getIsoFileReader() {
        return isoFileReader;
    }

    @Override
    public List<MyMessage> getMessages() {
        List<MyMessage> list = new LinkedList<MyMessage>();
        for (IsoMessage msg : messages) {
            list.add(msg);
        }
        return list;
    }

    public List<IsoMessage> getIsoMessages() {
        return messages;
    }

    public String asText() {
        String res = "";
        // res += Settings.lineSeperator;
        // res += "ISO 8583 file: " + cfg.source;
        // res += "\nEncoding: " + cfg.encoding;
        // res += "\nContainer (layout): " + cfg.container;
        // res += "\nStructure definition: " + cfg.variant;
        // res += Settings.lineSeperator;
        // res += "\n";
        // res += "\nMessages:\n\n";

        for (int i = 0; i < messages.size(); i++) {
            res += messages.get(i).asText();
            res += Settings.lineSeperator;
        }

        // if (checksum != null) {
        // res += "\nFile checksum: " + checksum + "\n";
        // }
        if (checksumProblems) {
            res += "\nOne or more errors met while calculating checksum for the file. Please refer to trace for details. \n";
        }

        return res;
    }

    public static IsoFile parse(InputStream in) throws IOException {
        Trace.log("IsoFile", "ISO file parsing started");

        IsoFile file = new IsoFile();

        IsoMessage msg = null;
        while (true) {
            msg = IsoMessage.parse(in);
            if (msg != null) {
                Trace.log("IsoFile", "Message parsed, adding to messages set");
                file.messages.add(msg);

            } else {
                break;
            }

        }
        Trace.log("IsoFile", "ISO file parsing finished");
        return file;
    }

    public void merge(OutputStream out) throws IOException {
        Trace.log("IsoFile", "ISO file merging started");
        for (IsoMessage message : messages) {
            message.merge(out);
        }
        out.flush();
        out.close();
        Trace.log("IsoFile", "ISO file merging finished");
    }

    public static IsoFile load(String source) throws Exception {
        Trace.log("IsoFile", "ISO file loading from XML started");
        IsoFile file = XmlReader.read(source);
        Trace.log("IsoFile", "ISO file loading from XML finished");
        return file;
    }

    public void save(String dst) {
        save(dst, Encoding.EBCDIC);
    }

    public void saveXMLFile(String dst) {
        save(dst, Encoding.ASCII);
    }

    public void saveISOFile(String dst) {
        save(dst, Encoding.EBCDIC);
    }

    public void save(String dst, Encoding encoding) {
        try {
            if (encoding == Encoding.ASCII || encoding == Encoding.XML) {
                Trace.log("IsoFile", "ISO file writing to XML started");
                XmlWriter.write(this, dst);
                Trace.log("IsoFile", "ISO file writing to XML finished");
            } else if (encoding == Encoding.EBCDIC) {
                merge(Container.getOutputStream(dst));
            }
        } catch (Exception ex) {
            LogManager.getLogger().error("Failed to save ISO file " + dst, ex);
        }
    }

    @Override
    public void loadPropertiesObject(Map<String, Map<String, Object>> data) {
        // IsoFile file = new IsoFile();
        getIsoMessages().clear();
        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) { // message
            IsoMessage msg = IsoMessage.newInstance("type");
            IsoField fld = null;

            int index = 0;
            int subFieldIdx = 0;

            for (Map.Entry<String, Object> entryF : entry.getValue().entrySet()) {
                Object obj = entryF.getValue();
                // if (obj instanceof String) {
                String value = new StringBuilder().append(obj).toString();
                int length = 0;
                LengthType lType = LengthType.Fixed;
                String name, desc = "";
                name = entryF.getKey().trim();
                if (name.contains(" "))
                    name = name.substring(0, name.indexOf(" "));
                if (entryF.getKey().contains(":") && entryF.getKey().contains("("))
                    desc = Utils.substringBetween(entryF.getKey(), ":", "(").trim();
                else if (entryF.getKey().contains(":") && entryF.getKey().contains("["))
                    desc = Utils.substringBetween(entryF.getKey(), ":", "[").trim();
                try {
                    String lStr = Utils.substringBetween(entryF.getKey(), "[", "]");
                    index = FilesService.getFieldIndexByTag(name);

                    if (lStr.equals("LL")) {
                        length = 2;
                        lType = LengthType.Embedded;
                    } else if (lStr.equals("LLL")) {
                        length = 3;
                        lType = LengthType.Embedded;
                    } else {
                        length = Integer.parseInt(lStr);
                        if (entryF.getKey().toUpperCase().contains("BITMAP")) {// is hex
                            length = 3 * length - 1; // 80 00 01 00 00 01 00 00
                        }
                        if (length > 0 && value.length() > length)
                            value = value.substring(0, length);
                        else if (length > 0 && value.length() < length)
                            value = value + Utils.multiplyChars(" ", length - value.length());
                    }

                } catch (Exception ex) {

                }
                if (name.toUpperCase().startsWith("MTI")) {
                    msg.setMti(value);
                    continue;
                }
                if (name.toUpperCase().startsWith("*RECORDSIZE")) {
                    msg.length = Integer.valueOf(value.trim());
                    continue;
                }
                if (name.toUpperCase().startsWith("BITMAP 2") || name.toUpperCase().startsWith("BITMAP2")) {
                    msg.setBitmapSecondary(value);
                    continue;
                } else if (name.toUpperCase().startsWith("BITMAP")) {
                    msg.setBitmapPrimary(value);
                    continue;
                }

                // process sub Field (start with " ")
                if (entryF.getKey().startsWith(FilesService.subFieldPrefix) && fld != null) {
                    // method 1
                    IsoField subFld = new IsoField();
                    subFld.name = name;
                    subFld.description = desc;
                    subFld.setLength(length);
                    subFld.setLengthType(LengthType.Fixed);
                    subFld.setData(value);

                    fld.getChildren().add(subFld);
                    continue;
                } else if (fld != null && fld.getChildren().size() > 0) {
                    fld.parseChildrenAndUpdateParent(); // method 1
                }

                subFieldIdx = 0;
                IsoFieldDefinition def = (new IpmMessageDefinition()).getDefinition(index);
                if (def != null && (def.length != length || def.lengthType != lType)) {
                    System.out.println(def);
                }

                if (def == null)
                    def = new IsoFieldDefinition(lType, length, name, false);

                fld = new IsoField();
                fld.isFullyParsed = false;
                fld.definition = def;
                fld.setLength(def.length);
                fld.setLengthType(def.lengthType);
                fld.setName(def.getName());
                fld.description = desc;

                if (fld.definition.binary) {
                    fld.setRawData(FilesService.hexToBytes(value));
                } else {
                    fld.setData(value);
                }
                fld.setIndex(index);

                msg.addField(fld);
                // }

            }
            getIsoMessages().add(msg);
        }

        System.out.println("After edit: " + asText());
        LogManager.getLogger().debug(asText());

        // IsoFile file1 = loadISOFile1(
        // "C:\\Bankworks\\rs2sp_uat_1\\Inst2001_GACQ\\out\\ipm\\R111.RS2FSEU.ipmout.22196.001");
    }

    // public Map<String, Object> asPropertiesObject() {
    // Map<String, Object> root = new LinkedHashMap<>();
    // for (IsoMessage msg : this.getMessages()) {

    // root.put(msg.getDescription().toUpperCase(), msg.asPropertiesObject());
    // }
    // return root;
    // }

    // public IsoFile run(String action, String[] args) {
    // IpmFileReader fgen = new IpmFileReader(this.fileName);
    // fgen.GenChargeBack(new String[] {});
    // LogManager.getLogger().debug("Gen Charge back: \n" + fgen.isoFile.asText());
    // return fgen.isoFile;
    // }

}
