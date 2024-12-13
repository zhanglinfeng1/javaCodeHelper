package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.SwingUtil;
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
public class CodeStatisticsDialog extends BaseDialog{
    private final String FILE_TYPE_TABLE_HEADER = "参与统计的文件后缀(例如：.java)";
    private final String GIT_EMAIL_TABLE_HEADER = "参与统计贡献率的git邮箱(默认当前邮箱)";
    private final DefaultTableModel gitEmailTableModel;
    private final DefaultTableModel defaultTableModel;
    /** ui组件 */
    private JPanel panel;
    private JBTable fileTypeTable;
    private JButton deleteFileTypeButton;
    private JButton addFileTypeButton;
    private JBTable gitEmailTable;
    private JButton addGitEmailButton;
    private JButton deleteGitEmailButton;
    private JCheckBox countEmptyLineCheckBox;
    private JCheckBox countCommentCheckBox;
    private JCheckBox countKeywordCheckBox;


    public CodeStatisticsDialog() {
        //文件类型
        defaultTableModel = new DefaultTableModel(null, new String[]{FILE_TYPE_TABLE_HEADER});
        initTable(defaultTableModel, fileTypeTable, addFileTypeButton, deleteFileTypeButton);
        //git账号
        gitEmailTableModel = new DefaultTableModel(null, new String[]{GIT_EMAIL_TABLE_HEADER});
        initTable(gitEmailTableModel, gitEmailTable, addGitEmailButton, deleteGitEmailButton);
        //初始化按钮背景色
        SwingUtil.initButtonBackground(addFileTypeButton, deleteFileTypeButton, addGitEmailButton, deleteGitEmailButton);
    }

    @Override
    public void reset() {
        CodeStatisticsConfig config = ConfigFactory.getInstance().getCodeStatisticsConfig();
        List<String> fileTypeList = config.getFileTypeList();
        List<String> gitEmailList = config.getGitEmailList();
        countEmptyLineCheckBox.setSelected(config.isCountEmptyLine());
        countCommentCheckBox.setSelected(config.isCountComment());
        countKeywordCheckBox.setSelected(config.isCountKeyword());

        defaultTableModel.getDataVector().clear();
        gitEmailTableModel.getDataVector().clear();
        SwingUtil.addMouseListener(addFileTypeButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(fileTypeList)) {
            SwingUtil.removeMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        } else {
            fileTypeList.forEach(value -> defaultTableModel.addRow(new String[]{value}));
            SwingUtil.addMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        }
        SwingUtil.addMouseListener(addGitEmailButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(gitEmailList)) {
            SwingUtil.removeMouseListener(deleteGitEmailButton, IconEnum.REMOVE);
        } else {
            gitEmailList.forEach(value -> gitEmailTableModel.addRow(new String[]{value}));
            SwingUtil.addMouseListener(deleteGitEmailButton, IconEnum.REMOVE);
        }
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    public boolean isCountComment() {
        return countCommentCheckBox.isSelected();
    }

    public boolean isCountEmptyLine() {
        return countEmptyLineCheckBox.isSelected();
    }

    public boolean isCountKeyword() {
        return countKeywordCheckBox.isSelected();
    }

    public List<String> getFileTypeList() {
        return SwingUtil.getTableContentList(defaultTableModel, 0);
    }

    public List<String> getGitEmailList() {
        return SwingUtil.getTableContentList(gitEmailTableModel, 0);
    }

    private void initTable(DefaultTableModel tableModel, JBTable jbTable, JButton addButton, JButton deleteButton) {
        jbTable.setModel(tableModel);
        jbTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jbTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
        addButton.addActionListener(e -> {
            tableModel.addRow(new String[]{Common.BLANK_STRING});
            SwingUtil.addMouseListener(deleteButton, IconEnum.REMOVE);
        });
        deleteButton.addActionListener(e -> Equals.of(jbTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            tableModel.removeRow(rowNum);
            if (jbTable.getRowCount() == 0) {
                SwingUtil.removeMouseListener(deleteButton, IconEnum.REMOVE);
            }
        }));
    }
}
