package com.java.modules.files.templates.mastercard;

import java.math.BigInteger;

import com.java.core.components.MyDialog;
import com.java.core.logs.LogManager;
import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.MyFile;
import com.java.modules.files.isoparser.elements.iso.IsoField;
import com.java.modules.files.isoparser.elements.iso.IsoFile;
import com.java.modules.files.isoparser.elements.iso.IsoMessage;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.templates.IsoFileTemplate;
import com.java.core.utils.Utils;

//migrate code from 010 Tool. 
//Rule1: Sprintf(a, ..) ===> a = Sprintf(a, ..)
//Rule2: exists(arr[i]) ===> exists(arr, i)
//Rule3: compare string, dont use == ===> use .equals
//Rule4: !(1,0) ==> == 0, == 1
public class IpmFileTemplate extends IsoFileTemplate {
  public static String ACTION_RUN_TEMPLATE = "Mastercard";

  // globals
  public IpmMessage[] ThisMsg;

  // local
  int new_file;
  String typeOfCb;
  String PAN_A;
  String de25;
  String de30;
  String de50;
  String de30_2;
  int[] allPositions = new int[4];
  int de30_int = 0;
  char[] de_30_str = new char[12];
  int[] pan_pos = new int[100000];
  int no_transactions;
  int i = 0;
  int p = 0;
  int lastPresentment;
  int skip_revert = 1;
  String message_count;

  Character IncluceRecSize = 0;
  int rec_size;
  int iccLen = 0;
  int tran_no = 0;
  int lastDummyLength = -1;

  public IpmFileTemplate(String file) {
    super(file);
  }

  @Override
  public MyFile createNewFile(String fileName, String mode) {
    return new IsoFile(fileName, mode);
  }

  public void initThisMsg() {
    this.ThisMsg = new IpmMessage[isoFile.getMessages().size()];
    int i = 0;

    for (IsoMessage msg : ((IsoFile) isoFile).getIsoMessages()) {
      this.ThisMsg[i] = (IpmMessage) msg;
      this.ThisMsg[i].initFields();
      i += 1;
    }
  }

  public Object Memcpy(IpmMessage msg, String attribute, char[] data, int size, int i, int j) {
    // byte[] data = new byte[size];
    return Memcpy(msg, attribute, new String(data), size, i, j);
  }

  public Object Memcpy(IpmMessage msg, String attribute, String data, int size, int i, int j) {

    // byte[] data = new byte[size];
    int pos = msg.setAttribute(attribute, data);
    WriteBytes(data, pos, size);
    System.out.println(
        "MemCpy " + msg.getDescription() + " " + attribute + " data: " + data + " size:" + String.valueOf(size));
    return msg;
  }

  // caculate dynamic field length
  int decodeLLxVARElem(int fieldLenMax, int len) {
    // return -1 * len;
    lastDummyLength = len;

    int fieldLen;
    int pos;
    int i;

    fieldLen = 0;
    pos = len - 1;

    // new caculation
    String tmp = ReadString(len);
    try {
      fieldLen = Integer.parseInt(tmp);
    } catch (Exception ex) {
      fieldLen = fieldLenMax;
    }

    FSkip(len);

    if (fieldLen <= fieldLenMax)
      return fieldLen;
    else
      return fieldLenMax;
  }

  String readMessage() {

    int[] Bitmap = new int[] { 0, 0, 0, 0 };
    int lenBitmap = 0;
    int Msg;
    int i;

    // 1. MTI
    addIsoField("MTI", 4, "Message Type Indicator");
    getCurrentMessage().offset = FTell() - 4;

    byte[] bm1 = ReadBytes(8);
    FSkip(8);
    byte[] bm2 = ReadBytes(8);
    FSkip(8);
    getCurrentMessage().setBitmapPrimary(bm1);
    getCurrentMessage().setBitmapSecondary(bm2);
    FSkip(-16);

    // read bitmap -- method 2
    Bitmap[0] = ReadInt();
    FSkip(4);

    Bitmap[1] = ReadInt();
    FSkip(4);

    // FSkip(8);

    if ((Bitmap[0] != 0x80000000)) { // 2147483648 = 0x80000000

      Bitmap[2] = ReadInt();
      FSkip(4);

      Bitmap[3] = ReadInt();
      FSkip(4);

      lenBitmap = 16;
    } else {
      lenBitmap = 8;
    }

    // Printf ("New Msg \r\n");
    // loop over all the possible messages
    for (i = 0; i < 4; i++) {
      for (Msg = 31; Msg >= 0; Msg--) {
        if ((Bitmap[i] & (int) (0x01 << Msg)) != 0) {
          // Printf ("%s \r\n", decodeDE ((127 - (((3-i) * 32) + Msg)) + 1));
          decodeDE((127 - (((3 - i) * 32) + Msg)) + 1);
        }
      }
    }

    getCurrentMessage().length = (Integer) FTell() - getCurrentMessage().offset;
    getCurrentMessage().rawData = ReadBytes(getCurrentMessage().offset, getCurrentMessage().length);

    return "";
  }

  public int ProcessNextTrans(int IncluceRecSize) {
    int rec_size = 0;

    if (IncluceRecSize == 1) {
      rec_size = ReadUInt();
      FSkip(4);
    }

    // LogManager.getLogger().debug(String.valueOf(rec_size) + ".");
    if ((rec_size > 0) || IncluceRecSize == 0) {
      readMessage();
      getCurrentMessage().length = rec_size;

      // //typedef //struct {
      // readMessage();
      // int icc = iccLen;
      // } Message;

      // Message ThisMsg <name=SetMsgDesc>;
    }
    return rec_size;
  }

  public IsoField getParentField(String tag) {
    String parentTag = "", parentDesc = "";
    int length = 0;
    LengthType lengthType = LengthType.Fixed;

    IsoField fld;

    parentTag = FilesService.getParentTag(tag);

    // IpmMessageDefinition defs = new IpmMessageDefinition();

    switch (parentTag.toUpperCase()) {
      case "PDS_0105":
      case "PDS_0122":
      case "PDS_0301":
      case "PDS_0306":
      case "PDS_0023":
      case "PDS_0148":
      case "PDS_0158":
      case "PDS_0165":
      case "PDS_0191":

        parentTag = "DE_48";
        length = 3;
        lengthType = LengthType.Embedded;
        break;

      default:
        if (parentTag.equalsIgnoreCase(tag)) {
          return null;
        }
        // get last Dummy Length from last function decodeLLxVARElem(19, 2);
        if (lastDummyLength > 0) { //
          length = lastDummyLength;
          lengthType = LengthType.Embedded;
          lastDummyLength = -1;
        }
        break;
    }

    IsoFieldDefinition def = null;
    IpmMessageDefinition msgDef = new IpmMessageDefinition();
    int fieldIdx = FilesService.getFieldIndexByTag(parentTag);
    // System.out.println("getFieldIdex " + parentTag + " " +
    // String.valueOf(fieldIdx));

    if (msgDef.getFieldDefinitions().containsKey(fieldIdx)) {
      def = msgDef.getDefinition(fieldIdx);
    } else {
      def = new IsoFieldDefinition();
      def.name = parentTag;
      def.length = length;
      def.lengthType = lengthType;
    }

    fld = getIsoField(parentTag, true); // new IsoField();
    if (fld != null) {
      fld.setName(def.name);
      fld.description = parentDesc;
      fld.setLength(def.length);
      fld.setLengthType(def.lengthType);
      fld.definition = def;
    }

    return fld;
  }

  // correct source amount
  void CorrectAmount() {
    String temp;
    String amount; // 16
    String amountA; // 12
    int i = 1;
    int lineNum;
    String MTI = ConvertString("1644", CHARSET_ASCII, CHARSET_EBCDIC);
    String code = ConvertString("695", CHARSET_ASCII, CHARSET_EBCDIC);

    while (exists(ThisMsg, i)) {
      amount = "0000000000000000";
      while (!(ThisMsg[i].MTI.equals(MTI)) || !(ThisMsg[i].DE_24.getData().equals(code))) {
        if (exists(ThisMsg[i].DE_4)) {
          amountA = ConvertString(ThisMsg[i].DE_4, CHARSET_EBCDIC, CHARSET_ASCII);
          amount = SumString(amountA, amount);
        }
        if (i == ThisMsg.length - 1)
          break;
        i++;
      }
      temp = ConvertString(ThisMsg[i].getAttribute("PDS.PDS_0301"), CHARSET_EBCDIC, CHARSET_ASCII);

      if (!amount.equals(temp)) {
        // Printf("Warning: source_amount Required (%s), Found (%s)\n", amount, temp);
        amount = ConvertString(amount, CHARSET_ASCII, CHARSET_EBCDIC);
        Memcpy(ThisMsg[i], "PDS.PDS_0301", amount, 16, 0, 0);
      }
      i++;
    }

  }

  // correct source amount
  void CorrectMessageNum() {
    String message_num = null; // 8
    String message_num_E;
    String found; // 8
    int i = 0;
    int msg_num = 0;

    while (exists(ThisMsg, i)) {
      message_num = SPrintf(message_num, "%08d", msg_num + 1);

      if (exists(ThisMsg[i].DE_71)) {
        found = ConvertString(ThisMsg[i].DE_71, CHARSET_EBCDIC, CHARSET_ASCII);
        if (!message_num.equals(found)) {
          // Printf("Incorrect message number found for Message[%d]. Expected %s, found
          // %s\n", i,message_num, found);
          message_num_E = ConvertString(message_num, CHARSET_ASCII, CHARSET_EBCDIC);
          Memcpy(ThisMsg[i].DE_71, message_num_E, 8, 0, 0);
        }
      }

      // fix trailer pds 0306
      if (exists(ThisMsg[i], "PDS.PDS_0306")) {
        found = ConvertString(ThisMsg[i].PDS.PDS_0306, CHARSET_EBCDIC, CHARSET_ASCII);
        if (!message_num.equals(found)) {
          // Printf("Incorrect message number found for Message[%d]. Expected %s, found
          // %s\n", i,message_num, found);
          message_num_E = ConvertString(message_num, CHARSET_ASCII, CHARSET_EBCDIC);
          Memcpy(ThisMsg[i], "PDS.PDS_0306", message_num_E, 8, 0, 0);
        }
        msg_num = 0;
      }

      msg_num++;
      i++;
    }
  }

  public void CorrectTrailer() {
    CorrectAmount();
    CorrectMessageNum();
  }

  public void RevertFile() {
    FSeek(0);

    if (FileSize() > 1013) {
      // check if 1013th char is an @ sign
      if (ReadByte(1012) == 0x40 && ReadByte(2024) == 0x40) {
        Printf("The File already reverted\n");
        Exit(0);
        return;
      }

      while (!FEof()) {
        FSkip(1012);
        Printf("Inserted at %d\n", FTell());
        InsertBytes(FTell(), 2, 0x40);
        FSkip(2);
      }
    }

    FSeek(FileSize() - 1);

    while (ReadByte(FTell()) == 0x00 || ReadByte(FTell()) == 0x40) {
      DeleteBytes(FTell(), 1);
      FSeek(FileSize() - 1);
    }

    FSeek(FileSize());
    InsertBytes(FTell(), 4, 0x00);
    FSkip(4);

    while (FileSize() % 1014 != 0) {
      InsertBytes(FTell(), 1, 0x40);
      FSkip(1);
    }
  }

  public IsoFile getIsoFile() {
    return (IsoFile) isoFile;
  }

  public void addIsoField(String tag, int length, String desc) {
    IsoField parentField = getParentField(tag);

    String value = ReadString(length);

    if (length > 0)
      FSkip(length);

    IsoMessage msg;
    if (tag.equalsIgnoreCase("MTI")) { // start new message
      msg = IsoMessage.newInstance(FilesService.TYPE_MASTERCARD);
      msg.offset = FTell() - length;
      msg.setMti(value);
      msg.number = getIsoFile().getIsoMessages().size();
      getIsoFile().getIsoMessages().add(msg);
      return;
    }
    LengthType lengthType = LengthType.Fixed;
    if (lastDummyLength > 0) { //
      length = lastDummyLength;
      lengthType = LengthType.Embedded;
      lastDummyLength = -1;
    }

    IsoField fld = new IsoField();
    fld.setName(tag);
    fld.description = desc;
    fld.setLength(length);
    fld.setLengthType(lengthType);
    fld.offset = FTell() - length;
    fld.setData(value);

    if (parentField != null) {
      parentField.getChildren().add(fld);
      parentField.parseChildrenAndUpdateParent();

    } else if (getCurrentMessage() != null) {
      getCurrentMessage().addField(fld);
    }
  }

  public void updateIsoField(String tag, String desc) {
    IsoField fld = getIsoField(tag);
    if (fld != null) {
      if (!desc.isEmpty())
        fld.description = desc;
    }
  }

  public IsoMessage getCurrentMessage() {
    return getIsoFile().getIsoMessages().size() == 0 ? null
        : getIsoFile().getIsoMessages().get(getIsoFile().getIsoMessages().size() - 1);
  }

  public IsoField getIsoField(String tag) {
    return getIsoField(tag, false);
  }

  public IsoField getIsoField(String tag, boolean autoCreate) {
    if (tag == null || tag.isEmpty())
      return null;
    IsoMessage currMsg = getCurrentMessage();
    if (currMsg != null) {
      for (IsoField fld : currMsg.getIsoFields()) {
        if (fld.getName().equalsIgnoreCase(tag)) {
          return fld;
        }
      }
      if (autoCreate) {
        IsoField fld = new IsoField();
        fld.isFullyParsed = false;
        fld.name = tag;
        currMsg.addField(fld);
        return fld;
      }
    }

    for (IsoMessage msg : getIsoFile().getIsoMessages()) {
      for (IsoField fld : msg.getIsoFields()) {
        if (fld.getName().equalsIgnoreCase(tag)) {
          return fld;
        }
      }
    }

    return null;
  }

  int ProcessICCCode(int dL) {

    // typedef struct(int dL){
    int code;
    int tempLen = 0;
    code = ReadUByte();
    if (code == 0x9F || code == 0x5F) {
      addIsoField("tag", 2, "Tag Value");
      // UBYTE tag[2] <open = suppress, name = "Tag Value">;
      tempLen += 2;
    } else {
      addIsoField("tag", 1, "Tag Value");

      // UBYTE tag[1] <open = suppress, name = "Tag Value">;
      tempLen++;
    }

    // UBYTE len <open = suppress, name = "Length">;
    addIsoField("len", 2, "Length");

    int len = 0;
    int l = len;
    tempLen++;
    if (dL - tempLen - len < 0) {
      // Printf("Warning: ICC field %s in transaction %d has exceeded length by %d
      // bytes\n", ConvertString(NameICC(tag), CHARSET_EBCDIC, CHARSET_ASCII),
      // tran_no,tempLen+len-dL);
      if (dL - tempLen > 0) {
        l = dL - tempLen;
        addIsoField("data", dL - tempLen, "Invalid Structure");
        // UBYTE data[dL - tempLen] <open = suppress, name = "Invalid Structure",
        // bgcolor = cRed>;
      } else
        FSkip(dL - tempLen);
    } else {
      addIsoField("data", len, "Data");

      // UBYTE data[len] <hidden = true, open = suppress, name = "Data">;
    }
    tempLen += len;

    // }iccMsg;

    // iccMsg iccCode(dL)<open = suppress, name = NameeICC_FN, read = readData,
    // write=writeData>;
    return tempLen;
  }

