package pers.zlf.plugin.dialog.database;

import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/18 9:56
 */
public class CommonParse extends BaseSqlParse {

    @Override
    public TableInfo parseSql(DbTable dbTable) {
        tableInfo = new TableInfo(dbTable.getName());
        tableInfo.setTableComment(dbTable.getComment());
        List<ColumnInfo> columnList = new ArrayList<>();
        for (DasColumn column : DasUtil.getColumns(dbTable)) {
            ColumnInfo columnInfo = new ColumnInfo(column.getName());
            columnInfo.setSqlColumnType(column.getDataType().typeName);
            columnInfo.setColumnType(toJavaType(column.getDataType().typeName));
            columnInfo.setColumnComment(Empty.of(column.getComment()).orElse(column.getName()));
            columnList.add(columnInfo);
        }
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }
}
