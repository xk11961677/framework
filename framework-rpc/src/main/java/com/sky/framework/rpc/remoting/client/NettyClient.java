package com.sky.framework.rpc.remoting.client;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.register.Registry;
import com.sky.framework.rpc.register.RegistryService;
import com.sky.framework.rpc.register.zookeeper.ZookeeperRegistryService;
import com.sky.framework.rpc.remoting.AbstractBootstrap;
import com.sky.framework.rpc.remoting.protocol.ProtocolDecoder;
import com.sky.framework.rpc.remoting.protocol.ProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class NettyClient extends AbstractBootstrap implements Registry {

    @Getter
    private static NettyClient client = null;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    @Getter
    private RegistryService registryService;

    public NettyClient() {
        init();
        client = this;
    }

    @Override
    public void startup() {
        super.startup();
    }


    @Override
    public void stop() {
        if (status()) {
            super.stop();
            if (group != null) {
                group.shutdownGracefully();
            }
        } else {
            LogUtils.info(log, " the consumer has been shutdown !");
        }
    }

    @Override
    public void init() {
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ProtocolEncoder());
                        p.addLast(new ProtocolDecoder());
                        p.addLast(new ClientChannelHandler());
                    }
                });
    }

    public Channel getChannel(String key) {
        String[] split = key.split(":");
        return getChannel(split[0], Integer.parseInt(split[1]));
    }

    private Channel getChannel(String address, int port) {
        ChannelFuture f = null;
        try {
            f = bootstrap.connect(address, port).sync();
            Channel channel = f.channel();
            return channel;
        } catch (InterruptedException e) {
            LogUtils.error(log, "the consumer get channel has error !", e.getMessage());
        }
        return null;
    }

    @Override
    public void connectToRegistryServer(String connectString) {
        registryService = new ZookeeperRegistryService();
        registryService.connectToRegistryServer(connectString);
    }
}
