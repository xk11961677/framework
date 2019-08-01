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

import java.util.concurrent.Future;

/**
 * 实现任务的代理
 *
 * @author
 */
public class ThreadPoolAdaptor implements IAsynchronousHandler {

    private IAsynchronousHandler handler;

    private Future<Object> future;

    private final long executeTime;

    public ThreadPoolAdaptor(IAsynchronousHandler handler, long time) {
        this.handler = handler;
        executeTime = System.currentTimeMillis() + time;
    }

    //获取真实的任务对象
    public IAsynchronousHandler getHandler() {
        return handler;
    }


    Future<Object> getFuture() {
        return future;
    }

    void setFuture(Future<Object> future) {
        this.future = future;
    }

    long getExecuteTime() {

        return executeTime;
    }

    @Override
    public Object call() throws Exception {

        return handler.call();
    }

    @Override
    public void executeAfter(Throwable t) {

        handler.executeAfter(t);

    }

    @Override
    public void executeBefore(Thread t) {

        handler.executeBefore(t);

    }


}
