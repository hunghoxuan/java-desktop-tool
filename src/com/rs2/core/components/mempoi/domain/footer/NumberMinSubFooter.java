/**
 * this sub footer shows the minimum vlaue for all the numeric columns in the report
 */

package com.rs2.core.components.mempoi.domain.footer;

public class NumberMinSubFooter extends NumberFormulaSubFooter {

    @Override
    protected String getFormula(String colLetter, int firstDataRowIndex, int lastDataRowIndex) {
        return "MIN(" + colLetter + firstDataRowIndex + ":" + colLetter + lastDataRowIndex + ")";
    }
}
