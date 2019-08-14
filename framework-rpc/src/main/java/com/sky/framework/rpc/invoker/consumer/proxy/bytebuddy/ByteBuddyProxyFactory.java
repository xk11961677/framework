package com.sky.framework.rpc.invoker.consumer.proxy.bytebuddy;

import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author
 */
@Slf4j
public class ByteBuddyProxyFactory implements ProxyFactory {
    @Override
    public String getScheme() {
        return "byteBuddy";
    }

    @Override
    public <T> T newInstance(Class<?> interfaceClass) {
        ByteBuddyProxy byteBuddyProxy = new ByteBuddyProxy(interfaceClass);
        Class<?> cls = new ByteBuddy()
                .subclass(interfaceClass)
                .method(ElementMatchers.isDeclaredBy(interfaceClass))
                .intercept(MethodDelegation.to(byteBuddyProxy, "handler"))
                .make()
                .load(interfaceClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        return newInstance(interfaceClass, cls);
    }

    public <T> T newInstance(Class<?> interfaceClass, Class<?> cls) {
        try {
            return (T) cls.newInstance();
        } catch (Exception e) {
            log.error("rpc spring consumer generate bytebuddy proxy instance fail:{}" + interfaceClass.getName(), e);
        }
        return null;
    }

}
