package pers.zlf.plugin.dialog;

import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CodeStatisticsConfig;
import pers.zlf.plugin.util.CollectionUtil;
import pers.zlf.plugin.util.StringUtil;
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
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2023/6/15 15:16
 */
public class CodeStatisticsDialog extends BaseDialog{
    private final String FILE_TYPE_TABLE_HEADER = "参与统计的文件后缀(例如：.java)";
    private final String GIT_EMAIL_TABLE_HEADER = "参与统计贡献率的git邮箱(默认当前邮箱)";
    private final DefaultTableModel GIT_EMAIL_TABLE_MODEL;
    private final DefaultTableModel DEFAULT_TABLE_MODEL;
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
    private JCheckBox countDateCheckBox;
    private JTextField countDateTextField;

    public CodeStatisticsDialog() {
        //文件类型
        DEFAULT_TABLE_MODEL = new DefaultTableModel(null, new String[]{FILE_TYPE_TABLE_HEADER});
        initTable(DEFAULT_TABLE_MODEL, fileTypeTable, addFileTypeButton, deleteFileTypeButton);
        //git账号
        GIT_EMAIL_TABLE_MODEL = new DefaultTableModel(null, new String[]{GIT_EMAIL_TABLE_HEADER});
        initTable(GIT_EMAIL_TABLE_MODEL, gitEmailTable, addGitEmailButton, deleteGitEmailButton);
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
        String countDate = config.getCountDate();
        if (StringUtil.isEmpty(countDate)) {
            countDateCheckBox.setSelected(false);
            countDateTextField.setText(null);
        } else {
            countDateCheckBox.setSelected(true);
            countDateTextField.setText(countDate);
        }

        DEFAULT_TABLE_MODEL.getDataVector().clear();
        GIT_EMAIL_TABLE_MODEL.getDataVector().clear();
        SwingUtil.addMouseListener(addFileTypeButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(fileTypeList)) {
            SwingUtil.removeMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        } else {
            fileTypeList.forEach(value -> DEFAULT_TABLE_MODEL.addRow(new String[]{value}));
            SwingUtil.addMouseListener(deleteFileTypeButton, IconEnum.REMOVE);
        }
        SwingUtil.addMouseListener(addGitEmailButton, IconEnum.ADD);
        if (CollectionUtil.isEmpty(gitEmailList)) {
            SwingUtil.removeMouseListener(deleteGitEmailButton, IconEnum.REMOVE);
        } else {
            gitEmailList.forEach(value -> GIT_EMAIL_TABLE_MODEL.addRow(new String[]{value}));
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
        return SwingUtil.getTableContentList(DEFAULT_TABLE_MODEL, 0);
    }

    public List<String> getGitEmailList() {
        return SwingUtil.getTableContentList(GIT_EMAIL_TABLE_MODEL, 0);
    }

    public String getCountDate() {
        if (!countDateCheckBox.isSelected()) {
            return Common.BLANK_STRING;
        } else {
            return Optional.ofNullable(countDateTextField.getText()).orElse(Common.BLANK_STRING);
        }
    }

    public void clearCountDate() {
        countDateTextField.setText(Common.BLANK_STRING);
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