  String decodeDE(int MsgNo) {
    System.out
        .println("Decode DE_" + String.valueOf(MsgNo) + " #" + String.valueOf(isoFile.getMessages().size()) + " pos: "
            + FTell() + "");
    int dummyLen;
    int PrevPos;

    switch (MsgNo) {

      case 1:
        return "Bitmap";

      case 2://
        dummyLen = decodeLLxVARElem(19, 2);
        addIsoField("DE_2", dummyLen, "002 - Primary Account Number (PANxx)");
        return "002 - Primary Account Number (PAN)";

      case 3: //
        // //typedef //struct {
        addIsoField("DE_3_01", 2, "01 - Cardholder Transaction Type");
        addIsoField("DE_3_02", 2, "02 - Cardholder From Account Code");
        addIsoField("DE_3_03", 2, "03 - Cardholder To Account Code");
        // } DE_3_STR;

        updateIsoField("DE_3", "003 - Processing Code");

        return "003 - Processing Code";

      case 4: //
        addIsoField("DE_4", 12, "004 - Amount, Txn");
        return "004 - Amount, Txn";

      case 5: //
        addIsoField("DE_5", 12, "005 - Amount, Reconciliation");
        return "005 - Amount, Reconciliation";

      case 6: //
        addIsoField("DE_6", 12, "006 - Amount, Cardholder Billing");
        return "006 - Amount, Cardholder Billing";

      case 7:
        addIsoField("DE_7", 10, "007 - Date and Time, Transmission");
        return "007 - Date and Time, Transmission";

      case 8:
        addIsoField("DE_8", 8, "008 - Amount, Cardholder Billing Fee");
        return "008 - Amount, Cardholder Billing Fee";

      case 9:
        addIsoField("DE_9", 8, "009 - Conversion Rate, Reconciliation");
        return "009 - Conversion Rate, Reconciliation";

      case 10:
        addIsoField("DE_10", 8, "010 - Conversion Rate, Cardholder Billing");
        return "010 - Conversion Rate, Cardholder Billing";

      case 11:
        addIsoField("DE_11", 6, "011 - Systems Trace Audit Number");
        return "011 - Systems Trace Audit Number";

      case 12: //
        // //typedef //struct {
        addIsoField("DE_12_01", 6, "01 - Date");
        addIsoField("DE_12_02", 6, "02 - Time");
        // } DE_12_STR;

        // DE_12_STR DE_12 <name="012 - Date and Time, Txn");
        return "012 - Date and Time, Txn";

      case 13: // Not Used by MC
        addIsoField("DE_13", 4, "013 - Date, Effective");
        return "013 - Date, Effective";

      case 14:
        addIsoField("DE_14", 4, "014 - Date, Expiration");
        return "014 - Date, Expiration";

      case 15: // Not Used by MC
        addIsoField("DE_15", 4, "015 - Date, Settlement");
        return "015 - Date, Settlement";

      case 16: // Not Used by MC
        addIsoField("DE_16", 4, "016 - Date, Conversion");
        return "016 - Date, Conversion";

      case 17: // Not Used by MC
        addIsoField("DE_17", 4, "017 - Date, Capture");
        return "017 - Date, Capture";

      case 18: // Not Used by MC
        addIsoField("DE_18", 4, "018 - Merchant Type");
        return "018 - Merchant Type";

      case 19: // Not Used by MC
        addIsoField("DE_19", 3, "019 - Country Code, Acquiring Inst");
        return "019 - Country Code, Acquiring Inst";

      case 20: // Not Used by MC
        addIsoField("DE_20", 3, "020 - Country Code, Primary Account Number");
        return "020 - Country Code, Primary Account Number";

      case 21: // Not Used by MC
        addIsoField("DE_21", 3, "021 - Country Code, Forwarding Inst");
        return "021 - Country Code, Forwarding Inst";

      case 22:
        // //typedef //struct {
        addIsoField("DE_22_01", 1, "01 - Terminal Data: Card Data Input Capability");
        addIsoField("DE_22_02", 1, "02 - Terminal Data: CardHolder Authentication Capability");
        addIsoField("DE_22_03", 1, "03 - Terminal Data: Card Capture Capability");
        addIsoField("DE_22_04", 1, "04 - Terminal Operating Environment");
        addIsoField("DE_22_05", 1, "05 - Cardholder Present Data");
        addIsoField("DE_22_06", 1, "06 - Card Present Data");
        addIsoField("DE_22_07", 1, "07 - Card Data: Input Mode");
        addIsoField("DE_22_08", 1, "08 - CardHolder Authentication Method");
        addIsoField("DE_22_09", 1, "09 - CardHolder Authentication Entity");
        addIsoField("DE_22_10", 1, "10 - Card Data Output Capability");
        addIsoField("DE_22_11", 1, "11 - Terminal Data Output Capability");
        addIsoField("DE_22_12", 1, "12 - Pin Capture Capability");
        // } DE_22_STR;

        // DE_22_STR DE_22 <name="022 - Point of Service Data Code");

        return "022 - Point of Service Data Code";

      case 23:
        addIsoField("DE_23", 3, "023 - Card Sequence Number");
        return "023 - Card Sequence Number";

      case 24:
        addIsoField("DE_24", 3, "024 - Function Code");
        return "024 - Function Code";

      case 25: //
        addIsoField("DE_25", 4, "025 - Message Reason Code"); // not agree
        return "025 - Message Reason Code";

      case 26: //
        addIsoField("DE_26", 4, "026 - Card Acceptor Business Code (MCC)");// not agree
        return "026 - Card Acceptor Business Code (MCC)";

      case 27:
        addIsoField("DE_27", 1, "027 - Approval Code Length");
        return "027 - Approval Code Length";

      case 28:
        addIsoField("DE_28", 3, "028 - Date, Reconciliation");
        return "028 - Date, Reconciliation";

      case 29:
        addIsoField("DE_29", 3, "029 - Reconciliation Indicator");
        return "029 - Reconciliation Indicator";

      case 30:
        // //typedef //struct {
        addIsoField("DE_30_01", 12, "01 - Original Amount, Transaction");
        addIsoField("DE_30_02", 12, "02 - Original Amount, Reconciliation");
        // } DE_30_STR;

        // DE_30_STR DE_30 <name="030 - Amounts, Original");

        return "030 - Amounts, Original";

      case 31: //
        dummyLen = decodeLLxVARElem(23, 2);

        // //typedef //struct {
        addIsoField("DE_31_01", 1, "01 - Mixed Use");
        addIsoField("DE_31_02", 6, "02 - Acquirer's Bin");
        addIsoField("DE_31_03", 4, "03 - Julian Processing Date YDDD");
        addIsoField("DE_31_04", 11, "04 - Acquirer's Sequence Number");
        addIsoField("DE_31_05", 1, "05 - Check Digit");
        // } DE_31_STR;

        // DE_31_STR DE_31 <name="031 - Acquirer Reference Data");

        return "031 - Acquirer Reference Data";

      case 32:
        dummyLen = decodeLLxVARElem(11, 2);
        addIsoField("DE_32", dummyLen, "032 - Acquirer Inst Id Code");
        return "032 - Acquirer Inst Id Code";

      case 33://
        dummyLen = decodeLLxVARElem(11, 2);
        addIsoField("DE_33", dummyLen, "033 - Forwarding Inst Id Code");
        return "033 - Forwarding Inst Id Code";

      case 34:
        addIsoField("DE_34", 3, "034 - Primary Account Number, Extended");
        return "034 - Primary Account Number, Extended";

      case 35:
        addIsoField("DE_35", 3, "035 - Track 2 Data");
        return "035 - Track 2 Data";

      case 36:
        addIsoField("DE_36", 3, "036 - Track 3 Data");
        return "036 - Track 3 Data";

      case 37://
        addIsoField("DE_37", 12, "037 - Retrieval Reference Number");
        return "037 - Retrieval Reference Number";

      case 38://
        addIsoField("DE_38", 6, "038 - Approval Code");
        return "038 - Approval Code";

      case 39: // Not Used by MC
        addIsoField("DE_39", 3, "039 - Action Code");
        return "039 - Action Code";

      case 40:
        addIsoField("DE_40", 3, "040 - Service Code");
        return "040 - Service Code";

      case 41:
        addIsoField("DE_41", 8, "041 - Card Acceptor Terminal Id");
        return "041 - Card Acceptor Terminal Id";

      case 42://
        addIsoField("DE_42", 15, "042 - Card Acceptor Id Code");
        return "042 - Card Acceptor Id Code";

      case 43://

        dummyLen = decodeLLxVARElem(99, 2);
        int StartField = (int) FTell();

        int FieldLen1;
        int FieldLen2;
        int FieldLen3;
        FieldLen1 = GetLenTillEndOfField();
        FSkip(FieldLen1 + 1);
        FieldLen2 = GetLenTillEndOfField();
        FSkip(FieldLen2 + 1);
        FieldLen3 = GetLenTillEndOfField();
        FSkip(FieldLen3 + 1);

        FSeek(StartField);

        if (FieldLen1 + FieldLen2 + FieldLen3 + 19 <= dummyLen) {
          if (FieldLen1 > 0)
            addIsoField("DE_43_01", FieldLen1, "01 - Card Acceptor Name");
          FSkip(1);
          if (FieldLen2 > 0)
            addIsoField("DE_43_02", FieldLen2, "02 - Card Acceptor Street Address");
          FSkip(1);
          if (FieldLen3 > 0)
            addIsoField("DE_43_03", FieldLen3, "03 - Card Acceptor City");
          FSkip(1);

          addIsoField("DE_43_04", 10, "04 - Postal Code");
          addIsoField("DE_43_05", 3, "05 - State, Province Or Region Code");
          addIsoField("DE_43_06", 3, "06 - Country Code");
        } else {
          addIsoField("DE_43", dummyLen, "043 - Unparsed Data");
        }

        // //typedef //struct (int dummyLen) {
        // int StartField = FTell();

        // int FieldLen1;
        // int FieldLen2;
        // int FieldLen3;

        // FieldLen1 = GetLenTillEndOfField ();
        // FSkip(FieldLen1+1);
        // FieldLen2 = GetLenTillEndOfField ();
        // FSkip(FieldLen2+1);
        // FieldLen3 = GetLenTillEndOfField ();
        // FSkip(FieldLen3+1);

        // FSeek(StartField);
        // if(FieldLen1 + FieldLen2 + FieldLen3 + 19 <= dummyLen){
        // if (FieldLen1 > 0){
        // addIsoField("DE_43_01", FieldLen1, "01 - Card Acceptor Name");
        // }
        // FSkip (1);

        // if (FieldLen2 > 0){
        // addIsoField("DE_43_02", FieldLen2, "02 - Card Acceptor Street Address");
        // }
        // FSkip (1);

        // if (FieldLen3 > 0){
        // addIsoField("DE_43_03", FieldLen3, "03 - Card Acceptor City");
        // }
        // FSkip (1);

        // addIsoField("DE_43_04", 10, "04 - Postal Code");
        // addIsoField("DE_43_05", 3, "05 - State, Province Or Region Code");
        // addIsoField("DE_43_06", 3, "06 - Country Code");
        // }
        // else{
        // //BYTE DE_43[dummyLen] <open=suppress, name = "043 - Unparsed Data");

        // }

        // } DE_43_STR;

        // DE_43_STR DE_43 (dummyLen) <name="043 - Card Acceptor Name/Location");

        return "043 - Card Acceptor Name/Location";

      case 44:
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_44", dummyLen, "044 - Additional Response Data");
        return "044 - Additional Response Data";

      case 45: // Not Used by MC
        addIsoField("DE_45", 3, "045 - Track 1 Data");
        return "045 - Track 1 Data";

      case 46: // Not Used by MC
        addIsoField("DE_46", 3, "046 - Amounts, Fees");
        return "046 - Amounts, Fees";

      case 47: // Not Used by MC
        addIsoField("DE_47", 3, "047 - Additional Data - National");
        return "047 - Additional Data - National";

      case 48:
        dummyLen = decodeLLxVARElem(999, 3);
        // //typedef //struct (int dummyLen){
        decodePDS(FTell() + dummyLen);
        // } PDSType;

        // PDSType PDS(FTell() + dummyLen) <name="048 - Additional Data Private");
        return "048 - Additional Data - Private";

      case 49:
        addIsoField("DE_49", 3, "049 - Currency Code, Txn");
        return "049 - Currency Code, Txn";

      case 50:
        addIsoField("DE_50", 3, "050 - Currency Code, Reconciliation");
        return "050 - Currency Code, Reconciliation";

      case 51:
        addIsoField("DE_51", 3, "051 - Currency Code, Cardholder Billing");
        return "051 - Currency Code, Cardholder Billing";

      case 52:// Not Used by MC
        addIsoField("DE_52", 3, "052 - Personal Id Number (PIN) Data");
        return "052 - Personal Id Number (PIN) Data";

      case 53:// Not Used by MC
        addIsoField("DE_53", 3, "053 - Security Related Control Information");
        return "053 - Security Related Control Information";

      case 54:
        dummyLen = decodeLLxVARElem(100, 3);
        // typedef struct {
        // addIsoField("DE_54_01", 2, "01 - Additional Amount, Account Type");
        // addIsoField("DE_54_02", 2, "02 - Additional Amount, Amount Type");
        // addIsoField("DE_54_03", 3, "03 - Additional Amount, Currency Code");
        // addIsoField("DE_54_04", 1, "04 - Additional Amount, Amount Sign");
        // addIsoField("DE_54_05", 12, "05- Additional Amount, Amount");
        // } DE_54_STR;

        // //struct (int dummyLen) {
        while (dummyLen >= 20) {
          addIsoField("DE_54_01", 2, "01 - Additional Amount, Account Type");
          addIsoField("DE_54_02", 2, "02 - Additional Amount, Amount Type");
          addIsoField("DE_54_03", 3, "03 - Additional Amount, Currency Code");
          addIsoField("DE_54_04", 1, "04 - Additional Amount, Amount Sign");
          addIsoField("DE_54_05", 12, "05- Additional Amount, Amount");

          // DE_54_STR DE_54 <name="Amounts, Additional");
          dummyLen -= 20;
        }
        // } DE_54 (dummyLen) <name="054 - Amounts, Additional");

        return "054 - Amounts, Additional";

      case 55:
        dummyLen = decodeLLxVARElem(255, 3);
        iccLen = dummyLen;
        int dL = dummyLen;
        // //typedef struct(int dL){
        int ICCLen;
        // int[] code[2];
        while (dL > 0 && ReadUByte() != 0x00) {
          ICCLen = ProcessICCCode(dL);
          dL -= ICCLen;
        }
        if (dL > 0) {
          addIsoField("DE_55", dL, "055 - IC Card System Related Data (ICC)");
          // UBYTE padding", dL] <open = suppress, name = "Padding");
          // Printf("Has padded value\n");
        }
        // //(dL ==0) Printf("No Padding\n");
        // }ICC;

        // ICC iccCode(dummyLen) <name="055 - IC Card System Related Data (ICC)");
        // addIsoField("DE_55", dummyLen, "055 - IC Card System Related Data (ICC)");
        return "055 - IC Card System Related Data (ICC)";

      case 56: // Not Used by MC
        addIsoField("DE_56", 3, "056 - Original Data Elements");
        return "056 - Original Data Elements";

      case 57:// Not Used by MC
        addIsoField("DE_57", 3, "057 - Authorization Life Cycle Code");
        return "057 - Authorization Life Cycle Code";

      case 58:// Not Used by MC
        addIsoField("DE_58", 3, "058 - Authorizing Agent Inst Id Code");
        return "058 - Authorizing Agent Inst Id Code";

      case 59:// Not Used by MC
        addIsoField("DE_59", 3, "059 - Transport Data");
        return "059 - Transport Data";

      case 60:// Not Used by MC
        addIsoField("DE_60", 3, "060 - Reserved for National use");
        return "060 - Reserved for National use";

      case 61:// Not Used by MC
        addIsoField("DE_61", 3, "061 - Reserved for National use");
        return "061 - Reserved for National use";

      case 62:
        dummyLen = decodeLLxVARElem(999, 3);

        // //typedef //struct (int dummyLen){
        decodePDS(FTell() + dummyLen);
        // } PDSType2;

        // PDSType2 PDS(FTell() + dummyLen) <name="062 - Additional Data 2");
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_62", dummyLen, "062 - Additional Data 2");
        return "062 - Additional Data 2";

      case 63:
        dummyLen = decodeLLxVARElem(999, 3);

        // //typedef //struct {
        addIsoField("DE_63_01", 1, "01 - Life Cycle Support Indicator");
        addIsoField("DE_63_02", 15, "02 - Trace ID");
        // } DE_63_STR;

        // DE_63_STR DE_63 <name="063 - Transaction Life Cycle ID");

        return "063 - Transaction Life Cycle ID";

      case 64:// Not Used by MC
        addIsoField("DE_64", 3, "064 - Message Authentication Code Field");
        return "064 - Message Authentication Code Field";

      case 65:// Not Used by MC
        addIsoField("DE_65", 3, "065 - Reserved for ISO use");
        return "065 - Reserved for ISO use";

      case 66:// Not Used by MC
        addIsoField("DE_66", 3, "066 - Amounts, Original Fees");
        return "066 - Amounts, Original Fees";

      case 67:// Not Used by MC
        addIsoField("DE_67", 3, "067 - Extended Payment Data");
        return "067 - Extended Payment Data";

      case 68:// Not Used by MC
        addIsoField("DE_68", 3, "068 - Country Code, Receiving Inst");
        return "068 - Country Code, Receiving Inst";

      case 69:// Not Used by MC
        addIsoField("DE_69", 3, "069 - Country Code, Settlement Inst");
        return "069 - Country Code, Settlement Inst";

      case 70:// Not Used by MC
        addIsoField("DE_70", 3, "070 - Country Code, Authorizing Agent Inst");
        return "070 - Country Code, Authorizing Agent Inst";

      case 71:
        addIsoField("DE_71", 8, "071 - Message Number");
        return "071 - Message Number";

      case 72:
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_72", dummyLen, "072 - Data Record");
        return "072 - Data Record";

      case 73:
        addIsoField("DE_73", 6, "073 - Date, Action");
        return "073 - Date, Action";

      case 74:// Not Used by MC
        addIsoField("DE_74", 3, "074 - Credits, Number");
        return "074 - Credits, Number";

      case 75:// Not Used by MC
        addIsoField("DE_75", 3, "075 - Credits, Reversal Number");
        return "075 - Credits, Reversal Number";

      case 76:// Not Used by MC
        addIsoField("DE_76", 3, "076 - Debits, Number");
        return "076 - Debits, Number";

      case 77:// Not Used by MC
        addIsoField("DE_77", 3, "077 - Debits, Reversal Number");
        return "077 - Debits, Reversal Number";

      case 78:// Not Used by MC
        addIsoField("DE_78", 3, "078 - Transfer, Number");
        return "078 - Transfer, Number";

      case 79:// Not Used by MC
        addIsoField("DE_79", 3, "079 - Transfer, Reversal Number");
        return "079 - Transfer, Reversal Number";

      case 80:// Not Used by MC
        addIsoField("DE_80", 3, "080 - Inquiries, Number");
        return "080 - Inquiries, Number";

      case 81:// Not Used by MC
        addIsoField("DE_81", 3, "081 - Authorizations, Number");
        return "081 - Authorizations, Number";

      case 82:// Not Used by MC
        addIsoField("DE_82", 3, "082 - Inquiries, Reversal Number");
        return "082 - Inquiries, Reversal Number";

      case 83:// Not Used by MC
        addIsoField("DE_83", 3, "083 - Payments, Number");
        return "083 - Payments, Number";

      case 84:// Not Used by MC
        addIsoField("DE_84", 3, "084 - Payments, Reversal Number");
        return "084 - Payments, Reversal Number";

      case 85:// Not Used by MC
        addIsoField("DE_85", 3, "085 - Fee Collections, Number");
        return "085 - Fee Collections, Number";

      case 86:// Not Used by MC
        addIsoField("DE_86", 3, "086 - Credits, Amount");
        return "086 - Credits, Amount";

      case 87:// Not Used by MC
        addIsoField("DE_87", 3, "087 - Credits, Reversal Amount");
        return "087 - Credits, Reversal Amount";

      case 88:// Not Used by MC
        addIsoField("DE_88", 3, "088 - Debits, Amount");
        return "088 - Debits, Amount";

      case 89:// Not Used by MC
        addIsoField("DE_89", 3, "089 - Debits, Reversal Amount");
        return "089 - Debits, Reversal Amount";

      case 90:// Not Used by MC
        addIsoField("DE_90", 3, "090 - Authorizations, Reversal Number");
        return "090 - Authorizations, Reversal Number";

      case 91:// Not Used by MC
        addIsoField("DE_91", 3, "091 - Country Code, Txn Destination Inst");
        return "091 - Country Code, Txn Destination Inst";

      case 92:// Not Used by MC
        addIsoField("DE_92", 3, "092 - Country Code, Txn Originator Inst");
        return "092 - Country Code, Txn Originator Inst";

      case 93:
        dummyLen = decodeLLxVARElem(11, 2);
        addIsoField("DE_93", dummyLen, "093 - Txn Destination Inst Id Code");
        return "093 - Txn Destination Inst Id Code";

      case 94: //
        dummyLen = decodeLLxVARElem(11, 2);
        addIsoField("DE_94", dummyLen, "094 - Txn Originator Inst Id Code");
        return "094 - Txn Originator Inst Id Code";

      case 95:
        dummyLen = decodeLLxVARElem(10, 2);
        addIsoField("DE_95", dummyLen, "095 - Card Issuer Reference Data");
        return "095 - Card Issuer Reference Data";

      case 96:// Not Used by MC
        addIsoField("DE_96", 3, "096 - Key Management Data");
        return "096 - Key Management Data";

      case 97:// Not Used by MC
        addIsoField("DE_97", 3, "097 - Amount, Net Reconciliation");
        return "097 - Amount, Net Reconciliation";

      case 98:// Not Used by MC
        addIsoField("DE_98", 3, "098 - Payee");
        return "098 - Payee";

      case 99:// Not Used by MC
        addIsoField("DE_99", 3, "099 - Settlement Inst Id Code");
        return "099 - Settlement Inst Id Code";

      case 100:
        dummyLen = decodeLLxVARElem(11, 2);
        addIsoField("DE_100", dummyLen, "100 - Receiving Inst Id Code");
        return "100 - Receiving Inst Id Code";

      case 101:// Not Used by MC
        addIsoField("DE_101", 3, "101 - File Name");
        return "101 - File Name";

      case 102:// Not Used by MC
        addIsoField("DE_102", 3, "102 - Account Id 1");
        return "102 - Account Id 1";

      case 103:// Not Used by MC
        addIsoField("DE_103", 3, "103 - Account Id 2");
        return "103 - Account Id 2";

      case 104:// Not Used by MC
        addIsoField("DE_104", 3, "104 - Txn Description");
        return "104 - Txn Description";

      case 105:// Not Used by MC
        addIsoField("DE_105", 3, "105 - Credits, Chargeback Amount");
        return "105 - Credits, Chargeback Amount";

      case 106:// Not Used by MC
        addIsoField("DE_106", 3, "106 - Debits, Chargeback Amount");
        return "106 - Debits, Chargeback Amount";

      case 107:// Not Used by MC
        addIsoField("DE_107", 3, "107 - Credits, Chargeback Number");
        return "107 - Credits, Chargeback Number";

      case 108:// Not Used by MC
        addIsoField("DE_108", 3, "108 - Debits, Chargeback Number");
        return "108 - Debits, Chargeback Number";

      case 109:// Not Used by MC
        addIsoField("DE_109", 3, "109 - Credits, Fee Amounts");
        return "109 - Credits, Fee Amounts";

      case 110:// Not Used by MC
        addIsoField("DE_110", 3, "110 - Debits, Fee Amounts");
        return "110 - Debits, Fee Amounts";

      case 111:
        dummyLen = decodeLLxVARElem(12, 3);
        addIsoField("DE_111", dummyLen, "111 - Ammount Currency Conversion Assessment");
        return "111 - Ammount Currency Conversion Assessment";

      case 112:// Not Used by MC
        addIsoField("DE_112", 3, "112 - Reserved for ISO use");
        return "112 - Reserved for ISO use";

      case 113:// Not Used by MC
        addIsoField("DE_113", 3, "113 - Reserved for ISO use");
        return "113 - Reserved for ISO use";

      case 114:// Not Used by MC
        addIsoField("DE_114", 3, "114 - Reserved for ISO use");
        return "114 - Reserved for ISO use";

      case 115:// Not Used by MC
        addIsoField("DE_115", 3, "115 - Reserved for ISO use");
        return "115 - Reserved for ISO use";

      case 116:// Not Used by MC
        addIsoField("DE_116", 3, "116 - Reserved for National use");
        return "116 - Reserved for National use";

      case 117:// Not Used by MC
        addIsoField("DE_117", 3, "117 - Reserved for National use");
        return "117 - Reserved for National use";

      case 118:// Not Used by MC
        addIsoField("DE_118", 3, "118 - Reserved for National use");
        return "118 - Reserved for National use";

      case 119:// Not Used by MC
        addIsoField("DE_119", 3, "119 - Reserved for National use");
        return "119 - Reserved for National use";

      case 120:// Not Used by MC
        addIsoField("DE_120", 3, "120 - Reserved for National use");
        return "120 - Reserved for National use";

      case 121:// Not Used by MC
        addIsoField("DE_121", 3, "121 - Reserved for National use");
        return "121 - Reserved for National use";

      case 122:// Not Used by MC
        addIsoField("DE_122", 3, "122 - Reserved for National use");
        return "122 - Reserved for National use";

      case 123:
        dummyLen = decodeLLxVARElem(999, 3);

        // //typedef //struct (int dummyLen){
        decodePDS(FTell() + dummyLen);
        // } PDSType3;

        // PDSType3 PDS(FTell() + dummyLen) <name="123 - Additional Data 3");
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_123", dummyLen, "123 - Additional Data 3");
        return "123 - Additional Data 3";

      case 124:
        dummyLen = decodeLLxVARElem(999, 3);

        // //typedef //struct (int dummyLen){
        decodePDS(FTell() + dummyLen);
        // } PDSType4;

        // PDSType4 PDS(FTell() + dummyLen) <name="124 - Additional Data 4");
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_124", dummyLen, "124 - Additional Data 4");
        // return "124 - Additional Data 4";

      case 125:
        dummyLen = decodeLLxVARElem(999, 3);

        // //typedef //struct (int dummyLen){
        decodePDS(FTell() + dummyLen);
        // } PDSType5;

        // PDSType5 PDS(FTell() + dummyLen) <name="125 - Additional Data 5");
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_125", dummyLen, "125 - Additional Data 5");
        return "125 - Additional Data 5";

      case 126:// Not Used by MC
        addIsoField("DE_126", 3, "126 - Reserved for Private use");
        return "126 - Reserved for Private use";

      case 127:
        dummyLen = decodeLLxVARElem(999, 3);
        addIsoField("DE_127", dummyLen, "127 - Network Data");
        return "127 - Network Data";

      case 128:// Not Used by MC
        addIsoField("DE_128", 3, "128 - Message Authentication Code Fi");
        return "128 - Message Authentication Code Fi";

    }

    return "haha";
  }

