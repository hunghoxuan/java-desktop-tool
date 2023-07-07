package com.rs2.modules.files.isoparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import com.rs2.modules.files.FilesService;

public class MyFileReader extends RandomAccessFile {
    public int fileNumber;
    public String fileName;
    public Charset charset = Charset.forName(FilesService.CHARSET_EBCDIC);
    public String mode;

    public MyFileReader(File f, String m) throws FileNotFoundException {
        super(f, m);
        mode = m;
        fileName = f.getPath();
    }

    public MyFileReader(String f, String m) throws FileNotFoundException {
        super(f, m);
        mode = m;
        fileName = f;
    }

}
