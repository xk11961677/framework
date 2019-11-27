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
package com.sky.framework.rpc.invoker.future;

import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.FastJsonSerializer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author
 */
@Slf4j
public class DefaultInvokeFuture<V> extends CompletableFuture<V> implements InvokeFuture<V> {

    private static final ConcurrentMap<Long, DefaultInvokeFuture<?>> roundFutures = new ConcurrentHashMap<>();

    private static final long DEFAULT_TIMEOUT_NANOSECONDS = TimeUnit.MILLISECONDS.toNanos(30000);

    private static final FastJsonSerializer serializer = new FastJsonSerializer();

    private final long invokeId;

    private final Class<V> returnType;

    private final long timeout;

    private final long startTime = System.nanoTime();

    public static <T> DefaultInvokeFuture<T> with(
            long invokeId, long timeoutMillis, Class<T> returnType) {

        return new DefaultInvokeFuture<>(invokeId, timeoutMillis, returnType);
    }

    private DefaultInvokeFuture(long invokeId, long timeoutMillis, Class<V> returnType) {
        this.invokeId = invokeId;
        this.timeout = timeoutMillis > 0 ? TimeUnit.MILLISECONDS.toNanos(timeoutMillis) : DEFAULT_TIMEOUT_NANOSECONDS;
        this.returnType = returnType;
        roundFutures.put(invokeId, this);
    }

    @Override
    public Class<V> returnType() {
        return returnType;
    }

    @Override
    public V getResult() throws Throwable {
        try {
            return get(timeout, TimeUnit.NANOSECONDS);
        } catch (TimeoutException e) {
            Response response = new Response(invokeId);
            response.setStatus(Status.CLIENT_TIMEOUT.value());
            DefaultInvokeFuture.fakeReceived(response);
        }
        return null;
    }

    /**
     * @param response
     */
    private void doReceived(Response response) {
        byte status = response.status();
        if (status == Status.OK.value()) {
            byte[] bytes = response.bytes();
            V v = serializer.deSerialize(bytes, returnType);
            complete(v);
        } else {
            setException(status, response);
        }
    }

    /**
     * set complete exception
     *
     * @param status
     * @param response
     */
    private void setException(byte status, Response response) {
        Throwable cause = new RuntimeException("todo exception"+status);
        //todo exception
        completeExceptionally(cause);
    }

    /**
     * @param ctx
     * @param response
     */
    public static void received(ChannelHandlerContext ctx, Response response) {
        long invokeId = response.id();
        DefaultInvokeFuture<?> future = roundFutures.remove(invokeId);
        future.doReceived(response);
    }

    /**
     * @param response
     */
    public static void fakeReceived(Response response) {
        long invokeId = response.id();
        DefaultInvokeFuture<?> future = roundFutures.remove(invokeId);
        future.doReceived(response);
    }
}
