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
package com.sky.framework.model.dto;

import com.sky.framework.model.util.LogLevel;
import com.sky.framework.model.util.LogType;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author
 *
 */
@Data
public class LogBaseDto {
    /**
     * pid
     */
    private static final String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    /**
     *
     */
    private static String hostName;
    /**
     *
     */
    private static String hostIp;


    /**
     * 应用名称
     */
    private String appName;

    /**
     * 日志时间
     */
    private String logTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    /**
     * 等级
     */
    private LogLevel level = LogLevel.Info;
    /**
     * 类型
     */
    private LogType type = LogType.Business;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 行号
     */
    private String num;
    /**
     * 标签
     */
    private String tag;
}
