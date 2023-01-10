package lineMarker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import factory.ConfigFactory;
import service.FastJump;
import service.impl.ControllerFastJump;
import service.impl.FeignFastJump;
import util.MyPsiUtil;

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
