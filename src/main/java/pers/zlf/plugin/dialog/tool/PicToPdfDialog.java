package pers.zlf.plugin.dialog.tool;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.FileUtil;
import pers.zlf.plugin.util.StringUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * @author zhanglinfeng
 * @date create in 2025/6/9 23:38
 */
public class PicToPdfDialog {
    private JPanel contentPanel;
    private JButton downButton;
    private JLabel picLabel;
    private static String uploadFilePath;

    public PicToPdfDialog() {
        //上传
        picLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String filePath = Optional.ofNullable(FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(), null, null)).map(VirtualFile::getPath).orElse(null);
                if (StringUtil.isEmpty(filePath)) {
                    return;
                }
                try {
                    Image logo = FileUtil.compressPicByWidth(filePath, picLabel.getHeight());
                    picLabel.setIcon(new ImageIcon(logo));
                    uploadFilePath = filePath;
                } catch (Exception ex) {
                    Message.notifyError(Message.UPLOAD_FAILED + ex.getMessage());
                }
            }
        });
        //转pdf
        downButton.addActionListener(e -> {
            if (StringUtil.isEmpty(uploadFilePath)) {
                Message.notifyError(Message.UPLOAD_FIRST);
                return;
            }
            try {
                String filePath = uploadFilePath.substring(0, uploadFilePath.lastIndexOf(Common.DOT)) + FileType.PDF_FILE;
                FileUtil.picToPdf(uploadFilePath, filePath);
                Message.notifyInfo(Message.PIC_TO_PDF_SUCCESS + filePath);
            } catch (Exception ex) {
                Message.notifyError(Message.PIC_TO_PDF_FAILED + ex.getMessage());
            }
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

}
