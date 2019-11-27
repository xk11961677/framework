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
package com.sky.framework.rpc.invoker.consumer;

import com.sky.framework.rpc.invoker.AbstractProcessor;
import com.sky.framework.rpc.invoker.future.DefaultInvokeFuture;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.core.CommonThreadPool;
import com.sky.framework.threadpool.core.DefaultAsynchronousHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 */
public class ConsumerProcessorHandler extends AbstractProcessor {

    private static ConsumerProcessorHandler instance;

    static {
        instance = new ConsumerProcessorHandler();
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }

    public ConsumerProcessorHandler() {
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Response response) {
        CommonThreadPool.execute(new DefaultAsynchronousHandler() {
            @Override
            public Object call() throws Exception {
                DefaultInvokeFuture.received(ctx, response);
                return null;
            }
        });
    }

    public static ConsumerProcessorHandler getInstance() {
        return instance;
    }
}
