package com.sky.framework.common.http;

import com.sky.framework.common.LogUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
            LogUtil.error(log, "build sslcontext exception:{}", e.getMessage());
        }
        return null;
    }
}