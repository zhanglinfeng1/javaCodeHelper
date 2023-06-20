package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.MESSAGE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BasicAction {

    @Override
    public boolean check() {
        //配置校验
        if (CollectionUtil.isEmpty(ConfigFactory.getInstance().getCommonConfig().getFileTypeList())) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(MESSAGE.CODE_STATISTICAL_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        return true;
    }

    @Override
    public void action() {
        countCodeLines(project);
    }

    public static void countCodeLines(Project project){
        //获取配置
        List<String> fileTypeList = ConfigFactory.getInstance().getCommonConfig().getFileTypeList();
        boolean countComment = ConfigFactory.getInstance().getCommonConfig().isCountComment();
        CodeLinesCountDecorator.clearLineCount();
        //按模块统计
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            int count = MyPsiUtil.getLineCount(virtualFile, fileTypeList, countComment);
            if (count == 0) {
                continue;
            }
            String moduleName = MyPsiUtil.getModuleNameByVirtualFile(virtualFile, project);
            CodeLinesCountDecorator.updateLineCount(moduleName, count);
        }
        //处理统计结果
        CodeLinesCountDecorator.updateNode();
    }
}
