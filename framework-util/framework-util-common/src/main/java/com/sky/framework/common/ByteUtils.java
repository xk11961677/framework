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
package com.sky.framework.common;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

/**
 * @author
 */
public final class ByteUtils {

    private static final String charsetName = "UTF-8";

    public static final byte[] EMPTY = new byte[0];

    /**
     * 字符串转字节数组
     *
     * @param s
     * @return
     */
    public static byte[] toBytes(String s) {
        if (s == null) {
            return EMPTY;
        }
        return s.getBytes(Charset.forName(charsetName));
    }

    /**
     * 对象转字节数组
     *
     * @param s
     * @return
     */
    public static byte[] toBytes(Object s) {
        if (s == null) {
            return EMPTY;
        }
        return toBytes(String.valueOf(s));
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return StringUtils.EMPTY;
        }
        return new String(bytes, Charset.forName(charsetName));
    }

    /**
     * 字节数组是否为空
     *
     * @param data
     * @return
     */
    public static boolean isEmpty(byte[] data) {
        return data == null || data.length == 0;
    }

    /**
     * 字节数组是否不为空
     *
     * @param data
     * @return
     */
    public static boolean isNotEmpty(byte[] data) {
        return !isEmpty(data);
    }

}
