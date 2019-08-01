/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.common.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-128 ECB加密.<br>
 *
 * <pre>
 * 字符集:UTF-8
 * 算法模式:ECB
 * 数据块:128位
 * 补码方式:PKCS5Padding
 * 加密结果编码方式:Base64
 * </pre>
 *
 * @author
 *
 */
public class AesUtils {
    private static final String UTF8 = "UTF-8";
    private static final String ALGORITHM = "AES";
    /** 默认的加密算法 */
    private static final String ALGORITHM_CIPHER = "AES/ECB/PKCS5Padding";

    private static final int LIMIT_LEN = 16;

    /**
     * 生成一个SecretKey
     * @param password 长度必须小于等于16
     * @return 返回SecretKey
     */
    public static SecretKey getSecretKey(String password) {
        byte[] passwordData = password.getBytes();
        if(passwordData.length > LIMIT_LEN) {
            throw new IllegalArgumentException("password 长度必须小于等于16");
        }
        // 创建一个空的16位字节数组（默认值为0）,16byte（128bit）
        byte[] keyData = new byte[16];
        System.arraycopy(passwordData, 0, keyData, 0, passwordData.length);

        return new SecretKeySpec(keyData, ALGORITHM);
    }

    /**
     * 加密
     * @param data 待加密数据
     * @param password 密码
     * @return 返回加密成功后数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKey(password);
        // Ciphr完成加密或解密工作类
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        // 对Cipher初始化，解密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // 加密data
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * @param data 待解密数据
     * @param password 密码
     * @return 返回解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKey(password);
        // Cipher完成加密或解密工作类
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        // 对Cipher初始化，解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 解密data
        return cipher.doFinal(data);
    }

    /**
     * 文本加密
     * @param content 明文
     * @param password 密码
     * @return 返回base64内容
     * @throws Exception
     */
    public static String encryptToBase64String(String content, String password) throws Exception {
        byte[] data = content.getBytes(UTF8);
        byte[] result = encrypt(data, password);
        return Base64.encodeBase64String(result);
    }

    /**
     * 文本解密
     * @param base64String 待解密文本
     * @param password 密码
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptFromBase64String(String base64String, String password) throws Exception {
        byte[] data = Base64.decodeBase64(base64String);
        byte[] contentData = decrypt(data, password);
        return new String(contentData, UTF8);
    }

    /**
     * 文本加密
     * @param content 明文
     * @param password 密码
     * @return 返回16进制内容
     * @throws Exception
     */
    public static String encryptToHex(String content, String password) throws Exception {
        byte[] data = content.getBytes(UTF8);
        byte[] result = encrypt(data, password);
        return Hex.encodeHexString(result);
    }

    /**
     * 文本解密
     * @param hex 待解密文本
     * @param password 密码
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptFromHex(String hex, String password) throws Exception {
        byte[] data = Hex.decodeHex(hex.toCharArray());
        byte[] contentData = decrypt(data, password);
        return new String(contentData,UTF8);
    }

    /*public static void main(String[] args) throws Exception {
        String content = "我爱你";
        String password = "1234567890123456";
        System.out.println("password:" + password);

        String ret2 = encryptToBase64String(content, password);
        System.out.println("密文：" + ret2);
        String content3 = decryptFromBase64String(ret2, password);
        System.out.println(content.equals(content3));
    }*/

}
