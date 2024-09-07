package pers.zlf.plugin.util;


import pers.zlf.plugin.constant.Common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MessageDigest加密工具
 *
 * @author zhanglinfeng
 * @date create in 2023/6/2 16:21
 */
public class MessageDigestUtil {

    public static String encode(String type, String inStr) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(type);
            byte[] byteArray = inStr.getBytes(StandardCharsets.UTF_8);
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = md5Byte & 255;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return Common.BLANK_STRING;
    }
}
