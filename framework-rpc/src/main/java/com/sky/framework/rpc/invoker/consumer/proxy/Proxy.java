package com.sky.framework.rpc.invoker.consumer.proxy;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.cluster.loadbalance.LoadBalance;
import com.sky.framework.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.RpcInvocation;
import com.sky.framework.rpc.invoker.future.DefaultInvokeFuture;
import com.sky.framework.rpc.register.meta.RegisterMeta;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.remoting.client.NettyClient;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericKeyedPool;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPool;
import com.sky.framework.rpc.remoting.client.pool.ChannelGenericPoolFactory;
import com.sky.framework.rpc.remoting.protocol.LongSequence;
import com.sky.framework.rpc.serializer.FastjsonSerializer;
import com.sky.framework.threadpool.AsyncThreadPoolProperties;
import com.sky.framework.threadpool.CommonThreadPool;
import io.netty.channel.Channel;
import jdk.net.SocketFlow;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author
 */
@Slf4j
public class Proxy {

    FastjsonSerializer serializer = new FastjsonSerializer();

    private LongSequence longSequence = new LongSequence();

    /*static {
        AsyncThreadPoolProperties properties = new AsyncThreadPoolProperties();
        CommonThreadPool.initThreadPool(properties);
    }*/

    private Class<?> interfaceClass;

    public Proxy(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Object remoteCall(Method method, Object[] args) throws Throwable {
        RegisterMeta.ServiceMeta serviceMeta = new RegisterMeta.ServiceMeta();
        serviceMeta.setGroup("test");
        serviceMeta.setServiceProviderName(interfaceClass.getName());
        serviceMeta.setVersion("1.0.0");

        LoadBalance instance = RoundRobinLoadBalance.getInstance();
        RegisterMeta.Address select = instance.select(serviceMeta);
        ChannelGenericPool channelGenericPool = ChannelGenericPoolFactory.getPoolConcurrentHashMap().get(select);

        Channel channel = channelGenericPool.getConnection();
        Object result;
        try {
            DefaultInvokeFuture invokeFuture = $invoke(channel, method, args);
            result = invokeFuture.getResult();
        } finally {
            channelGenericPool.releaseConnection(channel);
        }
        return result;
    }

    /**
     * doInvoke
     *
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    private DefaultInvokeFuture $invoke(Channel channel, Method method, Object[] args) throws Exception {
        DefaultInvokeFuture<?> invokeFuture = null;

        Method interfaceMethod = interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        long id = longSequence.next();
        try {
            Request request = new Request(id);
            RpcInvocation rpcInvocation = new RpcInvocation();
            rpcInvocation.setClazzName(interfaceClass.getName());
            rpcInvocation.setMethodName(interfaceMethod.getName());
            rpcInvocation.setParameterTypes(interfaceMethod.getParameterTypes());
            rpcInvocation.setArguments(args);

            byte[] serialize = serializer.serialize(rpcInvocation);
            request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
            invokeFuture = DefaultInvokeFuture.with(request.getId(), channel, 0, method.getReturnType());
            channel.writeAndFlush(request);
        } catch (Exception e) {
            //todo build error response and set DefaultInvokeFuture
            LogUtils.error(log, "then client proxy invoke failed:{}", e.getMessage());
            Response response = new Response(id);
            response.setStatus(Status.CLIENT_ERROR.value());
            DefaultInvokeFuture.fakeReceived(channel, response);
        }
        return invokeFuture;
    }

    /*
    Future<Object> future = CommonThreadPool.execute(invoke(method, args));
    Object result = future.get(3000, TimeUnit.SECONDS);
    private IAsynchronousHandler invoke(Method method, Object[] args) {
        DefaultAsynchronousHandler handler = new DefaultAsynchronousHandler() {
            @Override
            public Object call() throws Exception {
                DefaultInvokeFuture<?> invokeFuture = null;
                ChannelGenericKeyedPool channelGenericKeyedPool = nettyClient.getChannelGenericKeyedPool();
                Channel channel = channelGenericKeyedPool.getConnection(key);
                Method interfaceMethod = interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                try {
                    long id = longSequence.next();
                    Request request = new Request(id);
                    RpcInvocation rpcInvocation = new RpcInvocation();
                    rpcInvocation.setClazzName(interfaceMethod.getName());
                    rpcInvocation.setMethodName(interfaceMethod.getName());
                    rpcInvocation.setParameterTypes(interfaceMethod.getParameterTypes());
                    rpcInvocation.setArguments(args);

                    byte[] serialize = serializer.serialize(rpcInvocation);
                    request.bytes(SerializeEnum.FASTJSON.getSerializerCode(), serialize);
                    channel.writeAndFlush(request);
                    invokeFuture = DefaultInvokeFuture.with(request.getId(), channel, 3000, method.getReturnType());
                } catch (Exception e) {

                } finally {
                    channelGenericKeyedPool.releaseConnection(key, channel);
                }
                return invokeFuture;
            }
        };
        return handler;
    }*/
}
