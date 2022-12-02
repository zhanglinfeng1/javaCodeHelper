package factory.impl;

import constant.COMMON;
import constant.REGEX;
import factory.SqlParse;
import pojo.ColumnInfo;
import pojo.TableInfo;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/18 9:56
 */
public class MysqlParse extends SqlParse {

    public MysqlParse(String sqlStr) {
        super(sqlStr);
    }

    @Override
    public TableInfo getTableInfo() {
        List<String> lineList = Arrays.stream(sqlStr.split(REGEX.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(REGEX.SPACE).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = lineList.get(0).split(REGEX.SPACE)[2].split(REGEX.DOT);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(REGEX.SQL_REPLACE, COMMON.BLANK_STRING);
        tableInfo = new TableInfo(sqlTableName);
        tableInfo.setTableComment(StringUtil.getFirstMatcher(lineList.get(lineList.size() - 1), REGEX.APOSTROPHE));
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")).filter(StringUtil::isNotEmpty).map(s -> s.replaceAll(REGEX.SQL_REPLACE, COMMON.BLANK_STRING)).collect(Collectors.toList());
            if (COMMON.COMMENT.equalsIgnoreCase(valueList.get(valueList.size() - 2)) && !line.toUpperCase().contains("ENGINE=")) {
                ColumnInfo columnInfo = new ColumnInfo(valueList.get(0));
                columnInfo.setSqlColumnType(valueList.get(1));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnInfo.setColumnComment(valueList.get(valueList.size() - 1));
                columnList.add(columnInfo);
            }
        }
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }
}
