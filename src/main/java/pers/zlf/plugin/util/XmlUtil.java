package pers.zlf.plugin.util;

import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        return Optional.ofNullable(file.getDocument()).map(XmlDocument::getRootTag)
                .filter(t -> StringUtil.isNotEmpty(rootTagName) && rootTagName.equals(t.getName()))
                .orElse(null);
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
        Arrays.stream(tagNames).forEach(t -> tagList.addAll(List.of(rootTag.findSubTags(t))));
        return tagList;
    }

}
