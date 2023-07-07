package com.rs2.modules.files.templates;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.*;

import com.rs2.core.components.MyDialog;
import com.rs2.core.components.MyInputDialog;
import com.rs2.core.logs.LogManager;
import com.rs2.modules.files.FilesService;
import com.rs2.modules.files.isoparser.MyField;
import com.rs2.modules.files.isoparser.MyFile;
import com.rs2.modules.files.isoparser.MyFileReader;
import com.rs2.modules.files.isoparser.elements.iso.IsoField;
import com.rs2.modules.files.isoparser.elements.iso.IsoMessage;
import com.rs2.core.utils.Utils;

import java.nio.charset.Charset;

import static java.lang.Math.toIntExact;

public class IsoFileTemplate {
  public static String CHARSET_ASCII = FilesService.CHARSET_ASCII;
  public static String CHARSET_EBCDIC = FilesService.CHARSET_EBCDIC;

  public static String ACTION_RUN_TEMPLATE = "RUN_TEMPLATE";
  public static String ACTION_GEN_CHARGE_BACK = "GEN_CHARGE_BACK";

  public int HEXOP_ASSIGN = 1;
  public int HEXOP_ADD = 2;
  public int HEXOP_SUBTRACT = 3;
  public int HEXOP_MULTIPLY = 4;
  public int HEXOP_DIVIDE = 5;
  public int HEXOP_NEGATE = 6;
  public int HEXOP_MODULUS = 7;
  public int HEXOP_SET_MINIMUM = 8;
  public int HEXOP_SET_MAXIMUM = 9;
  public int HEXOP_SWAP_BYTES = 10;
  public int HEXOP_BINARY_AND = 11;
  public int HEXOP_BINARY_OR = 12;
  public int HEXOP_BINARY_XOR = 13;
  public int HEXOP_BINARY_INVERT = 14;

  public String[] args;
  // public String fileName;

  public int fileNum; // current fileNum
  public Map<Integer, MyFile> files = new LinkedHashMap<Integer, MyFile>();
  public MyFile isoFile;

  public ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
  public String charset = FilesService.CHARSET_EBCDIC;

  public int selection_start;
  public int selection_length;
  public byte[] clipboardData;
  public MyInputDialog inputDialog = new MyInputDialog(null);

  public IsoFileTemplate(String file, String charset) {
    // fileName = file;
    int filenum = FileLoad(file, charset);
    isoFile = FileSelect(filenum);
    this.charset = charset;
  }

  public IsoFileTemplate(String file) {
    this(file, CHARSET_EBCDIC);
  }

  public String GetArg(int index) {
    return args[index];
  }

  public int GetNumArgs() {
    return args.length;
  }

  public int GetFileNum() {
    return fileNum;
  }

  public String getFileName() {
    return isoFile.fileName;
  }

  public MyFileReader getFileReader() {
    try {
      if (isoFile.fileReader == null)
        isoFile.fileReader = new MyFileReader(isoFile.fileName, "r");
      return isoFile.fileReader;
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return null;
    }
  }

  public String GetFileName() {
    return getFileName();
  }

  public String FileNameGetBase(String filepath, boolean getBase) {
    File file = new File(filepath);
    return file.getName();
  }

  public String FileNameGetExtension(String filepath) {
    return filepath.substring(filepath.lastIndexOf("."), filepath.length() - 1);
  }

  public int CreateNewFile(int newFile) {
    String filepath = GetFileName();
    String filename = FileNameGetBase(filepath, false);
    // local string extension = FileNameGetExtension(filepath);
    newFile = FileNew();
    FileSelect(newFile);
    return newFile;
  }

  public int FileNew() {
    return FileNew(charset);
  }

  public int FileNew(String charset) {
    int fn = files.size() + 1;
    File file = new File(getFileReader().fileName);
    String folder = file.getParent();
    String filename = file.getName() + "." + String.valueOf(fn);

    filename = InputString("", "Please confirm new file name: ", filename);
    if (filename == null)
      return -1;
    return FileNew(folder + "\\" + filename, charset);
  }

  public MyFile createNewFile(String fileName, String mode) {
    return new MyFile(fileName, mode);
  }

