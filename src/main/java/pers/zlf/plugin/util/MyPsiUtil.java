package pers.zlf.plugin.util;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.REGEX;
import pers.zlf.plugin.constant.TYPE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 16:25
 */
public class MyPsiUtil {

    public static boolean isFeign(PsiClass psiClass) {
        if (null == psiClass || !psiClass.isInterface()) {
            return false;
        }
        return psiClass.getAnnotation(ANNOTATION.OPEN_FEIGN_CLIENT) != null || psiClass.getAnnotation(ANNOTATION.NETFLIX_FEIGN_CLIENT) != null;
    }

    public static boolean isController(PsiClass psiClass) {
        if (psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return false;
        }
        return Arrays.stream(psiClass.getAnnotations()).map(PsiAnnotation::getQualifiedName).anyMatch(ANNOTATION.CONTROLLER_LIST::contains);
    }

    public static boolean isController(PsiClass psiClass, List<String> gatewayModuleNameList) {
        String filePath = Optional.ofNullable(psiClass).map(PsiClass::getContainingFile).map(PsiFile::getVirtualFile).map(VirtualFile::getPath).orElse(null);
        if (StringUtil.isEmpty(filePath) || gatewayModuleNameList.stream().anyMatch(filePath::contains)) {
            return false;
        }
        return isController(psiClass);
    }

    /**
     * 获取注解属性值
     *
     * @param annotation    注解
     * @param attributeName 属性
     * @return 属性值
     */
    public static String getAnnotationValue(PsiAnnotation annotation, String attributeName) {
        if (null == annotation) {
            return COMMON.BLANK_STRING;
        }
        PsiAnnotationMemberValue value = annotation.findAttributeValue(attributeName);
        if (value == null) {
            return COMMON.BLANK_STRING;
        }
        //TODO 获取实际值
        String attributeValue = value.getText();
        if (attributeValue.startsWith(COMMON.LEFT_BRACE)) {
            attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
        }
        return attributeValue.replaceAll(REGEX.DOUBLE_QUOTES, COMMON.BLANK_STRING).trim();
    }

    /**
     * 获取方法包含的变量
     *
     * @param psiMethod 方法
     * @param endOffset 当前元素位置
     * @return key:变量名 value:变量类型
     */
    public static Map<String, PsiType> getVariableMapFromMethod(PsiMethod psiMethod, int endOffset) {
        Map<String, PsiType> variableMap = new HashMap<>(16);
        PsiCodeBlock codeBlock = psiMethod.getBody();
        if (null == codeBlock) {
            return variableMap;
        }
        //获取代码块中的变量
        Arrays.stream(codeBlock.getStatements()).filter(t -> t.getTextOffset() <= endOffset && t instanceof PsiDeclarationStatement).forEach(t -> {
            variableMap.putAll(Arrays.stream(((PsiDeclarationStatement) t).getDeclaredElements()).map(p -> {
                if (p instanceof PsiLocalVariable) {
                    return (PsiLocalVariable) p;
                }
                PsiElement psiElement = p.getFirstChild();
                return psiElement instanceof PsiLocalVariable ? (PsiLocalVariable) psiElement : null;
            }).filter(Objects::nonNull).collect(Collectors.toMap(PsiLocalVariable::getName, PsiLocalVariable::getType)));
        });
        //方法参数
        Arrays.stream(psiMethod.getParameterList().getParameters()).forEach(t -> variableMap.put(t.getName(), t.getType()));
        return variableMap;
    }

