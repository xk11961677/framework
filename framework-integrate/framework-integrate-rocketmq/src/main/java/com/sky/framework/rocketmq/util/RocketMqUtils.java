/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rocketmq.util;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rocketmq.exception.MqException;
import com.sky.framework.rocketmq.model.RocketMessage;
import com.sky.framework.rocketmq.model.RocketSendCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author
 */
@Component
@Slf4j
public class RocketMqUtils {

    public static Map<String, DefaultMQProducer> map = new HashMap<>();


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

        if (message.getBody() == null) {
            throw new MqException("body不能为空");
        }
        Message mq = new Message();
        mq.setTopic(message.getTopic());
        mq.setBody(SerializationUtils.serialize(message.getBody()));
        rocketMQTemplate.asyncSend(mq.getTopic(), mq, new RocketSendCallback());
    }


    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMsg(RocketMessage message) {
        if (message.getGroup() == null) {
            throw new MqException("group不能为空");
        }
        if (message.getTopic() == null) {
            throw new MqException("topic不能为空");
        }
        try {
            if (message.getBody() != null) {
                DefaultMQProducer producer = map.get(message.getGroup());
                if (producer == null) {
                    LogUtils.warn(log, "group:{} cannot find producer ", message.getGroup());
                    return;
                }
                Message msg = new Message(message.getTopic(), message.getTag(), UUID.randomUUID().toString(), String.valueOf(message.getBody()).getBytes());
                SendResult sendResult = producer.send(msg);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    LogUtils.info(log, "message:{} send successfully", msg.getKeys());
                } else {
                    LogUtils.warn(log, "message:{} status:{}", msg.getKeys(), sendResult.getSendStatus());
                }
            }
        } catch (Exception e) {
            LogUtils.error(log, "message send failed :{}", e.getMessage(), e);
        }
    }
}
