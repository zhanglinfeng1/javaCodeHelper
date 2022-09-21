package pojo;

import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/9/8 10:34
 */
public class TableInfo {
    /** 作者 */
    private String author;
    /** 时间 */
    private String dateTime;
    /** 项目名 */
    private String projectName;
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

    public TableInfo() {
    }

    public TableInfo(String createTableSql) {
        List<String> lineList = Arrays.stream(createTableSql.split("\\r?\\n")).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        this.sqlTableName = lineList.get(0).split(COMMON_CONSTANT.SPACE)[2];
        if (this.sqlTableName.contains(".")) {
            this.sqlTableName = this.sqlTableName.split("\\.")[1].replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING);
        }
        this.tableName = Arrays.stream(this.sqlTableName.split(COMMON_CONSTANT.UNDERSCORE)).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        this.firstLowerTableName = StringUtil.toLowerCaseFirst(this.tableName);
        Matcher m = Pattern.compile("'(.*?)'").matcher(lineList.get(lineList.size() - 1));
        if (m.find()) {
            this.tableComment = m.group(1).replaceAll("表", COMMON_CONSTANT.BLANK_STRING);
        }
        this.columnList = lineList.stream().filter(t -> t.contains("COMMENT") && !t.contains("ENGINE=")).map(t -> new ColumnInfo(Arrays.asList(t.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")))).collect(Collectors.toList());
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
}
