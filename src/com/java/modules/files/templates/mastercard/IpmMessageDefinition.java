package com.java.modules.files.templates.mastercard;

import java.util.*;

import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.Trace;
import com.java.modules.files.isoparser.elements.iso.IsoField;
import com.java.modules.files.isoparser.elements.iso.IsoFile;
import com.java.modules.files.isoparser.elements.iso.IsoMessage;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition;
import com.java.modules.files.isoparser.structure.IsoMessageDefinition;
import com.java.modules.files.isoparser.structure.IsoFieldDefinition.LengthType;
import com.java.modules.files.isoparser.structure.IsoMessageDefinition.ApplicationDataParseError;

/*
ISO 8583 parser
Original coDEby Sergey V. Shakshin (rigid.mgn@gmail.com)

Mastercard IPM File structure definition class
 */

public class IpmMessageDefinition extends IsoMessageDefinition {
    private String pdsBuffer = "";

    private Long checksum = Long.valueOf(0);
    private boolean checksumProblem = false;
    private int[] emvIndexes = new int[] { 55 };
    private int[] pdsIndexes = new int[] { 48, 62, 123, 124, 125 };

    private void addFields1() {
        addField(2, IsoFieldDefinition.LengthType.Embedded, 2, "DE_2", "002 - Primary Account Number (PAN)", false);
        addSubField(3, IsoFieldDefinition.LengthType.Fixed, 2, "DE_3_01", "01 - Cardholder Transaction Type", false);
        addSubField(3, IsoFieldDefinition.LengthType.Fixed, 2, "DE_3_02", "02 - Cardholder From Account Code", false);
        addSubField(3, IsoFieldDefinition.LengthType.Fixed, 2, "DE_3_03", "03 - Cardholder To Account Code", false);
        addField(3, IsoFieldDefinition.LengthType.Fixed, 0, "DE_3", "003 - Processing Code", false);
        addField(4, IsoFieldDefinition.LengthType.Fixed, 12, "DE_4", "004 - Amount, Txn", false);
        addField(5, IsoFieldDefinition.LengthType.Fixed, 12, "DE_5", "005 - Amount, Reconciliation", false);
        addField(6, IsoFieldDefinition.LengthType.Fixed, 12, "DE_6", "006 - Amount, Cardholder Billing", false);
        addField(7, IsoFieldDefinition.LengthType.Fixed, 10, "DE_7", "007 - Date and Time, Transmission", false);
        addField(8, IsoFieldDefinition.LengthType.Fixed, 8, "DE_8", "008 - Amount, Cardholder Billing Fee", false);
        addField(9, IsoFieldDefinition.LengthType.Fixed, 8, "DE_9", "009 - Conversion Rate, Reconciliation", false);
        addField(10, IsoFieldDefinition.LengthType.Fixed, 8, "DE_10", "010 - Conversion Rate, Cardholder Billing",
                false);
        addField(11, IsoFieldDefinition.LengthType.Fixed, 6, "DE_11", "011 - Systems Trace Audit Number", false);
        addSubField(12, IsoFieldDefinition.LengthType.Fixed, 6, "DE_12_01", "01 - Date", false);
        addSubField(12, IsoFieldDefinition.LengthType.Fixed, 6, "DE_12_02", "02 - Time", false);
        addField(12, IsoFieldDefinition.LengthType.Fixed, 0, "DE_12", "012 - Date and Time, Txn", false);
        addField(13, IsoFieldDefinition.LengthType.Fixed, 4, "DE_13", "013 - Date, Effective", false);
        addField(14, IsoFieldDefinition.LengthType.Fixed, 4, "DE_14", "014 - Date, Expiration", false);
        addField(15, IsoFieldDefinition.LengthType.Fixed, 4, "DE_15", "015 - Date, Settlement", false);
        addField(16, IsoFieldDefinition.LengthType.Fixed, 4, "DE_16", "016 - Date, Conversion", false);
        addField(17, IsoFieldDefinition.LengthType.Fixed, 4, "DE_17", "017 - Date, Capture", false);
        addField(18, IsoFieldDefinition.LengthType.Fixed, 4, "DE_18", "018 - Merchant Type", false);
        addField(19, IsoFieldDefinition.LengthType.Fixed, 3, "DE_19", "019 - Country Code, Acquiring Inst", false);
        addField(20, IsoFieldDefinition.LengthType.Fixed, 3, "DE_20", "020 - Country Code, Primary Account Number",
                false);
        addField(21, IsoFieldDefinition.LengthType.Fixed, 3, "DE_21", "021 - Country Code, Forwarding Inst", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_01",
                "01 - Terminal Data: Card Data Input Capability", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_02",
                "02 - Terminal Data: CardHolder Authentication Capability", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_03",
                "03 - Terminal Data: Card Capture Capability", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_04", "04 - Terminal Operating Environment",
                false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_05", "05 - Cardholder Present Data", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_06", "06 - Card Present Data", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_07", "07 - Card Data: Input Mode", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_08", "08 - CardHolder Authentication Method",
                false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_09", "09 - CardHolder Authentication Entity",
                false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_10", "10 - Card Data Output Capability", false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_11", "11 - Terminal Data Output Capability",
                false);
        addSubField(22, IsoFieldDefinition.LengthType.Fixed, 1, "DE_22_12", "12 - Pin Capture Capability", false);
        addField(22, IsoFieldDefinition.LengthType.Fixed, 0, "DE_22", "022 - Point of Service Data Code", false);
        addField(23, IsoFieldDefinition.LengthType.Fixed, 3, "DE_23", "023 - Card Sequence Number", false);
        addField(24, IsoFieldDefinition.LengthType.Fixed, 3, "DE_24", "024 - Function Code", false);
        addField(25, IsoFieldDefinition.LengthType.Fixed, 4, "DE_25", "025 - Message Reason Code", false);
        addField(26, IsoFieldDefinition.LengthType.Fixed, 4, "DE_26", "026 - Card Acceptor Business Code (MCC)", false);
        addField(27, IsoFieldDefinition.LengthType.Fixed, 1, "DE_27", "027 - Approval Code Length", false);
        addField(28, IsoFieldDefinition.LengthType.Fixed, 3, "DE_28", "028 - Date, Reconciliation", false);
        addField(29, IsoFieldDefinition.LengthType.Fixed, 3, "DE_29", "029 - Reconciliation Indicator", false);
        addSubField(30, IsoFieldDefinition.LengthType.Fixed, 12, "DE_30_01", "01 - Original Amount, Transaction",
                false);
        addSubField(30, IsoFieldDefinition.LengthType.Fixed, 12, "DE_30_02", "02 - Original Amount, Reconciliation",
                false);
        addField(30, IsoFieldDefinition.LengthType.Fixed, 0, "DE_30", "030 - Amounts, Original", false);
        addSubField(31, IsoFieldDefinition.LengthType.Fixed, 1, "DE_31_01", "01 - Mixed Use", false);
        addSubField(31, IsoFieldDefinition.LengthType.Fixed, 6, "DE_31_02", "02 - Acquirer's Bin", false);
        addSubField(31, IsoFieldDefinition.LengthType.Fixed, 4, "DE_31_03", "03 - Julian Processing Date YDDD", false);
        addSubField(31, IsoFieldDefinition.LengthType.Fixed, 11, "DE_31_04", "04 - Acquirer's Sequence Number", false);
        addSubField(31, IsoFieldDefinition.LengthType.Fixed, 1, "DE_31_05", "05 - Check Digit", false);
        addField(31, IsoFieldDefinition.LengthType.Fixed, 0, "DE_31", "031 - Acquirer Reference Data", false);
        addField(32, IsoFieldDefinition.LengthType.Embedded, 2, "DE_32", "032 - Acquirer Inst Id Code", false);
        addField(33, IsoFieldDefinition.LengthType.Embedded, 2, "DE_33", "033 - Forwarding Inst Id Code", false);
        addField(34, IsoFieldDefinition.LengthType.Fixed, 3, "DE_34", "034 - Primary Account Number, Extended", false);
        addField(35, IsoFieldDefinition.LengthType.Fixed, 3, "DE_35", "035 - Track 2 Data", false);
        addField(36, IsoFieldDefinition.LengthType.Fixed, 3, "DE_36", "036 - Track 3 Data", false);
        addField(37, IsoFieldDefinition.LengthType.Fixed, 12, "DE_37", "037 - Retrieval Reference Number", false);
        addField(38, IsoFieldDefinition.LengthType.Fixed, 6, "DE_38", "038 - Approval Code", false);
        addField(39, IsoFieldDefinition.LengthType.Fixed, 3, "DE_39", "039 - Action Code", false);
        addField(40, IsoFieldDefinition.LengthType.Fixed, 3, "DE_40", "040 - Service Code", false);
        addField(41, IsoFieldDefinition.LengthType.Fixed, 8, "DE_41", "041 - Card Acceptor Terminal Id", false);
        addField(42, IsoFieldDefinition.LengthType.Fixed, 15, "DE_42", "042 - Card Acceptor Id Code", false);
        addField(43, IsoFieldDefinition.LengthType.Embedded, 2, "DE_43", "043 - Unparsed Data", false);
        addField(44, IsoFieldDefinition.LengthType.Embedded, 3, "DE_44", "044 - Additional Response Data", false);
        addField(45, IsoFieldDefinition.LengthType.Fixed, 3, "DE_45", "045 - Track 1 Data", false);
        addField(46, IsoFieldDefinition.LengthType.Fixed, 3, "DE_46", "046 - Amounts, Fees", false);
        addField(47, IsoFieldDefinition.LengthType.Fixed, 3, "DE_47", "047 - Additional Data - National", false);
        addField(48, IsoFieldDefinition.LengthType.Fixed, 0, "DE_48", "048 - Additional Data - Private", false);
        addField(49, IsoFieldDefinition.LengthType.Fixed, 3, "DE_49", "049 - Currency Code, Txn", false);
        addField(50, IsoFieldDefinition.LengthType.Fixed, 3, "DE_50", "050 - Currency Code, Reconciliation", false);
        addField(51, IsoFieldDefinition.LengthType.Fixed, 3, "DE_51", "051 - Currency Code, Cardholder Billing", false);
        addField(52, IsoFieldDefinition.LengthType.Fixed, 3, "DE_52", "052 - Personal Id Number (PIN) Data", false);
        addField(53, IsoFieldDefinition.LengthType.Fixed, 3, "DE_53", "053 - Security Related Control Information",
                false);
        addField(54, IsoFieldDefinition.LengthType.Fixed, 0, "DE_54", "054 - Amounts, Additional", false);
        addField(55, IsoFieldDefinition.LengthType.Fixed, 0, "DE_55", "055 - IC Card System Related Data (ICC)", false);
        addField(56, IsoFieldDefinition.LengthType.Fixed, 3, "DE_56", "056 - Original Data Elements", false);
        addField(57, IsoFieldDefinition.LengthType.Fixed, 3, "DE_57", "057 - Authorization Life Cycle Code", false);
        addField(58, IsoFieldDefinition.LengthType.Fixed, 3, "DE_58", "058 - Authorizing Agent Inst Id Code", false);
        addField(59, IsoFieldDefinition.LengthType.Fixed, 3, "DE_59", "059 - Transport Data", false);
        addField(60, IsoFieldDefinition.LengthType.Fixed, 3, "DE_60", "060 - Reserved for National use", false);
        addField(61, IsoFieldDefinition.LengthType.Fixed, 3, "DE_61", "061 - Reserved for National use", false);
        addField(62, IsoFieldDefinition.LengthType.Embedded, 3, "DE_62", "062 - Additional Data 2", false);
        addSubField(63, IsoFieldDefinition.LengthType.Fixed, 1, "DE_63_01", "01 - Life Cycle Support Indicator", false);
        addSubField(63, IsoFieldDefinition.LengthType.Fixed, 15, "DE_63_02", "02 - Trace ID", false);
        addField(63, IsoFieldDefinition.LengthType.Fixed, 0, "DE_63", "063 - Transaction Life Cycle ID", false);
        addField(64, IsoFieldDefinition.LengthType.Fixed, 3, "DE_64", "064 - Message Authentication Code Field", false);
        addField(65, IsoFieldDefinition.LengthType.Fixed, 3, "DE_65", "065 - Reserved for ISO use", false);
        addField(66, IsoFieldDefinition.LengthType.Fixed, 3, "DE_66", "066 - Amounts, Original Fees", false);
        addField(67, IsoFieldDefinition.LengthType.Fixed, 3, "DE_67", "067 - Extended Payment Data", false);
        addField(68, IsoFieldDefinition.LengthType.Fixed, 3, "DE_68", "068 - Country Code, Receiving Inst", false);
        addField(69, IsoFieldDefinition.LengthType.Fixed, 3, "DE_69", "069 - Country Code, Settlement Inst", false);
        addField(70, IsoFieldDefinition.LengthType.Fixed, 3, "DE_70", "070 - Country Code, Authorizing Agent Inst",
                false);
        addField(71, IsoFieldDefinition.LengthType.Fixed, 8, "DE_71", "071 - Message Number", false);
        addField(72, IsoFieldDefinition.LengthType.Embedded, 3, "DE_72", "072 - Data Record", false);
        addField(73, IsoFieldDefinition.LengthType.Fixed, 6, "DE_73", "073 - Date, Action", false);
        addField(74, IsoFieldDefinition.LengthType.Fixed, 3, "DE_74", "074 - Credits, Number", false);
        addField(75, IsoFieldDefinition.LengthType.Fixed, 3, "DE_75", "075 - Credits, Reversal Number", false);
        addField(76, IsoFieldDefinition.LengthType.Fixed, 3, "DE_76", "076 - Debits, Number", false);
        addField(77, IsoFieldDefinition.LengthType.Fixed, 3, "DE_77", "077 - Debits, Reversal Number", false);
        addField(78, IsoFieldDefinition.LengthType.Fixed, 3, "DE_78", "078 - Transfer, Number", false);
        addField(79, IsoFieldDefinition.LengthType.Fixed, 3, "DE_79", "079 - Transfer, Reversal Number", false);
        addField(80, IsoFieldDefinition.LengthType.Fixed, 3, "DE_80", "080 - Inquiries, Number", false);
        addField(81, IsoFieldDefinition.LengthType.Fixed, 3, "DE_81", "081 - Authorizations, Number", false);
        addField(82, IsoFieldDefinition.LengthType.Fixed, 3, "DE_82", "082 - Inquiries, Reversal Number", false);
        addField(83, IsoFieldDefinition.LengthType.Fixed, 3, "DE_83", "083 - Payments, Number", false);
        addField(84, IsoFieldDefinition.LengthType.Fixed, 3, "DE_84", "084 - Payments, Reversal Number", false);
        addField(85, IsoFieldDefinition.LengthType.Fixed, 3, "DE_85", "085 - Fee Collections, Number", false);
        addField(86, IsoFieldDefinition.LengthType.Fixed, 3, "DE_86", "086 - Credits, Amount", false);
        addField(87, IsoFieldDefinition.LengthType.Fixed, 3, "DE_87", "087 - Credits, Reversal Amount", false);
        addField(88, IsoFieldDefinition.LengthType.Fixed, 3, "DE_88", "088 - Debits, Amount", false);
        addField(89, IsoFieldDefinition.LengthType.Fixed, 3, "DE_89", "089 - Debits, Reversal Amount", false);
        addField(90, IsoFieldDefinition.LengthType.Fixed, 3, "DE_90", "090 - Authorizations, Reversal Number", false);
        addField(91, IsoFieldDefinition.LengthType.Fixed, 3, "DE_91", "091 - Country Code, Txn Destination Inst",
                false);
        addField(92, IsoFieldDefinition.LengthType.Fixed, 3, "DE_92", "092 - Country Code, Txn Originator Inst", false);
        addField(93, IsoFieldDefinition.LengthType.Embedded, 2, "DE_93", "093 - Txn Destination Inst Id Code", false);
        addField(94, IsoFieldDefinition.LengthType.Embedded, 2, "DE_94", "094 - Txn Originator Inst Id Code", false);
        addField(95, IsoFieldDefinition.LengthType.Embedded, 2, "DE_95", "095 - Card Issuer Reference Data", false);
        addField(96, IsoFieldDefinition.LengthType.Fixed, 3, "DE_96", "096 - Key Management Data", false);
        addField(97, IsoFieldDefinition.LengthType.Fixed, 3, "DE_97", "097 - Amount, Net Reconciliation", false);
        addField(98, IsoFieldDefinition.LengthType.Fixed, 3, "DE_98", "098 - Payee", false);
        addField(99, IsoFieldDefinition.LengthType.Fixed, 3, "DE_99", "099 - Settlement Inst Id Code", false);
        addField(100, IsoFieldDefinition.LengthType.Embedded, 2, "DE_100", "100 - Receiving Inst Id Code", false);
        addField(101, IsoFieldDefinition.LengthType.Fixed, 3, "DE_101", "101 - File Name", false);
        addField(102, IsoFieldDefinition.LengthType.Fixed, 3, "DE_102", "102 - Account Id 1", false);
        addField(103, IsoFieldDefinition.LengthType.Fixed, 3, "DE_103", "103 - Account Id 2", false);
        addField(104, IsoFieldDefinition.LengthType.Fixed, 3, "DE_104", "104 - Txn Description", false);
        addField(105, IsoFieldDefinition.LengthType.Fixed, 3, "DE_105", "105 - Credits, Chargeback Amount", false);
        addField(106, IsoFieldDefinition.LengthType.Fixed, 3, "DE_106", "106 - Debits, Chargeback Amount", false);
        addField(107, IsoFieldDefinition.LengthType.Fixed, 3, "DE_107", "107 - Credits, Chargeback Number", false);
        addField(108, IsoFieldDefinition.LengthType.Fixed, 3, "DE_108", "108 - Debits, Chargeback Number", false);
        addField(109, IsoFieldDefinition.LengthType.Fixed, 3, "DE_109", "109 - Credits, Fee Amounts", false);
        addField(110, IsoFieldDefinition.LengthType.Fixed, 3, "DE_110", "110 - Debits, Fee Amounts", false);
        addField(111, IsoFieldDefinition.LengthType.Embedded, 3, "DE_111",
                "111 - Ammount Currency Conversion Assessment", false);
        addField(112, IsoFieldDefinition.LengthType.Fixed, 3, "DE_112", "112 - Reserved for ISO use", false);
        addField(113, IsoFieldDefinition.LengthType.Fixed, 3, "DE_113", "113 - Reserved for ISO use", false);
        addField(114, IsoFieldDefinition.LengthType.Fixed, 3, "DE_114", "114 - Reserved for ISO use", false);
        addField(115, IsoFieldDefinition.LengthType.Fixed, 3, "DE_115", "115 - Reserved for ISO use", false);
        addField(116, IsoFieldDefinition.LengthType.Fixed, 3, "DE_116", "116 - Reserved for National use", false);
        addField(117, IsoFieldDefinition.LengthType.Fixed, 3, "DE_117", "117 - Reserved for National use", false);
        addField(118, IsoFieldDefinition.LengthType.Fixed, 3, "DE_118", "118 - Reserved for National use", false);
        addField(119, IsoFieldDefinition.LengthType.Fixed, 3, "DE_119", "119 - Reserved for National use", false);
        addField(120, IsoFieldDefinition.LengthType.Fixed, 3, "DE_120", "120 - Reserved for National use", false);
        addField(121, IsoFieldDefinition.LengthType.Fixed, 3, "DE_121", "121 - Reserved for National use", false);
        addField(122, IsoFieldDefinition.LengthType.Fixed, 3, "DE_122", "122 - Reserved for National use", false);
        addField(123, IsoFieldDefinition.LengthType.Embedded, 3, "DE_123", "123 - Additional Data 3", false);
        addField(124, IsoFieldDefinition.LengthType.Embedded, 3, "DE_124", "124 - Additional Data 4", false);
        addField(125, IsoFieldDefinition.LengthType.Embedded, 3, "DE_125", "125 - Additional Data 5", false);
        addField(126, IsoFieldDefinition.LengthType.Fixed, 3, "DE_126", "126 - Reserved for Private use", false);
        addField(127, IsoFieldDefinition.LengthType.Embedded, 3, "DE_127", "127 - Network Data", false);
        addField(128, IsoFieldDefinition.LengthType.Fixed, 3, "DE_128", "128 - Message Authentication Code Fi", false);
    }

