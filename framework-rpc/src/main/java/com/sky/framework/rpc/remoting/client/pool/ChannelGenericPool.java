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
        config.maxIdle = 10;
        config.minIdle = 10;
        config.testWhileIdle = true;
        config.numTestsPerEvictionRun = 1;
        config.timeBetweenEvictionRunsMillis = 10000;
        pool = new GenericObjectPool(new BaseConnectionFactory(NettyClient.getClient(), key), config);

    }

    public ChannelGenericPool(NettyClient client) {
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 20;
        config.maxWait = 3000;
        config.maxIdle = 10;
        config.minIdle = 10;
        config.testWhileIdle = true;
        config.numTestsPerEvictionRun = 1;
        config.timeBetweenEvictionRunsMillis = 10000;
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
