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
import com.java.modules.files.isoparser.elements.iso.IsoFile;
import com.java.modules.files.templates.IsoFileTemplate;
import com.java.modules.files.templates.visa.structures.*;
import com.java.core.utils.Utils;

//migrate code from 010 Tool. 
//Rule1: Sprintf(a, ..) ===> a = Sprintf(a, ..)
//Rule2: exists(arr[i]) ===> exists(arr, i)
//Rule3: compare string, dont use == ===> use .equals
//Rule4: !(1,0) ==> == 0, == 1

public class VisaFileTemplate extends IsoFileTemplate {
  public VisaBatch[] VISA_BATCH = new VisaBatch[] {};
  public VisaBatch VISA_BATCH_;
  public VisaTrans[] VISA_TRN = new VisaTrans[] {};
  VisaBatch currBatch = null;
  VisaTrans currTrans = null;

  public static String ACTION_RUN_TEMPLATE = "Visa";
  public String visa_template_path = "..\\VISA\\VISA_ASCII_Template.bt";
  int numBatches = 1;
  int newFile;
  int fileNum1 = 0;
  int origFile = GetFileNum();

  String PAN;
  int i;
  int batch_num;

  String dispute_status;
  String dispute_financial_reason_code;
  char[] reason_code;
  String code; // 15 | 33 | -1 for both

  int[] batch_arr = new int[1000];
  int[] tran_arr = new int[1000];
  int transactions;

  public VisaFileTemplate(String file) {
    super(file, CHARSET_ASCII);
  }

  @Override
  public MyFile createNewFile(String fileName, String mode) {
    return new AsciiFile(fileName, mode);
  }

  public AsciiFile getIsoFile() {
    return (AsciiFile) isoFile;
  }

  public int startof(VisaTrans trans) {
    if (trans == null || trans.messages.size() == 0)
      return 0;
    if (trans.offset > 0)
      return trans.offset;
    return trans.messages.get(0).offset;
  }

  public int startof(AsciiMessage msg, String field) {
    int i = msg.offset;
    for (MyField fld : msg.getFields()) {
      if (fld.getName().equalsIgnoreCase(field))
        break;
      i += fld.getLength();
    }
    return i;
  }

  public int startof(VisaBatch batch) {
    if (batch == null)
      return 0;
    if (batch.offset > 0)
      return batch.offset;
    return batch.VISA_TRN.length == 0 ? 0 : startof(batch.VISA_TRN[0]);
  }

  public int startof(AsciiMessage msg) {
    return msg.offset;
  }

  public int sizeof(AsciiMessage msg) {
    return msg.getLength();
  }

  public int sizeof(VisaTrans trans) {
    if (trans == null || trans.messages.size() == 0)
      return 0;
    if (trans.length > 0)
      return trans.length;
    int l = 0;
    for (AsciiMessage msg : trans.messages) {
      l += msg.length + 2;
    }
    return l;
  }

  public Object Memcpy(VisaTrans msg, String attribute, String data, int size, int i, int j) {
    // byte[] data = new byte[size];
    return Memcpy(msg, attribute, data.toCharArray(), size, i, j);
  }

  public Object Memcpy(VisaTrans msg, String attribute, char[] data, int size, int i, int j) {
    // byte[] data = new byte[size];
    int pos = msg.setAttribute(attribute, data);
    if (pos > -1)
      WriteBytes(data, pos, size);
    System.out.println(
        "MemCpy " + msg.toString());
    return msg;
  }

  void PrintLog() {
    // OutputPaneClear();
    // Printf("Template (%d) \r\n", (FTell() * 100) / FileSize());
    // OutputPaneSave("C:\\log.txt");
  }

  public void SkipEndOfLine(int SkipStep) {

    if (!FEof() && !(((char) ReadByte() == '\r' || (char) ReadByte() == '\n'))) {
      Printf("Incorrect Line Size on line %d pos %d\r\n",
          TextAddressToLine(FTell()) + 1, FTell());
      Exit(-1);
      return;
    }

    while (!FEof() && ((char) ReadByte() == '\r' || (char) ReadByte() == '\n')) {
      FSkip(SkipStep);
    }
  }

  public void addMessage(AsciiMessage msg) {
    int len = msg.getLength();
    String content = ReadString(len);
    if (content.isEmpty())
      return;
    msg.setOffset(FTell());
    msg.setDataString(content);

    System.out.println(msg.toString(true));
    VisaTrans trans = getCurrentTrans();
    if (Utils.getFieldValueFromName(trans, msg.getClassName()) != null) {
      VISA_TRN_ADD();
    }
    getCurrentTrans().setAttribute(msg);
    getCurrentTrans().length = getCurrentTrans().length + len + 2;
    FSkip(len);

    getIsoFile().getIsoMessages().add(msg);
  }

  public VisaBatch[] VISA_BTCH_ADD() {
    if (VISA_BATCH == null)
      VISA_BATCH = new VisaBatch[] {};
    VisaBatch[] result = Arrays.copyOf(VISA_BATCH, VISA_BATCH.length + 1);
    VisaBatch newBatch = new VisaBatch();
    newBatch.offset = FTell();
    result[result.length - 1] = newBatch;
    VISA_BATCH = result;
    currBatch = newBatch;
    if (VISA_BATCH_ == null)
      VISA_BATCH_ = newBatch;
    return result;
  }

