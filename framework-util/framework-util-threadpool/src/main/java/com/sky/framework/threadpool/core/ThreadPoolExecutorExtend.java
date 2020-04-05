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
package com.sky.framework.threadpool.core;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池扩展对象
 *
 * @author
 */
public class ThreadPoolExecutorExtend extends ThreadPoolExecutor {

    /**
     * 需要处理的任务数
     */
    final AtomicInteger submittedTasksCount = new AtomicInteger();

    ThreadPoolExecutorExtend(int corePoolSize, int maximumPoolSize,
                             long keepAliveTime, TimeUnit unit,
                             BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                             RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, handler);

    }

    /**
     * 获取当前线程池正在执行的任务数
     *
     * @return
     */
    public AtomicInteger getSubmittedTasksCount() {
        return this.submittedTasksCount;
    }

    @Override
    public void execute(Runnable command) {
        submittedTasksCount.incrementAndGet();
        super.execute(command);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        //执行完毕减1
        submittedTasksCount.decrementAndGet();
        if (r instanceof CommonFutureTask) {
            IAsynchronousHandler handler = ((CommonFutureTask) r).getR();
            if (handler == null) {
                throw new NullPointerException("线程池参数对象为null!");
            }
            handler.executeAfter(t);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (r instanceof CommonFutureTask) {
            IAsynchronousHandler handler = ((CommonFutureTask) r).getR();
            if (handler == null) {
                throw new NullPointerException("线程池参数对象为null!");
            }
            handler.executeBefore(t);
        }
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new CommonFutureTask<T>(callable);
    }
}
