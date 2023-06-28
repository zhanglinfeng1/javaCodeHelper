package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
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
 * @author zhanglinfeng
 * @date create in 2023/6/15 15:16
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
    private final DefaultTableModel gitEmailTableModel;

    public CodeStatisticsDialog() {
        //文具类型
        defaultTableModel = new DefaultTableModel(null, new String[]{"参与统计的文件类型"});
        initTable(defaultTableModel, fileTypeTable, addFileTypeButton, deleteFileTypeButton);
        //git账号
        gitEmailTableModel = new DefaultTableModel(null, new String[]{"参与统计贡献率的git邮箱(默认当前邮箱)"});
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
        addMouseListener(addFileTypeButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(fileTypeList)) {
            removeMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        } else {
            fileTypeList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
            addMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        }
        addMouseListener(addGitEmailButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(gitEmailList)) {
            removeMouseListener(deleteGitEmailButton, IconEnum.REMOVE);
        } else {
            gitEmailList.forEach(value -> gitEmailTableModel.addRow(new String[]{value}));
            addMouseListener(deleteGitEmailButton, IconEnum.REMOVE);
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
            tableModel.addRow(new String[]{Common.BLANK_STRING});
            addMouseListener(deleteButton, IconEnum.REMOVE);
        });
        deleteButton.addActionListener(e -> Equals.of(jbTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            tableModel.removeRow(rowNum);
            if (jbTable.getRowCount() == 0) {
                removeMouseListener(deleteButton, IconEnum.REMOVE);
            }
        }));
    }
}
