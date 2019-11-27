package com.sky.framework.rpc.remoting.server;

import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@Slf4j
public class ServerIdleStateTrigger extends IdleStateHandler {

    public ServerIdleStateTrigger() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

   /* @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("idle happened:{}", evt.state().name());
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("idle check happen, so close the connection");
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }*/
}
