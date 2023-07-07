package com.java.modules.files.isoparser.containers.readers;

import java.io.IOException;
import java.io.InputStream;

import com.java.modules.files.isoparser.Trace;

public class FixedInputStream extends InputStream {

    private InputStream in = null;
    private byte[] buffer = null;
    private int offset = 0;

    public FixedInputStream(InputStream i) {
        super();
        in = i;
        Trace.log("Fixed1014 in", "Stream created");
    }

    private void fetchBlock() throws IOException {
        Trace.log("Fixed1014 in", "Time to fetch next block");
        offset = 0;
        buffer = new byte[1012];
        byte[] trailer = new byte[2];

        int cnt = in.read(buffer);
        if (cnt != 1012) {
            Trace.log("Fixed1014 in", "Wrong count of bytes fetched from parent stream: " + String.valueOf(cnt));
            throw new IOException("No enough bytes to read block body");
        }
        Trace.log("Fixed1014 in", "Block body fetched");
        cnt = in.read(trailer);
        if (cnt != 2) {
            Trace.log("Fixed1014 in", "Wrong count of bytes fetched from parent stream: " + String.valueOf(cnt));
            throw new IOException("No enough bytes to read block trailer");
        }
        Trace.log("Fixed1014 in", "Block trailer fetched");
    }

    @Override
    public int read() throws IOException {
        if (buffer == null || offset == buffer.length) {
            fetchBlock();
        }

        if (buffer == null)
            return -1;

        return buffer[offset++];
    }

}
