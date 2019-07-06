package com.sky.framework.job.handler;

import com.alibaba.fastjson.JSON;
import com.sky.framework.job.util.LogComposeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <T>
 * @author
 */
@SuppressWarnings("all")
@Slf4j
public abstract class BeanJobHandler<T> extends AbstractJobHandler {

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        String result = "";
        try {
            LogComposeUtil.log(log, "skycloud job begin:{} param:{}" + param);
            Object exec = this.exec(param);
            if (exec != null) {
                result = JSON.toJSONString(exec);
            }
        } catch (Exception e) {
            XxlJobLogger.log(e);
            FAIL.setContent(result);
            return FAIL;
        } finally {
            long end = System.currentTimeMillis();
            LogComposeUtil.log(log, "skycloud job end:{} cost time:{}" + (end - begin) + "ms");
        }
        SUCCESS.setContent(result);
        return SUCCESS;
    }
}
