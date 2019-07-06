package com.sky.framework.common.encrypt;

/**
 * 负责加解密
 *
 * @author
 */
public interface Encrypter {

    /**
     * AES文本加密
     *
     * @param content  明文
     * @param password 密码
     * @return 返回16进制内容
     * @throws Exception
     */
    String aesEncryptToHex(String content, String password) throws Exception;

    /**
     * AES文本解密
     *
     * @param hex      待解密文本,16进制内容
     * @param password 密码
     * @return 返回明文
     * @throws Exception
     */
    String aesDecryptFromHex(String hex, String password) throws Exception;

    /**
     * AES文本加密
     *
     * @param content  明文
     * @param password 密码
     * @return 返回base64内容
     * @throws Exception
     */
    String aesEncryptToBase64String(String content, String password) throws Exception;

    /**
     * AES文本解密
     *
     * @param base64String 待解密文本,16进制内容
     * @param password     密码
     * @return 返回明文
     * @throws Exception
     */
    String aesDecryptFromBase64String(String base64String, String password) throws Exception;


    /**
     * rsa私钥解密
     *
     * @param data       解密内容
     * @param privateKey 私钥
     * @return 返回明文
     * @throws Exception
     */
    String rsaDecryptByPrivateKey(String data, String privateKey) throws Exception;


    /**
     * rsa私钥加密
     *
     * @param data       明文
     * @param privateKey 私钥
     * @return 返回密文
     * @throws Exception
     */
    String rsaEncryptByPrivateKey(String data, String privateKey) throws Exception;

    /**
     * md5加密,全部小写
     *
     * @param value
     * @return 返回md5内容
     */
    String md5(String value);
}
