package com.rs2.modules.files;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JOptionPane;

import com.rs2.core.components.MyDialog;
import com.rs2.core.logs.LogManager;
import com.rs2.core.logs.Logger;
import com.rs2.core.settings.Settings;
import com.rs2.modules.files.isoparser.MyFile;
import com.rs2.modules.files.isoparser.Trace;
import com.rs2.modules.files.isoparser.configuration.AppConfig;
import com.rs2.modules.files.isoparser.containers.CleanInputStream;
import com.rs2.modules.files.isoparser.containers.Container;
import com.rs2.modules.files.isoparser.elements.ascii.AsciiFile;
import com.rs2.modules.files.isoparser.elements.iso.IsoFile;
import com.rs2.modules.files.isoparser.structure.IsoFieldDefinition;
import com.rs2.modules.files.isoparser.structure.IsoMessageDefinition;
import com.rs2.modules.files.templates.mastercard.IpmFileTemplate;
import com.rs2.modules.files.templates.visa.VisaFileTemplate;
import com.rs2.core.base.MyService;
import com.rs2.core.utils.Utils;

public class FilesService extends MyService {
    public static String settingRootFolder = Settings.TagLASTDIR + "_Files";
    private static Logger logger = LogManager.getLogger();
    public static String CHARSET_EBCDIC = "CP1047";
    public static String CHARSET_ASCII = "ASCII";
    public static String CHARSET_ISO = "ISO-8859-1";

    public static String TYPE_MASTERCARD = "IPM";
    public static String TYPE_VISA = "VISA";
    public static String TYPE_JCB = "JCB";
    public static String TYPE_DINER = "JCB";

    public static String subFieldPrefix = "      ";

    public static String[] getFileTypes() {
        return new String[] {
                VisaFileTemplate.ACTION_RUN_TEMPLATE,
                IpmFileTemplate.ACTION_RUN_TEMPLATE,
        };
    }

    public static String[] getFileDefaultActions() {
        return getFileTypes();
    }

    public static MyFile loadFile(String fileName, String content) {
        if (content == null)
            content = Utils.getContentFromFile(fileName);
        if (content.equals(VisaFileTemplate.ACTION_RUN_TEMPLATE))
            return loadVisaFile(fileName);
        if (content.equals(IpmFileTemplate.ACTION_RUN_TEMPLATE))
            return loadISOFile(fileName);

        MyFile myfile = null;

        if (content != null && !content.isEmpty()) {
            myfile = FilesService.loadVisaFile(fileName);
        }

        if (myfile == null)
            myfile = FilesService.loadISOFile(fileName);

        if (myfile != null) {
            try {
                if (myfile.getFileReader() != null)
                    myfile.getFileReader().close();
            } catch (Exception ex) {
                LogManager.getLogger().error(ex);
            }

        }
        return myfile;
    }

    public static AsciiFile loadVisaFile(String file) {
        VisaFileTemplate f = new VisaFileTemplate(file);
        f.run();
        // LogManager.getLogger().debug(visaReader.getIsoFile().asText());
        return f.getIsoFile();
    }

    public static MyFile loadISOFile(String fileName) {
        IpmFileTemplate f = new IpmFileTemplate(fileName);
        f.run();
        // LogManager.getLogger().debug("New method: \n" + f.isoFile.asText());

        return f.getIsoFile();
    }

    public static IsoFile loadISOFile1(String fileName) {
        AppConfig cfg = AppConfig.createForMasterCard(fileName);
        cfg.probe();
        if (!cfg.isValid()) {
            printHelp();
            return null;
        }
        // Trace.log("main", "Configuration prepared");
        // Trace.log("main", "Input file: " + cfg.source);
        CleanInputStream in;
        try {
            in = Container.getContainerStream(cfg, new FileInputStream(fileName));

            Trace.log("main", "Input stream opened");
        } catch (FileNotFoundException e) {
            Trace.error("main", "Input file not found");
            return null;
        } catch (IOException e) {
            Trace.error("main", "Can not open file: " + e.getMessage());
            return null;
        }
        IsoFile file = new IsoFile(cfg, in);
        // System.out.println(file.asText());

        if (file.getMessages().size() > 0) {
            if (!cfg.nodump) {
                logger.debug(file.asText());
            } else if (cfg.report != null) {
                IsoMessageDefinition struc = IsoMessageDefinition.getStructure(cfg);
                // struc.runReport(file, cfg.report);
            } else {
                logger.debug(String.format("%d messages parsed", file.getMessages().size()));
            }
        } else {
            logger.debug("No messages parsed");
        }

        return file;
    }