  public VisaTrans[] VISA_TRN_ADD() {
    VisaTrans newTrans = new VisaTrans();
    newTrans.offset = FTell();
    newTrans.length = 0;
    VisaTrans[] result = null;
    VisaBatch currBatch = getCurrentBatch();
    if (currBatch != null) {
      result = Arrays.copyOf(currBatch.VISA_TRN, currBatch.VISA_TRN.length + 1);
      result[result.length - 1] = newTrans;
      currBatch.VISA_TRN = result;
      newTrans.visaBatch = currBatch;
      // if (currBatch.VISA_TRN_ == null) //first Tran
      currBatch.VISA_TRN_ = newTrans; // last Tran

    } else {
      if (VISA_TRN == null)
        VISA_TRN = new VisaTrans[] {};
      result = Arrays.copyOf(VISA_TRN, VISA_TRN.length + 1);
      result[result.length - 1] = newTrans;
      VISA_TRN = result;
    }
    currTrans = newTrans;
    return result;
  }

  public VisaBatch getCurrentBatch() {
    return currBatch;
    // if (VISA_BATCH.length == 0) {
    // // VISA_BTCH_ADD();
    // return null;
    // }
    // return VISA_BATCH[VISA_BATCH.length - 1];
  }

  public VisaTrans getCurrentTrans() {
    VisaBatch currBatch = getCurrentBatch();
    if (currBatch != null) {
      if (currBatch.VISA_TRN.length > 0)
        return currBatch.VISA_TRN[currBatch.VISA_TRN.length - 1];
    } else {
      if (VISA_TRN.length > 0)
        return VISA_TRN[VISA_TRN.length - 1];
    }
    return null;
  }

