package pojo;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiStatement;
import constant.COMMON_CONSTANT;
import util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/11/18 9:38
 */
public class AutoCompletion {
    /** 返回类型 */
    private String returnTypeFullName;
    /** 返回类型 */
    private String returnTypeShortName;
    /** 返回类型的泛型对象类型 */
    private String paradigmType;
    /** 变量名 */
    private String parameterName;

    public AutoCompletion() {
    }

    public AutoCompletion(String returnTypeFullName) {
        this.returnTypeFullName = returnTypeFullName;
        this.returnTypeShortName = returnTypeFullName;
        this.parameterName = StringUtil.toLowerCaseFirst(returnTypeFullName);
    }

    public String getStartCode() {
        return this.returnTypeFullName + COMMON_CONSTANT.SPACE + this.parameterName + COMMON_CONSTANT.EQ_STR;
    }

    public String getEndCode() {
        return StringUtil.isEmpty(paradigmType) ? COMMON_CONSTANT.END_STR : COMMON_CONSTANT.GENERIC_PARADIGM_END_STR;
    }

    public boolean checkParameterExist(PsiCodeBlock codeBlock) {
        List<String> parameterList = new ArrayList<>();
        if (null != codeBlock) {
            for (PsiStatement psiStatement : codeBlock.getStatements()) {
                if (psiStatement instanceof PsiDeclarationStatement) {
                    PsiDeclarationStatement ps = (PsiDeclarationStatement) psiStatement;
                    parameterList = Arrays.stream(ps.getDeclaredElements()).filter(p -> p instanceof PsiLocalVariable).map(p -> ((PsiLocalVariable) p).getName()).collect(Collectors.toList());
                }
            }
        }
        return parameterList.contains(parameterName);
    }

    public void dealParameterName(PsiCodeBlock codeBlock) {
        if (checkParameterExist(codeBlock)) {
            parameterName = parameterName + 1;
            dealParameterName(codeBlock, 1);
        }
    }

    public void dealParameterName(PsiCodeBlock codeBlock, int num) {
        if (checkParameterExist(codeBlock)) {
            num++;
            parameterName = parameterName.substring(0, parameterName.length() - 1) + num;
            dealParameterName(codeBlock, num);
        }
    }

    public String getReturnTypeFullName() {
        return returnTypeFullName;
    }

    public void setReturnTypeFullName(String returnTypeFullName) {
        this.returnTypeFullName = returnTypeFullName;
    }

    public String getReturnTypeShortName() {
        return returnTypeShortName;
    }

    public void setReturnTypeShortName(String returnTypeShortName) {
        this.returnTypeShortName = returnTypeShortName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParadigmType() {
        return paradigmType;
    }

    public void setParadigmType(String paradigmType) {
        this.paradigmType = paradigmType;
    }
}
