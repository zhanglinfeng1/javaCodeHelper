package pers.zlf.plugin.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.MESSAGE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.node.CodeLinesCountDecorator;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/14 11:48
 */
public class CodeLineCountAction extends BasicAction {
    /** 参与统计的文件类型 */
    private List<String> fileTypeList;
    /** 统计注释 */
    private boolean countComment;

    @Override
    public boolean check() {
        //获取配置
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        fileTypeList = commonConfig.getFileTypeList();
        if (CollectionUtil.isEmpty(fileTypeList)) {
            WriteCommandAction.runWriteCommandAction(project, () -> Messages.showMessageDialog(MESSAGE.CODE_STATISTICAL_CONFIGURATION, COMMON.BLANK_STRING, Messages.getInformationIcon()));
            return false;
        }
        countComment = commonConfig.isCountComment();
        return true;
    }

    @Override
    public void action() {
        Map<String, Integer> lineCountMap = new HashMap<>();
        CodeLinesCountDecorator.lineCountMap.clear();
        //按模块统计
        for (VirtualFile virtualFile : ProjectRootManager.getInstance(project).getContentSourceRoots()) {
            String moduleName = MyPsiUtil.getModuleNameByVirtualFile(virtualFile, project);
            int count = MyPsiUtil.getLineCount(virtualFile, fileTypeList, countComment);
            lineCountMap.put(moduleName, Optional.ofNullable(lineCountMap.get(moduleName)).map(t -> t + count).orElse(count));
        }
        //处理统计结果
        lineCountMap.forEach((moduleName, count) -> {
            if (0 == count) {
                return;
            }
            CodeLinesCountDecorator.lineCountMap.put(moduleName, count);
            //更新代码行数
            CodeLinesCountDecorator.updateNode();
        });
    }

}
