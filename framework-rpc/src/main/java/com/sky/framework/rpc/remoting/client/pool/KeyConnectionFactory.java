package com.sky.framework.rpc.remoting.client.pool;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.remoting.client.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

/**
 * @author
 */
@Slf4j
public class KeyConnectionFactory implements KeyedPoolableObjectFactory<String, Channel> {

    private NettyClient client;

    public KeyConnectionFactory(NettyClient client) {
        this.client = client;
    }

    @Override
    public Channel makeObject(String key) throws Exception {
        return client.getChannel(key);
    }

    @Override
    public void destroyObject(String key, Channel channel) throws Exception {
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                LogUtils.info(log, "KeyConnectionFactory close channel complete !");
            }
        });
    }

    @Override
    public boolean validateObject(String key, Channel channel) {
        return channel.isActive();
    }

    @Override
    public void activateObject(String key, Channel obj) throws Exception {
    }

    /**
     * 挂起
     *
     * @param key
     * @param obj
     * @throws Exception
     */
    @Override
    public void passivateObject(String key, Channel obj) throws Exception {
    }
}
