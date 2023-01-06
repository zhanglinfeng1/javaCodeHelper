package service.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import pojo.MappingAnnotation;
import service.FastJump;
import util.MyPsiUtil;

import java.util.List;
import java.util.Map;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:51
 */
public class ControllerFastJump extends FastJump {

    public ControllerFastJump(String filterFolderName) {
        super(filterFolderName);
    }

    @Override
    public boolean end(Map<String, MappingAnnotation> map) {
        return false;
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isFeign(psiClass);
    }
}