  void decodePDS(long TagetPos) {

    int PDSNo;
    int dummyLen;

    while (FTell() < TagetPos) {
      // LogManager.getLogger().debug("Start PDS < " + String.valueOf(TagetPos) + "
      // pos: " + String.valueOf(FTell()));
      // System.out.println("Start PDS < " + String.valueOf(TagetPos) + " pos: " +
      // String.valueOf(FTell()));
      PDSNo = decodeLLxVARElem(9999, 4);
      dummyLen = decodeLLxVARElem(999, 3);
      lastDummyLength = 0;

      switch (PDSNo) {

        case 1:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0001_01", 2, "01 - Account Number Type");
          addIsoField("PDS_0001_02", dummyLen - 2, "02 - Account Number");
          // } //PDS_0001_STR;

          //// PDS_0001_STR //PDS_0001 (dummyLen) <name="0001 - Mapping Service Account
          //// Number");
          break;

        case 2:
          addIsoField("PDS_0002", dummyLen, "0002 - GCMS Product Identifier");
          break; //
        case 3:
          addIsoField("PDS_0003", dummyLen, "0003 - Licensed Product Identifier");
          break; //
        case 5:

          // typedef //struct {
          // addIsoField("PDS_0005_01", 5, "01 - Data Element ID");
          // addIsoField("PDS_0005_02", 2, "02 - Error Severity Code");
          // addIsoField("PDS_0005_03", 4, "03 - Error Message Code");
          // addIsoField("PDS_0005_04", 3, "04 - Subfield ID");
          // } //PDS_0005_STR;

          // typedef //struct (int dummyLen) {
          while (dummyLen >= 14) {
            addIsoField("PDS_0005_01", 5, "01 - Data Element ID");
            addIsoField("PDS_0005_02", 2, "02 - Error Severity Code");
            addIsoField("PDS_0005_03", 4, "03 - Error Message Code");
            addIsoField("PDS_0005_04", 3, "04 - Subfield ID");
            //// PDS_0005_STR //PDS_0005 <name="Message Error Indicator");
            dummyLen -= 14;
          }
          // } //PDS_0005_TOP_STR;

          //// PDS_0005_TOP_STR //PDS_0005_TOP (dummyLen) <name="0005 - Message Error
          //// Indicator");

          break;

        case 6:

          // typedef //struct {
          addIsoField("PDS_0006_01", 3, "01 - Card Program Identifier");
          addIsoField("PDS_0006_02", 1, "02 - Business Service Arrangement Type Code");
          addIsoField("PDS_0006_03", 6, "03 - Business Service ID Code");
          // } //PDS_0006_STR;

          //// PDS_0006_STR //PDS_0006 <name="0006 - Business Service Arrangement");
          break;

        case 23:
          addIsoField("PDS_0023", dummyLen, "0023 - Type");
          break; //
        case 25:

          // typedef struct(int dummyLen){
          addIsoField("PDS_0025_01", 1, "01 - Message Reversal Indicator");
          if (dummyLen > 1) {
            addIsoField("PDS_0025_02", 6, "02 - Central Site Processing Date of Original Message");
          }
          // } //PDS_0025_STR;

          // PDS_0025_STR //PDS_0025(dummyLen) <name="0025 - Message Reversal Indicator");
          break;

        case 26:

          // typedef //struct {
          addIsoField("PDS_0026_01", 1, "01 - File Reversal Indicator");
          if (dummyLen > 1) {
            addIsoField("PDS_0026_02", 6, "02 - Central Site Processing Date of Original Message");
          }
          // } //PDS_0026_STR;

          // PDS_0026_STR //PDS_0026 <name="0026 - File Reversal Indicator");
          break;

        case 42:
          addIsoField("PDS_0042", dummyLen, "0042 - Merchant Capability");
          break;
        case 43:
          addIsoField("PDS_0043", dummyLen, "0043 - Program Registration ID");
          break;

        case 44:

          // typedef //struct {
          addIsoField("PDS_0044_01", 1, "01 - CVC 2 Validation Program Indicator");
          addIsoField("PDS_0044_02", 1, "02 - QPS/PayPass Chargeback Eligibility Indicator");
          /*
           * addIsoField("PDS_0044_03", 1, "03 - Reserved for future use");
           * addIsoField("PDS_0044_04", 1, "04 - Reserved for future use");
           * addIsoField("PDS_0044_05", 1, "05 - Reserved for future use");
           * addIsoField("PDS_0044_06", 1, "06 - Reserved for future use");
           * addIsoField("PDS_0044_07", 1, "07 - Reserved for future use");
           * addIsoField("PDS_0044_08", 1, "08 - Reserved for future use");
           * addIsoField("PDS_0044_09", 1, "09 - Reserved for future use");
           * addIsoField("PDS_0044_10", 1, "10 - Reserved for future use");
           * addIsoField("PDS_0044_11", 1, "11 - Reserved for future use");
           * addIsoField("PDS_0044_12", 1, "12 - Reserved for future use");
           * addIsoField("PDS_0044_13", 1, "13 - Reserved for future use");
           * addIsoField("PDS_0044_14", 1, "14 - Reserved for future user");
           * addIsoField("PDS_0044_15", 1, "15 - Reserved for future use");
           * addIsoField("PDS_0044_16", 1, "16 - Reserved for future use");
           * addIsoField("PDS_0044_17", 1, "17 - Reserved for future use");
           * addIsoField("PDS_0044_18", 1, "18 - Reserved for future use");
           * addIsoField("PDS_0044_19", 1, "19 - Reserved for future use");
           * addIsoField("PDS_0044_20", 1, "20 - Reserved for future use");
           */
          // } //PDS_0044_STR;

          // PDS_0044_STR //PDS_0044 <name="0044 - Program Participation Indicator");

          break;

        case 52:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0052_01", 1, "01 - Security Protocol");
          if (dummyLen > 1) {
            addIsoField("PDS_0052_02", 1, "02 - Cardholder Authentication");
            if (dummyLen > 2) {
              addIsoField("PDS_0052_03", 1, "03 - UCAF Collection Indicator");
            }
          }
          // } //PDS_0052_STR;

          // PDS_0052_STR //PDS_0052 (dummyLen) <name="0052 - Electronic Commerce Security
          // Level Indicator");

          break;

        case 56:
          addIsoField("PDS_0056", dummyLen, "0056 - MasterCard Electronic Card Indicator");
          break;
        case 57:
          addIsoField("PDS_0057", dummyLen, "0057 - Transaction Category Indicator");
          break;

        case 71:

          // typedef //struct {
          addIsoField("PDS_0071_01", 2, "01 - On-behalf (OB) Service");
          addIsoField("PDS_0071_02", 1, "02 - On-behalf (OB) Result 1");
          addIsoField("PDS_0071_03", 1, "03 - On-behalf (OB) Result 2");
          // } //PDS_0071_STR;

          // PDS_0071_STR //PDS_0071 <name="0071 - Chip to Magnetic Stripe Conversion
          // Service Indicator");

          break;

        case 80:

          // typedef //struct {
          // addIsoField("PDS_0080_01", 3, "01 - Rate Type Code");
          // addIsoField("PDS_0080_02", 12, "02 - Amount, Value Added Tax");
          // addIsoField("PDS_0080_03", 3, "03 - Currency Code");
          // addIsoField("PDS_0080_04", 1, "04 - Currency Exponent");
          // addIsoField("PDS_0080_05", 1, "05 - Debit/Credit Indicator");
          // } //PDS_0080_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 20) {
            addIsoField("PDS_0080_01", 3, "01 - Rate Type Code");
            addIsoField("PDS_0080_02", 12, "02 - Amount, Value Added Tax");
            addIsoField("PDS_0080_03", 3, "03 - Currency Code");
            addIsoField("PDS_0080_04", 1, "04 - Currency Exponent");
            addIsoField("PDS_0080_05", 1, "05 - Debit/Credit Indicator");
            // PDS_0080_STR //PDS_0080 <name="Amount, Tax");
            dummyLen -= 20;
          }
          // } //PDS_0005 (dummyLen) <name="0080 - Amount, Tax");

          break;

        case 105:

          // typedef //struct {
          addIsoField("PDS_0105_01", 3, "01 - File Type");
          addIsoField("PDS_0105_02", 6, "02 - File Reference Date");
          addIsoField("PDS_0105_03", 11, "03 - Processor ID");
          addIsoField("PDS_0105_04", 5, "04 - File Sequence Number");
          // } //PDS_0105_STR;

          // PDS_0105_STR //PDS_0105 <name="0105 - File ID");

          break;

        case 110:

          // typedef //struct {
          addIsoField("PDS_0110_01", 3, "01 - Transmission Type");
          addIsoField("PDS_0110_02", 6, "02 - Transmission Reference Date");
          addIsoField("PDS_0110_03", 11, "03 - Processor ID");
          addIsoField("PDS_0110_04", 5, "04 - Transmission Sequence Number");
          // } //PDS_0110_STR;

          // PDS_0110_STR //PDS_0110 <name="0110 - Transmission ID");

          break;

        case 122:
          addIsoField("PDS_0122", dummyLen, "0122 - Processing Mode");
          break;
        case 137:
          addIsoField("PDS_0137", dummyLen, "0137 - Fee Collection Control");
          break;
        case 138:
          addIsoField("PDS_0138", dummyLen, "0138 - Source Message Number ID");
          break;

        case 140:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0140_01", 12, "01 - Amount, Cardholder Billing USD");
          if (dummyLen >= 12) {
            addIsoField("PDS_0140_02", 12, "02 - Amount, Currency Conversion Assessment USD");
          }
          // } //PDS_0140_STR;

          // PDS_0140_STR //PDS_0140 (dummyLen) <name="0140 - Amount, Cardholder Billing
          // Amounts USD");

          break;

        case 145:

          // typedef //struct {
          addIsoField("PDS_0145_01", 3, "01 - Currency Code, Fee");
          addIsoField("PDS_0145_02", 12, "02 - Amount, Fee");
          // } //PDS_0145_STR;

          // PDS_0145_STR //PDS_0145 <name="0145 - Amount, Alternate Transaction Fee");

          break;

        case 146:

          // typedef //struct {
          // addIsoField("PDS_0146_01", 2, "01 - Fee Type Code");
          // addIsoField("PDS_0146_02", 2, "02 - Fee Processing Code");
          // addIsoField("PDS_0146_03", 2, "03 - Fee Settlement Indicator");
          // addIsoField("PDS_0146_04", 3, "04 - Currency Code, Fee");
          // addIsoField("PDS_0146_05", 12, "05 - Amount, Fee");
          // addIsoField("PDS_0146_06", 3, "06 - Currency Code, Fee, Reconciliation");
          // addIsoField("PDS_0146_07", 12, "07 - Amount, Fee, Reconciliation");
          // } //PDS_0146_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 36) {
            addIsoField("PDS_0146_01", 2, "01 - Fee Type Code");
            addIsoField("PDS_0146_02", 2, "02 - Fee Processing Code");
            addIsoField("PDS_0146_03", 2, "03 - Fee Settlement Indicator");
            addIsoField("PDS_0146_04", 3, "04 - Currency Code, Fee");
            addIsoField("PDS_0146_05", 12, "05 - Amount, Fee");
            addIsoField("PDS_0146_06", 3, "06 - Currency Code, Fee, Reconciliation");
            addIsoField("PDS_0146_07", 12, "07 - Amount, Fee, Reconciliation");
            // PDS_0146_STR //PDS_0146 <name="Amounts, Transaction Fee");
            dummyLen -= 36;
          }
          // } //PDS_0146 (dummyLen) <name="0146 - Amounts, Transaction Fee");

          break;

        case 147:

          // typedef //struct {
          // addIsoField("PDS_0147_01", 2, "01 - Fee Type Code");
          // addIsoField("PDS_0147_02", 2, "02 - Fee Processing Code");
          // addIsoField("PDS_0147_03", 2, "03 - Fee Settlement Indicator");
          // addIsoField("PDS_0147_04", 3, "04 - Currency Code, Fee");
          // addIsoField("PDS_0147_05", 18, "05 - Interchange Amount, Fee");
          // addIsoField("PDS_0147_06", 3, "06 - Currency Code, Fee, Reconciliation");
          // addIsoField("PDS_0147_07", 18, "07 - Interchange Amount, Fee,
          // Reconciliation");
          // } //PDS_0147_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 48) {
            addIsoField("PDS_0147_01", 2, "01 - Fee Type Code");
            addIsoField("PDS_0147_02", 2, "02 - Fee Processing Code");
            addIsoField("PDS_0147_03", 2, "03 - Fee Settlement Indicator");
            addIsoField("PDS_0147_04", 3, "04 - Currency Code, Fee");
            addIsoField("PDS_0147_05", 18, "05 - Interchange Amount, Fee");
            addIsoField("PDS_0147_06", 3, "06 - Currency Code, Fee, Reconciliation");
            addIsoField("PDS_0147_07", 18, "07 - Interchange Amount, Fee, Reconciliation");
            // PDS_0147_STR //PDS_0147 <name="Extended Precision Amounts");
            dummyLen -= 48;
          }
          // } //PDS_0147 (dummyLen) <name="0147 - Extended Precision Amounts");

          break;

        case 148: //

