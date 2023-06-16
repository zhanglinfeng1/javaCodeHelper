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
    /** 代码行数Map */
    public static Map<String, Integer> lineCountMap = new HashMap<>();
    /** 贡献率Map */
    public static Map<String, String> contributionRateMap = new HashMap<>();
    /** 备注Map */
    private static Map<String, PresentationData> presentationDataMap = new HashMap<>();
    private static Map<String, String> locationStringMap = new HashMap<>();

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
            if (null == directoryName || !directoryName.startsWith(parentNodeName)) {
                return;
            }
            String locationString = StringUtil.toString(data.getLocationString());
            //实时统计
            if (commonConfig.isRealTimeStatistics()) {
                int count = 0;
                for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                    String moduleName = Optional.ofNullable(ModuleUtil.findModuleForFile(virtualFile, project)).map(Module::getName).orElse(COMMON.BLANK_STRING);
                    if (moduleName.startsWith(directoryName)) {
                        count = count + MyPsiUtil.getLineCount(virtualFile, fileTypeList, commonConfig.isCountComment());
                    }
                }
                lineCountMap.put(directoryName, count);
            }
            locationStringMap.put(directoryName, locationString);
            //更新备注
            updateNode(directoryName, data, locationString);
            presentationDataMap.put(directoryName, data);
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {

    }

    public static void updateNode() {
        presentationDataMap.forEach(CodeLinesCountDecorator::updateNode);
    }

    private static void updateNode(String moduleName, PresentationData data) {
        //更新备注
        updateNode(moduleName, data, StringUtil.toString(locationStringMap.get(moduleName)));
    }

    private static void updateNode(String moduleName, PresentationData data, String locationString) {
        //更新备注
        if (lineCountMap.containsKey(moduleName)) {
            locationString = COMMON.LEFT_PARENTHESES + lineCountMap.get(moduleName) + COMMON.RIGHT_PARENTHESES + locationString;
        }
        if (contributionRateMap.containsKey(moduleName)) {
            locationString = COMMON.LEFT_PARENTHESES + contributionRateMap.get(moduleName) + COMMON.RIGHT_PARENTHESES + locationString;
        }
        data.setLocationString(locationString);
    }
}
