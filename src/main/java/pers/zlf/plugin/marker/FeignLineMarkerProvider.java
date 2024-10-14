package pers.zlf.plugin.marker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.marker.feign.BaseFastJump;
import pers.zlf.plugin.marker.feign.ControllerFastJump;
import pers.zlf.plugin.marker.feign.FeignFastJump;
import pers.zlf.plugin.pojo.MappingAnnotation;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.Map;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/26 18:08
 */
public class FeignLineMarkerProvider extends BaseLineMarkerProvider<PsiClass> {

    @Override
    public boolean checkPsiElement(PsiElement element) {
        return element instanceof PsiClass;
    }

    @Override
    public void dealPsiElement() {
        //只处理controller和feign类
        BaseFastJump baseFastJump;
        if (MyPsiUtil.isFeign(currentElement)) {
            baseFastJump = new FeignFastJump();
        } else if (MyPsiUtil.isController(currentElement, ConfigFactory.getInstance().getFastJumpConfig().getModuleNameList())) {
            baseFastJump = new ControllerFastJump();
        } else {
            return;
        }
        //绑定跳转
        Map<String, MappingAnnotation> lineMarkerMap = baseFastJump.getLineMarkerMap(currentElement);
        lineMarkerMap.values().stream().filter(t -> !t.getTargetList().isEmpty()).forEach(t -> addLineMarker(t.getPsiAnnotation(), t.getTargetList()));
    }

}
