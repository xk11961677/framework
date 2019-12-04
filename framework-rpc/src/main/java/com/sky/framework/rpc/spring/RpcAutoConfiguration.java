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

import com.sky.framework.rpc.invoker.annotation.Provider;
import com.sky.framework.rpc.register.Register;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.register.zookeeper.ZookeeperRegister;
import com.sky.framework.rpc.remoting.client.NettyClient;
import com.sky.framework.rpc.remoting.server.NettyServer;
import com.sky.framework.rpc.spring.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Objects;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = RpcAutoConfiguration.class)
@Slf4j
public class RpcAutoConfiguration implements CommandLineRunner {

    @Autowired
    private RpcProperties properties;

    @Override
    public void run(String... args) {
        log.info("sky framework rpc startup successfully !");
    }

    @PostConstruct
    private void init() {
        initServer();
        initClient();
    }

    public void initServer() {
        String port = properties.getPort();
        if (Objects.isNull(port) || "".equals(port)) {
            log.error("the rpc server startup failed , because port is null");
            return;
        }
        String address = properties.getRegistry().getAddress();
        if (Objects.isNull(address) || "".equals(address)) {
            log.error("the rpc server startup failed  , because connect is null");
            return;
        }
        Thread thread = new Thread(new Server(properties));
        thread.start();
    }

    public void initClient() {
        String address = properties.getRegistry().getAddress();
        if (Objects.isNull(address) || "".equals(address)) {
            log.error("the rpc client startup failed  , because connect is null");
            return;
        }
        Thread thread = new Thread(new Client(properties));
        thread.start();
        log.info("the rpc client startup successfully !");
    }

    /**
     *
     */
    private class Server implements Runnable {

        private RpcProperties properties;

        private Server(RpcProperties properties) {
            this.properties = properties;
        }

        @Override
        public void run() {
            NettyServer nettyServer = new NettyServer(Integer.parseInt(properties.getPort()));
            Register register = new ZookeeperRegister();
            register.setConnect(properties.getRegistry().getAddress());
            register.setGroup(properties.getRegistry().getGroup());
            nettyServer.connectToRegistryServer(register);
            Collection<Provider> providers = AnnotationBean.providerConfigs.values();
            for (Provider provider : providers) {
                RegisterMeta registerMeta = new RegisterMeta();
                registerMeta.setPort(Integer.parseInt(properties.getPort()));
                registerMeta.setGroup(provider.group());
                registerMeta.setServiceProviderName(provider.name());
                registerMeta.setVersion(provider.version());
                nettyServer.getRegistryService().register(registerMeta);
            }
            nettyServer.startup();
            log.info("the rpc server startup successfully !");
        }
    }


    private class Client implements Runnable {

        private RpcProperties properties;

        private Client(RpcProperties properties) {
            this.properties = properties;
        }

        @Override
        public void run() {
            NettyClient nettyClient = new NettyClient();
            Register register = new ZookeeperRegister();
            register.setConnect(properties.getRegistry().getAddress());
            register.setGroup(properties.getRegistry().getGroup());
            nettyClient.connectToRegistryServer(register);
            Collection<ReferenceBean> referenceBeans = AnnotationBean.referenceConfigs.values();
            for (ReferenceBean referenceBean : referenceBeans) {
                Reference reference = referenceBean.getReference();
                RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
                serviceMeta.setGroup(reference.group());
                serviceMeta.setServiceProviderName(referenceBean.getInterfaceClass().getName());
                serviceMeta.setVersion(reference.version());
                nettyClient.getRegistryService().subscribe(serviceMeta);
            }
            log.info("the rpc server startup successfully !");
        }
    }
}