    private void addFields() {
        addField(2, IsoFieldDefinition.LengthType.Embedded, 2, "DE_2", "PAN - PRIMARY ACCOUNT NUMBER", false, true);
        addField(3, IsoFieldDefinition.LengthType.Fixed, 6, "DE_3", "PROCESSING CODE", false);
        addField(4, IsoFieldDefinition.LengthType.Fixed, 12, "DE_4", "AMOUNT, TRANSACTION", false);
        addField(5, IsoFieldDefinition.LengthType.Fixed, 12, "DE_5", "AMOUNT, SETTLEMENT", false);
        addField(6, IsoFieldDefinition.LengthType.Fixed, 12, "DE_6", "AMOUNT, CARDHOLDER BILLING", false);

        addField(9, IsoFieldDefinition.LengthType.Fixed, 8, "DE_9", "CONVERSION RATE, SETTLEMENT", false);
        addField(10, IsoFieldDefinition.LengthType.Fixed, 8, "DE_10", "CONVERSION RATE, CARDHOLDER BILLING", false);

        addField(12, IsoFieldDefinition.LengthType.Fixed, 12, "DE_12", "TIME, LOCAL TRANSACTION", false);

        addField(14, IsoFieldDefinition.LengthType.Fixed, 4, "DE_14", "DATE, EXPIRATION", false);
        addField(16, IsoFieldDefinition.LengthType.Fixed, 4, "DE_16", "DATE, SETTLEMENT", false);

        addField(22, IsoFieldDefinition.LengthType.Fixed, 12, "DE_22", "POINT OF SERVICE ENTRY MODE", false);
        addField(23, IsoFieldDefinition.LengthType.Fixed, 3, "DE_23", "CARD SEQUENCE NUMBER", false);
        addField(24, IsoFieldDefinition.LengthType.Fixed, 3, "DE_24", "FUNCTIONAL CODE", false);
        addField(25, IsoFieldDefinition.LengthType.Fixed, 4, "DE_25", "POINT OF SERVICE CONDITION CODE", false);
        addField(26, IsoFieldDefinition.LengthType.Fixed, 4, "DE_26", "POINT OF SERVICE PIN CAPTURE CODE", false);

        addField(30, IsoFieldDefinition.LengthType.Fixed, 24, "DE_30", "AMOUNT, TRANSACTION PROCESSING FEE", false);
        addField(31, IsoFieldDefinition.LengthType.Embedded, 2, "DE_31", "AMOUNT, SETTLEMENT PROCESSING FEE", false);
        addField(32, IsoFieldDefinition.LengthType.Embedded, 2, "DE_32", "ACQUIRING INSTITUTION IDENT CODE", false);
        addField(33, IsoFieldDefinition.LengthType.Embedded, 2, "DE_33", "FORWARDING INSTITUTION IDENT CODE", false);

        addField(37, IsoFieldDefinition.LengthType.Fixed, 12, "DE_37", "RETRIEVAL REFERENCE NUMBER", false);
        addField(38, IsoFieldDefinition.LengthType.Fixed, 6, "DE_38", "AUTHORIZATION IDENTIFICATION RESPONSE", false);

        addField(40, IsoFieldDefinition.LengthType.Fixed, 3, "DE_40", "SERVICE RESTRICTION CODE", false);
        addField(41, IsoFieldDefinition.LengthType.Fixed, 8, "DE_41", "CARD ACCEPTOR TERMINAL IDENTIFICACION", false);
        addField(42, IsoFieldDefinition.LengthType.Fixed, 15, "DE_42", "CARD ACCEPTOR IDENTIFICATION CODE", false);
        addField(43, IsoFieldDefinition.LengthType.Embedded, 2, "DE_43", "CARD ACCEPTOR NAME/LOCATION", false);

        addField(48, IsoFieldDefinition.LengthType.Embedded, 3, "DE_48", "ADITIONAL DATA - PRIVATE", false);
        addField(49, IsoFieldDefinition.LengthType.Fixed, 3, "DE_49", "CURRENCY CODE, TRANSACTION", false);
        addField(50, IsoFieldDefinition.LengthType.Fixed, 3, "DE_50", "CURRENCY CODE, SETTLEMENT", false);
        addField(51, IsoFieldDefinition.LengthType.Fixed, 3, "DE_51", "CURRENCY CODE, CARDHOLDER BILLING", false);

        addField(54, IsoFieldDefinition.LengthType.Embedded, 3, "DE_54", "ADDITIONAL AMOUNTS", false);
        addField(55, IsoFieldDefinition.LengthType.Embedded, 3, "DE_55", "RESERVED ISO", true);

        addField(62, IsoFieldDefinition.LengthType.Embedded, 3, "DE_62", "RESERVED PRIVATE", false);
        addField(63, IsoFieldDefinition.LengthType.Embedded, 3, "DE_63", "RESERVED PRIVATE", false);

        addField(71, IsoFieldDefinition.LengthType.Fixed, 8, "DE_71", "MESSAGE NUMBER", false);
        addField(72, IsoFieldDefinition.LengthType.Embedded, 3, "DE_72", "MESSAGE NUMBER LAST", false);
        addField(73, IsoFieldDefinition.LengthType.Fixed, 6, "DE_73", "DATE ACTION", false);

        addField(93, IsoFieldDefinition.LengthType.Embedded, 2, "DE_93", "RESPONSE INDICATOR", false);
        addField(94, IsoFieldDefinition.LengthType.Embedded, 2, "DE_94", "SERVICE INDICATOR", false);
        addField(95, IsoFieldDefinition.LengthType.Embedded, 2, "DE_95", "REPLACEMENT AMOUNTS", false);
        addField(100, IsoFieldDefinition.LengthType.Embedded, 2, "DE_100", "RECEIVING INSTITUTION IDENT CODE", false);

        addField(111, IsoFieldDefinition.LengthType.Embedded, 3, "DE_111", "RESERVED ISO USE", false);
        addField(123, IsoFieldDefinition.LengthType.Embedded, 3, "DE_123", "RESERVED PRIVATE USE", false);
        addField(124, IsoFieldDefinition.LengthType.Embedded, 3, "DE_124", "RESERVED PRIVATE USE", false);
        addField(125, IsoFieldDefinition.LengthType.Embedded, 3, "DE_125", "RESERVED PRIVATE USE", false);
        addField(127, IsoFieldDefinition.LengthType.Embedded, 3, "DE_127", "RESERVED PRIVATE USE", false);
    }

