package pers.zlf.plugin.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.constant.TYPE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/2/13 10:36
 */
public class FastJumpConfigDialog extends BaseDialog {
    private JPanel panel;
    private JTextField controllerTextField;
    private JTextField feignTextField;
    private JBTable moduleTable;
    private JButton addModuleButton;
    private JButton deleteModuleButton;
    private JButton editModuleButton;
    private final Set<String> totalSelectList;
    private final DefaultTableModel defaultTableModel;

    public FastJumpConfigDialog() {
        totalSelectList = new HashSet<>(ConfigFactory.getInstance().getCommonConfig().getModuleNameList());
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            totalSelectList.addAll(Arrays.stream(modules).map(Module::getName).filter(t -> !t.endsWith(TYPE.MAIN_FILE_SUFFIX) && !t.endsWith(TYPE.TEST_FILE_SUFFIX)).collect(Collectors.toSet()));
        }
        defaultTableModel = new DefaultTableModel(null, new String[]{COMMON.BLANK_STRING}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moduleTable.setModel(defaultTableModel);
        moduleTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addModuleButton.addActionListener(e -> Empty.of(getOptionalList()).ifPresent(list -> JBPopupFactory.getInstance().createPopupChooserBuilder(list).setTitle(COMMON.SELECT_MODULE)
                .setMovable(true).setItemChosenCallback(this::addCallback).createPopup().showUnderneathOf(addModuleButton)));

        deleteModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                defaultTableModel.removeRow(rowNum);
                addMouseListener(addModuleButton, ICON_ENUM.ADD);
                if (moduleTable.getRowCount() == 0) {
                    removeMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
                    removeMouseListener(editModuleButton, ICON_ENUM.EDIT);
                }
            }
        });

        editModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                List<String> optionalList = getOptionalList();
                optionalList.add(String.valueOf(defaultTableModel.getValueAt(rowNum, 0)));
                JBPopupFactory.getInstance().createPopupChooserBuilder(optionalList).setTitle(COMMON.SELECT_MODULE).setMovable(true)
                        .setItemChosenCallback(s -> defaultTableModel.setValueAt(s, rowNum, 0)).createPopup().showUnderneathOf(editModuleButton);
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        controllerTextField.setText(commonConfig.getControllerFolderName());
        feignTextField.setText(commonConfig.getFeignFolderName());
        defaultTableModel.getDataVector().clear();
        List<String> selectModuleList = commonConfig.getModuleNameList();
        addMouseListener(addModuleButton, ICON_ENUM.ADD);
        if (CollectionUtil.isEmpty(selectModuleList)) {
            removeMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
            removeMouseListener(editModuleButton, ICON_ENUM.EDIT);
        } else {
            commonConfig.getModuleNameList().forEach(value -> defaultTableModel.addRow(new String[]{value}));
            addMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
            addMouseListener(editModuleButton, ICON_ENUM.EDIT);
            List<String> optionalList = getOptionalList();
            if (CollectionUtil.isEmpty(optionalList)) {
                removeMouseListener(addModuleButton, ICON_ENUM.ADD);
            }
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

    public List<String> getModuleNameList() {
        List<String> gatewayModuleNameList = new ArrayList<>();
        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            gatewayModuleNameList.add(StringUtil.toString(defaultTableModel.getValueAt(i, 0)));
        }
        return gatewayModuleNameList;
    }

    private List<String> getOptionalList() {
        List<String> list = totalSelectList.stream().sorted().collect(Collectors.toList());
        list.removeAll(getModuleNameList());
        return list;
    }

    private void addCallback(String value){
        defaultTableModel.addRow(new String[]{value});
        addMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
        addMouseListener(editModuleButton, ICON_ENUM.EDIT);
        if (CollectionUtil.isEmpty(getOptionalList())) {
            removeMouseListener(addModuleButton, ICON_ENUM.ADD);
        }
    }
}
