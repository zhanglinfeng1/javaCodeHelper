package pers.zlf.plugin.marker.service.impl;

import com.intellij.psi.PsiClass;
import pers.zlf.plugin.marker.service.FastJump;
import pers.zlf.plugin.pojo.MappingAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;

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
    public boolean end() {
        return false;
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isFeign(psiClass);
    }
}
