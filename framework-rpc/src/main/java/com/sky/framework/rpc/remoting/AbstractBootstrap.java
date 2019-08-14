package com.sky.framework.rpc.remoting;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author
 */
public abstract class AbstractBootstrap implements Bootstrap {

    public AtomicBoolean status = new AtomicBoolean(false);

    @Override
    public void startup() {
        status.compareAndSet(false, true);
    }


    @Override
    public void stop() {
        status.compareAndSet(true, false);
    }

    @Override
    public boolean status() {
        return status.get();
    }

    /**
     *
     */
    public abstract void init();
}
