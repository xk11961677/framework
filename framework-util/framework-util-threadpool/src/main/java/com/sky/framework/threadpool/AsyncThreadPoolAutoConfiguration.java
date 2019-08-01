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
package com.sky.framework.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author
 */
@Configuration
@EnableConfigurationProperties(value = AsyncThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "asyncthreadpool", value = "enabled", matchIfMissing = true)
@Slf4j
public class AsyncThreadPoolAutoConfiguration {

    @Resource
    private AsyncThreadPoolProperties asyncThreadPoolProperties;

    @PostConstruct
    public void init() {
        if (CommonThreadPool.getThreadPool() == null) {
            log.info("Successfully initialized CommonThreadPool :{}" + asyncThreadPoolProperties);
            CommonThreadPool.initThreadPool(asyncThreadPoolProperties);
        }
    }

    @PreDestroy
    public void destory() {
        log.info("Start shutdown CommonThreadPool");
        boolean flag = CommonThreadPool.shutDown();
        log.info("CommonThreadPool is shutdown :{}"+flag);
    }
}
