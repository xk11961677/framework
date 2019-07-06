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
