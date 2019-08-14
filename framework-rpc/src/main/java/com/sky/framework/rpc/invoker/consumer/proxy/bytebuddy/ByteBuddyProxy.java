package com.sky.framework.rpc.invoker.consumer.proxy.bytebuddy;

import com.sky.framework.rpc.invoker.consumer.proxy.Proxy;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author
 */
public class ByteBuddyProxy extends Proxy implements InvocationHandler {

    public ByteBuddyProxy(Class<?> interfaceClass) {
        super(interfaceClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return remoteCall(method, args);
    }


    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return invoke(proxy, method, args);
    }


}
