package com.sky.framework.rpc.invoker.consumer;

import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.AbstractProcessor;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.invoker.future.DefaultInvokeFuture;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.serializer.FastjsonSerializer;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.CommonThreadPool;
import com.sky.framework.threadpool.IAsynchronousHandler;
import io.netty.channel.Channel;

/**
 * @author
 */
public class ConsumerProcessorHandler extends AbstractProcessor {

    public static final ConsumerProcessorHandler instance = new ConsumerProcessorHandler();

    static {
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }

    private FastjsonSerializer fastjsonSerializer = new FastjsonSerializer();

    public ConsumerProcessorHandler() {
    }

    @Override
    public void handler(Channel channel, Response response) {
        CommonThreadPool.execute(new IAsynchronousHandler() {
            @Override
            public void executeAfter(Throwable t) {
            }

            @Override
            public void executeBefore(Thread t) {
                //todo build a wrapper response object
                SerializeEnum acquire = SerializeEnum.acquire(response.serializerCode());
                byte[] bytes = response.bytes();
            }

            @Override
            public Object call() throws Exception {
                DefaultInvokeFuture.received(channel, response);
                return null;
            }
        });
    }
}
