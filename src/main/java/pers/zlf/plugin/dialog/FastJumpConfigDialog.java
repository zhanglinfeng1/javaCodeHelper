package pers.zlf.plugin.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.TYPE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/13 10:36
 */
public class FastJumpConfigDialog {
    private JPanel panel;
    private JTextField controllerFolderNameTextField;
    private JTextField feignFolderNameTextField;
    private JBTable moduleNameTable;
    private JButton addModuleButton;
    private JButton deleteModuleButton;
    private JButton editModuleButton;
    private final Set<String> totalSelectList;
    private final DefaultTableModel defaultTableModel;

    public FastJumpConfigDialog() {
        Module[] modules = ModuleManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getModules();
        totalSelectList = Arrays.stream(modules).map(Module::getName).filter(t -> !t.endsWith(TYPE.MAIN_FILE_SUFFIX) && !t.endsWith(TYPE.TEST_FILE_SUFFIX)).collect(Collectors.toSet());
        totalSelectList.addAll(ConfigFactory.getInstance().getCommonConfig().getModuleNameList());
        defaultTableModel = new DefaultTableModel(null, new String[]{COMMON.MODULE_NAME_TABLE_HEADER}){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moduleNameTable.setModel(defaultTableModel);
        moduleNameTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addModuleButton.addActionListener(e -> Empty.of(getOptionalList()).map(list -> list.get(0)).isPresent(this::insertRow));
        deleteModuleButton.addActionListener(e -> Equals.of(moduleNameTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(defaultTableModel::removeRow));
        editModuleButton.addActionListener(e -> {
            int rowNum = moduleNameTable.getSelectedRow();
            if (rowNum >= 0) {
                List<String> optionalList = getOptionalList();
                optionalList.add(String.valueOf(defaultTableModel.getValueAt(rowNum, 0)));
                JBPopupFactory.getInstance().createPopupChooserBuilder(optionalList).setTitle(COMMON.SELECT_MODULE).setMovable(true)
                        .setItemChosenCallback(s -> defaultTableModel.setValueAt(s, rowNum, 0)).createPopup().showInCenterOf(panel);
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        controllerFolderNameTextField.setText(commonConfig.getControllerFolderName());
        feignFolderNameTextField.setText(commonConfig.getFeignFolderName());
        defaultTableModel.getDataVector().clear();
        commonConfig.getModuleNameList().forEach(this::insertRow);
    }

    public JComponent getComponent() {
        return panel;
    }

    public String getControllerFolderName() {
        return controllerFolderNameTextField.getText();
    }

    public String getFeignFolderName() {
        return feignFolderNameTextField.getText();
    }

    public List<String> getModuleNameList() {
        List<String> gatewayModuleNameList = new ArrayList<>();
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            gatewayModuleNameList.add(StringUtil.toString(defaultTableModel.getValueAt(i, 0)));
        }
        return gatewayModuleNameList;
    }

    private List<String> getOptionalList() {
        List<String> list = new ArrayList<>(totalSelectList);
        list.removeAll(getModuleNameList());
        return list;
    }

    private void insertRow(String value) {
        JTextField jTextField = new JTextField();
        jTextField.setEnabled(false);
        defaultTableModel.insertRow(defaultTableModel.getRowCount(), new String[]{value});
        moduleNameTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(jTextField));
    }
}
