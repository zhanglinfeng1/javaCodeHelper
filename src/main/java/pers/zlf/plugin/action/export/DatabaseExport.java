package pers.zlf.plugin.action.export;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:46
 */
public class DatabaseExport extends BaseExport {
    private final DasNamespace dasNamespace;

    public DatabaseExport(DasNamespace dasNamespace) {
        this.dasNamespace = dasNamespace;
    }

    @Override
    protected String dealWorkbook(XSSFWorkbook workbook) {
        for (DasObject dasTable : dasNamespace.getDasChildren(ObjectKind.TABLE)) {
            createSheet(workbook, dasTable);
        }
        return dasNamespace.getName();
    }
}
