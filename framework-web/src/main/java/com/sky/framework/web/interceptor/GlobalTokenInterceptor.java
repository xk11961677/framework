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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息拦截器
 *
 * @author
 */
@Slf4j
@SuppressWarnings("unused")
public class GlobalTokenInterceptor implements HandlerInterceptor {
    /**
     * 服务间调用token用户信息,格式为json
     * {
     * "user_name":"必须有"
     * "自定义key:"value"
     * }
     */
    public static final String X_CLIENT_TOKEN_USER = "x-client-token-user";
    /**
     * 服务间调用的认证token
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
    public static List<String> OPEN_URL = new ArrayList<String>() {
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


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        /**
         * 从网关获取并校验,通过校验就可信任x-client-token-user中的信息
         */
        checkToken(request.getHeader(X_CLIENT_TOKEN));

        String userInfo = request.getHeader(X_CLIENT_TOKEN_USER);

        LogUtils.debug(log, "get x-client-token-user from header  :{} ", userInfo);

        String requestURI = request.getRequestURI();

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }


        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        boolean ignoreToken = method.isAnnotationPresent(IgnoreToken.class);

        Map map = getUserInfo(userInfo, request);
        UserContextHolder.getInstance().setContext(map);

        for (String key : OPEN_URL) {
            if (requestURI.startsWith(key)) {
                return true;
            }
        }

        if (!ignoreToken && !requestURI.startsWith(INTER_API) && StringUtils.isEmpty(userInfo)) {
            String result = JSON.toJSONString(new MessageRes(FailureCodeEnum.AUZ100001.getCode(), FailureCodeEnum.AUZ100001.getMsg()));
            LogUtils.debug(log, "token验证结果，result={}", result);
            response.setCharacterEncoding(WebConstants.UTF_8);
            response.setContentType(WebConstants.APPLICATION_JSOON_UTF_8);
            response.getWriter().println(result);
            return false;
        }
        return true;
    }

    private void checkToken(String token) {
        /**
         * TODO 从网关获取并校验,通过校验就可信任x-client-token-user中的信息
         */
        LogUtils.debug(log, "//TODO 校验 x-client-token-user:{}", token);
    }

    /**
     * 获取用户信息
     *
     * @param userInfo
     * @param request
     * @return
     */
    private Map getUserInfo(String userInfo, HttpServletRequest request) {
        /**
         * 获取用户信息 ,兼容旧系统,token不是jwt格式,重写user_id
         */
        Map map = null;
        if (StringUtils.isNotEmpty(userInfo)) {
            try {
                map = JSON.parseObject(userInfo, Map.class);
            } catch (Exception e) {
                LogUtils.error(log, "旧系统 token :{}", userInfo);
            }
        }
        if (map == null) {
            map = new HashMap();
        }
        String userId = request.getHeader("user_id");
        map.put("user_id", userId);
        String channel = request.getHeader("channel");
        map.put("channel", channel);
        map.put("token", userInfo);
        return map;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }
}
