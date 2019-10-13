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
import com.sky.framework.web.common.annotation.IgnoreToken;
import com.sky.framework.web.constant.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户信息拦截器
 *
 * @author
 */
@Slf4j
@Order(1100)
@Component
@ConditionalOnProperty(value = WebConstants.GLOBAL_TOKEN_INTERCEPTOR_ENABLE, matchIfMissing = true)
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
        boolean ignoreToken = ((HandlerMethod) handler).getMethod().isAnnotationPresent(IgnoreToken.class);
        String token = request.getHeader(X_CLIENT_TOKEN_USER);
        if (!ignoreToken && StringUtils.isEmpty(token)) {
            response(response);
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
        response.setCharacterEncoding(WebConstants.UTF8);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println(result);
        } catch (IOException e) {
            LogUtils.error(log, "" + e.getMessage(), e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * TODO 从网关获取并校验,通过校验就可信任x-client-token中的信息
     *
     * @param clientToken
     */
    private void verifyClientToken(String clientToken) {
        LogUtils.debug(log, "校验 x-client-token:{}", clientToken);
    }
}
