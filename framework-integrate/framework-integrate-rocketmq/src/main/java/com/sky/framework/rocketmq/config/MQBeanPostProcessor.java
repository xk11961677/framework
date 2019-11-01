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
import com.sky.framework.rocketmq.annotation.ConsumerListener;
import com.sky.framework.rocketmq.listener.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目启动扫描@ConsumerLister注解,将listener类启动mq消费端
 *
 * @author
 */
@Component
@Slf4j
public class MQBeanPostProcessor implements BeanPostProcessor {

    static Map<String, MessageHandler> map = new HashMap<>();


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (match(bean)) {
            MessageHandler handler = (MessageHandler) bean;
            //此处需要严谨处理代理类
            ConsumerListener annotation = handler.getClass().getAnnotation(ConsumerListener.class);
            if (annotation == null) {
                LogUtils.info(log, "found class cannot add mq consumer successfully , name:{}", handler.getClass().getName());
                return bean;
            }
            String group = annotation.group();
            String topic = annotation.topic();
            String tag = annotation.tag();
            String key = group + "_" + topic + "_" + tag;
            map.putIfAbsent(key, handler);
        }
        return bean;
    }

    private boolean match(Object bean) {
        return bean instanceof MessageHandler ? true : false;
    }
}