    @Override
    public Map<Integer, IsoFieldDefinition> getFieldDefinitions() {

        Trace.log("MC", "Preparing field definitions");
        if (isoFieldDefinitions != null)
            return isoFieldDefinitions;

        isoFieldDefinitions = new HashMap<Integer, IsoFieldDefinition>();

        addFields();
        addFields1();
        if (isoFieldDefinitions.containsKey(43))
            isoFieldDefinitions.get(43).subFieldConnector = "\\";

        return isoFieldDefinitions;
    }

    @Override
    public void afterMessageParsed(IsoMessage msg) throws ApplicationDataParseError {

        if (msg.isoFields.containsKey(4)) {
            try {
                String de4 = msg.getIsoFields().get(4).parsedData;
                Long de4l = Long.valueOf(de4);
                checksum += de4l;
            } catch (Throwable e) {
                Trace.log("MC", "DE_4 could not be parsed to numeric value: " + e.getMessage());
                checksumProblem = true;
            }
        }

        Trace.log("MC", "Performing application-level parsing");
        try {
            parseEMV(msg);
            Trace.log("MC", "EMV parser finished");
        } catch (Exception e) {
        }

        Trace.log("MC", "parsing PDS-fields");
        parsePDS(msg);

        for (IsoField fld : msg.isoFields.values()) {
            parseSubFields(fld);
        }
    }

