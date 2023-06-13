package pers.zlf.plugin.node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/9 15:26
 */
public class CodeLinesCountDecorator implements ProjectViewNodeDecorator {
    public static Map<String, PresentationData> lineCountMap = new HashMap<>();

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        //获取配置
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        Project project = node.getProject();
        if (CollectionUtil.isEmpty(fileTypeList) || project == null) {
            return;
        }
        if (node instanceof PsiDirectoryNode) {
            //只处理根节点
            String directoryName = node.getName();
            String parentNodeName = Optional.ofNullable(node.getParent()).map(AbstractTreeNode::getName).orElse(COMMON.BLANK_STRING);
            if (StringUtil.isEmpty(directoryName) || !directoryName.startsWith(parentNodeName)) {
                return;
            }
            //按模块统计
            int count = 0;
            for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                String moduleName = Optional.ofNullable(ModuleUtil.findModuleForFile(virtualFile, project)).map(Module::getName).orElse(COMMON.BLANK_STRING);
                if (moduleName.startsWith(directoryName)) {
                    count = count + MyPsiUtil.getLineCount(virtualFile, fileTypeList, commonConfig.isCountComment());
                }
            }
            if (0 != count) {
                data.setLocationString(COMMON.LEFT_PARENTHESES + count + COMMON.RIGHT_PARENTHESES + StringUtil.toString(data.getLocationString()));
                lineCountMap.put(directoryName, data);
            }
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {

    }
}
