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
package com.sky.framework.logback.appender;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.alibaba.fastjson.JSON;
import com.sky.framework.model.dto.LogBaseDto;
import com.sky.framework.model.util.LogType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;


/**
 * @author
 */
public class LogCustomAppender<E> extends UnsynchronizedAppenderBase<E> {

    /**
     * 项目名称
     */
    private String appName;

    protected Encoder<E> encoder;

    protected String timeZone = "UTC";

    protected String timeFormat = "yyyy-MM-dd'T'HH:mmZ";

    protected DateTimeFormatter formatter;

    protected java.time.format.DateTimeFormatter formatter1;

    private String mdcFields;

    @Override
    public void start() {
        try {
            doStart();
        } catch (Exception e) {
            addError("Failed to start LoghubAppender.", e);
        }
    }

    private void doStart() {
        try {
            formatter = DateTimeFormat.forPattern(timeFormat).withZone(DateTimeZone.forID(timeZone));
        } catch (Exception e) {
            formatter1 = java.time.format.DateTimeFormatter.ofPattern(timeFormat).withZone(ZoneId.of(timeZone));
        }
        super.start();
    }

    @Override
    public void stop() {
        try {
            doStop();
        } catch (Exception e) {
            addError("Failed to stop LogCustomAppender.", e);
        }
    }

    private void doStop() throws InterruptedException {
        if (!isStarted()) {
            return;
        }
        super.stop();
    }

    @Override
    public void append(E eventObject) {
        try {
            appendEvent(eventObject);
        } catch (Exception e) {
            addError("Failed to append event.", e);
        }
    }

    private void appendEvent(E eventObject) {
        //init Event Object
        if (!(eventObject instanceof LoggingEvent)) {
            return;
        }
        LoggingEvent event = (LoggingEvent) eventObject;

        LogBaseDto item = new LogBaseDto();
        item.setAppName(appName);
        item.setType(LogType.Logback.toString());
        item.setTime((int) (event.getTimeStamp() / 1000));

        if (formatter != null) {
            DateTime dateTime = new DateTime(event.getTimeStamp());
            item.setLogTime(dateTime.toString(formatter));
        } else {
            Instant instant = Instant.ofEpochMilli(event.getTimeStamp());
            item.setLogTime(formatter1.format(instant));
        }
        item.setLevel(event.getLevel().toString());
        item.setThread(event.getThreadName());

        StackTraceElement[] caller = event.getCallerData();
        if (caller != null && caller.length > 0) {
            item.setLocation(caller[0].toString());
        }

        String message = event.getFormattedMessage();
        item.setMessage(message);

        IThrowableProxy iThrowableProxy = event.getThrowableProxy();
        if (iThrowableProxy != null) {
            String throwable = getExceptionInfo(iThrowableProxy);
            throwable += fullDump(event.getThrowableProxy().getStackTraceElementProxyArray());
            item.setThrowable(throwable);
        }

        if (this.encoder != null) {
            item.setLog(new String(this.encoder.encode(eventObject)));
        }

        Optional.ofNullable(mdcFields).ifPresent(f -> item.setMdcFields(new HashMap<>(f.length())));
        Optional.ofNullable(mdcFields).ifPresent(
                f -> event.getMDCPropertyMap().entrySet().stream()
                        .filter(v -> Arrays.stream(f.split(",")).anyMatch(i -> i.equals(v.getKey())))
                        .forEach(map -> item.getMdcFields().put(map.getKey(), map.getValue()))
        );
        //TODO
        try {
            System.out.println(JSON.toJSONString(item));
        } catch (Exception e) {
            this.addError("Failed to send log item=" + item, e);
        }
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    private String getExceptionInfo(IThrowableProxy iThrowableProxy) {
        String s = iThrowableProxy.getClassName();
        String message = iThrowableProxy.getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    private String fullDump(StackTraceElementProxy[] stackTraceElementProxyArray) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElementProxy step : stackTraceElementProxyArray) {
            builder.append(CoreConstants.LINE_SEPARATOR);
            String string = step.toString();
            builder.append(CoreConstants.TAB).append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
        }
        return builder.toString();
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    // **** ==- ProjectConfig -== **********************
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setMdcFields(String mdcFields) {
        this.mdcFields = mdcFields;
    }
}