    @Override
    public void afterFileParsed(IsoFile file) {
        file.checksum = checksum.toString();
        file.checksumProblems = checksumProblem;
    }

    private String bufRead(Integer len) throws ApplicationDataParseError {
        if (len > pdsBuffer.length())
            throw new ApplicationDataParseError("No enough data in PDS buffer");
        String rd = pdsBuffer.substring(0, len);
        pdsBuffer = pdsBuffer.substring(len, pdsBuffer.length());
        return rd;
    }

    private void getPdsBuffer(IsoMessage msg) {
        Trace.log("MC", "Combining additional data fields to single buffer");
        for (int id : pdsIndexes) {
            pdsBuffer += msg.isoFields.containsKey(id) ? msg.isoFields.get(id).parsedData : "";

        }
        // pdsBuffer += msg.isoFields.containsKey(48) ? msg.isoFields.get(48).parsedData
        // : "";
        // pdsBuffer += msg.isoFields.containsKey(62) ? msg.isoFields.get(62).parsedData
        // : "";
        // pdsBuffer += msg.isoFields.containsKey(123) ?
        // msg.isoFields.get(123).parsedData : "";
        // pdsBuffer += msg.isoFields.containsKey(124) ?
        // msg.isoFields.get(124).parsedData : "";
        // pdsBuffer += msg.isoFields.containsKey(125) ?
        // msg.isoFields.get(125).parsedData : "";
    }

