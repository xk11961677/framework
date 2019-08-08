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
package com.sky.framework.oss.util;

/**
 *
 * @author
 */
public class FilePathHelper {

    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String DIR_SPLITER = "/";

    public static String parseFileExtension(String filePath) {
        if (filePath.contains("/")) {
            filePath = filePath.substring(filePath.lastIndexOf("/"));
        }
        filePath = filePath.split("\\?")[0];
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static String parseFileName(String filePath) {
        filePath = filePath.split("\\?")[0];
        int index = filePath.lastIndexOf("/") + 1;
        if (index > 0) {
            return filePath.substring(index);
        }
        return filePath;
    }

    public static void main(String[] args) {
        System.out.println(parseFileExtension("http:www.ssss.com/cccc/123.png?xxx"));
        System.out.println(parseFileExtension("123.png"));
        System.out.println(parseFileExtension("http:www.ssss.com/cccc/dtgh4r4tt/"));

        System.out.println(parseFileName("http:www.ssss.com/cccc/123.png?cfg"));
    }
}
