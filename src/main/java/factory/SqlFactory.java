package factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import constant.COMMON_CONSTANT;
import dialog.ToolWindowFirstDialog;
import dialog.ToolWindowSecondDialog;
import factory.impl.MysqlParse;
import factory.impl.OracleParse;
import factory.impl.PostgresqlParse;
import org.jetbrains.annotations.NotNull;
import pojo.TableInfo;
import util.StringUtil;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/26 18:20
 */
public class SqlFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //初始化组件
        ToolWindowFirstDialog firstDialog = new ToolWindowFirstDialog();
        ToolWindowSecondDialog secondDialog = new ToolWindowSecondDialog();
        //添加到IDEA中
        //TODO ContentFactory.SERVICE.getInstance() 计划删除
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(firstDialog.getContent(), COMMON_CONSTANT.BLANK_STRING, false);
        content.setDisplayName(COMMON_CONSTANT.BLANK_STRING);
        Content subContent = contentFactory.createContent(secondDialog.getContent(), COMMON_CONSTANT.BLANK_STRING, false);
        subContent.setDisplayName(COMMON_CONSTANT.BLANK_STRING);
        toolWindow.getContentManager().addContent(content);

        //下一步
        firstDialog.getNextButton().addActionListener(e -> {
            try {
                //解析sql
                SqlParse sqlParse;
                String sqlStr = firstDialog.getSqlStr();
                switch (firstDialog.getDataBaseType()) {
                    case COMMON_CONSTANT.MYSQL:
                        sqlParse = new MysqlParse(sqlStr);
                        break;
                    case COMMON_CONSTANT.ORACLE:
                        sqlParse = new OracleParse(sqlStr);
                        break;
                    case COMMON_CONSTANT.POSTGRESQL:
                        sqlParse = new PostgresqlParse(sqlStr);
                        break;
                    default:
                        throw new Exception("Database not exist");
                }
                TableInfo tableInfo = sqlParse.getTableInfo();
                tableInfo.setAuthor(firstDialog.getAuthor());
                //初始化文件路径
                TemplateFactory.getInstance().init(firstDialog.getFullPath(), firstDialog.getPackagePathField(), tableInfo);
                this.display(toolWindow, subContent);
                secondDialog.initColumn(tableInfo);
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            }
        });
        //上一步
        secondDialog.getBackButton().addActionListener(e -> this.display(toolWindow, content));
        //生成代码
        secondDialog.getButtonOK().addActionListener(e -> {
            try {
                //添加自定义模板
                String customTemplatesPath = ConfigFactory.getInstance().getCommonConfig().getCustomTemplatesPath();
                if (StringUtil.isNotEmpty(customTemplatesPath) && !COMMON_CONSTANT.CUSTOMER_TEMPLATE_PATH_INPUT_PLACEHOLDER.equals(customTemplatesPath)) {
                    TemplateFactory.getInstance().useCustomTemplates(customTemplatesPath);
                }
                TemplateFactory.getInstance().create(secondDialog.getQueryColumnList());
                this.display(toolWindow, content);
                Messages.showMessageDialog(COMMON_CONSTANT.SUCCESS, COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON_CONSTANT.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    private void display(ToolWindow toolWindow, Content content) {
        Content selectContent = toolWindow.getContentManager().getSelectedContent();
        if (null != selectContent) {
            toolWindow.getContentManager().removeContent(selectContent, true);
        }
        toolWindow.getContentManager().addContent(content);
    }
}
