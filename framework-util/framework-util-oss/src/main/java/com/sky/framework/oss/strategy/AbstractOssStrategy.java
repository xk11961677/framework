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
package com.sky.framework.oss.strategy;


import com.sky.framework.common.http.HttpClientUtils;

/**
 * 策略抽象类
 *
 * @author sky
 */
public abstract class AbstractOssStrategy implements OssStrategy {

    private static final String HTTP_PREFIX = "http://";

    private static final String HTTPS_PREFIX = "https://";

    protected static final String DIR_SPLITER = "/";

    /**
     * 文件访问前缀,如: http://oss-cn-hangzhou.aliyuncs.com
     */
    protected String urlPrefix;

    protected String bucketName;

    /**
     * 获取全路径
     *
     * @param file
     * @return java.lang.String
     * @author sky
     * @since
     */
    protected String getFullPath(String file) {
        if (file.startsWith(HTTP_PREFIX) || file.startsWith(HTTPS_PREFIX)) {
            return file;
        }
        return urlPrefix + file;
    }

    /**
     * 下载文件到指定目录
     *
     * @param file
     * @param localSaveDir
     * @return java.lang.String
     * @author sky
     * @since
     */
    @Override
    public String downloadAndSaveAs(String file, String localSaveDir) {
        return HttpClientUtils.downloadFile(getDownloadUrl(file), localSaveDir);
    }

}
