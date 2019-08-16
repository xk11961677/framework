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

import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import javassist.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author
 */
@Slf4j
public class JavassistProxyFactory implements ProxyFactory {

    private static final String proxyClassNamePrefix = "$JavassistProxy";

    private static final AtomicLong nextUniqueNumber = new AtomicLong();

    @Getter
    @Setter
    private Class<?> interfaceClass;

    public JavassistProxyFactory() {
    }

    private JavassistProxyFactory(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String getScheme() {
        return "javassist";
    }

    @Override
    public <T> T newInstance(Class<?> interfaceClass) {
        JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory(interfaceClass);
        return (T) javassistProxyFactory.newInstance();
    }

    private <T> T newInstance(JavassistProxy javassistProxy) {
        return (T) newInstance(interfaceClass, javassistProxy);
    }

    private <T> T newInstance() {
        JavassistProxy javassistProxy = new JavassistProxy(interfaceClass);
        return newInstance(javassistProxy);
    }


    private static Object newInstance(Class<?> clazz, JavassistProxy javassistProxy) {
        try {
            String name = clazz.getName() + proxyClassNamePrefix + nextUniqueNumber.getAndIncrement();

            ClassPool pool = ClassPool.getDefault();

            ClassClassPath classPath = new ClassClassPath(JavassistProxyFactory.class);

            pool.insertClassPath(classPath);
            try {
                CtClass ctClass = pool.get(name);
                return ctClass.toClass().getConstructor(JavassistProxy.class).newInstance(javassistProxy);
            } catch (NotFoundException e) {
            }


            Method[] methods = clazz.getDeclaredMethods();
            CtClass targetClass = pool.makeClass(name);
            targetClass.setInterfaces(new CtClass[]{pool.get(clazz.getName())});

            CtClass javassistProxyCtClass = pool.get(javassistProxy.getClass().getName());

            CtField ctField = new CtField(javassistProxyCtClass, "h", targetClass);
            ctField.setModifiers(Modifier.PRIVATE);
            targetClass.addField(ctField);

            CtConstructor cons = new CtConstructor(new CtClass[]{javassistProxyCtClass}, targetClass);
            cons.setBody("{$0.h = $1;}");
            targetClass.addConstructor(cons);

            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                String fieldSrc = String.format("private static java.lang.reflect.Method method%d = Class.forName(\"%s\").getDeclaredMethods()[%d];", i, targetClass.getName(), i);
                CtField methodField = CtField.make(fieldSrc, targetClass);
                targetClass.addField(methodField);

                String methodName = method.getName();
                Parameter[] parameters = method.getParameters();
                CtClass[] ctClasses = new CtClass[parameters.length];
                for (int j = 0; j < parameters.length; j++) {
                    ctClasses[j] = pool.get(parameters[j].getType().getName());
                }
                CtMethod ctMethod = new CtMethod(pool.get(method.getReturnType().getName()), methodName, ctClasses, targetClass);
                StringBuilder sb = new StringBuilder("{ ");
                sb.append("return ($r)h.invoke(");
                sb.append("$0").append(",").append("method" + i + "");
                sb.append(",");
                sb.append("$args);}");
                ctMethod.setBody(sb.toString());

                Class<?>[] exceptionTypes = method.getExceptionTypes();
                CtClass[] ctClassesTypes = new CtClass[exceptionTypes.length];
                for (int m = 0; m < exceptionTypes.length; m++) {
                    ctClassesTypes[m] = pool.get(exceptionTypes[m].getName());
                }
                ctMethod.setExceptionTypes(ctClassesTypes);
                targetClass.addMethod(ctMethod);
            }
            return targetClass.toClass().getConstructor(JavassistProxy.class).newInstance(javassistProxy);
        } catch (Exception e) {
            log.error("rpc spring consumer generate javassist proxy instance fail:{}" + clazz.getName(), e);
        }
        return null;
    }

}
