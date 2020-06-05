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
package com.sky.framework.redis.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.framework.redis.property.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis缓存配置
 * *@EnableCaching 开启声明式缓存支持. 之后就可以使用 @Cacheable/@CachePut/@CacheEvict 注解缓存数据.
 *
 * @author
 */
@Configuration
@EnableCaching
public class RedisConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * 覆盖默认的配置
     * 如果使用redisson则会将LettuceConnectionFactory(2.X默认)替换成redisson的连接池
     *
     * @return RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();
        // 设置key的序列化规则
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置value的序列化规则
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 解决注解方式存放到redis中的值是乱码的情况
     * 如果使用redisson则会将LettuceConnectionFactory(2.X默认)替换成redisson的连接池
     *
     * @param redisConnectionFactory 连接工厂
     * @return CacheManager
     */
    @Bean
    @ConditionalOnMissingBean(name = "cacheManager")
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {


        RedisCacheConfiguration redisCacheConfiguration = getRedisCacheConfiguration(Duration.ofMinutes(redisProperties.getDefaultTTL()));

        Map<String, Long> cacheNamesTTL = redisProperties.getCacheNamesTTL();
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>(cacheNamesTTL.size());
        for (Map.Entry<String, Long> entry : cacheNamesTTL.entrySet()) {
            RedisCacheConfiguration redisCacheConfigurationTTL = getRedisCacheConfiguration(Duration.ofSeconds(entry.getValue()));
            cacheConfigurations.put(entry.getKey(), redisCacheConfigurationTTL);
        }

        RedisCacheManager.RedisCacheManagerBuilder builder;

        if (redisProperties.getKeyTTL()) {
            CustomRedisCacheWriter customCacheWriter = new CustomRedisCacheWriter(redisConnectionFactory);
            builder = RedisCacheManager.builder(customCacheWriter);
        } else {
            builder = RedisCacheManager.builder(redisConnectionFactory);
        }
        return builder.cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations).build();
    }

    /**
     * 自定义key生成器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "keyGenerator")
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append("." + method.getName());
            if (params == null || params.length == 0 || params[0] == null) {
                return null;
            }
            String join = String.join("&", Arrays.stream(params).map(Object::toString).collect(Collectors.toList()));
            String format = String.format("%s{%s}", sb.toString(), join);
            return format;
        };
    }

    /**
     * redis cache 配置
     *
     * @return
     */
    private RedisCacheConfiguration getRedisCacheConfiguration(Duration duration) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration =
                config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                        //不缓存null对象
                        .disableCachingNullValues()
                        .entryTtl(duration);
        return redisCacheConfiguration;
    }

    /**
     * 获取jackson2JsonRedisSerializer
     *
     * @return
     */
    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}
