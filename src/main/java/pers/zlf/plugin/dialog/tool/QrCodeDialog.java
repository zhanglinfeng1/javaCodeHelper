package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.QRCodeUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhanglinfeng
 * @date create in 2024/3/13 9:38
 */
public class QrCodeDialog {
    private JTextArea upTextArea;
    private JButton downButton;
    private JButton upButton;
    private JButton downloadButton;
    private JPanel contentPanel;
    private TextFieldWithBrowseButton logoTextField;
    private JLabel qrCodeLabel;
    private static BufferedImage qrCodeImage;
    private static String uploadFilePath;

    public QrCodeDialog() {
        logoTextField.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(true, false, false, false, false, false)));

        //生成
        downButton.addActionListener(e -> {
            String content = upTextArea.getText();
            if (StringUtil.isEmpty(content)) {
                return;
            }
            try {
                qrCodeImage = QRCodeUtil.generateQRCode(content, logoTextField.getText());
                qrCodeLabel.setIcon(new ImageIcon(qrCodeImage));
            } catch (Exception ex) {
                Message.notifyError(Message.GENERATE_QR_CODE_FAILED + ex.getMessage());
            }
        });

        //下载
        downloadButton.addActionListener(e -> {
            if (qrCodeImage == null) {
                Message.notifyError(Message.GENERATE_QR_CODE_FIRST);
                return;
            }
            String path = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
            if (StringUtil.isEmpty(path)) {
                return;
            }
            String fileName = path + File.separator + UUID.randomUUID() + FileType.JPG_FILE;
            try {
                ImageIO.write(qrCodeImage, FileType.JPG_FILE.substring(1), new File(fileName));
                uploadFilePath = null;
                Message.notifyInfo(Message.DOWNLOAD_QR_CODE_SUCCESS + fileName);
            } catch (IOException ex) {
                Message.notifyError(Message.DOWNLOAD_QR_CODE_FAILED + ex.getMessage());
            }
        });

        //上传
        qrCodeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                uploadFilePath = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
                if (StringUtil.isNotEmpty(uploadFilePath)) {
                    try {
                        qrCodeLabel.setIcon(new ImageIcon(QRCodeUtil.compress(uploadFilePath)));
                        qrCodeImage = null;
                    } catch (Exception ex) {
                        Message.notifyError(Message.UPLOAD_QR_CODE_FAILED + ex.getMessage());
                    }
                }
            }
        });

        //解析
        upButton.addActionListener(e -> {
            if (StringUtil.isEmpty(uploadFilePath)) {
                Message.notifyError(Message.UPLOAD_QR_CODE_FIRST);
                return;
            }
            try {
                upTextArea.setText(QRCodeUtil.analysisQRCode(uploadFilePath));
            } catch (Exception ex) {
                Message.notifyError(Message.ANALYSIS_QR_CODE_FAILED + ex.getMessage());
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