  public void ParseLine() {
    int StartLine;
    int StartLineReturn;
    String StartLineReturnStr;
    String trans_code;
    String trans_comp_seq;

    String TransType;

    // Read the trans code
    StartLine = FTell();
    StartLineReturn = StartLine;
    StartLineReturnStr = ReadString(StartLineReturn);
    trans_code = ReadString(StartLine, 2);
    trans_comp_seq = ReadString(StartLine + 3, 1);
    // Printf("transcode=%s, tcs=%s", trans_code, trans_comp_seq);
    switch (trans_code) {
      case "01":
      case "02":
      case "03":

        switch (trans_comp_seq) {
          case "0":
          case "1":
          case "2":
          case "3":
          case "4":
          case "5":
          case "6":
          case "7":
          case "8":
            addMessage(new visa_b2_01_0(trans_comp_seq, StartLineReturn)); // StartLineReturn) <name="visa_b2_01_0">;
            break;

          case "9":
            addMessage(new visa_b2_01_9()); // <name="visa_b2_01_9">;
            break;
        }
        break;

      case "04":

        switch (trans_comp_seq) {
          case "0":
          case "1":
          case "2":
          case "3":
          case "4":
          case "5":
          case "6":
          case "7":
          case "8":
            addMessage(new visa_b2_04_0()); // <name="visa_b2_04_0">;
            break;

          case "9":
            addMessage(new visa_b2_04_9()); // <name="visa_b2_04_9">;
            break;
        }
        break;

      case "05": // TCR
      case "06": // TCR
      case "07": // TCR
      case "15":
      case "16":
      case "17":
      case "25":
      case "26":
      case "27":
      case "35":
      case "36":
      case "37":

        switch (trans_comp_seq) {
          case "0":
            addMessage(new visa_b2_05_0(0)); // <name="visa_b2_05_0">; // TCR0
            break;

          case "1":
            addMessage(new visa_b2_05_1(0)); // <name="visa_b2_05_1">; // TCR1
            break;

          case "2":
            String country_code = ReadString(StartLine + 16, 3); //
            switch (country_code) {
              case "SE ":
                addMessage(new visa_b2_05_2_SE(0)); // <name="visa_b2_05_2_SE">; // TCR2
                break;

              case "BR ":
                addMessage(new visa_b2_05_2_BR(0)); // <name="visa_b2_05_2_BR">; // TCR2
                break;

              case "AR ":
                addMessage(new visa_b2_05_2_AR(0)); // <name="visa_b2_05_2_AR">; // TCR2
                break;

              default:
                addMessage(new visa_b2_05_2(0)); // <name="visa_b2_05_2">; // TCR2
            }
            break;

          case "3":
            String bus_format_code = ReadString(StartLine + 16, 2); //

            switch (bus_format_code) {
              case "AI":
                addMessage(new visa_b2_05_3_AI(0)); // <name="visa_b2_05_3_AI">; // TCR3Airline
                break;

              case "LG":
                addMessage(new visa_b2_05_3_LG(0)); // <name="visa_b2_05_3_LG">; // TCR3Lodging
                break;

              case "CA":
                addMessage(new visa_b2_05_3_CA(0)); // <name="visa_b2_05_3_CA">; // TCR3AutoRental
                break;

              case "FL":
                addMessage(new visa_b2_05_3_FL(0)); // <name="visa_b2_05_3_FL">;
                break;

              case "CR":
                addMessage(new visa_b2_05_3_CR(0)); // <name="visa_b2_05_3_CR">; // TCR3AutoRental
                break;

              case "AN":
                addMessage(new visa_b2_05_3_AN(0)); // <name="visa_b2_05_3_AN">;
                break;

              default:
                addMessage(new visa_b2_05_3(0)); // <name="visa_b2_05_3">;
            }
            break;

          case "4":
            addMessage(new visa_b2_05_4_SVCRFD(0)); // <name="visa_b2_05_4">;
            break;

          case "5":
            addMessage(new visa_b2_05_5(0)); // <name="visa_b2_05_5">; // TCR5PaymSvc
            break;

          case "6":
            addMessage(new visa_b2_05_6(0)); // <name="visa_b2_05_6">; // TCR6
            break;

          case "7":
            addMessage(new visa_b2_05_7(0)); // <name="visa_b2_05_7">; // TCR7SmartCard
            break;

          case "8":
            addMessage(new visa_b2_05_8(0)); // <name="visa_b2_05_8">;
            break;

          case "9":
            addMessage(new visa_b2_05_9(0)); // <name="visa_b2_05_9">;
            break;

          case "E":
            bus_format_code = ReadString(StartLine + 4, 2); //

            switch (bus_format_code) {
              case "JA":
                addMessage(new visa_b2_05_E_JA(0)); // <name="visa_b2_05_E_JA">; // TCR3Airline
                break;

              default:
                addMessage(new visa_b2_05_E(0)); // <name="visa_b2_05_E">;
            }
        }
        break;

      case "10":
      case "20":
        switch (trans_comp_seq) {
          case "0":
            addMessage(new visa_b2_10_0()); // <name="visa_b2_10_0">;
            break;
          case "1":
            addMessage(new visa_b2_10_1()); // <name="visa_b2_10_1">;
            break;

          case "9":
            addMessage(new visa_b2_10_9()); // <name="visa_b2_10_9">;
            break;

          default:
            addMessage(new visa_b2_10()); // <name="visa_b2_10">;

        }
        break;

      case "32":
        switch (trans_comp_seq) {
          case "0":

            String action_indicator = ReadString(StartLine + 28, 2); //

            switch (action_indicator) {
              case "99":
                addMessage(new visa_b2_32_0_99()); // <name="visa_b2_32_0_99">;
                break;

              default:

                String acquirer_country_code = ReadString(StartLine + 31, 3); //

                switch (acquirer_country_code) {
                  case "GB ":
                    addMessage(new visa_b2_32_0_GB()); // <name="visa_b2_32_0_GB">;
                    break;

                  case "DE ":
                    addMessage(new visa_b2_32_0_DE()); // <name="visa_b2_32_0_DE">;
                    break;

                  case "AU ":
                  case "NZ ":
                    addMessage(new visa_b2_32_0_AU()); // <name="visa_b2_32_0_AU">;
                    break;
                }
            }
            break;

          default:
            addMessage(new visa_b2_32()); // <name="visa_b2_32">;
            break;
        }
        break;

      case "33":

        switch (trans_comp_seq) {
          case "0":

            String source_bin = ReadString(StartLine + 10, 6); //

            switch (source_bin) {
              case "400083":
                String tc33_application_code = ReadString(StartLine + 16, 3);//
                switch (tc33_application_code) {
                  case "POS":
                    addMessage(new visa_b2_33_0_400083_POS()); // <name="visa_b2_33_0_400083_POS">;
                    break;

                  case "PSR":
                    addMessage(new visa_b2_33_0_400083_PSR()); // <name="visa_b2_33_0_400083_PSR">;
                    break;

                }
                break;

              default:

                String report_identifier = ReadString(StartLine + 16, 10); //

                switch (report_identifier) {
                  case "TRSVISABIN":

                    String sequence_number_1 = ReadString(StartLine + 34, 6); //

                    switch (sequence_number_1) {
                      case "HEADER":
                        addMessage(new visa_b2_33_0_TRSVISABIN_HEADER()); // <name="visa_b2_33_0_TRSVISABIN_HEADER">;
                        break;

                      case "TRAILE":
                        addMessage(new visa_b2_33_0_TRSVISABIN_TRAILE()); // <name="visa_b2_33_0_TRSVISABIN_TRAILE">;
                        break;

                      default:
                        addMessage(new visa_b2_33_0_TRSVISABIN()); // <name="visa_b2_33_0_TRSVISABIN">;
                    }
                    break;

                  case "DSPLUSBIN ":
                  case "DSVPLUSBIN":

                    sequence_number_1 = ReadString(StartLine + 34, 6); //

                    switch (sequence_number_1) {
                      case "TAPEHE":
                        addMessage(new visa_b2_33_0_DSPLUSBIN_TAPEHE()); // <name="visa_b2_33_0_DSPLUSBIN_TAPEHE">;
                        break;

                      case "TAPETR":
                        addMessage(new visa_b2_33_0_DSPLUSBIN_TAPETR()); // <name="visa_b2_33_0_DSPLUSBIN_TAPETR">;
                        break;

                      default:
                        addMessage(new visa_b2_33_0_DSPLUSBIN()); // <name="visa_b2_33_0_DSPLUSBIN">;
                    }
                    break;

                  case "CFGEANSBII":
                    addMessage(new visa_b2_33_0_CFGEANSBII()); // <name="visa_b2_33_0_CFGEANSBII">;
                    break;

                  case "BIIDCCURR1":
                  case "BIIDCCURR2":
                  case "BIIDCCURR3":
                    addMessage(new visa_b2_33_0_BIIDCCURR1()); // <name="visa_b2_33_0_BIIDCCURR1">;
                    break;

                  case "SMSRAWDATA":
                    String report_type = ReadString(StartLine + 34, 6); //

                    switch (report_type) {
                      case "V22000":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22000()); // <name="visa_v2_33_0_SMSRAWDATA_HEADER">;
                        break;
                      case "V22120":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22120()); // <name="visa_v2_33_0_SMSRAWDATA_SETTLEMENT_INFO">;
                        break;
                      case "V22220":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22220()); // <name="visa_v2_33_0_SMSRAWDATA_V22220">;
                        break;
                      case "V22225":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22225()); // <name="visa_v2_33_0_SMSRAWDATA_V22225">;
                        break;
                      case "V22210":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22210()); // <name="visa_v2_33_0_SMSRAWDATA_V22210">;
                        break;
                      case "V22200":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22200()); // <name="visa_v2_33_0_SMSRAWDATA_V22200">;
                        break;
                      case "V22240":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22240()); // <name="visa_v2_33_0_SMSRAWDATA_V22240">;
                        break;
                      case "V22270":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22270()); // <name="visa_v2_33_0_SMSRAWDATA_V22270_STR">;
                        break;
                      case "V22255":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22255()); // <name="visa_v2_33_0_SMSRAWDATA_V22255_STR">;
                        break;
                      case "V22261":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22261()); // <name="visa_v2_33_0_SMSRAWDATA_V22261">;
                        break;
                      case "V22900":
                        addMessage(new visa_v2_33_0_SMSRAWDATA_V22900()); // <name="visa_v2_33_0_SMSRAWDATA_TRAILER">;
                        break;
                      default:
                        addMessage(new visa_v2_33_0_SMSRAWDATA_UNSUPPORTED()); // <name="visa_v2_33_0_SMSRAWDATA_UNSUPPORTED">;
                        break;
                    }
                  case "FEE400S000":
                  case "FEE401S000":
                  case "FEE402S000":
                  case "FEE403S000":
                    addMessage(new visa_v2_33_0_FEE_S()); // <name="visa_v2_33_0_FEE_S">;
                    break;
                  case "FEE400D000":
                  case "FEE401D000":
                  case "FEE402D000":
                  case "FEE403D000":
                    addMessage(new visa_v2_33_0_FEE_D()); // <name="visa_v2_33_0_FEE_D">;
                    break;
                  default:
                    addMessage(new visa_b2_33_0()); // <name="visa_b2_33_0">;
                }
            }
            break;

          case "1":
            String report_identifier = ReadString(StartLine + 12, 7);
            switch (report_identifier) {
              case "FEE400D":
              case "FEE401D":
              case "FEE402D":
              case "FEE403D":
                addMessage(new visa_v2_33_1_FEE()); // <name="visa_v2_33_1_FEE">;
                break;
              default:
                addMessage(new visa_b2_33_1()); // <name="visa_b2_33_1">;
                break;
            }

        }
        break;

      case "40":

        switch (trans_comp_seq) {
          case "0":

            String response_code = ReadString(StartLine + 70, 2); //

            switch (response_code) {
              case "TS":
                addMessage(new visa_b2_40_0_TS()); // <name="visa_b2_40_0_TS">;
                break;

              case "MS":
                addMessage(new visa_b2_40_0_MS()); // <name="visa_b2_40_0_MS">;
                break;

              case "TR":
                addMessage(new visa_b2_40_0_TR()); // <name="visa_b2_40_0_TR">;
                break;

              default:
                addMessage(new visa_b2_40_0()); // <name="visa_b2_40_0">;

            }
            break;

          case "1":
            addMessage(new visa_b2_40_1()); // <name="visa_b2_40_1">;
            break;

          case "2":
            addMessage(new visa_b2_40_2()); // <name="visa_b2_40_2">;
            break;

          case "3":
            addMessage(new visa_b2_40_3()); // <name="visa_b2_40_3">;
            break;

          case "4":
            addMessage(new visa_b2_40_4()); // <name="visa_b2_40_4">;
            break;

          // case "7":
          // visa_b2_33_1_STR visa_b2_33_7_38 <name="visa_b2_33_7_38">;
          // break;

        }
        break;

      case "46":

        switch (trans_comp_seq) {
          case "0":
            addMessage(new visa_b2_46_0()); // <name="visa_b2_46_0">;
            break;

          case "1":
            addMessage(new visa_b2_46_1()); // <name="visa_b2_46_1">;
            break;

          default:
            addMessage(new visa_b2_46()); // <name="visa_b2_46">;
        }
        break;

      case "48":

        switch (trans_comp_seq) {

          case "0":

            String format_code = ReadString(StartLine + 16, 1); //

            switch (format_code) {
              case "0":
                addMessage(new visa_b2_48_0_0()); // <name="visa_b2_48_0_0">;
                break;

              case "1":
                addMessage(new visa_b2_48_0_1()); // <name="visa_b2_48_0_1">;
                break;

              case "2":
                addMessage(new visa_b2_48_0_2()); // <name="visa_b2_48_0_2">;
                break;
            }
            break;

          case "1":
            addMessage(new visa_b2_48_1()); // <name="visa_b2_48_1">;
            break;

          case "2":
            addMessage(new visa_b2_48_2()); // <name="visa_b2_48_2">;
            break;

          case "6":
            addMessage(new visa_b2_48_6()); // <name="visa_b2_48_6">;
            break;

          case "7":
            addMessage(new visa_b2_48_7()); // <name="visa_b2_48_7">;
            break;

        }
        break;

      case "50":

        switch (trans_comp_seq) {

          case "0":

            String service_id = ReadString(StartLine + 16, 6); //

            switch (service_id) {

              case "PURCHA":
              case "CORPLA":
              case "COMMLA":
              case "CORPCD":
              case "COMMCA":
                addMessage(new visa_b2_50_0_PURCHA()); // <name="visa_b2_50_0_PURCHA">;
                break;

              case "PURCHL":
              case "CORPLL":
              case "COMMLL":
              case "CORPCL":
              case "COMMCL":
                addMessage(new visa_b2_50_0_PURCHL()); // <name="visa_b2_50_0_PURCHL">;
                break;

              case "CORPAI":
              case "COMMAG":
                addMessage(new visa_b2_50_0_CORPAI()); // <name="visa_b2_50_0_CORPAI">;
                break;

              case "CORPAS":
              case "COMMAS":
                addMessage(new visa_b2_50_0_CORPAS()); // <name="visa_b2_50_0_CORPAS">;
                break;

              case "CORPLG":
                addMessage(new visa_b2_50_0_CORPLG()); // <name="visa_b2_50_0_CORPLG">;
                break;

              case "CORPCA":
                addMessage(new visa_b2_50_0_CORPCA()); // <name="visa_b2_50_0_CORPCA">;
                break;

              case "OPNFMT":
                addMessage(new visa_b2_50_0_OPNFMT()); // <name="visa_b2_50_0_OPNFMT">;
                break;

              default:
                addMessage(new visa_b2_50_0()); // <name="visa_b2_50_0">;

            }
            break;

          default:
            addMessage(new visa_b2_50()); // <name="visa_b2_50">;

        }
        break;

      case "52":

        switch (trans_comp_seq) {
          case "0":
            addMessage(new visa_b2_52_0()); // <name="visa_b2_52_0">;
            break;
          case "1":
            addMessage(new visa_b2_52_1()); // <name="visa_b2_52_1">;
            break;
          case "4":
            addMessage(new visa_b2_52_4()); // <name="visa_b2_52_4">;
            break;

          case "5":
            addMessage(new visa_b2_52_4()); // <name="visa_b2_52_5">;
            break;
          case "6":
            addMessage(new visa_b2_52_4()); // <name="visa_b2_52_6">;
            break;
          case "7":
            addMessage(new visa_b2_52_4()); // <name="visa_b2_52_7">;
            break;
          case "8":
            addMessage(new visa_b2_52_4()); // <name="visa_b2_52_8">;
            break;

          default:
            FSkip(168);
            // BYTE undefined [168] <open=suppress, name="Undefined trans_comp_seq">;

        }
        break;
      case "51":
      case "53":

        switch (trans_comp_seq) {

          case "0":
            addMessage(new visa_b2_51_0()); // <name="visa_b2_51_0">;
            break;

          case "1":
            addMessage(new visa_b2_51_1()); // <name="visa_b2_51_1">;
            break;

          case "9":
            addMessage(new visa_b2_51_9()); // <name="visa_b2_51_9">;
            break;

          default:
            addMessage(new visa_b2_51()); // <name="visa_b2_51">;

        }
        break;

      case "56":

        switch (trans_comp_seq) {

          case "0":
            addMessage(new visa_b2_56_0()); // <name="visa_b2_56_0">;
            break;

          case "1":
            addMessage(new visa_b2_56_1()); // <name="visa_b2_56_1">;
            break;
        }
        break;

      case "90":
        addMessage(new visa_b2_90()); // <name="visa_b2_90">;
        break;

      case "91":
      case "92":
        addMessage(new visa_b2_91()); // <name="visa_b2_91">;
        break;

      case "55":
        addMessage(new visa_b2_55()); // <name="visa_b2_55">;
        break;
      default:
        addMessage(new visa_b2());
        // visa_b2_STR visa_b2 <name="visa_b2">;
    }
  }

  // public void Memcpy(IsoField fld, String data, int size, int i, int j) {

  // }

  void correctCount() {
    String temp = "";
    int record_no = 0;
    int count = 0;
    int batch_no = 0;
    int tempNum;

    int totalRecords = 0;

    int totalBatch;
    int totalTr;
    int lineNum;

    int totalMoneyTr;
    int actual_mon_tr = 0;

    while (batch_no < VISA_BATCH.length && exists(VISA_BATCH[batch_no])) {
      while ((record_no + 1) < VISA_BATCH[batch_no].VISA_TRN.length
          && exists(VISA_BATCH[batch_no].VISA_TRN[record_no + 1])) {
        totalRecords++;
        record_no++;
      }
      // Exit(0);
      lineNum = TextAddressToLine(startof(VISA_BATCH[batch_no].VISA_TRN[record_no])) + 1;
      // update batch trailer number_of_money_transactions
      tempNum = Atoi(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91.number_of_mon_trans);
      if (tempNum != record_no) {
        // Printf("Warning: Line %d : number_of_monetary_transactions Required (%d),
        // Found (%d)\n", lineNum, record_no, tempNum);
        temp = SPrintf(temp, "%012d", record_no);
        Memcpy(VISA_BATCH[batch_no].VISA_TRN[record_no], "visa_b2_91.number_of_mon_trans", temp, 12, 0, 0);
      }

      // update batch trailer number_of_transactions
      tempNum = Atoi(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91.number_of_trans);
      if (tempNum != record_no + 1) {
        // Printf("Warning: Line %d : number_of_transactions Required (%d), Found
        // (%d)\n", lineNum, record_no+1, tempNum);
        temp = SPrintf(temp, "%09d", record_no + 1);
        Memcpy(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91._number_of_trans, temp, 9, 0, 0);
      }

      // update sequence num of batch trailer
      tempNum = Atoi(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91.number_of_batches);
      if (tempNum != batch_no + 1) {
        // Printf("Warning: Line %d : Batch_number Required (%d), Found (%d)\n",
        // lineNum, batch_no+1, tempNum);
        temp = SPrintf(temp, "%06d", batch_no + 1);
        Memcpy(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91._number_of_batches, temp, 6, 0, 0);
      }
      actual_mon_tr += record_no;
      record_no = 0;
      batch_no++;
      totalRecords++; // +1 because of batch trailer
    }

    totalRecords++; // +1 because of file trailer
    lineNum = TextAddressToLine(startof(VISA_TRN[1])) + 1;
    totalBatch = Atoi(VISA_TRN[1].visa_b2_91.number_of_batches);
    if (totalBatch != batch_no) {
      temp = SPrintf(temp, "%06d", batch_no);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_batches, temp, 6, 0, 0);
      // Printf("Warning: Line %d : Batch number in file trailer Required (%d), Found
      // (%d)\n", lineNum, batch_no, totalBatch);
    }

    totalTr = Atoi(VISA_TRN[1].visa_b2_91.number_of_trans);
    if (totalRecords != totalTr) {
      temp = SPrintf(temp, "%09d", totalRecords);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_trans, temp, 9, 0, 0);
      // Printf("Warning: Line %d : Number of transactions in file trailer Required
      // (%d), Found (%d)\n", lineNum, totalRecords, totalTr);
    }

    totalMoneyTr = Atoi(VISA_TRN[1].visa_b2_91.number_of_mon_trans);
    if (totalMoneyTr != actual_mon_tr) {
      temp = SPrintf(temp, "%012d", actual_mon_tr);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_mon_trans, temp, 12, 0, 0);
      // Printf("Warning: Line %d : Number of monetary transactions in file trailer
      // Required (%d), Found (%d)\n", lineNum, actual_mon_tr, totalMoneyTr);
    }

  }

  // correct source amount
  void correctAmount() {
    char[] temp;
    String amount = "000000000000";
    String amountF = "000000000000000";
    int batch_no = 0;
    int record_no = 0;
    int lineNum;

    while (batch_no < VISA_BATCH.length && exists(VISA_BATCH[batch_no])) {

      while (record_no < (VISA_BATCH[batch_no].VISA_TRN.length - 1)
          && exists(VISA_BATCH[batch_no].VISA_TRN[record_no + 1])) { // ??
        if (exists(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_05_0))
          amount = SumString(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_05_0.source_amount, amount);
        record_no++;
      }

      lineNum = TextAddressToLine(startof(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91, "source_amount"));

      amountF = SumString(amount, amountF);
      temp = VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91.source_amount;

      if (!amount.equals(new String(temp))) {
        // Printf("Warning: Line %d : source_amount Required (%s), Found (%s)\n",
        // lineNum, amount, temp);
        Memcpy(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91._source_amount, amount, 15, 0, 0);
      }

      record_no = 0;
      amount = "000000000000";
      batch_no++;
    }

    temp = VISA_TRN[1].visa_b2_91.source_amount;
    lineNum = TextAddressToLine(startof(VISA_TRN[1].visa_b2_91)) + 1;

    if (!amountF.equals(new String(temp))) {
      Memcpy(VISA_TRN[1].visa_b2_91._source_amount, amountF, 15, 0, 0);
      // Printf("Warning: Line %d : source_amount in file trailer Required (%s), Found
      // (%s)\n", lineNum, amountF, temp);
    }
  }

  void correctTCR() {
    int tcrCount = 0;
    int totalTcrCount = 0;
    int batch_no = 0;
    int record_no = 0;
    int tcrBatch = 0;
    int lineNum;
    char[] temp = new char[] {};

    int startLine;
    int endLine;

    while (batch_no < VISA_BATCH.length && exists(VISA_BATCH[batch_no])) {
      while (record_no < VISA_BATCH[batch_no].VISA_TRN.length && exists(VISA_BATCH[batch_no].VISA_TRN[record_no]))
        record_no++;
      record_no--;

      startLine = TextAddressToLine(startof(VISA_BATCH[batch_no]));
      endLine = TextAddressToLine(startof(VISA_BATCH[batch_no].VISA_TRN[record_no]));
      tcrCount = endLine - startLine + 1;

      // if (exists(VISA_AXS_HEADER))
      // tcrCount++;

      totalTcrCount += tcrCount;
      tcrBatch = Atoi(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91.number_of_tcr);
      if (tcrBatch != tcrCount) {
        // Printf("Warning: Line %d : number_of_tcrs Required (%d), Found (%d)\n",
        // endLine, tcrCount, tcrBatch);
        temp = SPrintf(temp, "%012d", tcrCount);
        Memcpy(VISA_BATCH[batch_no].VISA_TRN[record_no].visa_b2_91._number_of_tcr, temp, 12, 0, 0);
      }

      record_no = 0;
      tcrCount = 0;
      batch_no++;
    }

    lineNum = TextAddressToLine(startof(VISA_TRN[1].visa_b2_91)) + 1;

    totalTcrCount++; // add 1 for file trailer.
    temp = VISA_TRN[1].visa_b2_91.number_of_tcr;
    tcrCount = Atoi(temp);
    if (tcrCount != totalTcrCount) {
      temp = SPrintf(temp, "%012d", totalTcrCount);
      Memcpy(VISA_TRN[1].visa_b2_91._number_of_tcr, temp, 12, 0, 0);
      // Printf("Warning: Line %d : number_of_tcrs in file trailer Required (%d),
      // Found (%d)\n", lineNum, totalTcrCount, tcrCount);
    }

  }

  public void ParseVisaTrn() {
    // typedef struct {
    String trans_code = ReadString(FTell(), 2);

    PrintLog();
    VISA_TRN_ADD();

    do {
      ParseLine();
      SkipEndOfLine(1); // skip CRLF
    } while (!FEof() && (char) ReadByte(FTell() + 3) != '0');

    // } VISA_TRN_STR;
    // VISA_TRN_STR VISA_TRN <name=VisaTrnName>;
  }

  void parseReturnedTCR(String trans_comp_seq, int StartLine) {
    switch (trans_comp_seq) {
      case "0":
        addMessage(new visa_b2_05_0(1)); // <name="visa_b2_05_0">; // TCR0
        break;

      case "1":
        addMessage(new visa_b2_05_1(1)); // <name="visa_b2_05_1">; // TCR1
        break;

      case "2":
        String country_code = ReadString(StartLine + 16, 3); //
        switch (country_code) {
          case "SE ":
            addMessage(new visa_b2_05_2_SE(1)); // <name="visa_b2_05_2_SE">; // TCR2
            break;

          case "BR ":
            addMessage(new visa_b2_05_2_BR(1)); // <name="visa_b2_05_2_BR">; // TCR2
            break;

          case "AR ":
            addMessage(new visa_b2_05_2_AR(1)); // <name="visa_b2_05_2_AR">; // TCR2
            break;

          default:
            addMessage(new visa_b2_05_2(1)); // <name="visa_b2_05_2">; // TCR2
        }
        break;

      case "3":
        String bus_format_code = ReadString(StartLine + 16, 2); //

        switch (bus_format_code) {
          case "AI":
            addMessage(new visa_b2_05_3_AI(1)); // <name="visa_b2_05_3_AI">; //TCR3Airline
            break;

          case "LG":
            addMessage(new visa_b2_05_3_LG(1)); // <name="visa_b2_05_3_LG">; // TCR3Lodging
            break;

          case "CA":
            addMessage(new visa_b2_05_3_CA(1)); // <name="visa_b2_05_3_CA">; // TCR3AutoRental
            break;

          case "FL":
            addMessage(new visa_b2_05_3_FL(1)); // <name="visa_b2_05_3_FL">;
            break;

          case "CR":
            addMessage(new visa_b2_05_3_CR(1)); // <name="visa_b2_05_3_CR">; //TCR3AutoRental
            break;

          case "AN":
            addMessage(new visa_b2_05_3_AN(1)); // <name="visa_b2_05_3_AN">;
            break;

          default:
            addMessage(new visa_b2_05_3(1)); // <name="visa_b2_05_3">;
        }
        break;

      case "4":
        addMessage(new visa_b2_05_4_SVCRFD(1)); // <name="visa_b2_05_4">;
        break;

      case "5":
        addMessage(new visa_b2_05_5(1)); // <name="visa_b2_05_5">; // TCR5PaymSvc
        break;

      case "6":
        addMessage(new visa_b2_05_6(1)); // <name="visa_b2_05_6">; // TCR6
        break;

      case "7":
        addMessage(new visa_b2_05_7(1)); // <name="visa_b2_05_7">; // TCR7SmartCard
        break;

      case "8":
        addMessage(new visa_b2_05_8(1)); // <name="visa_b2_05_8">;
        break;

      case "9":
        addMessage(new visa_b2_05_9(1)); // <name="visa_b2_05_9">;
        break;

      case "E":
        bus_format_code = ReadString(StartLine + 4, 2); //

        switch (bus_format_code) {
          case "JA":
            addMessage(new visa_b2_05_E_JA(1)); // <name="visa_b2_05_E_JA">; //TCR3Airline
            break;

          default:
            addMessage(new visa_b2_05_E(1)); // <name="visa_b2_05_E">;
        }
    }
  }

  public int TextGetLineSize(int lineNum, boolean check) {
    String[] arrays = TextLines();
    if (lineNum < 0 || lineNum >= arrays.length)
      return 0;
    String line = arrays[lineNum - 1];
    return line.length();
  }

  public int TextGetNumLines() {
    return TextLines().length;
  }

  public String[] TextLines() {
    return ReadFileContent().split(AsciiMessage.lineSeperator);
  }

  public int TextAddressToLine(int offset) {
    int i = offset / 170;
    return i;
  }

  @Override
  public void RunTemplate(String template) {
    if (template.equals(visa_template_path)) {
      MakeTemplate(new String[] {});
      return;
    }
  }

  @Override
  public void include(String template) {
    if (template.contains("PrepareFile")) {
      PrepareFile();
      return;
    }
  }

  @Override
  public void run(String[] args1) {
    this.args = args1;
    FileOpen(); // incase file reader is closed !
    MakeTemplate(args1);
  }

  public void MakeTemplate(String[] args) {
    VISA_BATCH = new VisaBatch[] {};
    VISA_TRN = new VisaTrans[] {};
    getIsoFile().getIsoMessages().clear();

    BigEndian();
    FileOpen();
    FSeek(0);
    SetFileInterface("ASCII");
    System.out.println("-----");

    int ready = 0;
    String transcode;

    ParseVisaTrn(); // File Header

    // transcode = ReadString(FTell(), 2);
    do {

      transcode = ReadString(FTell(), 2);
      if (Strcmp(transcode, "92") != 0) {
        VISA_BTCH_ADD();
        // typedef struct {
        do {
          transcode = ReadString(FTell(), 2);
          ParseVisaTrn();
        } while (!FEof()
            && !((Strcmp(transcode, "90") == 0) || (Strcmp(transcode, "91") == 0) || (Strcmp(transcode, "92") == 0)));
        // } VISA_BTCH_STR;
        // VISA_BTCH_STR VISA_BATCH <name="Batch">;

      } else {
        currBatch = null; // is trailer now
        ParseVisaTrn();
        SkipEndOfLine(1); // skip CRLF
        // ready = 1;
      }
    } while (!FEof() && (ready == 0));

    System.out.println("-----");
  }

  void saveMessage(AsciiMessage msg) {
    int msgLength = 0;
    msg.updateFromProperties();

    for (MyField fld : msg.getFields()) {
      // System.out.println(fld.asText());
      msgLength += fld.length;
      if (msgLength <= msg.maxLength)
        WriteBytes(fld.getData(), fld.offset, fld.length);
    }
  }

  public void FileSave() {
    for (VisaTrans tran : VISA_TRN) {
      for (AsciiMessage msg : tran.messages) {
        saveMessage(msg);
        // LogManager.getLogger().debug(msg.asText());
      }
    }

    for (VisaBatch batch : VISA_BATCH) {
      for (VisaTrans tran1 : batch.VISA_TRN) {
        for (AsciiMessage msg : tran1.messages) {
          saveMessage(msg);
          // LogManager.getLogger().debug(msg.asText());
        }
      }
    }
  }

  public static AsciiMessage createAsciiMessage(int offset, String line) {
    String trans_code = line.substring(0, 2);
    String trans_comp_seq = line.substring(3, 4);
    AsciiMessage lineArray = null;
    switch (trans_code) {
      case "05": // TCR
      case "06": // TCR
      case "07": // TCR
      case "15":
      case "16":
      case "17":
      case "25":
      case "26":
      case "27":
      case "35":
      case "36":
      case "37":
        switch (trans_comp_seq) {
          case "0":
            lineArray = new visa_b2_05_0(offset, line);
            break;
          case "1":
            lineArray = new visa_b2_05_1(offset, line);
            break;

          case "2":
            lineArray = new visa_b2_05_2(offset, line);
            break;
          case "6":
            lineArray = new visa_b2_05_6(offset, line);
            break;
        }
        break;

      case "90":
        lineArray = new visa_b2_90(offset, line);
        break;

      case "91":
      case "92":
        lineArray = new visa_b2_91(offset, line);
        break;
      default:
        // code block
    }
    return lineArray;
  }

  @Override
  public IsoFileTemplate createTemplate(String action, String fileName) {
    if (action.equals(ACTION_GEN_CHARGE_BACK))
      return new VisaGenChargeBack(fileName);
    return this;
  }
}
