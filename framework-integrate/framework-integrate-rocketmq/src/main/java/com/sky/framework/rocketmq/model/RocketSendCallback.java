package com.sky.framework.rocketmq.model;

import com.sky.framework.common.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @author
 */
@Slf4j
public class RocketSendCallback implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        LogUtil.info(log, "rocketSendCallback success:{}", sendResult.getMsgId());
    }

    @Override
    public void onException(Throwable throwable) {
        LogUtil.error(log, "rocketSendCallback exception:{}", throwable);
    }
}
