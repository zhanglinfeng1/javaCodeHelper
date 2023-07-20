package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
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
import com.intellij.psi.search.GlobalSearchScope;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.pojo.annotation.BaseApi;
import pers.zlf.plugin.pojo.annotation.ControllerApi;
import pers.zlf.plugin.pojo.annotation.FieldApi;
import pers.zlf.plugin.pojo.annotation.IgnoreApi;
import pers.zlf.plugin.pojo.annotation.MethodApi;
import pers.zlf.plugin.pojo.annotation.ModelApi;
import pers.zlf.plugin.pojo.annotation.ParameterApi;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
    int selectionStart;
    /** 鼠标选中的末尾位置 */
    int selectionEnd;

    @Override
    public boolean isVisible() {
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean visible = psiFile instanceof PsiJavaFile && psiFile.isWritable();
        if (visible) {
            psiJavaFile = (PsiJavaFile) psiFile;
            PsiClass psiClass = psiJavaFile.getClasses()[0];
            visible = !psiClass.isInterface() && !psiClass.isAnnotationType() && !psiClass.isEnum();
        }
        selectionStart = editor.getSelectionModel().getSelectionStart();
        selectionEnd = editor.getSelectionModel().getSelectionEnd();
        return visible;
    }

    @Override
    public void execute() {
        importClassSet = new HashSet<>(2);
        annotationMap = new HashMap<>(2);
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            Equals.of(psiClass).and(MyPsiUtil::isController).then(this::addSwaggerForController, this::addSwaggerForModel);
        }
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope globalSearchScope = psiJavaFile.getResolveScope();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            importClassSet.forEach(t -> Optional.ofNullable(javaPsiFacade.findClass(t, globalSearchScope)).ifPresent(psiJavaFile::importClass));
            annotationMap.forEach(PsiAnnotationOwner::addAnnotation);
        });
    }

    /**
     * Controller类添加注解
     *
     * @param psiClass PsiClass
     */
    private void addSwaggerForController(PsiClass psiClass) {
        addApiAnnotation(new ControllerApi(), psiClass, psiClass.getModifierList(), psiClass.getName());
        for (PsiMethod method : psiClass.getMethods()) {
            Map<String, String> parameterCommentMap = MyPsiUtil.getParamComment(method);
            addApiAnnotation(new MethodApi(), method, method.getModifierList(), method.getName());
            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                Map<String, PsiAnnotation> parameterAnnotationMap = Arrays.stream(parameter.getAnnotations()).collect(Collectors.toMap(PsiAnnotation::getQualifiedName, Function.identity()));
                String parameterComment = Empty.of(parameterCommentMap.get(parameter.getName())).orElse(parameter.getName());
                for (Map.Entry<String, PsiAnnotation> entry : parameterAnnotationMap.entrySet()) {
                    PsiAnnotation annotation = entry.getValue();
                    switch (entry.getKey()) {
                        case Annotation.REQUEST_ATTRIBUTE:
                        case Annotation.REQUEST_HEADER:
                            addApiAnnotation(new IgnoreApi(), parameter, parameter.getModifierList(), parameterComment);
                            break;
                        case Annotation.REQUEST_PARAM:
                        case Annotation.REQUEST_PART:
                        case Annotation.PATH_VARIABLE:
                        case Annotation.REQUEST_BODY:
                            ParameterApi parameterApi = new ParameterApi();
                            String required = MyPsiUtil.getAnnotationValue(annotation, Annotation.REQUIRED);
                            parameterApi.setRequired(required.equals(Common.TRUE));
                            addApiAnnotation(parameterApi, parameter, parameter.getModifierList(), parameterComment);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 对象类添加注解
     *
     * @param psiClass PsiClass
     */
    private void addSwaggerForModel(PsiClass psiClass) {
        addApiAnnotation(new ModelApi(), psiClass, psiClass.getModifierList(), psiClass.getName());
        for (PsiField field : psiClass.getFields()) {
            addApiAnnotation(new FieldApi(), field, field.getModifierList(), field.getName());
        }
    }

    /**
     * 添加注解
     *
     * @param baseApi        注解
     * @param psiElement     元素
     * @param modifierList   PsiModifierList
     * @param psiElementName 元素名
     */
    private void addApiAnnotation(BaseApi baseApi, PsiElement psiElement, PsiModifierList modifierList, String psiElementName) {
        if (needAdd(psiElement) && null != modifierList && !modifierList.hasAnnotation(baseApi.getQualifiedName())) {
            baseApi.setValue(Empty.of(MyPsiUtil.getComment(psiElement)).orElse(psiElementName));
            importClassSet.add(baseApi.getQualifiedName());
            annotationMap.put(modifierList, baseApi.toString());
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