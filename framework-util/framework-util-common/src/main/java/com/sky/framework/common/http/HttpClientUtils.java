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


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.framework.common.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * HttpClient工具类
 *
 * @author
 */
@Slf4j
public class HttpClientUtils {
    /**
     * 连接超时时间
     */
    private static final int CONNECTION_TIMEOUT = 5000;

    /**
     * 请求超时时间
     */
    private static final int CONNECTION_REQUEST_TIMEOUT = 5000;

    /**
     * 数据读取等待超时
     */
    private static final int SOCKET_TIMEOUT = 10000;

    /**
     * http
     */
    private static final String HTTP = "http";

    /**
     * https
     */
    private static final String HTTPS = "https";

    /**
     * http端口
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * https端口
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    /**
     * 默认编码
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";

    /**
     * get请求(1.处理http请求;2.处理https请求,信任所有证书)[默认编码:UTF-8]
     *
     * @param url (参数直接拼接到URL后面，即http://test.com?a=1&b=2的形式)
     * @return
     */
    public static String get(String url) {
        return get(url, null, DEFAULT_ENCODING);
    }

    /**
     * get请求(1.处理http请求;2.处理https请求,信任所有证书)[默认编码:UTF-8]
     *
     * @param url    (url不带参数，例：http://test.com)
     * @param reqMap (参数放置到一个map中)
     * @return
     */
    public static String get(String url, Map<String, String> reqMap) {
        return get(url, reqMap, DEFAULT_ENCODING);
    }

    /**
     * get请求(1.处理http请求;2.处理https请求,信任所有证书)
     *
     * @param url      (只能是http或https请求)
     * @param encoding
     * @return
     */
    public static String get(String url, Map<String, String> reqMap, String encoding) {
        String result = "";
        if (StringUtils.isBlank(url)) {
            LogUtils.info(log, "url is null");
            return result;
        }

        // 处理参数
        List<NameValuePair> params = new ArrayList<>();
        if (reqMap != null && reqMap.keySet().size() > 0) {
            Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entity = iter.next();
                params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
            }
        }

        CloseableHttpClient httpClient;
        if (url.startsWith(HTTPS)) {
            /**
             * 创建一个SSL信任所有证书的httpClient对象
             */
            httpClient = HttpClientUtils.createSSLInsecureClient();
        } else {
            httpClient = HttpClients.createDefault();
        }

        CloseableHttpResponse response = null;
        HttpGet httpGet;

