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
package com.sky.framework.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池参数vo
 *
 * @author
 */
@ConfigurationProperties(prefix = "asyncthreadpool")
public class AsyncThreadPoolProperties {

    private static final int CORE_POOL_SIZE = 5;

    private static final int MAXIMUM_POOLSIZE = 200;

    private static final int INITIAL_CAPACITY = 1000000;

    private static final int KEEP_ALIVE_TIME = 120;


    private boolean enabled = true;

    private int corePoolSize = CORE_POOL_SIZE;

    private int maximumPoolSize = MAXIMUM_POOLSIZE;

    private int initialCapacity = INITIAL_CAPACITY;

    private long keepAliveTime = KEEP_ALIVE_TIME;

    private String threadName = "framework-threadpool-";

    private boolean isDiscard = true;

    public boolean isDiscard() {
        return isDiscard;
    }

    public void setDiscard(boolean isDiscard) {
        this.isDiscard = isDiscard;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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
