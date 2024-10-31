package pers.zlf.plugin.action;

import com.intellij.database.psi.DbNamespace;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import pers.zlf.plugin.action.export.BaseExport;
import pers.zlf.plugin.action.export.DatabaseExport;
import pers.zlf.plugin.action.export.TableExport;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.StringUtil;

import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 17:44
 */
public class ExportTableInfoAction extends BaseAction {
    private BaseExport export;

    @Override
    public boolean isVisible() {
        //获取选中的PSI元素
        PsiElement psiElement = event.getData(LangDataKeys.PSI_ELEMENT);
        //选中的是单张表
        if (psiElement instanceof DbTable dbTable) {
            export = new TableExport(dbTable);
            return true;
        }
        //选中的是单个库
        if (psiElement instanceof DbNamespace dbNamespace) {
            export = new DatabaseExport(dbNamespace);
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        String path = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
        if (StringUtil.isEmpty(path)) {
            Message.showMessage(Message.TABLE_EXPORT_PATH_NOT_NULL);
            return;
        }
        export.exportXlsx(path);
    }
}