  public int createIsoFile(String fileName, String charset, String mode) {
    charset = getCharset(charset);

    int fn = files.size() + 1;
    try {
      MyFile fr = createNewFile(fileName, mode);
      fr.fileReader.charset = Charset.forName(charset);
      if (mode.equals("rw"))
        fr.fileReader.setLength(0); // override existing file
      fr.fileReader.seek(0);
      fr.isoFileReader = this;

      files.put(fn, fr);
      return fn;
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return -1;
    }
  }

  public int FileNew(String fileName, String charset) {
    int fn = createIsoFile(fileName, charset, "rw");
    return fn;
  }

  public int FileLoad(String fileName, String charset) {
    int fn = createIsoFile(fileName, charset, "r");
    return fn;
  }

  public MyFile FileSelect(int filenum) {
    if (files.containsKey(filenum)) {
      fileNum = filenum;
      isoFile = files.get(filenum);
      return isoFile;
    }
    return isoFile;
  }

  // public Object Memcpy(Object dest, char[] src, int size, int i, int j) {
  // byte[] data = new byte[size];
  // ByteBuffer buffer = ByteBuffer.wrap(data);
  // dest = buffer.toString();
  // return dest;
  // }

  public MyField Memcpy(MyField dest, char[] src, int size, int i, int j) {
    System.out
        .println("MemCpy " + dest.getName() + " data: " + new String(src) + " offset: " + String.valueOf(dest.offset)
            + " size:" + String.valueOf(size));

    // byte[] data = new byte[size];
    // ByteBuffer buffer = ByteBuffer.wrap(data);
    dest.setData(new String(src));
    WriteBytes(src, dest.offset, size);

    return dest;
  }

  public MyField Memcpy(MyField dest, MyField src, int size, int i, int j) {
    dest.setData(src.getData());
    return dest;
  }

  public MyField Memcpy(MyField dest, String data, int size, int i, int j) {
    System.out.println("MemCpy " + dest.getName() + " data: " + data + " offset: " + String.valueOf(dest.offset)
        + " size:" + String.valueOf(size));
    dest.setData(data);
    WriteBytes(data, dest.offset, size);
    return dest;
  }

  public String Memcpy(String dest, String data, int size, int i, int j) {
    dest = data;
    // Utils.setFieldFromNameValue(this, dest, data);
    return dest;
  }

  public char[] Memcpy(char[] dest, String data, int size, int i, int j) {
    dest = data.toCharArray();
    // Utils.setFieldFromNameValue(this, dest, data);
    return dest;
  }

  public int Random(int limit) {
    Random rn = new Random();
    return rn.nextInt(limit);
  }

  public String SumString(char[] num1, char[] num2) {
    return SumString(new String(num1), new String(num2));
  }

  public String SumString(char[] num1, String num2) {
    return SumString(new String(num1), num2);
  }

  public String SumString(String num1, char[] num2) {
    return SumString(num1, new String(num2));
  }

  public String SumString(String num1, String num2) {
    String num1a = SubStr(num1, 0, Strlen(num1) - 6);
    String num1b = SubStr(num1, Strlen(num1) - 6, 6);
    String num2a = SubStr(num2, 0, Strlen(num2) - 6);
    String num2b = SubStr(num2, Strlen(num2) - 6, 6);

    int numB_tot = Atoi(num1b) + Atoi(num2b);
    int numA_tot = Atoi(num1a) + Atoi(num2a) + (numB_tot / 1000000);
    numB_tot = numB_tot % 1000000;

    String resultA = ""; // 9
    String resultB = "";
    resultA = SPrintf(resultA, "%09d", numA_tot);
    resultB = SPrintf(resultB, "%06d", numB_tot);
    // Printf("Adding %s and %s = %s\n", num1, num2, resultA+resultB);
    return resultA + resultB;
  }

  // update bitmap
  public int HexOperation(int operation, int start, int size, Object operand, int step, int skip) {

    if (operation == HEXOP_ADD && size == 4) {
      int t = ReadInt(start) + (int) operand;
      WriteBytes(getBytes(t), start, size);

    } else if (operation == HEXOP_BINARY_OR) {
      byte[] currentBytes = ReadBytes(start, size);
      System.out.println("--" + FilesService.bin2hex(currentBytes));
      currentBytes = HexOR(currentBytes, operand);
      WriteBytes(currentBytes, start, size);
    }
    return 1;
  }

