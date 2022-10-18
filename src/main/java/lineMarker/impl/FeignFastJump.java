package lineMarker.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import lineMarker.FastJump;
import util.JavaFileUtil;

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
        return JavaFileUtil.isController(fastJumpType, psiClass);
    }
}
