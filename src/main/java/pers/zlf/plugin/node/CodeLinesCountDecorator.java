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
import pers.zlf.plugin.pojo.CodeStatisticsInfo;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MathUtil;
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
    /** 代码统计Map */
    private static Map<String, CodeStatisticsInfo> codeStatisticsInfoMap = new HashMap<>();

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
            //记录原始备注
            CodeStatisticsInfo codeStatisticsInfo = Optional.ofNullable(codeStatisticsInfoMap.get(directoryName)).orElse(new CodeStatisticsInfo(directoryName, StringUtil.toString(data.getLocationString())));
            //实时统计
            if (commonConfig.isRealTimeStatistics()) {
                int count = 0;
                for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
                    String moduleName = Optional.ofNullable(ModuleUtil.findModuleForFile(virtualFile, project)).map(Module::getName).orElse(COMMON.BLANK_STRING);
                    if (moduleName.startsWith(directoryName)) {
                        count = count + MyPsiUtil.getLineCount(virtualFile, fileTypeList, commonConfig.isCountComment());
                    }
                }
                codeStatisticsInfo.setLineCount(count);
            }
            codeStatisticsInfo.setData(data);
            codeStatisticsInfoMap.put(directoryName, codeStatisticsInfo);
            //更新备注
            updateNode(codeStatisticsInfo);
        }
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {

    }

    /**
     * 代码行数清零
     */
    public static void clearLineCount() {
        codeStatisticsInfoMap.values().forEach(t -> t.setLineCount(0));
    }

    /**
     * 更新代码行数
     *
     * @param moduleName 模块名
     * @param lineCount  代码行数
     */
    public static void updateLineCount(String moduleName, int lineCount) {
        Optional.ofNullable(codeStatisticsInfoMap.get(moduleName)).ifPresent(t -> t.setLineCount(lineCount + t.getLineCount()));
    }

    /**
     * 贡献率清零
     */
    public static void clearContributionRate() {
        codeStatisticsInfoMap.values().forEach(t -> {
            t.setTotalGitLineCount(0);
            t.setMyGitLineCount(0);
        });
    }

    /**
     * 更新代码贡献率
     *
     * @param moduleName 模块名
     * @param totalCount git总行数
     * @param myCount    我提交的行数
     */
    public static void updateContributionRate(String moduleName, int totalCount, int myCount) {
        Optional.ofNullable(codeStatisticsInfoMap.get(moduleName)).ifPresent(t -> {
            t.setTotalGitLineCount(totalCount + t.getTotalGitLineCount());
            t.setMyGitLineCount(myCount + t.getMyGitLineCount());
        });
    }

    /**
     * 更新 代码行数和贡献率
     */
    public static void updateNode() {
        codeStatisticsInfoMap.values().forEach(CodeLinesCountDecorator::updateNode);
    }

    /**
     * 更新 代码行数和贡献率
     *
     * @param codeStatisticsInfo 统计信息
     */
    private static void updateNode(CodeStatisticsInfo codeStatisticsInfo) {
        String locationString = codeStatisticsInfo.getOldLocationString();
        //代码行数
        if (0 != codeStatisticsInfo.getLineCount()) {
            locationString = COMMON.LEFT_PARENTHESES + codeStatisticsInfo.getLineCount() + COMMON.RIGHT_PARENTHESES + locationString;
        }
        //贡献率
        if (0 != codeStatisticsInfo.getTotalGitLineCount()) {
            String contributionRate = MathUtil.percentage(codeStatisticsInfo.getMyGitLineCount(), codeStatisticsInfo.getTotalGitLineCount(), 2) + COMMON.PERCENT_SIGN;
            locationString = COMMON.LEFT_PARENTHESES + contributionRate + COMMON.RIGHT_PARENTHESES + locationString;
        }
        codeStatisticsInfo.getData().setLocationString(locationString);
    }
}
