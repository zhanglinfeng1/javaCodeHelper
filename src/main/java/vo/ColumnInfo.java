package vo;

import util.TypeConversionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 10:34
 */
public class ColumnInfo {
    /** 字段名 */
    private String columnName;
    /** 字段名 */
    private String firstUpperColumnName;
    /** 字段类型 */
    private String columnType;
    /** 字段备注 */
    private String columnComment;

    public ColumnInfo() {
    }

    public ColumnInfo(List<String> valueList) {
        valueList = valueList.stream().filter(s -> null != s && s.trim().length() != 0).map(s -> s.replaceAll("[',`]", "")).collect(Collectors.toList());
        this.firstUpperColumnName = Arrays.stream(valueList.get(0).split("_")).map(s -> {
            char[] ch = s.toCharArray();
            ch[0] = (char) (ch[0] - 32);
            return new String(ch);
        }).collect(Collectors.joining());
        char[] ch = this.firstUpperColumnName.toCharArray();
        ch[0] = (char) (ch[0] + 32);
        this.columnName = new String(ch);
        this.columnType = TypeConversionUtil.conversion(valueList.get(1));
        this.columnComment = valueList.get(valueList.size() - 1);
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