    private void parseEMV(IsoMessage msg) {
        Trace.log("MC", "Parsing EMV data");
        for (int id : emvIndexes) {
            if (msg.getIsoFields().get(id) == null)
                continue;

            IsoField de = msg.getIsoFields().get(id);
            byte[] raw = de.rawData;
            // DataGenService.parseBerTLV(de55.rawData, de55.getChildren(),
            // de55.appParserProblems);
            int offset = 0;
            while (offset < raw.length) {
                String tag = null;
                String lengthStr = null;
                Integer length = null;
                String data = null;

                tag = FilesService.bin2hex(raw[offset++]);

                if (tag.substring(1).equals("f")) {
                    if (offset == raw.length) {
                        Trace.error("Utils", "BerTLV tag read failed: " + tag);
                        de.appParserProblems
                                .add("Can not read next BerTLV tag name: no enough bytes in buffer. Current read data: "
                                        + tag);
                        break;
                    }

                    while (true) {
                        byte lastTagByte = raw[offset++];
                        tag += FilesService.bin2hex(lastTagByte);
                        BitSet bs = BitSet.valueOf(new byte[] { lastTagByte });
                        if (!bs.get(7)) {
                            break;
                        }
                    }
                }

                if (offset == raw.length) {
                    Trace.error("Utils", "Can not read BerTLV tag length: " + tag);
                    de.appParserProblems
                            .add("Can not read next BerTLV tag length: no enough bytes in buffer. Tag name: " + tag);
                    break;
                }
                lengthStr = FilesService.bin2hex(raw[offset++]);
                length = Integer.decode("0x" + lengthStr);

                if (offset + length > raw.length) {
                    Trace.error("Utils", "Can not read BerTLV tag data: " + tag);
                    de.appParserProblems.add("Can not read next BerTLV tag data: no enough bytes in buffer. Tag name: "
                            + tag + "; Declared length: " + length.toString() + "; Actual bytes: "
                            + (raw.length - offset));
                    break;
                }
                byte[] dataR = new byte[length];
                for (int i = 0; i < length; i++)
                    dataR[i] = raw[offset + i];

                data = FilesService.bin2hex(dataR);

                offset += length;

                IsoField fd = new IsoField();
                fd.name = tag;
                fd.parsedData = data;
                fd.definition = new IsoFieldDefinition(LengthType.Fixed, length, tag, "", false);

                de.getChildren().add(fd);
            }
        }
    }

