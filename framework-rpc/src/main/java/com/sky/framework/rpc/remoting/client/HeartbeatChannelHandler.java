package com.sky.framework.rpc.remoting.client;

import com.sky.framework.rpc.remoting.protocol.ProtocolHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartbeatChannelHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("the client send heartbeat package !");
                ctx.writeAndFlush(heartbeatContent());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    private static final ByteBuf HEARTBEAT_BUF;

    static {
        ByteBuf buf = Unpooled.buffer(ProtocolHeader.HEADER_SIZE);
        buf.writeShort(ProtocolHeader.MAGIC);
        buf.writeByte(ProtocolHeader.HEARTBEAT);
        buf.writeByte(0);
        buf.writeLong(0);
        buf.writeInt(0);
        HEARTBEAT_BUF = Unpooled.unreleasableBuffer(buf).asReadOnly();
    }

    /**
     * Returns the shared heartbeat content.
     */
    public static ByteBuf heartbeatContent() {
        return HEARTBEAT_BUF.duplicate();
    }
}
