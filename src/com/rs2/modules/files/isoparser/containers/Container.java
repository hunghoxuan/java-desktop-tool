package com.rs2.modules.files.isoparser.containers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.rs2.modules.files.isoparser.Trace;
import com.rs2.modules.files.isoparser.configuration.AppConfig;
import com.rs2.modules.files.isoparser.configuration.AppConfig.Encoding;
import com.rs2.modules.files.isoparser.containers.mastercard.IPMBlockedInputStream;
import com.rs2.modules.files.isoparser.containers.mastercard.IPMPreEditInputStream;
import com.rs2.modules.files.isoparser.containers.readers.FixedInputStream;
import com.rs2.modules.files.isoparser.containers.readers.PreEditInputStream;
import com.rs2.modules.files.isoparser.containers.readers.RDWInputStream;
import com.rs2.modules.files.isoparser.containers.writers.FixedOutputStream;
import com.rs2.modules.files.isoparser.containers.writers.RDWOutputStream;

/*
ISO 8583 parser
Original code by Sergey V. Shakshin (rigid.mgn@gmail.com)

Container (layout) selector
*/

public class Container {
    public static CleanInputStream getContainerStream(AppConfig cfg, InputStream raw) throws IOException {
        CleanInputStream clean = new CleanInputStream(raw);

        switch (cfg.container) {
            case CLEAN:
                return clean;
            case RDW:
                return new com.rs2.modules.files.isoparser.containers.RDWInputStream(clean, cfg.mainframe);
            case FIXED1014:
                return new com.rs2.modules.files.isoparser.containers.RDWInputStream(new IPMBlockedInputStream(clean),
                        cfg.mainframe);
            case PREEDIT:
                return new IPMPreEditInputStream(clean);
            default:
                Trace.error("Container", "Unsupported container: " + cfg.container);
                return null;
        }
    }

    public static InputStream getInputStream() throws FileNotFoundException {
        InputStream clean = new BufferedInputStream(new FileInputStream(AppConfig.get().source));
        switch (AppConfig.get().container) {
            case CLEAN:
                Trace.log("Container", "Selected clean file reader");
                return clean;
            case RDW:
                Trace.log("Container", "Selected RDW file reader");
                return new RDWInputStream(clean, false);
            case FIXED1014:
                Trace.log("Container", "Selected Fixed1014 file reader");
                return new RDWInputStream(new FixedInputStream(clean), true);
            case PREEDIT:
                Trace.log("Container", "Selected pre-edit file reader");
                return new PreEditInputStream(clean);
        }
        return null;
    }

    public static OutputStream getOutputStream(String file, Encoding encoding) throws IOException {
        AppConfig.get().destination = file;
        AppConfig.get().encoding = encoding;

        return getOutputStream();
    }

    public static OutputStream getOutputStream(String file) throws IOException {
        AppConfig.get().destination = file;
        AppConfig.get().encoding = Encoding.EBCDIC;

        return getOutputStream();
    }

    public static OutputStream getOutputStream() throws IOException {
        OutputStream clean = new FileOutputStream(AppConfig.get().destination);
        switch (AppConfig.get().container) {
            case CLEAN:
                Trace.log("Container", "Selected  clean file writer");
                return clean;
            case RDW:
                Trace.log("Container", "Selected RDW file writer");
                return new RDWOutputStream(clean);
            case FIXED1014:
                Trace.log("Container", "Selected Fixed1014 file writer");
                return new RDWOutputStream(new FixedOutputStream(clean));
            case PREEDIT:
                Trace.log("Container", "Selected Pre-Edit file writer (unsupported)");
                throw new IOException("Pre-Edit writing is not supported");
        }
        throw new IOException("Unsupported writer specified");
    }
}
