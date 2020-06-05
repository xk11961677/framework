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
package com.sky.framework.redis.property;


import com.sky.framework.redis.RedisAutoConfiguration;
import com.sky.framework.redis.enums.LockModel;
import com.sky.framework.redis.enums.Model;
import lombok.Data;
import org.redisson.config.SslProvider;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.net.URI;

/**
 * @author
 */
@ConfigurationProperties(prefix = RedisAutoConfiguration.prefix + "redisson")
@Data
public class RedissonProperties {

    private Model model = Model.SINGLE;

    private String codec = "org.redisson.codec.JsonJacksonCodec";

    private Integer threads;

    private Integer nettyThreads;

    private TransportMode transportMode = TransportMode.NIO;

    /**
     * 公共参数
     **/
    private Integer idleConnectionTimeout = 10000;
    private Integer pingTimeout = 1000;
    private Integer connectTimeout = 10000;
    private Integer timeout = 3000;
    private Integer retryAttempts = 3;
    private Integer retryInterval = 1500;
    private String password;
    private Integer subscriptionsPerConnection = 5;
    private String clientName;
    private Boolean sslEnableEndpointIdentification = true;
    private SslProvider sslProvider = SslProvider.JDK;
    private URI sslTruststore;
    private String sslTruststorePassword;
    private URI sslKeystore;
    private String sslKeystorePassword;
    private Integer pingConnectionInterval = 0;
    private Boolean keepAlive = false;
    private Boolean tcpNoDelay = false;
    private Boolean referenceEnabled = true;
    private Long lockWatchdogTimeout = 30000L;
    private Boolean keepPubSubOrder = true;
    private Boolean decodeInExecutor = false;
    private Boolean useScriptCache = false;
    private Integer minCleanUpDelay = 5;
    private Integer maxCleanUpDelay = 1800;
    /**
     * 锁的模式 如果不设置 单个key默认可重入锁 多个key默认联锁
     */
    private LockModel lockModel;

    /**
     * 等待加锁超时时间 -1一直等待
     */
    private Long attemptTimeout = 10000L;

    /**
     * 数据缓存时间 默认30分钟
     */
    private Long dataValidTime = 1000 * 60 * 30L;
    /**
     * 公共参数结束
     **/

    @NestedConfigurationProperty
    private SingleServerConfig singleServerConfig;

    @NestedConfigurationProperty
    private MultipleServerConfig multipleServerConfig;


}
