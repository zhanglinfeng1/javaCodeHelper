package pers.zlf.plugin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算工具类
 *
 * @Author zhanglinfeng
 * @Date create in 2023/6/15 10:11
 */
public class MathUtil {

    /**
     * 求百分比
     *
     * @param data1 被除数1
     * @param data2 除数2
     * @param scale 小数位数
     * @return BigDecimal
     */
    public static BigDecimal percentage(Integer data1, Integer data2, int scale) {
        return new BigDecimal(data1).multiply(new BigDecimal(100)).divide(new BigDecimal(data2), scale, RoundingMode.HALF_UP);
    }

}
