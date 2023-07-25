package pers.zlf.plugin.factory.database;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/18 9:58
 */
public class OracleParse extends BaseSqlParse {

    @Override
    public TableInfo getTableInfo(String sqlStr) {
        int index = sqlStr.indexOf(Common.SEMICOLON);
        List<String> columnLineList = Arrays.stream(sqlStr.substring(0, index).split(Regex.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(Regex.SPACE).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = columnLineList.get(0).split(Regex.SPACE)[2].split(Regex.DOT);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(Regex.SQL_REPLACE, Common.BLANK_STRING);
        tableInfo = new TableInfo(sqlTableName);
        columnLineList.remove(0);
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : columnLineList) {
            List<String> valueList = Arrays.stream(line.split(Regex.SPACE)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            if (!Keyword.SQL_CONSTRAINT.equalsIgnoreCase(valueList.get(0)) && !line.startsWith(Common.RIGHT_PARENTHESES)) {
                ColumnInfo columnInfo = new ColumnInfo(valueList.get(0));
                columnInfo.setSqlColumnType(valueList.get(1));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnList.add(columnInfo);
            }
        }
        //备注
        Map<String, ColumnInfo> columnMap = columnList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
        List<String> commentLineList = Arrays.stream(sqlStr.substring(index).split(Regex.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(Regex.SPACE).length > 1).collect(Collectors.toList());
        for (String line : commentLineList) {
            if (line.toLowerCase().startsWith(Keyword.SQL_COMMENT)) {
                List<String> valueList = Arrays.stream(line.split(Regex.SPACE)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                String comment = StringUtil.getFirstMatcher(valueList.get(valueList.size() - 1), Regex.APOSTROPHE);
                if (Keyword.SQL_TABLE.equalsIgnoreCase(valueList.get(2))) {
                    tableInfo.setTableComment(comment);
                    continue;
                }
                String[] sqlColumnNameArr = valueList.get(3).split(Regex.DOT);
                String sqlColumnName = sqlColumnNameArr[sqlColumnNameArr.length - 1];
                Optional.ofNullable(columnMap.get(sqlColumnName)).ifPresent(t -> t.setColumnComment(comment));
            }
        }
        tableInfo.setColumnList(columnList);
        return tableInfo;
    }
}
