package pers.zlf.plugin.dialog;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.IconEnum;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.factory.TemplateFactory;
import pers.zlf.plugin.pojo.ColumnInfo;
import pers.zlf.plugin.pojo.TableInfo;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.SwingUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/8 10:33
 */
public class GenerateCodeDialog {
    private final String FULL_PATH_INPUT_PLACEHOLDER = "C:\\workspace\\javaCodeHelper\\src\\main\\java\\pers\\zlf\\plugin";
    private final String PACKAGR_PATH_INPUT_PLACEHOLDER = "pers.zlf.plugin";
    private final String[] SELECT_OPTIONS = {"=", ">", ">=", "<", "<=", "in", "not in", "like", "not like"};
    private final String[] DATA_TYPE_OPTIONS = {"String", "boolean", "Boolean", "int", "Integer", "double", "Double", "BigDecimal", "Date", "Time", "Timestamp", "LocalDateTime"};
    private final DefaultTableModel columnTableModel = new DefaultTableModel(null, new String[]{"字段名", "别名", "类型", "java数据类型", "备注"});
    private final DefaultTableModel queryTableModel = new DefaultTableModel(null, new String[]{"字段名", "别名", "查询方式"});
    private final Map<String, String> selectTemplateFileMap = new HashMap<>();
    private final Project project;
    private TableInfo tableInfo;
    private String[] columnArr;
    /** ui组件 */
    private JPanel contentPanel;
    /** 第一面板 */
    private JPanel firstPanel;
    private JBTable columnTable;
    private JButton nextButton;
    /** 第二面板 */
    private JPanel secondPanel;
    private TextFieldWithBrowseButton fullPathField;
    private JTextField packagePathField;
    private JButton backButton;
    private JButton submitButton;
    private JButton addButton;
    private JButton deleteButton;
    private JBTable queryTable;
    private JTextField tableNamePrefixField;
    private JComboBox<String> templateComboBox;
    private JPanel templateFilePanel;

