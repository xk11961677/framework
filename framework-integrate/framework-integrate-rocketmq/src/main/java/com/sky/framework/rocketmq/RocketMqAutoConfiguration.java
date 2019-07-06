package com.sky.framework.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = RocketMqAutoConfiguration.class)
@Slf4j
public class RocketMqAutoConfiguration implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("framework rocketmq started !");
    }
}
