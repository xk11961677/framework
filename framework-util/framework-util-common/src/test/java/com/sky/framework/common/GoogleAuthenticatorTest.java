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
package com.sky.framework.common;

import org.junit.Test;

public class GoogleAuthenticatorTest {

    @Test
    public void genSecretTest() {
        // 生成密码
        String secret = GoogleAuthenticator.generateSecretKey();
        // 生成二维码地址
        // 帐户名建议使用自己 APP名称+APP账户(手机/邮箱)， 例如：admin-18900000000
        // host 域名
        String qrcode = GoogleAuthenticator.getQRBarcodeURL(
                "admin-18911752664", "127.0.0.1", secret);
        System.out.println("二维码地址:" + qrcode);
        System.out.println("密钥:" + secret);
    }

    @Test
    public void verifyTest() {
        // 上面生成的密钥
        String secret = "EWQAMQX537B222FO";
        // Google验证器动态验证码
        String code = "989988";
        GoogleAuthenticator ga = new GoogleAuthenticator();
        boolean r = ga.checkCode(secret, code, System.currentTimeMillis());
        System.out.println("动态验证码是否正确：" + r);
    }

}
