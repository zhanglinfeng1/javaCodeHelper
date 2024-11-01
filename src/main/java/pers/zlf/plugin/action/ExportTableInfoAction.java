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
        VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
        if (virtualFile == null){
            return;
        }
        export.exportXlsx(virtualFile.getPath());
    }
}
