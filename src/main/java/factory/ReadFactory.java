package factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import constant.COMMON_CONSTANT;
import dialog.ToolWindowDialog;
import dialog.ToolWindowSubDialog;
import org.jetbrains.annotations.NotNull;
import util.StringUtil;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/26 18:20
 */
public class ReadFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //初始化组件
        ToolWindowDialog dialog = new ToolWindowDialog();
        ToolWindowSubDialog subDialog = new ToolWindowSubDialog();
        //添加到IDEA中
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(dialog.getContent(), COMMON_CONSTANT.BLANK_STRING, false);
        content.setDisplayName(COMMON_CONSTANT.BLANK_STRING);
        Content subContent = contentFactory.createContent(subDialog.getContent(), COMMON_CONSTANT.BLANK_STRING, false);
        subContent.setDisplayName(COMMON_CONSTANT.BLANK_STRING);
        toolWindow.getContentManager().addContent(content);
        //下一步
        dialog.getNextButton().addActionListener(e -> {
            try {
                String sqlStr = dialog.getTextArea().getText();
                if (StringUtil.isEmpty(sqlStr) || COMMON_CONSTANT.TEXT_AREA_PLACEHOLDER.equals(sqlStr)) {
                    throw new Exception("Sql不能为空");
                }
                String projectName = dialog.getProjectNameField().getText();
                if (COMMON_CONSTANT.PROJECT_INPUT_PLACEHOLDER.equals(projectName)) {
                    projectName = COMMON_CONSTANT.BLANK_STRING;
                }
                String packagePath = dialog.getPackagePathField().getText();
                if (COMMON_CONSTANT.PACKAGR_PATH_INPUT_PLACEHOLDER.equals(packagePath) || StringUtil.isEmpty(packagePath)) {
                    throw new Exception("包路径不能为空");
                }
                TemplateFactory.init(project.getBasePath(), dialog.getAuthorField().getText(), projectName, packagePath, sqlStr);
                toolWindow.getContentManager().removeContent(content, true);
                toolWindow.getContentManager().addContent(subContent);
                subDialog.initColumn();
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            }
        });
        //上一步
        subDialog.getBackButton().addActionListener(e -> {
            toolWindow.getContentManager().removeContent(subContent, true);
            toolWindow.getContentManager().addContent(content);
        });
        //生成代码
        subDialog.getButtonOK().addActionListener(e -> {
            try {
                TemplateFactory.create(subDialog.getQueryColumnList());
                subDialog.getQueryColumnList();
                toolWindow.getContentManager().removeContent(subContent, true);
                toolWindow.getContentManager().addContent(content);
                Messages.showMessageDialog(COMMON_CONSTANT.SUCCESS, COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

}
