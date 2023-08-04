package pers.zlf.plugin.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.TypeUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 14:18
 */
public class MethodCompletionContributor extends BaseCompletionContributor {
    /** 当前方法 */
    private PsiMethod currentMethod;
    /** 当前方法所在类 */
    private PsiClass currentMethodClass;
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> currentMethodVariableMap;
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> totalVariableMap;
    /** 自动补全List */
    private final List<LookupElementBuilder> builderList = new ArrayList<>();

    @Override
    protected boolean check() {
        //当前光标所在的方法
        currentMethod = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), PsiMethod.class);
        if (null != currentMethod && !currentMethod.isConstructor()) {
            this.currentMethodClass = currentMethod.getContainingClass();
            return null != currentMethodClass;
        }
        return false;
    }

    @Override
    protected void completion() {
        //当前方法内的变量
        currentMethodVariableMap = MyPsiUtil.getVariableMapFromMethod(currentMethod, currentElement.getTextOffset());
        currentMethodVariableMap.remove(currentText);
        //当前类的变量
        totalVariableMap = new HashMap<>(16);
        totalVariableMap.putAll(currentMethodVariableMap);
        totalVariableMap.putAll(MyPsiUtil.getVariableMapFromClass(currentMethodClass));
        //在新的一行
        if (MyPsiUtil.isNewLine(currentElement)) {
            //已有变量转化
            currentMethodVariableMap.entrySet().stream().filter(t -> t.getKey().contains(currentText))
                    .forEach(t -> addTransformation(t.getKey(), t.getValue(), t.getKey() + Common.EQ_STR));
            //寻找void类型方法
            addSameType(currentText, ClassType.VOID, Common.BLANK_STRING);
        } else if (currentElement instanceof PsiIdentifier && currentElement.getParent() instanceof PsiLocalVariable) {
            //当前元素是变量
            PsiLocalVariable variable = (PsiLocalVariable) currentElement.getParent();
            String variableName = MyPsiUtil.dealVariableName(variable.getName(), variable.getType(), new ArrayList<>(currentMethodVariableMap.keySet()));
            //新建变量转化
            addTransformation(variableName, variable.getType(), variableName + Common.EQ_STR);
            //寻找变量的同类型方法,无参需判断参数名
            addSameType(Common.BLANK_STRING, variable.getType().getInternalCanonicalText(), variableName + Common.EQ_STR);
        } else if (currentElement.getParent().getParent() instanceof PsiReturnStatement) {
            // 在return语句中
            Optional.ofNullable(currentMethod.getReturnType()).ifPresent(psiType -> {
                addTransformation(Common.BLANK_STRING, psiType, Common.BLANK_STRING);
                addSameType(currentText, psiType.getInternalCanonicalText(), Common.BLANK_STRING);
            });
        }
        builderList.forEach(this::addCompletionResult);
    }

    private void addSameType(String variableName, String typeName, String code) {
        //当前类的方法
        findFromClass(currentMethodClass, MyPsiUtil.getMethods(currentMethodClass, currentMethod, variableName), typeName, code + Common.THIS_STR);
        //方法所在类的变量
        for (PsiField psiField : currentMethodClass.getFields()) {
            if (StringUtil.isNotEmpty(variableName) && !psiField.getName().contains(variableName)) {
                continue;
            }
            Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(psiField.getType())).filter(psiFieldClass -> !TypeUtil.isSimpleType(psiFieldClass.getName()))
                    .ifPresent(psiFieldClass -> findFromClass(psiFieldClass, psiFieldClass.getMethods(), typeName, code + psiField.getName() + Common.DOT));
        }
    }

    private void findFromClass(PsiClass psiClass, PsiMethod[] methodArr, String typeName, String code) {
        List<String> setAndGetMethodList = Arrays.stream(psiClass.getFields()).map(f -> Common.SET + StringUtil.toUpperCaseFirst(f.getName())).collect(Collectors.toList());
        setAndGetMethodList.addAll(Arrays.stream(psiClass.getFields()).map(f -> Common.GET + StringUtil.toUpperCaseFirst(f.getName())).collect(Collectors.toList()));
        for (PsiMethod fieldMethod : methodArr) {
            if (builderList.size() > ConfigFactory.getInstance().getCommonConfig().getMaxCodeCompletionLength()) {
                return;
            }
            PsiType fieldMethodReturnType = fieldMethod.getReturnType();
            //返回类型不一致、set或get方法
            if (setAndGetMethodList.contains(fieldMethod.getName()) || null == fieldMethodReturnType || !typeName.equals(fieldMethodReturnType.getInternalCanonicalText())) {
                continue;
            }
            //变量所在类的方法包含的参数
            PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
            Empty.of(this.getParamNameList(psiParameterArr)).map(list -> String.format(Common.END_STR, String.join(Common.COMMA + Common.SPACE, list)))
                    .ifPresent(t -> builderList.add(LookupElementBuilder.create(code + fieldMethod.getName() + t).withPresentableText(code + fieldMethod.getName())));
        }
    }

    private List<String> getParamNameList(PsiParameter[] psiParameterArr) {
        List<String> paramNameList = new ArrayList<>();
        for (PsiParameter parameter : psiParameterArr) {
            PsiType variableType = totalVariableMap.get(parameter.getName());
            String parameterTypeStr = parameter.getType().getPresentableText();
            if (null != variableType && parameterTypeStr.equals(variableType.getPresentableText())) {
                paramNameList.add(parameter.getName());
                continue;
            }
            if (TypeUtil.isSimpleType(parameterTypeStr)) {
                return null;
            }
            totalVariableMap.entrySet().stream().filter(m -> parameterTypeStr.equals(m.getValue().getPresentableText())).map(Map.Entry::getKey).findAny().ifPresent(paramNameList::add);
        }
        return paramNameList;
    }

    private void addTransformation(String variableName, PsiType variableType, String startCode) {
        if (currentMethodVariableMap.isEmpty()) {
            return;
        }
        //当前变量类型的泛型类
        PsiClass psiClass = MyPsiUtil.getReferenceTypeClass(variableType);
        if (null == psiClass || TypeUtil.isSimpleType(psiClass.getName())) {
            return;
        }
        //变量类型存在
        PsiClass variableTypeClass = PsiUtil.resolveClassInClassTypeOnly(variableType);
        String endCode;
        if (TypeUtil.isList(variableTypeClass)) {
            endCode = psiClass.getName() + Common.COLLECT_LIST_STR;
        } else if (TypeUtil.isSet(variableTypeClass)) {
            endCode = psiClass.getName() + Common.COLLECT_SET_STR;
        } else {
            return;
        }
        //过滤只有一个参数的构造方法
        List<String> typeList = Arrays.stream(psiClass.getConstructors()).map(m -> m.getParameterList().getParameters())
                .filter(parameterArr -> 1 == parameterArr.length)
                .map(parameterArr -> parameterArr[0].getType().getInternalCanonicalText()).collect(Collectors.toList());
        if (typeList.isEmpty()) {
            return;
        }
        //方法内的所有变量
        for (Map.Entry<String, PsiType> entry : currentMethodVariableMap.entrySet()) {
            if (builderList.size() > ConfigFactory.getInstance().getCommonConfig().getMaxCodeCompletionLength()) {
                return;
            }
            String currentMethodVariableName = entry.getKey();
            if (currentMethodVariableName.equals(variableName)) {
                continue;
            }
            PsiType currentMethodVariableType = entry.getValue();
            String currentMethodVariableTypeName = currentMethodVariableType.getInternalCanonicalText();
            //list 或者 set 类型
            Equals.of(PsiUtil.resolveClassInClassTypeOnly(currentMethodVariableType)).and(TypeUtil::isList).or(TypeUtil::isSet)
                    .and(typeList.contains(StringUtil.getFirstMatcher(currentMethodVariableTypeName, Regex.ANGLE_BRACKETS).trim()))
                    .ifTrue(() -> builderList.add(LookupElementBuilder.create(startCode + currentMethodVariableName + Common.STREAM_MAP_STR + endCode)));
            //数组类型
            Equals.of(currentMethodVariableType).and(TypeUtil::isSimpleArr).and(typeList.contains(currentMethodVariableTypeName.split(Regex.LEFT_BRACKETS)[0]))
                    .ifTrue(() -> builderList.add(LookupElementBuilder.create(startCode + String.format(Common.ARRAYS_STREAM_STR, currentMethodVariableName) + endCode)
                            .withInsertHandler((context, item) -> {
                                PsiJavaFile javaFile = (PsiJavaFile) currentMethodClass.getContainingFile();
                                MyPsiUtil.findClassByFullName(variableType.getResolveScope(), ClassType.ARRAYS_PATH).ifPresent(javaFile::importClass);
                            })));
        }
    }
}
