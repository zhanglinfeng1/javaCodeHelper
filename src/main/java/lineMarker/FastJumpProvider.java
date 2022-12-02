package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import constant.COMMON;
import factory.ConfigFactory;
import lineMarker.impl.ControllerFastJump;
import lineMarker.impl.FeignFastJump;
import org.jetbrains.annotations.NotNull;
import constant.ICON;
import util.MyPsiUtil;

import java.util.Collection;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 18:08
 */
public class FastJumpProvider extends RelatedItemLineMarkerProvider {

    private final String fastJumpType = ConfigFactory.getInstance().getCommonConfig().getFastJumpType();
    private final String controllerFolderName = ConfigFactory.getInstance().getCommonConfig().getControllerFolderName();
    private final String feignFolderName = ConfigFactory.getInstance().getCommonConfig().getFeignFolderName();

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass psiClass = psiMethod.getContainingClass();
            FastJump fastJump;
            if (MyPsiUtil.isFeign(psiClass)) {
                fastJump = new FeignFastJump(psiClass, psiMethod, controllerFolderName, fastJumpType);
            } else if (MyPsiUtil.isController(fastJumpType, psiClass)) {
                fastJump = new ControllerFastJump(psiClass, psiMethod, feignFolderName, fastJumpType);
            } else {
                return;
            }
            List<PsiMethod> elementList = fastJump.getMethodList();
            if (!elementList.isEmpty()) {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(elementList).setTooltipText(COMMON.BLANK_STRING);
                result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}
