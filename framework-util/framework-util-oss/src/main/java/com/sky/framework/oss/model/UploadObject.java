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
/**
 *
 */
package com.sky.framework.oss.model;


import com.sky.framework.oss.util.FilePathHelper;
import com.sky.framework.oss.util.MimeTypeFileExtensionConvert;
import lombok.Data;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author
 */
@Data
public class UploadObject {

    private String fileName;

    private String mimeType;

    private String catalog;

    private String url;

    private byte[] bytes;

    private File file;

    private InputStream inputStream;

    private Map<String, Object> metadata = new HashMap<String, Object>();

    public UploadObject(String filePath) {
        if (filePath.startsWith(FilePathHelper.HTTP_PREFIX) || filePath.startsWith(FilePathHelper.HTTPS_PREFIX)) {
            this.url = filePath;
            this.fileName = FilePathHelper.parseFileName(this.url);
        } else {
            this.file = new File(filePath);
            this.fileName = file.getName();
        }
    }

    public UploadObject(File file) {
        this.fileName = file.getName();
        this.file = file;
    }

    public UploadObject(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }

    public UploadObject(String fileName, InputStream inputStream, String mimeType) {
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.mimeType = mimeType;
    }

    public UploadObject(String fileName, byte[] bytes, String mimeType) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.mimeType = mimeType;
    }

    public UploadObject(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.mimeType = perseMimeType(bytes);
    }

    public String getFileName() {
        if (StringUtils.isEmpty(fileName)) {
            fileName = UUID.randomUUID().toString().replaceAll("\\-", "");
        }
        if (mimeType != null && !fileName.contains(".")) {
            String fileExtension = MimeTypeFileExtensionConvert.getFileExtension(mimeType);
            if (fileExtension != null) {
                fileName = fileName + fileExtension;
            }
        }

        return fileName;
    }

    public UploadObject addMetaData(String key, Object value) {
        metadata.put(key, value);
        return this;
    }


    public UploadObject toCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    private static String perseMimeType(byte[] bytes) {
        try {
            MagicMatch match = Magic.getMagicMatch(bytes);
            String mimeType = match.getMimeType();
            return mimeType;
        } catch (Exception e) {
            return null;
        }
    }
}
