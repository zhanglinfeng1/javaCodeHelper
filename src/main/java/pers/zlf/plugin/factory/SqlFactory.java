package pers.zlf.plugin.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.dialog.ToolWindowFirstDialog;
import pers.zlf.plugin.dialog.ToolWindowSecondDialog;
import pers.zlf.plugin.factory.service.SqlParse;
import pers.zlf.plugin.factory.service.impl.MysqlParse;
import pers.zlf.plugin.factory.service.impl.OracleParse;
import pers.zlf.plugin.factory.service.impl.PostgresqlParse;
import pers.zlf.plugin.pojo.TableInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: zhanglinfeng
 * @Date: create in 2022/8/26 18:20
 */
public class SqlFactory implements ToolWindowFactory {
    private final Map<String, SqlParse> sqlParseMap = new HashMap<>() {{
        put(COMMON.MYSQL, new MysqlParse());
        put(COMMON.ORACLE, new OracleParse());
        put(COMMON.POSTGRESQL, new PostgresqlParse());
    }};

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
                SqlParse sqlParse = sqlParseMap.get(firstDialog.getDataBaseType());
                Optional.ofNullable(sqlParse).orElseThrow(() -> new Exception("Database not support"));
                TableInfo tableInfo = sqlParse.getTableInfo(firstDialog.getSqlStr());
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
                secondDialog.clearTableContent();
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