  public byte[] HexOR(byte[] input, Object operand) {
    byte[] bytes = Utils.convertToByteArray((Long) operand);
    int i = 0;
    while (i <= input.length - 1 && i <= bytes.length - 1) {
      // System.out.println(i);
      input[i] = (byte) (input[i] | bytes[i]);
      i += 1;
    }
    System.out.println(FilesService.bin2hex(input));
    return input;
  }

  // update bitmap
  public int HexOperation(int operation, int start, int size, Object operand) {
    return HexOperation(operation, start, size, operand, 0, 0);
  }

  public void DeleteBytes(int insertAt, int size) {
    try {
      // make a nice new file:
      RandomAccessFile raf = isoFile.fileReader; // new RandomAccessFile(fileName, "rw");
      long currPos = raf.getFilePointer();
      int fileLength = (int) raf.length();
      // read all bytes from insert position to end
      byte[] buf = ReadBytes(insertAt + size, fileLength - (int) insertAt - size);

      // copy those read bytes to a later poisition (which enlarges the file)
      raf.seek(insertAt);
      raf.write(buf);
      raf.setLength(fileLength - size);
      raf.seek(insertAt);
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return;
    }
  }

  public void WriteBytes(byte[] insertBytes, int insertAt, int size) {
    try {

      // make a nice new file:
      RandomAccessFile raf = isoFile.fileReader; // new RandomAccessFile(fileName, "rw");
      long currPos = raf.getFilePointer();
      int fileLength = (int) raf.length();
      if (insertAt == fileLength) {
        InsertBytes(insertAt, size, insertBytes);
        return;
      } else if (insertAt > fileLength - size) {
        raf.seek(insertAt);
        raf.write(insertBytes);
        raf.setLength(insertAt + size);
        raf.seek(currPos);
        return;
      }

      // read all bytes from insert position to end
      byte[] buf = ReadBytes(insertAt + size, fileLength - insertAt - size);

      // copy those read bytes to a later poisition (which enlarges the file)
      raf.seek(insertAt);
      raf.write(insertBytes);

      if (buf != null && buf.length > 0) {
        raf.seek(insertAt + insertBytes.length);
        raf.write(buf);
      }

      raf.setLength(fileLength + insertBytes.length - size);
      raf.seek(currPos);
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return;
    }
  }

  public void WriteBytes(char[] data, int insertAt, int size) {
    WriteBytes(new String(data), insertAt, size);
  }

  public void WriteBytes(String data, int insertAt, int size) {
    if (data == null)
      data = "";
    if (data.length() < size) {
      if (Utils.isNumeric(data))
        data = Utils.multiplyChars("0", size - data.length()) + data;
      else
        data = data + Utils.multiplyChars(" ", size - data.length());

    } else if (data.length() > size)
      data = data.substring(0, size - 1);
    byte[] insertBytes = getBytes(data);
    WriteBytes(insertBytes, insertAt, size);
  }

  public void InsertBytes(int insertAt, int size, char data) {
    char[] myArray = new char[size];
    for (int i = 0; i < size; i++) {
      myArray[i] = data;
    }
    InsertBytes(insertAt, size, new String(myArray));
  }

  public void InsertBytes(int insertAt, int size, String data) {
    if (data.length() == 1)
      data = Utils.multiplyChars(data, size);

    InsertBytes(insertAt, getBytes(data));
  }

  public void InsertBytes(int insertAt, int size, int data) {
    byte[] myArray = new byte[size];
    byte[] tmp = Utils.convertToByteArray(data);
    for (int i = 0; i < size; i++) {
      myArray[i] = i < tmp.length ? tmp[i] : tmp[0];
    }
    InsertBytes(insertAt, size, new String(myArray));
  }

  public void InsertBytes(int insertAt, int size, byte[] data) {
    InsertBytes(insertAt, data);
  }

  public void InsertBytes(byte[] insertBytes) {
    InsertBytes(FileSize(), insertBytes);
  }

  public void InsertBytes(int insertAt, int length) {
    InsertBytes(insertAt, length, ' ');
  }

