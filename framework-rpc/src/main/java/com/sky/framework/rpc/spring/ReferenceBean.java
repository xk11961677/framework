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

import org.springframework.beans.factory.FactoryBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端代理FactoryBean
 * scope  singleton
 *
 * @author
 */
public class ReferenceBean<T> extends ReferenceConfig implements FactoryBean<T> {

    private static final ConcurrentHashMap<String, Object> PROXY_INSTANCE_CACHE = new ConcurrentHashMap<>();

    @Override
    public T getObject() {
        if (!isSingleton()) {
            return get();
        }
        String key = getObjectType().getName() + ":" + getReference().version();
        Object obj = PROXY_INSTANCE_CACHE.get(key);
        if (obj == null) {
            PROXY_INSTANCE_CACHE.putIfAbsent(key, super.get());
        }
        return (T) PROXY_INSTANCE_CACHE.get(key);
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


}
