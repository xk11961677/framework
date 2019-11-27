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
package com.sky.framework.rpc.invoker.consumer.proxy;


import com.sky.framework.rpc.cluster.loadbalance.LoadBalance;
import com.sky.framework.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.invoker.annotation.Consumer;
import com.sky.framework.rpc.invoker.future.DefaultInvokeFuture;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPool;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPoolFactory;
import com.sky.framework.rpc.remoting.protocol.LongSequence;
import com.sky.framework.rpc.serializer.FastJsonSerializer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author
 */
@Slf4j
public class Proxy {

    private FastJsonSerializer serializer = new FastJsonSerializer();

    private LongSequence longSequence = new LongSequence();

    private Class<?> interfaceClass;

    public Proxy(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Object remoteCall(Method method, Object[] args) throws Throwable {
        // todo cluster
        Consumer annotation = interfaceClass.getAnnotation(Consumer.class);
        RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
        serviceMeta.setGroup(annotation.group());
        serviceMeta.setServiceProviderName(interfaceClass.getName());
        serviceMeta.setVersion(annotation.version());

        LoadBalance instance = RoundRobinLoadBalance.getInstance();
        RegisterMeta.Address select = instance.select(serviceMeta);
        ChannelGenericPool channelGenericPool = ChannelGenericPoolFactory.getClientPoolMap().get(select);
        Channel channel = null;
        DefaultInvokeFuture invokeFuture = null;
        try {
            channel = channelGenericPool.getConnection();
            invokeFuture = $invoke(channel, method, args);
        } finally {
            channelGenericPool.releaseConnection(channel);
        }
        Object result = invokeFuture.getResult();
        return result;
    }

    /**
     * doInvoke
     *
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    private DefaultInvokeFuture $invoke(Channel channel, Method method, Object[] args) throws Exception {
        DefaultInvokeFuture<?> invokeFuture = null;

        Method interfaceMethod = interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        long id = longSequence.next();
        try {
            Request request = new Request(id);
            RpcInvocation rpcInvocation = new RpcInvocation();
            rpcInvocation.setClazzName(interfaceClass.getName());
            rpcInvocation.setMethodName(interfaceMethod.getName());
            rpcInvocation.setParameterTypes(interfaceMethod.getParameterTypes());
            rpcInvocation.setArguments(args);

            byte[] serialize = serializer.serialize(rpcInvocation);
            request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
            invokeFuture = DefaultInvokeFuture.with(request.getId(), 0, method.getReturnType());
            channel.writeAndFlush(request);
        } catch (Exception e) {
            log.error("the client proxy invoke failed:{}", e.getMessage());
            Response response = new Response(id);
            response.setStatus(Status.CLIENT_ERROR.value());
            DefaultInvokeFuture.fakeReceived(response);
        }
        return invokeFuture;
    }
}
