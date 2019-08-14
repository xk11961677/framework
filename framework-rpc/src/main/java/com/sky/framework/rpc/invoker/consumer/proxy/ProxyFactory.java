package com.sky.framework.rpc.invoker.consumer.proxy;


/**
 * @author
 */
public interface ProxyFactory {

    String getScheme();

    <T> T newInstance(Class<?> interfaceClass);

}
