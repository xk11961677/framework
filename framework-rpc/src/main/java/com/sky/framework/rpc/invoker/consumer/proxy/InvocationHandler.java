package com.sky.framework.rpc.invoker.consumer.proxy;

import java.lang.reflect.Method;

/**
 * @author
 */
public interface InvocationHandler {

    /**
     * Proxy
     *
     * @param proxy
     * @param method
     * @param args
     * @return Object
     * @throws Throwable
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}
