package com.sky.framework.rpc.remoting.client.pool;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.remoting.client.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.BasePoolableObjectFactory;

/**
 * @author
 */
@Slf4j
public class BaseConnectionFactory extends BasePoolableObjectFactory<Channel> {

    private NettyClient client;

    /**
     * ip:port
     */
    private String key;

    public BaseConnectionFactory(NettyClient client) {
        this.client = client;
    }

    public BaseConnectionFactory(NettyClient client, String key) {
        this.client = client;
        this.key = key;
    }

    @Override
    public Channel makeObject() throws Exception {
        return client.getChannel(key);
    }

    @Override
    public void destroyObject(Channel channel) throws Exception {
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LogUtils.info(log, "baseConnectionFactory close channel complete !");
            }
        });
    }

    @Override
    public boolean validateObject(Channel channel) {
        return channel.isActive();
    }
}