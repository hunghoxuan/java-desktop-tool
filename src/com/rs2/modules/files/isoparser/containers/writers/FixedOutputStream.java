package com.rs2.modules.files.isoparser.containers.writers;

import java.io.IOException;
import java.io.OutputStream;

import com.rs2.modules.files.isoparser.Trace;

public class FixedOutputStream extends OutputStream {

    private OutputStream out = null;
    private int counter = 0;
    private byte[] trailer = new byte[] { 0x00, 0x00 };

    public FixedOutputStream(OutputStream _out) {
        super();
        out = _out;
        Trace.log("Fixed1014 out", "Stream created");
    }

    @Override
    public void write(int i) throws IOException {
        out.write(i);
        counter++;
        if (counter == 1012) {
            Trace.log("Fixed1014 out", "Time to write block trailer");
            out.write(trailer);
            counter = 0;
        }
    }

    @Override
    public void close() throws IOException {
        Trace.log("Fixed1014 out", "Aligning output file to block size");
        byte[] filler = new byte[] { 0x00 };
        while (counter < 1012) {
            out.write(filler);
            counter++;
        }
        out.write(trailer);
        super.close();
    }

}
