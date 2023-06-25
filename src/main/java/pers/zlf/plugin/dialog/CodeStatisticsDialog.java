package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON_ENUM;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.CommonConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.lambda.Equals;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/6/15 15:16
 */
public class CodeStatisticsDialog extends BaseDialog {
    private JPanel panel;
    private JCheckBox commentCheckBox;
    private JBTable fileTypeTable;
    private JButton deleteFileTypeButton;
    private JButton addFileTypeButton;
    private JBTable gitEmailTable;
    private JButton addGitEmailButton;
    private JButton deleteGitEmailButton;
    private JCheckBox realTimeStatisticsCheckBox;
    private DefaultTableModel gitEmailTableModel;

    public CodeStatisticsDialog() {
        //文具类型
        defaultTableModel = new DefaultTableModel(null, new String[]{COMMON.BLANK_STRING});
        initTable(defaultTableModel, fileTypeTable, addFileTypeButton, deleteFileTypeButton);
        //git账号
        gitEmailTableModel = new DefaultTableModel(null, new String[]{COMMON.BLANK_STRING});
        initTable(gitEmailTableModel, gitEmailTable, addGitEmailButton, deleteGitEmailButton);
    }

    public void reset() {
        CommonConfig commonConfig = ConfigFactory.getInstance().getCommonConfig();
        List<String> fileTypeList = commonConfig.getFileTypeList();
        List<String> gitEmailList = commonConfig.getGitEmailList();
        commentCheckBox.setSelected(commonConfig.isCountComment());
        realTimeStatisticsCheckBox.setSelected(commonConfig.isRealTimeStatistics());

        defaultTableModel.getDataVector().clear();
        gitEmailTableModel.getDataVector().clear();
        addMouseListener(addFileTypeButton, ICON_ENUM.ADD);
        if (CollectionUtil.isEmpty(fileTypeList)) {
            removeMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
        } else {
            fileTypeList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
            addMouseListener(deleteFileTypeButton, ICON_ENUM.REMOVE);
        }
        addMouseListener(addGitEmailButton, ICON_ENUM.ADD);
        if (CollectionUtil.isEmpty(gitEmailList)) {
            removeMouseListener(deleteGitEmailButton, ICON_ENUM.REMOVE);
        } else {
            gitEmailList.forEach(value -> gitEmailTableModel.addRow(new String[]{value}));
            addMouseListener(deleteGitEmailButton, ICON_ENUM.REMOVE);
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public boolean isCountComment() {
        return commentCheckBox.isSelected();
    }

    public boolean isRealTimeStatistics(){
        return realTimeStatisticsCheckBox.isSelected();
    }

    public List<String> getFileTypeList() {
        return getTableContentList(defaultTableModel, 0);
    }

    public List<String> getGitEmailList() {
        return getTableContentList(gitEmailTableModel, 0);
    }

    private void initTable(DefaultTableModel tableModel, JBTable jbTable, JButton addButton, JButton deleteButton) {
        jbTable.setModel(tableModel);
        jbTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jbTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
        addButton.addActionListener(e -> {
            tableModel.addRow(new String[]{COMMON.BLANK_STRING});
            addMouseListener(deleteButton, ICON_ENUM.REMOVE);
        });
        deleteButton.addActionListener(e -> Equals.of(jbTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            tableModel.removeRow(rowNum);
            if (jbTable.getRowCount() == 0) {
                removeMouseListener(deleteButton, ICON_ENUM.REMOVE);
            }
        }));
    }
}
