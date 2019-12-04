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
package com.sky.framework.rpc.spring.annotation;


import com.sky.framework.rpc.spring.AnnotationRegistrar;
import com.sky.framework.rpc.spring.RpcAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * @author
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcAutoConfiguration.class, AnnotationRegistrar.class})
@Documented
public @interface EnableRPC {

    /**
     * 要扫描的包名
     *
     * @return
     */
    String scan();


    /**
     * 代理方式
     * Consumer端参数
     *
     * @return
     * @see com.sky.framework.rpc.common.enums.ProxyEnum
     */
    String proxy() default "javassist";

    /**
     * 集群方式
     * Consumer端参数
     *
     * @return
     * @see com.sky.framework.rpc.common.enums.ClusterEnum
     */
    String cluster() default "failover";

    /**
     * 序列化方式
     * Consumer端参数
     *
     * @return
     * @see com.sky.framework.rpc.common.enums.SerializeEnum
     */
    String serialize() default "fastjson";

    /**
     * 负载均衡方式
     *
     * @return
     * @see com.sky.framework.rpc.common.enums.LoadBalanceEnum
     */
    String loadBalance() default "roundrobin";
}
