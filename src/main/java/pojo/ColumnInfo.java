package pojo;

import util.StringUtil;
import util.TypeUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 10:34
 */
public class ColumnInfo {
    /** sql原始字段名 */
    private String sqlColumnName;
    /** 字段名 */
    private String columnName;
    /** 首字母大写的字段名 */
    private String firstUpperColumnName;
    /** 字段类型 */
    private String columnType;
    /** 字段备注 */
    private String columnComment;

    public ColumnInfo() {
    }

    public ColumnInfo(List<String> valueList) {
        valueList = valueList.stream().filter(StringUtil::isNotEmpty).map(s -> s.replaceAll("[',`]", "")).collect(Collectors.toList());
        this.sqlColumnName = valueList.get(0);
        this.firstUpperColumnName = Arrays.stream(this.sqlColumnName.split("_")).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        this.columnName = StringUtil.toLowerCaseFirst(this.firstUpperColumnName);
        this.columnType = TypeUtil.toJavaType(valueList.get(1));
        this.columnComment = valueList.get(valueList.size() - 1);
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }

    public void setSqlColumnName(String sqlColumnName) {
        this.sqlColumnName = sqlColumnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFirstUpperColumnName() {
        return firstUpperColumnName;
    }

    public void setFirstUpperColumnName(String firstUpperColumnName) {
        this.firstUpperColumnName = firstUpperColumnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
