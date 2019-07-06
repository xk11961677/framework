package com.sky.framework.common.encrypt;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author
 */
public class DefaultEncrypter implements Encrypter{

    @Override
    public String aesEncryptToHex(String content, String password) throws Exception {
        return AesUtil.encryptToHex(content, password);
    }

    @Override
    public String aesDecryptFromHex(String hex, String password) throws Exception {
        return AesUtil.decryptFromHex(hex, password);
    }

    @Override
    public String aesEncryptToBase64String(String content, String password) throws Exception {
        return AesUtil.encryptToBase64String(content, password);
    }

    @Override
    public String aesDecryptFromBase64String(String base64String, String password) throws Exception {
        return AesUtil.decryptFromBase64String(base64String, password);
    }


    @Override
    public String rsaDecryptByPrivateKey(String data, String privateKey) throws Exception {
        return RsaUtil.decryptByPrivateKey(data, privateKey);
    }

    @Override
    public String rsaEncryptByPrivateKey(String data, String privateKey) throws Exception {
        return RsaUtil.encryptByPrivateKey(data, privateKey);
    }

    @Override
    public String md5(String value) {
        return DigestUtils.md5Hex(value);
    }
}
