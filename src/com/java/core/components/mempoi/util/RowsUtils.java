/**
 * created by firegloves
 */

package com.java.core.components.mempoi.util;

import lombok.experimental.UtilityClass;
import com.java.core.components.mempoi.domain.MempoiColumn;

import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

@UtilityClass
public class RowsUtils {

    // TODO add tests

    /**
     *
     * @param cellStyle    the style to modify
     * @param row          the row to set the height of
     * @param increaseSize the points to increase the size of
     */
    public static void adjustRowHeight(Workbook workbook, CellStyle cellStyle, Row row, int increaseSize) {
        if (cellStyle instanceof XSSFCellStyle) {
            row.setHeightInPoints((float) ((XSSFCellStyle) cellStyle).getFont()
                    .getFontHeightInPoints() + increaseSize);
        } else {
            row.setHeightInPoints((float) ((HSSFCellStyle) cellStyle)
                    .getFont(workbook).getFontHeightInPoints() + increaseSize);
        }
    }

    public static List<MempoiColumn> getColumnListFromDataList(List<List<String>> data) {
        List<MempoiColumn> columnList = new LinkedList<MempoiColumn>();
        if (data.size() > 0) {
            int i = 0;
            for (String _item : data.get(0)) {
                i += 1;
                // columnList.add(new MempoiColumn(rsmd.getColumnType(i),
                // rsmd.getColumnLabel(i), i - 1));
                columnList.add(new MempoiColumn(12, // varchar
                        _item, i - 1));
            }
        }
        // colListLen = columnList.size();
        return columnList;
    }
}
