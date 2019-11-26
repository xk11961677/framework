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
package com.sky.framework.rpc.register.zookeeper;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.sky.framework.common.IpUtils;
import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.register.AbstractRegistryService;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author
 */
@Slf4j
public class ZookeeperRegistryService extends AbstractRegistryService {

    /**
     * zk 客户端
     */
    private CuratorFramework client;

    /**
     * 本机IP地址
     */
    private final String address = IpUtils.getLocalIp();

    /**
     * children node watcher
     */
    private final ConcurrentMap<RegisterMeta.ServiceMeta, PathChildrenCache> pathChildrenCaches = new ConcurrentHashMap();

    /**
     * all subscribed service
     */
    private final ConcurrentHashMap<RegisterMeta.Address, ConcurrentHashSet<RegisterMeta.ServiceMeta>> serviceMetaMap = new ConcurrentHashMap();


    @Override
    public void connectToRegistryServer(String connect) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connect)
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(60 * 1000)
                .retryPolicy(retryPolicy)
                .build();
        client.getConnectionStateListenable().addListener((client, newState) -> {
            LogUtils.info(log, "Zookeeper connection state changed {}.", newState);

            if (newState == ConnectionState.RECONNECTED) {
                // 重新订阅
                for (RegisterMeta.ServiceMeta serviceMeta : getSubscribeSet()) {
                    doSubscribe(serviceMeta);
                }
                // 重新发布服务
                for (RegisterMeta meta : getRegisterMetaMap().keySet()) {
                    ZookeeperRegistryService.super.register(meta);
                }
            }
        });
        client.start();
    }

    @Override
    protected void doRegister(RegisterMeta meta) {
        //zk 目录
        String directory = String.format("/rpc/provider/%s/%s/%s",
                meta.getGroup(),
                meta.getServiceProviderName(),
                meta.getVersion());
        try {
            if (client.checkExists().forPath(directory) == null) {
                client.create().creatingParentsIfNeeded().forPath(directory);
            }
        } catch (Exception e) {
            LogUtils.error(log, "Create parent path failed, directory:{} exception:{}", directory, e.getMessage());
        }

        try {
            meta.setHost(address);
            client.create().withMode(CreateMode.EPHEMERAL).inBackground((client, event) -> {
                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                    getRegisterMetaMap().put(meta, KeeperException.Code.OK.toString());
                }
                LogUtils.info(log, "Register: {} - {}.", meta, event);
            }).forPath(String.format("%s/%s:%s",
                    directory,
                    meta.getHost(),
                    String.valueOf(meta.getPort())));
        } catch (Exception e) {
            LogUtils.error(log, "Create parent path failed, directory:{}", directory);
        }
    }

    @SuppressWarnings("all")
    @Override
    protected void doSubscribe(RegisterMeta.ServiceMeta serviceMeta) {
        PathChildrenCache childrenCache = pathChildrenCaches.get(serviceMeta);
        if (childrenCache == null) {
            String directory = String.format("/rpc/provider/%s/%s/%s",
                    serviceMeta.getGroup(),
                    serviceMeta.getServiceProviderName(),
                    serviceMeta.getVersion());

            PathChildrenCache newChildrenCache = new PathChildrenCache(client, directory, false);
            childrenCache = pathChildrenCaches.putIfAbsent(serviceMeta, newChildrenCache);
            if (childrenCache == null) {
                childrenCache = newChildrenCache;
                childrenCache.getListenable().addListener((client, event) -> {
                    LogUtils.info(log, "Child event: {}", event);
                    switch (event.getType()) {
                        case CHILD_ADDED: {
                            RegisterMeta registerMeta = parseRegisterMeta(event.getData().getPath());
                            RegisterMeta.Address address = registerMeta.getAddress();
                            RegisterMeta.ServiceMeta serviceMeta1 = registerMeta.getServiceMeta();

                            ConcurrentHashSet<RegisterMeta.ServiceMeta> serviceMetaSet = getServiceMeta(address);
                            serviceMetaSet.add(serviceMeta1);
                            ZookeeperRegistryService.super.notify(address, serviceMeta1, EventType.ADD);
                            break;
                        }
                        case CHILD_REMOVED: {
                            RegisterMeta registerMeta = parseRegisterMeta(event.getData().getPath());
                            RegisterMeta.Address address = registerMeta.getAddress();
                            RegisterMeta.ServiceMeta serviceMeta1 = registerMeta.getServiceMeta();
                            ConcurrentHashSet<RegisterMeta.ServiceMeta> serviceMetaSet = getServiceMeta(address);

                            serviceMetaSet.remove(serviceMeta1);
                            ZookeeperRegistryService.super.notify(address, serviceMeta1, EventType.REMOVE);

                            if (serviceMetaSet.isEmpty()) {
                                LogUtils.info(log, "Offline notify: {}.", address);
                                ZookeeperRegistryService.super.notify(address, serviceMeta1, EventType.OFFLINE);
                            }
                            break;
                        }
                    }
                });
                try {
                    childrenCache.start();
                } catch (Exception e) {
                    LogUtils.error(log, "Subscribe failed :{}", directory);
                }
            } else {
                try {
                    newChildrenCache.close();
                } catch (IOException e) {
                    LogUtils.error(log, "PathChildrenCache close failed:{}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void unregister(RegisterMeta meta) {

    }

    @Override
    public Collection<RegisterMeta> lookup(RegisterMeta.ServiceMeta serviceMeta) {
        String directory = String.format("/rpc/provider/%s/%s/%s",
                serviceMeta.getGroup(),
                serviceMeta.getServiceProviderName(),
                serviceMeta.getVersion());

        List<RegisterMeta> registerMetaList = new ArrayList<>();
        try {
            List<String> paths = client.getChildren().forPath(directory);
            for (String p : paths) {
                registerMetaList.add(parseRegisterMeta(String.format("%s/%s", directory, p)));
            }
        } catch (Exception e) {
            LogUtils.error(log, "lookup service meta: {} path failed, {}", serviceMeta, e.getMessage());
        }
        return registerMetaList;
    }

    /**
     * 解析zk存储数据,构建元信息
     *
     * @param data
     * @return
     */
    private RegisterMeta parseRegisterMeta(String data) {
        String[] metaArray = StringUtils.split(data, "/");
        RegisterMeta meta = new RegisterMeta();
        meta.setGroup(metaArray[2]);
        meta.setServiceProviderName(metaArray[3]);
        meta.setVersion(metaArray[4]);
        String[] arrayAddress = metaArray[5].split(":");
        meta.setHost(arrayAddress[0]);
        meta.setPort(Integer.parseInt(arrayAddress[1]));
        return meta;
    }

    @SuppressWarnings("all")
    private ConcurrentHashSet<RegisterMeta.ServiceMeta> getServiceMeta(RegisterMeta.Address address) {
        ConcurrentHashSet<RegisterMeta.ServiceMeta> serviceMetaSet = serviceMetaMap.get(address);
        if (serviceMetaSet == null) {
            ConcurrentHashSet<RegisterMeta.ServiceMeta> newServiceMetaSet = new ConcurrentHashSet<>();
            serviceMetaSet = serviceMetaMap.putIfAbsent(address, newServiceMetaSet);
            if (serviceMetaSet == null) {
                serviceMetaSet = newServiceMetaSet;
            }
        }
        return serviceMetaSet;
    }
}