    private String getPDSDescription(String tag) {
        switch (tag) {
            case "0105":
                return "FileID";
            case "0122":
                return "Processing Mode";
            default:
                return "";
        }
    }

    private void parsePDS(IsoMessage msg) {
        for (int id : pdsIndexes) {
            if (msg.getIsoFields().get(id) == null)
                continue;
            getPdsBuffer(msg);
            IsoField de = msg.getIsoFields().get(id);
            try {
                int offset = de.offset;
                while (pdsBuffer.length() > 0) {
                    String tag = bufRead(4);
                    Trace.log("MC", "PDS_ tag: " + tag);
                    String len = bufRead(3);
                    Trace.log("MC", "PDS_ length " + len);
                    Integer l = Integer.parseInt(len);
                    String data = bufRead(l);
                    Trace.log("MC", "PDS_ tag data read ok");

                    IsoField d = new IsoField();
                    d.name = "PDS_" + tag;
                    d.parsedData = data;

                    d.offset = offset + 7;
                    offset = offset + l;
                    d.definition = new IsoFieldDefinition(LengthType.Fixed, l, tag, getPDSDescription(tag), false);

                    // msg.fields.add(d);
                    de.getChildren().add(d);
                }
            } catch (Exception e) {
                Trace.log("MC", "Can not parse PDS: " + e.getMessage());
                // throw e;
                // throw new ApplicationDataParseError("Can not parse PDS: " + e.getMessage());
            }
        }
    }

