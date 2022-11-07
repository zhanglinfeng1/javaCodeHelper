package factory.impl;

import constant.COMMON_CONSTANT;
import factory.SqlParse;
import pojo.ColumnInfo;
import pojo.TableInfo;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/18 9:58
 */
public class OracleParse extends SqlParse {

    public OracleParse(String sqlStr) {
        super(sqlStr);
    }

    @Override
    public TableInfo getTableInfo() {
        int index = sqlStr.indexOf(COMMON_CONSTANT.SEMICOLON, 0);
        List<String> columnLineList = Arrays.stream(sqlStr.substring(0, index).split(COMMON_CONSTANT.WRAP_REGEX)).filter(s -> StringUtil.isNotEmpty(s) && s.split(COMMON_CONSTANT.SPACE_REGEX).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = columnLineList.get(0).split(COMMON_CONSTANT.SPACE_REGEX)[2].split(COMMON_CONSTANT.DOT_REGEX);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING);
        tableInfo = new TableInfo(sqlTableName);
        columnLineList.remove(0);
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : columnLineList) {
            List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            if (!COMMON_CONSTANT.CONSTRAINT.equalsIgnoreCase(valueList.get(0)) && !line.startsWith(COMMON_CONSTANT.RIGHT_PARENTHESES)) {
                ColumnInfo columnInfo = new ColumnInfo(valueList.get(0));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnList.add(columnInfo);
            }
        }
        //备注
        Map<String, ColumnInfo> columnMap = columnList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
        List<String> commentLineList = Arrays.stream(sqlStr.substring(index).split(COMMON_CONSTANT.WRAP_REGEX)).filter(s -> StringUtil.isNotEmpty(s) && s.split(COMMON_CONSTANT.SPACE_REGEX).length > 1).collect(Collectors.toList());
        for (String line : commentLineList) {
            if (line.toUpperCase().startsWith(COMMON_CONSTANT.COMMENT)) {
                List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                String comment = StringUtil.getFirstMatcher(valueList.get(valueList.size() - 1), COMMON_CONSTANT.APOSTROPHE_EN_REGEX);
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
