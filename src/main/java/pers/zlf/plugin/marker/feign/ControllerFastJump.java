package pers.zlf.plugin.marker.feign;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
        super(ConfigFactory.getInstance().getFastJumpConfig().getFeignFolderName());
    }

    @Override
    protected boolean jump(Project project, VirtualFile virtualFile) {
        return true;
    }

    @Override
    protected boolean checkTargetClass(PsiClass psiClass) {
        return MyPsiUtil.isFeign(psiClass);
    }

    @Override
    protected String getClassUrl(PsiClass psiClass) {
        return Common.BLANK_STRING;
    }
}
