package com.rs2.modules.files.isoparser.containers;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CleanInputStream extends FilterInputStream {

    private Long offset = Long.valueOf(0);

    public CleanInputStream(InputStream in) {
        super(in);
    }

    public Long getOffset() {
        return offset;
    }

    @Override
    public int read() throws IOException {
        offset++;
        return super.read();
    }
}
