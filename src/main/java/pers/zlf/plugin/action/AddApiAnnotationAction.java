package pers.zlf.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
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
import org.jetbrains.annotations.NotNull;
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
    private PsiJavaFile psiJavaFile;
    private Set<String> importClassSet;
    private Map<PsiModifierList, String> annotationMap;

    @Override
    public void update(@NotNull AnActionEvent event) {
        init(event);
        event.getPresentation().setVisible(psiFile instanceof PsiJavaFile && psiFile.isWritable());
    }

    @Override
    public void action() {
        psiJavaFile = (PsiJavaFile) psiFile;
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

    private void addSwaggerForModel(PsiClass psiClass) {
        if (psiClass.isInterface() || psiClass.isAnnotationType() || psiClass.isEnum()) {
            return;
        }
        addApiAnnotation(new ModelApi(), psiClass, psiClass.getModifierList(), psiClass.getName());
        for (PsiField field : psiClass.getFields()) {
            addApiAnnotation(new FieldApi(), field, field.getModifierList(), field.getName());
        }
    }

    private void addApiAnnotation(BaseApi baseApi, PsiElement psiElement, PsiModifierList modifierList, String psiElementName) {
        if (null != modifierList && !modifierList.hasAnnotation(baseApi.getQualifiedName())) {
            baseApi.setValue(Empty.of(MyPsiUtil.getComment(psiElement)).orElse(psiElementName));
            importClassSet.add(baseApi.getQualifiedName());
            annotationMap.put(modifierList, baseApi.toString());
        }
    }

}