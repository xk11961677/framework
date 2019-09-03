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
package com.sky.framework.web.common.registry;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设置安全开放API
 *
 * @author
 */
@Data
public class SecurityRegistry {

    private final List<String> defaultExcludePatterns = new ArrayList<>();

    private final List<String> excludePatterns = new ArrayList<>();

    public SecurityRegistry() {
        defaultExcludePatterns.add("/api/**");
        defaultExcludePatterns.add("/open/**");
        defaultExcludePatterns.add("/error/**");
        defaultExcludePatterns.add("/monitor/**");
        defaultExcludePatterns.add("/hystrix/**");
        defaultExcludePatterns.add("/consulhealth/**");
        defaultExcludePatterns.add("/doc.html");
        defaultExcludePatterns.add("/swagger-resources");
        defaultExcludePatterns.add("/images/**");
        defaultExcludePatterns.add("/webjars/**");
        defaultExcludePatterns.add("/v2/api-docs/**");
        defaultExcludePatterns.add("/configuration/ui/**");
        defaultExcludePatterns.add("/configuration/security/**");
        defaultExcludePatterns.add("/actuator/**");
    }

    /**
     * 设置放行api
     */
    public SecurityRegistry excludePathPatterns(String... patterns) {
        return excludePathPatterns(Arrays.asList(patterns));
    }

    /**
     * 设置放行api
     */
    public SecurityRegistry excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }

}
