package factory.impl;

import constant.COMMON_CONSTANT;
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
        List<String> lineList = Arrays.stream(sqlStr.split(COMMON_CONSTANT.WRAP_REGEX)).filter(s -> StringUtil.isNotEmpty(s) && s.split(COMMON_CONSTANT.SPACE_REGEX).length > 1).collect(Collectors.toList());
        List<String> sqlTableNameList = Arrays.stream(lineList.get(0).split(COMMON_CONSTANT.SPACE_REGEX)).map(s -> s.replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING))
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        String sqlTableName = sqlTableNameList.get(sqlTableNameList.size() - 1);
        if (sqlTableName.contains(COMMON_CONSTANT.DOT)) {
            sqlTableName = sqlTableName.split(COMMON_CONSTANT.DOT_REGEX)[1];
        }
        tableInfo = new TableInfo(sqlTableName);
        TableInfo oracleTableInfo = new OracleParse(sqlStr).getTableInfo();
        tableInfo.setTableComment(oracleTableInfo.getTableComment());
        tableInfo.setColumnList(oracleTableInfo.getColumnList());
        return tableInfo;
    }

}



