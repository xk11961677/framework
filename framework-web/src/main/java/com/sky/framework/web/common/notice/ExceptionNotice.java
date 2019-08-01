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
package com.sky.framework.web.common.notice;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Data
public class ExceptionNotice {

    /**
     * 工程名
     */
    protected String project;

    /**
     * 异常的标识码
     */
    protected String uid;

    /**
     * 方法名
     */
    protected String methodName;

    /**
     * 方法参数信息
     */
    protected List<Object> parames;

    /**
     * 类路径
     */
    protected String classPath;

    /**
     * 异常信息
     */
    protected String exceptionMessage;

    /**
     * 异常追踪信息
     */
    protected List<String> traceInfo;

    public ExceptionNotice(Throwable ex, String filterTrace, Object[] args) {
        this.exceptionMessage = gainExceptionMessage(ex);
        this.parames = args == null ? null : Arrays.stream(args).collect(toList());
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if(stackTrace!= null &&  stackTrace.length >= 50) {
            stackTrace = Arrays.copyOfRange(stackTrace,0,50);
        }
        List<StackTraceElement> list = Arrays.stream(stackTrace)
//                .filter(x -> x.getClassName().startsWith(filterTrace))
                .filter(x -> (!StringUtils.isEmpty(x.getFileName()) && !x.getFileName().equals("<generated>"))).collect(toList());
        if (list.size() > 0) {
            this.traceInfo = list.stream().map(x -> x.toString()).collect(toList());
            this.methodName = list.get(0).getMethodName();
            this.classPath = list.get(0).getClassName();
        }
        //this.uid = calUid();
    }

    private String gainExceptionMessage(Throwable exception) {
        String em = exception.toString();
        if (exception.getCause() != null) {
            em = String.format("%s\r\n\tcaused by : %s", em, gainExceptionMessage(exception.getCause()));
        }
        return em;
    }

    private String calUid() {
        String md5 = DigestUtils.md5DigestAsHex(String.format("%s-%s", exceptionMessage, traceInfo.get(0)).getBytes());
        return md5;
    }

    @SuppressWarnings("all")
    public String createText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("工程信息：").append(project).append("\n\n");
        stringBuilder.append("类路径：").append(classPath).append("\n\n");
        stringBuilder.append("方法名：").append(methodName).append("\n\n");
        if (parames != null && parames.size() > 0) {
            stringBuilder.append("参数信息：")
                    .append(String.join(",", parames.stream().map(x -> x.toString()).collect(toList()))).append("\n\n");
        }
        stringBuilder.append("异常信息：").append(exceptionMessage).append("\n\n");
        stringBuilder.append("异常追踪：").append("\n\n").append(String.join("\n", traceInfo)).append("\n\n");
        return stringBuilder.toString();

    }
}
