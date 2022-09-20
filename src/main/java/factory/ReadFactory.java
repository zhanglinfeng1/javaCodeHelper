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
import pojo.ColumnInfo;
import util.StringUtil;

import java.util.List;

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
        content.setDisplayName("基础信息");
        Content subContent = contentFactory.createContent(subDialog.getContent(), COMMON_CONSTANT.BLANK_STRING, false);
        subContent.setDisplayName("设置查询条件");
        toolWindow.getContentManager().addContent(content);
        //下一步
        dialog.getNextButton().addActionListener(e -> {
            try {
                String sqlStr = dialog.getTextArea().getText();
                if (StringUtil.isEmpty(sqlStr)) {
                    throw new Exception("Sql不能为空");
                }
                TemplateFactory.init(project.getBasePath(), dialog.getAuthorField().getText(), dialog.getProjectNameField().getText(), dialog.getPackagePathField().getText(), sqlStr);
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
                List<ColumnInfo> queryColumnList = subDialog.getQueryColumnList();
                TemplateFactory.create(queryColumnList);
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
