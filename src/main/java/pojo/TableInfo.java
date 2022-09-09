package pojo;

import com.google.gson.Gson;
import constant.COMMON_CONSTANT;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    /** 包路径 */
    private String packagePath;
    /** 创建者 */
    private String author;
    /** 创建时间 */
    private String dateTime;
    /** model路径 */
    private String modelPath;
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
        this.packagePath = COMMON_CONSTANT.BATH_PACKAGE;
        //this.modelPath = this.getFullPath() + this.tableName + ".java";
        this.modelPath = "C:\\Users\\86187\\Desktop\\" + this.tableName + ".java";
        this.author = COMMON_CONSTANT.AUTHOR;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
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

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
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

    private String getFullPath() {
        return COMMON_CONSTANT.PROJECT_PATH + COMMON_CONSTANT.BATH_PATH + COMMON_CONSTANT.BATH_PACKAGE.replaceAll("\\.","\\\\") + "\\";
    }
    public Map<String, Object> toMap(){
        Gson gs = new Gson();
        return gs.fromJson(gs.toJson(this),Map.class);
    }
}
