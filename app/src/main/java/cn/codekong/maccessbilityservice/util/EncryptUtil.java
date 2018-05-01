package cn.codekong.maccessbilityservice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 * Created by shangzhenhong on 05/01/2018
 * Email: szh@codekong.cn
 */
public class EncryptUtil {

    /**
     * MD5加密
     * @param srcStr
     * @return
     */
    public static String md5Encrypt(String srcStr){
        String result;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(srcStr.getBytes());
            result = bytesToHexString(mdBytes);
        } catch (NoSuchAlgorithmException e) {
            result = null;
        }
        return result;
    }

    /**
     * Convert byte[] to hex string. 把字节数组转化为字符串
     * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
