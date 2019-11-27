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


import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.AbstractProcessor;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.FastJsonSerializer;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.core.CommonThreadPool;
import com.sky.framework.threadpool.core.IAsynchronousHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class ProviderProcessorHandler extends AbstractProcessor {

    static {
        instance = new ProviderProcessorHandler();
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }

    private FastJsonSerializer fastjsonSerializer = new FastJsonSerializer();

    private static ProviderProcessorHandler instance;

    public ProviderProcessorHandler() {
    }

    @Override
    public void handler(ChannelHandlerContext ctx, Request request) {
        CommonThreadPool.execute(new IAsynchronousHandler() {

            private RpcInvocation rpcInvocation = null;

            private Object result = null;

            @Override
            public void executeAfter(Throwable t) {
                Response response = new Response(request.getId());
                response.setStatus(Status.OK.value());
                byte[] serialize = fastjsonSerializer.serialize(result);
                response.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
                ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    log.info("the server response completion:{}");
                });
                rpcInvocation = null;
                result = null;
            }

            @Override
            public void executeBefore(Thread t) {
                //todo default fastJson
                SerializeEnum serialize = SerializeEnum.acquire(request.serializerCode());
                byte[] bytes = request.bytes();
                rpcInvocation = fastjsonSerializer.deSerialize(bytes, RpcInvocation.class);
            }

            @Override
            public Object call() throws Exception {
                try {
                    result = ReflectAsmUtils.invoke(rpcInvocation.getClazzName(), rpcInvocation.getMethodName(),
                            rpcInvocation.getParameterTypes(), rpcInvocation.getArguments());
                } catch (Exception e) {
                    log.error("the provider reflect exception !", e.getMessage());
                }
                return null;
            }
        });
    }


    public static ProviderProcessorHandler getInstance() {
        return instance;
    }
}
