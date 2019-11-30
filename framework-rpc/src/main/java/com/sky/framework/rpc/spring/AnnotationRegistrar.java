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

import com.sky.framework.rpc.invoker.annotation.Provider;
import com.sky.framework.rpc.spring.annotation.EnableRPC;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 注解注册器
 *
 * @author
 */
public class AnnotationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableRPC.class.getName(), false));
        String scans = (String) attributes.get("scan");
        String proxy = (String) attributes.get("proxy");

        addRpcAnnotationBean(registry, scans, proxy);
        addRpcDefinitionScanner(registry, scans);
    }

    /**
     * 增加拦截RPC注解业务BEAN
     *
     * @param registry
     * @param scans
     * @param proxy
     */
    private void addRpcAnnotationBean(BeanDefinitionRegistry registry, String scans, String proxy) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AnnotationBean.class);
        builder.addPropertyValue("annotationPackage", scans);
        builder.addPropertyValue("proxy", proxy);
        registry.registerBeanDefinition(AnnotationBean.class.getName(), builder.getRawBeanDefinition());
    }

    /**
     * 增加自动扫描注解
     *
     * @param registry
     * @param scans
     */
    private void addRpcDefinitionScanner(BeanDefinitionRegistry registry, String scans) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        TypeFilter includeFilter = new AnnotationTypeFilter(Provider.class);
        scanner.addIncludeFilter(includeFilter);
        scanner.scan((scans == null || scans.length() == 0) ? null : AnnotationBean.COMMA_SPLIT_PATTERN.split(scans));
    }
}