  public void InsertBytes(int insertAt, byte[] insertBytes) {
    try {
      // make a nice new file:
      RandomAccessFile raf = isoFile.fileReader; // new RandomAccessFile(fileName, "rw");
      long currPos = raf.getFilePointer();
      int fileLength = (int) raf.length();

      // to insert new stuff:
      // read all bytes from insert position to end
      byte[] buf = ReadBytes(insertAt, fileLength - (int) insertAt);

      // copy those read bytes to a later poisition (which enlarges the file)
      raf.seek(insertAt + insertBytes.length);
      raf.write(buf);
      // now write the inserted bytes at their new position (overwriting old values)

      if (insertBytes.length > 0) {
        raf.seek(insertAt);
        raf.write(insertBytes);
      }
      raf.setLength(fileLength + insertBytes.length);
      raf.seek(insertAt);

    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return;
    }
  }

  public void InsertBytes(int insertAt, String data) {
    byte[] insertBytes = getBytes(data);
    InsertBytes(insertAt, insertBytes);
  }

  public byte[] getBytes(String data) {
    return data.getBytes(getFileReader().charset);
  }

  public byte[] getBytes(int data) {
    return Utils.convertToByteArray(data);
  }

  public void InsertBytes(String data) {
    InsertBytes(FileSize(), data);
  }

  public void SetSelection(int start, int length) {
    selection_start = start;
    selection_length = length;
  }

  public void CopyToClipboard() {
    clipboardData = ReadBytes(selection_start, selection_length);
  }

  public void PasteFromClipboard() {
    if (clipboardData != null) {
      InsertBytes(FTell(), clipboardData);
      FSeek(FTell() + clipboardData.length);
    }
  }

  public void RunTemplate(String template) {

  }

  public void include(String template) {

  }

  public void RunScriptOnTempFiles(String path, String params) {

  }

  public int startof(IsoField fld) {
    if (fld == null)
      return 0;
    return toIntExact(fld.offset);
  }

  public int sizeof(IsoField fld) {
    return fld.getLength();
  }

  public int startof(IsoMessage fld) {
    return toIntExact(fld.offset);
  }

  public int sizeof(IsoMessage fld) {
    return fld.getLength();
  }

  public String asText() {
    if (isoFile == null)
      return null;
    return isoFile.asText();
  }

  public void StatusMessage(String message) {
    LogManager.getLogger().debug(message);
  }

  public int FileSize() {
    try {
      return toIntExact(isoFile.getFileReader().length());
    } catch (Exception ex) {
      return -1;
    }
  }

  public void FileClose() {
    try {
      isoFile.fileReader.close();
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
    }
  }

  public void FileOpen() {
    try {
      isoFile.fileReader = isoFile.getFileReader();
      isoFile.fileReader.seek(0);
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
    }
  }

  public void FileSave(String destination) {
    // FilesService.saveISOFile(isoFile, destination);
    MyDialog.showInformation("File [ " + destination + "] saved successfully ! ");
    FileClose();
  }

  public void SetCursorPos(int position) {
    FSeek(position);
  }

  public void FSkip(int length) {
    FSeek(FTell() + length);
  }