    private void parseSubFields(IsoField field) {
        if (field.definition == null || field.definition.children == null || field.definition.getChildren().size() == 0)
            return;
        int beginIndex = 0, endIndex;
        for (IsoFieldDefinition def : field.definition.getChildren()) {
            IsoField d = new IsoField();
            d.name = def.name;
            d.description = def.description;
            endIndex = beginIndex + def.length;
            if (endIndex > field.parsedData.length())
                endIndex = field.parsedData.length();
            d.parsedData = field.parsedData.substring(beginIndex, endIndex);
            d.definition = def;

            beginIndex = endIndex;
            field.getChildren().add(d);
        }
    }

    // private void runSettleReport(IsoFile file) {
    // if (file.messages.size() > 0) {
    // HashMap<String, String> spdDt = new HashMap<>();
    // HashMap<String, Long> spdAmt = new HashMap<>();
    // HashMap<String, Long> fpdAmt = new HashMap<>();

    // Set<IsoMessage> fpds = new HashSet<>();
    // Set<IsoMessage> spds = new HashSet<>();
    // HashMap<String, HashSet<IsoMessage>> kSpds = new HashMap<>();

    // for (IsoMessage msg : file.messages) {
    // String mti = msg.header.mti;
    // String proc = msg.isoFields.get(24).parsedData;
    // String key = null;

