package factory.impl;

import constant.COMMON_CONSTANT;
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
        String tableComment = StringUtil.getFirstMatcher(lineList.get(lineList.size() - 1), COMMON_CONSTANT.APOSTROPHE_EN_REGEX).replaceAll("表", COMMON_CONSTANT.BLANK_STRING);
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split("[\\s]+(?=(([^']*[']){2})*[^']*$)")).filter(StringUtil::isNotEmpty).map(s -> s.replaceAll(COMMON_CONSTANT.SQL_REPLACE_REGEX, COMMON_CONSTANT.BLANK_STRING)).collect(Collectors.toList());
            if (COMMON_CONSTANT.COMMENT.equalsIgnoreCase(valueList.get(valueList.size() - 2)) && !line.toUpperCase().contains("ENGINE=")) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.dealColumnName(valueList.get(0));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnInfo.setColumnComment(valueList.get(valueList.size() - 1));
                columnList.add(columnInfo);
            }
        }
        tableInfo.setColumnList(columnList);
        tableInfo.setTableComment(tableComment);
        return tableInfo;
    }
}
