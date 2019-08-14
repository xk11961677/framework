package com.sky.framework.rpc.invoker.consumer.proxy.jdk;

import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Proxy;

/**
 * @author
 */
public class JdkProxyFactory implements ProxyFactory {


    @Getter
    @Setter
    private Class<?> interfaceClass;

    public JdkProxyFactory() {
    }

    private JdkProxyFactory(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String getScheme() {
        return "jdk";
    }

    @Override
    public <T> T newInstance(Class<?> interfaceClass) {
        JdkProxyFactory jdkProxyFactory = new JdkProxyFactory(interfaceClass);
        return (T) jdkProxyFactory.newInstance();
    }

    private <T> T newInstance() {
        JdkProxy jdkProxy = new JdkProxy(interfaceClass);
        return newInstance(jdkProxy);
    }

    private <T> T newInstance(JdkProxy jdkProxy) {
        return (T) Proxy.newProxyInstance(JdkProxyFactory.class.getClassLoader(), new Class[]{interfaceClass}, jdkProxy);
    }

}
