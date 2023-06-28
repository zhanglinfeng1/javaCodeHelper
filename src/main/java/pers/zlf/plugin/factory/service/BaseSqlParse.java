package pers.zlf.plugin.factory.service;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.pojo.TableInfo;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/18 9:54
 */
public abstract class BaseSqlParse {
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
            return Common.DATE_TYPE_MAP.get(commonConfig.getDateClassType());
        } else if (sqlType.contains("double") || sqlType.contains("float") || sqlType.contains("decimal") || sqlType.contains("number") || sqlType.contains("money")) {
            return "Double";
        } else {
            return "String";
        }
    }
}