    public static void printHelp() {
        System.out.println("\n" +
                "Command line options:\n" +
                "\n" +
                "   -m <working mode> (parse | merge)\n" +
                "   -mode\n" +
                "\n" +
                "   -t\n" +
                "   -trace\n" +
                "\n" +
                "   -s <path to source file>\n" +
                "   -src\n" +
                "   -source\n" +
                "\n" +
                "   -d <path to destination file>\n" +
                "   -dst\n" +
                "   -destination\n" +
                "\n" +
                "   -c <container type> (clean | RDW | PreEdit | Fixed1014 )\n" +
                "   -cont\n" +
                "   -container\n" +
                "\n" +
                "   -e <data encoding> (ACSII | EBCDIC)\n" +
                "   -enc\n" +
                "   -encoding\n" +
                "\n" +
                "In PARSE mode ISO messages wil be parsed from ISO file and written to XML.\n" +
                "In MERGE mode ISO messages will be loaded from XML file and written to ISO.\n" +
                "\n" +
                "This tool could be used to transcode ISO files between different formats/encodings or to edit messages data.\n"
                +
                "Only Mastercard IPM files are supported at the moment.");
    }

    public static int[] getIntArrayFromFile(final String filename) {
        try {

            FileInputStream fileInputStream = new FileInputStream(filename);
            int chararray[] = new int[fileInputStream.available()];
            int inChar;
            int index = 0;
            while ((inChar = fileInputStream.read()) != -1) {
                chararray[index++] = inChar;
            }
            fileInputStream.close();
            return chararray;
        } catch (Exception exception) {
            logger.debug(Utils.getExceptionMessage(exception));
        }
        return new int[0];
    }

    public static byte[] getByteArrayFromFile(final String filename) {
        try {
            byte[] bytes = Utils.getBytesFromFile(filename);
            return bytes;
        } catch (Exception exception) {
            logger.debug(Utils.getExceptionMessage(exception));
        }
        return new byte[0];
    }

    public static String bytesToHex(byte[] input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (i > 0)
                sb.append(" ");

            sb.append((String.format("%x", input[i])));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String input) {
        byte[] res = new byte[input.replace(" ", "").length() / 2];
        int offset = 0;
        for (String c : input.split(" ")) {
            res[offset++] = Integer.decode("0x" + c).byteValue();
        }
        return res;
    }

    public static byte[] convertBytesOnRead(byte[] src, Charset from) {
        if (from == Charset.forName(CHARSET_ASCII))
            return src;

        return new String(src, from).getBytes(Charset.forName(CHARSET_ASCII));
    }

    public static byte[] convertBytesOnWrite(byte[] src, Charset to) {
        if (to == Charset.forName(CHARSET_ASCII))
            return src;

        return new String(src, Charset.forName(CHARSET_ASCII)).getBytes(to);
    }

    public static String convert2StringEBCDICToASCII(byte[] rawArray) {
        return ConvertString(rawArray, CHARSET_EBCDIC, CHARSET_ASCII);
        // return new String(bytesToString(rawArray));
    }

    public static String ConvertString(byte[] strToConvert, String in, String out) {
        Charset charset_in = Charset.forName(in);
        Charset charset_out = Charset.forName(out);
        try {

            ByteBuffer inputBuffer = ByteBuffer.wrap(strToConvert);
            // decode UTF-8
            CharBuffer data = charset_in.decode(inputBuffer);
            // encode ISO-8559-1
            ByteBuffer outputBuffer = charset_out.encode(data);
            byte[] outputData = outputBuffer.array();

            return new String(outputData, charset_out);

        } catch (Exception e) {
            LogManager.getLogger().error(e);
            return null;
            // throw new IllegalStateException(e);
        }
    }

    public static String ConvertString(String strToConvert, String in, String out) {
        Charset charset_in = Charset.forName(in);
        return ConvertString(strToConvert.getBytes(charset_in), in, out);
    }

