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
package com.sky.framework.common.http;

import com.sky.framework.common.LogUtils;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * @author
 */
@Slf4j
public class SSLFactory {

    private String keystorePath;

    private String keystorePassword;

    private String trustStorePath;

    private String trustStorePassword;

    private String aliasPassword;

    public SSLFactory(String keystorePath, String keystorePassword, String trustStorePath, String trustStorePassword, String aliasPassword) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
        this.aliasPassword = aliasPassword;
    }

    private SSLContext getSSLContext() throws NoSuchAlgorithmException {
        SSLContext c = null;

        // pick the first protocol available, preferring TLSv1.2, then TLSv1,
        // falling back to SSLv3 if running on an ancient/crippled JDK
        for (String proto : Arrays.asList("TLSv1.2", "TLSv1", "SSLv3")) {
            try {
                c = SSLContext.getInstance(proto);
                return c;
            } catch (NoSuchAlgorithmException x) {
                // keep trying
            }
        }
        throw new NoSuchAlgorithmException();
    }

    /**
     * 创建sslContext
     *
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     */
    public SSLContext buildSSLContext() {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, aliasPassword.toCharArray());

            KeyStore tks = KeyStore.getInstance("PKCS12");
            tks.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext c = getSSLContext();
            c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            return c;
        } catch (Exception e) {
            LogUtils.error(log, "build sslcontext exception:{}", e.getMessage());
        }
        return null;
    }
}