package com.sky.framework.rpc.cluster.loadbalance;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.sky.framework.rpc.register.AbstractRegistryService;
import com.sky.framework.rpc.register.meta.RegisterMeta;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author
 */
public class RoundRobinLoadBalance extends AbstractLoadBalnace {

    private CopyOnWriteArrayList cList = new CopyOnWriteArrayList();

    private static final AtomicIntegerFieldUpdater<RoundRobinLoadBalance> indexUpdater =
            AtomicIntegerFieldUpdater.newUpdater(RoundRobinLoadBalance.class, "index");

    private volatile int index = 0;

    public static LoadBalance getInstance() {
        return new RoundRobinLoadBalance();
    }

    @Override
    public <T> T doSelect(RegisterMeta.ServiceMeta serviceMeta) {
        ConcurrentHashSet<RegisterMeta.Address> addresses = AbstractRegistryService.metaAddressMap.get(serviceMeta);
        this.snapshot(addresses);
        RegisterMeta.Address[] addressesArray = new RegisterMeta.Address[cList.size()];
        cList.toArray(addressesArray);
        int rrIndex = indexUpdater.getAndIncrement(this) & Integer.MAX_VALUE;
        RegisterMeta.Address address = addressesArray[rrIndex % addressesArray.length];
        return (T) address;
    }

    /**
     *
     * @param addresses
     */
    private void snapshot(ConcurrentHashSet<RegisterMeta.Address> addresses) {
        if (cList.size() != addresses.size()) {
            cList.addAll(addresses);
        }
    }
}
