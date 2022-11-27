package util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import constant.COMMON_CONSTANT;
import constant.TYPE_CONSTANT;

import java.util.Arrays;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 17:37
 */
public class TypeUtil {

    public static boolean isList(PsiClass psiClass) {
        return judgmentType(psiClass, TYPE_CONSTANT.LIST);
    }

    public static boolean isSet(PsiClass psiClass) {
        return judgmentType(psiClass, TYPE_CONSTANT.SET);
    }

    public static boolean isSimpleArr(PsiType psiType) {
        String str = psiType.getPresentableText();
        int firstIndex = str.indexOf(COMMON_CONSTANT.LEFT_BRACKETS);
        return firstIndex != -1 && firstIndex == str.lastIndexOf(COMMON_CONSTANT.LEFT_BRACKETS);
    }

    public static boolean judgmentType(PsiClass psiClass, String typeName) {
        if (psiClass == null) {
            return false;
        }
        if (typeName.equals(psiClass.getName())) {
            return true;
        }
        return Arrays.stream(psiClass.getImplementsListTypes()).anyMatch(t -> typeName.equals(t.getName()));
    }

}
