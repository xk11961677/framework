package com.sky.framework.job.handler;


import com.xxl.job.core.handler.IJobHandler;

/**
 * @author
 */
public abstract class AbstractJobHandler extends IJobHandler {

    /**
     * 执行
     *
     * @param param
     * @param <T>
     * @return
     */
    public abstract <T> T exec(String param);
}
