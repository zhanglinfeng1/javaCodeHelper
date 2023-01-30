package pers.zlf.plugin.dialog;

import com.intellij.ui.JBColor;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.util.StringUtil;
import pers.zlf.plugin.util.lambda.Empty;
import pers.zlf.plugin.util.lambda.Equals;

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
        return Equals.of(fullPathField.getText()).and(COMMON.FULL_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                .ifTrueThrow(() -> new Exception("Full path is not null"));
    }

    public String getPackagePathField() throws Exception {
        return Equals.of(packagePathField.getText()).and(COMMON.PACKAGR_PATH_INPUT_PLACEHOLDER::equals).or(StringUtil::isEmpty)
                .ifTrueThrow(() -> new Exception("Package is not null"));
    }

    public String getSqlStr() throws Exception {
        return Empty.of(textArea.getText()).ifEmptyThrow(() -> new Exception("Sql is not null"));
    }

    public String getDataBaseType() {
        return Objects.requireNonNull(dataBaseType.getSelectedItem()).toString();
    }
}
