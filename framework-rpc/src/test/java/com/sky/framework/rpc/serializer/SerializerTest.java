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
package com.sky.framework.rpc.serializer;

import com.sky.framework.rpc.BaseApplicationTests;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.example.User;
import com.sky.framework.rpc.example.UserService;
import com.sky.framework.rpc.example.UserServiceImpl;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.invoker.annotation.Provider;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class SerializerTest extends BaseApplicationTests {

    @Before
    public void build() {
        Provider annotation = UserServiceImpl.class.getAnnotation(Provider.class);
        ReflectAsmUtils.add(annotation, new UserServiceImpl());
    }

    @Test
    public void test4() throws Exception {
        test1();
        test2();
        test3();
    }

    @Test
    public void test1() throws Exception {
        Class<UserService> interfaceClass = UserService.class;
        Method method = interfaceClass.getMethod("getUser", User.class);
        User user = User.builder().id(1L).name("sky").build();
        Request request = new Request();
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setClazzName(interfaceClass.getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setParameterTypes(method.getParameterTypes());
        rpcInvocation.setArguments(new Object[]{user});

        FastJsonSerializer serializer = new FastJsonSerializer();
        byte[] serialize = serializer.serialize(rpcInvocation);
        request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
        RpcInvocation rpcInvocationD = serializer.deSerialize(request.bytes(), RpcInvocation.class);

        Object result = ReflectAsmUtils.invoke(rpcInvocationD.getClazzName(),
                rpcInvocationD.getMethodName(),
                rpcInvocationD.getParameterTypes(), rpcInvocationD.getArguments());

    }

    @Test
    public void test2() throws Exception {
        Class<UserService> interfaceClass = UserService.class;
        Method method = interfaceClass.getMethod("hello", String.class);
        Request request = new Request();
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setClazzName(interfaceClass.getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setParameterTypes(method.getParameterTypes());
        rpcInvocation.setArguments(new Object[]{"456"});

        FastJsonSerializer serializer = new FastJsonSerializer();
        byte[] serialize = serializer.serialize(rpcInvocation);
        request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
        RpcInvocation rpcInvocationD = serializer.deSerialize(request.bytes(), RpcInvocation.class);

        Object result = ReflectAsmUtils.invoke(rpcInvocationD.getClazzName(),
                rpcInvocationD.getMethodName(),
                rpcInvocationD.getParameterTypes(), rpcInvocationD.getArguments());
    }


    @Test
    public void test3() throws Exception {
        Class<UserService> interfaceClass = UserService.class;
        Method method = interfaceClass.getMethod("hello");
        Request request = new Request();
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setClazzName(interfaceClass.getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setParameterTypes(method.getParameterTypes());
        rpcInvocation.setArguments(new Object[]{});

        FastJsonSerializer serializer = new FastJsonSerializer();
        byte[] serialize = serializer.serialize(rpcInvocation);
        request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
        RpcInvocation rpcInvocationD = serializer.deSerialize(request.bytes(), RpcInvocation.class);

        Object result = ReflectAsmUtils.invoke(rpcInvocationD.getClazzName(),
                rpcInvocationD.getMethodName(),
                rpcInvocationD.getParameterTypes(), rpcInvocationD.getArguments());
    }
}
