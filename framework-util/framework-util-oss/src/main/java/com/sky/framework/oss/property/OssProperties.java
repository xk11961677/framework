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
package com.sky.framework.oss.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS公共实体
 *
 * @author sky
 */
@ConfigurationProperties(prefix = "oss")
@Data
public class OssProperties {

    /**
     * 图片访问地址前缀
     */
    private String urlPrefix;

    /**
     * bucket是否私有
     */
    private Boolean isPrivate = false;

    /**
     * @see com.sky.framework.oss.strategy.OssStrategyEnum
     */
    private String strategy;

    /**
     * 上传目录前缀
     */
    private String dirPrefix;

    /**
     * 回调接口地址(如: ali 通过前端上传时，ali需要回调地址通知图片信息或个人用户验签等)
     */
    private String callbackUrl;

    /**
     * 阿里云
     *
     * @see com.sky.framework.oss.property.AliyunOssProperties
     */
    private AliyunOssProperties aliyun = new AliyunOssProperties();

}
