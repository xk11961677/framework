/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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
package com.sky.framework.rocketmq.config;

import com.sky.framework.common.LogUtils;
import com.sky.framework.rocketmq.listener.MQMessageListenerConcurrently;
import com.sky.framework.rocketmq.listener.MessageHandler;
import com.sky.framework.rocketmq.util.RocketMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 根据配置启动mq producer consumer
 * 线程不安全
 *
 * @author
 */
@ConditionalOnBean(MQProperties.class)
@Component
@Slf4j
public class MQInitializingBean implements InitializingBean {

    @Autowired
    private MQProperties mqProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.info(log, "mq initializing :{}");
        consumer();
        producer();
    }

    /**
     * 初始化生产者
     */
    private void producer() {
        List<ProducerProperties> producers = mqProperties.getProducer();
        if (producers != null) {
            for (ProducerProperties producerProperties : producers) {
                DefaultMQProducer producer = new DefaultMQProducer();
                String group = producerProperties.getGroup();
                if (!RocketMqUtils.map.containsKey(group)) {
                    try {
                        String namesrvAddr = producerProperties.getNamesrvAddr();
                        namesrvAddr = StringUtils.isEmpty(namesrvAddr) ? mqProperties.getNamesrvAddr() : namesrvAddr;
                        producer.setNamesrvAddr(namesrvAddr);
                        producer.setProducerGroup(group);
                        producer.setRetryTimesWhenSendFailed(producerProperties.getRetryTimesWhenSendFailed());
                        producer.setSendMsgTimeout(producerProperties.getSendMsgTimeout());
                        producer.setVipChannelEnabled(producerProperties.getVipChannelEnabled());
                        producer.start();
                        RocketMqUtils.map.put(group, producer);
                    } catch (Exception e) {
                        LogUtils.error(log, "init producer group:{} failed :{}", group, e);
                    }
                }
            }
        }
    }

    /**
     * 初始化消费者
     */
    private void consumer() {
        for (Map.Entry<String, MessageHandler> entry : MQBeanPostProcessor.map.entrySet()) {
            String[] key = entry.getKey().split("_");
            try {
                ConsumerProperties consumerProperties = getConsumerProperties(key[0]);
                MessageHandler handler = entry.getValue();
                String namesrvAddr = consumerProperties.getNamesrvAddr();
                namesrvAddr = StringUtils.isEmpty(namesrvAddr) ? mqProperties.getNamesrvAddr() : namesrvAddr;
                DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(key[0]);
                pushConsumer.setNamesrvAddr(namesrvAddr);
                pushConsumer.subscribe(key[1], key[2]);
                if (consumerProperties.getConsumeThreadMin() != null) {
                    pushConsumer.setConsumeThreadMin(consumerProperties.getConsumeThreadMin());
                }
                if (consumerProperties.getConsumeThreadMax() != null) {
                    pushConsumer.setConsumeThreadMax(consumerProperties.getConsumeThreadMax());
                }
                if (consumerProperties.getConsumeMessageBatchMaxSize() != null) {
                    pushConsumer.setConsumeMessageBatchMaxSize(consumerProperties.getConsumeMessageBatchMaxSize());
                }
                pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                pushConsumer.registerMessageListener(new MQMessageListenerConcurrently(handler));
                pushConsumer.start();
            } catch (Exception e) {
                LogUtils.error(log, "init consumer key:{} failed :{}", key, e);
            }
        }
    }

    /**
     * 根据group name 获取消费者配置属性
     * 如未找到默认返回ConsumerProperties未设置属性的对象
     *
     * @param group
     * @return
     */
    private ConsumerProperties getConsumerProperties(String group) {
        List<ConsumerProperties> consumers = mqProperties.getConsumer();
        if (consumers != null) {
            for (ConsumerProperties consumerProperties : consumers) {
                if (consumerProperties.getGroup().equals(group)) {
                    return consumerProperties;
                }
            }
        }
        return new ConsumerProperties();
    }
}
