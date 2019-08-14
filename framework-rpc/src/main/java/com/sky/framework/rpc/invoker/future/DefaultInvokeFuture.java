package com.sky.framework.rpc.invoker.future;

import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.FastjsonSerializer;
import io.netty.channel.Channel;

import java.util.concurrent.*;

/**
 * @author
 */
public class DefaultInvokeFuture<V> extends CompletableFuture<V> implements InvokeFuture<V> {

    private static final ConcurrentMap<Long, DefaultInvokeFuture<?>> roundFutures = new ConcurrentHashMap<>();

    private static final long DEFAULT_TIMEOUT_NANOSECONDS = TimeUnit.MILLISECONDS.toNanos(30000);

    private static final FastjsonSerializer serializer = new FastjsonSerializer();

    private final long invokeId;

    private final Channel channel;

    private final Class<V> returnType;

    private final long timeout;

    private final long startTime = System.nanoTime();

    public static <T> DefaultInvokeFuture<T> with(
            long invokeId, Channel channel, long timeoutMillis, Class<T> returnType) {

        return new DefaultInvokeFuture<>(invokeId, channel, timeoutMillis, returnType);
    }

    private DefaultInvokeFuture(long invokeId, Channel channel, long timeoutMillis, Class<V> returnType) {
        this.invokeId = invokeId;
        this.channel = channel;
        this.timeout = timeoutMillis > 0 ? TimeUnit.MILLISECONDS.toNanos(timeoutMillis) : DEFAULT_TIMEOUT_NANOSECONDS;
        this.returnType = returnType;
        roundFutures.put(invokeId, this);
    }

    public Channel channel() {
        return channel;
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
            response.setStatus(Status.CLIENT_ERROR.value());
            DefaultInvokeFuture.fakeReceived(channel, response);
            //todo
            //throw new RuntimeException("the client get result timeout");
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
        Throwable cause = null;
        //todo exception
        completeExceptionally(cause);
    }

    /**
     * @param channel
     * @param response
     */
    public static void received(Channel channel, Response response) {
        long invokeId = response.id();
        DefaultInvokeFuture<?> future = roundFutures.remove(invokeId);
        future.doReceived(response);
    }

    /**
     * @param channel
     * @param response
     */
    public static void fakeReceived(Channel channel, Response response) {
        long invokeId = response.id();
        DefaultInvokeFuture<?> future = roundFutures.remove(invokeId);
        future.doReceived(response);
    }
}
