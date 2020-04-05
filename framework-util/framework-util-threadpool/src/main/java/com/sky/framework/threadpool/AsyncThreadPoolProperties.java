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
package com.sky.framework.threadpool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池参数vo
 *
 * @author sky
 */
@ConfigurationProperties(prefix = "asyncthreadpool")
public class AsyncThreadPoolProperties {
    /**
     * 默认核心线程数
     */
    private static final int CORE_POOL_SIZE = 5;
    /**
     * 默认线程池最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 200;
    /**
     * 默认队列大小
     */
    private static final int INITIAL_CAPACITY = 1000000;
    /**
     * 默认线程不被使用后存活时间
     */
    private static final int KEEP_ALIVE_TIME = 120;

    /**
     * 是否开启
     */
    @Getter
    @Setter
    private boolean enabled = true;
    /**
     * 核心线程数
     */
    @Getter
    @Setter
    private int corePoolSize = CORE_POOL_SIZE;
    /**
     * 线程池最大线程数
     */
    @Getter
    @Setter
    private int maximumPoolSize = MAXIMUM_POOL_SIZE;
    /**
     * 队列大小
     */
    @Getter
    @Setter
    private int initialCapacity = INITIAL_CAPACITY;
    /**
     * 线程不被使用后存活时间
     */
    @Getter
    @Setter
    private long keepAliveTime = KEEP_ALIVE_TIME;
    /**
     * 线程名称前缀
     */
    @Getter
    @Setter
    private String threadName = "sky-framework-threadpool-";
    /**
     * 是否丢弃,默认丢弃
     */
    @Getter
    @Setter
    private boolean isDiscard = true;


    @Override
    public String toString() {
        return "AsyncThreadPoolProperties{" +
                "corePoolSize=" + corePoolSize +
                ", maximumPoolSize=" + maximumPoolSize +
                ", initialCapacity=" + initialCapacity +
                ", keepAliveTime=" + keepAliveTime +
                ", threadName='" + threadName + '\'' +
                ", isDiscard=" + isDiscard +
                '}';
    }
}
