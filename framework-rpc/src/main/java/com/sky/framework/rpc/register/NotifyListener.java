package com.sky.framework.rpc.register;

import com.sky.framework.rpc.register.meta.RegisterMeta;

/**
 * @author
 */
public interface NotifyListener {

    void notify(RegisterMeta registerMeta, NotifyEvent event);

    enum NotifyEvent {
        CHILD_ADDED,
        CHILD_REMOVED
    }
}
