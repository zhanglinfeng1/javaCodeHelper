package pojo;

import com.google.gson.Gson;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:34
 */
public class TableInfo {
    /** sql表名 */
    private String sqlTableName;
    /** 表名 */
    private String tableName;
    /** 首字母小写表名 */
    private String firstLowerTableName;
    /** 表备注 */
    private String tableComment;
    /** 字段信息 */
    private List<ColumnInfo> columnList;

    public TableInfo() {
    }

    public TableInfo(String createTableSql) {
        List<String> lineList = List.of(createTableSql.split("\\r?\\n"));
        this.sqlTableName = lineList.get(0).split(COMMON_CONSTANT.SPACE)[2];
        if (this.sqlTableName.contains(".")) {
            this.sqlTableName = this.sqlTableName.split("\\.")[1].replaceAll("['`]", "");
        }
        this.tableName = Arrays.stream(this.sqlTableName.split("_")).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        this.firstLowerTableName = StringUtil.toLowerCaseFirst(this.tableName);
        Matcher m = Pattern.compile("'(.*?)'").matcher(lineList.get(lineList.size() - 1));
        if (m.find()) {
            this.tableComment = m.group(1).replaceAll("表", "");
        }
        this.columnList = lineList.stream().filter(t -> t.contains("COMMENT") && !t.contains("ENGINE=")).map(t -> new ColumnInfo(List.of(t.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")))).collect(Collectors.toList());
    }

    public String getSqlTableName() {
        return sqlTableName;
    }

    public void setSqlTableName(String sqlTableName) {
        this.sqlTableName = sqlTableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFirstLowerTableName() {
        return firstLowerTableName;
    }

    public void setFirstLowerTableName(String firstLowerTableName) {
        this.firstLowerTableName = firstLowerTableName;
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

    public Map<String, Object> toMap() {
        Gson gs = new Gson();
        return gs.fromJson(gs.toJson(this), Map.class);
    }
}