  public int FSeek(int position) {
    try {
      if (position > getFileReader().length())
        position = (int) getFileReader().length();
      if (position < 0)
        position = 0;
      getFileReader().seek(position);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return FTell();
  }

  public int FTell() {
    try {
      return (int) getFileReader().getFilePointer();
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return 0;
  }

  public int ReWind() {
    return FSeek(0);
  }

  public int MoveEnd() {
    try {
      getFileReader().seek(getFileReader().length());
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return FTell();
  }

  public char ReadChar() {
    return (char) ReadByte();
  }

  public char ReadChar(int position) {
    return (char) ReadByte(position);
  }

  public byte ReadByte(int position) {
    try {
      int curr = FTell();
      getFileReader().seek(position);
      byte r = getFileReader().readByte();
      FSeek(curr);
      return r;
    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return (byte) '\0';
  }

  public byte ReadByte() {
    try {
      int curr = FTell();
      byte r = getFileReader().readByte();
      FSeek(curr);
      return r;
    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return (byte) '\0';
  }

  public int ReadUByte() {
    try {
      int curr = FTell();
      // LogManager.getLogger().debug("ReadInt: " + String.valueOf(FTell()));
      int r = getFileReader().readUnsignedByte(); // getFileReader().readByte() & 0xFF;
      FSeek(curr);
      return r;
    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return (int) '\0';
  }

  public int ReadUInt() {
    return ReadInt();
  }

  public void ReadBytes(char[] data, int position, int length) {
    byte[] bytes = ReadBytes(position, length);
    data = new String(bytes).toCharArray();
  }

  public byte[] ReadBytes(int position, int length) {
    try {
      int curr = FTell();
      if (position + length > FileSize())
        length = FileSize() - position;

      if (length > 0) {
        byte[] b = new byte[length];
        getFileReader().seek((long) position);
        getFileReader().readFully(b);
        FSeek(curr);
        return b;
      }

    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return new byte[] {};
  }

  public byte[] ReadBytes(int length) {
    try {
      int curr = FTell();

      byte[] b = new byte[length];
      getFileReader().seek((long) curr);
      getFileReader().readFully(b);
      FSeek(curr);

      return b;
    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return new byte[] {};
  }

  public String ReadFileContent() {
    return ReadString(0, FileSize());
  }

  public String ReadString(int position, int length) {
    byte[] b = ReadBytes(position, length);
    if (b == null)
      return "";
    return new String(FilesService.bytesToString(b, isoFile.fileReader.charset));
    // return FilesService.convert2StringEBCDICToASCII(b);
  }

  public String ReadString(int length) {
    if (length <= 0)
      return "";
    return ReadString(FTell(), length);
  }

  public short ReadShort(int position) {
    int curr = FTell();
    FSeek(position);
    short r = ReadShort();
    FSeek(curr);
    return r;
  }

  public short ReadShort() {
    byte[] bytes = ReadBytes(2);
    if (bytes == null)
      return 0;
    if (byteOrder == ByteOrder.BIG_ENDIAN)
      return ByteBuffer.wrap(bytes).getShort();
    else
      return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
  }

  public long ReadLong(int position) {
    int curr = FTell();
    FSeek(position);
    long r = ReadLong();
    FSeek(curr);
    return r;
  }

  public long ReadLong() {
    byte[] bytes = ReadBytes(4);
    if (bytes == null)
      return 0;
    if (byteOrder == ByteOrder.BIG_ENDIAN)
      return ByteBuffer.wrap(bytes).getLong();
    else
      return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
  }

  public double ReadDouble(int position) {
    int curr = FTell();
    FSeek(position);
    double r = ReadDouble();
    FSeek(curr);
    return r;
  }

  public double ReadDouble() {
    byte[] bytes = ReadBytes(8);
    if (bytes == null)
      return 0;
    if (byteOrder == ByteOrder.BIG_ENDIAN)
      return ByteBuffer.wrap(bytes).getDouble();
    else
      return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
  }

  public float ReadFloat(int position) {
    int curr = FTell();
    FSeek(position);
    float r = ReadFloat();
    FSeek(curr);
    return r;
  }

  public float ReadFloat() {
    byte[] bytes = ReadBytes(4);
    if (bytes == null)
      return 0;
    if (byteOrder == ByteOrder.BIG_ENDIAN)
      return ByteBuffer.wrap(bytes).getFloat();
    else
      return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
  }

  public int ReadInt(int position) {
    int curr = FTell();
    FSeek(position);
    int r = ReadInt();
    FSeek(curr);
    return r;
  }

  public int ReadInt() {
    byte[] bytes = ReadBytes(4);
    if (bytes == null)
      return 0;
    if (byteOrder == ByteOrder.BIG_ENDIAN)
      return ByteBuffer.wrap(bytes).getInt();
    else
      return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

    // try {

    // int curr = FTell();
    // int r = getFileReader().readInt();
    // FSeek(curr);
    // return r;
    // } catch (EOFException ex) {
    // // LogManager.getLogger().error(ex);
    // } catch (IOException ex) {
    // LogManager.getLogger().error(ex);
    // }
    // return (int) '\0';
  }

  public void PrepareFile() {

  }

  public String getCharset(String charset) {
    if (charset.equalsIgnoreCase("EBCDIC"))
      charset = FilesService.CHARSET_EBCDIC;
    return charset;
  }

  public void SetFileInterface(String charset1) {
    this.charset = getCharset(charset1);
  }

  public void BigEndian() {
    byteOrder = ByteOrder.BIG_ENDIAN;
  }

  public void LittleEndian() {
    byteOrder = ByteOrder.LITTLE_ENDIAN;
  }

  public boolean FEof() {
    long curr = -1;

    try {
      curr = getFileReader().getFilePointer();
      if (curr >= getFileReader().length())
        return true;
      return false;
    } catch (EOFException ex) {
      // LogManager.getLogger().error(ex);
      return true;
    } catch (IOException ex) {
      LogManager.getLogger().error(ex);
    }
    return false;
  }

  public int GetLenTillEndOfField() {
    int curr = FTell();
    int Length = 0;
    while (ReadByte(curr + Length) != -32) {
      Length++;
    }
    FSeek(curr);
    return Length;
  }

  public int GetFieldString() {
    int Length = 0;
    while (!FEof() && ReadByte((int) FTell() + Length) != -32) {
      Length++;
    }
    return Length;
  }

  public String SPrintf(String text, String format, Object... args) {
    text = String.format(format, args);
    return text;
  }

  public char[] SPrintf(char[] text, String format, Object... args) {
    text = String.format(format, args).toCharArray();
    return text;
  }

  public void Printf(String text, Object... args) {
    LogManager.getLogger().debug(String.format(text, args));
  }

  public boolean exists(Object a) {
    return a != null;
  }

  public boolean exists(Object[] arr, int index) {
    return arr != null && index < arr.length && arr[index] != null;
  }

  public boolean exists(IsoMessage msg, String attribute) {
    String v = msg.getAttribute(attribute);
    return v != null && !v.isEmpty();
  }

  public int sizeof(Class dataType) {
    if (dataType == null)
      throw new NullPointerException();

    if (dataType == int.class || dataType == Integer.class)
      return 4;
    if (dataType == short.class || dataType == Short.class)
      return 2;
    if (dataType == byte.class || dataType == Byte.class)
      return 1;
    if (dataType == char.class || dataType == Character.class)
      return 2;
    if (dataType == long.class || dataType == Long.class)
      return 8;
    if (dataType == float.class || dataType == Float.class)
      return 4;
    if (dataType == double.class || dataType == Double.class)
      return 8;

    return 4; // 32-bit memory pointer...
              // (I'm not sure how this works on a 64-bit OS)
  }

  public int ConvertEBCDICToASCII(long a) {
    return (int) a;
  }

  public char ToUpper(char A) {
    return Character.toUpperCase(A);
  }

  String HexToBin(char[] hex) {
    String bin = "";
    int i;
    for (i = 0; i < 2; i++) {
      switch (ToUpper(hex[i])) {
        case '0':
          bin = Strcat(bin, "0000");
          break;

        case '1':
          bin = Strcat(bin, "0001");
          break;

        case '2':
          bin = Strcat(bin, "0010");
          break;

        case '3':
          bin = Strcat(bin, "0011");
          break;

        case '4':
          bin = Strcat(bin, "0100");
          break;

        case '5':
          bin = Strcat(bin, "0101");
          break;

        case '6':
          bin = Strcat(bin, "0110");
          break;

        case '7':
          bin = Strcat(bin, "0111");
          break;

        case '8':
          bin = Strcat(bin, "1000");
          break;

        case '9':
          bin = Strcat(bin, "1001");
          break;

        case 'A':
          bin = Strcat(bin, "1010");
          break;

        case 'B':
          bin = Strcat(bin, "1011");
          break;

        case 'C':
          bin = Strcat(bin, "1100");
          break;

        case 'D':
          bin = Strcat(bin, "1101");
          break;

        case 'E':
          bin = Strcat(bin, "1110");
          break;

        case 'F':
          bin = Strcat(bin, "1111");
          break;

        default:
          Printf("Invalid hex value %c\n", hex[i]);
          return "00000000";
      }
    }
    return bin;
  }

  // String getPDS1000No (//struct //PDS_1000_STR &UnkPDS){
  // char PDSText[];
  // SPrintf (PDSText, "%04d - Description not Available", UnkPDS.locPDSNo);
  // return ConvertString(PDSText, CHARSET_ASCII, CHARSET_EBCDIC);
  // }

  // String getUnkPDSNo (//struct unknownSTR &UnkPDS){
  // char PDSText[];
  // SPrintf (PDSText, "%04d - Description not Available", UnkPDS.locPDSNo);
  // return ConvertString(PDSText, CHARSET_ASCII, CHARSET_EBCDIC);
  // }

  // return the number of transaction having pan found

  public char ConvertASCIIToEBCDIC(char s) {
    return ConvertString(String.valueOf(s), CHARSET_ASCII, CHARSET_EBCDIC).charAt(0);
  }

  public String ConvertString(Object tag, String srcCharset, String destCharset) {
    if (tag == null)
      return null;

    String value;
    if (tag instanceof char[] || tag instanceof byte[])
      value = String.valueOf(tag);
    else if (tag instanceof IsoField)
      value = ((IsoField) tag).getData();
    else
      value = (String) tag;

    // return new String(value.getBytes(Charset.forName(destCharset)));

    // if (srcCharset.equalsIgnoreCase(CHARSET_ASCII))
    // return new String(FilesService.getBytesForEBIG(value));
    return FilesService.ConvertString(value, srcCharset, destCharset);
  }

  // public String ConvertString(char[] chars, String srcCharset, String
  // destCharset) {
  // return FilesService.ConvertString(String.valueOf(chars), srcCharset,
  // destCharset);
  // }

  public String InputString(String title, String msg, String defaultValue) {
    // return defaultValue;
    return inputDialog.showTextBox(msg, defaultValue);
  }

  public int InputNumber(String title, String msg, String defaultValue) {
    // return Integer.parseInt(defaultValue);

    return Integer.parseInt(inputDialog.showTextBox(msg, defaultValue));
  }

  public void Exit(Object i) {
    // MyDialog.showInformation(i instanceof String ? (String) i : "Stop functioning
    // due to error or data validation !");
    // throw new Exception(i instanceof String ? (String) i : "Stop functioning due
    // to error or data validation !");
  }

  public String SubStr(String data, int start, int length) {
    return data.substring(start, start + length);
  }

  public String SubStr(char[] data, int start, int length) {
    return SubStr(new String(data), start, length);
  }

  // CPP functions
  public long Pow(int x, int y) {
    return (long) Math.pow((double) x, (double) y);
  }

  public String Strcat(String bin, String val) {
    if (bin == null)
      bin = "";
    bin = bin + val;
    return bin;
  }

  public int Strstr(String Str, String inStr) {
    return Str.indexOf(inStr);
  }

  public int Strcmp(String Str, String anotherString) {
    if (Str == null)
      return -1;
    return Str.compareToIgnoreCase(anotherString);
  }

  public int Strcmpi(String Str, String anotherString) {
    if (Str == null)
      return -1;
    return Str.compareTo(anotherString);
  }

  public int Atoi(String v) {
    try {
      return Integer.parseInt(v);
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return 0;
    }
  }

  public int Atoi(char[] v) {
    try {
      return Integer.parseInt(String.valueOf(v));
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
      return 0;
    }
  }

  public int Strlen(String v) {
    return v.length();
  }

  public int Strlen(char[] v) {
    return v.length;
  }

  public int Abs(int i) {
    return Math.abs(i);
  }

  public double Sin(double i) {
    return Math.sin(i);
  }

  public double Cos(double i) {
    return Math.cos(i);
  }

  public double Sqrt(double i) {
    return Math.sqrt(i);
  }

  public double Tan(double i) {
    return Math.tan(i);
  }

  public double Atan(double i) {
    return Math.atan(i);
  }

  public double Log(double i) {
    return Math.log(i);
  }

  // ---------- Main Function --------------
  public void run() {
    run(new String[] {});
  }

  public void run(String[] args) {
    // run(new String[] {});
  }

  public String[] getActionsList() {
    return new String[] { ACTION_RUN_TEMPLATE, ACTION_GEN_CHARGE_BACK };
  }

  public IsoFileTemplate createTemplate(String action, String fileName) {
    return new IsoFileTemplate(fileName);
  }

}
