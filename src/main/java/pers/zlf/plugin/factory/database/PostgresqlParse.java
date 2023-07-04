package pers.zlf.plugin.factory.database;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/11/7 11:15
 */
public class PostgresqlParse extends BaseSqlParse {

    @Override
    public TableInfo getTableInfo(String sqlStr) {
        //表名
        List<String> lineList = Arrays.stream(sqlStr.split(Regex.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(Regex.SPACE).length > 1).collect(Collectors.toList());
        List<String> sqlTableNameList = Arrays.stream(lineList.get(0).split(Regex.SPACE)).map(s -> s.replaceAll(Regex.SQL_REPLACE, Common.BLANK_STRING))
                .filter(StringUtil::isNotEmpty).collect(Collectors.toList());
        String sqlTableName = sqlTableNameList.get(sqlTableNameList.size() - 1);
        if (sqlTableName.contains(Common.DOT)) {
            sqlTableName = sqlTableName.split(Regex.DOT)[1];
        }
        tableInfo = new TableInfo(sqlTableName);
        TableInfo oracleTableInfo = new OracleParse().getTableInfo(sqlStr);
        tableInfo.setTableComment(oracleTableInfo.getTableComment());
        tableInfo.setColumnList(oracleTableInfo.getColumnList());
        return tableInfo;
    }

}