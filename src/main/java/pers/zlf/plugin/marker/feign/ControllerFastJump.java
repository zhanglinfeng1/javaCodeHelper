package pers.zlf.plugin.marker.feign;

import com.intellij.psi.PsiClass;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.MyPsiUtil;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/17 16:51
 */
public class ControllerFastJump extends BaseFastJump {

    public ControllerFastJump() {
        super(ConfigFactory.getInstance().getCommonConfig().getFeignFolderName());
    }

    @Override
    public boolean jump(String virtualFilePath) {
        return true;
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isFeign(psiClass);
    }

    @Override
    public String getClassUrl(PsiClass psiClass) {
        return Common.BLANK_STRING;
    }
}