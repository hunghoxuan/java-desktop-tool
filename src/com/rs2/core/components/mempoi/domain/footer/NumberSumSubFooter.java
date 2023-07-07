/**
 * this sub footer shows a sum for all the numeric columns in the report
 */

package com.rs2.core.components.mempoi.domain.footer;

public class NumberSumSubFooter extends NumberFormulaSubFooter {

    @Override
    protected String getFormula(String colLetter, int firstDataRowIndex, int lastDataRowIndex) {
        return "SUM(" + colLetter + firstDataRowIndex + ":" + colLetter + lastDataRowIndex + ")";
    }
}
