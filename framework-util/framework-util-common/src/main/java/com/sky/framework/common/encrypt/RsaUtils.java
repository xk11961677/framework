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

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具<br>
 * @author
 */
public class RsaUtils {
    public static final String RSA_ALGORITHM = "RSA";
    public static final String UTF8 = "UTF-8";

    /**
     * 创建公钥私钥
     * 
     * @return 返回公私钥对
     * @throws Exception
     */
    public static KeyStore createKeys() throws Exception {
        KeyPairGenerator keyPairGeno = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGeno.initialize(1024);
        KeyPair keyPair = keyPairGeno.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        KeyStore keyStore = new KeyStore();
        keyStore.setPublicKey(Base64.encodeBase64String(publicKey.getEncoded()));
        keyStore.setPrivateKey(Base64.encodeBase64String(privateKey.getEncoded()));
        return keyStore;
    }

    /**
     * 获取公钥对象
     *
     * @param pubKeyData 公钥数据
     * @return 公钥对象
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(byte[] pubKeyData) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取公钥对象
     *
     * @param pubKey
     *            公钥
     * @return 返回公钥对象
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String pubKey) throws Exception {
        return getPublicKey(Base64.decodeBase64(pubKey));

    }

    /**
     * 获取私钥对象
     * 
     * @param priKey
     *            私钥
     * @return 返回私钥对象
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String priKey) throws Exception {
        return getPrivateKey(Base64.decodeBase64(priKey));
    }

    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     * 
     * @param keyBytes 私钥数据
     * @return 返回公钥对象
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

    }

    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        return encryptByPublicKey(data, getPublicKey(publicKey));
    }

    /**
     * 公钥加密
     *
     * @param data 内容
     * @param publicKey 公钥
     * @return 返回密文
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(data.getBytes(UTF8));
        return Base64.encodeBase64String(bytes);
    }

    public static String decryptByPublicKey(String data, String rsaPublicKey) throws Exception {
        return decryptByPublicKey(data, getPublicKey(rsaPublicKey));
    }

    /**
     * 公钥解密
     *
     * @param data 待解密内容
     * @param rsaPublicKey 公钥
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptByPublicKey(String data, RSAPublicKey rsaPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
        byte[] inputData = Base64.decodeBase64(data);
        byte[] bytes = cipher.doFinal(inputData);
        return new String(bytes, UTF8);
    }

    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        return encryptByPrivateKey(data, getPrivateKey(privateKey));
    }

    /**
     * 私钥加密
     * 
     * @param data 内容
     * @param privateKey 私钥
     * @return 返回密文
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(data.getBytes(UTF8));
        return Base64.encodeBase64String(bytes);
    }

    public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
        return decryptByPrivateKey(data, getPrivateKey(privateKey));
    }

    /**
     * 私钥解密
     * 
     * @param data 待解密内容
     * @param privateKey 私钥
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] inputData = Base64.decodeBase64(data);
        byte[] bytes = cipher.doFinal(inputData);
        return new String(bytes, UTF8);
    }
    

    /*
    pubKey:
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCG/iIZZzb16PxKqslkDMYa4tVFb3IVPBpLj4BgHQmDfe843sG4gkJIPXCm7+t6QxIbfDfynBpqZJLvu0c6E7TqlCtynBIlRFOBZrQVNEFkaanR2Kln3vd3CIidR571UstOC32XDyqAQNlvjD19zeIDVfmLa0Q+Or0zaxY99QwBHwIDAQAB
priKey:
MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIb+IhlnNvXo/EqqyWQMxhri1UVvchU8GkuPgGAdCYN97zjewbiCQkg9cKbv63pDEht8N/KcGmpkku+7RzoTtOqUK3KcEiVEU4FmtBU0QWRpqdHYqWfe93cIiJ1HnvVSy04LfZcPKoBA2W+MPX3N4gNV+YtrRD46vTNrFj31DAEfAgMBAAECgYBiNPQdwwcq86rHr2QAE4L0AF3ju+YlKKqAmg9s3PMU5ENq/jO0xZ7u6zPPXu/S7IR51m7lY0ecazqyiW6SA9AzYH7ImWWkZ4stZ03beTB2US3cSeJIkugoexoN5fQRAGZiZezTLs91CeJivESOZyDKnnQdgJ49mveBV5OvievD8QJBAMztpqiWWavdR4tqQ+plat+rwYoXqejsK3Hyfg0pVJqEdazve2sr74rla7yI9P47ZAh1sklCv0CO//ctICv366UCQQCoop3T0FeZtbKJG+fHzZvpAe63tXpdhLMaQvTBuXLG8vi78Wyfhg5r7HOWR0Z1V7nzF1gzMywL53Pmkq9tB65zAkAiHu/A4kfL9ewTqn3kaT6CP3baJ1aDEc+qCVYzms4bbDKruLQ0A/y+g7SMj8E7E2h0gCRPTm3JsgWsgjb5Gy6BAkAA8mjQd6sGQe7utilnBdCKTmh4v5wgSk53J0kYjWIHm/WpmIFzo90Q3hMIFP5gSk3Q/6CPKQpmRrZv5QL3KcPhAkEAuMoQbij/7hyLlIxRHZs2SMXxfHPiZgDc6rVi1KNxeq8HXTlERi7Npc2Uz5TeWN4JwBBx9uA50zowk9iS05nclQ==
用公钥加密mi : c3B0jtMdvkqrgaPxHZCK2cXMUQC2QzLud2ouLMNx0nBAj9k2/ytOuVJViTGe/DozB/ky5jvl4spD9Ey6aTMrwLHfQVhn0gRJ+wHcmx/51dXQDIgsldt6bf7YpdPdnghBjQz2+P5RhqSkeFDbTZKkl2BNaLE78a/OyWWeCGwN+4s=
true
用私钥加密mi2 : QU5vDnQ1ukj8GsauokFlgcB/g61U882tj82wHGrrqHEnvaga+4cXjML9RhjpZtKqwDGZTCujsmpynDk4qek6IGOQ/oxdWLwV4ZNjfa/oqA8OFDothVUT8wpqCu9kOYHrTdGybmXD0dB2Iy1/AMQTAgPNNXXiRXdvsz9xWYTV6z8=
true
     */
    /*public static void main(String[] args) throws Exception {
        KeyStore keys = createKeys();
        String pubKey = keys.getPublicKey();
        System.out.println("pubKey:");
        System.out.println(pubKey);
        String priKey = keys.getPrivateKey();
        System.out.println("priKey:");
        System.out.println(priKey);

        String ming = "1234567890123456";
        // 用公钥加密
        String mi = encryptByPublicKey(ming, pubKey);
        System.out.println("用公钥加密mi : " + mi);
        // 用私钥解密
        System.out.println(ming.equals(decryptByPrivateKey(mi, priKey)));

        // 用私钥加密
        String mi2 = encryptByPrivateKey(ming, priKey);

        System.out.println("用私钥加密mi2 : " + mi2);
        // 用公钥解密
        String ming2 = decryptByPublicKey(mi2, pubKey);
        System.out.println(ming.equals(ming2));
    }*/

}