        try {
            if (params != null && params.size() > 0) {
                URIBuilder builder = new URIBuilder(url);
                builder.setParameters(params);
                httpGet = new HttpGet(builder.build());
            } else {
                httpGet = new HttpGet(url);
            }

            //默认允许自动重定向
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setRedirectsEnabled(true)
                    .build();
            httpGet.setConfig(requestConfig);

            // 发送请求，并接收响应
            response = httpClient.execute(httpGet);

            result = handleResponse(url, encoding, response);
        } catch (Exception e) {
            log.error("-----> url:" + url + ",get请求异常:" + e.getMessage(), e);
        } finally {
            closeResource(httpClient, response);
        }
        return result;
    }


    /**
     * post请求(1.处理http请求;2.处理https请求,信任所有证书)[默认编码:UTF-8]
     *
     * @param url
     * @param reqMap
     * @return
     */
    public static String post(String url, Map<String, String> reqMap, SSLContext sslContext) {
        return post(url, reqMap, DEFAULT_ENCODING, sslContext);
    }

    /**
     * post请求(1.处理http请求;2.处理https请求,信任所有证书)[默认编码:UTF-8]
     *
     * @param url
     * @param reqMap
     * @return
     */
    public static JSONObject post(String url, Map<String, String> reqMap) {
        String post = post(url, reqMap, DEFAULT_ENCODING, null);
        try {
            return JSONObject.parseObject(post);
        } catch (Exception e) {
            LogUtils.error(log, "httpclient exception:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * post请求(1.处理http请求;2.处理https请求,信任所有证书)
     *
     * @param url
     * @param reqMap   入参是个map
     * @param encoding
     * @return
     */
    @SuppressWarnings("all")
    public static String post(String url, Map<String, String> reqMap, String encoding, SSLContext sslContext) {
        String result = "";
        if (StringUtils.isBlank(url)) {
            LogUtils.info(log, "url is null");
            return result;
        }

        // 添加参数
        List<NameValuePair> params = new ArrayList<>();
        if (reqMap != null && reqMap.keySet().size() > 0) {
            Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entity = iter.next();
                params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
            }
        }

        CloseableHttpClient httpClient;
        if (url.startsWith(HTTPS) && sslContext == null) {
            httpClient = HttpClientUtils.createSSLInsecureClient();
        } else if (url.startsWith(HTTPS) && sslContext != null) {
            httpClient = HttpClients.custom().setSSLContext(sslContext).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setRedirectsEnabled(true)//默认允许自动重定向
                    .build();
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(params, encoding));

            // 发送请求，并接收响应
            response = httpClient.execute(httpPost);
            result = handleResponse(url, encoding, response);
        } catch (IOException e) {
            LogUtils.error(log, "url:{} post请求异常:{}", url, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }

        return result;
    }

    /**
     * post请求(1.处理http请求;2.处理https请求,信任所有证书)
     *
     * @param url
     * @param jsonParams 入参是个json字符串
     * @param encoding
     * @return
     */
    @SuppressWarnings("all")
    public static String post(String url, String jsonParams, String encoding, SSLContext sslContext) {
        String result = "";
        if (StringUtils.isBlank(url)) {
            LogUtils.info(log, "url is null");
            return result;
        }

        CloseableHttpClient httpClient = null;
        if (url.startsWith(HTTPS) && sslContext == null) {
            httpClient = HttpClientUtils.createSSLInsecureClient();
        } else if (url.startsWith(HTTPS) && sslContext != null) {
            httpClient = HttpClients.custom().setSSLContext(sslContext).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
        CloseableHttpResponse response = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setRedirectsEnabled(true)
                    .build();
            httpPost.setConfig(requestConfig);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonParams, ContentType.create("application/json", encoding)));

            // 发送请求，并接收响应
            response = httpClient.execute(httpPost);
            result = handleResponse(url, encoding, response);
        } catch (IOException e) {
            LogUtils.error(log, "url:{} post请求异常:{}", url, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }

        return result;
    }

    /**
     * 创建一个SSL信任所有证书的httpClient对象
     *
     * @return
     */
    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                // 默认信任所有证书
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslContext);

            return HttpClients.custom().setSSLSocketFactory(sslcsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return HttpClients.createDefault();
    }

    /**
     * 处理响应，获取响应报文
     *
     * @param url
     * @param encoding
     * @param response
     * @return
     * @throws IOException
     */
    private static String handleResponse(String url, String encoding, CloseableHttpResponse response) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;

        try {
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        br = new BufferedReader(new InputStreamReader(entity.getContent(), encoding));
                        String s = null;
                        while ((s = br.readLine()) != null) {
                            sb.append(s);
                        }
                    }

                    // 释放entity
                    EntityUtils.consume(entity);
                } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    LogUtils.info(log, "get请求404,未找到资源. url:" + url);
                } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    LogUtils.info(log, "get请求500,服务器端异常. url:" + url);
                }
            }
        } catch (Exception e) {
            LogUtils.error(log, "url:" + url + ",处理响应，获取响应报文异常：" + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    /**
     * 释放资源
     *
     * @param httpClient
     * @param response
     */
    private static void closeResource(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                LogUtils.error(log, "释放response资源异常:" + e.getMessage(), e);
                e.printStackTrace();
            }
        }

        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                LogUtils.error(log, "释放httpclient资源异常:" + e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }


    /**
     * 采用绕过验证的方式处理https请求
     *
     * @param url
     * @param reqMap
     * @param encoding
     * @return
     */
    @SuppressWarnings("all")
    public static String postSSLUrl(String url, Map<String, String> reqMap, String encoding) {
        String result = "";
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 添加参数
        List<NameValuePair> params = new ArrayList<>();
        if (reqMap != null && reqMap.keySet().size() > 0) {
            Iterator<Map.Entry<String, String>> iter = reqMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entity = iter.next();
                params.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
            }
        }

        try {
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = createIgnoreVerifySSL();
            //设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

            //创建自定义的httpclient对象
            httpClient = HttpClients.custom().setConnectionManager(connManager).build();

            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, encoding));

            //指定报文头Content-type、User-Agent
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            /**
             * 设置User-Agent
             * httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
             */

            /**
             * 执行请求操作，并拿到结果（同步阻塞）
             */
            response = httpClient.execute(httpPost);
            result = handleResponse(url, encoding, response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(httpClient, response);
        }

        return result;
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }


    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url       请求地址
     * @param headerMap 头信息
     * @param paramMap  参数
     * @return
     */
    public static String doPost(String url, Map<String, String> headerMap, Map<String, String> paramMap, String bodyStr) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            // 头部请求信息
            if (null != headerMap) {
                Iterator iterator = headerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    httpPost.addHeader(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            // 请求参数信息
            if (null != paramMap) {
                String contentType = "";
                if (null != headerMap && headerMap.containsKey(HEADER_CONTENT_TYPE)) {
                    contentType = headerMap.get(HEADER_CONTENT_TYPE).toLowerCase();
                }
                switch (contentType) {
                    case HEADER_CONTENT_TYPE_JSON:
                        String paramStr = JSON.toJSON(paramMap).toString();
                        StringEntity stringEntity = new StringEntity(paramStr, DEFAULT_ENCODING);//解决中文乱码问题
                        httpPost.setEntity(stringEntity);
                        break;
                    default:
                        List<NameValuePair> pairList = new ArrayList<NameValuePair>(paramMap.size());
                        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                            pairList.add(pair);
                        }
                        httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(DEFAULT_ENCODING)));
                        break;
                }
            }

            if (null != bodyStr) {
                httpPost.setEntity(new StringEntity(bodyStr, DEFAULT_ENCODING));
            }
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            httpStr = EntityUtils.toString(entity, DEFAULT_ENCODING);
            LogUtils.info(log, "doPost响应状态码：" + statusCode + "，结果：" + httpStr + "");
        } catch (IOException e) {
            LogUtils.error(log, "doPost执行异常：" + e.getMessage() + "", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LogUtils.error(log, "doPost执行异常：" + e.getMessage() + "", e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 下载文档到指定位置
     *
     * @param fileURL
     * @param saveDir
     * @return
     */
    public static String downloadFile(String fileURL, String saveDir) {
        HttpURLConnection httpConn = null;
        FileOutputStream outputStream = null;
        try {
            URL url = new URL(fileURL);
            httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");

                if (disposition != null) {
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
                }
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = saveDir + File.separator + fileName;

                outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[2048];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                return saveFilePath;
            }
        } catch (IOException e) {
            LogUtils.error(log, "下载失败:{}", e.getMessage(), e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            } catch (Exception e2) {
            }
        }
        return "";
    }
}