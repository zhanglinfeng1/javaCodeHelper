package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.FileUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhanglinfeng
 * @date create in 2025/5/28 8:27
 */
public class PicToBase64Dialog {
    private JButton downButton;
    private JButton upButton;
    private JButton downloadButton;
    private JTextField fileSuffixTextField;
    private JTextArea downTextArea;
    private JLabel picLabel;
    private JPanel contentPanel;
    private static BufferedImage picImage;
    private static String uploadFilePath;

    public PicToBase64Dialog() {
        //上传
        picLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String filePath = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
                if (StringUtil.isEmpty(filePath)) {
                    return;
                }
                try {
                    Image pic = FileUtil.compressPic(filePath, picLabel.getWidth(), picLabel.getHeight());
                    picLabel.setIcon(new ImageIcon(pic));
                    uploadFilePath = filePath;
                    picImage = null;
                } catch (Exception ex) {
                    Message.notifyError(Message.UPLOAD_FAILED + ex.getMessage());
                }
            }
        });

        //转Base64
        downButton.addActionListener(e -> {
            if (StringUtil.isEmpty(uploadFilePath)) {
                Message.notifyError(Message.UPLOAD_FIRST);
                return;
            }
            try {
                downTextArea.setText(FileUtil.picToBase64(uploadFilePath));
            } catch (Exception ex) {
                Message.notifyError(Message.PIC_TO_BASE64_FAILED + ex.getMessage());
            }
        });

        //转图片
        upButton.addActionListener(e -> {
            String content = downTextArea.getText();
            if (StringUtil.isEmpty(content)) {
                return;
            }
            try {
                picImage = FileUtil.base64ToPic(content);
                picLabel.setIcon(new ImageIcon(FileUtil.compressPic(picImage, picLabel.getWidth(), picLabel.getHeight())));
                uploadFilePath = null;
            } catch (Exception ex) {
                picImage = null;
                Message.notifyError(Message.BASE64_TO_PIC_FAILED + ex.getMessage());
            }
        });

        //下载
        downloadButton.addActionListener(e -> {
            if (picImage == null) {
                Message.notifyError(Message.BASE64_TO_PIC_FIRST);
                return;
            }
            String fileSuffix = fileSuffixTextField.getText();
            if (StringUtil.isEmpty(fileSuffix)) {
                Message.notifyError(Message.FILE_SUFFIX_NOT_NULL);
                return;
            }
            String path = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
            if (StringUtil.isEmpty(path)) {
                return;
            }
            String fileName = path + File.separator + UUID.randomUUID() + Common.DOT + fileSuffix;
            try {
                ImageIO.write(picImage, fileSuffix, new File(fileName));
            } catch (IOException ex) {
                Message.notifyError(Message.DOWNLOAD_FAILED + ex.getMessage());
            }
        });

    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
