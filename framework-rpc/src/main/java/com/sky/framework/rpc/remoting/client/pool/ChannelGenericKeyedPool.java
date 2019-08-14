package com.sky.framework.rpc.remoting.client.pool;

import com.sky.framework.rpc.remoting.client.NettyClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * @author
 */
@Slf4j
public class ChannelGenericKeyedPool {

    private GenericKeyedObjectPool pool;

    public ChannelGenericKeyedPool(NettyClient client) {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        config.maxActive = 20;
        config.maxWait = 3000;
        config.minIdle = 10;
        config.testWhileIdle = true;
        config.numTestsPerEvictionRun = 1;
        config.timeBetweenEvictionRunsMillis = 1000;
        pool = new GenericKeyedObjectPool(new KeyConnectionFactory(client), config);
    }

    public Channel getConnection(String key) throws Exception {
        return (Channel) pool.borrowObject(key);
    }

    public void releaseConnection(String key, Channel channel) {
        try {
            pool.returnObject(key, channel);
        } catch (Exception e) {
            e.printStackTrace();
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
