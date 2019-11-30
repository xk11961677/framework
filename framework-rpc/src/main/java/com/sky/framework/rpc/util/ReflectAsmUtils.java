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

import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class ReflectAsmUtils {

    public static Map<String, Class> clazzMap = new ConcurrentHashMap<>();

    public static Map<Class, MethodAccess> accessMap = new ConcurrentHashMap<>();

    public static Map<Class, Object> objectMap = new ConcurrentHashMap<>();

    /**
     * @param clazz
     * @param method
     * @param parameterTypes
     * @param arguments
     * @return
     */
    public static Object invoke(String clazz, String method, Class<?>[] parameterTypes, Object[] arguments) {
        Class aClass = clazzMap.get(clazz);
        MethodAccess access = accessMap.get(aClass);
        Object target = objectMap.get(aClass);
        int index = access.getIndex(method, parameterTypes);
        Object result = access.invoke(target, index, arguments);
        return result;
    }

    public static void add(Class<?> clazz, Object bean) {
        MethodAccess access = MethodAccess.get(clazz);
        accessMap.put(clazz, access);
        clazzMap.put(clazz.getName(), clazz);
        objectMap.put(clazz, bean);
    }

    /*public static void add(Object bean) {
        Class clazz = AopTargetUtils.getTargetClass(bean);
        MethodAccess access = MethodAccess.get(clazz);
        accessMap.put(clazz, access);
        clazzMap.put(clazz.getName(), clazz);
        objectMap.put(clazz, bean);
    }*/
}
