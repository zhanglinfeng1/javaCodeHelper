package pers.zlf.plugin.util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 文件读取工具类
 *
 * @author zhanglinfeng
 * @date create in 2025/03/24 18:14
 */
public class FileUtil {

    /**
     * 根据文件路径读取byte[] 数组
     *
     * @param filePath 文件路径
     * @return byte[]
     * @throws IOException 异常
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length())) {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }
                return bos.toByteArray();
            }
        }
    }

    /**
     * 根据文件路径转成string
     *
     * @param filePath 文件路径
     * @return String
     * @throws IOException 异常
     */
    public static String toBaiduOcrString(String filePath) throws IOException {
        byte[] imgData = FileUtil.readFileByBytes(filePath);
        String imgStr = Base64Util.encode(imgData);
        return URLEncoder.encode(imgStr, StandardCharsets.UTF_8);
    }

    /**
     * 按目标宽度，等比压缩图片
     *
     * @param filePath    文件路径
     * @param targetWidth 目标宽度
     * @return Image
     * @throws IOException 异常
     */
    public static Image compressPicByWidth(String filePath, int targetWidth) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(filePath));
        return compressPicByWidth(bufferedImage, targetWidth);
    }

    /**
     * 按目标宽度，等比压缩图片
     *
     * @param bufferedImage BufferedImage
     * @param targetWidth   目标宽度
     * @return Image
     */
    public static Image compressPicByWidth(BufferedImage bufferedImage, int targetWidth) {
        int targetHeight = new BigDecimal(targetWidth).multiply(new BigDecimal(bufferedImage.getHeight())).divide(new BigDecimal(bufferedImage.getWidth()), 0, RoundingMode.HALF_UP).intValue();
        return bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }

    /**
     * Base64转图片
     *
     * @param content Base64
     * @return BufferedImage
     * @throws IOException 异常
     */
    public static BufferedImage base64ToPic(String content) throws IOException {
        byte[] contentBytes = Base64.getDecoder().decode(content);
        int length = contentBytes.length;
        for (int i = 0; i < length; ++i) {
            if (contentBytes[i] < 0) {
                contentBytes[i] += (byte) 256;
            }
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);
        return ImageIO.read(inputStream);
    }

    /**
     * 图片转Base64
     *
     * @param filePath 文件路径
     * @return String
     */
    public static String picToBase64(String filePath) throws IOException {
        File image = new File(filePath);
        FileInputStream inputStream = new FileInputStream(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
