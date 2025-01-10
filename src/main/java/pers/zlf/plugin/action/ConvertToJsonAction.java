package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.TextAreaDialog;
import pers.zlf.plugin.util.JsonUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.SwingUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2025/1/6 11:02
 */
public class ConvertToJsonAction extends BaseAction {

    @Override
    protected boolean isVisible() {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        return psiFile instanceof PsiJavaFile;
    }

    @Override
    protected void execute() {
        PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        String json;
        String displayName;
        if (psiElement instanceof PsiField psiField) {
            json = JsonUtil.format(getJsonString(psiField.getType()));
            displayName = psiField.getName();
        } else if (psiElement instanceof PsiParameter psiParameter) {
            json = JsonUtil.format(getJsonString(psiParameter.getType()));
            displayName = psiParameter.getName();
        } else if (psiElement instanceof PsiVariable psiVariable) {
            json = JsonUtil.format(getJsonString(psiVariable.getType()));
            displayName = psiVariable.getName();
        } else if (psiElement instanceof PsiClass psiClass) {
            json = JsonUtil.format(getJsonString(psiClass));
            displayName = psiClass.getName();
        } else {
            return;
        }
        SwingUtil.registerToolWindow(project, Common.JAVA_CODE_HELPER, new TextAreaDialog(json).getContent(), displayName);
    }

    public Map<String, Object> getJsonString(PsiClass psiClass) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        //排除接口、注解、枚举
        if (psiClass == null || psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return jsonMap;
        }
        List<String> methodNameList = Arrays.stream(psiClass.getAllMethods()).map(PsiMethod::getName).toList();
        for (PsiField field : psiClass.getAllFields()) {
            //排除注入字段
            PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(field.getAnnotations(), List.of(Annotation.RESOURCE, Annotation.AUTOWIRED));
            if (psiAnnotation != null) {
                continue;
            }
            PsiType fieldType = field.getType();
            String fieldName = field.getName();
            //只处理有get方法的字段
            if (methodNameList.contains(Common.GET + StringUtil.toUpperCaseFirst(fieldName))) {
                jsonMap.put(fieldName, getJsonString(fieldType));
            }
        }
        return jsonMap;
    }

    private Object getJsonString(PsiType fieldType) {
        String fieldTypeName = fieldType.getCanonicalText();
        // 基本类型
        if (fieldType instanceof PsiPrimitiveType) {
            return Optional.ofNullable(PsiTypesUtil.getDefaultValue(fieldType)).orElse(Common.BLANK_STRING);
        } else if (ClassType.defaultValueMap.containsKey(fieldTypeName)) {
            //指定类型的默认值
            return Optional.ofNullable(ClassType.defaultValueMap.get(fieldTypeName)).orElse(Common.BLANK_STRING);
        } else if (fieldType instanceof PsiArrayType) {
            //数组
            List<Object> list = new ArrayList<>();
            PsiType deepType = fieldType.getDeepComponentType();
            list.add(getJsonString(PsiUtil.resolveClassInType(deepType)));
            return list;
        } else if (InheritanceUtil.isInheritor(fieldType, CommonClassNames.JAVA_UTIL_COLLECTION)) {
            //集合
            List<Object> list = new ArrayList<>();
            Optional.ofNullable(PsiUtil.extractIterableTypeParameter(fieldType, false)).ifPresent(t -> list.add(getJsonString(t)));
            return list;
        } else if (InheritanceUtil.isInheritor(fieldType, CommonClassNames.JAVA_UTIL_MAP)) {
            // map
            return new HashMap<>(2);
        } else {
            return getJsonString(PsiUtil.resolveClassInType(fieldType));
        }
    }
}
