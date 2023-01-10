package util;

import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/10 10:09
 */
public class XmlUtil {

    /**
     * 获取指定名称的根节点
     *
     * @param file        xml文件
     * @param rootTagName rootTagName
     * @return XmlTag
     */
    public static XmlTag getRootTagByName(XmlFile file, String rootTagName) {
        XmlDocument document = file.getDocument();
        if (document == null) {
            return null;
        }
        XmlTag rootTag = document.getRootTag();
        if (rootTag == null || (StringUtil.isNotEmpty(rootTagName) && !rootTagName.equals(rootTag.getName()))) {
            return null;
        }
        return rootTag;
    }

    /**
     * 通过名称查找XmlTag
     *
     * @param rootTag  根节点
     * @param tagNames tagNames
     * @return List<XmlTag>
     */
    public static List<XmlTag> findTags(XmlTag rootTag, String... tagNames) {
        List<XmlTag> tagList = new ArrayList<>();
        Arrays.stream(tagNames).forEach(t -> tagList.addAll(Arrays.asList(rootTag.findSubTags(t))));
        return tagList;
    }

}
