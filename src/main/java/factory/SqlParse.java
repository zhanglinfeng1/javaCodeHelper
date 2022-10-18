package factory;

import constant.COMMON_CONSTANT;
import pojo.TableInfo;
import util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/18 9:54
 */
public class SqlParse {
    public TableInfo tableInfo = new TableInfo();
    public List<String> lineList;

    public SqlParse(String sqlStr) {
        lineList = Arrays.stream(sqlStr.split(COMMON_CONSTANT.WRAP_REGEX)).filter(s -> StringUtil.isNotEmpty(s) && s.split(COMMON_CONSTANT.SPACE_REGEX).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = lineList.get(0).split(COMMON_CONSTANT.SPACE_REGEX)[2].split(COMMON_CONSTANT.DOT_REGEX);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING);
        String tableName = Arrays.stream(sqlTableName.split(COMMON_CONSTANT.UNDERSCORE)).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        String firstLowerTableName = StringUtil.toLowerCaseFirst(tableName);
        tableInfo.setSqlTableName(sqlTableName);
        tableInfo.setTableName(tableName);
        tableInfo.setFirstLowerTableName(firstLowerTableName);
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public String toJavaType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.contains("int") || sqlType.contains("integer")) {
            return "Integer";
        } else if (sqlType.contains("timestamp") || sqlType.contains("date") || sqlType.contains("datetime") || sqlType.contains("time")) {
            return "Timestamp";
        } else if (sqlType.contains("double") || sqlType.contains("float") || sqlType.contains("decimal") || sqlType.contains("number")) {
            return "Double";
        } else {
            return "String";
        }
    }
}
