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
        List<ColumnInfo> columnList = new ArrayList<>();
        for (String line : lineList) {
            List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
            if (!COMMON_CONSTANT.COMMENT.equalsIgnoreCase(valueList.get(0)) && !COMMON_CONSTANT.CREATE.equalsIgnoreCase(valueList.get(0))) {
                ColumnInfo columnInfo = new ColumnInfo(valueList.get(0));
                columnInfo.setColumnType(toJavaType(valueList.get(1)));
                columnList.add(columnInfo);
            }
        }
        Map<String, ColumnInfo> columnMap = columnList.stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
        for (String line : lineList) {
            if (line.toUpperCase().startsWith(COMMON_CONSTANT.COMMENT)) {
                List<String> valueList = Arrays.stream(line.split(COMMON_CONSTANT.SPACE_REGEX)).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                String comment = StringUtil.getFirstMatcher(lineList.get(lineList.size() - 1), COMMON_CONSTANT.APOSTROPHE_EN_REGEX);
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
