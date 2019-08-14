package com.sky.framework.rpc.cluster.loadbalance;

import com.sky.framework.rpc.register.meta.RegisterMeta;

/**
 * @author
 */
public interface LoadBalance {

    /**
     *
     * @param serviceMeta
     * @param <T>
     * @return
     */
    <T> T select(RegisterMeta.ServiceMeta serviceMeta);
}
