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
package com.sky.framework.redis.configuration;

import com.sky.framework.common.LogUtils;
import com.sky.framework.common.nacos.NacosContainer;
import com.sky.framework.redis.util.RedissonLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@Slf4j
public class RedissonConfiguration {

    @ConditionalOnProperty(prefix = "spring.cloud.nacos.config", value = "enabled")
    @Bean(destroyMethod = "shutdown")
    @Primary
    public RedissonClient redisson() {
        InputStream redisson = NacosContainer.get("redisson.yaml");
        Config config = null;
        try {
            config = Config.fromYAML(redisson);
        } catch (IOException e) {
            LogUtils.error(log, "load redisson config exception:{}", e);
        }
        return Redisson.create(config);
    }

    @Bean
    public RedissonLockUtils redissonLockUtil(RedissonClient redissonClient) {
        RedissonLockUtils redissonLockUtils = new RedissonLockUtils();
        RedissonLockUtils.setRedissonClient(redissonClient);
        return redissonLockUtils;
    }
}
