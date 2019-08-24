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
package com.sky.framework.oss;

import com.sky.framework.oss.property.OssProperties;
import com.sky.framework.oss.strategy.OssStrategyAdapter;
import com.sky.framework.oss.strategy.OssStrategyEnum;
import com.sky.framework.oss.strategy.OssStrategyFactory;
import com.sky.framework.oss.util.OssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * OSS自动配置项
 *
 * @author sky
 */
@Configuration
@ComponentScan(basePackageClasses = OssAutoConfiguration.class)
@EnableConfigurationProperties({OssProperties.class})
@Slf4j
public class OssAutoConfiguration implements CommandLineRunner {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public void run(String... args) {
        log.info("sky framework oss started !");
    }

    /**
     * 创建OSS策略工厂
     *
     * @return com.sky.framework.oss.strategy.OssStrategyFactory
     * @author sky
     * @since
     */
    @Bean
    public OssStrategyFactory ossStrategyFactory() {
        OssStrategyFactory ossStrategyFactory = new OssStrategyFactory();
        OssStrategyFactory.init(ossProperties);
        return ossStrategyFactory;
    }

    /**
     * 默认阿里云OssUtils工具类
     *
     * @return com.sky.framework.oss.util.OssUtils
     * @author sky
     * @since
     */
    @ConditionalOnProperty(name = "oss.strategy", havingValue = "aliyun")
    @Bean
    public OssUtils ossUtils() {
        OssUtils ossUtils = new OssUtils();
        OssStrategyAdapter ossStrategy = OssStrategyFactory.createOssStrategy(OssStrategyEnum.ALIYUN);
        OssUtils.setOssStrategy(ossStrategy.getOssStrategy());
        return ossUtils;
    }

    /**
     * 关闭所有oss client
     *
     * @return void
     * @author sky
     * @since
     */
    @PreDestroy
    public void destroyClient() {
        OssStrategyFactory.destroyAll();
    }
}
