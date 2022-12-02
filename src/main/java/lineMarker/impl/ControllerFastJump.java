package lineMarker.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import lineMarker.FastJump;
import util.MyPsiUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:51
 */
public class ControllerFastJump extends FastJump {

    public ControllerFastJump(PsiClass psiClass, PsiMethod psiMethod, String filterFolderName, String fastJumpType) {
        super(psiClass, psiMethod, filterFolderName, fastJumpType);
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isFeign(psiClass);
    }
}
