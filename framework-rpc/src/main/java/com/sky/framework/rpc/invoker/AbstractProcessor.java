package com.sky.framework.rpc.invoker;

import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import io.netty.channel.Channel;

/**
 * @author
 */
public abstract class AbstractProcessor implements Processor {

    /**
     * request handler
     *
     * @param channel
     * @param request
     */
    @Override
    public void handler(Channel channel, Request request) {
    }

    /**
     * response handler
     *
     * @param channel
     * @param response
     */
    @Override
    public void handler(Channel channel, Response response) {
    }

}
