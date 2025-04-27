package pers.zlf.plugin.action.export;

import com.intellij.database.model.DasTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:44
 */
public class TableExport extends BaseExport {
    private final DasTable DAS_TABLE;

    public TableExport(DasTable dasTable) {
        this.DAS_TABLE = dasTable;
    }

    @Override
    protected String dealWorkbook(XSSFWorkbook workbook) {
        createSheet(workbook, DAS_TABLE);
        return DAS_TABLE.getName();
    }

}
