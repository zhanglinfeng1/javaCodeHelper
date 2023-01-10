package service.impl;

import constant.COMMON;
import constant.REGEX;
import service.SqlParse;
import pojo.ColumnInfo;
import pojo.TableInfo;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        int index = sqlStr.indexOf(COMMON.SEMICOLON);
        List<String> columnLineList = Arrays.stream(sqlStr.substring(0, index).split(REGEX.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(REGEX.SPACE).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = columnLineList.get(0).split(REGEX.SPACE)[2].split(REGEX.DOT);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(REGEX.SQL_REPLACE, COMMON.BLANK_STRING);
        tableInfo = new TableInfo(sqlTableName);
        columnLineList.remove(0);
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : columnLineList) {
            List<String> valueList = Arrays.stream(line.split(REGEX.SPACE)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            if (!COMMON.CONSTRAINT.equalsIgnoreCase(valueList.get(0)) && !line.startsWith(COMMON.RIGHT_PARENTHESES)) {
                ColumnInfo columnInfo = new ColumnInfo(valueList.get(0));
                columnInfo.setSqlColumnType(valueList.get(1));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnList.add(columnInfo);
            }
        }
        //备注
        Map<String, ColumnInfo> columnMap = columnList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
        List<String> commentLineList = Arrays.stream(sqlStr.substring(index).split(REGEX.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(REGEX.SPACE).length > 1).collect(Collectors.toList());
        for (String line : commentLineList) {
            if (line.toUpperCase().startsWith(COMMON.COMMENT)) {
                List<String> valueList = Arrays.stream(line.split(REGEX.SPACE)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                String comment = StringUtil.getFirstMatcher(valueList.get(valueList.size() - 1), REGEX.APOSTROPHE);
                if (COMMON.TABLE.equalsIgnoreCase(valueList.get(2))) {
                    tableInfo.setTableComment(comment);
                    continue;
                }
                String[] sqlColumnNameArr = valueList.get(3).split(REGEX.DOT);
                String sqlColumnName = sqlColumnNameArr[sqlColumnNameArr.length - 1];
                Optional.ofNullable(columnMap.get(sqlColumnName)).ifPresent(t -> t.setColumnComment(comment));
            }
        }
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }
}
