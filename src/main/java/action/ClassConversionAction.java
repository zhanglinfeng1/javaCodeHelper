package action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiUtilBase;
import constant.COMMON_CONSTANT;
import util.StringUtil;
import util.TypeUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassConversionAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        //获取当前的编辑器对象
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        //获取当前项目
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (null == project) {
            return;
        }
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiJavaFile psiJavaFile = (PsiJavaFile) file;
        if (null == psiJavaFile) {
            return;
        }
        PsiClass psiClass = psiJavaFile.getClasses()[0];
        PsiField[] fieldArr = psiClass.getFields();
        if (fieldArr.length == 0) {
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String parametersStr = "Object obj";
            String objectStr = "obj";
            PsiMethod deleteMethod = null;
            //获取构造方法
            PsiMethod[] constructorMethods = psiClass.getConstructors();
            if (constructorMethods.length > 0) {
                outCycle:
                for (PsiMethod psiMethod : constructorMethods) {
                    //获取方法为空的构造方法
                    if (!psiMethod.getText().contains("this.")) {
                        PsiParameter[] parameterArr = psiMethod.getParameterList().getParameters();
                        for (PsiParameter parameter : parameterArr) {
                            if (TypeUtil.isObject(parameter.getType().getCanonicalText())) {
                                parametersStr = Arrays.stream(parameterArr).map(PsiParameter::getText).collect(Collectors.joining(COMMON_CONSTANT.COMMA));
                                objectStr = parameter.getText().split(COMMON_CONSTANT.SPACE_REGEX)[1];
                                deleteMethod = psiMethod;
                                break outCycle;
                            }
                        }
                    }
                }
            }
            String finalObjectStr = objectStr;
            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            StringBuilder constructorMethodSb = new StringBuilder("public ").append(psiClass.getName()).append("(").append(parametersStr).append(") {");
            constructorMethodSb.append(Arrays.stream(fieldArr).map(f -> "this." + f.getName() + " = " + finalObjectStr + ".get" + StringUtil.toUpperCaseFirst(f.getName()) + "();").collect(Collectors.joining())).append("}");
            PsiMethod newConstructor = factory.createMethodFromText(constructorMethodSb.toString(), psiClass);
            if (null != deleteMethod) {
                psiClass.addAfter(newConstructor, deleteMethod);
                deleteMethod.delete();
            } else if (constructorMethods.length > 0) {
                psiClass.addAfter(newConstructor, constructorMethods[0]);
            } else {
                PsiMethod nullConstructor = factory.createMethodFromText("public "+ psiClass.getName() +"() {}", psiClass);
                psiClass.addAfter(newConstructor, fieldArr[fieldArr.length - 1]);
                psiClass.addAfter(nullConstructor, fieldArr[fieldArr.length - 1]);
            }
        });
    }
}
