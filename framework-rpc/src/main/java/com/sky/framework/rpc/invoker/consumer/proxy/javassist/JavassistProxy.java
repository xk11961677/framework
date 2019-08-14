package com.sky.framework.rpc.invoker.consumer.proxy.javassist;

import com.sky.framework.rpc.invoker.consumer.proxy.InvocationHandler;
import com.sky.framework.rpc.invoker.consumer.proxy.Proxy;

import java.lang.reflect.Method;

/**
 * @author
 */
public class JavassistProxy extends Proxy implements InvocationHandler {

    public JavassistProxy(Class<?> interfaceClass) {
        super(interfaceClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return remoteCall(method, args);
    }
}
