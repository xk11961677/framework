package com.sky.framework.rpc.invoker.future;

import java.util.concurrent.CompletionStage;

/**
 * @author
 */
public interface InvokeFuture<V> extends CompletionStage<V> {

    /**
     * invoke interface returnType
     * @return
     */
    Class<V> returnType();

    /**
     * Waits for this future to be completed and get the result.
     */
    V getResult() throws Throwable;
}
