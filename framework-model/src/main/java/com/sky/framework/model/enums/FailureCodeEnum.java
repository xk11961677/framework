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
package com.sky.framework.model.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author
 */
public enum FailureCodeEnum implements ErrorCode {
    /**
     * Gl 990001 error code enum.
     */
    GL990001(990001, "参数错误"),
    /**
     * Gl 990002 error code enum.
     */
    GL990002(990002, "微服务不在线,或者网络超时"),
    /**
     * Gl 990003 error code enum.
     */
    GL990003(990003, "Bad Request"),
    /**
     * Gl 990004 error code enum.
     */
    GL990004(990004, "找不到指定资源"),
    /**
     * Gl 990005 error code enum.
     */
    GL990005(990005, "签名错误"),
    /**
     * Gl 990006 error code enum.
     */
    GL990006(990006, "参数格式错误"),
    /**
     * Gl 990007 error code enum.
     */
    GL990007(990007, "不支持的方法请求类型"),
    /**
     * Gl 990008 error code enum.
     */
    GL990008(990008, "请求时间已失效"),
    /**
     * Gl 100001 error code enum.
     */
    AUZ100001(100001, "未授权"),
    /**
     * Gl 100003 error code enum.
     */
    AUZ100003(100003, "无权访问"),
    /**
     * Gl 100001 error code enum.
     */
    AUZ100016(100016, "token过期"),
    /**
     * Gl 999998 error code enum.
     */
    GL999998(999998, "数据库异常"),
    /**
     * Gl -1 error code enum.
     */
    GL999999(-1, "未知异常");


    private Integer code;

    private String msg;

    FailureCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    /**
     * @param code
     * @return FailureCodeEnum
     */
    public static FailureCodeEnum getByCode(Integer code) {
        Optional<FailureCodeEnum> optional = Arrays.stream(values()).filter(e -> Objects.equals(code, e.getCode())).findFirst();
        return optional.orElse(GL999999);
    }
}
