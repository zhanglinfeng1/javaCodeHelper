package pers.zlf.plugin.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Consumer;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.constant.TYPE;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.ListenerUtil;
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
public class FastJumpConfigDialog {
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
        defaultTableModel = new DefaultTableModel(null, new String[]{COMMON.MODULE_NAME_TABLE_HEADER}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        moduleTable.setModel(defaultTableModel);
        moduleTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Consumer<String> addCallback = value -> {
            defaultTableModel.addRow(new String[]{value});
            ListenerUtil.addMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
            ListenerUtil.addMouseListener(editModuleButton, ICON_ENUM.EDIT);
            if (CollectionUtil.isEmpty(getOptionalList())) {
                ListenerUtil.removeMouseListener(addModuleButton, ICON_ENUM.ADD);
            }
        };
        addModuleButton.addActionListener(e -> Empty.of(getOptionalList()).isPresent(list -> showChooseWindow(list, addCallback)));

        deleteModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                defaultTableModel.removeRow(rowNum);
                ListenerUtil.addMouseListener(addModuleButton, ICON_ENUM.ADD);
                if (moduleTable.getRowCount() == 0) {
                    ListenerUtil.removeMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
                    ListenerUtil.removeMouseListener(editModuleButton, ICON_ENUM.EDIT);
                }
            }
        });

        editModuleButton.addActionListener(e -> {
            int rowNum = moduleTable.getSelectedRow();
            if (rowNum >= 0) {
                List<String> optionalList = getOptionalList();
                optionalList.add(String.valueOf(defaultTableModel.getValueAt(rowNum, 0)));
                showChooseWindow(optionalList, s -> defaultTableModel.setValueAt(s, rowNum, 0));
            }
        });
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        controllerTextField.setText(commonConfig.getControllerFolderName());
        feignTextField.setText(commonConfig.getFeignFolderName());
        defaultTableModel.getDataVector().clear();
        List<String> selectModuleList = commonConfig.getModuleNameList();
        ListenerUtil.addMouseListener(addModuleButton, ICON_ENUM.ADD);
        if (CollectionUtil.isEmpty(selectModuleList)) {
            ListenerUtil.removeMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
            ListenerUtil.removeMouseListener(editModuleButton, ICON_ENUM.EDIT);
        } else {
            commonConfig.getModuleNameList().forEach(value -> defaultTableModel.addRow(new String[]{value}));
            ListenerUtil.addMouseListener(deleteModuleButton, ICON_ENUM.REMOVE);
            ListenerUtil.addMouseListener(editModuleButton, ICON_ENUM.EDIT);
            List<String> optionalList = getOptionalList();
            if (CollectionUtil.isEmpty(optionalList)) {
                ListenerUtil.removeMouseListener(addModuleButton, ICON_ENUM.ADD);
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

    private void showChooseWindow(List<String> optionalList, Consumer<String> callback) {
        JBPopupFactory.getInstance().createPopupChooserBuilder(optionalList).setTitle(COMMON.SELECT_MODULE)
                .setMovable(true).setItemChosenCallback(callback).createPopup().showInCenterOf(panel);
    }

}
