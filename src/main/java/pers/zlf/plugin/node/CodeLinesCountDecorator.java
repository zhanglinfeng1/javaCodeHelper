package pers.zlf.plugin.node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.StringUtil;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/9 15:26
 */
public class CodeLinesCountDecorator implements ProjectViewNodeDecorator {

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        if (CollectionUtil.isEmpty(fileTypeList)) {
            return;
        }
        if (node instanceof PsiDirectoryNode) {
            Project project = node.getProject();
            if (project == null) {
                return;
            }
            PsiDirectoryNode directoryNode = (PsiDirectoryNode) node;
            String directoryName = directoryNode.getName();
            if (StringUtil.isEmpty(directoryName)) {
                return;
            }
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                if (directoryName.equals(module.getName())) {
                    String count = COMMON.LEFT_PARENTHESES + MyPsiUtil.getLineCount(module, fileTypeList, commonConfig.isCountComment()) + COMMON.RIGHT_PARENTHESES;
                    data.setLocationString(count + data.getLocationString());
                    return;
                }
            }
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {

    }
}
