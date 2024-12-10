package pers.zlf.plugin.marker.feign;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import pers.zlf.plugin.constant.Annotation;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2022/10/17 16:35
 */
public class FeignFastJump extends BaseFastJump {

    public FeignFastJump() {
        super(ConfigFactory.getInstance().getFastJumpConfig().getControllerFolderName());
    }

    @Override
    protected boolean jump(Project project, VirtualFile virtualFile) {
        String moduleName = MyPsiUtil.getModuleName(virtualFile, project);
        List<String> gatewayModuleNameList = ConfigFactory.getInstance().getFastJumpConfig().getModuleNameList();
        return gatewayModuleNameList.stream().noneMatch(moduleName::equals);
    }

    @Override
    protected boolean checkTargetClass(PsiClass psiClass) {
        return MyPsiUtil.isController(psiClass);
    }

    @Override
    protected String getClassUrl(PsiClass psiClass) {
        return getMappingUrl(psiClass.getAnnotation(Annotation.REQUEST_MAPPING));
    }
}
