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
package com.sky.framework.rpc.invoker.consumer.proxy.javassist;

import com.sky.framework.rpc.invoker.consumer.proxy.AbstractProxyFactory;
import com.sky.framework.rpc.invoker.consumer.proxy.InvocationHandler;
import com.sky.framework.rpc.spring.annotation.Reference;
import com.sky.framework.rpc.util.SpiMetadata;
import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author
 */
@Slf4j
@SpiMetadata(name = "javassist")
public class JavassistProxyFactory extends AbstractProxyFactory {

    private static final String PROXY_CLASS_NAME_PREFIX = "$JavassistProxy";

    private static final AtomicLong nextUniqueNumber = new AtomicLong();

    private static final Map<String, Class<?>> proxyClassCache = new HashMap<>();

    public JavassistProxyFactory() {
    }

    private JavassistProxyFactory(Class<?> interfaceClass, Reference reference) {
        super.setInterfaceClass(interfaceClass);
        super.setReference(reference);
    }

    @Override
    public <T> T newInstance(Class<?> interfaceClass, Reference reference) {
        JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory(interfaceClass, reference);
        return (T) javassistProxyFactory.newInstance();
    }

    private <T> T newInstance() {
        JavassistProxy invocationHandler = new JavassistProxy(getInterfaceClass(), getReference());
        return newInstance(invocationHandler);
    }

    private <T> T newInstance(JavassistProxy invocationHandler) {
        return (T) newInstance(getInterfaceClass(), invocationHandler);
    }

    private Object newInstance(Class<?> targetClass, JavassistProxy invocationHandler) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 查看是否有缓存
            Class<?> proxyClass = getProxyClassCache(classLoader, targetClass);
            if (proxyClass != null) {
                // 实例化代理对象
                return proxyClass.getConstructor(JavassistProxy.class).newInstance(invocationHandler);
            }

