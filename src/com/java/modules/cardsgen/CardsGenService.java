package com.java.modules.cardsgen;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class CardsGenService {
    public static final String[] MASTERCARD_PREFIX_LIST = new String[] { "0626" };

    public static final String[] VISA_PREFIX_LIST = new String[] { "4539",
            "4556", "4916", "4532", "4929", "40240071", "4485", "4716", "4" };

    public static final String[] MASTERCARD_PREFIX_LIST2 = new String[] { "51",
            "52", "53", "54", "55" };

    public static final String[] AMEX_PREFIX_LIST = new String[] { "34", "37" };

    public static final String[] DISCOVER_PREFIX_LIST = new String[] { "6011" };

    public static final String[] DINERS_PREFIX_LIST = new String[] { "300",
            "301", "302", "303", "36", "38" };

    public static final String[] ENROUTE_PREFIX_LIST = new String[] { "2014",
            "2149" };

    public static final String[] JCB_PREFIX_LIST = new String[] { "35" };

    public static final String[] VOYAGER_PREFIX_LIST = new String[] { "8699" };

    static String strrev(String str) {
        if (str == null)
            return "";
        String revstr = "";
        for (int i = str.length() - 1; i >= 0; i--) {
            revstr += str.charAt(i);
        }

        return revstr;
    }

    public static String completed_number(String prefix, int length) {

        String ccnumber = prefix;

        while (ccnumber.length() < (length - 1)) {
            ccnumber += new Double(Math.floor(Math.random() * 10)).intValue();
        }

        String reversedCCnumberString = strrev(ccnumber);

        List<Integer> reversedCCnumberList = new Vector<Integer>();
        for (int i = 0; i < reversedCCnumberString.length(); i++) {
            reversedCCnumberList.add(new Integer(String
                    .valueOf(reversedCCnumberString.charAt(i))));
        }

        int sum = 0;
        int pos = 0;

        Integer[] reversedCCnumber = reversedCCnumberList
                .toArray(new Integer[reversedCCnumberList.size()]);
        while (pos < length - 1) {

            int odd = reversedCCnumber[pos] * 2;
            if (odd > 9) {
                odd -= 9;
            }

            sum += odd;

            if (pos != (length - 2)) {
                sum += reversedCCnumber[pos + 1];
            }
            pos += 2;
        }

        int checkdigit = new Double(
                ((Math.floor(sum / 10) + 1) * 10 - sum) % 10).intValue();
        ccnumber += checkdigit;

        return ccnumber;

    }

    public static String[] generateCardNumbers(String[] prefixList, int length, int howMany) {

        Stack<String> result = new Stack<String>();
        for (int i = 0; i < howMany; i++) {
            int randomArrayIndex = (int) Math.floor(Math.random()
                    * prefixList.length);
            String ccnumber = prefixList[randomArrayIndex];
            result.push(completed_number(ccnumber, length));
        }

        return result.toArray(new String[result.size()]);
    }

    public static String[] generateMasterCardNumbers(int howMany) {
        return generateCardNumbers(MASTERCARD_PREFIX_LIST, 19, howMany);
    }

    public static String generateMasterCardNumber() {
        return generateMasterCardNumbers(1)[0];
    }

    public static String[] generateVisaCardNumbers(int howMany) {
        return generateCardNumbers(VISA_PREFIX_LIST, 16, howMany);
    }

    public static String generateVisaCardNumber() {
        return generateVisaCardNumbers(1)[0];
    }

    public static String[] generateDinerCardNumbers(int howMany) {
        return generateCardNumbers(DINERS_PREFIX_LIST, 16, howMany);
    }

    public static String generateDinerCardNumbers() {
        return generateDinerCardNumbers(1)[0];
    }

    public static String[] generateAmexCardNumbers(int howMany) {
        return generateCardNumbers(AMEX_PREFIX_LIST, 16, howMany);
    }

    public static String generateAmexCardNumber() {
        return generateVisaCardNumbers(1)[0];
    }

    public static String[] generateJCBCardNumbers(int howMany) {
        return generateCardNumbers(AMEX_PREFIX_LIST, 16, howMany);
    }

    public static String generateJCBCardNumber() {
        return generateVisaCardNumbers(1)[0];
    }

    public static boolean isValidCreditCardNumber(String creditCardNumber) {
        boolean isValid = false;

        try {
            String reversedNumber = new StringBuffer(creditCardNumber)
                    .reverse().toString();
            int mod10Count = 0;
            for (int i = 0; i < reversedNumber.length(); i++) {
                int augend = Integer.parseInt(String.valueOf(reversedNumber
                        .charAt(i)));
                if (((i + 1) % 2) == 0) {
                    String productString = String.valueOf(augend * 2);
                    augend = 0;
                    for (int j = 0; j < productString.length(); j++) {
                        augend += Integer.parseInt(String.valueOf(productString
                                .charAt(j)));
                    }
                }

                mod10Count += augend;
            }

            if ((mod10Count % 10) == 0) {
                isValid = true;
            }
        } catch (NumberFormatException e) {
        }

        return isValid;
    }

    private static int sumToSingleDigit(int k) {
        if (k < 10)
            return k;
        return sumToSingleDigit(k / 10) + (k % 10);
    }
}