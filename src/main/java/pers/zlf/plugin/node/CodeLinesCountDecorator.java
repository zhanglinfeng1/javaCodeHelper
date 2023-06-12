package pers.zlf.plugin.node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            //获取节点文件夹名
            PsiDirectoryNode directoryNode = (PsiDirectoryNode) node;
            String directoryName = directoryNode.getName();
            if (StringUtil.isEmpty(directoryName)) {
                return;
            }
            //按模块统计
            Arrays.stream(ModuleManager.getInstance(project).getModules()).filter(t -> directoryName.equals(t.getName())).findAny().ifPresent(module -> {
                String count = COMMON.LEFT_PARENTHESES + MyPsiUtil.getLineCount(module, fileTypeList, commonConfig.isCountComment()) + COMMON.RIGHT_PARENTHESES;
                data.setLocationString(count + StringUtil.toString(data.getLocationString()));
                lineCountMap.put(module.getName(), data);
            });
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {

    }
}
