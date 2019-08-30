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
package com.sky.framework.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.sky.framework.common.LogUtils;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.model.util.UserContextHolder;
import com.sky.framework.web.common.annotation.IgnoreToken;
import com.sky.framework.web.constant.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用户信息拦截器
 *
 * @author
 */
@Slf4j
@SuppressWarnings("unused")
public class GlobalTokenInterceptor implements HandlerInterceptor {
    /**
     * 用户token信息,格式为json
     */
    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
    /**
     * 服务内部间调用的认证token
     */
    public static final String X_CLIENT_TOKEN = "x-client-token";
    /**
     * 默认字符串
     */
    private static final String DEFAULT_STR = "";
    /**
     * 内部服务间调用api接口
     */
    private static final String INTER_API = "/api";
    /**
     * 开放地址前缀
     */
    public static List<String> OPEN_URL = new CopyOnWriteArrayList() {
        {
            add("/open");
            add("/error");
            add("/monitor");
            add("/hystrix");
            add("/consulhealth");

            add("/doc.html");
            add("/swagger-resources");
            add("/images/");
            add("/webjars");
            add("/v2/api-docs");
            add("/configuration/ui");
            add("/configuration/security");
            add("/actuator");
        }
    };

    /**
     * checkToken(request.getHeader(X_CLIENT_TOKEN));
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String user = request.getHeader(X_CLIENT_TOKEN_USER);
        this.convertAndSetContext(user, request);
        String uri = request.getRequestURI();
        for (String key : OPEN_URL) {
            if (uri.startsWith(key)) {
                return true;
            }
        }
        boolean ignoreToken = ((HandlerMethod) handler).getMethod().isAnnotationPresent(IgnoreToken.class);
        if (!ignoreToken && !uri.startsWith(INTER_API) && StringUtils.isEmpty(user)) {
            this.response(response);
            return false;
        }
        return true;
    }

    /**
     * 返回响应信息
     *
     * @param response
     */
    private void response(HttpServletResponse response) {
        String result = JSON.toJSONString(new MessageRes(FailureCodeEnum.AUZ100001.getCode(), FailureCodeEnum.AUZ100001.getMsg()));
        LogUtils.debug(log, "token验证结果，result={}", result);
        response.setCharacterEncoding(WebConstants.UTF_8);
        response.setContentType(WebConstants.APPLICATION_JSOON_UTF_8);
        try {
            response.getWriter().println(result);
        } catch (IOException e) {
            LogUtils.error(log, "", e.getMessage(), e);
        }
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
        if (StringUtils.isNotEmpty(user)) {
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
        map.put("user_id", userId);
        map.put("channel", channel);
        map.put("token", user);
        UserContextHolder.getInstance().setContext(map);
    }

    /**
     * TODO 从网关获取并校验,通过校验就可信任x-client-token-user中的信息
     *
     * @param token
     */
    private void checkToken(String token) {
        LogUtils.debug(log, "校验 x-client-token-user:{}", token);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }
}
