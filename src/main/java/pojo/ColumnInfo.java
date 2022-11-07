package pojo;

import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.Arrays;
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
    /** 查询方式 */
    private String queryType;

    public ColumnInfo() {
    }

    public ColumnInfo(String sqlColumnName) {
        this.sqlColumnName = sqlColumnName;
        this.firstUpperColumnName = (Arrays.stream(this.sqlColumnName.split(COMMON_CONSTANT.UNDERSCORE)).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining()));
        this.columnName = (StringUtil.toLowerCaseFirst(this.firstUpperColumnName));
    }

    public ColumnInfo(Object sqlColumnName, Object columnName, Object queryType) {
        this.sqlColumnName = StringUtil.toString(sqlColumnName);
        this.columnName = StringUtil.toString(columnName);
        this.queryType = StringUtil.toString(queryType);
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

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

}
