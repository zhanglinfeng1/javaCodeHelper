package pers.zlf.plugin.util;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DataType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 17:37
 */
public class TypeUtil {
    private static final String GET_DAS_TYPE = "getDasType";
    private static final String TO_DATA_TYPE = "toDataType";
    private static final String GET_DATA_TYPE = "getDataType";


    /**
     * 判断对象是否是List
     *
     * @param psiClass 对象
     * @return boolean
     */
    public static boolean isList(PsiClass psiClass) {
        return judgmentType(psiClass, ClassType.LIST);
    }

    /**
     * 判断对象是否是Set
     *
     * @param psiClass 对象
     * @return boolean
     */
    public static boolean isSet(PsiClass psiClass) {
        return judgmentType(psiClass, ClassType.SET);
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
    public static boolean judgmentType(PsiClass psiClass, String typeName) {
        if (psiClass == null) {
            return false;
        }
        if (typeName.equals(psiClass.getName())) {
            return true;
        }
        return Arrays.stream(psiClass.getImplementsListTypes()).anyMatch(t -> typeName.equals(t.getName()));
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

    /**
     * 获取表字段类型
     *
     * @param dasColumn 字段
     * @return DataType
     */
    public static DataType getDataType(DasColumn dasColumn) {
        try {
            // 兼容2022.3.3及以上版本
            Method getDasTypeMethod = dasColumn.getClass().getMethod(GET_DAS_TYPE);
            Object dasType = getDasTypeMethod.invoke(dasColumn);
            Method toDataTypeMethod = dasType.getClass().getMethod(TO_DATA_TYPE);
            return (DataType) toDataTypeMethod.invoke(dasType);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // 兼容2022.3.3以下版本
            try {
                Method getDataTypeMethod = dasColumn.getClass().getMethod(GET_DATA_TYPE);
                return (DataType) getDataTypeMethod.invoke(dasColumn);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
