package pers.zlf.plugin.lineMarker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.service.FastJump;
import pers.zlf.plugin.service.impl.ControllerFastJump;
import pers.zlf.plugin.service.impl.FeignFastJump;
import pers.zlf.plugin.util.MyPsiUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 18:08
 */
public class FeignFastJumpProvider extends AbstractLineMarkerProvider<PsiClass> {

    private final String fastJumpType = ConfigFactory.getInstance().getCommonConfig().getFastJumpType();
    private final String controllerFolderName = ConfigFactory.getInstance().getCommonConfig().getControllerFolderName();
    private final String feignFolderName = ConfigFactory.getInstance().getCommonConfig().getFeignFolderName();

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof PsiClass;
    }

    @Override
    public void dealPsiElement() {
        FastJump fastJump;
        if (MyPsiUtil.isFeign(element)) {
            fastJump = new FeignFastJump(controllerFolderName);
        } else if (MyPsiUtil.isController(fastJumpType, element)) {
            fastJump = new ControllerFastJump(feignFolderName);
        } else {
            return;
        }
        fastJump.addLineMarker(result, element, fastJumpType);
    }

}
