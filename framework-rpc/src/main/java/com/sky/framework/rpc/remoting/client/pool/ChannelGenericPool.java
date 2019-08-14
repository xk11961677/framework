package com.sky.framework.rpc.remoting.client.pool;

import com.sky.framework.rpc.remoting.client.NettyClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author
 */
@Slf4j
public class ChannelGenericPool {

    private GenericObjectPool pool;

    public ChannelGenericPool(String key) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 20;
        config.maxWait = 3000;
        config.minIdle = 10;
        config.testWhileIdle = true;
        config.numTestsPerEvictionRun = 1;
        config.timeBetweenEvictionRunsMillis = 1000;
        pool = new GenericObjectPool(new BaseConnectionFactory(NettyClient.getClient(), key), config);

    }

    public ChannelGenericPool(NettyClient client) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 20;
        config.maxWait = 3000;
        config.minIdle = 10;
        config.testWhileIdle = true;
        config.numTestsPerEvictionRun = 1;
        config.timeBetweenEvictionRunsMillis = 1000;
        pool = new GenericObjectPool(new BaseConnectionFactory(client), config);

    }

    public Channel getConnection() throws Exception {
        return (Channel) pool.borrowObject();
    }

    public void releaseConnection(Channel channel) {
        try {
            pool.returnObject(channel);
        } catch (Exception e) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
