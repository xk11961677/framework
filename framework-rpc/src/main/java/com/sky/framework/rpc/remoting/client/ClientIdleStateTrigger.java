package com.sky.framework.rpc.remoting.client;

import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@Slf4j
public class ClientIdleStateTrigger extends IdleStateHandler {

    public ClientIdleStateTrigger() {
        super(0, 5, 0, TimeUnit.SECONDS);
    }

}
