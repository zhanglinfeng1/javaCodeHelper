package service.impl;

import com.intellij.psi.PsiClass;
import pojo.MappingAnnotation;
import service.FastJump;
import util.MyPsiUtil;

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
    public boolean end(Map<String, MappingAnnotation> map) {
        return map.values().stream().anyMatch(l -> !l.getTargetMethodList().isEmpty());
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isController(fastJumpType, psiClass);
    }
}
