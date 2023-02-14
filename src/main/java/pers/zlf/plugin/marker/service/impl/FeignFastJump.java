package pers.zlf.plugin.marker.service.impl;

import com.intellij.psi.PsiClass;
import pers.zlf.plugin.constant.ANNOTATION;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.marker.service.FastJump;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:35
 */
public class FeignFastJump extends FastJump {

    public FeignFastJump() {
        super(ConfigFactory.getInstance().getCommonConfig().getControllerFolderName());
    }

    @Override
    public boolean jump(String virtualFilePath) {
        List<String> gatewayModuleNameList = ConfigFactory.getInstance().getCommonConfig().getModuleNameList();
        return gatewayModuleNameList.stream().noneMatch(virtualFilePath::contains);
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isController(psiClass);
    }

    @Override
    public String getClassUrl(PsiClass psiClass) {
        return getMappingUrl(psiClass.getAnnotation(ANNOTATION.REQUEST_MAPPING));
    }
}
