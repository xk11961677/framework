package com.sky.framework.rpc.common.spi;

import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * @author
 */
public class SpiExchange {

    @Getter
    @Setter
    private static ProxyFactory proxyFactory;
}
