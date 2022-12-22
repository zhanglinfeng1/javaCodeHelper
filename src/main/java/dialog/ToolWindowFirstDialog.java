package dialog;

import com.intellij.ui.JBColor;
import constant.COMMON;
import util.StringUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/8 10:33
 */
public class ToolWindowFirstDialog extends JDialog {
    private JPanel contentPane;
    private JButton nextButton;
    private JTextArea textArea;
    private JTextField authorField;
    private JTextField fullPathField;
    private JTextField packagePathField;
    private JComboBox<String> dataBaseType;

    public ToolWindowFirstDialog() {
        fullPathField.setForeground(JBColor.GRAY);
        fullPathField.setText(COMMON.FULL_PATH_INPUT_PLACEHOLDER);
        packagePathField.setForeground(JBColor.GRAY);
        packagePathField.setText(COMMON.PACKAGR_PATH_INPUT_PLACEHOLDER);
        setContentPane(contentPane);
        setModal(true);

        fullPathField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON.FULL_PATH_INPUT_PLACEHOLDER.equals(fullPathField.getText())) {
                    fullPathField.setText(COMMON.BLANK_STRING);
                    fullPathField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(fullPathField.getText())) {
                    fullPathField.setForeground(JBColor.GRAY);
                    fullPathField.setText(COMMON.FULL_PATH_INPUT_PLACEHOLDER);
                }
            }
        });
        packagePathField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (COMMON.PACKAGR_PATH_INPUT_PLACEHOLDER.equals(packagePathField.getText())) {
                    packagePathField.setText(COMMON.BLANK_STRING);
                    packagePathField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtil.isEmpty(packagePathField.getText())) {
                    packagePathField.setForeground(JBColor.GRAY);
                    packagePathField.setText(COMMON.PACKAGR_PATH_INPUT_PLACEHOLDER);
                }
            }
        });
    }

    public JPanel getContent() {
        return this.contentPane;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public String getFullPath() throws Exception {
        String fullPath = fullPathField.getText();
        if (COMMON.FULL_PATH_INPUT_PLACEHOLDER.equals(fullPath) || StringUtil.isEmpty(fullPath)) {
            throw new Exception("Full path is not null");
        }
        return fullPathField.getText();
    }

    public String getPackagePathField() throws Exception {
        String packagePath = packagePathField.getText();
        if (COMMON.PACKAGR_PATH_INPUT_PLACEHOLDER.equals(packagePath) || StringUtil.isEmpty(packagePath)) {
            throw new Exception("Package is not null");
        }
        return packagePath;
    }

    public String getSqlStr() throws Exception {
        String sqlStr = textArea.getText();
        if (StringUtil.isEmpty(sqlStr)) {
            throw new Exception("Sql is not null");
        }
        return sqlStr;
    }

    public String getDataBaseType() {
        return Objects.requireNonNull(dataBaseType.getSelectedItem()).toString();
    }
}
