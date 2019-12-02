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
package com.sky.framework.rpc.common.spi;

import com.sky.framework.rpc.cluster.ClusterInvoker;
import com.sky.framework.rpc.cluster.FailoverClusterInvoker;
import com.sky.framework.rpc.cluster.loadbalance.LoadBalance;
import com.sky.framework.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.consumer.proxy.ProxyFactory;
import com.sky.framework.rpc.invoker.consumer.proxy.javassist.JavassistProxyFactory;
import com.sky.framework.rpc.serializer.FastJsonSerializer;
import com.sky.framework.rpc.serializer.ObjectSerializer;
import com.sky.framework.rpc.util.SpiLoader;
import com.sky.framework.rpc.util.SpiMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * todo 需要修改
 *
 * @author
 */
public class SpiExchange {

    private static SpiExchange instance = new SpiExchange();

    /**
     * consumer 代理方式
     */
    @Getter
    @Setter
    private ProxyFactory proxyFactory = new JavassistProxyFactory();

    /**
     * 序列化方式
     */
    @Getter
    @Setter
    private ObjectSerializer serializer = new FastJsonSerializer();

    /**
     * 序列化方式code
     */
    @Getter
    @Setter
    private byte serializerCode = SerializeEnum.FASTJSON.getSerializerCode();

    /**
     * 集群调用方式
     */
    @Getter
    @Setter
    private ClusterInvoker clusterInvoker = new FailoverClusterInvoker();

    /**
     * 负载均衡方式
     */
    @Getter
    @Setter
    private LoadBalance loadBalance = new RoundRobinLoadBalance();

    public static SpiExchange getInstance() {
        return instance;
    }


    public <T> T loadSpiSupport(Class<T> clazz, String name) {
        ServiceLoader<?> services = SpiLoader.loadAll(clazz);
        return (T) StreamSupport.stream(services.spliterator(), true)
                .filter(p -> Objects.equals(p.getClass().getAnnotation(SpiMetadata.class).name(), name))
                .findFirst().orElse(null);
    }
}
