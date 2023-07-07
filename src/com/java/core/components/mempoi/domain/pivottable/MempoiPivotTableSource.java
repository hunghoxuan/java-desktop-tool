/**
 * created by firegloves
 */

package com.java.core.components.mempoi.domain.pivottable;

import com.java.core.components.mempoi.domain.MempoiSheet;
import com.java.core.components.mempoi.domain.MempoiTable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.util.AreaReference;

@Data
@Accessors(chain = true)
public class MempoiPivotTableSource {

    /**
     * MemPOI table to use as source for the creating PivotTable
     * Conflits if specified in combination with areaReference or mempoiSheet
     */
    private final MempoiTable mempoiTable;

    /**
     * the Area reference to use as source for the creating PivotTable.
     * Conflits if specified in combination with mempoiTable
     */
    private final AreaReference areaReference;

    /**
     * the MempoiSheet to use as source for the creating PivotTable.
     * Conflits if specified in combination with mempoiTable
     */
    private final MempoiSheet mempoiSheet;
}