    public GenerateCodeDialog(Project project, DasTable dasTable) {
        this.project = project;
        //解析表结构
        tableInfo = new TableInfo(dasTable.getName(), dasTable.getComment());
        columnTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        columnTable.setModel(columnTableModel);
        for (DasColumn column : DasUtil.getColumns(dasTable)) {
            String sqlColumn = column.getName();
            String dataType = column.getDasType().toDataType().typeName;
            columnTableModel.addRow(new String[]{sqlColumn, StringUtil.toHumpStyle(sqlColumn), dataType, DATA_TYPE_OPTIONS[0], Empty.of(column.getComment()).orElse(Common.BLANK_STRING)});
        }
        JTextField textField = new JTextField();
        textField.setEnabled(false);
        JTextField textField2 = new JTextField();
        textField2.setEnabled(false);
        columnTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(textField));
        columnTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
        columnTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(textField2));
        columnTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(DATA_TYPE_OPTIONS)));
        columnTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()));
        //文本框初始化
        fullPathField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)));
        SwingUtil.addFocusListener(fullPathField.getTextField(), FULL_PATH_INPUT_PLACEHOLDER);
        SwingUtil.addFocusListener(packagePathField, PACKAGR_PATH_INPUT_PLACEHOLDER);
        //表格初始化
        queryTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queryTable.setModel(queryTableModel);
        queryTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                String value = StringUtil.toHumpStyle(queryTableModel.getValueAt(e.getFirstRow(), 0).toString());
                queryTableModel.setValueAt(value, e.getFirstRow(), 1);
            }
        });
        ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap().keySet().forEach(templateComboBox::addItem);
        templateComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                resetTemplateFilePanel();
                templateComboBox.revalidate();
            }
        });
        //初始化按钮
        initButtonListener();
        showFirstPanel();
    }

    private void showFirstPanel() {
        firstPanel.setVisible(true);
        secondPanel.setVisible(false);
    }

    private void showSecondPanel() {
        firstPanel.setVisible(false);
        secondPanel.setVisible(true);
        int rowCount = columnTableModel.getRowCount();
        if (rowCount > 0) {
            List<ColumnInfo> columnList = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                int row = i;
                Function<Integer, String> function = column -> StringUtil.toString(columnTableModel.getValueAt(row, column));
                ColumnInfo columnInfo = new ColumnInfo(function.apply(0), function.apply(1), function.apply(2), function.apply(3), function.apply(4));
                columnList.add(columnInfo);
                if (Common.ID.equals(columnInfo.getColumnName())) {
                    tableInfo.setIdColumnType(columnInfo.getColumnType());
                }
            }
            tableInfo.setColumnList(columnList);
            columnArr = columnList.stream().map(ColumnInfo::getSqlColumnName).toArray(String[]::new);
        }
        resetTemplateFilePanel();
    }

    private void initButtonListener() {
        //下一步
        nextButton.addActionListener(e -> showSecondPanel());
        //上一步
        backButton.addActionListener(e -> showFirstPanel());
        //初始化按钮背景色
        SwingUtil.initButtonBackground(addButton, deleteButton);
        //添加
        addButton.addActionListener(e -> {
            SwingUtil.addMouseListener(deleteButton, IconEnum.REMOVE);
            queryTableModel.addRow(new String[]{columnArr[0], StringUtil.toHumpStyle(columnArr[0]), SELECT_OPTIONS[0]});
            queryTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(columnArr)));
            queryTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
            queryTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(SELECT_OPTIONS)));
        });
        //删除
        deleteButton.addActionListener(e -> Equals.of(queryTable.getSelectedRow()).and(rowNum -> rowNum >= 0).ifTrue(rowNum -> {
            //TODO 点击下拉后未选择，再点删除，有BUG
            queryTableModel.removeRow(rowNum);
            if (queryTable.getRowCount() == 0) {
                SwingUtil.removeMouseListener(deleteButton, IconEnum.REMOVE);
            }
        }));
        //生成代码
        submitButton.addActionListener(e -> {
            String author = ConfigFactory.getInstance().getTemplateConfig().getAuthor();
            if (StringUtil.isEmpty(author)) {
                Message.notifyError(project, Message.PLEASE_CONFIGURE_AUTHOR_FIRST, Message.TO_CONFIGURE, Common.APPLICATION_CONFIGURABLE_TEMPLATE_ID);
                return;
            }
            try {
                String fullPath = Equals.of(fullPathField.getText()).and(FULL_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                        .ifTrueThrow(() -> new Exception(Message.FULL_PATH_NOT_NULL));
                String packagePath = Equals.of(packagePathField.getText()).and(PACKAGR_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                        .ifTrueThrow(() -> new Exception(Message.PACKAGE_PATH_NOT_NULL));
                tableInfo.setAuthor(author);
                tableInfo.setPackagePath(packagePath);
                tableInfo.setQueryColumnList(getQueryColumnList());
                //生成文件
                String selectedTemplate = templateComboBox.getSelectedItem().toString();
                TemplateFactory.getInstance().create(selectedTemplate, getSelectedTemplateFile(), fullPath, tableInfo);
                Message.notifyInfo(project, Message.GENERATE_CODE_SUCCESS);
                SwingUtil.closeToolWindowSelectedContent(project, Common.JAVA_CODE_HELPER);
            } catch (Exception ex) {
                Message.notifyError(project, Message.GENERATE_CODE_FAILED + ex.getMessage());
            }
        });
        SwingUtil.addMouseListener(addButton, IconEnum.ADD);
        SwingUtil.removeMouseListener(deleteButton, IconEnum.REMOVE);

        Runnable runnable = () -> {
            String text = fullPathField.getText();
            if (FULL_PATH_INPUT_PLACEHOLDER.equals(text)) {
                fullPathField.getTextField().setForeground(JBColor.GRAY);
            } else {
                fullPathField.getTextField().setForeground(JBColor.BLACK);
            }
            String packagePath = String.format(Common.SRC_MAIN_JAVA, File.separator, File.separator) + File.separator;
            int index = text.indexOf(packagePath);
            if (index != -1) {
                packagePathField.setText(text.substring(index + packagePath.length()).replace(File.separator, Common.DOT));
                packagePathField.setForeground(JBColor.BLACK);
                return;
            }
            packagePath = File.separator + Common.SRC + File.separator;
            index = text.indexOf(packagePath);
            if (index != -1) {
                packagePathField.setText(text.substring(index + packagePath.length()).replace(File.separator, Common.DOT));
                packagePathField.setForeground(JBColor.BLACK);
            }
        };
        fullPathField.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                runnable.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                runnable.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                runnable.run();
            }
        });
    }

    private List<ColumnInfo> getQueryColumnList() {
        List<ColumnInfo> queryColumnList = new ArrayList<>();
        int rowCount = queryTableModel.getRowCount();
        if (rowCount > 0) {
            Map<String, ColumnInfo> columnInfoMap = tableInfo.getColumnList().stream().collect(Collectors.toMap(ColumnInfo::getSqlColumnName, Function.identity()));
            for (int i = 0; i < rowCount; i++) {
                String columnName = StringUtil.toString(queryTableModel.getValueAt(i, 0));
                ColumnInfo queryColumnInfo = new ColumnInfo(columnName, queryTableModel.getValueAt(i, 1), queryTableModel.getValueAt(i, 2));
                Optional.ofNullable(columnInfoMap.get(columnName)).ifPresent(t -> {
                    queryColumnInfo.setSqlColumnType(t.getSqlColumnType());
                    queryColumnInfo.setColumnType(t.getColumnType());
                });
                queryColumnList.add(queryColumnInfo);
            }
        }
        return queryColumnList;
    }

    private void resetTemplateFilePanel() {
        String tableNamePrefix = tableNamePrefixField.getText();
        tableInfo.dealTableName(tableNamePrefix);
        String templateName = templateComboBox.getSelectedItem().toString();
        Map<String, String> templateFileMap = ConfigFactory.getInstance().getTemplateConfig().getTotalTemplateMap().get(templateName);
        List<String> templateFileNameList = templateFileMap.keySet().stream().toList();
        int length = templateFileNameList.size();
        int rowCount = length / 3;
        templateFilePanel.removeAll();
        templateFilePanel.setLayout(new GridLayout(rowCount + 1, 3));
        selectTemplateFileMap.clear();
        for (int i = 0; i < length; i++) {
            String templateFileName = templateFileNameList.get(i);
            String fileName = getFileName(templateFileName);
            templateFilePanel.add(new JCheckBox(fileName, true));
            selectTemplateFileMap.put(fileName, templateFileName);
        }
    }

    private String getFileName(String templateName) {
        String fileName = templateName.replaceAll(FileType.FREEMARKER_FILE, Common.BLANK_STRING);
        if ((Common.MODEL + FileType.JAVA_FILE).equals(fileName) || Common.MODEL.equals(fileName)) {
            fileName = tableInfo.getTableName();
        } else {
            fileName = tableInfo.getTableName() + fileName;
        }
        if (!fileName.contains(Common.DOT)) {
            fileName = fileName + FileType.JAVA_FILE;
        }
        return StringUtil.toUpperCaseFirst(fileName);
    }

    private Map<String, String> getSelectedTemplateFile() {
        Map<String, String> map = new HashMap<>();
        for (Component component : templateFilePanel.getComponents()) {
            if (component instanceof JCheckBox checkBox && checkBox.isSelected()) {
                String fileName = checkBox.getText();
                map.put(fileName, selectTemplateFileMap.get(fileName));
            }
        }
        return map;
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}