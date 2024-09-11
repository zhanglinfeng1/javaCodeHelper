package pers.zlf.plugin.action.export;

import com.intellij.database.model.ObjectKind;
import com.intellij.database.model.basic.BasicElement;
import com.intellij.database.psi.DbNamespace;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:46
 */
public class DatabaseExport extends BaseExport {
    private final DbNamespace dbNamespace;

    public DatabaseExport(DbNamespace dbNamespace) {
        this.dbNamespace = dbNamespace;
    }

    @Override
    public String dealWorkbook(XSSFWorkbook workbook) {
        if (dbNamespace.getDelegate() instanceof BasicElement basicElement) {
            for (BasicElement dbTable : basicElement.getDasChildren(ObjectKind.TABLE)) {
                createSheet(workbook, dbTable);
            }
        }
        return dbNamespace.getName();
    }
}
