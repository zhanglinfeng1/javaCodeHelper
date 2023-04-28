package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.search.GlobalSearchScope;
import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.pojo.annotation.BasicApi;
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
 * @Author zhanglinfeng
 * @Date create in 2023/4/24 15:26
 */
public class AddApiAnnotationAction extends BasicAction {
    private PsiJavaFile psiJavaFile;
    private Set<String> importClassSet;
    private Map<PsiModifierList, String> annotationMap;

    @Override
    public boolean check() {
        if (psiFile instanceof PsiJavaFile) {
            psiJavaFile = (PsiJavaFile) psiFile;
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        importClassSet = new HashSet<>();
        annotationMap = new HashMap<>();
        for (PsiClass psiClass : psiJavaFile.getClasses()) {
            Equals.of(psiClass).and(MyPsiUtil::isController).then(this::AddSwaggerForController, this::AddSwaggerForModel);
        }
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope globalSearchScope = psiJavaFile.getResolveScope();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            importClassSet.forEach(t -> Optional.ofNullable(javaPsiFacade.findClass(t, globalSearchScope)).ifPresent(psiJavaFile::importClass));
            annotationMap.forEach(PsiAnnotationOwner::addAnnotation);
        });
    }

    private void AddSwaggerForController(PsiClass psiClass) {
        addApiAnnotation(new ControllerApi(), psiClass, psiClass.getAnnotations(), psiClass.getModifierList(), psiClass.getName());
        for (PsiMethod method : psiClass.getMethods()) {
            Map<String, String> parameterCommentMap = MyPsiUtil.getParamComment(method);
            addApiAnnotation(new MethodApi(), method, method.getAnnotations(), method.getModifierList(), method.getName());
            for (PsiParameter parameter : method.getParameterList().getParameters()) {
                Map<String, PsiAnnotation> parameterAnnotationMap = Arrays.stream(parameter.getAnnotations()).collect(Collectors.toMap(PsiAnnotation::getQualifiedName, Function.identity()));
                String parameterComment = Empty.of(parameterCommentMap.get(parameter.getName())).orElse(parameter.getName());
                for (Map.Entry<String, PsiAnnotation> entry : parameterAnnotationMap.entrySet()) {
                    PsiAnnotation annotation = entry.getValue();
                    switch (entry.getKey()) {
                        case ANNOTATION.REQUEST_ATTRIBUTE:
                        case ANNOTATION.REQUEST_HEADER:
                            addApiAnnotation(new IgnoreApi(), parameter, parameter.getAnnotations(), parameter.getModifierList(), parameterComment);
                            break;
                        case ANNOTATION.REQUEST_PARAM:
                        case ANNOTATION.REQUEST_PART:
                        case ANNOTATION.PATH_VARIABLE:
                        case ANNOTATION.REQUEST_BODY:
                            ParameterApi parameterApi = new ParameterApi();
                            String required = MyPsiUtil.getAnnotationValue(annotation, ANNOTATION.REQUIRED);
                            parameterApi.setRequired(required.equals(COMMON.TRUE));
                            addApiAnnotation(parameterApi, parameter, parameter.getAnnotations(), parameter.getModifierList(), parameterComment);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void AddSwaggerForModel(PsiClass psiClass) {
        if (psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return;
        }
        addApiAnnotation(new ModelApi(), psiClass, psiClass.getAnnotations(), psiClass.getModifierList(), psiClass.getName());
        for (PsiField field : psiClass.getFields()) {
            addApiAnnotation(new FieldApi(), field, field.getAnnotations(), field.getModifierList(), field.getName());
        }
    }

    private void addApiAnnotation(BasicApi basicApi, PsiElement psiElement, PsiAnnotation[] annotationArr, PsiModifierList modifierList, String psiElementName) {
        boolean addApiAnnotations = Arrays.stream(annotationArr).noneMatch(a -> basicApi.qualifiedName.equals(a.getQualifiedName()));
        if (addApiAnnotations && null != modifierList) {
            basicApi.setValue(Empty.of(MyPsiUtil.getComment(psiElement)).orElse(psiElementName));
            importClassSet.add(basicApi.qualifiedName);
            annotationMap.put(modifierList, basicApi.toString());
        }
    }

}