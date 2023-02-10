package pers.zlf.plugin.marker.service.impl;

import com.intellij.psi.PsiClass;
import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.marker.service.FastJump;
import pers.zlf.plugin.util.MyPsiUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:35
 */
public class FeignFastJump extends FastJump {

    public FeignFastJump(String filterFolderName) {
        super(filterFolderName);
    }

    @Override
    public boolean end() {
        return COMMON.MODULAR.equals(fastJumpType) && map.values().stream().anyMatch(l -> !l.getTargetMethodList().isEmpty());
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isController(fastJumpType, psiClass);
    }

    @Override
    public String getClassUrl(PsiClass psiClass) {
        return getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
    }
}
