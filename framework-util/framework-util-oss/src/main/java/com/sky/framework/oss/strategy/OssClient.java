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

import com.sky.framework.oss.model.UploadToken;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author
 */
public interface OssClient {

    /**
     * 根据文件上传
     * @param file
     * @return
     */
    String upload(File file);

    /**
     * 文件和文件名称
     * @param fileKey
     * @param file
     * @return
     */
    String upload(String fileKey, File file);

    /**
     *
     * @param fileKey
     * @param file
     * @param catalog
     * @return
     */
    String upload(String fileKey, File file, String catalog);

    /**
     *
     * @param fileKey
     * @param contents
     * @return
     */
    String upload(String fileKey, byte[] contents);

    /**
     *
     * @param fileKey
     * @param contents
     * @param catalog
     * @return
     */
    String upload(String fileKey, byte[] contents, String catalog);

    /**
     *
     * @param fileKey
     * @param in
     * @param mimeType
     * @return
     */
    String upload(String fileKey, InputStream in, String mimeType);

    /**
     *
     * @param fileKey
     * @param in
     * @param mimeType
     * @param catalog
     * @return
     */
    String upload(String fileKey, InputStream in, String mimeType, String catalog);

    /**
     *
     * @param fileKey
     * @return
     */
    boolean delete(String fileKey);

    /**
     *
     * @param fileKey
     * @return
     */
    String getDownloadUrl(String fileKey);

    /**
     *
     * @param token
     * @return
     */
    Map<String, Object> createUploadToken(UploadToken token);

    /**
     *
     * @return
     */
    OssStrategy getOssStrategy();
}
