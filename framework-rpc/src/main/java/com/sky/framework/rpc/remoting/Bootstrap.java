package com.sky.framework.rpc.remoting;

/**
 * @author
 */
public interface Bootstrap {

    /**
     *
     */
    void startup();

    /**
     *
     */
    void stop();

    /**
     *
     */
    boolean status();
}
