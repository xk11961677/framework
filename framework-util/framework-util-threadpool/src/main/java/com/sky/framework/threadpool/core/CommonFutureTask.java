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

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * callable包装类
 *
 * @author
 */
public class CommonFutureTask<V> extends FutureTask<V> {

    private IAsynchronousHandler r;

    /**
     * 获取自定义IAsynchronousHandler
     *
     * @param callable
     * @since
     */
    public CommonFutureTask(Callable<V> callable) {
        super(callable);
        if (callable instanceof IAsynchronousHandler) {
            this.r = (IAsynchronousHandler) callable;
        }
    }

    /**
     * 获取有返回结果的自定义IAsynchronousHandler
     *
     * @param runnable
     * @param result
     * @since
     */
    public CommonFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    public IAsynchronousHandler getR() {
        return r;
    }

}
