package com.sky.framework.redis;

import com.sky.framework.redis.util.RedisTokenUtil;
import com.sky.framework.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = RedisAutoConfiguration.class)
@Slf4j
public class RedisAutoConfiguration implements CommandLineRunner {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... args) {
        log.info("framework redis started !");
        RedisTokenUtil.setRedisService(redisUtil);
    }
}
