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
public class IpmGenChargeBack extends IpmFileTemplate {

  public IpmGenChargeBack(String file) {
    super(file);
    // TODO Auto-generated constructor stub
  }

  /* FUNCTIONS */
  @Override
  public void run(String[] args1) {
    this.args = args1;
    FileOpen(); // incase file reader is closed !
    GenChargeBack(args1);
    MyDialog.showMessage("File [ " + isoFile.fileReader.fileName + "] saved successfully ! ");
  }

  // Inserts DE_50, DE_48, DE_30, DE_25 if they aren't currently present in file,
  // on the requested account number.
  void UpdateMsg(int i) {
    if (ThisMsg == null || i > ThisMsg.length - 1)
      return;
    String type_cb_2;
    String de25_2;
    int pdsPos;
    String mti = ConvertString("1442", CHARSET_ASCII, CHARSET_EBCDIC);
    de25_2 = ConvertString(de25, CHARSET_ASCII, CHARSET_EBCDIC);
    type_cb_2 = ConvertString(typeOfCb, CHARSET_ASCII, CHARSET_EBCDIC);

    if (typeOfCb == "450") {
      if (ThisMsg[i].DE_4 != null)
        de30 = ThisMsg[i].DE_4.getData(); // get amount
      de30 += ConvertString("000000000000", CHARSET_ASCII, CHARSET_EBCDIC);
    } else {
      // de30
      de30 = SPrintf(de30, "%012d000000000000", de30_int);
      de30 = ConvertString(de30, CHARSET_ASCII, CHARSET_EBCDIC);
    }

    // de50
    de50 = ThisMsg[i].DE_49.getData();

    // update de24 and MTI
    Memcpy(ThisMsg[i].DE_24, type_cb_2, 3, 0, 0);
    Memcpy(ThisMsg[i], "MTI", mti, 4, 0, 0);

    // Insert de50
    if (!exists(ThisMsg[i].DE_50)) {
      InsertDE("DE50", 0, i, allPositions[0], 3, 0x0000000000004000L, de50);
    } else {
      Memcpy(ThisMsg[i].DE_49, ThisMsg[i].DE_50, 3, 0, 0);
    }

    if (FindPDS(149, i) == 0) {

      pdsPos = SetPDSPos(i, 149);
      if (pdsPos > 0) {
        String data = ConvertString(ThisMsg[i].DE_49, CHARSET_EBCDIC, CHARSET_ASCII);
        InsertPDS("0149", pdsPos, data + "000", i);
        SetCursorPos(startof(ThisMsg[i].DE_49));
      }
    }

    if (FindPDS(2, i) == 0) {
      pdsPos = SetPDSPos(i, 2);
      if (pdsPos > 0) {
        InsertPDS("0002", pdsPos, "000", i);
        SetCursorPos(startof(ThisMsg[i].DE_49));
      }
    }

    // InsertDE(int i, less50, string tag, int pos, int mtiPos, int deLen, int64
    // operand, string data){
    if (!exists(ThisMsg[i].DE_30)) {
      InsertDE("DE30", 0, i, allPositions[2], 24, 0x0000000400000000L, de30);
    } else {
      de30 = Memcpy(de30, ThisMsg[i].getAttribute("DE_30.DE_30_01"), 12, 0, 0);
      de30_2 = ConvertString("000000000000", CHARSET_ASCII, CHARSET_EBCDIC);
      de30_2 = Memcpy(de30_2, ThisMsg[i].getAttribute("DE_30.DE_30_02"), 12, 0, 0);
    }

    if (!exists(ThisMsg[i].DE_25)) {
      InsertDE("DE25", 0, i, allPositions[3], 4, 0x0000008000000000L, de25_2);
    } else {
      Memcpy(ThisMsg[i].DE_25, de25, 4, 0, 0);
    }
  }

  int SetPDSPos(int i, int pds) {
    if (ThisMsg[i].PDS == null)
      return -1;
    FSeek(startof(ThisMsg[i].PDS) - 3);
    int pdsNum;
    int pdsLen;
    int maxLen = Atoi(ConvertString(ReadString(FTell(), 3), CHARSET_EBCDIC, CHARSET_ASCII));
    int nextPdsNum;
    FSkip(3);

    while (maxLen > 0) {
      pdsNum = Atoi(ConvertString(ReadString(FTell(), 4), CHARSET_EBCDIC, CHARSET_ASCII));
      pdsLen = Atoi(ConvertString(ReadString(FTell() + 4, 3), CHARSET_EBCDIC, CHARSET_ASCII));

      FSkip(7 + pdsLen);
      nextPdsNum = Atoi(ConvertString(ReadString(FTell(), 4), CHARSET_EBCDIC, CHARSET_ASCII));

      if (pds < pdsNum)
        return startof(ThisMsg[i].PDS);

      if ((pds > pdsNum) && (pds < nextPdsNum)) {
        return FTell();
      }
      maxLen -= 7 + pdsLen;
    }

    return allPositions[0];
  }

  void ComparePDS(int i) {

    // RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");
    FSeek(startof(ThisMsg[i].DE_49) - 12);
    int result = FTell();// Current pos of PDS 149 + meta data
    Printf("Result: %d\n", result);
    char[] pds149 = new char[24];
    ReadBytes(pds149, result, 12);
    pds149 = ConvertString(pds149, CHARSET_EBCDIC, CHARSET_ASCII).toCharArray();
    Printf("ReadByte Result: " + pds149 + "\n");
  }

