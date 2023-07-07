package com.java.modules.files.isoparser.containers.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.java.modules.files.isoparser.Trace;

public class RDWOutputStream extends OutputStream {

    private OutputStream out = null;

    public RDWOutputStream(OutputStream _out) {
        super();
        out = _out;
        Trace.log("RDW out", "Stream created");
    }

    @Override
    public void write(int i) throws IOException {
        out.write(i);
    }

    public void writeHeader(byte[] message) throws IOException {
        Trace.log("RDW out", "Writing RDW header");
        int len = message.length;
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(len);
        out.write(bb.array());
    }

    @Override
    public void close() throws IOException {
        out.close();
        super.close();
    }
}
