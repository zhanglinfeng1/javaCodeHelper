package pers.zlf.plugin.action.export;

import com.intellij.database.model.DasTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:44
 */
public class TableExport extends BaseExport {
    private final DasTable dasTable;

    public TableExport(DasTable dasTable) {
        this.dasTable = dasTable;
    }

    @Override
    protected String dealWorkbook(XSSFWorkbook workbook) {
        createSheet(workbook, dasTable);
        return dasTable.getName();
    }

}
