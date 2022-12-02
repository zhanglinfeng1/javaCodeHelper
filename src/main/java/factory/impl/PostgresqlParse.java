package factory.impl;

import constant.COMMON;
import constant.REGEX;
import factory.SqlParse;
import pojo.TableInfo;
import util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/11/7 11:15
 */
public class PostgresqlParse extends SqlParse {

    public PostgresqlParse(String sqlStr) {
        super(sqlStr);
    }

    @Override
    public TableInfo getTableInfo() {
        //表名
        List<String> lineList = Arrays.stream(sqlStr.split(REGEX.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(REGEX.SPACE).length > 1).collect(Collectors.toList());
        List<String> sqlTableNameList = Arrays.stream(lineList.get(0).split(REGEX.SPACE)).map(s -> s.replaceAll(REGEX.SQL_REPLACE, COMMON.BLANK_STRING))
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        String sqlTableName = sqlTableNameList.get(sqlTableNameList.size() - 1);
        if (sqlTableName.contains(COMMON.DOT)) {
            sqlTableName = sqlTableName.split(REGEX.DOT)[1];
        }
        tableInfo = new TableInfo(sqlTableName);
        TableInfo oracleTableInfo = new OracleParse(sqlStr).getTableInfo();
        tableInfo.setTableComment(oracleTableInfo.getTableComment());
        tableInfo.setColumnList(oracleTableInfo.getColumnList());
        return tableInfo;
    }

}



