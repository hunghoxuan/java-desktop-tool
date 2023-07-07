/**
 * this sub footer shows an average for all the numeric columns in the report
 */

package com.rs2.core.components.mempoi.domain.footer;

public class NumberAverageSubFooter extends NumberFormulaSubFooter {

    @Override
    protected String getFormula(String colLetter, int firstDataRowIndex, int lastDataRowIndex) {
        return "AVERAGE(" + colLetter + firstDataRowIndex + ":" + colLetter + lastDataRowIndex + ")";
    }
}
