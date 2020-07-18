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

import org.slf4j.Logger;

import java.util.function.Supplier;


/**
 * @author
 */
public class LogUtils {

    public static final LogUtils INSTANCE = new LogUtils();

    private LogUtils() {
        /**Private constructor: this class is not intended to be instantiated.**/
    }

    public static LogUtils getInstance() {
        return INSTANCE;
    }


    /**
     * debug 打印日志
     *
     * @param logger    日志
     * @param format    日志信息
     * @param suppliers supplier接口
     */
    public static void debug(Logger logger, String format, Supplier<?>... suppliers) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, getAll(suppliers));
        }
    }

    /**
     * debug 打印日志
     *
     * @param logger
     * @param supplier
     */
    public static void debug(Logger logger, Supplier<String> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(supplier.get());
        }
    }

    /**
     * info 打印日志
     *
     * @param logger
     * @param format
     * @param suppliers
     */
    public static void info(Logger logger, String format, Supplier<?>... suppliers) {
        if (logger.isInfoEnabled()) {
            logger.info(format, getAll(suppliers));
        }
    }

    /**
     * info 打印日志
     *
     * @param logger
     * @param supplier
     */
    public static void info(Logger logger, Supplier<String> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    /**
     * error 打印日志
     *
     * @param logger
     * @param format
     * @param suppliers
     */
    public static void error(Logger logger, String format, Supplier<?>... suppliers) {
        if (logger.isErrorEnabled()) {
            logger.error(format, getAll(suppliers));
        }
    }

    /**
     * error 打印日志
     *
     * @param logger
     * @param supplier
     */
    public static void error(Logger logger, Supplier<String> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(supplier.get());
        }
    }

    /**
     * warn 打印日志
     *
     * @param logger
     * @param format
     * @param suppliers
     */
    public static void warn(Logger logger, String format, Supplier<?>... suppliers) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, getAll(suppliers));
        }
    }

    /**
     * warn 打印日志
     *
     * @param logger
     * @param supplier
     */
    public static void warn(Logger logger, Supplier<String> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(supplier.get());
        }
    }

    /**
     * Converts an array of lambda expressions into an array of their evaluation results.
     *
     * @param suppliers an array of lambda expressions or {@code null}
     * @return an array containing the results of evaluating the lambda expressions (or {@code null} if the suppliers
     * array was {@code null}
     */
    private static Object[] getAll(final Supplier<?>... suppliers) {
        if (suppliers == null) {
            return null;
        }
        final Object[] result = new Object[suppliers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = suppliers[i].get();
        }
        return result;
    }


    /**
     * debug 打印日志
     *
     * @param logger  日志
     * @param format  日志信息
     * @param objects
     */
    @Deprecated
    public static void debug(Logger logger, String format, Object... objects) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, objects);
        }
    }

    /**
     * debug 打印日志
     *
     * @param logger
     * @param info
     */
    @Deprecated
    public static void debug(Logger logger, String info) {
        if (logger.isDebugEnabled()) {
            logger.debug(info);
        }
    }

    /**
     * info 打印日志
     *
     * @param logger
     * @param format
     * @param objects
     */
    @Deprecated
    public static void info(Logger logger, String format, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(format, objects);
        }
    }

    /**
     * info 打印日志
     *
     * @param logger
     * @param info
     */
    @Deprecated
    public static void info(Logger logger, String info) {
        if (logger.isInfoEnabled()) {
            logger.info(info);
        }
    }

    /**
     * error 打印日志
     *
     * @param logger
     * @param format
     * @param objects
     */
    @Deprecated
    public static void error(Logger logger, String format, Object... objects) {
        if (logger.isErrorEnabled()) {
            logger.error(format, objects);
        }
    }

    /**
     * error 打印日志
     *
     * @param logger
     * @param info
     */
    @Deprecated
    public static void error(Logger logger, String info) {
        if (logger.isErrorEnabled()) {
            logger.error(info);
        }
    }

    /**
     * warn 打印日志
     *
     * @param logger
     * @param format
     * @param objects
     */
    @Deprecated
    public static void warn(Logger logger, String format, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(format, objects);
        }
    }

    /**
     * warn 打印日志
     *
     * @param logger
     * @param info
     */
    @Deprecated
    public static void warn(Logger logger, String info) {
        if (logger.isWarnEnabled()) {
            logger.warn(info);
        }
    }
}
