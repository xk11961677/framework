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
package com.sky.framework.oss.strategy;

import com.sky.framework.oss.model.UploadObject;
import com.sky.framework.oss.model.UploadToken;

import java.io.Closeable;
import java.util.Map;

/**
 * 上传接口
 *
 * @author
 */
public interface OssStrategy extends Closeable {

    /**
     * @return
     */
    String name();

    /**
     * 文件上传
     *
     * @param object
     * @return
     */
    String upload(UploadObject object);

    /**
     * 获取文件下载地址
     *
     * @param fileKey 文件（全路径或者fileKey）
     * @return
     */
    String getDownloadUrl(String fileKey);

    /**
     * 删除图片
     *
     * @param fileKey
     * @return
     */
    boolean delete(String fileKey);

    /**
     * 下载并保存到某个位置
     *
     * @param fileKey
     * @param localSaveDir
     * @return
     */
    String downloadAndSaveAs(String fileKey, String localSaveDir);

    /**
     * 创建token
     *
     * @param uploadToken
     * @return
     */
    Map<String, Object> createUploadToken(UploadToken uploadToken);
}
