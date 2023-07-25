package pers.zlf.plugin.factory.database;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.pojo.TableInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/18 9:54
 */
public abstract class BaseSqlParse {
    /** java时间类型*/
    private final Map<Integer, String> dateTypeMap = new HashMap<>() {{
        put(Common.DATE_CLASS_TYPE, "Date");
        put(1, "Timestamp");
        put(2, "LocalDateTime");
    }};
    /** sql解析后的对象 */
    protected TableInfo tableInfo;

    /**
     * 处理sql语句
     *
     * @param sqlStr 建表语句
     * @return TableInfo
     */
    public abstract TableInfo getTableInfo(String sqlStr);

    public String toJavaType(String sqlType) {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        sqlType = sqlType.toLowerCase();
        if (sqlType.contains("int") || sqlType.contains("integer") || sqlType.contains("smallint") || sqlType.contains("serial") || sqlType.contains("smallserial")) {
            return "Integer";
        } else if (sqlType.contains("timestamp") || sqlType.contains("date") || sqlType.contains("datetime") || sqlType.contains("time")) {
            return dateTypeMap.get(commonConfig.getDateClassType());
        } else if (sqlType.contains("double") || sqlType.contains("float") || sqlType.contains("decimal") || sqlType.contains("number") || sqlType.contains("money")) {
            return "Double";
        } else {
            return "String";
        }
    }
}
