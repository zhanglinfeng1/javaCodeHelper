package pers.zlf.plugin.pojo;

import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 10:34
 */
public class TableInfo {
    /** 作者 */
    private String author;
    /** 时间 */
    private String dateTime;
    /** 包路径 */
    private String packagePath;
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
    /** 用于查询的字段信息 */
    private List<ColumnInfo> queryColumnList;
    /** id字段类型 */
    private String idColumnType = ClassType.STRING;

    public TableInfo() {
    }

    public TableInfo(String sqlTableName, String tableComment) {
        this.sqlTableName = sqlTableName;
        this.tableComment = Empty.of(tableComment).orElse(sqlTableName);
    }

    public void dealTableName(String tableNamePrefix){
        if (StringUtil.isNotEmpty(tableNamePrefix)){
            this.sqlTableName = this.sqlTableName.substring(tableNamePrefix.length());
        }
        String tableName = Arrays.stream(this.sqlTableName.split(Common.UNDERLINE)).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        String firstLowerTableName = StringUtil.toLowerCaseFirst(tableName);
        this.tableName = tableName;
        this.firstLowerTableName = firstLowerTableName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
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

    public List<ColumnInfo> getQueryColumnList() {
        return queryColumnList;
    }

    public void setQueryColumnList(List<ColumnInfo> queryColumnList) {
        this.queryColumnList = queryColumnList;
    }

    public String getIdColumnType() {
        return idColumnType;
    }

    public void setIdColumnType(String idColumnType) {
        this.idColumnType = idColumnType;
    }
}
