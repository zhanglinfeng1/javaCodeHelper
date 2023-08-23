package pers.zlf.plugin.util;

import com.intellij.psi.PsiExpressionList;
import pers.zlf.plugin.constant.Common;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/23 9:50
 */
public class MyExpressionUtil {

    /**
     * 只有一个参数时，返回参数名，有多个时，返回空字符串
     *
     * @param expressionList PsiExpressionList
     * @return String
     */
    public static String getOnlyOneParameterName(PsiExpressionList expressionList) {
        return Optional.ofNullable(expressionList)
                .map(PsiExpressionList::getExpressions)
                .filter(t -> t.length == 1)
                .map(t -> t[0].getText())
                .orElse(Common.BLANK_STRING);
    }
}