    public static byte[] bytesToString(byte[] src) {
        return bytesToString(src, Charset.forName(CHARSET_EBCDIC));
    }

    public static byte[] bytesToString(byte[] src, Charset from) {
        if (from == Charset.forName(CHARSET_ASCII))
            return src;

        return new String(src, from).getBytes(Charset.forName(CHARSET_ASCII));
    }

    public static byte[] readFromStream(InputStream in, IsoFieldDefinition def, AppConfig cfg) throws IOException {
        if (def.lengthType == IsoFieldDefinition.LengthType.Embedded) {
            byte[] rawLength = new byte[def.length];
            if (in.read(rawLength) < def.length) {
                Trace.error("Utils", "Can not read embedded length for field: " + def.name);
                throw new IOException("No enough bytes to read embedded length; Field: " + def.name);
            }

            Integer length;

            try {
                rawLength = bytesToString(rawLength, cfg.getCharset());
                String strLength = new String(rawLength);
                length = Integer.parseInt(strLength);
            } catch (Exception e) {
                Trace.error("Utils", "Can not parse embedded length for field: " + def.name);
                throw new IOException("Can not parse embedded length. Field: " + def.name + "; " + e.getMessage(), e);
            }

            try {
                byte[] buff = readFromStreamFixedLen(in, length);
                return buff;
            } catch (Exception e) {
                Trace.error("Utils", "Ca not read data for field: " + def.name);
                throw new IOException("Can not read data: " + e.getMessage() + "; Field: " + def.name, e);
            }

        } else if (def.lengthType == IsoFieldDefinition.LengthType.Fixed) {
            try {
                byte[] buff = readFromStreamFixedLen(in, def.length);
                return buff;
            } catch (Exception e) {
                Trace.error("Utils", "Can not rad data for field: " + def.name);
                throw new IOException("Can not read data: " + e.getMessage() + "; Field: " + def.name, e);
            }
        } else {
            Trace.error("Utils", "Unsupported field length type");
            throw new IOException("Unsupported length type: " + def.lengthType + "; Field: " + def.name);
        }
    }

    public static byte[] readFromStreamFixedLen(InputStream in, Integer length) throws IOException {
        byte[] buff = new byte[length];
        if (in.read(buff) < length) {
            Trace.error("Utils", "No enough data");
            throw new IOException("No enough bytes to read data");
        }

        return buff;
    }

    public static String bin2hex(byte bin) {
        return String.format("%02x", bin);
    }

    public static String bin2hex(byte[] bin) {
        if (bin == null)
            return "";

        String res = "";
        for (int i = 0; i < bin.length; i++) {
            if (i > 0)
                res += " ";
            res += bin2hex(bin[i]);
        }

        return res;
    }

    public static String getParentTag(String tag) {
        if (tag.contains(" "))
            tag = tag.substring(0, tag.indexOf(" "));
        String[] tmp = tag.split("_");
        if (tmp.length >= 2) {
            return String.join("_", new String[] { tmp[0], tmp[1] });
        }
        return tag;
    }

    public static String getFieldIndexStringByTag(String tag) {
        tag = getParentTag(tag);
        return tag.toUpperCase().replace("PDS", "").replace("DE", "").replace("_", "");
    }

    public static int getFieldIndexByTag(String tag) {
        try {
            return Integer.parseInt(getFieldIndexStringByTag(tag));
        } catch (Exception ex) {
            return 0;
        }
    }

    public static byte[] getBytes(String data, Charset charset) {
        return data.getBytes(charset);
    }

    public static byte[] getBytesForEBIG(String data) {
        return data.getBytes(Charset.forName(CHARSET_EBCDIC));
    }

    public static void deleteFile(String file) {
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Would You Like to delete [" + file + "] ?",
                "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                Files.deleteIfExists(
                        Paths.get(file));
            } catch (NoSuchFileException e) {
                MyDialog.showException(
                        "No such file/directory exists");
            } catch (DirectoryNotEmptyException e) {
                MyDialog.showException("Directory is not empty.");
            } catch (IOException e) {
                MyDialog.showException("Invalid permissions.");
            }
        }
    }

    public static String getAttributeName(String fieldName) {
        return "_" + fieldName;
    }

}
