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
package com.sky.framework.web.configuration;

import com.sky.framework.common.LogUtils;
import com.sky.framework.web.common.registry.SecurityRegistry;
import com.sky.framework.web.interceptor.GlobalContextInterceptor;
import com.sky.framework.web.interceptor.GlobalTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 验证token配置
 *
 * @author
 */
@Configuration
@Slf4j
public class SecurityConfiguration implements WebMvcConfigurer {

    @Autowired(required = false)
    private SecurityRegistry securityRegistry;

    @Autowired(required = false)
    private GlobalContextInterceptor globalContextInterceptor;

    @Autowired(required = false)
    private GlobalTokenInterceptor globalTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (globalContextInterceptor != null) {
            registry.addInterceptor(globalContextInterceptor);
            LogUtils.info(log, "sky framework web add globalContextInterceptor successfully !");
        }
        if (globalTokenInterceptor != null) {
            registry.addInterceptor(globalTokenInterceptor)
                    .excludePathPatterns(securityRegistry.getDefaultExcludePatterns())
                    .excludePathPatterns(securityRegistry.getExcludePatterns());
            LogUtils.info(log, "sky framework web add globalTokenInterceptor successfully !");
        }
    }
}