    /**
     * 获取方法所在类的变量
     *
     * @param psiClass 类
     * @return key:变量名 value:变量类型
     */
    public static Map<String, PsiType> getVariableMapFromClass(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields()).collect(Collectors.toMap(PsiField::getName, PsiField::getType));
    }

    /**
     * 处理变量名，重名在末尾加数字
     *
     * @param variableName     待处理的变量名
     * @param psiType          变量类型
     * @param variableNameList 已存在的变量
     * @return 处理后的变量名
     */
    public static String dealVariableName(String variableName, PsiType psiType, List<String> variableNameList) {
        variableName = variableName.replace(TYPE.INTELLIJ_IDEA_RULEZZZ, COMMON.BLANK_STRING);
        String basicTypeName = psiType.getPresentableText();
        String suggestedVariableName = COMMON.BLANK_STRING;
        if (basicTypeName.contains(COMMON.LESS_THAN_SIGN)) {
            String[] suggestedVariableNames = StringUtil.getFirstMatcher(basicTypeName, REGEX.PARENTHESES).split(COMMON.COMMA);
            suggestedVariableName = suggestedVariableNames[suggestedVariableNames.length - 1].trim();
            basicTypeName = basicTypeName.split(COMMON.LESS_THAN_SIGN)[0];
        } else if (basicTypeName.contains(COMMON.LEFT_BRACKETS)) {
            suggestedVariableName = basicTypeName.split(REGEX.LEFT_BRACKETS)[0];
            basicTypeName = COMMON.S_STR;
        } else if (TypeUtil.isSimpleType(basicTypeName)) {
            basicTypeName = COMMON.BLANK_STRING;
        }
        suggestedVariableName = (TypeUtil.isSimpleType(suggestedVariableName) ? COMMON.BLANK_STRING : StringUtil.toLowerCaseFirst(suggestedVariableName)) + basicTypeName;
        if (suggestedVariableName.contains(variableName)) {
            variableName = suggestedVariableName;
        } else if (StringUtil.toLowerCaseFirst(basicTypeName).contains(variableName)) {
            variableName = basicTypeName;
        } else {
            variableName = variableName + basicTypeName;
        }
        String val = StringUtil.toLowerCaseFirst(variableName);
        int num = 1;
        while (variableNameList.contains(val)) {
            val = variableName + num;
            num++;
        }
        return val;
    }

    /**
     * 获取泛型类
     *
     * @param psiType 类型
     * @return List<User> return User
     */
    public static PsiClass getReferenceTypeClass(PsiType psiType) {
        if (psiType instanceof PsiClassReferenceType) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) psiType;
            PsiType[] psiTypeArr = referenceType.getParameters();
            if (psiTypeArr.length == 1) {
                return PsiUtil.resolveClassInClassTypeOnly(psiTypeArr[0]);
            }
        }
        return null;
    }

    /**
     * 获取类的方法，排除当前所在的方法
     *
     * @param psiClass     当前类
     * @param method       当前方法
     * @param containsName 方法名包含的字符
     * @return 方法数组
     */
    public static PsiMethod[] getMethods(PsiClass psiClass, PsiMethod method, String containsName) {
        LinkedList<PsiMethod> classMethodList = Arrays.stream(psiClass.getMethods()).collect(Collectors.toCollection(LinkedList::new));
        Iterator<PsiMethod> iterator = classMethodList.iterator();
        while (iterator.hasNext()) {
            PsiMethod classMethod = iterator.next();
            if (StringUtil.isNotEmpty(containsName) && !classMethod.getName().contains(containsName)) {
                iterator.remove();
                continue;
            }
            if (isSameMethod(classMethod, method)) {
                iterator.remove();
                break;
            }
        }
        return classMethodList.toArray(new PsiMethod[0]);
    }

    /**
     * 判断是否是同一个方法
     *
     * @param method1 方法1
     * @param method2 方法2
     * @return boolean
     */
    public static boolean isSameMethod(PsiMethod method1, PsiMethod method2) {
        // 判断方法名称是否相同
        if (null == method1 || null == method2 || !method1.getName().equals(method2.getName())) {
            return false;
        }
        String methodClassName1 = ((PsiClass) method1.getParent()).getQualifiedName();
        String methodClassName2 = ((PsiClass) method2.getParent()).getQualifiedName();
        if (null != methodClassName1 && !methodClassName1.equals(methodClassName2)) {
            return false;
        }
        // 判断方法参数列表是否相同
        PsiParameter[] paramArr1 = method1.getParameterList().getParameters();
        PsiParameter[] paramArr2 = method2.getParameterList().getParameters();
        if (paramArr1.length != paramArr2.length) {
            return false;
        }
        for (int i = 0; i < paramArr1.length; i++) {
            if (!isSamePsiType(paramArr1[i].getType(), paramArr2[i].getType())) {
                return false;
            }
        }
        // 判断返回类型是否相同
        return isSamePsiType(method1.getReturnType(), method2.getReturnType());
    }

    /**
     * 判断是否是同一类型
     *
     * @param psiType1 类型1
     * @param psiType2 类型2
     * @return boolean
     */
    public static boolean isSamePsiType(PsiType psiType1, PsiType psiType2) {
        if ((psiType1 == null && psiType2 != null) || (psiType1 != null && psiType2 == null)) {
            return false;
        }
        if (psiType1 == null) {
            return true;
        }
        return psiType1.getInternalCanonicalText().equals(psiType2.getInternalCanonicalText());
    }

    /**
     * 获取当前模块路径
     *
     * @param virtualFile VirtualFile
     * @param project     Project
     * @return 模块路径
     */
    public static String getCurrentModulePath(VirtualFile virtualFile, Project project) {
        return Optional.ofNullable(ModuleUtil.findModuleForFile(virtualFile, project)).map(ModuleRootManager::getInstance).map(ModuleRootManager::getContentRoots)
                .map(virtualFiles -> virtualFiles[0]).map(VirtualFile::getPath).orElse(COMMON.BLANK_STRING);
    }

    /**
     * 查找PsiClass
     *
     * @param element       同项目元素
     * @param classFullName class全名
     * @return Optional<PsiClass>
     */
    public static Optional<PsiClass> findClassByFullName(PsiElement element, String classFullName) {
        if (StringUtil.isEmpty(classFullName) || null == element) {
            return Optional.empty();
        }
        return Optional.ofNullable(JavaPsiFacade.getInstance(element.getProject()).findClass(classFullName, element.getResolveScope()));
    }

    /**
     * 查找PsiClass
     *
     * @param globalSearchScope GlobalSearchScope
     * @param classFullName     class全名
     * @return Optional<PsiClass>
     */
    public static Optional<PsiClass> findClassByFullName(GlobalSearchScope globalSearchScope, String classFullName) {
        if (StringUtil.isEmpty(classFullName) || null == globalSearchScope || null == globalSearchScope.getProject()) {
            return Optional.empty();
        }
        return Optional.ofNullable(JavaPsiFacade.getInstance(globalSearchScope.getProject()).findClass(classFullName, globalSearchScope));
    }

    /**
     * 获取元素的注释
     *
     * @param element 元素
     * @return 注释
     */
    public static String getComment(PsiElement element) {
        StringBuilder comment = new StringBuilder(COMMON.BLANK_STRING);
        for (PsiElement childrenElement : element.getChildren()) {
            if (childrenElement instanceof PsiDocComment) {
                PsiDocComment docComment = (PsiDocComment) childrenElement;
                comment.append(Arrays.stream(docComment.getDescriptionElements()).map(PsiElement::getText).collect(Collectors.joining()).replaceAll(REGEX.WRAP, COMMON.SPACE));
            } else if (childrenElement instanceof PsiComment) {
                comment.append(childrenElement.getText());
            }
        }
        return comment.toString().trim();
    }

    /**
     * 获取元素带标签的注释
     *
     * @param element 元素
     * @return 注释
     */
    public static Map<String, String> getParamComment(PsiElement element) {
        Map<String, String> commentMap = new HashMap<>();
        for (PsiElement childrenElement : element.getChildren()) {
            if (childrenElement instanceof PsiDocComment) {
                PsiDocComment docComment = (PsiDocComment) childrenElement;
                for (PsiDocTag tag : docComment.getTags()) {
                    Optional.ofNullable(tag.getValueElement()).map(PsiDocTagValue::getText).ifPresent(paramName -> {
                        PsiElement[] dataElementArr = tag.getDataElements();
                        String paramComment = paramName;
                        if (dataElementArr.length != 0) {
                            paramComment = IntStream.range(1, dataElementArr.length - 1).mapToObj(i -> dataElementArr[i].getText())
                                    .filter(StringUtil::isNotEmpty).collect(Collectors.joining(COMMON.COMMA));
                        }
                        commentMap.put(paramName, paramComment);
                    });
                }
            }
        }
        return commentMap;
    }
}
