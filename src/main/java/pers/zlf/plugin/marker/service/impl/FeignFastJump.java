package pers.zlf.plugin.marker.service.impl;

import com.intellij.psi.PsiClass;
import pers.zlf.plugin.marker.service.FastJump;
import pers.zlf.plugin.pojo.MappingAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.Map;

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
        return map.values().stream().anyMatch(l -> !l.getTargetMethodList().isEmpty());
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isController(fastJumpType, psiClass);
    }
}
