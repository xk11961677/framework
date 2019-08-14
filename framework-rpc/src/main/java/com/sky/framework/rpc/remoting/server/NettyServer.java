package com.sky.framework.rpc.remoting.server;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.register.Registry;
import com.sky.framework.rpc.register.RegistryService;
import com.sky.framework.rpc.register.zookeeper.ZookeeperRegistryService;
import com.sky.framework.rpc.remoting.AbstractBootstrap;
import com.sky.framework.rpc.remoting.protocol.ProtocolDecoder;
import com.sky.framework.rpc.remoting.protocol.ProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
public class NettyServer extends AbstractBootstrap implements Registry {

    private EventLoopGroup bossGroup = null;

    private EventLoopGroup workerGroup = null;

    private ServerBootstrap bootstrap = null;

    @Getter
    private RegistryService registryService = null;

    /**
     * default port 8081
     */
    private int port = 8081;

    public NettyServer(int port) {
        this.port = port;
        init();
    }


    @Override
    public void startup() {
        super.startup();
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            LogUtils.info(log, "the provider start successfully !");

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LogUtils.error(log, "the provider start failed !", e.getMessage());
            stop();
        }
    }


    @Override
    public void stop() {
        if (status()) {
            super.stop();
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } else {
            LogUtils.info(log, " the provider has been shutdown !");
        }
    }

    /**
     * p.addLast(new LoggingHandler(LogLevel.INFO));
     */
    @Override
    public void init() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerChannelHandler serverChannelHandler = new ServerChannelHandler();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)

                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//                            p.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.MINUTES));
                            p.addLast(new ProtocolEncoder());
                            p.addLast(new ProtocolDecoder());
                            p.addLast(serverChannelHandler);
                        }
                    })
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
        } catch (Exception e) {
            LogUtils.error(log, "the provider init failed ! ", e.getMessage());
        }
    }

    @Override
    public void connectToRegistryServer(String connectString) {
        registryService = new ZookeeperRegistryService();
        registryService.connectToRegistryServer(connectString);
    }
}
