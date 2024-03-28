package pers.zlf.plugin.completion;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.sql.psi.impl.SqlTokenElement;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.XmlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author zhanglinfeng
 * @date create in 2023/8/7 15:05
 */
public class SqlCompletionContributorBase extends BaseMyBatisCompletionContributor {

    @Override
    protected boolean check() {
        if (!(currentElement instanceof SqlTokenElement)) {
            return false;
        }
        String text = currentElement.getParent().getText();
        if (StringUtil.isNotEmpty(text) && (text.startsWith(Common.HASH_LEFT_BRACE) || text.startsWith(Common.DOLLAR_LEFT_BRACE))) {
            InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(currentElement.getProject());
            PsiElement position = parameters.getOriginalPosition();
            PsiFile file = Optional.ofNullable(position).map(injectedLanguageManager::getTopLevelFile).orElse(null);
            if (file instanceof XmlFile) {
                XmlFile xmlFile = (XmlFile) file;
                int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
                currentElement = xmlFile.findElementAt(offset);
                currentTag = PsiTreeUtil.getParentOfType(currentElement, XmlTag.class);
                mapperTag = XmlUtil.getRootTagByName((XmlFile) file, Xml.MAPPER);
                return null != currentTag && null != mapperTag;
            }
        }
        return false;
    }

    @Override
    protected void completion() {
        completionTextList = new ArrayList<>();
        parameterMap = new HashMap<>();
        //补全变量
        completionVariable();
        //处理foreach标签中的item
        dealForeachTag(currentTag);
        completionTextList.forEach(t -> this.addCompletionResult(t, t));
    }
}
