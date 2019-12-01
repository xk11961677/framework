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
package com.sky.framework.rpc.util;

import cn.hutool.core.lang.Pair;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.sky.framework.rpc.invoker.annotation.Provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class ReflectAsmUtils {

    private static Map<String, Pair<MethodAccess, Object>> reflectAsmMapping = new ConcurrentHashMap<>();

    /**
     * 通过反射调用业务类
     *
     * @param serviceProviderName
     * @param method
     * @param parameterTypes
     * @param arguments
     * @return
     */
    public static Object invoke(String serviceProviderName, String method, Class<?>[] parameterTypes, Object[] arguments) {
        Pair<MethodAccess, Object> pair = reflectAsmMapping.get(serviceProviderName);
        MethodAccess access = pair.getKey();
        int index = access.getIndex(method, parameterTypes);
        Object result = access.invoke(pair.getValue(), index, arguments);
        return result;
    }

    public static void add(Provider provider, Object bean) {
        MethodAccess access = MethodAccess.get(bean.getClass());
        reflectAsmMapping.put(provider.name(), new Pair<>(access, bean));
    }

    public static Map<String, Pair<MethodAccess, Object>> getReflectAsmMapping() {
        return reflectAsmMapping;
    }
}
