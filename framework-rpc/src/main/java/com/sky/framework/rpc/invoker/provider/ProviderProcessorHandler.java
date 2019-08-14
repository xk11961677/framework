package com.sky.framework.rpc.invoker.provider;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.AbstractProcessor;
import com.sky.framework.rpc.invoker.Processor;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.FastjsonSerializer;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.CommonThreadPool;
import com.sky.framework.threadpool.IAsynchronousHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class ProviderProcessorHandler extends AbstractProcessor {

    public static final ProviderProcessorHandler instance = new ProviderProcessorHandler();

    static {
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }

    private FastjsonSerializer fastjsonSerializer = new FastjsonSerializer();

    public ProviderProcessorHandler() {
    }

    @Override
    public void handler(Channel channel, Request request) {
        CommonThreadPool.execute(new IAsynchronousHandler() {

            private RpcInvocation rpcInvocation = null;

            private Object result = null;

            @Override
            public void executeAfter(Throwable t) {
                Response response = new Response(request.getId());
                response.setStatus(Status.OK.value());
                byte[] serialize = fastjsonSerializer.serialize(result);
                response.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
                channel.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    System.out.println("write completion");
                });
            }

            @Override
            public void executeBefore(Thread t) {
                //default fastjson
                SerializeEnum acquire = SerializeEnum.acquire(request.serializerCode());
                byte[] bytes = request.bytes();
                rpcInvocation = fastjsonSerializer.deSerialize(bytes, RpcInvocation.class);
            }

            @Override
            public Object call() throws Exception {
                try {
                    result = ReflectAsmUtils.invoke(rpcInvocation.getClazzName(), rpcInvocation.getMethodName(),
                            rpcInvocation.getParameterTypes(), rpcInvocation.getArguments());
                } catch (Exception e) {
                    LogUtils.error(log, "the provider reflect has error !", e.getMessage());
                }
                return null;
            }
        });
    }
}
