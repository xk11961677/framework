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
package com.sky.framework.oss.strategy.aliyun;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.sky.framework.common.LogUtils;
import com.sky.framework.oss.exception.OssException;
import com.sky.framework.oss.model.UploadObject;
import com.sky.framework.oss.model.UploadToken;
import com.sky.framework.oss.strategy.AbstractOssStrategy;
import com.sky.framework.oss.strategy.OssStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云上传策略
 *
 * @author sky
 */
@Slf4j
public class AliyunOssStrategy extends AbstractOssStrategy {

    public static final String NAME = OssStrategyEnum.ALIYUN.getKey();

    private static final String URL_PREFIX_PATTERN = "(http).*\\.(com|cn)\\/";

    private static final String DEFAULT_CALLBACK_BODY = "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}";

    private OSS ossClient;

    private String bucketName;

    private String urlPrefix;

    private boolean isPrivate;

    private String accessKeyId;

    /**
     * 用户要往哪个域名发送上传请求
     * 如: oss-cn-hangzhou.aliyuncs.com
     */
    private String host;

    private String callbackUrl;

    public AliyunOssStrategy(String urlprefix, String endpoint, String bucketName, String accessKey, String secretKey, String callbackUrl, boolean isPrivate) {
        Validate.notBlank(endpoint, "[endpoint] not defined");
        Validate.notBlank(bucketName, "[bucketName] not defined");
        Validate.notBlank(accessKey, "[accessKey] not defined");
        Validate.notBlank(secretKey, "[secretKey] not defined");
        Validate.notBlank(urlprefix, "[urlprefix] not defined");

        this.accessKeyId = accessKey;
        this.bucketName = bucketName;
        this.urlPrefix = urlprefix.endsWith("/") ? urlprefix : (urlprefix + "/");
        this.isPrivate = isPrivate;
        this.callbackUrl = callbackUrl;
        this.host = StringUtils.remove(urlprefix, "/").split(":")[1];
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        if (!ossClient.doesBucketExist(bucketName)) {
            LogUtils.info(log, "Creating bucket " + bucketName + "\n");
            ossClient.createBucket(bucketName);
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            createBucketRequest.setCannedACL(isPrivate ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead);
            ossClient.createBucket(createBucketRequest);
        }
    }

    /**
     * 上传
     *
     * @param object
     * @return java.lang.String
     * @author sky
     * @since
     */
    @Override
    public String upload(UploadObject object) {
        try {
            PutObjectRequest request = null;
            if (object.getFile() != null) {
                request = new PutObjectRequest(bucketName, object.getFileName(), object.getFile());
            } else if (object.getBytes() != null) {
                request = new PutObjectRequest(bucketName, object.getFileName(), new ByteArrayInputStream(object.getBytes()));
            } else if (object.getInputStream() != null) {
                request = new PutObjectRequest(bucketName, object.getFileName(), object.getInputStream());
            } else {
                throw new IllegalArgumentException("upload object is NULL");
            }

            PutObjectResult result = ossClient.putObject(request);
            if (result.getResponse() == null) {
                return isPrivate ? object.getFileName() : urlPrefix + object.getFileName();
            }
            if (result.getResponse().isSuccessful()) {
                return result.getResponse().getUri();
            } else {
                throw new OssException(result.getResponse().getErrorResponseAsString());
            }
        } catch (OSSException e) {
            throw new OssException(e.getErrorMessage());
        }
    }

    /**
     * 生成签名
     * 服务端签名后前端直传使用
     *
     * @param param
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author sky
     * @since
     */
    //https://help.aliyun.com/document_detail/31926.html
    //https://help.aliyun.com/document_detail/31989.html?spm=a2c4g.11186623.6.907.tlMQcL
    @Override
    public Map<String, Object> createUploadToken(UploadToken param) {

        Map<String, Object> result = new HashMap<>();

        PolicyConditions policyConds = new PolicyConditions();
        if (param.getFsizeMin() != null && param.getFsizeMax() != null) {
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, param.getFsizeMin(), param.getFsizeMax());
        } else {
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        }
        if (param.getUploadDir() != null) {
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, param.getUploadDir());
        }

        if (StringUtils.isBlank(param.getCallbackHost())) {
            param.setCallbackHost(host);
        }

        if (StringUtils.isBlank(param.getCallbackBody())) {
            param.setCallbackBody(DEFAULT_CALLBACK_BODY);
        }

        if (StringUtils.isBlank(param.getCallbackUrl())) {
            param.setCallbackUrl(callbackUrl);
        }

        Date expire = DateUtils.addSeconds(new Date(), (int) param.getExpires());
        String policy = ossClient.generatePostPolicy(expire, policyConds);
        String policyBase64 = null;
        String callbackBase64 = null;
        try {
            policyBase64 = BinaryUtil.toBase64String(policy.getBytes(StandardCharsets.UTF_8.name()));
            String callbackJson = param.getCallbackRuleAsJson();
            if (callbackJson != null) {
                callbackBase64 = BinaryUtil.toBase64String(callbackJson.getBytes(StandardCharsets.UTF_8.name()));
            }
        } catch (Exception e) {
            throw new OssException(e);
        }
        String signature = ossClient.calculatePostSignature(policy);

        result.put("accessKeyId", accessKeyId);
        result.put("policy", policyBase64);
        result.put("signature", signature);
        result.put("host", param.getCallbackHost());
        result.put("dir", param.getUploadDir());
        result.put("expire", String.valueOf(expire.getTime()));
        if (callbackBase64 != null) {
            result.put("callback", callbackBase64);
        }
        return result;
    }

    /**
     * 删除
     *
     * @param fileKey
     * @return boolean
     * @author sky
     * @since
     */
    @Override
    public boolean delete(String fileKey) {
        ossClient.deleteObject(bucketName, fileKey);
        return true;
    }

    /**
     * ObjectAcl objectAcl = ossClient.getObjectAcl(bucketName, key);
     *
     * @param fileKey 文件（全路径或者fileKey）
     * @param fileKey
     * @return java.lang.String
     * @author sky
     * @since
     */
    @Override
    public String getDownloadUrl(String fileKey) {
        if (isPrivate) {
            URL url = ossClient.generatePresignedUrl(bucketName, fileKey, DateUtils.addHours(new Date(), 1));
            return url.toString().replaceFirst(URL_PREFIX_PATTERN, urlPrefix);
        }
        return urlPrefix + fileKey;
    }


    @Override
    public void close() {
        ossClient.shutdown();
    }

    @Override
    public String name() {
        return NAME;
    }
}
