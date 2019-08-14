package com.sky.framework.rpc.server;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.sky.framework.rpc.BaseApplicationTests;
import com.sky.framework.rpc.example.UserService;
import com.sky.framework.rpc.example.UserServiceImpl;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.server.NettyServer;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import org.junit.Test;

public class NettyServerTests extends BaseApplicationTests {

    private void build() {
        MethodAccess access = MethodAccess.get(UserService.class);
        ReflectAsmUtils.accessConstainer.put(UserService.class, access);
        ReflectAsmUtils.clazzConstainer.put(UserService.class.getName(), UserService.class);
        ReflectAsmUtils.objectConstainer.put(UserService.class, new UserServiceImpl());
    }

    @Test
    public void start() {
        build();

        NettyServer nettyServer = new NettyServer(8081);

        register(nettyServer);

        nettyServer.startup();
    }


    private void register(NettyServer nettyServer) {
        nettyServer.connectToRegistryServer("127.0.0.1:2181");
        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setPort(8081);
        registerMeta.setGroup("test");
        registerMeta.setServiceProviderName(UserService.class.getName());
        registerMeta.setVersion("1.0.0");
        nettyServer.getRegistryService().register(registerMeta);
    }
}
