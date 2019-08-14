package com.sky.framework.rpc.remoting.server;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rpc.common.enums.SerializeEnum;
import com.sky.framework.rpc.invoker.provider.ProviderProcessorHandler;
import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import com.sky.framework.rpc.remoting.Status;
import com.sky.framework.rpc.serializer.FastjsonSerializer;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelDuplexHandler {

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

    /**
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            LogUtils.info(log, "the server close an idle channel ! ");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("=====" + msg);
//        ctx.fireChannelRead(msg);
        Channel channel = ctx.channel();
        ProviderProcessorHandler.instance.handler(channel, (Request) msg);
    }

    /**
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

    /**
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