  void FillPositions(int msgNum) {
    allPositions[0] = FindPos(50, msgNum); // de50
    allPositions[1] = FindPos(48, msgNum); // de48
    allPositions[2] = FindPos(30, msgNum); // de30
    allPositions[3] = FindPos(25, msgNum); // de25
  }

  // return the number of transaction having pan found
  int FindPAN(String PAN) {
    int k = 0;
    int l = 0;
    char c = ConvertASCIIToEBCDIC(',');
    String PAN_E = ConvertString(PAN, CHARSET_ASCII, CHARSET_EBCDIC);
    while (exists(ThisMsg, k)) {
      if (exists(ThisMsg[k].DE_2)
          && (Strstr(PAN_E + c, ThisMsg[k].DE_2.getData() + c) != -1 || PAN.equals("-1"))) {
        pan_pos[l] = k;
        l++;
      }
      if (k >= ThisMsg.length - 1)
        break;
      k++;
    }
    if (l == 0) {
      Printf("No transaction with given PAN was found\n.");
      Exit(0);
    }
    return l;
  }

  public void MakeNewCopy() {
    int i = 0;
    int orig_file = GetFileNum();
    new_file = FileNew("EBCDIC");
    if (new_file == -1)
      return;

    // copy header
    FileSelect(orig_file);
    SetSelection(0, 78);
    CopyToClipboard();
    SetSelection(0, 0);

    FileSelect(new_file);
    PasteFromClipboard();

    // copy all presentments with given PAN
    FileSelect(orig_file);
    // RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");

    while (i < no_transactions) {
      FileSelect(orig_file);
      SetSelection(startof(ThisMsg[pan_pos[i]]) - 4, sizeof(ThisMsg[pan_pos[i]]) + 4);
      CopyToClipboard();
      FileSelect(new_file);
      SetSelection(0, 0);
      SetCursorPos(FileSize());
      PasteFromClipboard();
      i++;
    }

    // copy trailer
    while (exists(ThisMsg, i + 1))
      i++;
    if (i <= ThisMsg.length - 1) {
      FileSelect(orig_file);
      SetSelection(startof(ThisMsg[i]) - 4, 108);

      CopyToClipboard();
      FileSelect(new_file);
      SetSelection(0, 0);
      SetCursorPos(FileSize());
      PasteFromClipboard();
    }

    RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");
  }

  public void GenChargeBack(String[] args1) {
    try {
      this.args = args1;
      if (GetNumArgs() > 0) {
        PAN_A = GetArg(0); // transaction to insert into
        typeOfCb = GetArg(1);
        if (GetNumArgs() > 2) {
          de25 = GetArg(2);
          de30_int = Atoi(GetArg(3));
          skip_revert = Atoi(GetArg(4));
        }
      } else {
        PAN_A = InputString("PAN",
            "Enter value for PAN or -1 for all.\nMultiple PANs are sepearated by a comma. Example:\n 0011223344556677,0011223344556688",
            "-1");
        typeOfCb = InputString("Type of chargeback", "Enter the type of chargeback:\nFull - 450\nPartial - 453\n",
            "450");
        de25 = InputString("DE25", "Insert value for DE25 (Message Reason Code). Default '4973'", "4973");
        if (typeOfCb != "450") {
          de30_int = InputNumber("DE30", "Insert value for DE30 (Amounts Original). Default '0'", "0");
        }

        skip_revert = InputNumber("Revert", "Create revert file ?. Default '0", "0");
      }

      if (Strlen(PAN_A) < 16 && !PAN_A.equals("-1")) {
        Printf("PAN should be 16 characters long.\n");
        Exit(0);
        return;
      }

      include("..\\MASTERCARD\\MASTERCARD_PrepareFile.1sc");

      RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");

      // check multiple headers
      if (MultipleHeaders()) {

        de_30_str = SPrintf(de_30_str, "%012d", de30_int);
        String params = PAN_A + "," + typeOfCb + "," + de25 + "," + de_30_str;
        String script_path = "../MASTERCARD/MC_EBCDIC_Gen_Chargeback_PAN.1sc";
        RunScriptOnTempFiles(script_path, params);
        Printf("Dont forget to run MASTERCARD_RevertFile.1sc and save the new file.\n");
        Exit(0);
        return;
      }

      no_transactions = FindPAN(PAN_A);

      MakeNewCopy();

      // update header
      String file_type = ConvertString("001", CHARSET_ASCII, CHARSET_EBCDIC);
      Memcpy(ThisMsg[0], "PDS.PDS_0105.PDS_0105_01", file_type, 3, 0, 0);

      while (exists(ThisMsg, i))
        i++;

      // Update file Trailer
      message_count = SPrintf(message_count, "%08d", i);
      message_count = ConvertString(message_count, CHARSET_ASCII, CHARSET_EBCDIC);
      Memcpy(ThisMsg[i - 1], "PDS.PDS_0306", message_count, 8, 0, 0);

      // i is the last presentment
      for (i = no_transactions; i > 0; i--) {
        FillPositions(i);
        UpdateMsg(i);
      }

      RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");
      DeleteAddendum();
      include("..\\MASTERCARD\\MC_EBCDIC_Correct_Trailer.1sc");
      RunTemplate("..\\MASTERCARD\\MASTERCARD_Template_EBCDIC.bt");

      if (skip_revert == 0)
        RevertFile();

      if (GetNumArgs() > 5) {
        FileSave(GetArg(5));
      }
    } catch (Exception ex) {
      LogManager.getLogger().error(ex);
    }

  }

}
