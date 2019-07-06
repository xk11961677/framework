package com.sky.framework.common.encrypt;

import com.sky.framework.common.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DesUtil {

    private static Integer AES_LENGTH = 128;
    private static Integer RSA_LENGTH = 1024;
    private static String AES_ALGORITHM = "AES";
    private static String DES_ALGORITHM = "DES";
    private static String RSA_ALGORITHM = "RSA";
    private static String ALGORITHM_MODE = "/CBC/PKCS5Padding";

    public DesUtil() {
    }

    public static String encryptByAES(String content, String password) {
        String result = "";

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(AES_LENGTH, random);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
            cipher.init(1, sks, new IvParameterSpec(getIV()));
            byte[] resultByte = cipher.doFinal(content.getBytes("UTF-8"));
            result = new String(Hex.encodeHex(resultByte));
        } catch (Exception var9) {
            LogUtil.error(log,"encryptByAES error=" + var9.getMessage(), var9);
        }

        return result;
    }

    public static byte[] getIV() throws Exception {
        return "asdfivh7".getBytes("UTF-8");
    }

    public static String decryptByAES(byte[] content, String password) {
        String result = "";

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(AES_LENGTH, random);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
            cipher.init(2, sks, new IvParameterSpec(getIV()));
            result = new String(cipher.doFinal(content), "UTF-8");
        } catch (Exception var8) {
            LogUtil.error(log,"encryptByAES error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static String decryptByAES(String content, String password) {
        String result = "";

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            keyGenerator.init(AES_LENGTH, random);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec sks = new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM + ALGORITHM_MODE);
            cipher.init(2, sks, new IvParameterSpec(getIV()));
            result = new String(cipher.doFinal(Hex.decodeHex(content.toCharArray())), "UTF-8");
        } catch (Exception var8) {
            LogUtil.error(log,"encryptByAES error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static String encryptByDES(String content, String key) {
        String result = "";

        try {
            Key desKey = desKeyGenerator(key);
            new IvParameterSpec(key.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
            cipher.init(1, desKey, new IvParameterSpec(getIV()));
            byte[] byteResult = cipher.doFinal(content.getBytes("UTF-8"));
            result = Base64.encodeBase64String(byteResult);
        } catch (Exception var7) {
            LogUtil.error(log,"encryptByAES error=" + var7.getMessage(), var7);
        }

        return result;
    }

    public static String decryptByDES(String content, String key) {
        String result = "";

        try {
            Key desKey = desKeyGenerator(key);
            new IvParameterSpec(key.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM + ALGORITHM_MODE);
            cipher.init(2, desKey, new IvParameterSpec(getIV()));
            byte[] byteResult = cipher.doFinal(Base64.decodeBase64(content.getBytes("UTF-8")));
            result = new String(byteResult, "UTF-8");
        } catch (Exception var7) {
            LogUtil.error(log,"encryptByAES error=" + var7.getMessage(), var7);
        }

        return result;
    }

    public static String encryptByRSAPublicKey(String content, String publicKey) {
        String result = "";

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            RSAPublicKey rsaPublicKey = (RSAPublicKey)keyFactory.generatePublic(x509EncodeKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(1, rsaPublicKey);
            byte[] resultBytes = cipher.doFinal(content.getBytes("UTF-8"));
            result = Base64.encodeBase64String(resultBytes);
        } catch (Exception var8) {
            var8.printStackTrace();
            LogUtil.error(log,"encryptByRSAPublicKey error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static String decryptByRSAPrivateKey(String content, String privateKey) {
        String result = "";

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PKCS8EncodedKeySpec pkcs8EncodeKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8EncodeKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(2, rsaPrivateKey);
            byte[] resultBytes = cipher.doFinal(Base64.decodeBase64(content));
            result = new String(resultBytes, "UTF-8");
        } catch (Exception var8) {
            LogUtil.error(log,"decryptByRSAPrivateKey error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static String decryptByRSAPublicKey(String content, String publicKey) {
        String result = "";

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec x509EncodeKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            RSAPublicKey rsaPublicKey = (RSAPublicKey)keyFactory.generatePublic(x509EncodeKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(2, rsaPublicKey);
            byte[] resultBytes = cipher.doFinal(Base64.decodeBase64(content));
            result = new String(resultBytes, "UTF-8");
        } catch (Exception var8) {
            LogUtil.error(log,"decryptByRSAPublicKey error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static String encryptByRSAPrivateKey(String content, String privateKey) {
        String result = "";

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PKCS8EncodedKeySpec pkcs8EncodeKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyFactory.generatePrivate(pkcs8EncodeKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(1, rsaPrivateKey);
            byte[] resultBytes = cipher.doFinal(content.getBytes("UTF-8"));
            result = Base64.encodeBase64String(resultBytes);
        } catch (Exception var8) {
            LogUtil.error(log,"encryptByRSAPrivateKey error=" + var8.getMessage(), var8);
        }

        return result;
    }

    public static Map<String, String> createRSAKeys() {
        HashMap rsaMap = new HashMap();

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(RSA_LENGTH);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();
            rsaMap.put("publicKey", Base64.encodeBase64String(publicKey.getEncoded()));
            rsaMap.put("privateKey", Base64.encodeBase64String(privateKey.getEncoded()));
        } catch (Exception var5) {
            LogUtil.error(log,"createRSAKeys error=" + var5.getMessage(), var5);
        }

        return rsaMap;
    }

    private static SecretKey desKeyGenerator(String key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
        return keyFactory.generateSecret(desKey);
    }
}
