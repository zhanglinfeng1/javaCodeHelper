package completionContributor;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiUtil;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/16 19:02
 */
public class ConstructorCompletion extends BasicCompletion {

    public ConstructorCompletion(PsiMethod currentMethod) {
        super(currentMethod);
    }

    @Override
    public List<LookupElementBuilder> getLookupElement() {
        PsiClass psiClass = (PsiClass) currentMethod.getParent();
        //构造方法的方法体
        String currentMethodBodyStr = COMMON_CONSTANT.BLANK_STRING;
        PsiCodeBlock codeBlock = currentMethod.getBody();
        if (codeBlock != null) {
            currentMethodBodyStr = codeBlock.getText();
        }
        List<String> list = new ArrayList<>();
        List<String> existFieldNameList = new ArrayList<>();
        //构造方法所在类的变量
        PsiField[] currentMethodClassFieldArr = psiClass.getFields();
        PsiParameter[] currentMethodParameterArr = currentMethod.getParameterList().getParameters();
        for (PsiField methodClassField : currentMethodClassFieldArr) {
            String fieldName = methodClassField.getName();
            if (currentMethodBodyStr.contains("this." + fieldName)) {
                existFieldNameList.add(fieldName);
                continue;
            }
            //构造方法的参数
            for (PsiParameter parameter : currentMethodParameterArr) {
                if (existFieldNameList.contains(parameter.getName())) {
                    continue;
                }
                if (parameter.getName().equals(fieldName)) {
                    list.add("this." + fieldName + " = " + fieldName + COMMON_CONSTANT.SEMICOLON);
                    existFieldNameList.add(fieldName);
                    continue;
                }
                PsiClass parameterClass = PsiUtil.resolveClassInClassTypeOnly(parameter.getType());
                if (null == parameterClass) {
                    continue;
                }
                PsiField[] parameterClassFieldArr = parameterClass.getFields();
                for (PsiField field : parameterClassFieldArr) {
                    if (existFieldNameList.contains(field.getName())) {
                        continue;
                    }
                    if (field.getName().equals(fieldName)) {
                        list.add("this." + fieldName + " = " + parameter.getName() + ".get" + StringUtil.toUpperCaseFirst(fieldName) + "();");
                        existFieldNameList.add(fieldName);
                    }
                }
            }
        }
        String str = list.stream().filter(StringUtil::isNotEmpty).collect(Collectors.joining("\n"));
        List<LookupElementBuilder> lookupElementBuilderList = new ArrayList<>();
        if (StringUtil.isNotEmpty(str)) {
            lookupElementBuilderList.add(LookupElementBuilder.create(str).withPresentableText("fillConstructor"));
        }
        return lookupElementBuilderList;
    }

}
