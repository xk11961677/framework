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
package com.sky.framework.redis;

import com.sky.framework.redis.property.RedisProperties;
import com.sky.framework.redis.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author
 */
@Configuration
@ComponentScan(basePackageClasses = RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisAutoConfiguration implements CommandLineRunner {

    public static final String prefix = "fw.";

    @Override
    public void run(String... args) {
        log.info("sky framework redis startup successfully !");
    }

    @Bean
    public RedisUtils redisUtils(RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        RedisUtils redisUtil = new RedisUtils();
        RedisUtils.setRedisTemplate(redisTemplate);
        RedisUtils.setStringRedisTemplate(stringRedisTemplate);
        return redisUtil;
    }
}
