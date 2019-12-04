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
package com.sky.framework.rpc.server;

import com.sky.framework.rpc.BaseApplicationTests;
import com.sky.framework.rpc.example.UserServiceImpl;
import com.sky.framework.rpc.invoker.annotation.Provider;
import com.sky.framework.rpc.register.Register;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.register.zookeeper.ZookeeperRegister;
import com.sky.framework.rpc.remoting.server.NettyServer;
import com.sky.framework.rpc.util.ReflectAsmUtils;
import org.junit.Before;
import org.junit.Test;

public class NettyServerTests extends BaseApplicationTests {

    private int port = 8090;

    @Before
    public void build() {
        Provider annotation = UserServiceImpl.class.getAnnotation(Provider.class);
        ReflectAsmUtils.add(annotation, new UserServiceImpl());
    }

    @Test
    public void start() {

        NettyServer nettyServer = new NettyServer(port);

        register(nettyServer);

        nettyServer.startup();
    }


    private void register(NettyServer nettyServer) {
        Register register = new ZookeeperRegister();
        register.setConnect("127.0.0.1:2181");
        nettyServer.connectToRegistryServer(register);

        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setPort(port);

        Provider annotation = UserServiceImpl.class.getAnnotation(Provider.class);
        registerMeta.setGroup(annotation.group());
        registerMeta.setServiceProviderName(annotation.name());
        registerMeta.setVersion(annotation.version());
        nettyServer.getRegistryService().register(registerMeta);
    }
}
