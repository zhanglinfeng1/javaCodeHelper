package pers.zlf.plugin.factory.service.impl;

import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.factory.service.SqlParse;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/11/7 11:15
 */
public class PostgresqlParse extends SqlParse {

    @Override
    public TableInfo getTableInfo(String sqlStr) {
        //表名
        List<String> lineList = Arrays.stream(sqlStr.split(REGEX.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(REGEX.SPACE).length > 1).collect(Collectors.toList());
        List<String> sqlTableNameList = Arrays.stream(lineList.get(0).split(REGEX.SPACE)).map(s -> s.replaceAll(REGEX.SQL_REPLACE, COMMON.BLANK_STRING))
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        String sqlTableName = sqlTableNameList.get(sqlTableNameList.size() - 1);
        if (sqlTableName.contains(COMMON.DOT)) {
            sqlTableName = sqlTableName.split(REGEX.DOT)[1];
        }
        tableInfo = new TableInfo(sqlTableName);
        TableInfo oracleTableInfo = new OracleParse().getTableInfo(sqlStr);
        tableInfo.setTableComment(oracleTableInfo.getTableComment());
        tableInfo.setColumnList(oracleTableInfo.getColumnList());
        return tableInfo;
    }

}