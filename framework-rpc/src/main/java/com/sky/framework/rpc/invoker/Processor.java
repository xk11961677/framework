package com.sky.framework.rpc.invoker;

import com.sky.framework.rpc.remoting.Request;
import com.sky.framework.rpc.remoting.Response;
import io.netty.channel.Channel;

/**
 * @author
 */
public interface Processor {

    /**
     * request service handler
     *
     * @param channel
     * @param request
     */
    void handler(Channel channel, Request request);


    /**
     * response service handler
     *
     * @param channel
     * @param response
     */
    void handler(Channel channel, Response response);
}
