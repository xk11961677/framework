package com.sky.framework.rpc.invoker.consumer.proxy.jdk;


import com.sky.framework.rpc.invoker.consumer.proxy.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author
 */
public class JdkProxy extends Proxy implements InvocationHandler {

    public JdkProxy(Class<?> interfaceClass) {
        super(interfaceClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return remoteCall(method, args);
    }


}
