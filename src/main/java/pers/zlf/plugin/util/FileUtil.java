package pers.zlf.plugin.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
}