            ClassPool pool = new ClassPool(true);
            pool.importPackage(InvocationHandler.class.getName());
            pool.importPackage(Method.class.getName());
            // 被代理类
            CtClass targetCtClass = pool.get(targetClass.getName());
            // 检查被代理类是否是 final的
            if ((targetCtClass.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                throw new IllegalArgumentException("class is final");
            }
            // 新建代理类
            CtClass proxyCtClass = pool.makeClass(generateProxyClassName(targetClass));
            // 设置描述符
            proxyCtClass.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
            // 设置实现关系
            proxyCtClass.setInterfaces(new CtClass[]{targetCtClass});
            // 添加构造器
            addConstructor(pool, proxyCtClass, invocationHandler);
            // 添加方法
            addMethod(pool, proxyCtClass, targetCtClass, targetClass);
            // 从指定ClassLoader加载Class,调用此方法后,proxyCtClass不能再被修改,需要调用defrost解冻
            proxyClass = proxyCtClass.toClass(classLoader, null);
            // 缓存
            saveProxyClassCache(classLoader, targetClass, proxyClass);
//            File outputFile = new File("/Users/sky/Desktop/");
//            proxyCtClass.writeFile(outputFile.getAbsolutePath());
            proxyCtClass.defrost();
            return proxyClass.getConstructor(JavassistProxy.class).newInstance(invocationHandler);
        } catch (Exception e) {
            log.error("rpc spring consumer generate javassist proxy instance fail:{}" + targetClass.getName(), e);
        }
        return null;
    }

    private void addMethod(ClassPool pool, CtClass proxyCtClass, CtClass targetCtClass, Class<?> targetClass) throws Exception {
        int methodNameIndex = 0;
        //添加getMethods
        methodNameIndex = addMethod(pool, proxyCtClass, targetCtClass, targetClass.getMethods(), targetCtClass.getMethods(), true, methodNameIndex);
        //添加getDeclaredMethods
        addMethod(pool, proxyCtClass, targetCtClass, targetClass.getDeclaredMethods(), targetCtClass.getDeclaredMethods(), false, methodNameIndex);
    }

    /**
     * 代理类添加方法，基于被代理类的共有方法。因为{@link CtClass#getMethods()} 和 {@link Class#getMethods()}返回的列表顺序不一样，所以需要做一次匹配
     *
     * @param pool            类加载pool
     * @param proxyCtClass    代理类
     * @param targetCtClass   被代理类
     * @param methods         被代理类的方法数组
     * @param ctMethods       被代理类的方法数组
     * @param isPublic        是否是共有方法，是：只包含public方法；否：包含projected和default方法
     * @param methodNameIndex 新建方法的命名下标
     * @return
     * @throws Exception
     */
    private int addMethod(ClassPool pool, CtClass proxyCtClass, CtClass targetCtClass, Method[] methods, CtMethod[] ctMethods, boolean isPublic, int methodNameIndex) throws Exception {
        for (int i = 0; i < ctMethods.length; i++) {
            CtMethod ctMethod = ctMethods[i];
            // final和static修饰的方法不能被继承
            if ((ctMethod.getModifiers() & Modifier.FINAL) == Modifier.FINAL
                    || (ctMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                continue;
            }
            // 满足指定的修饰符
            int modifyFlag = -1;
            if (isPublic) {
                // public 方法
                if ((ctMethod.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
                    modifyFlag = Modifier.PUBLIC;
                }
            } else {
                // protected 方法
                if ((ctMethod.getModifiers() & Modifier.PROTECTED) == Modifier.PROTECTED) {
                    modifyFlag = Modifier.PROTECTED;
                } else if ((ctMethod.getModifiers() & Modifier.PUBLIC) == 0
                        && (ctMethod.getModifiers() & Modifier.PROTECTED) == 0
                        && (ctMethod.getModifiers() & Modifier.PRIVATE) == 0) {
                    modifyFlag = 0;
                }
            }
            if (modifyFlag == -1) {
                continue;
            }

            // 匹配对应的方法
            int methodIndex = findSomeMethod(methods, ctMethod);
            if (methodIndex == -1) {
                continue;
            }
            // 将这个方法作为字段保存，便于新增的方法能够访问原来的方法
            String code = null;
            if (isPublic) {
                code = String.format("private static Method method%d = Class.forName(\"%s\").getMethods()[%d];",
                        methodNameIndex, targetCtClass.getName(), methodIndex);
            } else {
                code = String.format("private static Method method%d = Class.forName(\"%s\").getDeclaredMethods()[%d];",
                        methodNameIndex, targetCtClass.getName(), methodIndex);
            }
            CtField field = CtField.make(code, proxyCtClass);
            proxyCtClass.addField(field);

            CtMethod newCtMethod = new CtMethod(ctMethod.getReturnType(), ctMethod.getName(), ctMethod.getParameterTypes(), proxyCtClass);
            // 区分静态与非静态，主要就是对象是否传null。注意这里必须用($r)转换类型，否则会发生类型转换失败的问题
            if ((ctMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                code = String.format("return ($r)invocationHandler.invoke(null, method%d, $args);", methodNameIndex);
            } else {
                code = String.format("return ($r)invocationHandler.invoke(this, method%d, $args);", methodNameIndex);
            }
            newCtMethod.setBody(code);
            newCtMethod.setModifiers(modifyFlag);
            //添加方法异常
            addMethodThrowable(pool, newCtMethod, methods, methodIndex);
            //将方法加入到代理类中
            proxyCtClass.addMethod(newCtMethod);
            methodNameIndex++;
        }
        return methodNameIndex;
    }

    /**
     * 添加方法抛出的异常
     *
     * @param pool
     * @param newCtMethod
     * @param methods
     * @param methodIndex
     * @throws Exception
     */
    private void addMethodThrowable(ClassPool pool, CtMethod newCtMethod, Method[] methods, int methodIndex) throws Exception {
        //添加抛出的异常
        Class<?>[] exceptionTypes = methods[methodIndex].getExceptionTypes();
        if (exceptionTypes.length != 0) {
            CtClass[] ctClassesTypes = new CtClass[exceptionTypes.length];
            for (int m = 0; m < exceptionTypes.length; m++) {
                ctClassesTypes[m] = pool.get(exceptionTypes[m].getName());
            }
            newCtMethod.setExceptionTypes(ctClassesTypes);
        }
    }

    /**
     * 添加构造方法
     *
     * @param pool
     * @param proxyClass
     * @param invocationHandler
     * @throws Exception
     */
    private void addConstructor(ClassPool pool, CtClass proxyClass, InvocationHandler invocationHandler) throws Exception {
        // 添加 invocationHandler 字段
        CtField field = CtField.make("private InvocationHandler invocationHandler = null;", proxyClass);
        proxyClass.addField(field);
        CtClass invocationHandlerCtClass = pool.get(invocationHandler.getClass().getName());
        CtConstructor newConstructor = new CtConstructor(new CtClass[]{invocationHandlerCtClass}, proxyClass);
        StringBuilder sb = new StringBuilder();
        String code = String.format("{this.invocationHandler = $1;}", sb.toString());
        newConstructor.setBody(code);
        proxyClass.addConstructor(newConstructor);
    }

    /**
     * 生成代理类名称
     *
     * @param targetClass
     * @return
     */
    private String generateProxyClassName(Class<?> targetClass) {
        String name = targetClass.getName() + PROXY_CLASS_NAME_PREFIX + nextUniqueNumber.getAndIncrement();
        return name;
    }

    /**
     * 缓存已经生成的代理类的Class，key值根据 classLoader 和 targetClass 共同决定
     */
    private static void saveProxyClassCache(ClassLoader classLoader, Class<?> targetClass, Class<?> proxyClass) {
        String key = classLoader.toString() + "_" + targetClass.getName();
        proxyClassCache.put(key, proxyClass);
    }

    /**
     * 从缓存中取得代理类的Class，如果没有则返回 null
     */
    private static Class<?> getProxyClassCache(ClassLoader classLoader, Class<?> targetClass) {
        String key = classLoader.toString() + "_" + targetClass.getName();
        return proxyClassCache.get(key);
    }


    /**
     * 从 methods 找到等于 ctMethod 的下标索引并返回。找不到则返回 -1
     */
    private static int findSomeMethod(Method[] methods, CtMethod ctMethod) {
        for (int i = 0; i < methods.length; i++) {
            if (equalsMethod(methods[i], ctMethod)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 判断{@link Method} 和 {@link CtMethod} 是否相等。主要从方法名、返回值类型、参数类型三个维度判断
     */
    private static boolean equalsMethod(Method method, CtMethod ctMethod) {
        if (method == null && ctMethod == null) {
            return true;
        }
        if (method == null || ctMethod == null) {
            return false;
        }
        try {
            if (method.getName().equals(ctMethod.getName())
                    && method.getReturnType().getName().equals(ctMethod.getReturnType().getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                CtClass[] parameterTypesCt = ctMethod.getParameterTypes();
                if (parameterTypes.length != parameterTypesCt.length) {
                    return false;
                }
                boolean equals = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (!parameterTypes[i].getName().equals(parameterTypesCt[i].getName())) {
                        equals = false;
                        break;
                    }
                }
                return equals;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