    // if (!mti.equals("1644"))
    // continue;
    // // if (proc != "685" && proc != "688") continue;

    // IsoField f50 = msg.isoFields.get(50);
    // IsoField f49 = msg.isoFields.get(49);
    // String f300 = msg.namedFields.get("PDS_0300");
    // key = (f50 == null ? (f49 == null ? "null" : f49.parsedData) :
    // f50.parsedData) + ":" + f300;

    // switch (proc) {
    // case "685":
    // fpds.add(msg);
    // String f394 = msg.namedFields.get("PDS_0394");
    // String f395 = msg.namedFields.get("PDS_0395");
    // Long l394 = f394 == null ? 0 : Long.parseLong(f394.substring(1));
    // Long l395 = f395 == null ? 0 : Long.parseLong(f395.substring(1));
    // fpdAmt.put(key, (fpdAmt.get(key) == null ? 0 : fpdAmt.get(key)) + l394 +
    // l395);
    // break;

    // case "688":
    // spds.add(msg);

    // HashSet<IsoMessage> ss = kSpds.get(key);
    // if (ss == null)
    // ss = new HashSet<>();
    // ss.add(msg);
    // kSpds.put(key, ss);

    // String f359 = msg.namedFields.get("PDS_0359");
    // if (f359 != null)
    // spdDt.put(key, f359);

    // String f390 = msg.namedFields.get("PDS_0390");
    // String f391 = msg.namedFields.get("PDS_0391");
    // Long l390 = f390 == null ? 0 : Long.parseLong(f390.substring(1));
    // Long l391 = f391 == null ? 0 : Long.parseLong(f391.substring(1));

    // Long samt = l390 + l391;
    // spdAmt.put(key, (spdAmt.get(key) == null ? 0 : spdAmt.get(key)) + samt);

    // String f392 = msg.namedFields.get("PDS_0392");
    // if (f392 != null) {
    // for (String item : f392.split("(?<=\\G.{18})")) {
    // String sf2 = item.substring(3);
    // Long sf2l = Long.parseLong(sf2);
    // spdAmt.put(key, (spdAmt.get(key) == null ? 0 : spdAmt.get(key)) + sf2l);
    // }
    // }
    // String f393 = msg.namedFields.get("PDS_0393");
    // if (f393 != null) {
    // for (String item : f393.split("(?<=\\G.{18})")) {
    // String sf2 = item.substring(3);
    // Long sf2l = Long.parseLong(sf2);
    // spdAmt.put(key, (spdAmt.get(key) == null ? 0 : spdAmt.get(key)) + sf2l);
    // }
    // }

    // break;
    // default:
    // continue;
    // }
    // }

    // HashSet<String> keys = new HashSet<>();
    // keys.addAll(fpdAmt.keySet());
    // keys.addAll(spdAmt.keySet());

    // System.out.println("File: '" + file.fileName + "'; FPDS: " + fpdAmt.size() +
    // "; SPDS: " + spdAmt.size());

    // for (String key : keys) {
    // Long samt = spdAmt.get(key);
    // Long famt = fpdAmt.get(key);
    // System.out.print(
    // (famt != null && samt != null && famt.intValue() == samt.intValue() ? "
    // MATCHED" : "UNMATCHED")
    // + ": ");
    // System.out.print("Key " + key + "; ");
    // System.out.print("FPD " + (famt == null ? "NOT FOUND" :
    // fpdAmt.get(key).toString()) + "; ");
    // System.out.print("SPD " + (samt == null ? "NOT FOUND" : samt.toString()) + ";
    // ");

    // System.out.println();
    // }
    // System.out.println();
    // }
    // }

    // @Override
    // public void runReport(IsoFile file, String report) {
    // switch (report.toUpperCase()) {
    // case "SETTLE":
    // case "STM":
    // case "SETTLEMENT":
    // runSettleReport(file);
    // break;
    // default:
    // Trace.error("MC", "No such report");
    // }
    // }

    public static String getSubFieldStringConnector(String tag) {
        switch (tag.toUpperCase()) {
            case "DE_43_01":
            case "DE_43_02":
            case "DE_43_03":
                return "\\";
            default:
                return "";
        }
    }

}