          // typedef //struct {
          // addIsoField("PDS_0148_01", 3, "01 - Currency Code");
          // addIsoField("PDS_0148_02", 1, "02 - Currency Exponent");
          // } //PDS_0148_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 4) {
            addIsoField("PDS_0148_01", 3, "01 - Currency Code");
            addIsoField("PDS_0148_02", 1, "02 - Currency Exponent");
            // PDS_0148_STR //PDS_0148 <name="Currency Exponents");
            dummyLen -= 4;
          }
          // } //PDS_0148 (dummyLen) <name="0148 - Currency Exponents");

          break;

        case 149:

          // typedef //struct {
          addIsoField("PDS_0149_01", 3, "01 - Currency Code, Original Transaction Amount");
          addIsoField("PDS_0149_02", 3, "02 - Currency Code, Original Reconciliation Amount");
          // } //PDS_0149_STR;

          // PDS_0149_STR //PDS_0149 <name="0149 - Currency Codes, Amounts, Original");

          break;

        case 157:
          addIsoField("PDS_0157", dummyLen, "0157 - Alternate Processor Indicator");
          break;

        case 158: //

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0158_01", 3, "01 - Card Program Identifier");
          if (dummyLen > 3) {
            addIsoField("PDS_0158_02", 1, "02 - Business Service Arrangement Type Code");
            if (dummyLen > 4) {
              addIsoField("PDS_0158_03", 6, "03 - Business Service ID Code");
              if (dummyLen > 10) {
                addIsoField("PDS_0158_04", 2, "04 - Interchange Rate Designator");
                if (dummyLen > 12) {
                  addIsoField("PDS_0158_05", 6, "05 - Central Site Business Date");
                  if (dummyLen > 18) {
                    addIsoField("PDS_0158_06", 2, "06 - Business Cycle");
                    if (dummyLen > 20) {
                      addIsoField("PDS_0158_07", 1, "07 - Card Acceptor Classification Override Indicator");
                      if (dummyLen > 21) {
                        addIsoField("PDS_0158_08", 3, "08 - Product Class Override Indicator");
                        if (dummyLen > 24) {
                          addIsoField("PDS_0158_09", 1, "09 - Corporate Incentive Rates Apply Indicator");
                          if (dummyLen > 25) {
                            addIsoField("PDS_0158_10", 1, "10 - Special Conditions Indicator");
                            if (dummyLen > 26) {
                              addIsoField("PDS_0158_11", 1, "11 - MasterCard Assigned ID Override Indicator");
                              if (dummyLen > 27) {
                                addIsoField("PDS_0158_12", 1, "12 - Account Level Management Account Category Code");
                                if (dummyLen > 28) {
                                  addIsoField("PDS_0158_13", 1, "13 - Rate Indicator");
                                  if (dummyLen > 29) {
                                    addIsoField("PDS_0158_14", 1, "14 - MasterPass Incentive Indicator");
                                    if (dummyLen > 30) {
                                      addIsoField("PDS_0158_15", 1, "15 - XXXXX Indicator");
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0158_STR;

          // PDS_0158_STR //PDS_0158 (dummyLen) <name="0158 - Business Activity");

          break;

        case 159: //

          // typedef //struct {
          addIsoField("PDS_0159_01", 11, "01 - Settlement Service Transfer Agent ID Code");
          addIsoField("PDS_0159_02", 28, "02 - Settlement Service Transfer Agent Account");
          addIsoField("PDS_0159_03", 1, "03 - Settlement Service Level Code");
          addIsoField("PDS_0159_04", 10, "04 - Settlement Service ID Code");
          addIsoField("PDS_0159_05", 1, "05 - Settlement Foreign Exchange Rate Class Code");
          addIsoField("PDS_0159_06", 6, "06 - Reconciliation Date");
          addIsoField("PDS_0159_07", 2, "07 - Reconciliation Cycle");
          addIsoField("PDS_0159_08", 6, "08 - Settlement Date");
          addIsoField("PDS_0159_09", 2, "09 - Settlement Cycle");
          // } //PDS_0159_STR;

          // PDS_0159_STR //PDS_0159 <name="0159 - Settlement Data");

          break;

        case 160:

          // typedef //struct {
          // addIsoField("PDS_0160_01", 6, "01 - Settlement Date");
          // addIsoField("PDS_0160_02", 12, "02 - Settlement Amount");
          // } //PDS_0160_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 18) {
            addIsoField("PDS_0160_01", 6, "01 - Settlement Date");
            addIsoField("PDS_0160_02", 12, "02 - Settlement Amount");
            // PDS_0160_STR //PDS_0160 <name="Settlement Data, Multiple");
            dummyLen -= 18;
          }
          // } //PDS_0160 (dummyLen) <name="0160 - Settlement Data, Multiple");

          break;

        case 164:

          // typedef //struct {
          // addIsoField("PDS_0164_01", 3, "01 - Currency Code");
          // addIsoField("PDS_0164_02", 11, "02 - Currency Conversion Rate");
          // addIsoField("PDS_0164_03", 1, "03 - Currency Conversion Type");
          // addIsoField("PDS_0164_04", 6, "04 - Business Date");
          // addIsoField("PDS_0164_05", 2, "05 - Delivery Cycle");
          // } //PDS_0164_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 23) {
            addIsoField("PDS_0164_01", 3, "01 - Currency Code");
            addIsoField("PDS_0164_02", 11, "02 - Currency Conversion Rate");
            addIsoField("PDS_0164_03", 1, "03 - Currency Conversion Type");
            addIsoField("PDS_0164_04", 6, "04 - Business Date");
            addIsoField("PDS_0164_05", 2, "05 - Delivery Cycle");
            // PDS_0164_STR //PDS_0164 <name="Currency Cross Rates");
            dummyLen -= 23;
          }
          // } //PDS_0164 (dummyLen) <name="0164 - Currency Cross Rates");

          break;

        case 165:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0165_01", 1, "01 - Settlement Indicator");
          if (dummyLen > 1) {
            addIsoField("PDS_0165_02", dummyLen - 1, "02 - Settlement Agreement Information");
          }
          // } //PDS_0165_STR;

          // PDS_0165_STR //PDS_0165 (dummyLen) <name="0165 - Settlement Indicator");
          break;

        case 170:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0170_01", 16, "01 - Customer Service Phone Number");
          if (dummyLen > 16) {
            addIsoField("PDS_0170_02", 16, "02 - Card Acceptor Phone Number");
            if (dummyLen > 32) {
              addIsoField("PDS_0170_03", dummyLen - 32, "03 - Additional Contact Information");
            }
          }
          // } //PDS_0170_STR;

          // PDS_0170_STR //PDS_0170 (dummyLen) <name="0170 - Card Acceptor Inquiry
          // Information");
          break;

        case 171:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0171_01", 3, "01 - Character Set Indicator");
          addIsoField("PDS_0171_02", dummyLen - 3, "02 - Card Acceptor Description Data");
          // } //PDS_0171_STR;

          // PDS_0171_STR //PDS_0171 (dummyLen) <name="0171 - Alternate Card Acceptor
          // Description Data");

          break;

        case 172:
          addIsoField("PDS_0172", dummyLen, "0172 - Sole Proprietor Name");
          break;
        case 173:
          addIsoField("PDS_0173", dummyLen, "0173 - Legal Corporate Name");
          break;
        case 174:
          addIsoField("PDS_0174", dummyLen, "0174 - Dun & Bradstreet Number");
          break;
        case 175:
          addIsoField("PDS_0175", dummyLen, "0175 - Card Acceptor URL");
          break;
        case 176:
          addIsoField("PDS_0176", dummyLen, "0176 - MasterCard Assigned ID");
          break;

        case 177:

          // typedef //struct {
          addIsoField("PDS_0177_01", 1, "01 - Cross-border Indicator");
          addIsoField("PDS_0177_02", 1, "02 - Currency Indicator");
          // } //PDS_0177_STR;

          // PDS_0177_STR //PDS_0177 <name="0177 - Cross-border");
          break;

        case 178:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0178_01", 3, "01 - Character Set Indicator 2");
          addIsoField("PDS_0178_02", dummyLen - 3, "02 - Card Acceptor Description Data 2");
          // } //PDS_0178_STR;

          // PDS_0178_STR //PDS_0178 (dummyLen) <name="0178 - Alternate Card Acceptor
          // Description Data 2");
          break;

        case 179:
          addIsoField("PDS_0179", dummyLen, "0179 - Long Running Transaction (LRT) Indicator");
          break;
        case 180:
          addIsoField("PDS_0180", dummyLen, "0180 - Domestic Card Acceptor Tax ID");
          break;

        case 181:

          // typedef struct(int dummyLen) {
          addIsoField("PDS_0181_01", 2, "01 - Type of Installments");
          addIsoField("PDS_0181_02", 2, "02 - Number of Installments");
          addIsoField("PDS_0181_03", 5, "03 - Interest Rate");
          addIsoField("PDS_0181_04", 12, "04 - First Installment Amount");
          addIsoField("PDS_0181_05", 12, "05 - Subsequent Installment Amount");
          // BYTE filler", dummyLen - 33, "Filler");
          // } //PDS_0181_STR;

          // PDS_0181_STR //PDS_0181(dummyLen) <name="0181 - Installment Payment Data");
          break;

        case 188:
          addIsoField("PDS_0188", dummyLen, "0188 - Private Data—Proprietary Service Data");
          break;

        case 189:

          // typedef //struct {
          addIsoField("PDS_0189_01", 1, "01 - Format Number");
          addIsoField("PDS_0189_02", 40, "02 - Phone Data");
          // } //PDS_0189_STR;

          // PDS_0189_STR //PDS_0189 <name="0189 - Point-of-Interaction (POI) Phone
          // Data");

          break;

        case 190:
          addIsoField("PDS_0190", dummyLen, "0190 - Partner ID Code");
          break;
        case 191:
          addIsoField("PDS_0191", dummyLen, "0191 - Originating Message Format");
          break;
        case 192:
          addIsoField("PDS_0192", dummyLen, "0192 - Payment Transaction Initiator");
          break;
        case 194:
          addIsoField("PDS_0194", dummyLen, "0194 - Remote Payments Program Data");
          break;

        case 195:

          // typedef //struct { // might need a set of ifs
          addIsoField("PDS_0195_01", 3, "01 - Total Number of Installments");
          addIsoField("PDS_0195_02", 2, "02 - Installment Option");
          addIsoField("PDS_0195_03", 3, "03 - Installment Number");
          addIsoField("PDS_0195_04", 1, "04 - Bonus Code");
          addIsoField("PDS_0195_05", 1, "05 - Bonus Month Code");
          addIsoField("PDS_0195_06", 1, "06 - Number of Bonus Payments per Year");
          addIsoField("PDS_0195_07", 12, "07 - Bonus Amount");
          addIsoField("PDS_0195_08", 4, "08 - First Month of Bonus Payment");

          // } //PDS_0195_STR;

          // PDS_0195_STR //PDS_0195 <name="0195 - Installment Data");
          break;

        case 196:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0196_01", 17, "01 - Mobile Phone Number");
          addIsoField("PDS_0196_02", dummyLen - 17, "02 - Mobile Phone Service Provider Name");
          // } //PDS_0196_STR;

          // PDS_0196_STR //PDS_0196 (dummyLen) <name="0196 - Mobile Phone Reload Data");

          break;

        case 197:

          // typedef //struct {
          addIsoField("PDS_0197_01", 12, "01 - Tax Amount 1");
          addIsoField("PDS_0197_02", 12, "02 - Tax Amount 2");
          addIsoField("PDS_0197_03", 5, "03 - Tax Percentage");
          addIsoField("PDS_0197_04", 12, "04 - Tax Base Amount");
          addIsoField("PDS_0197_05", 12, "05 - Tax Amount 3");
          // } //PDS_0197_STR;

          // PDS_0197_STR //PDS_0197 <name="0197 - Mobile Phone Reload Data");
          break;

        case 198:
          addIsoField("PDS_0198", dummyLen, "0198 - Device Type");
          break;
        case 199:
          addIsoField("PDS_0199", dummyLen, "0199 - Funding Transaction Information");
          break;

        case 200:

          // typedef //struct {
          addIsoField("PDS_0200_01", 6, "01 - Fraud Notification Service Date");
          addIsoField("PDS_0200_02", 2, "02 - Fraud Notification Service Chargeback Counter");
          // } //PDS_0200_STR;

          // PDS_0200_STR //PDS_0200 <name="0200 - Fraud Notification Date");

          break;

        case 202:
          addIsoField("PDS_0202", dummyLen, "0202 - Primary Account Number (PAN) Syntax Error");
          break;
        case 204:
          addIsoField("PDS_0204", dummyLen, "0204 - Amount, Syntax Error");
          break;
        case 205:

          // typedef //struct {
          addIsoField("PDS_0205_01", 5, "01 - Data Element ID");
          addIsoField("PDS_0205_02", 2, "02 - Error Severity Code");
          addIsoField("PDS_0205_03", 4, "03 - Error Message Code");
          addIsoField("PDS_0205_04", 3, "04 - Subfield ID");
          // } //PDS_0205_STR;

          // PDS_0205_STR //PDS_0205 <name="0205 - Syntax Return, Message Error
          // Indicator");
          break;

        case 206:

          // typedef //struct {
          addIsoField("PDS_0206_01", 3, "01 - Number of Days Since Transaction Occurred");
          addIsoField("PDS_0206_02", 1, "02 - Late Presentment Tier");
          // } //PDS_0206_STR;

          // PDS_0206_STR //PDS_0206 <name="0206 - Late Presentment Indicator");

          break;

        case 207:
          addIsoField("PDS_0207", dummyLen, "0207 - Wallet Identifier");
          break;

        case 210:

          // typedef //struct {
          addIsoField("PDS_0210_01", 2, "01 - Transit Transaction Type Indicator");
          addIsoField("PDS_0210_02", 2, "02 - Transportation Mode Indicator");
          // } //PDS_0210_STR;

          // PDS_0210_STR //PDS_0210 <name="0210 - Transit Program");

          break;

        case 212:

          // typedef //struct {
          addIsoField("PDS_0212_01", 3, "01 - Service Level Indicator");
          addIsoField("PDS_0212_02", 2, "02 - Response Code");
          // } //PDS_0212_STR;

          // PDS_0212_STR //PDS_0212 <name="0212 - Merchant Data Services");

          break;

        case 213:

          if (0 < dummyLen) {
            addIsoField("PDS_0213", dummyLen, "0213 - Merchant Country of Origin");
          }

          // //typedef //struct (int dummyLen) {
          // int FieldLen;
          // int StartField = FTell();
          //
          // int FieldLen1;
          // int FieldLen2;
          // int FieldLen3;
          //
          // FieldLen1 = GetLenTillEndOfField ();
          // FSkip(FieldLen1+1);
          // FieldLen2 = GetLenTillEndOfField ();
          // FSkip(FieldLen2+1);
          // FieldLen3 = GetLenTillEndOfField ();
          // FSkip(FieldLen3+1);
          //
          // FSeek(StartField);
          // if(FieldLen1 + FieldLen2 + FieldLen3 + 35 <= dummyLen){
          // if (FieldLen1 > 0){
          // //BYTE //PDS_0213_01", FieldLen1, "01 - Card Acceptor Name");
          // }
          // FSkip (1);
          //
          // if (FieldLen2 > 0){
          // //BYTE //PDS_0213_02", FieldLen2, "02 - Card Acceptor Street Address");
          // }
          // FSkip (1);
          //
          // if (FieldLen3 > 0){
          // //BYTE //PDS_0213_03", FieldLen3, "03 - Card Acceptor City");
          // }
          // FSkip (1);
          //
          // //BYTE //PDS_0213_04", 10, "04 - Card Acceptor Postal (ZIP) Code");
          // //BYTE //PDS_0213_05", 3, "05 - Card Acceptor State, Province or Region
          // Code");
          // //BYTE //PDS_0213_06", 3, "06 - Card Acceptor Country Code");
          // //BYTE //PDS_0213_07", 16, "07 - Card Acceptor Phone Number");
          // }
          // else{
          // addIsoField("PDS_0213", dummyLen, "0213 - Unparsed Data");
          // }
          //
          //// } //PDS_0213_STR;
          //
          //// PDS_0213_STR //PDS_0213 <name="0213 - Original Merchant Data");

          break;

        case 214:

          // typedef //struct {
          addIsoField("PDS_0214_01", 90, "01 - Cleansed Legal Corporate Name");
          addIsoField("PDS_0214_02", 3, "02 - Sales Channel—Percentage Brick");
          addIsoField("PDS_0214_03", 3, "03 - Sales Channel—Percentage Online"); // PROBABLY *
          addIsoField("PDS_0214_04", 3, "04 - Sales Channel—Percentage Other");
          addIsoField("PDS_0214_05", 255, "05 - Cleansed Merchant URL");

          // } //PDS_0214_STR;

          // PDS_0214_STR //PDS_0214 <name="0214 - Merchant Data Cleansing Plus");
          break;

        case 215:

          // typedef //struct {
          addIsoField("PDS_0215_01", 5, "01 - Aggregate Merchant ID");
          addIsoField("PDS_0215_02", 75, "02 - Aggregate Merchant Name");
          addIsoField("PDS_0215_03", 3, "03 - Industry Code");
          addIsoField("PDS_0215_04", 3, "04 - Super Industry Code");
          addIsoField("PDS_0215_05", 5, "05 - Key Aggregate Merchant ID");
          addIsoField("PDS_0215_06", 1, "06 - Channel Distribution ID");
          addIsoField("PDS_0215_07", 6, "07 - NAICS Code");
          addIsoField("PDS_0215_08", 8, "08 - Parent Aggregate Merchant ID");
          // } //PDS_0215_STR;

          // PDS_0215_STR //PDS_0215 <name="0215 - Merchant Data Cleansing Plus");
          break;

        case 225:
          addIsoField("PDS_0225", dummyLen, "0225 - Syntax Return, Original Message Reason Code");
          break;
        case 228:
          addIsoField("PDS_0228", dummyLen, "0228 - Retrieval Document Code");
          break;
        case 230:
          addIsoField("PDS_0230", dummyLen, "0230 - Fulfillment Document Code");
          break;
        case 241:
          addIsoField("PDS_0241", dummyLen, "0241 - MasterCom Control Number");
          break;

        case 243:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0243_01", 6, "01 - MasterCom Issuer Retrieval Request Date");
          if (dummyLen > 6) {
            addIsoField("PDS_0243_02", 1, "02 - MasterCom Acquirer Retrieval Response Code");
            if (dummyLen > 7) {
              addIsoField("PDS_0243_03", 6, "03 - MasterCom Acquirer Retrieval Response Sent Date");
              if (dummyLen > 13) {
                addIsoField("PDS_0243_04", 2, "04 - MasterCom Issuer Response Code");
                if (dummyLen > 15) {
                  addIsoField("PDS_0243_05", 6, "05 - MasterCom Issuer Response Date");
                  if (dummyLen > 21) {
                    addIsoField("PDS_0243_06", 10, "06 - MasterCom Issuer Reject Reasons");
                    if (dummyLen > 31) {
                      addIsoField("PDS_0243_07", 1, "07 - MasterCom Image Review Decision");
                      if (dummyLen > 32) {
                        addIsoField("PDS_0243_08", 6, "08 - MasterCom Image Review Date");
                      }
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0243_STR;

          // PDS_0243_STR //PDS_0243 (dummyLen) <name="0243 - MasterCom Retrieval Response
          // Data");

          break;

        case 244:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0244_01", 6, "01 - MasterCom Chrgback Support Doc. Sender Processing Date (1st Chrgback)");
          if (dummyLen > 6) {
            addIsoField("PDS_0244_02", 6, "02 - MasterCom Chrgback Support Doc. Sender Processing Date (2nd Present)");
          }
          // } //PDS_0244_STR;

          // PDS_0244_STR //PDS_0244 (dummyLen) <name="0244 - MasterCom Chargeback Support
          // Documentation Dates");

          break;

        case 245:
          addIsoField("PDS_0245", dummyLen, "0245 - MasterCom Arbitration Chargeback Sender Processing Date");
          break;
        case 246:
          addIsoField("PDS_0246", dummyLen, "0246 - MasterCom Sender Memo");
          break;
        case 247:
          addIsoField("PDS_0247", dummyLen, "0247 - MasterCom Receiver Memo");
          break;
        case 248:
          addIsoField("PDS_0248", dummyLen, "0248 - MasterCom Image Review Memo");
          break;
        case 249:
          addIsoField("PDS_0249", dummyLen, "0249 - MasterCom Record ID");
          break;
        case 250:

          // typedef //struct {
          addIsoField("PDS_0250_01", 7, "01 - MasterCom Sender Endpoint Number");
          addIsoField("PDS_0250_02", 7, "02 - MasterCom Receiver Endpoint Number");
          // } //PDS_0250_STR;

          // PDS_0250_STR //PDS_0250 <name="0250 - MasterCom Endpoints");

          break;

        case 251:
          addIsoField("PDS_0251", dummyLen, "0251 - MasterCom Fulfillment Document Code");
          break;
        case 252:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0252_01", 8, "01 - MasterCom TIFF Image Data Length");
          addIsoField("PDS_0252_02", 8, "02 - MasterCom TIFF Image Data Offset");
          addIsoField("PDS_0252_03", 8, "03 - MasterCom TIFF Image CRC-32");
          addIsoField("PDS_0252_04", dummyLen - 24, "04 - MasterCom TIFF Image Filename");
          // } //PDS_0252_STR;

          // PDS_0252_STR //PDS_0252 (dummyLen) <name="0252 - MasterCom Image Metadata");
          break;

        case 253:
          addIsoField("PDS_0253", dummyLen, "0253 - MasterCom System Enhanced Data");
          break;
        case 254:
          addIsoField("PDS_0254", dummyLen, "0254 - MasterCom Member Enhanced Data");
          break;
        case 255:
          addIsoField("PDS_0255", dummyLen, "0255 - MasterCom Message Type");
          break;
        case 260:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0260_01", 2, "01 - Exclusion Request Code");
          if (dummyLen > 2) {
            addIsoField("PDS_0260_02", 1, "02 - Exclusion Reason Code");
            if (dummyLen > 3) {
              addIsoField("PDS_0260_03", 1, "03 - Exclusion Results Code");
            }
          }
          // } //PDS_0260_STR;

          // PDS_0260_STR //PDS_0260 (dummyLen) <name="0260 - Edit Exclusion Indicator");

          break;

        case 261:
          addIsoField("PDS_0261", dummyLen, "0261 - Risk Management Approval Code");
          break;
        case 262:
          addIsoField("PDS_0262", dummyLen, "0262 - Documentation Indicator");
          break;
        case 263:
          addIsoField("PDS_0263", dummyLen, "0263 - Interchange Life Cycle Validation Code");
          break;
        case 264:
          addIsoField("PDS_0264", dummyLen, "0264 - Original Retrieval Reason for Request");
          break;
        case 265:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0265_01", 4, "01 - Initial Message Reason Code");
          addIsoField("PDS_0265_02", 6, "02 - Date, Initial Presentment Business");
          addIsoField("PDS_0265_03", dummyLen - 10, "03 - Data Record, Initial");
          // } //PDS_0265_STR;

          // PDS_0265_STR //PDS_0265 (dummyLen) <name="0265 - Initial Presentment/Fee
          // Collection Data");

          break;

        case 266:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0266_01", 4, "01 - Message Reason Code");
          addIsoField("PDS_0266_02", 6, "02 - Date, First Return Business");
          addIsoField("PDS_0266_03", 1, "03 - Edit Exclusion Reason Code");
          addIsoField("PDS_0266_04", 1, "04 - Edit Exclusion Results Code");
          addIsoField("PDS_0266_05", 12, "05 - Amount, First Return");
          addIsoField("PDS_0266_06", 3, "06 - Currency Code, First Return");
          addIsoField("PDS_0266_07", dummyLen - 27, "07 - Data Record, First Return");
          // } //PDS_0266_STR;

          // PDS_0266_STR //PDS_0266 (dummyLen) <name="0266 - First Chargeback/Fee
          // Collection Return Data");

          break;

        case 267:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0267_01", 4, "01 - Message Reason Code");
          addIsoField("PDS_0267_02", 6, "02 - Date, Second Return Business");
          addIsoField("PDS_0267_03", 1, "03 - Edit Exclusion Reason Code");
          addIsoField("PDS_0267_04", 1, "04 - Edit Exclusion Results Code");
          addIsoField("PDS_0267_05", 12, "05 - Amount, Second Return");
          addIsoField("PDS_0267_06", 3, "06 - Currency Code, Second Return");
          addIsoField("PDS_0267_07", dummyLen - 27, "07 - Data Record, Second Return");
          // } //PDS_0267_STR;

          // PDS_0267_STR //PDS_0267 (dummyLen) <name="0267 - Second Presentment/Fee
          // Collection Resubmission Data");
          break;

        case 268:

          // typedef //struct {
          addIsoField("PDS_0268_01", 12, "01 - Amount, Partial Transaction");
          addIsoField("PDS_0268_02", 3, "02 - Currency Code, Partial Transaction");

          // } //PDS_0268_STR;

          // PDS_0268_STR //PDS_0268 <name="0268 - Amount, Partial Transaction");

          break;

        case 280:

          // typedef //struct {
          addIsoField("PDS_0280_01", 3, "01 - File Type");
          addIsoField("PDS_0280_02", 6, "02 - File Reference Date");
          addIsoField("PDS_0280_03", 11, "03 - Processor ID");
          addIsoField("PDS_0280_04", 5, "04 - File Sequence Number");

          // } //PDS_0280_STR;

          // PDS_0280_STR //PDS_0280 <name="0280 - Source File ID");
          break;

        case 300:

          // typedef //struct {
          addIsoField("PDS_0300_01", 3, "01 - File Type");
          addIsoField("PDS_0300_02", 6, "02 - File Reference Date");
          addIsoField("PDS_0300_03", 11, "03 - Processor ID");
          addIsoField("PDS_0300_04", 5, "04 - File Sequence Number");
          // } //PDS_0300_STR;

          // PDS_0300_STR //PDS_0300 <name="0300 - Reconciled, File");

          break;

        case 301:
          addIsoField("PDS_0301", dummyLen, "0301 - File Amount, Checksum");
          break;
        case 302:
          addIsoField("PDS_0302", dummyLen, "0302 - Reconciled, Member Activity");
          break;
        case 306:
          addIsoField("PDS_0306", dummyLen, "0306 - File Message Counts");
          break;
        case 358:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0358_01", 3, "01 - Card Program Identifier");
          if (dummyLen > 3) {
            addIsoField("PDS_0358_02", 1, "02 - Business Service Arrangement Type Code");
            if (dummyLen > 4) {
              addIsoField("PDS_0358_03", 6, "03 - Business Service ID Code");
              if (dummyLen > 10) {
                addIsoField("PDS_0358_04", 2, "04 - Interchange Rate Designator");
                if (dummyLen > 12) {
                  addIsoField("PDS_0358_05", 6, "05 - Business Date");
                  if (dummyLen > 18) {
                    addIsoField("PDS_0358_06", 2, "06 - Business Cycle");
                    if (dummyLen > 20) {
                      addIsoField("PDS_0358_07", 1, "07 - Card Acceptor Classification Override Indicator");
                      if (dummyLen > 21) {
                        addIsoField("PDS_0358_08", 3, "08 - Product Class Override Indicator");
                        if (dummyLen > 22) {
                          addIsoField("PDS_0358_09", 1, "09 - Corporate Incentive Rates Apply Indicator");
                          if (dummyLen > 25) {
                            addIsoField("PDS_0358_10", 1, "10 - Special Conditions Indicator");
                            if (dummyLen > 26) {
                              addIsoField("PDS_0358_11", 1, "11 - MasterCard Assigned ID Override Indicator");
                              if (dummyLen > 27) {
                                addIsoField("PDS_0358_12", 1, "12 - Account Level Management Account Category Code");
                                if (dummyLen > 28) {
                                  addIsoField("PDS_0358_13", 1, "13 - Rate Indicator");
                                  if (dummyLen > 29) {
                                    addIsoField("PDS_0358_14", 1, "14 - MasterPass Incentive Indicator");
                                    if (dummyLen > 30) {
                                      addIsoField("PDS_0358_15", 1, "15 - XXXX Indicator");
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0358_STR;

          // PDS_0358_STR //PDS_0358 (dummyLen) <name="0358 - Reconciled, Business
          // Activity");
          break;

        case 359:

          // typedef //struct {
          addIsoField("PDS_0359_01", 11, "01 - Settlement Service Transfer Agent ID Code");
          addIsoField("PDS_0359_02", 28, "02 - Settlement Service Transfer Agent Account");
          addIsoField("PDS_0359_03", 1, "03 - Settlement Service Level Code");
          addIsoField("PDS_0359_04", 10, "04 - Settlement Service ID Code");
          addIsoField("PDS_0359_05", 1, "05 - Settlement Foreign Exchange Rate Class Code");
          addIsoField("PDS_0359_06", 6, "06 - Reconciliation Date");
          addIsoField("PDS_0359_07", 2, "07 - Reconciliation Cycle");
          addIsoField("PDS_0359_08", 6, "08 - Settlement Date");
          addIsoField("PDS_0359_09", 2, "09 - Settlement Cycle");
          // } //PDS_0359_STR;

          // PDS_0359_STR //PDS_0359 <name="0359 - Reconciled, Settlement Activity");
          break;

        case 367:
          addIsoField("PDS_0367", dummyLen, "0367 - Reconciled, Card Program Identifier");
          break;
        case 368:
          addIsoField("PDS_0368", dummyLen, "0368 - Reconciled, Transaction Function Group Code");
          break;
        case 369:
          addIsoField("PDS_0369", dummyLen, "0369 - Reconciled, Acquirer?s BIN");
          break;
        case 370:
          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0370_01", 19, "01 - Beginning Account Range ID");
          if (dummyLen > 19) {
            addIsoField("PDS_0370_02", 19, "02 - Ending Account Range ID");
          }
          // } //PDS_0370_STR;

          // PDS_0370_STR //PDS_0370 (dummyLen) <name="0370 - Reconciled, Account Range");

          break;

        case 372:

          // typedef //struct {
          addIsoField("PDS_0372_01", 4, "01 - Message Type Identifier");
          addIsoField("PDS_0372_02", 3, "02 - Function Code");
          // } //PDS_0372_STR;

          // PDS_0372_STR //PDS_0372 <name="0372 - Reconciled, Transaction Function");
          break;

        case 374:
          addIsoField("PDS_0374", dummyLen, "0374 - Reconciled, Processing Code");
          break;
        case 375:
          addIsoField("PDS_0375", dummyLen, "0375 - Member Reconciliation Indicator 1");
          break;
        case 378:
          addIsoField("PDS_0378", dummyLen, "0378 - Original/Reversal/Totals Indicator");
          break;
        case 380:

          // typedef //struct {
          addIsoField("PDS_0380_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0380_02", 16, "02 - Amount, Transaction");
          // } //PDS_0380_STR;

          // PDS_0380_STR //PDS_0380 <name="0380 - Debits, Transaction Amount in
          // Transaction Currency");
          break;

        case 381:
          // typedef //struct {
          addIsoField("PDS_0381_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0381_02", 16, "02 - Amount, Transaction");
          // } //PDS_0381_STR;

          // PDS_0381_STR //PDS_0381 <name="0381 - Credits, Transaction Amount in
          // Transaction Currency");
          break;

        case 384:

          // typedef //struct {
          addIsoField("PDS_0384_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0384_02", 16, "02 - Amount, Net");
          // } //PDS_0384_STR;

          // PDS_0384_STR //PDS_0384 <name="0384 - Amount, Net Transaction in Transaction
          // Currency");
          break;

        case 390:

          // typedef //struct {
          addIsoField("PDS_0390_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0390_02", 16, "02 - Amount, Transaction");
          // } //PDS_0390_STR;

          // PDS_0390_STR //PDS_0390 <name="0390 - Debits, Transaction Amount in
          // Reconciliation Currency");

          break;

        case 391:
          // typedef //struct {
          addIsoField("PDS_0391_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0391_02", 16, "02 - Amount, Transaction");
          // } //PDS_0391_STR;

          // PDS_0391_STR //PDS_0391 <name="0391 - Credits, Transaction Amount in
          // Reconciliation Currency");
          break;

        case 392:

          // typedef //struct {
          // addIsoField("PDS_0392_01", 2, "01 - Fee Type Code");
          // addIsoField("PDS_0392_02", 1, "02 - Debit/Credit Indicator");
          // addIsoField("PDS_0392_03", 15, "03 - Amount, Fee");
          // } //PDS_0392_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 18) {
            addIsoField("PDS_0392_01", 2, "01 - Fee Type Code");
            addIsoField("PDS_0392_02", 1, "02 - Debit/Credit Indicator");
            addIsoField("PDS_0392_03", 15, "03 - Amount, Fee");
            // PDS_0392_STR //PDS_0392 <name="Debits, Fee Amounts in Reconciliation
            // Currency");
            dummyLen -= 18;
          }
          // } //PDS_0392 (dummyLen) <name="0392 - Debits, Fee Amounts in Reconciliation
          // Currency");

          break;

        case 393:

          // typedef //struct {
          // addIsoField("PDS_0393_01", 2, "01 - Fee Type Code");
          // addIsoField("PDS_0393_02", 1, "02 - Debit/Credit Indicator");
          // addIsoField("PDS_0393_03", 15, "03 - Amount, Fee");
          // } //PDS_0393_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 18) {
            addIsoField("PDS_0393_01", 2, "01 - Fee Type Code");
            addIsoField("PDS_0393_02", 1, "02 - Debit/Credit Indicator");
            addIsoField("PDS_0393_03", 15, "03 - Amount, Fee");
            // PDS_0393_STR //PDS_0393 <name="Credits, Fee Amounts in Reconciliation
            // Currency");
            dummyLen -= 18;
          }
          // } //PDS_0393 (dummyLen) <name="0393 - Credits, Fee Amounts in Reconciliation
          // Currency");

          break;

        case 394:

          // typedef //struct {
          addIsoField("PDS_0394_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0394_02", 16, "02 - Amount, Net");
          // } //PDS_0394_STR;

          // PDS_0394_STR //PDS_0394 <name="0394 - Amount, Net Transaction in
          // Reconciliation Currency");
          break;

        case 395:

          // typedef //struct {
          addIsoField("PDS_0395_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0395_02", 15, "02 - Amount, Net Fee");
          // } //PDS_0395_STR;

          // PDS_0395_STR //PDS_0395 <name="0395 - Amount, Net Fee in Reconciliation
          // Currency");
          break;

        case 396:

          // typedef //struct {
          addIsoField("PDS_0396_01", 1, "01 - Debit/Credit Indicator");
          addIsoField("PDS_0396_02", 16, "02 - Amount, Net Total");
          // } //PDS_0396_STR;

          // PDS_0396_STR //PDS_0396 <name="0396 - Amount, Net Total in Reconciliation
          // Currency");
          break;

        case 400:
          addIsoField("PDS_0400", dummyLen, "0400 - Debits, Transaction Number");
          break;
        case 401:
          addIsoField("PDS_0401", dummyLen, "0401 - Credits, Transaction Number");
          break;
        case 402:
          addIsoField("PDS_0402", dummyLen, "0402 - Total, Transaction Number");
          break;
        case 446:
          addIsoField("PDS_0446", dummyLen, "0446 - Transaction Fee Amount, Syntax Error");
          break;
        case 501:

          // typedef //struct {
          addIsoField("PDS_0501_01", 2, "01 - Usage Code");
          addIsoField("PDS_0501_02", 3, "02 - Industry Record Number");
          addIsoField("PDS_0501_03", 3, "03 - Occurrence Indicator");
          addIsoField("PDS_0501_04", 8, "04 - Associated First Presentment Number");
          // } //PDS_0501_STR;

          // PDS_0501_STR //PDS_0501 <name="0501 - Transaction Description");
          break;

        case 502:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0502_01", 6, "01 - Custom Identifier Type");
          addIsoField("PDS_0502_02", dummyLen - 6, "02 - Custom Identifier Detail");
          // } //PDS_0502_STR;

          // PDS_0502_STR //PDS_0502 (dummyLen) <name="0502 - Custom Identifier");
          break;

        case 503:
          addIsoField("PDS_0503", dummyLen, "0503 - Travel Agency Sequence Number");
          break;
        case 504:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0504_01", 12, "01 - Travel Agency Fee Amount");
          addIsoField("PDS_0504_02", 1, "02 - Travel Agency Fee Amount Exponent");
          addIsoField("PDS_0504_03", 1, "03 - Travel Agency Fee Amount Sign");
          if (dummyLen > 14) {
            addIsoField("PDS_0504_04", 12, "04 - Travel Agency Fee Amount Rate");
            if (dummyLen > 26) {
              addIsoField("PDS_0504_05", 16, "05 - Travel Agency Fee Description");
            }
          }
          // } //PDS_0504_STR;

          // PDS_0504_STR //PDS_0504 (dummyLen) <name="0504 - Travel Agency Fee");
          break;

        case 505:
          addIsoField("PDS_0505", dummyLen, "0505 - Passenger Name");
          break;
        case 506:
          addIsoField("PDS_0506", dummyLen, "0506 - Ticket Number");
          break;
        case 507:
          addIsoField("PDS_0507", dummyLen, "0507 - Issuing Carrier");
          break;
        case 508:
          addIsoField("PDS_0508", dummyLen, "0508 - Customer Code");
          break;
        case 509:
          addIsoField("PDS_0509", dummyLen, "0509 - Issue Date");
          break;
        case 510:
          addIsoField("PDS_0510", dummyLen, "0510 - Travel Agency Code");
          break;
        case 511:
          addIsoField("PDS_0511", dummyLen, "0511 - Travel Agency Name");
          break;
        case 512:
          addIsoField("PDS_0512", dummyLen, "0512 - Total Fare");
          break;
        case 513:
          addIsoField("PDS_0513", dummyLen, "0513 - Total Fees");
          break;
        case 514:
          addIsoField("PDS_0514", dummyLen, "0514 - Total Taxes");
          break;
        case 515:
          addIsoField("PDS_0515", dummyLen, "0515 - Additional Card Acceptor Information");
          break;
        case 516:
          addIsoField("PDS_0516", dummyLen, "0516 - Austin Tetra Number");
          break;
        case 517:
          addIsoField("PDS_0517", dummyLen, "0517 - NAICS Code");
          break;
        case 518:
          addIsoField("PDS_0518", dummyLen, "0518 - Line Item Date");
          break;
        case 519:

          // typedef //struct {
          addIsoField("PDS_0519_01", 1, "01 - Hours of Operation/24 Hours Available");
          addIsoField("PDS_0519_02", 1, "02 - 18 Wheeler Access Available");
          addIsoField("PDS_0519_03", 1, "03 - Diesel Sites Available");
          addIsoField("PDS_0519_04", 1, "04 - Interstate Access/Exit Number with Directions Available");
          addIsoField("PDS_0519_05", 1, "05 - Convenience Store Available");
          addIsoField("PDS_0519_06", 1, "06 - Truck Stop Restaurant Available");
          addIsoField("PDS_0519_07", 1, "07 - Truck Stop Hotel Available");
          addIsoField("PDS_0519_08", 1, "08 - Truck Stop with Showers Available");
          addIsoField("PDS_0519_09", 1, "09 - Vehicle Maintenance/Repair Bays Available");
          addIsoField("PDS_0519_10", 1, "10 - Car Wash Available");
          addIsoField("PDS_0519_11", 1, "11 - Aviation Locations Available");
          addIsoField("PDS_0519_12", 1, "12 - Marina Locations Available");
          addIsoField("PDS_0519_13", 1, "13 - Alternative Fuel Locations Available");
          addIsoField("PDS_0519_14", 1, "14 - Pay-At-Pump Available");
          // } //PDS_0519_STR;

          // PDS_0519_STR //PDS_0519 <name="0519 - Additional Fuel Location Information");
          break;

        case 520:
          addIsoField("PDS_0520", dummyLen, "0520 - Travel Date");
          break;
        case 521:
          addIsoField("PDS_0521", dummyLen, "0521 - Carrier Code");
          break;
        case 522:
          addIsoField("PDS_0522", dummyLen, "0522 - Service Class Code");
          break;
        case 523:
          addIsoField("PDS_0523", dummyLen, "0523 - City of Origin/Airport Code");
          break;
        case 524:
          addIsoField("PDS_0524", dummyLen, "0524 - City of Destination/Airport Code");
          break;
        case 525:
          addIsoField("PDS_0525", dummyLen, "0525 - Stop Over Code");
          break;
        case 526:
          addIsoField("PDS_0526", dummyLen, "0526 - Conjunction Ticket");
          break;
        case 527:
          addIsoField("PDS_0527", dummyLen, "0527 - Exchange Ticket");
          break;
        case 528:
          addIsoField("PDS_0528", dummyLen, "0528 - Coupon Number");
          break;
        case 529:
          addIsoField("PDS_0529", dummyLen, "0529 - Fare Basis Code");
          break;
        case 530:
          addIsoField("PDS_0530", dummyLen, "0530 - Flight Number");
          break;
        case 531:

          // typedef //struct {
          addIsoField("PDS_0531_01", 4, "01 - Departure Time");
          addIsoField("PDS_0531_02", 1, "02 - Departure Time Segment");
          // } //PDS_0531_STR;

          // PDS_0531_STR //PDS_0531 <name="0531 - Departure Time");
          break;

        case 532:

          // typedef //struct {
          addIsoField("PDS_0532_01", 12, "01 - Total Charges Amount");
          addIsoField("PDS_0532_02", 1, "02 - Total Charges Exponent");
          addIsoField("PDS_0532_03", 1, "03 - Total Charges Sign");
          // } //PDS_0532_STR;

          // PDS_0532_STR //PDS_0532 <name="0532 - Total Charges");
          break;

        case 533:

          // typedef //struct {
          addIsoField("PDS_0533_01", 4, "01 - Arrival Time");
          addIsoField("PDS_0533_02", 1, "02 - Arrival Time Segment");
          // } //PDS_0533_STR;

          // PDS_0533_STR //PDS_0533 <name="0533 - Arrival Time");
          break;

        case 534:

          // typedef //struct {
          addIsoField("PDS_0534_01", 12, "01 - Total Non-Room Charges Amount");
          addIsoField("PDS_0534_02", 1, "02 - Total Non-Room Charges Exponent");
          addIsoField("PDS_0534_03", 1, "03 - Total Non-Room Charges Sign");
          // } //PDS_0534_STR;

          // PDS_0534_STR //PDS_0534 <name="0534 - Total Non-Room Charges");

          break;

        case 535:
          addIsoField("PDS_0535", dummyLen, "0535 - Fare");
          break;
        case 536:
          addIsoField("PDS_0536", dummyLen, "0536 - Fee");
          break;
        case 537:
          addIsoField("PDS_0537", dummyLen, "0537 - Taxes");
          break;
        case 538:
          addIsoField("PDS_0538", dummyLen, "0538 - Endorsements/Restrictions");
          break;

        case 539:

          // typedef //struct {
          addIsoField("PDS_0539_01", 12, "01 - Total Amount Charged on Card Amount");
          addIsoField("PDS_0539_02", 1, "02 - Total Amount Charged on Card Exponent");
          addIsoField("PDS_0539_03", 1, "03 - TotalTotal Amount Charged on Card Sign");
          // } //PDS_0539_STR;

          // PDS_0539_STR //PDS_0539 <name="0539 - Total Amount Charged on Credit Card");
          break;

        case 540:

          // typedef //struct {
          addIsoField("PDS_0540_01", 12, "01 - Room Service Charges Amount");
          addIsoField("PDS_0540_02", 1, "02 - Room Service Charges Exponent");
          addIsoField("PDS_0540_03", 1, "03 - Room Service Charges Sign");
          // } //PDS_0540_STR;

          // PDS_0540_STR //PDS_0540 <name="0540 - Room Service Charges");

          break;

        case 541:

          // typedef //struct {
          addIsoField("PDS_0541_01", 12, "01 - Lounge/Bar Charges Amount");
          addIsoField("PDS_0541_02", 1, "02 - Lounge/Bar Charges Exponent");
          addIsoField("PDS_0541_03", 1, "03 - Lounge/Bar Charges Sign");
          // } //PDS_0541_STR;

          // PDS_0541_STR //PDS_0541 <name="0541 - Lounge/Bar Charges");
          break;

        case 542:

          // typedef //struct {
          addIsoField("PDS_0542_01", 12, "01 - Transportation Charges Amount");
          addIsoField("PDS_0542_02", 1, "02 - Transportation Charges Exponent");
          addIsoField("PDS_0542_03", 1, "03 - Transportation Charges Sign");
          // } //PDS_0542_STR;

          // PDS_0542_STR //PDS_0542 <name="0542 - Transportation Charges");
          break;

        case 543:
          addIsoField("PDS_0543", dummyLen, "0543 - Gratuity Charges");

          // typedef //struct {
          addIsoField("PDS_0543_01", 12, "01 - Gratuity Charges Amount");
          addIsoField("PDS_0543_02", 1, "02 - Gratuity Charges Exponent");
          addIsoField("PDS_0543_03", 1, "03 - Gratuity Charges Sign");
          // } //PDS_0543_STR;

          // PDS_0543_STR //PDS_0543 <name="0543 - Gratuity Charges");
          break;

        case 544:
          addIsoField("PDS_0544", dummyLen, "0544 - Rental Agreement Number");
          break;
        case 545:
          addIsoField("PDS_0545", dummyLen, "0545 - Renter Name");
          break;
        case 546:
          addIsoField("PDS_0546", dummyLen, "0546 - Rental Return City");
          break;
        case 547:
          addIsoField("PDS_0547", dummyLen, "0547 - Rental Return State/Province");
          break;
        case 548:
          addIsoField("PDS_0548", dummyLen, "0548 - Rental Return Country");
          break;
        case 549:
          addIsoField("PDS_0549", dummyLen, "0549 - Rental Return Location ID");
          break;
        case 550:
          addIsoField("PDS_0550", dummyLen, "0550 - Rental Return Date");
          break;
        case 551:
          addIsoField("PDS_0551", dummyLen, "0551 - Rental Check-out Date");
          break;
        case 552:
          addIsoField("PDS_0552", dummyLen, "0552 - Customer Service Toll-Free (800) Number");
          break;
        case 553:

          // typedef //struct {
          addIsoField("PDS_0553_01", 1, "01 - Rental Rate Indicator");
          addIsoField("PDS_0553_02", 12, "02 - Rental Rate");
          // } //PDS_0553_STR;

          // PDS_0553_STR //PDS_0553 <name="0553 - Rental Rate");
          break;

        case 554:

          // typedef //struct {
          addIsoField("PDS_0554_01", 12, "01 - Conference Room Charges Amount");
          addIsoField("PDS_0554_02", 1, "02 - Conference Room Charges Exponent");
          addIsoField("PDS_0554_03", 1, "03 - Conference Room Charges Sign");
          // } //PDS_0554_STR;

          // PDS_0554_STR //PDS_0554 <name="0554 - Conference Room Charges");
          break;

        case 555:
          addIsoField("PDS_0555", dummyLen, "0555 - Rate per Mile (or per Kilometer)");
          break;
        case 556:
          addIsoField("PDS_0556", dummyLen, "0556 - Total Miles (or Kilometers)");
          break;
        case 557:
          addIsoField("PDS_0557", dummyLen, "0557 - Maximum Free Miles (or Kilometers)");
          break;
        case 558:
          addIsoField("PDS_0558", dummyLen, "0558 - Miles/Kilometers Indicator");
          break;
        case 559:

          // typedef //struct {
          addIsoField("PDS_0559_01", 1, "01 - Insurance Indicator");
          addIsoField("PDS_0559_02", 12, "02 - Conference Room Charges Exponent");
          // } //PDS_0559_STR;

          // PDS_0559_STR //PDS_0559 <name="0559 - Vehicle Insurance");
          break;

        case 560:

          // typedef //struct {
          addIsoField("PDS_0560_01", 12, "01 - Audio Visual Charges Amount");
          addIsoField("PDS_0560_02", 1, "02 - Audio Visual Charges Exponent");
          addIsoField("PDS_0560_03", 1, "03 - Audio Visual Charges Sign");
          // } //PDS_0560_STR;

          // PDS_0560_STR //PDS_0560 <name="0560 - Audio Visual Charges");
          break;

        case 561:

          // typedef //struct {
          addIsoField("PDS_0561_01", 1, "01 - Adjusted Amount Indicator");
          addIsoField("PDS_0561_02", 12, "02 - Adjusted Amount");
          // } //PDS_0561_STR;

          // PDS_0561_STR //PDS_0561 <name="0561 - Adjusted Amount");
          break;

        case 562:

          // typedef //struct {
          addIsoField("PDS_0562_01", 12, "01 - Banquet Charges Amount");
          addIsoField("PDS_0562_02", 1, "02 - Banquet Charges Exponent");
          addIsoField("PDS_0562_03", 1, "03 - Banquet Charges Sign");
          // } //PDS_0562_STR;

          // PDS_0562_STR //PDS_0562 <name="0562 - Banquet Charges");
          break;

        case 563:
          addIsoField("PDS_0563", dummyLen, "0563 - Program Code");
          break;
        case 564:
          addIsoField("PDS_0564", dummyLen, "0564 - Rental Location City");
          break;
        case 565:
          addIsoField("PDS_0565", dummyLen, "0565 - Rental Location State/Province");
          break;
        case 566:
          addIsoField("PDS_0566", dummyLen, "0566 - Rental Location Country");
          break;
        case 567:
          addIsoField("PDS_0567", dummyLen, "0567 - Rental Location ID");
          break;
        case 568:
          addIsoField("PDS_0568", dummyLen, "0568 - Rental Class ID");
          break;
        case 569:

          // typedef //struct {
          addIsoField("PDS_0569_01", 12, "01 - Internet Access Charges Amount");
          addIsoField("PDS_0569_02", 1, "02 - Internet Access Charges Exponent");
          addIsoField("PDS_0569_03", 1, "03 - Internet Access Charges Sign");
          // } //PDS_0569_STR;

          // PDS_0569_STR //PDS_0569 <name="0569 - Internet Access Charges");
          break;

        case 570:

          // typedef //struct {
          addIsoField("PDS_0570_01", 12, "01 - Early Departure Charges Amount");
          addIsoField("PDS_0570_02", 1, "02 - Early Departure Charges Exponent");
          addIsoField("PDS_0570_03", 1, "03 - Early Departure Charges Sign");
          // } //PDS_0570_STR;

          // PDS_0570_STR //PDS_0570 <name="0570 - Early Departure Charges");
          break;

        case 571:
          addIsoField("PDS_0571", dummyLen, "0571 - Guest Name");
          break;
        case 572:
          addIsoField("PDS_0572", dummyLen, "0572 - Guest Number");
          break;
        case 573:
          addIsoField("PDS_0573", dummyLen, "0573 - Invoice Number");
          break;
        case 574:
          addIsoField("PDS_0574", dummyLen, "0574 - Arrival Date");
          break;
        case 575:
          addIsoField("PDS_0575", dummyLen, "0575 - Departure Date");
          break;
        case 576:
          addIsoField("PDS_0576", dummyLen, "0576 - Folio Number");
          break;
        case 577:
          addIsoField("PDS_0577", dummyLen, "0577 - Property Phone Number");
          break;
        case 578:

          // typedef //struct {
          addIsoField("PDS_0578_01", 1, "01 - Billing Adjustment Indicator");
          addIsoField("PDS_0578_02", 12, "02 - Billing Adjustment Amount");
          addIsoField("PDS_0578_03", 1, "03 - Billing Adjustment Exponent");
          addIsoField("PDS_0578_04", 1, "04 - Billing Adjustment Sign");
          // } //PDS_0578_STR;

          // PDS_0578_STR //PDS_0578 <name="0578 - Billing Adjustment");
          break;

        case 579:
          addIsoField("PDS_0579", dummyLen, "0579 - Invoice Date");
          break;
        case 580:

          // typedef //struct {
          addIsoField("PDS_0580_01", 12, "01 - Room Rate Amount");
          addIsoField("PDS_0580_02", 1, "02 - Room Rate Exponent");
          // } //PDS_0580_STR;

          // PDS_0580_STR //PDS_0580 <name="0580 - Room Rate");
          break;

        case 581:

          // typedef //struct {
          addIsoField("PDS_0581_01", 12, "01 - Total Room Tax Amount");
          addIsoField("PDS_0581_02", 1, "02 - Total Room Tax Exponent");
          addIsoField("PDS_0581_03", 1, "03 - Total Room Tax Sign");
          // } //PDS_0581_STR;

          // PDS_0581_STR //PDS_0581 <name="0581 - Total Room Tax");
          break;

        case 582:
          addIsoField("PDS_0582", dummyLen, "0582 - Program Code");
          break;
        case 583:

          // typedef //struct {
          addIsoField("PDS_0583_01", 12, "01 - Phone Charges Amount");
          addIsoField("PDS_0583_02", 1, "02 - Phone Charges Exponent");
          addIsoField("PDS_0583_03", 1, "03 - Phone Charges Sign");
          // } //PDS_0583_STR;

          // PDS_0583_STR //PDS_0583 <name="0583 - Phone Charges");
          break;

        case 584:

          // typedef //struct {
          addIsoField("PDS_0584_01", 12, "01 - Restaurant Charges Amount");
          addIsoField("PDS_0584_02", 1, "02 - Restaurant Charges Exponent");
          addIsoField("PDS_0584_03", 1, "03 - Restaurant Charges Sign");
          // } //PDS_0584_STR;

          // PDS_0584_STR //PDS_0584 <name="0584 - Restaurant Charges");
          break;

        case 585:

          // typedef //struct {
          addIsoField("PDS_0585_01", 12, "01 - Mini-Bar Charges Amount");
          addIsoField("PDS_0585_02", 1, "02 - Mini-Bar Charges Exponent");
          addIsoField("PDS_0585_03", 1, "03 - Mini-Bar Charges Sign");
          // } //PDS_0585_STR;

          // PDS_0585_STR //PDS_0585 <name="0585 - Mini-Bar Charges");
          break;

        case 586:

          // typedef //struct {
          addIsoField("PDS_0586_01", 12, "01 - Gift Shop Charges Amount");
          addIsoField("PDS_0586_02", 1, "02 - Gift Shop Charges Exponent");
          addIsoField("PDS_0586_03", 1, "03 - Gift Shop Charges Sign");
          // } //PDS_0586_STR;

          // PDS_0586_STR //PDS_0586 <name="0586 - Gift Shop Charges");
          break;

        case 587:

          // typedef //struct {
          addIsoField("PDS_0587_01", 12, "01 - Laundry and Dry Cleaning Charges Amount");
          addIsoField("PDS_0587_02", 1, "02 - Laundry and Dry Cleaning Charges Exponent");
          addIsoField("PDS_0587_03", 1, "03 - Laundry and Dry Cleaning Charges Sign");
          // } //PDS_0587_STR;

          // PDS_0587_STR //PDS_0587 <name="0587 - Laundry and Dry Cleaning Charges");

          break;

        case 588:

          // typedef //struct {
          // addIsoField("PDS_0588_01", 3, "01 - Other Services Code");
          // addIsoField("PDS_0588_02", 12, "02 - Other Services Amount");
          // addIsoField("PDS_0588_03", 1, "03 - Other Services Exponent");
          // addIsoField("PDS_0588_04", 1, "04 - Other Services Sign");
          // } //PDS_0588_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 17) {
            addIsoField("PDS_0588_01", 3, "01 - Other Services Code");
            addIsoField("PDS_0588_02", 12, "02 - Other Services Amount");
            addIsoField("PDS_0588_03", 1, "03 - Other Services Exponent");
            addIsoField("PDS_0588_04", 1, "04 - Other Services Sign");
            // PDS_0588_STR //PDS_0588 <name="Other Services");
            dummyLen -= 17;
          }
          // } //PDS_0588 (dummyLen) <name="0588 - Other Services");

          break;

        case 589:

          // typedef //struct {
          addIsoField("PDS_0589_01", 6, "01 - Invoice Creation Date");
          addIsoField("PDS_0589_02", 6, "02 - Invoice Creation Time");
          // } //PDS_0589_STR;

          // PDS_0589_STR //PDS_0589 <name="0589 - Invoice Creation Date/Time");
          break;

        case 590:
          addIsoField("PDS_0590", dummyLen, "0590 - Party Identification");
          break;
        case 591:

          // struct (int dummyLen) {
          while (dummyLen >= 40) {
            addIsoField("PDS_0591", dummyLen, "Party Name");
            dummyLen -= 40;
          }
          // } //PDS_0591 (dummyLen) <name="0591 - Party Name");

          break;

        case 592:
          // struct (int dummyLen) {
          while (dummyLen >= 40) {
            addIsoField("PDS_0592", dummyLen, "Party Address");
            dummyLen -= 40;
          }
          // } //PDS_0592 (dummyLen) <name="0592 - Party Address");

          break;

        case 593:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0593_01", 25, "01 - City");
          if (dummyLen > 25) {
            addIsoField("PDS_0593_02", 3, "02 - State/Province Code");
            if (dummyLen > 28) {
              addIsoField("PDS_0593_03", 3, "03 - Country Code");
              if (dummyLen > 31) {
                addIsoField("PDS_0593_04", 15, "04 - Postal Code");
              }
            }
          }
          // } //PDS_0593_STR;

          // PDS_0593_STR //PDS_0593 (dummyLen) <name="0593 - Party Postal Information");
          break;

        case 594:

          // typedef //struct {
          // addIsoField("PDS_0594_01", 1, "01 - Party Contact Descriptor");
          // addIsoField("PDS_0594_02", 60, "02 - Party Contact Information");
          // } //PDS_0594_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 61) {
            addIsoField("PDS_0594_01", 1, "01 - Party Contact Descriptor");
            addIsoField("PDS_0594_02", 60, "02 - Party Contact Information");
            // PDS_0594_STR //PDS_0594 <name="Party Contact");
            dummyLen -= 61;
          }
          // } //PDS_0594 (dummyLen) <name="0594 - Party Contact");

          break;

        case 595:

          // typedef //struct {
          addIsoField("PDS_0595_01", 1, "01 - Business Type");
          addIsoField("PDS_0595_02", 1, "02 - Business Owner Type");
          addIsoField("PDS_0595_03", 1, "03 - Business Certification Type");
          addIsoField("PDS_0595_04", 1, "04 - Business Racial/Ethnic Type");
          addIsoField("PDS_0595_05", 1, "05 - Business Type Provided Code");
          addIsoField("PDS_0595_06", 1, "06 - Business Owner Type Provided Code");
          addIsoField("PDS_0595_07", 1, "07 - Business Certification Type Provided Code");
          addIsoField("PDS_0595_08", 1, "08 - Business Racial/Ethnic Type Provided Code");
          // } //PDS_0595_STR;

          // PDS_0595_STR //PDS_0595 <name="0595 - Card Acceptor Type");

          break;

        case 596:

          // typedef //struct {
          addIsoField("PDS_0596_01", 20, "01 - Card Acceptor Tax ID");
          addIsoField("PDS_0596_02", 1, "02 - Card Acceptor Tax ID Provided Code");
          // } //PDS_0596_STR;

          // PDS_0596_STR //PDS_0596 <name="0596 - Card Acceptor Tax ID");

          break;

        case 597:

          // typedef //struct {
          addIsoField("PDS_0597_01", 12, "01 - Total Tax Amount");
          addIsoField("PDS_0597_02", 1, "02 - Total Tax Exponent");
          addIsoField("PDS_0597_03", 1, "03 - Total Tax Sign");
          // } //PDS_0597_STR;

          // PDS_0597_STR //PDS_0597 <name="0597 - Total Tax Amount");

          break;

        case 598:
          addIsoField("PDS_0598", dummyLen, "0598 - Total Tax Collected Indicator");
          break;
        case 599:
          addIsoField("PDS_0599", dummyLen, "0599 - Corporation VAT Number");
          break;
        case 600:
          addIsoField("PDS_0600", dummyLen, "0600 - Card Acceptor Reference Number");
          break;
        case 601:
          addIsoField("PDS_0601", dummyLen, "0601 - Party Nature of Filing");
          break;
        case 602:

          // typedef //struct {
          // addIsoField("PDS_0602_01", 40, "01 - Party Supplemental Data Description 1");
          // addIsoField("PDS_0602_02", 40, "02 - Party Supplemental Data 1");
          // addIsoField("PDS_0602_03", 1, "03 - Party Supplemental Data Code 1");
          // } //PDS_0602_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 84) {
            addIsoField("PDS_0602_01", 40, "01 - Party Supplemental Data Description 1");
            addIsoField("PDS_0602_02", 40, "02 - Party Supplemental Data 1");
            addIsoField("PDS_0602_03", 1, "03 - Party Supplemental Data Code 1");
            // PDS_0602_STR //PDS_0602 <name="Party Supplemental Data 1");
            dummyLen -= 84;
          }
          // } //PDS_0602 (dummyLen) <name="0602 - Party Supplemental Data 1");

          break;

        case 603:

          // typedef //struct {
          // addIsoField("PDS_0603_01", 40, "01 - Party Supplemental Data Description 2");
          // addIsoField("PDS_0603_02", 40, "02 - Party Supplemental Data 2");
          // addIsoField("PDS_0603_03", 1, "03 - Party Supplemental Data Code 2");
          // } //PDS_0603_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 84) {
            addIsoField("PDS_0603_01", 40, "01 - Party Supplemental Data Description 2");
            addIsoField("PDS_0603_02", 40, "02 - Party Supplemental Data 2");
            addIsoField("PDS_0603_03", 1, "03 - Party Supplemental Data Code 2");
            // PDS_0603_STR //PDS_0603 <name="Party Supplemental Data 2");
            dummyLen -= 84;
          }
          // } //PDS_0603 (dummyLen) <name="0603 - Party Supplemental Data 2");

          break;

        case 604:

          // typedef //struct {
          // addIsoField("PDS_0604_01", 40, "01 - Transaction Supplemental Data
          // Description
          // 1");
          // addIsoField("PDS_0604_02", 40, "02 - Transaction Supplemental Data 1");
          // addIsoField("PDS_0604_03", 1, "03 - Transaction Supplemental Data Code 1");
          // } //PDS_0604_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 84) {
            addIsoField("PDS_0604_01", 40, "01 - Transaction Supplemental Data Description 1");
            addIsoField("PDS_0604_02", 40, "02 - Transaction Supplemental Data 1");
            addIsoField("PDS_0604_03", 1, "03 - Transaction Supplemental Data Code 1");
            // PDS_0604_STR //PDS_0604 <name="Transaction Supplemental Data 1");
            dummyLen -= 84;
          }
          // } //PDS_0604 (dummyLen) <name="0604 - Transaction Supplemental Data 1");

          break;

        case 605:

          // typedef //struct {
          // addIsoField("PDS_0605_01", 40, "01 - Transaction Supplemental Data
          // Description
          // 2");
          // addIsoField("PDS_0605_02", 40, "02 - Transaction Supplemental Data 2");
          // addIsoField("PDS_0605_03", 1, "03 - Transaction Supplemental Data Code 2");
          // } //PDS_0605_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 84) {
            addIsoField("PDS_0605_01", 40, "01 - Transaction Supplemental Data Description 2");
            addIsoField("PDS_0605_02", 40, "02 - Transaction Supplemental Data 2");
            addIsoField("PDS_0605_03", 1, "03 - Transaction Supplemental Data Code 2");
            // PDS_0605_STR //PDS_0605 <name="Transaction Supplemental Data 2");
            dummyLen -= 84;
          }
          // } //PDS_0605 (dummyLen) <name="0605 - Transaction Supplemental Data 2");

          break;

        case 606:

          // typedef //struct {
          addIsoField("PDS_0606_01", 12, "01 - Freight Amount");
          addIsoField("PDS_0606_02", 1, "02 - Freight Exponent");
          addIsoField("PDS_0606_03", 1, "03 - Freight Sign");
          // } //PDS_0606_STR;

          // PDS_0606_STR //PDS_0606 <name="0606 - Freight Amount");
          break;

        case 607:

          // typedef //struct {
          addIsoField("PDS_0607_01", 12, "01 - Duty Amount");
          addIsoField("PDS_0607_02", 1, "02 - Duty Exponent");
          addIsoField("PDS_0607_03", 1, "03 - Duty Sign");
          // } //PDS_0607_STR;

          // PDS_0607_STR //PDS_0607 <name="0607 - Duty Amount");
          break;

        case 608:
          addIsoField("PDS_0608", dummyLen, "0608 - Destination Postal Code");
          break;
        case 609:
          addIsoField("PDS_0609", dummyLen, "0609 - Destination State/Province Code");
          break;
        case 610:
          addIsoField("PDS_0610", dummyLen, "0610 - Destination Country Code");
          break;
        case 611:
          addIsoField("PDS_0611", dummyLen, "0611 - Sequence Number");
          break;
        case 612:
          addIsoField("PDS_0612", dummyLen, "0612 - Ship Date");
          break;
        case 613:
          addIsoField("PDS_0613", dummyLen, "0613 - Ship-From Postal Code");
          break;
        case 614:
          addIsoField("PDS_0614", dummyLen, "0614 - Order Date");
          break;
        case 615:
          addIsoField("PDS_0615", dummyLen, "0615 - Medical Services Ship to Health Industry Number");
          break;
        case 616:
          addIsoField("PDS_0616", dummyLen, "0616 - Contract Number");
          break;
        case 617:
          addIsoField("PDS_0617", dummyLen, "0617 - Medical Services Price Adjustment");
          break;
        case 618:
          addIsoField("PDS_0618", dummyLen, "0618 - Medical Services Product Number Qualifier");
          break;
        case 619:
          addIsoField("PDS_0619", dummyLen, "0619 - User Name");
          break;
        case 620:
          addIsoField("PDS_0620", dummyLen, "0620 - Oil Company Brand Name");
          break;
        case 621:
          addIsoField("PDS_0621", dummyLen, "0621 - Purchase Time");
          break;
        case 622:
          addIsoField("PDS_0622", dummyLen, "0622 - Motor Fuel Service Type");
          break;
        case 623:

          // typedef //struct {
          addIsoField("PDS_0623_01", 3, "01 - Motor Fuel Product Code");
          addIsoField("PDS_0623_02", 12, "02 - Motor Fuel Unit Price");
          addIsoField("PDS_0623_03", 1, "03 - Motor Fuel Unit of Measure");
          addIsoField("PDS_0623_04", 6, "04 - Motor Fuel Quantity");
          addIsoField("PDS_0623_05", 1, "05 - Motor Fuel Quantity Exponent");
          addIsoField("PDS_0623_06", 12, "06 - Motor Fuel Sale Amount");
          // } //PDS_0623_STR;

          // PDS_0623_STR //PDS_0623 <name="0623 - Motor Fuel Information");
          break;

        case 624:
          addIsoField("PDS_0624", dummyLen, "0624 - User Account Number");
          break;
        case 625:
          addIsoField("PDS_0625", dummyLen, "0625 - User Telephone Number");
          break;
        case 626:

          // typedef //struct {
          addIsoField("PDS_0626_01", 6, "01 - Statement Start Date");
          addIsoField("PDS_0626_02", 6, "02 - Statement End Date");

          // } //PDS_0626_STR;

          // PDS_0626_STR //PDS_0626 <name="0626 - Billing Statement Period");
          break;

        case 627:

          // typedef //struct {
          // addIsoField("PDS_0627_01", 12, "01 - Billing Event Amount 1");
          // addIsoField("PDS_0627_02", 1, "02 - Billing Event Exponent 1");
          // addIsoField("PDS_0627_03", 1, "03 - Billing Event Sign 1");
          // addIsoField("PDS_0627_04", 40, "04 - Billing Event Description");
          // } //PDS_0627_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 54) {
            addIsoField("PDS_0627_01", 12, "01 - Billing Event Amount 1");
            addIsoField("PDS_0627_02", 1, "02 - Billing Event Exponent 1");
            addIsoField("PDS_0627_03", 1, "03 - Billing Event Sign 1");
            addIsoField("PDS_0627_04", 40, "04 - Billing Event Description");
            // PDS_0627_STR //PDS_0627 <name="Billing Event 1");
            dummyLen -= 54;
          }
          // } //PDS_0627 (dummyLen) <name="0627 - Billing Event 1");

          break;

        case 628:

          // typedef //struct {
          // addIsoField("PDS_0628_01", 12, "01 - Billing Event Amount 2");
          // addIsoField("PDS_0628_02", 1, "02 - Billing Event Exponent 2");
          // addIsoField("PDS_0628_03", 1, "03 - Billing Event Sign 2");
          // addIsoField("PDS_0628_04", 40, "04 - Billing Event Description");
          // } //PDS_0628_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 54) {
            addIsoField("PDS_0628_01", 12, "01 - Billing Event Amount 2");
            addIsoField("PDS_0628_02", 1, "02 - Billing Event Exponent 2");
            addIsoField("PDS_0628_03", 1, "03 - Billing Event Sign 2");
            addIsoField("PDS_0628_04", 40, "04 - Billing Event Description");
            // PDS_0628_STR //PDS_0628 <name="Billing Event 2");
            dummyLen -= 54;
          }
          // } //PDS_0628 (dummyLen) <name="0628 - Billing Event 2");

          break;

        case 629:
          addIsoField("PDS_0629", dummyLen, "0629 - Odometer Reading");
          break;
        case 630:
          addIsoField("PDS_0630", dummyLen, "0630 - Vehicle Number");
          break;
        case 631:
          addIsoField("PDS_0631", dummyLen, "0631 - Driver Number/ID Number");
          break;
        case 632:
          addIsoField("PDS_0632", dummyLen, "0632 - Product Type Code");
          break;
        case 633:
          addIsoField("PDS_0633", dummyLen, "0633 - Coupon/Discount Amount");
          break;
        case 634:
          addIsoField("PDS_0634", dummyLen, "0634 - Tax Amount 1");
          break;
        case 635:
          addIsoField("PDS_0635", dummyLen, "0635 - Tax Amount 2");
          break;
        case 636:
          addIsoField("PDS_0636", dummyLen, "0636 - Call Date");
          break;
        case 637:
          addIsoField("PDS_0637", dummyLen, "0637 - Call Time");
          break;
        case 638:

          // typedef //struct {
          addIsoField("PDS_0638_01", 40, "01 - Call To City");
          addIsoField("PDS_0638_02", 1, "02 - Call To State/Province");
          addIsoField("PDS_0638_03", 1, "03 - Call To Country");
          addIsoField("PDS_0638_04", 40, "04 - Call To Number");
          addIsoField("PDS_0638_05", 40, "05 - Call To Type");
          // } //PDS_0638_STR;

          // PDS_0638_STR //PDS_0638 <name="0638 - Call To Information");

          break;

        case 639:
          addIsoField("PDS_0639", dummyLen, "0639 - Call Duration");
          break;
        case 640:
          addIsoField("PDS_0640", dummyLen, "0640 - Call Time Period");
          break;
        case 641:
          addIsoField("PDS_0641", dummyLen, "0641 - Product Code");
          break;
        case 642:
          addIsoField("PDS_0642", dummyLen, "0642 - Item Description");
          break;

        case 643:

          // typedef //struct {
          addIsoField("PDS_0643_01", 12, "01 - Item Quantity");
          addIsoField("PDS_0643_02", 1, "02 - Item Quantity Exponent");
          // } //PDS_0643_STR;

          // PDS_0643_STR //PDS_0643 <name="0643 - Item Quantity");
          break;

        case 644:

          // typedef //struct {
          addIsoField("PDS_0644_01", 40, "01 - Call From City");
          addIsoField("PDS_0644_02", 40, "02 - Call From State/Province");
          addIsoField("PDS_0644_03", 40, "03 - Call From Country");
          addIsoField("PDS_0644_04", 25, "04 - Call From Number");
          // } //PDS_0644_STR;

          // PDS_0644_STR //PDS_0644 <name="0644 - Call From Information");
          break;

        case 645:
          addIsoField("PDS_0645", dummyLen, "0645 - Item Unit of Measure");
          break;
        case 646:

          // typedef //struct {
          addIsoField("PDS_0646_01", 12, "01 - Unit Price");
          addIsoField("PDS_0646_02", 1, "02 - Unit Price Exponent");
          // } //PDS_0646_STR;

          // PDS_0646_STR //PDS_0646 <name="0646 - Unit Price");
          break;

        case 647:

          // typedef //struct {
          addIsoField("PDS_0647_01", 12, "01 - Extended Item Amount");
          addIsoField("PDS_0647_02", 1, "02 - Extended Item Amount Exponent");
          addIsoField("PDS_0647_03", 1, "03 - Extended Item Amount Sign");
          // } //PDS_0647_STR;

          // PDS_0647_STR //PDS_0647 <name="0647 - Extended Item Amount");
          break;

        case 648:
          // typedef //struct {
          addIsoField("PDS_0648_01", 1, "01 - Discount Indicator");
          addIsoField("PDS_0648_02", 12, "02 - Discount Amount");
          addIsoField("PDS_0648_03", 5, "03 - Item Discount Rate");
          addIsoField("PDS_0648_04", 1, "04 - Item Discount Rate Exponent");
          addIsoField("PDS_0648_05", 1, "05 - Item Discount Amount Sign");
          // } //PDS_0648_STR;

          // PDS_0648_STR //PDS_0648 <name="0648 - Item Discount");
          break;

        case 649:
          // typedef //struct {
          addIsoField("PDS_0649_01", 12, "01 - Call Usage Amount");
          addIsoField("PDS_0649_02", 1, "02 - Call Usage Amount Exponent");
          addIsoField("PDS_0649_03", 1, "03 - Call Usage Amount Sign");
          // } //PDS_0649_STR;

          // PDS_0649_STR //PDS_0649 <name="0649 - Call Usage Amount");
          break;

        case 650:
          addIsoField("PDS_0650", dummyLen, "0650 - Zero Cost to Customer Indicator");
          break;
        case 651:
          addIsoField("PDS_0651", dummyLen, "0651 - Procedure ID");
          break;

        case 652:
          // typedef //struct {
          addIsoField("PDS_0652_01", 2, "01 - Service Type");
          addIsoField("PDS_0652_02", 2, "02 - Service Nature");
          // } //PDS_0652_STR;

          // PDS_0652_STR //PDS_0652 <name="0652 - Service Type");
          break;

        case 653:

          // typedef //struct {
          addIsoField("PDS_0653_01", 2, "01 - Service Amount");
          addIsoField("PDS_0653_02", 2, "02 - Service Amount Exponent");
          addIsoField("PDS_0653_03", 2, "03 - Service Amount Sign");
          // } //PDS_0653_STR;

          // PDS_0653_STR //PDS_0653 <name="0653 - Service Amount");
          break;

        case 654:
          addIsoField("PDS_0654", dummyLen, "0654 - Debit or Credit Indicator");
          break;
        case 655:

          // typedef //struct {
          addIsoField("PDS_0655_01", 12, "01 - Call Long Distance Amount");
          addIsoField("PDS_0655_02", 1, "02 - Call Long Distance Amount Exponent");
          addIsoField("PDS_0655_03", 1, "03 - Call Long Distance Amount Sign");
          // } //PDS_0655_STR;

          // PDS_0655_STR //PDS_0655 <name="0655 - Call Long Distance Amount");
          break;

        case 656:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0656_01", 12, "01 - Full VAT Gross Amount");
          addIsoField("PDS_0656_02", 1, "02 - Full VAT Gross Amount Exponent");
          addIsoField("PDS_0656_03", 1, "03 - Full VAT Gross Amount Sign");
          if (dummyLen > 14) {
            addIsoField("PDS_0656_04", 12, "04 - Full VAT Tax Amount");
            addIsoField("PDS_0656_05", 1, "05 - Full VAT Tax Amount Exponent");
            addIsoField("PDS_0656_06", 1, "06 - Full VAT Tax Amount Sign");
          }
          // } //PDS_0656_STR;

          // PDS_0656_STR //PDS_0656 (dummyLen) <name="0656 - Full VAT Amounts");
          break;

        case 657:

          // typedef //struct {
          addIsoField("PDS_0657_01", 12, "01 - Half VAT Gross Amount");
          addIsoField("PDS_0657_02", 1, "02 - Half VAT Gross Amount Exponent");
          addIsoField("PDS_0657_03", 1, "03 - Half VAT Gross Amount Sign");
          if (dummyLen > 14) {
            addIsoField("PDS_0657_04", 12, "04 - Half VAT Tax Amount");
            addIsoField("PDS_0657_05", 1, "05 - Half VAT Tax Amount Exponent");
            addIsoField("PDS_0657_06", 1, "06 - Half VAT Tax Amount Sign");
          }
          // } //PDS_0657_STR;

          // PDS_0657_STR //PDS_0657 <name="0657 - Half VAT Amounts");
          break;

        case 658:

          // typedef //struct {
          addIsoField("PDS_0658_01", 12, "01 - Call Connect Amount");
          addIsoField("PDS_0658_02", 1, "02 - Call Connect Amount Exponent");
          addIsoField("PDS_0658_03", 1, "03 - Call Connect Amount Sign");
          // } //PDS_0658_STR;

          // PDS_0658_STR //PDS_0658 <name="0658 - Call Connect Amount");
          break;

        case 659:
          addIsoField("PDS_0659", dummyLen, "0659 - Other Description");
          break;
        case 660:

          // typedef //struct {
          // addIsoField("PDS_0660_01", 2, "01 - Customer Reference Value ID");
          // addIsoField("PDS_0660_02", 17, "02 - Customer Reference Value Detail");
          // } //PDS_0660_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 19) {
            addIsoField("PDS_0660_01", 2, "01 - Customer Reference Value ID");
            addIsoField("PDS_0660_02", 17, "02 - Customer Reference Value Detail");
            // PDS_0660_STR //PDS_0660 <name="Customer Reference");
            dummyLen -= 19;
          }
          // } //PDS_0660 (dummyLen) <name="0660 - Customer Reference");

          break;

        case 661:
          addIsoField("PDS_0661", dummyLen, "0661 - Traffic Code");
          break;
        case 662:
          addIsoField("PDS_0662", dummyLen, "0662 - Sample Number");
          break;
        case 663:
          addIsoField("PDS_0663", dummyLen, "0663 - Free-Form Description");
          break;
        case 664:
          addIsoField("PDS_0664", dummyLen, "0664 - Start Station");
          break;
        case 665:
          addIsoField("PDS_0665", dummyLen, "0665 - Destination Station");
          break;
        case 667:

          // typedef //struct {
          addIsoField("PDS_0667_01", 3, "01 - Generic Code");
          addIsoField("PDS_0667_02", 20, "02 - Generic Number");
          addIsoField("PDS_0667_03", 3, "03 - Generic Other Code");
          addIsoField("PDS_0667_04", 20, "04 - Generic Other Number");
          // } //PDS_0667_STR;

          // PDS_0667_STR //PDS_0667 <name="0667 - Generic Data");
          break;

        case 668:

          // typedef //struct {
          addIsoField("PDS_0668_01", 3, "01 - Reduction Code");
          addIsoField("PDS_0668_02", 10, "02 - Reduction Number");
          addIsoField("PDS_0668_03", 3, "03 - Reduction Other Code");
          addIsoField("PDS_0668_04", 10, "04 - Reduction Other Number");
          // } //PDS_0668_STR;

          // PDS_0668_STR //PDS_0668 <name="0668 - Reduction Data");

          break;

        case 669:
          addIsoField("PDS_0669", dummyLen, "0669 - Transportation Other Code");
          break;
        case 670:

          // typedef //struct {

          addIsoField("PDS_0670_01", 25, "01 - Payer Name/User ID");
          addIsoField("PDS_0670_02", 30, "02 - Payer Address");
          addIsoField("PDS_0670_03", 25, "03 - Payer City");
          addIsoField("PDS_0670_04", 3, "04 - Payer State/Province Code");
          addIsoField("PDS_0670_05", 3, "05 - Payer Country Code");
          addIsoField("PDS_0670_06", 10, "06 - Payer Postal Code");
          // addIsoField("PDS_0670_07", 8, "07 - Payer Date of Birth");
          // } //PDS_0670_STR;

          // PDS_0670_STR //PDS_0670 <name="0670 - Payer Name or User ID");
          break;

        case 671:
          addIsoField("PDS_0671", dummyLen, "0671 - Date of Funds Requested");
          break;
        case 672:
          addIsoField("PDS_0672", dummyLen, "0672 - Recipient Name (Seller)");
          break;
        case 673:
          addIsoField("PDS_0673", dummyLen, "0673 - Date of Anticipated Receipt of Funds");
          break;
        case 674:
          addIsoField("PDS_0674", dummyLen, "0674 - Additional Trace/Reference Number Used by Card Acceptor");
          break;
        case 675:
          addIsoField("PDS_0675", dummyLen, "0675 - Additional Transaction Description Data");
          break;
        case 676:
          addIsoField("PDS_0676", dummyLen, "0676 - Card Acceptor VAT Number");
          break;
        case 677:
          addIsoField("PDS_0677", dummyLen, "0677 - Customer VAT Number");
          break;
        case 678:
          addIsoField("PDS_0678", dummyLen, "0678 - Unique Invoice Number");
          break;
        case 679:
          addIsoField("PDS_0679", dummyLen, "0679 - Commodity Code");
          break;
        case 680:
          addIsoField("PDS_0680", dummyLen, "0680 - Authorized Contact Name");
          break;
        case 681:
          addIsoField("PDS_0681", dummyLen, "0681 - Authorized Contact Phone");
          break;
        case 682:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0682_01", 1, "01 - Detail Tax Amount Indicator 1");
          addIsoField("PDS_0682_02", 12, "02 - Detail Tax Amount 1");
          if (dummyLen > 13) {
            addIsoField("PDS_0682_03", 5, "03 - Detail Tax Rate 1");
            if (dummyLen > 18) {
              addIsoField("PDS_0682_04", 1, "04 - Detail Tax Rate Exponent 1");
              if (dummyLen > 19) {
                addIsoField("PDS_0682_05", 4, "05 - Detail Tax Type Applied 1");
                if (dummyLen > 23) {
                  addIsoField("PDS_0682_06", 2, "06 - Detail Tax Type Identifier 1");
                  if (dummyLen > 25) {
                    addIsoField("PDS_0682_07", 20, "07 - Card Acceptor Tax ID 1");
                    if (dummyLen > 45) {
                      addIsoField("PDS_0682_08", 1, "08 - Detail Tax Amount Sign 1");
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0682_STR;

          // PDS_0682_STR //PDS_0682 (dummyLen) <name="0682 - Detail Tax Amount 1");
          break;

        case 683:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0683_01", 1, "01 - Detail Tax Amount Indicator 2");
          addIsoField("PDS_0683_02", 12, "02 - Detail Tax Amount 2");
          if (dummyLen > 13) {
            addIsoField("PDS_0683_03", 5, "03 - Detail Tax Rate 2");
            if (dummyLen > 18) {
              addIsoField("PDS_0683_04", 1, "04 - Detail Tax Rate Exponent 2");
              if (dummyLen > 19) {
                addIsoField("PDS_0683_05", 4, "05 - Detail Tax Type Applied 2");
                if (dummyLen > 23) {
                  addIsoField("PDS_0683_06", 2, "06 - Detail Tax Type Identifier 2");
                  if (dummyLen > 25) {
                    addIsoField("PDS_0683_07", 20, "07 - Card Acceptor Tax ID 2");
                    if (dummyLen > 45) {
                      addIsoField("PDS_0683_08", 1, "08 - Detail Tax Amount Sign 2");
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0683_STR;

          // PDS_0683_STR //PDS_0683 (dummyLen) <name="0683 - Detail Tax Amount 2");
          break;

        case 684:

          // typedef //struct (int dummyLen) {
          addIsoField("PDS_0684_01", 1, "01 - Detail Tax Amount Indicator 3");
          addIsoField("PDS_0684_02", 12, "02 - Detail Tax Amount 3");
          if (dummyLen > 13) {
            addIsoField("PDS_0684_03", 5, "03 - Detail Tax Rate 3");
            if (dummyLen > 18) {
              addIsoField("PDS_0684_04", 1, "04 - Detail Tax Rate Exponent 3");
              if (dummyLen > 19) {
                addIsoField("PDS_0684_05", 4, "05 - Detail Tax Type Applied 3");
                if (dummyLen > 23) {
                  addIsoField("PDS_0684_06", 2, "06 - Detail Tax Type Identifier 3");
                  if (dummyLen > 25) {
                    addIsoField("PDS_0684_07", 20, "07 - Card Acceptor Tax ID 3");
                    if (dummyLen > 45) {
                      addIsoField("PDS_0684_08", 1, "08 - Detail Tax Amount Sign 3");
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0684_STR;

          // PDS_0684_STR //PDS_0684 (dummyLen) <name="0684 - Detail Tax Amount 3");
          break;

        case 685:
          addIsoField("PDS_0685", dummyLen, "0685 - Type of Supply");
          break;
        case 686:
          addIsoField("PDS_0686", dummyLen, "0686 - Tax Exempt Indicator");
          break;
        case 687:
          addIsoField("PDS_0687", dummyLen, "0687 - Unique VAT Invoice Reference Number");
          break;
        case 689:
          addIsoField("PDS_0689", dummyLen, "0689 - Corporate Identifier");
          break;
        case 690:
          addIsoField("PDS_0690", dummyLen, "0690 - No Show Indicator");
          break;
        case 691:
          addIsoField("PDS_0691", dummyLen, "0691 - Days Rented");
          break;
        case 692:
          addIsoField("PDS_0692", dummyLen, "0692 - Weekly Rental Amount");
          break;
        case 693:

          // typedef //struct {
          addIsoField("PDS_0693_01", 12, "01 - Total Authorized Amount");
          addIsoField("PDS_0693_02", 1, "02 - Total Authorized Amount Exponent");
          addIsoField("PDS_0693_03", 1, "03 - Total Authorized Amount Sign");
          // } //PDS_0693_STR;

          // PDS_0693_STR //PDS_0693 <name="0693 - Total Authorized Amount");
          break;

        case 694:
          addIsoField("PDS_0694", dummyLen, "0694 - One Way Drop Off Charge");
          break;
        case 695:
          addIsoField("PDS_0695", dummyLen, "0695 - Regular Mileage Charge");
          break;
        case 696:
          addIsoField("PDS_0696", dummyLen, "0696 - Extra Mileage Charge");
          break;
        case 697:
          addIsoField("PDS_0697", dummyLen, "0697 - Late Charge");
          break;
        case 698:
          addIsoField("PDS_0698", dummyLen, "0698 - Fuel Charge");
          break;
        case 699:

          // typedef //struct {
          addIsoField("PDS_0699_01", 12, "01 - Lodging Total Tax Amount");
          addIsoField("PDS_0699_02", 1, "02 - Lodging Total Tax Amount Exponent");
          addIsoField("PDS_0699_03", 1, "03 - Lodging Total Tax Amount Sign");
          // } //PDS_0699_STR;

          // PDS_0699_STR //PDS_0699 <name="0699 - Lodging Total Tax Amount");
          break;

        case 700:
          addIsoField("PDS_0700", dummyLen, "0700 - Towing Charges");
          break;
        case 701:
          addIsoField("PDS_0701", dummyLen, "0701 - Extra Charges");
          break;
        case 702:
          addIsoField("PDS_0702", dummyLen, "0702 - Other Charges");
          break;
        case 703:
          addIsoField("PDS_0703", dummyLen, "0703 - Total Room Nights");
          break;
        case 704:

          // typedef //struct {
          addIsoField("PDS_0704_01", 12, "01 - Prepaid Expenses Amount");
          addIsoField("PDS_0704_02", 1, "02 - Prepaid Expenses Amount Exponent");
          addIsoField("PDS_0704_03", 1, "03 - Prepaid Expenses Amount Sign");
          // } //PDS_0704_STR;

          // PDS_0704_STR //PDS_0704 <name="0704 - Prepaid Expenses");
          break;

        case 705:

          // typedef //struct {
          addIsoField("PDS_0705_01", 12, "01 - Total Non-Room Tax Amount");
          addIsoField("PDS_0705_02", 1, "02 - Total Non-Room Tax Amount Exponent");
          addIsoField("PDS_0705_03", 1, "03 - Total Non-Room Tax Amount Sign");
          // } //PDS_0705_STR;

          // PDS_0705_STR //PDS_0705 <name="0705 - Total Non-Room Tax Amount");
          break;

        case 706:

          // typedef //struct {
          addIsoField("PDS_0706_01", 12, "01 - Cash Advances Amount");
          addIsoField("PDS_0706_02", 1, "02 - Cash Advances Exponent");
          addIsoField("PDS_0706_03", 1, "03 - Cash Advances Sign");
          // } //PDS_0706_STR;

          // PDS_0706_STR //PDS_0706 <name="0706 - Cash Advances");
          break;

        case 707:

          // typedef //struct {
          addIsoField("PDS_0707_01", 12, "01 - Valet Charges Amount");
          addIsoField("PDS_0707_02", 1, "02 - Valet Charges Exponent");
          addIsoField("PDS_0707_03", 1, "03 - Valet Charges Sign");
          // } //PDS_0707_STR;

          // PDS_0707_STR //PDS_0707 <name="0707 - Valet Charges");
          break;

        case 708:
          // typedef //struct {
          addIsoField("PDS_0708_01", 12, "01 - Movie Charges Amount");
          addIsoField("PDS_0708_02", 1, "02 - Movie Charges Exponent");
          addIsoField("PDS_0708_03", 1, "03 - Movie Charges Sign");
          // } //PDS_0708_STR;

          // PDS_0708_STR //PDS_0708 <name="0708 - Movie Charges");
          break;

        case 709:

          // typedef //struct {
          addIsoField("PDS_0709_01", 12, "01 - Business Center Charges Amount");
          addIsoField("PDS_0709_02", 1, "02 - Business Center Charges Exponent");
          addIsoField("PDS_0709_03", 1, "03 - Business Center Charges Sign");
          // } //PDS_0709_STR;

          // PDS_0709_STR //PDS_0709 <name="0709 - Business Center Charges");
          break;

        case 710:

          // typedef //struct {
          addIsoField("PDS_0710_01", 12, "01 - Health Club Charges Amount");
          addIsoField("PDS_0710_02", 1, "02 - Health Club Charges Exponent");
          addIsoField("PDS_0710_03", 1, "03 - Health Club Charges Sign");
          // } //PDS_0710_STR;

          // PDS_0710_STR //PDS_0710 <name="0710 - Health Club Charges");
          break;

        case 711:
          addIsoField("PDS_0711", dummyLen, "0711 - Fire Safety Act Indicator");
          break;
        case 712:

          // typedef //struct {
          addIsoField("PDS_0712_01", 12, "01 - Net Fuel Price");
          addIsoField("PDS_0712_02", 1, "02 - Net Fuel Price Exponent");
          // } //PDS_0712_STR;

          // PDS_0712_STR //PDS_0712 <name="0712 - Net Fuel Price");
          break;

        case 713:
          addIsoField("PDS_0713", dummyLen, "0713 - Restricted Ticket Indicator");
          break;
        case 714:
          addIsoField("PDS_0714", dummyLen, "0714 - Exchange Ticket Amount");
          break;
        case 715:
          addIsoField("PDS_0715", dummyLen, "0715 - Exchange Fee Amount");
          break;
        case 716:
          addIsoField("PDS_0716", dummyLen, "0716 - Travel Authorization Code");
          break;
        case 717:
          addIsoField("PDS_0717", dummyLen, "0717 - IATA Client Code");
          break;
        case 718:
          addIsoField("PDS_0718", dummyLen, "0718 - Employee/Temp Name/ID");
          break;
        case 719:
          addIsoField("PDS_0719", dummyLen, "0719 - Employee Social Security Number or ID");
          break;
        case 720:
          addIsoField("PDS_0720", dummyLen, "0720 - Job Description");
          break;
        case 721:
          addIsoField("PDS_0721", dummyLen, "0721 - Job Code");
          break;
        case 722:
          addIsoField("PDS_0722", dummyLen, "0722 - Flat Rate Indicator");
          break;
        case 723:

          // typedef //struct {
          addIsoField("PDS_0723_01", 6, "01 - Regular Hours Worked");
          addIsoField("PDS_0723_02", 1, "02 - Regular Hours Worked Exponent");
          // } //PDS_0723_STR;

          // PDS_0723_STR //PDS_0723 <name="0723 - Regular Hours Worked");
          break;

        case 724:

          // typedef //struct {
          addIsoField("PDS_0724_01", 6, "01 - Regular Hours Rate");
          addIsoField("PDS_0724_02", 1, "02 - Regular Hours Rate Exponent");
          // } //PDS_0724_STR;

          // PDS_0724_STR //PDS_0724 <name="0724 - Regular Hours Rate");
          break;

        case 725:

          // typedef //struct {
          addIsoField("PDS_0725_01", 6, "01 - Overtime Hours Worked");
          addIsoField("PDS_0725_02", 1, "02 - Overtime Hours Worked Exponent");
          // } //PDS_0725_STR;

          // PDS_0725_STR //PDS_0725 <name="0725 - Overtime Hours Worked");
          break;

        case 726:

          // typedef //struct {
          addIsoField("PDS_0726_01", 6, "01 - Overtime Hours Rate");
          addIsoField("PDS_0726_02", 1, "02 - Overtime Hours Rate Exponent");
          // } //PDS_0726_STR;

          // PDS_0726_STR //PDS_0726 <name="0726 - Overtime Hours Rate");
          break;

        case 727:
          addIsoField("PDS_0727", dummyLen, "0727 - Temp Start Date");
          break;
        case 728:
          addIsoField("PDS_0728", dummyLen, "0728 - Temp Week Ending");
          break;
        case 729:
          addIsoField("PDS_0729", dummyLen, "0729 - Requestor Name or ID");
          break;
        case 730:
          addIsoField("PDS_0730", dummyLen, "0730 - Supervisor/Reports To");
          break;
        case 731:
          addIsoField("PDS_0731", dummyLen, "0731 - Time Sheet Number");
          break;
        case 732:

          // typedef //struct {
          addIsoField("PDS_0732_01", 12, "01 - Discount Amount");
          addIsoField("PDS_0732_02", 1, "02 - Discount Amount Exponent");
          addIsoField("PDS_0732_03", 1, "03 - Discount Amount Sign");
          addIsoField("PDS_0732_04", 4, "04 - Discount type");
          // } //PDS_0732_STR;

          // PDS_0732_STR //PDS_0732 <name="0732 - Discount Amount");
          break;

        case 733:

          // typedef //struct {
          addIsoField("PDS_0733_01", 12, "01 - Subtotal Amount");
          addIsoField("PDS_0733_02", 1, "02 - Subtotal Amount Exponent");
          addIsoField("PDS_0733_03", 1, "03 - Subtotal Amount Sign");
          // } //PDS_0733_STR;

          // PDS_0733_STR //PDS_0733 <name="0733 - Subtotal Amount");
          break;

        case 734:

          // typedef //struct {
          // addIsoField("PDS_0734_01", 40, "01 - Miscellaneous Expense Description");
          // addIsoField("PDS_0734_02", 12, "02 - Miscellaneous Expense Amount");
          // addIsoField("PDS_0734_03", 1, "03 - Miscellaneous Expenses Amount Exponent");
          // addIsoField("PDS_0734_04", 1, "04 - Miscellaneous Expenses Amount Sign");
          // } //PDS_0734_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 54) {
            addIsoField("PDS_0734_01", 40, "01 - Miscellaneous Expense Description");
            addIsoField("PDS_0734_02", 12, "02 - Miscellaneous Expense Amount");
            addIsoField("PDS_0734_03", 1, "03 - Miscellaneous Expenses Amount Exponent");
            addIsoField("PDS_0734_04", 1, "04 - Miscellaneous Expenses Amount Sign");
            // PDS_0734_STR //PDS_0734 <name="Miscellaneous Expenses");
            dummyLen -= 54;
          }
          // } //PDS_0734 (dummyLen) <name="0734 - Miscellaneous Expenses");

          break;

        case 735:
          addIsoField("PDS_0735", dummyLen, "0735 - Service Descriptor Codes");
          break;
        case 736:
          addIsoField("PDS_0736", dummyLen, "0736 - Tracking Number or Pickup Number");
          break;
        case 737:

          // typedef //struct {
          addIsoField("PDS_0737_01", 12, "01 - Shipping Net Amount");
          addIsoField("PDS_0737_02", 1, "02 - Shipping Net Amount Exponent");
          addIsoField("PDS_0737_03", 1, "03 - Shipping Net Amount Sign");
          // } //PDS_0737_STR;

          // PDS_0737_STR //PDS_0737 <name="0737 - Shipping Net Amount");
          break;

        case 738:

          // typedef //struct {
          addIsoField("PDS_0738_01", 12, "01 - Incentive Amount");
          addIsoField("PDS_0738_02", 1, "02 - Incentive Amount Exponent");
          addIsoField("PDS_0738_03", 1, "03 - Incentive Amount Sign");
          // } //PDS_0738_STR;

          // PDS_0738_STR //PDS_0738 <name="0738 - Incentive Amount");
          break;

        case 739:
          addIsoField("PDS_0739", dummyLen, "0739 - Pickup Date");
          break;
        case 740:
          addIsoField("PDS_0740", dummyLen, "0740 - Delivery Date");
          break;
        case 741:
          addIsoField("PDS_0741", dummyLen, "0741 - Number of Packages");
          break;
        case 742:
          addIsoField("PDS_0742", dummyLen, "0742 - Package Weight");
          break;
        case 743:
          addIsoField("PDS_0743", dummyLen, "0743 - Unit of Measure");
          break;

        case 744:
          // struct (int dummyLen) {
          while (dummyLen >= 50) {
            addIsoField("PDS_0744", dummyLen, "Shipping Party Information");
            dummyLen -= 50;
          }
          // } //PDS_0744 (dummyLen) <name="0744 - Shipping Party Information");
          break;

        case 745:
          // struct (int dummyLen) {
          while (dummyLen >= 50) {
            addIsoField("PDS_0745", dummyLen, "Shipping Party Address");
            dummyLen -= 50;
          }
          // } //PDS_0745 (dummyLen) <name="0745 - Shipping Party Address");
          break;

        case 746:

          // typedef //struct {
          addIsoField("PDS_0746_01", 25, "01 - Shipping Party City");
          if (dummyLen > 25) {
            addIsoField("PDS_0746_02", 3, "02 - Shipping Party State/Province Code");
            if (dummyLen > 28) {
              addIsoField("PDS_0746_03", 3, "03 - Shipping Party Country Code");
              if (dummyLen > 31) {
                addIsoField("PDS_0746_04", 10, "04 - Shipping Party Postal Code");
              }
            }
          }
          // } //PDS_0746_STR;

          // PDS_0746_STR //PDS_0746 <name="0746 - Shipping Party Postal Information");
          break;

        case 747:

          // typedef //struct {
          // addIsoField("PDS_0747_01", 1, "01 - Shipping Party Descriptor");
          // addIsoField("PDS_0747_02", 60, "02 - Shipping Party Contact Information");
          // } //PDS_0747_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 61) {
            addIsoField("PDS_0747_01", 1, "01 - Shipping Party Descriptor");
            addIsoField("PDS_0747_02", 60, "02 - Shipping Party Contact Information");
            // PDS_0747_STR //PDS_0747 <name="Shipping Party Contact");
            dummyLen -= 61;
          }
          // } //PDS_0747 (dummyLen) <name="0747 - Shipping Party Contact");

          break;

        case 748:

          // struct (int dummyLen) {
          while (dummyLen >= 50) {
            addIsoField("PDS_0748", dummyLen, "Delivery Party Information");
            dummyLen -= 50;
          }
          // } //PDS_0748 (dummyLen) <name="0748 - Delivery Party Information");

          break;

        case 749:

          // struct (int dummyLen) {
          while (dummyLen >= 50) {
            addIsoField("PDS_0749", dummyLen, "Delivery Party Address Information");
            dummyLen -= 50;
          }
          // } //PDS_0749 (dummyLen) <name="0749 - Delivery Party Address Information");
          break;

        case 750:
          // //typedef //struct {
          addIsoField("PDS_0750_01", 25, "01 - Delivery Party City");
          if (dummyLen > 25) {
            addIsoField("PDS_0750_02", 3, "02 - Delivery Party State/Province Code");
            if (dummyLen > 28) {
              addIsoField("PDS_0750_03", 3, "03 - Delivery Party Country Code");
              if (dummyLen > 31) {
                addIsoField("PDS_0750_04", 10, "04 - Delivery Party Postal Code");
              }
            }
          }
          // //} //PDS_0750_STR;

          // //PDS_0750_STR //PDS_0750 <name="0750 - Delivery Party Postal Information");
          break;

        case 751:

          // //typedef //struct {
          addIsoField("PDS_0751_01", 1, "01 - Detail Tax Amount Indicator 4");
          if (dummyLen > 1) {
            addIsoField("PDS_0751_02", 12, "02 - Detail Tax Amount 4");
            if (dummyLen > 13) {
              addIsoField("PDS_0751_03", 5, "03 - Detail Tax Rate 4");
              if (dummyLen > 18) {
                addIsoField("PDS_0751_04", 1, "04 - Detail Tax Rate Exponent 4");
                if (dummyLen > 19) {
                  addIsoField("PDS_0751_05", 4, "05 - Detail Tax Type Applied 4");
                  if (dummyLen > 23) {
                    addIsoField("PDS_0751_06", 2, "06 - Detail Tax Type Identifier 4");
                    if (dummyLen > 25) {
                      addIsoField("PDS_0751_07", 20, "07 - Card Acceptor Tax ID 4");
                      if (dummyLen > 45) {
                        addIsoField("PDS_0751_08", 1, "08 - Detail Tax Amount Sign 4");
                      }
                    }
                  }
                }
              }
            }
          }
          // //} //PDS_0751_STR;

          // //PDS_0751_STR //PDS_0751 <name="0751 - Detail Tax Amount 4");
          break;

        case 752:

          // typedef //struct {
          addIsoField("PDS_0752_01", 1, "01 - Detail Tax Amount Indicator 5");
          if (dummyLen > 1) {
            addIsoField("PDS_0752_02", 12, "02 - Detail Tax Amount 5");
            if (dummyLen > 13) {
              addIsoField("PDS_0752_03", 5, "03 - Detail Tax Rate 5");
              if (dummyLen > 18) {
                addIsoField("PDS_0752_04", 1, "04 - Detail Tax Rate Exponent 5");
                if (dummyLen > 19) {
                  addIsoField("PDS_0752_05", 4, "05 - Detail Tax Type Applied 5");
                  if (dummyLen > 23) {
                    addIsoField("PDS_0752_06", 2, "06 - Detail Tax Type Identifier 5");
                    if (dummyLen > 25) {
                      addIsoField("PDS_0752_07", 20, "07 - Card Acceptor Tax ID 5");
                      if (dummyLen > 45) {
                        addIsoField("PDS_0752_08", 1, "08 - Detail Tax Amount Sign 5");
                      }
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0752_STR;

          // PDS_0752_STR //PDS_0752 <name="0752 - Detail Tax Amount 5");
          break;

        case 753:

          // typedef //struct {
          addIsoField("PDS_0753_01", 1, "01 - Detail Tax Amount Indicator 6");
          if (dummyLen > 1) {
            addIsoField("PDS_0753_02", 12, "02 - Detail Tax Amount 6");
            if (dummyLen > 13) {
              addIsoField("PDS_0753_03", 5, "03 - Detail Tax Rate 6");
              if (dummyLen > 18) {
                addIsoField("PDS_0753_04", 1, "04 - Detail Tax Rate Exponent 6");
                if (dummyLen > 19) {
                  addIsoField("PDS_0753_05", 4, "05 - Detail Tax Type Applied 6");
                  if (dummyLen > 23) {
                    addIsoField("PDS_0753_06", 2, "06 - Detail Tax Type Identifier 6");
                    if (dummyLen > 25) {
                      addIsoField("PDS_0753_07", 20, "07 - Card Acceptor Tax ID 6");
                      if (dummyLen > 45) {
                        addIsoField("PDS_0753_08", 1, "08 - Detail Tax Amount Sign 6");
                      }
                    }
                  }
                }
              }
            }
          }
          // } //PDS_0753_STR;

          // PDS_0753_STR //PDS_0753 <name="0753 - Detail Tax Amount 6");
          break;

        case 754:

          // typedef //struct {
          // addIsoField("PDS_0754_01", 1, "01 - Delivery Party Descriptor");
          // addIsoField("PDS_0754_02", 60, "02 - Delivery Party Contact Information");
          // } //PDS_0754_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 61) {
            addIsoField("PDS_0754_01", 1, "01 - Delivery Party Descriptor");
            addIsoField("PDS_0754_02", 60, "02 - Delivery Party Contact Information");
            // PDS_0754_STR //PDS_0754 <name="Delivery Party Contact");
            dummyLen -= 61;
          }
          // } //PDS_0754 (dummyLen) <name="0754 - Delivery Party Contact");

          break;

        case 755:

          // typedef //struct {
          addIsoField("PDS_0755_01", 12, "01 - Line Item Total Amount");
          addIsoField("PDS_0755_02", 1, "02 - Line Item Total Amount Exponent");
          addIsoField("PDS_0755_03", 1, "03 - Line Item Total Amount Sign");
          // } //PDS_0755_STR;

          // PDS_0755_STR //PDS_0755 <name="0755 - Line Item Total Amount");
          break;

        case 756:

          // typedef //struct {
          addIsoField("PDS_0756_01", 12, "01 - Number of Adults");
          addIsoField("PDS_0756_02", 1, "02 - Number of Children");
          addIsoField("PDS_0756_03", 1, "03 - Class");
          // } //PDS_0756_STR;

          // PDS_0756_STR //PDS_0756 <name="0756 - Passenger Description");
          break;

        case 757:
          addIsoField("PDS_0757", dummyLen, "0757 - Transportation Service Provider");
          break;
        case 758:
          addIsoField("PDS_0758", dummyLen, "0758 - Transportation Services Offered");
          break;
        case 759:
          addIsoField("PDS_0759", dummyLen, "0759 - Delivery Order Number");
          break;
        case 760:
          addIsoField("PDS_0760", dummyLen, "0760 - Credit Card Slip Number");
          break;
        case 761:
          addIsoField("PDS_0761", dummyLen, "0761 - Travel Agency ID");
          break;
        case 762:
          addIsoField("PDS_0762", dummyLen, "0762 - Data Source");
          break;
        case 763:
          addIsoField("PDS_0763", dummyLen, "0763 - VAT Suppression Indicator");
          break;
        case 764:
          addIsoField("PDS_0764", dummyLen, "0764 - Healthcare Eligible Status Indicator");
          break;

        case 770:
          // typedef //struct {
          addIsoField("PDS_0770_01", 25, "01 - Sales Rep Name");
          if (dummyLen > 25) {
            addIsoField("PDS_0770_02", 16, "02 - Sales Rep Phone Number");
            if (dummyLen > 41) {
              addIsoField("PDS_0770_03", 16, "03 - Sales Rep Fax Number");
              if (dummyLen > 57) {
                addIsoField("PDS_0770_04", 16, "04 - Technical Support Phone Number");
              }
            }
          }
          // } //PDS_0770_STR;

          // PDS_0770_STR //PDS_0770 <name="0770 - Additional Card Acceptor Inquiry
          // Information");
          break;

        case 771:

          // typedef //struct {
          addIsoField("PDS_0771_01", 18, "01 - Cost Center Number");
          if (dummyLen > 18) {
            addIsoField("PDS_0771_02", 18, "02 - Cost Center Description");
          }
          // } //PDS_0771_STR;

          // PDS_0771_STR //PDS_0771 <name="0771 - Cost Center Information");
          break;

        case 772:

          // typedef //struct {
          addIsoField("PDS_0772_01", 15, "01 - Driver License/State Issued ID Number");
          if (dummyLen > 15) {
            addIsoField("PDS_0772_02", 3, "02 - Driver License/State Issued ID State, Province, or Region Code");
          }
          // } //PDS_0772_STR;

          // PDS_0772_STR //PDS_0772 <name="0772 - Driver License/State Issued ID
          // Information");
          break;

        case 773:

          // typedef //struct {
          // addIsoField("PDS_0773_01", 15, "01 - Driver License/State Issued ID Number");
          // addIsoField("PDS_0773_02", 3, "02 - Driver License/State Issued ID State,
          // Province, or Region Code");
          // } //PDS_0773_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 14) {
            addIsoField("PDS_0773_01", 15, "01 - Driver License/State Issued ID Number");
            addIsoField("PDS_0773_02", 3, "02 - Driver License/State Issued ID State, Province, or Region Code");
            // PDS_0754_STR //PDS_0754 <name="Payment Method Information");
            dummyLen -= 14;
          }
          // } //PDS_0773 (dummyLen) <name="0773 - Payment Method Information");
          break;

        case 774:
          addIsoField("PDS_0774", dummyLen, "0774 - Stock Keeping Unit (SKU) Description");
          break;
        case 775:
          addIsoField("PDS_0775", dummyLen, "0775 - Department Code");
          break;
        case 776:

          // typedef //struct {
          // addIsoField("PDS_0776_01", 6, "01 - Alternate Custom Identifier Type");
          // addIsoField("PDS_0776_02", 76, "02 - Alternate Custom Identifier Detail");
          // } //PDS_0776_STR;

          // struct (int dummyLen) {
          while (dummyLen >= 82) {
            addIsoField("PDS_0776_01", 6, "01 - Alternate Custom Identifier Type");
            addIsoField("PDS_0776_02", 76, "02 - Alternate Custom Identifier Detail");
            // PDS_0776_STR //PDS_0776 <name="Alternate Custom Identifier");
            dummyLen -= 82;
          }
          // } //PDS_0776 (dummyLen) <name="0776 - Alternate Custom Identifier");

          break;

        case 777:
          addIsoField("PDS_0777", dummyLen, "0777 - Promotion Code");
          break;
        case 799:

          // //typedef //struct {
          // addIsoField("PDS_0799_01", 8, "01 - Auxiliary Field 1");
          // addIsoField("PDS_0799_02", 25, "02 - Auxiliary Field 2");
          // //} //PDS_0799_STR;

          // //struct (int dummyLen) {
          while (dummyLen >= 33) {
            addIsoField("PDS_0799_01", 8, "01 - Auxiliary Field 1");
            addIsoField("PDS_0799_02", 25, "02 - Auxiliary Field 2");
            // PDS_0799_STR //PDS_0799 <name="Test Case Traceability Identifiers");
            dummyLen -= 33;
          }
          // //} //PDS_0799 (dummyLen) <name="0799 - Test Case Traceability Identifiers");
          break;

        default:

          if (PDSNo >= 1000 && PDSNo <= 1099) {
            // typedef //struct (int dummyLen, int PDSNo) {
            int locPDSNo = PDSNo;
            addIsoField("PDS_1000", dummyLen, "1000-1099 - Member-to-Member Proprietary Data");
            // } //PDS_1000_STR;
            // PDS_1000_STR //PDS_1000 (dummyLen, PDSNo) <open=suppress, name=getPDS1000No>;

          } else {

            // typedef //struct (int dummyLen, int PDSNo) {
            // int locPDSNo = PDSNo;
            // addIsoField("PDS_XX", dummyLen];
            // } unknownSTR;
            // unknownSTR unknown (dummyLen, PDSNo) <open=suppress, name=getUnkPDSNo>;
          }
      }
    }
  }

  void DeleteAddendum() {
    int k = 0;
    int j;
    String status;
    while (exists(ThisMsg, k)) {
      k++;
    }

    for (j = k - 1; j >= 0; j--) {
      status = ConvertString(ThisMsg[j].DE_24, CHARSET_EBCDIC, CHARSET_ASCII);
      if (!Utils.isContainsInArray(new String[] { "200", "695", "450", "451", "453", "454", "697" }, status)) {
        DeleteBytes(startof(ThisMsg[j]) - 4, sizeof(ThisMsg[j]) + 4); // delete whole msg
      }
    }
  }

  int FindPos(int field, int k) {

    switch (field) {
      case 48:
        if (exists(ThisMsg[k].DE_49))
          return startof(ThisMsg[k].DE_49);
        else {
          Printf("Error: Could not find DE_49. Could not add additional data (DE_48) for Message[%d].\n", k);
          Exit(0);
        }
      case 100:
        if (exists(ThisMsg[k].DE_99))
          return startof(ThisMsg[k].DE_99) + sizeof(ThisMsg[k].DE_99);
        if (exists(ThisMsg[k].DE_98))
          return startof(ThisMsg[k].DE_98) + sizeof(ThisMsg[k].DE_98);
        if (exists(ThisMsg[k].DE_97))
          return startof(ThisMsg[k].DE_97) + sizeof(ThisMsg[k].DE_97);
        if (exists(ThisMsg[k].DE_96))
          return startof(ThisMsg[k].DE_96) + sizeof(ThisMsg[k].DE_96);
        if (exists(ThisMsg[k].DE_95))
          return startof(ThisMsg[k].DE_95) + sizeof(ThisMsg[k].DE_95);
        if (exists(ThisMsg[k].DE_94))
          return startof(ThisMsg[k].DE_94) + sizeof(ThisMsg[k].DE_94);
        if (exists(ThisMsg[k].DE_93))
          return startof(ThisMsg[k].DE_93) + sizeof(ThisMsg[k].DE_93);
      case 93:
        if (exists(ThisMsg[k].DE_92))
          return startof(ThisMsg[k].DE_92) + sizeof(ThisMsg[k].DE_92);
        if (exists(ThisMsg[k].DE_91))
          return startof(ThisMsg[k].DE_91) + sizeof(ThisMsg[k].DE_91);
        if (exists(ThisMsg[k].DE_90))
          return startof(ThisMsg[k].DE_90) + sizeof(ThisMsg[k].DE_90);
        if (exists(ThisMsg[k].DE_89))
          return startof(ThisMsg[k].DE_89) + sizeof(ThisMsg[k].DE_89);
        if (exists(ThisMsg[k].DE_88))
          return startof(ThisMsg[k].DE_88) + sizeof(ThisMsg[k].DE_88);
        if (exists(ThisMsg[k].DE_87))
          return startof(ThisMsg[k].DE_87) + sizeof(ThisMsg[k].DE_87);
        if (exists(ThisMsg[k].DE_86))
          return startof(ThisMsg[k].DE_86) + sizeof(ThisMsg[k].DE_86);
        if (exists(ThisMsg[k].DE_85))
          return startof(ThisMsg[k].DE_85) + sizeof(ThisMsg[k].DE_85);
        if (exists(ThisMsg[k].DE_84))
          return startof(ThisMsg[k].DE_84) + sizeof(ThisMsg[k].DE_84);
        if (exists(ThisMsg[k].DE_83))
          return startof(ThisMsg[k].DE_83) + sizeof(ThisMsg[k].DE_83);
        if (exists(ThisMsg[k].DE_82))
          return startof(ThisMsg[k].DE_82) + sizeof(ThisMsg[k].DE_82);
        if (exists(ThisMsg[k].DE_81))
          return startof(ThisMsg[k].DE_81) + sizeof(ThisMsg[k].DE_81);
        if (exists(ThisMsg[k].DE_80))
          return startof(ThisMsg[k].DE_80) + sizeof(ThisMsg[k].DE_80);
        if (exists(ThisMsg[k].DE_79))
          return startof(ThisMsg[k].DE_79) + sizeof(ThisMsg[k].DE_79);
        if (exists(ThisMsg[k].DE_78))
          return startof(ThisMsg[k].DE_78) + sizeof(ThisMsg[k].DE_78);
        if (exists(ThisMsg[k].DE_77))
          return startof(ThisMsg[k].DE_77) + sizeof(ThisMsg[k].DE_77);
        if (exists(ThisMsg[k].DE_76))
          return startof(ThisMsg[k].DE_76) + sizeof(ThisMsg[k].DE_76);
        if (exists(ThisMsg[k].DE_75))
          return startof(ThisMsg[k].DE_75) + sizeof(ThisMsg[k].DE_75);
        if (exists(ThisMsg[k].DE_74))
          return startof(ThisMsg[k].DE_74) + sizeof(ThisMsg[k].DE_74);
        if (exists(ThisMsg[k].DE_73))
          return startof(ThisMsg[k].DE_73) + sizeof(ThisMsg[k].DE_73);
        if (exists(ThisMsg[k].DE_72))
          return startof(ThisMsg[k].DE_72) + sizeof(ThisMsg[k].DE_72);
        if (exists(ThisMsg[k].DE_71))
          return startof(ThisMsg[k].DE_71) + sizeof(ThisMsg[k].DE_71);
        if (exists(ThisMsg[k].DE_70))
          return startof(ThisMsg[k].DE_70) + sizeof(ThisMsg[k].DE_70);
        if (exists(ThisMsg[k].DE_69))
          return startof(ThisMsg[k].DE_69) + sizeof(ThisMsg[k].DE_69);
        if (exists(ThisMsg[k].DE_68))
          return startof(ThisMsg[k].DE_68) + sizeof(ThisMsg[k].DE_68);
        if (exists(ThisMsg[k].DE_67))
          return startof(ThisMsg[k].DE_67) + sizeof(ThisMsg[k].DE_67);
        if (exists(ThisMsg[k].DE_66))
          return startof(ThisMsg[k].DE_66) + sizeof(ThisMsg[k].DE_66);
        if (exists(ThisMsg[k].DE_65))
          return startof(ThisMsg[k].DE_65) + sizeof(ThisMsg[k].DE_65);
        if (exists(ThisMsg[k].DE_64))
          return startof(ThisMsg[k].DE_64) + sizeof(ThisMsg[k].DE_64);

        if (exists(ThisMsg[k].DE_63))
          return startof(ThisMsg[k].DE_63) + sizeof(ThisMsg[k].DE_63);
        if (exists(ThisMsg[k].DE_62))
          return startof(ThisMsg[k].DE_62) + sizeof(ThisMsg[k].DE_62);
        if (exists(ThisMsg[k].DE_61))
          return startof(ThisMsg[k].DE_61) + sizeof(ThisMsg[k].DE_61);
        if (exists(ThisMsg[k].DE_60))
          return startof(ThisMsg[k].DE_60) + sizeof(ThisMsg[k].DE_60);
        if (exists(ThisMsg[k].DE_59))
          return startof(ThisMsg[k].DE_59) + sizeof(ThisMsg[k].DE_59);
        if (exists(ThisMsg[k].DE_58))
          return startof(ThisMsg[k].DE_58) + sizeof(ThisMsg[k].DE_58);
        if (exists(ThisMsg[k].DE_57))
          return startof(ThisMsg[k].DE_57) + sizeof(ThisMsg[k].DE_57);
        if (exists(ThisMsg[k].DE_56))
          return startof(ThisMsg[k].DE_56) + sizeof(ThisMsg[k].DE_56);
        if (exists(ThisMsg[k].DE_56))
          return startof(ThisMsg[k].DE_56) + sizeof(ThisMsg[k].DE_56);
        if (exists(ThisMsg[k].DE_55))
          return startof(ThisMsg[k].DE_55) + sizeof(ThisMsg[k].DE_55);
        if (exists(ThisMsg[k].DE_54))
          return startof(ThisMsg[k].DE_54) + sizeof(ThisMsg[k].DE_54);
        if (exists(ThisMsg[k].DE_53))
          return startof(ThisMsg[k].DE_53) + sizeof(ThisMsg[k].DE_53);
        if (exists(ThisMsg[k].DE_52))
          return startof(ThisMsg[k].DE_52) + sizeof(ThisMsg[k].DE_52);
        if (exists(ThisMsg[k].DE_51))
          return startof(ThisMsg[k].DE_51) + sizeof(ThisMsg[k].DE_51);
        if (exists(ThisMsg[k].DE_50))
          return startof(ThisMsg[k].DE_50) + sizeof(ThisMsg[k].DE_50);

      case 50:
        if (exists(ThisMsg[k].DE_49))
          return startof(ThisMsg[k].DE_49) + sizeof(ThisMsg[k].DE_49);
        if (exists(ThisMsg[k].PDS))
          return startof(ThisMsg[k].PDS) + sizeof(ThisMsg[k].PDS);
        if (exists(ThisMsg[k].DE_47))
          return startof(ThisMsg[k].DE_47) + sizeof(ThisMsg[k].DE_47) + 3;
        if (exists(ThisMsg[k].DE_45))
          return startof(ThisMsg[k].DE_45) + sizeof(ThisMsg[k].DE_45);
        if (exists(ThisMsg[k].DE_45))
          return startof(ThisMsg[k].DE_45) + sizeof(ThisMsg[k].DE_45);
        if (exists(ThisMsg[k].DE_44))
          return startof(ThisMsg[k].DE_44) + sizeof(ThisMsg[k].DE_44);
        if (exists(ThisMsg[k].DE_43))
          return startof(ThisMsg[k].DE_43) + sizeof(ThisMsg[k].DE_43);
        if (exists(ThisMsg[k].DE_42))
          return startof(ThisMsg[k].DE_42) + sizeof(ThisMsg[k].DE_42);
        if (exists(ThisMsg[k].DE_41))
          return startof(ThisMsg[k].DE_41) + sizeof(ThisMsg[k].DE_41);
        if (exists(ThisMsg[k].DE_40))
          return startof(ThisMsg[k].DE_40) + sizeof(ThisMsg[k].DE_40);
        if (exists(ThisMsg[k].DE_39))
          return startof(ThisMsg[k].DE_39) + sizeof(ThisMsg[k].DE_39);
        if (exists(ThisMsg[k].DE_38))
          return startof(ThisMsg[k].DE_38) + sizeof(ThisMsg[k].DE_38);
        if (exists(ThisMsg[k].DE_37))
          return startof(ThisMsg[k].DE_37) + sizeof(ThisMsg[k].DE_37);
      case 37:
        if (exists(ThisMsg[k].DE_36))
          return startof(ThisMsg[k].DE_36) + sizeof(ThisMsg[k].DE_36);
        if (exists(ThisMsg[k].DE_35))
          return startof(ThisMsg[k].DE_35) + sizeof(ThisMsg[k].DE_35);
        if (exists(ThisMsg[k].DE_34))
          return startof(ThisMsg[k].DE_34) + sizeof(ThisMsg[k].DE_34);
        if (exists(ThisMsg[k].DE_33))
          return startof(ThisMsg[k].DE_33) + sizeof(ThisMsg[k].DE_33);
        if (exists(ThisMsg[k].DE_32))
          return startof(ThisMsg[k].DE_32) + sizeof(ThisMsg[k].DE_32);
        if (exists(ThisMsg[k].DE_31))
          return startof(ThisMsg[k].DE_31) + sizeof(ThisMsg[k].DE_31);
        if (exists(ThisMsg[k].DE_30))
          return startof(ThisMsg[k].DE_30) + sizeof(ThisMsg[k].DE_30);
      case 30:
        if (exists(ThisMsg[k].DE_29))
          return startof(ThisMsg[k].DE_29) + sizeof(ThisMsg[k].DE_29);
        if (exists(ThisMsg[k].DE_28))
          return startof(ThisMsg[k].DE_28) + sizeof(ThisMsg[k].DE_28);
        if (exists(ThisMsg[k].DE_27))
          return startof(ThisMsg[k].DE_27) + sizeof(ThisMsg[k].DE_27);
        if (exists(ThisMsg[k].DE_26))
          return startof(ThisMsg[k].DE_26) + sizeof(ThisMsg[k].DE_26);
        if (exists(ThisMsg[k].DE_25))
          return startof(ThisMsg[k].DE_25) + sizeof(ThisMsg[k].DE_25);
      case 25:
        if (exists(ThisMsg[k].DE_24))
          return startof(ThisMsg[k].DE_24) + sizeof(ThisMsg[k].DE_24);
      case 24:
        if (exists(ThisMsg[k].DE_23))
          return startof(ThisMsg[k].DE_23) + sizeof(ThisMsg[k].DE_23);
        if (exists(ThisMsg[k].DE_22))
          return startof(ThisMsg[k].DE_22) + sizeof(ThisMsg[k].DE_22);
        if (exists(ThisMsg[k].DE_21))
          return startof(ThisMsg[k].DE_21) + sizeof(ThisMsg[k].DE_21);
        if (exists(ThisMsg[k].DE_20))
          return startof(ThisMsg[k].DE_20) + sizeof(ThisMsg[k].DE_20);
        if (exists(ThisMsg[k].DE_19))
          return startof(ThisMsg[k].DE_19) + sizeof(ThisMsg[k].DE_19);
        if (exists(ThisMsg[k].DE_18))
          return startof(ThisMsg[k].DE_18) + sizeof(ThisMsg[k].DE_18);
        if (exists(ThisMsg[k].DE_17))
          return startof(ThisMsg[k].DE_17) + sizeof(ThisMsg[k].DE_17);
        if (exists(ThisMsg[k].DE_16))
          return startof(ThisMsg[k].DE_16) + sizeof(ThisMsg[k].DE_16);
      case 16:
        if (exists(ThisMsg[k].DE_15))
          return startof(ThisMsg[k].DE_15) + sizeof(ThisMsg[k].DE_15);
        if (exists(ThisMsg[k].DE_14))
          return startof(ThisMsg[k].DE_14) + sizeof(ThisMsg[k].DE_14);
        if (exists(ThisMsg[k].DE_13))
          return startof(ThisMsg[k].DE_13) + sizeof(ThisMsg[k].DE_13);
        if (exists(ThisMsg[k].DE_12))
          return startof(ThisMsg[k].DE_12) + sizeof(ThisMsg[k].DE_12);
      case 12:
        if (exists(ThisMsg[k].DE_11))
          return startof(ThisMsg[k].DE_11) + sizeof(ThisMsg[k].DE_11);
        if (exists(ThisMsg[k].DE_10))
          return startof(ThisMsg[k].DE_10) + sizeof(ThisMsg[k].DE_10);
        if (exists(ThisMsg[k].DE_9))
          return startof(ThisMsg[k].DE_9) + sizeof(ThisMsg[k].DE_9);
      case 9:
        if (exists(ThisMsg[k].DE_8))
          return startof(ThisMsg[k].DE_8) + sizeof(ThisMsg[k].DE_8);
        if (exists(ThisMsg[k].DE_7))
          return startof(ThisMsg[k].DE_7) + sizeof(ThisMsg[k].DE_7);
        if (exists(ThisMsg[k].DE_6))
          return startof(ThisMsg[k].DE_6) + sizeof(ThisMsg[k].DE_6);
        if (exists(ThisMsg[k].DE_5))
          return startof(ThisMsg[k].DE_5) + sizeof(ThisMsg[k].DE_5);
      case 5:
        if (exists(ThisMsg[k].DE_4))
          return startof(ThisMsg[k].DE_4) + sizeof(ThisMsg[k].DE_4);
        if (exists(ThisMsg[k].DE_3))
          return startof(ThisMsg[k].DE_3) + sizeof(ThisMsg[k].DE_3);
        if (exists(ThisMsg[k].DE_2))
          return startof(ThisMsg[k].DE_2) + sizeof(ThisMsg[k].DE_2);

      default:
        Printf("Could not find where to insert de_%d", field);
        Exit(0);
        return 0;
    }
  }

  void InsertDE(String tag, int skipMTI, int i, int pos, int deLen, long operand, String data) {
    // String data_ebc = ConvertString(data, CHARSET_ASCII, CHARSET_EBCDIC);

    int mtiPos = startof(ThisMsg[i]);
    int hexPos = mtiPos - 4;

    int success = HexOperation(HEXOP_ADD, hexPos, 4, deLen);
    hexPos = mtiPos + 4 + 4 * skipMTI;
    // Printf("skip =%d\n", hexPos);
    Printf(
        "Inserting DE_" + tag
            + " in Record[%d] at posMti:%d, posBitmap1:%d, pos:%d, len:%d, fileNum: %d,  Operand: %d \n",
        i,
        mtiPos, hexPos, pos, deLen,
        fileNum, operand);
    success = HexOperation(HEXOP_BINARY_OR, hexPos, 12, operand);
    FSeek(pos);
    InsertBytes(pos, deLen, ' ');
    if (data != "")
      WriteBytes(data, pos, Strlen(data));

    i = 0;

  }

  int FindPDS(int pds, int i) {
    if (ThisMsg[i].PDS == null)
      return -1;

    FSeek(startof(ThisMsg[i].PDS) - 3);
    int pdsNum;
    int pdsLen;
    int maxLen = Atoi(ConvertString(ReadString(FTell(), 3), CHARSET_EBCDIC, CHARSET_ASCII));
    FSkip(3);

    while (maxLen > 0) {
      pdsNum = Atoi(ConvertString(ReadString(FTell(), 4), CHARSET_EBCDIC, CHARSET_ASCII));
      if (pdsNum == pds)
        return 1;
      pdsLen = Atoi(ConvertString(ReadString(FTell() + 4, 3), CHARSET_EBCDIC, CHARSET_ASCII));
      maxLen -= 7 + pdsLen;
      FSkip(7 + pdsLen);
    }
    return 0;
  }

  void InsertPDS(String tag, int pos, String data, int i) {
    String len = "";
    len = SPrintf(len, "%03d", Strlen(data));
    int pdsLen = Atoi(len) + 7;

    data = tag + len + data;
    Printf("Data = " + data);
    data = ConvertString(data, CHARSET_ASCII, CHARSET_EBCDIC);
    Printf("Inserting PDS" + tag + " in Record[%d] at pos %d\n", i, pos);
    int hexPos = startof(ThisMsg[i]) - 4;
    int success = HexOperation(HEXOP_ADD, hexPos, 4, pdsLen);
    AddPDSLen(i, pdsLen);
    FSeek(pos);
    InsertBytes(pos, pdsLen, ConvertASCIIToEBCDIC(' '));
    WriteBytes(data, pos, pdsLen);
  }

  void AddPDSLen(int i, int size) {
    if (ThisMsg[i].PDS == null)
      return;

    int pdslen = Atoi(ConvertString(ReadString(startof(ThisMsg[i].PDS) - 3, 3), CHARSET_EBCDIC, CHARSET_ASCII));
    String len = "";
    len = SPrintf(len, "%03d", pdslen + size);
    String len_ebc = ConvertString(len, CHARSET_ASCII, CHARSET_EBCDIC);

    WriteBytes(len_ebc, startof(ThisMsg[i].PDS) - 3, 3);
  }

  @Override
  public void PrepareFile() {
    int DelimPres = 0;

    if (FileSize() >= 1014) {
      if (FileSize() >= 2028) {
        if (((ReadByte(FTell() + 1012) == 0) || (ReadByte(FTell() + 1012) == 64))
            && ((ReadByte(FTell() + 2026) == 0) || (ReadByte(FTell() + 2026) == 64))) {
          DelimPres = 1;
        }
      } else {
        if ((ReadShort(FTell() + 1012) == 0) || (ReadShort(FTell() + 1012) == 64)) {
          DelimPres = 1;
        }
      }
    }

    if (DelimPres == 1) {
      while (!FEof()) {
        FSkip(1012);
        DeleteBytes(FTell(), 2);
      }
    }
  }

  boolean MultipleHeaders() {
    return false;
  }

  @Override
  public void RunTemplate(String template) {
    if (template.contains("MASTERCARD_Template_EBCDIC.bt")) {
      MakeTemplate(new String[] {});
      return;
    }
  }

  @Override
  public void include(String template) {
    if (template.contains("MASTERCARD_PrepareFile")) {
      PrepareFile();
      return;
    }
    if (template.contains("MC_EBCDIC_Correct_Trailer")) {
      CorrectTrailer();
      return;
    }
  }

  /* FUNCTIONS */
  @Override
  public void run(String[] args1) {
    this.args = args1;
    FileOpen(); // incase file reader is closed !
    MakeTemplate(args1);
  }

  public void MakeTemplate(String[] args1) {
    int bgcol_Msg;
    int i = 0;
    String MsgName;
    // char MTI[];
    // char FC[];
    int IncluceRecSize = 0;
    int rec_size;
    int iccLen = 0;
    tran_no = 0;

    getIsoFile().getIsoMessages().clear();

    BigEndian();
    SetFileInterface("EBCDIC");

    FSeek(0);

    if (ReadShort() == 0) {
      IncluceRecSize = 1; // The file consists of record size followed by the content
    }

    ProcessNextTrans(IncluceRecSize); // Process Header

    tran_no++;
    do {
      rec_size = ProcessNextTrans(IncluceRecSize); // Process Next Transaction
      // LogManager.getLogger().debug("REC SIZE " + String.valueOf(rec_size));
      tran_no++;
    } while (!FEof() && ((rec_size > 0) || IncluceRecSize == 0) /* && FC != "695" */);

    initThisMsg();
  }

  @Override
  public IsoFileTemplate createTemplate(String action, String fileName) {
    if (action.equals(ACTION_GEN_CHARGE_BACK))
      return new IpmGenChargeBack(fileName);
    return this;
  }

}
