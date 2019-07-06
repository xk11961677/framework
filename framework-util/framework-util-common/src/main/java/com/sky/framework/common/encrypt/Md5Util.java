package com.sky.framework.common.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author
 */
@Slf4j
public class Md5Util {

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * md5 32位
     *
     * @param sourceStr
     * @return
     */
    public static String getMD532Str(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 对字符串做(32位小写)MD5
     *
     * @param string 需要处理的字符串
     * @return 处理后的字符串。
     */
    public static String encode(String string) {
        try {
            byte[] stringByte = string.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(stringByte);
            byte[] md5Byte = messageDigest.digest();
            int j = md5Byte.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byteValue = md5Byte[i];
                str[k++] = hexDigits[byteValue >>> 4 & 0xf];
                str[k++] = hexDigits[byteValue & 0xf];
            }
            return (new String(str));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return ObjectUtils.toString(hexDigits[d1]) + ObjectUtils.toString(hexDigits[d2]);
    }

}
