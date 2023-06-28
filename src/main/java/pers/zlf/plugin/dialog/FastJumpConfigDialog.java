package pers.zlf.plugin.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2023/2/13 10:36
 */
public class FastJumpConfigDialog extends BaseDialog {
    private JPanel panel;
    private JTextField controllerTextField;
    private JTextField feignTextField;
    private JBTable moduleTable;
    private JButton addModuleButton;
    private JButton deleteModuleButton;
    private final Set<String> totalSelectList;

    public FastJumpConfigDialog() {
        totalSelectList = new HashSet<>(ConfigFactory.getInstance().getCommonConfig().getModuleNameList());
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                VirtualFile[] virtualFiles = ModuleRootManager.getInstance(module).getContentRoots();
                if (virtualFiles.length > 0) {
                    totalSelectList.add(MyPsiUtil.getModuleName(virtualFiles[0], project));
                }
            }
        }
        defaultTableModel = new DefaultTableModel(null, new String[]{Common.BLANK_STRING}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moduleTable.setModel(defaultTableModel);
        moduleTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addModuleButton.addActionListener(e -> Empty.of(getOptionalList()).ifPresent(list -> JBPopupFactory.getInstance().createPopupChooserBuilder(list).setTitle(Common.SELECT_MODULE)
                .setMovable(true).setItemChosenCallback(value -> this.addCallback(List.of(value))).createPopup().showUnderneathOf(addModuleButton)));

        deleteModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                defaultTableModel.removeRow(rowNum);
                addMouseListener(addModuleButton, IconEnum.ADD);
                if (moduleTable.getRowCount() == 0) {
                    removeMouseListener(deleteModuleButton, IconEnum.REMOVE);
                }
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        controllerTextField.setText(commonConfig.getControllerFolderName());
        feignTextField.setText(commonConfig.getFeignFolderName());
        defaultTableModel.getDataVector().clear();
        List<String> selectModuleList = commonConfig.getModuleNameList().stream().sorted().collect(Collectors.toList());
        addMouseListener(addModuleButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(selectModuleList)) {
            removeMouseListener(deleteModuleButton, IconEnum.REMOVE);
        } else {
            this.addCallback(selectModuleList);
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getControllerFolderName() {
        return controllerTextField.getText();
    }

    public String getFeignFolderName() {
        return feignTextField.getText();
    }

    public List<String> getModuleNameList(){
        return getTableContentList(defaultTableModel, 0);
    }

    private List<String> getOptionalList() {
        List<String> list = totalSelectList.stream().sorted().collect(Collectors.toList());
        list.removeAll(getModuleNameList());
        return list;
    }

    private void addCallback(List<String> valueList) {
        valueList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
        addMouseListener(deleteModuleButton, IconEnum.REMOVE);
        if (CollectionUtil.isEmpty(getOptionalList())) {
            removeMouseListener(addModuleButton, IconEnum.ADD);
        }
    }
}
