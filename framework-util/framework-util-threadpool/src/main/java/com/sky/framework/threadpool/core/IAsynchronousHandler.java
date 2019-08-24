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
package com.sky.framework.threadpool.core;

import java.util.concurrent.Callable;


/**
 * 线程池线程处理器接口
 *
 * @author
 */
public interface IAsynchronousHandler extends Callable<Object> {

    /**
     * 执行完成后，调用的方法
     * 即在runnable()方法执行完成后在执行executeAfter()
     * t - 导致终止的异常；如果执行正常结束，则为 null
     *
     * @param t
     * @since
     */
    void executeAfter(Throwable t);

    /**
     * 执行完成后前，调用的方法
     * 即在runnable()方法执行前在执行executeBefore()
     * t - 将运行任务 r 的线程
     *
     * @param t
     * @since
     */
    void executeBefore(Thread t);

}
