package pers.zlf.plugin.action.export;

import com.intellij.database.psi.DbTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:44
 */
public class TableExport extends BaseExport {
    private final DbTable dbTable;

    public TableExport(DbTable dbTable) {
        this.dbTable = dbTable;
    }

    @Override
    public String dealWorkbook(XSSFWorkbook workbook) {
        createSheet(workbook, dbTable);
        return dbTable.getName();
    }

}
