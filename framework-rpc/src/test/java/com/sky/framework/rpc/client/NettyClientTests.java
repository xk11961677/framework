package com.sky.framework.rpc.client;

import com.sky.framework.rpc.BaseApplicationTests;
import com.sky.framework.rpc.example.UserService;
import com.sky.framework.rpc.invoker.consumer.proxy.Proxy;
import com.sky.framework.rpc.invoker.consumer.proxy.javassist.JavassistProxyFactory;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.client.NettyClient;
import org.junit.Test;

/**
 * @author
 */
public class NettyClientTests extends BaseApplicationTests {

    @Test
    public void start() throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connectToRegistryServer("127.0.0.1:2181");

        RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
        serviceMeta.setGroup("test");
        serviceMeta.setServiceProviderName(UserService.class.getName());
        serviceMeta.setVersion("1.0.0");
        nettyClient.getRegistryService().subscribe(serviceMeta);

        JavassistProxyFactory javassistProxyFactory = new JavassistProxyFactory();
        UserService userService = javassistProxyFactory.newInstance(UserService.class);
        String hello = userService.hello("123");
        System.out.println("=====result:{}" + hello);
        userService.hello();

    }
}
