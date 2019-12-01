package com.sky.framework.rpc.remoting.server;

import com.sky.framework.rpc.monitor.MetricsMonitor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author
 */
@ChannelHandler.Sharable
public class MetricsChannelHandler extends ChannelDuplexHandler {

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MetricsMonitor.getServerChannelCount().incrementAndGet();
        super.channelActive(ctx);
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MetricsMonitor.getServerChannelCount().decrementAndGet();
        super.channelInactive(ctx);
    }
}
