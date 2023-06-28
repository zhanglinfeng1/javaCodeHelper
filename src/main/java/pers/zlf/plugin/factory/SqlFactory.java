package pers.zlf.plugin.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.dialog.ToolWindowFirstDialog;
import pers.zlf.plugin.dialog.ToolWindowSecondDialog;
import pers.zlf.plugin.factory.service.BaseSqlParse;
import pers.zlf.plugin.factory.service.impl.MysqlParse;
import pers.zlf.plugin.factory.service.impl.OracleParse;
import pers.zlf.plugin.factory.service.impl.PostgresqlParse;
import pers.zlf.plugin.pojo.TableInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: zhanglinfeng
 * @date: create in 2022/8/26 18:20
 */
public class SqlFactory implements ToolWindowFactory {
    private final Map<String, BaseSqlParse> sqlParseMap = new HashMap<>() {{
        put(Common.MYSQL, new MysqlParse());
        put(Common.ORACLE, new OracleParse());
        put(Common.POSTGRESQL, new PostgresqlParse());
    }};

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //初始化组件
        ToolWindowFirstDialog firstDialog = new ToolWindowFirstDialog();
        ToolWindowSecondDialog secondDialog = new ToolWindowSecondDialog();
        //添加到IDEA中
        ContentFactory contentFactory = toolWindow.getContentManager().getFactory();
        Content content = contentFactory.createContent(firstDialog.getContent(), Common.BLANK_STRING, false);
        content.setDisplayName(Common.BLANK_STRING);
        Content subContent = contentFactory.createContent(secondDialog.getContent(), Common.BLANK_STRING, false);
        subContent.setDisplayName(Common.BLANK_STRING);
        toolWindow.getContentManager().addContent(content);

        //下一步
        firstDialog.getNextButton().addActionListener(e -> {
            try {
                //解析sql
                BaseSqlParse baseSqlParse = sqlParseMap.get(firstDialog.getDataBaseType());
                Optional.ofNullable(baseSqlParse).orElseThrow(() -> new Exception("Database not support"));
                TableInfo tableInfo = baseSqlParse.getTableInfo(firstDialog.getSqlStr());
                tableInfo.setAuthor(firstDialog.getAuthor());
                //初始化文件路径
                TemplateFactory.getInstance().init(firstDialog.getFullPath(), firstDialog.getPackagePathField(), tableInfo);
                this.display(toolWindow, subContent);
                secondDialog.initColumn(tableInfo);
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
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
                Messages.showMessageDialog(Common.SUCCESS, Common.BLANK_STRING, Messages.getInformationIcon());
            } catch (Exception ex) {
                Messages.showMessageDialog(ex.getMessage(), Common.BLANK_STRING, Messages.getInformationIcon());
            }
        });
    }

    private void display(ToolWindow toolWindow, Content content) {
        Optional.ofNullable(toolWindow.getContentManager().getSelectedContent()).ifPresent(t -> toolWindow.getContentManager().removeContent(t, true));
        toolWindow.getContentManager().addContent(content);
    }
}
