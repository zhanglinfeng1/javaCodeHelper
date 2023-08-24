package pers.zlf.plugin.dialog.database;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/18 9:56
 */
public class MysqlParse extends BaseSqlParse {

    @Override
    public TableInfo parseSql(String sqlStr) {
        List<String> lineList = Arrays.stream(sqlStr.split(Regex.WRAP)).filter(s -> StringUtil.isNotEmpty(s) && s.split(Regex.SPACE).length > 1).collect(Collectors.toList());
        String[] sqlTableNameArr = lineList.get(0).split(Regex.SPACE)[2].split(Regex.DOT);
        String sqlTableName = sqlTableNameArr[sqlTableNameArr.length - 1].replaceAll(Regex.SQL_REPLACE, Common.BLANK_STRING);
        tableInfo = new TableInfo(sqlTableName);
        tableInfo.setTableComment(StringUtil.getFirstMatcher(lineList.get(lineList.size() - 1), Regex.APOSTROPHE));
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")).filter(StringUtil::isNotEmpty).map(s -> s.replaceAll(Regex.SQL_REPLACE, Common.BLANK_STRING)).collect(Collectors.toList());
            if (Keyword.SQL_COMMENT.equalsIgnoreCase(valueList.get(valueList.size() - 2)) && !line.toUpperCase().contains("ENGINE=")) {
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
