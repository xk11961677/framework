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
package com.sky.framework.rpc.client;

import com.sky.framework.rpc.BaseApplicationTests;
import com.sky.framework.rpc.example.User;
import com.sky.framework.rpc.example.UserService;
import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import com.sky.framework.rpc.invoker.consumer.proxy.javassist.JavassistProxyFactory;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.client.NettyClient;
import com.sky.framework.rpc.spring.annotation.Reference;
import com.sky.framework.rpc.util.IdUtils;
import org.junit.Test;

import java.lang.annotation.Annotation;

/**
 * @author
 */
public class NettyClientTests extends BaseApplicationTests {

    @Test
    public void start() throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connectToRegistryServer("127.0.0.1:2181");

        Reference reference = new Reference() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Reference.class;
            }

            @Override
            public String group() {
                return "test";
            }

            @Override
            public String version() {
                return "1.0.0";
            }
        };
        RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
        serviceMeta.setGroup(reference.group());
        serviceMeta.setServiceProviderName(UserService.class.getName());
        serviceMeta.setVersion(reference.version());
        nettyClient.getRegistryService().subscribe(serviceMeta);

        ProxyFactory factory = new JavassistProxyFactory();
//        ProxyFactory factory = new JdkProxyFactory();
//        ProxyFactory factory = new ByteBuddyProxyFactory();

        UserService userService = factory.newInstance(UserService.class, reference);

        String hello = userService.hello("123");
        System.out.println("=====result hello:{}" + hello);

        User user = User.builder().id(IdUtils.getId()).name("sky:" + IdUtils.getId() + ":").build();
        user = userService.getUser(user);
        System.out.println("=====result user:{}" + user);

        userService.hello();

        while (true) {
//            Thread.sleep(10000);
            Thread.sleep(3000);
            hello = userService.hello("123");
            System.out.println("=====result:{}" + hello);

            user = User.builder().id(IdUtils.getId()).name("sky:" + IdUtils.getId() + ":").build();
            user = userService.getUser(user);
            System.out.println("=====result user:{}" + user);
        }
    }
}
