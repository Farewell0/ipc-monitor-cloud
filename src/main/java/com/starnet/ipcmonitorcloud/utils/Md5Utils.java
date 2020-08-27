/**
 * @title: Md5Utils
 * @package: com.nexhome.community.utils.crypt
 * @project: community
 * @description:
 * @author: hades
 * @company:
 * @date: 2019/9/16 17:09
 * @version V1.0
 */
package com.starnet.ipcmonitorcloud.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @className: Md5Utils
 * @description:
 * @author: hades
 * @date: 2019/9/16 17:09
 * @mark:
 */
public class Md5Utils {
    /**
     *
     * @param path
     * @return 小写的md5
     */
    public static String getMd5ByFile(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(path))) {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param source
     * @return 小写的md5
     */
    public static String getMd5(String source) {
        if (source == null || source.length() == 0)
            return null;
        try {
            return getMd5(source.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param source
     * @return 小写的md5
     */
    public static String getMd5(byte[] source) {
        String s = null;
        char hexDigits[] = {       // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();          // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2];   // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0;                                // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) {          // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i];                 // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf];            // 取字节中低 4 位的数字转换
            }
            s = new String(str).toLowerCase();                                 // 换后的结果转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}