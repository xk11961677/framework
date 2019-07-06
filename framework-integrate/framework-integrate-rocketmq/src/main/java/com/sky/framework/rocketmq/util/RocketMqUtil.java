package com.sky.framework.rocketmq.util;

import com.sky.framework.rocketmq.exception.MqException;
import com.sky.framework.rocketmq.model.RocketMessage;
import com.sky.framework.rocketmq.model.RocketSendCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;

/**
 * @author
 */
@Component
@Slf4j
public class RocketMqUtil {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送
     *
     * @param message
     * @return
     */
    public void asyncSend(RocketMessage message) {
        if (message.getTopic() == null) {
            throw new MqException("topic不能为空");
        }

        if(message.getBody() == null) {
            throw new MqException("body不能为空");
        }
        Message mq = new Message();
        mq.setTopic(message.getTopic());
        mq.setBody(SerializationUtils.serialize(message.getBody()));
        rocketMQTemplate.asyncSend(mq.getTopic(), mq, new RocketSendCallback());
    }

}
