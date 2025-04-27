package pers.zlf.plugin.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

/**
 * 二维码工具
 *
 * @author zhanglinfeng
 * @date create in 2024/3/13 15:44
 */
public class QRCodeUtil {
    /**
     * 二维码尺寸
     */
    private static final int QRCODE_SIZE = 300;
    /**
     * LOGO尺寸
     */
    private static final int LOGO_SIZE = 60;

    /**
     * 生成二维码
     *
     * @param content  文本
     * @param logoPath logo路径
     * @return BufferedImage
     * @throws Exception 异常
     */
    public static BufferedImage generateQRCode(String content, String logoPath) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = UIUtil.createImage(null, width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (StringUtil.isEmpty(logoPath)) {
            return image;
        }
        insertLogo(image, logoPath);
        return image;
    }

    /**
     * 解析二维码
     *
     * @param qrCodePath 二维码路径
     * @return 解析后的内容
     * @throws Exception 异常
     */
    public static String analysisQRCode(String qrCodePath) throws Exception {
        File file = new File(qrCodePath);
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 压缩图片
     *
     * @param logoPath 图片地址
     * @return Image
     * @throws Exception 异常
     */
    public static Image compress(String logoPath) throws Exception {
        Image logo = ImageIO.read(new File(logoPath));
        return logo.getScaledInstance(QRCODE_SIZE, QRCODE_SIZE, Image.SCALE_SMOOTH);
    }

    /**
     * 插入logo
     *
     * @param source   二维码BufferedImage
     * @param logoPath logo路径
     * @throws Exception 异常
     */
    private static void insertLogo(BufferedImage source, String logoPath) throws Exception {
        File file = new File(logoPath);
        if (!file.exists()) {
            return;
        }
        Image logo = ImageIO.read(new File(logoPath));
        logo = logo.getScaledInstance(LOGO_SIZE, LOGO_SIZE, Image.SCALE_SMOOTH);
        BufferedImage tag = UIUtil.createImage(null, LOGO_SIZE, LOGO_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(logo, 0, 0, null);
        g.dispose();
        var graph = source.createGraphics();
        int x = (QRCODE_SIZE - LOGO_SIZE) / 2;
        int y = (QRCODE_SIZE - LOGO_SIZE) / 2;
        graph.drawImage(logo, x, y, LOGO_SIZE, LOGO_SIZE, null);
        Shape shape = new RoundRectangle2D.Float(x, y, LOGO_SIZE, LOGO_SIZE, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

}
