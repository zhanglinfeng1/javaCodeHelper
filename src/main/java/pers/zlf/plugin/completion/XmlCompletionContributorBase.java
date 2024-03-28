package pers.zlf.plugin.completion;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import pers.zlf.plugin.constant.Xml;
import pers.zlf.plugin.util.XmlUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author zhanglinfeng
 * @date create in 2023/8/7 15:05
 */
public class XmlCompletionContributorBase extends BaseMyBatisCompletionContributor {

    @Override
    protected boolean check() {
        if (!(currentElement instanceof XmlToken) || ((XmlToken) currentElement).getTokenType() != XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            return false;
        }
        PsiFile file = parameters.getOriginalFile();
        if (file instanceof XmlFile) {
            currentTag = PsiTreeUtil.getParentOfType(currentElement, XmlTag.class);
            mapperTag = XmlUtil.getRootTagByName((XmlFile) file, Xml.MAPPER);
            return null != currentTag && null != mapperTag;
        }
        return false;
    }

    @Override
    protected void completion() {
        completionTextList = new ArrayList<>();
        parameterMap = new HashMap<>();
        //补全标签
        completionAttributeValue();
        //处理foreach标签中的item
        dealForeachTag(currentTag);
        completionTextList.forEach(t -> this.addCompletionResult(t, t));
    }
}
