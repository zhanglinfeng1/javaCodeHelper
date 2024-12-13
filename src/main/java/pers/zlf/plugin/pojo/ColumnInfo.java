package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 10:34
 */
public class ColumnInfo {
    /** sql原始字段名 */
    private String sqlColumnName;
    /** 字段名 */
    private String columnName;
    /** 字段set方法 */
    private String columnSetMethod;
    /** 字段get方法 */
    private String columnGetMethod;
    /** 首字母大写的字段名 */
    private String firstUpperColumnName;
    /** 下划线格式字段名 */
    private String underlineUpperColumnName;
    /** 原始字段类型 */
    private String sqlColumnType;
    /** java类型 */
    private String columnType;
    /** 字段备注 */
    private String columnComment;
    /** 查询方式 */
    private String queryType;

    public ColumnInfo() {
    }

    public ColumnInfo(String sqlColumnName, String columnName, String sqlColumnType, String columnType, String columnComment) {
        this.sqlColumnName = sqlColumnName;
        this.columnName = columnName;
        this.sqlColumnType = sqlColumnType;
        this.columnType = columnType;
        this.columnComment = columnComment;
        this.firstUpperColumnName = StringUtil.toUpperCaseFirst(this.columnName);
        this.columnSetMethod = Common.SET + this.firstUpperColumnName;
        if (columnType.equals(ClassType.BOOLEAN) || columnType.equals(ClassType.BOOLEAN_WRAPPER)) {
            this.columnGetMethod = Common.IS + this.firstUpperColumnName;
        } else {
            this.columnGetMethod = Common.GET + this.firstUpperColumnName;
        }
        this.underlineUpperColumnName = StringUtil.toUnderlineStyle(this.columnName);
    }

    public ColumnInfo(String sqlColumnName, Object columnName, Object queryType) {
        this.sqlColumnName = sqlColumnName;
        this.columnName = StringUtil.toString(columnName);
        this.underlineUpperColumnName = StringUtil.toUnderlineStyle(this.columnName);
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

    public String getColumnSetMethod() {
        return columnSetMethod;
    }

    public void setColumnSetMethod(String columnSetMethod) {
        this.columnSetMethod = columnSetMethod;
    }

    public String getColumnGetMethod() {
        return columnGetMethod;
    }

    public void setColumnGetMethod(String columnGetMethod) {
        this.columnGetMethod = columnGetMethod;
    }

    public String getFirstUpperColumnName() {
        return firstUpperColumnName;
    }

    public void setFirstUpperColumnName(String firstUpperColumnName) {
        this.firstUpperColumnName = firstUpperColumnName;
    }

    public String getUnderlineUpperColumnName() {
        return underlineUpperColumnName;
    }

    public void setUnderlineUpperColumnName(String underlineUpperColumnName) {
        this.underlineUpperColumnName = underlineUpperColumnName;
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

    public String getSqlColumnType() {
        return sqlColumnType;
    }

    public void setSqlColumnType(String sqlColumnType) {
        this.sqlColumnType = sqlColumnType;
    }
}
