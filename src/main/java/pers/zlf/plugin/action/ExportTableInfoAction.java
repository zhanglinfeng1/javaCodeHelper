package pers.zlf.plugin.action;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.action.export.BaseExport;
import pers.zlf.plugin.action.export.DatabaseExport;
import pers.zlf.plugin.action.export.TableExport;
import pers.zlf.plugin.constant.MyDataKeys;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 17:44
 */
public class ExportTableInfoAction extends BaseAction {
    private BaseExport export;

    @Override
    protected boolean isVisible() {
        //获取选中的元素
        Object[] data = event.getData(MyDataKeys.DATABASE_NODES);
        if (data == null || data.length == 0) {
            return false;
        }
        //选中的是单张表
        if (data[0] instanceof DasTable dasTable) {
            export = new TableExport(dasTable);
            return true;
        }

        //选中的是单个库
        if (data[0] instanceof DasNamespace dasNamespace) {
            export = new DatabaseExport(dasNamespace);
            return true;
        }
        return false;
    }

    @Override
    protected void execute() {
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
        if (virtualFile == null) {
            return;
        }
        export.exportXlsx(project, virtualFile.getPath());
    }
}
