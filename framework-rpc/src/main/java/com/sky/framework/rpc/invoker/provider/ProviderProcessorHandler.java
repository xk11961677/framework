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
package com.sky.framework.rpc.invoker.provider;


import com.sky.framework.rpc.invoker.AbstractProcessor;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.ObjectSerializer;
import com.sky.framework.rpc.serializer.SerializerHolder;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.core.CommonThreadPool;
import com.sky.framework.threadpool.core.DefaultAsynchronousHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class ProviderProcessorHandler extends AbstractProcessor {

    private static ProviderProcessorHandler instance = new ProviderProcessorHandler();

    private ProviderProcessorHandler() {
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Request request) {
        CommonThreadPool.execute(new DefaultAsynchronousHandler() {

            @Override
            public Object call() throws Exception {
                Response response = new Response(request.getId());
                response.setStatus(Status.OK.value());
                response.setSerializerCode(request.getSerializerCode());
                try {
                    ObjectSerializer serializer = SerializerHolder.getInstance().getSerializer(request.getSerializerCode());

                    byte[] bytes = request.bytes();
                    RpcInvocation rpcInvocation = serializer.deSerialize(bytes, RpcInvocation.class);

                    Object result = ReflectAsmUtils.invoke(rpcInvocation.getClazzName(),
                            rpcInvocation.getMethodName(),
                            rpcInvocation.getParameterTypes(), rpcInvocation.getArguments());

                    byte[] body = serializer.serialize(result);
                    response.bytes(body);

                } catch (Exception e) {
                    response.setStatus(Status.SERVER_ERROR.value());
                    log.error("the server exception :{}", e);
                } finally {
                    Channel channel = ctx.channel();
                    if (channel.isActive() && channel.isWritable()) {
                        channel.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                            log.info("the server response completed:{}");
                        });
                    }
                }
                return null;
            }
        });
    }


    public static ProviderProcessorHandler getInstance() {
        return instance;
    }


}
