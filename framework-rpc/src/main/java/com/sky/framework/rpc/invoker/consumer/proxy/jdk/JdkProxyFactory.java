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
package com.sky.framework.rpc.invoker.consumer.proxy.jdk;

import com.sky.framework.rpc.invoker.consumer.proxy.AbstractProxyFactory;
import com.sky.framework.rpc.spring.annotation.Reference;
import com.sky.framework.rpc.util.SpiMetadata;

import java.lang.reflect.Proxy;

/**
 * @author
 */
@SpiMetadata(name = "jdk")
public class JdkProxyFactory extends AbstractProxyFactory {


    public JdkProxyFactory() {
    }

    private JdkProxyFactory(Class<?> interfaceClass, Reference reference) {
        super.setInterfaceClass(interfaceClass);
        super.setReference(reference);
    }

    @Override
    public <T> T newInstance(Class<?> interfaceClass, Reference reference) {
        JdkProxyFactory jdkProxyFactory = new JdkProxyFactory(interfaceClass, reference);
        return (T) jdkProxyFactory.newInstance();
    }

    private <T> T newInstance() {
        JdkProxy jdkProxy = new JdkProxy(getInterfaceClass(), getReference());
        return newInstance(jdkProxy);
    }

    private <T> T newInstance(JdkProxy jdkProxy) {
        return (T) Proxy.newProxyInstance(JdkProxyFactory.class.getClassLoader(), new Class[]{getInterfaceClass()}, jdkProxy);
    }


}
