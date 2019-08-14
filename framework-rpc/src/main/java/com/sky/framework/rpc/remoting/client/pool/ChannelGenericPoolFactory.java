package com.sky.framework.rpc.remoting.client.pool;

import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.client.NettyClient;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author
 */
public class ChannelGenericPoolFactory {

    @Getter
    private static ConcurrentHashMap<RegisterMeta.Address, ChannelGenericPool> poolConcurrentHashMap = new ConcurrentHashMap<>();

    private static final ReentrantLock reentrantLock = new ReentrantLock();

    public static void create(RegisterMeta.Address address) {
        try {
            reentrantLock.lock();
            ChannelGenericPool channelGenericPool = poolConcurrentHashMap.get(address);
            if (channelGenericPool == null) {
                channelGenericPool = new ChannelGenericPool(address.getHost() + ":" + address.getPort());
                poolConcurrentHashMap.put(address, channelGenericPool);
            }
        } finally {
            reentrantLock.unlock();
        }
    }
}
