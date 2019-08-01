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
package com.sky.framework.common;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * @author
 */
public class LogUtils {

    public static final LogUtils LOG_UTIL = new LogUtils();

    private LogUtils() {

    }

    public static LogUtils getInstance() {
        return LOG_UTIL;
    }


    /**
     * debug 打印日志
     *
     * @param logger   日志
     * @param format   日志信息
     * @param supplier supplier接口
     */
    public static void debug(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, supplier.get());
        }
    }

    public static void debug(Logger logger, Supplier<Object> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(Objects.toString(supplier.get()));
        }
    }

    /**
     * debug 打印日志
     *
     * @param logger   日志
     * @param format   日志信息
     * @param objects
     */
    public static void debug(Logger logger, String format, Object... objects) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, objects);
        }
    }


    public static void debug(Logger logger, String info) {
        if (logger.isDebugEnabled()) {
            logger.debug(info);
        }
    }


    public static void info(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(format, supplier.get());
        }
    }


    public static void info(Logger logger, Supplier<Object> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(Objects.toString(supplier.get()));
        }
    }

    public static void info(Logger logger, String format, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(format, objects);
        }
    }

    public static void info(Logger logger, String info) {
        if (logger.isInfoEnabled()) {
            logger.info(info);
        }
    }


    public static void error(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(format, supplier.get());
        }
    }

    public static void error(Logger logger, Supplier<Object> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(Objects.toString(supplier.get()));
        }
    }


    public static void error(Logger logger, String format, Object... objects) {
        if (logger.isErrorEnabled()) {
            logger.error(format, objects);
        }
    }


    public static void error(Logger logger, String info) {
        if (logger.isErrorEnabled()) {
            logger.error(info);
        }
    }

    public static void warn(Logger logger, String format, Supplier<Object> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, supplier.get());
        }
    }

    public static void warn(Logger logger, Supplier<Object> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(Objects.toString(supplier.get()));
        }
    }

    public static void warn(Logger logger, String format, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, objects);
        }
    }

    public static void warn(Logger logger, String info) {
        if (logger.isWarnEnabled()) {
            logger.warn(info);
        }
    }


}
