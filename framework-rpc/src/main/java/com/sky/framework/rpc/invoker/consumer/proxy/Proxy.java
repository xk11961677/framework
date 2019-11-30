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


import com.sky.framework.rpc.cluster.ClusterInvoker;
import com.sky.framework.rpc.cluster.FailoverClusterInvoker;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.invoker.consumer.Dispatcher;
import com.sky.framework.rpc.invoker.consumer.InvokerDispatcher;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.serializer.FastJsonSerializer;
import com.sky.framework.rpc.spring.annotation.Reference;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * todo 每个代理类都会实例化,需要调整成单例
 * serializer
 * dispatcher
 * invoker
 *
 * @author
 */
@Slf4j
public class Proxy {

    //todo spi extension
    private FastJsonSerializer serializer = new FastJsonSerializer();

    private Dispatcher dispatcher = new InvokerDispatcher();

    //todo spi extension
    private ClusterInvoker invoker = new FailoverClusterInvoker(dispatcher);

    private Class<?> interfaceClass;

    private Reference reference;

    public Proxy(Class<?> interfaceClass, Reference reference) {
        this.interfaceClass = interfaceClass;
        this.reference = reference;
    }

    public Object remoteCall(Method method, Object[] args) {
        RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
        serviceMeta.setGroup(reference.group());
        serviceMeta.setServiceProviderName(interfaceClass.getName());
        serviceMeta.setVersion(reference.version());

        Request request = new Request();
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setClazzName(interfaceClass.getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setParameterTypes(method.getParameterTypes());
        rpcInvocation.setArguments(args);

        byte[] serialize = serializer.serialize(rpcInvocation);
        request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);

        Object result = invoker.invoke(request, serviceMeta, method.getReturnType());

        return result;
    }


}
