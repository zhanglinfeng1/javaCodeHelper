package util;

import constant.COMMON_CONSTANT;
import pojo.ColumnInfo;
import pojo.TableInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/23 11:36
 */
public class SqlParseUtil {

    public static TableInfo parse(String dataBaseType, String sqlStr) throws Exception {
        TableInfo tableInfo = new TableInfo();
        List<String> lineList = Arrays.stream(sqlStr.split("\\r?\\n")).filter(s -> StringUtil.isNotEmpty(s) && s.split(COMMON_CONSTANT.SPACE_REGEX).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = lineList.get(0).split(COMMON_CONSTANT.SPACE_REGEX)[2].split(COMMON_CONSTANT.DOT_REGEX);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING);
        String tableName = Arrays.stream(sqlTableName.split(COMMON_CONSTANT.UNDERSCORE)).map(StringUtil::toUpperCaseFirst).collect(Collectors.joining());
        String firstLowerTableName = StringUtil.toLowerCaseFirst(tableName);
        tableInfo.setSqlTableName(sqlTableName);
        tableInfo.setTableName(tableName);
        tableInfo.setFirstLowerTableName(firstLowerTableName);
        switch (dataBaseType) {
            case COMMON_CONSTANT.MYSQL:
                return dealMysql(tableInfo, lineList);
            case COMMON_CONSTANT.ORACLE:
                return dealOracle(tableInfo, lineList);
            default:
                throw new Exception("数据库类型不存在");
        }
    }

    private static TableInfo dealMysql(TableInfo tableInfo, List<String> lineList) {
        String tableComment = StringUtil.getFirstValueByRegex(lineList.get(lineList.size() - 1), COMMON_CONSTANT.APOSTROPHE_EN_REGEX).replaceAll("表", COMMON_CONSTANT.BLANK_STRING);
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")).filter(StringUtil::isNotEmpty).map(s -> s.replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING)).collect(Collectors.toList());
            if (COMMON_CONSTANT.COMMENT.equalsIgnoreCase(valueList.get(valueList.size() - 2)) && !line.toUpperCase().contains("ENGINE=")) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.dealColumnName(valueList.get(0));
                columnInfo.setColumnType(TypeUtil.toJavaType(valueList.get(1)));
                columnInfo.setColumnComment(valueList.get(valueList.size() - 1));
                columnList.add(columnInfo);
            }
        }
        tableInfo.setTableComment(tableComment);
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }

    private static TableInfo dealOracle(TableInfo tableInfo, List<String> lineList) {
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            if (!COMMON_CONSTANT.COMMENT.equalsIgnoreCase(valueList.get(0)) && !COMMON_CONSTANT.CREATE.equalsIgnoreCase(valueList.get(0))) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.dealColumnName(valueList.get(0));
                columnInfo.setColumnType(TypeUtil.toJavaType(valueList.get(1)));
                columnList.add(columnInfo);
            }
        }
        Map<String, ColumnInfo> columnMap = columnList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
        for (String line : lineList) {
            if (line.toUpperCase().contains(COMMON_CONSTANT.COMMENT)) {
                List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                String comment = StringUtil.getFirstValueByRegex(lineList.get(lineList.size() - 1), COMMON_CONSTANT.APOSTROPHE_EN_REGEX).replaceAll("表", COMMON_CONSTANT.BLANK_STRING);
                if (COMMON_CONSTANT.TABLE.equalsIgnoreCase(valueList.get(2))) {
                    tableInfo.setTableComment(comment);
                    continue;
                }
                String[] sqlColumnNameArr = valueList.get(3).split(COMMON_CONSTANT.DOT_REGEX);
                String sqlColumnName = sqlColumnNameArr[sqlColumnNameArr.length - 1];
                ColumnInfo columnInfo = columnMap.get(sqlColumnName);
                if (null != columnInfo) {
                    columnInfo.setColumnComment(comment);
                }
            }
        }
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }

}
