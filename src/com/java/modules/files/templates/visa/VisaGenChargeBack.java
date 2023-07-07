package com.java.modules.files.templates.visa;

import java.util.Arrays;

import com.java.core.components.MyDialog;
import com.java.core.logs.*;
import com.java.modules.files.isoparser.MyField;
import com.java.modules.files.isoparser.MyFile;
import com.java.modules.files.isoparser.elements.ascii.AsciiField;
import com.java.modules.files.isoparser.elements.ascii.AsciiFile;
import com.java.modules.files.isoparser.elements.ascii.AsciiMessage;
import com.java.modules.files.isoparser.elements.iso.IsoField;
import com.java.modules.files.templates.IsoFileTemplate;
import com.java.modules.files.templates.visa.structures.*;
import com.java.core.utils.Utils;

//migrate code from 010 Tool. 
//Rule1: Sprintf(a, ..) ===> a = Sprintf(a, ..)
//Rule2: exists(arr[i]) ===> exists(arr, i)
//Rule3: compare string, dont use == ===> use .equals
//Rule4: !(1,0) ==> == 0, == 1

public class VisaGenChargeBack extends VisaFileTemplate {

  public VisaGenChargeBack(String file) {
    super(file);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void PrepareFile() {
    int EndofLine;
    int NoChrs = 0;
    int LineLength = 168;

    while (!FEof()) {
      while (!FEof() && (char) ReadByte() != '\r' && (char) ReadByte() != '\n') {
        FSkip(1);
      }

      NoChrs = 168 - (FTell() % 170);
      if (NoChrs > 0) {
        InsertBytes(FTell(), NoChrs, ' ');
      }
      FSkip(2); // Skip CRLF, 168 + 2
    }
  }

  int CreateCopy(int fileNum1) {

    // copy header
    int orig_file_num = GetFileNum();
    SetSelection(0, 170);
    CopyToClipboard();
    int i = 0;

    // string filepath = GetFileName();
    // string filename = FileNameGetBase(filepath, false);
    // string extension = FileNameGetExtension(filepath);

    fileNum1 = FileNew();
    FileSelect(fileNum1);
    PasteFromClipboard();

    while (i < transactions) {
      FileSelect(orig_file_num);
      SetSelection(startof(VISA_BATCH[batch_arr[i]].VISA_TRN[tran_arr[i]]),
          sizeof(VISA_BATCH[batch_arr[i]].VISA_TRN[tran_arr[i]]));
      CopyToClipboard();
      FileSelect(fileNum1);
      SetCursorPos(FileSize());
      PasteFromClipboard();
      // WriteBytes(getBytes("\n"), FileSize());
      i++;
    }

    // trailer
    FileSelect(orig_file_num);
    SetSelection(startof(VISA_TRN[1]) - 170, 168 + 170);
    CopyToClipboard();
    FileSelect(fileNum1);
    PasteFromClipboard();
    RunTemplate(visa_template_path);
    // FileSave(filename+"_Chargeback"+extension);
    return fileNum1;
  }

  void Chargeback() {
    int batch_num = 0;
    int k = 0;

    while (batch_num < VISA_BATCH.length && exists(VISA_BATCH[batch_num])) {
      while (k < VISA_BATCH[batch_num].VISA_TRN.length && exists(VISA_BATCH[batch_num].VISA_TRN[k])) {
        if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0)) {
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.trans_code[0] = '1';
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.trans_code[0] = '1';
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.destination_currency[0] = VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.source_currency[0];
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.destination_currency[1] = VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.source_currency[1];
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.destination_currency[2] = VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.source_currency[2];

          // switch usage code to '9'
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.usage_code[0] = '9';

          // switch reason code to tht chosen by user
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.reason_code[0] = reason_code[0];
          VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_0.reason_code[1] = reason_code[1];

          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].ARN))
            VISA_BATCH[batch_num].VISA_TRN[k].ARN.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_01_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_01_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_01_9))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_01_9.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_04_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_04_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_04_9))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_04_9.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_LG))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_LG.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_CA))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_CA.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_FL))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_FL.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_CR))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_CR.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_AN))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_AN.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_E_JA))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_E_JA.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_E))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_E.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_4))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_4.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_5))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_5.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_6))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_6.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_7))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_7.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_8))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_8.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2_SE))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2_SE.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2_BR))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2_BR.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_2.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_AI))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_3_AI.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_9))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_05_9.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10_9))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10_9.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_10.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_99))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_99.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_GB))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_GB.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_DE))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_DE.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_AU))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32_0_AU.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_32.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_400083_POS))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_400083_POS.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_400083_PSR))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_400083_PSR.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN_HEADER))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN_HEADER.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN_TRAILE))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN_TRAILE.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_TRSVISABIN.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN_TAPEHE))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN_TAPEHE.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN_TAPETR))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN_TAPETR.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_DSPLUSBIN.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_CFGEANSBII))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_CFGEANSBII.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_BIIDCCURR1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0_BIIDCCURR1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_33_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_TS))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_TS.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_MS))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_MS.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_TR))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0_TR.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_2))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_2.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_3))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_3.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_4))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_4.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_7))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_40_7.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_46.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_7))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_7.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_2))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_0_2.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_2))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_2.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_6))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_48_6.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_PURCHA))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_PURCHA.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_PURCHL))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_PURCHL.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPAI))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPAI.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPAS))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPAS.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPLG))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPLG.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPCA))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_CORPCA.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_OPNFMT))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0_OPNFMT.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_50.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_9))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51_9.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_51.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_55))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_55.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_56_0))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_56_0.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_56_1))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_56_1.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_90))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_90.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_91))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2_91.trans_code[0] = '1';
          if (exists(VISA_BATCH[batch_num].VISA_TRN[k].visa_b2))
            VISA_BATCH[batch_num].VISA_TRN[k].visa_b2.trans_code[0] = '1';
        }
        k++;
      }
      k = 0;
      batch_num++;
    }
  }

  @Override
  public MyFile createNewFile(String fileName, String mode) {
    return new AsciiFile(fileName, mode);
  }

  void Insert04() {
    int i = numBatches - 1;
    int k = 0;
    int visa_2_len;
    String visa_b2_05_4 = "1504AB345     DF0002                          0010ABC222222222223333333333444444444455555555555555555555F1                                                              \r\n";
    while (i >= 0) {
      while (k < VISA_BATCH[i].VISA_TRN.length && exists(VISA_BATCH[i].VISA_TRN[k])) {
        k++;
      }
      k -= 2;

      while (k >= 0) {
        if (k < VISA_BATCH[i].VISA_TRN.length && exists(VISA_BATCH[i].VISA_TRN[k].visa_b2_05_5)) {
          FSeek(startof(VISA_BATCH[i].VISA_TRN[k].visa_b2_05_5)); // insert visa_b2_04_4 before visa_b2_05_5

          if (!exists(VISA_BATCH[i].VISA_TRN[k].visa_b2_05_4)) {
            InsertBytes(FTell(), Strlen(visa_b2_05_4));
            WriteBytes(visa_b2_05_4, FTell(), Strlen(visa_b2_05_4));
          }
        }
        k--;
      }
      i--;
    }
  }

  void UpdateFileID() {
    char[] new_file_id;
    new_file_id = VISA_TRN[0].visa_b2_90.file_id;
    new_file_id = SPrintf(new_file_id, "%03d", Atoi(new_file_id) + 1);

    Memcpy(VISA_TRN[0].visa_b2_90._file_id, new_file_id, 3, 0, 0);
    Printf("Chargeback complete. Do not forget to save the new file.\n");

  }

  void UpdateProcBinDate() {
    char[] pBin = VISA_TRN[0].visa_b2_90.processing_bin;
    char[] pDate = VISA_TRN[0].visa_b2_90.processing_date;
    if (VISA_TRN.length > 1 && exists(VISA_TRN[1].visa_b2_91)) {
      Memcpy(VISA_TRN[1].visa_b2_91._processing_bin, pBin, 6, 0, 0);
      Memcpy(VISA_TRN[1].visa_b2_91._processing_date, pDate, 5, 0, 0);
    }
  }

  void RandomizeCBKNum() {
    int k = 0;
    int num = 0;
    int random_int;
    String random = "";
    while (num < VISA_BATCH.length && exists(VISA_BATCH[num])) {
      while (k < VISA_BATCH[num].VISA_TRN.length && exists(VISA_BATCH[num].VISA_TRN[k])) {
        if (exists(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_1)) {
          // update chargeback_ref_nm
          random_int = Random(1000000);
          random = SPrintf(random, "%06d", random_int);
          Memcpy(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_1._chargeback_ref_num, random, 6, 0, 0);
        }
        k++;
      }
      k = 0;
      num++;
    }
  }

  void GenerateTC33() {
    int num = 0;
    int k = 0;
    String processing_bin = "";
    String account_number;
    String account_extension;
    String acquirer_ref_number;
    String purchase_date;
    String destination_currency;
    String source_amount;
    String merchant_category;
    String merchant_zip_code;
    String authorisation_code;
    String pos_entry_mode;
    String central_proc_date;
    String reimbursement_attr;
    String source_currency;
    String source_bin = "123456";
    String visa_33_0_string;
    String visa_33_1_string;

    // copy the file header
    FileSelect(fileNum1);
    if (exists(VISA_TRN[0].visa_b2_90)) {
      SetSelection(0, sizeof(VISA_TRN[0]));
      CopyToClipboard();
      FileSelect(newFile);
      PasteFromClipboard();
      FileSelect(fileNum1);
      processing_bin = new String(VISA_TRN[0].visa_b2_90.processing_bin);
    } else {
      Printf("Orignal file does not have header. Failed. \n");
      Exit(0);
      return;
    }
    while (num < VISA_BATCH.length && exists(VISA_BATCH[num])) {
      // Printf("num = %d\n",num);
      while (k < VISA_BATCH[num].VISA_TRN.length && exists(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0)) {
        // Printf("k=%d\n",k);
        account_number = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.account_number);
        account_extension = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.account_extension);
        acquirer_ref_number = ReadString(startof(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0) + 26, 23);
        purchase_date = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.purchase_date);
        destination_currency = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.destination_currency);
        source_amount = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.source_amount);
        merchant_category = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.merchant_category);
        merchant_zip_code = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.merchant_zip_code);
        authorisation_code = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.authorisation_code);
        pos_entry_mode = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.pos_entry_mode);
        central_proc_date = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.central_proc_date);
        // reimbursement_attr =
        // VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.reimbursement_attr;
        reimbursement_attr = "0";
        source_currency = new String(VISA_BATCH[num].VISA_TRN[k].visa_b2_05_0.source_currency);

        visa_33_0_string = "3300" + processing_bin + source_bin + "VCR" + dispute_status + "070 " + account_number
            + account_extension + acquirer_ref_number + purchase_date + source_amount + source_currency
            + "PLATE1          PLATE2    GB " + merchant_category + "  " + merchant_zip_code + " " + authorisation_code
            + pos_entry_mode + central_proc_date + "202354886671410" + reimbursement_attr;
        visa_33_1_string = "33010002" + dispute_financial_reason_code
            + "222222222223333333333444444444455555555555555555555  0000F 008900722600000000000000000                                  ";

        visa_33_0_string = visa_33_0_string
            + Utils.multiplyChars(" ", AsciiMessage.maxLength - visa_33_0_string.length()) + AsciiMessage.lineSeperator;
        visa_33_1_string = visa_33_1_string
            + Utils.multiplyChars(" ", AsciiMessage.maxLength - visa_33_1_string.length()) + AsciiMessage.lineSeperator;

        FileSelect(newFile);
        FSeek(FileSize());
        WriteBytes(visa_33_0_string + visa_33_1_string, FTell(), Strlen(visa_33_0_string + visa_33_1_string));

        FileSelect(fileNum1);
        k++;
      }

      // copy the batch trailer
      FileSelect(fileNum1);
      if (exists(VISA_BATCH[num].VISA_TRN[k].visa_b2_91)) {
        SetSelection(startof(VISA_BATCH[num].VISA_TRN[k].visa_b2_91),
            sizeof(VISA_BATCH[num].VISA_TRN[k].visa_b2_91) + 2);
        CopyToClipboard();
        FileSelect(newFile);
        SetCursorPos(FileSize());
        PasteFromClipboard();
      }

      FileSelect(fileNum1);
      k = 0;
      num++;
    }

    // copy the file trailer
    FileSelect(fileNum1);
    if (exists(VISA_TRN[1].visa_b2_91)) {
      SetSelection(startof(VISA_TRN[1].visa_b2_91), sizeof(VISA_TRN[1].visa_b2_91));
      CopyToClipboard();
      FileSelect(newFile);
      SetCursorPos(FileSize());
      PasteFromClipboard();
    }

    RunTemplate(visa_template_path);

    Memcpy(VISA_TRN[0].visa_b2_90._test_option, "TEST", 4, 0, 0);

    while (num < VISA_BATCH.length && exists(VISA_BATCH[num])) {
      while (k < VISA_BATCH[num].VISA_TRN.length && exists(VISA_BATCH[num].VISA_TRN[k])) {
        if (exists(VISA_BATCH_.VISA_TRN[1].visa_b2_91)) {
          VISA_BATCH_.VISA_TRN[1].visa_b2_91.number_of_mon_trans[11] = '0';
          VISA_TRN[1].visa_b2_91.number_of_mon_trans[11] = '0';
        }
        k++;
      }
      k = 0;
      num++;
    }

  }

  void correctNumMonTrans33() {
    String temp = "";
    int requiredMonTrans = 0;
    int foundMonTrans = Atoi(VISA_BATCH[0].VISA_TRN_.visa_b2_91.number_of_mon_trans);
    // int lineNum1 =
    // TextAddressToLine(startof(VISA_BATCH[0].VISA_TRN_.visa_b2_91)) + 1;
    // int lineNum2 = TextAddressToLine(startof(VISA_TRN[0].visa_b2_91)) + 1;
    if (requiredMonTrans != foundMonTrans) {
      temp = SPrintf(temp, "%012d", requiredMonTrans);
      Memcpy(VISA_BATCH[0].VISA_TRN_.visa_b2_91._number_of_mon_trans, temp, 12, 0, 0);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_mon_trans, temp, 12, 0, 0);
      // Printf("Warning: Line %d, %d : number_of_mon_trans in batch trailer & file
      // trailer Required (%d), Found (%d)\n", lineNum1, lineNum2, requiredMonTrans,
      // foundMonTrans);
    }

    // fix numebr of trans
    Memcpy(VISA_TRN[1].visa_b2_91._number_of_trans, VISA_BATCH[0].VISA_TRN_.visa_b2_91.number_of_trans, 9, 0, 0);
  }

  void correctDestAndSourceAmount33() {
    String temp = "";
    int requiredDestAmount = 0;
    int foundDestAmount = Atoi(VISA_BATCH[0].VISA_TRN_.visa_b2_91.destination_amount);
    // int lineNum1 =
    // TextAddressToLine(startof(VISA_BATCH[0].VISA_TRN_.visa_b2_91)) + 1;
    // int lineNum2 = TextAddressToLine(startof(VISA_TRN[0].visa_b2_91)) + 1;
    if (requiredDestAmount != foundDestAmount) {
      temp = SPrintf(temp, "%015d", requiredDestAmount);

      // fix source
      Memcpy(VISA_BATCH[0].VISA_TRN_.visa_b2_91._source_amount,
          VISA_BATCH[0].VISA_TRN_.visa_b2_91.destination_amount, 15,
          0, 0);
      Memcpy(VISA_TRN[1].visa_b2_91._source_amount, VISA_TRN[1].visa_b2_91.destination_amount, 15, 0, 0);

      // fix dest
      Memcpy(VISA_BATCH[0].VISA_TRN_.visa_b2_91._destination_amount, temp, 15, 0, 0);
      Memcpy(VISA_TRN[1].visa_b2_91._destination_amount, temp, 15, 0, 0);
      // Printf("Warning: Line %d, %d : destination_amount in batch trailer & file
      // trailer Required (%d), Found (%d)\n", lineNum1, lineNum2, requiredMonTrans,
      // foundMonTrans);

    }
  }

  void correctTCR33() {
    String temp = "";
    int foundTcr = Atoi(VISA_BATCH[0].VISA_TRN_.visa_b2_91.number_of_tcr);
    int lineNum = TextAddressToLine(startof(VISA_BATCH[0].VISA_TRN_.visa_b2_91));
    if (lineNum != foundTcr) {
      temp = SPrintf(temp, "%012d", lineNum);
      Memcpy(VISA_BATCH[0].VISA_TRN_.visa_b2_91._number_of_tcr, temp, 12, 0, 0);
      Printf("Warning: Line %d : number_of_tcr in batch trailer Required (%d), Found (%d)\n", lineNum + 1, lineNum,
          foundTcr);
    }

    foundTcr = Atoi(VISA_TRN[1].visa_b2_91.number_of_tcr);
    lineNum = TextAddressToLine(startof(VISA_TRN[1].visa_b2_91));
    if (lineNum != foundTcr) {
      temp = SPrintf(temp, "%012d", lineNum);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_tcr, temp, 12, 0, 0);
      Printf("Warning: Line %d : number_of_tcr in file trailer Required (%d), Found (%d)\n", lineNum + 1, lineNum,
          foundTcr);
    }
  }

  public void GenChargeBack(String[] args) {
    BigEndian();

    if (GetNumArgs() > 0) {
      PAN = GetArg(0);
      Printf("Cmd params: = %s\n", GetArg(0));
      code = GetArg(1);
      dispute_status = GetArg(2);
      dispute_financial_reason_code = GetArg(3);
      reason_code = GetArg(4).toCharArray();
    } else {
      PAN = InputString("PAN",
          "Enter value for PAN or -1 for all.\nMultiple PANs are sepearated by a comma. Example:\n 0011223344556677,0011223344556688",
          "-1");
      Printf("No cmd parameters input. Processing current open file\n");
      code = InputString("TC Code", "Choose between \"15\", \"33\", or both \"-1\" for both.\n", "-1");
      dispute_status = InputString("Dispute Status", "Enter Dispute Status (2 chars)", "F1");
      dispute_financial_reason_code = InputString("Dispute Financial Reason Code",
          "Enter Dispute Financial Reason Code (3 chars)", "000");
      reason_code = InputString("Reason Code", "Enter Reason Code", "00").toCharArray();
    }

    if (!code.equals("15") && !code.equals("33") && !code.equals("-1")) {
      Printf("Invalid code");
      Exit(0);
      return;
    }

    RunTemplate(visa_template_path);
    transactions = CheckPAN(PAN);
    Printf("Transations found = %d\n", transactions);
    PrepareFile(); // prepare visa script

    if (code.equals("15") || code.equals("-1")) {
      fileNum1 = CreateCopy(fileNum1);
      Insert04();
      RunTemplate(visa_template_path);
      Chargeback();
      RandomizeCBKNum();
      UpdateFileID();
      UpdateProcBinDate();
      correctNumMonTrans33();
      correctTCR33();
      correctDestAndSourceAmount33();
      FileSave();
    }

    if (code.equals("33") || code.equals("-1")) {
      newFile = CreateNewFile(newFile);
      // if (fileNum1 == 0)
      fileNum1 = origFile;
      GenerateTC33();
      RandomizeCBKNum();
      UpdateFileID();
      UpdateProcBinDate();
      FileSelect(fileNum1);
      correctTCR();
      correctAmount();
      correctCount();
      FileSave();
    }
  }

  int CheckPAN(String pan) {
    int k = 0;
    int j = 0;
    char c = ',';
    int arr_pos = 0;

    while (j < VISA_BATCH.length && exists(VISA_BATCH[j])) {
      while (k < VISA_BATCH[j].VISA_TRN.length && exists(VISA_BATCH[j].VISA_TRN[k])) {
        if (exists(VISA_BATCH[j].VISA_TRN[k].visa_b2_05_0)) {
          if (Strstr(PAN + c, VISA_BATCH[j].VISA_TRN[k].visa_b2_05_0.account_number.toString() + c) != -1
              || pan.equals("-1")) {
            batch_arr[arr_pos] = j;
            tran_arr[arr_pos] = k;
            arr_pos++;
          }
        }
        k++;
      }
      k = 0;
      j++;
    }
    if (arr_pos == 0) {
      Printf("No pan match found\n");
      Exit(0);
    }
    return arr_pos;
  }

  public void CorrectTrailer() {
    if (exists(VISA_BATCH[0].VISA_TRN[0].visa_b2_33_0)) {
      correctNumMonTrans33();
      correctTCR33();
      correctDestAndSourceAmount33();
    }

    if (exists(VISA_BATCH[0].VISA_TRN[0].visa_b2_05_0)) {
      correctTCR();
      correctAmount();
      correctCount();
    }

  }

  @Override
  public void include(String template) {
    if (template.contains("PrepareFile")) {
      PrepareFile();
      return;
    }

    if (template.contains("VISA_ASCII_Correct_Trailer")) {
      CorrectTrailer();
      return;
    }
    super.include(template);
  }

  @Override
  public void run(String[] args1) {
    this.args = args1;
    FileOpen(); // incase file reader is closed !
    GenChargeBack(args1);
    MyDialog.showMessage("File [ " + isoFile.fileReader.fileName + "] saved successfully ! ");
  }

}
