package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.annotation.BaseAnnotation;
import pers.zlf.plugin.pojo.annotation.ControllerAnnotation;
import pers.zlf.plugin.pojo.annotation.FieldAnnotation;
import pers.zlf.plugin.pojo.annotation.IgnoreAnnotation;
import pers.zlf.plugin.pojo.annotation.MethodAnnotation;
import pers.zlf.plugin.pojo.annotation.ModelAnnotation;
import pers.zlf.plugin.pojo.annotation.ParameterAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/4/24 15:26
 */
public class AddApiAnnotationAction extends BaseAction {
    /** 当前文件 */
    private PsiJavaFile psiJavaFile;
    /** 需要导入的类 */
    private Set<String> importClassSet;
    /** 需要添加的类 */
    private Map<PsiModifierList, String> annotationMap;
    /** 鼠标选中的起始位置 */
    private int selectionStart;
    /** 鼠标选中的末尾位置 */
    private int selectionEnd;

    @Override
    protected boolean isVisible() {
        if (null == project) {
            return false;
        }
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean visible = psiFile instanceof PsiJavaFile && psiFile.isWritable();
        if (visible) {
            psiJavaFile = (PsiJavaFile) psiFile;
            PsiClass psiClass = psiJavaFile.getClasses()[0];
            visible = !psiClass.isInterface() && !psiClass.isAnnotationType() && !psiClass.isEnum();
        }
        return visible;
    }

    @Override
    protected void execute() {
        selectionStart = editor.getSelectionModel().getSelectionStart();
        selectionEnd = editor.getSelectionModel().getSelectionEnd();
        importClassSet = new HashSet<>(2);
        annotationMap = new HashMap<>(2);
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            Equals.of(psiClass).and(MyPsiUtil::isController).then(this::addSwaggerForController, this::addSwaggerForModel);
            for (PsiClass innerClasses : psiClass.getAllInnerClasses()) {
                Equals.of(innerClasses).and(MyPsiUtil::isController).ifFalse(this::addSwaggerForModel);
            }
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            MyPsiUtil.importClass(psiJavaFile, importClassSet.toArray(new String[0]));
            annotationMap.forEach(PsiAnnotationOwner::addAnnotation);
        });
    }

    /**
     * Controller类添加注解
     *
     * @param psiClass Controller类
     */
    private void addSwaggerForController(PsiClass psiClass) {
        addApiAnnotation(new ControllerAnnotation(), psiClass, psiClass.getModifierList(), psiClass.getName());
        addSwaggerForMethod(psiClass.getMethods());
    }

    /**
     * 方法添加注解
     *
     * @param methods 方法数组
     */
    private void addSwaggerForMethod(PsiMethod[] methods) {
        List<String> mappingList = List.of(Annotation.REQUEST_MAPPING, Annotation.POST_MAPPING, Annotation.GET_MAPPING, Annotation.PUT_MAPPING, Annotation.DELETE_MAPPING, Annotation.PATCH_MAPPING);
        for (PsiMethod method : methods) {
            //获取注解
            PsiAnnotation psiAnnotation = MyPsiUtil.findAnnotation(method.getAnnotations(), mappingList);
            if (null == psiAnnotation) {
                continue;
            }
            addApiAnnotation(new MethodAnnotation(), method, method.getModifierList(), method.getName());
            addSwaggerForParameter(method, method.getParameterList().getParameters());
        }
    }

    /**
     * 参数添加注解
     *
     * @param method     方法
     * @param parameters 参数数组
     */
    private void addSwaggerForParameter(PsiMethod method, PsiParameter[] parameters) {
        Map<String, String> parameterCommentMap = MyPsiUtil.getParamComment(method);
        for (PsiParameter parameter : parameters) {
            if (!needAdd(parameter)) {
                continue;
            }
            addSwaggerForParameter(parameterCommentMap, parameter);
        }
    }

    /**
     * 参数添加注解
     *
     * @param parameterCommentMap 参数注释
     * @param parameter           参数
     */
    private void addSwaggerForParameter(Map<String, String> parameterCommentMap, PsiParameter parameter) {
        Map<String, PsiAnnotation> parameterAnnotationMap = Arrays.stream(parameter.getAnnotations()).collect(Collectors.toMap(PsiAnnotation::getQualifiedName, Function.identity()));
        String parameterComment = Empty.of(parameterCommentMap.get(parameter.getName())).orElse(parameter.getName());
        for (Map.Entry<String, PsiAnnotation> entry : parameterAnnotationMap.entrySet()) {
            PsiAnnotation annotation = entry.getValue();
            switch (entry.getKey()) {
                case Annotation.REQUEST_ATTRIBUTE:
                case Annotation.REQUEST_HEADER:
                    addApiAnnotation(new IgnoreAnnotation(), parameter, parameter.getModifierList(), parameterComment);
                    break;
                case Annotation.REQUEST_PARAM:
                case Annotation.REQUEST_PART:
                case Annotation.PATH_VARIABLE:
                case Annotation.REQUEST_BODY:
                    ParameterAnnotation parameterAnnotation = new ParameterAnnotation();
                    String required = MyPsiUtil.getAnnotationValue(annotation, Annotation.REQUIRED);
                    parameterAnnotation.setRequired(required.equals(Common.TRUE));
                    addApiAnnotation(parameterAnnotation, parameter, parameter.getModifierList(), parameterComment);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 对象类添加注解
     *
     * @param psiClass PsiClass
     */
    private void addSwaggerForModel(PsiClass psiClass) {
        addApiAnnotation(new ModelAnnotation(), psiClass, psiClass.getModifierList(), psiClass.getName());
        for (PsiField field : MyPsiUtil.getPsiFieldList(psiClass)) {
            addApiAnnotation(new FieldAnnotation(), field, field.getModifierList(), field.getName());
        }
    }

    /**
     * 添加注解
     *
     * @param baseAnnotation 注解
     * @param psiElement     元素
     * @param modifierList   PsiModifierList
     * @param psiElementName 元素名
     */
    private void addApiAnnotation(BaseAnnotation baseAnnotation, PsiElement psiElement, PsiModifierList modifierList, String psiElementName) {
        if (needAdd(psiElement) && null != modifierList && !modifierList.hasAnnotation(baseAnnotation.getQualifiedName())) {
            baseAnnotation.setValue(Empty.of(MyPsiUtil.getComment(psiElement)).orElse(psiElementName));
            importClassSet.add(baseAnnotation.getQualifiedName());
            annotationMap.put(modifierList, baseAnnotation.getText());
        }
    }

    /**
     * 是否需要添加注解
     *
     * @param psiElement PsiElement
     * @return boolean
     */
    private boolean needAdd(PsiElement psiElement) {
        if (selectionStart == selectionEnd) {
            return true;
        }
        int psiElementStart = psiElement.getTextRange().getStartOffset();
        int psiElementEnd = psiElement.getTextRange().getEndOffset();
        boolean psiElementStartBetween = psiElementStart >= selectionStart && psiElementStart <= selectionEnd;
        boolean psiElementEndBetween = psiElementEnd >= selectionStart && psiElementEnd <= selectionEnd;
        return psiElementStartBetween || psiElementEndBetween;
    }
}