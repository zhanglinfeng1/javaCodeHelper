package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import factory.ConfigFactory;
import org.jetbrains.annotations.NotNull;
import service.FastJump;
import service.impl.ControllerFastJump;
import service.impl.FeignFastJump;
import util.MyPsiUtil;

import java.util.Collection;

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
    public void addLineMarker(PsiClass psiClass, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        FastJump fastJump;
        if (MyPsiUtil.isFeign(psiClass)) {
            fastJump = new FeignFastJump(controllerFolderName);
        } else if (MyPsiUtil.isController(fastJumpType, psiClass)) {
            fastJump = new ControllerFastJump(feignFolderName);
        } else {
            return;
        }
        fastJump.addLineMarker(result, psiClass, fastJumpType);
    }

}
