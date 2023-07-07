package com.rs2.modules.files.isoparser.configuration;

import java.nio.charset.Charset;

import com.rs2.modules.files.FilesService;
import com.rs2.modules.files.isoparser.containers.mastercard.IPMProbe;
import com.rs2.modules.files.isoparser.structure.IsoMessageDefinition;
import com.rs2.modules.files.templates.mastercard.IpmMessageDefinition;

public class AppConfig {
    public enum Mode {
        PARSE, MERGE
    }

    public enum Encoding {
        ASCII, EBCDIC, XML
    }

    public enum IsoVariant {
        IPM, JCB
    }

    public enum Container {
        CLEAN, RDW, PREEDIT, FIXED1014
    }

    public Mode mode = Mode.PARSE;
    public String source = null;
    public String destination = null;
    public Encoding encoding = Encoding.ASCII;
    public IsoVariant variant = IsoVariant.IPM;
    public Container container = Container.CLEAN;

    public boolean trace = false;
    public boolean mainframe = false;
    public boolean masked;

    public boolean nodump = true;
    public String report;
    public boolean isValid = true;
    public boolean probe = true;;
    public boolean offset = false;
    public boolean raw = false;

    private static AppConfig _instance = null;

    public static AppConfig init(String[] args) {
        _instance = new AppConfig(args);
        return _instance;
    }

    public static AppConfig get() {
        if (_instance == null)
            _instance = new AppConfig();

        return _instance;
    }

    private AppConfig() {

    }

    private AppConfig(String[] args) {
        int i = 0;
        while (i < args.length) {
            switch (args[i++].toUpperCase()) {
                case "-TRACE":
                case "-T":
                    trace = true;
                    break;
                case "-MODE":
                case "-M":
                    if (i >= args.length) {
                        System.out.println("Wrong number of arguments");
                        isValid = false;
                        return;
                    }
                    switch (args[i].toUpperCase()) {
                        case "PARSE":
                            mode = Mode.PARSE;
                            break;
                        case "MERGE":
                            mode = Mode.MERGE;
                            break;
                        default:
                            System.out.println("Invalid mode: " + args[i]);
                            isValid = false;
                            return;
                    }
                    i++;
                    break;
                case "-SOURCE":
                case "-INPUT":
                case "-SRC":
                case "-S":
                    if (i >= args.length) {
                        System.out.println("Wrong number of arguments");
                        isValid = false;
                        return;
                    }
                    source = args[i];
                    i++;
                    break;
                case "-DESTINATION":
                case "-DST":
                case "-D":
                    if (i >= args.length) {
                        System.out.println("Wrong number of arguments");
                        isValid = false;
                        return;
                    }
                    destination = args[i];
                    i++;
                    break;
                case "-ENCODING":
                case "-ENC":
                case "-E":
                    if (i >= args.length) {
                        System.out.println("Wrong number of arguments");
                        isValid = false;
                        return;
                    }
                    switch (args[i].toUpperCase()) {
                        case "ASCII":
                            encoding = Encoding.ASCII;
                            break;
                        case "EBCDIC":
                            encoding = Encoding.EBCDIC;
                            break;
                        default:
                            System.out.println("Unknown encoding: " + args[i]);
                            isValid = false;
                            return;
                    }
                    i++;
                    break;
                case "-CONTAINER":
                case "-CONT":
                case "-C":
                    if (i >= args.length) {
                        System.out.println("Wrong number of arguments");
                        isValid = false;
                        return;
                    }
                    switch (args[i].toUpperCase()) {
                        case "CLEAN":
                            container = Container.CLEAN;
                            break;
                        case "RDW":
                            container = Container.RDW;
                            break;
                        case "PREEDIT":
                            container = Container.PREEDIT;
                            break;
                        case "FIXED1014":
                            container = Container.FIXED1014;
                            break;
                        default:
                            System.out.println("Unknown container: " + args[i]);
                            isValid = false;
                            return;
                    }
                    i++;
                    break;
                default:
                    // System.out.println("Unknown option: " + args[i]);
                    // isValid = false;
                    // return;
            }
        }

        if (source == null) {
            System.out.println("Source undefined");
            isValid = false;
            return;
        }

        if (destination == null) {
            destination = source + ".xml";
            // System.out.println("Destination undefined");
            // isValid = false;
            return;
        }
    }

    public Charset getCharset() {
        return getRawCharset();
    }

    public Charset getRawCharset() {
        switch (encoding) {
            case ASCII:
                return Charset.forName(FilesService.CHARSET_ASCII);
            case EBCDIC:
                return Charset.forName(FilesService.CHARSET_EBCDIC);
        }
        return Charset.forName(FilesService.CHARSET_ASCII);
    }

    public IsoMessageDefinition getMesssageDef() {
        switch (variant) {
            case IPM:
                return new IpmMessageDefinition();
        }
        return null;
    }

    public static AppConfig get(String[] args) {
        // if (_instance == null) {
        _instance = new AppConfig(args);
        // }
        return _instance;
    }

    public static AppConfig createForMasterCard(String file) {
        if (_instance == null) {
            String[] args = new String[] { "-INPUT", file };
            _instance = new AppConfig(args);
        }
        _instance.source = file;
        return _instance;
    }

    public void probe() {
        if (probe) {
            IPMProbe prb = new IPMProbe();
            prb.probe(source);
            if (prb.container != null)
                container = prb.container;
            if (prb.mainframe != null)
                mainframe = prb.mainframe;
            if (prb.encoding != null)
                encoding = prb.encoding;
        }
    }

    public boolean isValid() {
        if (source == null)
            return false;

        if (container == null)
            return false;

        if (encoding == null)
            return false;

        if (variant == null)
            return false;

        return true;
    }
}
