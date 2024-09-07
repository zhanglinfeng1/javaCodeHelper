package pers.zlf.plugin.util;

import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Message;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/7 20:27
 */
public class AESUtil {
    private static final String AES_ECB = "AES/ECB/PKCS5Padding";
    private static final String AES_CBC = "AES/CBC/NoPadding";

    /**
     * AES_ECB加密
     *
     * @param text      待加密文本
     * @param secretKey 秘钥
     * @return String
     * @throws Exception 异常
     */
    public static String encrypt(String text, String secretKey) throws Exception {
        if (StringUtil.isEmpty(secretKey) || secretKey.length() != 16) {
            throw new Exception(Message.AES_SECRET_KEY_LENGTH_ERROR);
        }
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, Common.AES);
        Cipher cipher = Cipher.getInstance(AES_ECB);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] result = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * AES_CBC加密
     *
     * @param text      待加密文本
     * @param secretKey 秘钥
     * @param iv        偏移量，长度16
     * @return String
     * @throws Exception 异常
     */
    public static String encrypt(String text, String secretKey, String iv) throws Exception {
        if (StringUtil.isEmpty(secretKey) || secretKey.length() != 16) {
            throw new Exception(Message.AES_SECRET_KEY_LENGTH_ERROR);
        }
        if (StringUtil.isEmpty(iv) || iv.length() != 16) {
            throw new Exception(Message.AES_IV_LENGTH_ERROR);
        }
        Cipher cipher = Cipher.getInstance(AES_CBC);
        int blockSize = cipher.getBlockSize();
        byte[] dataBytes = text.getBytes();
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }
        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), Common.AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext));
    }

    /**
     * AES_ECB解密
     *
     * @param text      待解密文本
     * @param secretKey 秘钥
     * @return String
     * @throws Exception 异常
     */
    public static String decrypt(String text, String secretKey) throws Exception {
        if (StringUtil.isEmpty(secretKey) || secretKey.length() != 16) {
            throw new Exception(Message.AES_SECRET_KEY_LENGTH_ERROR);
        }
        byte[] raw = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, Common.AES);
        Cipher cipher = Cipher.getInstance(AES_ECB);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8).trim();
    }


    /**
     * AES_CBC解密
     *
     * @param text      待解密文本
     * @param secretKey 秘钥
     * @param iv        偏移量，长度16
     * @return String
     * @throws Exception 异常
     */
    public static String decrypt(String text, String secretKey, String iv) throws Exception {
        if (StringUtil.isEmpty(secretKey) || secretKey.length() != 16) {
            throw new Exception(Message.AES_SECRET_KEY_LENGTH_ERROR);
        }
        if (StringUtil.isEmpty(iv) || iv.length() != 16) {
            throw new Exception(Message.AES_IV_LENGTH_ERROR);
        }
        byte[] encrypted1 = Base64.getDecoder().decode(text);
        Cipher cipher = Cipher.getInstance(AES_CBC);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), Common.AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(encrypted1), StandardCharsets.UTF_8).trim();
    }

}
