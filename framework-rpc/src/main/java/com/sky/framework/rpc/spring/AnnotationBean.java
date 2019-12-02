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
package com.sky.framework.rpc.spring;

import com.sky.framework.rpc.cluster.ClusterInvoker;
import com.sky.framework.rpc.cluster.loadbalance.LoadBalance;
import com.sky.framework.rpc.common.enums.ClusterEnum;
import com.sky.framework.rpc.common.enums.LoadBalanceEnum;
import com.sky.framework.rpc.common.enums.ProxyEnum;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.common.spi.SpiExchange;
import com.sky.framework.rpc.invoker.annotation.Provider;
import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import com.sky.framework.rpc.serializer.ObjectSerializer;
import com.sky.framework.rpc.spring.annotation.Reference;
import com.sky.framework.rpc.util.AopTargetUtils;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * 注入客户端代理与加载服务端
 * BeanDefinitionRegistryPostProcessor
 *
 * @author
 */
@Slf4j
public class AnnotationBean implements InitializingBean, BeanPostProcessor {

    static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    static final ConcurrentMap<String, ReferenceBean> referenceConfigs = new ConcurrentHashMap();

    static final ConcurrentMap<Class, Provider> providerConfigs = new ConcurrentHashMap();

    @Setter
    private String proxy;

    @Setter
    private String cluster;

    @Setter
    private String serializer;

    @Setter
    private String loadBalance;

    private String annotationPackage;

    private String[] annotationPackages;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.annotationPackage, "Property 'annotationPackage' is required");
        loadSpiSupport();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!isMatchPackage(bean)) {
            return bean;
        }
        Class targetClass = AopTargetUtils.getTarget(bean).getClass();
        this.autowireBeanByMethod(targetClass, bean);
        this.autowireBeanByField(targetClass, bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!isMatchPackage(bean)) {
            return bean;
        }
        Object target = AopTargetUtils.getTarget(bean);
        Provider annotation = AnnotationUtils.findAnnotation(target.getClass(), Provider.class);
        if (annotation != null) {
            ReflectAsmUtils.add(annotation, bean);
            providerConfigs.put(target.getClass(), annotation);
        }
        return bean;
    }

    /**
     * 通过field属性注入bean
     *
     * @param targetClass
     * @param bean
     */
    private void autowireBeanByField(Class targetClass, Object bean) {
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Reference reference = field.getAnnotation(Reference.class);
                if (reference != null) {
                    Object value = refer(reference, field.getType());
                    if (value != null) {
                        field.set(bean, value);
                    }
                }
            } catch (Throwable e) {
                log.error("Failed to init remote service reference at filed " + field.getName() + " in class " + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 通过set方法注入bean属性
     *
     * @param targetClass
     * @param bean
     */
    private void autowireBeanByMethod(Class targetClass, Object bean) {
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.length() > 3 && name.startsWith("set") && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                try {
                    Reference reference = method.getAnnotation(Reference.class);
                    if (reference != null) {
                        Object value = refer(reference, method.getParameterTypes()[0]);
                        if (value != null) {
                            method.invoke(bean, new Object[]{});
                        }
                    }
                } catch (Throwable e) {
                    log.error("failed to init remote service reference at method " + name + " in class " + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 返回接口代理对象
     *
     * @param reference
     * @param referenceClass
     * @return
     */
    private Object refer(Reference reference, Class<?> referenceClass) {
        if (!referenceClass.isInterface()) {
            throw new IllegalStateException("The @Reference undefined interfaceClass, and the property type " + referenceClass.getName() + " is not a interface.");
        }
        String interfaceName = referenceClass.getName();
        String key = interfaceName + ":" + reference.version();
        ReferenceBean referenceBean = referenceConfigs.get(key);
        if (referenceBean == null) {
            referenceBean = new ReferenceBean();
            referenceBean.setReference(reference);
            referenceBean.setInterfaceClass(referenceClass);
            referenceConfigs.putIfAbsent(key, referenceBean);
        }
        return referenceBean.getObject();
    }

    /**
     * 匹配包
     *
     * @param bean
     * @return
     */
    private boolean isMatchPackage(Object bean) {
        if (annotationPackages == null || annotationPackages.length == 0) {
            return true;
        }
        String beanClassName = AopTargetUtils.getTarget(bean).getClass().getName();
        for (String pkg : annotationPackages) {
            if (beanClassName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    private void loadSpiSupport() {
        SpiExchange instance = SpiExchange.getInstance();
        //加载proxy spi
        ProxyEnum proxyEnum = ProxyEnum.acquire(proxy);
        ProxyFactory proxyFactory = instance.loadSpiSupport(ProxyFactory.class, proxyEnum.getKey());
        instance.setProxyFactory(proxyFactory);

        //加载cluster spi
        ClusterEnum clusterEnum = ClusterEnum.acquire(cluster);
        ClusterInvoker invoker = instance.loadSpiSupport(ClusterInvoker.class, clusterEnum.getKey());
        instance.setClusterInvoker(invoker);

        //加载serialize spi
        SerializeEnum serializeEnum = SerializeEnum.acquire(serializer);
        ObjectSerializer objectSerializer = instance.loadSpiSupport(ObjectSerializer.class, serializeEnum.getSerialize());
        instance.setSerializer(objectSerializer);
        instance.setSerializerCode(serializeEnum.getSerializerCode());

        //加载loadBalance spi
        LoadBalanceEnum loadBalanceEnum = LoadBalanceEnum.acquire(loadBalance);
        LoadBalance loadBalance = instance.loadSpiSupport(LoadBalance.class, loadBalanceEnum.getKey());
        instance.setLoadBalance(loadBalance);
    }

    public void setAnnotationPackage(String annotationPackage) {
        this.annotationPackage = annotationPackage;
        this.annotationPackages = (annotationPackage == null || annotationPackage.length() == 0) ? null : COMMA_SPLIT_PATTERN.split(annotationPackage);
    }
}
