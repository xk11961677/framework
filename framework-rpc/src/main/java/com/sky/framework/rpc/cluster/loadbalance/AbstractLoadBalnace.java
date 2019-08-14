package com.sky.framework.rpc.cluster.loadbalance;

import com.sky.framework.rpc.register.meta.RegisterMeta;

/**
 * @author
 */
public abstract class AbstractLoadBalnace implements LoadBalance {

    @Override
    public <T> T select(RegisterMeta.ServiceMeta serviceMeta) {
        return doSelect(serviceMeta);
    }

    /**
     * doSelect
     *
     * @param serviceMeta
     * @param <T>
     * @return
     */
    public abstract <T> T doSelect(RegisterMeta.ServiceMeta serviceMeta);
}
