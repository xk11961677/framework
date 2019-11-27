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
package com.sky.framework.rpc.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.thread.NamedThreadFactory;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPoolFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author
 */
@Slf4j
public abstract class AbstractRegistryService implements RegistryService {

    /**
     * 存储需要注册的元信息
     */
    private final LinkedBlockingQueue<RegisterMeta> queue = new LinkedBlockingQueue<>();

    /**
     * 注册中心是否启动
     */
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    /**
     * 已注册服务
     * <元信息:注册状态>
     */
    @Getter
    private volatile ConcurrentHashMap<RegisterMeta, String> registerMetaMap = new ConcurrentHashMap();

    /**
     * 已订阅服务
     */
    @Getter
    private final ConcurrentHashSet<RegisterMeta.ServiceMeta> subscribeSet = new ConcurrentHashSet();

    /**
     * <元信息,{所有提供此元信息的地址}>
     */
    public static final ConcurrentHashMap<RegisterMeta.ServiceMeta, ConcurrentHashSet<RegisterMeta.Address>> metaAddressMap = new ConcurrentHashMap();

    /**
     * 元信息注册器线程池 时机: 项目初次启动时,异步注册
     */
    private final ExecutorService registerExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("register-executor", true));

    /**
     * 重新将元信息注册  时机: 当registerExecutor注册异常时,调用此定时器重新注册(仅执行一次)
     */
    private final ScheduledExecutorService registerScheduledExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("register-scheduled-executor", true));


    /**
     * 检测有效性
     */
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("register-available", true));

    public AbstractRegistryService() {
        registerExecutor.execute(() -> {
            while (!shutdown.get()) {
                RegisterMeta meta = null;
                try {
                    meta = queue.take();
                    doRegister(meta);
                } catch (InterruptedException e) {
                    log.warn("register meta failed !");
                } catch (Exception e) {
                    if (meta != null) {
                        log.warn("register meta try again , meta:{}", meta.getServiceProviderName());
                        final RegisterMeta finalMeta = meta;
                        registerScheduledExecutor.schedule(() -> {
                            queue.add(finalMeta);
                        }, 1, TimeUnit.SECONDS);
                    }
                }
            }
        });

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Map<RegisterMeta.ServiceMeta, Collection<RegisterMeta>> map = new HashMap<>();
            for (Map.Entry<RegisterMeta, String> entry : registerMetaMap.entrySet()) {
                RegisterMeta.ServiceMeta serviceMeta = entry.getKey().getServiceMeta();
                Collection<RegisterMeta> registerMetas = map.get(serviceMeta);
                if (registerMetas == null) {
                    registerMetas = lookup(serviceMeta);
                    map.put(serviceMeta, registerMetas);
                }
                if (registerMetas == null || !registerMetas.contains(entry.getKey())) {
                    register(entry.getKey());
                }
            }
            map.clear();
        }, 30, 5, TimeUnit.SECONDS);
    }

    @Override
    public void register(RegisterMeta meta) {
        queue.add(meta);
    }

    @Override
    public void unregister(RegisterMeta meta) {
        if (!queue.remove(meta)) {
            registerMetaMap.remove(meta);
            doUnRegister(meta);
        }
    }


    @Override
    public void subscribe(RegisterMeta.ServiceMeta serviceMeta) {
        subscribeSet.add(serviceMeta);
        doSubscribe(serviceMeta);
    }

    /*****************************************************************************************/

    /**
     * doRegister
     *
     * @param meta
     */
    protected abstract void doRegister(final RegisterMeta meta);

    /**
     * doSubscribe
     *
     * @param serviceMeta
     */
    protected abstract void doSubscribe(RegisterMeta.ServiceMeta serviceMeta);

    /**
     * doRegister
     *
     * @param meta
     */
    protected abstract void doUnRegister(final RegisterMeta meta);


    protected void notify(RegisterMeta.Address address, RegisterMeta.ServiceMeta meta, EventType eventType) {
        if (EventType.ADD.equals(eventType)) {
            ChannelGenericPoolFactory.create(address);
            ConcurrentHashSet<RegisterMeta.Address> addressSet = getAddress(meta);
            addressSet.add(address);
        }
        if (EventType.REMOVE.equals(eventType)) {
            Optional.ofNullable(metaAddressMap.get(address)).ifPresent(m -> m.remove(meta));
        }
        if (EventType.OFFLINE.equals(eventType)) {
            //1. destroy ip:port channel pool


            //2. clear empty metaAddressMap address set
            Optional.ofNullable(metaAddressMap.get(address)).ifPresent(m -> m.clear());
            metaAddressMap.remove(address);
        }
    }


    @SuppressWarnings("all")
    private ConcurrentHashSet<RegisterMeta.Address> getAddress(RegisterMeta.ServiceMeta meta) {
        ConcurrentHashSet<RegisterMeta.Address> addressSet = metaAddressMap.get(meta);
        if (addressSet == null) {
            ConcurrentHashSet<RegisterMeta.Address> newAddressSet = new ConcurrentHashSet<>();
            addressSet = metaAddressMap.putIfAbsent(meta, newAddressSet);
            if (addressSet == null) {
                addressSet = newAddressSet;
            }
        }
        return addressSet;
    }

    protected enum EventType {
        ADD, REMOVE, OFFLINE
    }


    protected enum RegisterMetaType {
        OK, INIT
    }
}
