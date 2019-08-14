package com.sky.framework.rpc.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.thread.NamedThreadFactory;
import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPoolFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
     * 注册元信息
     */
    private final ExecutorService registerExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("register-executor", true));

    /**
     * 定时重新将元信息注册
     */
    private final ScheduledExecutorService registerScheduledExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("register-executor", true));

    /**
     *
     */
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    /**
     * 已注册服务
     */
    @Getter
    @Setter
    private ConcurrentHashMap<RegisterMeta, String> registerMetaMap = new ConcurrentHashMap();

    /**
     * 已订阅服务
     */
    @Getter
    @Setter
    private final ConcurrentHashSet<RegisterMeta.ServiceMeta> subscribeSet = new ConcurrentHashSet();

    /**
     * address provider by meta
     */
    public static final ConcurrentHashMap<RegisterMeta.ServiceMeta, ConcurrentHashSet<RegisterMeta.Address>> metaAddressMap = new ConcurrentHashMap();

    /**
     * 检测有效性
     */
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("register-available", true));

    public AbstractRegistryService() {
        //async register
        LogUtils.info(log, "async register start!");
        registerExecutor.execute(() -> {
            while (!shutdown.get()) {
                RegisterMeta meta = null;
                try {
                    meta = queue.take();
                    LogUtils.info(log, "meta take :{}", meta);
                    doRegister(meta);
                } catch (InterruptedException e) {
                    LogUtils.info(log, "register meta failed !");
                } catch (Exception e) {
                    if (meta != null) {
                        LogUtils.info(log, "register meta try again meta:{}", meta.getServiceProviderName());

                        final RegisterMeta finalMeta = meta;
                        registerScheduledExecutor.schedule(() -> {
                            queue.add(finalMeta);
                        }, 1, TimeUnit.SECONDS);
                    }
                }
            }
        });
    }

    @Override
    public void register(RegisterMeta meta) {
        queue.add(meta);
    }

    @Override
    public void unregister(RegisterMeta meta) {
        if (!queue.remove(meta)) {
            registerMetaMap.remove(meta);
//            doUnregister(meta);
        }
    }


    @Override
    public void subscribe(RegisterMeta.ServiceMeta serviceMeta) {
        subscribeSet.add(serviceMeta);
        doSubscribe(serviceMeta);
    }

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


    protected void notify(RegisterMeta.Address address, RegisterMeta.ServiceMeta meta, EventType eventType) {
        if (EventType.ADD.equals(eventType)) {
            ChannelGenericPoolFactory.create(address);
            ConcurrentHashSet<RegisterMeta.Address> addressSet = getAddress(meta);
            addressSet.add(address);
        }
        if (EventType.REMOVE.equals(eventType)) {
            metaAddressMap.get(address).remove(meta);
        }
        if (EventType.OFFLINE.equals(eventType)) {
            //1. destroy ip:port channel pool


            //2. clear empty metaAddressMap address set
            metaAddressMap.get(address).clear();
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
}
