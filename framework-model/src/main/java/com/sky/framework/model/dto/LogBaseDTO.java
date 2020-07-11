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
package com.sky.framework.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author
 */
@Data
public class LogBaseDTO {

    private static final String pidOrigin = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public LogBaseDTO() {
        pid = pidOrigin;
    }

    /**
     * pid
     */
    private String pid;
    /**
     *
     */
    private String host;
    /**
     *
     */
    private String ip;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 日志时间
     */
    private String logTime;
    /**
     * 日志时间int类型
     */
    private Integer time;
    /**
     * 等级
     */
    private String level;
    /**
     * 类型
     *
     * @see com.sky.framework.model.util.LogType
     */
    private String type;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 标签
     */
    private String tag;
    /**
     * 线程名称
     */
    private String thread;
    /**
     * 日志输出来源类名与行号
     */
    private String location;
    /**
     * 异常栈信息
     */
    private String throwable;
    /**
     * encoder 之后的日志信息
     */
    private String log;
    /**
     * mdc 信息
     */
    private Map<String, String> mdcFields;
}
