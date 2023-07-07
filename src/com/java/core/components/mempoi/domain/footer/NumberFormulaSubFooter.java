/**
 * this sub footer shows an average for all the numeric columns in the report
 */

package com.java.core.components.mempoi.domain.footer;

import com.java.core.components.mempoi.styles.StandardDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class NumberFormulaSubFooter extends FormulaSubFooter {

    @Override
    protected void customizeSubFooterCellStyle(Workbook workbook, CellStyle subFooterCellStyle) {

        subFooterCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        subFooterCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
                .getFormat(StandardDataFormat.STANDARD_FLOATING_NUMBER_FORMAT.getFormat()));
    }

}
