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
package com.sky.framework.oss.model;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
@Data
public class UploadToken {

    private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String PATH_SEPARATOR = "/";
    /**
     * 过期时间（单位秒）
     */
    private long expires = 3600;

    private String bucketName;

    private String fileType;

    private String uploadDir;

    private String fileName;

    private String callbackUrl;

    private String callbackBody;

    private String callbackHost;

    private boolean callbackBodyUseJson = false;

    /**
     * 如：image/jpg 可以支持通配符image/*
     */
    private String mimeLimit;
    /**
     * 单位 byte
     */
    private Long fsizeMin;

    private Long fsizeMax;

    private Integer deleteAfterDays;


    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
        if (!StringUtils.isEmpty(uploadDir)) {
            if (!this.uploadDir.endsWith(PATH_SEPARATOR)) {
                this.uploadDir = this.uploadDir.concat(PATH_SEPARATOR);
            }
            if (this.uploadDir.startsWith(PATH_SEPARATOR)) {
                this.uploadDir = this.uploadDir.substring(1);
            }
        }
    }

    public String getCallbackBodyType() {
        return callbackBodyUseJson ? CONTENT_TYPE_JSON : CONTENT_TYPE_FORM_URLENCODED;
    }

    public String getFileKey() {
        if (StringUtils.isEmpty(uploadDir) || StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        return uploadDir.concat(fileName);
    }

    public String getCallbackRuleAsJson() {
        if (StringUtils.isEmpty(callbackBody) || StringUtils.isEmpty(callbackHost) || StringUtils.isEmpty(callbackUrl)) {
            return null;
        }
        Map<String, String> map = new HashMap<>(8);
        map.put("callbackBody", callbackBody);
        map.put("callbackHost", callbackHost);
        map.put("callbackUrl", callbackUrl);
        map.put("callbackBodyType", getCallbackBodyType());
        return JSON.toJSONString(map);
    }

}
