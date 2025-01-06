package pers.zlf.plugin.util;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;

import java.util.Arrays;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 17:37
 */
public class TypeUtil {

    /**
     * 判断对象是否是List
     *
     * @param psiClass 对象
     * @return boolean
     */
    public static boolean isList(PsiClass psiClass) {
        return judgmentType(psiClass, CommonClassNames.JAVA_UTIL_LIST, CommonClassNames.JAVA_UTIL_ARRAY_LIST, CommonClassNames.JAVA_UTIL_LINKED_LIST);
    }

    /**
     * 判断对象是否是Set
     *
     * @param psiClass 对象
     * @return boolean
     */
    public static boolean isSet(PsiClass psiClass) {
        return judgmentType(psiClass, CommonClassNames.JAVA_UTIL_SET, CommonClassNames.JAVA_UTIL_HASH_SET, CommonClassNames.JAVA_UTIL_LINKED_HASH_SET, CommonClassNames.JAVA_UTIL_SORTED_SET);
    }

    /**
     * 判断是否是简单数组
     *
     * @param psiType 对象
     * @return boolean
     */
    public static boolean isSimpleArr(PsiType psiType) {
        String str = psiType.getPresentableText();
        int firstIndex = str.indexOf(Common.LEFT_BRACKETS);
        return firstIndex != -1 && firstIndex == str.lastIndexOf(Common.LEFT_BRACKETS);
    }

    /**
     * 判断对象类型
     *
     * @param psiClass 对象
     * @param typeName 类型名
     * @return boolean
     */
    public static boolean judgmentType(PsiClass psiClass, String... typeNames) {
        if (psiClass == null) {
            return false;
        }
        return Arrays.stream(typeNames).allMatch(t->t.equals(psiClass.getQualifiedName()));
    }

    /**
     * 判断是否是简单类型
     *
     * @param typeStr 类型
     * @return boolean
     */
    public static boolean isSimpleType(String typeStr) {
        return ClassType.BASIC_TYPE_LIST.contains(typeStr);
    }
}
