package vo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:34
 */
public class TableInfo {
    /** 表名 */
    private String tableName;
    /** 表备注 */
    private String tableComment;
    /** 字段类型 */
    private List<ColumnInfo> columnList;

    public TableInfo() {
    }

    public TableInfo(String createTableSql) {
        List<String> lineList = List.of(createTableSql.split("\\r?\\n"));
        this.tableName = lineList.get(0).split("\\s+")[2];
        if(this.tableName.contains(".")){
            this.tableName = this.tableName.split("\\.")[1].replaceAll("['`]", "");
        }
        Matcher m = Pattern.compile("\'(.*?)\'").matcher(lineList.get(lineList.size()-1));
        if (m.find()){
            this.tableComment = m.group(1);
        }
        this.columnList = lineList.stream().filter(t -> t.contains("COMMENT") && !t.contains("ENGINE=")).map(t -> new ColumnInfo(List.of(t.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")))).collect(Collectors.toList());
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<ColumnInfo> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ColumnInfo> columnList) {
        this.columnList = columnList;
    }
}
