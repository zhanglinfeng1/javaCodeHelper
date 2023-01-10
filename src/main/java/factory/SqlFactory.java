package factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import constant.COMMON;
import dialog.ToolWindowFirstDialog;
import dialog.ToolWindowSecondDialog;
import org.jetbrains.annotations.NotNull;
import pojo.TableInfo;
import service.SqlParse;
import service.impl.MysqlParse;
import service.impl.OracleParse;
import service.impl.PostgresqlParse;

import java.util.Optional;

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
        ContentFactory contentFactory = toolWindow.getContentManager().getFactory();
        Content content = contentFactory.createContent(firstDialog.getContent(), COMMON.BLANK_STRING, false);
        content.setDisplayName(COMMON.BLANK_STRING);
        Content subContent = contentFactory.createContent(secondDialog.getContent(), COMMON.BLANK_STRING, false);
        subContent.setDisplayName(COMMON.BLANK_STRING);
        toolWindow.getContentManager().addContent(content);

        //下一步
        firstDialog.getNextButton().addActionListener(e -> {
            try {
                //解析sql
                SqlParse sqlParse;
                String sqlStr = firstDialog.getSqlStr();
                switch (firstDialog.getDataBaseType()) {
                    case COMMON.MYSQL:
                        sqlParse = new MysqlParse(sqlStr);
                        break;
                    case COMMON.ORACLE:
                        sqlParse = new OracleParse(sqlStr);
                        break;
                    case COMMON.POSTGRESQL:
                        sqlParse = new PostgresqlParse(sqlStr);
                        break;
                    default:
                        throw new Exception("Database not support");
                }
                TableInfo tableInfo = sqlParse.getTableInfo();
                tableInfo.setAuthor(firstDialog.getAuthor());
                //初始化文件路径
                TemplateFactory.getInstance().init(firstDialog.getFullPath(), firstDialog.getPackagePathField(), tableInfo);
                this.display(toolWindow, subContent);
                secondDialog.initColumn(tableInfo);
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
            }
        });
        //上一步
        secondDialog.getBackButton().addActionListener(e -> this.display(toolWindow, content));
        //生成代码
        secondDialog.getSubmitButton().addActionListener(e -> {
            try {
                TemplateFactory.getInstance().create(secondDialog.getQueryColumnList(), secondDialog.useDefaultTemplate());
                this.display(toolWindow, content);
                Messages.showMessageDialog(COMMON.SUCCESS, COMMON.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), COMMON.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    private void display(ToolWindow toolWindow, Content content) {
        Optional.ofNullable(toolWindow.getContentManager().getSelectedContent()).ifPresent(t -> toolWindow.getContentManager().removeContent(t, true));
        toolWindow.getContentManager().addContent(content);
    }
}
