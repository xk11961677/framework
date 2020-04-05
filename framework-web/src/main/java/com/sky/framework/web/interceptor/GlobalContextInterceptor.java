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
package com.sky.framework.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.util.UserContextHolder;
import com.sky.framework.web.constant.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息转换拦截器
 *
 * @author
 */
@Slf4j
@Order(1000)
@Component
@ConditionalOnProperty(value = WebConstants.GLOBAL_CONTEXT_INTERCEPTOR_ENABLE, matchIfMissing = true)
public class GlobalContextInterceptor implements HandlerInterceptor {
    /**
     * 用户token信息,格式为json
     */
    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";

    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String user = request.getHeader(X_CLIENT_TOKEN_USER);
        convertAndSetContext(user, request);
        return true;
    }

    /**
     * 获取用户信息 ,兼容旧系统,token不是jwt格式,重写user_id
     *
     * @param user
     * @param request
     * @return
     */
    private void convertAndSetContext(String user, HttpServletRequest request) {
        LogUtils.debug(log, "get x-client-token-user from header  :{} ", user);
        Map map = null;
        if (!StringUtils.isEmpty(user)) {
            try {
                map = JSON.parseObject(user, Map.class);
            } catch (Exception e) {
                LogUtils.error(log, "旧系统 token :{}", user);
            }
        }
        if (map == null) {
            map = new HashMap(8);
        }
        String userId = request.getHeader("user_id");
        String channel = request.getHeader("channel");
        //优先header
        if (!StringUtils.isEmpty(userId)) {
            map.put("user_id", userId);
        }
        map.put("channel", channel);
        map.put("token", user);
        UserContextHolder.getInstance().setContext(map);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }
}
