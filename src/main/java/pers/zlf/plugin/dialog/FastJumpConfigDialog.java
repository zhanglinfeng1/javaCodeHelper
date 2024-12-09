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
import pers.zlf.plugin.pojo.config.FastJumpConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.MyPsiUtil;
import pers.zlf.plugin.util.SwingUtil;
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
public class FastJumpConfigDialog extends BaseDialog{
    private final Set<String> totalSelectList;
    private final DefaultTableModel defaultTableModel;
    /** ui组件 */
    private JPanel panel;
    private JTextField controllerTextField;
    private JTextField feignTextField;
    private JBTable moduleTable;
    private JButton addModuleButton;
    private JButton deleteModuleButton;

    public FastJumpConfigDialog() {
        totalSelectList = new HashSet<>(ConfigFactory.getInstance().getFastJumpConfig().getModuleNameList());
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
        //初始化按钮背景色
        SwingUtil.initButtonBackground(addModuleButton, deleteModuleButton);

        addModuleButton.addActionListener(e -> Empty.of(getOptionalList()).ifPresent(list -> JBPopupFactory.getInstance().createPopupChooserBuilder(list).setTitle(Common.SELECT_MODULE)
                .setMovable(true).setItemChosenCallback(value -> this.addCallback(List.of(value))).createPopup().showUnderneathOf(addModuleButton)));

        deleteModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                defaultTableModel.removeRow(rowNum);
                SwingUtil.addMouseListener(addModuleButton, IconEnum.ADD);
                if (moduleTable.getRowCount() == 0) {
                    SwingUtil.removeMouseListener(deleteModuleButton, IconEnum.REMOVE);
                }
            }
        });
    }

    @Override
    public void reset() {
        FastJumpConfig config = ConfigFactory.getInstance().getFastJumpConfig();
        controllerTextField.setText(config.getControllerFolderName());
        feignTextField.setText(config.getFeignFolderName());
        defaultTableModel.getDataVector().clear();
        List<String> selectModuleList = config.getModuleNameList().stream().sorted().collect(Collectors.toList());
        SwingUtil.addMouseListener(addModuleButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(selectModuleList)) {
            SwingUtil.removeMouseListener(deleteModuleButton, IconEnum.REMOVE);
        } else {
            this.addCallback(selectModuleList);
        }
    }

    @Override
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
        return SwingUtil.getTableContentList(defaultTableModel, 0);
    }

    private List<String> getOptionalList() {
        List<String> list = totalSelectList.stream().sorted().collect(Collectors.toList());
        list.removeAll(getModuleNameList());
        return list;
    }

    private void addCallback(List<String> valueList) {
        valueList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
        SwingUtil.addMouseListener(deleteModuleButton, IconEnum.REMOVE);
        if (CollectionUtil.isEmpty(getOptionalList())) {
            SwingUtil.removeMouseListener(addModuleButton, IconEnum.ADD);
        }
    }
}
