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

import com.sky.framework.oss.model.UploadObject;
import com.sky.framework.oss.model.UploadToken;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Object Adapter Pattern
 *
 * @author
 */
public class OssStrategyAdapter extends AbstractOssClient {

    private OssStrategy ossStrategy;

    public OssStrategyAdapter(OssStrategy ossStrategy) {
        this.ossStrategy = ossStrategy;
    }

    @Override
    public String upload(File file) {
        return ossStrategy.upload(new UploadObject(file));
    }

    @Override
    public String upload(String fileKey, File file) {
        return ossStrategy.upload(new UploadObject(fileKey, file));
    }

    @Override
    public String upload(String fileKey, File file, String catalog) {
        return ossStrategy.upload(new UploadObject(fileKey, file).toCatalog(catalog));
    }

    @Override
    public String upload(String fileKey, byte[] contents) {
        return ossStrategy.upload(new UploadObject(fileKey, contents));
    }

    @Override
    public String upload(String fileKey, byte[] contents, String catalog) {
        return ossStrategy.upload(new UploadObject(fileKey, contents).toCatalog(catalog));
    }

    @Override
    public String upload(String fileKey, InputStream in, String mimeType) {
        return ossStrategy.upload(new UploadObject(fileKey, in, mimeType));
    }

    @Override
    public String upload(String fileKey, InputStream in, String mimeType, String catalog) {
        return ossStrategy.upload(new UploadObject(fileKey, in, mimeType).toCatalog(catalog));
    }

    @Override
    public boolean delete(String fileKey) {
        return ossStrategy.delete(fileKey);
    }

    @Override
    public String getDownloadUrl(String fileKey) {
        return ossStrategy.getDownloadUrl(fileKey);
    }

    @Override
    public Map<String, Object> createUploadToken(UploadToken param) {
        return ossStrategy.createUploadToken(param);
    }

    @Override
    public OssStrategy getOssStrategy() {
        return ossStrategy;
    }
}
