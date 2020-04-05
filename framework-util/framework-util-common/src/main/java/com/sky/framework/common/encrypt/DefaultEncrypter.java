/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
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


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author
 */
public class DefaultEncrypter implements Encrypter{

    @Override
    public String aesEncryptToHex(String content, String password) throws Exception {
        return AesUtils.encryptToHex(content, password);
    }

    @Override
    public String aesDecryptFromHex(String hex, String password) throws Exception {
        return AesUtils.decryptFromHex(hex, password);
    }

    @Override
    public String aesEncryptToBase64String(String content, String password) throws Exception {
        return AesUtils.encryptToBase64String(content, password);
    }

    @Override
    public String aesDecryptFromBase64String(String base64String, String password) throws Exception {
        return AesUtils.decryptFromBase64String(base64String, password);
    }


    @Override
    public String rsaDecryptByPrivateKey(String data, String privateKey) throws Exception {
        return RsaUtils.decryptByPrivateKey(data, privateKey);
    }

    @Override
    public String rsaEncryptByPrivateKey(String data, String privateKey) throws Exception {
        return RsaUtils.encryptByPrivateKey(data, privateKey);
    }

    @Override
    public String md5(String value) {
        return DigestUtils.md5Hex(value);
    }
}
