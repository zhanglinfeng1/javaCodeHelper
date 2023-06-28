package pers.zlf.plugin.marker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.marker.service.impl.ControllerFastJump;
import pers.zlf.plugin.marker.service.impl.FeignFastJump;
import pers.zlf.plugin.util.MyPsiUtil;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/26 18:08
 */
public class FeignFastJumpProvider extends BaseLineMarkerProvider<PsiClass> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof PsiClass;
    }

    @Override
    public void dealPsiElement() {
        if (MyPsiUtil.isFeign(element)) {
            new FeignFastJump().addLineMarker(result, element);
        } else if (MyPsiUtil.isController(element, ConfigFactory.getInstance().getCommonConfig().getModuleNameList())) {
            new ControllerFastJump().addLineMarker(result, element);
        }
    }

}
