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

import lombok.Data;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 多节点配置
 *
 * @author
 */
@Data
public class MultipleServerConfig {

    private String loadBalancer = "org.redisson.connection.balancer.RoundRobinLoadBalancer";
    private Integer slaveConnectionMinimumIdleSize = 32;
    private Integer slaveConnectionPoolSize = 64;
    private Integer failedSlaveReconnectionInterval = 3000;
    private Integer failedSlaveCheckInterval = 180000;
    private Integer masterConnectionMinimumIdleSize = 32;
    private Integer masterConnectionPoolSize = 64;
    private ReadMode readMode = ReadMode.SLAVE;
    private SubscriptionMode subscriptionMode = SubscriptionMode.SLAVE;
    private Integer subscriptionConnectionMinimumIdleSize = 1;
    private Integer subscriptionConnectionPoolSize = 50;
    private Long dnsMonitoringInterval = 5000L;

    private List<String> nodeAddresses = new ArrayList();

    /**
     * 集群,哨兵,云托管
     */
    private Integer scanInterval = 1000;

    /**
     * 哨兵模式,云托管,主从
     */
    private Integer database = 0;

    /**
     * 哨兵模式
     */
    private String masterName;


}
