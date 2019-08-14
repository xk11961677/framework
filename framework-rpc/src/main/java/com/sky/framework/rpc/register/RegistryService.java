package com.sky.framework.rpc.register;

import com.sky.framework.rpc.register.meta.RegisterMeta;

import java.util.Collection;

/**
 * @author
 */
public interface RegistryService extends Registry {

    /**
     * @param meta
     */
    void register(RegisterMeta meta);

    /**
     * @param meta
     */
    void unregister(RegisterMeta meta);

    /**
     * @param serviceMeta
     */
    void subscribe(RegisterMeta.ServiceMeta serviceMeta);

    /**
     * @param serviceMeta
     * @return
     */
    Collection<RegisterMeta> lookup(RegisterMeta.ServiceMeta serviceMeta);

}
