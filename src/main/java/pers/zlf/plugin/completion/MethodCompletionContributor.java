package pers.zlf.plugin.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import pers.zlf.plugin.constant.ClassType;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Keyword;
import pers.zlf.plugin.constant.Regex;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.TypeUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/14 14:18
 */
public class MethodCompletionContributor extends BaseCompletionContributor {
    /** 当前方法 */
    private PsiMethod currentMethod;
    /** 当前方法所在类 */
    private PsiClass currentMethodClass;
    /** 当前方法所在类 */
    private List<PsiField> currentMethodClassFieldList;
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> currentMethodVariableMap;
    /** 当前方法包含的变量Map */
    private Map<String, PsiType> totalVariableMap;
    /** 剩余补全数量 */
    private int completionLength = 0;

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
        completionLength = ConfigFactory.getInstance().getCommonConfig().getMaxCodeCompletionLength();
        //当前方法内的变量
        currentMethodVariableMap = getVariableMapFromMethod(currentMethod, currentElement.getTextOffset());
        currentMethodVariableMap.remove(currentText);
        //当前类的变量
        currentMethodClassFieldList = MyPsiUtil.getPsiFieldList(currentMethodClass);
        totalVariableMap = new HashMap<>(16);
        totalVariableMap.putAll(currentMethodVariableMap);
        totalVariableMap.putAll(currentMethodClassFieldList.stream().collect(Collectors.toMap(PsiField::getName, PsiField::getType, (k1, k2) -> k2)));
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
            String variableName = MyPsiUtil.dealVariableName(currentText, variable.getType(), new ArrayList<>(currentMethodVariableMap.keySet()));
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
    }

    /**
     * 获取方法包含的变量
     *
     * @param psiMethod 方法
     * @param endOffset 当前元素位置
     * @return key:变量名 value:变量类型
     */
    private Map<String, PsiType> getVariableMapFromMethod(PsiMethod psiMethod, int endOffset) {
        Map<String, PsiType> variableMap = new HashMap<>(16);
        //方法参数
        Arrays.stream(psiMethod.getParameterList().getParameters()).forEach(t -> variableMap.put(t.getName(), t.getType()));
        //获取代码块中的变量
        Function<PsiElement, List<PsiLocalVariable>> function = element -> {
            List<PsiLocalVariable> localVariableList = new ArrayList<>();
            if (element.getTextOffset() <= endOffset && element instanceof PsiDeclarationStatement) {
                PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) element;
                Arrays.stream(declarationStatement.getDeclaredElements()).filter(t -> t instanceof PsiLocalVariable).map(t -> (PsiLocalVariable) t).forEach(localVariableList::add);
            }
            return localVariableList;
        };
        List<PsiLocalVariable> localVariableList = MyPsiUtil.getElementFromPsiCodeBlock(psiMethod.getBody(), function);
        localVariableList.forEach(t -> variableMap.put(t.getName(), t.getType()));
        return variableMap;
    }

    private void addSameType(String variableName, String typeName, String code) {
        //当前类的方法
        findFromClass(currentMethodClass, MyPsiUtil.getMethods(currentMethodClass, currentMethod, variableName), typeName, code + Keyword.JAVA_THIS + Common.DOT);
        //方法所在类的变量
        for (PsiField psiField : currentMethodClassFieldList) {
            if (StringUtil.isNotEmpty(variableName) && !psiField.getName().contains(variableName)) {
                continue;
            }
            Optional.ofNullable(PsiUtil.resolveClassInClassTypeOnly(psiField.getType())).filter(psiFieldClass -> !TypeUtil.isSimpleType(psiFieldClass.getName()))
                    .ifPresent(psiFieldClass -> findFromClass(psiFieldClass, psiFieldClass.getMethods(), typeName, code + psiField.getName() + Common.DOT));
        }
    }

    private void findFromClass(PsiClass psiClass, PsiMethod[] methodArr, String typeName, String code) {
        List<String> setAndGetMethodList = Stream.of(Common.SET, Common.GET).flatMap(o1 ->
                MyPsiUtil.getPsiFieldList(psiClass).stream().map(f -> StringUtil.toUpperCaseFirst(f.getName()))).collect(Collectors.toList());
        boolean currentStaticMethod = currentMethod.getModifierList().hasModifierProperty(PsiModifier.STATIC);
        for (PsiMethod fieldMethod : methodArr) {
            if (completionLength == 0) {
                return;
            }
            PsiType fieldMethodReturnType = fieldMethod.getReturnType();
            //返回类型不一致、set或get方法、静态方法调用非静态方法
            boolean fieldStaticMethod = fieldMethod.getModifierList().hasModifierProperty(PsiModifier.STATIC);
            if (setAndGetMethodList.contains(fieldMethod.getName()) || null == fieldMethodReturnType ||
                    !typeName.equals(fieldMethodReturnType.getInternalCanonicalText()) || (currentStaticMethod && !fieldStaticMethod)) {
                continue;
            }
            if (fieldStaticMethod){
                code = code.replace(Keyword.JAVA_THIS + Common.DOT,Common.BLANK_STRING);
            }
            String finalCode = code;
            //变量所在类的方法包含的参数
            PsiParameter[] psiParameterArr = fieldMethod.getParameterList().getParameters();
            Empty.of(this.getParamNameList(psiParameterArr)).map(list -> String.format(Common.END_STR, String.join(Common.COMMA + Common.SPACE, list)))
                    .ifPresent(t -> {
                        addCompletionResult(finalCode + fieldMethod.getName() + t);
                        completionLength--;
                    });
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
            if (completionLength == 0) {
                return;
            }
            String currentMethodVariableName = entry.getKey();
            if (currentMethodVariableName.equals(variableName)) {
                continue;
            }
            PsiType currentMethodVariableType = entry.getValue();
            String currentMethodVariableTypeName = currentMethodVariableType.getInternalCanonicalText();
            PsiClass currentMethodVariableClass = PsiUtil.resolveClassInClassTypeOnly(currentMethodVariableType);
            //list 或者 set 类型
            if (TypeUtil.isList(currentMethodVariableClass) || TypeUtil.isSet(currentMethodVariableClass)) {
                if (typeList.contains(StringUtil.getFirstMatcher(currentMethodVariableTypeName, Regex.ANGLE_BRACKETS).trim())) {
                    addCompletionResult(startCode + currentMethodVariableName + Common.STREAM_MAP_STR + endCode);
                    completionLength--;
                }
            } else if (TypeUtil.isSimpleArr(currentMethodVariableType)) {
                //数组类型
                if (typeList.contains(currentMethodVariableTypeName.split(Regex.LEFT_BRACKETS)[0])) {
                    String completionText = startCode + String.format(Common.ARRAYS_STREAM_STR, currentMethodVariableName) + endCode;
                    InsertHandler<LookupElement> insertHandler = (context, item) -> MyPsiUtil.importClass(currentMethodClass.getContainingFile(), ClassType.ARRAYS_PATH, ClassType.COLLECTORS);
                    addCompletionResult(completionText, completionText, insertHandler);
                    completionLength--;
                }
            }
        }
    }
}
