package service.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import service.FastJump;
import util.MyPsiUtil;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/10/17 16:35
 */
public class FeignFastJump extends FastJump {

    public FeignFastJump(PsiClass psiClass, PsiMethod psiMethod, String filterFolderName, String fastJumpType) {
        super(psiClass, psiMethod, filterFolderName, fastJumpType);
    }

    @Override
    public boolean checkClass(PsiClass psiClass) {
        return MyPsiUtil.isController(fastJumpType, psiClass);
    }
}
