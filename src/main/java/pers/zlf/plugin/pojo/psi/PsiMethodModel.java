package pers.zlf.plugin.pojo.psi;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.List;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/7/29 13:00
 */
public class PsiMethodModel {
    /** 方法名 */
    private String name;
    /** 返回类型 */
    private String returnType;
    /** sql类型 */
    private String sqlType;
    /** 参数 */
    private List<PsiParameterModel> parameterModelList;

    public PsiMethodModel() {
    }

    public PsiMethodModel(String methodName, PsiType returnType) {
        this.name = methodName;
        if (null != returnType) {
            this.returnType = Optional.ofNullable(MyPsiUtil.getReferenceTypeClass(returnType)).map(PsiClass::getQualifiedName).orElse(returnType.getInternalCanonicalText());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public List<PsiParameterModel> getParameterModelList() {
        return parameterModelList;
    }

    public void setParameterModelList(List<PsiParameterModel> parameterModelList) {
        this.parameterModelList = parameterModelList;
    }
}
